package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.EzpayTable;
import com.lineage.server.datatables.storage.EzpayStorage;

/**
 * 网站购物资料
 * 
 * @author dexc
 */
public class EzpayReading {

	private final Lock _lock;

	private final EzpayStorage _storage;

	private static EzpayReading _instance;

	private EzpayReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new EzpayTable();
	}

	public static EzpayReading get() {
		if (_instance == null) {
			_instance = new EzpayReading();
		}
		return _instance;
	}

	/**
	 * 传回指定帐户汇款资料
	 * 
	 * @param loginName
	 *            帐号名称
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
	 * 传回指定帐户汇款资料
	 * 
	 * @param loginName
	 *            帐号名称
	 * @param id
	 *            流水号
	 * @return
	 */
	public int[] ezpayInfo(final String loginName, final int id) {
		this._lock.lock();
		int[] tmp = null;
		try {
			tmp = this._storage.ezpayInfo(loginName, id);

		} finally {
			this._lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新资料
	 * 
	 * @param loginName
	 *            帐号名称
	 * @param id
	 *            ID
	 * @param pcname
	 *            领取人物
	 * @param ip
	 *            IP
	 */
	public boolean update(final String loginName, final int id,
			final String pcname, final String ip, String pclan) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.update(loginName, id, pcname, ip,pclan);

		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}
