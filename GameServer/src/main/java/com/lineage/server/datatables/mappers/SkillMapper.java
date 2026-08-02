package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Skills;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Skills 模型轉換器
 */
public class SkillMapper implements RowMapper<L1Skills> {

    private static final SkillMapper _instance = new SkillMapper();

    public static SkillMapper get() {
        return _instance;
    }

    @Override
    public L1Skills mapRow(ResultSet rs) throws SQLException {
        final L1Skills skill = new L1Skills();
        
        skill.setSkillId(rs.getInt("skill_id"));
        skill.setName(rs.getString("name"));
        skill.setSkillLevel(rs.getInt("skill_level"));
        skill.setSkillNumber(rs.getInt("skill_number"));
        skill.setMpConsume(rs.getInt("mp_consume"));
        skill.setHpConsume(rs.getInt("hp_consume"));
        skill.setItemConsumeId(rs.getInt("itme_consume_id"));
        skill.setItemConsumeCount(rs.getInt("itme_consume_count"));
        skill.setReuseDelay(rs.getInt("reuse_delay"));
        skill.setBuffDuration(rs.getInt("buff_duration"));
        skill.setTarget(rs.getString("target"));
        skill.setTargetTo(rs.getInt("target_to"));
        skill.setDamageValue(rs.getInt("damage_value"));
        skill.setDamageDice(rs.getInt("damage_dice"));
        skill.setDamageDiceCount(rs.getInt("damage_dice_count"));
        skill.setProbabilityValue(rs.getInt("probability_value"));
        skill.setProbabilityDice(rs.getInt("probability_dice"));
        skill.setAttr(rs.getInt("attr"));
        skill.setType(rs.getInt("type"));
        skill.setLawful(rs.getInt("lawful"));
        skill.setRanged(rs.getInt("ranged"));
        skill.setArea(rs.getInt("area"));
        skill.setThrough(rs.getInt("is_through") != 0);
        skill.setId(rs.getInt("id"));
        skill.setNameId(rs.getString("name_id"));
        skill.setActionId(rs.getInt("action_id"));
        skill.setCastGfx(rs.getInt("castgfx"));
        skill.setCastGfx2(rs.getInt("castgfx2"));
        skill.setSysmsgIdHappen(rs.getInt("sysmsg_id_happen"));
        skill.setSysmsgIdStop(rs.getInt("sysmsg_id_stop"));
        skill.setSysmsgIdFail(rs.getInt("sysmsg_id_fail"));

        return skill;
    }
}
