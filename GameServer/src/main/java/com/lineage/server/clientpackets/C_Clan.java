package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Emblem;
import com.lineage.server.templates.L1EmblemIcon;

/**
 * 要求更新盟輝
 * @author DaiEn
 *
 */
public class C_Clan extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Clan.class);

	public C_Clan(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isDead()) { // 死亡
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}
			
			final int clanId = this.readD();

			final L1Clan clan = ClanReading.get().getTemplate(clanId);
			
			if (clan == null) {
				return;
			}

			final L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan.getClanId());

			if (emblemIcon != null) {
				pc.sendPackets(new S_Emblem(emblemIcon));
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
