package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.datatables.sql.SpawnBossTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <font color=#00800>穿雲箭(血盟)</font><br>
 *
 * @since 類：Scroll_Clan_Teleport2
 * @since 修改時間：2021-07-20 上午5:30:33
 * @author 阿國
 *
 */
public class NewYear_Teleprot extends ItemExecutor {

    private NewYear_Teleprot() {
        // TODO 自動產生的建構子 Stub
    }

    public static ItemExecutor get() {
        return new NewYear_Teleprot();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final Collection<L1PcInstance> clanMembers = World.get().getAllPlayers();
        final String msg = "玩家 " + pc.getName() + " 使用" + item.getName();
        final L1Spawn spawn = SpawnBossReading.get().getTemplate(98077); //.getBossSeach(20004);
        if (spawn == null) {
            pc.sendPackets(new S_SystemMessage("年獸目前還未出生。"));
            return;
        }
        final long existTime = spawn.get_existTime() * 60 * 1000;
        final long spawnTime = spawn.get_nextSpawnTime().getTimeInMillis();
        final long nowTime = System.currentTimeMillis();
        if (existTime + spawnTime > nowTime) {
            pc.sendPackets(new S_SystemMessage("年獸目前還未出生。"));
            return;
        }

        for (final L1PcInstance listner : clanMembers) {
            // 商店村模式
            if (!listner.isPrivateShop() && listner != pc) {
                    final L1Location location = new L1Location();
                    location.setX(spawn.getTmpLocX());
                    location.setY(spawn.getTmpLocY());
                    location.setMap(spawn.getTmpMapid());
                    // 解除魔法技能絕對屏障
                    L1BuffUtil.cancelAbsoluteBarrier(listner);
                    final L1Location loc = location.randomLocation(3, true);
                    listner.setTeleportX(location.getX());
                    listner.setTeleportY(location.getY());
                    listner.setTeleportMapId((short) loc.getMapId());
                    listner.sendPackets(new S_Message_YN(620, "\\f2血盟成員" + pc.getName() + "使用了穿雲箭(血盟)需要您支援。(Y/N)！"));
                }
        }
        World.get().broadcastPacketToAll(new S_ServerMessage(msg + " 邀請您一同前往擊殺。"));
        World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f=" + msg + " 邀請您一同前往擊殺。"));
        L1Teleport.teleport(pc, spawn.getTmpLocX(), spawn.getTmpLocY(), spawn.getTmpMapid(), 5, false);
        // 刪除道具
        pc.getInventory().removeItem(item, 1);
    }
}
