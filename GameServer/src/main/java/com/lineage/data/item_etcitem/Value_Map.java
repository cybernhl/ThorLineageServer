package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 完成的藏寶圖40692
 */
public class Value_Map extends ItemExecutor {

	/**
	 *
	 */
	private Value_Map() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Value_Map();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if (pc.getInventory().checkItem(40621)) {
			// 沒有任何事情發生。
			pc.sendPackets(new S_ServerMessage(79));
		} else if (((pc.getX() >= 32856) && (pc.getX() <= 32858))
				&& ((pc.getY() >= 32857) && (pc.getY() <= 32858))
				&& (pc.getMapId() == 443)) { // 海賊島第三層
			L1Teleport.teleport(pc, 32794, 32839, (short) 443, 5, true);

		} else {
			// 沒有任何事情發生。
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

}
