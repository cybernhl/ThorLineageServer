package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.CustomAttachStatTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Random;

public class CustomArmorAttachStat extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(CustomArmorAttachStat.class);

    private static  final Random _random = new Random();

    public CustomArmorAttachStat() { }

    public static ItemExecutor get() {
        return new CustomArmorAttachStat();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 對像OBJID
            final int targObjId = data[0];

            final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

            if (tgItem == null) {
                return;
            }

            final int use_type = tgItem.getItem().getUseType();
            switch (use_type) {
                case 2:// 盔甲
                case 18:// T恤
                case 19:// 斗篷
                case 20:// 手套
                case 21:// 靴
                case 22:// 頭盔
                case 25:// 盾牌
                    break;
                default:
                    pc.sendPackets(new S_ServerMessage("只能對防具使用"));
                    return;
            }
            if (tgItem.getAttachIndex() > 0) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            // 取回物件屬性
            final Map<Integer, CustomAttachStatTable.CustomAttachStat> statData = CustomAttachStatTable.get().getArmorDatas();
            if (statData.isEmpty()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (tgItem.get_card_use() == 1) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage("裝備狀態無法升級"));
                return;
            }
            if (tgItem.isEquipped()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage("裝備狀態無法升級"));
                return;
            }

            // 刪除卷軸
            pc.getInventory().removeItem(item, 1);
            boolean successed = false;
            while(!successed) {
                for (final Map.Entry<Integer, CustomAttachStatTable.CustomAttachStat> stat : statData.entrySet()) {
                    if (rand(0, 999999) < stat.getValue().get機率()) {
                        tgItem.setAttachIndex(stat.getKey());
                        pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
                        pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
                        pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
                        pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_ATTACH_INDEX);
                        successed = true;
                        break;
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}