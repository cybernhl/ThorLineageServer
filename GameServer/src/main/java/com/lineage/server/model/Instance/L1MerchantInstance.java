package com.lineage.server.model.Instance;

import com.add.CustomBaccarat;
import com.add.CustomTaiwanMahjong;
import com.lineage.server.serverpackets.*;
import com.william.L1BlendTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.lock.TownReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.WorldClan;
import com.william.NpcTalkTable;

import java.util.Map;

/**
 * 對像:對話NPC 控制項
 * @author daien
 *
 */
public class L1MerchantInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1MerchantInstance.class);
	
	/**
	 * 對像:對話NPC
	 * @param template
	 */
	public L1MerchantInstance(final L1Npc template) {
		super(template);
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack_M(this));
			//this.onNpcAI();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			//attack.calcHit();
			attack.action();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onNpcAI() {
		if (this.isAiRunning()) {
			return;
		}
		this.setActived(false);
		this.startAI();
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		final int objid = this.getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(this.getNpcTemplate().get_npcId());
		final int npcid = this.getNpcTemplate().get_npcId();
		final L1PcQuest quest = player.getQuest();
		String htmlid = null;
		String[] htmldata = null;

		final int pcX = player.getX();
		final int pcY = player.getY();
		final int npcX = this.getX();
		final int npcY = this.getY();
		
		if ((NpcTalkTable.getSetList() != null) && (NpcTalkTable.forNpcAction(player, npcid, objid))) {
			return;
		}

		// 不具備工作的NPC
			
			
		if (this.WORK == null) {
			if (this.getNpcTemplate().getChangeHead()) {
				if ((pcX == npcX) && (pcY < npcY)) {
					this.setHeading(0);
					
				} else if ((pcX > npcX) && (pcY < npcY)) {
					this.setHeading(1);
					
				} else if ((pcX > npcX) && (pcY == npcY)) {
					this.setHeading(2);
					
				} else if ((pcX > npcX) && (pcY > npcY)) {
					this.setHeading(3);
					
				} else if ((pcX == npcX) && (pcY > npcY)) {
					this.setHeading(4);
					
				} else if ((pcX < npcX) && (pcY > npcY)) {
					this.setHeading(5);
					
				} else if ((pcX < npcX) && (pcY == npcY)) {
					this.setHeading(6);
					
				} else if ((pcX < npcX) && (pcY < npcY)) {
					this.setHeading(7);
				}
				this.broadcastPacketAll(new S_ChangeHeading(this));
			}
		}

		if (talking != null) {
			if (npcid == 70841) { // 
				if (player.isElf()) { // 
					htmlid = "luudielE1";
				} else if (player.isDarkelf()) { // 
					htmlid = "luudielCE1";
				} else {
					htmlid = "luudiel1";
				}

			/*} else if (npcid == 70724) { // 
				if (player.isElf()) { // 
					final int lv45_step = quest.get_step(L1PcQuest.QUEST_LEVEL45);
					if (lv45_step >= 4) { // 終了濟
						htmlid = "heit5";
					} else if (lv45_step >= 3) { // 交換濟
						htmlid = "heit3";
					} else if (lv45_step >= 2) { // 同意濟
						htmlid = "heit2";
					} else if (lv45_step >= 1) { // 同意濟
						htmlid = "heit1";
					}
				}*/

			/*} else if (npcid == 70904) { // 
				if (player.isDarkelf()) {
					if (quest.get_step(L1PcQuest.QUEST_LEVEL45) == 1) { // 同意濟
						htmlid = "koup12";
					}
				}*/

			} else if (npcid == 70087) { // 
				if (player.isDarkelf()) {
					htmlid = "sedia";
				}
			} else if (npcid == 600007) { // 庫伯--油布斗篷
				if (!quest.isEnd(L1PcQuest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "kuper1";
					}
				}
			} else if (npcid == 70796) { // 
				if (!quest.isEnd(L1PcQuest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "dunham1";
					}
				}
			} else if (npcid == 70011) { // 話島船著管理人
				final int time = L1GameTimeClock.getInstance().currentTime()
				.getSeconds() % 86400;
				if ((time < 60 * 60 * 6) || (time > 60 * 60 * 20)) { // 20:00～6:00
					htmlid = "shipEvI6";
				}
			} else if (npcid == 70553) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "ishmael1";
					} else {
						htmlid = "ishmael6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "ishmael7";
				}
			} else if (npcid == 70822) { // 森  
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.OT_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "seghem1";
					} else {
						htmlid = "seghem6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "seghem7";
				}
			} else if (npcid == 70784) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "othmond1";
					} else {
						htmlid = "othmond6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "othmond7";
				}
			} else if (npcid == 70623) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "orville1";
					} else {
						htmlid = "orville6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "orville7";
				}
			} else if (npcid == 70880) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "fisher1";
					} else {
						htmlid = "fisher6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "fisher7";
				}
			} else if (npcid == 70665) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "potempin1";
					} else {
						htmlid = "potempin6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "potempin7";
				}
			} else if (npcid == 70721) { // 城 侍從長 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "timon1";
					} else {
						htmlid = "timon6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "timon7";
				}
			} else if (npcid == 81155) { // 要塞 
				final boolean hascastle = this.checkHasCastle(player,
						L1CastleLocation.DIAD_CASTLE_ID);
				if (hascastle) { // 城盟成員
					if (this.checkClanLeader(player)) { // 血盟主
						htmlid = "olle1";
					} else {
						htmlid = "olle6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "olle7";
				}
			} else if (npcid == 80057) { // 
				switch (player.getKarmaLevel()) {
				case 0:
					htmlid = "alfons1";
					break;
				case -1:
					htmlid = "cyk1";
					break;
				case -2:
					htmlid = "cyk2";
					break;
				case -3:
					htmlid = "cyk3";
					break;
				case -4:
					htmlid = "cyk4";
					break;
				case -5:
					htmlid = "cyk5";
					break;
				case -6:
					htmlid = "cyk6";
					break;
				case -7:
					htmlid = "cyk7";
					break;
				case -8:
					htmlid = "cyk8";
					break;
				case 1:
					htmlid = "cbk1";
					break;
				case 2:
					htmlid = "cbk2";
					break;
				case 3:
					htmlid = "cbk3";
					break;
				case 4:
					htmlid = "cbk4";
					break;
				case 5:
					htmlid = "cbk5";
					break;
				case 6:
					htmlid = "cbk6";
					break;
				case 7:
					htmlid = "cbk7";
					break;
				case 8:
					htmlid = "cbk8";
					break;
				default:
					htmlid = "alfons1";
				break;
				}
			} else if (npcid == 80058) { // 次元扉(砂漠)
				final int level = player.getLevel();
				if (level <= 44) {
					htmlid = "cpass03";
				} else if ((level <= 51) && (45 <= level)) {
					htmlid = "cpass02";
				} else {
					htmlid = "cpass01";
				}
			} else if (npcid == 80059) { // 次元扉(土)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40917)) { // 地支配者
					htmlid = "wpass14";
				} else if (player.getInventory().checkItem(40912) // 風通行證
						|| player.getInventory().checkItem(40910) // 水通行證
						|| player.getInventory().checkItem(40911)) { // 火通行證
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40909)) { // 地通行證
					final int count = this.getNecessarySealCount(player);
					if (player.getInventory().checkItem(40913, count)) { // 地印章
						this.createRuler(player, 1, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40913)) { // 地印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80060) { // 次元扉(風)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40920)) { // 風支配者
					htmlid = "wpass13";
				} else if (player.getInventory().checkItem(40909) // 地通行證
						|| player.getInventory().checkItem(40910) // 水通行證
						|| player.getInventory().checkItem(40911)) { // 火通行證
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40912)) { // 風通行證
					final int count = this.getNecessarySealCount(player);
					if (player.getInventory().checkItem(40916, count)) { // 風印章
						this.createRuler(player, 8, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40916)) { // 風印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80061) { // 次元扉(水)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40918)) { // 水支配者
					htmlid = "wpass11";
				} else if (player.getInventory().checkItem(40909) // 地通行證
						|| player.getInventory().checkItem(40912) // 風通行證
						|| player.getInventory().checkItem(40911)) { // 火通行證
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40910)) { // 水通行證
					final int count = this.getNecessarySealCount(player);
					if (player.getInventory().checkItem(40914, count)) { // 水印章
						this.createRuler(player, 4, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40914)) { // 水印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80062) { // 次元扉(火)
				if (player.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40919)) { // 火支配者
					htmlid = "wpass12";
				} else if (player.getInventory().checkItem(40909) // 地通行證
						|| player.getInventory().checkItem(40912) // 風通行證
						|| player.getInventory().checkItem(40910)) { // 水通行證
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40911)) { // 火通行證
					final int count = this.getNecessarySealCount(player);
					if (player.getInventory().checkItem(40915, count)) { // 火印章
						this.createRuler(player, 2, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40915)) { // 火印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80065) { // 密偵
				if (player.getKarmaLevel() < 3) {
					htmlid = "uturn0";
				} else {
					htmlid = "uturn1";
				}
			} else if (npcid == 80047) { // 召使
				if (player.getKarmaLevel() > -3) {
					htmlid = "uhelp1";
				} else {
					htmlid = "uhelp2";
				}
			} else if (npcid == 80049) { // 搖者
				if (player.getKarma() <= -10000000) {
					htmlid = "betray11";
				} else {
					htmlid = "betray12";
				}
			} else if (npcid == 80050) { // 執政官
				if (player.getKarmaLevel() > -1) {
					htmlid = "meet103";
				} else {
					htmlid = "meet101";
				}
			} else if (npcid == 80053) { // 鍛冶屋
				final int karmaLevel = player.getKarmaLevel();
				if (karmaLevel == 0) {
					htmlid = "aliceyet";
				} else if (karmaLevel >= 1) {
					if (player.getInventory().checkItem(196)
							|| player.getInventory().checkItem(197)
							|| player.getInventory().checkItem(198)
							|| player.getInventory().checkItem(199)
							|| player.getInventory().checkItem(200)
							|| player.getInventory().checkItem(201)
							|| player.getInventory().checkItem(202)
							|| player.getInventory().checkItem(203)) {
						htmlid = "alice_gd";
					} else {
						htmlid = "gd";
					}
				} else if (karmaLevel <= -1) {
					if (player.getInventory().checkItem(40991)) {
						if (karmaLevel <= -1) {
							htmlid = "Mate_1";
						}
					} else if (player.getInventory().checkItem(196)) {
						if (karmaLevel <= -2) {
							htmlid = "Mate_2";
						} else {
							htmlid = "alice_1";
						}
					} else if (player.getInventory().checkItem(197)) {
						if (karmaLevel <= -3) {
							htmlid = "Mate_3";
						} else {
							htmlid = "alice_2";
						}
					} else if (player.getInventory().checkItem(198)) {
						if (karmaLevel <= -4) {
							htmlid = "Mate_4";
						} else {
							htmlid = "alice_3";
						}
					} else if (player.getInventory().checkItem(199)) {
						if (karmaLevel <= -5) {
							htmlid = "Mate_5";
						} else {
							htmlid = "alice_4";
						}
					} else if (player.getInventory().checkItem(200)) {
						if (karmaLevel <= -6) {
							htmlid = "Mate_6";
						} else {
							htmlid = "alice_5";
						}
					} else if (player.getInventory().checkItem(201)) {
						if (karmaLevel <= -7) {
							htmlid = "Mate_7";
						} else {
							htmlid = "alice_6";
						}
					} else if (player.getInventory().checkItem(202)) {
						if (karmaLevel <= -8) {
							htmlid = "Mate_8";
						} else {
							htmlid = "alice_7";
						}
					} else if (player.getInventory().checkItem(203)) {
						htmlid = "alice_8";
					} else {
						htmlid = "alice_no";
					}
				}
			} else if (npcid == 80055) { // 補佐官
				int amuletLevel = 0;
				if (player.getInventory().checkItem(20358)) { // 奴隸
					amuletLevel = 1;
				} else if (player.getInventory().checkItem(20359)) { // 約束
					amuletLevel = 2;
				} else if (player.getInventory().checkItem(20360)) { // 解放
					amuletLevel = 3;
				} else if (player.getInventory().checkItem(20361)) { // 獵犬
					amuletLevel = 4;
				} else if (player.getInventory().checkItem(20362)) { // 魔族
					amuletLevel = 5;
				} else if (player.getInventory().checkItem(20363)) { // 勇士
					amuletLevel = 6;
				} else if (player.getInventory().checkItem(20364)) { // 將軍
					amuletLevel = 7;
				} else if (player.getInventory().checkItem(20365)) { // 大將軍
					amuletLevel = 8;
				}
				if (player.getKarmaLevel() == -1) {
					if (amuletLevel >= 1) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet1";
					}
				} else if (player.getKarmaLevel() == -2) {
					if (amuletLevel >= 2) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet2";
					}
				} else if (player.getKarmaLevel() == -3) {
					if (amuletLevel >= 3) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet3";
					}
				} else if (player.getKarmaLevel() == -4) {
					if (amuletLevel >= 4) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet4";
					}
				} else if (player.getKarmaLevel() == -5) {
					if (amuletLevel >= 5) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet5";
					}
				} else if (player.getKarmaLevel() == -6) {
					if (amuletLevel >= 6) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet6";
					}
				} else if (player.getKarmaLevel() == -7) {
					if (amuletLevel >= 7) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet7";
					}
				} else if (player.getKarmaLevel() == -8) {
					if (amuletLevel >= 8) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet8";
					}
				} else {
					htmlid = "uamulet0";
				}
			} else if (npcid == 80056) { // 業管理者
				if (player.getKarma() <= -10000000) {
					htmlid = "infamous11";
				} else {
					htmlid = "infamous12";
				}
			} else if (npcid == 80064) { // 執政官
				if (player.getKarmaLevel() < 1) {
					htmlid = "meet003";
				} else {
					htmlid = "meet001";
				}
			} else if (npcid == 80066) { // 搖者
				if (player.getKarma() >= 10000000) {
					htmlid = "betray01";
				} else {
					htmlid = "betray02";
				}
			} else if (npcid == 80071) { // 補佐官
				int earringLevel = 0;
				if (player.getInventory().checkItem(21020)) { // 踴躍
					earringLevel = 1;
				} else if (player.getInventory().checkItem(21021)) { // 雙子
					earringLevel = 2;
				} else if (player.getInventory().checkItem(21022)) { // 友好
					earringLevel = 3;
				} else if (player.getInventory().checkItem(21023)) { // 極知
					earringLevel = 4;
				} else if (player.getInventory().checkItem(21024)) { // 暴走
					earringLevel = 5;
				} else if (player.getInventory().checkItem(21025)) { // 從魔
					earringLevel = 6;
				} else if (player.getInventory().checkItem(21026)) { // 血族
					earringLevel = 7;
				} else if (player.getInventory().checkItem(21027)) { // 奴隸
					earringLevel = 8;
				}
				if (player.getKarmaLevel() == 1) {
					if (earringLevel >= 1) {
						htmlid = "lringd";
					} else {
						htmlid = "lring1";
					}
				} else if (player.getKarmaLevel() == 2) {
					if (earringLevel >= 2) {
						htmlid = "lringd";
					} else {
						htmlid = "lring2";
					}
				} else if (player.getKarmaLevel() == 3) {
					if (earringLevel >= 3) {
						htmlid = "lringd";
					} else {
						htmlid = "lring3";
					}
				} else if (player.getKarmaLevel() == 4) {
					if (earringLevel >= 4) {
						htmlid = "lringd";
					} else {
						htmlid = "lring4";
					}
				} else if (player.getKarmaLevel() == 5) {
					if (earringLevel >= 5) {
						htmlid = "lringd";
					} else {
						htmlid = "lring5";
					}
				} else if (player.getKarmaLevel() == 6) {
					if (earringLevel >= 6) {
						htmlid = "lringd";
					} else {
						htmlid = "lring6";
					}
				} else if (player.getKarmaLevel() == 7) {
					if (earringLevel >= 7) {
						htmlid = "lringd";
					} else {
						htmlid = "lring7";
					}
				} else if (player.getKarmaLevel() == 8) {
					if (earringLevel >= 8) {
						htmlid = "lringd";
					} else {
						htmlid = "lring8";
					}
				} else {
					htmlid = "lring0";
				}
			} else if (npcid == 80072) { // 鍛冶屋
				final int karmaLevel = player.getKarmaLevel();
				if (karmaLevel == 1) {
					htmlid = "lsmith0";
				} else if (karmaLevel == 2) {
					htmlid = "lsmith1";
				} else if (karmaLevel == 3) {
					htmlid = "lsmith2";
				} else if (karmaLevel == 4) {
					htmlid = "lsmith3";
				} else if (karmaLevel == 5) {
					htmlid = "lsmith4";
				} else if (karmaLevel == 6) {
					htmlid = "lsmith5";
				} else if (karmaLevel == 7) {
					htmlid = "lsmith7";
				} else if (karmaLevel == 8) {
					htmlid = "lsmith8";
				} else {
					htmlid = "";
				}
			} else if (npcid == 80074) { // 業管理者
				if (player.getKarma() >= 10000000) {
					htmlid = "infamous01";
				} else {
					htmlid = "infamous02";
				}
			} else if (npcid == 80104) { // 騎馬團員
				if (!player.isCrown()) { // 君主
					htmlid = "horseseller4";
				}
			} else if (npcid == 70528) { // 話島村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
			} else if (npcid == 70546) { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_KENT);
			} else if (npcid == 70567) { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GLUDIO);
			} else if (npcid == 70815) { // 火田村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
			} else if (npcid == 70774) { // 村 
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_WINDAWOOD);
			} else if (npcid == 70799) { //  
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
			} else if (npcid == 70594) { // 都市 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GIRAN);
			} else if (npcid == 70860) { // 都市 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_HEINE);
			} else if (npcid == 70654) { // 村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_WERLDAN);
			} else if (npcid == 70748) { // 象牙塔村 
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_OREN);
			} else if (npcid == 70534) { // 話島村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
			} else if (npcid == 70556) { // 村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_KENT);
			} else if (npcid == 70572) { // 村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GLUDIO);
			} else if (npcid == 70830) { // 火田村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
			} else if (npcid == 70788) { // 村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WINDAWOOD);
			} else if (npcid == 70806) { //  
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
			} else if (npcid == 70631) { // 都市 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GIRAN);
			} else if (npcid == 70876) { // 都市 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_HEINE);
			} else if (npcid == 70663) { // 村 
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WERLDAN);
			} else if (npcid == 70761) { // 象牙塔村 
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_OREN);

			} else if (npcid == 70506) { // 
				htmlid = this.talkToRuba(player);

			} else if (npcid == 71026) { // 
				if (player.getLevel() < 10) {
					htmlid = "en0113";
				} else if ((player.getLevel() >= 10) && (player.getLevel() < 25)) {
					htmlid = "en0111";
				} else if (player.getLevel() > 25) {
					htmlid = "en0112";
				}
			} else if (npcid == 71027) { // 
				if (player.getLevel() < 10) {
					htmlid = "en0283";
				} else if ((player.getLevel() >= 10) && (player.getLevel() < 25)) {
					htmlid = "en0281";
				} else if (player.getLevel() > 25) {
					htmlid = "en0282";
				}

			} else if (npcid == 70512) { // 治療師（歌島 村中）
				if (player.getLevel() >= 25) {
					htmlid = "jpe0102";
				}
			} else if (npcid == 70514) { // 師
				if (player.getLevel() >= 25) {
					htmlid = "jpe0092";
				}
			} else if (npcid == 71038) { // 長老 
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41090) // 
							|| player.getInventory().checkItem(41091) // -
							|| player.getInventory().checkItem(41092)) { // 
						htmlid = "orcfnoname7";
					} else {
						htmlid = "orcfnoname8";
					}
				} else {
					htmlid = "orcfnoname1";
				}
			} else if (npcid == 71040) { // 調查團長  
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41065)) { // 調查團證書
						if (player.getInventory().checkItem(41086) // 根
								|| player.getInventory().checkItem(41087) // 表皮
								|| player.getInventory().checkItem(41088) // 葉
								|| player.getInventory().checkItem(41089)) { // 木枝
							htmlid = "orcfnoa6";
						} else {
							htmlid = "orcfnoa5";
						}
					} else {
						htmlid = "orcfnoa2";
					}
				} else {
					htmlid = "orcfnoa1";
				}
			} else if (npcid == 71041) { //  
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41064)) { // 調查團證書
						if (player.getInventory().checkItem(41081) // 
								|| player.getInventory().checkItem(41082) // 
								|| player.getInventory().checkItem(41083) // 
								|| player.getInventory().checkItem(41084) // 
								|| player.getInventory().checkItem(41085)) { // 予言者
							htmlid = "orcfhuwoomo2";
						} else {
							htmlid = "orcfhuwoomo8";
						}
					} else {
						htmlid = "orcfhuwoomo1";
					}
				} else {
					htmlid = "orcfhuwoomo5";
				}
			} else if (npcid == 71042) { //  
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41062)) { // 調查團證書
						if (player.getInventory().checkItem(41071) // 銀盆
								|| player.getInventory().checkItem(41072) // 銀燭台
								|| player.getInventory().checkItem(41073) // 鍵
								|| player.getInventory().checkItem(41074) // 袋
								|| player.getInventory().checkItem(41075)) { // 污發毛
							htmlid = "orcfbakumo2";
						} else {
							htmlid = "orcfbakumo8";
						}
					} else {
						htmlid = "orcfbakumo1";
					}
				} else {
					htmlid = "orcfbakumo5";
				}
			} else if (npcid == 71043) { // - 
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41063)) { // 調查團證書
						if (player.getInventory().checkItem(41076) // 污地
								|| player.getInventory().checkItem(41077) // 污水
								|| player.getInventory().checkItem(41078) // 污火
								|| player.getInventory().checkItem(41079) // 污風
								|| player.getInventory().checkItem(41080)) { // 污精靈
							htmlid = "orcfbuka2";
						} else {
							htmlid = "orcfbuka8";
						}
					} else {
						htmlid = "orcfbuka1";
					}
				} else {
					htmlid = "orcfbuka5";
				}
			} else if (npcid == 71044) { // - 
				if (player.getInventory().checkItem(41060)) { // 推薦書
					if (player.getInventory().checkItem(41061)) { // 調查團證書
						if (player.getInventory().checkItem(41066) // 污根
								|| player.getInventory().checkItem(41067) // 污枝
								|| player.getInventory().checkItem(41068) // 污拔殼
								|| player.getInventory().checkItem(41069) // 污
								|| player.getInventory().checkItem(41070)) { // 污妖精羽
							htmlid = "orcfkame2";
						} else {
							htmlid = "orcfkame8";
						}
					} else {
						htmlid = "orcfkame1";
					}
				} else {
					htmlid = "orcfkame5";
				}
			} else if (npcid == 71055) { // （海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 3) {
					htmlid = "lukein13";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == L1PcQuest.QUEST_END)
						&& (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 2)
						&& player.getInventory().checkItem(40631)) {
					htmlid = "lukein10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == L1PcQuest.QUEST_END) {
					htmlid = "lukein0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 11) {
					if (player.getInventory().checkItem(40716)) {
						htmlid = "lukein9";
					}
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) >= 1)
						&& (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) <= 10)) {
					htmlid = "lukein8";
				}
			} else if (npcid == 71063) { // 小箱-１番目（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_TBOX1) == L1PcQuest.QUEST_END) {
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 1) {
					htmlid = "maptbox";
				}
			} else if (npcid == 71064) { // 小箱-2番目-ｂ地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 2) {
					htmlid = this.talkToSecondtbox(player);
				}
			} else if (npcid == 71065) { // 小箱-2番目-c地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 3) {
					htmlid = this.talkToSecondtbox(player);
				}
			} else if (npcid == 71066) { // 小箱-2番目-d地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 4) {
					htmlid = this.talkToSecondtbox(player);
				}
			} else if (npcid == 71067) { // 小箱-3番目-e地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 5) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71068) { // 小箱-3番目-f地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 6) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71069) { // 小箱-3番目-g地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 7) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71070) { // 小箱-3番目-h地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 8) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71071) { // 小箱-3番目-i地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 9) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71072) { // 小箱-3番目-j地點（海賊島秘密）
				if (player.getQuest().get_step(L1PcQuest.QUEST_LUKEIN1) == 10) {
					htmlid = this.talkToThirdtbox(player);
				}
			} else if (npcid == 71056) { // （消息子）
				if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 4) {
					if (player.getInventory().checkItem(40631)) {
						htmlid = "SIMIZZ11";
					} else {
						htmlid = "SIMIZZ0";
					}
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == 2) {
					htmlid = "SIMIZZ0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == L1PcQuest.QUEST_END) {
					htmlid = "SIMIZZ15";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == 1) {
					htmlid = "SIMIZZ6";
				}
			} else if (npcid == 71057) { // （寶地圖1）
				if (player.getQuest().get_step(L1PcQuest.QUEST_DOIL) == L1PcQuest.QUEST_END) {
					htmlid = "doil4b";
				}
			} else if (npcid == 71059) { // （寶地圖2）
				if (player.getQuest().get_step(L1PcQuest.QUEST_RUDIAN) == L1PcQuest.QUEST_END) {
					htmlid = "rudian1c";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_RUDIAN) == 1) {
					htmlid = "rudian7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_DOIL) == L1PcQuest.QUEST_END) {
					htmlid = "rudian1b";
				} else {
					htmlid = "rudian1a";
				}
			} else if (npcid == 71060) { // （寶地圖3）
				if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == L1PcQuest.QUEST_END) {
					htmlid = "resta1e";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == L1PcQuest.QUEST_END) {
					htmlid = "resta14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 4) {
					htmlid = "resta13";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 3) {
					htmlid = "resta11";
					player.getQuest().set_step(L1PcQuest.QUEST_RESTA, 4);
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_RESTA) == 2) {
					htmlid = "resta16";
				} else if (((player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == 2)
						&& (player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 1))
						|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1a";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 1)
						|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1c";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_SIMIZZ) == 2) {
					htmlid = "resta1b";
				}
			} else if (npcid == 71061) { // （寶地圖4）
				if (player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == L1PcQuest.QUEST_END) {
					htmlid = "cadmus1c";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 3) {
					htmlid = "cadmus8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 2) {
					htmlid = "cadmus1a";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_DOIL) == L1PcQuest.QUEST_END) {
					htmlid = "cadmus1b";
				}
			} else if (npcid == 71036) { // （真實）
				if (player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == L1PcQuest.QUEST_END) {
					htmlid = "kamyla26";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 4)
						&& player.getInventory().checkItem(40717)) {
					htmlid = "kamyla15";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 4) {
					htmlid = "kamyla14";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 3)
						&& player.getInventory().checkItem(40630)) {
					htmlid = "kamyla12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 3) {
					htmlid = "kamyla11";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 2)
						&& player.getInventory().checkItem(40644)) {
					htmlid = "kamyla9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 1) {
					htmlid = "kamyla8";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == L1PcQuest.QUEST_END)
						&& player.getInventory().checkItem(40621)) {
					htmlid = "kamyla1";
				}
			} else if (npcid == 71089) { // （真實）
				if (player.getQuest().get_step(L1PcQuest.QUEST_KAMYLA) == 2) {
					htmlid = "francu12";
				}
			} else if (npcid == 71090) { // 試練2（真實）
				if ((player.getQuest().get_step(L1PcQuest.QUEST_CRYSTAL) == 1)
						&& player.getInventory().checkItem(40620)) {
					htmlid = "jcrystal2";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_CRYSTAL) == 1) {
					htmlid = "jcrystal3";
				}
			} else if (npcid == 71091) { // 試練3（真實）
				if ((player.getQuest().get_step(L1PcQuest.QUEST_CRYSTAL) == 2)
						&& player.getInventory().checkItem(40654)) {
					htmlid = "jcrystall2";
				}
			} else if (npcid == 71074) { // 長老
				if (player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == L1PcQuest.QUEST_END) {
					htmlid = "lelder0";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 3)
						&& player.getInventory().checkItem(40634)) {
					htmlid = "lelder12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 3) {
					htmlid = "lelder11";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 2)
						&& player.getInventory().checkItem(40633)) {
					htmlid = "lelder7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 2) {
					htmlid = "lelder7b";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 1) {
					htmlid = "lelder7b";
				} else if (player.getLevel() >= 40) {
					htmlid = "lelder1";
				}
			} else if (npcid == 71076) { // 
				if (player.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == L1PcQuest.QUEST_END) {
					htmlid = "ylizardb";
				} else {
				}
			} else if (npcid == 80079) { // 
				if ((player.getQuest().get_step(L1PcQuest.QUEST_KEPLISHA) == L1PcQuest.QUEST_END)
						&& !player.getInventory().checkItem(41312)) {
					htmlid = "keplisha6";
				} else {
					if (player.getInventory().checkItem(41314)) { // 占星術師守
						htmlid = "keplisha3";
					} else if (player.getInventory().checkItem(41313)) { // 占星術師玉
						htmlid = "keplisha2";
					} else if (player.getInventory().checkItem(41312)) { // 占星術師壺
						htmlid = "keplisha4";
					}
				}
			} else if (npcid == 80102) { // 
				if (player.getInventory().checkItem(41329)) { // 剝制製作依賴書
					htmlid = "fillis3";
				}
			} else if (npcid == 71167) { // 
				if (player.getTempCharGfx() == 3887) {// 變身
					htmlid = "frim1";
				}
			} else if (npcid == 71141) { // 坑夫1
				if (player.getTempCharGfx() == 3887) {// 變身
					htmlid = "moumthree1";
				}
			} else if (npcid == 71142) { // 坑夫2
				if (player.getTempCharGfx() == 3887) {// 變身
					htmlid = "moumtwo1";
				}
			} else if (npcid == 71145) { // 坑夫3
				if (player.getTempCharGfx() == 3887) {// 變身
					htmlid = "moumone1";
				}

			} else if (npcid == 81200) { // 特典管理人
				if (player.getInventory().checkItem(21069) // 新生
						|| player.getInventory().checkItem(21074)) { // 親睦
					htmlid = "c_belt";
				}
			} else if (npcid == 80076) { // 倒航海士
				if (player.getInventory().checkItem(41058)) { // 完成航海日誌
					htmlid = "voyager8";
				} else if (player.getInventory().checkItem(49082) // 未完成航海日誌
						|| player.getInventory().checkItem(49083)) {
					// 追加狀態
					if (player.getInventory().checkItem(41038) // 航海日誌 1
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 2
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 3
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 4
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 5
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 6
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 7
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 8
							|| player.getInventory().checkItem(41039) // 航海日誌
							// 9
							|| player.getInventory().checkItem(41039)) { // 航海日誌
						// 10
						htmlid = "voyager9";
					} else {
						htmlid = "voyager7";
					}
				} else if (player.getInventory().checkItem(49082) // 未完成航海日誌
						|| player.getInventory().checkItem(49083)
						|| player.getInventory().checkItem(49084)
						|| player.getInventory().checkItem(49085)
						|| player.getInventory().checkItem(49086)
						|| player.getInventory().checkItem(49087)
						|| player.getInventory().checkItem(49088)
						|| player.getInventory().checkItem(49089)
						|| player.getInventory().checkItem(49090)
						|| player.getInventory().checkItem(49091)) {
					// 追加狀態
					htmlid = "voyager7";
				}
			} else if (npcid == 80048) { // 空間歪
				final int level = player.getLevel();
				if (level <= 44) {
					htmlid = "entgate3";
				} else if ((level >= 45) && (level <= 51)) {
					htmlid = "entgate2";
				} else {
					htmlid = "entgate";
				}
			} else if (npcid == 71168) { // 真冥王 
				if (player.getInventory().checkItem(41028)) { // 書
					htmlid = "dantes1";
				}
			} else if (npcid == 80067) { // 諜報員(慾望洞窟)
				if (player.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == L1PcQuest.QUEST_END) {
					htmlid = "minicod10";
				} else if (player.getKarmaLevel() >= 1) {
					htmlid = "minicod07";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == 1)
						&& (player.getTempCharGfx() == 6034)) { // 變身
					htmlid = "minicod03";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == 1)
						&& (player.getTempCharGfx() != 6034)) {
					htmlid = "minicod05";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == L1PcQuest.QUEST_END // 影神殿側終了
				)
				|| player.getInventory().checkItem(41121) // 指令書
				|| player.getInventory().checkItem(41122)) { // 命令書
					htmlid = "minicod01";
				} else if (player.getInventory().checkItem(41130) // 血痕指令書
						&& player.getInventory().checkItem(41131)) { // 血痕命令書
					htmlid = "minicod06";
				} else if (player.getInventory().checkItem(41130)) { // 血痕命令書
					htmlid = "minicod02";
				}
			} else if (npcid == 81202) { // 諜報員(影神殿)
				if (player.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == L1PcQuest.QUEST_END) {
					htmlid = "minitos10";
				} else if (player.getKarmaLevel() <= -1) {
					htmlid = "minitos07";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == 1)
						&& (player.getTempCharGfx() == 6035)) { // 變身
					htmlid = "minitos03";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_SHADOWS) == 1)
						&& (player.getTempCharGfx() != 6035)) {
					htmlid = "minitos05";
				} else if ((player.getQuest().get_step(L1PcQuest.QUEST_DESIRE) == L1PcQuest.QUEST_END // 慾望洞窟側終了
				)
				|| player.getInventory().checkItem(41130) // 血痕指令書
				|| player.getInventory().checkItem(41131)) { // 血痕命令書
					htmlid = "minitos01";
				} else if (player.getInventory().checkItem(41121) // 指令書
						&& player.getInventory().checkItem(41122)) { // 命令書
					htmlid = "minitos06";
				} else if (player.getInventory().checkItem(41121)) { // 命令書
					htmlid = "minitos02";
				}
			} else if (npcid == 81208) { // 污
				if (player.getInventory().checkItem(41129) // 血痕精髓
						|| player.getInventory().checkItem(41138)) { // 精髓
					htmlid = "minibrob04";
				} else if ((player.getInventory().checkItem(41126) // 血痕墮落精髓
						&& player.getInventory().checkItem(41127) // 血痕無力精髓
						&& player.getInventory().checkItem(41128) // 血痕我執精髓
				)
				|| (player.getInventory().checkItem(41135) // 墮落精髓
						&& player.getInventory().checkItem(41136) // 我執精髓
						&& player.getInventory().checkItem(41137))) { // 我執精髓
					htmlid = "minibrob02";
				}
			} else if (npcid == 50113) { // 溪谷村 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orena14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orena0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orena2";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orena3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orena4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orena5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orena6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orena7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orena8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orena9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orena10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orena11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orena12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orena13";
				}
			} else if (npcid == 50112) { // 舊・歌島 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenb14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenb0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenb2";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenb3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenb4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenb5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenb6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenb7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenb8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenb9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenb10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenb11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenb12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenb13";
				}
			} else if (npcid == 50111) { // 話島 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenc14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenc1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenc0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenc3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenc4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenc5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenc6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenc7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenc8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenc9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenc10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenc11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenc12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenc13";
				}
			} else if (npcid == 50116) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orend14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orend3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orend1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orend0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orend4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orend5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orend6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orend7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orend8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orend9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orend10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orend11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orend12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orend13";
				}
			} else if (npcid == 50117) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orene14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orene3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orene4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orene1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orene0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orene5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orene6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orene7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orene8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orene9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orene10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orene11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orene12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orene13";
				}
			} else if (npcid == 50119) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenf14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenf3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenf4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenf5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenf1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenf0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenf6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenf7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenf8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenf9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenf10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenf11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenf12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenf13";
				}
			} else if (npcid == 50121) { // 火田村 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "oreng14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreng3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreng4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreng5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreng6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreng1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreng0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreng7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreng8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreng9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreng10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreng11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreng12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreng13";
				}
			} else if (npcid == 50114) { // 森 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenh14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenh3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenh4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenh5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenh6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenh7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenh1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenh0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenh8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenh9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenh10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenh11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenh12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenh13";
				}
			} else if (npcid == 50120) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "oreni14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreni3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreni4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreni5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreni6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreni7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreni8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreni1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreni0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreni9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreni10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreni11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreni12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreni13";
				}
			} else if (npcid == 50122) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenj14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenj3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenj4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenj5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenj6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenj7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenj8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenj9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenj1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenj0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenj10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenj11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenj12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenj13";
				}
			} else if (npcid == 50123) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenk14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenk3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenk4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenk5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenk6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenk7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenk8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenk9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenk10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenk1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenk0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenk11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenk12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenk13";
				}
			} else if (npcid == 50125) { // 象牙塔 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenl14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenl3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenl4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenl5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenl6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenl7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenl8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenl9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenl10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenl11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenl1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenl0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenl12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenl13";
				}
			} else if (npcid == 50124) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenm14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenm3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenm4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenm5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenm6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenm7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenm8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenm9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenm10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenm11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenm12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenm1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenm0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenm13";
				}
			} else if (npcid == 50126) { //  
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "orenn14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenn3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenn4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenn5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenn6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenn7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenn8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenn9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenn10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenn11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenn12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenn13";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenn1";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenn0";
				}
			} else if (npcid == 50115) { // 沈默洞窟 
				if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == L1PcQuest.QUEST_END) {
					htmlid = "oreno0";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreno3";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreno4";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreno5";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreno6";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreno7";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreno8";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreno9";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreno10";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreno11";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreno12";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreno13";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreno14";
				} else if (player.getQuest().get_step(L1PcQuest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreno1";
				}

			} else if (npcid == 70838) { // 
				if (player.isCrown() || player.isKnight() || player.isWizard()) {
					htmlid = "nerupam1";
				} else if (player.isDarkelf() && (player.getLawful() <= -1)) {
					htmlid = "nerupaM2";
				} else if (player.isDarkelf()) {
					htmlid = "nerupace1";
				} else if (player.isElf()) {
					htmlid = "nerupae1";
				}
			} else if (npcid == 80099) { // 治安團長
				if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 1) {
					if (player.getInventory().checkItem(41325, 1)) {
						htmlid = "rarson8";
					} else {
						htmlid = "rarson10";
					}
				} else if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 2) {
					if (player.getInventory().checkItem(41317, 1)
							&& player.getInventory().checkItem(41315, 1)) {
						htmlid = "rarson13";
					} else {
						htmlid = "rarson19";
					}
				} else if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 3) {
					htmlid = "rarson14";
				} else if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 4) {
					if (!(player.getInventory().checkItem(41326, 1))) {
						htmlid = "rarson18";
					} else if (player.getInventory().checkItem(41326, 1)) {
						htmlid = "rarson11";
					} else {
						htmlid = "rarson17";
					}
				} else if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) >= 5) {
					htmlid = "rarson1";
				}
			} else if (npcid == 80101) { // 
				if (player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 4) {
					if ((player.getInventory().checkItem(41315, 1))
							&& player.getInventory().checkItem(40494, 30)
							&& player.getInventory().checkItem(41317, 1)) {
						htmlid = "kuen4";
					} else if (player.getInventory().checkItem(41316, 1)) {
						htmlid = "kuen1";
					} else if (!player.getInventory().checkItem(41316)) {
						player.getQuest().set_step(
								L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT, 1);
					}
				} else if ((player.getQuest().get_step(
						L1PcQuest.QUEST_GENERALHAMELOFRESENTMENT) == 2)
						&& (player.getInventory().checkItem(41317, 1))) {
					htmlid = "kuen3";
				} else {
					htmlid = "kuen1";
				}
			}

			// html表示送信
			if (htmlid != null) { // htmlid指定場合
				if (htmldata != null) { // html指定場合表示
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (L1BlendTable.getInstance().containsKey(npcid)) {
					final String[] msgs = new String[42];
					Map<String, String> craftlist = L1BlendTable.getInstance().get_craftlist();

					if (!craftlist.isEmpty()) {
						for (int i = 0; i < 41; i++) {
							// msgs[i] = craftlist.get(npcid + "craft" + i);
							msgs[i] = craftlist.get(npcid + "craft" + i);
							if (msgs[i] == null || msgs[i].isEmpty()) {
								msgs[i] = "　";
							}
						}
					}

					if (msgs[0] != null) {
						player.sendPackets(new S_NPCTalkReturn(objid, "twcustomnpc", msgs));
					} else {
						player.sendPackets(new S_SystemMessage("沒有可以製造的道具。"));
					}
				} else {
					if (talking.getNormalAction() != null && talking.getNormalAction().equals("custom_taiwan_mahjong")) {
						CustomTaiwanMahjong.get().sendNpcTalk(player, objid);
					} else if (talking.getNormalAction() != null && talking.getNormalAction().equals("bigTrail")) {
						CustomBaccarat.getInstance().bigTrailTalk(player);
					} else if (player.getLawful() < -1000) { // 
						player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
					} else {
						player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
					}
				}
			}

			// 動作暫停
			set_stop_time(REST_MILLISEC);
			this.setRest(true);
		}
	}

	private static String talkToTownadviser(final L1PcInstance pc, final int town_id) {
		String htmlid;
		if ((pc.getHomeTownId() == town_id) && TownReading.get().isLeader(pc, town_id)) {
			htmlid = "secretary1";
		} else {
			htmlid = "secretary2";
		}

		return htmlid;
	}

	private static String talkToTownmaster(final L1PcInstance pc, final int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "hometown";
		} else {
			htmlid = "othertown";
		}
		return htmlid;
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
	}

	public void doFinalAction(final L1PcInstance player) {
	}

	private boolean checkHasCastle(final L1PcInstance player, final int castle_id) {
		if (player.getClanid() != 0) { // 所屬中
			final L1Clan clan = WorldClan.get().getClan(player.getClanname());
			if (clan != null) {
				if (clan.getCastleId() == castle_id) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkClanLeader(final L1PcInstance player) {
		if (player.isCrown()) { // 君主
			final L1Clan clan = WorldClan.get().getClan(player.getClanname());
			if (clan != null) {
				if (player.getId() == clan.getLeaderId()) {
					return true;
				}
			}
		}
		return false;
	}

	private int getNecessarySealCount(final L1PcInstance pc) {
		int rulerCount = 0;
		int necessarySealCount = 10;
		if (pc.getInventory().checkItem(40917)) { // 地支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40920)) { // 風支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40918)) { // 水支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40919)) { // 火支配者
			rulerCount++;
		}
		if (rulerCount == 0) {
			necessarySealCount = 10;
		} else if (rulerCount == 1) {
			necessarySealCount = 100;
		} else if (rulerCount == 2) {
			necessarySealCount = 200;
		} else if (rulerCount == 3) {
			necessarySealCount = 500;
		}
		return necessarySealCount;
	}

	private void createRuler(final L1PcInstance pc, final int attr, final int sealCount) {
		// 1.地屬性,2.火屬性,4.水屬性,8.風屬性
		int rulerId = 0;
		int protectionId = 0;
		int sealId = 0;
		if (attr == 1) {
			rulerId = 40917;
			protectionId = 40909;
			sealId = 40913;
		} else if (attr == 2) {
			rulerId = 40919;
			protectionId = 40911;
			sealId = 40915;
		} else if (attr == 4) {
			rulerId = 40918;
			protectionId = 40910;
			sealId = 40914;
		} else if (attr == 8) {
			rulerId = 40920;
			protectionId = 40912;
			sealId = 40916;
		}
		pc.getInventory().consumeItem(protectionId, 1);
		pc.getInventory().consumeItem(sealId, sealCount);
		final L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(143,
					this.getNpcTemplate().get_name(), item.getLogName())); // \f1%0%1。
		}
	}

	private String talkToRuba(final L1PcInstance pc) {
		String htmlid = "";

		if (pc.isCrown() || pc.isWizard()) {
			htmlid = "en0101";
		} else if (pc.isKnight() || pc.isElf() || pc.isDarkelf()) {
			htmlid = "en0102";
		}

		return htmlid;
	}

	private String talkToSecondtbox(final L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1PcQuest.QUEST_TBOX1) == L1PcQuest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxa";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}

	private String talkToThirdtbox(final L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1PcQuest.QUEST_TBOX2) == L1PcQuest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxd";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}
}
