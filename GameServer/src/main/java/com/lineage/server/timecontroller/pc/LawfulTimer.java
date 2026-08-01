package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

/**
 * PC Lawful更新處理 時間軸<BR>
 * 血盟技能光環 顯示處理<BR>
 * @author dexc
 *
 */
public class LawfulTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(LawfulTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 650;
		_timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	private static int _time = 0;// 計時器

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}
			
			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (check(tgpc)) {
					tgpc.onChangeLawful();

					if (_time % 20 == 0) {
						switch (tgpc.getMapId()) {
						case 340:// 古魯丁商店村
						case 350:// 奇巖商店村
						case 360:// 歐瑞商店村
						case 370:// 銀騎士商店村
							break;
							
						default:
							// 檢查城堡戰爭狀態
							if (ServerWarExecutor.get().checkCastleWar() <= 0) {
								if (checkC(tgpc)) {
									showClan(tgpc);
								}
							}
							break;
						}
					}
					Thread.sleep(1);
				}
			}
			
			/*for (final L1PcInstance tgpc : all) {
				Thread.sleep(1);
				if (check(tgpc)) {
					tgpc.onChangeLawful();

					if (_time % 20 == 0) {
						switch (tgpc.getMapId()) {
						case 340:// 古魯丁商店村
						case 350:// 奇巖商店村
						case 360:// 歐瑞商店村
						case 370:// 銀騎士商店村
							break;
							
						default:
							// 檢查城堡戰爭狀態
							if (ServerWarExecutor.get().checkCastleWar() <= 0) {
								if (checkC(tgpc)) {
									showClan(tgpc);
								}
							}
							break;
						}
					}
				}
			}*/
			_time++;

		} catch (final Exception e) {
			_log.error("Lawful更新處理時間軸異常重啟", e);
			PcOtherThreadPool.get().cancel(_timer, false);
			final LawfulTimer lawfulTimer = new LawfulTimer();
			lawfulTimer.start();
		}
	}

	/**
	 * 主判斷
	 * @param tgpc 
	 * @return true:執行 false:不執行
	 */
	private static boolean check(L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}
			
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			
			if (tgpc.isTeleport()) {
				return false;
			}
			
			if (tgpc.isDead()) {// 死亡
				return false;
			}

			if (tgpc.getCurrentHp() <= 0) {// 目前HP小於0
				return false;
			}
			
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 血盟技能光環系統 判斷
	 * @return true:執行 false:不執行
	 */
	private static boolean checkC(L1PcInstance tgpc) {
		try {
			if (tgpc.getClan() == null) {// 無血盟
				return false;
			}

			if (!tgpc.getClan().isClanskill()) {// 血盟無血盟技能
				return false;
			}

			final int count = tgpc.getClan().getOnlineClanMemberSize();
			if (count < 16) {// 血盟人數16人以下
				return false;
			}
			
			if (tgpc.get_other().get_clanskill() == 0) {// 無血盟技能
				return false;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * 展示效果
	 */
	private static void showClan(L1PcInstance tgpc) {
		try {
			final int count = tgpc.getClan().getOnlineClanMemberSize();
			
			if (count >= 30) {
				// 送出封包
				tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), 5201));

			} else {
				// 送出封包
				tgpc.sendPacketsX8(new S_SkillSound(tgpc.getId(), 5263));
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
