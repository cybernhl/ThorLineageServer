package com.lineage.data.npc.shop;

import java.sql.*;
import java.util.Map;

import com.lineage.DatabaseFactory;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.EzpayReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.WriteLogTxt;

/**
 * 商品領取專員NPC<BR>
 *
 * DELETE FROM `npc` WHERE `npcid`='70558' ; INSERT INTO `npc` VALUES ('70558',
 * '攻擊箭孔', '', '0', '', 'L1Bow', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '', '0', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '-1', '0',
 * '14', '0', '1', '0');
 *
 * DELETE FROM `npc` WHERE `npcid`='70750' ; INSERT INTO `npc` VALUES ('70750',
 * '商品領取專員', '商品領取專員', 'shop.NPC_Ezpay', '', 'L1Merchant', '6989', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '-1', '-1', '0', '0',
 * '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
 * '0', '0', '0', '0', '-1', '0', '14', '0', '1', '0');
 *
 * DELETE FROM `server_event` WHERE `id`='34'; INSERT INTO `server_event` VALUES
 * ('34', '特殊領取專員(商品領取專員)', 'SpawnOtherSet', '1', 'true', '說明:啟動特殊領取專員以及召喚');
 *
 * DELETE FROM `server_event_spawn` WHERE `eventid`='34'; DELETE FROM
 * `server_event_spawn` WHERE `id`='40329'; DELETE FROM `server_event_spawn`
 * WHERE `id`='40330'; INSERT INTO `server_event_spawn` VALUES (40329, 34,
 * '商品領取專員(騎士村)', 1, 70750, 0, 33083, 33406, 0, 0, 6, 0, 4, 0, 1); INSERT INTO
 * `server_event_spawn` VALUES (40330, 34, '商品領取專員(奇岩村)', 1, 70750, 0, 33430,
 * 32806, 0, 0, 4, 0, 4, 0, 1);
 *
 *
 * @author dexc
 *
 */
