package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求給予角色血盟階級
 *
 * @author daien 未啟用
 *
 */
public class C_Rank extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Rank.class);

	public C_Rank(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			int data = 0;
			int rank = 0;
			String name = "";

			try {
				data = this.readC();
				rank = this.readC();
				name = this.readS();

			} catch (final Exception e) {
				return;
			}

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}
			
			switch (data) {
			case 1:// 階級
				this.rank(pc, rank, name, client);
				break;
				
			case 2:// 同盟目錄
			case 3:// 加入同盟
			case 4:// 退出同盟
				break;
				
			case 5:
				break;
				
			case 6:
				break;
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	private void rank(final L1PcInstance pc, final int rank, final String name, ClientExecutor client) {
		final L1PcInstance targetPc = World.get().getPlayer(name);
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}
		boolean isOK = false;
		// rank 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
		if (rank >= 2 && rank <= 10) {
			isOK = true;
		}

		if (!isOK) {
			// 2,149：\f1請輸入以下內容: "/階級 \f0角色名稱 階級[守護騎士, 修習騎士, 一般]\f1"  
			pc.sendPackets(new S_ServerMessage(2149));
			return;
		}
		if (pc.isCrown()) { // 君主
			if (pc.getId() != clan.getLeaderId()) { // 血盟主
				// 785 你不再是君主了
				pc.sendPackets(new S_ServerMessage(785));
				return;
			}
			
		} else {
			// 518 血盟君主才可使用此命令。
			pc.sendPackets(new S_ServerMessage(518));
			return;
		}

		if (targetPc != null) {
			try {
				if (pc.getClanid() == targetPc.getClanid()) {
					targetPc.setClanRank(rank);
					targetPc.save();
					targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank));

				} else {
					// 201：\f1%0%d不是你的血盟成員。
					pc.sendPackets(new S_ServerMessage(201, name));
					return;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		} else { // 線上無此人物
			try {
				final L1PcInstance restorePc = CharacterTable.get().restoreCharacter(name, client);
				if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) { // 相同血盟
					restorePc.setClanRank(rank);
					restorePc.save();

				} else {
					// 109 沒有叫%0的人。
					pc.sendPackets(new S_ServerMessage(109, name));
					return;
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
