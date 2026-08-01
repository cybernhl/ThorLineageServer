package com.lineage.server.model.Instance;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.L1WarSpawn;
import com.lineage.server.serverpackets.S_CastleMaster;
import com.lineage.server.serverpackets.S_PacketBoxWar;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 對像:王冠 控制項
 * @author dexc
 *
 */
public class L1CrownInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1CrownInstance.class);
	private static final Lock lock = new ReentrantLock(false);

	/**
	 * 對像:王冠
	 * @param template
	 */
	public L1CrownInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance player) {
		lock.lock();
		try {
			
			boolean in_war = false;
			// 例外狀況:不具備血盟
			if (player.getClanid() == 0) {
				return;
			}
			
			// 人物所屬血盟名稱
			final String playerClanName = player.getClanname();
			// 該血盟數據
			final L1Clan clan = WorldClan.get().getClan(playerClanName);
			
			// 例外狀況:不具備血盟
			if (clan == null) {
				return;
			}
			// 例外狀況:人物死亡
			if (player.isDead()) {
				return;
			}
			// 例外狀況:人物死亡
			if (player.getCurrentHp() <= 0) {
				return;
			}
			// 鬼魂模式
			if (player.isGhost()) {
				return;
			}
			// 傳送中
			if (player.isTeleport()) {
				return;
			}
			// 例外狀況:不是王族
			if (!player.isCrown()) {
				return;
			}
			// 例外狀況:有變身(人物外型不是公主或王子)
			if ((player.getTempCharGfx() != 0) && 
					(player.getTempCharGfx() != 1)) {
				return;
			}
			// 例外狀況:非盟主
			if (player.getId() != clan.getLeaderId()) {
				return;
			}
			// 例外狀況:距離過遠
			if (!this.checkRange(player)) {
				return;
			}
			// 例外狀況:其他城堡城主
			if (clan.getCastleId() != 0) {
				// 474:你已經擁有城堡，無法再擁有其他城堡。
				player.sendPackets(new S_ServerMessage(474));
				return;
			}

			// 王冠座標取回城堡編號
			final int castle_id = L1CastleLocation.getCastleId(this.getX(), this.getY(), this.getMapId());

			// 佈告。但、城主居場合佈告不要
			boolean existDefenseClan = false;
			
			final L1Clan defence_clan = L1CastleLocation.mapCastle().get(new Integer(castle_id));
			
			if (defence_clan != null) {
				existDefenseClan = true;
			}

			// 取回全部戰爭訊息
			final List<L1War> wars = WorldWar.get().getWarList();
			for (final L1War war : wars) {
				// 為戰爭中城堡
				if (castle_id == war.getCastleId()) {
					in_war = war.checkClanInWar(playerClanName);
					break;
				}
			}
			
			// 城主居、佈告場合
			if (existDefenseClan && (in_war == false)) {
				return;
			}

			// 表示
			for (final L1War war : wars) {
				if (war.checkClanInWar(playerClanName) && existDefenseClan) {
					// 自參加中、城主交代
					war.winCastleWar(playerClanName);
					break;
				}
			}

			// 原城盟不為空 消除相關城堡數據
			if (existDefenseClan && (defence_clan != null)) { // 原城主不為空
				defence_clan.setCastleId(0);
				ClanReading.get().updateClan(defence_clan);
				
				// 原城主王冠消除
				if (L1CastleLocation.mapCastle().get(new Integer(castle_id)) != null) {
					L1CastleLocation.removeCastle(new Integer(castle_id));
				}
				
				World.get().broadcastPacketToAll(new S_CastleMaster(0, defence_clan.getLeaderId()));
			}
			
			clan.setCastleId(castle_id);
			ClanReading.get().updateClan(clan);
			
			// 新城主王冠顯示
			if (L1CastleLocation.mapCastle().get(new Integer(castle_id)) == null) {
				L1CastleLocation.putCastle(castle_id, clan);
			}
			if (castle_id == 2) {// 妖魔城主 暫時使用8顯示王冠
				World.get().broadcastPacketToAll(new S_CastleMaster(8, player.getId()));
				
			} else {
				World.get().broadcastPacketToAll(new S_CastleMaster(castle_id, player.getId()));
			}

			// 血盟以外成員強制移出
			int[] loc = new int[3];
			for (final L1PcInstance pc : World.get().getAllPlayers()) {
				if ((pc.getClanid() != player.getClanid()) && !pc.isGm()) {
					if (L1CastleLocation.checkInWarArea(castle_id, pc)) {
						// 戰爭旗幟範圍內
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						final int locx = loc[0];
						final int locy = loc[1];
						final short mapid = (short) loc[2];
						L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
					}
				}
			}
			
			final L1PcInstance[] clanMember = clan.getOnlineClanMember();

			if (clanMember.length > 0) {
				for (final L1PcInstance pc : clanMember) {
					if (pc.getId() == clan.getLeaderId()) {
						// (642) 已掌握了城堡的主導權。
						pc.sendPackets(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_INITIATIVE));
					}
					// (643) 已佔領城堡。
					pc.sendPackets(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_OCCUPY));
				}
			}

			// 地面王冠物件刪除
			this.deleteMe();

			// 刪除損壞的守護塔
			for (final L1Object l1object : World.get().getObject()) {
				if (l1object instanceof L1TowerInstance) {
					final L1TowerInstance tower = (L1TowerInstance) l1object;
					if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
						tower.deleteMe();
					}
				}
			}
			
			final L1WarSpawn warspawn = new L1WarSpawn();
			warspawn.spawnTower(castle_id);

			// 城門元戾
			for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
				if (L1CastleLocation.checkInWarArea(castle_id, door)) {
					door.repairGate();
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void deleteMe() {
		this._destroyed = true;
		if (this.getInventory() != null) {
			this.getInventory().clearItems();
		}
		this.allTargetClear();
		this._master = null;
		World.get().removeVisibleObject(this);
		World.get().removeObject(this);
		
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		
		this.removeAllKnownObjects();
	}

	private boolean checkRange(final L1PcInstance pc) {
		return ((this.getX() - 1 <= pc.getX()) && (pc.getX() <= this.getX() + 1)
				&& (this.getY() - 1 <= pc.getY()) && (pc.getY() <= this.getY() + 1));
	}
}
