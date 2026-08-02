package com.lineage.server.datatables.mappers;

import com.lineage.server.model.L1Clan;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Clan 模型轉換器
 */
public class ClanMapper implements RowMapper<L1Clan> {

    private static final ClanMapper _instance = new ClanMapper();

    public static ClanMapper get() {
        return _instance;
    }

    @Override
    public L1Clan mapRow(ResultSet rs) throws SQLException {
        final L1Clan clan = new L1Clan();
        
        clan.setClanId(rs.getInt("clan_id"));
        clan.setClanName(rs.getString("clan_name"));
        clan.setLeaderId(rs.getInt("leader_id"));
        clan.setLeaderName(rs.getString("leader_name"));
        clan.setCastleId(rs.getInt("hascastle"));
        clan.setHouseId(rs.getInt("hashouse"));
        clan.setLevel(rs.getInt("level"));
        
        // 額外字段處理 (技能相關)
        try {
            clan.set_clanskill(rs.getBoolean("clanskill"));
            clan.set_skilltime(rs.getTimestamp("skilltime"));
        } catch (SQLException e) {
            // 忽略缺失欄位
        }

        return clan;
    }
}
