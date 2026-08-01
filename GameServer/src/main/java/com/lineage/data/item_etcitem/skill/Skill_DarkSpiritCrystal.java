package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>黑暗精靈水晶(黑暗精靈魔法)</font><BR>
 * Dark Spirit Crystal
 * @author dexc
 *
 */
public class Skill_DarkSpiritCrystal extends ItemExecutor {

	/**
	 *
	 */
	private Skill_DarkSpiritCrystal() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_DarkSpiritCrystal();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 不是黑暗精靈
		if (!pc.isDarkelf()) {
			// 79 沒有任何事情發生
			final S_ServerMessage msg = new S_ServerMessage(79);
			pc.sendPackets(msg);

		} else {
			// 取得名稱
			final String nameId = item.getItem().getNameId();
			// 技能編號
			int skillid = 0;
			// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
			// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
			final int attribute = 6;
			// 分組
			int magicLv = 0;

			if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗隱術)")) {// 黑暗精靈水晶(暗隱術)
				// 技能編號
				skillid = BLIND_HIDING;
				// 分組
				magicLv = 41;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(附加劇毒)")) {// 黑暗精靈水晶(附加劇毒)
				// 技能編號
				skillid = ENCHANT_VENOM;
				// 分組
				magicLv = 41;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(影之防護)")) {// 黑暗精靈水晶(影之防護)
				// 技能編號
				skillid = SHADOW_ARMOR;
				// 分組
				magicLv = 41;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(提煉魔石)")) {// 黑暗精靈水晶(提煉魔石)
				// 技能編號
				skillid = BRING_STONE;
				// 分組
				magicLv = 41;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(力量提升)")) {// 黑暗精靈水晶(力量提升)
				// 技能編號
				skillid = DRESS_MIGHTY;
				// 分組
				magicLv = 41;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(行走加速)")) {// 黑暗精靈水晶(行走加速)
				// 技能編號
				skillid = MOVING_ACCELERATION;
				// 分組
				magicLv = 42;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(燃燒鬥志)")) {// 黑暗精靈水晶(燃燒鬥志)
				// 技能編號
				skillid = BURNING_SPIRIT;
				// 分組
				magicLv = 42;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗黑盲咒)")) {// 黑暗精靈水晶(暗黑盲咒)
				// 技能編號
				skillid = DARK_BLIND;
				// 分組
				magicLv = 42;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(毒性抵抗)")) {// 黑暗精靈水晶(毒性抵抗)
				// 技能編號
				skillid = VENOM_RESIST;
				// 分組
				magicLv = 42;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(敏捷提升)")) {// 黑暗精靈水晶(敏捷提升)
				// 技能編號
				skillid = DRESS_DEXTERITY;
				// 分組
				magicLv = 42;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(雙重破壞)")) {// 黑暗精靈水晶(雙重破壞)
				// 技能編號
				skillid = DOUBLE_BRAKE;
				// 分組
				magicLv = 43;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗影閃避)")) {// 黑暗精靈水晶(暗影閃避)
				// 技能編號
				skillid = UNCANNY_DODGE;
				// 分組
				magicLv = 43;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(暗影之牙)")) {// 黑暗精靈水晶(暗影之牙)
				// 技能編號
				skillid = SHADOW_FANG;
				// 分組
				magicLv = 43;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(會心一擊)")) {// 黑暗精靈水晶(會心一擊)
				// 技能編號
				skillid = FINAL_BURN;
				// 分組
				magicLv = 43;

			} else if (nameId.equalsIgnoreCase("黑暗精靈水晶(閃避提升)")) {// 黑暗精靈水晶(閃避提升)
				// 技能編號
				skillid = DRESS_EVASION;
				// 分組
				magicLv = 43;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
