package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_3;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 變卷任務<BR>
 * 500056<BR>
 * 說明:金色巴風特變卷 (全職30級以上任務)
 * @author dexc
 *
 */
public class Npc_Jem2 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jem2.class);

	private Npc_Jem2() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jem2();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
					// 完成任務
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem61"));
					
				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_3.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_3.QUEST.get_id())) {
						case 0:// 任務尚未開始
							// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_3.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
							// 接受任務
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;

						case 2:// 達到2(任務開始)
							// 交給任務道具
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;
						}
						
					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengde1"));
					}
				}

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
					// 完成任務
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem61"));
					
				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_3.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_3.QUEST.get_id())) {
						case 0:// 任務尚未開始
							// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_3.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
							// 接受任務
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;

						case 2:// 達到2(任務開始)
							// 交給任務
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;
						}
						
					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengde1"));
					}
				}
				
			} else if (pc.isElf()) {// 精靈
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
					// 完成任務
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem61"));
					
				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_3.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_3.QUEST.get_id())) {
						case 0:// 任務尚未開始
							// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_3.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
							// 接受任務
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;

						case 2:// 達到2(任務開始)
							// 交給任務道具
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;
						}
						
					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengde1"));
					}
				}

			} else if (pc.isWizard()) {// 法師
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
					// 完成任務
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem61"));
					
				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_3.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_3.QUEST.get_id())) {
						case 0:// 任務尚未開始
							// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_3.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
							// 接受任務
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;

						case 2:// 達到2(任務開始)
							// 交給任務道具
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw62"));
							break;
						}
						
					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));
					}
				}
	

			} else {
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw61"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		
		if (pc.isWizard()||pc.isCrown()||pc.isKnight()||pc.isElf()) {// 法師
			// 任務已經完成
			if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
				return;
			}

			if (cmd.equalsIgnoreCase("Variablevolume")) {// 交給骷髏弓*10的卡司特腰帶*10。
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv15_3.QUEST.get_id())) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 
							new int[]{
							40557,// 骷髏弓
							40558,// 卡司特腰帶						
								},
							new int[]{
									10,
									10,
								})
							< 1) {// 傳回可交換道具數小於10(需要物件不足)
						// 關閉對話窗
						pc.sendPackets(new S_CloseList(pc.getId()));
						
					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 
								new int[]{
								40557,// 骷髏弓
								40558,// 卡司特腰帶
								},
								new int[]{
									10,
									10,

								},
								new int[]{
								400525,// 金巴風特變身卷軸 x 10
								}, 
								1, 
								new int[]{
									10,
								}
						);// 給予
						
						// 提升任務進度
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, WizardLv15_3.QUEST.get_id());
						// 完成任務
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem61"));
					}
				}
				
			}
			
		} else {
			isCloseList = true;
		}
		
		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
