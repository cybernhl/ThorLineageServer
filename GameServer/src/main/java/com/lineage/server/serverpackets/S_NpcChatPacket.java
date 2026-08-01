/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1NpcInstance;


public class S_NpcChatPacket extends ServerBasePacket {

	private byte[] _byte = null;

	public S_NpcChatPacket(L1NpcInstance npc, String chat, int type) {
		buildPacket(npc, chat, type);
	}

	private void buildPacket(L1NpcInstance npc, String chat, int type) {
		switch (type) {
		case 0: // normal chat
			writeC(S_OPCODE_NPCSHOUT); // Key is 16 , can use
												// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
			writeS(npc.getName() + ": " + chat);
			break;

		case 2: // shout
			writeC(S_OPCODE_NPCSHOUT); // Key is 16 , can use
												// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
			writeS(npc.getName() + ": " + chat);
			break;

		case 3: // world chat
			writeC(S_OPCODE_NPCSHOUT);
			writeC(type); // XXX 白色になる
			writeD(npc.getId());
			writeS("[" + npc.getName() + "] " + chat);
			break;
			
		case 4: // 欧林副本使用的TYPE
            writeC(S_OPCODE_NPCSHOUT);
            writeC(0x00);
            writeD(npc.getId());
            writeS(chat);
            break;
			
		default:
			break;
		}
	}
	
	/**
	 * NPC属性资讯
	 * @param object
	 * @param chat
	 * @param x
	 */
	public S_NpcChatPacket(L1Object object, String chat, int x) {
		this.writeC(S_OPCODE_NORMALCHAT);
		this.writeC(0x00);
		this.writeD(object.getId());
		this.writeS(chat);
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
		return "[S] " + this.getClass().getSimpleName();
	}
}
