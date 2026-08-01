package com.lineage.data.item_etcitem.hole;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HoleGem2 extends ItemExecutor {
    private static  final Random _random = new Random();
    public static ItemExecutor get() {
        return new HoleGem2();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 對像OBJID
        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem == null) {
            return;
        }
        switch (tgItem.getItem().getUseType()) {
            case 1: // 武器
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
                break;
            default:
            {
                pc.sendPackets(new S_ServerMessage("只能對防具使用"));
                return;
            }
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
//        if (tgItem.getGemHole() <= 0) {
//            pc.sendPackets(new S_ServerMessage("請先將武器打洞"));
//            return;
//        }
        if (tgItem.getGemHoleIndex() > 0) {
            pc.sendPackets(new S_ServerMessage("無法使用。"));
            return;
        }
        if (!CustomHoleGemDataByArmor.getInstance().containsKey(item.getItemId())) {
            pc.sendPackets(new S_ServerMessage("沒有任何事情發生。"));
            return;
        }
        final List<CustomHoleGemDataByArmor.GemData> gemData = new ArrayList<>(CustomHoleGemDataByArmor.getInstance().getGemData(item.getItemId()));
        Collections.shuffle(gemData);
        CustomHoleGemDataByArmor.GemData result = null;
        while (result == null) {
            for (final CustomHoleGemDataByArmor.GemData gem : gemData) {
                if (rand(0, 999999) < gem.getChance()) {
                    result = gem;
                    break;
                }
            }
        }
        pc.getInventory().removeItem(item, 1);
        tgItem.setGemHoleIndex(result.getIndex());
        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_GEM_HOLE_INDEX);
        pc.sendPackets(new S_ServerMessage("鑲崁成功 [" +  result.getName() + "]"));
        if (result.isOut()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("玩家: ").append(pc.getName()).append(" 成功將").append("[");
            if (!tgItem.isIdentified()) {
                sb.append(tgItem.getItem().getName());

            } else {
                if (tgItem.getEnchantLevel() > 0) {
                    sb.append("+" + tgItem.getEnchantLevel() + " " + tgItem.getItem().getName());

                } else if (tgItem.getEnchantLevel() < 0) {
                    sb.append(tgItem.getEnchantLevel() + " " + tgItem.getItem().getName());

                } else {
                    sb.append(tgItem.getItem().getName());
                }
            }
            sb.append("]");
            sb.append(" 鑲崁上 【").append(result.getName()).append("】");
            World.get().broadcastPacketToAll(
                    new S_HelpMessage(pc.getName(),
                            sb.toString()));
        }
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
