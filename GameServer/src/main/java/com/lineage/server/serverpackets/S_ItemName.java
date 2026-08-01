package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 更新物品顯示名稱(背包)<br>
 * 類名稱：S_ItemName<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 上午10:01:48<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class S_ItemName extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物品顯示名稱(背包)
	 */
	public S_ItemName(final L1ItemInstance item) {
		if (item == null) {
			return;
		}
		this.writeC(S_OPCODE_ITEMNAME);
		this.writeD(item.getId());
		this.writeS(item.getViewName());
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
