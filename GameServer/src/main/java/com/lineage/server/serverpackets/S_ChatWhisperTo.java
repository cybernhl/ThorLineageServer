package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 密語交談(發送)頻道<br>
 * 類名稱：S_ChatWhisperTo<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:34:43<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChatWhisperTo extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ChatWhisperTo(final L1PcInstance pc, final String chat) {
		this.buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(0x09);
		this.writeS("-> (" + pc.getName() + ") " + chat);
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