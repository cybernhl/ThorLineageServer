package com.lineage.server.model.skill;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;
import static com.lineage.server.model.skill.L1SkillId.TRIPLE_ARROW;
import com.lineage.server.model.skill.skillmode.TRIPLE_ARROW;

import com.lineage.server.model.skill.skillmode.*;

public class L1SkillMode {

	private static final Log _log = LogFactory.getLog(L1SkillMode.class);
	
	// Map<K,V>
	private static final Map<Integer, SkillMode> _skillMode = new HashMap<Integer, SkillMode>();

	private static L1SkillMode _instance;

	public static L1SkillMode get() {
		if (_instance == null) {
			_instance = new L1SkillMode();
		}
		return _instance;
	}

	/**
	 * 不會被相消的技能
	 */
	public boolean isNotCancelable(final int skillNum) {
		return (skillNum == ENCHANT_WEAPON) ||
		(skillNum == BLESSED_ARMOR) ||
		(skillNum == ABSOLUTE_BARRIER) ||
		(skillNum == ADVANCE_SPIRIT) ||
		(skillNum == SHOCK_STUN) ||
		(skillNum == SHADOW_FANG) ||
		(skillNum == REDUCTION_ARMOR) ||
		(skillNum == SOLID_CARRIAGE) ||
		(skillNum == COUNTER_BARRIER) ||
		(skillNum == STATUS_CHAT_PROHIBITED) ||
		(skillNum == STATUS_CURSE_BARLOG) ||
		(skillNum == STATUS_CURSE_YAHEE) ||
		(skillNum == CKEW_LV50) ||
		(skillNum == DE_LV30);
	}

	public void load() {
		try {
			// 法師
			_skillMode.put(TELEPORT, new TELEPORT());// 指定傳送
			_skillMode.put(MASS_TELEPORT, new MASS_TELEPORT());// 集體傳送術
			_skillMode.put(HASTE, new HASTE());// 加速術
			_skillMode.put(CANCELLATION, new CANCELLATION());// 魔法相消術
			_skillMode.put(CURE_POISON, new CURE_POISON());// 解毒術
			_skillMode.put(REMOVE_CURSE, new REMOVE_CURSE());// 聖潔之光
			_skillMode.put(SUMMON_MONSTER, new SUMMON_MONSTER());// 召喚術
			_skillMode.put(RESURRECTION, new RESURRECTION());// 返生術
			_skillMode.put(GREATER_RESURRECTION, new GREATER_RESURRECTION());// 終極返生術
			_skillMode.put(ADVANCE_SPIRIT, new ADVANCE_SPIRIT());// 靈魂昇華
			_skillMode.put(CURSE_PARALYZE, new CURSE_PARALYZE());// 木乃伊的詛咒
			_skillMode.put(CURSE_PARALYZE2, new CURSE_PARALYZE());// 魔法效果:麻痺
			_skillMode.put(CURSE_BLIND, new CURSE_BLIND());// 闇盲咒術20
			_skillMode.put(DARKNESS, new CURSE_BLIND());// 黑闇之影40
			
			// 王族
			_skillMode.put(CALL_CLAN, new CALL_CLAN());// 呼喚盟友
			_skillMode.put(RUN_CLAN, new RUN_CLAN());// 援護盟友
			_skillMode.put(TRUE_TARGET, new TRUE_TARGET());// 精準目標
			
			// 騎士
			_skillMode.put(SHOCK_STUN, new SHOCK_STUN());// 衝擊之暈
			_skillMode.put(SOLID_CARRIAGE, new SOLID_CARRIAGE());// 堅固防護
			
			// 精靈
			_skillMode.put(CALL_OF_NATURE, new CALL_OF_NATURE());// 生命呼喚
			_skillMode.put(ELEMENTAL_FALL_DOWN, new ELEMENTAL_FALL_DOWN());// 弱化屬性
			_skillMode.put(BODY_TO_MIND, new BODY_TO_MIND());// 心靈轉換
			_skillMode.put(BLOODY_SOUL, new BLOODY_SOUL());// 魂體轉換
			_skillMode.put(TRIPLE_ARROW, new TRIPLE_ARROW());// 三重矢
			_skillMode.put(TELEPORT_TO_MATHER, new TELEPORT_TO_MATHER());// 世界樹的呼喚
			_skillMode.put(AQUA_PROTECTER, new AQUA_PROTECTER());// 水之防護
			_skillMode.put(GREATER_ELEMENTAL, new GREATER_ELEMENTAL());// 召喚強力屬性精靈
			_skillMode.put(LESSER_ELEMENTAL, new LESSER_ELEMENTAL());// 召喚屬性精靈
			_skillMode.put(WIND_SHACKLE, new WIND_SHACKLE());// 風之枷鎖
			
			// 黑暗精靈
			_skillMode.put(UNCANNY_DODGE, new UNCANNY_DODGE());// 暗影閃避106
			_skillMode.put(DARK_BLIND, new CURSE_BLIND());// 暗黑盲咒103
			// 魔法效果
			_skillMode.put(STATUS_FREEZE, new STATUS_FREEZE());
			
			// 道具效果
			_skillMode.put(DRAGON1, new DRAGON1());// 火 額外攻擊點+2，持續1200秒
			_skillMode.put(DRAGON2, new DRAGON2());// 地 攻擊迴避提升 石化耐性+3，持續1200秒
			_skillMode.put(DRAGON3, new DRAGON3());// 水 魔法傷害減免 寒冰耐心+3，持續1200秒
			_skillMode.put(DRAGON4, new DRAGON4());// 風 魔法重擊增加 睡眠耐性+3，持續1200秒
			_skillMode.put(DRAGON5, new DRAGON5());// 生命-物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 額外攻擊點數+2 防護中毒狀態
			_skillMode.put(DRAGON6, new DRAGON6());// 誕生-物理攻擊迴避率+10% 魔法傷害減免+50 暗黑耐性+3
			_skillMode.put(DRAGON7, new DRAGON7());// 形象-物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 支撐耐性+3
			
			// NPC 特殊效果
			_skillMode.put(ADLV80_1, new ADLV80_1());// 卡瑞的祝福(地龍副本)
			_skillMode.put(ADLV80_2, new ADLV80_2());// 莎爾的祝福(水龍副本)
			_skillMode.put(AGLV85_1X, new AGLV85_1X());// 莎爾的祝福(水龍副本-強化版)
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
	}
	
	public SkillMode getSkill(final int skillid) {
		return _skillMode.get(skillid);
	}
}
