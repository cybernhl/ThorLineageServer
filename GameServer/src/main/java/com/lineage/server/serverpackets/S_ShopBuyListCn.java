package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ShopItem;

/**
 * NPC回收道具
 * 
 * @author terry0412
 * 
 */
public class S_ShopBuyListCn extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC回收道具
	 * 
	 * @param pc
	 * @param tgObjid
	 * @param npcid
	 */
	public S_ShopBuyListCn(final L1PcInstance pc, final L1NpcInstance npc) {
		this.writeC(S_OPCODE_SHOWSHOPSELLLIST);
		this.writeD(npc.getId());

		final ArrayList<L1ShopItem> shopItems = ShopCnTable.get().get(
				npc.getNpcId());
		if (shopItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		final Map<Integer, Integer> assessedItems = new HashMap<Integer, Integer>();

		//System.out.println("1.當前販售清單長度為:"+assessedItems.size());
		for (final L1ItemInstance item : pc.getInventory().getItems()) {
			switch (item.getItem().getItemId()) {
			case 40308: // 金幣
			case 44070: // 貨幣
			case 40314: // 項圈
			case 40316: // 高等寵物項圈
				continue;
			}

			if (item.isEquipped()) {// 使用中
				continue;
			}
			if (item.getBless() >= 128) {
				continue;
			}
			if (item.getLastUsed() != null) {
				continue;
			}
			if (item.getItem().cantBeSold()) {
				continue;
			}
			//System.out.println("2.當前販售清單長度為:"+assessedItems.size());
			for (final L1ShopItem shopItem : shopItems) {
				//System.out.println("準備回收道具："+shopItem.getItemId());
				if (shopItem.getItemId() == item.getItemId()) {
					if (shopItem.getPurchasingPrice()<0) {
						//System.out.println("檢測到當前物品"+item.getItemId()+"賣出價格小於0，略過");
						continue;
					}
					//System.out.println("道具itemid:"+item.getItemId()+"id："+item.getId()+"符合回收需求加入回收清單");
					pc.get_otherList().add_cnList(shopItem, item.getId());

					assessedItems.put(item.getId(),
							shopItem.getPurchasingPrice());
				}
			}
		}
		//System.out.println("3.當前販售清單長度為:"+assessedItems.size());
		if (assessedItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		this.writeH(assessedItems.size());

		for (final Entry<Integer, Integer> entrySet : assessedItems.entrySet()) {
			this.writeD(entrySet.getKey());
			this.writeD(entrySet.getValue());
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