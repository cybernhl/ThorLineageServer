package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 物理暴擊藥水10
 * 
 * @author xiaoxiao
 */
public class ScrollForStrengtheningMBJ8 extends ItemExecutor {

    public static ItemExecutor get() {
        return new ScrollForStrengtheningMBJ8();
    }

    private ScrollForStrengtheningMBJ8() {
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

        final short skillId = L1SkillId.EFFECT_STRENGTHENING_MBJ8;        
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ10)){
			pc.sendPackets(new S_ServerMessage("需等待10%魔法暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_WBJ10)+"秒才可使用"));
			return;
		}
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_WBJ6)){
			pc.sendPackets(new S_ServerMessage("需等待6%物理暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_WBJ6)+"秒才可使用"));
			return;
		}
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_WBJ8)){
			pc.sendPackets(new S_ServerMessage("需等待8%物理暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_WBJ8)+"秒才可使用"));
			return;
		}
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_WBJ10)){
			pc.sendPackets(new S_ServerMessage("需等待10%物理暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_WBJ10)+"秒才可使用"));
			return;
		}
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ8)){
			pc.sendPackets(new S_ServerMessage("需等待8%魔法暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_MBJ8)+"秒才可使用"));
			return;
		}
        if(pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ12)){
			pc.sendPackets(new S_ServerMessage("需等待12%魔法暴擊效果"+pc.getSkillEffectTimeSec(L1SkillId.EFFECT_STRENGTHENING_MBJ12)+"秒才可使用"));
			return;
		}
        pc.setSkillEffect(skillId, 600 * 1000);        
        pc.getInventory().removeItem(item, 1);
       }
  

    
}