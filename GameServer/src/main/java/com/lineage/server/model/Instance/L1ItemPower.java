package com.lineage.server.model.Instance;

import java.util.HashMap;
import java.util.Map;

/**
 * 物品能力值
 * @author dexc
 *
 */
public class L1ItemPower {

	private final L1ItemInstance _itemInstance;

	// 抗魔 = 追加質
	public static final Map<Integer, Integer> MR2 = new HashMap<Integer, Integer>();

	/**
	 * 載入強化質影響抗魔的裝備
	 */
	public static void load() {
		// MR * 1
		MR2.put(20011, new Integer(1));// 抗魔法頭盔
		MR2.put(320011, new Integer(1));// 抗魔法頭盔
		MR2.put(420011, new Integer(1));// 抗魔法頭盔
		MR2.put(520011, new Integer(1));// 抗魔法頭盔
		MR2.put(20110, new Integer(1));// 抗魔法鏈甲
		MR2.put(320110, new Integer(1));// 抗魔法鏈甲
		MR2.put(420110, new Integer(1));// 抗魔法鏈甲
		MR2.put(520110, new Integer(1));// 抗魔法鏈甲
		MR2.put(21108, new Integer(1));// 魔法抵抗內衣

		// MR * 2
		MR2.put(20056, new Integer(2));// 抗魔法斗篷
		MR2.put(320056, new Integer(2));// 抗魔法斗篷
		MR2.put(420056, new Integer(2));// 抗魔法斗篷
		MR2.put(520056, new Integer(2));// 抗魔法斗篷

		MR2.put(70092, new Integer(3));// 馬昆斯斗篷
		MR2.put(70034, new Integer(1));// 塔拉斯長靴
		
		// 林德拜爾
		MR2.put(30328, new Integer(1));// 林德拜爾的力量
		MR2.put(30329, new Integer(1));// 林德拜爾的魅惑
		MR2.put(30330, new Integer(1));// 林德拜爾的泉源
		MR2.put(30331, new Integer(1));// 林德拜爾的霸氣

		MR2.put(320236, new Integer(1));// 精靈盾牌
		MR2.put(420236, new Integer(1));// 精靈盾牌
		MR2.put(120236, new Integer(1));// 精靈盾牌
		MR2.put(20236, new Integer(1));// 精靈盾牌
	}
	
	protected L1ItemPower(final L1ItemInstance itemInstance) {
		this._itemInstance = itemInstance;
	}

	/**
	 * 抗魔裝備設置
	 * @param armor
	 * @return
	 */
	protected int getMr() {
		int mr = _itemInstance.getItem().get_mdef();
		final Integer integer = MR2.get(_itemInstance.getItemId());
		if (integer != null) {
			mr += (_itemInstance.getEnchantLevel() * integer);
		}
		return mr;
	}
}
