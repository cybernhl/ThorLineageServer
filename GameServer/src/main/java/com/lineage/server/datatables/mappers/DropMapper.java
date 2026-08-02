package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Drop;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Drop 模型轉換器
 */
public class DropMapper implements RowMapper<L1Drop> {

    private static final DropMapper _instance = new DropMapper();

    public static DropMapper get() {
        return _instance;
    }

    @Override
    public L1Drop mapRow(ResultSet rs) throws SQLException {
        return new L1Drop(
            rs.getInt("mobId"),
            rs.getInt("itemid"),
            rs.getInt("min"),
            rs.getInt("max"),
            rs.getInt("chance"),
            rs.getInt("bless")
        );
    }
}
