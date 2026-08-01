package com.lineage.data.item_etcitem.brave;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 強化勇氣的藥水41415<br>
 */
public class Potion_2 extends ItemExecutor {

	/**
	 *
	 */
	private Potion_2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Potion_2();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 1,447：目前暫不開放。  
		pc.sendPackets(new S_ServerMessage(1447));
	}
}