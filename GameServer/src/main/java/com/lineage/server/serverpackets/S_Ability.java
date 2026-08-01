package com.lineage.server.serverpackets;

/**
 * 戒指(傳戒、夜視頭、召戒)<br>
 * 類名稱：S_Ability<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:49:30<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Ability extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 戒指
	 * @param type
	 * @param equipped
	 */
	public S_Ability(final int type, final boolean equipped) {
		this.buildPacket(type, equipped);
	}

	private void buildPacket(final int type, final boolean equipped) {
		this.writeC(S_OPCODE_ABILITY);
		this.writeC(type); // 1:ROTC 5:ROSC
		this.writeC(equipped? 0x01 : 0x00);
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
