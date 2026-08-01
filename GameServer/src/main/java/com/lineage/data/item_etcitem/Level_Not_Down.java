package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Level_Not_Down extends ItemExecutor {
    public static ItemExecutor get() {
        return new Level_Not_Down();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        pc.resetBaseMaxHp();
        pc.resetBaseMaxMp();
        pc.setCurrentHp(pc.getMaxHp());
        pc.setCurrentMp(pc.getMaxMp());
        pc.getInventory().removeItem(item, 1);
    }
}
