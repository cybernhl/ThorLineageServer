package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.config.Config;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 掉落物品資料
 *
 * @author dexc
 *
 */
public class DropTable {

	private static final Log _log = LogFactory.getLog(DropTable.class);

	private static DropTable _instance;

	public static DropTable get() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

	public void load() {
		//System.out.println(this.getClass().getSimpleName());// XXX
		final Map<Integer, ArrayList<L1Drop>> droplists = this.allDropList();

		final SetDropExecutor setDropExecutor = new SetDrop();
		setDropExecutor.addDropMap(droplists);
	}

	private Map<Integer, ArrayList<L1Drop>> allDropList() {
		final PerformanceTimer timer = new PerformanceTimer();
		final Map<Integer, ArrayList<L1Drop>> droplistMap = new HashMap<Integer, ArrayList<L1Drop>>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `droplist`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int mobId = rs.getInt("mobId");
				final int itemId = rs.getInt("itemId");
				final int min = rs.getInt("min");
				final int max = rs.getInt("max");
				final int chance = rs.getInt("chance");
				final int bless = rs.getInt("bless");
				if (check_item(itemId)) {
					final L1Drop drop = new L1Drop(mobId, itemId, min, max, chance, bless);

					ArrayList<L1Drop> dropList = droplistMap.get(drop.getMobid());
					if (dropList == null) {
						dropList = new ArrayList<L1Drop>();
						droplistMap.put(new Integer(drop.getMobid()), dropList);
					}
					dropList.add(drop);
				}
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入掉落物品資料數量: " + droplistMap.size() + "(" + timer.get() + "ms)");
		return droplistMap;
	}

	private boolean check_item(int itemId) {
		final L1Item itemTemplate = ItemTable.get().getTemplate(itemId);
		if (itemTemplate == null) {
			if (Config.DELETE) {
				// 無該物品資料 移除
				errorItem(itemId);
			}
			return false;
		}
		return true;
	}

	/**
	 * 刪除錯誤物品資料
	 * @param objid
	 */
	private static void errorItem(int itemid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM `droplist` WHERE `itemId`=?");
			pstm.setInt(1, itemid);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}