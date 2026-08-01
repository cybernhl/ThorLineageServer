package com.lineage.server.model;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.datatables.InnKeyTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.datatables.sql.LetterTable;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

/**
 * 背包
 * @author dexc
 *
 */
public class L1Inventory extends L1Object {

	private static final Log _log = LogFactory.getLog(L1Inventory.class);

	private static final long serialVersionUID = 1L;

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();
	
	public static final int MAX_WEIGHT = 1500;

	public L1Inventory() {
		//
	}

	/**
	 * 背包內全部數量
	 * @return
	 */
	public int getSize() {
		if (this._items.isEmpty()) {
			return 0;
		}
		return this._items.size();
	}

	/**
	 * 背包內全物件清單
	 * @return
	 */
	public List<L1ItemInstance> getItems() {
		return this._items;
	}

	/**
	 * 背包內全部重量
	 * @return
	 */
	public int getWeight() {
		int weight = 0;

		for (final L1ItemInstance item : this._items) {
			weight += item.getWeight();
		}

		return weight;
	}

	public static final int OK = 0;// 成功

	public static final int SIZE_OVER = 1;// 超過數量

	public static final int WEIGHT_OVER = 2;// 超過可攜帶重量

	public static final int AMOUNT_OVER = 3;// 超過LONG最大質

