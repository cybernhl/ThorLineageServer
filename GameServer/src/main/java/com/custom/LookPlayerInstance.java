package com.custom;

import com.custom.ability.AbilityData;
import com.custom.ability.CustomArmorAbility;
import com.custom.ability.CustomWeaponAbility;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemPower;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;
import com.william.L1WilliamEnchantOrginal;
import com.william.L1WilliamEnchantOrginal1;

import java.util.ArrayList;

public class LookPlayerInstance {
    private final String name;
    private final ArrayList<L1ItemInstance> armors;
    private final L1ItemInstance weapon;
    private final int maxhp,maxmp,ac,mr,str,con,dex,wis,int_,cha,sp,level;
    private final L1PcInstance pc;
    public LookPlayerInstance(final L1PcInstance pc, final String name, final ArrayList<L1ItemInstance> armors, final L1ItemInstance weapon,
                              final int maxhp, final int maxmp, final int ac, final int mr, final int str, final int con,
                              final int dex, final int wis, final int int_, final int cha, final int sp, final int level) {
        this.pc = pc;
        this.name = name;
        this.armors = armors;
        this.weapon = weapon;
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.ac = ac;
        this.mr = mr;
        this.str = str;
        this.con = con;
        this.dex = dex;
        this.wis = wis;
        this.int_ = int_;
        this.cha = cha;
        this.sp = sp;
        this.level = level;
    }

    public int getMaxhp() {
        return maxhp;
    }

    public int getMaxmp() {
        return maxmp;
    }

    public int getAc() {
        return ac;
    }

    public int getMr() {
        return mr;
    }

    public int getStr() {
        return str;
    }

    public int getCon() {
        return con;
    }

    public int getDex() {
        return dex;
    }

    public int getWis() {
        return wis;
    }

    public int getInt() {
        return int_;
    }

    public int getCha() {
        return cha;
    }

    public int getSp() {
        return sp;
    }

