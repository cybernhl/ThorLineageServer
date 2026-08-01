package com.lineage.server.timecontroller.server;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 坐船系統時間軸
 * 
 * @author terry0412
 */
public class ServerShipTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerShipTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1PcInstance, Long> MAP = new ConcurrentHashMap<L1PcInstance, Long>();

	public void start() {
		final int timeMillis = 5000; // 5秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			checkShipTime();

		} catch (final Exception e) {
			_log.error("坐船系統時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final ServerShipTimer shipTimer = new ServerShipTimer();
			shipTimer.start();
		}
	}
	private void checkShipTime() {
		long servertime = L1GameTimeClock.getInstance().currentTime()
				.getSeconds();
		long nowtime = servertime % 86400;
		// 到達港口時間修正 
/*		if (nowtime >= 90 * 60 && nowtime < 179 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 359 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 539 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 719 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 899 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1079 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1259 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1439 * 60 // 22.30~24
			) {*/
		if (nowtime >= 660 * 60 && nowtime < 720 * 60 // 11~12
				|| nowtime >= 900 * 60 && nowtime < 960 * 60 // 15~16
				|| nowtime >= 1140 * 60 && nowtime < 1200 * 60 // 19~20
				|| nowtime >= 1380 * 60 && nowtime < 0 // 23~00
			) {
			for (L1PcInstance pc : World.get().getAllPlayers()) {//到達遺忘
				if (pc.getMapId() == 83) {
					pc.getInventory().consumeItem(40300, 1);
					L1Teleport.teleport(pc, 32936, 33057, (short) 70, 0, false);
				}
			}
		}
/*		if (nowtime >= 0  && nowtime < 89 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 269 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 449 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 629 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 809 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 989 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1169 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1349 * 60 // 21~22.30
			) {*/
		if (nowtime >= 780 * 60  && nowtime < 840 * 60 // 13 ~ 14
				|| nowtime >= 1020 * 60 && nowtime < 1080 * 60 // 17~18
				|| nowtime >= 1260 * 60 && nowtime < 1320 * 60 // 21~22
				|| nowtime >= 60 * 60 && nowtime < 120 * 60 // 1~2
			) {
			for (L1PcInstance pc : World.get().getAllPlayers()) {//到達海音
				if (pc.getMapId() == 84) {
					pc.getInventory().consumeItem(40301, 1);
					L1Teleport.teleport(pc, 33426, 33499, (short) 4, 0, false);
				}
			}
		}
/*		if (nowtime >= 0  && nowtime < 90 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 270 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 450 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 630 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 810 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 990 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1170 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1350 * 60 // 21~22.30
			) {*/
/*		if (nowtime >= 0  && nowtime < 89 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 269 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 449 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 629 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 809 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 989 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1169 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1349 * 60 // 21~22.30
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 447) {
					pc.getInventory().consumeItem(40302, 1);
					L1Teleport.teleport(pc, 32297, 33087, (short) 440, 0, false);
				}
			}
		}*/
/*		if (nowtime >= 90 * 60 && nowtime < 180 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 360 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 540 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 720 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 900 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1080 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1260 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1440 * 60 // 22.30~24
			) {*/
/*		if (nowtime >= 90 * 60 && nowtime < 179 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 359 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 539 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 719 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 899 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1079 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1259 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1439 * 60 // 22.30~24
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 446) {
					pc.getInventory().consumeItem(40303, 1);
					L1Teleport.teleport(pc, 32750, 32874, (short) 445, 0, false);
				}
			}
		}*/
/*		if (nowtime >= 0 && nowtime < 90 * 60
				|| nowtime >= 180 * 60 && nowtime < 270 * 60
				|| nowtime >= 360 * 60 && nowtime < 450 * 60
				|| nowtime >= 540 * 60 && nowtime < 630 * 60
				|| nowtime >= 720 * 60 && nowtime < 810 * 60
				|| nowtime >= 900 * 60 && nowtime < 990 * 60
				|| nowtime >= 1080 * 60 && nowtime < 1170 * 60
				|| nowtime >= 1260 * 60 && nowtime < 1350 * 60) {*/
		if (nowtime >= 780 * 60  && nowtime < 840 * 60 // 13 ~ 14
				|| nowtime >= 1020 * 60 && nowtime < 1080 * 60 // 17~18
				|| nowtime >= 1260 * 60 && nowtime < 1320 * 60 // 21~22
				|| nowtime >= 60 * 60 && nowtime < 120 * 60 // 1~2
			) {
			for (L1PcInstance pc : World.get().getAllPlayers()) { // 到達古魯丁
				if (pc.getMapId() == 5) { // 船到達港口人在船上
					pc.getInventory().consumeItem(40299, 1);
					L1Teleport.teleport(pc, 32542, 32726, (short) 4, 0, false); // 古魯丁港口
				}
			}
		}
/*		if (nowtime >= 90 * 3600 && nowtime < 180 * 3600
				|| nowtime >= 270 * 3600 && nowtime < 360 * 60
				|| nowtime >= 450 * 3600 && nowtime < 540 * 60
				|| nowtime >= 630 * 3600 && nowtime < 720 * 60
				|| nowtime >= 810 * 3600 && nowtime < 900 * 60
				|| nowtime >= 990 * 3600 && nowtime < 1080 * 60
				|| nowtime >= 1170 * 3600 && nowtime < 1260 * 60
				|| nowtime >= 1350 * 3600 && nowtime < 1440 * 60) {*/
		if (nowtime >= 660 * 60 && nowtime < 720 * 60 // 11~12
				|| nowtime >= 900 * 60 && nowtime < 960 * 60 // 15~16
				|| nowtime >= 1140 * 60 && nowtime < 1200 * 60 // 19~20
				|| nowtime >= 1380 * 60 && nowtime < 0 // 23~00
			) {
			for (L1PcInstance pc : World.get().getAllPlayers()) { // 往說話之島的船
				if (pc.getMapId() == 6) { // 船到達港口人在船上
					pc.getInventory().consumeItem(40298, 1);
					L1Teleport.teleport(pc, 32632, 32981, (short) 0, 0, false); // 說話之島港口
				}
			}
		}
		// 到達港口時間修正  end
	}

}
