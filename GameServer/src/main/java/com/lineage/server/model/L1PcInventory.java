package com.lineage.server.model;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRate;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_AddItem;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_DeleteInventoryItem;
import com.lineage.server.serverpackets.S_ItemColor;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

/**
 * 人物背包數據
 * @author dexc
 *
 */
public class L1PcInventory extends L1Inventory {

	private static final Log _log = LogFactory.getLog(L1PcInventory.class);

	private static final long serialVersionUID = 1L;

	private static final int MAX_SIZE = 180;// 最大容量

	private final L1PcInstance _owner; // 背包所有者

	private int _arrowId; // 優先使用的箭ItemID

	private int _stingId; // 優先使用的飛刀ItemID

	public L1PcInventory(final L1PcInstance owner) {
		this._owner = owner;
		this._arrowId = 0;
		this._stingId = 0;
	}

	public L1PcInstance getOwner() {
		return this._owner;
	}

	/**
	 * 傳回182階段重量
	 * @return
	 */
	public int getWeight182() {
		return this.calcWeight182(this.getWeight());
	}

	/**
	 * 182階段重量計算
	 * @param weight
	 * @return
	 */
	public int calcWeight182(final long weight) {
		int weight29 = 0;
		if (ConfigRate.RATE_WEIGHT_LIMIT != 0) {
			final double maxWeight = this._owner.getMaxWeight();
			if (weight > maxWeight) {
				weight29 = 29;
				
			} else {
				double wpTemp = (weight * 100 / maxWeight) * 29.00 / 100.00;
				final DecimalFormat df = new DecimalFormat("00.##");
				df.format(wpTemp);
				wpTemp = Math.round(wpTemp);
				weight29 = (int) (wpTemp);
			}
			
		} else { // ０重量常０
			weight29 = 0;
		}
		
		return weight29;
	}

	/**
	 * 增加物品是否成功(背包)
	 * @param item 物品
	 * @param count 數量
	 * @return 
	 * 			0:成功
	 * 			1:超過可攜帶數量
	 * 			2:超過可攜帶重量
	 * 			3:超過LONG最大質
	 */
	@Override
	public int checkAddItem(final L1ItemInstance item, final long count) {
		return this.checkAddItem(item, count, true);
	}

	/**
	 * 增加物品是否成功(背包)
	 * @param item 物品數據
	 * @param count 數量
	 * @param message 發送訊息
	 * @return 
	 * 			0:成功
	 * 			1:超過可攜帶數量
	 * 			2:超過可攜帶重量
	 * 			3:超過LONG最大質
	 */
	public int checkAddItem(final L1Item item, final long count) {
		if (item == null) {
			return -1;
		}
		if (count <= 0) {
			return -1;
		}
		boolean isMaxSize = false;// 容量數據異常
		boolean isWeightOver = false;// 重量數據異常
		
		// 可以堆疊
		if (item.isStackable()) {
			// 身上不具備該物件
			if (!this.checkItem(item.getItemId())) {
				// 超過可攜帶數量
				if (this.getSize() + 1 >= MAX_SIZE) {
					isMaxSize = true;
				}
			}
			
		// 不可以堆疊
		} else {
			// 超過可攜帶數量
			if (this.getSize() + 1 >= MAX_SIZE) {
				isMaxSize = true;
			}
		}

		if (isMaxSize) {
			// 263 \f1一個角色最多可攜帶180個道具。
			this.sendOverMessage(263);
			return SIZE_OVER;
		}
		
		// 現有重量 + (物品重量 * 數量 / 1000) + 1
		final long weight = this.getWeight() + item.getWeight() * count / 1000 + 1;
		
		// 重量數據異常 (重量計算表示小於0)
		if ((weight < 0) || ((item.getWeight() * count / 1000) < 0)) {
			isWeightOver = true;
		}
		
		// 超過可攜帶重量
		if (this.calcWeight182(weight) >= 240 && !isWeightOver) {
			isWeightOver = true;
		}

		if (isWeightOver) {
			// 82 此物品太重了，所以你無法攜帶。
			this.sendOverMessage(82);
			return WEIGHT_OVER;
		}
		return OK;
	}

