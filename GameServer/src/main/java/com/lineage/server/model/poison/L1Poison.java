package com.lineage.server.model.poison;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 特別狀態
 * @author dexc
 *
 */
public abstract class L1Poison {

	/**
	 * 毒性抵抗狀態
	 * @param cha
	 * @return true:中毒 false:不會中毒
	 */
	protected static boolean isValidTarget(final L1Character cha) {
		if (cha == null) {
			return false;
		}
		// 已經中毒
		if (cha.getPoison() != null) {
			return false;
		}

		if (!(cha instanceof L1PcInstance)) {
			return true;
		}

		final L1PcInstance player = (L1PcInstance) cha;
		// 防中毒道具
		if (player.get_venom_resist() > 0) {
			return false;
		}
		// 防中毒技能
		if (player.hasSkillEffect(L1SkillId.VENOM_RESIST)) {// 毒性抵抗
			return false;
		}
		// 防中毒技能
		if (player.hasSkillEffect(L1SkillId.DRAGON5)) {// 生命
			return false;
		}
		return true;
	}

	/**
	 * 傳送訊息
	 * @param cha
	 * @param msgId
	 */
	protected static void sendMessageIfPlayer(final L1Character cha, final int msgId) {
		if (!(cha instanceof L1PcInstance)) {
			return;
		}

		final L1PcInstance pc = (L1PcInstance) cha;
		pc.sendPackets(new S_ServerMessage(msgId));
	}

	/**
	 * 毒ID返。
	 *
	 * @see S_Poison#S_Poison(int, int)
	 *
	 * @return S_Poison使用ID
	 */
	public abstract int getEffectId();

	/**
	 * 毒效果取除。<br>
	 *
	 * @see L1Character#curePoison()
	 */
	public abstract void cure();
}
