package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 實行處理
 *
 * 處理、以外<br>
 * public static L1CommandExecutor getInstance()<br>
 * 實裝。
 * 通常、自化返、必要應返、他化返。
 */
public interface L1CommandExecutor {

	/**
	 * 實行。
	 *
	 * @param pc
	 *            實行者
	 * @param cmdName
	 *            實行名
	 * @param arg
	 *            引數
	 */
	public void execute(L1PcInstance pc, String cmdName, String arg);
}
