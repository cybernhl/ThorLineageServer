package com.lineage.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PinkName;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;

/**
 * 紅名設置
 * @author DaiEn
 *
 */
public class L1PinkName {

	public static final Log _log = LogFactory.getLog(L1PinkName.class);

	private L1PinkName() {
	}

	private static class PinkNameTimer implements Runnable {
		
		private L1PcInstance _attacker = null;
		private Point _point = null;
		private int _mapid = -1;
		
		public PinkNameTimer(final L1PcInstance attacker) {
			_attacker = attacker;
			_point = new Point(attacker.getX(), attacker.getY());
			_mapid = attacker.getMapId();
		}

		@Override
		public void run() {
			for (int i = 0; i < 40; i++) {
				try {
					Thread.sleep(1000);
					
				} catch (final Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				if (_mapid != _attacker.getMapId()) {
					break;
				}
				if (!_attacker.getLocation().isInScreen(_point)) {
					break;
				}
				// 紅名者死亡中止
				if (_attacker.isDead()) {
					break;
				}
			}
			stopPinkName();
		}

		private void stopPinkName() {
			_attacker.sendPacketsAll(new S_PinkName(_attacker.getId(), 0));
			_attacker.setPinkName(false);
		}
	}

	/**
	 * 紅名設置判斷
	 * @param tgpc 被攻擊者
	 * @param atk 攻擊者
	 */
	public static void onAction(final L1PcInstance tgpc, final L1Character atk) {
		// 物件為空
		if ((tgpc == null) || (atk == null)) {
			//_log.error("物件為空");
			return;
		}
		// 攻擊者非人物
		if (!(atk instanceof L1PcInstance)) {
			//_log.error("攻擊者非人物");
			return;
		}
		
		final L1PcInstance attacker = (L1PcInstance) atk;
		// 自己
		if (tgpc.getId() == attacker.getId()) {
			//_log.error("攻擊者自己");
			return;
		}

		// 是決鬥對像
		if (attacker.getFightId() == tgpc.getId()) {
			//_log.error("是決鬥對像");
			return;
		}
		
		// 被攻擊者在非一般區域
		if (tgpc.getZoneType() != 0) {
			//_log.error("被攻擊者在非一般區域");
			return;
		}
		
		// 攻擊者在非一般區域
		if (attacker.getZoneType() != 0) {
			//_log.error("攻擊者在非一般區域");
			return;
		}

		boolean isNowWar = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(tgpc);
		if (castleId != 0) {// 戰爭旗內
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}
		
		// 戰爭中
		if (isNowWar == true) {
			//_log.error("戰爭中");
			return;
		}

		// 被攻擊者正義質小於0
		if (tgpc.getLawful() < 0) {
			//_log.error("被攻擊者正義質小於0");
			return;
		}
		
		// 被攻擊者已在紅名狀態
		if (tgpc.isPinkName()) {
			//_log.error("被攻擊者已在紅名狀態");
			return;
		}

		attacker.sendPacketsAll(new S_PinkName(attacker.getId(), 40));
		
		if (!attacker.isPinkName()) {
			attacker.setPinkName(true);

			final PinkNameTimer pink = new PinkNameTimer(attacker);
			GeneralThreadPool.get().execute(pink);
		}
	}
}
