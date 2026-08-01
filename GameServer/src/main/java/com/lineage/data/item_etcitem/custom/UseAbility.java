package com.lineage.data.item_etcitem.custom;

import com.custom.ability.AbilityData;
import com.custom.ability.CustomWeaponAbility;
import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_IdentifyDesc;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Map;
import java.util.Random;

public class UseAbility extends ItemExecutor {
    public static ItemExecutor get() {
        return new UseAbility();
    }
    private static final Random _random = new Random();
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        if (tgItem.getCanAbilityType() != 1) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        int chance = rand(0, 999999);
        if (chance < ConfigOther.ABILITY_3_CHANCE) {
            tgItem.setCanAbilityType(4);
        } else {
            tgItem.setCanAbilityType(3);
        }
        pc.getInventory().removeItem(item, 1);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ABILITY_POS_1_ID + L1PcInventory.COL_ABILITY_POS_2_ID + L1PcInventory.COL_ABILITY_POS_3_ID + L1PcInventory.COL_CAN_ABILITY_TYPE);
        if (!tgItem.isIdentified()) {
            tgItem.setIdentified(true);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_IS_ID);
            pc.sendPackets(new S_IdentifyDesc(tgItem));
        }
        World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f=" + pc.getName() + " 的 " + tgItem.getViewName() + " 鑑定出了！"));
        World.get().broadcastPacketToAll(new S_ServerMessage("\\fT" + pc.getName() + " 的 " + tgItem.getViewName() + " 鑑定出了！"));
    }
    @Override
    public void set_set(String[] set) {

    }
}
