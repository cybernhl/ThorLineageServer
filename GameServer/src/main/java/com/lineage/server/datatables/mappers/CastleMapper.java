package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Castle;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * L1Castle 模型轉換器
 */
public class CastleMapper implements RowMapper<L1Castle> {

    private static final CastleMapper _instance = new CastleMapper();

    public static CastleMapper get() {
        return _instance;
    }

    @Override
    public L1Castle mapRow(ResultSet rs) throws SQLException {
        final L1Castle castle = new L1Castle(rs.getInt("castle_id"), rs.getString("name"));
        
        final Timestamp warTime = rs.getTimestamp("war_time");
        if (warTime != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(warTime.getTime());
            castle.setWarTime(cal);
        }
        
        castle.setTaxRate(rs.getInt("tax_rate"));
        castle.setPublicMoney(rs.getLong("public_money"));

        return castle;
    }
}
