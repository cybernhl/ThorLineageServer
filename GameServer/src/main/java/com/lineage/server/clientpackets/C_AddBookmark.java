package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求增加記憶座標
 *
 * @author dexc
 *
 */
public class C_AddBookmark extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AddBookmark.class);

	public C_AddBookmark(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

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

			final String locName = this.readS();

			if (pc.getMap().isMarkable() || pc.isGm()) {
				if ((L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())
						|| L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId()))) {
					// \f1這個地點不能夠標記。
					pc.sendPackets(new S_ServerMessage(214));

				} else {
					if (L1TownLocation.isGambling(pc)) {
						// \f1這個地點不能夠標記。
						pc.sendPackets(new S_ServerMessage(214));
						return;
					}
					CharBookReading.get().addBookmark(pc, locName);
				}

			} else {
				// \f1這個地點不能夠標記。
				pc.sendPackets(new S_ServerMessage(214));
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
