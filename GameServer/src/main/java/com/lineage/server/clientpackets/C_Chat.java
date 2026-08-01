package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lineage.config.ConfigOther;
import com.lineage.data.item_etcitem.skill.Skill_RemainingTime;
import com.lineage.data.npc.game.CustomDots;
import com.lineage.data.npc.shop.CustomPlayerShopByNpc;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRecord;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * 要求使用一般聊天頻道
 *
 * @author daien
 *
 */
public class C_Chat extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Chat.class);

	public C_Chat(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			final L1PcInstance pc = client.getActiveChar();

			if (decrypt.length > 108) {
				_log.warn("人物:" + pc.getName() + "對話長度超過限制:" + client.getIp().toString());
				client.set_error(client.get_error() + 1);
				return;
			}

			boolean isStop = false;// 停止輸出
			
			boolean errMessage = false;// 異常訊息
			
			// 中毒狀態
			if (pc.hasSkillEffect(SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(AREA_OF_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 中毒狀態
			if (pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
				if (!pc.isGm()) {
					isStop = true;
				}
			}

			// 你從現在被禁止閒談。
			if (pc.hasSkillEffect(STATUS_CHAT_PROHIBITED)) {
				isStop = true;
				errMessage = true;
			}

			if (isStop) {
				if (errMessage) {
					pc.sendPackets(new S_ServerMessage(242));
				}
				return;
			}

			// 取回對話內容
			final int chatType = this.readC();
			final String chatText = this.readS();
			if (chatText.equals("商店")) {
				CustomPlayerShopByNpc.getInstance().UseShop(pc);
				return;
			}
			if (chatText.equalsIgnoreCase("skill") || chatText.equals("技能")) {
				Skill_RemainingTime.get().execute(null, pc, null);
				return;
			}

//			if (chatText.equals("阿醜")) {
//				CustomDots.getInstance().setBankerOID(pc);
//				return;
//			}
			
			switch (chatType) {
			case 0:// 一般頻道
				if (pc.checkFV(chatText.trim())) {
					pc.setFV(true);
					return;
				}
				if (pc.is_retitle()) {
					re_title(pc, chatText.trim());
					return;
				}
				if (pc.is_repass() != 0) {
					re_repass(pc, chatText.trim());
					return;
				}
				chatType_0(pc, chatText);
				break;

			case 2: // 大叫頻道(!)
				chatType_2(pc, chatText);
				break;

			case 4: // 血盟頻道(@)
				chatType_4(pc, chatText);
				break;

			case 11: // 隊伍頻道(#)
				if (ConfigRecord.GM_OVERHEARD11) {
					for (L1Object visible : World.get().getAllPlayers()) {
						if ((visible instanceof L1PcInstance)) {
							L1PcInstance GM = (L1PcInstance) visible;
							if ((GM.isGm()) && (pc.getId() != GM.getId()) && !pc.isFV()) {
								GM.sendPackets(new S_SystemMessage("【隊伍】" + pc.getName() + ":" + chatText));
							}
						}
					}
				}
				chatType_11(pc, chatText);
				break;

			case 13: // 連盟頻道(%)
				if (ConfigRecord.GM_OVERHEARD13) {
					for (L1Object visible : World.get().getAllPlayers()) {
						if ((visible instanceof L1PcInstance)) {
							L1PcInstance GM = (L1PcInstance) visible;
							if ((GM.isGm()) && (pc.getId() != GM.getId()) && !pc.isFV()) {
								GM.sendPackets(new S_SystemMessage("【聯盟】" + pc.getName() + ":" + chatText));
							}
						}
					}
				}
				chatType_13(pc, chatText);
				break;

			case 14: // 隊伍頻道(聊天)
				if (ConfigRecord.GM_OVERHEARD11) {
					for (L1Object visible : World.get().getAllPlayers()) {
						if ((visible instanceof L1PcInstance)) {
							L1PcInstance GM = (L1PcInstance) visible;
							if ((GM.isGm()) && (pc.getId() != GM.getId())) {
								GM.sendPackets(new S_SystemMessage("【隊伍】" + pc.getName() + ":" + chatText));
							}
						}
					}
				}
				chatType_14(pc, chatText);
				break;
			}

			if (!pc.isGm()) {
				pc.checkChatInterval();
			}

			if (!pc.isFV() && ConfigRecord.GM_OVERHEARD0) {
				for (L1Object visible : World.get().getAllPlayers()) {
					if ((visible instanceof L1PcInstance)) {
						L1PcInstance GM = (L1PcInstance) visible;
						if ((GM.isGm()) && (pc.getId() != GM.getId())) {
							GM.sendPackets(new S_SystemMessage("【一般】" + pc.getName() + ":" + chatText));
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

	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
	
	private void re_repass(L1PcInstance pc, String password) {
		try {
			switch (pc.is_repass()) {
			case 1:// 輸入舊密碼
				if (!pc.getNetConnection().getAccount().get_password().equals(password)) {
					pc.getInventory().storeItem(44063, 1L);
					// 1,744：密碼錯誤  
					pc.sendPackets(new S_ServerMessage(1744));
					pc.repass(0);
					return;
				}
				pc.repass(2);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[]{"請輸入您的新密碼"}));
				break;
				
			case 2:// 輸入新密碼
				boolean iserr = false;
				for (int i = 0 ; i < password.length() ; i++) {
					final String ch = password.substring(i, i + 1);
					if (!_check_pwd.contains(ch.toLowerCase())) {
						// 1,742：帳號或密碼中有無效的字元  
						pc.sendPackets(new S_ServerMessage(1742));
						iserr = true;
						break;
					}
				}
				if (password.length() > 13) {
					// 1,742：帳號或密碼中有無效的字元  
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (password.length() < 3) {
					// 1,742：帳號或密碼中有無效的字元  
					pc.sendPackets(new S_ServerMessage(166, "密碼長度過長"));
					iserr = true;
				}
				if (iserr) {
					pc.getInventory().storeItem(44063, 1L);
					pc.repass(0);
					return;
				}
				pc.setText(password);
				pc.repass(3);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "y_pass_01", new String[]{"請確認您的新密碼"}));
				break;
				
			case 3:// 確認新密碼
				if (!pc.getText().equals(password)) {
					pc.repass(0);
					pc.getInventory().storeItem(44063, 1L);
					// 1,982：所輸入的密碼不一致.請重新輸入.
					pc.sendPackets(new S_ServerMessage(1982));
					return;
				}
				pc.sendPackets(new S_CloseList(pc.getId()));
				// 1,985：角色密碼成功地變更.(忘記密碼時請至天堂網站詢問)  
				pc.sendPackets(new S_ServerMessage(1985));
				AccountReading.get().updatePwd(pc.getAccountName(), password);
				pc.setText(null);
				pc.repass(0);
				break;
			}
			
		} catch (final Exception e) {
			pc.sendPackets(new S_CloseList(pc.getId()));
			// 45：未知的錯誤%d   
			pc.sendPackets(new S_ServerMessage(45));
			pc.setText(null);
			pc.repass(0);
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 變更封號
	 * @param pc
	 * @param chatText
	 */
	private void re_title(final L1PcInstance pc, final String chatText) {
		try {
			final String newchatText = chatText.trim();
			if (newchatText.isEmpty() || newchatText.length() <= 0) {
				pc.sendPackets(new S_ServerMessage("\\fU請輸入封號內容"));
				return;
			}
			final int length = 18;// 長度判斷
			if (newchatText.getBytes().length > length) {
				pc.sendPackets(new S_ServerMessage("\\fU封號長度過長"));
				return;
			}
			final StringBuilder title = new StringBuilder();
			title.append(newchatText);
			
			pc.setTitle(title.toString());
			pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
			pc.save();
			pc.retitle(false);
			pc.sendPackets(new S_ServerMessage("\\fU封號變更完成"));
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 隊伍頻道(聊天)
	 * @param pc
	 * @param chatText
	 */
	private void chatType_14(final L1PcInstance pc, final String chatText) {
		if (pc.isInChatParty()) {
			S_ChatParty2 chatpacket = new S_ChatParty2(pc, chatText);
			final L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
			for (final L1PcInstance listner : partyMembers) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 14);
			}
		}
	}

	/**
	 * 連盟頻道(%)
	 * @param pc
	 * @param chatText
	 */
	private void chatType_13(final L1PcInstance pc, final String chatText) {
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan == null) {
				return;
			}
			switch (pc.getClanRank()) {
			case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士 
			case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
			case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主 
			case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主 
			case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
				final S_ChatClanUnion chatpacket = new S_ChatClanUnion(pc, chatText);
				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						switch (listner.getClanRank()) {
						case L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN:// 6:守護騎士 
						case L1Clan.NORMAL_CLAN_RANK_GUARDIAN:// 9:守護騎士
						case L1Clan.CLAN_RANK_GUARDIAN:// 3:副君主 
						case L1Clan.CLAN_RANK_PRINCE:// 4:聯盟君主 
						case L1Clan.NORMAL_CLAN_RANK_PRINCE:// 10:聯盟君主
							listner.sendPackets(chatpacket);
							break;
						}
					}
				}

				if (ConfigRecord.LOGGING_CHAT_COMBINED) {
					LogChatReading.get().noTarget(pc, chatText, 13);
				}
				break;
			}
		}
	}

	/**
	 * 隊伍頻道(#)
	 * @param pc
	 * @param chatText
	 */
	private void chatType_11(final L1PcInstance pc, final String chatText) {
		if (pc.isInParty()) {
			S_ChatParty chatpacket = new S_ChatParty(pc, chatText);

			final ConcurrentHashMap<Integer, L1PcInstance> pcs = pc.getParty().partyUsers();
			
			if (pcs.isEmpty()) {
				return;
			}
			if (pcs.size() <= 0) {
				return;
			}
			
			for (final Iterator<L1PcInstance> iter = pcs.values().iterator(); iter.hasNext();) {
				final L1PcInstance listner = iter.next();
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(chatpacket);
				}
			}

			if (ConfigRecord.LOGGING_CHAT_PARTY) {
				LogChatReading.get().noTarget(pc, chatText, 11);
			}
		}
	}

	/**
	 * 血盟頻道(@)
	 * @param pc
	 * @param chatText
	 */
	private void chatType_4(final L1PcInstance pc, final String chatText) {
		if (ConfigRecord.GM_OVERHEARD4) {
			for (L1Object visible : World.get().getAllPlayers()) {
				if ((visible instanceof L1PcInstance)) {
					L1PcInstance GM = (L1PcInstance) visible;
					if ((GM.isGm()) && (pc.getId() != GM.getId()) && !pc.isFV()) {
						GM.sendPackets(new S_SystemMessage("【血盟】" + pc.getName() + ":" + chatText));
					}
				}
			}
		}
		if (pc.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				final S_ChatClan chatpacket = new S_ChatClan(pc, chatText);
				final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
				for (final L1PcInstance listner : clanMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(chatpacket);
					}
				}

				if (ConfigRecord.LOGGING_CHAT_CLAN) {
					LogChatReading.get().noTarget(pc, chatText, 4);
				}
			}
		}
	}

	/**
	 * 大叫頻道(!)
	 * @param pc
	 * @param chatText
	 */
	private void chatType_2(final L1PcInstance pc, final String chatText) {
		if (pc.isGhost()) {
			return;
		}
		if (pc.get_food() < 6) {
			pc.sendPackets(new S_ServerMessage(462));
			return;
		}
		pc.set_food(pc.get_food() - ConfigOther.SET_BIG_CHAT_COUNT);
		pc.sendPackets(new S_OwnCharStatus(pc));
		S_ChatShouting chatpacket = new S_ChatShouting(pc, chatText);
		pc.sendPackets(chatpacket);
		for (final L1PcInstance listner : World.get().getVisiblePlayer(pc, 50)) {
			if (!listner.getExcludingList().contains(pc.getName())) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(chatpacket);
				}
			}
		}

		if (ConfigRecord.LOGGING_CHAT_SHOUT) {
			LogChatReading.get().noTarget(pc, chatText, 2);
		}
		// 變形怪重複對話
		this.doppelShouting(pc, chatText);
	}

	/**
	 * 一般頻道
	 * @param pc
	 * @param chatText
	 */
	private void chatType_0(final L1PcInstance pc, final String chatText) {
		if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
			return;
		}
		if (pc.isFV()) {
			if (chatText.startsWith(".")) {
				final String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}
		}

		if (pc.getAccessLevel() > 0) {
			// GM命令
			if (chatText.startsWith(".")) {
				final String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}
		}
	
		
		// 開啟決鬥
		/*if (chatText.equals("pvp")) {
			if (!pc.isPVP()) {
				pc.setPVP(true);
				pc.sendPackets(new S_SystemMessage("強制攻擊開啟!!"));
				pc.sendPackets(new S_SystemMessage("請按住ctrl攻擊下目標，即可啟動免ctrl功能!!"));
			} else {
				pc.setPVP(false);
				pc.setFightId(0);
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
				pc.sendPackets(new S_SystemMessage("請注意，強制攻擊關閉!!"));
			}
		}
		
		// 掛機瞬移開關
		if (chatText.equals("sy")) {
			if (!pc.isgjsy()) {
				pc.setgjsy(true);
				pc.sendPackets(new S_SystemMessage("掛機瞬移開啟!!"));				
			} else {
				pc.setgjsy(false);				
				pc.sendPackets(new S_SystemMessage("掛機瞬移關閉!!"));
			}
		}*/
		if (chatText.equals("垃圾清除")) {
		  L1NpcInstance newnpc = 
		          L1SpawnUtil.spawnT(80000, 
		          pc.getX(), 
		          pc.getY(), 
		          pc.getMapId(), 
		          pc.getHeading(), 10);
		  newnpc.onNpcAI();
		  return;
		}
		// 添加喇叭系统 1015088888
		if (pc.isBigChat()) {
			if (pc.getInventory().consumeItem(99000, 1)) {
				World.get().broadcastPacketToAll(new S_SystemMessage("\\fX玩家 " + pc.getName() + " 廣播內容:" + chatText));
				for (L1PcInstance tg : World.get().getAllPlayers()) {
					try {
						tg.sendPackets( new S_BlueMessage(166,"\\f2玩家 " + pc.getName() + " 廣播內容:" + chatText));
					} catch (Exception exception) {
					}
				}
			} else {
				pc.setBigChat(false);
				pc.sendPackets(new S_SystemMessage("大聲公屏已用完。"));
			}
		}

		// 產生封包
		S_Chat chatpacket = new S_Chat(pc, chatText);
		pc.sendPackets(chatpacket);
		/** 說話輸入 指令 解卡點 */
		if (chatText.equalsIgnoreCase("解卡點")) {
			if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
			}
			if (pc.hasSkillEffect(55688)) {
				pc.sendPackets(new S_SystemMessage("指令使用的太快了。"));
				return;
			} else {
				L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), 5, true);
				pc.setSkillEffect(55688, 3 * 1000);
			}
		}
		if (chatText.equalsIgnoreCase("組隊訊息")) {
			pc.setDropPartyMsg();
			return;
		}
		for (final L1PcInstance listner : World.get().getRecognizePlayer(pc)) {
			if (!listner.getExcludingList().contains(pc.getName())) {
				// 副本ID相等
				if (pc.get_showId() == listner.get_showId()) {
					listner.sendPackets(chatpacket);
				}
			}
		}

		// 外掛檢測 by：樂在其中
		if (pc.get_showGm() != null) { // 掛機提問回答
			try {
				final int ch = Integer.parseInt(chatText);
				if (ch == pc.get_showGm().getInt()) {
					// pc.get_showGm().stopGm();
				}
				if (chatText.startsWith(String
						.valueOf(pc.get_showGm().getInt()))) {// 檢測外掛狀態 hjx1000
					pc.get_showGm().stopGm();
					final long nowTick = System.currentTimeMillis();
					if ((nowTick - pc.getCheckPluginTick()) < ConfigOther.check_plugin_delay) {
						WriteLogTxt.GmLog("疑似外掛紀錄.txt", "玩家:" + pc.getName() + " 疑似使用外掛自動回復");
					}
				} else {
					pc.setChack_game(pc.getChack_game() + 1);
					pc.sendPackets(new S_SystemMessage("此次問答您已經回答錯誤了"
							+ pc.getChack_game() + "次,如果連續錯誤3次將直接斷線"));
					if (pc.getChack_game() >= 3) {
						pc.getNetConnection().kick();// 中斷
					}
				}
			} catch (final Exception e) {
			}
		}

		// 對話紀錄
		if (ConfigRecord.LOGGING_CHAT_NORMAL) {
			LogChatReading.get().noTarget(pc, chatText, 0);
		}
		// 變形怪重複對話
		doppelGenerally(pc, chatText);
	}

	/**
	 * 變形怪重複對話(一般頻道)
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelGenerally(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChat(mob, chatText));
				}
			}
		}
	}

	/**
	 * 變形怪重複對話(大喊頻道)
	 * @param pc
	 * @param chatType
	 * @param chatText
	 */
	private void doppelShouting(final L1PcInstance pc, final String chatText) {
		// 變形怪重複對話
		for (final L1Object obj : pc.getKnownObjects()) {
			if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
					mob.broadcastPacketX8(new S_NpcChatShouting(mob, chatText));
				}
			}
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
