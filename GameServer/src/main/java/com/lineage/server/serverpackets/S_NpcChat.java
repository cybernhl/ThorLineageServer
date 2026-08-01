package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * NPC 一般頻道<br>
 * 類名稱：S_NpcChat<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:33:45<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_NpcChat extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC 一般頻道
	 * @param npc NPC
	 * @param chat 對話內容
	 */
	public S_NpcChat(final L1NpcInstance npc, final String chat) {
		this.writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		this.writeC(0x00); // Color
		this.writeD(npc.getId());
		this.writeS(npc.getNameId() + ": " + chat);
	}
	
	/**
	 * NPC 一般頻道
	 * @param npc NPC
	 * @param chat 對話內容
	 * @param name 是否顯示名稱 true:是 false:不是
	 */
	public S_NpcChat(final L1NpcInstance npc, final String chat, boolean name) {
		this.writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		this.writeC(0x00); // Color
		this.writeD(npc.getId());
		this.writeS((name? npc.getNameId() + ": ":"") + chat);
	}
	
	/**
	 * PC對話輸出(使用NPC頻道)
	 * @param pc
	 * @param chat
	 */
	public S_NpcChat(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_NPCSHOUT);
		// desc-?.tbl
		this.writeC(0x00); // Color
		this.writeD(pc.getId());
		this.writeS(pc.getName() + ": " + chat);
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
