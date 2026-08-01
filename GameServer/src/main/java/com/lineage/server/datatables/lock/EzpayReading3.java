package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.EzpayTable3;
import com.lineage.server.datatables.storage.EzpayStorage3;

/**
 * 網站購物資料
 *
 * @author dexc
 *
 */
public class EzpayReading3 {

	private final Lock _lock;

	private final EzpayStorage3 _storage;

	private static EzpayReading3 _instance;

	private EzpayReading3() {
		this._lock = new ReentrantLock(true);
		this._storage = new EzpayTable3();
	}

	public static EzpayReading3 get() {
		if (_instance == null) {
			_instance = new EzpayReading3();
		}
		return _instance;
	}

	/**
	 * 傳回指定帳戶匯款資料
	 * 
	 * @param loginName 帳號名稱
	 * @return
	 */
	public Map<Integer, int[]> ezpayInfo(final String loginName) {
		this._lock.lock();
		Map<Integer, int[]> tmp = null;
		try {
			tmp = this._storage.ezpayInfo(loginName);

		} finally {
			this._lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id        ID
	 * @param pcname    領取人物
	 * @param ip        IP
	 */
	public boolean update(final String loginName, final int id, final String pcname, final String ip) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.update(loginName, id, pcname, ip);

		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}