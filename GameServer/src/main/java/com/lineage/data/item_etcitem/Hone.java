package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/***
 * 磨刀石40317
 */
public class Hone extends ItemExecutor {

	/**
	 *
	 */
	private Hone() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Hone();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int itemobj = data[0];
		final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
		if (item1 == null) {
			return;
		}
		// 對像為武器或者防具
		if ((item1.getItem().getType2() != 0) && (item1.get_durability() > 0)) {
			String msg0;
			pc.getInventory().recoveryDamage(item1);
			msg0 = item1.getLogName();
			if (item1.get_durability() == 0) {
				pc.sendPackets(new S_ServerMessage(464, msg0)); // %0
				// 現在變成像個新的一樣。
			} else {
				pc.sendPackets(new S_ServerMessage(463, msg0)); // %0 變好多了。
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
		}
		pc.getInventory().removeItem(item, 1);
	}

}
