package com.lineage.server.serverpackets;

/**
 * 物件隱形<br>
 * 類名稱：S_Invis<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午5:41:57<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Invis extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件隱形
	 * @param objid
	 * @param type 0:無 1:隱形
	 */
	public S_Invis(final int objid, final int type) {
		this.buildPacket(objid, type);
	}

	private void buildPacket(final int objid, final int type) {
		this.writeC(S_OPCODE_INVIS);
		this.writeD(objid);
		this.writeC(type);
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
