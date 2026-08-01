package com.lineage.server.model.skillUse;

import static com.lineage.server.model.skill.L1SkillId.*;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;

/**
 * 技能相關使用條件/限制
 * @author daien
 *
 */
public class L1SkillUseMode {

	/**
	 * 該技能所需耗用的HP/MP/材料/正義質
	 * @param user 執行者
	 * @param skill 技能資料
	 * @return
	 */
	public boolean isConsume(final L1Character user, L1Skills skill) {
		int mpConsume = skill.getMpConsume();// 取回:技能耗用的MP
		int hpConsume = skill.getHpConsume();// 取回:技能耗用的HP
		
		final int itemConsume = skill.getItemConsumeId();// 取回:所需耗用材料
		final int itemConsumeCount = skill.getItemConsumeCount();// 取回:所需耗用材料數量
		
		final int lawful = skill.getLawful();// 取回:所需耗用正義質
		
		final int skillId = skill.getSkillId();// 取回:技能編號
		
		int currentMp = 0;// 執行者目前的MP
		int currentHp = 0;// 執行者目前的HP
		
		L1NpcInstance useNpc = null;
		L1PcInstance usePc = null;

		// 執行者是NPC
		if (user instanceof L1NpcInstance) {
			useNpc = (L1NpcInstance) user;
			currentMp = useNpc.getCurrentMp();
			currentHp = useNpc.getCurrentHp();

			boolean isStop = false;

			// 下列狀態無法使用魔法(魔法封印)
			if (useNpc.hasSkillEffect(SILENCE)) {
				isStop = true;
			}

			// 下列狀態無法使用魔法(封印禁地)
			if (useNpc.hasSkillEffect(AREA_OF_SILENCE) && !isStop) {
				isStop = true;
			}

			// 下列狀態無法使用魔法(沈默毒素效果)
			if (useNpc.hasSkillEffect(STATUS_POISON_SILENCE) && !isStop) {
				isStop = true;
			}
			
			if (isStop) {
				return false;
			}
		}
		
		// 執行者是PC
		if (user instanceof L1PcInstance) {
			usePc = (L1PcInstance) user;
			
			currentMp = usePc.getCurrentMp();
			currentHp = usePc.getCurrentHp();
			
			// 智力 對應 技能等級 降低MP損耗
			if ((usePc.getInt() > 12) && (skillId > HOLY_WEAPON)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV2以上
				mpConsume--;
			}
			if ((usePc.getInt() > 13) && (skillId > STALAC)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV3以上
				mpConsume--;
			}
			if ((usePc.getInt() > 14) && (skillId > WEAK_ELEMENTAL)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV4以上
				mpConsume--;
			}
			if ((usePc.getInt() > 15) && (skillId > MEDITATION)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV5以上
				mpConsume--;
			}
			if ((usePc.getInt() > 16) && (skillId > DARKNESS)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV6以上
				mpConsume--;
			}
			if ((usePc.getInt() > 17) && (skillId > BLESS_WEAPON)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV7以上
				mpConsume--;
			}
			if ((usePc.getInt() > 18) && (skillId > DISEASE)
					&& (skillId <= FREEZING_BLIZZARD)) { // LV8以上
				mpConsume--;
			}

			if ((usePc.getInt() > 12) && (skillId >= SHOCK_STUN)
					&& (skillId <= COUNTER_BARRIER)) {
				mpConsume -= (usePc.getInt() - 12);
			}

			// 裝備減低MP損耗
			switch (skillId) {
			case PHYSICAL_ENCHANT_DEX:// 通暢氣脈術
				if (usePc.getInventory().checkEquipped(20013)) {// 敏捷魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320013)) {// 敏捷魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case HASTE:// 加速術
				if (usePc.getInventory().checkEquipped(20013)) {// 敏捷魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320013)) {// 敏捷魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(20008)) {// 小型風之頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case HEAL:// 初級治癒術
				if (usePc.getInventory().checkEquipped(20014)) {// 治癒魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320014)) {// 治癒魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case EXTRA_HEAL:// 中級治癒術
				if (usePc.getInventory().checkEquipped(20014)) {// 治癒魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320014)) {// 治癒魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case ENCHANT_WEAPON:// 擬似魔法武器
				if (usePc.getInventory().checkEquipped(20015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case DETECTION:// 無所遁形術
				if (usePc.getInventory().checkEquipped(20015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
				
			case PHYSICAL_ENCHANT_STR:// 體魄強健術
				if (usePc.getInventory().checkEquipped(20015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				if (usePc.getInventory().checkEquipped(320015)) {// 力量魔法頭盔
					mpConsume = mpConsume >> 1;
				}
				break;

			case GREATER_HASTE:// 強力加速術
				if (usePc.getInventory().checkEquipped(20023)) {// 風之頭盔
					mpConsume = mpConsume >> 1;
				}
				break;
			}

			// 智力 對應 降低MP損耗
			if (usePc.getOriginalMagicConsumeReduction() > 0) {
				mpConsume -= usePc.getOriginalMagicConsumeReduction();
			}

			if (0 < skill.getMpConsume()) { // 該技能MP耗用大於0
				mpConsume = Math.max(mpConsume, 1); // 最低耗用1
			}
			
			// 精靈的限制
			if (usePc.isElf()) {
				boolean isError = false;
				String msg = null;
				if ((skill.getSkillLevel() >= 17) && (skill.getSkillLevel() <= 22)) {
					final int magicattr = skill.getAttr();// 取回:技能屬性
					switch (magicattr) {
					case 1:// 地
						if (magicattr != usePc.getElfAttr()) {
							isError = true;
							msg = "$1062";
						}
						break;
						
					case 2:// 火
						if (magicattr != usePc.getElfAttr()) {
							isError = true;
							msg = "$1059";
						}
						break;
						
					case 4:// 水
						if (magicattr != usePc.getElfAttr()) {
							isError = true;
							msg = "$1060";
						}
						break;
						
					case 8:// 風
						if (magicattr != usePc.getElfAttr()) {
							isError = true;
							msg = "$1061";
						}
						break;
					}
					if ((skillId == ELEMENTAL_PROTECTION) && (usePc.getElfAttr() == 0)) {
						// 280 \f1施咒失敗。
						usePc.sendPackets(new S_ServerMessage(280));
						return false;
					}
				}
				if (isError) {
					if (!usePc.isGm()) {
						//1059火屬性
						//1060水屬性
						//1061風屬性
						//1062地屬性
						// 352 若要使用這個法術，屬性必須成為 %0。
						usePc.sendPackets(new S_ServerMessage(1385, msg));
						return false;
					}
				}
			}
			
			// 法師的限制
			if (usePc.isWizard()) {
				// 究極光裂術
				if ((skillId == DISINTEGRATE) && (usePc.getLawful() < 500)) {
					// 352 若要使用這個法術，屬性必須成為 %0。
					usePc.sendPackets(new S_ServerMessage(352, "$967"));
					return false;
				}
			}

			// 黑暗精靈的限制
			if (usePc.isDarkelf()) {
				if (skillId == FINAL_BURN) {
					hpConsume = currentHp - 1;
				}
			}
			
			// 材料判定
			if (itemConsume != 0) {
				if (!usePc.getInventory().checkItem(itemConsume, itemConsumeCount)) {
					if (!usePc.isGm()) {
						// 299 \f1施放魔法所需材料不足。
						usePc.sendPackets(new S_ServerMessage(299));
						return false;
					}
				}
			}
		}

		// 體力不足
		if (currentHp < hpConsume + 1) {
			if (usePc != null) {
				// 279 \f1因體力不足而無法使用魔法。
				usePc.sendPackets(new S_ServerMessage(279));
			}
			return false;
		}
		
		// 魔力不足
		if (currentMp < mpConsume) {
			if (usePc != null) {
				// 278 \f1因魔力不足而無法使用魔法。
				usePc.sendPackets(new S_ServerMessage(278));
				// 執行者是GM 恢復魔力
				if (usePc.isGm()) {
					usePc.setCurrentMp(usePc.getMaxMp());
				}
			}
			return false;
		}
		
		if (usePc != null) {
			// 正義質增減
			if (lawful != 0) {
				int newLawful = usePc.getLawful() + lawful;
				if (newLawful > 32767) {
					newLawful = 32767;
				}
				if (newLawful < -32767) {
					newLawful = -32767;
				}
				usePc.setLawful(newLawful);
			}
			
			if (itemConsume != 0) {
				// 材料的消耗
				usePc.getInventory().consumeItem(itemConsume, itemConsumeCount);
			}
		}
		
		final int current_hp = user.getCurrentHp() - hpConsume;
		user.setCurrentHp(current_hp);

		final int current_mp = user.getCurrentMp() - mpConsume;
		user.setCurrentMp(current_mp);
		
		return true;
	}
}