    public String getName() {
        return name;
    }
    public void showMainPage() {
        final String[] msg = new String[41];
        msg[0] = this.name;
        msg[1] = this.weapon == null ? "無裝備武器" : this.weapon.getLogName();
        for (int i = 0; i < this.armors.size(); i++) {
            msg[i + 2] = this.armors.get(i) == null ? "" : this.armors.get(i).getLogName();
        }
        pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayer", msg));
    }
    public boolean showPage(final String cmd) {
        if (cmd.equalsIgnoreCase("look_page_up")) {
            showMainPage();
            return true;
        }
        if (!cmd.startsWith("look_")) {
            return false;
        }
        try {
            int index = Integer.parseInt(cmd.replaceAll("look_", ""));
            final String[] msg = new String[21];
            if (index == 0) {
                msg[0] = "角色屬性如下：";
                msg[1] = "等級: " + level;
                msg[2] = "最大血量: " + maxhp;
                msg[3] = "最大魔力: " + maxmp;
                msg[4] = "防禦力: " + ac;
                msg[5] = "魔法抵抗: " + mr;
                msg[6] = "力量: " + str;
                msg[7] = "體力: " + con;
                msg[8] = "敏捷: " + dex;
                msg[9] = "精神: " + wis;
                msg[10] = "智力" + int_;
                msg[11] = "魅力: " + cha;
                msg[12] = "魔法攻擊力: " + sp;
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayershow", msg));
                return true;
            } else if (index == 1) {
                if (this.weapon == null) {
                    return false;
                }
                msg[0] = weapon.getLogName();
                int attr_DmgSmall = this.weapon.getAttachDmgSmall();
                int attr_DmgLarge = this.weapon.getAttachDmgLarge();
                int attr_HitModifier = this.weapon.getAttachHit();
                int attr_DmgModifier = this.weapon.getAttachOtherDamage();
                int add_str = 0;
                int add_con = 0;
                int add_dex = 0;
                int add_int = 0;
                int add_wis = 0;
                int add_cha = 0;
                int add_hp = 0;
                int add_mp = 0;
                int add_sp = 0;
                int add_ac = 0;
                msg[1] = "小怪攻擊: " + this.weapon.getItem().getDmgSmall() + "+" + this.weapon.getEnchantLevel();
                msg[2] = "大怪攻擊: " + this.weapon.getItem().getDmgLarge() + "+" + this.weapon.getEnchantLevel();
                msg[3] = "強化數: +" + this.weapon.getEnchantLevel();

                int get_addstr = this.weapon.getItem().get_addstr() + add_str;// 力量
                int get_adddex = this.weapon.getItem().get_adddex() + add_dex;// 敏捷
                int get_addcon = this.weapon.getItem().get_addcon() + add_con;// 體質
                int get_addwis = this.weapon.getItem().get_addwis() + add_wis;// 精神
                int get_addint = this.weapon.getItem().get_addint() + add_int;// 智力
                int get_addcha = this.weapon.getItem().get_addcha() + add_cha;// 魅力

                int get_addhp = this.weapon.getItem().get_addhp() + add_hp + this.weapon.getAttachHp();// +HP
                int get_addmp = this.weapon.getItem().get_addmp() + add_mp + this.weapon.getAttachMp();// +MP
                int mr = this.weapon.getAttachMr();// MR(抗魔)

                int addWeaponSp = this.weapon.getItem().get_addsp() + add_sp + this.weapon.getAttachMagicDamage();// SP(魔攻)
                int addDmgModifier = this.weapon.getItem().getDmgModifier() + attr_DmgModifier;// DG(攻擊力)
                int addHitModifier = this.weapon.getItem().getHitModifier() + attr_HitModifier;// Hit(攻擊成功)

                int pw_d4_1 = this.weapon.getItem().get_defense_fire();// 火屬性
                int pw_d4_2 = this.weapon.getItem().get_defense_water();// 水屬性
                int pw_d4_3 = this.weapon.getItem().get_defense_wind();// 風屬性
                int pw_d4_4 = this.weapon.getItem().get_defense_earth();// 地屬性

                int pw_k6_1 = this.weapon.getItem().get_regist_freeze();// 寒冰耐性
                int pw_k6_2 = this.weapon.getItem().get_regist_stone();// 石化耐性
                int pw_k6_3 = this.weapon.getItem().get_regist_sleep();// 睡眠耐性
                int pw_k6_4 = this.weapon.getItem().get_regist_blind();// 暗黑耐性
                int pw_k6_5 = this.weapon.getItem().get_regist_stun();// 昏迷耐性
                int pw_k6_6 = this.weapon.getItem().get_regist_sustain();// 支撐耐性

                if (weapon.get_power_name() != null) {
                    final L1ItemPower_name power = weapon.get_power_name();
                    switch (power.get_hole_1()) {
                        case 1:// 力  力+1
                            get_addstr += 1;
                            break;
                        case 2:// 敏  敏+1
                            get_adddex += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            get_addcon += 1;
                            get_addhp += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            get_addwis += 1;
                            get_addmp += 15;
                            break;
                        case 5:// 智  智力+1
                            get_addint += 1;
                            break;
                        case 6:// 魅  魅力+1
                            get_addcha += 1;
                            break;
                        case 7:// 血  血+25
                            get_addhp += 25;
                            break;
                        case 8:// 魔  魔+25
                            get_addmp += 25;
                            break;
                        case 9:// 攻  額外攻擊+3
                            addDmgModifier += 3;
                            break;
                    }
                    switch (power.get_hole_2()) {
                        case 1:// 力  力+1
                            get_addstr += 1;
                            break;
                        case 2:// 敏  敏+1
                            get_adddex += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            get_addcon += 1;
                            get_addhp += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            get_addwis += 1;
                            get_addmp += 15;
                            break;
                        case 5:// 智  智力+1
                            get_addint += 1;
                            break;
                        case 6:// 魅  魅力+1
                            get_addcha += 1;
                            break;
                        case 7:// 血  血+25
                            get_addhp += 25;
                            break;
                        case 8:// 魔  魔+25
                            get_addmp += 25;
                            break;
                        case 9:// 攻  額外攻擊+3
                            addDmgModifier += 3;
                            break;
                    }
                    switch (power.get_hole_3()) {
                        case 1:// 力  力+1
                            get_addstr += 1;
                            break;
                        case 2:// 敏  敏+1
                            get_adddex += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            get_addcon += 1;
                            get_addhp += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            get_addwis += 1;
                            get_addmp += 15;
                            break;
                        case 5:// 智  智力+1
                            get_addint += 1;
                            break;
                        case 6:// 魅  魅力+1
                            get_addcha += 1;
                            break;
                        case 7:// 血  血+25
                            get_addhp += 25;
                            break;
                        case 8:// 魔  魔+25
                            get_addmp += 25;
                            break;
                        case 9:// 攻  額外攻擊+3
                            addDmgModifier += 3;
                            break;
                    }
                    switch (power.get_hole_4()) {
                        case 1:// 力  力+1
                            get_addstr += 1;
                            break;
                        case 2:// 敏  敏+1
                            get_adddex += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            get_addcon += 1;
                            get_addhp += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            get_addwis += 1;
                            get_addmp += 15;
                            break;
                        case 5:// 智  智力+1
                            get_addint += 1;
                            break;
                        case 6:// 魅  魅力+1
                            get_addcha += 1;
                            break;
                        case 7:// 血  血+25
                            get_addhp += 25;
                            break;
                        case 8:// 魔  魔+25
                            get_addmp += 25;
                            break;
                        case 9:// 攻  額外攻擊+3
                            addDmgModifier += 3;
                            break;
                    }
                    switch (power.get_hole_5()) {
                        case 1:// 力  力+1
                            get_addstr += 1;
                            break;
                        case 2:// 敏  敏+1
                            get_adddex += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            get_addcon += 1;
                            get_addhp += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            get_addwis += 1;
                            get_addmp += 15;
                            break;
                        case 5:// 智  智力+1
                            get_addint += 1;
                            break;
                        case 6:// 魅  魅力+1
                            get_addcha += 1;
                            break;
                        case 7:// 血  血+25
                            get_addhp += 25;
                            break;
                        case 8:// 魔  魔+25
                            get_addmp += 25;
                            break;
                        case 9:// 攻  額外攻擊+3
                            addDmgModifier += 3;
                            break;
                    }
                }
                L1WilliamEnchantOrginal armorOrginal = L1WilliamEnchantOrginal.getL1WilliamEnchantOrginal(weapon);
                if (armorOrginal != null) {
                    if (armorOrginal.getAddStr() != 0) {
                        get_addstr += armorOrginal.getAddStr();
                    }
                    if (armorOrginal.getAddDex() != 0) {
                        get_adddex += armorOrginal.getAddDex();
                    }
                    if (armorOrginal.getAddCon() != 0) {
                        get_addcon += armorOrginal.getAddCon();
                    }
                    if (armorOrginal.getAddInt() != 0) {
                        get_addint += armorOrginal.getAddInt();
                    }
                    if (armorOrginal.getAddWis() != 0) {
                        get_addwis += armorOrginal.getAddWis();
                    }
                    if (armorOrginal.getAddCha() != 0) {
                        get_addcha += armorOrginal.getAddCha();
                    }
                    if (armorOrginal.getAddMaxHp() != 0) {
                        get_addhp += armorOrginal.getAddMaxHp();
                    }
                    if (armorOrginal.getAddMaxMp() != 0) {
                        get_addmp += armorOrginal.getAddMaxMp();
                    }
                    if (armorOrginal.getAddDmg() != 0) {
                        if (weapon.getItem().getType1() != 20
                                && weapon.getItem().getType1() != 62) {
                            addDmgModifier += armorOrginal.getAddDmg();
                        }
                    }
                    if (armorOrginal.getAddHit() != 0) {
                        if (weapon.getItem().getType1() != 20
                                && weapon.getItem().getType1() != 62) {
                            addHitModifier += armorOrginal.getAddHit();
                        }
                    }
                    if (armorOrginal.getAddBowDmg() != 0) {
                        if (weapon.getItem().getType1() == 20
                                || weapon.getItem().getType1() == 62) {
                            addDmgModifier += armorOrginal.getAddBowDmg();
                        }
                    }
                    if (armorOrginal.getAddBowHit() != 0) {
                        if (weapon.getItem().getType1() == 20
                                || weapon.getItem().getType1() == 62) {
                            addHitModifier += armorOrginal.getAddBowHit();
                        }
                    }
                    if (armorOrginal.getAddSp() != 0) {
                        if (weapon.getItem().getType1() == 40
                                || weapon.getItem().getType1() == 4) {
                            addWeaponSp += armorOrginal.getAddSp();
                        }
                    }
                }
                int i = 4;
                if (addHitModifier != 0) {
                    msg[i++] = "攻擊成功+ " + addHitModifier;
                }
                if (addDmgModifier != 0) {
                    msg[i++] = "額外攻擊點數+ " + addDmgModifier;
                }
                if (get_addstr != 0) {
                    msg[i++] = "力量+ " + get_addstr;
                }
                if (get_adddex != 0) {
                    msg[i++] = "敏捷+ " + get_adddex;
                }
                if (get_addcon != 0) {
                    msg[i++] = "體力+ " + get_addcon;
                }
                if (get_addwis != 0) {
                    msg[i++] = "精神+ " + get_addwis;
                }
                if (get_addint != 0) {
                    msg[i++] = "智力+ " + get_addint;
                }
                if (get_addcha != 0) {
                    msg[i++] = "魅力+ " + get_addcha;
                }
                if (get_addhp != 0) {
                    msg[i++] = "血量+ " + get_addhp;
                }
                if (get_addmp != 0) {
                    msg[i++] = "魔力+ " + get_addmp;
                }
                if (mr != 0) {
                    msg[i++] = "抗魔+ " + mr;
                }
                if (addWeaponSp != 0) {
                    msg[i++] = "魔攻+ " + addWeaponSp;
                }
                if (pw_d4_1 != 0) {
                    msg[i++] = "火屬性+ " + pw_d4_1;
                }
                if (pw_d4_2!= 0) {
                    msg[i++] = "水屬性+ " + pw_d4_2;
                }
                if (pw_d4_3 != 0) {
                    msg[i++] = "風屬性+ " + pw_d4_3;
                }
                if (pw_d4_4 != 0) {
                    msg[i++] = "地屬性+ " + pw_d4_4;
                }
                if (pw_k6_1 != 0) {
                    msg[i++] = "凍結耐性+ " + pw_k6_1;
                }
                if (pw_k6_2 != 0) {
                    msg[i++] = "石化耐性+ " + pw_k6_2;
                }
                if (pw_k6_3 != 0) {
                    msg[i++] = "睡眠耐性+ " + pw_k6_3;
                }
                if (pw_k6_4 != 0) {
                    msg[i++] = "暗闇耐性+ " + pw_k6_4;
                }
                if (pw_k6_5 != 0) {
                    msg[i++] = "昏迷耐性+ " + pw_k6_5;
                }
                if (pw_k6_6 != 0) {
                    msg[i++] = "支撐耐性+ " + pw_k6_6;
                }
                if (weapon.getCanAbilityType() > 1) {
                    if (CustomWeaponAbility.getInstance().canUseType(weapon.getItem().getUseType())) {
                        msg[i++] = "一 武器潛力屬性 一";
                        if (weapon.getCanAbilityType() >= 2) {
                            if (weapon.getAbilityPos1ID() > 0) {
                                msg[i++] = "1." + CustomWeaponAbility.getInstance().getData().get(weapon.getAbilityPos1ID()).getName();
                            } else {
                                msg[i++] = "1.未賦予";
                            }
                        }
                        if (weapon.getCanAbilityType() >= 3) {
                            if (weapon.getAbilityPos2ID() > 0) {
                                msg[i++] = "2." + CustomWeaponAbility.getInstance().getData().get(weapon.getAbilityPos2ID()).getName();
                            } else {
                                msg[i++] = "2.未賦予";
                            }
                        }
                        if (weapon.getCanAbilityType() >= 4) {
                            if (weapon.getAbilityPos3ID() > 0) {
                                msg[i++] = "3." + CustomWeaponAbility.getInstance().getData().get(weapon.getAbilityPos3ID()).getName();
                            } else {
                                msg[i++] = "3.未賦予";
                            }
                        }
                    }
                }
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayershow", msg));
                return true;
            } else {
                index -= 2;
                if (index < 0 || index >= this.armors.size()) {
                    return false;
                }
                final L1ItemInstance _itemInstance = this.armors.get(index);
                if (_itemInstance == null) {
                    return false;
                }
                final L1Item _item = _itemInstance.getItem();
                int add_str = 0;
                int add_con = 0;
                int add_dex = 0;
                int add_int = 0;
                int add_wis = 0;
                int add_cha = 0;
                int add_hp = _itemInstance.getAttachHp();
                int add_mp = _itemInstance.getAttachMp();
                int add_sp = _itemInstance.getAttachMagicDamage();
                int m_def = _itemInstance.getAttachMr();
                msg[0] = _itemInstance.getLogName();
                msg[1] = "強化數+ " + _itemInstance.getEnchantLevel();
                int ac = _item.get_ac();
                int pw_sDg = _item.getDmgModifierByArmor() + _itemInstance.getAttachOtherDamage();// DG(攻擊力)
                int pw_sHi = _item.getHitModifierByArmor() + _itemInstance.getAttachHit();// Hit(攻擊成功)
                L1WilliamEnchantOrginal1 armorOrginal = L1WilliamEnchantOrginal1.getL1WilliamEnchantOrginal(_itemInstance);
                if (armorOrginal != null) {
                    if (armorOrginal.getAddAc() != 0) { // 額外防禦
                        ac += armorOrginal.getAddAc();
                    }
                    if (armorOrginal.getAddStr() != 0) {
                        add_str += armorOrginal.getAddStr();
                    }
                    if (armorOrginal.getAddDex() != 0) {
                        add_dex += armorOrginal.getAddDex();
                    }
                    if (armorOrginal.getAddCon() != 0) {
                        add_con += armorOrginal.getAddCon();
                    }
                    if (armorOrginal.getAddInt() != 0) {
                        add_int += armorOrginal.getAddInt();
                    }
                    if (armorOrginal.getAddWis() != 0) {
                        add_wis += armorOrginal.getAddWis();
                    }
                    if (armorOrginal.getAddCha() != 0) {
                        add_cha += armorOrginal.getAddCha();
                    }
                    if (armorOrginal.getAddMaxHp() != 0) {
                        add_hp += armorOrginal.getAddMaxHp();
                    }
                    if (armorOrginal.getAddMaxMp() != 0) {
                        add_mp += armorOrginal.getAddMaxMp();
                    }
                    if (armorOrginal.getAddDmg() != 0) {
                        if (_itemInstance.getItem().getType1() != 20
                                && _itemInstance.getItem().getType1() != 62) {
                            pw_sDg += armorOrginal.getAddDmg();
                        }
                    }
                    if (armorOrginal.getAddHit() != 0) {
                        if (_itemInstance.getItem().getType1() != 20
                                && _itemInstance.getItem().getType1() != 62) {
                            pw_sHi += armorOrginal.getAddHit();
                        }
                    }
                    if (armorOrginal.getAddBowDmg() != 0) {
                        if (_itemInstance.getItem().getType1() == 20
                                || _itemInstance.getItem().getType1() == 62) {
                            pw_sDg += armorOrginal.getAddBowDmg();
                        }
                    }
                    if (armorOrginal.getAddBowHit() != 0) {
                        if (_itemInstance.getItem().getType1() == 20
                                || _itemInstance.getItem().getType1() == 62) {
                            pw_sHi += armorOrginal.getAddBowHit();
                        }
                    }
                    if (armorOrginal.getAddMr() != 0) {
                        m_def += armorOrginal.getAddMr();
                    }
                    if (armorOrginal.getAddSp() != 0) {
                        add_sp += armorOrginal.getAddSp();
                    }
                }
                if (ac < 0) {
                    ac = Math.abs(ac);
                }
                int i = 2;
                msg[i++] = "防禦力: " + ac + "+" + _itemInstance.getEnchantLevel();

                int pw_s1 = _item.get_addstr() + add_str;// 力量
                int pw_s2 = _item.get_adddex() + add_dex;// 敏捷
                int pw_s3 = _item.get_addcon() + add_con;// 體質
                int pw_s4 = _item.get_addwis() + add_wis;// 精神
                int pw_s5 = _item.get_addint() + add_int;// 智力
                int pw_s6 = _item.get_addcha() + add_cha;// 魅力

                int pw_sHp = _item.get_addhp() + add_hp;// +HP
                int pw_sMp = _item.get_addmp() + add_mp;// +MP
                int pw_sMr = m_def;// MR(抗魔)
                int pw_sSp = _item.get_addsp() + add_sp;// SP(魔攻)

                int pw_d4_1 = _item.get_defense_fire();// 火屬性
                int pw_d4_2 = _item.get_defense_water();// 水屬性
                int pw_d4_3 = _item.get_defense_wind();// 風屬性
                int pw_d4_4 = _item.get_defense_earth();// 地屬性

                // 攻擊成功
                if (pw_sHi != 0) {
                    msg[i++] = "攻擊成功+ " + pw_sHi;
                }

                // 追加打擊
                if (pw_sDg != 0) {
                    msg[i++] = "額外攻擊點數+ " + pw_sDg;
                }

                // 特別定義套裝
                int s6_1 = 0;// 力量
                int s6_2 = 0;// 敏捷
                int s6_3 = 0;// 體質
                int s6_4 = 0;// 精神
                int s6_5 = 0;// 智力
                int s6_6 = 0;// 魅力
                int aH_1 = 0;// +HP
                int aM_1 = 0;// +MP
                int aMR_1 = 0;// MR(抗魔)
                int aSP_1 = 0;// SP(魔攻)
                int aSS_1 = 0;// 加速效果
                int d4_1 = 0;// 火屬性
                int d4_2 = 0;// 水屬性
                int d4_3 = 0;// 風屬性
                int d4_4 = 0;// 地屬性
                if (_itemInstance.isMatch()) {// 完成套裝
                    s6_1 = _item.get_mode()[0];// 力量
                    s6_2 = _item.get_mode()[1];// 敏捷
                    s6_3 = _item.get_mode()[2];// 體質
                    s6_4 = _item.get_mode()[3];// 精神
                    s6_5 = _item.get_mode()[4];// 智力
                    s6_6 = _item.get_mode()[5];// 魅力
                    aH_1 = _item.get_mode()[6];// +HP
                    aM_1 = _item.get_mode()[7];// +MP
                    aMR_1 = _item.get_mode()[8];// MR(抗魔)
                    aSP_1 = _item.get_mode()[9];// SP(魔攻)
                    aSS_1 = _item.get_mode()[10];// 加速效果
                    d4_1 = _item.get_mode()[11];// 火屬性
                    d4_2 = _item.get_mode()[12];// 水屬性
                    d4_3 = _item.get_mode()[13];// 風屬性
                    d4_4 = _item.get_mode()[14];// 地屬性
                }

                if (_itemInstance.get_power_name() != null) {
                    final L1ItemPower_name power = _itemInstance.get_power_name();
                    switch (power.get_hole_1()) {
                        case 1:// 力  力+1
                            s6_1 += 1;
                            break;
                        case 2:// 敏  敏+1
                            s6_2 += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            s6_3 += 1;
                            aH_1 += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            s6_4 += 1;
                            aM_1 += 15;
                            break;
                        case 5:// 智  智力+1
                            s6_5 += 1;
                            break;
                        case 6:// 魅  魅力+1
                            s6_6 += 1;
                            break;
                        case 7:// 血  血+25
                            aH_1 += 25;
                            break;
                        case 8:// 魔  魔+25
                            aM_1 += 25;
                            break;
                        case 9:// 攻
                            break;
                        case 10:// 防  防禦-2
                            break;
                        case 11:// 抗  抗魔+3
                            aMR_1 += 3;
                            break;
                    }
                    switch (power.get_hole_2()) {
                        case 1:// 力  力+1
                            s6_1 += 1;
                            break;
                        case 2:// 敏  敏+1
                            s6_2 += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            s6_3 += 1;
                            aH_1 += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            s6_4 += 1;
                            aM_1 += 15;
                            break;
                        case 5:// 智  智力+1
                            s6_5 += 1;
                            break;
                        case 6:// 魅  魅力+1
                            s6_6 += 1;
                            break;
                        case 7:// 血  血+25
                            aH_1 += 25;
                            break;
                        case 8:// 魔  魔+25
                            aM_1 += 25;
                            break;
                        case 9:// 攻
                            break;
                        case 10:// 防  防禦-2
                            break;
                        case 11:// 抗  抗魔+3
                            aMR_1 += 3;
                            break;
                    }
                    switch (power.get_hole_3()) {
                        case 1:// 力  力+1
                            s6_1 += 1;
                            break;
                        case 2:// 敏  敏+1
                            s6_2 += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            s6_3 += 1;
                            aH_1 += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            s6_4 += 1;
                            aM_1 += 15;
                            break;
                        case 5:// 智  智力+1
                            s6_5 += 1;
                            break;
                        case 6:// 魅  魅力+1
                            s6_6 += 1;
                            break;
                        case 7:// 血  血+25
                            aH_1 += 25;
                            break;
                        case 8:// 魔  魔+25
                            aM_1 += 25;
                            break;
                        case 9:// 攻
                            break;
                        case 10:// 防  防禦-2
                            break;
                        case 11:// 抗  抗魔+3
                            aMR_1 += 3;
                            break;
                    }
                    switch (power.get_hole_4()) {
                        case 1:// 力  力+1
                            s6_1 += 1;
                            break;
                        case 2:// 敏  敏+1
                            s6_2 += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            s6_3 += 1;
                            aH_1 += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            s6_4 += 1;
                            aM_1 += 15;
                            break;
                        case 5:// 智  智力+1
                            s6_5 += 1;
                            break;
                        case 6:// 魅  魅力+1
                            s6_6 += 1;
                            break;
                        case 7:// 血  血+25
                            aH_1 += 25;
                            break;
                        case 8:// 魔  魔+25
                            aM_1 += 25;
                            break;
                        case 9:// 攻
                            break;
                        case 10:// 防  防禦-2
                            break;
                        case 11:// 抗  抗魔+3
                            aMR_1 += 3;
                            break;
                    }
                    switch (power.get_hole_5()) {
                        case 1:// 力  力+1
                            s6_1 += 1;
                            break;
                        case 2:// 敏  敏+1
                            s6_2 += 1;
                            break;
                        case 3:// 體  體+1 血+15
                            s6_3 += 1;
                            aH_1 += 15;
                            break;
                        case 4:// 精  精+1 魔+15
                            s6_4 += 1;
                            aM_1 += 15;
                            break;
                        case 5:// 智  智力+1
                            s6_5 += 1;
                            break;
                        case 6:// 魅  魅力+1
                            s6_6 += 1;
                            break;
                        case 7:// 血  血+25
                            aH_1 += 25;
                            break;
                        case 8:// 魔  魔+25
                            aM_1 += 25;
                            break;
                        case 9:// 攻
                            break;
                        case 10:// 防  防禦-2
                            break;
                        case 11:// 抗  抗魔+3
                            aMR_1 += 3;
                            break;
                    }
                }
                // 力量
                final int addstr =
                        pw_s1 + s6_1;
                if (addstr != 0) {
                    msg[i++] = "力量+ " + addstr;
                }
                // 敏捷
                final int adddex =
                        pw_s2 + s6_2;
                if (adddex != 0) {
                    msg[i++] = "敏捷+ " + adddex;
                }
                // 體質
                final int addcon =
                        pw_s3 + s6_3;
                if (addcon != 0) {
                    msg[i++] = "體力+ " + addcon;
                }
                // 精神
                final int addwis =
                        pw_s4 + s6_4;
                if (addwis != 0) {
                    msg[i++] = "精神+ " + addwis;
                }
                // 智力
                final int addint =
                        pw_s5 + s6_5;
                if (addint != 0) {
                    msg[i++] = "智力+ " + addint;
                }
                // 魅力
                final int addcha =
                        pw_s6 + s6_6;
                if (addcha != 0) {
                    msg[i++] = "魅力+ " + addcha;
                }
                // +HP
                final int addhp =
                        pw_sHp + aH_1;
                if (addhp != 0) {
                    msg[i++] = "血量+ " + addhp;
                }
//		// +MP
                final int addmp =
                        pw_sMp + aM_1;
                if (addmp != 0) {
                    msg[i++] = "魔力+ " + addmp;
                }
                // MR(抗魔)
                final int addmr =
                        pw_sMr + aMR_1;
                if (addmr != 0) {
                    msg[i++] = "抗魔+ " + addmr;
                }
                // SP(魔攻)
                final int addsp = pw_sSp + aSP_1;
                if (addsp != 0) {
                    msg[i++] = "魔攻+ " + addsp;
                }
                // 增加火屬性
                final int fire =
                        pw_d4_1 + d4_1;
                if (fire != 0) {
                    msg[i++] = "火屬性+ " + fire;
                }
                // 增加水屬性
                final int water =
                        pw_d4_2 + d4_2;
                if (water != 0) {
                    msg[i++] = "水屬性+ " + water;
                }
                // 增加風屬性
                final int wind =
                        pw_d4_3 + d4_3;
                if (wind != 0) {
                    msg[i++] = "風屬性+ " + wind;
                }
                // 增加地屬性
                final int earth =
                        pw_d4_4 + d4_4;
                if (earth != 0) {
                    msg[i++] = "地屬性+ " + earth;
                }
                if (_itemInstance.getCanAbilityType() > 1) {
                    if (CustomArmorAbility.getInstance().canUseType(_itemInstance.getItem().getUseType())) {
                        msg[i++] = "一 防具潛力屬性 一";
                        if (_itemInstance.getCanAbilityType() >= 2) {
                            if (_itemInstance.getAbilityPos1ID() > 0) {
                                msg[i++] = "1." + CustomArmorAbility.getInstance().getData().get(_itemInstance.getAbilityPos1ID()).getName();
                            } else {
                                msg[i++] = "1.未賦予";
                            }
                        }
                        if (_itemInstance.getCanAbilityType() >= 3) {
                            if (_itemInstance.getAbilityPos2ID() > 0) {
                                msg[i++] = "2." + CustomArmorAbility.getInstance().getData().get(_itemInstance.getAbilityPos2ID()).getName();
                            } else {
                                msg[i++] = "2.未賦予";
                            }
                        }
                        if (_itemInstance.getCanAbilityType() >= 4) {
                            if (_itemInstance.getAbilityPos3ID() > 0) {
                                msg[i++] = "3." + CustomArmorAbility.getInstance().getData().get(_itemInstance.getAbilityPos3ID()).getName();
                            } else {
                                msg[i++] = "3.未賦予";
                            }
                        }
                    }
                }
                pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ajplayershow", msg));
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
}
