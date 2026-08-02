package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Trap;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Trap 模型轉換器
 */
public class TrapMapper implements RowMapper<L1Trap> {

    private static final TrapMapper _instance = new TrapMapper();

    public static TrapMapper get() {
        return _instance;
    }

    @Override
    public L1Trap mapRow(ResultSet rs) throws SQLException {
        // L1Trap 比較特殊，它原本就在構造函數中包含了 ResultSet 處理邏輯
        return new L1Trap(rs);
    }
}
