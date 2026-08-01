package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter00;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 尤麗婭<BR>
 * 91327<BR>
 * 說明:穿越時空的探險(秘譚)
 * @author dexc
 *
 */
public class Npc_YouLiya extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_YouLiya.class);

	private Npc_YouLiya() {
		// TODO Auto-generated constructor stub
	}
	
	public static NpcExecutor get() {
		return new Npc_YouLiya();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getInventory().checkItem(49312)) {// 身上有時空之甕
				// 天啊！！ 很高興見到你！！！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html00"));
				
			} else {
				// 天啊！！ 很高興見到你！！！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html01"));
			}

		} catch (final Exception e) {
			// 該訊息只有發生錯誤時才會顯示。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html05"));
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			boolean isCloseList = false;

			if (cmd.equalsIgnoreCase("a")) {// 「交出捐款和時空之玉。」
				int[] items = new int[]{
						40308, 49313
				};
				int[] counts = new int[]{
						10000, 1
				};
				
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, 
						items, // 需要物件
						counts)
						< 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 收取捐款10,000金幣和時空之玉。請確認身上有沒有。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html02"));
					
				} else {
					// 需要的物件確認
					final L1ItemInstance item1 = pc.getInventory().checkItemX(items[0], counts[0]);
					boolean error = false;
					if (item1 != null) {
						pc.getInventory().removeItem(item1, counts[0]);// 刪除道具
					} else {
						// 刪除失敗
						error = true;
					}
					// 需要的物件確認
					final L1ItemInstance item2 = pc.getInventory().checkItemX(items[1], counts[1]);
					if (item2 != null && !error) {
						long remove = counts[1];
						if (item2.getCount() >= 4) {
							remove = item2.getCount() - 2;
						}
						pc.getInventory().removeItem(item2, remove);// 刪除道具
					} else {
						// 刪除失敗
						error = true;
					}
					if (!error) {
						L1PolyMorph.undoPoly(pc);
						L1Teleport.teleport(pc, 32747, 32861, (short)9100, 5, true);
						
						// 將任務設置結束
						QuestClass.get().endQuest(pc, Chapter00.QUEST.get_id());
					}
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("d")) {// 「復原哈汀的日記本。」
				int[] items = new int[]{
						49301, 49302, 49303, 49304, 49305, 49306, 49307, 49308, 49309, 49310
				};
				int[] counts = new int[]{
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1
				};
				int[] gitems = new int[]{49311};// 黑暗哈汀的日記本
				int[] gcounts = new int[]{1};
				
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, 
						items, // 需要物件
						counts)
						< 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 製作日記本所需的日記不足
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html06"));
					
				} else {// 需要物件充足
					// 收回需要物件 給予完成物件
					CreateNewItem.createNewItem(pc, 
							items, counts, // 需要
							gitems, 1, gcounts);// 給予
					// 日記復原已完成
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html04"));
				}

			} else if (cmd.equalsIgnoreCase("c")) {// 「收下時空之甕。」
				if (!pc.getInventory().checkItem(49312)) {// 身上沒有時空之甕
					// 給予任務道具(時空之甕 49312)
					CreateNewItem.createNewItem(pc, 49312, 1);
					
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, Chapter00.QUEST.get_id());
					isCloseList = true;
					
				} else {
					// 已經給你時空之甕了。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html03"));
				}
			}

			if (isCloseList) {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}
			
		} catch (final Exception e) {
			// 該訊息只有發生錯誤時才會顯示。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_html05"));
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
