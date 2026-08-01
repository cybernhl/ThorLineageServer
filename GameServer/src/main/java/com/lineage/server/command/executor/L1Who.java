package com.lineage.server.command.executor;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.*;

/**
 * 顯示線上實際人數
 * @author dexc
 *
 */
public class L1Who implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Who.class);

	private L1Who() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Who();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final Collection<L1PcInstance> players = World.get().getAllPlayers();

			final String amount = String.valueOf(players.size());
			
			int a = WorldCrown.get().map().size();// 1127：[王族]
			int b = WorldKnight.get().map().size();// 1128：[騎士]
			int c = WorldElf.get().map().size();// 1129：[妖精]
			int d = WorldWizard.get().map().size();// 1130：[法師]
		//	int e = WorldDarkelf.get().map().size();// 2503：[黑暗妖精]  
			
			if (pc == null) {
				_log.warn("系統命令執行: who");
				_log.info("[王族]:" + a);
				_log.info("[騎士]:" + b);
				_log.info("[妖精]:" + c);
				_log.info("[法師]:" + d);
				//_log.info("[黑妖]:" + e);
				_log.info("Server Ver: " + Config.VER);
				
			} else {
				pc.sendPackets(new S_ServerMessage("目前線上有: " + amount));
				pc.sendPackets(new S_ServerMessage("王族:" + a));
				pc.sendPackets(new S_ServerMessage("騎士:" + b));
				pc.sendPackets(new S_ServerMessage("妖精:" + c));
				pc.sendPackets(new S_ServerMessage("法師:" + d));
			//	pc.sendPackets(new S_ServerMessage("黑妖:" + e));
				
				pc.sendPackets(new S_ServerMessage(166, "Server Ver: " + Config.VER)); 
			} 

		} catch (final Exception e) {
			if (pc == null) {
				_log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
				
			} else {
				_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
				// 261 \f1指令錯誤。
				pc.sendPackets(new S_ServerMessage(261));
			}
		}
	}
}
