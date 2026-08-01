package com.lineage.data.npc.other;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 釣魚小童 80082
 * 
 * @author dexc
 * 
 */
public class Npc_Fishing_1 extends NpcExecutor {

    /**
     *
     */
    private Npc_Fishing_1() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_Fishing_1();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_in_1"));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        boolean flag = false;
        if (cmd.equalsIgnoreCase("L")) { // 遠拋長釣竿
            if (pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) { // 扣錢
                CreateNewItem.createNewItem(pc, 41293, 1); // 長釣竿
                flag = true;
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_in_0"));
            }
        }
        if (cmd.equalsIgnoreCase("S")) { // 近拋短釣竿
            if (pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) { // 扣錢
                CreateNewItem.createNewItem(pc, 41294, 1); // 短釣竿
                flag = true;
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "fk_in_0"));
            }
        }

        if (flag) {
            L1PolyMorph.undoPoly(pc);
            L1Teleport.teleport(pc, 32810, 32794, (short) 5124, 6, true);
        }
    }
}
