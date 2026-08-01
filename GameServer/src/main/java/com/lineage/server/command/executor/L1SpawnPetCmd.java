package com.lineage.server.command.executor;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * 召喚NPC(參數:NPCID - 數量 - 範圍)
 * @author dexc
 *
 */
public class L1SpawnPetCmd implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1SpawnPetCmd.class);

	private L1SpawnPetCmd() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnPetCmd();
	}

	private void sendErrorMessage(final L1PcInstance pc, final String cmdName) {
		final String errorMsg = cmdName + " npcid [等級]";
		pc.sendPackets(new S_SystemMessage(errorMsg));
	}

	/**
	 * 取回NPCID
	 * @param nameId
	 * @return
	 */
	private int parseNpcId(final String nameId) {
		int npcid = 0;
		try {
			// 依照ID取回
			npcid = Integer.parseInt(nameId);
			
		} catch (final NumberFormatException e) {
			// 依照名稱取回
			npcid = NpcTable.get().findNpcIdByNameWithoutSpace(nameId);
		}
		return npcid;
	}
	private boolean isTamePet(final L1NpcInstance npc) {
		return true;
	}
	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer tok = new StringTokenizer(arg);
			final String nameId = tok.nextToken();
			final int level = Integer.parseInt(tok.nextToken());

			// 取回NPCID
			final int _npcId = this.parseNpcId(nameId);

			try {
				final L1NpcInstance npc = NpcTable.get().newNpcInstance(_npcId);

				if (npc == null) {
					return;
				}
				if (!isTamePet(npc)) {
					pc.sendPackets(new S_ServerMessage(324));
					return;
				}

				npc.setId(IdFactoryNpc.get().nextId());
				npc.setMap(pc.getLocation().getMap());

				npc.getLocation().set(pc.getLocation());

				npc.setHomeX(npc.getX());
				npc.setHomeY(npc.getY());
				npc.setHeading(5);

				// 設置副本編號 TODO
				npc.set_showId(pc.get_showId());

				L1QuestUser q = WorldQuest.get().get(pc.get_showId());
				if (q != null) {
					q.addNpc(npc);
				}

				World.get().storeObject(npc);
				World.get().addVisibleObject(npc);

				npc.turnOnOffLight();

				// 設置NPC現身
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);

				final L1ItemInstance petamu = pc.getInventory().storeItem(40314, 1); // 項圈
				if (petamu != null) {
					final L1PetInstance pet = new L1PetInstance(npc, pc, petamu.getId());
					final int levelBefore = pet.getLevel();
					long totalExp = ExpTable.getExpByLevel(level);
					pet.setExp(totalExp);
					pet.setLevel(ExpTable.getLevelByExp(totalExp));
					final int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);
					final int gap = pet.getLevel() - levelBefore;
					for (int i = 1; i <= gap; i++) {
						final RangeInt hpUpRange = pet.getPetType().getHpUpRange();
						final RangeInt mpUpRange = pet.getPetType().getMpUpRange();
						pet.addMaxHp(hpUpRange.randomValue());
						pet.addMaxMp(mpUpRange.randomValue());
					}
					pet.setExpPercent(expPercentage);
					pc.sendPackets(new S_NPCPack_Pet(pet, pc));
					pc.sendPackets(new S_ItemName(petamu));
				}

			} catch (final Exception e) {
				_log.error("執行NPC召喚發生異常: " + _npcId, e);
			}

		} catch (final NoSuchElementException e) {
			this.sendErrorMessage(pc, cmdName);

		} catch (final NumberFormatException e) {
			this.sendErrorMessage(pc, cmdName);

		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
