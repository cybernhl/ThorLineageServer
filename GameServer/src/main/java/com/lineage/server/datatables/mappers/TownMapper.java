package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Town;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Town 模型轉換器
 */
public class TownMapper implements RowMapper<L1Town> {

    private static final TownMapper _instance = new TownMapper();

    public static TownMapper get() {
        return _instance;
    }

    @Override
    public L1Town mapRow(ResultSet rs) throws SQLException {
        final L1Town town = new L1Town();
        
        town.set_townid(rs.getInt("town_id"));
        town.set_name(rs.getString("name"));
        town.set_leader_id(rs.getInt("leader_id"));
        town.set_leader_name(rs.getString("leader_name"));
        town.set_tax_rate(rs.getInt("tax_rate"));
        town.set_tax_rate_reserved(rs.getInt("tax_rate_reserved"));
        town.set_sales_money(rs.getInt("sales_money"));
        town.set_sales_money_yesterday(rs.getInt("sales_money_yesterday"));
        town.set_town_tax(rs.getInt("town_tax"));
        town.set_town_fix_tax(rs.getInt("town_fix_tax"));

        return town;
    }
}
