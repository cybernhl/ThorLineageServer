package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Npc;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Npc 模型轉換器
 */
public class NpcMapper implements RowMapper<L1Npc> {

    private static final NpcMapper _instance = new NpcMapper();

    public static NpcMapper get() {
        return _instance;
    }

    @Override
    public L1Npc mapRow(ResultSet rs) throws SQLException {
        final L1Npc npc = new L1Npc();
        final int npcId = rs.getInt("npcid");
        npc.set_npcId(npcId);
        npc.set_name(rs.getString("name"));
        npc.set_nameid(rs.getString("nameid"));
        
        // 提取 classname 邏輯
        npc.set_classname(rs.getString("classname"));
        
        npc.setImpl(rs.getString("impl"));
        npc.set_gfxid(rs.getInt("gfxid"));
        npc.set_level(rs.getInt("lvl"));
        npc.set_hp(rs.getInt("hp"));
        npc.set_mp(rs.getInt("mp"));
        npc.set_ac(rs.getInt("ac"));
        npc.set_str(rs.getByte("str"));
        npc.set_con(rs.getByte("con"));
        npc.set_dex(rs.getByte("dex"));
        npc.set_wis(rs.getByte("wis"));
        npc.set_int(rs.getByte("intel"));
        npc.set_mr(rs.getInt("mr"));
        npc.set_exp(rs.getInt("exp"));
        npc.set_lawful(rs.getInt("lawful"));
        npc.set_size(rs.getString("size"));
        npc.set_weakAttr(rs.getInt("weakAttr"));
        npc.set_ranged(rs.getInt("ranged"));
        npc.setTamable(rs.getBoolean("tamable"));
        npc.set_passispeed(rs.getInt("passispeed"));
        npc.set_atkspeed(rs.getInt("atkspeed"));
        
        // 額外字段處理 (根據 NpcTable.java 邏輯)
        try {
            npc.setAtkMagicSpeed(rs.getInt("atk_magic_speed"));
            npc.setSubMagicSpeed(rs.getInt("sub_magic_speed"));
        } catch (SQLException e) {
            // 部分表可能缺少這些欄位，保持容錯
        }

        npc.set_undead(rs.getInt("undead"));
        npc.set_poisonatk(rs.getInt("poison_atk"));
        npc.set_paralysisatk(rs.getInt("paralysis_atk"));
        npc.set_agro(rs.getBoolean("agro"));
        npc.set_agrososc(rs.getBoolean("agrososc"));
        npc.set_agrocoi(rs.getBoolean("agrocoi"));
        
        // Family 邏輯在 NpcTable 中有靜態 Map，此處僅映射原始值
        // npc.set_family_string(rs.getString("family")); 

        npc.set_agrofamily(rs.getInt("agrofamily"));
        npc.set_agrogfxid1(rs.getInt("agrogfxid1"));
        npc.set_agrogfxid2(rs.getInt("agrogfxid2"));
        npc.set_picupitem(rs.getBoolean("picupitem"));
        npc.set_digestitem(rs.getInt("digestitem"));
        npc.set_bravespeed(rs.getBoolean("bravespeed"));
        npc.set_hprinterval(rs.getInt("hprinterval"));
        npc.set_hpr(rs.getInt("hpr"));
        npc.set_mprinterval(rs.getInt("mprinterval"));
        npc.set_mpr(rs.getInt("mpr"));
        npc.set_teleport(rs.getBoolean("teleport"));
        npc.set_randomlevel(rs.getInt("randomlevel"));
        npc.set_randomhp(rs.getInt("randomhp"));
        npc.set_randommp(rs.getInt("randommp"));
        npc.set_randomac(rs.getInt("randomac"));
        npc.set_randomexp(rs.getInt("randomexp"));
        npc.set_randomlawful(rs.getInt("randomlawful"));
        npc.set_damagereduction(rs.getInt("damage_reduction"));
        npc.set_hard(rs.getBoolean("hard"));
        npc.set_doppel(rs.getBoolean("doppel"));
        npc.set_IsTU(rs.getBoolean("IsTU"));
        npc.set_IsErase(rs.getBoolean("IsErase"));
        npc.setBowActId(rs.getInt("bowActId"));
        npc.setKarma(rs.getInt("karma"));
        npc.setTransformId(rs.getInt("transform_id"));
        npc.setTransformGfxId(rs.getInt("transform_gfxid"));
        npc.setLightSize(rs.getInt("light_size"));
        npc.setAmountFixed(rs.getBoolean("amount_fixed"));
        npc.setChangeHead(rs.getBoolean("change_head"));
        npc.setCantResurrect(rs.getBoolean("cant_resurrect"));

        return npc;
    }
}
