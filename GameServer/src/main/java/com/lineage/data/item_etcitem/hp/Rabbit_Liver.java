package com.lineage.data.item_etcitem.hp;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBoxHpMsg;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 兔子的肝 40043
 */
public class Rabbit_Liver extends ItemExecutor {

	/**
	 *
	 */
	private Rabbit_Liver() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Rabbit_Liver();
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
			UseHeallingPotion(pc, 600, 189);
			pc.getInventory().removeItem(item, 1);
		}
	}

	private static void UseHeallingPotion(final L1PcInstance pc, int healHp, final int gfxid) {
		// 解除魔法技能絕對屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		final Random random = new Random();

		pc.sendPacketsX8(new S_SkillSound(pc.getId(), gfxid));
		
		healHp *= (random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.get_up_hp_potion() > 0) {
			healHp += (healHp * pc.get_up_hp_potion()) / 100;
		}
		if (pc.hasSkillEffect(POLLUTE_WATER)) {
			healHp = (healHp >> 1);
		}
		if (pc.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
			healHp = (healHp >> 1);
		}
		if (pc.hasSkillEffect(ADLV80_2_1)) {
			healHp *= -1;
		}
		if (healHp > 0) {
			// 你覺得舒服多了訊息
			pc.sendPackets(new S_PacketBoxHpMsg());
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}
}
