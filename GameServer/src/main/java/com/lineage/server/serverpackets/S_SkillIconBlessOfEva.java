package com.lineage.server.serverpackets;

/**
 * 魔法效果:水底呼吸<br>
 * 類名稱：S_SkillIconBlessOfEva<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:11:29<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SkillIconBlessOfEva extends ServerBasePacket {

	/**
	 * 魔法效果:水底呼吸
	 * @param objectId
	 * @param time
	 */
	public S_SkillIconBlessOfEva(final int objectId, final int time) {
		this.writeC(S_OPCODE_BLESSOFEVA);
		this.writeD(objectId);
		this.writeH(time);
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
	}
}
