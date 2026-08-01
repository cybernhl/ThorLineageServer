package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemPowerUpdateTable2;
import com.lineage.server.datatables.ItemPowerUpdateTable3;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 啟用/取消GM隱身
 * @author dexc
 *
 */
public class L1ReloadPower implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ReloadPower.class);

	private L1ReloadPower() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ReloadPower();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			ItemPowerUpdateTable.get().load();
			ItemPowerUpdateTable2.get().load();
			ItemPowerUpdateTable3.get().load();

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
