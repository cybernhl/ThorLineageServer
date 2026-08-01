package com.lineage.server.timecontroller.server;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * NPC傳送計時地圖時間軸
 * 
 * @author juonena
 * 
 */
public class NpcTeleportUseMapTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcTeleportUseMapTimer.class);

	private ScheduledFuture<?> _timer;

	// <L1PcInstance, <mapid, time>>
	private static final Map<L1PcInstance, HashMap<Integer, Integer>> MAP = new HashMap<L1PcInstance, HashMap<Integer, Integer>>();

	public void start() {
		
		final int timeMillis = 1000; // 1秒
		
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	/**
	 * 加入計時地圖物件清單<BR>
	 * 同時更新地圖使用時間<BR>
	 * 送出時間封包
	 * 
	 * @param pc
	 * @param time 秒
	 */
	public static void put(final L1PcInstance pc, final int mapid, final int time) {
		pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIME, time));
		HashMap<Integer, Integer> list = MAP.get(pc);
		if (list != null) {
			list.put(mapid, time);
		} else {
			list = new HashMap<Integer, Integer>();
			list.put(mapid, time);
			MAP.put(pc, list);
		}
	}

	@Override
	public void run() {
		try {
			// 包含元素
			if (!MAP.isEmpty()) {
				for (final L1PcInstance pc : MAP.keySet()) {
					// 取得PC清單
					HashMap<Integer, Integer> list = MAP.get(pc);
					
					// 循環地圖清單
					for (Integer map : list.keySet()) {
						
						// 取回剩餘時間
						Integer value = list.get(map);
						value--;
						
						if (value <= 0) {
							teleport(pc);
							Thread.sleep(1);

						} else {
							// 更新可用時間
							MAP.put(pc, list);
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error("計時地圖時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final NpcTeleportUseMapTimer useMapTimer = new NpcTeleportUseMapTimer();
			useMapTimer.start();
		}
	}

	/**
	 * 傳出PC
	 * 
	 * @param item
	 */
	public static void teleport(final L1PcInstance pc) {
		MAP.remove(pc);

		if (World.get().getPlayer(pc.getName()) == null) {
			// 人物離開世界
			return;
		}

		if (pc.getMapId() == pc.get_other().get_usemap()) {
			L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
		}
		pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
	}
}
