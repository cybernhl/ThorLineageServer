package com.lineage.server.clientpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.custom.clan.ClanSpecialStatFactory;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.event.CardSet;
import com.lineage.data.event.OnlineGiftSet;
import com.lineage.data.npc.Npc_clan;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.GetBackRestartTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.CharacterConfigReading;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.templates.L1Config;
import com.lineage.server.templates.L1EmblemIcon;
import com.lineage.server.templates.L1GetBackRestart;
import com.lineage.server.templates.L1PcOtherList;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1UserSkillTmp;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldSummons;
import com.lineage.server.world.WorldWar;

/**
 * 要求進入遊戲
 *
 * @author daien
 *
 */
public class C_LoginToServer extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_LoginToServer.class);

	public C_LoginToServer(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final String loginName = client.getAccountName();
			// System.out.println("帳號: " + login);
			// System.out.println("人物名稱: " + charName);

			if (client.getActiveChar() != null) {
				_log.error("帳號重複登入人物: " + loginName + "強制中斷連線");
				client.kick();
				return;
			}

			final String charName = this.readS();

			final L1PcInstance pc = L1PcInstance.load(charName, client);

			if ((pc == null) || !loginName.equals(pc.getAccountName())) {
				_log.info("無效登入要求: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");
				client.kick();
				return;
			}

			/*if (Config.LEVEL_DOWN_RANGE != 0) {
				if (pc.getHighLevel() - pc.getLevel() >= Config.LEVEL_DOWN_RANGE) {
					_log.info("超出人物可創範圍: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");
					client.kick();
					return;
				}
			}*/

			_log.info("登入遊戲: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");

			pc.setNetConnection(client);// 登記封包接收組

			final int currentHpAtLoad = pc.getCurrentHp();
			final int currentMpAtLoad = pc.getCurrentMp();

			// 重置錯誤次數
			client.set_error(0);

			pc.clearSkillMastery();// 清除技能資訊
			pc.setOnlineStatus(1);// 設定連線狀態

			CharacterTable.updateOnlineStatus(pc);
			World.get().storeObject(pc);

			pc.setNetConnection(client);// 登記封包接收組
			client.setActiveChar(pc);// 登記玩家資料

			this.getOther(pc);// 額外紀錄資料

			// 宣告進入遊戲
			pc.sendPackets(new S_EnterGame());
			
			pc.sendPackets(new S_Exp(pc));

			// 讀取角色道具
			this.items(pc);

			// 取得記憶座標資料
			this.bookmarks(pc);

			// 判斷座標資料
			this.backRestart(pc);

			// 取得遊戲焦點
			this.getFocus(pc);

			pc.sendVisualEffectAtLogin();

			this.skills(pc);// 取得角色魔法技能資料

			pc.turnOnOffLight();

			if (pc.getCurrentHp() > 0) {
				pc.setDead(false);
				pc.setStatus(0);
				
			} else {
				pc.setDead(true);
				pc.setStatus(ActionCodes.ACTION_Die);
			}
			
			// 取回快速鍵紀錄
			final L1Config config = 
				CharacterConfigReading.get().get(pc.getId());
			if (config != null) {
				pc.sendPackets(new S_PacketBoxConfig(config));
			}

			this.serchSummon(pc);// 取得殘留寵物資訊

			ServerWarExecutor.get().checkCastleWar(pc);// 檢查城堡戰爭狀態

			this.war(pc);// 戰爭狀態

			this.marriage(pc);// 取得婚姻資料

			if (currentHpAtLoad > pc.getCurrentHp()) {
				pc.setCurrentHp(currentHpAtLoad);
			}
			if (currentMpAtLoad > pc.getCurrentMp()) {
				pc.setCurrentMp(currentMpAtLoad);
			}

			this.buff(pc);// 取得物品與魔法特殊效果

			pc.startHpRegeneration();
			pc.startMpRegeneration();
			
			pc.startObjectAutoUpdate();// PC 可見物更新處理
			
			this.crown(pc);// 送出王冠資料

			pc.save(); // 資料回存

			if (pc.getHellTime() > 0) {
				pc.beginHell(false);
			}
			
			// 載入人物任務資料
			pc.getQuest().load();
			
			
            bonusstats(pc);
			if (CardSet.START) {// 月卡系统
				CardSet.load_card_mode(pc);
			}
			
			// 送出展示視窗
			pc.showWindows();
			
			if (pc.getLevel() <= 20) {// LOLI 戰鬥特化
				pc.sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 1));
			}
			if (pc.getAccessLevel() >= 1) {
				pc.setGmInvis(true);
				pc.sendPackets(new S_Invis(pc.getId(), 1));
				pc.broadcastPacketAll(new S_RemoveObject(pc));
			}
			bonusstats(pc);// 送出加點能力值選取
			pc.sendPackets(new S_ServerMessage("找私服請搜尋 私服123 網址:Gamex123.com"));
			ClanSpecialStatFactory.getInstance().clanStatSet(pc.getClan());
			for (L1Object visible : World.get().getAllPlayers()) {
				if ((visible instanceof L1PcInstance)) {
					L1PcInstance GM = (L1PcInstance) visible;
					if ((GM.isGm()) && (pc.getId() != GM.getId())) {
						GM.sendPackets(new S_SystemMessage("【" + pc.getName() + "】上線了"));
					}
				}
			}
