package com.lineage.server.serverpackets;

/**
 * 
 * 類名稱：S_UseMap<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:21:45<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_UseMap extends ServerBasePacket {

	private byte[] _byte = null;

	public S_UseMap(final int item, final int mapid) {
		this.buildPacket(item, mapid);
	}

	private void buildPacket(final int item, final int mapid) {
		this.writeC(S_OPCODE_USEMAP);
		this.writeD(item);
		this.writeC(mapid);
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
