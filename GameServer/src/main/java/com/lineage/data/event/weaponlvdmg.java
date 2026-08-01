package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

public class weaponlvdmg extends EventExecutor {
	private static final Log _log = LogFactory.getLog(weaponlvdmg.class);

	public static boolean START = false;

	public static EventExecutor get() {
		return new weaponlvdmg();
	}

	public void execute(L1Event event) {
		try {
			START = true;

			// EnchantOrginal.getInstance(); // 装备强化能力系统
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}