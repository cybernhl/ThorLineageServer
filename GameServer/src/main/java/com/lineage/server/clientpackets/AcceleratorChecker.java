package com.lineage.server.clientpackets;

import java.util.Collection;
import java.util.EnumMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_ToGmMessage;
import com.lineage.server.world.World;

/**
 * 加速器檢測
 */
public class AcceleratorChecker {

	private static final Log _log = LogFactory.getLog(AcceleratorChecker.class);

	private final L1PcInstance _pc;

	private int _injusticeCount;

	private int _justiceCount;

	private static final int INJUSTICE_COUNT_LIMIT = 10;// 允許錯誤次數

	private static final int JUSTICE_COUNT_LIMIT = 4;// 必須正常次數

	// 加大的允許範圍質
	public static double CHECK_STRICTNESS = ConfigOther.SPEED_TIME;

	private final EnumMap<ACT_TYPE, Long> _actTimers = new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	private final EnumMap<ACT_TYPE, Long> _checkTimers = new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	public static enum ACT_TYPE {
		MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
	}

	public static final int R_OK = 0;// 正常 

	public static final int R_DETECTED = 1;// 異常

	public static final int R_DISCONNECTED = 2;// 連續異常

	public AcceleratorChecker(final L1PcInstance pc) {
		this._pc = pc;
		this._injusticeCount = 0;
		this._justiceCount = 0;
		final long now = System.currentTimeMillis();
		for (final ACT_TYPE each : ACT_TYPE.values()) {
			this._actTimers.put(each, now);
			this._checkTimers.put(each, now);
		}
	}

	/**
	 * 斷開用戶
	 */
	private void doDisconnect() {
		try {
			final StringBuilder name = new StringBuilder();
			name.append(this._pc.getName());
			// 945：發現外掛程式因此強制中斷遊戲  
			this._pc.sendPackets(new S_Disconnect());
			
			this._pc.getNetConnection().kick();// 中斷
			toGmKickMsg(name.toString());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 通知GM
	 */
	private void toGmErrMsg(final String name, int count) {
		try {
			if (count >= 5) {
				final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
				for (L1PcInstance tgpc : allPc) {
					if (tgpc.isGm()) {
						tgpc.sendPackets(new S_ToGmMessage("人物:" + name + " 速度異常!(" + count + "次)"));
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 通知GM
	 */
	private void toGmKickMsg(final String name) {
		try {
			final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
			for (L1PcInstance tgpc : allPc) {
				if (tgpc.isGm()) {
					tgpc.sendPackets(new S_ToGmMessage("人物:" + name + " 速度異常斷線!"));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 封包速度正常與否的檢測
	 * @param type 檢測類型
	 * @return 0:正常 1:異常 2:連續異常
	 */
	public int checkInterval(final ACT_TYPE type) {
		if (!ConfigOther.SPEED) {
			return R_OK;
		}
		int result = R_OK;
		try {
			final long now = System.currentTimeMillis();
			long interval = now - this._actTimers.get(type);
			final int rightInterval = this.getRightInterval(type);

			interval *= CHECK_STRICTNESS;
			
			if ((0 < interval) && (interval < rightInterval)) {
				this._injusticeCount++;
				toGmErrMsg(this._pc.getName(), this._injusticeCount);
				this._justiceCount = 0;
				if (this._injusticeCount >= INJUSTICE_COUNT_LIMIT) {// 允許錯誤次數
					this.doDisconnect();
					return R_DISCONNECTED;
				}
				result = R_DETECTED;

			} else if (interval >= rightInterval) {
				this._justiceCount++;
				if (this._justiceCount >= JUSTICE_COUNT_LIMIT) {// 連續正常 恢復計算
					this._injusticeCount = 0;
					this._justiceCount = 0;
				}
			}

			this._actTimers.put(type, now);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 正常的速度
	 * @param type 檢測類型
	 * @return 正常應該接收的速度(MS)
	 */
	private int getRightInterval(final ACT_TYPE type) {
		int interval = 0;

		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(
					this._pc.getTempCharGfx(), this._pc.getCurrentWeapon() + 1);
			break;

		case MOVE:
			interval = SprTable.get().getMoveSpeed(
					this._pc.getTempCharGfx(), this._pc.getCurrentWeapon());
			break;

		default:
			return 0;
		}
		return intervalR(type, interval);
	}

	private int intervalR(final ACT_TYPE type, int interval) {
		try {
			if (this._pc.isHaste()) {
				interval *= 0.755;// 0.755
			}
			
			if (type.equals(ACT_TYPE.MOVE) && this._pc.isFastMovable()) {
				interval *= 0.665;// 0.665
			}

			if (type.equals(ACT_TYPE.ATTACK)) {
				interval *= 0.775;// 0.775
			}
			
			if (this._pc.isBrave()) {
				interval *= 0.755;// 0.755
			}
			
			if (this._pc.isBraveX()) {
				interval *= 0.755;// 0.755
			}
			
			if (this._pc.isElfBrave()) {
				interval *= 0.855;// 0.855
			}
			
			if (type.equals(ACT_TYPE.ATTACK) && this._pc.isElfBrave()) {
				interval *= 0.9;// 0.9
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return interval;
	}
}
