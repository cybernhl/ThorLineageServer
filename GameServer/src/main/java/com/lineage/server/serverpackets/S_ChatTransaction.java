package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 交易頻道<br>
 * 類名稱：S_ChatTransaction<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:34:24<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChatTransaction extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 交易頻道
	 * @param pc
	 * @param chat
	 */
	public S_ChatTransaction(final L1PcInstance pc, final String chat) {
		this.buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(0x0c);
		this.writeS("[" + pc.getName() + "] " + chat);
	}

	/**
	 * NPC對話輸出
	 * @param de
	 * @param chat
	 */
	public S_ChatTransaction(L1NpcInstance npc, String chat) {
		this.writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(0x0c);
		this.writeS("[" + npc.getNameId() + "] " + chat);
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