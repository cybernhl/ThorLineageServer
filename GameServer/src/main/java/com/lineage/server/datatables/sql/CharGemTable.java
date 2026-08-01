package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.CharGemStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ItemGem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldItem;

/**
 * 
 * 類名稱：CharGemTable<br>
 * 類描述：武器寶石鑲嵌系統<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:13:27<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class CharGemTable implements CharGemStorage {

	private static final Log _log = LogFactory.getLog(CharGemTable.class);

	private static final Map<Integer, L1ItemGem> _GemMap = new HashMap<Integer, L1ItemGem>();

	/**
	 * 資料預先載入  
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement( "SELECT * FROM `character_gem`");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				final int item_obj_id = rs.getInt("item_obj_id");
				final int dmg = rs.getInt("dmg");
				final int dmgew = rs.getInt("dmgew");
				final int hit = rs.getInt("hit");
				final int punchcount = rs.getInt("punchcount");
				final String itemname = rs.getString("itemname");
				final String pcname = rs.getString("pcname");

				final L1ItemGem Gem = new L1ItemGem();
				Gem.set_item_obj_id(item_obj_id);
				Gem.set_dmg(dmg);
				Gem.set_dmgew(dmgew);
				Gem.set_hit(hit);
				Gem.set_punchcount(punchcount);
				Gem.set_itemname(itemname);
				Gem.set_pcname(pcname);
				
				addValue(item_obj_id, Gem);
				i++;
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入武器寶石鑲嵌系統: " + i + "(" + timer.get() + "ms)");
	}

	/**
	 * 初始化建立資料
	 * @param item_obj_id
	 * @param value
	 */
	private static void addValue(final int item_obj_id, final L1ItemGem Gem) {
		final L1ItemInstance item = WorldItem.get().getItem(item_obj_id);
		boolean isError = true;
		if (item != null) {
			if (item.get_Gem_name() == null) {
				item.set_Gem_name(Gem);
			}
			isError = false;
		}

		if (isError) {
			errorItem(item_obj_id);
		}
	}

	/**
	 * 刪除 錯誤/遺失 物品資料
	 * @param objid
	 */
	private static void errorItem(int item_obj_id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM `character_gem` WHERE `item_obj_id`=?");
			pstm.setInt(1, item_obj_id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 增加物額外屬性資料
	 * @param item_obj_id
	 * @param Gem
	 * @throws Exception
	 */
	@Override
	public void storeItem(final int item_obj_id, final L1ItemGem Gem) throws Exception {
		if (_GemMap.get(item_obj_id) != null) {
			return;
		}
		_GemMap.put(item_obj_id, Gem);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_gem` SET `item_obj_id`=?,`dmg`=?,`dmgew`=?,`hit`=?,`punchcount`=?,`itemname`=?,`pcname`=?");

			int i = 0;
			pstm.setInt(++i, item_obj_id);
			pstm.setInt(++i, Gem.get_dmg());
			pstm.setInt(++i, Gem.get_dmgew());
			pstm.setInt(++i, Gem.get_hit());
			pstm.setInt(++i, Gem.get_punchcount());
			pstm.setString(++i, Gem.get_itemname());
			pstm.setString(++i, Gem.get_pcname());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 更新物額外屬性資料
	 * @param item_obj_id
	 * @param Gem
	 */
	@Override
	public void updateItem(final int item_obj_id, final L1ItemGem Gem) {
		Connection co = null;
		PreparedStatement pm = null;
		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement(
					"UPDATE `character_gem` SET `dmg`=?,`dmgew`=?,`hit`=?,`punchcount`=?,`itemname`=?,`pcname`=? WHERE `item_obj_id`=?");
			
			int i = 0;
			pm.setInt(++i, Gem.get_dmg());
			pm.setInt(++i, Gem.get_dmgew());
			pm.setInt(++i, Gem.get_hit());
			pm.setInt(++i, Gem.get_punchcount());
			pm.setString(++i, Gem.get_itemname());
			pm.setString(++i, Gem.get_pcname());
			
			pm.setInt(++i, item_obj_id);
			
			pm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
	}
}
