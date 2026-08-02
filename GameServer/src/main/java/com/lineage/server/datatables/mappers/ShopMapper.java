package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1ShopS;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1ShopS 模型轉換器
 */
public class ShopMapper implements RowMapper<L1ShopS> {

    private static final ShopMapper _instance = new ShopMapper();

    public static ShopMapper get() {
        return _instance;
    }

    @Override
    public L1ShopS mapRow(ResultSet rs) throws SQLException {
        final L1ShopS shop = new L1ShopS();
        
        shop.set_id(rs.getInt("id"));
        shop.set_item_obj_id(rs.getInt("item_obj_id"));
        shop.set_user_obj_id(rs.getInt("user_obj_id"));
        shop.set_adena(rs.getInt("adena"));
        shop.set_overtime(rs.getTimestamp("overtime"));
        shop.set_end(rs.getInt("end"));
        shop.set_none(rs.getString("none"));

        return shop;
    }
}
