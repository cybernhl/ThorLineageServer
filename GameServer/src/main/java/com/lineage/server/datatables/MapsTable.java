package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.MapData;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 地圖設置資料
 *
 * @author dexc
 *
 */
public final class MapsTable {

	private static final Log _log = LogFactory.getLog(MapsTable.class);

	/*private class MapData {
		public int startX = 0;
		public int endX = 0;
		public int startY = 0;
		public int endY = 0;
		public double monster_amount = 1;
		public double dropRate = 1;
		public boolean isUnderwater = false;
		public boolean markable = false;
		public boolean teleportable = false;
		public boolean escapable = false;
		public boolean isUseResurrection = false;
		public boolean isUsePainwand = false;
		public boolean isEnabledDeathPenalty = false;
		public boolean isTakePets = false;
		public boolean isRecallPets = false;
		public boolean isUsableItem = false;
		public boolean isUsableSkill = false;
	}*/

	private static MapsTable _instance;

	/**
	 * Key(mapId) Value(MapData)
	 */
	private static final Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * 可否讀迂、HashMap _maps格納。
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `mapids`");

			for (rs = pstm.executeQuery(); rs.next();) {
				final MapData data = new MapData();
				final int mapId = rs.getInt("mapid");
				
				data.mapId = mapId;
				data.locationname = rs.getString("locationname");
				data.startX = rs.getInt("startX");
				data.endX = rs.getInt("endX");
				data.startY = rs.getInt("startY");
				data.endY = rs.getInt("endY");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");
                data.isBot = rs.getBoolean("bot");// 此地圖是否可以掛機
                data.isBotItem = rs.getBoolean("botitem");// 此地圖掛機是否可以獲得物品
				data.isClan_Teleprot = rs.getBoolean("穿雲箭血盟限制地圖");// 穿雲箭血盟限制地圖
                
				_maps.put(new Integer(mapId), data);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		_log.info("載入地圖設置資料數量: " + _maps.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * MapsTable返。
	 *
	 * @return MapsTable
	 */
	public static MapsTable get() {
		if (_instance == null) {
			_instance = new MapsTable();
		}
		return _instance;
	}
	
	/**
	 * 傳回全部地圖資料
	 * @return
	 */
	public Map<Integer, MapData> getMaps() {
		return _maps;
	}

	/**
	 * 傳回地圖名稱
	 * 
	 * @param mapId
	 *            地圖編號
	 * @return 地圖名稱
	 */
	public String getMapName(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return null;
		}
		return _maps.get(mapId).locationname;
	}
	
	/**
	 * X開始座標
	 *
	 * @param mapId 地圖編號
	 * @return X開始座標
	 */
	public int getStartX(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * X終了座標返。
	 *
	 * @param mapId
	 *            調ID
	 * @return X終了座標
	 */
	public int getEndX(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * Y開始座標返。
	 *
	 * @param mapId
	 *            調ID
	 * @return Y開始座標
	 */
	public int getStartY(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * Y終了座標返。
	 *
	 * @param mapId
	 *            調ID
	 * @return Y終了座標
	 */
	public int getEndY(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * NPC數量
	 *
	 * @param mapId
	 *            調ID
	 * @return 量倍率
	 */
	public double getMonsterAmount(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * 掉落倍率
	 *
	 * @param mapId
	 *            調ID
	 * @return 倍率
	 */
	public double getDropRate(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * 水中
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return 水中true
	 */
	public boolean isUnderwater(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUnderwater;
	}

	/**
	 * 記憶座標
	 *
	 * @param mapId
	 *            調ID
	 * @return 可能true
	 */
	public boolean isMarkable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * 使用傳送
	 *
	 * @param mapId
	 *            調ID
	 * @return 可能true
	 */
	public boolean isTeleportable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * 使用回捲
	 *
	 * @param mapId
	 *            調ID
	 * @return 可能true
	 */
	public boolean isEscapable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * 復活
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return 復活可能true
	 */
	public boolean isUseResurrection(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * 使用魔杖
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return 使用可能true
	 */
	public boolean isUsePainwand(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * 死亡逞罰
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return true
	 */
	public boolean isEnabledDeathPenalty(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * 攜帶寵物
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return ・連行true
	 */
	public boolean isTakePets(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * 召喚寵物
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return ・呼出true
	 */
	public boolean isRecallPets(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * 使用物品
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return 使用true
	 */
	public boolean isUsableItem(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * 使用技能
	 *
	 * @param mapId
	 *            調ID
	 *
	 * @return 使用true
	 */
	public boolean isUsableSkill(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}
	
    /**
     * 此地圖是否允許掛機
     * @param mapId
     * @return
     */
    public boolean isBot(final int mapId) {
    	final MapData map = _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return _maps.get(mapId).isBot;
    }
    
    /**
     * 此地圖掛機是否有收益
     * @param mapId
     * @return
     */
    public boolean isBotItem(final int mapId) {
    	final MapData map = _maps.get(mapId);
        if (map == null) {
            return false;
        }
        return _maps.get(mapId).isBotItem;
    }

    /**
     * 傳回指定的地圖資料
     * @param mapids
     * @return
     */
	public MapData getTemplate(int mapids) {
		return _maps.get(mapids);
	}

}
