package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

/**
 * <font color=#00800>穿雲箭(血盟)</font><br>
 *
 * @since 類：Scroll_Clan_Teleport2
 * @since 修改時間：2021-07-20 上午5:30:33
 * @author 阿國
 *
 */
public class Party_Teleprot extends ItemExecutor {

	private Party_Teleprot() {
		// TODO 自動產生的建構子 Stub
	}

	public static ItemExecutor get() {
		return new Party_Teleprot();
	}

	/**
	 * 道具物件執行
	 *
	 * @param data 參數
	 * @param pc   執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final L1Party party = pc.getParty();
		if (party != null) {
			for (L1PcInstance listner : party.partyUsers().values()) {
			// 商店村模式
				if (!listner.isPrivateShop() && listner != pc) {
					if (/*!listner.isFreeze() && !listner.isStun() && */!listner.isSleeped() && !listner.isDead() && !listner.isParalyzed()) {
//						if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
//							if (ServerWarExecutor.get().isWarIng()) {
//								pc.sendPackets(new S_SystemMessage("攻城戰期間內，無法使用穿雲箭。"));
//								return;
//							}
//						}
						// 解除魔法技能絕對屏障
						L1BuffUtil.cancelAbsoluteBarrier(listner);
						final L1Location loc = pc.getLocation().randomLocation(3, true);
						listner.setTeleportX(loc.getX());
						listner.setTeleportY(loc.getY());
						listner.setTeleportMapId((short) loc.getMapId());
						// L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), pc.getHeading(), true, L1Teleport.CHANGE_POSITION);
						// 你的血盟成員想要傳送你。你答應嗎？(Y/N)！
						// tgpc.sendPackets(new S_Message_YN(748));
						listner.sendPackets(new S_Message_YN(620, "隊伍成員" + pc.getName() + "使用了穿雲箭(隊伍)需要您支援。(Y/N)！"));
					}
				}
			}
			final String mapName = MapsTable.get().getMapName(pc.getMapId());
			World.get().broadcastPacketToAll(new S_ServerMessage("玩家：【" + pc.getName() + "】位置在【" + mapName + "】使用了穿雲箭(隊伍)千軍萬馬來相見。"));
			World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f2玩家：【" + pc.getName() + "】位置在【" + mapName + "】使用了穿雲箭(隊伍)千軍萬馬來相見。"));
			// 刪除道具
			pc.getInventory().removeItem(item, 1);
		} else {
			pc.sendPackets(new S_ServerMessage("您沒有血盟"));
		}
	}
}
