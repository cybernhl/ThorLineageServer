package com.lineage.server.model.Instance;


import com.custom.ability.AbilityData;
import com.custom.bless.BlessStatFactory;
import com.lineage.server.datatables.CardUseTalble;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.utils.BinaryOutputStream;
import com.william.CardUse;
import com.william.L1WilliamEnchantOrginal;
import com.william.L1WilliamEnchantOrginal1;

/**
 * 物品詳細資料
 *
 * @author dexc
 */
public class L1ItemStatus {

    private final L1ItemInstance _itemInstance;

    private final L1Item _item;

    private final BinaryOutputStream _os;

    private final L1ItemPower _itemPower;

    /**
     * 物品詳細資料
     *
     * @param itemInstance L1ItemInstance
     */
    public L1ItemStatus(final L1ItemInstance itemInstance) {
        this._itemInstance = itemInstance;
        this._item = itemInstance.getItem();
        this._os = new BinaryOutputStream();
        this._itemPower = new L1ItemPower(this._itemInstance);
    }

    /**
     * 物品詳細資料
     *
     * @param template L1Item
     */
    public L1ItemStatus(final L1Item template) {
        this._itemInstance = new L1ItemInstance();
        this._itemInstance.setItem(template);
        this._item = template;
        this._os = new BinaryOutputStream();
        this._itemPower = new L1ItemPower(this._itemInstance);
    }

    public BinaryOutputStream getStatusBytes() {
        // 分類
        final int use_type = this._item.getUseType();
        switch (use_type) {
            case -11: // 對讀取方法調用無法分類的物品
            case -10: // 加速藥水
            case -9: // 技術書
            case -8: // 料理書
            case -7: // 增HP道具
            case -6: // 增MP道具
            case -5: // 食人妖精競賽票
            case -4: // 項圈
            case -1: // 無法使用(材料等)
            case 0: // 一般物品
            case 3: // 創造怪物魔杖(無須選取目標 - 無數量:沒有任何事情發生)
            case 5: // 魔杖類型(須選取目標)
            case 6: // 瞬間移動卷軸
            case 7: // 鑒定卷軸
            case 9: // 傳送回家的卷軸
            case 8: // 復活卷軸
            case 12: // 信紙
            case 13: // 信紙(寄出)
            case 14: // 請選擇一個物品(道具欄位)
            case 15: // 哨子
            case 16: // 變形卷軸
            case 17: // 選取目標 (近距離)
            case 26: // 對武器施法的卷軸
            case 27: // 對盔甲施法的卷軸
            case 28: // 空的魔法卷軸
            case 29: // 瞬間移動卷軸(祝福)
            case 30: // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
            case 31: // 聖誕卡片
            case 32: // 聖誕卡片(寄出)
            case 33: // 情人節卡片
            case 34: // 情人節卡片(寄出)
            case 35: // 白色情人節卡片
            case 36: // 白色情人節卡片(寄出)
            case 39: // 選取目標 (遠距離)
            case 42: // 釣魚桿
            case 46: // 飾品強化卷軸
            case 55: // 請選擇魔法娃娃
                return this.etcitem();

            case -12: // 寵物用具
                final L1PetItem petItem = PetItemTable.get().getTemplate(this._item.getItemId());
                // 武器
                if (petItem.isWeapom()) {
                    return this.petweapon(petItem);
                    // 防具
                } else {
                    return this.petarmor(petItem);
                }

            case -3: // 飛刀
            case -2: // 箭
                return this.arrow();

            case 38: // 食物
                return this.fooditem();

            case 10: // 照明道具
                return this.lightitem();

            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
                return this.armor();

            case 40: // 耳環
            case 23: // 戒指
            case 24: // 項鏈
            case 37: // 腰帶
                return this.accessories();

            case 43: // 副助道具右
            case 44: // 副助道具左
            case 45: // 副助道具中
            case 48: // 副助道具右下
            case 47: // 副助道具左下
            case 0x7FFF:
            case 0x8000:
            case 0x8001:
            case 0x8002:
            case 0x8003:
            case 0x8004:
            case 0x8005:
            case 0x8006:
            case 0x8007:
            case 0x8008:
                return this.accessories2();
            case 1: // 武器
                return this.weapon();
        }
        return null;
    }

    /**
     * 飛刀
     * 箭
     *
     * @return
     */
    private BinaryOutputStream arrow() {
        this._os.writeC(0x01); // 打擊值
        this._os.writeC(this._item.getDmgSmall());
        this._os.writeC(this._item.getDmgLarge());
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        return this._os;
    }

    /**
     * 食物
     *
     * @return
     */
    private BinaryOutputStream fooditem() {
        this._os.writeC(0x15);
        // 榮養
        this._os.writeH(this._item.getFoodVolume());
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        return this._os;
    }

    /**
     * 照明道具
     *
     * @return
     */
    private BinaryOutputStream lightitem() {
        this._os.writeC(0x16);
        this._os.writeH(this._item.getLightRange());
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        return this._os;
    }

