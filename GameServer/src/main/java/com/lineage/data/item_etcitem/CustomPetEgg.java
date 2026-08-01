package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;

public class CustomPetEgg extends ItemExecutor {
    public CustomPetEgg() {}
    public static ItemExecutor get() {
        return new CustomPetEgg();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (monster_id <= 0) {
            return;
        }
        int petcost = 0;
        final Object[] petList = pc.getPetList().values().toArray();
        for (final Object pet : petList) {
            int nowpetcost = ((L1NpcInstance) pet).getPetcost();
            petcost += nowpetcost;
        }

        int charisma = pc.getCha();

        charisma -= petcost;

        if (charisma <= 0) {
            // 489：你無法一次控制那麼多寵物。
            pc.sendPackets(new S_ServerMessage(489));
            return;
        }
        final L1PcInventory inv = pc.getInventory();
        if (inv.getSize() < 180) {
            final L1ItemInstance petamu = inv.storeItem(40314, 1); // 項圈
            if (petamu != null) {
                new L1PetInstance(monster_id, pc, petamu.getId());
                pc.sendPackets(new S_ItemName(petamu));

                // 刪除道具
                pc.getInventory().removeItem(item, 1);
            }
        }
    }
    private int monster_id = 0;
    @Override
    public void set_set(String[] set) {
        try {
            monster_id = Integer.parseInt(set[1]);
        } catch (Exception e) {

        }
    }
}
