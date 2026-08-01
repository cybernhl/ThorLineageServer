package com.lineage.server.clientpackets;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.data.npc.shop.CustomPlayerShopByNpc;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.WorldPcShop;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.ShopXSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.ShopXTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.DwarfForClanReading;
import com.lineage.server.datatables.lock.DwarfForElfReading;
import com.lineage.server.datatables.lock.DwarfReading;
import com.lineage.server.datatables.lock.OtherUserBuyReading;
import com.lineage.server.datatables.lock.OtherUserSellReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1CnInstance;
import com.lineage.server.model.Instance.L1DwarfInstance;
import com.lineage.server.model.Instance.L1GamblingInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.model.shop.L1ShopBuyOrderList;
import com.lineage.server.model.shop.L1ShopSellOrderList;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemUpdate;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求列表物品取得
 * @author dexc
 *
 */
public class C_Result extends ClientBasePacket {

	public static final Log _log = LogFactory.getLog(C_Result.class);

	public static final Random _random = new Random();
	private static final Lock warehouse_lock = new ReentrantLock();

	public C_Result(final byte[] decrypt, final ClientExecutor client) {
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

			final int npcObjectId = this.readD();
			final int resultType = this.readC();
			final int size = this.readC();
			final int unknown = this.readC();

			/*System.out.println(npcObjectId);
			System.out.println(resultType);
			System.out.println(size);
			System.out.println(unknown);*/
			
			int npcId = 0;
			//String npcImpl = "";
			//String nameid = null;
			boolean isPrivateShop = false;

			final L1Object findObject = World.get().findObject(npcObjectId);
			final L1NpcInstance npc = WorldPcShop.get().get(npcObjectId);
			if (npc != null && npc.getShopObjectId() > -1 && npc.getNpcId() == 3067888) {
				if (resultType == 1) {
					if (size == 1) {
						CustomPlayerShopByNpc.getInstance().SellItem(pc, npcObjectId, this.readD(), Math.max(0, this.readD()));
					} else {
						pc.sendPackets(new S_HelpMessage("\\fW一次只能販售一個道具"));
					}
				} else {
					final List<Pair<Integer, Integer>> items = new ArrayList<>();
					for (int i = 0; i < size; i++) {
						items.add(new Pair<>(this.readD(), this.readD()));
					}
					CustomPlayerShopByNpc.getInstance().BuyItem(pc, npcObjectId, items);
					items.clear();
				}
				return;
			}
			if (findObject != null) {
				boolean isStop = false;
				// 對象是NPC
				if (findObject instanceof L1NpcInstance) {
					final L1NpcInstance targetNpc = (L1NpcInstance) findObject;
					npcId = targetNpc.getNpcTemplate().get_npcId();
					isStop = true;
					
				// 對象是PC
				} else if (findObject instanceof L1PcInstance) {
					isPrivateShop = true;
					isStop = true;
				}
				
				if (isStop) {
					final int diffLocX = Math.abs(pc.getX() - findObject.getX());
					final int diffLocY = Math.abs(pc.getY() - findObject.getY());
					// 距離3格以上無效
					if ((diffLocX > 3) || (diffLocY > 3)) {
						return;
					}
				}
			}
			//System.out.println("resultType:"+resultType);
			
			switch (resultType) {
			case 0:// 買入物品
				if (size > 0) {
					if (findObject instanceof L1MerchantInstance) {
						switch (npcId) {
						case 70535:// 托售管理員
							this.mode_shopS(pc, size);
							break;

						default:
							this.mode_buy(pc, npcId, size);
							break;
						}
						return;
					}

					if (findObject instanceof L1GamblingInstance) {
						this.mode_gambling(pc, npcId, size, true);
						return;
					}

					if (findObject instanceof L1CnInstance) {
						this.mode_cn(pc, size, true);
						return;
					}

					if (pc.equals(findObject)) {// 是自己
						this.mode_cn(pc, size, true);
						return;
					}

					if (findObject instanceof L1PcInstance) {
						if (isPrivateShop) {// 買入個人商店物品
							final L1PcInstance targetPc = (L1PcInstance) findObject;
							this.mode_buypc(pc, targetPc, size);
							return;
						}
					}
				}
				break;

			case 1:// 賣出物品
				if (size > 0) {
					if (findObject instanceof L1MerchantInstance) {
						switch (npcId) {
						case 99999:// 亞丁商團(垃圾回收)
							this.mode_sellall(pc, size);
							break;

						default:
							this.mode_sell(pc, npcId, size);
							break;
						}
						return;
					}
					
					if (findObject instanceof L1GamblingInstance) {
						this.mode_gambling(pc, npcId, size, false);
						return;
					}

					if (findObject instanceof L1PcInstance) {// 賣出物品給個人商店
						if (isPrivateShop) {
							final L1PcInstance targetPc = (L1PcInstance) findObject;
							this.mode_sellpc(pc, targetPc, size);
						}
					}
				}
				break;

			case 2:// 個人倉庫存入
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if (level >= 5) {
							this.mode_warehouse_in(pc, npcId, size);
						}
					}
				}
				break;

