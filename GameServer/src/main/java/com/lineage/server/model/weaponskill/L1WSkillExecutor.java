package com.lineage.server.model.weaponskill;

import com.lineage.server.model.L1Character;

/**
 * 武器各項能力設置抽像接口
 * @author daien
 *
 */
public abstract class L1WSkillExecutor {

	/**
	 * 設置武器技能設定值
	 * @param int1 應用設定1
	 * @param int2 應用設定2
	 * @param range 範圍輸出距離 設定質0無範圍輸出
	 * @param range_mode 0:不排除物件 1:排除盟友隊友 2:排除人物 3:排除NPC(range設置大於0本項才有作用)
	 */
	public abstract void set_power(int int1, int int2, int range, int range_mode);

	/**
	 * 武器技能發動(1)
	 * @param cha
	 * @return
	 */
	public abstract void start_power_skill_1(L1Character cha);
}