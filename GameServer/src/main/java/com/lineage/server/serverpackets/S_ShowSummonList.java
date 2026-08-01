package com.lineage.server.serverpackets;

/**
 * 召喚術清單<br>
 * 類名稱：S_ShowSummonList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午3:28:42<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ShowSummonList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShowSummonList(final int objid) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS("summonlist");
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
