package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 技能設置資料
 *
 * @author dexc
 *
 */
public class SkillsTable {

	private static final Log _log = LogFactory.getLog(SkillsTable.class);

	private static SkillsTable _instance;

	private static final Map<Integer, L1Skills> _skills = new HashMap<Integer, L1Skills>();
	private static final Map<Integer, String> skillItemNames = new HashMap<>();

	public static SkillsTable get() {
		if (_instance == null) {
			_instance = new SkillsTable();
		}
		return _instance;
	}
	public final String getSkillItemName(final int skillId) {
		if (!skillItemNames.containsKey(skillId)) {
			return "";
		}
		return skillItemNames.get(skillId);
	}

	public void load() {
		skillItemNames.put(L1SkillId.EXP11, "經驗加倍10%效果");
		skillItemNames.put(L1SkillId.EXP12, "經驗加倍20%效果");
		skillItemNames.put(L1SkillId.EXP13, "經驗加倍30%效果");
		skillItemNames.put(L1SkillId.EXP15, "經驗加倍50%效果");
		skillItemNames.put(L1SkillId.EXP17, "經驗加倍70%效果");
		skillItemNames.put(L1SkillId.EXP20, "經驗加倍120%效果");
		skillItemNames.put(L1SkillId.EXP25, "經驗加倍150%效果");
		skillItemNames.put(L1SkillId.STATUS_HASTE, "加速效果");
		skillItemNames.put(L1SkillId.STATUS_BRAVE, "勇敢藥水效果");
		skillItemNames.put(L1SkillId.STATUS_BLUE_POTION, "魔力回復藥水效果");
		skillItemNames.put(L1SkillId.STATUS_UNDERWATER_BREATH, "伊娃的祝福藥水效果");
		skillItemNames.put(L1SkillId.STATUS_WISDOM_POTION, "慎重藥水效果");
		skillItemNames.put(L1SkillId.STATUS_HOLY_WATER, "聖水效果");
		skillItemNames.put(L1SkillId.STATUS_POISON, "毒素效果");
		skillItemNames.put(L1SkillId.STATUS_POISON_SILENCE, "沈默毒素效果");
		skillItemNames.put(L1SkillId.STATUS_POISON_PARALYZING, "麻痺毒素效果");
		skillItemNames.put(L1SkillId.STATUS_CURSE_PARALYZING, "詛咒型麻痺效果");
		skillItemNames.put(L1SkillId.STATUS_FLOATING_EYE, "漂浮之眼肉效果");
		skillItemNames.put(L1SkillId.STATUS_HOLY_MITHRIL_POWDER, "神聖的米索莉粉效果");
		skillItemNames.put(L1SkillId.STATUS_HOLY_WATER_OF_EVA, "伊娃的聖水效果");
		skillItemNames.put(L1SkillId.STATUS_ELFBRAVE, "精靈餅乾效果");
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `skills`");
			rs = pstm.executeQuery();
			this.fillSkillsTable(rs);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入技能設置資料數量: " + _skills.size() + "(" + timer.get() + "ms)");
	}

	private void fillSkillsTable(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			final L1Skills l1skills = new L1Skills();
			final int skill_id = rs.getInt("skill_id");
			l1skills.setSkillId(skill_id);
			l1skills.setName(rs.getString("name"));
			l1skills.setSkillLevel(rs.getInt("skill_level"));
			l1skills.setSkillNumber(rs.getInt("skill_number"));
			l1skills.setMpConsume(rs.getInt("mpConsume"));
			l1skills.setHpConsume(rs.getInt("hpConsume"));
			l1skills.setItemConsumeId(rs.getInt("itemConsumeId"));
			l1skills.setItemConsumeCount(rs.getInt("itemConsumeCount"));
			l1skills.setReuseDelay(rs.getInt("reuseDelay"));
			l1skills.setBuffDuration(rs.getInt("buffDuration"));
			l1skills.setTarget(rs.getString("target"));
			l1skills.setTargetTo(rs.getInt("target_to"));
			l1skills.setDamageValue(rs.getInt("damage_value"));
			l1skills.setDamageDice(rs.getInt("damage_dice"));
			l1skills.setDamageDiceCount(rs.getInt("damage_dice_count"));
			l1skills.setProbabilityValue(rs.getInt("probability_value"));
			l1skills.setProbabilityDice(rs.getInt("probability_dice"));
			l1skills.setAttr(rs.getInt("attr"));
			l1skills.setType(rs.getInt("type"));
			l1skills.setLawful(rs.getInt("lawful"));
			l1skills.setRanged(rs.getInt("ranged"));
			l1skills.setArea(rs.getInt("area"));
			l1skills.setThrough(rs.getBoolean("through"));
			l1skills.setId(rs.getInt("id"));
			l1skills.setNameId(rs.getString("nameid"));
			l1skills.setActionId(rs.getInt("action_id"));
			l1skills.setCastGfx(rs.getInt("castgfx"));
			l1skills.setCastGfx2(rs.getInt("castgfx2"));
			l1skills.setSysmsgIdHappen(rs.getInt("sysmsgID_happen"));
			l1skills.setSysmsgIdStop(rs.getInt("sysmsgID_stop"));
			l1skills.setSysmsgIdFail(rs.getInt("sysmsgID_fail"));

			_skills.put(new Integer(skill_id), l1skills);
		}
	}
	public void spellMastery(int playerobjid, int skillid, String skillname,
			int active, int time) {
		if (spellCheck(playerobjid, skillid)) {
			return;
		}
		L1PcInstance pc = (L1PcInstance) World.get().findObject(playerobjid);
		if (pc != null) {
			pc.setSkillMastery(skillid);
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_skills SET char_obj_id=?, skill_id=?, skill_name=?, is_active=?, activetimeleft=?");
			pstm.setInt(1, playerobjid);
			pstm.setInt(2, skillid);
			pstm.setString(3, skillname);
			pstm.setInt(4, active);
			pstm.setInt(5, time);
			pstm.execute();
		} catch (Exception e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	public boolean spellCheck(int playerobjid, int skillid) {
		boolean ret = false;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_skills WHERE char_obj_id=? AND skill_id=?");
			pstm.setInt(1, playerobjid);
			pstm.setInt(2, skillid);
			rs = pstm.executeQuery();
			if (rs.next()) {
				ret = true;
			} else {
				ret = false;
			}
		} catch (Exception e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return ret;
	}
	public L1Skills getTemplate(final int i) {
		return _skills.get(new Integer(i));
	}

}
