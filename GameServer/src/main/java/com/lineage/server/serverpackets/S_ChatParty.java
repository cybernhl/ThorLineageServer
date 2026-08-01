package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 隊伍頻道<br>
 * 類名稱：S_ChatParty<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:34:10<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChatParty extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 隊伍頻道
	 * @param pc
	 * @param chat
	 */
	public S_ChatParty(final L1PcInstance pc, final String chat) {
		this.buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(0x0b);
		this.writeS("(" + pc.getName() + ") " + chat);
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