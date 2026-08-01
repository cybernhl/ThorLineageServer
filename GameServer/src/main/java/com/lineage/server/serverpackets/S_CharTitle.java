package com.lineage.server.serverpackets;

/**
 * 角色封號<br>
 * 類名稱：S_CharTitle<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:54:30<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CharTitle extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色封號
	 * @param objid
	 * @param title
	 */
	public S_CharTitle(int objid, StringBuilder title) {
		this.writeC(S_OPCODE_CHARTITLE);
		this.writeD(objid);
		this.writeS(title.toString());
	}
	
	/**
	 * 消除角色封號
	 * @param objid
	 * @param title
	 */
	public S_CharTitle(final int objid) {
		this.writeC(S_OPCODE_CHARTITLE);
		this.writeD(objid);
		this.writeS("");
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
