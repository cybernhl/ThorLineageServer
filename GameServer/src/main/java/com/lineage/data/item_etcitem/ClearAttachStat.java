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

public class ClearAttachStat extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ClearAttachStat.class);

    private static  final Random _random = new Random();

    public ClearAttachStat() { }

    public static ItemExecutor get() {
        return new ClearAttachStat();
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
            if (tgItem.getAttachIndex() <= 0) {
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
            tgItem.setAttachIndex(0);
            pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_ATTACH_INDEX);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}