			case 3:// 個人倉庫取出
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if (level >= 5) {
							this.mode_warehouse_out(pc, npcId, size);
						}
					}
				}
				break;

			case 4:// 血盟倉庫存入
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if (level >= 5) {
							this.mode_warehouse_clan_in(pc, npcId, size);
						}
					}
				}
				break;

			case 5:// 血盟倉庫取出
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if (level >= 5) {
							this.mode_warehouse_clan_out(pc, npcId, size);

						} else {// 血盟倉庫取出中 Cancel、ESC
							final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
							if (clan != null) {
								clan.setWarehouseUsingChar(0); // 血盟倉庫使用解除
							}
						}
					}
				}
				break;
				
			case 6:// 無傳遞封包
				break;
				
			case 7:// 無傳遞封包
				break;
				
			case 8:// 精靈倉庫存入
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if ((level >= 5) && pc.isElf()) {
							this.mode_warehouse_elf_in(pc, npcId, size);
						}
					}
				}
				break;

			case 9:// 精靈倉庫取出
				if (size > 0) {
					if (findObject instanceof L1DwarfInstance) {
						final int level = pc.getLevel();
						if ((level >= 5) && pc.isElf()) {
							this.mode_warehouse_elf_out(pc, npcId, size);
						}
					}
				}
				break;

			case 10:// 物品強化
				if (size > 0) {
					switch (npcId) {
					case 91141:// 物品升級專員
					case 91142:// 物品升級專員
					case 91143:// 物品升級專員
						mode_update_item(pc, size, npcObjectId);
						break;

					default:
						break;
					}
				}
				break;
				
			case 11:// 無傳遞封包
				break;
				
			case 12:// 提煉武器/防具
				if (size > 0) {
					switch (npcId) {
					case 70535:// 托售管理員
						this.mode_shop_item(pc, size, npcObjectId);
						break;
					}
				}
				break;
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	private void mode_update_item(L1PcInstance pc, int size, int npcObjectId) {
		try {
			if (size != 1) {
				pc.sendPackets(new S_ServerMessage("\\fR你只能選取一樣裝備用來升級。"));
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			final int orderId = this.readD();
			final int count = Math.max(0, this.readD());// 數量
			if (count != 1) {
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			
			final L1ItemInstance item = pc.getInventory().getItem(orderId);
			final ArrayList<L1ItemUpdate> items = ItemUpdateTable.get().get(item.getItemId());
			final String[] names = new String[items.size()];
			for (int index = 0 ; index < items.size() ; index++) {
				int toid = items.get(index).get_toid();
				final L1Item tgitem = ItemTable.get().getTemplate(toid);
				if (tgitem != null) {
					names[index] = tgitem.getName();
				}
			}
			
			pc.set_mode_id(orderId);
			pc.sendPackets(new S_NPCTalkReturn(npcObjectId, L1ItemUpdate._html_02, names));
			
		} catch (Exception e) {
			_log.error("升級裝備物品數據異常: " + pc.getName());
		}
	}

	/**
	 * 托售管理員(購買物品)
	 * @param pc
	 * @param size
	 */
	private void mode_shopS(L1PcInstance pc, int size) {
		try {
			final Map<Integer, Integer> sellScoreMapMap = new HashMap<Integer, Integer>();
			for (int i = 0; i < size; i++) {
				final int orderId = this.readD();
				int count = this.readD();
				WriteLogTxt.NormalLog("托售購買", pc.getName() + " orderid: " + orderId + " count: " + count);
				count = Math.max(0, count);// 數量
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				sellScoreMapMap.put(new Integer(orderId), new Integer(count));

			}
			pc.get_otherList().get_buyCnS(sellScoreMapMap);
			
		} catch (Exception e) {
			_log.error("購買人物托售物品數據異常: " + pc.getName());
		}
	}

	/**
	 * 托售管理員(托售物品)
	 * @param pc
	 * @param size
	 * @param npcObjectId
	 */
	private void mode_shop_item(L1PcInstance pc, int size, int npcObjectId) {
		try {
			if (size == 1) {
				final int objid = this.readD();
				final L1Object object = pc.getInventory().getItem(objid);

				boolean isError = false;
				if (object instanceof L1ItemInstance) {
					final L1ItemInstance item = (L1ItemInstance) object;
					if (item.isEquipped()) {// 使用中物件
						isError = true;
					}
					if (!item.isIdentified()) {// 未鑒定物品
						isError = true;
					}
					if (item.getItem().getMaxUseTime() != 0) {// 具有時間限制
						isError = true;
					}
					if (item.get_time() != null) {
						isError = true;
					}
					
					if (ShopXTable.get().getTemplate(item.getItem().getItemId()) != null) {// 不可托售物品
						isError = true;
					}

					// 寵物
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (item.getId() == pet.getItemObjId()) {
								isError = true;
							}
						}
					}

					// 取回娃娃
					if (pc.getDoll(item.getId()) != null) {
						isError = true;
					}

					if (item.getGamNo() != null) {// 賭票
						isError = true;
					}
					if (item.getEnchantLevel() < 0) {// 強化為負值
						isError = true;
					}
					if (item.getItem().getMaxChargeCount() != 0) {// 具有次數
						if (item.getChargeCount() <= 0) {// 已無次數
							isError = true;
						}
					}
					
					if (isError) {
						pc.sendPackets(new S_NPCTalkReturn(npcObjectId, "y_x_e1"));
						
					} else {
						// 取回天寶數量
						final L1ItemInstance itemT = pc.getInventory().checkItemX(44070, ShopXSet.ADENA);
						if (itemT == null) {
							// 337：\f1%0不足%s。 0_o"
							pc.sendPackets(new S_ServerMessage(337, "天寶"));
							// 關閉對話窗
							pc.sendPackets(new S_CloseList(pc.getId()));
							return;
						}
						// 暫存物件訊息
						pc.get_other().set_item(item);
						pc.sendPackets(new S_CnsSell(npcObjectId, "y_x_3", "ma"));
					}
				}
				
			} else {
				pc.sendPackets(new S_NPCTalkReturn(npcObjectId, "y_x_e"));
			}
			
		} catch (Exception e) {
			_log.error("人物托售物品數據異常: " + pc.getName());
		}
	}

	/**
	 * 回收商人/買入玩家物品
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_sellall(final L1PcInstance pc, final int size) {
		try {
			final Map<Integer, Integer> sellallMap = new HashMap<Integer, Integer>();
			for (int i = 0; i < size; i++) {
				final int objid = this.readD();
				int count = this.readD();
				WriteLogTxt.NormalLog("回收商人", pc.getName() + " orderid: " + objid + " count: " + count);
				count = Math.max(0, count);// 數量
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				final L1ItemInstance itemInstance = pc.getInventory().getItem(
						objid);
				if (itemInstance == null) {
					continue;
				}
				sellallMap.put(new Integer(objid), new Integer(count));

			}
			pc.get_otherList().sellall(sellallMap);
			
		} catch (Exception e) {
			_log.error("回收商人/買入玩家物品數據異常: " + pc.getName());
		}
	}

	/**
	 * 奇怪的商人
	 * @param pc
	 * @param size
	 * @param isShop
	 */
	private void mode_cn(final L1PcInstance pc, final int size, final boolean isShop) {
		try {
			if (isShop) {// 買入
				final Map<Integer, Integer> cnMap = new HashMap<Integer, Integer>();
				for (int i = 0; i < size; i++) {
					final int orderId = this.readD();
					int count = this.readD();
					WriteLogTxt.NormalLog("奇怪的商人", pc.getName() + " orderid: " + orderId + " count: " + count);
					count = Math.max(0, count);// 數量
					if (count <= 0) {
						_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
						continue;
					}
					cnMap.put(new Integer(orderId), new Integer(count));
				}
				pc.get_otherList().get_buyCn(cnMap);
			}
			
		} catch (Exception e) {
			_log.error("奇怪的商人買入物品數據異常: " + pc.getName());
		}
	}

	/**
	 * 賭場NPC
	 * @param pc
	 * @param npcId
	 * @param size
	 * @param isShop true買入 false賣出
	 */
	private void mode_gambling(final L1PcInstance pc, final int npcId, final int size, final boolean isShop) {
		if (isShop) {// 買入
			if (GamblingTime.isStart()) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			final Map<Integer, Integer> gamMap = new HashMap<Integer, Integer>();
			for (int i = 0; i < size; i++) {
				final int orderId = this.readD();
				int count = this.readD();
				WriteLogTxt.NormalLog("賭狗NPC", pc.getName() + " orderid: " + orderId + " count: " + count);
				count = Math.max(0, count);// 數量
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				gamMap.put(new Integer(orderId), new Integer(count));
			}
			pc.get_otherList().get_buyGam(gamMap);
			
		} else {// 賣出
			for (int i = 0; i < size; i++) {
				final int objid = this.readD();
				final int count = Math.max(0, this.readD());// 數量
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				pc.get_otherList().get_sellGam(objid, count);
			}
		}
	}

	/**
	 * 精靈倉庫取出 XXX
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_elf_out(final L1PcInstance pc, final int npcId, final int size) {
		warehouse_lock.lock();
		try {
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = this.readD();
				count = this.readD();
				WriteLogTxt.NormalLog("精靈倉庫取出", pc.getName() + " orderid: " + objectId + " count: " + count);
				count = Math.max(0, count);
				if (count <= 0) {
					_log.error("要求精靈倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
					break;
				}
				item = pc.getDwarfForElfInventory().getItem(objectId);
				if (item == null) {
					_log.error("精靈倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				if (!DwarfForElfReading.get().getUserItems(pc.getAccountName(), objectId, count)) {
					_log.error("精靈倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}

				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
					if (pc.getInventory().consumeItem(40494, 2)) { // 
						pc.getDwarfForElfInventory().tradeItem(item, count, pc.getInventory());
						//  RecordTable.get().recordeWarehouse(pc.getName(), "領", "妖", item.getLogName(),
						//	count, item.getId(), pc.getIp());

					} else {
						pc.sendPackets(new S_ServerMessage(337, "$767")); // \f1%0不足。
						break;
					}

				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
					break;
				}
			}
		} finally {
			warehouse_lock.unlock();
		}
	}

	/**
	 * 精靈倉庫存入
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_elf_in(final L1PcInstance pc, final int npcId, final int size) {
		warehouse_lock.lock();
		try {
			int objectId, count;
			for (int i = 0; i < size; i++) {
				objectId = this.readD();
				count = this.readD();
				WriteLogTxt.NormalLog("精靈倉庫存入", pc.getName() + " orderid: " + objectId + " count: " + count);
				count = Math.max(0, count);
				if (count <= 0) {
					_log.error("要求精靈倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
					break;
				}
				final L1Object object = pc.getInventory().getItem(objectId);
				if (object == null) {
					_log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
					_log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				final L1ItemInstance item = (L1ItemInstance) object;
				if (item.getItem().getItemId() == 44070) {
					continue;
				}
				if (!item.getItem().isTradable()) {
					// 210 \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					break;
				}

				if (item.get_time() != null) {
					// \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					break;
				}

				// 寵物
				final Object[] petlist = pc.getPetList().values().toArray();
				for (final Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						final L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							// \f1%0%d是不可轉移的…
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
							break;
						}
					}
				}

				// 取回娃娃
				if (pc.getDoll(item.getId()) != null) {
					// 1,181：這個魔法娃娃目前正在使用中。
					pc.sendPackets(new S_ServerMessage(1181));
					break;
				}

				if (pc.getDwarfForElfInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
					break;
				}
				pc.getInventory().tradeItem(objectId, count, pc.getDwarfForElfInventory());
				// pc.turnOnOffLight();
				//   RecordTable.get().recordeWarehouse(pc.getName(), "存", "妖", item.getLogName(),
				//		count, item.getId(), pc.getIp());
			}
		} finally {
			warehouse_lock.unlock();
		}
	}

	/**
	 * 血盟倉庫取出 XXX
	 * 
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_clan_out(final L1PcInstance pc, final int npcId, final int size) {
		int objectId, count;
		L1ItemInstance item;
		warehouse_lock.lock();
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		try {
			if (clan != null) {
				for (int i = 0; i < size; i++) {
					objectId = this.readD();
					count = this.readD();
					WriteLogTxt.NormalLog("血盟倉庫取出", pc.getName() + " orderid: " + objectId + " count: " + count);
					count = Math.max(0, count);
                    //血盟倉庫有人在使用
                    if(clan.getWarehouseUsingChar() != 0 && clan.getWarehouseUsingChar() !=pc.getId()) {
                    	pc.sendPackets(new S_CloseList(pc.getId()));
                    	return; 
                    }
					if (count <= 0) {
						_log.error("要求血盟倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					item = clan.getDwarfForClanInventory().getItem(objectId);
					if (item == null) {
						_log.error("血盟倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
						break;
					}
					if (!DwarfForClanReading.get().getUserItems(pc.getClanname(), objectId, count)) {
						_log.error("血盟倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
						break;
					}
					
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							clan.sendPacketsAll(new S_ServerMessage("\\fW" + pc.getName() + " 在血盟倉庫領出了 " + item.getName() + "(" + count + ")"));
							clan.getDwarfForClanInventory().tradeItem(item, count, pc.getInventory());
                         //   RecordTable.get().recordeWarehouse(pc.getName(), "領", "盟", item.getLogName(), 
                            //		count, item.getId(), pc.getIp());

						} else {
							pc.sendPackets(new S_ServerMessage(189)); // 189 \f1金幣不足。
							break;
						}

					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
						break;
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			if (clan != null) {
				clan.setWarehouseUsingChar(0); // 解除盟倉使用狀態
			}
			warehouse_lock.unlock();
		}
	}

	/**
	 * 血盟倉庫存入
	 * 
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_clan_in(final L1PcInstance pc, final int npcId, final int size) {
		warehouse_lock.lock();
		int objectId, count;
		try {
			if (pc.getClanid() != 0) { // 所屬
				for (int i = 0; i < size; i++) {
					objectId = this.readD();
					count = this.readD();
					WriteLogTxt.NormalLog("血盟倉庫存入", pc.getName() + " orderid: " + objectId + " count: " + count);
					count = Math.max(0, count);
					if (count <= 0) {
						_log.error("要求血盟倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
						break;
					}
					final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
					final L1Object object = pc.getInventory().getItem(objectId);
					if (object == null) {
						_log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
						break;
					}
					if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
						_log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
						break;
					}
					final L1ItemInstance item = (L1ItemInstance) object;

					if (item.getItem().getItemId() == 44070) {
						continue;
					}

					if (!item.getItem().isTradable()) {
						// 210 \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
						break;
					}

					if (item.get_time() != null) {
						// \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
						break;
					}
					if (ItemRestrictionsTable.RESTRICTIONS.contains(item.getItemId())) {
						// \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
						break;
					}

					// 寵物
					final Object[] petlist = pc.getPetList().values().toArray();
					for (final Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							final L1PetInstance pet = (L1PetInstance) petObject;
							if (item.getId() == pet.getItemObjId()) {
								// 210 \f1%0%d是不可轉移的…
								pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
								break;
							}
						}
					}

					// 取回娃娃
					if (pc.getDoll(item.getId()) != null) {
						// 1,181：這個魔法娃娃目前正在使用中。  
						pc.sendPackets(new S_ServerMessage(1181));
						break;
					}
					
					if (clan != null) {
						if (clan.getDwarfForClanInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_CLAN) == L1Inventory.SIZE_OVER) {
							pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
							break;
						}
						clan.sendPacketsAll(new S_ServerMessage("\\fW" + pc.getName() + " 在血盟倉庫存入了 " + item.getName() + "(" + count + ")"));
						pc.getInventory().tradeItem(objectId, count, clan.getDwarfForClanInventory());
						// pc.turnOnOffLight();
                      //  RecordTable.get().recordeWarehouse(pc.getName(), "存", "盟", item.getLogName(), 
                        //		count, item.getId(), pc.getIp());
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1血盟倉庫使用血盟加入。
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			warehouse_lock.unlock();
		}
	}

	/**
	 * 個人倉庫取出 XXX
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_out(final L1PcInstance pc, final int npcId, final int size) {
		warehouse_lock.lock();
		try {
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = this.readD();
				count = this.readD();
				item = pc.getDwarfInventory().getItem(objectId);
				WriteLogTxt.NormalLog("個人倉庫取出", pc.getName() + " orderid: " + objectId + " count: " + count + " " + (item == null ? "" : item.getName() + " " + item.getCount()));
				count = Math.max(0, count);
				if (count <= 0) {
					_log.error("要求個人倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
					break;
				}
				if (item == null) {
					_log.error("個人倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				if (!DwarfReading.get().getUserItems(pc.getAccountName(), objectId, count)) {
					_log.error("個人倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}

				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {// 容量重量確認
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						pc.getDwarfInventory().tradeItem(item, count, pc.getInventory());
						//   RecordTable.get().recordeWarehouse(pc.getName(), "領", "自", item.getLogName(),
						// 		count, item.getId(), pc.getIp());


					} else {
						// \f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
						break;
					}

				} else {
					// 270 \f1當你負擔過重時不能交易。
					pc.sendPackets(new S_ServerMessage(270));
					break;
				}
			}
		} finally {
			warehouse_lock.unlock();
		}
	}

	/**
	 * 個人倉庫存入
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_warehouse_in (final L1PcInstance pc, final int npcId, final int size) {
		warehouse_lock.lock();
		try {
			int objectId, count;
			for (int i = 0; i < size; i++) {
				objectId = this.readD();
				count = this.readD();
				final L1Object object = pc.getInventory().getItem(objectId);
				WriteLogTxt.NormalLog("個人倉庫存入", pc.getName() + " orderid: " + objectId + " count: " + count + " " + (object == null ? "" : ((L1ItemInstance) object).getName() + " " + ((L1ItemInstance) object).getCount()));
				count = Math.max(0, count);
				if (count <= 0) {
					_log.error("要求個人倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
					break;
				}

				if (object == null) {
					_log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
					_log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
					break;
				}
				final L1ItemInstance item = (L1ItemInstance) object;
				if (item.getCount() <= 0) {
					break;
				}

				if (item.getItem().getItemId() == 44070) {
					continue;
				}

				if (!item.getItem().isTradable()) {
					// 210 \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					break;
				}

				if (item.get_time() != null) {
					// \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					break;
				}

				// 寵物
				final Object[] petlist = pc.getPetList().values().toArray();
				for (final Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						final L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							// 210 \f1%0%d是不可轉移的…
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
							break;
						}
					}
				}

				// 取回娃娃
				if (pc.getDoll(item.getId()) != null) {
					// 1,181：這個魔法娃娃目前正在使用中。
					pc.sendPackets(new S_ServerMessage(1181));
					break;
				}

				if (pc.getDwarfInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
					break;
				}
				pc.getInventory().tradeItem(objectId, count, pc.getDwarfInventory());
				//  RecordTable.get().recordeWarehouse(pc.getName(), "存", "自", item.getLogName(),
				// 		count, item.getId(), pc.getIp());
				//pc.turnOnOffLight();
			}
		} finally {
			warehouse_lock.unlock();
		}
	}

	/**
	 * 賣出物品給個人商店
	 * @param pc 賣出物品的玩家
	 * @param targetPc 設置商店的玩家
	 * @param size 數量
	 */
	private void mode_sellpc(final L1PcInstance pc, final L1PcInstance targetPc, final int size) {
		int count;
		int order;
		ArrayList<L1PrivateShopBuyList> buyList;
		L1PrivateShopBuyList psbl;
		int itemObjectId;
		L1ItemInstance item;// 賣出物品
		
		int buyItemObjectId;
		long buyPrice;
		int buyTotalCount;
		int buyCount;
		
		//L1ItemInstance targetItem;
		final boolean[] isRemoveFromList = new boolean[8];

		// 正在執行個人商店交易
		if (targetPc.isTradingInPrivateShop()) {
			return;
		}
		
		targetPc.setTradingInPrivateShop(true);
		buyList = targetPc.getBuyList();

		for (int i = 0; i < size; i++) {
			itemObjectId = this.readD();
			count = this.readCH();
			WriteLogTxt.NormalLog("賣出物品給個人商店", pc.getName() + " orderid: " + itemObjectId + " count: " + count);
			count = Math.max(0, count);
			if (count <= 0) {
				_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
				break;
			}
			order = this.readC();
			item = pc.getInventory().getItem(itemObjectId);
			if (item == null) {
				continue;
			}
			if (item.get_time() != null) {
				// 具有時間
				continue;
			}
			psbl = buyList.get(order);
			
			buyItemObjectId = psbl.getItemObjectId();// 傳回要購買的物品OBJID(商店玩家身上)
			buyPrice = psbl.getBuyPrice();// 回收價格
			buyTotalCount = psbl.getBuyTotalCount(); // 預計買入數量
			buyCount = psbl.getBuyCount(); // 買入數量累計
			
			if (count > buyTotalCount - buyCount) {
				count = buyTotalCount - buyCount;
			}
			
			if (item.isEquipped()) {
				// 無法販賣裝備中的道具。
				pc.sendPackets(new S_ServerMessage(905));
				continue;
			}

			final L1ItemInstance srcItem = targetPc.getInventory().getItem(buyItemObjectId);

			if (srcItem.get_time() != null) {
				// 具有時間
				continue;
			}
			if ((item.getItemId() == srcItem.getItemId())
					&& (item.getEnchantLevel() == srcItem.getEnchantLevel())) {
				if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
					for (int j = 0; j < count; j++) {
						if (buyPrice * j > 2000000000) {
							// 總共販賣價格無法超過 %d金幣。
							targetPc.sendPackets(new S_ServerMessage(904, "2000000000"));
							return;
						}
					}

					// 判斷回收者身上金幣數量是否足夠
					if (targetPc.getInventory().checkItem(L1ItemId.ADENA, count * buyPrice)) {
						// 取回金幣資料
						final L1ItemInstance adena = targetPc.getInventory().findItemId(L1ItemId.ADENA);
						// 金幣足夠
						if (adena != null) {
							// 出售者物件不足
							if (item.getCount() < count) {
								// 989：無法與開設個人商店的玩家進行交易。 
								pc.sendPackets(new S_ServerMessage(989)); 
								_log.error("可能使用bug進行交易 人物名稱(賣出道具給予個人商店/交易數量不吻合): " + pc.getName() + " objid:" + pc.getId());
								continue;
							}
							// 賣出物品給個人商店紀錄
							OtherUserSellReading.get().add(
									item.getItem().getName(), item.getId(), 
									(int) buyPrice, count, 
									pc.getId(), pc.getName(), 
									targetPc.getId(), targetPc.getName()
									);
							
							// 移動回收者物件
							targetPc.getInventory().tradeItem(adena, (count * buyPrice), pc.getInventory());
							// 移動出售者物件
							pc.getInventory().tradeItem(item, count, targetPc.getInventory());
							
							psbl.setBuyCount(count + buyCount);
							buyList.set(order, psbl);
							
							if (psbl.getBuyCount() == psbl.getBuyTotalCount()) { // 購買數量已達到
								isRemoveFromList[order] = true;
							}
						}

					} else {
						// \f1金幣不足。
						targetPc.sendPackets(new S_ServerMessage(189));
						break;
					}

				} else {
					// \f1對方攜帶的物品過重，無法交易。
					pc.sendPackets(new S_ServerMessage(271));
					break;
				}
				
			// 交易條件不吻合
			} else {
				_log.error("可能使用bug進行交易 人物名稱(賣出道具給予個人商店/交易條件不吻合): " + pc.getName() + " objid:" + pc.getId());
				return;
			}
		}

		// 買切末尾削除
		for (int i = 7; i >= 0; i--) {
			if (isRemoveFromList[i]) {
				buyList.remove(i);
			}
		}
		targetPc.setTradingInPrivateShop(false);
	}

	/**
	 * 買入個人商店物品
	 * @param pc 買入商店物品的玩家
	 * @param targetPc 設置商店的玩家
	 * @param size 買入物品種類清單
	 */
	private void mode_buypc(final L1PcInstance pc, final L1PcInstance targetPc, final int size) {
		int order;
		int count;
		int price;
		ArrayList<L1PrivateShopSellList> sellList;
		L1PrivateShopSellList pssl;
		int itemObjectId;
		int sellPrice;
		int sellTotalCount;
		int sellCount;
		L1ItemInstance item;
		final boolean[] isRemoveFromList = new boolean[8];

		// 正在執行交易
		if (targetPc.isTradingInPrivateShop()) {
			return;
		}

		// 擺放個人商店的全部物品種類清單
		sellList = targetPc.getSellList();
		
		synchronized (sellList) {
			// 售出數量異常
			if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
				return;
			}
			
			targetPc.setTradingInPrivateShop(true);

			for (int i = 0; i < size; i++) { // 購入予定商品
				order = this.readD();
				count = this.readD();
				WriteLogTxt.NormalLog("買入個人商店物品", pc.getName() + " & " + targetPc.getName() + " orderid: " + order + " count: " + count);
				count = Math.max(0, count);
				if (count <= 0) {
					_log.error("要求買入個人商店物品傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
					break;
				}
				// 取回商店賣出的道具資訊
				pssl = sellList.get(order);
				itemObjectId = pssl.getItemObjectId();// 物品objid
				sellPrice = pssl.getSellPrice();// 售價
				sellTotalCount = pssl.getSellTotalCount(); // 預計賣出的數量
				sellCount = pssl.getSellCount(); // 已經賣出數量的累計

				// 取回賣出物品資料
				item = targetPc.getInventory().getItem(itemObjectId);
				if (item == null) {
					// 該物件為空的狀態
					continue;
				}

				if (item.get_time() != null) {
					// 具有時間
					continue;
				}

				// 賣出物品 買方選取超出數量
				if (count > sellTotalCount - sellCount) {
					count = sellTotalCount - sellCount;
				}

				// 賣出物品數量為零
				if (count <= 0) {
					continue;
				}

				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認
					for (int j = 0; j < count; j++) { // 計算收入
						if (sellPrice * j > 2000000000) {
							// 總共販賣價格無法超過 %d金幣。
							pc.sendPackets(new S_ServerMessage(904, "2000000000"));
							targetPc.setTradingInPrivateShop(false);
							return;
						}
					}
					
					// 所需花費
					price = count * sellPrice;
					
					// 取回金幣資料
					final L1ItemInstance adena = pc.getInventory().findItemId(L1ItemId.ADENA);
					
					if (adena == null) {
						// \f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
						continue;
					}
					// 買入物品玩家金幣數量不足
					if (adena.getCount() < price) {
						// \f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
						continue;
					}
					
					// 商店玩家對像對像不為空
					if (targetPc != null) {
						// 賣出物品數量不足
						if (item.getCount() < count) {
							// 989：無法與開設個人商店的玩家進行交易。 
							pc.sendPackets(new S_ServerMessage(989)); 
							continue;
						}
						
						// 買入個人商店物品紀錄
						OtherUserBuyReading.get().add(
								item.getItem().getName(), item.getId(), 
								sellPrice, count, 
								pc.getId(), pc.getName(), 
								targetPc.getId(), targetPc.getName()
								);
						
						// 轉移道具給予購買者
						targetPc.getInventory().tradeItem(item, count, pc.getInventory());
						// 把花費的金幣轉移給設置為商店的玩家
						pc.getInventory().tradeItem(adena, price, targetPc.getInventory());
						
						// 產生訊息
						final String message = item.getItem().getNameId() + " (" + String.valueOf(count) + ")";
						// 877 將 %1%o 賣給 %0。
						targetPc.sendPackets(new S_ServerMessage(877, pc.getName(), message));
						
						// 設置物品已賣出數量
						pssl.setSellCount(count + sellCount);
						sellList.set(order, pssl);

						// 販賣物件已售完
						if (pssl.getSellCount() == pssl.getSellTotalCount()) {
							isRemoveFromList[order] = true;
						}
					}

				} else {
					// \f1當你負擔過重時不能交易。
					pc.sendPackets(new S_ServerMessage(270));
					break;
				}
			}
			// 該道具販賣結束移出販賣清單
			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					sellList.remove(i);
				}
			}
			targetPc.setTradingInPrivateShop(false);
		}
	}

	/**
	 * NPC商店賣出物品(買入玩家物品)
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_sell(final L1PcInstance pc, final int npcId, final int size) {
		final L1Shop shop = ShopTable.get().get(npcId);
		if (shop != null) {
			final L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
			for (int i = 0; i < size; i++) {
				final int objid = this.readD();
				int count = this.readD();
				final L1ItemInstance itemInstance = pc.getInventory().getItem(
						objid);
				WriteLogTxt.NormalLog("NPC商店賣出物品", pc.getName() + " orderid: " + objid + " count: " + count + " " + (itemInstance == null ? "" : itemInstance.getName()));
				count = Math.max(0, count);// 數量
				
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				if (itemInstance == null) {
					continue;
				}
				orderList.add(objid, count);
			}
			shop.buyItems(orderList);

		// 沒有販售資料
		} else {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * NPC商店買入物品(賣出物品給予玩家)
	 * @param pc
	 * @param npcId
	 * @param size
	 */
	private void mode_buy(final L1PcInstance pc, final int npcId, final int size) {
		final L1Shop shop = ShopTable.get().get(npcId);
		if (shop != null) {
			final L1ShopBuyOrderList orderList = shop.newBuyOrderList();
			for (int i = 0; i < size; i++) {
				final int orderId = this.readD();
				int count = this.readD();
				WriteLogTxt.NormalLog("商店買入物品", pc.getName() + " orderid: " + orderId + " count: " + count + " NPC:" + shop.getNpcId());
				count = Math.max(0, count);// 數量
				if (count <= 0) {
					_log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
					continue;
				}
				orderList.add(orderId, count);
			}
			shop.sellItems(pc, orderList);

		// 沒有販售資料
		} else {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
