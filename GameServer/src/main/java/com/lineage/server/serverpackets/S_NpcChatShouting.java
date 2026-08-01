package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * NPC 大喊頻道<br>
 * 類名稱：S_NpcChatShouting<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:34:11<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_NpcChatShouting extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC 大喊頻道
	 * @param npc
	 * @param chat
	 */
	public S_NpcChatShouting(final L1NpcInstance npc, final String chat) {
		this.buildPacket(npc, chat);
	}

	private void buildPacket(final L1NpcInstance npc, final String chat) {
		this.writeC(S_OPCODE_NPCSHOUT); // Key is 16 , can use
		// desc-?.tbl
		this.writeC(0x02); // Color
		this.writeD(npc.getId());
		this.writeS("<" + npc.getNameId() + "> " + chat);
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
