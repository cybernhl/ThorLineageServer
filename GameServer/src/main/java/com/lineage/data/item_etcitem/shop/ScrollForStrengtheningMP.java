package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 魔力增强卷轴
 * 
 * @author xiaoxiao
 */
public class ScrollForStrengtheningMP extends ItemExecutor {

    public static ItemExecutor get() {
        return new ScrollForStrengtheningMP();
    }

    private ScrollForStrengtheningMP() {
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

        final short skillId = L1SkillId.EFFECT_STRENGTHENING_MP;        
        if(pc.hasSkillEffect(L1SkillId.EFFECT_ENCHANTING_BATTLE)){
			pc.sendPackets(new S_ServerMessage("需等待強化戰鬥卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_ENCHANTING_BATTLE)+"秒才可使用"));
			return;
		}
		if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_HP))		{
			pc.sendPackets(new S_ServerMessage("需等待體力增強卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_HP)+"秒才可使用"));
			return;
		}
		if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MP))		{
			pc.sendPackets(new S_ServerMessage("需等待魔力增強卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_MP)+"秒才可使用"));
			return;
		}
        pc.setSkillEffect(skillId, 3600 * 1000);
        pc.addMaxMp(40); // 最高MP+40
        pc.addMpr(4); // 回魔量+4
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 更新最高魔量与当前魔量

        pc.getInventory().removeItem(item, 1);
    }
	
}
