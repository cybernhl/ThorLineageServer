package com.lineage.server.model.skill;

/**
 * 技能效果時間軸
 * @author daien
 *
 */
public interface L1SkillTimer {
	
	/**
	 * 剩餘時間(秒)
	 * @return
	 */
	public int getRemainingTime();

	/**
	 * 效果開始執行
	 */
	public void begin();

	/**
	 * 效果結束
	 */
	public void end();

	/**
	 * 效果時間軸終止
	 */
	public void kill();
}