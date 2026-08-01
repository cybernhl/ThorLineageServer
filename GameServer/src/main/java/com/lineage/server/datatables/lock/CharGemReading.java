package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharGemTable;
import com.lineage.server.datatables.storage.CharGemStorage;
import com.lineage.server.templates.L1ItemGem;

/**
 * 
 * 類名稱：CharGemReading<br>
 * 類描述：武器寶石鑲嵌系統<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:13:05<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class CharGemReading {

	private final Lock _lock;

	private final CharGemStorage _storage;

	private static CharGemReading _instance;

	private CharGemReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CharGemTable();
	}

	public static CharGemReading get() {
		if (_instance == null) {
			_instance = new CharGemReading();
		}
		return _instance;
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		this._lock.lock();
		try {
			this._storage.load();
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 增加人物額外屬性資料
	 * @param objId
	 * @param attr
	 */
	public void storeItem(final int objId, final L1ItemGem attr) {
		this._lock.lock();
		try {
			this._storage.storeItem(objId, attr);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 更新人物額外屬性資料
	 * @param item_obj_id
	 * @param attr
	 */
	public void updateItem(final int item_obj_id, final L1ItemGem attr) {
		this._lock.lock();
		try {
			this._storage.updateItem(item_obj_id, attr);
			
		} finally {
			this._lock.unlock();
		}
	}
}
