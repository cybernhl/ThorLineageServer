package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_OwnCharPack;

/**
 * 畫面更新符 58002
 */
public class Move_Reel2 extends ItemExecutor {

    /**
     *
     */
    private Move_Reel2() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Move_Reel2();
    }

    /**
     * 道具物件執行
     *
     * @param data
     *            參數
     * @param pc
     *            執行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        try {
            // pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
            Thread.sleep(200);
            final int x = pc.getX();
            final int y = pc.getY();
            final short map = pc.getMapId();
            final int h = pc.getHeading();
            L1Teleport.teleport(pc, x, y, map, h, false);
            pc.sendPackets(new S_OwnCharPack(pc));
            pc.removeAllKnownObjects();
            pc.updateObject();
            pc.sendVisualEffectAtTeleport();
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.setTeleport(false);
            // pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
