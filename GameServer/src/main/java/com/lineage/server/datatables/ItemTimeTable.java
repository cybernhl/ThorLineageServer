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
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 物品可用時間指定
 *
 * @author dexc
 *
 */
    public class ItemTimeTable {

	private static final Log _log = LogFactory.getLog(ItemTimeTable.class);

	private static ItemTimeTable _instance;
	
	// 物品編號 / 可用時間(小時)
	public static final Map<Integer, Integer> TIME = new HashMap<Integer, Integer>();
	
	   /**
     * 靜態初始化器，由JVM來保證線程安全.
     */
    private static class Holder {
        static ItemTimeTable instance = new ItemTimeTable();
    }

    /**
     * 取得該類的實例.
     */
    public static ItemTimeTable get() {
        return Holder.instance;
    }

    private ItemTimeTable() {
    }

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_item_time`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int key = rs.getInt("itemid");
				final int value = rs.getInt("h");
				TIME.put(key, value);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入掉落時間限制道具: " + TIME.size() + "(" + timer.get() + "ms)");
	}
}