package com.lineage.server.model.skill;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 技能延遲使用
 * @author dexc
 *
 */
public class L1SkillDelay {

	private static final Log _log = LogFactory.getLog(L1SkillDelay.class);

	/**
	 * 技能延遲使用
	 */
	private L1SkillDelay() {
	}

	static class SkillDelayTimer implements Runnable {

		private L1Character _cha;
		private int skill_id;

		public SkillDelayTimer(final L1Character cha, final int skill_id) {
			_cha = cha;
			this.skill_id = skill_id;
		}

		@Override
		public void run() {
			stopDelayTimer();
		}

		public void stopDelayTimer() {
			if (this.skill_id == L1SkillId.NATURES_TOUCH) {

			}
			_cha.setSkillDelay(false);
		}
	}

	/**
	 * 設置技能延遲使用
	 * @param cha
	 * @param time
	 */
	public static void onSkillUse(final L1Character cha, final int time, final int skill_id) {
		try {
			cha.setSkillDelay(true);
			if (skill_id == L1SkillId.NATURES_TOUCH) {

			}
			GeneralThreadPool.get().schedule(new SkillDelayTimer(cha, skill_id), time);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
