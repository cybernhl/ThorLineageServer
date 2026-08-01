package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品刪除<br>
 * 類名稱：S_DeleteInventoryItem<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午6:15:50<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_DeleteInventoryItem extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品刪除
	 *
	 * @param item
	 */
	public S_DeleteInventoryItem(final L1ItemInstance item) {
		this.writeC(S_OPCODE_DELETEINVENTORYITEM);
		this.writeD(item.getId());
	}
	
	public S_DeleteInventoryItem(final int objid) {
		this.writeC(S_OPCODE_DELETEINVENTORYITEM);
		this.writeD(objid);
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
