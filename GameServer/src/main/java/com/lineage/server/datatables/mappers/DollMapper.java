package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Doll;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Doll 模型轉換器
 */
public class DollMapper implements RowMapper<L1Doll> {

    private static final DollMapper _instance = new DollMapper();

    public static DollMapper get() {
        return _instance;
    }

    @Override
    public L1Doll mapRow(ResultSet rs) throws SQLException {
        final L1Doll doll = new L1Doll();
        
        doll.set_itemid(rs.getInt("itemid"));
        doll.set_time(rs.getInt("time"));
        doll.set_type(rs.getInt("type"));
        
        // 額外字段處理 (根據 DollPowerTable.java 可能有的擴充)
        try {
            doll.set_gfxid(rs.getInt("gfxid"));
            doll.set_nameid(rs.getString("nameid"));
        } catch (SQLException e) {
            // 忽略
        }

        return doll;
    }
}
