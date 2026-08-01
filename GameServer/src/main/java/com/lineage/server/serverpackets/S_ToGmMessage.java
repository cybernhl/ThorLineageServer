package com.lineage.server.serverpackets;

/**
 * 給GM的訊息<br>
 * 類名稱：S_ToGmMessage<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:34:56<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ToGmMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 給GM的訊息
	 * @param mode
	 */
	public S_ToGmMessage(final String info) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(0x00);// 一般頻道
		this.writeD(0x00000000);
		this.writeS("\\fY" + info);
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
