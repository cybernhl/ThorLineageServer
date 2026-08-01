package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.shop.L1AssessedItem;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.world.World;

/**
 * 賣<br>
 * 類名稱：S_ShopBuyList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午2:36:06<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ShopBuyList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopBuyList(final int objid, final L1PcInstance pc) {
		final L1Object object = World.get().findObject(objid);
		if (!(object instanceof L1NpcInstance)) {
			return;
		}
		final L1NpcInstance npc = (L1NpcInstance) object;
		final int npcId = npc.getNpcTemplate().get_npcId();
		final L1Shop shop = ShopTable.get().get(npcId);
		if (shop == null) {
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		final List<L1AssessedItem> assessedItems = shop.assessItems(pc.getInventory());

		if (assessedItems.isEmpty()) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			
		}

		if (assessedItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
		
		}

		this.writeC(S_OPCODE_SHOWSHOPSELLLIST);
		this.writeD(objid);

		this.writeH(assessedItems.size());

		for (final L1AssessedItem item : assessedItems) {
			this.writeD(item.getTargetId());
			this.writeD(item.getAssessedPrice());
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