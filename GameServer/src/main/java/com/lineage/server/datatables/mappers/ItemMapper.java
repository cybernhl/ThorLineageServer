package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemsArmor;
import com.lineage.server.templates.L1ItemsEtcItem;
import com.lineage.server.templates.L1ItemsWeapon;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 道具、武器、防具模型轉換器
 */
public class ItemMapper {

    private static final ItemMapper _instance = new ItemMapper();

    public static ItemMapper get() {
        return _instance;
    }

    /**
     * 封裝基礎 L1Item 屬性 (共通欄位)
     */
    private void fillBaseAttributes(L1Item item, ResultSet rs) throws SQLException {
        item.setItemId(rs.getInt("item_id"));
        item.setName(rs.getString("name"));
        item.setClassname(rs.getString("classname"));
        item.setNameId(rs.getString("name_id"));
        item.setWeight(rs.getInt("weight"));
        item.setGfxId(rs.getInt("invgfx"));
        item.setGroundGfxId(rs.getInt("grdgfx"));
        item.setItemDescId(rs.getInt("itemdesc_id"));
        item.setMinLevel(rs.getInt("min_lvl"));
        item.setMaxLevel(rs.getInt("max_lvl"));
        item.setBless(rs.getInt("bless"));
        item.setTradable(rs.getInt("trade") == 0);
        item.setCantDelete(rs.getInt("cant_delete") == 1);
        
        try {
            item.setAbility(rs.getInt("ability"));
        } catch (SQLException e) {
            // 容錯處理
        }
    }

    /**
     * 轉換 EtcItem (道具)
     */
    public L1ItemsEtcItem mapEtcItem(ResultSet rs) throws SQLException {
        final L1ItemsEtcItem item = new L1ItemsEtcItem();
        fillBaseAttributes(item, rs);
        
        item.setType2(0);
        // 此處對應 ItemTable.java 中的類型映射邏輯
        // 外部傳入解析後的 int 值，或在此處根據字串解析
        
        item.setDmgSmall(rs.getInt("dmg_small"));
        item.setDmgLarge(rs.getInt("dmg_large"));
        item.set_stackable(rs.getInt("stackable") == 1);
        item.setMaxChargeCount(rs.getInt("max_charge_count"));
        item.set_delayid(rs.getInt("delay_id"));
        item.set_delaytime(rs.getInt("delay_time"));
        item.set_delayEffect(rs.getInt("delay_effect"));
        item.setFoodVolume(rs.getInt("food_volume"));
        item.setToBeSavedAtOnce(rs.getInt("save_at_once") == 1);
        
        item.setQuality1(rs.getString("Quality1"));
        item.setQuality2(rs.getString("Quality2"));
        // ... 其他 Quality 欄位
        
        return item;
    }

    /**
     * 轉換 Weapon (武器)
     */
    public L1ItemsWeapon mapWeapon(ResultSet rs) throws SQLException {
        final L1ItemsWeapon weapon = new L1ItemsWeapon();
        fillBaseAttributes(weapon, rs);
        
        weapon.setType2(1);
        weapon.setUseType(1);
        
        weapon.setDmgSmall(rs.getInt("dmg_small"));
        weapon.setDmgLarge(rs.getInt("dmg_large"));
        weapon.setRange(rs.getInt("range"));
        weapon.set_safeenchant(rs.getInt("safenchant"));
        weapon.setUseRoyal(rs.getInt("use_royal") != 0);
        weapon.setUseKnight(rs.getInt("use_knight") != 0);
        weapon.setUseElf(rs.getInt("use_elf") != 0);
        weapon.setUseMage(rs.getInt("use_mage") != 0);
        weapon.setUseDarkelf(rs.getInt("use_darkelf") != 0);
        weapon.setHitModifier(rs.getInt("hitmodifier"));
        weapon.setDmgModifier(rs.getInt("dmgmodifier"));
        weapon.set_addstr(rs.getByte("add_str"));
        weapon.set_adddex(rs.getByte("add_dex"));
        weapon.set_addcon(rs.getByte("add_con"));
        weapon.set_addint(rs.getByte("add_int"));
        weapon.set_addwis(rs.getByte("add_wis"));
        weapon.set_addcha(rs.getByte("add_cha"));
        weapon.set_addhp(rs.getInt("add_hp"));
        weapon.set_addmp(rs.getInt("add_mp"));
        weapon.set_addhpr(rs.getInt("add_hpr"));
        weapon.set_addmpr(rs.getInt("add_mpr"));
        weapon.set_addsp(rs.getInt("add_sp"));
        weapon.set_mdef(rs.getInt("m_def"));
        weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
        weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
        weapon.set_canbedmg(rs.getInt("canbedmg"));
        weapon.setHasteItem(rs.getInt("haste_item") != 0);
        weapon.setMaxUseTime(rs.getInt("max_use_time"));
        weapon.set_isItemAttr(rs.getInt("isItemAttr"));
        
        return weapon;
    }

