package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * NPC 廣播頻道<br>
 * 類名稱：S_NpcChatGlobal<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:33:59<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_NpcChatGlobal extends ServerBasePacket {

	private byte[] _byte = null;

	public S_NpcChatGlobal(final L1NpcInstance npc, final String chat) {
		this.buildPacket(npc, chat);
	}

	private void buildPacket(final L1NpcInstance npc, final String chat) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(0x03); // XXX 白色
		this.writeD(npc.getId());
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
