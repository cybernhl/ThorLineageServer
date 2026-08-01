package com.lineage.data.npc.quest2;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ADLv80_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.WorldQuest;

/**
 * 綠色 龍之門扉<BR>
 * 安塔瑞斯棲息地 
 * 70932<BR>
 * 
 * @author dexc
 *
 */
public class Npc_DragonA1 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_DragonA1.class);

	/**已經參加過的人員列表*/
	private static final Map<Integer, String> _playList = new HashMap<Integer, String>();
	
	private Npc_DragonA1() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_DragonA1();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (isError(pc, npc)) {
				return;
			}

			// 傳送前往<font fg=66ff66><var src="#0"></font>執行副本條件限制(6,529：安塔瑞斯棲息地 )
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon1", new String[]{"安塔瑞斯棲息地"}));
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equalsIgnoreCase("0")) {// 傳送進入
			if (isError(pc, npc)) {
				return;
			}
			// 安塔瑞斯棲息地 
			staraQuestA(pc);
			
		}
		
		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 進入副本執行任務(安塔瑞斯棲息地)
	 * @param pc
	 * @return 
	 */
	private void staraQuestA(L1PcInstance pc) {
		try {
			// 任務編號
			final int questid = ADLv80_1.QUEST.get_id();
			
			// 任務地圖編號
			final int mapid = ADLv80_1.MAPID;
			
			// 取回新的任務副本編號
			final int showId = WorldQuest.get().nextId();
			
			// 進入人數限制
			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) {// 無限制
				users = Byte.MAX_VALUE;// 設置為127
			}
			
			final L1Party party = pc.getParty();
			
			if (party != null) {
				int i = 0;
				// 隊伍成員
				for (L1PcInstance otherPc : party.partyUsers().values()) {
					if (i <= (users - 1)) {
						if (otherPc.getId() != party.getLeaderID()) {
							// 加入副本執行成員
							WorldQuest.get().put(showId, mapid, questid, otherPc);
							_playList.put(new Integer(otherPc.getId()), otherPc.getName());
							// 傳送成員
							L1Teleport.teleport(otherPc, 32595, 32747, (short) mapid, 1, true);
							
							// 將任務設置為執行中
							QuestClass.get().startQuest(otherPc, ADLv80_1.QUEST.get_id());
							// 將任務設置為結束
							QuestClass.get().endQuest(otherPc, ADLv80_1.QUEST.get_id());
						}
					}
					i++;
				}
			}

			// 加入副本執行成員
			final L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid, pc);

			if (quest == null) {
				_log.error("副本設置過程發生異常!!");
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			
			// 取回進入時間限制
			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}
			
			// 召喚門0:/ 1:\  ↓Y←X
			//L1SpawnUtil.spawnDoor(quest, 10008, 1796, 32680, 32745, (short) mapid, 1);// T
			
			L1SpawnUtil.spawnDoor(quest, 10005, 7556, 32759, 32799, (short) mapid, 1);// A
			L1SpawnUtil.spawnDoor(quest, 10006, 7556, 32787, 32848, (short) mapid, 1);// B
			L1SpawnUtil.spawnDoor(quest, 10007, 7556, 32781, 32919, (short) mapid, 1);// C
			
			final L1Location loc = new L1Location(32781, 32705, mapid);
			L1SpawnUtil.spawn(71014, loc, 5, showId);// 新安塔瑞斯(1階段)
			
			// 設置副本參加編號(已經在WorldQuest加入編號)
			//pc.set_showId(showId);
			// 傳送任務執行者
			L1Teleport.teleport(pc, 32595, 32747, (short) mapid, 1, true);
			
			// 將任務設置為執行中
			QuestClass.get().startQuest(pc, ADLv80_1.QUEST.get_id());
			// 將任務設置為結束
			QuestClass.get().endQuest(pc, ADLv80_1.QUEST.get_id());
			
			_playList.put(new Integer(pc.getId()), pc.getName());
			
			// 移除掉落物
			for (L1NpcInstance npc : quest.npcList()) {
				if (npc instanceof L1MonsterInstance) {
					final L1MonsterInstance mob = (L1MonsterInstance) npc;
					if (mob.getNpcId() == 71014) {// 新安塔瑞斯(1階段)
						continue;
					}
					if (mob.getNpcId() == 71015) {// 新安塔瑞斯(2階段)
						continue;
					}
					if (mob.getNpcId() == 71016) {// 新安塔瑞斯(3階段)
						continue;
					}
					mob.set_storeDroped(true);
					mob.getInventory().clearItems();
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 執行條件
	 * @param pc
	 * @param npc
	 * @return
	 */
	private boolean isError(L1PcInstance pc, L1NpcInstance npc) {
		if (pc.isGm()) {
			return false;
		}
		final L1Party party = pc.getParty();
		if (party == null) {
			// 必須在<font fg=66ff66>隊伍狀態</font>。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon2"));
			return true;
		}
		if (!party.isLeader(pc)) {
			// 必須由隊伍隊長執行傳送命令。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon6"));
			return true;
		}

		if (party.getNumOfMembers() < 3) {
			// 隊伍成員必須超過<font fg=66ff66>5人</font>。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon3"));
			return true;
		}
		
		StringBuilder list80 = null;
		StringBuilder list = null;
		for (L1PcInstance tgpc : party.partyUsers().values()) {
			// 等級低於80
			if (tgpc.getLevel() < 80) {
				if (list80 == null) {
					list80 = new StringBuilder();
				}
				list80.append(tgpc.getName() + " ");
			}
			// 參加過
			if (_playList.get(new Integer(tgpc.getId())) != null) {
				if (list == null) {
					list = new StringBuilder();
				}
				list.append(tgpc.getName() + " ");
			}
		}
		
		if (list80 != null) {
			// 隊伍成員必須<font fg=66ff66>80級</font>以上。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon4", new String[]{list80.toString()}));
			return true;
		}
		
		if (list != null) {
			// 副本任務<font fg=66ff66>每次服務器重新啟動</font>可執行一次。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon5", new String[]{list.toString()}));
			return true;
		}
		return false;
	}
}
