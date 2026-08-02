package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.MapData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MapData 模型轉換器
 */
public class MapDataMapper implements RowMapper<MapData> {

    private static final MapDataMapper _instance = new MapDataMapper();

    public static MapDataMapper get() {
        return _instance;
    }

    @Override
    public MapData mapRow(ResultSet rs) throws SQLException {
        final MapData data = new MapData();
        
        data.mapId = rs.getInt("mapid");
        data.locationname = rs.getString("locationname");
        data.startX = rs.getInt("startX");
        data.endX = rs.getInt("endX");
        data.startY = rs.getInt("startY");
        data.endY = rs.getInt("endY");
        data.monster_amount = rs.getDouble("monster_amount");
        data.dropRate = rs.getDouble("drop_rate");
        data.isUnderwater = rs.getBoolean("is_underwater");
        data.markable = rs.getBoolean("markable");
        data.teleportable = rs.getBoolean("teleportable");
        data.escapable = rs.getBoolean("escapable");
        data.isUseResurrection = rs.getBoolean("is_use_resurrection");
        data.isUsePainwand = rs.getBoolean("is_use_painwand");
        data.isEnabledDeathPenalty = rs.getBoolean("is_enabled_death_penalty");
        data.isTakePets = rs.getBoolean("is_take_pets");
        data.isRecallPets = rs.getBoolean("is_recall_pets");
        data.isUsableItem = rs.getBoolean("is_usable_item");
        data.isUsableSkill = rs.getBoolean("is_usable_skill");
        
        try {
            data.isBot = rs.getBoolean("is_bot");
            data.isBotItem = rs.getBoolean("is_bot_item");
            data.isClan_Teleprot = rs.getBoolean("is_clan_teleport");
        } catch (SQLException e) {
            // 忽略缺失欄位
        }

        return data;
    }
}
