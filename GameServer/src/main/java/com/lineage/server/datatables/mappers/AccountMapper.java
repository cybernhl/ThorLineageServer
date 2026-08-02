package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Account;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Account 模型轉換器
 */
public class AccountMapper implements RowMapper<L1Account> {

    private static final AccountMapper _instance = new AccountMapper();

    public static AccountMapper get() {
        return _instance;
    }

    @Override
    public L1Account mapRow(ResultSet rs) throws SQLException {
        final L1Account account = new L1Account();
        
        account.set_login(rs.getString("login"));
        account.set_password(rs.getString("password"));
        account.set_lastactive(rs.getTimestamp("lastactive"));
        account.set_access_level(rs.getInt("access_level"));
        account.set_ip(rs.getString("ip"));
        account.set_mac(rs.getString("mac"));
        account.set_character_slot(rs.getInt("character_slot"));
        account.set_spw(rs.getString("spw"));
        account.set_warehouse(rs.getInt("warehouse"));
        
        // 額外字段處理 (金流相關)
        try {
            account.set_FullAmount_Log(rs.getInt("full_amount_log"));
            account.set_CumulativeStored_Log(rs.getInt("cumulative_stored_log"));
            account.set_CumulativeConsumption_Log(rs.getInt("cumulative_consumption_log"));
            account.set_StoredMoney(rs.getInt("stored_money"));
            account.set_ConsumptionMoney(rs.getInt("consumption_money"));
            account.set_first_pay(rs.getInt("first_pay"));
        } catch (SQLException e) {
            // 保持相容性，忽略缺失欄位
        }

        return account;
    }
}
