package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1TeleportAction;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * NPC自定對話傳送
 * 
 * @author juonena
 * 
 */
public class NpcTeleportActionTable {

	private static final Log _log = LogFactory.getLog(NpcTeleportActionTable.class);

	private static NpcTeleportActionTable _instance;

	private static final Map<Integer, HashMap<String, L1TeleportAction>> _teleportList = new HashMap<Integer, HashMap<String, L1TeleportAction>>();

	public static NpcTeleportActionTable get() {
		if (_instance == null) {
			_instance = new NpcTeleportActionTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `npc_teleport`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int npc_id = rs.getInt("npc_id");
				final String cmd = rs.getString("orderid");
				final int check_class = rs.getInt("check_class");// 檢查職業 (0=不檢查 1=王 2=騎 4=妖 8=法 16=黑 32=龍 64=幻, 如需檢查2種以上請相加即可 例如 檢查王跟法 那就是1+8=輸入9)
				final int check_lawful = rs.getInt("check_lawful");// 檢查正義屬性 (0= 不檢查 1=中立 2=正義 3=邪惡)
				final int min = rs.getInt("min");// 等級限制
				final int max = rs.getInt("max");// 等級限制
				final int start_week = rs.getInt("start_week");// 限制星期幾傳送 1~7
				final int start_hour = rs.getInt("start_hour");// 限制幾點傳送 0~23
				final int end_hour = rs.getInt("end_hour");// 限制幾點傳出 0~23
				final int time = rs.getInt("time");
				final int itemid = rs.getInt("itemid");
				final int count = rs.getInt("count");
				final int locx = rs.getInt("locx");
				final int locy = rs.getInt("locy");
				final int mapid = rs.getInt("mapid");

				L1TeleportAction teleport = new L1TeleportAction();
				
				teleport.setCheckClass(check_class);
				teleport.setCheckLawful(check_lawful);
				teleport.setMin(min);
				teleport.setMax(max);
				
				teleport.setStartWeek(start_week);
				teleport.setStartHour(start_hour);
				teleport.setEndHour(end_hour);
				teleport.setTime(time);
				
				teleport.setItemId(itemid);
				teleport.setCount(count);
				
				teleport.setLocx(locx);
				teleport.setLocy(locy);
				teleport.setMapid(mapid);
				
				HashMap<String, L1TeleportAction> _map = _teleportList.get(npc_id);
				if (_map == null) {
					_map = new HashMap<String, L1TeleportAction>();
					_map.put(cmd, teleport);
					_teleportList.put(npc_id, _map);

				} else {
					_map.put(cmd, teleport);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入NPC自定傳送點設置數量: " + _teleportList.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 取得傳送資料
	 * @param npcid
	 * @param cmd
	 * @return
	 */
	public L1TeleportAction get_loc(final int npcid, final String cmd) {
		if (_teleportList.containsKey(npcid)) {
			HashMap<String, L1TeleportAction> list = _teleportList.get(npcid);
			if (list.containsKey(cmd)) {
				return list.get(cmd);
			}
		}
		return null;
	}
}
