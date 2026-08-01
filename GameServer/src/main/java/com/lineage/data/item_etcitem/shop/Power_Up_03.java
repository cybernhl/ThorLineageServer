package com.lineage.data.item_etcitem.shop;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemPowerUpdateTable3;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemPowerUpdate;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Random;

/**
 * 自訂物品進化卷軸[需實裝至DB內]
 * 
# 範例:
UPDATE `etcitem` SET `classname`='shop.Power_Up_01',`use_type`='choice' WHERE `item_id`='60175';#定置物品進化卷軸

 */
public class Power_Up_03 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Power_Up_03.class);

	private static  final Random _random = new Random();

	/**
	 *
	 */
	private Power_Up_03() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_Up_03();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		try {
			// 對像OBJID
			final int targObjId = data[0];

			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

			if (tgItem == null) {
				return;
			}
			
			// 取回物件屬性
			final L1ItemPowerUpdate info =
					ItemPowerUpdateTable3.get().get(tgItem.getItemId());
			if (info == null) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			
			if (info.get_mode() == 4) {// 不能再強化
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			if (info.get_nedid() != item.getItemId()) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			if (tgItem.get_card_use() == 1) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage("裝備狀態無法升級"));
				return;
			}
			if (tgItem.isEquipped()) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage("裝備狀態無法升級"));
				return;
			}
			// 同組物品清單
			final Map<Integer, L1ItemPowerUpdate> tmplist =
					ItemPowerUpdateTable3.get().get_type_id(tgItem.getItemId());
			if (tmplist.isEmpty()) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			
			final int order_id = info.get_order_id();// 排序
			final L1ItemPowerUpdate tginfo = tmplist.get(order_id + 1);// 取回下一個排序資料
			if (tginfo == null) {
				// 79：\f1沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			
			// 刪除卷軸
			pc.getInventory().removeItem(item, 1);
			
 			if (_random.nextInt(1000) <= tginfo.get_random()) {
				// 強化成功
				pc.getInventory().removeItem(tgItem, 1);
				
				//CreateNewItem.createNewItem(pc, tginfo.get_itemid(), 1);

				// 產生新物件
				final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
				if (tginfo_item != null) {
					tginfo_item.setCount(1);
					tginfo_item.setEnchantLevel(tgItem.getEnchantLevel());
					tginfo_item.setAttrEnchantKind(tgItem.getAttrEnchantKind());
					tginfo_item.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
					tginfo_item.setGemHole(tgItem.getGemHole());
					tginfo_item.setGemHoleIndex(tgItem.getGemHoleIndex());
					tginfo_item.setSpecialStat(tgItem.getSpecialStat());
					if (tgItem.getCanAbilityType() > 0) {
						tginfo_item.setCanAbilityType(tgItem.getCanAbilityType());
						tginfo_item.setAbilityPos1(tgItem.getAbilityPos1ID());
						tginfo_item.setAbilityPos2(tgItem.getAbilityPos2ID());
						tginfo_item.setAbilityPos3(tgItem.getAbilityPos3ID());
					}
					pc.getInventory().storeItem(tginfo_item);
					pc.sendPackets(new S_ServerMessage("\\fT" + tgItem.getName() + " 祈福後獲得全新的 "+tginfo_item.getName()));
					World.get().broadcastPacketToAll(new S_SystemMessage("\\fT 玩家【" + pc.getName() + "】的 " + tgItem.getName() + " 祈福後獲得全新的 "+tginfo_item.getName()));
					World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f2 玩家【" + pc.getName() + "】的 " + tgItem.getName() + " 祈福後獲得全新的 "+tginfo_item.getName()));
					return;
					
				} else {
					_log.error("給予物件失敗 原因: 指定編號物品不存在(" + tginfo.get_itemid() + ")");
					return;
				}
			} else {
				// 強化失敗
				switch (info.get_mode()) {// 目前物品失敗時處理類型
				case 0:
					// \f1%0%s %2 產生激烈的 %1 光芒，但是沒有任何事情發生。
					pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
					break;
					
				case 1:// 1:強化失敗會倒退
					final L1ItemPowerUpdate ole1 = tmplist.get(order_id - 1);// 取回上一個排序資料
					pc.sendPackets(new S_ServerMessage("\\fR"+tgItem.getName()+"升級失敗!"));
					pc.getInventory().removeItem(tgItem, 1);
					CreateNewItem.createNewItem(pc, ole1.get_itemid(), 1);
					break;
					
				case 2:// 2:強化失敗會消失
					// 164 \f1%0%s 產生激烈的 %1 光芒，一會兒後就消失了。銀色的
					pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
					pc.getInventory().removeItem(tgItem, 1);
					break;
					
				case 3:// 強化失敗會倒退 或 強化失敗會消失
					if (_random.nextBoolean()) {// 強化失敗會倒退
						final L1ItemPowerUpdate ole2 = tmplist.get(order_id - 1);// 取回上一個排序資料
						pc.getInventory().removeItem(tgItem, 1);
						CreateNewItem.createNewItem(pc, ole2.get_itemid(), 1);
						
					} else {// 強化失敗會消失
						// 164 \f1%0%s 產生激烈的 %1 光芒，一會兒後就消失了。銀色的
						pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
						pc.getInventory().removeItem(tgItem, 1);
					}
					break;
				}
			}
			
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}