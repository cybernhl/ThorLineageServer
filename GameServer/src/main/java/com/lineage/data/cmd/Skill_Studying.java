package com.lineage.data.cmd;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;

/**
 * 技能學習成功與否的判斷
 * @author dexc
 *
 */
public class Skill_Studying implements Skill_StudyingExecutor {

	/**
	 * 學習技能等級限制 與地圖位置判斷
	 * @param pc 人物
	 * @param skillId 技能編號
	 * @param magicAtt 技能等級分組<BR>
	 * 					1~10共同魔法<BR>
	 * 					11~20精靈魔法<BR>
	 * 					21~30王族魔法<BR>
	 * 					31~40騎士魔法<BR>
	 * 					41~50黑暗精靈魔法<BR>
	 *
	 * @param attribute 技能屬性<BR>
	 * 					0:中立屬性魔法<BR>
	 * 					1:正義屬性魔法<BR>
	 * 					2:邪惡屬性魔法<BR>
	 * 					3:精靈專屬魔法<BR>
	 * 					4:王族專屬魔法<BR>
	 * 					5:騎士專屬技能<BR>
	 * 					6:黑暗精靈專屬魔法<BR>
	 *
	 * @param itemObj 道具objid(點選的物品)
	 */
	@Override
	public void magic(final L1PcInstance pc, final int skillId, final int magicAtt, final int attribute, final int itemObj) {
		// 人物等級
		final int pclvl = pc.getLevel();
		// 是否足夠等級學習
		boolean isUse = true;

		// TODO 王族
		if (pc.isCrown()) {
			switch(magicAtt){
			case 1:
				if (pclvl >= 10) {// magic lv1
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 2:
				if (pclvl >= 20) {// magic lv2
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 21:
				if (pclvl >= 15) {// Crown magic lv1
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 22:
				if (pclvl >= 30) {// Crown magic lv2
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 23:
				if (pclvl >= 40) {// Crown magic lv3
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 24:
				if (pclvl >= 45) {// Crown magic lv4
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 25:
				if (pclvl >= 50) {// Crown magic lv5
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 26:
				if (pclvl >= 55) {// Crown magic lv6
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			default:
				// 79 沒有任何事情發生
				final S_ServerMessage msg = new S_ServerMessage(79);
				pc.sendPackets(msg);
				break;
			}

			// TODO 騎士
		} else if (pc.isKnight()) {
			switch(magicAtt){
			case 1:
				if (pclvl >= 50) {// magic lv1
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 31:
				if (pclvl >= 50) {// Knight magic lv1
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 32:
				if (pclvl >= 60) {// Knight magic lv2
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			default:
				// 79 沒有任何事情發生
				final S_ServerMessage msg = new S_ServerMessage(79);
				pc.sendPackets(msg);
				break;
			}

			// TODO 法師
		} else if (pc.isWizard()) {
			switch(magicAtt){
			case 1:// magic lv1
				if (pclvl >= 4) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 2:// magic lv2
				if (pclvl >= 8) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 3:// magic lv3
				if (pclvl >= 12) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 4:// magic lv4
				if (pclvl >= 16) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 5:// magic lv5
				if (pclvl >= 20) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 6:// magic lv6
				if (pclvl >= 24) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 7:// magic lv7
				if (pclvl >= 28) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 8:// magic lv8
				if (pclvl >= 32) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 9:// magic lv9
				if (pclvl >= 36) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 10:// magic lv10
				if (pclvl >= 40) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			default:
				// 79 沒有任何事情發生
				final S_ServerMessage msg = new S_ServerMessage(79);
				pc.sendPackets(msg);
				break;
			}

			// TODO 精靈
		} else if (pc.isElf()) {
			switch(magicAtt){
			case 1:// magic lv1
				if (pclvl >= 8) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 2:// magic lv2
				if (pclvl >= 16) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 3:// magic lv3
				if (pclvl >= 24) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 4:// magic lv4
				if (pclvl >= 32) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 5:// magic lv5
				if (pclvl >= 40) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 6:// magic lv6
				if (pclvl >= 48) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 11:// elf magic lv1
				if (pclvl >= 10) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 12:// elf magic lv2
				if (pclvl >= 20) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 13:// elf magic lv3
				if (pclvl >= 30) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 14:// elf magic lv4
				if (pclvl >= 40) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 15:// elf magic lv5
				if (pclvl >= 50) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			default:
				// 79 沒有任何事情發生
				final S_ServerMessage msg = new S_ServerMessage(79);
				pc.sendPackets(msg);
				break;
			}

			// TODO 黑暗精靈
		} else if (pc.isDarkelf()) {
			switch(magicAtt){
			case 1:// magic lv1
				if (pclvl >= 12) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 2:// magic lv2
				if (pclvl >= 24) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 41:// Darkelf magic lv1
				if (pclvl >= 15) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 42:// Darkelf magic lv2
				if (pclvl >= 30) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			case 43:// Darkelf magic lv3
				if (pclvl >= 45) {
					this.mapPosition(pc, skillId, attribute, itemObj);

				} else {
					isUse = false;
				}
				break;

			default:
				// 79 沒有任何事情發生
				final S_ServerMessage msg = new S_ServerMessage(79);
				pc.sendPackets(msg);
				break;
			}

		}

		if (!isUse) {
			// 312 你還不能學習法術。
			final S_ServerMessage msg = new S_ServerMessage(312);
			pc.sendPackets(msg);
		}
	}

	/**
	 * 學習法術 人物位置判斷
	 * @param pc 人物
	 * @param skillId 技能編號
	 * @param attribute 技能屬性
	 * 					0:中立屬性魔法<BR>
	 * 					1:正義屬性魔法<BR>
	 * 					2:邪惡屬性魔法<BR>
	 * 					3:精靈專屬魔法<BR>
	 * 					4:王族專屬魔法<BR>
	 * 					5:騎士專屬技能<BR>
	 * 					6:黑暗精靈專屬魔法<BR>
	 *
	 * @param itemObj 道具objid(點選的物品)
	 */
	private void mapPosition(final L1PcInstance pc, final int skillId, final int attribute, final int itemObj) {
		// 要求使用正確學習點
		final boolean isOk = true;

		if (isOk) {
			final int x = pc.getX();
			final int y = pc.getY();
			final int m = pc.getMapId();
			switch(attribute){
			case 0:// 中立屬性魔法
				if(((x > 32880) && (x < 32892) && (y > 32646) && (y < 32658) && (m == 4 // 邪惡神殿(然柳)
				)
				)
				|| ((x > 32662) && (x < 32674) && (y > 32297) && (y < 32309) && (m == 4 // 邪惡神殿(然柳)
				)
				)
				|| ((x > 33135) && (x < 33146) && (y > 32232) && (y < 32249) && (m == 4 // 正義神殿(妖森)
				)
				)
				|| ((x > 33116) && (x < 33128) && (y > 32930) && (y < 32942) && (m == 4 // 正義神殿(肯特)
				)
				)
				|| ((x > 32791) && (x < 32796) && (y > 32842) && (y < 32848) && (m == 76))) {// 正義神殿(象牙塔)

					this.spellBook(pc, skillId, itemObj);

				} else {
					// 79 沒有任何事情發生
					final S_ServerMessage msg = new S_ServerMessage(79);
					pc.sendPackets(msg);
				}
				break;

			case 1:// 正義屬性魔法
				if(((x > 33116) && (x < 33128) && (y > 32930) && (y < 32942) && (m == 4 // 正義神殿(肯特)
				)
				)
				|| ((x > 33135) && (x < 33146) && (y > 32232) && (y < 32249) && (m == 4 // 正義神殿(妖森)
				)
				)
				|| ((x > 32791) && (x < 32796) && (y > 32842) && (y < 32848) && (m == 76))) {// 正義神殿(象牙塔)

					this.spellBook(pc, skillId, itemObj);

				} else if(((x > 32880) && (x < 32892) && (y > 32646) && (y < 32658) && (m == 4 // 邪惡神殿(然柳)
				)
				)
				|| ((x > 32662) && (x < 32674) && (y > 32297) && (y < 32309) && (m == 4))) {// 邪惡神殿(然柳)

					// 刪除道具(錯誤地點學習)
					pc.getInventory().removeItem(pc.getInventory().getItem(itemObj), 1);

					// 隨機數字範圍(傷害力)
					final short dmg = (short) ((Math.random() * 50) + 30);

					// HP減少計算
					pc.receiveDamage(pc, dmg, false, true);

					if (pc.isInvisble()) {// 隱身狀態
						pc.delInvis(); // 解除隱身狀態
					}

					// 產生動畫封包(電擊)
					final S_SkillSound sound = new S_SkillSound(pc.getId(), 10);
					pc.sendPacketsX8(sound);

					// 產生動作封包(招受攻擊)
					final S_DoActionGFX pack = new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage);
					pc.sendPacketsX8(pack);

					// HP 更新
					final S_HPUpdate newHp = new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp());
					pc.sendPackets(newHp);
					if (pc.isInParty()) { // 隊伍中
						pc.getParty().updateMiniHP(pc);// 更新隊伍畫面顯示
					}

				} else {
					// 79 沒有任何事情發生
					final S_ServerMessage msg = new S_ServerMessage(79);
					pc.sendPackets(msg);
				}
				break;

			case 2:// 邪惡屬性魔法
				if(((x > 32880) && (x < 32892) && (y > 32646) && (y < 32658) && (m == 4 // 邪惡神殿(然柳)
				)
				)
				|| ((x > 32662) && (x < 32674) && (y > 32297) && (y < 32309) && (m == 4))) {// 邪惡神殿(然柳)

					this.spellBook(pc, skillId, itemObj);

				} else if(((x > 33116) && (x < 33128) && (y > 32930) && (y < 32942) && (m == 4 // 正義神殿(肯特)
				)
				)
				|| ((x > 33135) && (x < 33146) && (y > 32232) && (y < 32249) && (m == 4 // 正義神殿(妖森)
				)
				)
				|| ((x > 32791) && (x < 32796) && (y > 32842) && (y < 32848) && (m == 76))) {// 正義神殿(象牙塔2F)

					// 刪除道具(錯誤地點學習)
					pc.getInventory().removeItem(pc.getInventory().getItem(itemObj), 1);

					// 隨機數字範圍(傷害力)
					final short dmg = (short) ((Math.random() * 50) + 30);

					// HP減少計算
					pc.receiveDamage(pc, dmg, false, true);

					if (pc.isInvisble()) {// 隱身狀態
						pc.delInvis(); // 解除隱身狀態
					}

					// 產生動畫封包(電擊)
					final S_SkillSound sound = new S_SkillSound(pc.getId(), 10);
					pc.sendPacketsX8(sound);

					// 產生動作封包(招受攻擊)
					final S_DoActionGFX pack = new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage);
					pc.sendPacketsX8(pack);

					// HP 更新
					final S_HPUpdate newHp = new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp());
					pc.sendPackets(newHp);
					if (pc.isInParty()) { // 隊伍中
						pc.getParty().updateMiniHP(pc);// 更新隊伍畫面顯示
					}

				} else {
					// 79 沒有任何事情發生
					final S_ServerMessage msg = new S_ServerMessage(79);
					pc.sendPackets(msg);
				}
				break;

			case 3:// 精靈專屬魔法
				if(((x > 33049) && (x < 33061) && (y > 32330) && (y < 32343) && (m == 4 // 世界樹
				)
				)
				|| ((x > 32788) && (x < 32794) && (y > 32847) && (y < 32853) && (m == 75))) {// 象牙塔1F

					this.spellBook(pc, skillId, itemObj);

				} else {
					// 79 沒有任何事情發生
					final S_ServerMessage msg = new S_ServerMessage(79);
					pc.sendPackets(msg);
				}
				break;

			case 4:// 王族專屬魔法
			case 5:// 騎士專屬技能
			case 6:// 黑暗精靈專屬魔法
				this.spellBook(pc, skillId, itemObj);
				break;
			}

		} else {
			this.spellBook(pc, skillId, itemObj);
		}
	}

	/**
	 * 人物技能寫入
	 * @param pc 使用物品的人物
	 * @param skillId 技能編號
	 * @param itemObj 點選的物品 objectId
	 *
	 * @return
	 */
	private void spellBook(final L1PcInstance pc, final int skillId, final int itemObj) {
		// 刪除道具
		pc.getInventory().removeItem(pc.getInventory().getItem(itemObj), 1);

		// 更新技能畫面顯示
		pc.sendPackets(new S_AddSkill(pc, skillId));

		// 動畫效果
		final char c = '\343';
		final S_SkillSound sound = new S_SkillSound(pc.getId(), c);

		pc.sendPacketsX8(sound);

		// 取得魔法資料
		final L1Skills skill = SkillsTable.get().getTemplate(skillId);
		// 取得技能名稱
		final String skillName = skill.getName();

		// 寫入人物技能資料庫
		CharSkillReading.get().spellMastery(pc.getId(), skillId, skillName, 0, 0);
	}
}