	/**
	 * 增加物品是否成功(背包)
	 * @param item 物品(物品已加入世界)
	 * @param count 數量
	 * @param message 發送訊息
	 * @return 
	 * 			0:成功
	 * 			1:超過可攜帶數量
	 * 			2:超過可攜帶重量
	 * 			3:超過LONG最大質
	 */
	public int checkAddItem(final L1ItemInstance item, final long count, final boolean message) {
		if (item == null) {
			return -1;
		}
		if (count <= 0) {
			return -1;
		}

		boolean isMaxSize = false;// 容量數據異常
		boolean isWeightOver = false;// 重量數據異常
		
		// 可以堆疊
		if (item.isStackable()) {
			// 身上不具備該物件
			if (!this.checkItem(item.getItem().getItemId())) {
				// 超過可攜帶數量
				if (this.getSize() + 1 >= MAX_SIZE) {
					isMaxSize = true;
				}
			}
			
		// 不可以堆疊
		} else {
			// 超過可攜帶數量
			if (this.getSize() + 1 >= MAX_SIZE) {
				isMaxSize = true;
			}
		}

		if (isMaxSize) {
			if (message) {
				// 263 \f1一個角色最多可攜帶180個道具。
				this.sendOverMessage(263);
			}
			return SIZE_OVER;
		}
		
		// 現有重量 + (物品重量 * 數量 / 1000) + 1
		final long weight = this.getWeight() + item.getItem().getWeight() * count / 1000 + 1;
		
		// 重量數據異常 (重量計算表示小於0)
		if ((weight < 0) || ((item.getItem().getWeight() * count / 1000) < 0)) {
			isWeightOver = true;
		}
		
		// 超過可攜帶重量
		if (this.calcWeight182(weight) >= 240 && !isWeightOver) {
			isWeightOver = true;
		}

		if (isWeightOver) {
			if (message) {
				// 82 此物品太重了，所以你無法攜帶。
				this.sendOverMessage(82);
			}
			return WEIGHT_OVER;
		}
		return OK;
	}

	public void sendOverMessage(final int message_id) {
		this._owner.sendPackets(new S_ServerMessage(message_id));
	}

