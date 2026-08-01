package com.lineage.server.serverpackets;

import com.lineage.data.npc.shop.CustomPlayerShopByNpc;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.List;
import java.util.Map;

public class S_PcShopSellList extends ServerBasePacket {
    private byte[] _byte = null;
    public S_PcShopSellList(final int objid, final Map<Integer, CustomPlayerShopByNpc.CustomPlayerShopItem> items, boolean self) {
        this.writeC(S_OPCODE_SHOWSHOPBUYLIST);

        this.writeD(objid);

        if (items.size() <= 0) {
            this.writeH(0x0000);
            this.writeH(0x17d4); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
            return;
        }

        this.writeH(items.size());

        int i = 0;
        for (final Map.Entry<Integer, CustomPlayerShopByNpc.CustomPlayerShopItem> cItem : items.entrySet()) {
            i++;
            L1ItemInstance item = cItem.getValue().getItem();
            this.writeD(cItem.getKey());// 排序編號
            this.writeH(item.getItem().getGfxId());
            this.writeD(self ? 0 : cItem.getValue().getPrice());

            this.writeS(item.getViewName());

            // 取回物品詳細資訊
            final byte[] status = item.getStatusBytes();
            this.writeC(status.length);
            for (final byte b : status) {
                this.writeC(b);
            }
        }

        this.writeH(0x17d4); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
    }
    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
