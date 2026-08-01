package com.lineage.data.item_etcitem.hole;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;

public class ClearHoleGem extends ItemExecutor {
    public static ItemExecutor get() {
        return new ClearHoleGem();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
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
        if (tgItem.getGemHoleIndex() <= 0) {
            pc.sendPackets(new S_ServerMessage("沒有任何事情發生。"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        tgItem.setGemHoleIndex(0);
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_GEM_HOLE_INDEX);
        pc.sendPackets(new S_ServerMessage("\\fW鑲崁能力已清除"));
    }
}
