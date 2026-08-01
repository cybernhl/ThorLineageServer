package com.lineage.server.command.executor;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 顯示人物附加屬性(參數:人物名稱)
 * XXX 待加入顯示背包
 *
 * @author dexc
 *
 */
public class L1Describe implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1Describe.class);

	private L1Describe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Describe();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			if (pc == null) {
				_log.warn("系統命令執行: " + cmdName + " " + arg + " 顯示人物附加屬性。");
			}
			
			final ArrayList<String> msg = new ArrayList<String>();
			
			L1PcInstance target = World.get().getPlayer(arg);
			
			if (pc == null) {
				if (target == null) {
					_log.error("指令異常: 指定人物不在線上，這個命令必須輸入正確人物名稱才能執行。");
					return;
				}
				
			} else {
				if (target == null) {
					target = pc;
				}
			}

			msg.add("-- 顯示資訊人物: " + target.getName() + " --");
			msg.add("傷害附加: +" + target.getDmgup());
			msg.add("命中附加: +" + target.getHitup());
			msg.add("抗魔: " + target.getMr() + "%");
			
			final int hpr = target.getHpr() + target.getInventory().hpRegenPerTick();
			final int mpr = target.getMpr() + target.getInventory().mpRegenPerTick();
			msg.add("HP額外回復量: " + hpr);
			msg.add("MP額外回復量: " + mpr);
			msg.add("有好度: " + target.getKarma());
			msg.add("背包物品數量: " + target.getInventory().getSize());
			
			if (pc == null) {
				String items = "";
				for (final L1ItemInstance item : target.getInventory().getItems()) {
					items += "[" + item.getNumberedName(item.getCount(), false) + "]";
				}
				msg.add(items);
			}
			
			for (final String info : msg) {
				if (pc == null) {
					_log.info(info);
					
				} else {
					pc.sendPackets(new S_ServerMessage(166, info));
				}
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
