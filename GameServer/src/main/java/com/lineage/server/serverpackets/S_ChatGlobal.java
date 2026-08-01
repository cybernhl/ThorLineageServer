package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 廣播頻道<br>
 * 類名稱：S_ChatGlobal<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:29:51<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChatGlobal extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 廣播頻道
	 * @param pc
	 * @param chat
	 */
	public S_ChatGlobal(final L1PcInstance pc, final String chat) {
		buildPacket(pc, chat);
	}

	private void buildPacket(final L1PcInstance pc, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x03);
		writeS(pc.isGm()? "[******] " + chat : "[" + pc.getName() + "] " + chat);
	}
	
	/**
	 * NPC對話輸出
	 * @param npc
	 * @param chat
	 */
	public S_ChatGlobal(final L1NpcInstance npc, final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x03);
		writeS("[" + npc.getNameId() + "] " + chat);
	}

	/**
	 * 共用廣播頻道
	 * @param pc
	 * @param chat
	 */
	public S_ChatGlobal(final String chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x03);
		writeS(chat);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}