package com.lineage.server.serverpackets;

/**
 * 寶物公告<br>
 * 類名稱：S_BoxMessage<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:32:58<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_BoxMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 寶物公告
	 * @param msg
	 */
	public S_BoxMessage(final String msg) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(0x00);// 顏色
		this.writeD(0x00000000);
		this.writeS(msg);
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
