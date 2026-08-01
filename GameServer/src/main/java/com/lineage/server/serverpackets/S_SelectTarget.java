package com.lineage.server.serverpackets;

/**
 * 選擇一個目標<br>
 * 類名稱：S_SelectTarget<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午10:25:09<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SelectTarget extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選擇一個目標
	 * @param ObjectId
	 */
	public S_SelectTarget(final int ObjectId) {
		this.writeC(S_OPCODE_SELECTTARGET);
		this.writeD(ObjectId);
		this.writeC(0x00);
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