	/**
	 * 初始化人物背包資料
	 */
	@Override
	public void loadItems() {
		try {
			final CopyOnWriteArrayList<L1ItemInstance> items = 
				CharItemsReading.get().loadItems(this._owner.getId());
			
			if (items != null) {
				_items = items;
				
				List<L1ItemInstance> equipped = new CopyOnWriteArrayList<L1ItemInstance>();
				for (final L1ItemInstance item : items) {
					if (item.isEquipped()) {
						equipped.add(item);
					}
					
					item.setEquipped(false);
					
					if ((item.getItem().getType2() == 0)
							&& (item.getItem().getType() == 2)) { // 照明道具
						item.setRemainingTime(item.getItem().getLightFuel());
					}
				}
				
				// 將已經設置為裝備的防具 重新設置為裝備狀態
				for (final L1ItemInstance item : equipped) {
					this.setEquipped(item, true, true, false);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * LIST物品資料新增
	 */
	@Override
	public void insertItem(final L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return;
		}
		// 設置使用者OBJID
		item.set_char_objid(this._owner.getId());
		
		this._owner.sendPackets(new S_AddItem(item));
		if (item.getItem().getWeight() != 0) {
			// 重量
			this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight182()));
		}
		
		try {
			CharItemsReading.get().storeItem(this._owner.getId(), item);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static final int COL_ATTR_ENCHANT_LEVEL = 2048;

	public static final int COL_ATTR_ENCHANT_KIND = 1024;

	public static final int COL_BLESS = 512;

	public static final int COL_REMAINING_TIME = 256;

	public static final int COL_CHARGE_COUNT = 128;

	public static final int COL_ITEMID = 64;

	public static final int COL_DELAY_EFFECT = 32;

	public static final int COL_COUNT = 16;

	public static final int COL_EQUIPPED = 8;

	public static final int COL_ENCHANTLVL = 4;

	public static final int COL_IS_ID = 2;

	public static final int COL_DURABILITY = 1;

	public static final int COL_ATTACH_INDEX = 4096;
	public static final int COL_PROTECT_INDEX = 8192;
	public static final int COL_GEM_HOLE = 16384;
	public static final int COL_GEM_HOLE_INDEX = 32768;
	public static final int COL_SPECIAL_STAT = 65536;
	public static final int COL_ABILITY_POS_1_ID = 131073;
	public static final int COL_ABILITY_POS_2_ID = 262147;
	public static final int COL_ABILITY_POS_3_ID = 524295;
	public static final int COL_CAN_ABILITY_TYPE = 1048591;

	@Override
	public void updateItem(final L1ItemInstance item) {
		this.updateItem(item, COL_COUNT);
		if (item.getItem().isToBeSavedAtOnce()) {
			this.saveItem(item, COL_COUNT);
		}
	}

	/**
	 * 背包內物件狀態更新
	 *
	 * @param item 需要更新的物件
	 * @param column 更新種類
	 */
	@Override
	public void updateItem(final L1ItemInstance item, int column) {
		if (column >= COL_ATTR_ENCHANT_LEVEL) { // 屬性強化數
			this._owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_LEVEL;
		}
		
		if (column >= COL_ATTR_ENCHANT_KIND) { // 屬性強化種類
			this._owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_KIND;
		}
		
		if (column >= COL_BLESS) { // 祝福・封印
			this._owner.sendPackets(new S_ItemColor(item));
			column -= COL_BLESS;
		}
		
		if (column >= COL_REMAINING_TIME) { // 殘餘可用時間
			this._owner.sendPackets(new S_ItemName(item));
			column -= COL_REMAINING_TIME;
		}
		
		if (column >= COL_CHARGE_COUNT) { // 殘餘可用次數
			this._owner.sendPackets(new S_ItemName(item));
			column -= COL_CHARGE_COUNT;
		}
		
		if (column >= COL_ITEMID) { // 別場合(便箋開封)
			this._owner.sendPackets(new S_ItemStatus(item));
			this._owner.sendPackets(new S_ItemColor(item));
			this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight182()));
			column -= COL_ITEMID;
		}
		
		if (column >= COL_DELAY_EFFECT) { // 效果
			column -= COL_DELAY_EFFECT;
		}
		
		if (column >= COL_COUNT) { // 
			this._owner.sendPackets(new S_ItemStatus(item));

			final int weight = item.getWeight();
			if (weight != item.getLastWeight()) {
				item.setLastWeight(weight);
				this._owner.sendPackets(new S_ItemStatus(item));
				
			} else {
				this._owner.sendPackets(new S_ItemName(item));
			}
			if (item.getItem().getWeight() != 0) {
				// XXX 182段階變化場合送
				this._owner.sendPackets(
						new S_PacketBox(
								S_PacketBox.WEIGHT,
								this.getWeight182()
								));
			}
			column -= COL_COUNT;
		}
		
		if (column >= COL_EQUIPPED) { // 裝備狀態
			this._owner.sendPackets(new S_ItemName(item));
			column -= COL_EQUIPPED;
		}
		
		if (column >= COL_ENCHANTLVL) { // 
			this._owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ENCHANTLVL;
		}
		
		if (column >= COL_IS_ID) { // 確認狀態
			this._owner.sendPackets(new S_ItemStatus(item));
			this._owner.sendPackets(new S_ItemColor(item));
			column -= COL_IS_ID;
		}
		
		if (column >= COL_DURABILITY) { // 耐久性
			this._owner.sendPackets(new S_ItemStatus(item));
			column -= COL_DURABILITY;
		}
	}

	/**
	 * 背包內資料更新(SQL)
	 *
	 * @param item
	 *            - 更新對像
	 * @param column
	 *            - 更新種類
	 */
	public void saveItem(final L1ItemInstance item, int column) {
		if (column == 0) {
			return;
		}

		try {
			if (column >= COL_CAN_ABILITY_TYPE) {
				CharItemsReading.get().updateCanAbilityType(item);
				column -= COL_CAN_ABILITY_TYPE;
			}
			if (column >= COL_ABILITY_POS_3_ID) {
				CharItemsReading.get().updateAbilityPos3ID(item);
				column -= COL_ABILITY_POS_3_ID;
			}
			if (column >= COL_ABILITY_POS_2_ID) {
				CharItemsReading.get().updateAbilityPos2ID(item);
				column -= COL_ABILITY_POS_2_ID;
			}
			if (column >= COL_ABILITY_POS_1_ID) {
				CharItemsReading.get().updateAbilityPos1ID(item);
				column -= COL_ABILITY_POS_1_ID;
			}
			if (column >= COL_SPECIAL_STAT) {
				CharItemsReading.get().updateSpecialStat(item);
				column -= COL_SPECIAL_STAT;
			}
			if (column >= COL_GEM_HOLE_INDEX) {
				CharItemsReading.get().updateGemHoleIndex(item);
				column -= COL_GEM_HOLE_INDEX;
			}
			if (column >= COL_GEM_HOLE) {
				CharItemsReading.get().updateGemHole(item);
				column -= COL_GEM_HOLE;
			}
			if (column >= COL_PROTECT_INDEX) {
				CharItemsReading.get().updateItemProtection(item);
				column -= COL_PROTECT_INDEX;
			}
			if (column >= COL_ATTACH_INDEX) {
				CharItemsReading.get().updateAttachIndex(item);
				column -= COL_ATTACH_INDEX;
			}
			if (column >= COL_ATTR_ENCHANT_LEVEL) { // 屬性強化數
				CharItemsReading.get().updateItemAttrEnchantLevel(item);
				column -= COL_ATTR_ENCHANT_LEVEL;
			}
			
			if (column >= COL_ATTR_ENCHANT_KIND) { // 屬性強化種類
				CharItemsReading.get().updateItemAttrEnchantKind(item);
				column -= COL_ATTR_ENCHANT_KIND;
			}
			
			if (column >= COL_BLESS) { // 祝福・封印
				CharItemsReading.get().updateItemBless(item);
				column -= COL_BLESS;
			}
			
			if (column >= COL_REMAINING_TIME) { // 使用可能殘時間
				CharItemsReading.get().updateItemRemainingTime(item);
				column -= COL_REMAINING_TIME;
			}
			
			if (column >= COL_CHARGE_COUNT) { // 數
				CharItemsReading.get().updateItemChargeCount(item);
				column -= COL_CHARGE_COUNT;
			}
			
			if (column >= COL_ITEMID) { // 別場合(便箋開封)
				CharItemsReading.get().updateItemId(item);
				column -= COL_ITEMID;
			}
			
			if (column >= COL_DELAY_EFFECT) { // 效果
				CharItemsReading.get().updateItemDelayEffect(item);
				column -= COL_DELAY_EFFECT;
			}
			
			if (column >= COL_COUNT) { // 
				CharItemsReading.get().updateItemCount(item);
				column -= COL_COUNT;
			}
			
			if (column >= COL_EQUIPPED) { // 裝備狀態
				CharItemsReading.get().updateItemEquipped(item);
				column -= COL_EQUIPPED;
			}
			
			if (column >= COL_ENCHANTLVL) { // 
				CharItemsReading.get().updateItemEnchantLevel(item);
				column -= COL_ENCHANTLVL;
			}
			
			if (column >= COL_IS_ID) { // 確認狀態
				CharItemsReading.get().updateItemIdentified(item);
				column -= COL_IS_ID;
			}
			
			if (column >= COL_DURABILITY) { // 耐久性
				CharItemsReading.get().updateItemDurability(item);
				column -= COL_DURABILITY;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * LIST物品資料移除
	 */
	@Override
	public void deleteItem(final L1ItemInstance item) {
		try {
			CharItemsReading.get().deleteItem(this._owner.getId(), item);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		if (item.isEquipped()) {
			this.setEquipped(item, false);
		}
		
		if (item != null) {
			this._owner.sendPackets(new S_DeleteInventoryItem(item));
			this._items.remove(item);
			if (item.getItem().getWeight() != 0) {
				this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight182()));
			}
		}
	}

	/**
	 * 裝著脫著（L1ItemInstance變更、補正值設定、character_items更新、送信管理）
	 * @param item
	 * @param equipped
	 */
	public void setEquipped(final L1ItemInstance item, final boolean equipped) {
		this.setEquipped(item, equipped, false, false);
	}

	public void setEquipped(final L1ItemInstance item, final boolean equipped,
			final boolean loaded, final boolean changeWeapon) {
		if (item.isEquipped() != equipped) { // 設定值違場合處理
			final L1Item temp = item.getItem();
			if (equipped) { // 裝著
				item.setEquipped(true);
				// 裝備穿著效果判斷
				this._owner.getEquipSlot().set(item);
				
			} else { // 脫著
				if (!loaded) {
					//  裝備中狀態場合狀態解除
					if ((temp.getItemId() == 20077) 
							|| (temp.getItemId() == 20062)
							|| (temp.getItemId() == 120077)) {
						if (this._owner.isInvisble()) {
							this._owner.delInvis();
							return;
						}
					}
				}
				item.setEquipped(false);
				// 裝備脫除效果判斷
				this._owner.getEquipSlot().remove(item);
			}
			
			if (!loaded) { // 最初讀迂時ＤＢ關連處理
				//System.out.println("物品裝備狀態");
				// XXX:意味
				this._owner.setCurrentHp(this._owner.getCurrentHp());
				this._owner.setCurrentMp(this._owner.getCurrentMp());
				this.updateItem(item, COL_EQUIPPED);
				this._owner.sendPackets(new S_OwnCharStatus(this._owner));
				// 武器場合更新。、武器持替武器脫著時更新
				if ((temp.getType2() == 1) && (changeWeapon == false)) {
					this._owner.sendPacketsAll(new S_CharVisualUpdate(this._owner));
				}
			}
		}
	}

	/**
	 * 裝備具有指定編號道具
	 * @param id 物品編號
	 * @return 傳回該物品
	 */
	public L1ItemInstance checkEquippedItem(final int id) {
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品編號相同 並且在使用中
				if ((item.getItem().getItemId() == id) && item.isEquipped()) {
					return item;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return null;
	}

	/**
	 * 裝備具有指定編號道具
	 * @param id 物品編號
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkEquipped(final int id) {
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品編號相同 並且在使用中
				if ((item.getItem().getItemId() == id) && item.isEquipped()) {
					return true;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	/**
	 * 裝備具有指定名稱道具
	 * @param nameid 物品名稱
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkEquipped(final String nameid) {
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品名稱相同 並且在使用中
				if ((item.getName().equals(nameid)) && item.isEquipped()) {
					return true;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}
	/**
	 * 装备的卡片
	 * @param itemid 物品编号
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkCardEquipped(final int itemid) {
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品编号相同 并且在使用中(具备使用期限)
				if ((item.getItem().getItemId() == itemid) && item.get_card_use() == 1) {
					return true;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}
	/**
	 * 装备的卡片类别
	 * @param itemid 物品编号
	 * @return true:使用中 false:非使用中
	 */
	public boolean checkCardEquipped1(final int itemid) {
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品编号相同 并且在使用中(具备使用期限)
				if ((item.getItem().getWeight() == itemid) && item.get_card_use() == 1) {
					return true;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return false;
	}

	/**
	 * 裝備具有指定編號道具群(套裝)
	 * @param ids
	 * @return
	 */
	public boolean checkEquipped(final int[] ids) {
		try {
			for (final int id : ids) {
				if (!this.checkEquipped(id)) {
					return false;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return true;
	}

	/**
	 * 裝備具有指定名稱道具群(套裝)
	 * @param names
	 * @return
	 */
	public boolean checkEquipped(final String[] names) {
		try {
			for (final String name : names) {
				if (!this.checkEquipped(name)) {
					return false;
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return true;
	}

	/**
	 * 裝備中指定類型物品數量
	 * @param type2 類型
	 * @param type 物品分類
	 * 
	 * @return 裝備中指定類型物品數量
	 */
	public int getTypeEquipped(final int type2, final int type) {
		int equipeCount = 0;// 裝備中指定位置物品數量
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品類型相等 物品分類相等 並且在使用中
				if ((item.getItem().getType2() == type2) &&  
						(item.getItem().getType() == type) && 
						item.isEquipped()) {
					equipeCount++;// 使用數量+1
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return equipeCount;
	}

	/**
	 * 裝備中指定類型物品
	 * @param type2 類型
	 * @param type 物品分類
	 * 
	 * @return 裝備中指定類型物品
	 */
	public L1ItemInstance getItemEquipped(final int type2, final int type) {
		L1ItemInstance equipeitem = null;
		try {
			for (final L1ItemInstance item : this._items) {
				// 物品類型相等 物品分類相等 並且在使用中
				if ((item.getItem().getType2() == type2)
						&& (item.getItem().getType() == type) && 
						item.isEquipped()) {
					equipeitem = item;
					break;
				}
			}

		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return equipeitem;
	}
	
	/**
	 * 設置 顯示/消除 套裝效果 XXX
	 * @param armorSet 套裝
	 * @param isMode 是否顯示 額外屬性
	 */
	public void setPartMode(final ArmorSet armorSet, final boolean isMode) {
		final int tgItemId = armorSet.get_ids()[0];// 取回套裝第一樣物品ID
		final L1ItemInstance[] tgItems = findItemsId(tgItemId);
		for (L1ItemInstance tgItem : tgItems) {
			tgItem.setIsMatch(isMode);
			this._owner.sendPackets(new S_ItemStatus(tgItem));
		}
	}
	
	/**
	 * 裝備中界指陣列
	 * @return
	 */
	public L1ItemInstance[] getRingEquipped() {
		final L1ItemInstance equipeItem[] = new L1ItemInstance[2];
		try {
			int equipeCount = 0;
			for (final L1ItemInstance item : this._items) {
				// 物品為戒指 並且在使用中
				if (item.getItem().getUseType() == 23 && // 戒指
						item.isEquipped()) {
					equipeItem[equipeCount] = item;
					equipeCount++;
					if (equipeCount == 2) {
						break;
					}
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
		return equipeItem;
	}

	// 變身時裝備裝備外
	public void takeoffEquip(final int polyid) {
		this.takeoffWeapon(polyid);
		this.takeoffArmor(polyid);
	}

	// 變身時裝備武器外
	private void takeoffWeapon(final int polyid) {
		if (this._owner.getWeapon() == null) { // 素手
			return;
		}

		boolean takeoff = false;
		final int weapon_type = this._owner.getWeapon().getItem().getType();
		// 裝備出來武器裝備？
		takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

		if (takeoff) {
			this.setEquipped(this._owner.getWeapon(), false, false, false);
		}
	}

	// 變身時裝備防具外
	private void takeoffArmor(final int polyid) {
		L1ItemInstance armor = null;

		// 
		for (int type = 0; type <= 13; type++) {
			// 裝備、裝備不可場合外
			if ((this.getTypeEquipped(2, type) != 0)
					&& !L1PolyMorph.isEquipableArmor(polyid, type)) {
				if (type == 9) { // 場合、兩手分外
					armor = this.getItemEquipped(2, type);
					if (armor != null) {
						this.setEquipped(armor, false, false, false);
					}
					
					armor = this.getItemEquipped(2, type);
					if (armor != null) {
						this.setEquipped(armor, false, false, false);
					}
					
				} else {
					armor = this.getItemEquipped(2, type);
					if (armor != null) {
						this.setEquipped(armor, false, false, false);
					}
				}
			}
		}
	}

	/**
	 * 使用的箭
	 * @return
	 */
	public L1ItemInstance getArrow() {
		return this.getBullet(-2);
	}

	/**
	 * 使用的飛刀
	 * @return
	 */
	public L1ItemInstance getSting() {
		return this.getBullet(-3);
	}

	/**
	 * 
	 * @param useType
	 * @return
	 */
	private L1ItemInstance getBullet(final int useType) {
		L1ItemInstance bullet;
		int priorityId = 0;
		if (useType == -2) {
			if (this._owner.getWeapon().getItemId() == 192) {// 水精靈之弓
				bullet = this.findItemId(40742);// 古代之箭
				if (bullet == null) {
					// 329：\f1沒有具有 %0%o。  
					this._owner.sendPackets(new S_ServerMessage(329, "$2377"));
				}
				return bullet;
				
			} else {
				priorityId = this._arrowId; // 箭
			}
		}
		
		if (useType == -3) {
			priorityId = this._stingId; // 飛刀
		}
		
		if (priorityId > 0) {// 優先彈
			bullet = this.findItemId(priorityId);
			if (bullet != null) {
				return bullet;
				
			} else {// 場合優先消
				if (useType == -2) {
					this._arrowId = 0;
				}
				if (useType == -3) {
					this._stingId = 0;
				}
			}
		}

		for (final Object itemObject : this._items) {// 彈探
			bullet = (L1ItemInstance) itemObject;
			if (bullet.getItem().getUseType() == useType) {
				if (useType == -2) {// 箭
					this._arrowId = bullet.getItem().getItemId(); // 優先
				}
				
				if (useType == -3) {
					this._stingId = bullet.getItem().getItemId(); // 優先
				}
				return bullet;
			}
		}
		return null;
	}

	// 優先設定
	public void setArrow(final int id) {
		this._arrowId = id;
	}

	// 優先設定
	public void setSting(final int id) {
		this._stingId = id;
	}

	/**
	 * 裝備 hp自然回復補正
	 * @return
	 */
	public int hpRegenPerTick() {
		int hpr = 0;
		try {
			for (final Object itemObject : this._items) {
				final L1ItemInstance item = (L1ItemInstance) itemObject;
				if (item.isEquipped()) {
				
						hpr += item.getItem().get_addhpr();
					
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return hpr;
	}

	/**
	 * 裝備 mp自然回復補正
	 * @return
	 */
	public int mpRegenPerTick() {
		int mpr = 0;
		try {
			for (final Object itemObject : this._items) {
				final L1ItemInstance item = (L1ItemInstance) itemObject;
				if (item.isEquipped()) {
				
						mpr += item.getItem().get_addmpr();
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return mpr;
	}

	/**
	 * 傳回隨機掉落物品
	 * @return
	 */
	public L1ItemInstance caoPenalty() {
		try {
			final Random random = new Random();
			final int rnd = random.nextInt(_items.size());
			final L1ItemInstance penaltyItem = _items.get(rnd);
			// 天寶
			if (penaltyItem.getItem().getItemId() == 44070) {
				return null;
			}
			
			// 金幣
			if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA) {
				return null;
			}
			
			// 不可刪除物品
			if (penaltyItem.getItem().isCantDelete()) {
				return null;
			}
			
			// 不可轉移物品
			if (!penaltyItem.getItem().isTradable()) {
				return null;
			}
			
			// 具有時間限制
			if (penaltyItem.get_time() != null) {
				return null;
			}
			
			// 寵物項圈
			final Object[] petlist = this._owner.getPetList().values().toArray();
			for (final Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (penaltyItem.getId() == pet.getItemObjId()) {
						return null;
					}
				}
			}

			// 取回娃娃
			if (_owner.getDoll(penaltyItem.getId()) != null) {
				return null;
			}

			// 解除使用狀態
			this.setEquipped(penaltyItem, false);
			return penaltyItem;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 移除全部指定編號道具
	 * @param itemId
	 */
	public void delQuestItem(final int itemId) {
		try {
			final Random random = new Random();
			for (L1ItemInstance item : this._items) {
				if (item.getItemId() == itemId) {
					removeItem(item); 
					// 445：\f1%0%s 漸漸變熱之後燃燒成灰燼。  
					// 446：\f1%0%s 凍結之後破碎。  
					// 447：\f1%0%s 經過狂烈的震動之後變成土。  
					// 448：\f1%0%s 漸漸腐蝕之後被風吹散。  
					this._owner.sendPackets(new S_ServerMessage(random.nextInt(4) + 445, item.getName()));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
