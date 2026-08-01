package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;

public class ItemUseTable {
	private static final Log _log = LogFactory.getLog(ItemUseTable.class);

	private static ArrayList<ArrayList<Object>> _array = new ArrayList<ArrayList<Object>>();
	private static final String TOKEN = ",";
	private static ItemUseTable _instance;

	public static ItemUseTable get() {
		if (_instance == null) {
			_instance = new ItemUseTable();
		}
		return _instance;
	}

	private ItemUseTable() {
		PerformanceTimer timer = new PerformanceTimer();
		getData();
		_log.info("載入道具使用效果設置資料數量: " + _array.size() + "(" + timer.get()
				+ "ms)");
		if (_array.size() <= 0) {
			_array.clear();
			_array = null;
		}
	}

	public static void forItemUSe(L1PcInstance pc, L1ItemInstance itemInstance) {
		if (_array.size() <= 0) {
			return;
		}
		
		boolean isSave = false;

		int itemid = itemInstance.getItemId();

		ArrayList<Object> aTempData = null;
		
		for (int i = 0; i < _array.size(); i++) {
			aTempData = _array.get(i);
			
			if (((Integer) aTempData.get(0)).intValue() == itemid) {
				if (((Integer) aTempData.get(1)).intValue() != 0) {
					byte class_id = 0;
					String msg = "";
					if (pc.isCrown()){
						class_id = 1;
					}else if (pc.isKnight()){
						class_id = 2;
					}else if (pc.isWizard()){
						class_id = 3;
					}else if (pc.isElf()){
						class_id = 4;
					}
					switch (((Integer) aTempData.get(1)).intValue()) {
					case 1:
						msg = "王族";
						break;
					case 2:
						msg = "騎士";
						break;
					case 3:
						msg = "法師";
						break;
					case 4:
						msg = "妖精";
						break;
					}

					if (((Integer) aTempData.get(1)).intValue() != class_id) {
						pc.sendPackets(new S_SystemMessage("你的職業無法使用" + msg + "的專屬道具。"));
						return;
					}
				}

				/**
				 * 檢查種族
				 */
				/*
				 * if (((Integer) aTempData.get(36)).intValue() != 0) { if
				 * (pc.getRace() != ((Integer) aTempData.get(36)).intValue())
				 * { pc.sendPackets(new S_ServerMessage(3142, "不符")); return;
				 * } }
				 */
				//
				if ((((Integer) aTempData.get(14)).intValue() != 0) && (pc.getLevel() < ((Integer) aTempData.get(14)).intValue())) {
					pc.sendPackets(new S_SystemMessage("等級" + ((Integer) aTempData.get(14)).intValue() + "以上才可使用此道具。"));
					return;
				}
				//
				if ((((Integer) aTempData.get(2)).intValue() != 0) && (!pc.getInventory().checkItem(((Integer) aTempData.get(2)).intValue()))) {
					L1Item temp = ItemTable.get().getTemplate(((Integer) aTempData.get(2)).intValue());
					pc.sendPackets(new S_SystemMessage("使用此道具需要(" + temp.getName() + ")來作為媒介。"));
					return;
				}

				// if ((((Integer) aTempData.get(17)).intValue() != 0)
				// && (pc.getKyo() != ((Integer) aTempData.get(17))
				// .intValue())) {
				// pc.sendPackets(new S_SystemMessage("這個道具不是你目前的種族能使用的。"));
				// return;
				// }
				//
				// if (((Integer) aTempData.get(18)).intValue() != 0) {
				// if (pc.getQigong() <= ((Integer) aTempData.get(18))
				// .intValue()) {
				// pc.sendPackets(new S_SystemMessage("精元("
				// + ((Integer) aTempData.get(18)).intValue()
				// + ")不足，無法使用道具。"));
				// return;
				// }
				// }
				//
				// if ((((Integer) aTempData.get(19)).intValue() != 0)
				// && (pc.getPrestige() <= ((Integer) aTempData.get(19))
				// .intValue())) {
				// pc.sendPackets(new S_SystemMessage("聲望尚未到達標準，無法使用道具。"));
				// return;
				// }
				//
				if (((Integer) aTempData.get(3)).intValue() != 0) {
					L1ItemInstance item = pc.getInventory().findItemId(((Integer) aTempData.get(0)).intValue());
					pc.getInventory().removeItem(item.getId(), 1);
				}
				//
				if (((Integer) aTempData.get(4)).intValue() != 0) {
					if ((pc.getMapId() == 9000) 
							|| (pc.getMapId() == 9100)
							|| (pc.getMapId() == 9102)
							|| (pc.getMapId() == 9202)) {
						pc.sendPackets(new S_ServerMessage(1170));
						return;
					}
					if (pc.hasSkillEffect(67)) {
						pc.removeSkillEffect(67);
					}
					L1PolyMorph.doPoly(pc, ((Integer) aTempData.get(4)).intValue(), ((Integer) aTempData.get(5)).intValue(), 1);
				}
				//
				if (((Integer) aTempData.get(6)).intValue() != 0) {
					pc.addBaseMaxHp((short)((Integer) aTempData.get(6)).intValue());
					pc.sendPackets(new S_SystemMessage("HP永久增加了(" + ((Integer) aTempData.get(6)).intValue() + ")滴。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(7)).intValue() != 0) {
					pc.addBaseMaxMp((short)((Integer) aTempData.get(7)).intValue());
					pc.sendPackets(new S_SystemMessage("MP永久增加了(" + ((Integer) aTempData.get(7)).intValue() + ")滴。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(8)).intValue() != 0) {
					pc.addBaseStr((byte) ((Integer) aTempData.get(8)).intValue());
					pc.sendPackets(new S_SystemMessage("力量永久增加了(" + ((Integer) aTempData.get(8)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(8)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(9)).intValue() != 0) {
					pc.addBaseDex((byte) ((Integer) aTempData.get(9)).intValue());
					pc.sendPackets(new S_SystemMessage("敏捷永久增加了(" + ((Integer) aTempData.get(9)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(9)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(10)).intValue() != 0) {
					pc.addBaseCon((byte) ((Integer) aTempData.get(10)).intValue());
					pc.sendPackets(new S_SystemMessage("體質永久增加了(" + ((Integer) aTempData.get(10)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(10)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(11)).intValue() != 0) {
					pc.addBaseWis((byte) ((Integer) aTempData.get(11)).intValue());
					pc.sendPackets(new S_SystemMessage("精神永久增加了(" + ((Integer) aTempData.get(11)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(11)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(12)).intValue() != 0) {
					pc.addBaseInt((byte) ((Integer) aTempData.get(12)).intValue());
					pc.sendPackets(new S_SystemMessage("智力永久增加了(" + ((Integer) aTempData.get(12)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(12)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(13)).intValue() != 0) {
					pc.addBaseCha((byte) ((Integer) aTempData.get(13)) .intValue());
					pc.sendPackets(new S_SystemMessage("魅力永久增加了(" + ((Integer) aTempData.get(13)).intValue() + ")點。"));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.setElixirStats(pc.getElixirStats() + ((Integer) aTempData.get(13)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(15)).intValue() != 0) {
					if (pc.hasSkillEffect(71)) {
						pc.sendPackets(new S_ServerMessage(698));
						return;
					}
					//
					if (pc.hasSkillEffect(5167)) {
						pc.setCurrentHp(pc.getCurrentHp() - RandomArrayList.getInc(32, 12));
						
					} else {
						pc.setCurrentHp(pc.getCurrentHp() + ((Integer) aTempData.get(15)).intValue());
					}
					
					pc.sendPackets(new S_ServerMessage(77));
				}
				//
				if (((Integer) aTempData.get(16)).intValue() != 0) {
					if (pc.hasSkillEffect(71)) {
						pc.sendPackets(new S_ServerMessage(698));
						return;
					}
					pc.setCurrentMp(pc.getCurrentMp() + ((Integer) aTempData.get(16)).intValue());
					pc.sendPackets(new S_ServerMessage(338, "$1084"));
				}
				// 加經驗
				if (((Integer) aTempData.get(20)).intValue() != 0) {
					pc.setExp(pc.getExp() + ((Integer) aTempData.get(20)).intValue());
					isSave = true;
				}
				//
				if (((Integer) aTempData.get(21)).intValue() != 0) {
					int Lawful = pc.getLawful() + ((Integer) aTempData.get(21)).intValue();
					if (Lawful > 32767) {
						Lawful = 32767;
					} else if (Lawful < -32768) {
						Lawful = -32768;
					}
					pc.setLawful(Lawful);
					isSave = true;
				}
				
				// 特效
				if (((Integer) aTempData.get(22)).intValue() != 0) {
					pc.sendPackets(new S_SkillSound(pc.getId(), ((Integer) aTempData.get(22)).intValue()));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), ((Integer) aTempData.get(22)).intValue()));
				}
				
				// 額外命中
				int hitall = ((Integer) aTempData.get(23)).intValue();
				if (hitall != 0) {
					//pc.setAddHitModifier(pc.getAddHitModifier() + ((Integer) aTempData.get(23)).intValue());
					pc.addHitup(hitall);
					pc.addBowHitup(hitall);
					pc.setLvup_hit(pc.getLvup_hit() + hitall);
					pc.setLvup_fhit(pc.getLvup_fhit() + hitall);
					isSave = true;
				}
				
				// 額外傷害
				int dmgall = ((Integer) aTempData.get(24)).intValue();
				if (dmgall != 0) {
					//pc.setAddDmgModifier(pc.getAddDmgModifier() + ((Integer) aTempData.get(24)).intValue());
					pc.addDmgup(dmgall);
					pc.addBowDmgup(dmgall);
					pc.setLvup_dmg(pc.getLvup_dmg() + dmgall);
					pc.setLvup_fdmg(pc.getLvup_fdmg() + dmgall);
					isSave = true;
				}
				
				// 魔攻
				int sp = ((Integer) aTempData.get(25)).intValue();
				if (sp != 0) {
					//pc.setAddSp(pc.getAddSp() + ((Integer) aTempData.get(25)).intValue());
					pc.addSp(sp);
					pc.setLvup_sp(pc.getLvup_sp() + sp);
					isSave = true;
				}
				
				// 魔防
				int mr = ((Integer) aTempData.get(26)).intValue();
				if (mr != 0) {
					//pc.setAddAllMr(pc.getAddAllMr() + ((Integer) aTempData.get(26)).intValue());
					pc.addMr(mr);
					pc.setLvup_mr(pc.getLvup_mr() + mr);
					isSave = true;
				}
				
				// 技能
				if ((int[]) aTempData.get(27) != null) {
					int[] Skills = (int[]) aTempData.get(27);
					int time = ((Integer) aTempData.get(28)).intValue();
					for (int j = 0; j < Skills.length; j++) {
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(pc, Skills[j], pc.getId(), pc.getX(), pc.getY(), time, L1SkillUse.TYPE_GMBUFF);
					}
				}
				
				// 近戰傷害
				int dmg = ((Integer) aTempData.get(29)).intValue();
				if (dmg != 0) {
					//pc.setModifierDmg(pc.getModifierDmg() + ((Integer) aTempData.get(29)).intValue());
					pc.addDmgup(dmg);
					pc.setLvup_dmg(pc.getLvup_dmg() + dmg);
					isSave = true;
				}
				
				// 傷減
				int dmgr = ((Integer) aTempData.get(30)).intValue();
				if (dmgr != 0) {
					//pc.setReductionDmg(pc.getReductionDmg() + ((Integer) aTempData.get(30)).intValue());
					pc.add_reduction_dmg(dmgr);
					pc.setLvup_dmgr(pc.getLvup_dmgr() + dmgr);
					isSave = true;
				}
				
				// 魔傷
				int mdmg = ((Integer) aTempData.get(31)).intValue();
				if (mdmg != 0) {
					//pc.setMagicModifierDmg(pc.getMagicModifierDmg() + ((Integer) aTempData.get(31)).intValue());
					pc.add_magic_modifier_dmg(mdmg);
					pc.setLvup_mdmg(pc.getLvup_mdmg() + mdmg);
					isSave = true;
				}
				
				// 魔傷減
				int mdmgr = ((Integer) aTempData.get(32)).intValue();
				if (mdmgr != 0) {
					//pc.setMagicReductionDmg(pc.getMagicReductionDmg() + ((Integer) aTempData.get(32)).intValue());
					pc.add_magic_reduction_dmg(mdmgr);
					pc.setLvup_mdmgr(pc.getLvup_mdmgr() + mdmgr);
					isSave = true;
				}
				//
				// if (((Integer) aTempData.get(33)).intValue() != 0) {
				// pc.setKarma(pc.getKarma()
				// + ((Integer) aTempData.get(33)).intValue());
				// isSave = true;
				// }

				// if (((Integer) aTempData.get(34)).intValue() != 0) {
				// pc.setKarma(pc.getKarma()
				// + ((Integer) aTempData.get(34)).intValue());
				// isSave = true;
				// }
				//
				// if (((Integer) aTempData.get(35)).intValue() != 0) {
				// pc.setKarma(pc.getKarma()
				// + ((Integer) aTempData.get(35)).intValue());
				// isSave = true;
				// }
				break;
			}
		}
		if (isSave) {
			try {
				pc.save();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getData() {
		Connection cn = null;
		Statement ps = null;
		ResultSet rset = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.createStatement();
			rset = ps.executeQuery("SELECT * FROM william_item_use");

			while (rset.next()) {
				ArrayList<Object> aReturn = new ArrayList<Object>();

				aReturn.add(0, new Integer(rset.getInt("item_id")));
				aReturn.add(1, new Integer(rset.getInt("checkClass")));
				aReturn.add(2, new Integer(rset.getInt("checkItem")));
				aReturn.add(3, new Integer(rset.getInt("removeItem")));
				aReturn.add(4, new Integer(rset.getInt("polyId")));
				aReturn.add(5, new Integer(rset.getInt("polyTime")));
				aReturn.add(6, new Integer(rset.getInt("permanenceHp")));
				aReturn.add(7, new Integer(rset.getInt("permanenceMp")));
				aReturn.add(8, new Integer(rset.getInt("permanenceStr")));
				aReturn.add(9, new Integer(rset.getInt("permanenceDex")));
				aReturn.add(10, new Integer(rset.getInt("permanenceCon")));
				aReturn.add(11, new Integer(rset.getInt("permanenceWis")));
				aReturn.add(12, new Integer(rset.getInt("permanenceInt")));
				aReturn.add(13, new Integer(rset.getInt("permanenceCha")));
				aReturn.add(14, new Integer(rset.getInt("level")));
				aReturn.add(15, new Integer(rset.getInt("hp")));
				aReturn.add(16, new Integer(rset.getInt("mp")));
				// aReturn.add(17, new Integer(rset.getInt("Kyo")));
				// aReturn.add(18, new Integer(rset.getInt("Qigong")));
				// aReturn.add(19, new Integer(rset.getInt("Prestige")));
				aReturn.add(17, new Integer(0));
				aReturn.add(18, new Integer(0));
				aReturn.add(19, new Integer(0));
				
				aReturn.add(20, new Integer(rset.getInt("Exp")));
				aReturn.add(21, new Integer(rset.getInt("Lawful")));
				aReturn.add(22, new Integer(rset.getInt("Gfx")));
				aReturn.add(23, new Integer(rset.getInt("AddHitModifier")));
				aReturn.add(24, new Integer(rset.getInt("AddDmgModifier")));
				aReturn.add(25, new Integer(rset.getInt("AddSp")));
				aReturn.add(26, new Integer(rset.getInt("AddAllMr")));

				if ((rset.getString("Skills") != null)
						&& (!rset.getString("Skills").equals(""))
						&& (!rset.getString("Skills").equals("0")))
					aReturn.add(27,
							getArray(rset.getString("Skills"), TOKEN, 1));
				else {
					aReturn.add(27, null);
				}
				aReturn.add(28, new Integer(rset.getInt("SkillsTime")));
				aReturn.add(29, new Integer(rset.getInt("ModifierDmg")));
				aReturn.add(30, new Integer(rset.getInt("ReductionDmg")));
				aReturn.add(31, new Integer(rset.getInt("MagicModifierDmg")));
				aReturn.add(32, new Integer(rset.getInt("MagicReductionDmg")));
				// aReturn.add(33, new Integer(rset.getInt("Karma")));
				// aReturn.add(34, new Integer(rset.getInt("effect_exp")));
				// aReturn.add(35, new Integer(rset.getInt("effect_exp_time")));
				// aReturn.add(36, new Integer(rset.getInt("checkRace")));
				_array.add(aReturn);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rset);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
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