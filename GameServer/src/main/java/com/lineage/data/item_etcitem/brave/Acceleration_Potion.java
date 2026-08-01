package com.lineage.data.item_etcitem.brave;

import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.MASS_SLOW;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 綠色藥水(自我加速藥水)40013<br>
 * 綠色藥水(自我加速藥水)(祝福)140013<br>
 * 強化綠色藥水(強化自我加速藥水)40018<br>
 * 強化綠色藥水(強化自我加速藥水)(祝福)140018<br>
 * 紅酒40039<br>
 * 威士忌40040<br>
 * 象牙塔加速藥水40030<br>
 * 受祝福的葡萄酒41338<br>
 * 梅杜莎之血41342<br>
 */
public class Acceleration_Potion extends ItemExecutor {

	/**
	 *
	 */
	private Acceleration_Potion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Acceleration_Potion();
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
			final int itemId = item.getItemId();
			this.useGreenPotion(pc, itemId);
			pc.getInventory().removeItem(item, 1);
		}
	}

	private void useGreenPotion(final L1PcInstance pc, final int itemId) {
		// 解除魔法技能絕對屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		int time = 0;
		if (itemId == 40013) { // 綠色藥水(自我加速藥水)
			time = 300;

		} else if (itemId == 140013) { // 綠色藥水(自我加速藥水)(祝福)
			time = 350;

		} else if ((itemId == 40018 )// 強化綠色藥水(強化自我加速藥水)
				|| (itemId == 41338 )// 受祝福的葡萄酒
				|| (itemId == 41342)) { // 梅杜莎之血
			time = 1800;

		} else if (itemId == 140018) { // 強化綠色藥水(強化自我加速藥水)(祝福)
			time = 2100;

		} else if (itemId == 40039) { // 紅酒
			time = 600;

		} else if (itemId == 40040) { // 威士忌
			time = 900;

		} else if (itemId == 49297) { // 強化自我加速藥水
			time = 1800;
			
		} else if (itemId == 40030) { // 象牙塔加速藥水
			time = 300;
		} else if (itemId == 140526) {
			time = 300;
		}
		if (time  == 300) {
			int totalTime = pc.getSkillEffectTimeSec(L1SkillId.STATUS_HASTE);
			if (totalTime <= 0) {
				totalTime = 0;
			}
			time += totalTime;
			if (time >= 7200) {
				time = 7200;
			}
		}

		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 191));
		
		if (pc.getHasteItemEquipped() > 0) {
			return;
		}
		// 解除醉酒狀態
		pc.setDrink(false);

		// 加速效果 抵銷對應技能
		L1BuffUtil.hasteStart(pc);

		// 解除各種被施加的減速魔法技能
		if (pc.hasSkillEffect(SLOW)) {
			pc.killSkillEffectTimer(SLOW);
			pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));

		} else if (pc.hasSkillEffect(MASS_SLOW)) {
			pc.killSkillEffectTimer(MASS_SLOW);
			pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));

		} else if (pc.hasSkillEffect(ENTANGLE)) {
			pc.killSkillEffectTimer(ENTANGLE);
			pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));

		} else {
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
			pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
			pc.setMoveSpeed(1);
			pc.setSkillEffect(STATUS_HASTE, time * 1000);
		}
	}
}