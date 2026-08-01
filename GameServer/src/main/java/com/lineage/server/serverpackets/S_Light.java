package com.lineage.server.serverpackets;

/**
 * 更新物件亮度<br>
 * 類名稱：S_Light<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 上午10:12:02<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Light extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物件亮度
	 * @param objid
	 * @param type
	 */
	public S_Light(final int objid, final int type) {
		this.buildPacket(objid, type);
	}

	private void buildPacket(final int objid, final int type) {
		this.writeC(S_OPCODE_LIGHT);
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
