package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.skill.EffectFirewallTimer;

/**
 * SKILL專用時間軸 初始化啟動
 * @author dexc
 *
 */
public class StartTimer_Skill {

	public void start() {

		// 法師技能(火牢)
		final EffectFirewallTimer firewall = new EffectFirewallTimer();
		firewall.start();

	}
}
