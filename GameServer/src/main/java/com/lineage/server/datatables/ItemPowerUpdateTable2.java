package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.config.Config;
import com.lineage.server.templates.L1ItemPowerUpdate;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 特殊物品升級資料
 * @author loli
 *
 */
public class ItemPowerUpdateTable2 {

	private static final Log _log = LogFactory.getLog(ItemPowerUpdateTable2.class);
	private final Lock lock = new ReentrantLock(false);

	private static Map<Integer, L1ItemPowerUpdate> _updateMap = 
			new HashMap<Integer, L1ItemPowerUpdate>();
	private final List<L1ItemPowerUpdate> updateList = new ArrayList<>();
	
	private static ItemPowerUpdateTable2 _instance;

	public static ItemPowerUpdateTable2 get() {
		if (_instance == null) {
			_instance = new ItemPowerUpdateTable2();
		}
		return _instance;
	}
	
	/**
	 * 初始化載入
	 */
	public void load() {
		lock.lock();
		_updateMap.clear();
		updateList.clear();
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_item_power_update_2`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemid = rs.getInt("itemid");
				if (ItemTable.get().getTemplate(itemid) == null) {
					_log.error("特殊物品升級資料錯誤: 沒有這個編號的道具:" + itemid);
					if (Config.DELETE) {
						delete(itemid);
					}
					continue;
				}
				final int nedid = rs.getInt("nedid");
				final int type_id = rs.getInt("type_id");
				final int order_id = rs.getInt("order_id");
				final int mode = rs.getInt("mode");
				final int random = rs.getInt("random");
				
				L1ItemPowerUpdate value = _updateMap.get(itemid);
				if (value == null) {
					value = new L1ItemPowerUpdate();
					value.set_itemid(itemid);
					value.set_nedid(nedid);
					value.set_type_id(type_id);
					value.set_order_id(order_id);
					value.set_mode(mode);
					value.set_random(random);
				}
				
				_updateMap.put(itemid, value);

				L1ItemPowerUpdate value_2 = new L1ItemPowerUpdate();
				value_2.set_itemid(itemid);
				value_2.set_nedid(nedid);
				value_2.set_type_id(type_id);
				value_2.set_order_id(order_id);
				value_2.set_mode(mode);
				value_2.set_random(random);
				updateList.add(value_2);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			lock.unlock();
		}
		_log.info("載入物品升級資料數量: " + _updateMap.size() + "(" + timer.get() + "ms)");
	}
	
	/**
	 * 刪除錯誤資料
	 * @param itemid
	 */
	public static void delete(final int itemid) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"DELETE FROM `server_item_power_update_2` WHERE `itemid`=?");
			ps.setInt(1, itemid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 資訊
	 * @param key
	 * @return
	 */
	public Map<Integer, L1ItemPowerUpdate> get_type_id(final int itemid) {
		final Map<Integer, L1ItemPowerUpdate> updateMap = 
				new HashMap<Integer, L1ItemPowerUpdate>();
		final L1ItemPowerUpdate tmp = _updateMap.get(itemid);
		if (tmp != null) {
			final int type_id = tmp.get_type_id();
			for (final L1ItemPowerUpdate value : updateList) {
				if (value.get_type_id() == type_id) {
					updateMap.put(value.get_order_id(), value);
				}
			}
		}
		return updateMap;
	}

	/**
	 * L1ItemPowerUpdate 資訊
	 * @param key
	 * @return
	 */
	public L1ItemPowerUpdate get(final int key) {
		return _updateMap.get(key);
	}

	/**
	 * Map<Integer, L1ItemPowerUpdate> 資訊
	 * @return
	 */
	public Map<Integer, L1ItemPowerUpdate> map() {
		return _updateMap;
	}
}
