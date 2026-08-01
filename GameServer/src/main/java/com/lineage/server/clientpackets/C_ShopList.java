package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PrivateShop;

/**
 * 要求角色個人商店清單
 *
 * @author daien
 *
 */
public class C_ShopList extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ShopList.class);

	public C_ShopList(final byte[] decrypt, final ClientExecutor client) {
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

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int mapId = pc.getMapId();
			
			boolean isShopMap = false;
			if (mapId == 340) {
				isShopMap = true;
			}
			
			if (mapId == 350) {
				isShopMap = true;
			}
			
			if (mapId == 360) {
				isShopMap = true;
			}
			
			if (mapId == 370) {
				isShopMap = true;
			}
			
			if (isShopMap) {
				final int type = this.readC();
				final int objectId = this.readD();
				
				pc.sendPackets(new S_PrivateShop(pc, objectId, type));
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
