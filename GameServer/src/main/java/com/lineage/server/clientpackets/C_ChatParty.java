package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1ChatParty;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Party;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求隊伍對話控制
 *
 * @author daien
 *
 */
public class C_ChatParty extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChatParty.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (decrypt.length > 108) {
				_log.warn("人物:" + pc.getName() + "對話(隊伍)長度超過限制:" + client.getIp().toString());
				client.set_error(client.get_error() + 1);
				return;
			}

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int type = this.readC();
			switch (type) {
			case 0:// /chatbanish
				final String name = this.readS();

				if (!pc.isInChatParty()) {
					// 425 您並沒有參加任何隊伍。
					pc.sendPackets(new S_ServerMessage(425));
					return;
				}
				
				if (!pc.getChatParty().isLeader(pc)) {
					// 427 只有領導者才有驅逐隊伍成員的權力。
					pc.sendPackets(new S_ServerMessage(427));
					return;
				}
				
				final L1PcInstance targetPc = World.get().getPlayer(name);
				if (targetPc == null) {
					// 109 沒有叫%0的人。
					pc.sendPackets(new S_ServerMessage(109));
					return;
				}
				
				if (pc.getId() == targetPc.getId()) {
					return;
				}

				for (final L1PcInstance member : pc.getChatParty().getMembers()) {
					if (member.getName().toLowerCase().equals(name.toLowerCase())) {
						pc.getChatParty().kickMember(member);
						return;
					}
				}
				// 426 %0%d 不屬於任何隊伍。
				pc.sendPackets(new S_ServerMessage(426, name));
				break;

			case 1: // /chatoutparty
				if (pc.isInChatParty()) {
					pc.getChatParty().leaveMember(pc);
				}
				break;

			case 2: // /chatparty
				final L1ChatParty chatParty = pc.getChatParty();
				if (pc.isInChatParty()) {
					pc.sendPackets(new S_Party("party", pc.getId(),
							chatParty.getLeader().getName(),
							chatParty.getMembersNameList()));
					
				} else {
					// 425 您並沒有參加任何隊伍。
					pc.sendPackets(new S_ServerMessage(425));
				}
				break;
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
