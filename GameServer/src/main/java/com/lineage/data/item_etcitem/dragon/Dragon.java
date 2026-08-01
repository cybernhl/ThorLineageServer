package com.lineage.data.item_etcitem.dragon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * <font color=#00800>49500	龍 結晶</font><BR>
 * 生命  額外攻擊點+2   攻擊迴避提升   魔法傷害減免   魔法重擊增加 ，持續1200秒
 * @author dexc
 *
 */
public class Dragon extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Dragon.class);

	/**
	 *
	 */
	private Dragon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Dragon();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}

		int time = L1BuffUtil.cancelDragon(pc);
		if (time != -1) {
			// 1,139：%0 分鐘之內無法使用。  
			pc.sendPackets(new S_ServerMessage(1139, String.valueOf(time / 60)));
			return;
		}

		pc.getInventory().removeItem(item, 1);
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7467));

		// SKILL移轉
		final SkillMode mode = L1SkillMode.get().getSkill(L1SkillId.DRAGON5);
		if (mode != null) {
			try {
				mode.start(pc, null, null, 1200);
				
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}