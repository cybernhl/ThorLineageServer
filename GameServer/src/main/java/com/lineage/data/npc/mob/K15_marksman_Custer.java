package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_2;
import com.lineage.data.quest.WizardLv15_3;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 卡司特<BR>
 * 45213<BR>
 * @author dexc
 *
 */
public class K15_marksman_Custer extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(K15_marksman_Custer.class);

	private K15_marksman_Custer() {
		// TODO Auto-generated constructor stub
	}
	
	public static NpcExecutor get() {
		return new K15_marksman_Custer();
	}

	@Override
	public int type() {
		return 8;
	}

	private static Random _random = new Random();

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);

			if (pc != null) {
				// LV15任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_3.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv15_3.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40558,10)) { // 已經具有物品 
						return pc;
					}
					if (_random.nextInt(100) < 50) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40558, 1);// 卡司特腰帶 x 10
					}
				}
			}
			return pc;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
