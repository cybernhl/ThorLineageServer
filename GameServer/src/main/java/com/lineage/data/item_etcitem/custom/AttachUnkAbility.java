package com.lineage.data.item_etcitem.custom;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_IdentifyDesc;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Random;

public class AttachUnkAbility extends ItemExecutor {
    public static ItemExecutor get() {
        return new AttachUnkAbility();
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
        if (tgItem.getCanAbilityType() != 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (rand(0, 999999) < this.chance) {
            tgItem.setCanAbilityType(1);
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ABILITY_POS_1_ID + L1PcInventory.COL_ABILITY_POS_2_ID + L1PcInventory.COL_ABILITY_POS_3_ID + L1PcInventory.COL_CAN_ABILITY_TYPE);
            if (!tgItem.isIdentified()) {
                tgItem.setIdentified(true);
                pc.getInventory().updateItem(tgItem, L1PcInventory.COL_IS_ID);
                pc.sendPackets(new S_IdentifyDesc(tgItem));
            }
            World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\fX" + pc.getName() + " 的 " + tgItem.getViewName() + " 賦予潛力成功了！"));
            World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f=" + pc.getName() + " 的 " + tgItem.getViewName() + " 賦予潛力成功了！"));
        } else {
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.sendPackets(new S_ServerMessage("\\fW賦予 " + tgItem.getLogName() + " 潛力失敗。"));
        }
    }
    private int chance;
    @Override
    public void set_set(String[] set) {
        try {
            chance = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
