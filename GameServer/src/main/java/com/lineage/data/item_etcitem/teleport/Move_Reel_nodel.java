package com.lineage.data.item_etcitem.teleport;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import java.util.ArrayList;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;

/**
 * 掛機瞬移符 40863<br>
 */
public class Move_Reel_nodel extends ItemExecutor {

    /**
	 *
	 */
    private Move_Reel_nodel() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Move_Reel_nodel();
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
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
        // 所在地圖編號
        Short mapID = (short) data[0];
        int mapX = data[1];
        int mapY = data[2];
        // 所在位置 是否允許傳送
        final boolean isTeleport = pc.getMap().isTeleportable();
        if (!isTeleport) {
            // 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
                    false));
        } else {
            boolean flag = false; // 傳送模式
            final ArrayList<L1BookMark> bookList = CharBookReading.get()
                    .getBookMarks(pc);
            // 檢查是否有此坐標
            if (bookList != null) {
                for (final L1BookMark book : bookList) {
                    if (book.getMapId() == mapID && book.getLocX() == mapX
                            && book.getLocY() == mapY) {
                        flag = true;
                    }
                }
           } else {
               L1Teleport.randomTeleport(pc, true);
            }
            if (flag) 
            {
                L1Teleport.teleport(pc, mapX, mapY, mapID, 5, true);
            } else 
            { // 隨機傳送
                L1Teleport.randomTeleport(pc, true);
            }
            // 絕對屏障解除
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
            }
            // pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
            // false));
        }
    }
}
