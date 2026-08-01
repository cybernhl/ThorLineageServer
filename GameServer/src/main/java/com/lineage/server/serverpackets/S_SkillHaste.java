package com.lineage.server.serverpackets;

/**
 * 魔法效果:加速纇<br>
 * 類名稱：S_SkillHaste<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午3:10:08<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class S_SkillHaste extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 魔法效果:加速纇
	 * @param objid 對像objid
	 * @param mode 效果 <br>
	 * 0:正常<br>
	 * 1:加速<br>
	 * 2:減速<br>
	 * @param time 時間
	 */
	public S_SkillHaste(final int objid, final int mode, final int time) {
		this.writeC(S_OPCODE_SKILLHASTE);
		this.writeD(objid);
		this.writeC(mode);
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
