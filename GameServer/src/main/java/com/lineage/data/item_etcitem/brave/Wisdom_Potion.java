package com.lineage.data.item_etcitem.brave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_PacketBoxWisdomPotion;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 慎重藥水40016<br>
 * 慎重藥水(祝福)140016
 *
 * @author dexc
 */
public class Wisdom_Potion extends ItemExecutor {

	/**
	 *
	 */
	private Wisdom_Potion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Wisdom_Potion();
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
			if (pc.isWizard()) {
				this.useWisdomPotion(pc, itemId);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // \f1沒有任何事情發生。
			}
			pc.getInventory().removeItem(item, 1);
			
		}
	}

	private void useWisdomPotion(final L1PcInstance pc, final int item_id) {
		// 解除魔法技能絕對屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		int time = 0;
		if (item_id == 40016) { // 慎重藥水
			time = 300;
			
		}
		
		if (item_id == 140016) { // 慎重藥水(祝福)
			time = 360;
		}

		if (!pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
			pc.addSp(2);
			pc.sendPackets(new S_SPMR(pc));
		}

		pc.sendPackets(new S_PacketBoxWisdomPotion((time / 4)));
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 750));

		pc.setSkillEffect(STATUS_WISDOM_POTION, time * 1000);
	}
}