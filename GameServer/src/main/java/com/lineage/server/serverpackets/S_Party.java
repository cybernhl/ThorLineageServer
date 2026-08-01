package com.lineage.server.serverpackets;

/**
 * 隊伍名單<br>
 * 類名稱：S_Party<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 上午10:11:55<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Party extends ServerBasePacket {

	private byte[] _byte = null;

	public S_Party(final String htmlid, final int objid) {
		this.buildPacket(htmlid, objid, "", "", 0);
	}

	public S_Party(final String htmlid, final int objid, final String partyname,
			final String partymembers) {

		this.buildPacket(htmlid, objid, partyname, partymembers, 0);
	}

	private void buildPacket(final String htmlid, final int objid, final String partyname,
			final String partymembers, final int type) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS(htmlid);
		this.writeC(type);
		this.writeH(0x02);
		this.writeS(partyname);
		this.writeS(partymembers);
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
