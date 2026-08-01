package com.lineage.server.command.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 召喚虛擬人物(參數:數量 / 範圍)
 * @author dexc
 *
 */
public class L1SpawnDe implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1SpawnDe.class);

	private L1SpawnDe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnDe();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {

	}
}
