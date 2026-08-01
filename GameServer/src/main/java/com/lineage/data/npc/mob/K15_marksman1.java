package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_4;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 綠洲怪邪惡蜥蜴,毒蠍<BR>
 * 45381<BR>
 * @author dexc
 *
 */
public class K15_marksman1 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(K15_marksman1.class);

	private K15_marksman1() {
		// TODO Auto-generated constructor stub
	}
	
	public static NpcExecutor get() {
		return new K15_marksman1();
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
				if (pc.getQuest().isEnd(WizardLv15_4.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv15_4.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40560,20)) { // 已經具有物品 
						return pc;
					}
					if (_random.nextInt(100) < 100) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40560, 1);// 綠洲水晶(邪惡蜥蜴) x 1
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
