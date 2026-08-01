package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.CustomSpecialStatTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class CustomSpecialStat extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(CustomSpecialStat.class);

    private static  final Random _random = new Random();
    public CustomSpecialStat() {}
    public static ItemExecutor get() {
        return new CustomSpecialStat();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("裝備狀態無法升級"));
            return;
        }
        if (tgItem.getSpecialStat() > 0) {
            pc.sendPackets(new S_ServerMessage("無法繼續使用。"));
            return;
        }
        if (!CustomSpecialStatTable.get().getSpecialDatas().containsKey(tgItem.getItemId())) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        if (rand(0, 999999) < (chance * 10000)) {
            tgItem.setSpecialStat(1);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_SPECIAL_STAT);
            World.get().broadcastPacketToAll(new S_ServerMessage("玩家【" + pc.getName() + "】成功將【" + tgItem.getItem().getName() + "】強化成功了!!!"));
        }
    }
    private int chance = 0;

    @Override
    public void set_set(String[] set) {
        try {
            chance = Integer.parseInt(set[1]);
        } catch (Exception e) {

        }
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
