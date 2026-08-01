package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_1;
import com.lineage.data.quest.WizardLv15_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 詹姆<BR>
 * 70531<BR>
 * 說明:詹姆的請求 (法師15級以上官方任務)
 * @author dexc
 *
 */
public class Npc_Jem1 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jem1.class);

	private Npc_Jem1() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jem1();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));

			} else if (pc.isKnight()) {// 騎士
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
				
			} else if (pc.isElf()) {// 精靈
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));

			} else if (pc.isWizard()) {// 法師
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_2.QUEST.get_id())) {
					// 如果想要瞭解更多魔法的事情，請去找吉倫吧。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
					
				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_2.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_2.QUEST.get_id())) {
						case 0:// 任務尚未開始
							// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw1"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_2.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
							// 接受畢業考驗
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw1"));
							break;

						case 2:// 達到2(任務開始)
							// 交給史巴托骨頭
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw1"));
							break;
						}
						
					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengde1"));
					}
				}

				
			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));

			} else {
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		
		if (pc.isWizard()) {// 法師
			// 任務已經完成
			if (pc.getQuest().isEnd(WizardLv15_2.QUEST.get_id())) {
				return;
			}

			if (cmd.equalsIgnoreCase("gerengtest")) {// 交給食屍鬼的牙齒與指甲。
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv15_2.QUEST.get_id())) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 
							new int[]{
									40556,// 史巴托的骨頭

								},
							new int[]{
									1,
								})
							< 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 關閉對話窗
						pc.sendPackets(new S_CloseList(pc.getId()));
						
					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 
								new int[]{
									40556,// 史巴托的骨頭

								},
								new int[]{
									1,

								},
								new int[]{
									126,// 瑪那魔杖 x 1
								}, 
								1, 
								new int[]{
									1,
								}
						);// 給予
						
						// 提升任務進度
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, WizardLv15_2.QUEST.get_id());
						// 如果想要瞭解更多魔法的事情，請去找吉倫吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
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
