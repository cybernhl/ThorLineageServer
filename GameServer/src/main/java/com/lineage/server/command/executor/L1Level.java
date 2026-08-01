package com.lineage.server.command.executor;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.RangeInt;

/**
 * 變更該GM人物等級
 * @author dexc
 *
 */
public class L1Level implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Level.class);

	private L1Level() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Level();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final int level = Integer.parseInt(tok.nextToken());
			if (level == pc.getLevel()) {
				return;
			}
			if (!RangeInt.includes(level, 1, ExpTable.MAX_LEVEL)) {
				pc.sendPackets(new S_SystemMessage("範圍限制 1~" + ExpTable.MAX_LEVEL));
				return;
			}
			pc.setExp(ExpTable.getExpByLevel(level));

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
