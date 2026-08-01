package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 洗血藥水
 *
 * @author dexc
 *
 */
public class Level_Down extends ItemExecutor {

	/**
	 *
	 */
	private Level_Down() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Level_Down();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if (pc.getLevel() > 9) {
			pc.setExp(6581);// 玩家等級直接變成9級
			pc.sendPackets(new S_ServerMessage(822)); // 你感受到體內深處產生一股不明力量。
			// 刪除道具
			pc.getInventory().removeItem(item, 1);

		} else {
			pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
		}
	}

}
