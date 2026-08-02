package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1House;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * L1House 模型轉換器
 */
public class HouseMapper implements RowMapper<L1House> {

    private static final HouseMapper _instance = new HouseMapper();

    public static HouseMapper get() {
        return _instance;
    }

    @Override
    public L1House mapRow(ResultSet rs) throws SQLException {
        final L1House house = new L1House();
        
        house.setHouseId(rs.getInt("house_id"));
        house.setHouseName(rs.getString("house_name"));
        house.setHouseArea(rs.getInt("house_area"));
        house.setLocation(rs.getString("location"));
        house.setKeeperId(rs.getInt("keeper_id"));
        house.setOnSale(rs.getInt("is_on_sale") != 0);
        house.setPurchaseBasement(rs.getInt("is_purchase_basement") != 0);
        
        final Timestamp taxDeadline = rs.getTimestamp("tax_deadline");
        if (taxDeadline != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(taxDeadline.getTime());
            house.setTaxDeadline(cal);
        }

        return house;
    }
}
