package com.lineage.server.timecontroller.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 自動重啟
 *
 * @author dexc
 *
 */
public class ServerRestartTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerRestartTimer.class);

	private ScheduledFuture<?> _timer;

	private static final ArrayList<Calendar> _restartList = new ArrayList<Calendar>();

	private static Calendar _restart = null;
	
	private static String _string = "yyyy/MM/dd HH:mm:ss";
	
	private static String _startTime = null;
	
	private static String _restartTime = null;

	/**
	 * 重新啟動時間
	 * @return
	 */
	public static String get_restartTime() {
		return _restartTime;
	}

	/**
	 * 啟動時間
	 * @return
	 */
	public static String get_startTime() {
		return _startTime;
	}

	/**
	 * 距離關機小逾10分鐘
	 * @return
	 */
	public static boolean isRtartTime() {
		if (_restart == null) {
			return false;
		}
		return (_restart.getTimeInMillis() - System.currentTimeMillis()) <= (10 * 60 * 1000);
	}
	
	private static Calendar timestampToCalendar() {
		final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(_tz);

		return cal;
	}
	
	public void start() {
		if (Config.AUTORESTART == null) {
			return;
		}

		final Calendar cals = timestampToCalendar();
		
		if (_startTime == null) {
			final String nowDate = new SimpleDateFormat(_string).format(cals.getTime());
			_startTime = nowDate;
		}

		if (Config.AUTORESTART != null) {
			final String HH = new SimpleDateFormat("HH").format(cals.getTime());
			int HHi = Integer.parseInt(HH);
			final String mm = new SimpleDateFormat("mm").format(cals.getTime());
			int mmi = Integer.parseInt(mm);

			for (String hm : Config.AUTORESTART) {
				String[] hm_b = hm.split(":");
				String hh_b = hm_b[0];
				String mm_b = hm_b[1];

				int newHH = Integer.parseInt(hh_b);
				int newMM = Integer.parseInt(mm_b);

				final Calendar cal = timestampToCalendar();

				int xh = -1;
				int xhh = newHH - HHi;
				if (xhh > 0) {
					xh = xhh;

				} else {
					xh = (24 - HHi) + newHH;
				}
				
				int xm = newMM - mmi;

				cal.add(Calendar.HOUR, xh);
				cal.add(Calendar.MINUTE, xm);

				_restartList.add(cal);
			}

			for (Calendar tmpCal : _restartList) {
				if (_restart == null) {
					_restart = tmpCal;
					
				} else {
					boolean re = tmpCal.before(_restart);
					if (re) {
						_restart = tmpCal;
					}
				}
			}

		}
		
		final String restartTime = new SimpleDateFormat(_string).format(_restart.getTime());
		_restartTime = restartTime;

		_log.warn(
				"\n\r--------------------------------------------------" +
				"\n\r       開機完成時間為:" + _startTime +
				"\n\r       設置關機時間為:" + _restartTime +
				"\n\r--------------------------------------------------"
				);
		
		final int timeMillis = 60 * 1000;// 1分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			startCommand();

		} catch (final Exception e) {
			_log.error("自動重啟時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final ServerRestartTimer restartTimer = new ServerRestartTimer();
			restartTimer.start();
		}
	}

    private void startCommand() {
        if (Config.AUTORESTART != null) {
            final Calendar cals = Calendar.getInstance();
            cals.setTimeInMillis(System.currentTimeMillis());
     
      if (_restart.before(cals)) {
        Shutdown.getInstance().startShutdown(null, 300, true);
      }
    }
        
        

        if (ConfigAlt.LotterySet) {
        	Calendar cal = Calendar.getInstance(); // 目前時間
			int hour = cal.get(Calendar.HOUR_OF_DAY); // 小時
			int minute = cal.get(Calendar.MINUTE);    // 分鐘

			String nowTime = hour + ":" + minute;
			
			if (ConfigAlt.LotteryTime.equals(nowTime)) {
				List<String> listPc = new ArrayList<String>();

				for (final L1PcInstance pc : World.get().getAllPlayers()) {
					if (pc != null && pc.getOnlineStatus() != 0 
							&& pc.getNetConnection() != null && pc.getAccessLevel() <= 0) {
						listPc.add(pc.getName());
					}					
				}

				int endOfNum = 0;
				if ( listPc.size() <= ConfigAlt.LotteryQuota) {
					endOfNum = listPc.size();
				} else {
					endOfNum = ConfigAlt.LotteryQuota;
				}
				
				for(int i = 0; i < endOfNum; i++) {
					L1PcInstance tgpc = World.get().getPlayer(listPc.get(i));
					L1ItemInstance item = ItemTable.get().createItem(ConfigAlt.LotteryItemId);
					item.setCount(ConfigAlt.LotteryItemAmount);
					if (item != null) {
						if (tgpc.getInventory().checkAddItem(item, 1) == 0) {
							tgpc.getInventory().storeItem(item);
							tgpc.sendPackets(new S_ServerMessage("獲得每日抽獎禮品: " + item.getLogName()));
							World.get().broadcastPacketToAll(new S_SystemMessage("恭喜玩家"+tgpc.getName()+"獲得每日抽獎禮品: " + item.getLogName()));
							
						}
					}
				}
			}
        }
	}
}