    /**
     * 防具類
     *
     * @return
     */
    private BinaryOutputStream armor() {
        int add_str = 0;
        int add_con = 0;
        int add_dex = 0;
        int add_int = 0;
        int add_wis = 0;
        int add_cha = 0;
        int add_hp = this._itemInstance.getAttachHp();
        int add_mp = this._itemInstance.getAttachMp();
        int add_sp = this._itemInstance.getAttachMagicDamage();
        int m_def = this._itemInstance.getAttachMr();
        int ac = this._item.get_ac();
        int dmgR = 0, hit = 0, bow_hit = 0, dmg = 0, bow_dmg = 0, hpr = 0, mpr = 0, pvp_dmg = 0;
        final BlessStatFactory.BlessStat stat = BlessStatFactory.getInstance().getData(this._itemInstance);
        if (stat != null) {
            ac += stat.getAc();
            add_str += stat.getStr();
            add_dex += stat.getDex();
            add_int += stat.get_int();
            add_wis += stat.getWis();
            add_cha += stat.getCha();
            add_con += stat.get_con();
            add_hp += stat.getHp();
            add_mp += stat.getMp();
            add_sp += stat.getSp();
            m_def += stat.getMr();
            dmgR += stat.getDmgR();
            hit += stat.getHit();
            bow_hit += stat.getBow_hit();
            dmg += stat.getDmgu();
            bow_dmg += stat.getBow_dmgup();
            hpr += stat.getHpR();
            mpr += stat.getMpR();
            pvp_dmg += stat.getPvp_dmg();
        }


        // AC
        this._os.writeC(0x13);
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        this._os.writeC(ac);

        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        // 強化數
        if (this._itemInstance.getEnchantLevel() != 0) {
            this._os.writeC(0x02);
            this._os.writeC(this._itemInstance.getEnchantLevel());
        }
        // 損傷度
        if (this._itemInstance.get_durability() != 0) {
            this._os.writeC(0x03);
            this._os.writeC(this._itemInstance.get_durability());
        }

        int pw_s1 = this._item.get_addstr() + add_str;// 力量
        int pw_s2 = this._item.get_adddex() + add_dex;// 敏捷
        int pw_s3 = this._item.get_addcon() + add_con;// 體質
        int pw_s4 = this._item.get_addwis() + add_wis;// 精神
        int pw_s5 = this._item.get_addint() + add_int;// 智力
        int pw_s6 = this._item.get_addcha() + add_cha;// 魅力

        int pw_sHp = this._item.get_addhp() + add_hp;// +HP
        int pw_sMp = this._item.get_addmp() + add_mp;// +MP
        int pw_sMr = this._itemPower.getMr() + m_def;// MR(抗魔)
        int pw_sSp = this._item.get_addsp() + add_sp;// SP(魔攻)

        int pw_sDg = this._item.getDmgModifierByArmor() + this._itemInstance.getAttachOtherDamage();// DG(攻擊力)
        int pw_sHi = this._item.getHitModifierByArmor() + this._itemInstance.getAttachHit();// Hit(攻擊成功)

        int pw_d4_1 = this._item.get_defense_fire();// 火屬性
        int pw_d4_2 = this._item.get_defense_water();// 水屬性
        int pw_d4_3 = this._item.get_defense_wind();// 風屬性
        int pw_d4_4 = this._item.get_defense_earth();// 地屬性

//		int pw_k6_1 = this._item.get_regist_freeze();// 寒冰耐性
//		int pw_k6_2 = this._item.get_regist_stone();// 石化耐性
//		int pw_k6_3 = this._item.get_regist_sleep();// 睡眠耐性
//		int pw_k6_4 = this._item.get_regist_blind();// 暗黑耐性
//		int pw_k6_5 = this._item.get_regist_stun();// 昏迷耐性
        int pw_k6_6 = this._item.get_regist_sustain();// 支撐耐性

        // 攻擊成功
        if (pw_sHi != 0) {
            this._os.writeC(0x05);
            this._os.writeC(pw_sHi);
        }

        // 追加打擊
        if (pw_sDg != 0) {
            this._os.writeC(0x06);
            this._os.writeC(pw_sDg);
        }

        // 使用可能
        int bit = 0;
        bit |= this._item.isUseRoyal() ? 1 : 0;
        bit |= this._item.isUseKnight() ? 2 : 0;
        bit |= this._item.isUseElf() ? 4 : 0;
        bit |= this._item.isUseMage() ? 8 : 0;
//		bit |= this._item.isUseDarkelf() ? 16 : 0;
        this._os.writeC(0x07);
        this._os.writeC(bit);

//		// 弓命中追加
//		if (this._item.getBowHitModifierByArmor() != 0) {
//			this._os.writeC(0x18);
//			this._os.writeC(this._item.getBowHitModifierByArmor());
//		}
//
//		// 弓傷害追加
//		if (this._item.getBowDmgModifierByArmor() != 0) {
//			this._os.writeC(0x23);
//			this._os.writeC(this._item.getBowDmgModifierByArmor());
//		}

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
//		int k6_1 = 0;// 寒冰耐性
//		int k6_2 = 0;// 石化耐性
//		int k6_3 = 0;// 睡眠耐性
//		int k6_4 = 0;// 暗黑耐性
//		int k6_5 = 0;// 昏迷耐性
//		int k6_6 = 0;// 支撐耐性
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
//			k6_1 = _item.get_mode()[15];// 寒冰耐性
//			k6_2 = _item.get_mode()[16];// 石化耐性
//			k6_3 = _item.get_mode()[17];// 睡眠耐性
//			k6_4 = _item.get_mode()[18];// 暗黑耐性
//			k6_5 = _item.get_mode()[19];// 昏迷耐性
//			k6_6 = _item.get_mode()[20];// 支撐耐性
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
        dmgR += this._item.getDamageReduction();
        hit += this._item.getHitModifierByArmor();
        bow_hit += this._item.getBowHitModifierByArmor();
        dmg += this._item.getDmgModifierByArmor();
        bow_dmg += this._item.getBowDmgModifierByArmor();
        hpr += this._item.get_addhpr();
        mpr += this._item.get_addmpr();
        if (dmgR > 0) {
            this._os.writeC(0x27);
            this._os.writeS("傷害減免+" + dmgR + ".");
        }
        if (hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離命中+" + hit + ".");
        }
        if (bow_hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離命中+" + bow_hit + ".");
        }
        if (dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離傷害+" + dmg + ".");
        }
        if (bow_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離傷害+" + bow_dmg + ".");
        }
        if (hpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回血+" + hpr + ".");
        }
        if (mpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回魔+" + mpr + ".");
        }
        if (pvp_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("PVP傷害+" + pvp_dmg + ".");
        }
        // 力量
        final int addstr =
                pw_s1 + s6_1;
        if (addstr != 0) {
            this._os.writeC(0x08);
            this._os.writeC(addstr);
        }
        // 敏捷
        final int adddex =
                pw_s2 + s6_2;
        if (adddex != 0) {
            this._os.writeC(0x09);
            this._os.writeC(adddex);
        }
        // 體質
        final int addcon =
                pw_s3 + s6_3;
        if (addcon != 0) {
            this._os.writeC(0x0a);
            this._os.writeC(addcon);
        }
        // 精神
        final int addwis =
                pw_s4 + s6_4;
        if (addwis != 0) {
            this._os.writeC(0x0b);
            this._os.writeC(addwis);
        }
        // 智力
        final int addint =
                pw_s5 + s6_5;
        if (addint != 0) {
            this._os.writeC(0x0c);
            this._os.writeC(addint);
        }
        // 魅力
        final int addcha =
                pw_s6 + s6_6;
        if (addcha != 0) {
            this._os.writeC(0x0d);
            this._os.writeC(addcha);
        }
        // +HP
        final int addhp =
                pw_sHp + aH_1;
        if (addhp != 0) {
            this._os.writeC(0x0e);
            this._os.writeH(addhp);
        }
//		// +MP
        final int addmp =
                pw_sMp + aM_1;
        if (addmp != 0) {
            this._os.writeC(0x20);
            this._os.writeH(addmp);
        }
        // MR(抗魔)
        final int addmr =
                pw_sMr + aMR_1;
        if (addmr != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(addmr);
        }
        // SP(魔攻)
        final int addsp = pw_sSp + aSP_1;
        if (addsp != 0) {
            this._os.writeC(0x11);
            this._os.writeC(addsp);
        }
        // 具備加速效果
        boolean haste = this._item.isHasteItem();

        if (aSS_1 == 1) {
            haste = true;
        }
        if (haste) {
            this._os.writeC(0x12);
        }
        // 增加火屬性
        final int fire =
                pw_d4_1 + d4_1;
        if (fire != 0) {
            this._os.writeC(0x1b);
            this._os.writeC(fire);
        }
        // 增加水屬性
        final int water =
                pw_d4_2 + d4_2;
        if (water != 0) {
            this._os.writeC(0x1c);
            this._os.writeC(water);
        }
        // 增加風屬性
        final int wind =
                pw_d4_3 + d4_3;
        if (wind != 0) {
            this._os.writeC(0x1d);
            this._os.writeC(wind);
        }
        // 增加地屬性
        final int earth =
                pw_d4_4 + d4_4;
        if (earth != 0) {
            this._os.writeC(0x1e);
            this._os.writeC(earth);
        }
        AbilityData.itemDesc(_itemInstance, _os);
        L1WilliamEnchantOrginal1.itemDesc(_itemInstance, _os);

//		boolean isOut = false;
//		// 寒冰耐性
//		final int freeze = 
//			pw_k6_1 + k6_1;
//		//System.out.println("寒冰耐性:"+freeze);
//		if (freeze != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(freeze);
//			this._os.writeC(0x21);
//			this._os.writeC(0x01);
//		}
//		// 石化耐性
//		final int stone = 
//			pw_k6_2 + k6_2;
//		//System.out.println("石化耐性:"+stone);
//		if (stone != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stone);
//			this._os.writeC(0x21);
//			this._os.writeC(0x02);
//		}
//		// 睡眠耐性
//		final int sleep = 
//			pw_k6_3 + k6_3;
//		//System.out.println("睡眠耐性:"+sleep);
//		if (sleep != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sleep);
//			this._os.writeC(0x21);
//			this._os.writeC(0x03);
//		}
//		// 暗黑耐性
//		final int blind = 
//			pw_k6_4 + k6_4;
//		//System.out.println("暗黑耐性:"+blind);
//		if (blind != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(blind);
//			this._os.writeC(0x21);
//			this._os.writeC(0x04);
//		}
//		// 昏迷耐性
//		final int stun = 
//			pw_k6_5 + k6_5;
//		//System.out.println("昏迷耐性:"+stun);
//		if (stun != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stun);
//			this._os.writeC(0x21);
//			this._os.writeC(0x05);
//		}
        // 支撐耐性
//		final int sustain = 
//			pw_k6_6 + k6_6;
//		//System.out.println("支撐耐性:"+sustain);
//		if (sustain != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sustain);
//			this._os.writeC(0x21);
//			this._os.writeC(0x06);
//		}
        return this._os;
    }

