package com.lineage.data.item_etcitem.shop;



import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 体力增强卷轴
 * 
 * @author xiaoxiao
 */
public class ScrollForStrengtheningHP extends ItemExecutor {

    public static ItemExecutor get() {
        return new ScrollForStrengtheningHP();
    }

    private ScrollForStrengtheningHP() {
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

        final short skillId = L1SkillId.EFFECT_STRENGTHENING_HP;  
        
        if(pc.hasSkillEffect(L1SkillId.EFFECT_ENCHANTING_BATTLE)){
			pc.sendPackets(new S_ServerMessage("需等待強化戰鬥卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_ENCHANTING_BATTLE)+"秒才可使用"));
			return;
		}
		if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_HP)){
			pc.sendPackets(new S_ServerMessage("需等待體力增強卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_HP)+"秒才可使用"));
			return;
		}
		if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MP)){
			pc.sendPackets(new S_ServerMessage("需等待魔力增強卷軸"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_MP)+"秒才可使用"));
			return;
		}
        pc.setSkillEffect(skillId, 3600 * 1000);
        pc.addMaxHp(50); // 最高HP+50
        pc.addHpr(4); // 回血量+4
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp())); // 更新角色当前血量与最大血量
        if (pc.isInParty()) { // 组队中
            pc.getParty().updateMiniHP(pc); // 更新组队血条
        }

        pc.getInventory().removeItem(item, 1);	
	
	}
}