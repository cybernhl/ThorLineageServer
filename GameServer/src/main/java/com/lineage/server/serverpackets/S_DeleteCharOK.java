package com.lineage.server.serverpackets;

/**
 * 角色移除(立即/非立即)<br>
 * 類名稱：S_DeleteCharOK<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午3:00:28<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_DeleteCharOK extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色移除(立即/非立即)
	 * @param type
	 */
	public S_DeleteCharOK() {
		this.writeC(S_OPCODE_DETELECHAROK);
		this.writeC(0x05);
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
