package com.lineage.server.serverpackets;

/**
 * 關閉對話窗<br>
 * 類名稱：S_CloseList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月6日 下午12:49:02<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CloseList extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 關閉對話窗
	 * @param objid
	 */
	public S_CloseList(final int objid) {
		this.writeC(S_OPCODE_SHOWHTML);
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
