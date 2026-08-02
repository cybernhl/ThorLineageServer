package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Exp;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Exp 模型轉換器
 */
public class ExpMapper implements RowMapper<L1Exp> {

    private static final ExpMapper _instance = new ExpMapper();

    public static ExpMapper get() {
        return _instance;
    }

    @Override
    public L1Exp mapRow(ResultSet rs) throws SQLException {
        final L1Exp exp = new L1Exp();
        exp.set_level(rs.getInt("level"));
        exp.set_exp(rs.getLong("exp"));
        exp.set_expPenalty(rs.getDouble("expPenalty"));
        return exp;
    }
}
