package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.AccountTable;
import com.lineage.server.datatables.storage.AccountStorage;
import com.lineage.server.templates.L1Account;

/**
 * 人物帳戶資料
 *
 * @author dexc
 *
 */
public class AccountReading {

	private final Lock _lock;

	private final AccountStorage _storage;

	private static AccountReading _instance;

	private AccountReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new AccountTable();
	}

	public static AccountReading get() {
		if (_instance == null) {
			_instance = new AccountReading();
		}
		return _instance;
	}
	
	/**
	 * 預先加載帳戶名稱
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
	 * 傳回指定帳戶名稱資料是否存在
	 * @param loginName 帳號名稱
	 * 
	 * @return true:有該帳號 false:沒有該帳號
	 */
	public boolean isAccountUT(final String loginName) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.isAccountUT(loginName);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 建立帳號資料
	 * @param loginName 帳號
	 * @param pwd 密碼
	 * @param ip IP位置
	 * @param host MAC位置
	 * @param spwd 超級密碼
	 * @return L1Account
	 */
	public L1Account create(final String loginName, final String pwd,
			final String ip, final String host, final String spwd) {
		this._lock.lock();
		L1Account tmp = null;
		try {
			tmp = this._storage.create(loginName, pwd, ip, host, spwd);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 傳回指定帳戶資料是否存在
	 * @param loginName 帳號名稱
	 * 
	 * @return true:有該帳號 false:沒有該帳號
	 */
	public boolean isAccount(final String loginName) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.isAccount(loginName);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 傳回指定帳戶資料
	 * @param loginName 帳號
	 * 
	 * @return L1Account
	 */
	public L1Account getAccount(final String loginName) {
		this._lock.lock();
		L1Account tmp = null;
		try {
			tmp = this._storage.getAccount(loginName);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新倉庫密碼
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	public void updateWarehouse(final String loginName, final int pwd) {
		this._lock.lock();
		try {
			this._storage.updateWarehouse(loginName, pwd);
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新上線時間/IP/MAC
	 * @param account 帳戶
	 */
	public void updateLastActive(final L1Account account) {
		this._lock.lock();
		try {
			this._storage.updateLastActive(account);
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新帳號可用人物數量
	 * @param loginName 帳號
	 * @param count 擴充數量
	 */
	public void updateCharacterSlot(final String loginName, final int count) {
		this._lock.lock();
		try {
			this._storage.updateCharacterSlot(loginName, count);
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新帳號密碼
	 * @param loginName 帳號
	 * 
	 * @param newpwd 新密碼
	 */
	public void updatePwd(final String loginName, final String newpwd) {
		this._lock.lock();
		try {
			this._storage.updatePwd(loginName, newpwd);
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新帳號連線狀態
	 * @param loginName 帳號
	 * @param islan 是否連線
	 */
	public void updateLan(final String loginName, final boolean islan) {
		this._lock.lock();
		try {
			this._storage.updateLan(loginName, islan);
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新帳號連線狀態(全部離線)
	 */
	public void updateLan() {
		this._lock.lock();
		try {
			this._storage.updateLan();
			
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新滿額紀錄
	 *
	 * @param loginName
	 *            帳號
	 * @param value
	 *            紀錄編號
	 */
	public void updateFullAmountLog(final String loginName, final int value) {
		this._lock.lock();
		try {
			this._storage.updateFullAmountLog(loginName, value);
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新累積儲值紀錄
	 *
	 * @param loginName
	 *            帳號
	 * @param value
	 *            紀錄編號
	 */
	public void updateCumulativeStoredLog(final String loginName, final int value) {
		this._lock.lock();
		try {
			this._storage.updateCumulativeStoredLog(loginName, value);
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新累積消費紀錄
	 *
	 * @param loginName
	 *            帳號
	 * @param value
	 *            紀錄編號
	 */
	public void updateCumulativeConsumptionLog(final String loginName, final int value) {
		this._lock.lock();
		try {
			this._storage.updateCumulativeConsumptionLog(loginName, value);
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新累積儲值金額
	 *
	 * @param loginName
	 *            帳號
	 * @param value
	 *            紀錄編號
	 */
	public void updateStoredMoney(final String loginName, final int value) {
		this._lock.lock();
		try {
			this._storage.updateStoredMoney(loginName, value);
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 更新累積消費金額
	 *
	 * @param loginName
	 *            帳號
	 * @param value
	 *            紀錄編號
	 */
	public void updateConsumptionMoney(final String loginName, final int value) {
		this._lock.lock();
		try {
			this._storage.updateConsumptionMoney(loginName, value);
		} finally {
			this._lock.unlock();
		}
	}

	/**
	 * 是否首儲
	 *
	 */
	public void updatefp(final String loginName, final int fp) { // SRC0701
		this._lock.lock();
		try {
			this._storage.updatefp(loginName, 1);

		} finally {
			this._lock.unlock();
		}
	}
}
