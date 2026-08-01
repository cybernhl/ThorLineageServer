package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;
import static com.lineage.server.model.skill.L1SkillId.SOLID_CARRIAGE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.lineage.server.datatables.InnTable;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Inn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.ItemClass;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Box;
import com.lineage.server.templates.L1ItemsEtcItem;
import com.lineage.server.utils.CheckUtil;

/**
 * 要求使用物品
 *
 * @author daien
 *
 */
public class C_ItemUSe extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ItemUSe.class);

	public C_ItemUSe(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			// 使用者
			final L1PcInstance pc = client.getActiveChar();

			// 鬼魂模式
			if (pc.isGhost()) {
				return;
			}

			// 例外狀況:人物死亡
			if (pc.isDead()) {
				return;
			}

			// 例外狀況:傳送鎖定狀態
			if (pc.isTeleport()) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}

			// 使用物件的OBJID
			final int itemObjid = this.readD();

			// 取回使用物件
			final L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);

			// 例外狀況:物件為空
			if (useItem == null) {
				return;
			}

			// 設置使用者OBJID
			useItem.set_char_objid(pc.getId());

			boolean isStop = false;

			// 再生聖殿 1樓/2樓/3樓
			if (pc.getMapId() == CKEWLv50_1.MAPID) {
				
			}
			
			// 例外狀況:該地圖不允許使用道具
			if (!pc.getMap().isUsableItem()) {
				//System.out.println("例外狀況:該地圖不允許使用道具");
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				isStop = true;
			}
			
			// 無法攻擊/使用道具/技能/回城的狀態
			if (pc.isParalyzedX() && !isStop) {
				isStop = true;
			}

			if (!isStop) {
				switch (pc.getMapId()) {
				case 22:// 傑瑞德的試煉地監
					switch (useItem.getItemId()) {
					case 30:// 紅騎士之劍
					case 40017:// 解毒藥水
						break;
						
					default:
						// 563 \f1你無法在這個地方使用。
						pc.sendPackets(new S_ServerMessage(563));
						isStop = true;
						break;
					}
				}
			}

			if (!CheckUtil.getUseItemAll(pc) && !isStop) {
				isStop = true;
			}

			if (pc.isPrivateShop() && !isStop) {// 商店村模式
				isStop = true;
			}
			
			// 取得物件觸發事件
			final int use_type = useItem.getItem().getUseType();

			if (isStop) {
				pc.setTeleport(false);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}
			
			// 例外狀況:數量小於等於0
			/*if (useItem.getCount() <= 0) {
				return;
			}*/
			boolean isClass = false;// 是否具有CLASS
			final String className = useItem.getItem().getclassname();// 獨立執行項位置
			if (!className.equals("0")) {
				isClass = true;
			}
			
			if (pc.getCurrentHp() > 0) {
				int delay_id = 0;
				if (useItem.getItem().getType2() == 0) { // 種別:一般使用物品:nrmal
					delay_id = ((L1ItemsEtcItem) useItem.getItem()).get_delayid();
				}
				
				if (delay_id != 0) {
					// 延遲作用中
					if (pc.hasItemDelay(delay_id) == true) {
						//System.out.println("延遲作用中");
						pc.sendPackets(new S_Paralysis(
								S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						return;
					}
				}
				
				// 例外狀況:數量異常
				if (useItem.getCount() <= 0) {
					// \f1沒有具有 %0%o。
					pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
					return;
				}

				// 取回可用等級限制
				final int min = useItem.getItem().getMinLevel();
				final int max = useItem.getItem().getMaxLevel();

				// 例外狀況:等級不足
				if ((min != 0) && (min > pc.getLevel())) {// 等級不足
					if (min < 50) {
						// 672 等級%d以上才可使用此道具。
						final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(min, 0);
						pc.sendPackets(toUser);

					} else {
						// 318 等級 %0以上才可使用此道具。
						final S_ServerMessage toUser = new S_ServerMessage(318, String.valueOf(min));
						pc.sendPackets(toUser);
					}
					return;
				}
				// 例外狀況:等級過高
				if ((max != 0) && (max < pc.getLevel())) {// 等級過高
					final S_PacketBoxItemLv toUser = new S_PacketBoxItemLv(0, max);
					pc.sendPackets(toUser);
					return;
				}

				// 例外狀況:具有時間設置
				boolean isDelayEffect = false;
				if (useItem.getItem().getType2() == 0) {
					final int delayEffect = ((L1ItemsEtcItem) useItem.getItem()).get_delayEffect();
					if (delayEffect > 0) {
						isDelayEffect = true;
						final Timestamp lastUsed = useItem.getLastUsed();
						if (lastUsed != null) {
							final Calendar cal = Calendar.getInstance();
							long useTime = (cal.getTimeInMillis() - lastUsed.getTime()) / 1000;
							if (useTime <= delayEffect) {
								// 轉換為需等待時間
								useTime = (delayEffect - useTime) / 60;
								// 時間數字 轉換為字串
								final String useTimeS = 
									useItem.getLogName() + " " + String.valueOf(useTime);
								// 1139 %0 分鐘之內無法使用。
								pc.sendPackets(new S_ServerMessage(1139, useTimeS));
								return;
							}
						}
					}
				}
				
				// 取得物件觸發事件判斷
				switch (use_type) {
				case -11:// 對讀取方法調用無法分類的物品
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -10:// 加速藥水
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -9:// 技術書
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -8:// 料理書
					if (isClass) {
						try {
							final int[] newData = new int[2];
							newData[0] = this.readC();
							newData[1] = this.readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case -7:// 增HP道具
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -6:// 增MP道具
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -4:// 項圈
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case -3:// 飛刀
					pc.getInventory().setSting(useItem.getItemId());
					// 452 %0%s 被選擇了。
					pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
					break;

				case -2:// 箭
					pc.getInventory().setArrow(useItem.getItemId());
					// 452 %0%s 被選擇了。
					pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
					break;

				case -12:// 寵物用具
				case -5:// 食人妖精競賽票 / 死亡競賽票
				case -1:// 無法使用
					// 無法使用訊息
					pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
					break;

				case 0:// 一般物品(直接施放)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 1:// 武器
					// 武器禁止使用
					if (pc.hasItemDelay(L1ItemDelay.WEAPON) == true) {
						return;
					}
					if (this.useItem(pc, useItem)) {
						this.useWeapon(pc, useItem);
					}
					break;

				case 2:// 盔甲
				case 18:// T恤
				case 19:// 斗篷
				case 20:// 手套
				case 21:// 靴
				case 22:// 頭盔
				case 23:// 戒指
				case 24:// 項鏈
				case 25:// 盾牌
				case 37:// 腰帶
				case 40:// 耳環
				case 43:// 副助道具
				case 44:// 副助道具
				case 45:// 副助道具
				case 48:// 副助道具
				case 47:// 副助道具
					// 防具禁止使用
					if (pc.hasItemDelay(L1ItemDelay.ARMOR) == true) {
						return;
					}
					if (this.useItem(pc, useItem)) {
						this.useArmor(pc, useItem);
					}
					break;

				case 3:// 創造怪物魔杖(無須選取目標) (無數量:沒有任何事情發生)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 4:// 希望魔杖(無須選取目標)(有數量:你想要什麼 / 無數量:沒有任何事情發生)
					break;

				case 5:// 魔杖類型(須選取目標)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 選取目標的OBJID
							newData[1] = this.readH();// X座標
							newData[2] = this.readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 6:// 瞬間移動卷軸
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						try {
							final int[] newData = new int[2];
							newData[1] = this.readH();
							newData[0] = this.readD();
							ItemClass.get().item(newData, pc, useItem);
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 7:// 鑒定卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 8:// 復活卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 選取目標的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 9:// 傳送回家的卷軸 / 血盟傳送卷軸
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 10:// 照明道具
					// 取得道具編號
					if ((useItem.getRemainingTime() <= 0) && (useItem.getItemId() != 40004)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 14:// 請選擇一個物品(道具欄位) 燈油/磨刀石/膠水
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 15:// 哨子
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 16:// 變形卷軸
					if (isClass) {
						final String cmd = this.readS();
						pc.setText(cmd);// 選取的變身命令
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 17:// 選取目標 (近距離)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 選取目標的OBJID
							newData[1] = this.readH();// X座標
							newData[2] = this.readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;
					
				case 27:// 對盔甲施法的卷軸
				case 26:// 對武器施法的卷軸
				case 46:// 飾品強化卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							// 選取目標的OBJID
							newData[0] = this.readD();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 28:// 空的魔法卷軸
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readC();
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 29:// 瞬間移動卷軸(祝福)
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						try {
							final int[] newData = new int[2];
							// 所在地圖編號
							newData[1] = this.readH();
							// 選取目標的OBJID
							newData[0] = this.readD();
							ItemClass.get().item(newData, pc, useItem);
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 30:// 選取目標 (Ctrl 遠距離)
					if (isClass) {
						try {
							final int obj = this.readD();// 選取目標的OBJID
							final int[] newData = new int[]{obj};
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 12:// 信紙
				case 31:// 聖誕卡片
				case 33:// 情人節卡片
				case 35:// 白色情人節卡片
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readH();
							pc.setText(this.readS());
							pc.setTextByte(this.readByte());
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 13:// 信紙(打開)
				case 32:// 聖誕卡片(打開)
				case 34:// 情人節卡片(打開)
				case 36:// 白色情人節卡片(打開)
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 38:// 食物
					if (!CheckUtil.getUseItem(pc)) {
						return;
					}
					if (isClass) {
						ItemClass.get().item(null, pc, useItem);
					}
					break;

				case 39:// 選取目標 (遠距離)
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readD();// 選取目標的OBJID
							newData[1] = this.readH();// X座標
							newData[2] = this.readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 42:// 釣魚桿
					if (isClass) {
						try {
							final int[] newData = new int[3];
							newData[0] = this.readH();// X座標
							newData[1] = this.readH();// Y座標
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;

				case 55:// 魔法娃娃成長藥劑
					if (isClass) {
						try {
							final int[] newData = new int[1];
							newData[0] = this.readD();// 選取物件的OBJID
							ItemClass.get().item(newData, pc, useItem);

						} catch (final Exception e) {
							return;
						}
					}
					break;
				case 0x7FFF:
				case 0x8000:
				case 0x8001:
				case 0x8002:
				case 0x8003:
				case 0x8004:
				case 0x8005:
				case 0x8006:
				case 0x8007:
				case 0x8008: {
						if (pc.hasItemDelay(L1ItemDelay.ARMOR) == true) {
							return;
						}
						if (this.useItem(pc, useItem)) {
							this.useArmor(pc, useItem);
						}
						break;
					}

				default:// 測試
					_log.info("未處理的物品分類: " + use_type);
					break;
				}

				if ((useItem.getItem().getType2() == 0) && (use_type == 0)) { // 種別：一般道具
					// 取得道具編號
					final int itemId = useItem.getItem().getItemId();
					
					switch (itemId) {
						case 40312:// 旅館鑰匙
							// 判斷是否為可使用旅館的地圖
							if (pc.getMapId() != 4 && pc.getMapId() != 0 && pc.getMapId() != 440 && pc.getMapId() != 480) {
								pc.sendPackets(new S_SystemMessage("非可使用旅館鑰匙的地圖。"));
								return;
							}
							int npcId = useItem.getInnNpcId();// 鑰匙所屬NPC
							for (int i = 0; i < 16; i++) {
								L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
								if (inn.getKeyId() == useItem.getKeyId()) {
									Timestamp dueTime = useItem.getDueTime();
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
											pc.setInnKeyId(useItem.getKeyId()); // 登入鑰匙編號
											if (!useItem.checkRoomOrHall()) { // 房間
												L1Teleport.teleport(pc, data[0], data[1], (short) data[2], 6, false);
											} else { // 會議室
												L1Teleport.teleport(pc, data[3], data[4], (short) data[5], 6, false);
												break;
											}
										}
									}
								}
							}
							break;
					case 40630:// 迪哥的舊日記
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "diegodiary"));
						break;

					case 40663:// 兒子的信
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "sonsletter"));
						break;

					case 40701:// 小藏寶圖
						if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 1) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"firsttmap"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 2) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"secondtmapa"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 3) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"secondtmapb"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 4) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"secondtmapc"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 5) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmapd"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 6) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmape"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 7) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmapf"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 8) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmapg"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 9) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmaph"));
						} else if (pc.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 10) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"thirdtmapi"));
						}
						break;

					case 41007:// 伊莉絲的命令書：靈魂之安息
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll"));
						break;

					case 41009:// 伊莉絲的命令書：同盟之意志
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "erisscroll2"));
						break;

					case 41060:// 諾曼阿吐巴的信
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
						break;

					case 41061:// 妖精調查書：卡麥都達瑪拉
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
						break;

					case 41062:// 人類調查書：巴庫摩那魯加
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
						break;

					case 41063:// 精靈調查書：可普都達瑪拉
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
						break;

					case 41064:// 妖魔調查書：弧鄔牟那魯加
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
						break;

					case 41065:// 死亡之樹調查書：諾亞阿吐巴
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
						break;

					case 41317:// 拉羅森的推薦書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
						break;

					case 41318:// 可恩的便條紙
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
						break;

					case 41329:// 標本製作委託書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "anirequest"));
						break;

					case 41340:// 傭兵團長多文的推薦書
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
						break;

					case 41356:// 波倫的資源清單
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
						break;

					}
				}
				if (useItem.getItem().getType() == 16 && useItem.getItem().getUseType() != 45) { // treasure_box
					// 容量確認
					if (pc.getInventory().getSize() >= 160) {
						// 263 \f1一個角色最多可攜帶180個道具。
						pc.sendPackets(new S_ServerMessage(263));
						return;
					}
					
					if (pc.getInventory().getWeight182() >= 180) {
						// 82 此物品太重了，所以你無法攜帶。
						pc.sendPackets(new S_ServerMessage(82));
						return;
					}
					
					final ArrayList<L1Box> list = ItemBoxTable.get().get(pc, useItem);
					if (list == null) {
						ItemBoxTable.get().get_all(pc, useItem);
					}
				}

				// 物件使用延遲設置
				if (isDelayEffect) {
					final Timestamp ts = new Timestamp(System.currentTimeMillis());
					// 設置使用時間
					useItem.setLastUsed(ts);
					pc.getInventory().updateItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
					pc.getInventory().saveItem(useItem, L1PcInventory.COL_DELAY_EFFECT);
				}
				try {
					// 分類道具使用延遲
					L1ItemDelay.onItemUse(client, useItem);
					
				}catch (final Exception e) {
					_log.error("分類道具使用延遲異常:"+useItem.getItemId(), e);
				}
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	/**
	 * 武器防具的使用<BR>
	 *
	 * @param pc
	 * @param useItem
	 * @return 該職業可用傳回:true
	 */
	private boolean useItem(final L1PcInstance pc, final L1ItemInstance useItem) {
		boolean isEquipped = false;
		// 職業與物件的使用限制
		if (pc.isCrown()) {// 王族
			if (useItem.getItem().isUseRoyal()) {
				isEquipped = true;
			}
		} else if (pc.isKnight()) {// 騎士
			if (useItem.getItem().isUseKnight()) {
				isEquipped = true;
			}
		} else if (pc.isElf()) {// 精靈
			if (useItem.getItem().isUseElf()) {
				isEquipped = true;
			}
		} else if (pc.isWizard()) {// 法師
			if (useItem.getItem().isUseMage()) {
				isEquipped = true;
			}
		} else if (pc.isDarkelf()) {// 黑暗精靈;
			if (useItem.getItem().isUseDarkelf()) {
				isEquipped = true;
			}
		}

		if (!isEquipped) {
			// 264 \f1你的職業無法使用此裝備。
			pc.sendPackets(new S_ServerMessage(264));
		}
		return isEquipped;
	}

	/**
	 * 防具的使用<BR>
	 * 特別地圖定義
	 *
	 * @param pc
	 * @param useItem
	 */
	/*private void armor(final L1PcInstance pc, final L1ItemInstance useItem) {
		// 種別：防具
		if ((pc.isCrown() && useItem.getItem().isUseRoyal()// 皇族
		)
		|| (pc.isKnight() && useItem.getItem().isUseKnight()// 騎士
		)
		|| (pc.isElf() && useItem.getItem().isUseElf() // 精靈
		)
		|| (pc.isWizard() && useItem.getItem().isUseMage()// 魔法師
		)
		|| (pc.isDarkelf() && useItem.getItem().isUseDarkelf()// 黑暗妖精
		{
			this.useArmor(pc, useItem);
		} else {
			// 264 \f1你的職業無法使用此裝備。
			pc.sendPackets(new S_ServerMessage(264));
		}
	}*/

	/**
	 * 設置防具的裝備
	 *
	 * @param pc
	 * @param armor
	 */
	private void useArmor(final L1PcInstance pc, final L1ItemInstance armor) {
		final int type = armor.getItem().getType();
		final L1PcInventory pcInventory = pc.getInventory();
		boolean equipeSpace; // 裝備欄是否有空位
		if (type == 9) { // type類型為9是戒指可戴2個,其他都只能戴1個
			equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 1;
			
		} else {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		}
		// 防具穿戴
		if (equipeSpace && !armor.isEquipped()) { // 要安裝的裝備欄尚未安裝物品
			final int polyid = pc.getTempCharGfx();

			if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // 不可此穿戴防具的變身形態下
				return;
			}

			if (((type == 13) && (pcInventory.getTypeEquipped(2, 7) >= 1 // 已經裝備其他東西。
			)
			)
			|| ((type == 7) && (pcInventory.getTypeEquipped(2, 13) >= 1))) {
				pc.sendPackets(new S_ServerMessage(124));
				return;
			}

			if ((type == 7) && (pc.getWeapon() != null)) { // 使用雙手武器時無法使用盾
				if (pc.getWeapon().getItem().isTwohandedWeapon()) { // 雙手武器
					// 129 \f1當你使用雙手武器時，無法裝備盾牌。
					pc.sendPackets(new S_ServerMessage(129));
					return;
				}
			}

			if ((type == 3) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿著斗篷時不可穿內衣
				// 126 \f1穿著%1 無法裝備 %0%o 。
				pc.sendPackets(new S_ServerMessage(126, "$224", "$225"));
				return;

			} else if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >= 1)) { // 穿著盔甲時不可穿內衣
				// 126 \f1穿著%1 無法裝備 %0%o 。
				pc.sendPackets(new S_ServerMessage(126, "$224", "$226"));
				return;

			} else if ((type == 2) && (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿著斗篷時不可穿盔甲
				// 126 \f1穿著%1 無法裝備 %0%o 。
				pc.sendPackets(new S_ServerMessage(126, "$226", "$225"));
				return;
			}

			// 物品穿戴成功解除技能：魔法屏障
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
			}

			pcInventory.setEquipped(armor, true);

		// 防具脫除
		} else if (armor.isEquipped()) { // 所選防具穿戴在身上
			if (armor.getItem().getBless() == 2) { // 咒場合
				// 150 \f1你無法這樣做。這個物品已經被詛咒了。
				pc.sendPackets(new S_ServerMessage(150));
				return;
				
			} else {
				if ((type == 3) && (pcInventory.getTypeEquipped(2, 2) >= 1)) { // 穿著盔甲時不能脫下內衣
					// 127 \f1你不能夠脫掉那個。
					pc.sendPackets(new S_ServerMessage(127));
					return;

				} else if (((type == 2) || (type == 3))
						&& (pcInventory.getTypeEquipped(2, 4) >= 1)) { // 穿著斗篷時不能脫下內衣
					// 127 \f1你不能夠脫掉那個。
					pc.sendPackets(new S_ServerMessage(127));
					return;
				}
				if (type == 7) { // 解除魔法技能堅固防禦
					if (pc.hasSkillEffect(SOLID_CARRIAGE)) {
						pc.removeSkillEffect(SOLID_CARRIAGE);
					}
				}

				pcInventory.setEquipped(armor, false);
			}
			/*
			 * 1:helm, 頭盔 2:armor, 盔甲 3:T,內衣 4:cloak,斗篷 5:glove,手套 6:boots, 靴子
			 * 7:shield, 盾 8:amulet, 項鏈 9:ring, 戒指 10:belt, 腰帶 11:ring2,
			 * 12:earring耳環 13:
			 */
		} else {
			if (armor.getItem().getUseType() == 23) {
				// 144 \f1你已經戴著二個戒指。
				pc.sendPackets(new S_ServerMessage(144));
				return;
				
			} else {
				// 124 \f1已經裝備其他東西。
				pc.sendPackets(new S_ServerMessage(124));
				return;
			}
		}

		// 更新HP,MP
		pc.setCurrentHp(pc.getCurrentHp());
		pc.setCurrentMp(pc.getCurrentMp());

		// 更改人物狀態
		pc.sendPackets(new S_OwnCharStatus(pc));
		// 更改人物魔法攻擊與魔法防禦
		pc.sendPackets(new S_SPMR(pc));

	}

	/**
	 * 設置武器的裝備
	 *
	 * @param pc
	 * @param weapon
	 */
	private void useWeapon(final L1PcInstance pc, final L1ItemInstance weapon) {
		switch (weapon.getItemId()) {
		case 65:// 天空之劍
		case 133:// 古代人的智慧
		case 191:// 水之豎琴
		case 192:// 水精靈之弓
			if (pc.getMapId() != CKEWLv50_1.MAPID && !pc.getWeapon().equals(weapon)) {
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}
			break;
			
		default:
			if (pc.hasSkillEffect(CKEW_LV50)) {
				// 563 \f1你無法在這個地方使用。
				pc.sendPackets(new S_ServerMessage(563));
				return;
			}
			break;
		}
		final L1PcInventory pcInventory = pc.getInventory();
		if ((pc.getWeapon() == null) || !pc.getWeapon().equals(weapon)) { // 沒有使用武器或使用武器與所選武器不同
			final int weapon_type = weapon.getItem().getType();
			final int polyid = pc.getTempCharGfx();

			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 不可使用此武器的變身形態下使用武器
				return;
			}
			if (weapon.getItem().isTwohandedWeapon()
					&& (pcInventory.getTypeEquipped(2, 7) >= 1)) { // 使用了盾時不可再使用雙手武器
				pc.sendPackets(new S_ServerMessage(128));
				return;
			}
		}
		// 解除魔法技能：絕對屏障
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}

		if (pc.getWeapon() != null) { // 已有裝備的狀態
			if (pc.getWeapon().getItem().getBless() == 2) {
				// 150 \f1你無法這樣做。這個物品已經被詛咒了。
				pc.sendPackets(new S_ServerMessage(150));
				return;
			}
			if (pc.getWeapon().equals(weapon)) {
				// 解除裝備
				pcInventory.setEquipped(pc.getWeapon(), false, false, false);
				return;

				// 武器交換
			} else {
				pcInventory.setEquipped(pc.getWeapon(), false, false, true);
			}

		}

		if (weapon.getItem().getBless() == 2) {
			// \f1%0%s 主動固定在你的手上！
			pc.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
