package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;

/**
 * 特殊商店(NC助理)<br>
 * 天堂助理<br>
 * 類名稱：S_ShopSellListCnX<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午2:28:13<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ShopSellListCnX extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopSellListCnX(final L1PcInstance pc, final int tgObjid) {
		this.buildPacket(pc, tgObjid);
	}

	private void buildPacket(final L1PcInstance pc, final int tgObjid) {
		this.writeC(S_OPCODE_SHOWSHOPBUYLIST);
		
		this.writeD(tgObjid);

		final ArrayList<L1ShopItem> shopItems = ShopCnTable.get().get(1);

		if (shopItems.size() <= 0) {
			this.writeH(0x0000);
			return;
		}

		this.writeH(shopItems.size());

		int i = 0;
		for (final L1ShopItem shopItem : shopItems) {
			i++;
			pc.get_otherList().add_cnList(shopItem, i);
			
			final L1Item item = shopItem.getItem();
			
			this.writeD(i);
			this.writeH(item.getGfxId());
			this.writeD(shopItem.getPrice());

			if (shopItem.getPackCount() > 1) {
				this.writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");
				
			} else {
				this.writeS(item.getNameId());
			}

			final L1ItemStatus itemInfo = new L1ItemStatus(item);
			// 取回物品資訊
			final byte[] status = itemInfo.getStatusBytes().getBytes();
			this.writeC(status.length);
			for (final byte b : status) {
				this.writeC(b);
			}
			// 降低封包量 不傳送詳細資訊
			//this.writeC(0x00);
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
