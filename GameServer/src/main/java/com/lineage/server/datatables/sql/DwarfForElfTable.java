package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.datatables.InnKeyTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.storage.DwarfForElfStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 精靈倉庫物件清單 
 * @author dexc
 *
 */
public class DwarfForElfTable implements DwarfForElfStorage {

	private static final Log _log = LogFactory.getLog(DwarfForElfTable.class);
	
	// 精靈倉庫物件清單 (帳號名稱) (物品清單)
	private static final Map<String, CopyOnWriteArrayList<L1ItemInstance>> _itemList = 
		new ConcurrentHashMap<String, CopyOnWriteArrayList<L1ItemInstance>>();

	/**
	 * 預先加載
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT * FROM `character_warehouse_elf`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int objid = rs.getInt("id");
				final String account_name = rs.getString("account_name").toLowerCase();

				final boolean account = AccountReading.get().isAccountUT(account_name);
				if (account) {
					final int item_id = rs.getInt("item_id");
					//final String item_name = rs.getString("item_name");
					final long count = rs.getLong("count");
					//final int is_equipped = rs.getInt("is_equipped");
					final int enchantlvl = rs.getInt("enchantlvl");
					final int is_id = rs.getInt("is_id");
					final int durability = rs.getInt("durability");
					final int charge_count = rs.getInt("charge_count");
					final int remaining_time = rs.getInt("remaining_time");
					final Timestamp last_used = rs.getTimestamp("last_used");
					final int bless = rs.getInt("bless");
					final int attr_enchant_kind = rs.getInt("attr_enchant_kind");
					final int attr_enchant_level = rs.getInt("attr_enchant_level");
					final String gamno = rs.getString("gamno");
					final int attach_index = rs.getInt("attach_index");
					final int special_stat = rs.getInt("special_stat");
					final int gem_hole = rs.getInt("gem_hole");
					final int gem_hole_index = rs.getInt("gem_hole_index");
					final int proctect = rs.getInt("proctect");
					final int proctectRom = rs.getInt("proctect_rom");
					final int proctectType = rs.getInt("proctect_type");
					final int ability_pos_1 = rs.getInt("ability_pos_1");
					final int ability_pos_2 = rs.getInt("ability_pos_2");
					final int ability_pos_3 = rs.getInt("ability_pos_3");
					final int can_ability_type = rs.getInt("can_ability_type");
					
					final L1ItemInstance item = new L1ItemInstance();
					item.setId(objid);

					final L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
					if (itemTemplate == null) {
						// 無該物品資料 移除
						errorItem(objid);
						continue;
					}
					item.setItem(itemTemplate);
					item.setCount(count);
					item.setEquipped(false);
					item.setEnchantLevel(enchantlvl);
					item.setIdentified(is_id != 0 ? true : false);
					item.set_durability(durability);
					item.setChargeCount(charge_count);
					item.setRemainingTime(remaining_time);
					item.setLastUsed(last_used);
					item.setBless(bless);
					item.setAttrEnchantKind(attr_enchant_kind);
					item.setAttrEnchantLevel(attr_enchant_level);
					item.setGamNo(gamno);
					item.setAttachIndex(attach_index);
					item.setSpecialStat(special_stat);
					item.setGemHole(gem_hole);
					item.setGemHoleIndex(gem_hole_index);
					item.setproctect(proctect > 0);
					item.setProctectRom(proctectRom);
					item.setProctectType(proctectType);
					item.setAbilityPos1(ability_pos_1);
					item.setAbilityPos2(ability_pos_2);
					item.setAbilityPos3(ability_pos_3);
					item.setCanAbilityType(can_ability_type);
					// 登入鑰匙紀錄
					if (item.getItem().getItemId() == 40312) {
						InnKeyTable.checkey(item);
					}
					addItem(account_name, item);
					i++;
					
				} else {
					deleteItem(account_name);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入精靈倉庫物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get()
				+ "ms)");
	}

	/**
	 * 刪除錯誤物品資料
	 * @param objid
	 */
	private static void errorItem(int objid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM `character_warehouse_elf` WHERE `id`=?");
			pstm.setInt(1, objid);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 建立資料
	 * @param accName
	 * @param item
	 */
	private static void addItem(final String account_name, final L1ItemInstance item) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(account_name);
		if (list == null) {
			list = new CopyOnWriteArrayList<L1ItemInstance>();
			if (!list.contains(item)) {
				list.add(item);
			}
			
		} else {
			if (!list.contains(item)) {
				list.add(item);
			}
		}
		// 將物品加入世界
		if (World.get().findObject(item.getId()) == null) {
			World.get().storeObject(item);
		}
		_itemList.put(account_name, list);
	}

