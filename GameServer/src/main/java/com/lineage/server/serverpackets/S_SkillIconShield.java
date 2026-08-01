package com.lineage.server.serverpackets;

/**
 * 魔法效果:防禦<br>
 * 類名稱：S_SkillIconShield<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:07:43<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SkillIconShield extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:防禦
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_SkillIconShield(final int type, final int time) {
		this.writeC(S_OPCODE_SKILLICONSHIELD);
		this.writeH(time);
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
