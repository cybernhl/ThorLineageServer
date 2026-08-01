package com.lineage.server.serverpackets;

/**
 * 產生動畫(物件)<br>
 * 類名稱：S_SkillSound<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午11:03:39<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SkillSound extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 產生動畫(物件)
	 * @param objid
	 * @param gfxid
	 */
	public S_SkillSound(final int objid, final int gfxid) {
		this.buildPacket(objid, gfxid);
	}

	private void buildPacket(final int objid, final int gfxid) {
		this.writeC(S_OPCODE_SKILLSOUNDGFX);
		this.writeD(objid);
		this.writeH(gfxid);
	}

	/**
	 * 產生動畫(物件)<br>
	 * 屬性武器特殊攻擊 施展魔法封印(封印時間:S秒)
	 * @param objid
	 * @param gfxid
	 * @param time
	 */
	public S_SkillSound(final int objid, final int gfxid, final int time) {
		this.writeC(S_OPCODE_SKILLSOUNDGFX);
		this.writeD(objid);
		this.writeH(gfxid);
		this.writeH(time);
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
