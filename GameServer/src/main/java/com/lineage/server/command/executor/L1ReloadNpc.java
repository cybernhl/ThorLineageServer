package com.lineage.server.command.executor;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.NPCTalkDataTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * NPC資料重置資料庫
 * 
 * @author dexc
 * 
 */
public class L1ReloadNpc implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1ReloadNpc.class);

	private L1ReloadNpc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ReloadNpc();
	}

	@Override
	public void execute(L1PcInstance paramL1PcInstance, String paramString1,
			String paramString2) {
		NpcTable.get().load();
		NPCTalkDataTable.get().load();
		paramL1PcInstance.sendPackets(new S_SystemMessage("[npc]資料庫已重讀完成!"));

	}
}
