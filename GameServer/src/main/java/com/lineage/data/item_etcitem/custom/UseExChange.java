package com.lineage.data.item_etcitem.custom;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.item_etcitem.Cursed_Blood;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class UseExChange extends ItemExecutor {
    public UseExChange() {}
    public static ItemExecutor get() {
        return new UseExChange();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final long count = pc.getInventory().countItems(item.getItemId());
        if (count >= needCount) {
            pc.getInventory().consumeItem(item.getItemId(), needCount);
            pc.getInventory().storeItem(give_item_id, give_item_count);
        } else {
            pc.sendPackets(new S_SystemMessage(item.getItem().getName() + "(" + needCount + ") 不足"));
        }
    }
    private int needCount;
    private int give_item_id, give_item_count;
    @Override
    public void set_set(String[] set) {
        try {
            needCount = Integer.parseInt(set[1]);
            give_item_id = Integer.parseInt(set[2]);
            give_item_count = Integer.parseInt(set[3]);
        } catch (Exception e) {
        }
    }
}
