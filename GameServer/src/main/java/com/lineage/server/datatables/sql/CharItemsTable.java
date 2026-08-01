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
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.storage.CharItemsStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 人物背包資料
 * @author dexc
 *
 */
public class CharItemsTable implements CharItemsStorage {

	private static final Log _log = LogFactory.getLog(BuddyTable.class);
	
	// 背包物件清單 (人物OBJID) (物品清單)
	private static final Map<Integer, CopyOnWriteArrayList<L1ItemInstance>> _itemList = 
		new ConcurrentHashMap<Integer, CopyOnWriteArrayList<L1ItemInstance>>();
	
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
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement( "SELECT * FROM `character_items`");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				final int objid = rs.getInt("id");
				final int item_id = rs.getInt("item_id");
				final int char_id = rs.getInt("char_id");
				//final String item_name = rs.getString("item_name");

				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(char_id) != null) {
					final L1Item itemTemplate = ItemTable.get().getTemplate(item_id);
					if (itemTemplate == null) {
						// 無該物品資料 移除
						errorItem(objid);
						continue;
					}
					final long count = rs.getLong("count");
					final int is_equipped = rs.getInt("is_equipped");
					final int enchantlvl = rs.getInt("enchantlvl");
					final int is_id = rs.getInt("is_id");
					final int durability = rs.getInt("durability");
					final int charge_count = rs.getInt("charge_count");
					final int remaining_time = rs.getInt("remaining_time");
					final Timestamp last_used = rs.getTimestamp("last_used");
					final int bless = rs.getInt("bless");
					final int attr_enchant_kind = rs.getInt("attr_enchant_kind");
					final int attr_enchant_level = rs.getInt("attr_enchant_level");
					final int attach_index = rs.getInt("attach_index");
					final int special_stat = rs.getInt("special_stat");
					final int gem_hole = rs.getInt("gem_hole");
					final int gem_hole_index = rs.getInt("gem_hole_index");
					final String gamno = rs.getString("gamno");
					final int proctect = rs.getInt("proctect");
					final int proctectRom = rs.getInt("proctect_rom");
					final int proctectType = rs.getInt("proctect_type");
					final int ability_pos_1 = rs.getInt("ability_pos_1");
					final int ability_pos_2 = rs.getInt("ability_pos_2");
					final int ability_pos_3 = rs.getInt("ability_pos_3");
					final int can_ability_type = rs.getInt("can_ability_type");
					
					L1ItemInstance item = new L1ItemInstance();
					item.setId(objid);
					item.setItem(itemTemplate);
					item.setCount(count);
					item.setEquipped(is_equipped != 0 ? true : false);
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
					item.set_char_objid(char_id);
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
					
					item.getLastStatus().updateAll();
					// 登入鑰匙紀錄
					if (item.getItem().getItemId() == 40312) {
						InnKeyTable.checkey(item);
					}
					addItem(char_id, item);
					i++;
					
				} else {
					deleteItem(char_id);
				}
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入人物背包物件清單資料數量: " + _itemList.size() + "/" + i + "(" + timer.get()
				+ "ms)");
	}

	public void updateItemProtection(final L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("UPDATE `character_items` SET proctect = ?,proctect_rom = ?,proctect_type = ? WHERE `id`=?");
			pstm.setInt(1, item.getproctect() ? 1 : 0);
			pstm.setInt(2, item.getProctectRom());
			pstm.setInt(3, item.getProctectType());
			pstm.setInt(4, item.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		item.getLastStatus().updateLastUsed();
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
					"DELETE FROM `character_items` WHERE `id`=?");
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
	 * @param objid
	 * @param item
	 */
	private static void addItem(final Integer objid, final L1ItemInstance item) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
		if (list == null) {// 該人物尚無背包數據
			list = new CopyOnWriteArrayList<L1ItemInstance>();
			if (!list.contains(item)) {// 清單中不包含該物品
				list.add(item);
			}
			
		} else {
			if (!list.contains(item)) {// 清單中不包含該物品
				list.add(item);
			}
		}
		// 世界物件中不包含該OBJID數據
		if (World.get().findObject(item.getId()) == null) {
			World.get().storeObject(item);
		}
		_itemList.put(objid, list);
	}

	/**
	 * 刪除遺失資料
	 * @param objid
	 */
	private static void deleteItem(final Integer objid) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.remove(objid);
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
					"DELETE FROM `character_items` WHERE `char_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 傳回該人物背包資料
	 * @param objid
	 * @return
	 */
	@Override
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final Integer objid) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
		if (list != null) {
			return list;
		}
		return null;
	}
	
	/**
	 * 刪除人物背包資料(完整)
	 * @param objid
	 */
	@Override
	public void delUserItems(final Integer objid) {
		deleteItem(objid);
	}
	
	/**
	 * 該人物背包是否有指定數據
	 * @param pcObjid
	 * @param objid
	 * @param count 
	 * @return 
	 */
	@Override
	public boolean getUserItems(final Integer pcObjid, final int objid, final long count) {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(pcObjid);
		if (list != null) {
			for (L1ItemInstance item : list) {
				if (item.getId() == objid && item.getCount() >= count) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 是否有指定數據
	 * @param pcObjid
	 * @param objid
	 * @param count
	 * @return 
	 */
	@Override
	public boolean getUserItem(final int objid) {
		for (CopyOnWriteArrayList<L1ItemInstance> list : _itemList.values()) {
			for (L1ItemInstance item : list) {
				if (item.getId() == objid) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 傳回佣有該物品ID的人物清單<BR>
	 * (適用該物品每人只能傭有一個的狀態)
	 * @param itemid
	 * @return 
	 */
	@Override
	public Map<Integer, L1ItemInstance> getUserItems(final int itemid) {
		// 人物OBJID / 物品
		final Map<Integer, L1ItemInstance> outList = new ConcurrentHashMap<Integer, L1ItemInstance>();
		try {
			for (Integer key : _itemList.keySet()) {
				CopyOnWriteArrayList<L1ItemInstance> value = _itemList.get(key);
				for (L1ItemInstance item : value) {
					if (item.getItemId() == itemid) {
						outList.put(key, item);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
		return outList;
	}
	
	/**
	 * 刪除指定編號全部數據
	 * @param itemid
	 */
	@Override
	public void del_item(final int itemid) {
		try {
			for (Integer key : _itemList.keySet()) {
				// 人物背包
				final CopyOnWriteArrayList<L1ItemInstance> value = _itemList.get(key);
				for (L1ItemInstance item : value) {
					if (item.getItemId() == itemid) {
						deleteItem(key, item);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 增加背包物品
	 * @param objId
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void storeItem(final int objId, final L1ItemInstance item) throws Exception {
		addItem(objId, item);
		item.getLastStatus().updateAll();
		
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_items` SET `id`=?,`item_id`=?,`char_id`=?," +
					"`item_name`=?,`count`=?,`is_equipped`=?,`enchantlvl`=?,`is_id`=?," +
					"`durability`=?,`charge_count`=?,`remaining_time`=?,`last_used`=?,`bless`=?," +
					"`attr_enchant_kind`=?,`attr_enchant_level`=?,`gamno`=?, `attach_index`=?,proctect = ?,proctect_rom = ?,proctect_type = ?,gem_hole=?,gem_hole_index=?,special_stat=?,ability_pos_1=?,ability_pos_2=?,ability_pos_3=?,can_ability_type=?");

			int i = 0;
			pstm.setInt(++i, item.getId());
			pstm.setInt(++i, item.getItem().getItemId());
			pstm.setInt(++i, objId);
			pstm.setString(++i, item.getItem().getName());
			pstm.setLong(++i, item.getCount());
			pstm.setInt(++i, item.isEquipped() ? 1 : 0);
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
			_log.error("背包物品增加時發生異常 人物OBJID:" + objId, e);

			/*final L1Object object = World.get().findObject(item.get_char_objid());
			if (object != null) {
				if (object instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) object;
					// 刪除物品
					tgpc.getInventory().removeItem(item);
				}
			}*/

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
	 * 刪除背包物品
	 * @param objid 人物OBJID
	 * @param item 物品
	 * 
	 * @throws Exception
	 */
	@Override
	public void deleteItem(final int objid, final L1ItemInstance item) throws Exception {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
		if (list != null) {
			list.remove(item);
			
			Connection cn = null;
			PreparedStatement ps = null;
			try {
				cn = DatabaseFactoryLogin.get().getConnection();
				ps = cn.prepareStatement(
						"DELETE FROM `character_items` WHERE `id`=?");
				ps.setInt(1, item.getId());
				ps.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(ps);
				SQLUtil.close(cn);
			}
		}
	}

	/**
	 * 更新物品ITEMID 與中文名稱
	 * @param item
	 */
	@Override
	public void updateItemId_Name(final L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("UPDATE `character_items` SET `item_id`=?,`item_name`=?,`bless`=? WHERE `id`=?");
			pstm.setInt(1, item.getItemId());
			pstm.setString(2, item.getItem().getName());
			pstm.setInt(3, item.getItem().getBless());
			pstm.setInt(4, item.getId());
			pstm.execute();
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新ITEMID
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemId(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `item_id`=? WHERE `id`=?",
				item.getItemId()
				);
		item.getLastStatus().updateItemId();
	}

	/**
	 * 更新數量
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemCount(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `count`=? WHERE `id`=?",
				item.getCount()
				);
		item.getLastStatus().updateCount();
	}

	/**
	 * 更新損壞度
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemDurability(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `durability`=? WHERE `id`=?",
				item.get_durability()
				);
		item.getLastStatus().updateDuraility();
	}

	/**
	 * 更新可用次數
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemChargeCount(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `charge_count`=? WHERE `id`=?",
				item.getChargeCount()
				);
		item.getLastStatus().updateChargeCount();
	}

	/**
	 * 更新可用時間
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemRemainingTime(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `remaining_time`=? WHERE `id`=?",
				item.getRemainingTime()
				);
		item.getLastStatus().updateRemainingTime();
	}

	/**
	 * 更新強化度
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemEnchantLevel(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `enchantlvl`=? WHERE `id`=?",
				item.getEnchantLevel()
				);
		item.getLastStatus().updateEnchantLevel();
	}

	/**
	 * 更新使用狀態
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemEquipped(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `is_equipped`=? WHERE `id`=?",
				(item.isEquipped() ? 1 : 0)
				);
		item.getLastStatus().updateEquipped();
	}

	/**
	 * 更新鑒定狀態
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemIdentified(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `is_id`=? WHERE `id`=?",
				(item.isIdentified() ? 1 : 0)
				);
		item.getLastStatus().updateIdentified();
	}

	/**
	 * 更新祝福狀態
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemBless(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `bless`=? WHERE `id`=?",
				item.getBless()
				);
		item.getLastStatus().updateBless();
	}

	/**
	 * 更新強化屬性
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemAttrEnchantKind(final L1ItemInstance item) throws Exception {
		executeUpdate(
				item.getId(),
				"UPDATE `character_items` SET `attr_enchant_kind`=? WHERE `id`=?",
				item.getAttrEnchantKind()
				);
		item.getLastStatus().updateAttrEnchantKind();
	}

	/**
	 * 更新強化屬性強化度
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemAttrEnchantLevel(final L1ItemInstance item) throws Exception {
		executeUpdate(
				item.getId(),
				"UPDATE `character_items` SET `attr_enchant_level`=? WHERE `id`=?",
				item.getAttrEnchantLevel()
				);
		item.getLastStatus().updateAttrEnchantLevel();
	}

	/**
	 * 更新最後使用時間
	 * @param item
	 * @throws Exception
	 */
	@Override
	public void updateItemDelayEffect(final L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `last_used`=? WHERE `id`=?",
				item.getLastUsed()
				);
		item.getLastStatus().updateLastUsed();
	}

	/**
	 * 傳回對應所有物品數量
	 * @param objId
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getItemCount(final int objId) throws Exception {
		int count = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT * FROM `character_items` WHERE `char_id`=?");
			pstm.setInt(1, objId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				count++;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return count;
	}

	public final void updateAttachIndex(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `attach_index`=? WHERE `id`=?",
				item.getAttachIndex()
		);
		item.getLastStatus().updateLastUsed();
	}

	public final void updateSpecialStat(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `special_stat`=? WHERE `id`=?",
				item.getSpecialStat()
		);
		item.getLastStatus().updateLastUsed();
	}

	public final void updateGemHole(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `gem_hole`=? WHERE `id`=?",
				item.getGemHole()
		);
		item.getLastStatus().updateLastUsed();
	}

	public final void updateGemHoleIndex(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `gem_hole_index`=? WHERE `id`=?",
				item.getGemHoleIndex()
		);
		item.getLastStatus().updateLastUsed();
	}
	public final void updateCanAbilityType(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `can_ability_type`=? WHERE `id`=?",
				item.getCanAbilityType()
		);
		item.getLastStatus().updateLastUsed();
	}
	public final void updateAbilityPos1ID(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `ability_pos_1`=? WHERE `id`=?",
				item.getAbilityPos1ID()
		);
		item.getLastStatus().updateLastUsed();
	}
	public final void updateAbilityPos2ID(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `ability_pos_2`=? WHERE `id`=?",
				item.getAbilityPos2ID()
		);
		item.getLastStatus().updateLastUsed();
	}
	public final void updateAbilityPos3ID(final L1ItemInstance item) {
		executeUpdate(item.getId(),
				"UPDATE `character_items` SET `ability_pos_3`=? WHERE `id`=?",
				item.getAbilityPos3ID()
		);
		item.getLastStatus().updateLastUsed();
	}

	/**
	 * 給予金幣(對離線人物)
	 * @param objid 人物OBJID
	 * @param count 金幣給予數量
	 * @throws Exception
	 */
	@Override
	public void getAdenaCount(final int objid, final long count) throws Exception {
		CopyOnWriteArrayList<L1ItemInstance> list = _itemList.get(objid);
		if (list != null) {
			boolean isAdena = false;// 背包有金幣
			for (L1ItemInstance item : list) {
				if (item.getItemId() == L1ItemId.ADENA) {
					// 更新數量
					item.setCount(item.getCount() + count);
					updateItemCount(item);
					isAdena = true;
				}
			}
			// 背包無金幣
			if (!isAdena) {
				final L1ItemInstance item = ItemTable.get().createItem(L1ItemId.ADENA);
				item.setCount(count);
				this.storeItem(objid, item);
			}
		}
	}

	private void executeUpdate(final int objId, final String sql, final long updateNum) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setLong(1, updateNum);
			pstm.setInt(2, objId);
			pstm.execute();
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static void executeUpdate(final int objId, final String sql, final Timestamp ts) throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setTimestamp(1, ts);
			pstm.setInt(2, objId);
			pstm.execute();
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