public class NPC_Ezpay extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(NPC_Ezpay.class);

	/**
	 *
	 */
	private NPC_Ezpay() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new NPC_Ezpay();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_0"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		boolean isCloseList = false;
		if (cmd.equals("up")) {
			final int page = pc.get_other().get_page() - 1;
			showPage(pc, npc, page);

		} else if (cmd.equals("dn")) {
			final int page = pc.get_other().get_page() + 1;
			showPage(pc, npc, page);

		} else if (cmd.equalsIgnoreCase("1")) {// 領取商品
			pc.get_otherList().SHOPLIST.clear();
			// 取回資料
			final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(
					pc.getAccountName().toLowerCase());
			if (info.size() <= 0) {
				// 無資料
				isCloseList = true;
				// 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
				pc.sendPackets(new S_ServerMessage("\\fV並沒有查詢到您的相關商品記錄!!"));

			} else {
				pc.get_other().set_page(0);
				int index = 0;
				for (Integer key : info.keySet()) {
					int[] value = info.get(key);
					if (value != null) {
						pc.get_otherList().SHOPLIST.put(index, value);
						index++;
					}
				}
				showPage(pc, npc, 0);
			}

		} else if (cmd.equalsIgnoreCase("2")) {// 全部領取
			// 取回資料
			final Map<Integer, int[]> info = EzpayReading.get().ezpayInfo(
					pc.getAccountName().toLowerCase());
			if (info.size() <= 0) {
				// 無資料
				isCloseList = true;
				// 並沒有查詢到您的相關贊助記錄!!請您再仔細查詢一次。
				pc.sendPackets(new S_ServerMessage("\\fV並沒有查詢到您的相關商品記錄!!"));

			} else {
				for (Integer key : info.keySet()) {
					int[] value = info.get(key);
					int id = value[0];
					int itemid = value[1];
					int count = value[2];
					String pcxue = "";
					if (pc.getClan() != null) {
						pcxue = pc.getClanname();
					}
					if (EzpayReading.get().update(pc.getAccountName(), id,
							pc.getName(),
							pc.getNetConnection().getIp().toString(), pcxue)) {
						// 給予物品
						if (itemid == 44070) {
							final L1Account account = AccountReading.get().getAccount(pc.getAccountName());
							int counts = count + account.get_StoredMoney();
							AccountReading.get().updateStoredMoney(pc.getAccountName(), counts);
							FullValueStoredReward(pc, count);
							CumulativeStoredValueReward(pc, counts);
							Ratio(pc, count, itemid);
							// NewVip(pc , count);
							// RedPackage(pc, count);
							Rang(pc, count);
						} else {
							createNewItem(pc, npc, itemid, count);
						}
						_log.fatal("帳號:" + pc.getAccountName().toLowerCase()
								+ " 人物:" + pc.getName() + " 領取交易序號:" + id + "("
								+ itemid + ") 數量:" + count + " 完成!!");
						WriteLogTxt.YanBoLog("儲值紀錄",
								"帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id
										+ "(" + itemid + ") 數量:" + count + " 完成!!,時間:" + "("
										+ new Timestamp(System.currentTimeMillis()) + ")。");
					} else {
						pc.sendPackets(new S_ServerMessage(
								"\\fV領取失敗!!請聯繫線上GM!! ID:" + id));
						_log.fatal("帳號:" + pc.getAccountName().toLowerCase()
								+ " 人物:" + pc.getName() + " 領取交易序號:" + id
								+ " 領取失敗!!");
						pc.sendPackets(new S_ServerMessage("\\fV領取失敗!!請聯繫線上GM!! ID:" + id));
						_log.fatal("帳號:" + pc.getAccountName().toLowerCase() + " 人物:" + pc.getName() + " 領取交易序號:" + id
								+ " 領取失敗!!");
						isCloseList = true;
					}
				}
			}
			isCloseList = true;

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 範圍滿額
	 *
	 * @param pc
	 * @param count
	 */
	private static void Rang(final L1PcInstance pc, final long count) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM 贊助_範圍滿額獎勵");
			rs = pstm.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					final int min = rs.getInt("金額範圍最小值");
					final int max = rs.getInt("金額範圍最大值");
					final int item_id = rs.getInt("道具編號");
					final int show = rs.getInt("是否公告");
					if (count >= min && count <= max) {
						final L1ItemInstance item = ItemTable.get().createItem(item_id);
						CreateNewItem.createNewItem(pc, item, 1);
						pc.sendPackets(new S_ServerMessage("\\fW贊助專員給你" + item.getLogName())); // 获得0%。
						if (show > 0) {
							World.get().broadcastPacketToAll(
									new S_SystemMessage("恭喜玩家[" + pc.getName() + "]獲得儲值獎勵[" + item.getName() + "]"));
						}
						WriteLogTxt.YanBoLog("範圍滿額獎勵紀錄", "玩家:【 " + pc.getName() + " 】 領取範圍滿額獎勵[" + item.getLogName()
								+ "]" + "1個" + ",時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
					}
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	/**
	 * 比值器
	 *
	 * @param pc
	 * @param count
	 */
	private static void Ratio(final L1PcInstance pc, final long count, final int itemid) {
		int sw = 0;
		final L1ItemInstance item = ItemTable.get().createItem(itemid);
		if (itemid == 44070) {
			Connection conn = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				conn = DatabaseFactory.get().getConnection();
				pstm = conn.prepareStatement("SELECT * FROM 贊助_金流比值器");
				rs = pstm.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						int min = rs.getInt("最小贊助比值");
						int max = rs.getInt("最大贊助比值");
						int ratio = rs.getInt("比值");
						if (count >= min && count <= max) {
							final int TM = (int) ((int) (count * ratio) * 0.01);
							CreateNewItem.createNewItem(pc, item, TM);
							if (item.getItemId() == 44070) {
								World.get().broadcastPacketToAll(new S_ServerMessage("【贊助專員】" + pc.getName() + " 領取了 " + item.getLogName()));
							}
							pc.sendPackets(new S_ServerMessage("\\fW贊助專員給你" + item.getLogName())); // 获得0%。
							WriteLogTxt.YanBoLog("比值器領取元寶紀錄",
									"玩家:【 " + pc.getName() + " 】 領取儲值(經過比值..原" + count + "個變為 [ " + TM + " ] " + "個元寶"
											+ ",時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
							sw++;
						}
					}
				}
			} catch (SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(conn);
			}
			if (sw == 0) {
				CreateNewItem.createNewItem(pc, item, count);
				pc.sendPackets(new S_ServerMessage("\\fW贊助專員給你" + item.getLogName())); // 获得0%。
				WriteLogTxt.YanBoLog("比值器領取元寶紀錄", "玩家:【 " + pc.getName() + " 】 領取儲值(未經比值)" + " [ " + count + " ] "
						+ "個元寶" + ",時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
			}
		} else {
			CreateNewItem.createNewItem(pc, item, count);
			pc.sendPackets(new S_ServerMessage("\\fW贊助專員給你" + item.getLogName())); // 获得0%。
		}
	}

	/**
	 * 累積儲值
	 *
	 * @param pc
	 * @param count
	 */
	private static void CumulativeStoredValueReward(final L1PcInstance pc, final long count) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM 贊助_累積儲值獎勵設置");
			rs = pstm.executeQuery();
			if (rs != null) {
				final L1Account account = AccountReading.get().getAccount(pc.getAccountName());
				while (rs.next()) {
					int money = rs.getInt("累積額度");
					int itemid = rs.getInt("贈送物品");
					int itemcount = rs.getInt("數量");
					int q = rs.getInt("累積儲值紀錄ID");
					if (q == 0 && count >= money) {// 可以無限領取
						pc.sendPackets(new S_SystemMessage("系統錯誤，請連絡線上GM"));
					} else if (q != 0 && count >= money) {// 限制只能領一次
						if (account.get_CumulativeStored_Log() < q) {
							L1ItemInstance items = pc.getInventory().storeItem(itemid, itemcount);
							AccountReading.get().updateCumulativeStoredLog(pc.getAccountName(), q);
							pc.sendPackets(new S_SystemMessage(
									"獲得累積儲值" + money + "好禮:" + items.getName() + "(" + itemcount + ")"));
							WriteLogTxt.YanBoLog("累積儲值獎勵紀錄",
									"玩家:【 " + pc.getName() + " 】 獲得累積儲值獎勵" + money + " 好禮[ " + items.getName() + "("
											+ itemcount + "),時間:" + "(" + new Timestamp(System.currentTimeMillis())
											+ ")。");
						}
					}
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private static void FullValueStoredReward(final L1PcInstance pc, final long count) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM 贊助_滿額送禮設置");
			rs = pstm.executeQuery();
			if (rs != null) {
				final L1Account account = AccountReading.get().getAccount(pc.getAccountName());
				while (rs.next()) {
					int money = rs.getInt("額度");
					int itemid = rs.getInt("贈送物品");
					int itemcount = rs.getInt("數量");
					int q = rs.getInt("滿額紀錄ID");
					if (q == 0 && count >= money && itemcount > 0) {// 可以無限領取
						L1ItemInstance items = pc.getInventory().storeItem(itemid, itemcount);
						pc.sendPackets(
								new S_SystemMessage("獲得贊助滿" + money + "好禮:" + items.getName() + "(" + itemcount + ")"));
						WriteLogTxt.YanBoLog("贊助滿額(可連續)獎勵紀錄",
								"玩家:【 " + pc.getName() + " 】 獲得贊助滿額獎勵" + money + " 好禮[ " + items.getName() + "("
										+ itemcount + "),時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
					} else if (q != 0 && count >= money) {// 限制只能領一次
						if (account.get_FullAmount_Log() < q) {
							L1ItemInstance items = pc.getInventory().storeItem(itemid, itemcount);
							AccountReading.get().updateFullAmountLog(pc.getAccountName(), q);
							pc.sendPackets(new S_SystemMessage(
									"獲得贊助滿" + money + "好禮:" + items.getName() + "(" + itemcount + ")"));
							WriteLogTxt.YanBoLog("贊助滿額(一次性)獎勵紀錄",
									"玩家:【 " + pc.getName() + " 】 獲得贊助滿額獎勵" + money + " 好禮[ " + items.getName() + "("
											+ itemcount + "),時間:" + "(" + new Timestamp(System.currentTimeMillis())
											+ ")。");
						}
					}
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	/**
	 * 展示指定頁面
	 *
	 * @param pc
	 * @param npc
	 * @param page
	 */
	private static void showPage(final L1PcInstance pc,
			final L1NpcInstance npc, int page) {
		final Map<Integer, int[]> list = pc.get_otherList().SHOPLIST;

		// 全部頁面數量
		int allpage = list.size() / 10;
		if ((page > allpage) || (page < 0)) {
			page = 0;
		}

		if (list.size() % 10 != 0) {
			allpage += 1;
		}

		pc.get_other().set_page(page);// 設置頁面

		final int showId = page * 10;// 要顯示的項目ID

		final StringBuilder stringBuilder = new StringBuilder();
		// 每頁顯示10筆(showId + 10)資料
		for (int key = showId; key < showId + 10; key++) {
			final int[] info = list.get(key);
			if (info != null) {
				// 找回物品
				final L1Item itemtmp = ItemTable.get().getTemplate(info[1]);
				if (itemtmp != null) {
					stringBuilder.append(itemtmp.getName() + "(" + info[2]
							+ "),");
				}
			}
		}
		final String[] clientStrAry = stringBuilder.toString().split(",");
		if (allpage == 1) {
			// 核心要求顯示僅有一頁
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_1",
					clientStrAry));

		} else {
			if (page < 1) {// 無上一頁
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_3",
						clientStrAry));

			} else if (page >= (allpage - 1)) {// 無下一頁(吻合第一頁為0 所以 -1)
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_4",
						clientStrAry));

			} else {// 正常
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_2",
						clientStrAry));
			}
		}
	}

	private static void createNewItem(final L1PcInstance pc,
			final L1NpcInstance npc, final int item_id, final long count) {
		try {
			if (pc == null) {
				return;
			}
			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(item_id);
			if (item != null) {
				item.setCount(count);
				item.setIdentified(true);

				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage("\\fW" + npc.getNameId()
						+ "給你" + item.getLogName())); // 获得0%。

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
