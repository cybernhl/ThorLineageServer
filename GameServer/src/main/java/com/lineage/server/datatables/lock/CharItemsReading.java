package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharItemsTable;
import com.lineage.server.datatables.storage.CharItemsStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 人物背包資料
 * @author dexc
 *
 */
public class CharItemsReading {

	private final Lock _lock;

	private final CharItemsStorage _storage;

	private static CharItemsReading _instance;

	private CharItemsReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CharItemsTable();
	}

	public static CharItemsReading get() {
		if (_instance == null) {
			_instance = new CharItemsReading();
		}
		return _instance;
	}

	/**
	 * 資料預先載入
	 */
	public void load() {
		_lock.lock();
		try {
			_storage.load();
			
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 傳回該人物背包資料
	 * @param objid
	 * @return
	 */
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final Integer objid) {
		_lock.lock();
		CopyOnWriteArrayList<L1ItemInstance> tmp = null;
		try {
			tmp = _storage.loadItems(objid);
			
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 刪除人物背包資料(完整)
	 * @param objid
	 */
	public void delUserItems(final Integer objid) {
		_lock.lock();
		try {
			_storage.delUserItems(objid);
			
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 刪除指定編號全部數據
	 * @param itemid
	 */
	public void del_item(final int itemid) {
		_lock.lock();
		try {
			_storage.del_item(itemid);
			
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 增加背包物品
	 * @param objId
	 * @param item
	 * @throws Exception
	 */
	public void storeItem(final int objId, final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.storeItem(objId, item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除背包物品
	 * @param objid 人物OBJID
	 * @param item 物品
	 * 
	 * @throws Exception
	 */
	public void deleteItem(final int objid, final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.deleteItem(objid, item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新物品ITEMID 與中文名稱
	 * @param item
	 */
	public void updateItemId_Name(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemId_Name(item);
			
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 更新ITEMID
	 * @param item
	 * @throws Exception
	 */
	public void updateItemId(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemId(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新數量
	 * @param item
	 * @throws Exception
	 */
	public void updateItemCount(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemCount(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新損壞度
	 * @param item
	 * @throws Exception
	 */
	public void updateItemDurability(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemDurability(item);
			
		} finally {
			_lock.unlock();
		}
	}

	public void updateAttachIndex(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateAttachIndex(item);

		} finally {
			_lock.unlock();
		}
	}

	public void updateGemHole(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateGemHole(item);

		} finally {
			_lock.unlock();
		}
	}

	public void updateGemHoleIndex(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateGemHoleIndex(item);

		} finally {
			_lock.unlock();
		}
	}
	public void updateCanAbilityType(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateCanAbilityType(item);

		} finally {
			_lock.unlock();
		}
	}
	public void updateAbilityPos1ID(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateAbilityPos1ID(item);

		} finally {
			_lock.unlock();
		}
	}
	public void updateAbilityPos2ID(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateAbilityPos2ID(item);

		} finally {
			_lock.unlock();
		}
	}
	public void updateAbilityPos3ID(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateAbilityPos3ID(item);

		} finally {
			_lock.unlock();
		}
	}
	public void updateSpecialStat(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateSpecialStat(item);

		} finally {
			_lock.unlock();
		}
	}

	public void updateItemProtection(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemProtection(item);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新可用次數
	 * @param item
	 * @throws Exception
	 */
	public void updateItemChargeCount(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemChargeCount(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新可用時間
	 * @param item
	 * @throws Exception
	 */
	public void updateItemRemainingTime(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemRemainingTime(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新強化度
	 * @param item
	 * @throws Exception
	 */
	public void updateItemEnchantLevel(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemEnchantLevel(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新使用狀態
	 * @param item
	 * @throws Exception
	 */
	public void updateItemEquipped(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemEquipped(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新鑒定狀態
	 * @param item
	 * @throws Exception
	 */
	public void updateItemIdentified(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemIdentified(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新祝福狀態
	 * @param item
	 * @throws Exception
	 */
	public void updateItemBless(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemBless(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新強化屬性
	 * @param item
	 * @throws Exception
	 */
	public void updateItemAttrEnchantKind(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemAttrEnchantKind(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新強化屬性強化度
	 * @param item
	 * @throws Exception
	 */
	public void updateItemAttrEnchantLevel(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemAttrEnchantLevel(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新最後使用時間
	 * @param item
	 * @throws Exception
	 */
	public void updateItemDelayEffect(final L1ItemInstance item) throws Exception {
		_lock.lock();
		try {
			_storage.updateItemDelayEffect(item);
			
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 傳回對應所有物品數量
	 * @param objId
	 * @return
	 * @throws Exception
	 */
	public int getItemCount(final int objId) throws Exception {
		_lock.lock();
		int tmp = 0;
		try {
			tmp = _storage.getItemCount(objId);
			
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 給予金幣(對離線人物)
	 * @param objId
	 * @param count
	 * @throws Exception
	 */
	public void getAdenaCount(final int objId, final long count) throws Exception {
		_lock.lock();
		try {
			_storage.getAdenaCount(objId, count);
			
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * 該人物背包是否有指定數據
	 * @param pcObjId
	 * @param objid
	 * @param count 
	 * @return 
	 */
	public boolean getUserItems(final int pcObjId, final int objid, long count) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.getUserItems(pcObjId, objid, count);
			
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 是否有指定數據
	 * @param pcObjid
	 * @param objid
	 * @param count
	 * @return 
	 */
	public boolean getUserItem(final int objid) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.getUserItem(objid);
			
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回佣有該物品ID的人物清單<BR>
	 * (適用該物品每人只能傭有一個的狀態)
	 * @param itemid
	 * @return
	 */
	public Map<Integer, L1ItemInstance> getUserItems(int itemid) {
		_lock.lock();
		Map<Integer, L1ItemInstance> tmp = null;
		try {
			tmp = _storage.getUserItems(itemid);
			
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
