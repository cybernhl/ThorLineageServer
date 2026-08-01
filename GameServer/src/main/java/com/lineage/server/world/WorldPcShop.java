package com.lineage.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import com.lineage.server.model.Instance.L1NpcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 世界個人商店暫存區<BR>
 *
 * @author dexc
 *
 */
public class WorldPcShop {

    private static final Log _log = LogFactory.getLog(WorldPcShop.class);

    private static WorldPcShop _instance;

    private final ConcurrentHashMap<Integer, L1NpcInstance> _isNpc;

    private Collection<L1NpcInstance> _allNpcValues;

    public static WorldPcShop get() {
        if (_instance == null) {
            _instance = new WorldPcShop();
        }
        return _instance;
    }

    private WorldPcShop() {
        _isNpc = new ConcurrentHashMap<Integer, L1NpcInstance>();
    }

    /**
     * 全部寵物
     * @return
     */
    public Collection<L1NpcInstance> all() {
        try {
            final Collection<L1NpcInstance> vs = _allNpcValues;
            return (vs != null) ? vs : (_allNpcValues = Collections.unmodifiableCollection(_isNpc.values()));
            //return Collections.unmodifiableCollection(_isCrown.values());

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 寵物清單
     * @return
     */
    public ConcurrentHashMap<Integer, L1NpcInstance> map() {
        return _isNpc;
    }

    /**
     * 指定寵物數據
     * @param key
     * @return
     */
    public L1NpcInstance get(final Integer key) {
        try {
            return _isNpc.get(key);

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 加入寵物清單
     * @param key
     * @param value
     */
    public void put(final Integer key, final L1NpcInstance value) {
        try {
            _isNpc.put(key, value);

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移出寵物清單
     * @param key
     */
    public void remove(final Integer key) {
        try {
            _isNpc.remove(key);

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
