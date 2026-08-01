package com.lineage.data.item_etcitem.custom;

import com.custom.ability.AbilityData;
import com.custom.ability.CustomArmorAbility;
import com.custom.ability.CustomWeaponAbility;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Random;

public class WeaponAbility extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(WeaponAbility.class);
    private static final Random _random = new Random();

    public static ItemExecutor get() {
        return new WeaponAbility();
    }
    public final AbilityData randWeaponAbilityData() {
        while (true) {
            for (final Map.Entry<Integer, AbilityData> data : CustomWeaponAbility.getInstance().getData().entrySet()) {
                if (data.getValue().getChance() >= rand(0, 999999)) {
                    return data.getValue();
                }
            }
        }
    }
    public final AbilityData randArmorAbilityData() {
        while (true) {
            for (final Map.Entry<Integer, AbilityData> data : CustomArmorAbility.getInstance().getData().entrySet()) {
                if (data.getValue().getChance() >= rand(0, 999999)) {
                    return data.getValue();
                }
            }
        }
    }
    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        switch (tgItem.getItem().getUseType()) {
            case 1: // 武器
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
                break;
            default:
            {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("請先將裝備脫除。"));
            return;
        }
        if (tgItem.getCanAbilityType() <= 1) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        switch (tgItem.getItem().getUseType()) {
            case 1: // 武器
                if (!CustomWeaponAbility.getInstance().canUseType(tgItem.getItem().getUseType())) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
                switch (tgItem.getCanAbilityType()) {
                    case 2:
                        tgItem.setAbilityPos1(randWeaponAbilityData().getId());
                        tgItem.setAbilityPos2(0);
                        tgItem.setAbilityPos3(0);
                        break;
                    case 3:
                        tgItem.setAbilityPos1(randWeaponAbilityData().getId());
                        tgItem.setAbilityPos2(randWeaponAbilityData().getId());
                        tgItem.setAbilityPos3(0);
                        break;
                    case 4:
                        tgItem.setAbilityPos1(randWeaponAbilityData().getId());
                        tgItem.setAbilityPos2(randWeaponAbilityData().getId());
                        tgItem.setAbilityPos3(randWeaponAbilityData().getId());
                        break;
                }
                break;
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
                if (!CustomArmorAbility.getInstance().canUseType(tgItem.getItem().getUseType())) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
                switch (tgItem.getCanAbilityType()) {
                    case 2:
                        tgItem.setAbilityPos1(randArmorAbilityData().getId());
                        tgItem.setAbilityPos2(0);
                        tgItem.setAbilityPos3(0);
                        break;
                    case 3:
                        tgItem.setAbilityPos1(randArmorAbilityData().getId());
                        tgItem.setAbilityPos2(randArmorAbilityData().getId());
                        tgItem.setAbilityPos3(0);
                        break;
                    case 4:
                        tgItem.setAbilityPos1(randArmorAbilityData().getId());
                        tgItem.setAbilityPos2(randArmorAbilityData().getId());
                        tgItem.setAbilityPos3(randArmorAbilityData().getId());
                        break;
                }
                break;
        }
        // 刪除卷軸
        pc.getInventory().removeItem(item, 1);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ABILITY_POS_1_ID + L1PcInventory.COL_ABILITY_POS_2_ID + L1PcInventory.COL_ABILITY_POS_3_ID + L1PcInventory.COL_CAN_ABILITY_TYPE);
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
