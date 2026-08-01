package com.lineage.server.serverpackets;

/**
 * 宣告進入遊戲<br>
 * 類名稱：S_EnterGame<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:56:24<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_EnterGame extends ServerBasePacket {

	private byte[] _byte = null;

	public S_EnterGame() {
		this.writeC(S_OPCODE_UNKNOWN1);
		this.writeC(0x03);
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
