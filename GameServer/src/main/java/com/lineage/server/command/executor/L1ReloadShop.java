package com.lineage.server.command.executor;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.ShopCnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.DropTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 商店資料重置資料庫
 * 
 * @author dexc
 * 
 */
public class L1ReloadShop implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ReloadShop.class);

	private L1ReloadShop() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ReloadShop();
	}

	@Override
	public void execute(L1PcInstance paramL1PcInstance, String paramString1,
			String paramString2) {
		ShopTable.get().restshop();
		ShopCnTable.get().restshopCn();
		paramL1PcInstance.sendPackets(new S_SystemMessage(
				"[shop]+[shop_cn]資料庫已重讀完成!"));

	}
}
