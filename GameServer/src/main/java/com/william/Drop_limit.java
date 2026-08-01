// 
// Decompiled by Procyon v0.5.36
// 

package com.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;

public class Drop_limit {
	private static Logger _log;
	private static Log _log1;
	private static Drop_limit _instance;
	public static HashMap<Integer, Drop_limit> _itemIdIndex;
	private int _itemId;
	private int _totalCount;
	private int _appearCount;
	private Calendar _next_drop_time;
	private int _drop_interval_min;
	private int _drop_interval_max;
	private static final Map<Integer, Drop_limit> _mons = new HashMap<Integer, Drop_limit>();
	static {
		Drop_limit._log = Logger.getLogger(Drop_limit.class.getName());
		Drop_limit._log1 = LogFactory.getLog(Drop_limit.class);
		Drop_limit._itemIdIndex = new HashMap<Integer, Drop_limit>();
	}

	public static Drop_limit get() {
		if (Drop_limit._instance == null) {
			Drop_limit._instance = new Drop_limit();
		}
		return Drop_limit._instance;
	}

	public static void reload() {
		Drop_limit._itemIdIndex.clear();
		Drop_limit._instance = null;
		get();
	}

	private Drop_limit() {
		loadChackDrop();
	}

	private static void loadChackDrop() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM 系統_限制掉落系統_1");
			rs = pstm.executeQuery();
			fillChackDrop(rs);
		} catch (SQLException e) {
			Drop_limit._log.log(Level.SEVERE, "error while creating 系統_限制掉落系統_1  table", e);
			return;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close((Statement) pstm);
			SQLUtil.close(con);
		}
		SQLUtil.close(rs);
		SQLUtil.close((Statement) pstm);
		SQLUtil.close(con);
	}

	private static Calendar timestampToCalendar(final Timestamp ts) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	private static void fillChackDrop(final ResultSet rs) throws SQLException {
		final PerformanceTimer timer = new PerformanceTimer();
		while (rs.next()) {
			final int itemId = rs.getInt("itemId");
			final int totalCount = rs.getInt("totalCount");
			final int appearCount = rs.getInt("appearCount");
			final Timestamp time = rs.getTimestamp("next_drop_time");
			final int drop_interval_min = rs.getInt("drop_interval_min");
			int drop_interval_max = rs.getInt("drop_interval_max");
			if (drop_interval_max < drop_interval_min) {
				drop_interval_max = drop_interval_min;
			}
			Calendar next_drop_time = null;
			if (time != null) {
				next_drop_time = timestampToCalendar(rs.getTimestamp("next_drop_time"));
			}
			final Drop_limit armor_upgrade = new Drop_limit(itemId, totalCount, appearCount, next_drop_time,
					drop_interval_min, drop_interval_max);
			Drop_limit._itemIdIndex.put(itemId, armor_upgrade);
		}
		Drop_limit._log1
				.info((Object) ("載入限制掉落系統數據資料數量: " + Drop_limit._itemIdIndex.size() + "(" + timer.get() + "ms)"));
	}

	public Drop_limit getTemplate(final int itemId) {
		return Drop_limit._itemIdIndex.get(itemId);
	}

	public Drop_limit[] getDissolveList() {
		return (Drop_limit[]) Drop_limit._itemIdIndex.values().toArray(new Drop_limit[Drop_limit._itemIdIndex.size()]);
	}

	public boolean checkItemIdCanDrop(final int itemId) {
		final Drop_limit limit = get().getTemplate(itemId);
		if (limit == null) {
			return true;
		}
		if (limit.get_totalCount() <= limit.get_appearCount()) {
			return false;
		}
		if (limit.get_next_drop_time() != null) {
			final Calendar cals = Calendar.getInstance();
			final long nowTime = System.currentTimeMillis();
			cals.setTimeInMillis(nowTime);
			return cals.after(limit.get_next_drop_time());
		}
		return true;
	}

	public void addCount(final int itemId, final int count) {
		final Drop_limit limit = get().getTemplate(itemId);
		if (limit != null) {
			limit.add_appearCount(count);
			Calendar cals = null;
			if (limit.get_next_drop_time() != null) {
				final int time = limit.get_drop_interval_min()
						+ RandomArrayList.getInt(limit.get_drop_interval_max() - limit.get_drop_interval_min());
				final long newTime = time * 60 * 1000L;
				cals = Calendar.getInstance();
				cals.setTimeInMillis(System.currentTimeMillis() + newTime);
				limit.get_next_drop_time().setTimeInMillis(cals.getTimeInMillis());
			} else if (limit.get_drop_interval_min() != 0) {
				final int time = limit.get_drop_interval_min()
						+ RandomArrayList.getInt(limit.get_drop_interval_max() - limit.get_drop_interval_min());
				final long newTime = time * 60 * 1000L;
				cals = Calendar.getInstance();
				cals.setTimeInMillis(System.currentTimeMillis() + newTime);
				limit.get_next_drop_time().setTimeInMillis(cals.getTimeInMillis());
			}
			limit.upDateNextSpawnTime(itemId, cals, limit.get_appearCount());
		}
	}

	public void upDateNextSpawnTime(final int itemId, final Calendar spawnTime, final int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("UPDATE `系統_限制掉落系統_1` SET `next_drop_time`=?, `appearCount`=? WHERE `itemId`=?");
			String fm = null;
			if (spawnTime != null) {
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				fm = sdf.format(spawnTime.getTime());
			}
			int i = 0;
			pstm.setString(++i, fm);
			pstm.setInt(++i, count);
			pstm.setInt(++i, itemId);
			pstm.execute();
		} catch (Exception e) {
			Drop_limit._log1.error((Object) e.getLocalizedMessage(), (Throwable) e);
			return;
		} finally {
			SQLUtil.close((Statement) pstm);
			SQLUtil.close(con);
		}
		SQLUtil.close((Statement) pstm);
		SQLUtil.close(con);
	}

	public Drop_limit(final int itemId, final int totalCount, final int appearCount, final Calendar next_drop_time,
			final int drop_interval_min, final int drop_interval_max) {
		this._itemId = itemId;
		this._totalCount = totalCount;
		this._appearCount = appearCount;
		this._next_drop_time = next_drop_time;
		this._drop_interval_min = drop_interval_min;
		this._drop_interval_max = drop_interval_max;
	}

	public int get_itemId() {
		return this._itemId;
	}

	public int get_totalCount() {
		return this._totalCount;
	}

	public int get_appearCount() {
		return this._appearCount;
	}

	public void add_appearCount(final int i) {
		this._appearCount += i;
	}

	public Calendar get_next_drop_time() {
		return this._next_drop_time;
	}

	public void set_next_drop_time(final Calendar i) {
		this._next_drop_time = i;
	}

	public static int CheckNpcMid(int Npc_Mid) {

		Drop_limit mon = Drop_limit.get().getMon(Npc_Mid);

		if (mon == null) {
			return 0;
		}

		int npcmid = mon._itemId;
		return npcmid;
	}

	public int get_drop_interval_min() {
		return this._drop_interval_min;
	}

	public int get_drop_interval_max() {
		return this._drop_interval_max;
	}

	public Drop_limit getMon(int Item_Id) {
		return _mons.get(Item_Id);
	}
}
