package com.custom.ability;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.utils.BinaryOutputStream;

public class AbilityData {
    private final String name;
    private final int value;
    private final int id;
    private final int type;
    private final int chance;
    public AbilityData(final int id, final String name, int type, int value, int chance) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.type = type;
        this.chance = chance;
    }
    public final int getId() {
        return this.id;
    }
    public final String getName() {
        return this.name;
    }
    public final int getType() {
        return this.type;
    }
    public final int getValue() {
        return this.value;
    }
    public final int getChance() {
        return this.chance;
    }
    public static void set(final L1PcInstance pc, final L1ItemInstance item) {
        if (item.getCanAbilityType() <= 1) {
            return;
        }
        if (item.getAbilityPos1ID() <= 0 && item.getAbilityPos2ID() <= 0 && item.getAbilityPos3ID() <= 0) {
            return;
        }
        setStatusById(pc, item.getAbilityPos1ID(), item.getItem().getUseType() == 1, true);
        setStatusById(pc, item.getAbilityPos2ID(), item.getItem().getUseType() == 1, true);
        setStatusById(pc, item.getAbilityPos3ID(), item.getItem().getUseType() == 1, true);
    }
    public static void remove(final L1PcInstance pc, final L1ItemInstance item)  {
        if (item.getCanAbilityType() <= 1) {
            return;
        }
        if (item.getAbilityPos1ID() <= 0 && item.getAbilityPos2ID() <= 0 && item.getAbilityPos3ID() <= 0) {
            return;
        }
        setStatusById(pc, item.getAbilityPos1ID(), item.getItem().getUseType() == 1, false);
        setStatusById(pc, item.getAbilityPos2ID(), item.getItem().getUseType() == 1, false);
        setStatusById(pc, item.getAbilityPos3ID(), item.getItem().getUseType() == 1, false);
    }
    public static void setStatusById(final L1PcInstance pc, final int ability_pos_id, boolean isWeapon, boolean set)  {
        final AbilityData data = isWeapon ? CustomWeaponAbility.getInstance().getData().get(ability_pos_id) : CustomArmorAbility.getInstance().getData().get(ability_pos_id);
        if (data == null) {
            return;
        }
        switch (data.getType()) {
            case 0: // 近距離攻擊力
                if (set) {
                    pc.addDmgup(data.value);
                } else {
                    pc.addDmgup(-data.value);
                }
                break;
            case 1: // 遠距離攻擊力
                if (set) {
                    pc.addBowDmgup(data.value);
                } else {
                    pc.addBowDmgup(-data.value);
                }
                break;
            case 2: // 額外攻擊
                if (set) {
                    pc.addDmgup(data.value);
                    pc.addBowDmgup(data.value);
                } else {
                    pc.addDmgup(-data.value);
                    pc.addBowDmgup(-data.value);
                }
                break;
            case 3: // 近距離命中率
                if (set) {
                    pc.addHitup(data.value);
                } else {
                    pc.addHitup(-data.value);
                }
                break;
            case 4: // 遠距離命中率
                if (set) {
                    pc.addBowHitup(data.value);
                } else {
                    pc.addBowHitup(-data.value);
                }
                break;
            case 5: // 攻擊成功
                if (set) {
                    pc.addHitup(data.value);
                    pc.addBowHitup(data.value);
                } else {
                    pc.addHitup(-data.value);
                    pc.addBowHitup(-data.value);
                }
                break;
            case 6: // 防禦
                if (set) {
                    pc.addAc(-data.value);
                } else {
                    pc.addAc(data.value);
                }
                break;
            case 7: // 抗魔
                if (set) {
                    pc.addMr(data.value);
                } else {
                    pc.addMr(-data.value);
                }
                pc.sendPackets(new S_SPMR(pc));
                break;
            case 8: // 血量
                if (set) {
                    pc.addMaxHp(data.value);
                } else {
                    pc.addMaxHp(-data.value);
                }
                break;
            case 9: // 魔力
                if (set) {
                    pc.addMaxMp(data.value);
                } else {
                    pc.addMaxMp(-data.value);
                }
                break;
            case 10: // 魔攻
                if (set) {
                    pc.addSp(data.value);
                } else {
                    pc.addSp(-data.value);
                }
                break;
            case 11: // 力量
                if (set) {
                    pc.addStr(data.value);
                } else {
                    pc.addStr(-data.value);
                }
                break;
            case 12: // 敏捷
                if (set) {
                    pc.addDex(data.value);
                } else {
                    pc.addDex(-data.value);
                }
                break;
            case 13: // 體質
                if (set) {
                    pc.addCon(data.value);
                } else {
                    pc.addCon(-data.value);
                }
                break;
            case 14: // 精神
                if (set) {
                    pc.addWis(data.value);
                } else {
                    pc.addWis(-data.value);
                }
                pc.resetBaseMr();
                break;
            case 15: // 智力
                if (set) {
                    pc.addInt(data.value);
                } else {
                    pc.addInt(-data.value);
                }
                break;
            case 16: // 魅力
                if (set) {
                    pc.addCha(data.value);
                } else {
                    pc.addCha(-data.value);
                }
                break;
            case 17: // 回血
                if (set) {
                    pc.addHpr(data.value);
                } else {
                    pc.addHpr(-data.value);
                }
                break;
            case 18: // 回魔
                if (set) {
                    pc.addMpr(data.value);
                } else {
                    pc.addMpr(-data.value);
                }
                break;
            case 19: // 物理傷害減免
                if (set) {
                    pc.addDamageReductionByArmor(data.value);
                } else {
                    pc.addDamageReductionByArmor(-data.value);
                }
                break;
            case 20: // 魔法傷害減免
                if (set) {
                    pc.add_magic_reduction_dmg(data.value);
                } else {
                    pc.add_magic_reduction_dmg(-data.value);
                }
                break;
        }
    }
    public static void itemDesc(final L1ItemInstance item, final BinaryOutputStream _os) {
        if (item.getCanAbilityType() <= 1) {
            return;
        }
        if (item.getItem().getUseType() == 1) {
            if (!CustomWeaponAbility.getInstance().canUseType(item.getItem().getUseType())) {
                return;
            }
            _os.writeC(0x27);
            _os.writeS("一 武器潛力屬性 一");
            if (item.getCanAbilityType() == 1) {
                _os.writeC(0x27);
                _os.writeS("潛力尚未鑑定.");
            }
            if (item.getCanAbilityType() >= 2) {
                if (item.getAbilityPos1ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("1." + CustomWeaponAbility.getInstance().getData().get(item.getAbilityPos1ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("1.未賦予");
                }
            }
            if (item.getCanAbilityType() >= 3) {
                if (item.getAbilityPos2ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("2." + CustomWeaponAbility.getInstance().getData().get(item.getAbilityPos2ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("2.未賦予");
                }
            }
            if (item.getCanAbilityType() >= 4) {
                if (item.getAbilityPos3ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("3." + CustomWeaponAbility.getInstance().getData().get(item.getAbilityPos3ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("3.未賦予");
                }
            }
        } else {
            if (!CustomArmorAbility.getInstance().canUseType(item.getItem().getUseType())) {
                return;
            }
            _os.writeC(0x27);
            _os.writeS("一 防具潛力屬性 一");
            if (item.getCanAbilityType() == 1) {
                _os.writeC(0x27);
                _os.writeS("潛力尚未鑑定.");
            }
            if (item.getCanAbilityType() >= 2) {
                if (item.getAbilityPos1ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("1." + CustomArmorAbility.getInstance().getData().get(item.getAbilityPos1ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("1.未賦予");
                }
            }
            if (item.getCanAbilityType() >= 3) {
                if (item.getAbilityPos2ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("2." + CustomArmorAbility.getInstance().getData().get(item.getAbilityPos2ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("2.未賦予");
                }
            }
            if (item.getCanAbilityType() >= 4) {
                if (item.getAbilityPos3ID() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("3." + CustomArmorAbility.getInstance().getData().get(item.getAbilityPos3ID()).getName());
                } else {
                    _os.writeC(0x27);
                    _os.writeS("3.未賦予");
                }
            }
        }
    }
}
