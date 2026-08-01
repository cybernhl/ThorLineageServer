package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.WorldClan;

/**
 * 要求存入資金
 *
 * @author daien
 *
 */
public class C_Deposit extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Deposit.class);

	public C_Deposit(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final int objid = this.readD();
			long count = this.readD();
			if (count > Integer.MAX_VALUE) {
				count = Integer.MAX_VALUE;
			}
			count = Math.max(0, count);
			final L1PcInstance player = client.getActiveChar();
			if (objid == player.getId()) {
				final L1Clan clan = WorldClan.get().getClan(player.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主
						final L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
						synchronized (l1castle) {
							long money = l1castle.getPublicMoney();
							if (player.getInventory().consumeItem(L1ItemId.ADENA, count)) {
								money += count;
								l1castle.setPublicMoney(money);
								CastleReading.get().updateCastle(l1castle);
							}
						}
					}
				}
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
