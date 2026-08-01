package com.lineage.server.command.executor;

import com.lineage.data.item_etcitem.hole.CustomHoleGemData;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 創造物品(參數:物品編號 - 數量 - 追加質)
 * @author dexc
 *
 */
public class L1CreateItemCustom implements L1CommandExecutor {

	private static final Log _log = LogFactory.getLog(L1CreateItemCustom.class);

	private L1CreateItemCustom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CreateItemCustom();
	}

	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String nameid = st.nextToken();
			// 數量
			long count = 1;
			if (st.hasMoreTokens()) {
				count = Long.parseLong(st.nextToken());
			}
			
			// 強化質
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}

			// 物品編號
			int itemid = 0;
			try {
				itemid = Integer.parseInt(nameid);
				
			} catch (final NumberFormatException e) {
				itemid = ItemTable.get().findItemIdByNameWithoutSpace(
						nameid);
				if (itemid == 0) {
					pc.sendPackets(new S_SystemMessage("沒有找到條件吻合的物品。"));
					return;
				}
			}
			
			// 物品資料
			final L1Item temp = ItemTable.get().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					// 可以堆疊的物品
					final L1ItemInstance item = ItemTable.get().createItem(
							itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
//					item.setIdentified(true);
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
						// 403:獲得0%。
						pc.sendPackets(new S_ServerMessage(403,
								item.getLogName() + "(ID:" + itemid + ")"));
					}
				} else {
					count = 1;
					itemid = 301068;
					// 不可以堆疊的物品
					if (count > 10) {
						pc.sendPackets(new S_SystemMessage("不可以堆疊的物品一次創造數量禁止超過10"));
						return;
					}
					
					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.get().createItem(itemid);
						item.setEnchantLevel(enchant);
						item.setIdentified(true);
						final List<CustomHoleGemData.GemData> gemData = new ArrayList<>(CustomHoleGemData.getInstance().getGemData(140520));
						for (final CustomHoleGemData.GemData gem : gemData) {
							if (gem.getName().contains("傷害A+")) {
								item.setGemHoleIndex(gem.getIndex());
								pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
								pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL + L1PcInventory.COL_GEM_HOLE_INDEX);
								break;
							}
						}

//						item.setIdentified(true);

						if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							pc.getInventory().storeItem(item);
						} else {
							break;
						}
					}
					if (createCount > 0) {
						// 403:獲得0%。
						pc.sendPackets(new S_ServerMessage(403,
								item.getLogName() + "(ID:" + itemid + ")"));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("指定ID不存在"));
			}
		} catch (final Exception e) {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}
