package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.ArrayList;
import java.util.List;

public class S_PcShopBuyList extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PcShopBuyList(final int objid, final L1PcInstance pc) {
        final List<L1ItemInstance> items = pc.getInventory().getItems();
        final List<L1ItemInstance> packItems = new ArrayList<>();
        for (final L1ItemInstance item : items) {
            if (item.isIdentified() && item.getItem().isTradable()) {
                packItems.add(item);
            }
        }
        this.writeC(S_OPCODE_SHOWSHOPSELLLIST);
        this.writeD(objid);
        this.writeH(packItems.size());
        for (final L1ItemInstance item : packItems) {
            this.writeD(item.getId());
            this.writeD(0);
        }
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
