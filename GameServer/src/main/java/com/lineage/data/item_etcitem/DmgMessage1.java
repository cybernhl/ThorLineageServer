package com.lineage.data.item_etcitem;


import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 經驗加倍藥水
 * @author admin
 *
 */
public class DmgMessage1 extends ItemExecutor {
	
	private DmgMessage1() {
		
	}

	public static ItemExecutor get() {
		return new DmgMessage1();
	}
	
	/**
	 * 開始執行
	 */
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		if (pc.getDmgMessage1() == 1){
			pc.setDmgMessage1(0);//關閉
			pc.sendPackets(new S_SystemMessage("掛機自動技能：關閉"));
		}else{
			pc.setDmgMessage1(1);
			pc.sendPackets(new S_SystemMessage("掛機自動技能：開啟"));
		}	
	}
}