    /**
     * 飾品類
     *
     * @return
     */
    private BinaryOutputStream accessories() {
        int add_str = 0;
        int add_con = 0;
        int add_dex = 0;
        int add_int = 0;
        int add_wis = 0;
        int add_cha = 0;
        int add_hp = 0;
        int add_mp = 0;
        int add_sp = 0;
        int m_def = 0;
        int ac = this._item.get_ac();
        int dmgR = 0, hit = 0, bow_hit = 0, dmg = 0, bow_dmg = 0, hpr = 0, mpr = 0, pvp_dmg = 0;
        final BlessStatFactory.BlessStat stat = BlessStatFactory.getInstance().getData(this._itemInstance);
        if (stat != null) {
            ac += stat.getAc();
            add_str += stat.getStr();
            add_dex += stat.getDex();
            add_int += stat.get_int();
            add_wis += stat.getWis();
            add_cha += stat.getCha();
            add_con += stat.get_con();
            add_hp += stat.getHp();
            add_mp += stat.getMp();
            add_sp += stat.getSp();
            m_def += stat.getMr();
            dmgR += stat.getDmgR();
            hit += stat.getHit();
            bow_hit += stat.getBow_hit();
            dmg += stat.getDmgu();
            bow_dmg += stat.getBow_dmgup();
            hpr += stat.getHpR();
            mpr += stat.getMpR();
            pvp_dmg += stat.getPvp_dmg();
        }
        // AC
        this._os.writeC(0x13);
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        this._os.writeC(ac);

        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        int pw_s1 = this._item.get_addstr() + add_str;// 力量
        int pw_s2 = this._item.get_adddex() + add_dex;// 敏捷
        int pw_s3 = this._item.get_addcon() + add_con;// 體質
        int pw_s4 = this._item.get_addwis() + add_wis;// 精神
        int pw_s5 = this._item.get_addint() + add_int;// 智力
        int pw_s6 = this._item.get_addcha() + add_cha;// 魅力

        int pw_sHp = this._item.get_addhp() + add_hp + this._itemInstance.getAttachHp();// +HP
        int pw_sMp = this._item.get_addmp() + add_mp + this._itemInstance.getAttachMp();// +MP
        int pw_sMr = this._itemPower.getMr() + m_def + this._itemInstance.getAttachMr();// MR(抗魔)
        int pw_sSp = this._item.get_addsp() + add_sp + this._itemInstance.getAttachMagicDamage();// SP(魔攻)

        int pw_sDg = this._item.getDmgModifierByArmor() + this._itemInstance.getAttachOtherDamage();// DG(攻擊力)
        int pw_sHi = this._item.getHitModifierByArmor() + this._itemInstance.getAttachHit();// Hit(攻擊成功)

        int pw_d4_1 = this._item.get_defense_fire();// 火屬性
        int pw_d4_2 = this._item.get_defense_water();// 水屬性
        int pw_d4_3 = this._item.get_defense_wind();// 風屬性
        int pw_d4_4 = this._item.get_defense_earth();// 地屬性

        int pw_k6_1 = this._item.get_regist_freeze();// 寒冰耐性
        int pw_k6_2 = this._item.get_regist_stone();// 石化耐性
        int pw_k6_3 = this._item.get_regist_sleep();// 睡眠耐性
        int pw_k6_4 = this._item.get_regist_blind();// 暗黑耐性
        int pw_k6_5 = this._item.get_regist_stun();// 昏迷耐性
        int pw_k6_6 = this._item.get_regist_sustain();// 支撐耐性

        // 攻擊成功
        if (pw_sHi != 0) {
            this._os.writeC(0x05);
            this._os.writeC(pw_sHi);
        }

        // 追加打擊
        if (pw_sDg != 0) {
            this._os.writeC(0x06);
            this._os.writeC(pw_sDg);
        }

        // 使用可能
        int bit = 0;
        bit |= this._item.isUseRoyal() ? 1 : 0;
        bit |= this._item.isUseKnight() ? 2 : 0;
        bit |= this._item.isUseElf() ? 4 : 0;
        bit |= this._item.isUseMage() ? 8 : 0;
        bit |= this._item.isUseDarkelf() ? 16 : 0;
        this._os.writeC(0x07);
        this._os.writeC(bit);

//		// 弓命中追加
//		if (this._item.getBowHitModifierByArmor() != 0) {
//			this._os.writeC(0x18);
//			this._os.writeC(this._item.getBowHitModifierByArmor());
//		}
//		// 弓傷害追加
//		if (this._item.getBowDmgModifierByArmor() != 0) {
//			this._os.writeC(0x23);
//			this._os.writeC(this._item.getBowDmgModifierByArmor());
//		}

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
        int k6_1 = 0;// 寒冰耐性
        int k6_2 = 0;// 石化耐性
        int k6_3 = 0;// 睡眠耐性
        int k6_4 = 0;// 暗黑耐性
        int k6_5 = 0;// 昏迷耐性
        int k6_6 = 0;// 支撐耐性

        if (this._itemInstance.isMatch()) {// 完成套裝
            s6_1 = this._item.get_mode()[0];// 力量
            s6_2 = this._item.get_mode()[1];// 敏捷
            s6_3 = this._item.get_mode()[2];// 體質
            s6_4 = this._item.get_mode()[3];// 精神
            s6_5 = this._item.get_mode()[4];// 智力
            s6_6 = this._item.get_mode()[5];// 魅力
            aH_1 = this._item.get_mode()[6];// +HP
            aM_1 = this._item.get_mode()[7];// +MP
            aMR_1 = this._item.get_mode()[8];// MR(抗魔)
            aSP_1 = this._item.get_mode()[9];// SP(魔攻)
            aSS_1 = this._item.get_mode()[10];// 加速效果
            d4_1 = this._item.get_mode()[11];// 火屬性
            d4_2 = this._item.get_mode()[12];// 水屬性
            d4_3 = this._item.get_mode()[13];// 風屬性
            d4_4 = this._item.get_mode()[14];// 地屬性
            k6_1 = this._item.get_mode()[15];// 寒冰耐性
            k6_2 = this._item.get_mode()[16];// 石化耐性
            k6_3 = this._item.get_mode()[17];// 睡眠耐性
            k6_4 = this._item.get_mode()[18];// 暗黑耐性
            k6_5 = this._item.get_mode()[19];// 昏迷耐性
            k6_6 = this._item.get_mode()[20];// 支撐耐性
        }
        dmgR += this._item.getDamageReduction();
        hit += this._item.getHitModifierByArmor();
        bow_hit += this._item.getBowHitModifierByArmor();
        dmg += this._item.getDmgModifierByArmor();
        bow_dmg += this._item.getBowDmgModifierByArmor();
        hpr += this._item.get_addhpr();
        mpr += this._item.get_addmpr();
        if (dmgR > 0) {
            this._os.writeC(0x27);
            this._os.writeS("傷害減免+" + dmgR + ".");
        }
        if (hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離命中+" + hit + ".");
        }
        if (bow_hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離命中+" + bow_hit + ".");
        }
        if (dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離傷害+" + dmg + ".");
        }
        if (bow_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離傷害+" + bow_dmg + ".");
        }
        if (hpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回血+" + hpr + ".");
        }
        if (mpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回魔+" + mpr + ".");
        }
        if (pvp_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("PVP傷害+" + pvp_dmg + ".");
        }
        // 力量
        final int addstr = pw_s1 + s6_1;
        if (addstr != 0) {
            this._os.writeC(0x08);
            this._os.writeC(addstr);
        }
        // 敏捷
        final int adddex = pw_s2 + s6_2;
        if (adddex != 0) {
            this._os.writeC(0x09);
            this._os.writeC(adddex);
        }
        // 體質
        final int addcon = pw_s3 + s6_3;
        if (addcon != 0) {
            this._os.writeC(0x0a);
            this._os.writeC(addcon);
        }
        // 精神
        final int addwis = pw_s4 + s6_4;
        if (addwis != 0) {
            this._os.writeC(0x0b);
            this._os.writeC(addwis);
        }
        // 智力
        final int addint = pw_s5 + s6_5;
        if (addint != 0) {
            this._os.writeC(0x0c);
            this._os.writeC(addint);
        }
        // 魅力
        final int addcha = pw_s6 + s6_6;
        if (addcha != 0) {
            this._os.writeC(0x0d);
            this._os.writeC(addcha);
        }

        // +HP MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addhp = pw_sHp + aH_1;
        if (addhp != 0) {
            this._os.writeC(0x0e);
            this._os.writeH(addhp);
        }

//		// +MP MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addmp = pw_sMp + aM_1;
        if (addmp != 0) {
            this._os.writeC(0x20);
            this._os.writeH(addmp);
        }

        // MR(抗魔) MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addmr = pw_sMr + aMR_1;
        if (addmr != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(addmr);
        }
        // SP(魔攻)火 水 風 地 HP MP MR SP HPR MPR
        final int addsp = pw_sSp + aSP_1;
        if (addsp != 0) {
            this._os.writeC(0x11);
            this._os.writeC(addsp);
        }

//		// 具備加速效果
//		boolean haste = this._item.isHasteItem();
//		if (aSS_1 == 1) {
//			haste = true;
//		}
//		if (haste) {
//			this._os.writeC(0x12);
//		}

        // 增加火屬性
        final int defense_fire = pw_d4_1 + d4_1;
        if (defense_fire != 0) {
            this._os.writeC(0x1b);
            this._os.writeC(defense_fire);
        }

        // 增加水屬性
        final int defense_water = pw_d4_2 + d4_2;
        if (defense_water != 0) {
            this._os.writeC(0x1c);
            this._os.writeC(defense_water);
        }

        // 增加風屬性
        final int defense_wind = pw_d4_3 + d4_3;
        if (defense_wind != 0) {
            this._os.writeC(0x1d);
            this._os.writeC(defense_wind);
        }

        // 增加地屬性
        final int defense_earth = pw_d4_4 + d4_4;
        if (defense_earth != 0) {
            this._os.writeC(0x1e);
            this._os.writeC(defense_earth);
        }
        AbilityData.itemDesc(_itemInstance, _os);
        L1WilliamEnchantOrginal1.itemDesc(_itemInstance, _os);

//		boolean isOut = false;
//		// 寒冰耐性
//		final int freeze = pw_k6_1 + k6_1;
//		if (freeze != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(freeze);
//			this._os.writeC(0x21);
//			this._os.writeC(0x01);
//		}
//
//		// 石化耐性
//		final int stone = pw_k6_2 + k6_2;
//		if (stone != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stone);
//			this._os.writeC(0x21);
//			this._os.writeC(0x02);
//		}
//
//		// 睡眠耐性
//		final int sleep = pw_k6_3 + k6_3;
//		if (sleep != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sleep);
//			this._os.writeC(0x21);
//			this._os.writeC(0x03);
//		}
//
//		// 暗黑耐性
//		final int blind = pw_k6_4 + k6_4;
//		if (blind != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(blind);
//			this._os.writeC(0x21);
//			this._os.writeC(0x04);
//		}
//
//		// 昏迷耐性
//		final int stun = pw_k6_5 + k6_5;
//		if (stun != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stun);
//			this._os.writeC(0x21);
//			this._os.writeC(0x05);
//		}
//
//		// 支撐耐性
//		final int sustain = pw_k6_6 + k6_6;
//		if (sustain != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sustain);
//			this._os.writeC(0x21);
//			this._os.writeC(0x06);
//		}
        return this._os;
    }

