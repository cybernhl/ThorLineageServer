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

public class ArmorAbility extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ArmorAbility.class);
    private static final Random _random = new Random();

    public static ItemExecutor get() {
        return new ArmorAbility();
    }
    public final AbilityData randAbilityData() {
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
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("請先將裝備脫除。"));
            return;
        }
        if (tgItem.getCanAbilityType() <= 1) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (!CustomArmorAbility.getInstance().canUseType(tgItem.getItem().getUseType())) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        switch (tgItem.getCanAbilityType()) {
            case 2:
                tgItem.setAbilityPos1(randAbilityData().getId());
                tgItem.setAbilityPos2(0);
                tgItem.setAbilityPos3(0);
                break;
            case 3:
                tgItem.setAbilityPos1(randAbilityData().getId());
                tgItem.setAbilityPos2(randAbilityData().getId());
                tgItem.setAbilityPos3(0);
                break;
            case 4:
                tgItem.setAbilityPos1(randAbilityData().getId());
                tgItem.setAbilityPos2(randAbilityData().getId());
                tgItem.setAbilityPos3(randAbilityData().getId());
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
