package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 密語交談(接收)頻道<br>
 * 類名稱：S_ChatWhisperFrom<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午6:00:03<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChatWhisperFrom extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 密語交談(接收)頻道
	 * @param pc
	 * @param chat
	 */
	public S_ChatWhisperFrom(final L1PcInstance pc, final String chat) {
		this.buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_WHISPERCHAT);
		this.writeS(pc.getName());
		this.writeS(chat);
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