    /**
     * 副助道具
     *
     * @return
     */
    private BinaryOutputStream accessories2() {
        int add_str = 0;
        int add_con = 0;
        int add_dex = 0;
        int add_int = 0;
        int add_wis = 0;
        int add_cha = 0;
        int add_hp = this._itemInstance.getAttachHp();
        int add_mp = this._itemInstance.getAttachMp();


        // AC
        this._os.writeC(0x13);
        int ac = this._item.get_ac();
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        this._os.writeC(ac);

        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        int pw_s1 = this._item.get_addstr() + add_str;// 力量
        int pw_s2 = this._item.get_adddex() + add_dex;// 敏捷
        int pw_s3 = this._item.get_addcon() + add_con;// 體質
        int pw_s4 = this._item.get_addwis() + add_wis;// 精神
        int pw_s5 = this._item.get_addint() + add_int;// 智力
        int pw_s6 = this._item.get_addcha() + add_cha;// 魅力

        int pw_sHp = this._item.get_addhp() + add_hp;// +HP
        int pw_sMp = this._item.get_addmp() + add_mp;// +MP
        int pw_sMr = this._itemPower.getMr() + this._itemInstance.getAttachMr();// MR(抗魔)
        int pw_sSp = this._item.get_addsp() + this._itemInstance.getAttachMagicDamage();// SP(魔攻)

        int pw_sDg = this._item.getDmgModifierByArmor() + this._itemInstance.getAttachOtherDamage();// DG(攻擊力)
        int pw_sHi = this._item.getHitModifierByArmor() + this._itemInstance.getAttachHit();// Hit(攻擊成功)

        int pw_d4_1 = this._item.get_defense_fire();// 火屬性
        int pw_d4_2 = this._item.get_defense_water();// 水屬性
        int pw_d4_3 = this._item.get_defense_wind();// 風屬性
        int pw_d4_4 = this._item.get_defense_earth();// 地屬性

        int pw_k6_1 = this._item.get_regist_freeze();// 寒冰耐性
        int pw_k6_2 = this._item.get_regist_stone();// 石化耐性
        int pw_k6_3 = this._item.get_regist_sleep();// 睡眠耐性
        int pw_k6_4 = this._item.get_regist_blind();// 暗黑耐性
        int pw_k6_5 = this._item.get_regist_stun();// 昏迷耐性
        int pw_k6_6 = this._item.get_regist_sustain();// 支撐耐性

        // 使用可能
        int bit = 0;
        bit |= this._item.isUseRoyal() ? 1 : 0;
        bit |= this._item.isUseKnight() ? 2 : 0;
        bit |= this._item.isUseElf() ? 4 : 0;
        bit |= this._item.isUseMage() ? 8 : 0;
        bit |= this._item.isUseDarkelf() ? 16 : 0;
        this._os.writeC(0x07);
        this._os.writeC(bit);

//		// 弓命中追加
//		if (this._item.getBowHitModifierByArmor() != 0) {
//			this._os.writeC(0x18);
//			this._os.writeC(this._item.getBowHitModifierByArmor());
//		}
//		// 弓傷害追加
//		if (this._item.getBowDmgModifierByArmor() != 0) {
//			this._os.writeC(0x23);
//			this._os.writeC(this._item.getBowDmgModifierByArmor());
//		}
        if (this._item.getDamageReduction() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("傷害減免+" + this._item.getDamageReduction() + ".");
        }
        if (this._item.getHitModifierByArmor() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離命中+" + this._item.getHitModifierByArmor() + ".");
        }
        if (this._item.getBowHitModifierByArmor() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離命中+" + this._item.getBowHitModifierByArmor() + ".");
        }
        if (this._item.getDmgModifierByArmor() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離傷害+" + this._item.getDmgModifierByArmor() + ".");
        }
        if (this._item.getBowDmgModifierByArmor() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離傷害+" + this._item.getBowDmgModifierByArmor() + ".");
        }
        if (this._item.get_addhpr() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回血+" + this._item.get_addhpr() + ".");
        }
        if (this._item.get_addmpr() > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回魔+" + this._item.get_addmpr() + ".");
        }
        // 攻擊成功
        if (pw_sHi != 0) {
            this._os.writeC(0x05);
            this._os.writeC(pw_sHi);
        }

        // 追加打擊
        if (pw_sDg != 0) {
            this._os.writeC(0x06);
            this._os.writeC(pw_sDg);
        }
        // 力量
        final int addstr = pw_s1;
        if (addstr != 0) {
            this._os.writeC(0x08);
            this._os.writeC(addstr);
        }
        // 敏捷
        final int adddex = pw_s2;
        if (adddex != 0) {
            this._os.writeC(0x09);
            this._os.writeC(adddex);
        }
        // 體質
        final int addcon = pw_s3;
        if (addcon != 0) {
            this._os.writeC(0x0a);
            this._os.writeC(addcon);
        }
        // 精神
        final int addwis = pw_s4;
        if (addwis != 0) {
            this._os.writeC(0x0b);
            this._os.writeC(addwis);
        }
        // 智力
        final int addint = pw_s5;
        if (addint != 0) {
            this._os.writeC(0x0c);
            this._os.writeC(addint);
        }
        // 魅力
        final int addcha = pw_s6;
        if (addcha != 0) {
            this._os.writeC(0x0d);
            this._os.writeC(addcha);
        }

        // +HP MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addhp = pw_sHp;
        if (addhp != 0) {
            this._os.writeC(0x0e);
            this._os.writeH(addhp);
        }

//		// +MP MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addmp = pw_sMp;
        if (addmp != 0) {
            this._os.writeC(0x20);
            this._os.writeH(addmp);
        }

        // MR(抗魔) MR 火 水 風 地 HP MP MR SP HPR MPR
        final int addmr = pw_sMr;
        if (addmr != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(addmr);
        }
        // SP(魔攻)火 水 風 地 HP MP MR SP HPR MPR
        final int addsp = pw_sSp;
        if (addsp != 0) {
            this._os.writeC(0x11);
            this._os.writeC(addsp);
        }

//		// 具備加速效果
//		boolean haste = this._item.isHasteItem();
//		if (haste) {
//			this._os.writeC(0x12);
//		}

        // 增加火屬性
        final int defense_fire = pw_d4_1;
        if (defense_fire != 0) {
            this._os.writeC(0x1b);
            this._os.writeC(defense_fire);
        }

        // 增加水屬性
        final int defense_water = pw_d4_2;
        if (defense_water != 0) {
            this._os.writeC(0x1c);
            this._os.writeC(defense_water);
        }

        // 增加風屬性
        final int defense_wind = pw_d4_3;
        if (defense_wind != 0) {
            this._os.writeC(0x1d);
            this._os.writeC(defense_wind);
        }

        // 增加地屬性
        final int defense_earth = pw_d4_4;
        if (defense_earth != 0) {
            this._os.writeC(0x1e);
            this._os.writeC(defense_earth);
        }
        AbilityData.itemDesc(_itemInstance, _os);
        L1WilliamEnchantOrginal1.itemDesc(_itemInstance, _os);

//		boolean isOut = false;
//		// 寒冰耐性
//		final int freeze = pw_k6_1;
//		if (freeze != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(freeze);
//			this._os.writeC(0x21);
//			this._os.writeC(0x01);
//		}
//
//		// 石化耐性
//		final int stone = pw_k6_2;
//		if (stone != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stone);
//			this._os.writeC(0x21);
//			this._os.writeC(0x02);
//		}
//
//		// 睡眠耐性
//		final int sleep = pw_k6_3;
//		if (sleep != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sleep);
//			this._os.writeC(0x21);
//			this._os.writeC(0x03);
//		}
//
//		// 暗黑耐性
//		final int blind = pw_k6_4;
//		if (blind != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(blind);
//			this._os.writeC(0x21);
//			this._os.writeC(0x04);
//		}
//
//		// 昏迷耐性
//		final int stun = pw_k6_5;
//		if (stun != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(stun);
//			this._os.writeC(0x21);
//			this._os.writeC(0x05);
//		}
//
//		// 支撐耐性
//		final int sustain = pw_k6_6;
//		if (sustain != 0) {
//			if (addmr != 0 && !isOut) {
//				this._os.writeC(0x21);
//				this._os.writeC(0xd6);
//				isOut = true;
//			}
//			this._os.writeC(0x0f);
//			this._os.writeH(sustain);
//			this._os.writeC(0x21);
//			this._os.writeC(0x06);
//		}
        return this._os;
    }

