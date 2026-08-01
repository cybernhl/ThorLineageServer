package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.FaceToFace;
import com.lineage.server.world.World;

/**
 * 要求交易(個人)
 *
 * @author daien
 *
 */
public class C_Trade extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Trade.class);

	public C_Trade(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			//this.read(decrypt);

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
			
			final L1PcInstance target = FaceToFace.faceToFace(pc);

			// 取回交易對像(舊的交易者 已經發起交易再次交易)
			final L1PcInstance srcTrade = 
				(L1PcInstance) World.get().findObject(pc.getTradeID());
			
			if (srcTrade != null) {  // 取消舊交易者交易狀態
				// \f1你正在與別的人交易。
				pc.sendPackets(new S_ServerMessage(258));
				final L1Trade trade = new L1Trade();
				trade.tradeCancel(srcTrade);
				return;
			}
			
			if (target != null) {
				// 取回交易對像(接收指令者 面對對像正在交易)
				final L1PcInstance srcTradetarget =
						(L1PcInstance) World.get().findObject(target.getTradeID());

				if (srcTradetarget != null) {
					// \f1他正在與別的人交易
					pc.sendPackets(new S_ServerMessage(259));

					final L1Trade trade = new L1Trade();
					trade.tradeCancel(srcTradetarget);
					return;
				}
				if (!target.isParalyzed()) {
					pc.get_trade_clear();
					target.get_trade_clear();
					
					pc.setTradeID(target.getId()); // 保存相互交易對像OBJID
					target.setTradeID(pc.getId());
					target.sendPackets(new S_Message_YN(pc.getName()));
				}
			}
			
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