    /**
     * 轉換 Armor (防具)
     */
    public L1ItemsArmor mapArmor(ResultSet rs) throws SQLException {
        final L1ItemsArmor armor = new L1ItemsArmor();
        fillBaseAttributes(armor, rs);
        
        armor.setType2(2);
        armor.set_ac(rs.getInt("ac"));
        armor.set_safeenchant(rs.getInt("safenchant"));
        armor.setUseRoyal(rs.getInt("use_royal") != 0);
        armor.setUseKnight(rs.getInt("use_knight") != 0);
        armor.setUseElf(rs.getInt("use_elf") != 0);
        armor.setUseMage(rs.getInt("use_mage") != 0);
        armor.setUseDarkelf(rs.getInt("use_darkelf") != 0);
        armor.set_addstr(rs.getByte("add_str"));
        armor.set_addcon(rs.getByte("add_con"));
        armor.set_adddex(rs.getByte("add_dex"));
        armor.set_addint(rs.getByte("add_int"));
        armor.set_addwis(rs.getByte("add_wis"));
        armor.set_addcha(rs.getByte("add_cha"));
        armor.set_addhp(rs.getInt("add_hp"));
        armor.set_addmp(rs.getInt("add_mp"));
        armor.set_addhpr(rs.getInt("add_hpr"));
        armor.set_addmpr(rs.getInt("add_mpr"));
        armor.set_addsp(rs.getInt("add_sp"));
        armor.set_mdef(rs.getInt("m_def"));
        armor.setDamageReduction(rs.getInt("damage_reduction"));
        armor.setWeightReduction(rs.getInt("weight_reduction"));
        armor.setHitModifierByArmor(rs.getInt("hit_modifier"));
        armor.setDmgModifierByArmor(rs.getInt("dmg_modifier"));
        armor.setBowHitModifierByArmor(rs.getInt("bow_hit_modifier"));
        armor.setBowDmgModifierByArmor(rs.getInt("bow_dmg_modifier"));
        armor.setHasteItem(rs.getInt("haste_item") != 0);
        armor.set_defense_earth(rs.getInt("defense_earth"));
        armor.set_defense_water(rs.getInt("defense_water"));
        armor.set_defense_wind(rs.getInt("defense_wind"));
        armor.set_defense_fire(rs.getInt("defense_fire"));
        armor.set_regist_stun(rs.getInt("regist_stun"));
        armor.set_regist_stone(rs.getInt("regist_stone"));
        armor.set_regist_sleep(rs.getInt("regist_sleep"));
        armor.set_regist_freeze(rs.getInt("regist_freeze"));
        armor.set_regist_sustain(rs.getInt("regist_sustain"));
        armor.set_regist_blind(rs.getInt("regist_blind"));
        armor.setMaxUseTime(rs.getInt("max_use_time"));
        armor.set_isItemAttr(rs.getInt("isItemAttr"));
        
        return armor;
    }
}