	public int checkAddItem(final int item, final long count) {
		return -1;
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
	public int checkAddItem(final L1ItemInstance item, final long count) {
		if (item == null) {
			return -1;
		}

		if ((item.getCount() <= 0) || (count <= 0)) {
			return -1;
		}

		if ((this.getSize() > ConfigAlt.MAX_NPC_ITEM)
				|| ((this.getSize() == ConfigAlt.MAX_NPC_ITEM) && (!item.isStackable() || !this.checkItem(item
						.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		final long weight = this.getWeight() + item.getItem().getWeight() * count / 1000 + 1;
		if ((weight < 0) || ((item.getItem().getWeight() * count / 1000) < 0)) {
			return WEIGHT_OVER;
		}
		if (weight > (MAX_WEIGHT * ConfigRate.RATE_WEIGHT_LIMIT_PET)) { // 重量確認
			return WEIGHT_OVER;
		}

		final L1ItemInstance itemExist = this.findItemId(item.getItemId());
		if ((itemExist != null) && ((itemExist.getCount() + count) > Long.MAX_VALUE)) {
			return AMOUNT_OVER;
		}

		return OK;
	}

	public static final int WAREHOUSE_TYPE_PERSONAL = 0;// 個人/精靈倉庫

	public static final int WAREHOUSE_TYPE_CLAN = 1;// 血盟倉庫

	/**
	 * 增加物品是否成功(倉庫)
	 * @param item 物品
	 * @param count 數量
	 * @param type 模式 0:個人/精靈倉庫 1:血盟倉庫
	 * @return
	 * 			0:成功
	 * 			1:超過數量
	 */
	public int checkAddItemToWarehouse(final L1ItemInstance item, final long count, final int type) {
		if (item == null) {
			return -1;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return -1;
		}

		int maxSize = 100;
		if (type == WAREHOUSE_TYPE_PERSONAL) {
			maxSize = ConfigAlt.MAX_PERSONAL_WAREHOUSE_ITEM;

		} else if (type == WAREHOUSE_TYPE_CLAN) {
			maxSize = ConfigAlt.MAX_CLAN_WAREHOUSE_ITEM;
		}
		if ((this.getSize() > maxSize)
				|| ((this.getSize() == maxSize) && (!item.isStackable() || !this.checkItem(item.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		return OK;
	}

	/**
	 * 全新物件加入背包
	 * @param id
	 * @param count
	 * @return
	 */
	public synchronized L1ItemInstance storeItem(final int id, final long count) {
		try {
			if (count <= 0) {
				return null;
			}
			final L1Item temp = ItemTable.get().getTemplate(id);
			if (temp == null) {
				return null;
			}

			if (temp.isStackable()) {
				final L1ItemInstance item = new L1ItemInstance(temp, count);

				if (this.findItemId(id) == null) { // 新生成必要場合ID發行L1World登錄行
					item.setId(IdFactory.get().nextId());
					World.get().storeObject(item);
				}

				return this.storeItem(item);
			}

			// 場合
			L1ItemInstance result = null;
			for (int i = 0; i < count; i++) {
				final L1ItemInstance item = new L1ItemInstance(temp, 1);
				item.setId(IdFactory.get().nextId());
				World.get().storeObject(item);
				this.storeItem(item);
				result = item;
			}
			// 最後作返。配列戾定義變更良。
			return result;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 背包中新物品的增加
	 * (物品購買/道具交換)
	 * @param item
	 * @return
	 */
	public synchronized L1ItemInstance storeItem(final L1ItemInstance item) {
		try {
			if (item == null) {
				return null;
			}
			if (item.getCount() <= 0) {
				return null;
			}

			if (item.isStackable()) {
				if (item.getItem().getUseType() == -5) {// 食人妖精競賽票
					final L1ItemInstance[] items = this.findItemsId(item.getItemId());
					//System.out.println(items);
					for (final L1ItemInstance tgitem : items) {
						final String gamNo = tgitem.getGamNo();
						if (item.getGamNo().equals(gamNo)) {
							tgitem.setCount(tgitem.getCount() + item.getCount());
							this.updateItem(tgitem);
							return tgitem;
						}
					}

				} else {
					final L1ItemInstance findItem = this.findItemId(item.getItem().getItemId());
					if (findItem != null) {
						findItem.setCount(findItem.getCount() + item.getCount());
						this.updateItem(findItem);
						return findItem;
					}
				}
			}
			item.setX(this.getX());
			item.setY(this.getY());
			item.setMap(this.getMapId());

			// 資料庫最大可用次數
			int chargeCount = item.getItem().getMaxChargeCount();

			// 魔杖類次數給予判斷
			switch (item.getItem().getItemId()) {
			case 20383: // 軍馬頭盔
				chargeCount = 50;
				break;

			case 40006: // 創造怪物魔杖
			case 140006: // 創造怪物魔杖

			case 40008: // 變形魔杖
			case 140008: // 變形魔杖
				
			case 40007: // 閃電魔杖
			case 40009: // 驅逐魔杖
				final Random random1 = new Random();
				chargeCount -= random1.nextInt(5);
				break;

			default:
				break;
			}

			item.setChargeCount(chargeCount);
			
			if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // 照明道具時間設置
				item.setRemainingTime(item.getItem().getLightFuel());
				
			} else {
				// 登入鑰匙紀錄
				if (item.getItem().getItemId() == 40312) {
					if (!InnKeyTable.checkey(item)) {
						InnKeyTable.StoreKey(item);
					}

				} else {
					item.setRemainingTime(item.getItem().getMaxUseTime());
				}
			}
			
			item.setBless(item.getItem().getBless());
			set_time_item(item);
			this._items.add(item);
			this.insertItem(item);
			return item;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 背包中新物品的增加
	 * (倉庫取回/倉庫存入/丟棄/撿取)
	 * @param item
	 * @return
	 */
	public synchronized L1ItemInstance storeTradeItem(final L1ItemInstance item) {
		try {
			if (item == null) {
				return null;
			}
			if (item.getCount() <= 0) {
				return null;
			}

			if (item.isStackable()) {
				if (item.getItem().getUseType() == -5) {// 食人妖精競賽票/死亡競賽票/彩票
					final L1ItemInstance[] items = this.findItemsId(item.getItemId());
					//System.out.println(items);
					for (final L1ItemInstance tgitem : items) {
						final String gamNo = tgitem.getGamNo();
						if (item.getGamNo().equals(gamNo)) {
							tgitem.setCount(tgitem.getCount() + item.getCount());
							this.updateItem(tgitem);
							return tgitem;
						}
					}

				} else {
					final L1ItemInstance findItem = this.findItemId(item.getItem().getItemId());
					if (findItem != null) {
						findItem.setCount(findItem.getCount() + item.getCount());
						this.updateItem(findItem);
						return findItem;
					}

				}
			}
			item.setX(this.getX());
			item.setY(this.getY());
			item.setMap(this.getMapId());
			// 登入鑰匙紀錄
			if (item.getItem().getItemId() == 40312) {
				if (!InnKeyTable.checkey(item)) {
					InnKeyTable.StoreKey(item);
				}
			}
			/*if (!this._items.contains(item)) {
				this._items.add(item);
			}*/
			this._items.add(item);
			this.insertItem(item);
			return item;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 刪除指定編號物品及數量
	 *
	 * @param itemid
	 *            - 刪除物品的編號
	 * @param count
	 *            - 刪除的數量
	 * @return true:刪除完成 false:刪除失敗
	 */
	public boolean consumeItem(final int itemid, final long count) {
		if (count <= 0) {
			return false;
		}
		// 物品可以堆疊
		if (ItemTable.get().getTemplate(itemid).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				this.removeItem(item, count);
				return true;
			}
			
		} else {
			final L1ItemInstance[] itemList = this.findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					this.removeItem(itemList[i], 1);
				}
				return true;

			} else if (itemList.length > count) {
				// 指定物品具有多個
				final DataComparator dc = new DataComparator();
				Arrays.sort(itemList, dc); // 按照強化質 由低至高排列
				for (int i = 0; i < count; i++) {
					// 先由強化質低的開始移除
					this.removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 按照強化質 由低至高排列物品
	 * @author daien
	 *
	 */
	public class DataComparator implements Comparator<Object> {
		@Override
		public int compare(final Object item1, final Object item2) {
			return ((L1ItemInstance) item1).getEnchantLevel() - ((L1ItemInstance) item2).getEnchantLevel();
		}
	}

	/**
	 * 移轉物品
	 * @param objectId
	 * @param count
	 * @return 
	 */
	public L1ItemInstance shiftingItem(final int objectId, final long count) {
		final L1ItemInstance item = this.getItem(objectId);
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}
		if (item.getCount() == count) {
			if (!item.isEquipped()) {
				this.deleteItem(item);
				return item;
			}
			
		}
		return null;
	}
	
	/**
	 * 指定OBJID以及數量 刪除物品
	 * @param objectId
	 * @param count
	 * @return 實際刪除數量
	 */
	public long removeItem(final int objectId, final long count) {
		final L1ItemInstance item = this.getItem(objectId);
		return this.removeItem(item, count);
	}

	/**
	 * 指定物品(全部數量) 刪除物品
	 * @param item
	 * @return 實際刪除數量
	 */
	public long removeItem(final L1ItemInstance item) {
		return this.removeItem(item, item.getCount());
	}

	/**
	 * 指定物品以及數量 刪除物品
	 * @param item
	 * @param count
	 * @return 實際刪除數量
	 */
	public long removeItem(final L1ItemInstance item, long count) {
		if (item == null) {
			return 0;
		}
		if (!_items.contains(item)) {
			return 0;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return 0;
		}
		if (item.getCount() < count) {
			count = item.getCount();
		}
		if (item.getCount() == count) {
			final int itemId = item.getItem().getItemId();
			if ((itemId >= 49016) && (itemId <= 49025)) { // 便箋
				final LetterTable lettertable = new LetterTable();
				lettertable.deleteLetter(item.getId());

			} else if ((itemId >= 41383) && (itemId <= 41400)) { // 傢俱
				for (final L1Object l1object : World.get().getObject()) {
					if (l1object instanceof L1FurnitureInstance) {
						final L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
						if (furniture.getItemObjId() == item.getId()) { // 既引出傢俱
							FurnitureSpawnReading.get().deleteFurniture(furniture);
						}
					}
				}
			}
			this.deleteItem(item);
			World.get().removeObject(item);
			
		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
		}
		return count;
	}
	/**
	 * 物品資料消除
	 * @param item
	 */
	public void deleteItem(final L1ItemInstance item) {
		// 刪除鑰匙紀錄
		if (item.getItem().getItemId() == 40312) {
			InnKeyTable.DeleteKey(item);
		}
		this._items.remove(item);
	}

	// 引數移讓
	public synchronized L1ItemInstance tradeItem(final int objectId, final long count,
			final L1Inventory inventory) {
		final L1ItemInstance item = this.getItem(objectId);
		return this.tradeItem(item, count, inventory);
	}

	/**
	 * 物品轉移
	 * @param item 轉移的物品
	 * @param count 移出的數量
	 * @param showId 副本編號
	 * @param inventory 移出對象的背包
	 */
	public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, int showId,
			L1GroundInventory inventory) {
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}
		/*if (!this.checkItem(item.getItem().getItemId(), count)) {
			return null;
		}*/
		L1ItemInstance carryItem;
		if (item.getCount() == count) {
			this.deleteItem(item);
			carryItem = item;
			// 副本編號
			carryItem.set_showId(showId);
		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
			carryItem = ItemTable.get().createItem(item.getItem().getItemId());
			// 副本編號
			carryItem.set_showId(showId);
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getBless());
		}
		
		return inventory.storeTradeItem(carryItem);
	}

	/**
	 * 物品轉移
	 * @param item 轉移的物品
	 * @param count 移出的數量
	 * @param inventory 移出對象的背包
	 * @return
	 */
	public synchronized L1ItemInstance tradeItem(final L1ItemInstance item,
			final long count, final L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}
		/*if (!this.checkItem(item.getItem().getItemId(), count)) {
			return null;
		}*/
		L1ItemInstance carryItem;
		if (item.getCount() == count) {
			this.deleteItem(item);
			carryItem = item;

		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
			carryItem = ItemTable.get().createItem(item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getBless());
		}
		return inventory.storeTradeItem(carryItem);
	}

	/**
	 * 損傷・損耗（武器・防具含） 場合、損耗 武器・防具損傷度表。
	 */
	public L1ItemInstance receiveDamage(final int objectId) {
		final L1ItemInstance item = this.getItem(objectId);
		return this.receiveDamage(item);
	}

	public L1ItemInstance receiveDamage(final L1ItemInstance item) {
		return this.receiveDamage(item, 1);
	}

	public L1ItemInstance receiveDamage(final L1ItemInstance item, final int count) {
		if (item == null) {
			return null;
		}
		final int itemType = item.getItem().getType2();
		final int currentDurability = item.get_durability();

		if (((currentDurability == 0) && (itemType == 0)) || (currentDurability < 0)) {
			item.set_durability(0);
			return null;
		}

		// 武器・防具損傷度
		if (itemType == 0) {
			final int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.set_durability(durability);
			}
		} else {
			final int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				item.set_durability(durability);
			}
		}

		this.updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(final L1ItemInstance item) {
		if (item == null) {
			return null;
		}
		final int itemType = item.getItem().getType2();
		final int durability = item.get_durability();

		if (((durability == 0) && (itemType != 0)) || (durability < 0)) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			// 耐久度。
			item.set_durability(durability + 1);
		} else {
			// 損傷度。
			item.set_durability(durability - 1);
		}

		this.updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	/**
	 * 找尋指定物品(未裝備)
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance findItemIdNoEq(final int itemId) {
		for (final L1ItemInstance item : this._items) {
			if (item.getItem().getItemId() == itemId && !item.isEquipped()) {
				if (item.get_time() == null) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 找尋指定物品<BR>
	 * 不檢查裝備狀態
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance findItemId(final int itemId) {
		for (final L1ItemInstance item : this._items) {
			if (item.getItem().getItemId() == itemId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 找尋指定物品
	 * @param nameid
	 * @return
	 */
	public L1ItemInstance findItemId(final String nameid) {
		for (final L1ItemInstance item : this._items) {
			if (item.getName().equals(nameid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 傳出是否有該編號物品(陣列)
	 * @param itemId 物品編號
	 * @return
	 */
	public L1ItemInstance[] findItemsId(final int itemId) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getItemId() == itemId) {// itemid相等
				if (item.get_time() == null) {// 不具備時間限制
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 未裝備物品清單(陣列)
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance[] findItemsIdNotEquipped(final int itemId) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getItemId() == itemId) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 未裝備物品清單(陣列)
	 * @param nameid
	 * @return
	 */
	public L1ItemInstance[] findItemsIdNotEquipped(final String nameid) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getName().equals(nameid)) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 檢查是否具有指定OBJID物品
	 * @param objectId
	 * @return
	 */
	public L1ItemInstance getItem(final int objectId) {
		for (final Object itemObject : this._items) {
			final L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 檢查指定物品是否足夠數量1（矢 魔石的確認）
	 * @param id
	 * @return
	 */
	public boolean checkItem(final int id) {
		return this.checkItem(id, 1);
	}

	/**
	 * 檢查指定物品是否足夠數量
	 * @param itemId 物品編號
	 * @param count 需要數量
	 * @return
	 */
	public boolean checkItem(final int itemId, final long count) {
		if (count <= 0) {
			return true;
		}
		
		// 可堆疊
		if (ItemTable.get().getTemplate(itemId).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemId);
			if ((item != null) && (item.getCount() >= count)) {
				return true;
			}
			
		// 不可堆疊
		} else {
			final Object[] itemList = this.findItemsId(itemId);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 檢查指定物品是否足夠數量
	 * @param item 物品
	 * @param count 需要數量
	 * @return
	 */
	public boolean checkItem(final L1ItemInstance item, final long count) {
		if (count <= 0) {
			return true;
		}
		if (item.getCount() >= count) {
			return true;
		}
		return false;
	}

	/**
	 * 指定物品編號以及數量<BR>
	 * 該物件未在裝備狀態
	 * @param itemid
	 * @param count
	 * @return 足夠傳回物品
	 */
	public L1ItemInstance checkItemX(final int itemid, final long count) {
		if (count <= 0) {
			return null;
		}
		if (ItemTable.get().getTemplate(itemid) != null) {
			final L1ItemInstance item = this.findItemIdNoEq(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 指定物品編號以及數量(未裝備)
	 * @param itemid
	 * @param count
	 * @return 足夠傳回物品
	 */
	public L1ItemInstance checkItemXNoEq(final int itemid, final long count) {
		if (count <= 0) {
			return null;
		}
		if (ItemTable.get().getTemplate(itemid) != null) {
			final L1ItemInstance item = this.findItemIdNoEq(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 是否具有未裝備指定的物品包含強化質 (可堆疊 不可堆疊通用)
	 * 
	 * @param id
	 *            指定物件編號
	 * @param enchant
	 *            指定強化質
	 * @param count
	 *            數量
	 * @return
	 */
	public boolean checkEnchantItem(final int itemid, final int enchant,
			final long count) {
		if (ItemTable.get().getTemplate(itemid).isStackable()) {// 可以堆疊的物品
			L1ItemInstance item = findItemIdNoEq(itemid);
			if ((item != null) && (item.getEnchantLevel() == enchant)
					&& (item.getCount() >= count)) {
				return true;
			}
		} else {// 無法堆疊的物品
			L1ItemInstance[] itemList = findItemsIdNoEqWithEnchant(itemid,
					enchant);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 未裝備物品清單包含強化值(陣列) (不可堆疊物品)
	 * 
	 * @param itemId
	 * @param enchant
	 *            檢查強化值
	 * @return
	 */
	public L1ItemInstance[] findItemsIdNoEqWithEnchant(final int itemId,
			int enchant) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if ((item.getEnchantLevel() == enchant) && (!item.isEquipped())) {// 強化值相同且未裝備
				if (item.getItemId() == itemId) {
					itemList.add(item);
				} else {// 道具ID不相同 改為尋找名字相同的道具
					L1Item finditem = ItemTable.get().getTemplate(itemId);// 正在尋找的道具
					if (finditem != null && finditem.getType2() != 0) {// 道具資料不為空
																		// 且
																		// 不是一般道具
						if (item.getName().equals(finditem.getName())) {// 與身上道具名稱相同
							itemList.add(item); // 加入列表
						}
					}
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}
	/**
	 * 刪除未裝備指定的物品包含強化質
	 * @param id 指定物件編號
	 * @param enchant 指定強化質
	 * @param count 數量
	 * @return
	 */
	public boolean consumeEnchantItem(final int id, final int enchant, final long count) {
		int num = 0;
		for (final L1ItemInstance item : this._items) {
			if (item.isEquipped()) { // 裝備該當
				continue;
			}
			if ((item.getItemId() == id) && (item.getEnchantLevel() == enchant)) {
				if (item.getItem().isStackable()) {
					this.removeItem(item, count);
					return true;
				} else {
					this.removeItem(item, 1L);
					num++;
					if (num >= count) {
						return true;
					}
				}
			}
		}
		final L1ItemInstance[] items = findItemsIdNoEqWithEnchant(id, enchant);
		for (final L1ItemInstance item : items) {
			if (item.isEquipped()) {
				continue;
			}
			if (!item.getItem().isStackable()) {
				this.removeItem(item, 1L);
				num++;
				if (num >= count) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 比較未裝備物品數量
	 * @param nameid
	 * @param count
	 * @return
	 */
	public boolean checkItemNotEquipped(final String nameid, final long count) {
		if (count == 0) {
			return true;
		}
		return count <= this.countItems(nameid);
	}

	/**
	 * 比較未裝備物品數量
	 * @param id
	 * @param count
	 * @return
	 */
	public boolean checkItemNotEquipped(final int id, final long count) {
		if (count == 0) {
			return true;
		}
		return count <= this.countItems(id);
	}

	// 特定全必要個數所持確認（複數所持確認）
	public boolean checkItem(final int[] ids) {
		final int len = ids.length;
		final int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return this.checkItem(ids, counts);
	}

	public boolean checkItem(final int[] ids, final int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!this.checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找未裝備物品數量
	 * @param itemId
	 * @return
	 */
	public long countItems(final int itemId) {
		// 可堆疊
		if (ItemTable.get().getTemplate(itemId).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemId);
			if (item != null) {
				return item.getCount();
			}
			
		// 不可堆疊
		} else {
			final Object[] itemList = this.findItemsIdNotEquipped(itemId);
			return itemList.length;
		}
		return 0;
	}

	/**
	 * 查找未裝備物品數量
	 * @param nameid
	 * @return
	 */
	public long countItems(final String nameid) {
		// 可堆疊
		if (ItemTable.get().getTemplate(nameid).isStackable()) {
			final L1ItemInstance item = this.findItemId(nameid);
			if (item != null) {
				return item.getCount();
			}
			
		// 不可堆疊
		} else {
			final Object[] itemList = this.findItemsIdNotEquipped(nameid);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(this._items);
	}

	/**
	 * 背包內全部物件刪除
	 */
	public void clearItems() {
		for (final Object itemObject : this._items) {
			final L1ItemInstance item = (L1ItemInstance) itemObject;
			World.get().removeObject(item);
		}
	}

	// 用
	public void loadItems() {
	}

	public void insertItem(final L1ItemInstance item) {
	}

	public void updateItem(final L1ItemInstance item) {
	}

	public void updateItem(final L1ItemInstance item, final int colmn) {
	}

	/**
	 * 限制道具存在时间
	 * 
	 * @param item
	 */
	private void set_time_item(L1ItemInstance item) {
		if (item.get_time() == null) {
			int date = -1;
			if (ItemTimeTable.TIME.get(item.getItemId()) != null) {
				date = ItemTimeTable.TIME.get(item.getItemId()).intValue();
			}

			if (date != -1) {
				long time = System.currentTimeMillis();// 目前时间豪秒
				long x1 = date * 60 * 60;// 指定小时耗用秒数
				long x2 = x1 * 1000;// 转为豪秒
				long upTime = x2 + time;// 目前时间 加上指定天数耗用秒数

				// 时间数据
				final Timestamp ts = new Timestamp(upTime);
				item.set_time(ts);
				item.setIdentified(true);
				// 人物背包物品使用期限资料
				CharItemsTimeReading.get().addTime(item.getId(), ts);
				L1PcInventory pc_inv = (L1PcInventory) this;
				L1PcInstance pc = pc_inv.getOwner();
				pc.sendPackets(new S_ItemName(item));
			  }
		   }
	    }
     }

