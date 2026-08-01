package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharacterQuestTable;
import com.lineage.server.datatables.storage.CharacterQuestStorage;

/**
 * 任務紀錄
 * @author dexc
 *
 */
public class CharacterQuestReading {

	private final Lock _lock;

	private final CharacterQuestStorage _storage;

	private static CharacterQuestReading _instance;

	private CharacterQuestReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CharacterQuestTable();
	}

	public static CharacterQuestReading get() {
		if (_instance == null) {
			_instance = new CharacterQuestReading();
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
	 * 傳回任務組
	 * @param char_id 人物OBJID
	 * @return
	 */
	public Map<Integer, Integer> get(final int char_id) {
		this._lock.lock();
		Map<Integer, Integer> tmp = null;
		try {
			tmp = this._storage.get(char_id);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 新建任務
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 * @param value 任務進度
	 */
	public void storeQuest(final int char_id, final int key, final int value) {
		this._lock.lock();
		try {
			this._storage.storeQuest(char_id, key, value);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 更新任務進度
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 * @param value 任務進度
	 */
	public void updateQuest(final int char_id, final int key, final int value) {
		this._lock.lock();
		try {
			this._storage.updateQuest(char_id, key, value);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 解除任務
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 */
	public void delQuest(final int char_id, final int key) {
		this._lock.lock();
		try {
			this._storage.delQuest(char_id, key);
			
		} finally {
			this._lock.unlock();
		}
    }  
    /**
     * 每日领取充值时间
     * 
     * @param char_id
     *            人物OBJID
     * @param key
     *            任務編號
     */
    public void storeQuest2(int char_id, int key, int value)
    {
      this._lock.lock();
      try
      {
        this._storage.storeQuest2(char_id, key, value);
      }
      finally
      {
        this._lock.unlock();
      }
    }
    /**
     * 每日领取充值时间
     * 
     * @param char_id
     *            人物OBJID
     * @param key
     *            任務編號
     */
    public void delQuest2(int key)
    {
      this._lock.lock();
      try
      {
        this._storage.delQuest2(key);
      }
      finally
      {
        this._lock.unlock();
      }
    }
  }