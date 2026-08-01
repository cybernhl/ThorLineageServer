package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 要求完成交易(個人)
 *
 * @author daien
 *
 */
public class C_TradeOK extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_TradeOK.class);
	private static Lock lock = new ReentrantLock(false);

	public C_TradeOK(final byte[] decrypt, final ClientExecutor client) {
		lock.lock();
		try {
			// 資料載入
			//this.read(decrypt);

			final L1PcInstance player = client.getActiveChar();
			final L1PcInstance trading_partner = (L1PcInstance) World.get().findObject(player.getTradeID());
			if (trading_partner != null) {
				player.sendPackets(new S_SystemMessage("等待對方確認交易!"));
				trading_partner.sendPackets(new S_SystemMessage("對方已經確認交易!"));
				player.setTradeOk(true);

				if (player.getTradeOk() && trading_partner.getTradeOk()) {
					// (180 - 16)個未滿成立。
					// 本來重（等）既持場合考慮。
					if ((player.getInventory().getSize() < (180 - 20))
							&& (trading_partner.getInventory().getSize() < (180 - 20))) {
						final L1Trade trade = new L1Trade();
						trade.tradeOK(player);
						
					} else {
						// \f1一個角色最多可攜帶180個道具。
						player.sendPackets(new S_ServerMessage(263));
						// \f1一個角色最多可攜帶180個道具。
						trading_partner.sendPackets(new S_ServerMessage(263));
						final L1Trade trade = new L1Trade();
						trade.tradeCancel(player);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
			lock.unlock();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
