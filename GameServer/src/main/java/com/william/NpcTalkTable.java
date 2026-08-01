package com.william;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public class NpcTalkTable {
	private static final Log _log = LogFactory.getLog(NpcTalkTable.class);

	private static ArrayList<ArrayList<Object>> _array = new ArrayList<ArrayList<Object>>();
	private static final String TOKEN = ",";
	private static NpcTalkTable _instance;

	public static NpcTalkTable get() {
		if (_instance == null) {
			_instance = new NpcTalkTable();
		}
		return _instance;
	}

	private NpcTalkTable() {
		PerformanceTimer timer = new PerformanceTimer();
		getData();
		_log.info("載入NPC顯示對話設置條件資料數量: " + _array.size() + "(" + timer.get()
				+ "ms)");
		if (_array.size() <= 0) {
			_array.clear();
			_array = null;
		}
	}

	private void getData() {
		Connection cn = null;
		Statement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.createStatement();
			rs = ps.executeQuery("SELECT * FROM william_npc_action");

			while (rs.next()) {
				ArrayList<Object> arraylist = new ArrayList<Object>();

				arraylist.add(0, new Integer(rs.getInt("npcid")));
				arraylist.add(1, new Integer(rs.getInt("checkLevel")));
				arraylist.add(2, new Integer(rs.getInt("checkClass")));
				arraylist.add(3, new Integer(rs.getInt("checkPoly")));
				arraylist.add(4, new Integer(rs.getInt("checkQuestId")));
				arraylist.add(5, new Integer(rs.getInt("checkQuestOrder")));

				if ((rs.getString("checkItem") != null)
						&& (!rs.getString("checkItem").equals("")))
					arraylist.add(6,
							getArray(rs.getString("checkItem"), TOKEN, 1));
				else {
					arraylist.add(6, null);
				}
				if ((rs.getString("checkItemCount") != null)
						&& (!rs.getString("checkItemCount").equals("")))
					arraylist.add(7,
							getArray(rs.getString("checkItemCount"), TOKEN, 1));
				else {
					arraylist.add(7, null);
				}
				if ((rs.getString("notHaveItem") != null)
						&& (!rs.getString("notHaveItem").equals("")))
					arraylist.add(8,
							getArray(rs.getString("notHaveItem"), TOKEN, 1));
				else {
					arraylist.add(8, null);
				}
				if ((rs.getString("notHaveItemCount") != null)
						&& (!rs.getString("notHaveItemCount").equals("")))
					arraylist.add(9,
							getArray(rs.getString("notHaveItemCount"), TOKEN, 1));
				else {
					arraylist.add(9, null);
				}
				if ((rs.getString("ShowHtml") != null)
						&& (!rs.getString("ShowHtml").equals("")))
					arraylist.add(10, new String(rs.getString("ShowHtml")));
				else
					arraylist.add(10, null);
				if ((rs.getString("ShowHtmlData") != null)
						&& (!rs.getString("ShowHtmlData").equals("")))
					arraylist.add(11,
							getArray(rs.getString("ShowHtmlData"), TOKEN, 2));
				else
					arraylist.add(11, null);
				_array.add(arraylist);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	public static boolean forNpcAction(L1PcInstance pc, int npcid, int oid) {
		if (_array == null) {
			return false;
		}
		ArrayList<Object> aTempData = null;

		L1PcQuest quest = pc.getQuest();

		int class_id = 0;
		if (pc.isCrown())
			class_id = 1;
		else if (pc.isKnight())
			class_id = 2;
		else if (pc.isWizard())
			class_id = 3;
		else if (pc.isElf())
			class_id = 4;
		else if (pc.isDarkelf())
			class_id = 5;
           {
	
		}

		for (int i = 0; i < _array.size(); i++) {
			aTempData = _array.get(i);
			if ((aTempData.get(0) != null)
					&& (((Integer) aTempData.get(0)).intValue() == npcid)
					&& ((pc.getLevel() == ((Integer) aTempData.get(1))
							.intValue()) || (((Integer) aTempData.get(1))
							.intValue() == 0))
					&& (((((Integer) aTempData.get(2)).intValue() != 0) && (((Integer) aTempData
							.get(2)).intValue() == class_id)) || ((((Integer) aTempData
							.get(2)).intValue() == 0) && (((((Integer) aTempData
							.get(3)).intValue() != 0) && (pc.getTempCharGfx() == ((Integer) aTempData
							.get(3)).intValue())) || ((((Integer) aTempData
							.get(3)).intValue() == 0) && (((((Integer) aTempData
							.get(4)).intValue() != 0) && (((Integer) aTempData
							.get(5)).intValue() == quest
							.get_step(((Integer) aTempData.get(4)).intValue()))) || ((((Integer) aTempData
							.get(4)).intValue() == 0) && ((((int[]) aTempData
							.get(6) != null)
							&& ((int[]) aTempData.get(7) != null) && (pc
								.getInventory().checkItem(
							(int[]) aTempData.get(6), (int[]) aTempData.get(7)))) || (((int[]) aTempData
							.get(6) == null)
							&& ((int[]) aTempData.get(7) == null) && ((((int[]) aTempData
							.get(8) != null)
							&& ((int[]) aTempData.get(9) != null) && (!pc
							.getInventory().checkItem((int[]) aTempData.get(8),
									(int[]) aTempData.get(9)))) || (((int[]) aTempData
							.get(8) == null) && ((int[]) aTempData.get(9) == null)))))))))))) {
				if ((String[]) aTempData.get(11) != null) {
					pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData
							.get(10), (String[]) aTempData.get(11)));
					return true;
				}
				pc.sendPackets(new S_NPCTalkReturn(oid, (String) aTempData
						.get(10)));
				return true;
			}

		}

		return false;
	}

	private static Object getArray(String s, String sToken, int iType) {
		StringTokenizer st = new StringTokenizer(s, sToken);
		int iSize = st.countTokens();
		String sTemp = null;

		if (iType == 1) {
			int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}

		if (iType == 2) {
			String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}

		if (iType == 3) {
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		return null;
	}

	public static ArrayList<ArrayList<Object>> getSetList() {
		return _array;
	}
}