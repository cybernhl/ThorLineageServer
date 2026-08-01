package com.lineage.server.clientpackets;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 要求踢出隊伍
 *
 * @author dexc
 *
 */
public class C_BanParty extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_BanParty.class);

	public C_BanParty(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (!pc.getParty().isLeader(pc)) {
				// 只有領導者才有驅逐隊伍成員的權力。
				pc.sendPackets(new S_ServerMessage(427));
				return;
			}

			final String userName = this.readS();

			final ConcurrentHashMap<Integer, L1PcInstance> pcs = pc.getParty().partyUsers();
			
			final L1PcInstance tgpc = World.get().getPlayer(userName);
			
			if (tgpc.getId() == pc.getId()) {
				pc.sendPackets(new S_SystemMessage("無法驅離自己."));
				return;
			}
			
			if (pcs.isEmpty()) {
				return;
			}
			if (pcs.size() <= 0) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = pcs.values().iterator(); iter.hasNext();) {
				final L1PcInstance member = iter.next();
				final String memberName = member.getName().toLowerCase();
				if (memberName.equals(userName.toLowerCase())) {
					pc.getParty().kickMember(member);
					return;
				}
			}
			
			// %0%d 不屬於任何隊伍。
			pc.sendPackets(new S_ServerMessage(426, userName));
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
