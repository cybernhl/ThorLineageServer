package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 減傷的芒果刨冰 (10分鐘、可交易)
 * 
 * @author xiaoxiao
 */
public class ScrollForEnchantingJSBB extends ItemExecutor {
	public static ItemExecutor get() {
		return new ScrollForEnchantingJSBB();
	}
	private ScrollForEnchantingJSBB() {
	}

	/**
	 * 道具执行
	 * 
	 * @param data
	 *            参数
	 * @param pc
	 *            对象
	 * @param item
	 *            道具
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		final short skillId = L1SkillId.EFFECT_STRENGTHENING_jsdbb;
		if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_jsdbb)){
			pc.sendPackets(new S_ServerMessage("需等待減傷的芒果刨冰"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_jsdbb)+"秒才可使用"));
			return;
		}
		pc.setSkillEffect(skillId, 600 * 1000);
		pc.addAc(-4);
		pc.sendPackets(new S_OwnCharStatus(pc));
		pc.getInventory().removeItem(item, 1);
	
	}
}
