package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.BLESSED_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;
import static com.lineage.server.model.skill.L1SkillId.DE_LV30;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import com.add.CustomBaccarat;
import com.add.CustomTaiwanMahjong;
import com.lineage.config.ConfigOther;
import com.lineage.server.datatables.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.*;
import com.lineage.server.world.WorldPcShop;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.lock.TownReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1HauntedHouse;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.L1UltimateBattle;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1HousekeeperInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.william.L1Blend;
import com.william.L1BlendTable;
import com.william.NpcQuest;

/**
 * 要求物件對話視窗結果
 *
 * @author daien
 *
 */
public class C_NPCAction extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_NPCAction.class);

	private static Random _random = new Random();

	public C_NPCAction(final byte[] decrypt, final ClientExecutor client) {
		try {
			// System.out.println("資料載入");
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

			final int objid = this.readD();
			final String s = this.readS();

			int[] materials = null;
			int[] counts = null;
			int[] createitem = null;
			int[] createcount = null;

			String htmlid = null;
			String success_htmlid = null;
			String failure_htmlid = null;
			String[] htmldata = null;
			L1Npc npctemp = null;
			final L1Object obj = World.get().findObject(objid);
			final L1NpcInstance npcObj = WorldPcShop.get().get(objid);
			if (npcObj != null && npcObj.getShopObjectId() > -1 && npcObj.getNpcId() == 3067888) {
				npctemp = npcObj.getNpcTemplate();
				String s2 = null;
				try {
					if (npcObj.getNpcTemplate().get_classname()
							.equalsIgnoreCase("other.Npc_AuctionBoard")) {
						s2 = this.readS();
					} else if (npcObj.getNpcTemplate().get_classname()
							.equalsIgnoreCase("other.Npc_Board")) {
						s2 = this.readS();
					}
				} catch (Exception e) {
				}

				final int difflocx = Math.abs(pc.getX() - npcObj.getX());
				final int difflocy = Math.abs(pc.getY() - npcObj.getY());
				// 3以上離場合無效
				if ((difflocx > 3) || (difflocy > 3)) {
					return;
				}
				if (s.equalsIgnoreCase("bet_type_0") || s.equalsIgnoreCase("bet_type_1") || s.equalsIgnoreCase("bet_type_2") || s.equalsIgnoreCase("bet_type_3") || s.equalsIgnoreCase("bet_type_4")) {
					CustomTaiwanMahjong.get().npcAction(pc, objid, s);
					return;
				}
				if (s.equalsIgnoreCase("cheat_0") || s.equalsIgnoreCase("cheat_1") || s.equalsIgnoreCase("cheat_2") || s.equalsIgnoreCase("cheat_3") || s.equalsIgnoreCase("cheat_4") || s.equalsIgnoreCase("cheat_5") || s.equalsIgnoreCase("cheat_6") || s.equalsIgnoreCase("cheat_7") || s.equalsIgnoreCase("cheat_8")) {
					CustomTaiwanMahjong.get().cheatAction(pc, s);
					return;
				}
				/** 顯示可製造的物品列表 */
				if (s.equalsIgnoreCase("request craft")) {
					ShowCraftList(pc, npcObj);
					// pc.sendPackets(new S_ServerMessage("測試"));
					return;
				}
				/** END */

				/** 顯示條件清單 */
				String craftkey = npctemp.get_npcId() + s;// 製造命令
				L1Blend ItemBlend = L1BlendTable.getInstance().getTemplate(
						craftkey);
				if (ItemBlend != null) {
					ItemBlend.ShowCraftHtml(pc, npcObj, ItemBlend);// 顯示條件清單
					pc.set_craftkey(craftkey);// 暫存製造命令
					return;
				}
				/** END */

				/** 確認或取消製造道具 */
				String craftkey2 = pc.get_craftkey();// 取回製造命令
				L1Blend ItemBlend2 = L1BlendTable.getInstance()
						.getTemplate(craftkey2);
				if (ItemBlend2 != null) {
					if (s.equalsIgnoreCase("confirm craft")) {// 確認製造道具
						ItemBlend2.CheckCraftItem(pc, npcObj, ItemBlend2, 1,
								false);
						return;
					} else if (s.equalsIgnoreCase("cancel craft")) {// 取消製造道具
						pc.sendPackets(new S_CloseList(pc.getId()));
						pc.set_craftkey(null);// 清空製造命令
						return;
					}
				}
				/** END */
				if (npcObj.ACTION != null) {
					if (s2 != null && s2.length() > 0) {
						npcObj.ACTION.action(pc, npcObj, s + "," + s2, 0);
						return;
					}
					npcObj.ACTION.action(pc, npcObj, s, 0);
					return;
				}
				npcObj.onFinalAction(pc, s);
				return;
			}
			if (obj == null) {
				_log.error("該OBJID編號的 NPC已經不存在世界中: " + objid);
				return;
			}
			// 命令來自於NPC
			if (obj instanceof L1NpcInstance) {
				final L1NpcInstance tmp = (L1NpcInstance) obj;
				npctemp = tmp.getNpcTemplate();
				String s2 = null;
				try {
					if (tmp.getNpcTemplate().get_classname()
							.equalsIgnoreCase("other.Npc_AuctionBoard")) {
						s2 = this.readS();
					} else if (tmp.getNpcTemplate().get_classname()
							.equalsIgnoreCase("other.Npc_Board")) {
						s2 = this.readS();
					}
				} catch (Exception e) {
				}
				if (obj instanceof L1PetInstance) {
					final L1PetInstance npc = (L1PetInstance) obj;
					pc.getActionPet().action(npc, s);
					return;

				} else if (obj instanceof L1SummonInstance) {
					final L1SummonInstance npc = (L1SummonInstance) obj;
					pc.getActionSummon().action(npc, s);
					return;

				} else {
					final L1NpcInstance npc = (L1NpcInstance) obj;

					final int difflocx = Math.abs(pc.getX() - npc.getX());
					final int difflocy = Math.abs(pc.getY() - npc.getY());
					if (s.equalsIgnoreCase("bet_player")
							|| s.equalsIgnoreCase("bet_player_pair")
							|| s.equalsIgnoreCase("bet_Tie")
							|| s.equalsIgnoreCase("bet_banker_pair")
							|| s.equalsIgnoreCase("bet_banker")
							|| s.equalsIgnoreCase("bet_100")
							|| s.equalsIgnoreCase("bet_500")
							|| s.equalsIgnoreCase("bet_1000")
							|| s.equalsIgnoreCase("bet_10000")
							|| s.equalsIgnoreCase("bet_remove")
							|| s.equalsIgnoreCase("bet_exit")) {
						CustomBaccarat.getInstance().npcAction(pc, s);
						return;
					}
					// 3以上離場合無效
					if ((difflocx > 3) || (difflocy > 3)) {
						return;
					}
					if (s.equalsIgnoreCase("bet_type_0") || s.equalsIgnoreCase("bet_type_1") || s.equalsIgnoreCase("bet_type_2") || s.equalsIgnoreCase("bet_type_3") || s.equalsIgnoreCase("bet_type_4")) {
						CustomTaiwanMahjong.get().npcAction(pc, objid, s);
						return;
					}
					if (s.equalsIgnoreCase("cheat_0") || s.equalsIgnoreCase("cheat_1") || s.equalsIgnoreCase("cheat_2") || s.equalsIgnoreCase("cheat_3") || s.equalsIgnoreCase("cheat_4") || s.equalsIgnoreCase("cheat_5") || s.equalsIgnoreCase("cheat_6") || s.equalsIgnoreCase("cheat_7") || s.equalsIgnoreCase("cheat_8")) {
						CustomTaiwanMahjong.get().cheatAction(pc, s);
						return;
					}
					/** 顯示可製造的物品列表 */
					if (s.equalsIgnoreCase("request craft")) {
						ShowCraftList(pc, npc);
						// pc.sendPackets(new S_ServerMessage("測試"));
						return;
					}
					/** END */

					/** 顯示條件清單 */
					String craftkey = npctemp.get_npcId() + s;// 製造命令
					L1Blend ItemBlend = L1BlendTable.getInstance().getTemplate(
							craftkey);
					if (ItemBlend != null) {
						ItemBlend.ShowCraftHtml(pc, npc, ItemBlend);// 顯示條件清單
						pc.set_craftkey(craftkey);// 暫存製造命令
						return;
					}
					/** END */

					/** 確認或取消製造道具 */
					String craftkey2 = pc.get_craftkey();// 取回製造命令
					L1Blend ItemBlend2 = L1BlendTable.getInstance()
							.getTemplate(craftkey2);
					if (ItemBlend2 != null) {
						if (s.equalsIgnoreCase("confirm craft")) {// 確認製造道具
							ItemBlend2.CheckCraftItem(pc, npc, ItemBlend2, 1,
									false);
							return;
						} else if (s.equalsIgnoreCase("cancel craft")) {// 取消製造道具
							pc.sendPackets(new S_CloseList(pc.getId()));
							pc.set_craftkey(null);// 清空製造命令
							return;
						}
					}
					/** END */
					if (npc.ACTION != null) {
						if (s2 != null && s2.length() > 0) {
							npc.ACTION.action(pc, npc, s + "," + s2, 0);
							return;
						}
						npc.ACTION.action(pc, npc, s, 0);
						return;
					}
					npc.onFinalAction(pc, s);
				}

				// 命令來自於PC
			} else if (obj instanceof L1PcInstance) {
				final L1PcInstance target = (L1PcInstance) obj;
				target.getAction().action(s, 0);
				return;
			}

			// XML化
			final L1NpcAction action = NpcActionTable.getInstance().get(s, pc,
					obj);
			if (action != null) {
				final L1NpcHtml result = action.execute(s, pc, obj,
						this.readByte());
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
				}
				return;
			}

			if (NpcQuest.forNpcQuest(s, pc, (L1NpcInstance) obj,
					((L1NpcInstance) obj).getNpcTemplate().get_npcId(), objid)) {
				htmlid = "";
				return;
			}

			/*
			 * 其他命令處理
			 */
			if (s.equalsIgnoreCase("buy")) {// 買
				try {
					// 出售物品列表
					pc.sendPackets(new S_ShopSellList(objid));

				} catch (final Exception e) {

				}

			} else if (s.equalsIgnoreCase("sell")) {// 賣
				final int npcid = ((L1NpcInstance) obj).getNpcTemplate()
						.get_npcId();
				if ((npcid == 70523) || (npcid == 70805)) { //  or 
					htmlid = "ladar2";

				} else if ((npcid == 70537) || (npcid == 70807)) { //  or
																	// 
					htmlid = "farlin2";

				} else if ((npcid == 600006) // 連 說話之島骨頭碎片npc
						|| (npcid == 70804)) { //  or 
					htmlid = "lien2";

				} else if ((npcid == 50527) || (npcid == 50505)
						|| (npcid == 50519) || (npcid == 50545)
						|| (npcid == 50531) || (npcid == 50529)
						|| (npcid == 50516) || (npcid == 50538)
						|| (npcid == 50518) || (npcid == 50509)
						|| (npcid == 50536) || (npcid == 50520)
						|| (npcid == 50543) || (npcid == 50526)
						|| (npcid == 50512) || (npcid == 50510)
						|| (npcid == 50504) || (npcid == 50525)
						|| (npcid == 50534) || (npcid == 50540)
						|| (npcid == 50515) || (npcid == 50513)
						|| (npcid == 50528) || (npcid == 50533)
						|| (npcid == 50542) || (npcid == 50511)
						|| (npcid == 50501) || (npcid == 50503)
						|| (npcid == 50508) || (npcid == 50514)
						|| (npcid == 50532) || (npcid == 50544)
						|| (npcid == 50524) || (npcid == 50535)
						|| (npcid == 50521) || (npcid == 50517)
						|| (npcid == 50537) || (npcid == 50539)
						|| (npcid == 50507) || (npcid == 50530)
						|| (npcid == 50502) || (npcid == 50506)
						|| (npcid == 50522) || (npcid == 50541)
						|| (npcid == 50523) || (npcid == 50620)
						|| (npcid == 50623) || (npcid == 50619)
						|| (npcid == 50621) || (npcid == 50622)
						|| (npcid == 50624) || (npcid == 50617)
						|| (npcid == 50614) || (npcid == 50618)
						|| (npcid == 50616) || (npcid == 50615)
						|| (npcid == 50626) || (npcid == 50627)
						|| (npcid == 50628) || (npcid == 50629)
						|| (npcid == 50630) || (npcid == 50631)) { // NPC
					final String sellHouseMessage = this.sellHouse(pc, objid,
							npcid);
					if (sellHouseMessage != null) {
						htmlid = sellHouseMessage;
					}
				} else { // 一般商人
					// 買取表示
					pc.sendPackets(new S_ShopBuyList(objid, pc));
				}

			} else if (s.equalsIgnoreCase("retrieve")) { // 「個人倉庫：受取」
				if (pc.getLevel() >= 5) {
					final int size = pc.getDwarfInventory().getItems().size();
					if (size > 0) {
						// 834：倉庫密碼。
						int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd != -256) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrieveList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("retrieve-elven")) { // 「倉庫：荷物受取」
				if ((pc.getLevel() >= 5) && pc.isElf()) {
					final int size = pc.getDwarfForElfInventory().getSize();
					if (size > 0) {
						// 834：倉庫密碼。
						int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd != -256) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrieveElfList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("retrieve-pledge")) { // 「血盟倉庫：荷物受取」
				if (pc.getLevel() >= 5) {
					if (pc.getClanid() == 0) {
						// \f1血盟倉庫使用血盟加入。
						pc.sendPackets(new S_ServerMessage(208));
						return;
					}

					// final L1Clan clan =
					// WorldClan.get().getClan(pc.getClanname());
					final int size = pc.getClan().getDwarfForClanInventory()
							.getSize();

					if (size > 0) {
						final int rank = pc.getClanRank();
						switch (rank) {
						case L1Clan.CLAN_RANK_PUBLIC:// 2:一般
						case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主
						case L1Clan.ALLIANCE_CLAN_RANK_ATTEND:// 5:修習騎士
						case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士
						case L1Clan.NORMAL_CLAN_RANK_GENERAL:// 7:一般
						case L1Clan.NORMAL_CLAN_RANK_ATTEND:// 8:修習騎士
						case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
							if (pc.getTitle().equalsIgnoreCase("")) {
								// 只有收到稱謂的人才能使用血盟倉庫。
								pc.sendPackets(new S_ServerMessage(728));
								return;
							}
							break;

						case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主
						case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
							break;
						default:
							// 只有收到稱謂的人才能使用血盟倉庫。
							pc.sendPackets(new S_ServerMessage(728));
							return;
						}
						// 834：倉庫密碼。
						int srcpwd = client.getAccount().get_warehouse();
						if (srcpwd != -256) {
							pc.sendPackets(new S_ServerMessage(834));
							return;
						}
						pc.sendPackets(new S_RetrievePledgeList(objid, pc));

					} else {
						// noitemret
						pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
					}
				}

			} else if (s.equalsIgnoreCase("get")) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				final int npcId = npc.getNpcTemplate().get_npcId();
				//  or 
				if ((npcId == 600007) // 庫伯--油布斗篷
						|| (npcId == 70796)) {
					final L1ItemInstance item = pc.getInventory().storeItem(
							810005, 1); // 油布斗篷
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					pc.getQuest().set_end(L1PcQuest.QUEST_OILSKINMANT);
					htmlid = ""; // 消
				}
				// ：報酬
				else if ((npcId == 70528) || (npcId == 70546)
						|| (npcId == 70567) || (npcId == 70594)
						|| (npcId == 70654) || (npcId == 70748)
						|| (npcId == 70774) || (npcId == 70799)
						|| (npcId == 70815) || (npcId == 70860)) {

					if (pc.getHomeTownId() > 0) {

					} else {

					}
				}

			} else if (s.equalsIgnoreCase("room")) { // 租房間
				L1NpcInstance npc = (L1NpcInstance) obj;
				int npcId = npc.getNpcTemplate().get_npcId();
				boolean canRent = false;
				boolean findRoom = false;
				boolean isRent = false;
				boolean isHall = false;
				int roomNumber = 0;
				byte roomCount = 0;
				for (int i = 0; i < 16; i++) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
					if (inn != null) { // 此旅館NPC資訊不為空值
						Timestamp dueTime = inn.getDueTime();
						Calendar cal = Calendar.getInstance();
						long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000;
						if (inn.getLodgerId() == pc.getId() && checkDueTime < 0) { // 出租時間未到的房間租用人判斷
							if (inn.isHall()) { // 租用的是會議室
								isHall = true;
							}
							isRent = true; // 已租用
							break;
						} else if (!findRoom && !isRent) { // 未租用且尚未找到可租用的房間
							if (checkDueTime >= 0) { // 租用時間已到
								canRent = true;
								findRoom = true;
								roomNumber = inn.getRoomNumber();
							} else { // 計算出租時間未到的數量
								if (!inn.isHall()) { // 一般房間
									roomCount++;
								}
							}
						}
					}
				}

				if (isRent) {
					if (isHall) {
						htmlid = "inn15"; // 真是抱歉，你已經租借過會議廳了。
					} else {
						htmlid = "inn5"; // 對不起，你已經有租房間了。
					}
				} else if (roomCount >= 12) {
					htmlid = "inn6"; // 真不好意思，現在沒有房間了。
				} else if (canRent) {
					pc.setInnRoomNumber(roomNumber); // 房間編號
					pc.setHall(false); // 一般房間
					pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn2"));
				}
			} else if (s.equalsIgnoreCase("hall") && (obj instanceof L1MerchantInstance)) { // 租會議廳
				if (pc.isCrown()) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					int npcId = npc.getNpcTemplate().get_npcId();
					boolean canRent = false;
					boolean findRoom = false;
					boolean isRent = false;
					boolean isHall = false;
					int roomNumber = 0;
					byte roomCount = 0;
					for (int i = 0; i < 16; i++) {
						L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
						if (inn != null) { // 此旅館NPC資訊不為空值
							Timestamp dueTime = inn.getDueTime();
							Calendar cal = Calendar.getInstance();
							long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000;
							if (inn.getLodgerId() == pc.getId() && checkDueTime < 0) { // 出租時間未到的房間租用人判斷
								if (inn.isHall()) { // 租用的是會議室
									isHall = true;
								}
								isRent = true; // 已租用
								break;
							} else if (!findRoom && !isRent) { // 未租用且尚未找到可租用的房間
								if (checkDueTime >= 0) { // 租用時間已到
									canRent = true;
									findRoom = true;
									roomNumber = inn.getRoomNumber();
								} else { // 計算出租時間未到的數量
									if (inn.isHall()) { // 會議室
										roomCount++;
									}
								}
							}
						}
					}

					if (isRent) {
						if (isHall) {
							htmlid = "inn15"; // 真是抱歉，你已經租借過會議廳了。
						} else {
							htmlid = "inn5"; // 對不起，你已經有租房間了。
						}
					} else if (roomCount >= 4) {
						htmlid = "inn16"; // 不好意思，目前正好沒有空的會議廳。
					} else if (canRent) {
						pc.setInnRoomNumber(roomNumber); // 房間編號
						pc.setHall(true); // 會議室
						pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn12"));
					}
				} else {
					// 王子和公主才能租用會議廳。
					htmlid = "inn10";
				}
			} else if (s.equalsIgnoreCase("return")) { // 退租
				L1NpcInstance npc = (L1NpcInstance) obj;
				int npcId = npc.getNpcTemplate().get_npcId();
				int price = 0;
				boolean isBreak = false;
				// 退租判斷
				for (int i = 0; i < 16; i++) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
					if (inn != null) { // 此旅館NPC房間資訊不為空值
						if (inn.getLodgerId() == pc.getId()) { // 欲退租的租用人
							Timestamp dueTime = inn.getDueTime();
							if (dueTime != null) { // 時間不為空值
								Calendar cal = Calendar.getInstance();
								if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // 租用時間未到
									isBreak = true;
									price += 60; // 退 20%租金
								}
							}
							Timestamp ts = new Timestamp(System.currentTimeMillis()); // 目前時間
							inn.setDueTime(ts); // 退租時間
							inn.setLodgerId(0); // 租用人
							inn.setKeyId(0); // 旅館鑰匙
							inn.setHall(false);
							// DB更新
							InnTable.getInstance().updateInn(inn);
							break;
						}
					}
				}
				// 刪除鑰匙判斷
				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getInnNpcId() == npcId) { // 鑰匙與退租的NPC相符
						price += 20 * item.getCount(); // 鑰匙的價錢 20 * 鑰匙數量
						InnKeyTable.DeleteKey(item); // 刪除鑰匙紀錄
						pc.getInventory().removeItem(item); // 刪除鑰匙
						isBreak = true;
					}
				}

				if (isBreak) {
					htmldata = new String[] { npc.getName(), String.valueOf(price) };
					htmlid = "inn20";
					pc.getInventory().storeItem(L1ItemId.ADENA, price); // 取得金幣
				} else {
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("enter")) { // 進入房間或會議廳
				L1NpcInstance npc = (L1NpcInstance) obj;
				int npcId = npc.getNpcTemplate().get_npcId();

				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getInnNpcId() == npcId) { // 鑰匙與NPC相符
						for (int i = 0; i < 16; i++) {
							L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
							if (inn.getKeyId() == item.getKeyId()) {
								Timestamp dueTime = item.getDueTime();
								if (dueTime != null) { // 時間不為空值
									Calendar cal = Calendar.getInstance();
									if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // 鑰匙租用時間未到
										int[] data = null;
										switch (npcId) {
											case 70012: // 說話之島 - 瑟琳娜
												data = new int[] { 32745, 32803, 16384, 32743, 32808, 16896 };
												break;
											case 70019: // 古魯丁 - 羅利雅
												data = new int[] { 32743, 32803, 17408, 32744, 32807, 17920 };
												break;
											case 70031: // 奇岩 - 瑪理
												data = new int[] { 32744, 32803, 18432, 32744, 32807, 18944 };
												break;
											case 70065: // 歐瑞 - 小安安
												data = new int[] { 32744, 32803, 19456, 32744, 32807, 19968 };
												break;
											case 70070: // 風木 - 維萊莎
												data = new int[] { 32744, 32803, 20480, 32744, 32807, 20992 };
												break;
											case 70075: // 銀騎士 - 米蘭德
												data = new int[] { 32744, 32803, 21504, 32744, 32807, 22016 };
												break;
											case 70084: // 海音 - 伊莉
												data = new int[] { 32744, 32803, 22528, 32744, 32807, 23040 };
												break;
											case 70096: // 海賊島 - 米列
												data = new int[] { 32744, 32803, 23552, 32744, 32807, 24064 };
												break;
											default:
												break;
										}
										pc.setInnKeyId(item.getKeyId()); // 登入鑰匙編號
										if (!item.checkRoomOrHall()) { // 房間
											L1Teleport.teleport(pc, data[0], data[1], (short) data[2], 6, false);
										} else { // 會議室
											L1Teleport.teleport(pc, data[3], data[4], (short) data[5], 6, false);
											break;
										}
									}
								}
							}
						}
					}
				}
			} else if (s.equalsIgnoreCase("openigate")) { //  / 城門開
				final L1NpcInstance npc = (L1NpcInstance) obj;
				this.openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("closeigate")) { //  / 城門閉
				final L1NpcInstance npc = (L1NpcInstance) obj;
				this.openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("askwartime")) { // 近衛兵 /
															// 次攻城戰時間
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (npc.getNpcTemplate().get_npcId() == 60514) { // 城近衛兵
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
					htmlid = "ktguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 60560) { // 近衛兵
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
					htmlid = "orcguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 60552) { // 城近衛兵
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
					htmlid = "wdguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60524) || // 街入口近衛兵(弓)
						(npc.getNpcTemplate().get_npcId() == 60525) || // 街入口近衛兵
						(npc.getNpcTemplate().get_npcId() == 60529)) { // 城近衛兵
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
					htmlid = "grguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 70857) { // 城
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
					htmlid = "heguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60530) || // 城
						(npc.getNpcTemplate().get_npcId() == 60531)) {
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
					htmlid = "dcguard7";
				} else if ((npc.getNpcTemplate().get_npcId() == 60533) || // 城
																			// 
						(npc.getNpcTemplate().get_npcId() == 60534)) {
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
					htmlid = "adguard7";
				} else if (npc.getNpcTemplate().get_npcId() == 81156) { // 偵察兵（要塞）
					htmldata = this
							.makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
					htmlid = "dfguard3";
				}
			} else if (s.equalsIgnoreCase("inex")) { // 收入/支出報告受
				// 暫定的公金表示。
				// 適當。
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主
						final L1Castle l1castle = CastleReading.get()
								.getCastleTable(castle_id);
						pc.sendPackets(new S_ServerMessage(309, // %0精算總額%1。
								l1castle.getName(), String.valueOf(l1castle
										.getPublicMoney())));
						htmlid = ""; // 消
					}
				}
			} else if (s.equalsIgnoreCase("tax")) { // 稅率調節
//				pc.sendPackets(new S_TaxRate(pc.getId()));

			} else if (s.equalsIgnoreCase("withdrawal")) { // 資金引出
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int castle_id = clan.getCastleId();
					if (castle_id != 0) { // 城主
						final L1Castle l1castle = CastleReading.get()
								.getCastleTable(castle_id);
						pc.sendPackets(new S_Drawal(pc.getId(), l1castle
								.getPublicMoney()));
					}
				}
			} else if (s.equalsIgnoreCase("cdeposit")) { // 資金入金
				pc.sendPackets(new S_Deposit(pc.getId()));
			} else if (s.equalsIgnoreCase("employ")) { // 傭兵僱用

			} else if (s.equalsIgnoreCase("arrange")) { // 僱用傭兵配置

			} else if (s.equalsIgnoreCase("castlegate")) { // 城門管理
				this.repairGate(pc);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("encw")) { // 武器專門家 / 武器強化魔法受
				if (pc.getWeapon() == null) {
					pc.sendPackets(new S_ServerMessage(79));
				} else {
					for (final L1ItemInstance item : pc.getInventory()
							.getItems()) {
						if (pc.getWeapon().equals(item)) {
							final L1SkillUse l1skilluse = new L1SkillUse();
							l1skilluse.handleCommands(pc, ENCHANT_WEAPON,
									item.getId(), 0, 0, 0,
									L1SkillUse.TYPE_SPELLSC);
							break;
						}
					}
				}
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("enca")) { // 防具專門家 / 防具強化魔法受
				final L1ItemInstance item = pc.getInventory().getItemEquipped(
						2, 2);
				if (item != null) {
					final L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(pc, BLESSED_ARMOR, item.getId(),
							0, 0, 0, L1SkillUse.TYPE_SPELLSC);
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
				htmlid = ""; // 消

			} else if (s.equalsIgnoreCase("depositnpc")) { // 「動物預」
				final Object[] petList = pc.getPetList().values().toArray();
				for (final Object petObject : petList) {
					if (petObject instanceof L1PetInstance) { // 
						final L1PetInstance pet = (L1PetInstance) petObject;
						pet.collect(true);
						pc.removePet(pet);
						// pc.getPetList().remove(pet.getId());
						pet.deleteMe();
					}
				}
				htmlid = ""; // 消

			} else if (s.equalsIgnoreCase("withdrawnpc")) { // 「動物受取」
				pc.sendPackets(new S_PetList(objid, pc));

				/*
				 * } else if (s.equalsIgnoreCase("select")) { // 競賣揭示板
				 * pc.sendPackets(new S_AuctionBoardRead(objid, s2));
				 * 
				 * } else if (s.equalsIgnoreCase("map")) { // 位置確
				 * pc.sendPackets(new S_HouseMap(objid, s2));
				 * 
				 * } else if (s.equalsIgnoreCase("apply")) { // 競賣參加 final
				 * L1Clan clan = WorldClan.get().getClan(pc.getClanname()); if
				 * (clan != null) { if (pc.isCrown() && (pc.getId() ==
				 * clan.getLeaderId())) { // 君主、、血盟主 if (pc.getLevel() >= 15)
				 * { if (clan.getHouseId() == 0) { pc.sendPackets(new
				 * S_ApplyAuction(objid, s2)); } else { pc.sendPackets(new
				 * S_ServerMessage(521)); // 家所有。 htmlid = ""; //
				 * 消 } } else { pc.sendPackets(new S_ServerMessage(519));
				 * // 15未滿君主競賣參加。 htmlid = ""; // 消 } } else {
				 * pc.sendPackets(new S_ServerMessage(518)); //
				 * 命令血盟君主利用。 htmlid = ""; // 消 } } else {
				 * pc.sendPackets(new S_ServerMessage(518)); //
				 * 命令血盟君主利用。 htmlid = ""; // 消 }
				 */
			} else if (s.equalsIgnoreCase("open") // 開
					|| s.equalsIgnoreCase("close")) { // 閉
				final L1NpcInstance npc = (L1NpcInstance) obj;
				this.openCloseDoor(pc, npc, s);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("expel")) { // 外部人間追出
				final L1NpcInstance npc = (L1NpcInstance) obj;
				this.expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("pay")) { // 稅金納
				final L1NpcInstance npc = (L1NpcInstance) obj;
				htmldata = this.makeHouseTaxStrings(pc, npc);
				htmlid = "agpay";
			} else if (s.equalsIgnoreCase("payfee")) { // 稅金納
				final L1NpcInstance npc = (L1NpcInstance) obj;
				this.payFee(pc, npc);
				htmlid = "";
			} else if (s.equalsIgnoreCase("name")) { // 家名前決
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(
								houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							pc.setTempID(houseId); // ID保存
							pc.sendPackets(new S_Message_YN(512)); // 家名前？
						}
					}
				}
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("rem")) { // 家中傢俱取除
			} else if (s.equalsIgnoreCase("tel0") // (倉庫)
					|| s.equalsIgnoreCase("tel1") // (保管所)
					|| s.equalsIgnoreCase("tel2") // (贖罪使者)
					|| s.equalsIgnoreCase("tel3")) { // (市場)
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(
								houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							int[] loc = new int[3];
							if (s.equalsIgnoreCase("tel0")) {
								loc = L1HouseLocation.getHouseTeleportLoc(
										houseId, 0);
							} else if (s.equalsIgnoreCase("tel1")) {
								loc = L1HouseLocation.getHouseTeleportLoc(
										houseId, 1);
							} else if (s.equalsIgnoreCase("tel2")) {
								loc = L1HouseLocation.getHouseTeleportLoc(
										houseId, 2);
							} else if (s.equalsIgnoreCase("tel3")) {
								loc = L1HouseLocation.getHouseTeleportLoc(
										houseId, 3);
							}
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) loc[2], 5, true);
						}
					}
				}
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("upgrade")) { // 地下作
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(
								houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							if (pc.isCrown()
									&& (pc.getId() == clan.getLeaderId())) { // 君主、、血盟主
								if (house.isPurchaseBasement()) {
									// 既地下所有。
									pc.sendPackets(new S_ServerMessage(1135));
								} else {
									if (pc.getInventory().consumeItem(
											L1ItemId.ADENA, 5000000)) {
										house.setPurchaseBasement(true);
										HouseReading.get().updateHouse(house); // DB書迂
										// 地下生成。
										pc.sendPackets(new S_ServerMessage(1099));
									} else {
										// 189 \f1金幣不足。
										pc.sendPackets(new S_ServerMessage(189));
									}
								}
							} else {
								// 命令血盟君主利用。
								pc.sendPackets(new S_ServerMessage(518));
							}
						}
					}
				}
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("hall")
					&& (obj instanceof L1HousekeeperInstance)) { // 地下
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					final int houseId = clan.getHouseId();
					if (houseId != 0) {
						final L1House house = HouseReading.get().getHouseTable(
								houseId);
						final int keeperId = house.getKeeperId();
						final L1NpcInstance npc = (L1NpcInstance) obj;
						if (npc.getNpcTemplate().get_npcId() == keeperId) {
							if (house.isPurchaseBasement()) {
								int[] loc = new int[3];
								loc = L1HouseLocation.getBasementLoc(houseId);
								L1Teleport.teleport(pc, loc[0], loc[1],
										(short) (loc[2]), 5, true);
							} else {
								// 地下、。
								pc.sendPackets(new S_ServerMessage(1098));
							}
						}
					}
				}
				htmlid = ""; // 消
			}

			// ElfAttr:0.無屬性,1.地屬性,2.火屬性,4.水屬性,8.風屬性
			else if (s.equalsIgnoreCase("fire")) // 屬性變更「火系列習」
			{
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(2);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 1)); // 體隅火精靈力染。
					htmlid = ""; // 消
				}
			} else if (s.equalsIgnoreCase("water")) { // 屬性變更「水系列習」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(4);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 2)); // 體隅水精靈力染。
					htmlid = ""; // 消
				}
			} else if (s.equalsIgnoreCase("air")) { // 屬性變更「風系列習」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(8);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 3)); // 体の隅々に水の精霊力が染みこんできます。。
					htmlid = ""; // 消
				}
			} else if (s.equalsIgnoreCase("earth")) { // 屬性變更「地系列習」
				if (pc.isElf()) {
					if (pc.getElfAttr() != 0) {
						return;
					}
					pc.setElfAttr(1);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 4)); // 体の隅々に風の精霊力が染みこんできます。
					htmlid = ""; // 消
				}
			} else if (s.equalsIgnoreCase("init")) { // エルフの属性変更「精霊力を除去する」
				if (pc.isElf()) {
					if (pc.getElfAttr() == 0) {
						return;
					}
					for (int cnt = 129; cnt <= 168; cnt++) {// 全エルフ魔法をチェック
						final L1Skills l1skills1 = SkillsTable.get()
								.getTemplate(cnt);
						final int skill_attr = l1skills1.getAttr();
						if (skill_attr != 0) {// 無属性魔法以外のエルフ魔法をDBから削除する
							CharSkillReading.get().spellLost(pc.getId(),
									l1skills1.getSkillId());
						}
					}
					// エレメンタルプロテクションによって上昇している属性防御をリセット
					if (pc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
						pc.removeSkillEffect(ELEMENTAL_PROTECTION);
					}
					pc.sendPackets(new S_DelSkill(pc, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255)); // 無属性魔法以外のエルフ魔法を魔法ウィンドウから削除する
					pc.setElfAttr(0);
					pc.save(); // 資料存檔
					pc.sendPackets(new S_ServerMessage(678));
					htmlid = ""; // ウィンドウを消す
				}
			} else if (s.equalsIgnoreCase("exp")) { // 「経験値を回復する」
				if (pc.getExpRes() == 1) {
					int cost = 0;
					final int level = pc.getLevel();
					final int lawful = pc.getLawful();
					if (level < 45) {
						cost = level * level * 100;
					} else {
						cost = level * level * 200;
					}
					if (lawful >= 0) {
						cost = (cost / 2);
					}
					pc.sendPackets(new S_Message_YN(738, String.valueOf(cost))); // 経験値を回復するには%0のアデナが必要です。経験値を回復しますか？
				} else {
					pc.sendPackets(new S_ServerMessage(739)); // 今は経験値を回復することができません。。
					htmlid = ""; // ウィンドウを消す
				}

			} else if (s.equalsIgnoreCase("ent")) {
				this.watchUb(pc, 50038);
				// 「お化け屋敷に入る」
				// 「アルティメット バトルに参加する」または
				// 「観覧モードで闘技場に入る」
				// 「ステータス再分配」
				final int npcId = ((L1NpcInstance) obj).getNpcId();
				if ((npcId == 80085) || (npcId == 80086) || (npcId == 80087)) {
					htmlid = this.enterHauntedHouse(pc);

				} else if ((npcId == 50038) || (npcId == 50042)
						|| (npcId == 50029) || (npcId == 50019)
						|| (npcId == 50062)) { // 副管理人の場合は観戦
					htmlid = this.watchUb(pc, npcId);

				} else {
					htmlid = this.enterUb(pc, npcId);
				}
			} else if (s.equalsIgnoreCase("par")) { // UB関連「アルティメット バトルに参加する」
													// 副管理人経由
				htmlid = this.enterUb(pc, ((L1NpcInstance) obj).getNpcId());
			} else if (s.equalsIgnoreCase("info")) { // 「情報を確認する」「競技情報を確認する」
				final int npcId = ((L1NpcInstance) obj).getNpcId();
				if ((npcId == 80085) || (npcId == 80086) || (npcId == 80087)) {
				} else {
					htmlid = "colos2";
				}
			} else if (s.equalsIgnoreCase("sco")) { // UB關連「高得點者一覽確認」
				htmldata = new String[10];
				htmlid = "colos3";
			}

			else if (s.equalsIgnoreCase("haste")) { // 師
				final L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
				final int npcid = l1npcinstance.getNpcTemplate().get_npcId();
				if (npcid == 70514) {
					pc.sendPackets(new S_ServerMessage(183));
					pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
					pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
					pc.sendPacketsX8(new S_SkillSound(pc.getId(), 755));
					pc.setMoveSpeed(1);
					pc.setSkillEffect(STATUS_HASTE, 1600 * 1000);
					htmlid = ""; // 消
				}
			}
			// 變身專門家
			else if (s.equalsIgnoreCase("skeleton nbmorph")) {
				this.poly(client, 2374);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
				this.poly(client, 3874);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("shelob nbmorph")) {
				this.poly(client, 95);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("ghoul nbmorph")) {
				this.poly(client, 3873);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("ghast nbmorph")) {
				this.poly(client, 3875);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
				this.poly(client, 3868);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
				this.poly(client, 2376);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("troll nbmorph")) {
				this.poly(client, 3878);
				htmlid = ""; // 消
			}
			// 長老 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
				// 「手紙受取」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41060, 1); // 推薦書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfnoname9";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41060, 1)) {
						htmlid = "orcfnoname11";
					}
				}
			}
			// - 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
				// 「、場所送」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "orcfbuwoo2";
				}
			}
			// 調查團長  
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
				// 「」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41065, 1); // 調查團證書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfnoa4";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41065, 1)) {
						htmlid = "orcfnoa7";
					}
				}
			}
			//  
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
				// 「調查」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41064, 1); // 調查團證書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfhuwoomo4";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41064, 1)) {
						htmlid = "orcfhuwoomo6";
					}
				}
			}
			//  
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
				// 「調查」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41062, 1); // 調查團證書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfbakumo4";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41062, 1)) {
						htmlid = "orcfbakumo6";
					}
				}
			}
			// - 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
				// 「調查」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41063, 1); // 調查團證書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfbuka4";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41063, 1)) {
						htmlid = "orcfbuka6";
					}
				}
			}
			// - 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
				// 「調查」
				if (s.equalsIgnoreCase("A")) {
					final L1NpcInstance npc = (L1NpcInstance) obj;
					final L1ItemInstance item = pc.getInventory().storeItem(
							41061, 1); // 調查團證書
					final String npcName = npc.getNpcTemplate().get_name();
					final String itemName = item.getItem().getNameId();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
					htmlid = "orcfkame4";
				}
				// 「調查」
				else if (s.equalsIgnoreCase("Z")) {
					if (pc.getInventory().consumeItem(41061, 1)) {
						htmlid = "orcfkame6";
					}
				}
			}
			// 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
				// 「入」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "usender2";
				}
			}
			// 治安團長
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
				// 「私手伝」
				if (s.equalsIgnoreCase("teleportURL")) {
					htmlid = "amisoo2";
				}
			}
			// 空間歪
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
				// 「」
				if (s.equalsIgnoreCase("2")) {
					htmlid = ""; // 消
				}
			}
			// 搖者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
				// 「意志迎入」
				if (s.equalsIgnoreCase("1")) {
					if (pc.getKarma() <= -10000000) {
						pc.setKarma(1000000);
						// 笑聲腦裡強打。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "betray13";
					}
				}
			}
			// 執政官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
				// 「私靈魂樣…」
				if (s.equalsIgnoreCase("1")) {
					htmlid = "meet105";
				}
				// 「私靈魂樣忠誠誓…」
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getInventory().checkItem(40718)) { // 欠片
						htmlid = "meet106";
					} else {
						htmlid = "meet110";
					}
				}
				// 「欠片1個捧」
				else if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().consumeItem(40718, 1)) {
						pc.addKarma((int) (-100 * ConfigRate.RATE_KARMA));
						// 姿近感。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet107";
					} else {
						htmlid = "meet104";
					}
				}
				// 「欠片10個捧」
				else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().consumeItem(40718, 10)) {
						pc.addKarma((int) (-1000 * ConfigRate.RATE_KARMA));
						// 姿近感。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet108";
					} else {
						htmlid = "meet104";
					}
				}
				// 「欠片100個捧」
				else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().consumeItem(40718, 100)) {
						pc.addKarma((int) (-10000 * ConfigRate.RATE_KARMA));
						// 姿近感。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "meet109";
					} else {
						htmlid = "meet104";
					}
				}
				// 「樣會」
				else if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(40615) // 影神殿2階鍵
							|| pc.getInventory().checkItem(40616)) { // 影神殿3階鍵
						htmlid = "";
					} else {
						L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5,
								true);
					}
				}
			}
			// 軍師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80052) {
				// 私力・・・
				if (s.equalsIgnoreCase("a")) {
					if (pc.hasSkillEffect(DE_LV30)) {
						pc.removeSkillEffect(DE_LV30);
					}
					if (pc.hasSkillEffect(CKEW_LV50)) {
						pc.removeSkillEffect(CKEW_LV50);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
						// 沒有任何事情發生
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						pc.setSkillEffect(STATUS_CURSE_BARLOG, 1500 * 1000);
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7246));
						// pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(),
						// 1020));
						// pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750));
						pc.sendPackets(new S_ServerMessage(1127));
					}
				}
			}
			// 鍛冶屋
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
				final int karmaLevel = pc.getKarmaLevel();
				// 「材料用意」
				if (s.equalsIgnoreCase("a")) {
					//   / 鍛冶屋
					int aliceMaterialId = 0;
					final int[] aliceMaterialIdList = { 40991, 196, 197, 198,
							199, 200, 201, 202, 203 };
					for (final int id : aliceMaterialIdList) {
						if (pc.getInventory().checkItem(id)) {
							aliceMaterialId = id;
							break;
						}
					}
					if (aliceMaterialId == 0) {
						htmlid = "alice_no";
					} else if (aliceMaterialId == 40991) {
						if (karmaLevel <= -1) {
							materials = new int[] { 40995, 40718, 40991 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 196 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_1";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "aliceyet";
						}
					} else if (aliceMaterialId == 196) {
						if (karmaLevel <= -2) {
							materials = new int[] { 40997, 40718, 196 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 197 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_2";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_1";
						}
					} else if (aliceMaterialId == 197) {
						if (karmaLevel <= -3) {
							materials = new int[] { 40990, 40718, 197 };
							counts = new int[] { 100, 100, 1 };
							createitem = new int[] { 198 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_3";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_2";
						}
					} else if (aliceMaterialId == 198) {
						if (karmaLevel <= -4) {
							materials = new int[] { 40994, 40718, 198 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 199 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_4";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_3";
						}
					} else if (aliceMaterialId == 199) {
						if (karmaLevel <= -5) {
							materials = new int[] { 40993, 40718, 199 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 200 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_5";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_4";
						}
					} else if (aliceMaterialId == 200) {
						if (karmaLevel <= -6) {
							materials = new int[] { 40998, 40718, 200 };
							counts = new int[] { 50, 100, 1 };
							createitem = new int[] { 201 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_6";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_5";
						}
					} else if (aliceMaterialId == 201) {
						if (karmaLevel <= -7) {
							materials = new int[] { 40996, 40718, 201 };
							counts = new int[] { 10, 100, 1 };
							createitem = new int[] { 202 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_7";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_6";
						}
					} else if (aliceMaterialId == 202) {
						if (karmaLevel <= -8) {
							materials = new int[] { 40992, 40718, 202 };
							counts = new int[] { 10, 100, 1 };
							createitem = new int[] { 203 };
							createcount = new int[] { 1 };
							success_htmlid = "alice_8";
							failure_htmlid = "alice_no";
						} else {
							htmlid = "alice_7";
						}
					} else if (aliceMaterialId == 203) {
						htmlid = "alice_8";
					}
				}
			}
			// 補佐官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				htmlid = this.getYaheeAmulet(pc, npc, s);
			}
			// 業管理者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (pc.getKarma() <= -10000000) {
					this.getBloodCrystalByKarma(pc, npc, s);
				}
				htmlid = "";
			}
			// 次元扉(部屋)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
				// 「中入」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(40921)) { // 元素支配者
						L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2,
								true);
					} else {
						htmlid = "gpass02";
					}
				}
			}
			// 執政官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
				// 「私永遠主樣…」
				if (s.equalsIgnoreCase("1")) {
					htmlid = "meet005";
				}
				// 「私靈魂樣忠誠誓…」
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getInventory().checkItem(40678)) { // 欠片
						htmlid = "meet006";
					} else {
						htmlid = "meet010";
					}
				}
				// 「欠片1個捧」
				else if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().consumeItem(40678, 1)) {
						pc.addKarma((int) (100 * ConfigRate.RATE_KARMA));
						// 笑聲腦裡強打。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet007";
					} else {
						htmlid = "meet004";
					}
				}
				// 「欠片10個捧」
				else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().consumeItem(40678, 10)) {
						pc.addKarma((int) (1000 * ConfigRate.RATE_KARMA));
						// 笑聲腦裡強打。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet008";
					} else {
						htmlid = "meet004";
					}
				}
				// 「欠片100個捧」
				else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().consumeItem(40678, 100)) {
						pc.addKarma((int) (10000 * ConfigRate.RATE_KARMA));
						// 笑聲腦裡強打。
						pc.sendPackets(new S_ServerMessage(1078));
						htmlid = "meet009";
					} else {
						htmlid = "meet004";
					}
				}
				// 「樣會」
				else if (s.equalsIgnoreCase("d")) {
					if (pc.getInventory().checkItem(40909) // 地通行證
							|| pc.getInventory().checkItem(40910) // 水通行證
							|| pc.getInventory().checkItem(40911) // 火通行證
							|| pc.getInventory().checkItem(40912) // 風通行證
							|| pc.getInventory().checkItem(40913) // 地印章
							|| pc.getInventory().checkItem(40914) // 水印章
							|| pc.getInventory().checkItem(40915) // 火印章
							|| pc.getInventory().checkItem(40916) // 風印章
							|| pc.getInventory().checkItem(40917) // 地支配者
							|| pc.getInventory().checkItem(40918) // 水支配者
							|| pc.getInventory().checkItem(40919) // 火支配者
							|| pc.getInventory().checkItem(40920) // 風支配者
							|| pc.getInventory().checkItem(40921)) { // 元素支配者
						htmlid = "";
					} else {
						L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2,
								true);
					}
				}
			}
			// 搖者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
				// 「意志受入」
				if (s.equalsIgnoreCase("1")) {
					if (pc.getKarma() >= 10000000) {
						pc.setKarma(-1000000);
						// 姿近感。
						pc.sendPackets(new S_ServerMessage(1079));
						htmlid = "betray03";
					}
				}
			}
			// 補佐官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				htmlid = this.getBarlogEarring(pc, npc, s);
			}
			// 軍師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80073) {
				// 私力・・・
				if (s.equalsIgnoreCase("a")) {
					if (pc.hasSkillEffect(DE_LV30)) {
						pc.removeSkillEffect(DE_LV30);
					}
					if (pc.hasSkillEffect(CKEW_LV50)) {
						pc.removeSkillEffect(CKEW_LV50);
					}
					if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
						// 沒有任何事情發生
						pc.sendPackets(new S_ServerMessage(79));

					} else {
						pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7247));
						// pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(),
						// 1020));
						// pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750));
						pc.sendPackets(new S_ServerMessage(1127));
					}
				}
			}
			// 鍛冶屋
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
				final int karmaLevel = pc.getKarmaLevel();
				if (s.equalsIgnoreCase("0")) {
					htmlid = "lsmitha";
				} else if (s.equalsIgnoreCase("1")) {
					htmlid = "lsmithb";
				} else if (s.equalsIgnoreCase("2")) {
					htmlid = "lsmithc";
				} else if (s.equalsIgnoreCase("3")) {
					htmlid = "lsmithd";
				} else if (s.equalsIgnoreCase("4")) {
					htmlid = "lsmithe";
				} else if (s.equalsIgnoreCase("5")) {
					htmlid = "lsmithf";
				} else if (s.equalsIgnoreCase("6")) {
					htmlid = "";
				} else if (s.equalsIgnoreCase("7")) {
					htmlid = "lsmithg";
				} else if (s.equalsIgnoreCase("8")) {
					htmlid = "lsmithh";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("a") && (karmaLevel >= 1)) {
					materials = new int[] { 20158, 40669, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20083 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithaa";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("b") && (karmaLevel >= 2)) {
					materials = new int[] { 20144, 40672, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20131 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithbb";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("c") && (karmaLevel >= 3)) {
					materials = new int[] { 20075, 40671, 40678 };
					counts = new int[] { 1, 50, 100 };
					createitem = new int[] { 20069 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithcc";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("d") && (karmaLevel >= 4)) {
					materials = new int[] { 20183, 40674, 40678 };
					counts = new int[] { 1, 20, 100 };
					createitem = new int[] { 20179 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithdd";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("e") && (karmaLevel >= 5)) {
					materials = new int[] { 20190, 40674, 40678 };
					counts = new int[] { 1, 40, 100 };
					createitem = new int[] { 20209 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithee";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("f") && (karmaLevel >= 6)) {
					materials = new int[] { 20078, 40674, 40678 };
					counts = new int[] { 1, 5, 100 };
					createitem = new int[] { 20290 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithff";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("g") && (karmaLevel >= 7)) {
					materials = new int[] { 20078, 40670, 40678 };
					counts = new int[] { 1, 1, 100 };
					createitem = new int[] { 20261 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithgg";
				}
				//  / 鍛冶屋
				else if (s.equalsIgnoreCase("h") && (karmaLevel >= 8)) {
					materials = new int[] { 40719, 40673, 40678 };
					counts = new int[] { 1, 1, 100 };
					createitem = new int[] { 20031 };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = "lsmithhh";
				}
			}
			// 業管理者
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
				final L1NpcInstance npc = (L1NpcInstance) obj;
				if (pc.getKarma() >= 10000000) {
					this.getSoulCrystalByKarma(pc, npc, s);
				}
				htmlid = "";
			}
			// 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
				htmlid = this.karmaLevelToHtmlId(pc.getKarmaLevel());
				htmldata = new String[] { String.valueOf(pc.getKarmaPercent()) };
			}
			// 次元扉(土風水火)
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062)) {
				htmlid = this.talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
			}

			// 最近物價
			// 、、、、
			else if (s.equalsIgnoreCase("pandora6")
					|| s.equalsIgnoreCase("cold6")
					|| s.equalsIgnoreCase("balsim3")
					|| s.equalsIgnoreCase("mellin3")
					|| s.equalsIgnoreCase("glen3")) {
				htmlid = s;
				final int npcid = ((L1NpcInstance) obj).getNpcTemplate()
						.get_npcId();
				final int taxRatesCastle = L1CastleLocation
						.getCastleTaxRateByNpcId(npcid);
				htmldata = new String[] { String.valueOf(taxRatesCastle) };
			}
			// （村住民登錄）
			else if (s.equalsIgnoreCase("set")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate()
							.get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);

					if ((town_id >= 1) && (town_id <= 10)) {
						if (pc.getHomeTownId() == -1) {
							// \f1新住民登錄行時間。時間置登錄。
							pc.sendPackets(new S_ServerMessage(759));
							htmlid = "";
						} else if (pc.getHomeTownId() > 0) {
							// 既登錄
							if (pc.getHomeTownId() != town_id) {
								final L1Town town = TownReading.get()
										.getTownTable(pc.getHomeTownId());
								if (town != null) {
									// 現在、住民登錄場所%0。
									pc.sendPackets(new S_ServerMessage(758,
											town.get_name()));
								}
								htmlid = "";
							} else {
								// ？
								htmlid = "";
							}
						} else if (pc.getHomeTownId() == 0) {
							// 登錄
							if (pc.getLevel() < 10) {
								// \f1住民登錄10以上。
								pc.sendPackets(new S_ServerMessage(757));
								htmlid = "";
							} else {
								final int level = pc.getLevel();
								final int cost = level * level * 10;
								if (pc.getInventory().consumeItem(
										L1ItemId.ADENA, cost)) {
									pc.setHomeTownId(town_id);
									pc.setContribution(0); // 念
									pc.save();
								} else {
									// 不足。
									pc.sendPackets(new S_ServerMessage(337,
											"$4"));
								}
								htmlid = "";
							}
						}
					}
				}
			}
			// （住民登錄取消）
			else if (s.equalsIgnoreCase("clear")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate()
							.get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);
					if (town_id > 0) {
						if (pc.getHomeTownId() > 0) {
							if (pc.getHomeTownId() == town_id) {
								pc.setHomeTownId(-1);
								pc.setContribution(0); // 貢獻度
								pc.save();
							} else {
								// \f1他村住民。
								pc.sendPackets(new S_ServerMessage(756));
							}
						}
						htmlid = "";
					}
				}
			}
			// （村村長誰聞）
			else if (s.equalsIgnoreCase("ask")) {
				if (obj instanceof L1NpcInstance) {
					final int npcid = ((L1NpcInstance) obj).getNpcTemplate()
							.get_npcId();
					final int town_id = L1TownLocation.getTownIdByNpcid(npcid);

					if ((town_id >= 1) && (town_id <= 10)) {
						final L1Town town = TownReading.get().getTownTable(
								town_id);
						final String leader = town.get_leader_name();
						if ((leader != null) && (leader.length() != 0)) {
							htmlid = "owner";
							htmldata = new String[] { leader };
						} else {
							htmlid = "noowner";
						}
					}
				}
			}
			// 
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876)) {
				// （收入關報告）
				if (s.equalsIgnoreCase("r")) {
					if (obj instanceof L1NpcInstance) {
						final int npcid = ((L1NpcInstance) obj)
								.getNpcTemplate().get_npcId();
						final int town_id = L1TownLocation
								.getTownIdByNpcid(npcid);
					}
				}
				// （稅率變更）
				else if (s.equalsIgnoreCase("t")) {

				}
				// （報酬）
				else if (s.equalsIgnoreCase("c")) {

				}
			}

			// 治療師（歌島中：ＨＰ回復）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
				// 治療受("fullheal"來？)
				if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
					final int hp = _random.nextInt(21) + 70;
					pc.setCurrentHp(pc.getCurrentHp() + hp);

					// 你覺得舒服多了訊息
					pc.sendPackets(new S_PacketBoxHpMsg());
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));

					pc.sendPackets(new S_HPUpdate(pc));
					htmlid = ""; // 消
				}
			}
			// 治療師（訓練場：HPMP回復）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71037) {
				if (s.equalsIgnoreCase("0")) {
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());

					// 你覺得舒服多了訊息
					pc.sendPackets(new S_PacketBoxHpMsg());
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));

					pc.sendPackets(new S_HPUpdate(pc));
					pc.sendPackets(new S_MPUpdate(pc));
				}
			}
			// 治療師（西部）
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71030) {
				if (s.equalsIgnoreCase("fullheal")) {
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
						pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
						pc.setCurrentHp(pc.getMaxHp());
						pc.setCurrentMp(pc.getMaxMp());

						// 你覺得舒服多了訊息
						pc.sendPackets(new S_PacketBoxHpMsg());
						pc.sendPackets(new S_SkillSound(pc.getId(), 830));

						pc.sendPackets(new S_HPUpdate(pc));
						pc.sendPackets(new S_MPUpdate(pc));

						if (pc.isInParty()) { // 中
							pc.getParty().updateMiniHP(pc);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
					}
				}
			}
			// 師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71002) {
				// 魔法
				if (s.equalsIgnoreCase("0")) {
					if (pc.getLevel() <= 13) {
						final L1SkillUse skillUse = new L1SkillUse();
						skillUse.handleCommands(pc, CANCELLATION, pc.getId(),
								pc.getX(), pc.getY(), 0,
								L1SkillUse.TYPE_NPCBUFF, (L1NpcInstance) obj);
						htmlid = ""; // 消
					}
				}
			}
			// (歌島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
				if (s.equalsIgnoreCase("0")) {
					final int[] item_ids = { 41225, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
					}
					htmlid = "jpe0083";
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
				// 受取
				if (s.equalsIgnoreCase("0")) {
					final int[] item_ids = { 40701, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
					}
					pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 1);
					htmlid = "lukein8";
				} else if (s.equalsIgnoreCase("2")) {
					htmlid = "lukein12";
					pc.getQuest().set_step(L1PcQuest.QUEST_RESTA, 3);
				}
			}
			// 小箱-1番目
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
				if (s.equalsIgnoreCase("0")) {
					materials = new int[] { 40701 }; // 小寶地圖
					counts = new int[] { 1 };
					createitem = new int[] { 40702 }; // 小袋
					createcount = new int[] { 1 };
					htmlid = "maptbox1";
					pc.getQuest().set_end(L1PcQuest.QUEST_TBOX1);
					final int[] nextbox = { 1, 2, 3 };
					final int pid = _random.nextInt(nextbox.length);
					final int nb = nextbox[pid];
					if (nb == 1) { // b地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 2);
					} else if (nb == 2) { // c地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 3);
					} else if (nb == 3) { // d地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 4);
					}
				}
			}
			// 小箱-2番目
			else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065)
					|| (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066)) {
				if (s.equalsIgnoreCase("0")) {
					materials = new int[] { 40701 }; // 小寶地圖
					counts = new int[] { 1 };
					createitem = new int[] { 40702 }; // 小袋
					createcount = new int[] { 1 };
					htmlid = "maptbox1";
					pc.getQuest().set_end(L1PcQuest.QUEST_TBOX2);
					final int[] nextbox2 = { 1, 2, 3, 4, 5, 6 };
					final int pid = _random.nextInt(nextbox2.length);
					final int nb2 = nextbox2[pid];
					if (nb2 == 1) { // e地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 5);
					} else if (nb2 == 2) { // f地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 6);
					} else if (nb2 == 3) { // g地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 7);
					} else if (nb2 == 4) { // h地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 8);
					} else if (nb2 == 5) { // i地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 9);
					} else if (nb2 == 6) { // j地點
						pc.getQuest().set_step(L1PcQuest.QUEST_LUKEIN1, 10);
					}
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
				// 息子搜
				if (s.equalsIgnoreCase("a")) {
					pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ, 1);
					htmlid = "SIMIZZ7";
				} else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkItem(40661)
							&& pc.getInventory().checkItem(40662)
							&& pc.getInventory().checkItem(40663)) {
						htmlid = "SIMIZZ8";
						pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ, 2);
						materials = new int[] { 40661, 40662, 40663 };
						counts = new int[] { 1, 1, 1 };
						createitem = new int[] { 20044 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "SIMIZZ9";
					}
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "SIMIZZ12";
					pc.getQuest().set_step(L1PcQuest.QUEST_SIMIZZ,
							L1PcQuest.QUEST_END);
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
				// 聞
				if (s.equalsIgnoreCase("3")) {
					htmlid = "doil4";
				} else if (s.equalsIgnoreCase("6")) {
					htmlid = "doil6";
				} else if (s.equalsIgnoreCase("1")) {
					if (pc.getInventory().checkItem(40714)) {
						htmlid = "doil8";
						materials = new int[] { 40714 };
						counts = new int[] { 1 };
						createitem = new int[] { 40647 };
						createcount = new int[] { 1 };
						pc.getQuest().set_step(L1PcQuest.QUEST_DOIL,
								L1PcQuest.QUEST_END);
					} else {
						htmlid = "doil7";
					}
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
				// 賴受入
				if (s.equalsIgnoreCase("A")) {
					htmlid = "rudian6";
					final int[] item_ids = { 40700 };
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
					}
					pc.getQuest().set_step(L1PcQuest.QUEST_RUDIAN, 1);
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(40710)) {
						htmlid = "rudian8";
						materials = new int[] { 40700, 40710 };
						counts = new int[] { 1, 1 };
						createitem = new int[] { 40647 };
						createcount = new int[] { 1 };
						pc.getQuest().set_step(L1PcQuest.QUEST_RUDIAN,
								L1PcQuest.QUEST_END);
					} else {
						htmlid = "rudian9";
					}
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
				// 仲間
				if (s.equalsIgnoreCase("A")) {
					if (pc.getQuest().get_step(L1PcQuest.QUEST_RUDIAN) == L1PcQuest.QUEST_END) {
						htmlid = "resta6";
					} else {
						htmlid = "resta4";
					}
				} else if (s.equalsIgnoreCase("B")) {
					htmlid = "resta10";
					pc.getQuest().set_step(L1PcQuest.QUEST_RESTA, 2);
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
				// 地圖組合
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40647, 3)) {
						htmlid = "cadmus6";
						pc.getInventory().consumeItem(40647, 3);
						pc.getQuest().set_step(L1PcQuest.QUEST_CADMUS, 2);
					} else {
						htmlid = "cadmus5";
						pc.getQuest().set_step(L1PcQuest.QUEST_CADMUS, 1);
					}
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
				if (s.equalsIgnoreCase("a")) {
					htmlid = "kamyla7";
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 1);
				} else if (s.equalsIgnoreCase("c")) {
					htmlid = "kamyla10";
					pc.getInventory().consumeItem(40644, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 3);
				} else if (s.equalsIgnoreCase("e")) {
					htmlid = "kamyla13";
					pc.getInventory().consumeItem(40630, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 4);
				} else if (s.equalsIgnoreCase("i")) {
					htmlid = "kamyla25";
				} else if (s.equalsIgnoreCase("b")) { // （迷宮）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 1) {
						L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5,
								true);
					}
				} else if (s.equalsIgnoreCase("d")) { // （閉牢）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 3) {
						L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5,
								true);
					}
				} else if (s.equalsIgnoreCase("f")) { // （地下牢）
					if (pc.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 4) {
						L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5,
								true);
					}
				}
			}
			// (海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
				// 潔白證明
				if (s.equalsIgnoreCase("a")) {
					htmlid = "francu10";
					final int[] item_ids = { 40644 };
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_KAMYLA, 2);
					}
				}
			}
			// 試練2(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
				// 、武器
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					final int[] item_ids = { 246, 247, 248, 249, 40660 };
					final int[] item_amounts = { 1, 1, 1, 1, 5 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 1);
					}
				} else if (s.equalsIgnoreCase("b")) {
					if (pc.getInventory().checkEquipped(246)
							|| pc.getInventory().checkEquipped(247)
							|| pc.getInventory().checkEquipped(248)
							|| pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else if (pc.getInventory().checkItem(40660)) {
						htmlid = "jcrystal4";
					} else {
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 2);
						L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4,
								true);
					}
				} else if (s.equalsIgnoreCase("c")) {
					if (pc.getInventory().checkEquipped(246)
							|| pc.getInventory().checkEquipped(247)
							|| pc.getInventory().checkEquipped(248)
							|| pc.getInventory().checkEquipped(249)) {
						htmlid = "jcrystal5";
					} else {
						pc.getInventory().checkItem(40660);
						final L1ItemInstance l1iteminstance = pc.getInventory()
								.findItemId(40660);
						final long sc = l1iteminstance.getCount();
						if (sc > 0) {
							pc.getInventory().consumeItem(40660, sc);
						} else {
						}
						pc.getInventory().consumeItem(246, 1);
						pc.getInventory().consumeItem(247, 1);
						pc.getInventory().consumeItem(248, 1);
						pc.getInventory().consumeItem(249, 1);
						pc.getInventory().consumeItem(40620, 1);
						pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL, 0);
						L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4,
								true);
					}
				}
			}
			// 試練2(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
				// ！！
				if (s.equalsIgnoreCase("a")) {
					htmlid = "";
					pc.getInventory().consumeItem(40654, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_CRYSTAL,
							L1PcQuest.QUEST_END);
					L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
				}
			}
			// 長老(海賊島)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
				// 戰士今？
				if (s.equalsIgnoreCase("A")) {
					htmlid = "lelder5";
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, 1);
					// 寶取戾
				} else if (s.equalsIgnoreCase("B")) {
					htmlid = "lelder10";
					pc.getInventory().consumeItem(40633, 1);
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, 3);
				} else if (s.equalsIgnoreCase("C")) {
					htmlid = "lelder13";
					if (pc.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == L1PcQuest.QUEST_END) {
					}
					materials = new int[] { 40634 };
					counts = new int[] { 1 };
					createitem = new int[] { 20167 }; // 
					createcount = new int[] { 1 };
					pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD,
							L1PcQuest.QUEST_END);
				}
			}
			// 占星術師
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
				// 魂契約結
				if (s.equalsIgnoreCase("0")) {
					if (!pc.getInventory().checkItem(41312)) { // 占星術師壺
						final L1ItemInstance item = pc.getInventory()
								.storeItem(41312, 1);
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getNameId())); // \f1%0%1。
							pc.getQuest().set_step(L1PcQuest.QUEST_KEPLISHA,
									L1PcQuest.QUEST_END);
						}
						htmlid = "keplisha7";
					}
				}
				// 援助金出運勢見
				else if (s.equalsIgnoreCase("1")) {
					if (!pc.getInventory().checkItem(41314)) { // 占星術師守
						if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
							materials = new int[] { L1ItemId.ADENA, 41313 }; // 、占星術師玉
							counts = new int[] { 1000, 1 };
							createitem = new int[] { 41314 }; // 占星術師守
							createcount = new int[] { 1 };
							final int htmlA = _random.nextInt(3) + 1;
							final int htmlB = _random.nextInt(100) + 1;
							switch (htmlA) {
							case 1:
								htmlid = "horosa" + htmlB; // horosa1 ~
								// horosa100
								break;
							case 2:
								htmlid = "horosb" + htmlB; // horosb1 ~
								// horosb100
								break;
							case 3:
								htmlid = "horosc" + htmlB; // horosc1 ~
								// horosc100
								break;
							default:
								break;
							}
						} else {
							htmlid = "keplisha8";
						}
					}
				}
				// 祝福受
				else if (s.equalsIgnoreCase("2")) {
					if (pc.getTempCharGfx() != pc.getClassId()) {
						htmlid = "keplisha9";
					} else {
						if (pc.getInventory().checkItem(41314)) { // 占星術師守
							pc.getInventory().consumeItem(41314, 1); // 占星術師守
							final int html = _random.nextInt(9) + 1;
							final int PolyId = 6180 + _random.nextInt(64);
							this.polyByKeplisha(client, PolyId);
							switch (html) {
							case 1:
								htmlid = "horomon11";
								break;
							case 2:
								htmlid = "horomon12";
								break;
							case 3:
								htmlid = "horomon13";
								break;
							case 4:
								htmlid = "horomon21";
								break;
							case 5:
								htmlid = "horomon22";
								break;
							case 6:
								htmlid = "horomon23";
								break;
							case 7:
								htmlid = "horomon31";
								break;
							case 8:
								htmlid = "horomon32";
								break;
							case 9:
								htmlid = "horomon33";
								break;
							default:
								break;
							}
						}
					}
				}
				// 壺割契約破棄
				else if (s.equalsIgnoreCase("3")) {
					if (pc.getInventory().checkItem(41312)) { // 占星術師壺
						pc.getInventory().consumeItem(41312, 1);
						htmlid = "";
					}
					if (pc.getInventory().checkItem(41313)) { // 占星術師玉
						pc.getInventory().consumeItem(41313, 1);
						htmlid = "";
					}
					if (pc.getInventory().checkItem(41314)) { // 占星術師守
						pc.getInventory().consumeItem(41314, 1);
						htmlid = "";
					}
				}
			}

			// 怪商人 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {
				// 「資源」
				if (s.equalsIgnoreCase("q")) {
					if (pc.getInventory().checkItem(41356, 1)) {
						htmlid = "rparum4";
					} else {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(41356, 1);
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.get_name(), item.getItem()
											.getNameId())); // \f1%0%1。
						}
						htmlid = "rparum3";
					}
				}
			}
			// 騎馬團員
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
				// 恢復軍馬頭盔可用次數
				if (s.equalsIgnoreCase("c")) {
					if (pc.isCrown()) {
						if (pc.getInventory().checkItem(20383, 1)) {// 軍馬頭盔
							if (pc.getInventory().checkItem(L1ItemId.ADENA,
									100000)) {
								final L1ItemInstance item = pc.getInventory()
										.findItemId(20383);
								if ((item != null)
										&& (item.getChargeCount() != 50)) {
									item.setChargeCount(50);
									pc.getInventory().updateItem(item,
											L1PcInventory.COL_CHARGE_COUNT);
									pc.getInventory().consumeItem(
											L1ItemId.ADENA, 100000);
									htmlid = "";
								}
							} else {
								pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
							}
						}
					}
				}
			}
			// 補佐官
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
				// 「。私協力」
				if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(41007, 1)) { // 命令書：靈魂安息
						htmlid = "eris10";
					} else {
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory()
								.storeItem(41007, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "eris6";
					}
				} else if (s.equalsIgnoreCase("C")) {
					if (pc.getInventory().checkItem(41009, 1)) { // 命令書：同盟意思
						htmlid = "eris10";
					} else {
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory()
								.storeItem(41009, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "eris8";
					}
				} else if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(41007, 1)) { // 命令書：靈魂安息
						if (pc.getInventory().checkItem(40969, 20)) { // 魂結晶體
							htmlid = "eris18";
							materials = new int[] { 40969, 41007 };
							counts = new int[] { 20, 1 };
							createitem = new int[] { 41008 }; // 
							createcount = new int[] { 1 };
						} else {
							htmlid = "eris5";
						}
					} else {
						htmlid = "eris2";
					}
				} else if (s.equalsIgnoreCase("E")) {
					if (pc.getInventory().checkItem(41010, 1)) { // 推薦書
						htmlid = "eris19";
					} else {
						htmlid = "eris7";
					}
				} else if (s.equalsIgnoreCase("D")) {
					if (pc.getInventory().checkItem(41010, 1)) { // 推薦書
						htmlid = "eris19";
					} else {
						if (pc.getInventory().checkItem(41009, 1)) { // 命令書：同盟意思
							if (pc.getInventory().checkItem(40959, 1)) { // 冥法軍王印章
								htmlid = "eris17";
								materials = new int[] { 40959, 41009 }; // 冥法軍王印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40960, 1)) { // 魔靈軍王印章
								htmlid = "eris16";
								materials = new int[] { 40960, 41009 }; // 魔靈軍王印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40961, 1)) { // 魔獸靈軍王印章
								htmlid = "eris15";
								materials = new int[] { 40961, 41009 }; // 魔獸軍王印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王印章
								htmlid = "eris14";
								materials = new int[] { 40962, 41009 }; // 暗殺軍王印章
								counts = new int[] { 1, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40635, 10)) { // 魔靈軍
								htmlid = "eris12";
								materials = new int[] { 40635, 41009 }; // 魔靈軍
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40638, 10)) { // 魔獸軍
								htmlid = "eris11";
								materials = new int[] { 40638, 41009 }; // 魔靈軍
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40642, 10)) { // 冥法軍
								htmlid = "eris13";
								materials = new int[] { 40642, 41009 }; // 冥法軍
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else if (pc.getInventory().checkItem(40667, 10)) { // 暗殺軍
								htmlid = "eris13";
								materials = new int[] { 40667, 41009 }; // 暗殺軍
								counts = new int[] { 10, 1 };
								createitem = new int[] { 41010 }; // 推薦書
								createcount = new int[] { 1 };
							} else {
								htmlid = "eris8";
							}
						} else {
							htmlid = "eris7";
						}
					}
				}
			}
			// 倒航海士
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076) {
				if (s.equalsIgnoreCase("A")) {
					final int[] diaryno = { 49082, 49083 };
					final int pid = _random.nextInt(diaryno.length);
					final int di = diaryno[pid];
					if (di == 49082) { // 奇數拔
						htmlid = "voyager6a";
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory()
								.storeItem(di, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
					} else if (di == 49083) { // 偶數拔
						htmlid = "voyager6b";
						final L1NpcInstance npc = (L1NpcInstance) obj;
						final L1ItemInstance item = pc.getInventory()
								.storeItem(di, 1);
						final String npcName = npc.getNpcTemplate().get_name();
						final String itemName = item.getItem().getNameId();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
					}
				}
			}
			// 煉金術師 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71128) {
				if (s.equals("A")) {
					if (pc.getInventory().checkItem(41010, 1)) { // 推薦書
						htmlid = "perita2";
					} else {
						htmlid = "perita3";
					}
				} else if (s.equals("p")) {
					// 咒判別
					if (pc.getInventory().checkItem(40987, 1) // 
							&& pc.getInventory().checkItem(40988, 1) // 
							&& pc.getInventory().checkItem(40989, 1)) { // 
						htmlid = "perita43";
					} else if (pc.getInventory().checkItem(40987, 1) // 
							&& pc.getInventory().checkItem(40989, 1)) { // 
						htmlid = "perita44";
					} else if (pc.getInventory().checkItem(40987, 1) // 
							&& pc.getInventory().checkItem(40988, 1)) { // 
						htmlid = "perita45";
					} else if (pc.getInventory().checkItem(40988, 1) // 
							&& pc.getInventory().checkItem(40989, 1)) { // 
						htmlid = "perita47";
					} else if (pc.getInventory().checkItem(40987, 1)) { // 
						htmlid = "perita46";
					} else if (pc.getInventory().checkItem(40988, 1)) { // 
						htmlid = "perita49";
					} else if (pc.getInventory().checkItem(40987, 1)) { // 
						htmlid = "perita48";
					} else {
						htmlid = "perita50";
					}
				} else if (s.equals("q")) {
					// 判別
					if (pc.getInventory().checkItem(41173, 1) // 
							&& pc.getInventory().checkItem(41174, 1) // 
							&& pc.getInventory().checkItem(41175, 1)) { // 
						htmlid = "perita54";
					} else if (pc.getInventory().checkItem(41173, 1) // 
							&& pc.getInventory().checkItem(41175, 1)) { // 
						htmlid = "perita55";
					} else if (pc.getInventory().checkItem(41173, 1) // 
							&& pc.getInventory().checkItem(41174, 1)) { // 
						htmlid = "perita56";
					} else if (pc.getInventory().checkItem(41174, 1) // 
							&& pc.getInventory().checkItem(41175, 1)) { // 
						htmlid = "perita58";
					} else if (pc.getInventory().checkItem(41174, 1)) { // 
						htmlid = "perita57";
					} else if (pc.getInventory().checkItem(41175, 1)) { // 
						htmlid = "perita60";
					} else if (pc.getInventory().checkItem(41176, 1)) { // 
						htmlid = "perita59";
					} else {
						htmlid = "perita61";
					}
				} else if (s.equals("s")) {
					//  判別
					if (pc.getInventory().checkItem(41161, 1) // 
							&& pc.getInventory().checkItem(41162, 1) // 
							&& pc.getInventory().checkItem(41163, 1)) { // 
						htmlid = "perita62";
					} else if (pc.getInventory().checkItem(41161, 1) // 
							&& pc.getInventory().checkItem(41163, 1)) { // 
						htmlid = "perita63";
					} else if (pc.getInventory().checkItem(41161, 1) // 
							&& pc.getInventory().checkItem(41162, 1)) { // 
						htmlid = "perita64";
					} else if (pc.getInventory().checkItem(41162, 1) // 
							&& pc.getInventory().checkItem(41163, 1)) { // 
						htmlid = "perita66";
					} else if (pc.getInventory().checkItem(41161, 1)) { // 
						htmlid = "perita65";
					} else if (pc.getInventory().checkItem(41162, 1)) { // 
						htmlid = "perita68";
					} else if (pc.getInventory().checkItem(41163, 1)) { // 
						htmlid = "perita67";
					} else {
						htmlid = "perita69";
					}
				} else if (s.equals("B")) {
					// 淨化
					if (pc.getInventory().checkItem(40651, 10) // 火息吹
							&& pc.getInventory().checkItem(40643, 10) // 水息吹
							&& pc.getInventory().checkItem(40618, 10) // 大地息吹
							&& pc.getInventory().checkItem(40645, 10) // 風息吹
							&& pc.getInventory().checkItem(40676, 10) // 闇息吹
							&& pc.getInventory().checkItem(40442, 5) // 胃液
							&& pc.getInventory().checkItem(40051, 1)) { // 高級
						htmlid = "perita7";
						materials = new int[] { 40651, 40643, 40618, 40645,
								40676, 40442, 40051 };
						counts = new int[] { 10, 10, 10, 10, 20, 5, 1 };
						createitem = new int[] { 40925 }; // 淨化
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita8";
					}
				} else if (s.equals("G") || s.equals("h") || s.equals("i")) {
					//  ：１段階
					if (pc.getInventory().checkItem(40651, 5) // 火息吹
							&& pc.getInventory().checkItem(40643, 5) // 水息吹
							&& pc.getInventory().checkItem(40618, 5) // 大地息吹
							&& pc.getInventory().checkItem(40645, 5) // 風息吹
							&& pc.getInventory().checkItem(40676, 5) // 闇息吹
							&& pc.getInventory().checkItem(40675, 5) // 闇礦石
							&& pc.getInventory().checkItem(40049, 3) // 高級
							&& pc.getInventory().checkItem(40051, 1)) { // 高級
						htmlid = "perita27";
						materials = new int[] { 40651, 40643, 40618, 40645,
								40676, 40675, 40049, 40051 };
						counts = new int[] { 5, 5, 5, 5, 10, 10, 3, 1 };
						createitem = new int[] { 40926 }; // ：１段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita28";
					}
				} else if (s.equals("H") || s.equals("j") || s.equals("k")) {
					//  ：２段階
					if (pc.getInventory().checkItem(40651, 10) // 火息吹
							&& pc.getInventory().checkItem(40643, 10) // 水息吹
							&& pc.getInventory().checkItem(40618, 10) // 大地息吹
							&& pc.getInventory().checkItem(40645, 10) // 風息吹
							&& pc.getInventory().checkItem(40676, 20) // 闇息吹
							&& pc.getInventory().checkItem(40675, 10) // 闇礦石
							&& pc.getInventory().checkItem(40048, 3) // 高級
							&& pc.getInventory().checkItem(40051, 1)) { // 高級
						htmlid = "perita29";
						materials = new int[] { 40651, 40643, 40618, 40645,
								40676, 40675, 40048, 40051 };
						counts = new int[] { 10, 10, 10, 10, 20, 10, 3, 1 };
						createitem = new int[] { 40927 }; // ：２段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita30";
					}
				} else if (s.equals("I") || s.equals("l") || s.equals("m")) {
					//  ：３段階
					if (pc.getInventory().checkItem(40651, 20) // 火息吹
							&& pc.getInventory().checkItem(40643, 20) // 水息吹
							&& pc.getInventory().checkItem(40618, 20) // 大地息吹
							&& pc.getInventory().checkItem(40645, 20) // 風息吹
							&& pc.getInventory().checkItem(40676, 30) // 闇息吹
							&& pc.getInventory().checkItem(40675, 10) // 闇礦石
							&& pc.getInventory().checkItem(40050, 3) // 高級
							&& pc.getInventory().checkItem(40051, 1)) { // 高級
						htmlid = "perita31";
						materials = new int[] { 40651, 40643, 40618, 40645,
								40676, 40675, 40050, 40051 };
						counts = new int[] { 20, 20, 20, 20, 30, 10, 3, 1 };
						createitem = new int[] { 40928 }; // ：３段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita32";
					}
				} else if (s.equals("J") || s.equals("n") || s.equals("o")) {
					//  ：４段階
					if (pc.getInventory().checkItem(40651, 30) // 火息吹
							&& pc.getInventory().checkItem(40643, 30) // 水息吹
							&& pc.getInventory().checkItem(40618, 30) // 大地息吹
							&& pc.getInventory().checkItem(40645, 30) // 風息吹
							&& pc.getInventory().checkItem(40676, 30) // 闇息吹
							&& pc.getInventory().checkItem(40675, 20) // 闇礦石
							&& pc.getInventory().checkItem(40052, 1) // 最高級
							&& pc.getInventory().checkItem(40051, 1)) { // 高級
						htmlid = "perita33";
						materials = new int[] { 40651, 40643, 40618, 40645,
								40676, 40675, 40052, 40051 };
						counts = new int[] { 30, 30, 30, 30, 30, 20, 1, 1 };
						createitem = new int[] { 40928 }; // ：４段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita34";
					}
				} else if (s.equals("K")) { // １段階(靈魂)
					int earinga = 0;
					int earingb = 0;
					if (pc.getInventory().checkEquipped(21014)
							|| pc.getInventory().checkEquipped(21006)
							|| pc.getInventory().checkEquipped(21007)) {
						htmlid = "perita36";
					} else if (pc.getInventory().checkItem(21014, 1)) { // 
						earinga = 21014;
						earingb = 41176;
					} else if (pc.getInventory().checkItem(21006, 1)) { // 
						earinga = 21006;
						earingb = 41177;
					} else if (pc.getInventory().checkItem(21007, 1)) { // 
						earinga = 21007;
						earingb = 41178;
					} else {
						htmlid = "perita36";
					}
					if (earinga > 0) {
						materials = new int[] { earinga };
						counts = new int[] { 1 };
						createitem = new int[] { earingb };
						createcount = new int[] { 1 };
					}
				} else if (s.equals("L")) { // ２段階(知惠)
					if (pc.getInventory().checkEquipped(21015)) {
						htmlid = "perita22";
					} else if (pc.getInventory().checkItem(21015, 1)) {
						materials = new int[] { 21015 };
						counts = new int[] { 1 };
						createitem = new int[] { 41179 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita22";
					}
				} else if (s.equals("M")) { // ３段階(真實)
					if (pc.getInventory().checkEquipped(21016)) {
						htmlid = "perita26";
					} else if (pc.getInventory().checkItem(21016, 1)) {
						materials = new int[] { 21016 };
						counts = new int[] { 1 };
						createitem = new int[] { 41182 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita26";
					}
				} else if (s.equals("b")) { // ２段階(情熱)
					if (pc.getInventory().checkEquipped(21009)) {
						htmlid = "perita39";
					} else if (pc.getInventory().checkItem(21009, 1)) {
						materials = new int[] { 21009 };
						counts = new int[] { 1 };
						createitem = new int[] { 41180 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita39";
					}
				} else if (s.equals("d")) { // ３段階(名譽)
					if (pc.getInventory().checkEquipped(21012)) {
						htmlid = "perita41";
					} else if (pc.getInventory().checkItem(21012, 1)) {
						materials = new int[] { 21012 };
						counts = new int[] { 1 };
						createitem = new int[] { 41183 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita41";
					}
				} else if (s.equals("a")) { // ２段階(憤怒)
					if (pc.getInventory().checkEquipped(21008)) {
						htmlid = "perita38";
					} else if (pc.getInventory().checkItem(21008, 1)) {
						materials = new int[] { 21008 };
						counts = new int[] { 1 };
						createitem = new int[] { 41181 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita38";
					}
				} else if (s.equals("c")) { // ３段階(勇猛)
					if (pc.getInventory().checkEquipped(21010)) {
						htmlid = "perita40";
					} else if (pc.getInventory().checkItem(21010, 1)) {
						materials = new int[] { 21010 };
						counts = new int[] { 1 };
						createitem = new int[] { 41184 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "perita40";
					}
				}
			}
			// 寶石細工師 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71129) {
				if (s.equals("Z")) {
					htmlid = "rumtis2";
				} else if (s.equals("Y")) {
					if (pc.getInventory().checkItem(41010, 1)) { // 推薦書
						htmlid = "rumtis3";
					} else {
						htmlid = "rumtis4";
					}
				} else if (s.equals("q")) {
					htmlid = "rumtis92";
				} else if (s.equals("A")) {
					if (pc.getInventory().checkItem(41161, 1)) {
						// 
						htmlid = "rumtis6";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("B")) {
					if (pc.getInventory().checkItem(41164, 1)) {
						// 
						htmlid = "rumtis7";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("C")) {
					if (pc.getInventory().checkItem(41167, 1)) {
						// 
						htmlid = "rumtis8";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("T")) {
					if (pc.getInventory().checkItem(41167, 1)) {
						// 
						htmlid = "rumtis9";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("w")) {
					if (pc.getInventory().checkItem(41162, 1)) {
						// 
						htmlid = "rumtis14";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("x")) {
					if (pc.getInventory().checkItem(41165, 1)) {
						// 
						htmlid = "rumtis15";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("y")) {
					if (pc.getInventory().checkItem(41168, 1)) {
						// 
						htmlid = "rumtis16";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("z")) {
					if (pc.getInventory().checkItem(41171, 1)) {
						// 
						htmlid = "rumtis17";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("U")) {
					if (pc.getInventory().checkItem(41163, 1)) {
						// 
						htmlid = "rumtis10";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("V")) {
					if (pc.getInventory().checkItem(41166, 1)) {
						// 
						htmlid = "rumtis11";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("W")) {
					if (pc.getInventory().checkItem(41169, 1)) {
						// 
						htmlid = "rumtis12";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("X")) {
					if (pc.getInventory().checkItem(41172, 1)) {
						// 
						htmlid = "rumtis13";
					} else {
						htmlid = "rumtis101";
					}
				} else if (s.equals("D") || s.equals("E") || s.equals("F")
						|| s.equals("G")) {
					int insn = 0;
					int bacn = 0;
					int me = 0;
					int mr = 0;
					int mj = 0;
					int an = 0;
					int men = 0;
					int mrn = 0;
					int mjn = 0;
					int ann = 0;
					if (pc.getInventory().checkItem(40959, 1) // 冥法軍王印章
							&& pc.getInventory().checkItem(40960, 1) // 魔靈軍王印章
							&& pc.getInventory().checkItem(40961, 1) // 魔獸軍王印章
							&& pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王印章
						insn = 1;
						me = 40959;
						mr = 40960;
						mj = 40961;
						an = 40962;
						men = 1;
						mrn = 1;
						mjn = 1;
						ann = 1;
					} else if (pc.getInventory().checkItem(40642, 10) // 冥法軍
							&& pc.getInventory().checkItem(40635, 10) // 魔靈軍
							&& pc.getInventory().checkItem(40638, 10) // 魔獸軍
							&& pc.getInventory().checkItem(40667, 10)) { // 暗殺軍
						bacn = 1;
						me = 40642;
						mr = 40635;
						mj = 40638;
						an = 40667;
						men = 10;
						mrn = 10;
						mjn = 10;
						ann = 10;
					}
					if (pc.getInventory().checkItem(40046, 1) // 
							&& pc.getInventory().checkItem(40618, 5) // 大地息吹
							&& pc.getInventory().checkItem(40643, 5) // 水息吹
							&& pc.getInventory().checkItem(40645, 5) // 風息吹
							&& pc.getInventory().checkItem(40651, 5) // 火息吹
							&& pc.getInventory().checkItem(40676, 5)) { // 闇息吹
						if ((insn == 1) || (bacn == 1)) {
							htmlid = "rumtis60";
							materials = new int[] { me, mr, mj, an, 40046,
									40618, 40643, 40651, 40676 };
							counts = new int[] { men, mrn, mjn, ann, 1, 5, 5,
									5, 5, 5 };
							createitem = new int[] { 40926 }; // 加工：１段階
							createcount = new int[] { 1 };
						} else {
							htmlid = "rumtis18";
						}
					}
				}
			}
			// 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71119) {
				// 「歷史書1章8章全部渡」
				if (s.equalsIgnoreCase("request las history book")) {
					materials = new int[] { 41019, 41020, 41021, 41022, 41023,
							41024, 41025, 41026 };
					counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
					createitem = new int[] { 41027 };
					createcount = new int[] { 1 };
					htmlid = "";
				}
			}
			// 長老隨行員
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71170) {
				// 「歷史書渡」
				if (s.equalsIgnoreCase("request las weapon manual")) {
					materials = new int[] { 41027 };
					counts = new int[] { 1 };
					createitem = new int[] { 40965 };
					createcount = new int[] { 1 };
					htmlid = "";
				}
			}
			// 真冥王 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71168) {
				// 「異界魔物場所送」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(41028, 1)) {
						L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6,
								true);
						pc.getInventory().consumeItem(41028, 1);
					}
				}
			}
			// 諜報員(慾望洞窟側)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80067) {
				// 「動搖承諾」
				if (s.equalsIgnoreCase("n")) {
					htmlid = "";
					this.poly(client, 6034);
					final int[] item_ids = { 41132, 41133, 41134 };
					final int[] item_amounts = { 1, 1, 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE, 1);
					}
					// 「任務」
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "minicod09";
					pc.getInventory().consumeItem(41130, 1);
					pc.getInventory().consumeItem(41131, 1);
					// 「初期化」
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "";
					pc.getInventory().consumeItem(41132, 1); // 血痕墮落粉
					pc.getInventory().consumeItem(41133, 1); // 血痕無力粉
					pc.getInventory().consumeItem(41134, 1); // 血痕我執粉
					pc.getInventory().consumeItem(41135, 1); // 墮落精髓
					pc.getInventory().consumeItem(41136, 1); // 無力精髓
					pc.getInventory().consumeItem(41137, 1); // 我執精髓
					pc.getInventory().consumeItem(41138, 1); // 精髓
					pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE, 0);
					// 精髓渡
				} else if (s.equalsIgnoreCase("e")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == L1PcQuest.QUEST_END)
							|| (pc.getKarmaLevel() >= 1)) {
						htmlid = "";
					} else {
						if (pc.getInventory().checkItem(41138)) {
							htmlid = "";
							pc.addKarma((int) (1600 * ConfigRate.RATE_KARMA));
							pc.getInventory().consumeItem(41130, 1); // 血痕契約書
							pc.getInventory().consumeItem(41131, 1); // 血痕指令書
							pc.getInventory().consumeItem(41138, 1); // 精髓
							pc.getQuest().set_step(L1PcQuest.QUEST_DESIRE,
									L1PcQuest.QUEST_END);
						} else {
							htmlid = "minicod04";
						}
					}
					// 
				} else if (s.equalsIgnoreCase("g")) {
					htmlid = "";
					final int[] item_ids = { 41130 }; // 血痕契約書
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
					}
				}
			}
			// 諜報員(影神殿側)
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81202) {
				// 「頭承諾」
				if (s.equalsIgnoreCase("n")) {
					htmlid = "";
					this.poly(client, 6035);
					final int[] item_ids = { 41123, 41124, 41125 };
					final int[] item_amounts = { 1, 1, 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
						pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS, 1);
					}
					// 「任務」
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "minitos09";
					pc.getInventory().consumeItem(41121, 1);
					pc.getInventory().consumeItem(41122, 1);
					// 「初期化」
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "";
					pc.getInventory().consumeItem(41123, 1); // 墮落粉
					pc.getInventory().consumeItem(41124, 1); // 無力粉
					pc.getInventory().consumeItem(41125, 1); // 我執粉
					pc.getInventory().consumeItem(41126, 1); // 血痕墮落精髓
					pc.getInventory().consumeItem(41127, 1); // 血痕無力精髓
					pc.getInventory().consumeItem(41128, 1); // 血痕我執精髓
					pc.getInventory().consumeItem(41129, 1); // 血痕精髓
					pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS, 0);
					// 精髓渡
				} else if (s.equalsIgnoreCase("e")) {
					if ((pc.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == L1PcQuest.QUEST_END)
							|| (pc.getKarmaLevel() >= 1)) {
						htmlid = "";
					} else {
						if (pc.getInventory().checkItem(41129)) {
							htmlid = "";
							pc.addKarma((int) (-1600 * ConfigRate.RATE_KARMA));
							pc.getInventory().consumeItem(41121, 1); // 契約書
							pc.getInventory().consumeItem(41122, 1); // 指令書
							pc.getInventory().consumeItem(41129, 1); // 血痕精髓
							pc.getQuest().set_step(L1PcQuest.QUEST_SHADOWS,
									L1PcQuest.QUEST_END);
						} else {
							htmlid = "minitos04";
						}
					}
					// 素早受取
				} else if (s.equalsIgnoreCase("g")) {
					htmlid = "";
					final int[] item_ids = { 41121 }; // 契約書
					final int[] item_amounts = { 1 };
					for (int i = 0; i < item_ids.length; i++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getNameId()));
					}
				}
			}
			// 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71252) {
				int weapon1 = 0;
				int weapon2 = 0;
				int newWeapon = 0;
				if (s.equalsIgnoreCase("A")) {
					weapon1 = 5; // +7
					weapon2 = 10; // +7
					newWeapon = 259; // 
					htmlid = "joegolem9";
				} else if (s.equalsIgnoreCase("B")) {
					weapon1 = 145; // +7
					weapon2 = 148; // +7
					newWeapon = 260; // 
					htmlid = "joegolem10";
				} else if (s.equalsIgnoreCase("C")) {
					weapon1 = 52; // +7
					weapon2 = 64; // +7
					newWeapon = 262; // 
					htmlid = "joegolem11";
				} else if (s.equalsIgnoreCase("D")) {
					weapon1 = 125; // +7
					weapon2 = 129; // +7
					newWeapon = 261; // 
					htmlid = "joegolem12";
				} else if (s.equalsIgnoreCase("E")) {
					weapon1 = 99; // +7
					weapon2 = 104; // +7
					newWeapon = 263; // 
					htmlid = "joegolem13";
				} else if (s.equalsIgnoreCase("F")) {
					weapon1 = 32; // +7
					weapon2 = 42; // +7
					newWeapon = 264; // 
					htmlid = "joegolem14";
				}
				if (pc.getInventory().checkEnchantItem(weapon1, 7, 1)
						&& pc.getInventory().checkEnchantItem(weapon2, 7, 1)
						&& pc.getInventory().checkItem(41246, 1000) // 結晶體
						&& pc.getInventory().checkItem(49143, 10)) { // 勇氣結晶
					pc.getInventory().consumeEnchantItem(weapon1, 7, 1);
					pc.getInventory().consumeEnchantItem(weapon2, 7, 1);
					pc.getInventory().consumeItem(41246, 1000);
					pc.getInventory().consumeItem(49143, 10);
					final L1ItemInstance item = pc.getInventory().storeItem(
							newWeapon, 1);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getNameId()));
				} else {
					htmlid = "joegolem15";
					if (!pc.getInventory().checkEnchantItem(weapon1, 7, 1)) {
						pc.sendPackets(new S_ServerMessage(337, "+7 "
								+ ItemTable.get().getTemplate(weapon1)
										.getNameId())); // \f1%0不足。
					}
					if (!pc.getInventory().checkEnchantItem(weapon2, 7, 1)) {
						pc.sendPackets(new S_ServerMessage(337, "+7 "
								+ ItemTable.get().getTemplate(weapon2)
										.getNameId())); // \f1%0不足。
					}
					if (!pc.getInventory().checkItem(41246, 1000)) {
						long itemCount = 0;
						itemCount = 1000 - pc.getInventory().countItems(41246);
						pc.sendPackets(new S_ServerMessage(337, ItemTable.get()
								.getTemplate(41246).getNameId()
								+ "(" + itemCount + ")")); // \f1%0不足。
					}
					if (!pc.getInventory().checkItem(49143, 10)) {
						long itemCount = 0;
						itemCount = 10 - pc.getInventory().countItems(49143);
						pc.sendPackets(new S_ServerMessage(337, ItemTable.get()
								.getTemplate(49143).getNameId()
								+ "(" + itemCount + ")")); // \f1%0不足。
					}
				}
			}
			//  砂漠
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71253) {
				// 「歪作」
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(49101, 100)) {
						materials = new int[] { 49101 };
						counts = new int[] { 100 };
						createitem = new int[] { 49092 };
						createcount = new int[] { 1 };
						htmlid = "joegolem18";
					} else {
						htmlid = "joegolem19";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(49101, 1)) {
						pc.getInventory().consumeItem(49101, 1);
						L1Teleport.teleport(pc, 33966, 33253, (short) 4, 5,
								true);
						htmlid = "";
					} else {
						htmlid = "joegolem20";
					}
				}
			}
			//  祭壇
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71255) {
				// 「祭壇鍵持、祭壇送。」
				if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49242, 1)) { // 鍵(20人限定/時歪現2h30未實裝)
						pc.getInventory().consumeItem(49242, 1);
						L1Teleport.teleport(pc, 32735, 32831, (short) 782, 2,
								true);
						htmlid = "";
					} else {
						htmlid = "tebegate3";
						// 「上限人數達場合」
						// htmlid = "tebegate4";
					}
				}
			}

			// 治安團長
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40308, 300)) {
						pc.getInventory().consumeItem(40308, 300);
						pc.getInventory().storeItem(41315, 1);
						pc.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(40308, 300)) {
						htmlid = "rarson7";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 1)
							&& (pc.getInventory().checkItem(41325, 1))) {
						pc.getInventory().consumeItem(41325, 1);
						pc.getInventory().storeItem(40308, 2000);
						pc.getInventory().storeItem(41317, 1);
						pc.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 2);
						htmlid = "rarson9";
					} else {
						htmlid = "rarson10";
					}
				} else if (s.equalsIgnoreCase("C")) {
					if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 4)
							&& (pc.getInventory().checkItem(41326, 1))) {
						pc.getInventory().storeItem(40308, 30000);
						pc.getInventory().consumeItem(41326, 1);
						htmlid = "rarson12";
						pc.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 5);
					} else {
						htmlid = "rarson17";
					}
				} else if (s.equalsIgnoreCase("D")) {
					if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) <= 1)
							|| (pc.getQuest().get_step(
									L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 5)) {
						if (pc.getInventory().checkItem(40308, 300)) {
							pc.getInventory().consumeItem(40308, 300);
							pc.getInventory().storeItem(41315, 1);
							pc.getQuest()
									.set_step(
											L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT,
											1);
							htmlid = "rarson16";
						} else if (!pc.getInventory().checkItem(40308, 300)) {
							htmlid = "rarson7";
						}
					} else if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) >= 2)
							&& (pc.getQuest().get_step(
									L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) <= 4)) {
						if (pc.getInventory().checkItem(40308, 300)) {
							pc.getInventory().consumeItem(40308, 300);
							pc.getInventory().storeItem(41315, 1);
							htmlid = "rarson16";
						} else if (!pc.getInventory().checkItem(40308, 300)) {
							htmlid = "rarson7";
						}
					}
				}
			}
			// 
			else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80101) {
				if (s.equalsIgnoreCase("request letter of kuen")) {
					if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 2)
							&& (pc.getInventory().checkItem(41317, 1))) {
						pc.getInventory().consumeItem(41317, 1);
						pc.getInventory().storeItem(41318, 1);
						pc.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 3);
						htmlid = "";
					} else {
						htmlid = "";
					}
				} else if (s.equalsIgnoreCase("request holy mithril dust")) {
					if ((pc.getQuest().get_step(
							L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 3)
							&& (pc.getInventory().checkItem(41315, 1))
							&& pc.getInventory().checkItem(40494, 30)
							&& pc.getInventory().checkItem(41318, 1)) {
						pc.getInventory().consumeItem(41315, 1);
						pc.getInventory().consumeItem(41318, 1);
						pc.getInventory().consumeItem(40494, 30);
						pc.getInventory().storeItem(41316, 1);
						pc.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 4);
						htmlid = "";
					} else {
						htmlid = "";
					}
				}
			}

			// else System.out.println("C_NpcAction: " + s);
			if ((htmlid != null) && htmlid.equalsIgnoreCase("colos2")) {
				htmldata = this.makeUbInfoStrings(((L1NpcInstance) obj)
						.getNpcTemplate().get_npcId());
			}
			if (createitem != null) { // 精製
				boolean isCreate = true;
				for (int j = 0; j < materials.length; j++) {
					if (!pc.getInventory().checkItemNotEquipped(materials[j],
							counts[j])) {
						final L1Item temp = ItemTable.get().getTemplate(
								materials[j]);
						pc.sendPackets(new S_ServerMessage(337, temp
								.getNameId())); // \f1%0不足。
						isCreate = false;
					}
				}

				if (isCreate) {
					// 容量重量計算
					int create_count = 0; // 個數（纏物1個）
					int create_weight = 0;
					for (int k = 0; k < createitem.length; k++) {
						final L1Item temp = ItemTable.get().getTemplate(
								createitem[k]);
						if (temp.isStackable()) {
							if (!pc.getInventory().checkItem(createitem[k])) {
								create_count += 1;
							}
						} else {
							create_count += createcount[k];
						}
						create_weight += temp.getWeight() * createcount[k]
								/ 1000;
					}
					// 容量確認
					if (pc.getInventory().getSize() + create_count > 180) {
						pc.sendPackets(new S_ServerMessage(263)); // 263
																	// \f1一個角色最多可攜帶180個道具。
						return;
					}
					// 重量確認
					if (pc.getMaxWeight() < pc.getInventory().getWeight()
							+ create_weight) {
						pc.sendPackets(new S_ServerMessage(82)); // 82
																	// 此物品太重了，所以你無法攜帶。
						return;
					}

					for (int j = 0; j < materials.length; j++) {
						// 材料消費
						pc.getInventory().consumeItem(materials[j], counts[j]);
					}
					for (int k = 0; k < createitem.length; k++) {
						final L1ItemInstance item = pc.getInventory()
								.storeItem(createitem[k], createcount[k]);
						if (item != null) {
							final String itemName = ItemTable.get()
									.getTemplate(createitem[k]).getNameId();
							String createrName = "";
							if (obj instanceof L1NpcInstance) {
								createrName = ((L1NpcInstance) obj)
										.getNpcTemplate().get_name();
							}
							if (createcount[k] > 1) {
								pc.sendPackets(new S_ServerMessage(143,
										createrName, itemName + " ("
												+ createcount[k] + ")")); // \f1%0%1。
							} else {
								pc.sendPackets(new S_ServerMessage(143,
										createrName, itemName)); // \f1%0%1。
							}
						}
					}
					if (success_htmlid != null) { // html指定場合表示
						pc.sendPackets(new S_NPCTalkReturn(objid,
								success_htmlid, htmldata));
					}
				} else { // 精製失敗
					if (failure_htmlid != null) { // html指定場合表示
						pc.sendPackets(new S_NPCTalkReturn(objid,
								failure_htmlid, htmldata));
					}
				}
			}

			if (htmlid != null) { // html指定場合表示
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			this.over();
		}
	}

	private String karmaLevelToHtmlId(final int level) {
		if ((level == 0) || (level < -7) || (7 < level)) {
			return "";
		}
		String htmlid = "";
		if (0 < level) {
			htmlid = "vbk" + level;
		} else if (level < 0) {
			htmlid = "vyk" + Math.abs(level);
		}
		return htmlid;
	}

	private String watchUb(final L1PcInstance pc, final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		final L1Location loc = ub.getLocation();
		if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
			try {
				pc.save();
				pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(),
						true);

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		} else {
			pc.sendPackets(new S_ServerMessage(189)); // 189 \f1金幣不足。
		}
		return "";
	}

	private String enterUb(final L1PcInstance pc, final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		if (!ub.isActive() || !ub.canPcEnter(pc)) { // 時間外
			return "colos2";
		}
		if (ub.isNowUb()) { // 競技中
			return "colos1";
		}
		if (ub.getMembersCount() >= ub.getMaxPlayer()) { // 定員
			return "colos4";
		}

		ub.addMember(pc); // 追加
		final L1Location loc = ub.getLocation().randomLocation(10, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
		return "";
	}

	private String enterHauntedHouse(final L1PcInstance pc) {
		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // 競技中
			pc.sendPackets(new S_ServerMessage(1182)); // 始。
			return "";
		}
		if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // 定員
			pc.sendPackets(new S_ServerMessage(1184)); // 化屋敷人。
			return "";
		}

		L1HauntedHouse.getInstance().addMember(pc); // 追加
		L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
		return "";
	}

	/**
	 * 寵物競賽
	 * 
	 * @param pc
	 * @param objid2
	 * @return
	 */
	/*
	 * private String enterPetMatch(final L1PcInstance pc, final int objid2) {
	 * final Object[] petlist = pc.getPetList().values().toArray(); if
	 * (petlist.length > 0) { // 1187 寵物項鏈正在使用中。 pc.sendPackets(new
	 * S_ServerMessage(1187)); return ""; } if
	 * (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) { // 1182 遊戲已經開始了
	 * pc.sendPackets(new S_ServerMessage(1182)); } return ""; }
	 */

	private void poly(final ClientExecutor clientthread, final int polyId) {
		final L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
		}
	}

	private void polyByKeplisha(final ClientExecutor clientthread,
			final int polyId) {
		final L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
		}
	}

	private String sellHouse(final L1PcInstance pc, final int objectId,
			final int npcId) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan == null) {
			return ""; // 消
		}
		final int houseId = clan.getHouseId();
		if (houseId == 0) {
			return ""; // 消
		}

		final L1House house = HouseReading.get().getHouseTable(houseId);
		final int keeperId = house.getKeeperId();
		if (npcId != keeperId) {
			return ""; // 消
		}
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
			return ""; // 消
		}
		if (pc.getId() != clan.getLeaderId()) {
			pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
			return ""; // 消
		}
		if (house.isOnSale()) {
			return "agonsale";
		}

		pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
		return null;
	}

	private void openCloseDoor(final L1PcInstance pc, final L1NpcInstance npc,
			final String s) {
		final int doorId = 0;
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					L1DoorInstance door1 = null;
					L1DoorInstance door2 = null;
					L1DoorInstance door3 = null;
					L1DoorInstance door4 = null;
					for (final L1DoorInstance door : DoorSpawnTable.get()
							.getDoorList()) {
						if (door.getKeeperId() == keeperId) {
							if (door1 == null) {
								door1 = door;
								continue;
							}
							if (door2 == null) {
								door2 = door;
								continue;
							}
							if (door3 == null) {
								door3 = door;
								continue;
							}
							if (door4 == null) {
								door4 = door;
								break;
							}
						}
					}
					if (door1 != null) {
						if (s.equalsIgnoreCase("open")) {
							door1.open();
						} else if (s.equalsIgnoreCase("close")) {
							door1.close();
						}
					}
					if (door2 != null) {
						if (s.equalsIgnoreCase("open")) {
							door2.open();
						} else if (s.equalsIgnoreCase("close")) {
							door2.close();
						}
					}
					if (door3 != null) {
						if (s.equalsIgnoreCase("open")) {
							door3.open();
						} else if (s.equalsIgnoreCase("close")) {
							door3.close();
						}
					}
					if (door4 != null) {
						if (s.equalsIgnoreCase("open")) {
							door4.open();
						} else if (s.equalsIgnoreCase("close")) {
							door4.close();
						}
					}
				}
			}
		}
	}

	private void openCloseGate(final L1PcInstance pc, final int keeperId,
			final boolean isOpen) {
		boolean isNowWar = false;
		int pcCastleId = 0;
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				pcCastleId = clan.getCastleId();
			}
		}
		if ((keeperId == 70656) || (keeperId == 70549) || (keeperId == 70985)) { // 城
			if (this.isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.KENT_CASTLE_ID);
		} else if (keeperId == 70600) { // OT
			if (this.isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.OT_CASTLE_ID);
		} else if ((keeperId == 70778) || (keeperId == 70987)
				|| (keeperId == 70687)) { // WW城
			if (this.isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.WW_CASTLE_ID);
		} else if ((keeperId == 70817) || (keeperId == 70800)
				|| (keeperId == 70988) || (keeperId == 70990)
				|| (keeperId == 70989) || (keeperId == 70991)) { // 城
			if (this.isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.GIRAN_CASTLE_ID);
		} else if ((keeperId == 70863) || (keeperId == 70992)
				|| (keeperId == 70862)) { // 城
			if (this.isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.HEINE_CASTLE_ID);
		} else if ((keeperId == 70995) || (keeperId == 70994)
				|| (keeperId == 70993)) { // 城
			if (this.isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.DOWA_CASTLE_ID);
		} else if (keeperId == 70996) { // 城
			if (this.isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = ServerWarExecutor.get().isNowWar(
					L1CastleLocation.ADEN_CASTLE_ID);
		}

		for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
			if (door.getKeeperId() == keeperId) {
				if (isNowWar && (door.getMaxHp() > 1)) { // 戰爭中城門開閉不可
				} else {
					if (isOpen) { // 開
						door.open();
					} else { // 閉
						door.close();
					}
				}
			}
		}
	}

	private boolean isExistDefenseClan(final int castleId) {
		boolean isExistDefenseClan = false;
		final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
		for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
			final L1Clan clan = iter.next();
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}

	private void expelOtherClan(final L1PcInstance clanPc, final int keeperId) {
		int houseId = 0;
		final Collection<L1House> houseList = HouseReading.get()
				.getHouseTableList().values();
		for (final L1House house : houseList) {
			if (house.getKeeperId() == keeperId) {
				houseId = house.getHouseId();
			}
		}
		if (houseId == 0) {
			return;
		}

		int[] loc = new int[3];
		for (final L1PcInstance pc : World.get().getAllPlayers()) {
			if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(),
					pc.getMapId())
					&& (clanPc.getClanid() != pc.getClanid())) {
				loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
				if (pc != null) {
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5,
							true);
				}
			}
		}
	}

	private void repairGate(final L1PcInstance pc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int castleId = clan.getCastleId();
			if (castleId != 0) { // 城主
				if (!ServerWarExecutor.get().isNowWar(castleId)) {
					// 城門元戾
					for (final L1DoorInstance door : DoorSpawnTable.get()
							.getDoorList()) {
						if (L1CastleLocation.checkInWarArea(castleId, door)) {
							door.repairGate();
						}
					}
					pc.sendPackets(new S_ServerMessage(990)); // 城門自動修理命令。
				} else {
					pc.sendPackets(new S_ServerMessage(991)); // 城門自動修理命令取消。
				}
			}
		}
	}

	private void payFee(final L1PcInstance pc, final L1NpcInstance npc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					long money = ConfigOther.CLAN_HOUSE_TAXES;
					if (pc.getInventory().checkItem(L1ItemId.ADENA, money)) {
						pc.getInventory().consumeItem(L1ItemId.ADENA, money);
						final TimeZone tz = TimeZone
								.getTimeZone(Config.TIME_ZONE);
						final Calendar cal = Calendar.getInstance(tz);
						cal.add(Calendar.DATE, ConfigAlt.HOUSE_TAX_INTERVAL);
						cal.set(Calendar.MINUTE, 0); // 分、秒切捨
						cal.set(Calendar.SECOND, 0);
						house.setTaxDeadline(cal);
						SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
						String formatted = format1.format(cal.getTime());
						pc.sendPackets(new S_ServerMessage(String.format("成功支付稅金 %,d 元，下次支付日期 『%s』", money, formatted)));
						World.get().broadcastPacketToAll(new S_ServerMessage(String.format("血盟『%s』成功支付稅金 %,d 元", clan.getClanName(), money)));
						HouseReading.get().updateHouse(house); // DB書迂
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // 189
																	// \f1金幣不足。
					}
				}
			}
		}
	}

	private String[] makeHouseTaxStrings(final L1PcInstance pc,
			final L1NpcInstance npc) {
		final String name = npc.getNpcTemplate().get_name();
		String[] result;
		result = new String[] { name, "2000", "1", "1", "00" };
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan != null) {
			final int houseId = clan.getHouseId();
			if (houseId != 0) {
				final L1House house = HouseReading.get().getHouseTable(houseId);
				final int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					final Calendar cal = house.getTaxDeadline();
					final int month = cal.get(Calendar.MONTH) + 1;
					final int day = cal.get(Calendar.DATE);
					final int hour = cal.get(Calendar.HOUR_OF_DAY);
					result = new String[] { name, String.valueOf(clan.getAllMembersSize() * 10000),
							String.valueOf(month), String.valueOf(day),
							String.valueOf(hour) };
				}
			}
		}
		return result;
	}

	private String[] makeWarTimeStrings(final int castleId) {
		final L1Castle castle = CastleReading.get().getCastleTable(castleId);
		if (castle == null) {
			return null;
		}
		final Calendar warTime = castle.getWarTime();
		final int year = warTime.get(Calendar.YEAR);
		final int month = warTime.get(Calendar.MONTH) + 1;
		final int day = warTime.get(Calendar.DATE);
		final int hour = warTime.get(Calendar.HOUR_OF_DAY);
		final int minute = warTime.get(Calendar.MINUTE);
		String[] result;
		if (castleId == L1CastleLocation.OT_CASTLE_ID) {
			result = new String[] { String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		} else {
			result = new String[] { "", String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		}
		return result;
	}

	private String getYaheeAmulet(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		final int[] amuletIdList = { 20358, 20359, 20360, 20361, 20362, 20363,
				20364, 20365 };
		int amuletId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1")) {
			amuletId = amuletIdList[0];
		} else if (s.equalsIgnoreCase("2")) {
			amuletId = amuletIdList[1];
		} else if (s.equalsIgnoreCase("3")) {
			amuletId = amuletIdList[2];
		} else if (s.equalsIgnoreCase("4")) {
			amuletId = amuletIdList[3];
		} else if (s.equalsIgnoreCase("5")) {
			amuletId = amuletIdList[4];
		} else if (s.equalsIgnoreCase("6")) {
			amuletId = amuletIdList[5];
		} else if (s.equalsIgnoreCase("7")) {
			amuletId = amuletIdList[6];
		} else if (s.equalsIgnoreCase("8")) {
			amuletId = amuletIdList[7];
		}
		if (amuletId != 0) {
			item = pc.getInventory().storeItem(amuletId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			for (final int id : amuletIdList) {
				if (id == amuletId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String getBarlogEarring(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		final int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025,
				21026, 21027 };
		int earringId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1")) {
			earringId = earringIdList[0];
		} else if (s.equalsIgnoreCase("2")) {
			earringId = earringIdList[1];
		} else if (s.equalsIgnoreCase("3")) {
			earringId = earringIdList[2];
		} else if (s.equalsIgnoreCase("4")) {
			earringId = earringIdList[3];
		} else if (s.equalsIgnoreCase("5")) {
			earringId = earringIdList[4];
		} else if (s.equalsIgnoreCase("6")) {
			earringId = earringIdList[5];
		} else if (s.equalsIgnoreCase("7")) {
			earringId = earringIdList[6];
		} else if (s.equalsIgnoreCase("8")) {
			earringId = earringIdList[7];
		}
		if (earringId != 0) {
			item = pc.getInventory().storeItem(earringId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			for (final int id : earringIdList) {
				if (id == earringId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String[] makeUbInfoStrings(final int npcId) {
		final L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		return ub.makeUbInfoStrings();
	}

	private String talkToDimensionDoor(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		String htmlid = "";
		int protectionId = 0;
		int sealId = 0;
		int locX = 0;
		int locY = 0;
		short mapId = 0;
		if (npc.getNpcTemplate().get_npcId() == 80059) { // 次元扉(土)
			protectionId = 40909;
			sealId = 40913;
			locX = 32773;
			locY = 32835;
			mapId = 607;
		} else if (npc.getNpcTemplate().get_npcId() == 80060) { // 次元扉(風)
			protectionId = 40912;
			sealId = 40916;
			locX = 32757;
			locY = 32842;
			mapId = 606;
		} else if (npc.getNpcTemplate().get_npcId() == 80061) { // 次元扉(水)
			protectionId = 40910;
			sealId = 40914;
			locX = 32830;
			locY = 32822;
			mapId = 604;
		} else if (npc.getNpcTemplate().get_npcId() == 80062) { // 次元扉(火)
			protectionId = 40911;
			sealId = 40915;
			locX = 32835;
			locY = 32822;
			mapId = 605;
		}

		// 「中入」「元素支配者近」「通行證使」「通過」
		if (s.equalsIgnoreCase("a")) {
			L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
			htmlid = "";
		}
		// 「繪突出部分取除」
		else if (s.equalsIgnoreCase("b")) {
			final L1ItemInstance item = pc.getInventory().storeItem(
					protectionId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			htmlid = "";
		}
		// 「通行證捨、地」
		else if (s.equalsIgnoreCase("c")) {
			htmlid = "wpass07";
		}
		// 「續」
		else if (s.equalsIgnoreCase("d")) {
			if (pc.getInventory().checkItem(sealId)) { // 地印章
				final L1ItemInstance item = pc.getInventory()
						.findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
		}
		// 「」「慌拾」
		else if (s.equalsIgnoreCase("e")) {
			htmlid = "";
		}
		// 「消」
		else if (s.equalsIgnoreCase("f")) {
			if (pc.getInventory().checkItem(protectionId)) { // 地通行證
				pc.getInventory().consumeItem(protectionId, 1);
			}
			if (pc.getInventory().checkItem(sealId)) { // 地印章
				final L1ItemInstance item = pc.getInventory()
						.findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
			htmlid = "";
		}
		return htmlid;
	}

	private void getBloodCrystalByKarma(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		L1ItemInstance item = null;

		// 「欠片1個」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (500 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿記憶難。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「欠片10個」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (5000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿記憶難。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「欠片100個」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (50000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿記憶難。
			pc.sendPackets(new S_ServerMessage(1081));
		}
	}

	private void getSoulCrystalByKarma(final L1PcInstance pc,
			final L1NpcInstance npc, final String s) {
		L1ItemInstance item = null;

		// 「欠片1個」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (-500 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感惡寒走。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「欠片10個」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (-5000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感惡寒走。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「欠片100個」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (-50000 * ConfigRate.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感惡寒走。
			pc.sendPackets(new S_ServerMessage(1080));
		}
	}

	/*
	 * private boolean usePolyScroll(final L1PcInstance pc, final int itemId,
	 * final String s) { int time = 0; if ((itemId == 40088) || (itemId ==
	 * 40096)) { // 變身、象牙塔變身 time = 1800; } else if (itemId ==
	 * 140088) { // 祝福變身 time = 2100; }
	 * 
	 * final L1PolyMorph poly = PolyTable.get().getTemplate(s); final
	 * L1ItemInstance item = pc.getInventory().findItemId(itemId); boolean
	 * isUseItem = false; if ((poly != null) || s.equals("none")) { if
	 * (s.equals("none")) { if ((pc.getTempCharGfx() == 6034) ||
	 * (pc.getTempCharGfx() == 6035)) { isUseItem = true; } else {
	 * pc.removeSkillEffect(SHAPE_CHANGE); isUseItem = true; } } else if
	 * ((poly.getMinLevel() <= pc.getLevel()) || pc.isGm()) {
	 * L1PolyMorph.doPoly(pc, poly.getPolyId(), time,
	 * L1PolyMorph.MORPH_BY_ITEMMAGIC); isUseItem = true; } } if (isUseItem) {
	 * pc.getInventory().removeItem(item, 1); } else { pc.sendPackets(new
	 * S_ServerMessage(181)); // \f1變身。 } return isUseItem; }
	 */

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 顯示道具製造清單
	 * 
	 * @param pc
	 * @param npc
	 */
	private void ShowCraftList(L1PcInstance pc, L1NpcInstance npc) {
		String msg0 = "";
		String msg1 = "";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		String msg5 = "";
		String msg6 = "";
		String msg7 = "";
		String msg8 = "";
		String msg9 = "";
		String msg10 = "";
		String msg11 = "";
		String msg12 = "";
		String msg13 = "";
		String msg14 = "";
		String msg15 = "";
		String msg16 = "";
		String msg17 = "";
		String msg18 = "";
		String msg19 = "";
		String msg20 = "";
		String msg21 = "";
		String msg22 = "";
		String msg23 = "";
		String msg24 = "";
		String msg25 = "";
		String msg26 = "";
		String msg27 = "";
		String msg28 = "";
		String msg29 = "";
		String msg30 = "";
		String msg31 = "";
		String msg32 = "";
		String msg33 = "";
		String msg34 = "";
		String msg35 = "";
		String msg36 = "";
		String msg37 = "";
		String msg38 = "";
		String msg39 = "";
		String msg40 = "";

		int npcid = npc.getNpcId();

		Map<String, String> craftlist = L1BlendTable.getInstance()
				.get_craftlist();

		if (!craftlist.isEmpty()) {
			msg0 = craftlist.get(npcid + "A");
			msg1 = craftlist.get(npcid + "B");
			msg2 = craftlist.get(npcid + "C");
			msg3 = craftlist.get(npcid + "D");
			msg4 = craftlist.get(npcid + "E");
			msg5 = craftlist.get(npcid + "F");
			msg6 = craftlist.get(npcid + "G");
			msg7 = craftlist.get(npcid + "H");
			msg8 = craftlist.get(npcid + "I");
			msg9 = craftlist.get(npcid + "J");
			msg10 = craftlist.get(npcid + "K");
			msg11 = craftlist.get(npcid + "L");
			msg12 = craftlist.get(npcid + "M");
			msg13 = craftlist.get(npcid + "N");
			msg14 = craftlist.get(npcid + "O");
			msg15 = craftlist.get(npcid + "P");
			msg16 = craftlist.get(npcid + "Q");
			msg17 = craftlist.get(npcid + "R");
			msg18 = craftlist.get(npcid + "S");
			msg19 = craftlist.get(npcid + "T");
			msg20 = craftlist.get(npcid + "U");
			msg21 = craftlist.get(npcid + "V");
			msg22 = craftlist.get(npcid + "W");
			msg23 = craftlist.get(npcid + "X");
			msg24 = craftlist.get(npcid + "Y");
			msg25 = craftlist.get(npcid + "Z");
			msg26 = craftlist.get(npcid + "a1");
			msg27 = craftlist.get(npcid + "a2");
			msg28 = craftlist.get(npcid + "a3");
			msg29 = craftlist.get(npcid + "a4");
			msg30 = craftlist.get(npcid + "a5");
			msg31 = craftlist.get(npcid + "a6");
			msg32 = craftlist.get(npcid + "a7");
			msg33 = craftlist.get(npcid + "a8");
			msg34 = craftlist.get(npcid + "a9");
			msg35 = craftlist.get(npcid + "a10");
			msg36 = craftlist.get(npcid + "a11");
			msg37 = craftlist.get(npcid + "a12");
			msg38 = craftlist.get(npcid + "a13");
			msg39 = craftlist.get(npcid + "a14");
			msg40 = craftlist.get(npcid + "a15");
		}

		String msgs[] = { msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8,
				msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17,
				msg18, msg19, msg20, msg21, msg22, msg23, msg24, msg25, msg26,
				msg27, msg28, msg29, msg30, msg31, msg32, msg33, msg34, msg35,
				msg36, msg37, msg38, msg39, msg40 };

		if (msg0 != null) {// 至少有設定一項道具製造資料
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "smithitem1", msgs));
		} else {
			pc.sendPackets(new S_SystemMessage("沒有可以製作的道具。"));
			return;
		}

	}
}
