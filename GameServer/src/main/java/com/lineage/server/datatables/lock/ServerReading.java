package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ServerTable;
import com.lineage.server.datatables.storage.ServerStorage;

/**
 * 服務器資料
 *
 * @author dexc
 *
 */
public class ServerReading {

	private final Lock _lock;

	private final ServerStorage _storage;

	private static ServerReading _instance;

	private ServerReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new ServerTable();
	}

	public static ServerReading get() {
		if (_instance == null) {
			_instance = new ServerReading();
		}
		return _instance;
	}
	
	/**
	 * 預先加載服務器存檔資料
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
	 * 傳回服務器最小編號設置
	 */
	public int minId() {
		this._lock.lock();
		int temp = 0;
		try {
			temp = this._storage.minId();
			
		} finally {
			this._lock.unlock();
		}
		return temp;
	}
	
	/**
	 * 傳回服務器最大編號設置
	 */
	public int maxId() {
		this._lock.lock();
		int temp = 0;
		try {
			temp = this._storage.maxId();
			
		} finally {
			this._lock.unlock();
		}
		return temp;
	}
	
	/**
	 * 設定服務器關機<BR>
	 * 同時記錄已用最大編號
	 */
	public void isStop() {
		this._lock.lock();
		try {
			this._storage.isStop();
			
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 更新時間
	 * @param loginName
	 * @return
	 */
	public void Updata(final Long time) {
		this._lock.lock();
		try {
			this._storage.Updata(time);
		} finally {
			this._lock.unlock();
		}
	}
}
