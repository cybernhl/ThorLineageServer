package com.lineage.server.serverpackets;

/**
 * 能力質選取資料<br>
 * 類名稱：S_Bonusstats<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月6日 下午12:48:39<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Bonusstats extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 能力質選取資料
	 * @param objid
	 */
	public S_Bonusstats(final int objid) {
		this.buildPacket(objid);
	}

	private void buildPacket(final int objid) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS("RaiseAttr");
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