	/**
	 * 刪除遺失資料
	 * @param objid
	 */
	private static void deleteItem(final String account_name) {
		final CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(account_name);
		if (list != null) {
			// 移出世界
			for (L1ItemInstance item : list) {
				World.get().removeObject(item);
			}
		}
		
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement(
					"DELETE FROM `character_warehouse_elf` WHERE `account_name`=?");
			ps.setString(1, account_name);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 傳回精靈倉庫數據
	 * @return 
	 */
	@Override
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String account_name) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(account_name);
		if (list != null) {
			return list;
		}
		return null;
	}
	
	/**
	 * 刪除精靈倉庫資料(完整)
	 * @param account_name
	 */
	@Override
	public void delUserItems(final String account_name) {
		deleteItem(account_name);
	}
	
	/**
	 * 該精靈倉庫是否有指定數據
	 * @param account_name
	 * @param objid
	 * @param count
	 * @return 
	 */
	@Override
	public boolean getUserItems(final String account_name, final int objid, final int count) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(account_name);
		if (list != null) {
			if (list.size() <= 0) {
				return false;
			}
			for (L1ItemInstance item : list) {
				if (item.getId() == objid) {
					if (item.getCount() >= count) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 加入精靈倉庫數據
	 */
	@Override
	public void insertItem(final String account_name, final L1ItemInstance item) {
		addItem(account_name, item);
		
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_warehouse_elf` SET `id`=?," +
					"`account_name`=?,`item_id`= ?,`item_name`=?,`count`=?," +
					"`is_equipped`=0,`enchantlvl`=?,`is_id`=?,`durability`=?," +
					"`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?," +
					"`attr_enchant_kind`=?,`attr_enchant_level`=?,`gamno`=?, `attach_index`=?,proctect = ?,proctect_rom = ?,proctect_type = ?,gem_hole=?,gem_hole_index=?,special_stat=?,ability_pos_1=?,ability_pos_2=?,ability_pos_3=?,can_ability_type=?");

			int i = 0;
			pstm.setInt(++i, item.getId());
			pstm.setString(++i, account_name);
			pstm.setInt(++i, item.getItemId());
			pstm.setString(++i, item.getItem().getName());
			pstm.setLong(++i, item.getCount());
			pstm.setInt(++i, item.getEnchantLevel());
			pstm.setInt(++i, item.isIdentified() ? 1 : 0);
			pstm.setInt(++i, item.get_durability());
			pstm.setInt(++i, item.getChargeCount());
			pstm.setInt(++i, item.getRemainingTime());
			pstm.setTimestamp(++i, item.getLastUsed());
			pstm.setInt(++i, item.getBless());
			pstm.setInt(++i, item.getAttrEnchantKind());
			pstm.setInt(++i, item.getAttrEnchantLevel());
			pstm.setString(++i, item.getGamNo());
			pstm.setInt(++i, item.getAttachIndex());
			pstm.setInt(++i, item.getproctect() ? 1 : 0);
			pstm.setInt(++i, item.getProctectRom());
			pstm.setInt(++i, item.getProctectType());
			pstm.setInt(++i, item.getGemHole());
			pstm.setInt(++i, item.getGemHoleIndex());
			pstm.setInt(++i, item.getSpecialStat());
			pstm.setInt(++i, item.getAbilityPos1ID());
			pstm.setInt(++i, item.getAbilityPos2ID());
			pstm.setInt(++i, item.getAbilityPos3ID());
			pstm.setInt(++i, item.getCanAbilityType());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 精靈倉庫資料更新(物品數量)
	 * @param item
	 */
	@Override
	public void updateItem(final L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE `character_warehouse_elf` SET `count`=? WHERE `id`=?");
			pstm.setLong(1, item.getCount());
			pstm.setInt(2, item.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 精靈倉庫物品資料刪除
	 * @param account_name
	 * @param item
	 */
	@Override
	public void deleteItem(final String account_name, final L1ItemInstance item) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(account_name);
		if (list != null) {
			list.remove(item);
			
			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = DatabaseFactoryLogin.get().getConnection();
				pstm = con.prepareStatement(
						"DELETE FROM `character_warehouse_elf` WHERE `id`=?");
				pstm.setInt(1, item.getId());
				pstm.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}
}
