package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;

/**
 * PC 鬼魂模式處理 時間軸
 * @author dexc
 *
 */
public class PcGhostTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PcGhostTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1100;// 1秒
		_timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

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
				// 非鬼魂狀態
				if (!tgpc.isGhost()) {
					continue;
				}
				
				int time = tgpc.get_ghostTime();
				time--;
				check(tgpc, time);
				Thread.sleep(1);
			}
			
			/*for (final L1PcInstance tgpc : all) {
				// 非鬼魂狀態
				if (!tgpc.isGhost()) {
					continue;
				}
				
				int time = tgpc.get_ghostTime();
				time--;
				check(tgpc, time);
				Thread.sleep(1);
			}*/

		} catch (final Exception e) {
			_log.error("PC 鬼魂模式處理時間軸異常重啟", e);
			PcOtherThreadPool.get().cancel(_timer, false);
			final PcGhostTimer pcHprTimer = new PcGhostTimer();
			pcHprTimer.start();
		}
	}

	/**
	 * 檢查鬼魂模式時間
	 * @param tgpc
	 * @param time
	 */
	private static void check(final L1PcInstance tgpc, final Integer time) {
		if (time > 0) {
			// 角色死亡加入移出清單
			if (!tgpc.isDead()) {
				// 更新
				tgpc.set_ghostTime(-1);
				
			} else {
				// 更新
				tgpc.set_ghostTime(time);
			}

		} else {
			// 時間到
			tgpc.set_ghostTime(-1);

			// 未斷線移除狀態
			if (tgpc.getNetConnection() != null) {
				outPc(tgpc);
			}
		}
	}

	/**
	 * 離開鬼魂模式(傳送回出發點)
	 * @param tgpc
	 */
	private static void outPc(final L1PcInstance tgpc) {
		try {
			if (tgpc != null) {
				tgpc.makeReadyEndGhost();
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
