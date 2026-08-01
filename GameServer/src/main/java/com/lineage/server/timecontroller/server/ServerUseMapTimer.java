package com.lineage.server.timecontroller.server;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 計時地圖時間軸
 * @author dexc
 *
 */
public class ServerUseMapTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerUseMapTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1PcInstance, Integer> MAP = 
		new ConcurrentHashMap<L1PcInstance, Integer>();

	public void start() {
		final int timeMillis = 1100;// 1.1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
	
	/**
	 * 加入計時地圖物件清單<BR>
	 * 同時更新地圖使用時間<BR>
	 * 送出時間封包
	 * @param pc
	 * @param time 秒
	 */
	public static void put(final L1PcInstance pc, final int time) {
		pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIME, time));
		pc.get_other().set_usemapTime(time);
		MAP.put(pc, new Integer(time));
	}

	@Override
	public void run() {
		try {
			// 包含元素
			if (!MAP.isEmpty()) {
				for (final L1PcInstance key : MAP.keySet()) {
					// 取回剩餘時間
					Integer value = MAP.get(key);
					value--;
					if (value <= 0) {
						teleport(key);
						Thread.sleep(1);
						
					} else {
						// 更新可用時間
						key.get_other().set_usemapTime(value);
						MAP.put(key, value);
					}
				}
			}

		} catch (final Exception e) {
			_log.error("計時地圖時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final ServerUseMapTimer useMapTimer = new ServerUseMapTimer();
			useMapTimer.start();
		}
	}

	/**
	 * 傳出PC
	 * @param item
	 */
	public static void teleport(final L1PcInstance tgpc) {
		MAP.remove(tgpc);

		if (World.get().getPlayer(tgpc.getName()) == null) {
			// 人物離開世界
			return;
		}

		if (tgpc.getMapId() == tgpc.get_other().get_usemap()) {
			L1Teleport.teleport(tgpc, 33080, 33392, (short) 4, 5, true);
		}
		tgpc.get_other().set_usemapTime(0);
		tgpc.get_other().set_usemap(-1);
		tgpc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
	}
}
