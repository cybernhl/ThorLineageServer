package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 交易增加物品<br>
 * 類名稱：S_TradeAddItem<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 上午11:12:00<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_TradeAddItem extends ServerBasePacket {

	/**
	 * 交易增加物品
	 * @param item
	 * @param count
	 * @param type
	 */
	public S_TradeAddItem(final L1ItemInstance item, final long count, final int type) {
		this.writeC(S_OPCODE_TRADEADDITEM);
		this.writeC(type); // 0:交易視窗上半部 1:交易視窗下半部 
		this.writeH(item.getItem().getGfxId());

		String name = item.getNumberedViewName(count);

		this.writeS(name);
		// 0:祝福 
		// 1:通常
		// 2:詛咒 
		// 3:未鑒定
		// 128:祝福&封印 
		// 129:&封印
		// 130:詛咒&封印
		// 131:未鑒定&封印
		this.writeC(item.getBless());

		if (item.isIdentified()) {
			final byte[] status = item.getStatusBytes();
			this.writeC(status.length);
			for (final byte b : status) {
				this.writeC(b);
			}
		} else {
			// 未鑒定 不發送詳細資料
			this.writeC(0x00);
		}
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
