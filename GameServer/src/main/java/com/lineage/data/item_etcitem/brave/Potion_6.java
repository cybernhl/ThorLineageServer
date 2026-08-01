package com.lineage.data.item_etcitem.brave;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 名譽貨幣<br>
 * 名譽貨幣 - 40733<br>
 * 名譽貨幣(新手禮包) - 49299<br>
 */
public class Potion_6 extends ItemExecutor {

	/**
	 *
	 */
	private Potion_6() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Potion_6();
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
		
		if (L1BuffUtil.stopPotion(pc)) {
			this.useBravePotion(pc);
			pc.getInventory().removeItem(item, 1);
		}
	}

	private void useBravePotion(final L1PcInstance pc) {
		// // 解除魔法技能絕對屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		final int time = 600;

		// 勇敢效果 抵銷對應技能
		L1BuffUtil.braveStart(pc);
		
		int mode = 1;
		int skillMode = L1SkillId.STATUS_BRAVE;
		
		pc.sendPackets(new S_SkillBrave(pc.getId(), mode, time));
		pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), mode, 0));
		
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 751));
		pc.setSkillEffect(skillMode, time * 1000);

		pc.setBraveSpeed(1);
	}
}