package com.lineage.data.item_etcitem.hole;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Random;

public class SetHole extends ItemExecutor {
    public static ItemExecutor get() {
        return new SetHole();
    }
    private static Random _random = new Random();
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 1 && tgItem.getItem().getUseType() != 22) {
            pc.sendPackets(new S_ServerMessage("沒有任何事情發生。"));
            return;
        }
        if (tgItem.get_card_use() == 1) {
            // 79：\f1沒有任何事情發生。
            pc.sendPackets(new S_ServerMessage("裝備狀態無法使用"));
            return;
        }
        if (tgItem.isEquipped()) {
            // 79：\f1沒有任何事情發生。
            pc.sendPackets(new S_ServerMessage("裝備狀態無法使用"));
            return;
        }
        if (tgItem.getGemHole() > 0) {
            pc.sendPackets(new S_ServerMessage("沒有任何事情發生"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        if ((_random.nextInt(9999999) + 1) < chance * 10000) {
            tgItem.setGemHole(1);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_GEM_HOLE);
            pc.sendPackets(new S_ServerMessage("道具發生強烈的藍色光芒，打洞成功了 !!!"));
            World.get().broadcastPacketToAll(new S_ServerMessage(pc.getName() + " 的 " + tgItem.getLogName() + " 打洞成功了!"));
        } else {
            pc.sendPackets(new S_ServerMessage("道具發生強烈的光芒，但是沒有任何事情發生"));
        }
    }
    private int chance;

    public void set_set(String[] set) {
        try {
            chance = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
