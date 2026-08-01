package com.william;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.server.datatables.lock.CharSkillReading;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 简易系统-升级自动学习技能
 * 
 * @author admin
 *
 */
public class AutoAddSkillTable {

	private static final Log _log = LogFactory.getLog(AutoAddSkillTable.class);

	private static ArrayList<ArrayList<Object>> _array = new ArrayList<ArrayList<Object>>();

	private static final String TOKEN = ",";

	private static AutoAddSkillTable _instance;

	public static AutoAddSkillTable get() {
		if (_instance == null) {
			_instance = new AutoAddSkillTable();
		}
		return _instance;
	}

	private AutoAddSkillTable() {
		PerformanceTimer timer = new PerformanceTimer();
		getData();
		_log.info("载入升级自动学习技能资料数量: " + _array.size() + "(" + timer.get()
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

		ArrayList<Object> arraylist;

		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.createStatement();
			rs = ps.executeQuery("SELECT * FROM william_autoaddskill");

			while (rs.next()) {
				// 每次均不同
				arraylist = new ArrayList<Object>();

				arraylist.add(0, new Integer(rs.getInt("Pc_Level")));

				if (rs.getString("Skill_Id") != null
						&& !rs.getString("Skill_Id").equals("")
						&& !rs.getString("Skill_Id").equals("0")) {
					arraylist.add(1,
							getArray(rs.getString("Skill_Id"), TOKEN, 1));
				} else {
					arraylist.add(1, null);
				}
				arraylist.add(2, new Integer(rs.getInt("Royal")));
				arraylist.add(3, new Integer(rs.getInt("Knight")));
				arraylist.add(4, new Integer(rs.getInt("Mage")));
				arraylist.add(5, new Integer(rs.getInt("Elf")));
				arraylist.add(6, new Integer(rs.getInt("Darkelf")));
				arraylist.add(7, new Integer(rs.getInt("DragonKnight")));
				arraylist.add(8, new Integer(rs.getInt("Illusionist")));
				_array.add(arraylist);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	public static void forAutoAddSkill(final L1PcInstance pc) {
		if (_array.size() <= 0) {
			return;
		}
		ArrayList<?> aTempData = null;

		for (int i = 0; i < _array.size(); i++) {
			aTempData = _array.get(i);

			if (pc.getLevel() >= ((Integer) aTempData.get(0)).intValue()) {
				int[] id = (int[]) aTempData.get(1);
				// 王族
				if (((Integer) aTempData.get(2)).intValue() != 0
						&& pc.isCrown()) {
					for (int l = 0; l < id.length; l++) {
						int skillid = id[l];
						Skill(pc, skillid);
					}
				}
				// 骑士
				if (((Integer) aTempData.get(3)).intValue() != 0
						&& pc.isKnight()) {
					for (int l = 0; l < id.length; l++) {
						int skillid = id[l];
						Skill(pc, skillid);
					}
				}
				// 法师
				if (((Integer) aTempData.get(4)).intValue() != 0
						&& pc.isWizard()) {
					for (int l = 0; l < id.length; l++) {
						int skillid = id[l];
						Skill(pc, skillid);
					}
				}
				// 妖精
				if (((Integer) aTempData.get(5)).intValue() != 0 && pc.isElf()) {
					for (int l = 0; l < id.length; l++) {
						int skillid = id[l];
						Skill(pc, skillid);
					}
				}
				// 黑暗妖精
				if (((Integer) aTempData.get(6)).intValue() != 0
						&& pc.isDarkelf()) {
					for (int l = 0; l < id.length; l++) {
						int skillid = id[l];
						Skill(pc, skillid);
					}
				}	
			}
		}
	}

	private static void Skill(L1PcInstance pc, int skillid) {
		if (pc.isSkillMastery(skillid)) {
			return;
		}
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int i8 = 0;
		int j8 = 0;
		int k8 = 0;
		int l8 = 0;
		L1Skills l1skills = SkillsTable.get().getTemplate(skillid);
		int l6 = l1skills.getSkillLevel();
		int i7 = l1skills.getId();
		s = l1skills.getName();
		i = l1skills.getSkillId();
		switch (l6) {
		case 1: // '\001'
			j = i7;
			break;
		case 2: // '\002'
			k = i7;
			break;
		case 3: // '\003'
			l = i7;
			break;
		case 4: // '\004'
			i1 = i7;
			break;
		case 5: // '\005'
			j1 = i7;
			break;
		case 6: // '\006'
			k1 = i7;
			break;
		case 7: // '\007'
			l1 = i7;
			break;
		case 8: // '\b'
			i2 = i7;
			break;
		case 9: // '\t'
			j2 = i7;
			break;
		case 10: // '\n'
			k2 = i7;
			break;
		case 11: // '\013'
			l2 = i7;
			break;
		case 12: // '\f'
			i3 = i7;
			break;
		case 13: // '\r'
			j3 = i7;
			break;
		case 14: // '\016'
			k3 = i7;
			break;
		case 15: // '\017'
			l3 = i7;
			break;
		case 16: // '\020'
			i4 = i7;
			break;
		case 17: // '\021'
			j4 = i7;
			break;
		case 18: // '\022'
			k4 = i7;
			break;
		case 19: // '\023'
			l4 = i7;
			break;
		case 20: // '\024'
			i5 = i7;
			break;
		case 21: // '\025'
			j5 = i7;
			break;
		case 22: // '\026'
			k5 = i7;
			break;
		case 23: // '\027'
			l5 = i7;
			break;
		case 24: // '\030'
			i6 = i7;
			break;
		case 25: // '\031'
			j8 = i7;
			break;
		case 26: // '\032'
			k8 = i7;
			break;
		case 27: // '\033'
			l8 = i7;
			break;
		case 28: // '\034'
			i8 = i7;
			break;
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, j8, k8, l8,
				i8));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.get().spellMastery(k6, i, s, 0, 0);
		pc.sendPackets(new S_SystemMessage("自动學習 ( " + s + " ) 技能"));
		CharSkillReading.get().spellMastery(pc.getId(), i, s, 0, 0);
	}

	private static Object getArray(String s, String sToken, int iType) {
		StringTokenizer st = new StringTokenizer(s, sToken);
		int iSize = st.countTokens();
		String sTemp = null;
		if (iType == 1) { // int
			int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}

		if (iType == 2) { // String
			String[] sReturn = new String[iSize];
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

	public static ArrayList<ArrayList<Object>> getSetList() {
		return _array;
	}

}
