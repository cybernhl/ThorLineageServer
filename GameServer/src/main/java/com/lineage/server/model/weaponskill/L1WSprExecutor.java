package com.lineage.server.model.weaponskill;

import com.lineage.server.model.L1Character;

/**
 * 武器各項動畫設置抽像接口
 * @author daien
 *
 */
public abstract class L1WSprExecutor {

	/**
	 * 設置武器技能設定值
	 * @param spr 動畫編號
	 * @param istg 動畫是否施展在目標 true:動畫在目標身上 false:動畫在施展者身上
	 * @param direction 動畫方向(設制動畫方向istg設置將無作用)
	 * @param range 發動距離(具有動畫方向本設置才有作用)
	 * @param sleep 延遲時間 0:不延遲
	 */
	public abstract void set_power(int spr, int direction, int range, int sleep);

	/**
	 * 武器技能發動動畫(1)
	 * @param cha
	 * @return
	 */
	public abstract void start_power_skill_1(L1Character cha);
}