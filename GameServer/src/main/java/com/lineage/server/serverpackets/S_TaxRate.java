package com.lineage.server.serverpackets;

/**
 * 稅收設定<br>
 * 類名稱：S_TaxRate<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 下午2:31:20<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_TaxRate extends ServerBasePacket {

	private byte[] _byte = null;

	public S_TaxRate(final int objecId) {
		this.writeC(S_OPCODE_TAXRATE);
		this.writeD(objecId);
		this.writeC(0x0a); //10 10%~50%
		this.writeC(0x32); // 50
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
