package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemUseTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class ItemUse extends ItemExecutor {
	
	public static ItemExecutor get() {
		return new ItemUse();
	}

	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		
		if (ItemUseTable.getSetList() != null) {
			ItemUseTable.forItemUSe(pc, item);
		}
	}
}