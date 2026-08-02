package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Bank;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Bank 模型轉換器
 */
public class BankMapper implements RowMapper<L1Bank> {

    private static final BankMapper _instance = new BankMapper();

    public static BankMapper get() {
        return _instance;
    }

    @Override
    public L1Bank mapRow(ResultSet rs) throws SQLException {
        final L1Bank bank = new L1Bank();
        
        bank.set_account_name(rs.getString("account_name"));
        bank.set_adena_count(rs.getLong("adena_count"));
        bank.set_pass(rs.getString("pass"));

        return bank;
    }
}
