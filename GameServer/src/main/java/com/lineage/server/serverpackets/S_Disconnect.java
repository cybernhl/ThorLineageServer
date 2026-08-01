package com.lineage.server.serverpackets;

/**
 * 立即中斷連線<br>
 * 類名稱：S_Disconnect<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午12:38:44<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Disconnect extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 立即中斷連線
	 */
	public S_Disconnect() {
		this.writeC(S_OPCODE_DISCONNECT);
		this.writeC(0x0A);
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
