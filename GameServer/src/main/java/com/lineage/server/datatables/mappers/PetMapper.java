package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Pet;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Pet 模型轉換器
 */
public class PetMapper implements RowMapper<L1Pet> {

    private static final PetMapper _instance = new PetMapper();

    public static PetMapper get() {
        return _instance;
    }

    @Override
    public L1Pet mapRow(ResultSet rs) throws SQLException {
        final L1Pet pet = new L1Pet();
        
        pet.set_itemobjid(rs.getInt("item_obj_id"));
        pet.set_objid(rs.getInt("objid"));
        pet.set_npcid(rs.getInt("npcid"));
        pet.set_name(rs.getString("name"));
        pet.set_level(rs.getInt("lvl"));
        pet.set_hp(rs.getInt("hp"));
        pet.set_mp(rs.getInt("mp"));
        pet.set_exp(rs.getInt("exp"));
        pet.set_lawful(rs.getInt("lawful"));

        return pet;
    }
}
