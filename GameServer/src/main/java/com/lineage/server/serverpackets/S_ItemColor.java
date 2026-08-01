package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品色彩狀態<br>
 * 類名稱：S_ItemColor<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:05:42<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ItemColor extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品色彩狀態
	 */
	public S_ItemColor(final L1ItemInstance item) {
		if (item == null) {
			return;
		}
		this.buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		this.writeC(S_OPCODE_ITEMCOLOR);
		this.writeD(item.getId());
		// 0:祝福 1:通常 2:咒 3:未鑒定
		// 128:祝福&封印 129:&封印 130:咒&封印 131:未鑒定&封印
		this.writeC(item.getBless());
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