    /**
     * 武器
     *
     * @return
     */
    private BinaryOutputStream weapon() {
        // 打擊值
        this._os.writeC(0x01);
        int attr_DmgSmall = this._itemInstance.getAttachDmgSmall();
        int attr_DmgLarge = this._itemInstance.getAttachDmgLarge();
        int attr_HitModifier = this._itemInstance.getAttachHit();
        int attr_DmgModifier = this._itemInstance.getAttachOtherDamage();
        int add_str = 0;
        int add_con = 0;
        int add_dex = 0;
        int add_int = 0;
        int add_wis = 0;
        int add_cha = 0;
        int add_hp = 0;
        int add_mp = 0;
        int add_sp = 0;
        int dmgR = 0, hit = 0, bow_hit = 0, dmg = 0, bow_dmg = 0, hpr = 0, mpr = 0, pvp_dmg = 0;
        final BlessStatFactory.BlessStat stat = BlessStatFactory.getInstance().getData(this._itemInstance);
        if (stat != null) {
            add_str += stat.getStr();
            add_dex += stat.getDex();
            add_int += stat.get_int();
            add_wis += stat.getWis();
            add_cha += stat.getCha();
            add_con += stat.get_con();
            add_hp += stat.getHp();
            add_mp += stat.getMp();
            add_sp += stat.getSp();
            dmgR += stat.getDmgR();
            hit += stat.getHit();
            bow_hit += stat.getBow_hit();
            dmg += stat.getDmgu();
            bow_dmg += stat.getBow_dmgup();
            hpr += stat.getHpR();
            mpr += stat.getMpR();
            pvp_dmg += stat.getPvp_dmg();
        }


        this._os.writeC(this._item.getDmgSmall() + attr_DmgSmall);
        this._os.writeC(this._item.getDmgLarge() + attr_DmgLarge);

        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        // 強化數
        if (this._itemInstance.getEnchantLevel() != 0) {
            this._os.writeC(0x02);
            this._os.writeC(this._itemInstance.getEnchantLevel());
        }
        // 損傷度
        if (this._itemInstance.get_durability() != 0) {
            this._os.writeC(0x03);
            this._os.writeC(this._itemInstance.get_durability());
        }
        // 兩手武器
        if (this._item.isTwohandedWeapon()) {
            this._os.writeC(0x04);
        }

        int get_addstr = this._item.get_addstr() + add_str;// 力量
        int get_adddex = this._item.get_adddex() + add_dex;// 敏捷
        int get_addcon = this._item.get_addcon() + add_con;// 體質
        int get_addwis = this._item.get_addwis() + add_wis;// 精神
        int get_addint = this._item.get_addint() + add_int;// 智力
        int get_addcha = this._item.get_addcha() + add_cha;// 魅力

        int get_addhp = this._item.get_addhp() + add_hp + this._itemInstance.getAttachHp();// +HP
        int get_addmp = this._item.get_addmp() + add_mp + this._itemInstance.getAttachMp();// +MP
        int mr = this._itemPower.getMr() + this._itemInstance.getAttachMr();// MR(抗魔)

        int addWeaponSp = this._item.get_addsp() + add_sp + this._itemInstance.getAttachMagicDamage();// SP(魔攻)
        int addDmgModifier = this._item.getDmgModifier() + attr_DmgModifier;// DG(攻擊力)
        int addHitModifier = this._item.getHitModifier() + attr_HitModifier;// Hit(攻擊成功)

        int pw_d4_1 = this._item.get_defense_fire();// 火屬性
        int pw_d4_2 = this._item.get_defense_water();// 水屬性
        int pw_d4_3 = this._item.get_defense_wind();// 風屬性
        int pw_d4_4 = this._item.get_defense_earth();// 地屬性

        int pw_k6_1 = this._item.get_regist_freeze();// 寒冰耐性
        int pw_k6_2 = this._item.get_regist_stone();// 石化耐性
        int pw_k6_3 = this._item.get_regist_sleep();// 睡眠耐性
        int pw_k6_4 = this._item.get_regist_blind();// 暗黑耐性
        int pw_k6_5 = this._item.get_regist_stun();// 昏迷耐性
        int pw_k6_6 = this._item.get_regist_sustain();// 支撐耐性

        if (_itemInstance.get_power_name() != null) {
            final L1ItemPower_name power = _itemInstance.get_power_name();
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
        dmgR += this._item.getDamageReduction();
        hit += this._item.getHitModifierByArmor();
        bow_hit += this._item.getBowHitModifierByArmor();
        dmg += this._item.getDmgModifierByArmor();
        bow_dmg += this._item.getBowDmgModifierByArmor();
        hpr += this._item.get_addhpr();
        mpr += this._item.get_addmpr();
        if (dmgR > 0) {
            this._os.writeC(0x27);
            this._os.writeS("傷害減免+" + dmgR + ".");
        }
        if (hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離命中+" + hit + ".");
        }
        if (bow_hit > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離命中+" + bow_hit + ".");
        }
        if (dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("近距離傷害+" + dmg + ".");
        }
        if (bow_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("遠距離傷害+" + bow_dmg + ".");
        }
        if (hpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回血+" + hpr + ".");
        }
        if (mpr > 0) {
            this._os.writeC(0x27);
            this._os.writeS("回魔+" + mpr + ".");
        }
        if (pvp_dmg > 0) {
            this._os.writeC(0x27);
            this._os.writeS("PVP傷害+" + pvp_dmg + ".");
        }

        // 攻擊成功
        //int addHitModifier = this._item.getHitModifier() + pw_sHi;
        if (addHitModifier != 0) {
            this._os.writeC(0x05);
            this._os.writeC(addHitModifier);
        }

        // 追加打擊
        //int addDmgModifier = this._item.getDmgModifier() + pw_sDg;
        if (addDmgModifier != 0) {
            this._os.writeC(0x06);
            this._os.writeC(addDmgModifier);
        }

        // 使用可能
        int bit = 0;
        bit |= this._item.isUseRoyal() ? 1 : 0;
        bit |= this._item.isUseKnight() ? 2 : 0;
        bit |= this._item.isUseElf() ? 4 : 0;
        bit |= this._item.isUseMage() ? 8 : 0;
        bit |= this._item.isUseDarkelf() ? 16 : 0;
        this._os.writeC(0x07);
        this._os.writeC(bit);

        // 弓命中追加
		/*if (_item.getBowHitModifierByArmor() != 0) {
			os.writeC(24);
			os.writeC(_item.getBowHitModifierByArmor());
		}
		// 弓傷害追加
		if (_item.getBowDmgModifierByArmor() != 0) {
			os.writeC(35);
			os.writeC(_item.getBowDmgModifierByArmor());
		}*/
        // MP吸收
        if ((this._itemInstance.getItemId() == 126) || (this._itemInstance.getItemId() == 127) || (this._itemInstance.getItemId() == 301126) || (this._itemInstance.getItemId() == 301127)) {
            this._os.writeC(0x10);
        }
        // HP吸收
        if (this._itemInstance.getItemId() == 262) {
            this._os.writeC(0x22);
        }

        //int get_addstr = this._item.get_addstr();
        // STR~CHA
        if (get_addstr != 0) {
            this._os.writeC(0x08);
            this._os.writeC(get_addstr);
        }

        //int get_adddex = this._item.get_adddex();
        if (get_adddex != 0) {
            this._os.writeC(0x09);
            this._os.writeC(get_adddex);
        }

        //int get_addcon = this._item.get_addcon();
        if (get_addcon != 0) {
            this._os.writeC(0x0a);
            this._os.writeC(get_addcon);
        }

        //int get_addwis = this._item.get_addwis();
        if (get_addwis != 0) {
            this._os.writeC(0x0b);
            this._os.writeC(get_addwis);
        }

        //int get_addint = this._item.get_addint();
        if (get_addint != 0) {
            this._os.writeC(0x0c);
            this._os.writeC(get_addint);
        }

        //int get_addcha = this._item.get_addcha();
        if (get_addcha != 0) {
            this._os.writeC(0x0d);
            this._os.writeC(get_addcha);
        }

        // HP, MP

        //int get_addhp = this._item.get_addhp();
        if (get_addhp != 0) {
            this._os.writeC(0x0e);
            this._os.writeH(get_addhp);
        }

        //int get_addmp = this._item.get_addmp();
        if (get_addmp != 0) {
            this._os.writeC(0x20);
            this._os.writeH(get_addmp);
        }

        // MR
        //final int mr = this._itemPower.getMr();
        if (mr != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(mr);
        }
        // SP(魔法攻擊力)
        //int addWeaponSp = this._item.get_addsp() + pw_sSp;
        if (addWeaponSp != 0) {
            this._os.writeC(0x11);
            this._os.writeC(addWeaponSp);
        }
        // 具備加速效果
        if (this._item.isHasteItem()) {
            this._os.writeC(0x12);
        }
        // 增加火屬性
        if (pw_d4_1 != 0) {
            this._os.writeC(0x1b);
            this._os.writeC(pw_d4_1);
        }
        // 增加水屬性
        if (pw_d4_2 != 0) {
            this._os.writeC(0x1c);
            this._os.writeC(pw_d4_2);
        }
        // 增加風屬性
        if (pw_d4_3 != 0) {
            this._os.writeC(0x1d);
            this._os.writeC(pw_d4_3);
        }
        // 增加地屬性
        if (pw_d4_4 != 0) {
            this._os.writeC(0x1e);
            this._os.writeC(pw_d4_4);
        }

        // 凍結耐性
        if (pw_k6_1 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_1);
            this._os.writeC(0x21);
            this._os.writeC(0x01);
        }
        // 石化耐性
        if (pw_k6_2 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_2);
            this._os.writeC(0x21);
            this._os.writeC(0x02);
        }
        // 睡眠耐性
        if (pw_k6_3 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_3);
            this._os.writeC(0x21);
            this._os.writeC(0x03);
        }
        // 暗闇耐性
        if (pw_k6_4 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_4);
            this._os.writeC(0x21);
            this._os.writeC(0x04);
        }
        // 昏迷耐性
        if (pw_k6_5 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_5);
            this._os.writeC(0x21);
            this._os.writeC(0x05);
        }
        // 支撐耐性
        if (pw_k6_6 != 0) {
            this._os.writeC(0x0f);
            this._os.writeH(pw_k6_6);
            this._os.writeC(0x21);
            this._os.writeC(0x06);
        }
        AbilityData.itemDesc(_itemInstance, _os);
        L1WilliamEnchantOrginal.itemDesc(_itemInstance, _os);
        return this._os;
    }

    /**
     * 一般道具
     *
     * @return
     */
    private BinaryOutputStream etcitem() {
        for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
            if (liu.getItemId() == this._itemInstance.getItemId()) {
                int kong = liu.getAddExpGet();
                int Hp = liu.getAddHp();
                int Mp = liu.getAddMp();
                int Hpr = liu.getAddHpr();
                int Mpr = liu.getAddMpr();
                int Str = liu.getAddStr();
                int Dex = liu.getAddDex();
                int Int = liu.getAddInt();
                int Con = liu.getAddCon();
                int Wis = liu.getAddWis();
                int Cha = liu.getAddCha();
                int Earth = liu.getAddEarth();
                int Water = liu.getAddWater();
                int Fire = liu.getAddFire();
                int Wind = liu.getAddWind();
                int Stun = liu.getAddStun();
                int Stone = liu.getAddStone();
                int Sleep = liu.getAddSleep();
                int Freeze = liu.getAddFreeze();
                int Sustain = liu.getAddSustain();
                int Blind = liu.getAddBlind();
                int Mr = liu.getAddMr();
                int Sp = liu.getAddSp();
                int Hit = liu.getAddHit();
                int BowHit = liu.getAddBowHit();
                int Dmg = liu.getAddDmg();
                int BowDmg = liu.getAddBowDmg();
                int ac = liu.getAddAc();
                int aa = Dmg;
                if (ac != 0) {
                    this._os.writeC(0x13);
                    if (ac < 0) {
                        ac = Math.abs(ac);
                    }
                    this._os.writeC(ac);
                    this._os.writeC(this._item.getMaterial());
                    this._os.writeD(this._itemInstance.getWeight());
                } else {
                    this._os.writeC(0x17); // 材質
                    this._os.writeC(this._item.getMaterial());
                    this._os.writeD(this._itemInstance.getWeight());
                }
                if (BowDmg > 0) {
                    aa = BowDmg;
                }

                if (Str != 0) {
                    this._os.writeC(0x08);
                    this._os.writeC(Str);
                }
                if (Dex != 0) {
                    this._os.writeC(0x09);
                    this._os.writeC(Dex);
                }
                if (Con != 0) {
                    this._os.writeC(0x0a);
                    this._os.writeC(Con);
                }
                if (Wis != 0) {
                    this._os.writeC(0x0b);
                    this._os.writeC(Wis);
                }
                if (Int != 0) {
                    this._os.writeC(0x0c);
                    this._os.writeC(Int);
                }
                if (Cha != 0) {
                    this._os.writeC(0x0d);
                    this._os.writeC(Cha);
                }
                if (Hp != 0) {
                    this._os.writeC(0x0e);
                    this._os.writeH(Hp);
                }
                if (Mp != 0) {
                    this._os.writeC(0x20);
                    this._os.writeH(Mp);
                }
                if (Mr != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Mr);
                }
                if (Sp != 0) {
                    this._os.writeC(0x11);
                    this._os.writeC(Sp);
                }
                // 增加火屬性
                if (Fire != 0) {
                    this._os.writeC(0x1b);
                    this._os.writeC(Fire);
                }
                // 增加水屬性
                if (Water != 0) {
                    this._os.writeC(0x1c);
                    this._os.writeC(Water);
                }
                // 增加風屬性
                if (Wind != 0) {
                    this._os.writeC(0x1d);
                    this._os.writeC(Wind);
                }
                // 增加地屬性
                if (Earth != 0) {
                    this._os.writeC(0x1e);
                    this._os.writeC(Earth);
                }
                // 凍結耐性
                if (Freeze != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Freeze);
                    this._os.writeC(0x21);
                    this._os.writeC(0x01);
                }
                // 石化耐性
                if (Stone != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Stone);
                    this._os.writeC(0x21);
                    this._os.writeC(0x02);
                }
                // 睡眠耐性
                if (Sleep != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Sleep);
                    this._os.writeC(0x21);
                    this._os.writeC(0x03);
                }
                // 暗闇耐性
                if (Blind != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Blind);
                    this._os.writeC(0x21);
                    this._os.writeC(0x04);
                }
                // 昏迷耐性
                if (Stun != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Stun);
                    this._os.writeC(0x21);
                    this._os.writeC(0x05);
                }
                // 支撐耐性
                if (Sustain != 0) {
                    this._os.writeC(0x0f);
                    this._os.writeH(Sustain);
                    this._os.writeC(0x21);
                    this._os.writeC(0x06);
                }
                //弓傷害追加
		      /* if (BowDmg != 0) {
		           this._os.writeC(0x23);
		           this._os.writeC(BowDmg);		    
		       }*/
                if (BowHit != 0) {
                    this._os.writeC(0x18);
                    this._os.writeC(BowHit);
                }
                // 攻擊成功
                if (Hit != 0) {
                    this._os.writeC(0x05);
                    this._os.writeC(Hit);
                }              // 追加打擊
                if (Dmg != 0 || BowDmg != 0) {
                    this._os.writeC(0x06);
                    this._os.writeC(aa);
                }
                return this._os;
            }
        }
        this._os.writeC(0x17); // 材質
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        return this._os;
    }

    /**
     * 寵物防具
     *
     * @return
     */
    private BinaryOutputStream petarmor(final L1PetItem petItem) {
        this._os.writeC(0x13);
        int ac = petItem.getAddAc();
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        this._os.writeC(ac);
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        if (petItem.getHitModifier() != 0) {
            this._os.writeC(5);
            this._os.writeC(petItem.getHitModifier());
        }

        if (petItem.getDamageModifier() != 0) {
            this._os.writeC(6);
            this._os.writeC(petItem.getDamageModifier());
        }

        if (petItem.isHigher()) {
            this._os.writeC(7);
            this._os.writeC(128);
        }

        if (petItem.getAddStr() != 0) {
            this._os.writeC(8);
            this._os.writeC(petItem.getAddStr());
        }
        if (petItem.getAddDex() != 0) {
            this._os.writeC(9);
            this._os.writeC(petItem.getAddDex());
        }
        if (petItem.getAddCon() != 0) {
            this._os.writeC(10);
            this._os.writeC(petItem.getAddCon());
        }
        if (petItem.getAddWis() != 0) {
            this._os.writeC(11);
            this._os.writeC(petItem.getAddWis());
        }
        if (petItem.getAddInt() != 0) {
            this._os.writeC(12);
            this._os.writeC(petItem.getAddInt());
        }

        // HP, MP
        if (petItem.getAddHp() != 0) {
            this._os.writeC(14);
            this._os.writeH(petItem.getAddHp());
        }
        if (petItem.getAddMp() != 0) {
            this._os.writeC(32);
            this._os.writeC(petItem.getAddMp());
        }
        // MR
        if (petItem.getAddMr() != 0) {
            this._os.writeC(15);
            this._os.writeH(petItem.getAddMr());
        }
        // SP(魔力)
        if (petItem.getAddSp() != 0) {
            this._os.writeC(17);
            this._os.writeC(petItem.getAddSp());
        }
        return this._os;
    }

    /**
     * 寵物武器
     *
     * @return
     */
    private BinaryOutputStream petweapon(final L1PetItem petItem) {
        this._os.writeC(0x01); // 打擊值
        this._os.writeC(0x00);
        this._os.writeC(0x00);
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());

        if (petItem.isHigher()) {
            this._os.writeC(7);
            this._os.writeC(128);
        }

        if (petItem.getAddStr() != 0) {
            this._os.writeC(8);
            this._os.writeC(petItem.getAddStr());
        }
        if (petItem.getAddDex() != 0) {
            this._os.writeC(9);
            this._os.writeC(petItem.getAddDex());
        }
        if (petItem.getAddCon() != 0) {
            this._os.writeC(10);
            this._os.writeC(petItem.getAddCon());
        }
        if (petItem.getAddWis() != 0) {
            this._os.writeC(11);
            this._os.writeC(petItem.getAddWis());
        }
        if (petItem.getAddInt() != 0) {
            this._os.writeC(12);
            this._os.writeC(petItem.getAddInt());
        }

        // HP, MP
        if (petItem.getAddHp() != 0) {
            this._os.writeC(14);
            this._os.writeH(petItem.getAddHp());
        }
        if (petItem.getAddMp() != 0) {
            this._os.writeC(32);
            this._os.writeC(petItem.getAddMp());
        }
        // MR
        if (petItem.getAddMr() != 0) {
            this._os.writeC(15);
            this._os.writeH(petItem.getAddMr());
        }
        return this._os;
    }
}
