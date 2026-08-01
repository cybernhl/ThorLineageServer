package com.lineage.server.clientpackets;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_DoActionShop;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;

/**
 * 要求開設個人商店
 *
 * @author daien
 *
 */
public class C_Shop extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Shop.class);

	public C_Shop(final byte[] decrypt, final ClientExecutor client) {
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

			final int mapId = pc.getMapId();
			if ((mapId != 340) && (mapId != 350) && (mapId != 360) && (mapId != 370)) {
				// 876 無法在此開設個人商店。
				pc.sendPackets(new S_ServerMessage(876));
				return;
			}

			final ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
			final ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
			L1ItemInstance checkItem;
			boolean tradable = true;

			final int type = this.readC();
			if (type == 0) { // 開始
				final int sellTotalCount = this.readH();// 出售道具
				for (int i = 0; i < sellTotalCount; i++) {
					final int sellObjectId = this.readD();
					final int sellPrice = Math.max(0, this.readD());
					if (sellPrice <= 0) {
						_log.error("要求開設個人商店傳回金幣小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					final int sellCount = Math.max(0, this.readD());
					if (sellCount <= 0) {
						_log.error("要求開設個人商店傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					// 取引可能
					checkItem = pc.getInventory().getItem(sellObjectId);
					if (!checkItem.getItem().isTradable()) {
						tradable = false;
						// 1497 此道具無法在[個人商店]上販售。
						pc.sendPackets(new S_ServerMessage(1497));
					}

					if (checkItem.get_time() != null) {
						// 1497 此道具無法在[個人商店]上販售。
						pc.sendPackets(new S_ServerMessage(1497));
						tradable = false;
					}
					
					if (checkItem.isEquipped()) {
						// \f1你不能夠將轉移已經裝備的物品。
						pc.sendPackets(new S_ServerMessage(141));
						return;
					}
					
					// 取回寵物列表
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								// 1,187：寵物項鏈正在使用中。  
								pc.sendPackets(new S_ServerMessage(1187));
								return;
							}
						}
					}

					// 取回娃娃
					if (pc.getDoll(checkItem.getId()) != null) {
						// 1,181：這個魔法娃娃目前正在使用中。  
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}

					final L1PrivateShopSellList pssl = new L1PrivateShopSellList();
					pssl.setItemObjectId(sellObjectId);
					pssl.setSellPrice(sellPrice);
					pssl.setSellTotalCount(sellCount);
					sellList.add(pssl);
				}

				final int buyTotalCount = this.readH();// 買入道具
				for (int i = 0; i < buyTotalCount; i++) {
					final int buyObjectId = this.readD();
					final int buyPrice = Math.max(0, this.readD());
					if (buyPrice <= 0) {
						_log.error("要求買入道具傳回金幣小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					final int buyCount = Math.max(0, this.readD());
					if (buyCount <= 0) {
						_log.error("要求買入道具傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					// 取引可能
					checkItem = pc.getInventory().getItem(buyObjectId);

					if (checkItem.getCount() <= 0) {
						continue;
					}

					if (!checkItem.getItem().isTradable()) {
						tradable = false;
						// 1497 此道具無法在[個人商店]上販售
						pc.sendPackets(new S_ServerMessage(1497));
					}

					if (checkItem.getBless() >= 128) { // 封印的裝備
						// 1497 此道具無法在[個人商店]上販售
						pc.sendPackets(new S_ServerMessage(1497));
						return;
					}
					
					if (checkItem.isEquipped()) {
						// \f1你不能夠將轉移已經裝備的物品。
						pc.sendPackets(new S_ServerMessage(141));
						return;
					}
					
					// 取回寵物列表
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								// 1,187：寵物項鏈正在使用中。  
								pc.sendPackets(new S_ServerMessage(1187));
								return;
							}
						}
					}

					// 取回娃娃
					if (pc.getDoll(checkItem.getId()) != null) {
						// 1,181：這個魔法娃娃目前正在使用中。  
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
					
					final L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
					psbl.setItemObjectId(buyObjectId);
					psbl.setBuyPrice(buyPrice);
					psbl.setBuyTotalCount(buyCount);
					buyList.add(psbl);
				}
				if (!tradable) { // 取引不可能含場合、個人商店終了
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
					return;
				}

				byte[] chat = this.readByte();
				pc.setShopChat(chat);
				pc.setPrivateShop(true);
				pc.sendPacketsAll(new S_DoActionShop(pc.getId(), chat));

			} else if (type == 1) { // 終了
				sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
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
