package com.lineage.data.item_etcitem.test;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Letter;

/**
 * 測試
 * 類名稱：Test<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午10:33:24<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class Test extends ItemExecutor {

	/**
	 *
	 */
	private Test() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Test();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		pc.sendPackets(new S_Letter());
	}
}
