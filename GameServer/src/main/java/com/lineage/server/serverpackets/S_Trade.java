package com.lineage.server.serverpackets;

/**
 * 交易封包<br>
 * 類名稱：S_Trade<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 上午11:06:36<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Trade extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 交易封包
	 * @param name
	 */
	public S_Trade(final String name) {
		this.writeC(S_OPCODE_TRADE);
		this.writeS(name);
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
