package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 一般頻道<br>
 * 類名稱：S_Chat<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:55:06<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class S_Chat extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 一般頻道
	 * @param pc
	 * @param chat
	 */
	public S_Chat(final L1PcInstance pc, final String chat) {
		this.buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		this.writeC(S_OPCODE_NORMALCHAT);
		this.writeC(0x00);
		this.writeD(pc.isInvisble()? 0 : pc.getId());
		this.writeS(pc.getName() + ": " + chat);
	}
	
	/**
	 * NPC對話輸出
	 * @param npc
	 * @param chat
	 */
	public S_Chat(final L1NpcInstance npc, final String chat) {
		this.writeC(S_OPCODE_NORMALCHAT);
		this.writeC(0x00);
		this.writeD(npc.isInvisble()? 0 : npc.getId());
		this.writeS(npc.getNameId() + ": " + chat);
	}
	
	/**
	 * 墳墓對話輸出
	 * @param npc
	 * @param chat
	 */
	public S_Chat(final L1PcInstance pc) {
		this.writeC(S_OPCODE_NORMALCHAT);
		this.writeC(0x00);
		this.writeD(pc.isInvisble()? 0 : pc.getId());
		this.writeS(pc.getName() + " 的墳墓.");
	}

	public S_Chat(L1Object object, String chat, int x) {
		this.writeC(S_OPCODE_NORMALCHAT);
		this.writeC(0x00);
		this.writeD(object.getId());
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