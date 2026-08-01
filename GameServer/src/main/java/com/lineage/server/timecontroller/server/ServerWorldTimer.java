package com.lineage.server.timecontroller.server;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_GameTime;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 世界時間計算<br>
 * 類名稱：ServerWorldTimer<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月30日 下午5:30:26<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class ServerWorldTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerWorldTimer.class);

	private ScheduledFuture<?> _timer;

	
	public void start() {
		final int timeMillis = 1 * 500;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
	
	@Override
	public void run() {
		try {
			
			long currentTime = System.currentTimeMillis();
			// hhmm 取值為12小時制   HHmm取值為24小時制
			SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
			Date date = new Date(currentTime);
			
			// formatter.format(date)為String 強制轉換為long
			long time = Long.parseLong(formatter.format(date));
			
			// 更新world_time
			
			int  uptime = L1GameTimeClock.getInstance().currentTime().getSeconds();
			if ((uptime%300) == 0) {// 每分鐘一次更新目前遊戲時間 條件滿足才執行角色判斷
				final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
				// 不包含元素
				if (allPc.isEmpty()) {
					return;
				}				
				ServerReading.get().Updata(time);
				for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
					final L1PcInstance tgpc = iter.next();
					if (check(tgpc)) {
						tgpc.sendPackets(new S_GameTime(uptime));
					}
				}
			}
		} catch (final Exception e) {
			_log.error("世界時間計算時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final ServerWorldTimer worldtimer = new ServerWorldTimer();
			worldtimer.start();
		}
	}
	
	/**
	 * 判斷
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	protected boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
}
