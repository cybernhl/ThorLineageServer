package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Object;

/**
 * 物件刪除<br>
 * 類名稱：S_RemoveObject<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月28日 下午4:03:46<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_RemoveObject extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件刪除
	 * @param obj
	 */
	public S_RemoveObject(final L1Object obj) {
		this.writeC(S_OPCODE_REMOVE_OBJECT);
		this.writeD(obj.getId());
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
