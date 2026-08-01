package com.lineage.server.model.Instance;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.WorldClan;

/**
 * 對像:盟屋管家 控制項
 * @author daien
 *
 */
public class L1HousekeeperInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1HousekeeperInstance.class);

	/**
	 * 對像:盟屋管家
	 * @param template
	 */
	public L1HousekeeperInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			attack.calcHit();
			attack.action();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance pc) {
		final int objid = this.getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(
				this.getNpcTemplate().get_npcId());
		final int npcid = this.getNpcTemplate().get_npcId();
		String htmlid = null;
		String[] htmldata = null;
		boolean isOwner = false;

		if (talking != null) {
			// 話PC所有者員調
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());

			if (clan != null) {
				final int houseId = clan.getHouseId();
				if (houseId != 0) {
					final L1House house = HouseReading.get().getHouseTable(houseId);
					if (npcid == house.getKeeperId()) {
						isOwner = true;
					}
				}
			}

			// 所有者員以外會話內容變
			if (!isOwner) {
				// Housekeeper屬取得
				L1House targetHouse = null;
				final Collection<L1House> houseList = HouseReading.get().getHouseTableList().values();
				for (final L1House house : houseList) {
					if (npcid == house.getKeeperId()) {
						targetHouse = house;
						break;
					}
				}

				// 所有者居調
				boolean isOccupy = false;
				String clanName = null;
				String leaderName = null;
				final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
				for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
					final L1Clan targetClan = iter.next();
					if (targetHouse.getHouseId() == targetClan.getHouseId()) {
						isOccupy = true;
						clanName = targetClan.getClanName();
						leaderName = targetClan.getLeaderName();
						break;
					}
				}

				// 會話內容設定
				if (isOccupy) { // 所有者
					htmlid = "agname";
					htmldata = new String[] { clanName, leaderName,
							targetHouse.getHouseName() };
				} else { // 所有者(競賣中)
					htmlid = "agnoname";
					htmldata = new String[] { targetHouse.getHouseName() };
				}
			}

			// html表示送信
			if (htmlid != null) { // htmlid指定場合
				if (htmldata != null) { // html指定場合表示
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (pc.getLawful() < -1000) { // 
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance pc, final String action) {
	}

	public void doFinalAction(final L1PcInstance pc) {
	}

}
