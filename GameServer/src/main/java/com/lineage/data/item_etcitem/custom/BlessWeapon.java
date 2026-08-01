package com.lineage.data.item_etcitem.custom;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.item_etcitem.hole.CustomHoleGemData;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlessWeapon extends ItemExecutor {
    private static  final Random _random = new Random();
    public static ItemExecutor get() {
        return new BlessWeapon();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.getBless() == 0) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        if (tgItem.isEquipped() || tgItem.get_card_use() == 1) {
            pc.sendPackets(new S_ServerMessage("請先脫除後再試。"));
            return;
        }
        if (!tgItem.isIdentified()) {
            pc.sendPackets(new S_ServerMessage("請先鑑定後再試。"));
            return;
        }
        switch (tgItem.getItem().getUseType()) {
            case 1:
                break;
            default:
                pc.sendPackets(new S_ServerMessage(79));
                return;
        }
        pc.getInventory().removeItem(item, 1);
        if (rand(0, 999999) > this.chance) {
            pc.sendPackets(new S_ServerMessage("這次祝福失敗了.."));
            return;
        }
        tgItem.setBless(0);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_BLESS);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_BLESS);
        pc.sendPackets(new S_ServerMessage("成功對【" + tgItem.getViewName() + "】祝福成功"));
        World.get().broadcastPacketToAll(new S_ServerMessage("\\fY玩家【" + pc.getId() + "】成功對【" + tgItem.getViewName() + "】祝福成功"));
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
    private int chance;
    @Override
    public void set_set(String[] set) {
        try {
            chance = Integer.parseInt(set[1]);
        } catch (Exception e) {
            chance = 0;
        }
    }
}
