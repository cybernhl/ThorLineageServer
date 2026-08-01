package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.ProtocolHandlerIp;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 禁止IP列表<br>
 * 類名稱：BadIpDatabase<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 上午9:47:43<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class BadIpDatabase {

	private static final Log _log = LogFactory.getLog(CharObjidTable.class);
	 
	static private List<ProtocolHandlerIp> list;

	/**
	 * 載入數據
	 */
	static public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		
		list = new ArrayList<ProtocolHandlerIp>();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM bad_ip");
			rs = ps.executeQuery();
			while (rs.next()) {
				ProtocolHandlerIp i = new ProtocolHandlerIp();
				i.setIp(rs.getString("ip"));
				try {
					i.setTime(rs.getTimestamp("register_date").getTime());
				} catch (Exception e) {
				}

				list.add(i);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
		}
		_log.info("載入禁止IP列表數量: " + list.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 比對數據
	 * @param ip
	 * @return
	 */
	static public ProtocolHandlerIp find(String ip) {
		// 搜索
		for (ProtocolHandlerIp i : list) {
			String check_ip = i.getIp();

			int idx = check_ip.indexOf("+");
			if (idx >= 0)
				check_ip = check_ip.substring(0, idx);
			if (ip.startsWith(check_ip))
				return i;
		}
		return null;
	}

	static public int getSize() {
		return list.size();
	}

}
