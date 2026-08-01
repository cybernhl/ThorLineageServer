package com.lineage.server.command.executor;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 取回指定帳號資料(參數:帳號)
 * @author dexc
 *
 */
public class L1ClearIP implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ClearIP.class);

	private L1ClearIP() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ClearIP();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			LanSecurityManager.BANIPMAP.clear();
			pc.sendPackets(new S_ServerMessage("IP CHECK 清理完畢"));
		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
