package com.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddItem;
import com.lineage.server.serverpackets.S_DeleteInventoryItem;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public class NpcQuest {
	//private static final Log _log = LogFactory.getLog(NpcQuest.class);
	private static Logger _log = Logger.getLogger(NpcQuest.class.getName());
	
	private static ArrayList<ArrayList<Object>> aData = new ArrayList<ArrayList<Object>>();
	
	//private static boolean NO_GET_DATA = false;

	public static final String TOKEN = ",";

	private static NpcQuest _instance;

	public static NpcQuest get() {
		if (_instance == null) {
			_instance = new NpcQuest();
		}
		return _instance;
	}
	
	private NpcQuest() {
		PerformanceTimer timer = new PerformanceTimer();
		getData();
		_log.info("載入NPC任務按鈕設置條件資料數量: " + aData.size() + "(" + timer.get() + "ms)");
		if (aData.size() <= 0) {
			aData.clear();
			aData = null;
		}
	}
	
	public static boolean forNpcQuest(final String s, final L1PcInstance pc, final L1NpcInstance npc, final int npcid, final int oid) {
	try {
		if (aData == null) {
			return false;
		}
		ArrayList<Object> aTempData = null;
//		if (!NO_GET_DATA) {
//			NO_GET_DATA = true;
//			getData();
//		}

		final L1PcQuest quest = pc.getQuest();

		int class_id = 0;
		if (pc.isCrown()) {
			class_id = 1;
		} else if (pc.isKnight()) {
			class_id = 2;
		} else if (pc.isWizard()) {
			class_id = 3;
		} else if (pc.isElf()) {
			class_id = 4;
		} else if (pc.isDarkelf()) {
			class_id = 5;
		}

		for (int i = 0; i < aData.size(); i++) {
			aTempData = aData.get(i);
			if (((aTempData.get(0) != null) && (((Integer) aTempData.get(0))
					.intValue() == npcid)) // NPC編號
					&& ((String) aTempData.get(1)).equals(s) // 確認選項
					&& (((((Integer) aTempData.get(2)).intValue() != 0) && (pc
							.getLevel() >= ((Integer) aTempData.get(2))
							.intValue())) || (((Integer) aTempData.get(2))
							.intValue() == 0)) // 確認等級
					&& (((((Integer) aTempData.get(3)).intValue() != 0) && (((Integer) aTempData
							.get(3)).intValue() == class_id)) || (((Integer) aTempData
							.get(3)).intValue() == 0)) // 確認職業
					&& (((((Integer) aTempData.get(4)).intValue() != 0) && (pc
							.getTempCharGfx() == ((Integer) aTempData.get(4))
							.intValue())) || (((Integer) aTempData.get(4))
							.intValue() == 0)) // 確認變身
					&& (((((Integer) aTempData.get(5)).intValue() != 0) && (quest
							.get_step(((Integer) aTempData.get(5)).intValue()) == ((Integer) aTempData
							.get(6)).intValue())) || (((Integer) aTempData
							.get(5)).intValue() == 0)) // 確認任務流程
					&& (((((Integer) aTempData.get(7)).intValue() != 0) && (quest
							.get_step(((Integer) aTempData.get(7)).intValue()) != ((Integer) aTempData
							.get(8)).intValue())) || (((Integer) aTempData
							.get(7)).intValue() == 0)) // 確認任務流程
					&& ((((int[]) aTempData.get(11) != null)
							&& ((int[]) aTempData.get(12) != null) && !pc
							.getInventory().checkItem(
									(int[]) aTempData.get(11),
									(int[]) aTempData.get(12))) || (((int[]) aTempData
							.get(11) == null) && ((int[]) aTempData.get(12) == null)))
					/*&& ((((int[]) aTempData.get(9) != null)
							&& ((int[]) aTempData.get(10) != null) && pc
							.getInventory().checkItem((int[]) aTempData.get(9),
									(int[]) aTempData.get(10))) || (((int[]) aTempData
							.get(9) == null) && ((int[]) aTempData.get(10) == null)))*/) {
				boolean isCheck = true;
				if ((int[]) aTempData.get(9) != null && (int[]) aTempData.get(10) != null) {
					final int[] ci = (int[]) aTempData.get(9);
					final int[] cc = (int[]) aTempData.get(10);
					// 檢查等級
					if ((int[]) aTempData.get(24) != null) {
						
						final int[] cl = (int[]) aTempData.get(24);
						
						for (int j = 0; j < ci.length; j++) {
							final L1Item temp = ItemTable.get().getTemplate(ci[j]);
							if (!pc.getInventory().checkEnchantItem(ci[j], cl[j], cc[j])) {
								pc.sendPackets(new S_ServerMessage(337, "+" + cl[j] + " " + temp.getName() + "(" + cc[j] + ")")); // \f1%0不足。
								//return false;
								isCheck = false;
							}
						}
						
					} else {
						//if (!pc.getInventory().checkItem((int[]) aTempData.get(9), (int[]) aTempData.get(10))) {
						//	return false;
						//}
						for (int j = 0; j < ci.length; j++) {
							final L1Item temp = ItemTable.get().getTemplate(ci[j]);
							if (!pc.getInventory().checkItem(ci[j], cc[j])) {
								pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + cc[j] + ")")); // \f1%0不足。
								//return false;
								isCheck = false;
							}
						}
					}
				}
				
				boolean isGet = false;
				boolean isCreate = true;
				// 保留數值
				boolean isRetention = false;
				if (((Integer) aTempData.get(26)).intValue() != 0) {
					isRetention = true;
				}
				
				final int[] materials = (int[]) aTempData.get(13);
				final int[] counts = (int[]) aTempData.get(14);
				// 材料等級
				int[] levels = (int[]) aTempData.get(27);
				
				// 檢查身上的物品
				if (materials != null && counts != null) {
					for (int j = 0; j < materials.length; j++) {
						final L1Item temp = ItemTable.get().getTemplate(materials[j]);
						if (levels != null) {
							if (levels[j] > 0) {
								if (!pc.getInventory().checkEnchantItem(
										materials[j], levels[j], counts[j])) {
//									pc.sendPackets(new S_ServerMessage(337, "+"
//											+ levels[j]
//											+ " "
//											+ temp.getName()
//											+ "("
//											+ counts[j]
//											+ ")")); // \f1%0不足。
									isCreate = false;
								}
							} else {
								if (!pc.getInventory().checkItem(materials[j],
										counts[j])) {
//									pc.sendPackets(new S_ServerMessage(337, temp
//											.getName()
//											+ "("
//											+ (counts[j] - pc.getInventory()
//													.countItems(temp.getItemId()))
//											+ ")")); // \f1%0が不足しています。
									isCreate = false;
								}
							}
							
						} else {
							if (!pc.getInventory().checkItem(materials[j],
									counts[j])) {
//								pc.sendPackets(new S_ServerMessage(337, temp
//										.getName()
//										+ "("
//										+ (counts[j] - pc.getInventory()
//												.countItems(temp.getItemId()))
//										+ ")")); // \f1%0が不足しています。
								isCreate = false;
							}
						}
						
					}

					if (!isCreate || !isCheck) {
						if ((String[]) aTempData.get(23) != null) {
							pc.sendPackets(new S_NPCTalkReturn(oid,
									(String) aTempData.get(22),
									(String[]) aTempData.get(23)));
						} else if (aTempData.get(22) != null) {
							pc.sendPackets(new S_NPCTalkReturn(oid,
									(String) aTempData.get(22)));
						}
						return true;
					}
					
					if (isCreate && (((Integer) aTempData.get(15)).intValue() == 0)) { // 刪除確認的道具、並給予任務道具
						// 刪除道具
						for (int k = 0; k < materials.length; k++) {
							//pc.getInventory().consumeItem(materials[k], counts[k]);
							// 材料等級
							if (levels != null) {
								if (levels[k] > 0) {
									pc.getInventory().consumeEnchantItem(
											materials[k], levels[k],
											counts[k]);
								} else {
									pc.getInventory().consumeItem(materials[k],
											counts[k]);
								}
								
							} else {
								pc.getInventory().consumeItem(materials[k],
										counts[k]);
							}
						}
					}
				}

				// 給予道具
				if ((((Integer) aTempData.get(15)).intValue() == 0)
						&& ((int[]) aTempData.get(16) != null)
						&& ((int[]) aTempData.get(17) != null)) {

					final int[] giveMaterials = (int[]) aTempData.get(16);
					final int[] giveCounts = (int[]) aTempData.get(17);
					final int[] glevels = (int[]) aTempData.get(25);
					// 保留數值
					if (isRetention) {
						//for (int k = 0; k < materials.length; k++) {//(int[]) aTempData.get(9) != null
							final L1ItemInstance itemG = pc.getInventory().findItemId(((int[]) aTempData.get(9))[0]);
							L1Item item2 = ItemTable.get().getTemplate(giveMaterials[0]);
							pc.sendPackets(new S_DeleteInventoryItem(itemG));
							itemG.setItemId(giveMaterials[0]);
							itemG.setItem(item2);
							try {
								CharItemsReading.get().updateItemId_Name(itemG);
							} catch (Exception e) {
								//_log.error(e.getLocalizedMessage(), e);
							}
							pc.sendPackets(new S_AddItem(itemG));
							isGet = true;
						//}
					} else {
						
					//}
					for (int l = 0; l < giveMaterials.length; l++) {
						// 保留數值
						/*if (isRetention) {
							//for (int k = 0; k < materials.length; k++) {//(int[]) aTempData.get(9) != null
								final L1ItemInstance itemG = pc.getInventory().findItemId(((int[]) aTempData.get(9))[0]);
								L1Item item2 = ItemTable.get().getTemplate(giveMaterials[0]);
								pc.sendPackets(new S_DeleteInventoryItem(itemG));
								itemG.setItemId(giveMaterials[0]);
								itemG.setItem(item2);
								try {
									CharItemsReading.get().updateItemId_Name(itemG);
								} catch (Exception e) {
									//_log.error(e.getLocalizedMessage(), e);
								}
								pc.sendPackets(new S_AddItem(itemG));
							//}
							
						} else {*/
							final L1ItemInstance item = ItemTable.get().createItem(giveMaterials[l]);

							if (item.isStackable()) {// 可重疊
								item.setCount(giveCounts[l]);// 數量
							} else {
								item.setCount(1);
							}
							// 給予等級
							if (glevels != null) {
								item.setEnchantLevel(glevels[l]);// 強化數
							}
							
							if (item != null) {
								isGet = true;
								if (pc.getInventory().checkAddItem(item,
										(giveCounts[l])) == L1Inventory.OK) {
									pc.getInventory().storeItem(item);
								} // else { // 持てない場合は地面に落とす 處理のキャンセルはしない（不正防止）
									// L1World.getInstance()
									// .getInventory(pc.getX(), pc.getY(),
									// pc.getMapId()).storeItem(item);
								// }

								pc.sendPackets(new S_ServerMessage(143, npc
										.getNpcTemplate().get_name(), item
										.getLogName()));
							}
						}
					}
				}

				// 紀錄任務
				if ((((Integer) aTempData.get(18)).intValue() != 0)
						&& (((Integer) aTempData.get(19)).intValue() != 0)
						&& (isGet || isCreate)) {
					pc.getQuest().set_step(
							((Integer) aTempData.get(18)).intValue(),
							((Integer) aTempData.get(19)).intValue());
				}

				// 顯示對話檔
				if (((String[]) aTempData.get(21) != null)
						&& (isGet || isCreate)) {
					pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData
							.get(20), (String[]) aTempData.get(21)));
					return true;
				} else if ((aTempData.get(20) != null) && (isGet || isCreate)) {
					pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData
							.get(20)));
					return true;
				} else {
					pc.sendPackets(new S_NPCTalkReturn(oid, ""));
					return true;
				}
				// 顯示對話檔 end
			}
		}
	} catch (final Exception e) {
		//_log.error(e.getLocalizedMessage(), e);
		_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
	}
		return false;
	}

	private static Object getArray(final String s, final String sToken,
			final int iType) {
		final StringTokenizer st = new StringTokenizer(s, sToken);
		final int iSize = st.countTokens();
		String sTemp = null;

		if (iType == 1) { // int
			final int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}

		if (iType == 2) { // String
			final String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}

		if (iType == 3) { // String
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		return null;
	}

	private static void getData() {
		//java.sql.Connection con = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rset = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM william_npc_quest");
			//ArrayList<Object> aReturn = null;
			rset = pstm.executeQuery();
			//String sTemp = null;
			//if (rset != null) {
			//	while (rset.next()) {
			while (rset.next()) {
					ArrayList<Object> aReturn = new ArrayList<Object>();
					aReturn.add(0, new Integer(rset.getInt("npcid"))); // NPC編號
					String sTemp = rset.getString("action");
					aReturn.add(1, sTemp);
					aReturn.add(2, new Integer(rset.getInt("checkLevel"))); // 確認等級
					aReturn.add(3, new Integer(rset.getInt("checkClass"))); // 確認職業
					aReturn.add(4, new Integer(rset.getInt("checkPoly"))); // 確認變身
					aReturn.add(5, new Integer(rset.getInt("checkHaveQuestId"))); // 確認任務編號
					aReturn.add(6,
							new Integer(rset.getInt("checkHaveQuestOrder"))); // 確認任務流程
					aReturn.add(7, new Integer(rset.getInt("notHaveQuestId"))); // 確認任務編號
					aReturn.add(8,
							new Integer(rset.getInt("notHaveQuestOrder"))); // 確認任務流程

					if ((rset.getString("checkItem") != null)
							&& !rset.getString("checkItem").equals("")
							&& !rset.getString("checkItem").equals("0")) {
						aReturn.add(9,
								getArray(rset.getString("checkItem"), TOKEN, 1));
					} else {
						aReturn.add(9, null);
					}

					if ((rset.getString("checkItemCount") != null)
							&& !rset.getString("checkItemCount").equals("")
							&& !rset.getString("checkItemCount").equals("0")) {
						aReturn.add(
								10,
								getArray(rset.getString("checkItemCount"),
										TOKEN, 1));
					} else {
						aReturn.add(10, null);
					}

					if ((rset.getString("notHaveItem") != null)
							&& !rset.getString("notHaveItem").equals("")
							&& !rset.getString("notHaveItem").equals("0")) {
						aReturn.add(
								11,
								getArray(rset.getString("notHaveItem"), TOKEN,
										1));
					} else {
						aReturn.add(11, null);
					}

					if ((rset.getString("notHaveItemCount") != null)
							&& !rset.getString("notHaveItemCount").equals("")
							&& !rset.getString("notHaveItemCount").equals("0")) {
						aReturn.add(
								12,
								getArray(rset.getString("notHaveItemCount"),
										TOKEN, 1));
					} else {
						aReturn.add(12, null);
					}

					if ((rset.getString("material") != null)
							&& !rset.getString("material").equals("")
							&& !rset.getString("material").equals("0")) {
						aReturn.add(13,
								getArray(rset.getString("material"), TOKEN, 1));
					} else {
						aReturn.add(13, null);
					}

					if ((rset.getString("materialCount") != null)
							&& !rset.getString("materialCount").equals("")
							&& !rset.getString("materialCount").equals("0")) {
						aReturn.add(
								14,
								getArray(rset.getString("materialCount"),
										TOKEN, 1));
					} else {
						aReturn.add(14, null);
					}
					

					
					aReturn.add(15,
							new Integer(rset.getInt("justCheckMaterial"))); // 只確認道具、不刪除確認的道具
					

					
					if ((rset.getString("GiveItem") != null)
							&& !rset.getString("GiveItem").equals("")
							&& !rset.getString("GiveItem").equals("0")) {
						aReturn.add(16,
								getArray(rset.getString("GiveItem"), TOKEN, 1));
					} else {
						aReturn.add(16, null);
					}

					if ((rset.getString("GiveItemCount") != null)
							&& !rset.getString("GiveItemCount").equals("")
							&& !rset.getString("GiveItemCount").equals("0")) {
						aReturn.add(
								17,
								getArray(rset.getString("GiveItemCount"),
										TOKEN, 1));
					} else {
						aReturn.add(17, null);
					}

					
					aReturn.add(18, new Integer(rset.getInt("saveQuestId"))); // 紀錄任務編號
					aReturn.add(19, new Integer(rset.getInt("saveQuestOrder"))); // 紀錄任務流程

					if ((rset.getString("ShowHtml") != null)
							&& !rset.getString("ShowHtml").equals("")) {
						aReturn.add(20, new String(rset.getString("ShowHtml"))); // 顯示對話檔
					} else {
						aReturn.add(20, null);
					}

					if ((rset.getString("ShowHtmlData") != null)
							&& !rset.getString("ShowHtmlData").equals("")) {
						aReturn.add(
								21,
								getArray(rset.getString("ShowHtmlData"), TOKEN,
										2)); // 顯示對話內容
					} else {
						aReturn.add(21, null);
					}

					if ((rset.getString("ShowNotHaveHtml") != null)
							&& !rset.getString("ShowNotHaveHtml").equals("")) {
						aReturn.add(22,
								new String(rset.getString("ShowNotHaveHtml"))); // 顯示對話檔
					} else {
						aReturn.add(22, null);
					}

					if ((rset.getString("ShowNotHaveHtmlData") != null)
							&& !rset.getString("ShowNotHaveHtmlData")
									.equals("")) {
						aReturn.add(
								23,
								getArray(rset.getString("ShowNotHaveHtmlData"),
										TOKEN, 2)); // 顯示對話內容
					} else {
						aReturn.add(23, null);
					}

					// 檢查物品等級
					if ((rset.getString("checkItemLv") != null)
							&& !rset.getString("checkItemLv").equals("")
							&& !rset.getString("checkItemLv").equals("0")) {
						aReturn.add(24, getArray(rset.getString("checkItemLv"), TOKEN, 1));
					} else {
						aReturn.add(24, null);
					}
					
					// 給予物品等級
					if ((rset.getString("GiveItemLv") != null)
							&& !rset.getString("GiveItemLv").equals("")
							&& !rset.getString("GiveItemLv").equals("0")) {
						aReturn.add(25, getArray(rset.getString("GiveItemLv"), TOKEN, 1));
					} else {
						aReturn.add(25, null);
					}
					
					// 給予物品如果是裝備武器是否保留原數值
					aReturn.add(26, new Integer(rset.getInt("Retention")));
					
					// 材料等級
					if ((rset.getString("materialLv") != null)
							&& !rset.getString("materialLv").equals("")
							&& !rset.getString("materialLv").equals("0")) {
						aReturn.add(
								27,
								getArray(rset.getString("materialLv"),
										TOKEN, 1));
					} else {
						aReturn.add(27, null);
					}
					
					aData.add(aReturn);
				}
			//}
			//if ((con != null) && !con.isClosed()) {
			//	con.close();
			//}
		//} catch (final Exception ex) {
		//}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rset, pstm, con);
		}
	}

//	public static void main(final String a[]) {
//		while (true) {
//			try {
//				Server.main(null);
//			} catch (final Exception ex) {
//			}
//		}
//	}
}