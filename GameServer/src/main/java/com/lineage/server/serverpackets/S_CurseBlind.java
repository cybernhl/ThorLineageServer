package com.lineage.server.serverpackets;

/**
 * 魔法效果:暗盲咒術<br>
 * 類名稱：S_CurseBlind<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午3:16:21<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CurseBlind extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:暗盲咒術
	 * @param type 0:OFF 1:自己 2:周邊物件可見
	 */
	public S_CurseBlind(final int type) {
		this.buildPacket(type);
	}

	private void buildPacket(final int type) {
		this.writeC(S_OPCODE_CURSEBLIND);
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
