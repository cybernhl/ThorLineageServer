package com.lineage.server.datatables.lock;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.SpawnBossTable;
import com.lineage.server.datatables.storage.SpawnBossStorage;
import com.lineage.server.model.L1Spawn;

/**
 * BOSS召喚資料
 * 
 * @author dexc
 * 
 */
public class SpawnBossReading {

    private final Lock _lock;

    private final SpawnBossStorage _storage;

    /**
     * 靜態初始化器，由JVM來保證線程安全.
     */
    private static class Holder {
        static SpawnBossReading instance = new SpawnBossReading();
    }

    /**
     * 取得該類的實例.
     */
    public static SpawnBossReading get() {
        return Holder.instance;
    }

    private SpawnBossReading() {
        this._lock = new ReentrantLock(true);
        this._storage = new SpawnBossTable();
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
     * 更新資料庫 下次召喚時間紀錄
     * 
     * @param id
     * @param spawnTime
     */
    public void upDateNextSpawnTime(final int id, final Calendar spawnTime) {
        this._lock.lock();
        try {
            this._storage.upDateNextSpawnTime(id, spawnTime);

        } finally {
            this._lock.unlock();
        }
    }

    /**
     * BOSS召喚列表中物件
     * 
     * @param key
     * @return
     */
    public L1Spawn getTemplate(final int key) {
        this._lock.lock();
        L1Spawn tmp = null;
        try {
            tmp = this._storage.getTemplate(key);

        } finally {
            this._lock.unlock();
        }
        return tmp;
    }

    /**
     * BOSS召喚列表中物件(NPCID)
     * 
     * @return _bossId
     */
    public List<Integer> bossIds() {
        this._lock.lock();
        List<Integer> tmp = null;
        try {
            tmp = this._storage.bossIds();

        } finally {
            this._lock.unlock();
        }
        return tmp;
    }
    /**
     * 傳回所有BOSS召喚資料
     *
     * @return
     */
    public Map<Integer, L1Spawn> get_bossSpawnTable() {
        _lock.lock();
        Map<Integer, L1Spawn> tmp = null;
        try {
            tmp = _storage.get_bossSpawnTable();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public int BossSeachSize() {
        _lock.lock();
        int s = 0;
        try {
            s = _storage.BossSeachSize();
        } finally {
            this._lock.unlock();
        }
        return s;
    }

    public L1Spawn getBossSeach(final int key) {
        this._lock.lock();
        L1Spawn tmp = null;
        try {
            tmp = this._storage.getBossSeach(key);
        } finally {
            this._lock.unlock();
        }
        return tmp;
    }
}
