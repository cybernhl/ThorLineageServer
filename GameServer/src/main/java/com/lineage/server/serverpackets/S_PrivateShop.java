package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.world.World;

/**
 * 交易個人商店清單(購買)
 * @author dexc
 *
 */
public class S_PrivateShop extends ServerBasePacket {

	public S_PrivateShop(final L1PcInstance pc, final int objectId, final int type) {
		final L1Object shopObj = World.get().findObject(objectId);
		if (shopObj instanceof L1PcInstance) {
			isPc(pc, objectId, type);
		}
	}

	/**
	 * 對象是PC
	 * @param pc
	 * @param objectId
	 * @param type
	 */
	private void isPc(final L1PcInstance pc, final int objectId, final int type) {
		final L1PcInstance shopPc = (L1PcInstance) World.get().findObject(objectId);

		if (shopPc == null) {
			return;
		}
		System.out.println("未知封包：" + this.getType());
		//this.writeC(S_OPCODE_PRIVATESHOPLIST);
		this.writeC(type);
		this.writeD(objectId);

		if (type == 0) {// 賣出物品
			final ArrayList<?> list = shopPc.getSellList();
			
			if (list.isEmpty()) {
				this.writeH(0x0000);
				return;
			}
			
			final int size = list.size();
			
			if (size <= 0) {
				this.writeH(0x0000);
				return;
			}
			
			pc.setPartnersPrivateShopItemCount(size);
			
			this.writeH(size);
			for (int i = 0; i < size; i++) {
				final L1PrivateShopSellList pssl = (L1PrivateShopSellList) list.get(i);
				final int itemObjectId = pssl.getItemObjectId();
				final int count = pssl.getSellTotalCount() - pssl.getSellCount();
				final int price = pssl.getSellPrice();
				final L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
				if (item != null) {
					this.writeC(i);
					this.writeC(item.getBless());
					this.writeH(item.getItem().getGfxId());
					this.writeD(count);
					this.writeD(price);

					String name = item.getNumberedViewName(count);

					this.writeS(name);
					this.writeC(0x00);

					/*final L1ItemStatus itemInfo = new L1ItemStatus(item);
					// 取回物品資訊
					final byte[] status = itemInfo.getStatusBytes().getBytes();
					this.writeC(status.length);
					for (final byte b : status) {
						this.writeC(b);
					}*/
				}
			}
			
		} else if (type == 1) {// 回收物品
			final ArrayList<?> list = shopPc.getBuyList();
			
			if (list.isEmpty()) {
				this.writeH(0x0000);
				return;
			}
			
			final int size = list.size();
			
			if (size <= 0) {
				this.writeH(0x0000);
				return;
			}
			
			this.writeH(size);
			for (int i = 0; i < size; i++) {
				final L1PrivateShopBuyList psbl = (L1PrivateShopBuyList) list.get(i);
				final int itemObjectId = psbl.getItemObjectId();
				final int count = psbl.getBuyTotalCount();
				final int price = psbl.getBuyPrice();
				final L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
				for (final L1ItemInstance pcItem : pc.getInventory().getItems()) {
					if ((item.getItemId() == pcItem.getItemId())
							&& (item.getEnchantLevel() == pcItem.getEnchantLevel())) {
						this.writeC(i);
						this.writeD(pcItem.getId());
						this.writeD(count);
						this.writeD(price);
					}
				}
			}
		}
	}
	
	@Override
	public byte[] getContent() {
		return this.getBytes();
	}
}