//			pc.sendPackets(new S_ServerMessage("雷神科技-開服一條龍、抗攻擊主機 請洽LINE客服 @516tmskn"));
			
//			pc.sendPackets(new S_ServerMessage("\\fR伺服器由[伊薇JAVA技術團隊]研究修改"));
//			pc.sendPackets(new S_ServerMessage("\\fR任何行為[伊薇技術團隊]不附連帶責任"));
//			pc.sendPackets(new S_ServerMessage("\\fR伊薇JAVA論壇網址http://yiwei.co"));
			

			

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	/**
	 * 送出王冠資料
	 * @param pc
	 */
	private void crown(final L1PcInstance pc) {
		try {
			final Map<Integer, L1Clan> map = L1CastleLocation.mapCastle();
			for (Integer key : map.keySet()) {
				final L1Clan clan = map.get(key);
				if (clan != null) {
					if (key.equals(2)) {
						pc.sendPackets(new S_CastleMaster(8, clan.getLeaderId()));
						
					} else {
						pc.sendPackets(new S_CastleMaster(key, clan.getLeaderId()));
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得焦點
	 *
	 * @param pc
	 */
	private void getFocus( L1PcInstance pc) {
		try {
			// 重置副本編號
			pc.set_showId(-1);
			
			// 將物件增加到MAP世界裡
			World.get().addVisibleObject(pc);

			// 角色資訊
			pc.sendPackets(new S_OwnCharStatus(pc));

			// 更新角色所在的地圖
			pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));

			// 物件封包(本身)
			pc.sendPackets(new S_OwnCharPack(pc));

			final ArrayList<L1PcInstance> otherPc = World.get().getVisiblePlayer(pc);
			if (otherPc.size() > 0) {
				for (final L1PcInstance tg : otherPc) {
					// 物件封包(其他人物)
					tg.sendPackets(new S_OtherCharPacks(pc));
				}
			}

			// 更新魔攻與魔防
			pc.sendPackets(new S_SPMR(pc));
			
			// 閃避率更新 修正 thatmystyle (UID: 3602)
			pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));
			
			// 天氣效果
			pc.sendPackets(new S_Weather(World.get().getWeather()));
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 能力質選取資料
	 *
	 * @param pc
	 */
	private void bonusstats(final L1PcInstance pc) {
		try {
			// 人物屬性變更選取
			if ((pc.getLevel() >= 51) && (pc.getLevel() - 50 > pc.getBonusStats())) {
				if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
						+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < (ConfigAlt.POWER * 6)) {
					pc.sendPackets(new S_Bonusstats(pc.getId()));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得婚姻資料
	 *
	 * @param pc
	 */
	private void marriage(final L1PcInstance pc) {
		try {
			if (pc.getPartnerId() != 0) { // 結婚中
				final L1PcInstance partner = (L1PcInstance) World.get().findObject(pc.getPartnerId());
				if ((partner != null) && (partner.getPartnerId() != 0)) {
					if ((pc.getPartnerId() == partner.getId()) && (partner.getPartnerId() == pc.getId())) {
						// 548 你的情人目前正在線上。
						pc.sendPackets(new S_ServerMessage(548));
						// 549 你的情人上線了!!
						partner.sendPackets(new S_ServerMessage(549));
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 其它數據
	 *
	 * @param pc
	 * @throws Exception
	 */
	private void getOther(final L1PcInstance pc) throws Exception {
		try {
			pc.set_otherList(new L1PcOtherList(pc));
			
			pc.addMaxHp(pc.get_other().get_addhp());
			pc.addMaxMp(pc.get_other().get_addmp());
			
			// 在線獎勵
			OnlineGiftSet.add(pc);

			final int time = pc.get_other().get_usemapTime();

			if (time > 0) {
				ServerUseMapTimer.put(pc, time);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得血盟 與 血盟戰爭資料
	 *
	 * @param pc
	 */
	private void war(final L1PcInstance pc) {
		try {
			if (pc.getClanid() != 0) { // 血盟資料不為0
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					// 判斷血盟名稱相等 雨 血盟編號相等
					if ((pc.getClanid() == clan.getClanId()) &&
							pc.getClanname().toLowerCase().equals(clan.getClanName().toLowerCase())) {
						final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
						for (final L1PcInstance clanMember : clanMembers) {
							if (clanMember.getId() != pc.getId()) {
								// 843 血盟成員%0%s剛進入遊戲。
								clanMember.sendPackets(new S_ServerMessage(843, pc.getName()));
							}
						}

						final int clanMan = clan.getOnlineClanMember().length;
						pc.sendPackets(new S_ServerMessage("\\fU線上血盟成員:" + clanMan));

						if (clan.isClanskill()) {
							switch (pc.get_other().get_clanskill()) {
							case 1:// 狂暴
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[0]));
								break;
							case 2:// 寂靜
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[1]));
								break;
							case 4:// 魔擊
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[2]));
								break;
							case 8:// 消魔
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[3]));
								break;
							}
						}

						// 送出盟輝
						final L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan.getClanId());
						if (emblemIcon != null) {
							pc.sendPackets(new S_Emblem(emblemIcon));
						}

						// 目前全部戰爭資訊取得
						for (final L1War war : WorldWar.get().getWarList()) {
							final boolean ret = war.checkClanInWar(pc.getClanname());
							if (ret) { // 是否正在戰爭中
								final String enemy_clan_name = war.getEnemyClanName(pc.getClanname());
								if (enemy_clan_name != null) {
									// \f1目前你的血盟與 %0 血盟交戰當中。
									pc.sendPackets(new S_War(8, pc.getClanname(), enemy_clan_name));
								}
								break;
							}
						}
					}
					
				} else {
					pc.setClanid(0);
					pc.setClanname("");
					pc.setClanRank(0);
					pc.save();
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 所在座標位置資料判斷
	 *
	 * @param pc
	 */
	private void backRestart(final L1PcInstance pc) {
		try {

			// 指定MAP回村設置
			final L1GetBackRestart gbr = 
				GetBackRestartTable.get().getGetBackRestart(pc.getMapId());
			if (gbr != null) {
				pc.setX(gbr.getLocX());
				pc.setY(gbr.getLocY());
				pc.setMap(gbr.getMapId());
			}

			// 戰爭區域回村設置
			final int castle_id = L1CastleLocation.getCastleIdByArea(pc);
			if (castle_id > 0) {
				if (ServerWarExecutor.get().isNowWar(castle_id)) {
					final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
					if (clan != null) {
						if (clan.getCastleId() != castle_id) {
							// 城主
							int[] loc = new int[3];
							loc = L1CastleLocation.getGetBackLoc(castle_id);
							pc.setX(loc[0]);
							pc.setY(loc[1]);
							pc.setMap((short) loc[2]);
						}
						
					} else {
						// 所屬居場合歸還
						int[] loc = new int[3];
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						pc.setX(loc[0]);
						pc.setY(loc[1]);
						pc.setMap((short) loc[2]);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得物品資料
	 *
	 * @param pc
	 */
	private void items(final L1PcInstance pc) {
		try {
			// 背包物品封包傳遞
			CharacterTable.restoreInventory(pc);
			final List<L1ItemInstance> items = pc.getInventory().getItems();
			if (items.size() > 0) {
				for (L1ItemInstance item : items) {
					pc.sendPackets(new S_AddItem(item));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得記憶座標資料
	 *
	 * @param pc
	 */
	private void bookmarks(final L1PcInstance pc) {
		try {
			final ArrayList<L1BookMark> bookList = CharBookReading.get().getBookMarks(pc);
			if (bookList != null) {
				if (bookList.size() > 0) {
					for (final L1BookMark book : bookList) {
						pc.sendPackets(new S_Bookmarks(book.getName(), book.getMapId(), book.getId()));
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得人物技能清單
	 *
	 * @param pc
	 */
	private void skills(final L1PcInstance pc) {
		try {
			final ArrayList<L1UserSkillTmp> skillList = 
				CharSkillReading.get().skills(pc.getId());
			
			final int[] skills = new int[22];
			
			if (skillList != null) {
				if (skillList.size() > 0) {
					for (final L1UserSkillTmp userSkillTmp : skillList) {
						// 取得魔法資料
						final L1Skills skill = 
							SkillsTable.get().getTemplate(userSkillTmp.get_skill_id());
						skills[(skill.getSkillLevel() - 1)] += skill.getId();
						//_log.error("skill.getSkillLevel() - 1" + (skill.getSkillLevel() - 1) + " skill.getId():" + skill.getId());
					}
					// 送出資料
					pc.sendPackets(new S_AddSkill(pc, skills));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得殘留寵物資訊
	 *
	 * @param pc
	 */
	private void serchSummon(final L1PcInstance pc) {
		try {
			final Collection<L1SummonInstance> summons = WorldSummons.get().all();
			if (summons.size() > 0) {
				for (final L1SummonInstance summon : summons) {
					if (summon.getMaster().getId() == pc.getId()) {
						summon.setMaster(pc);
						pc.addPet(summon);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得保留技能紀錄
	 * @param pc
	 */
	private void buff(final L1PcInstance pc) {
		try {
			// 保留技能紀錄
			CharBuffReading.get().buff(pc);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
   

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
