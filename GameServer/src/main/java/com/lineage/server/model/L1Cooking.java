package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_S;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 料理
 * @author daien
 *
 */
public class L1Cooking {

	private static final Log _log = LogFactory.getLog(L1Cooking.class);

	private L1Cooking() {
	}

	public static void useCookingItem(final L1PcInstance pc, final L1ItemInstance item) {
		final int itemId = item.getItem().getItemId();
		switch (itemId) {
		case 41284: // 蘑菇湯
		case 41292: // 特別的 蘑菇湯
		case 49056: // 蟹肉湯
		case 49064: // 特別的蟹肉湯
		case 49251: // 邪惡蜥蜴蛋湯
		case 49259: // 特別的邪惡蜥蜴蛋湯
			// 飽食度未滿無法使用
			if (pc.get_food() != 225) {
				// 74 \f1%0%o 無法使用。
				pc.sendPackets(new S_ServerMessage(74, item.getNumberedName(1, true)));
				return;
			}
			break;
		}
		if (((itemId >= 41277) && (itemId <= 41283 // Lv1料理
		)
		)
		|| ((itemId >= 41285) && (itemId <= 41291 // Lv1幻想料理
		)
		)
		|| ((itemId >= 49049) && (itemId <= 49055 // Lv2料理
		)
		)
		|| ((itemId >= 49057) && (itemId <= 49063 // Lv2幻想料理
		)
		)
		|| ((itemId >= 49244) && (itemId <= 49250 // Lv3料理
		)
		)
		|| ((itemId >= 49252) && (itemId <= 49258))) { // Lv3幻想料理
			final int cookingId = pc.getCookingId();
			if (cookingId != 0) {
				pc.removeSkillEffect(cookingId);
			}
		}

		if ((itemId == 41284) || (itemId == 41292) || (itemId == 49056)
				|| (itemId == 49064) || (itemId == 49251) || (itemId == 49259)) { // 
			final int dessertId = pc.getDessertId();
			if (dessertId != 0) {
				pc.removeSkillEffect(dessertId);
			}
		}

		int cookingId;
		final int time = 900;
		if ((itemId == 41277) || (itemId == 41285)) { // 
			if (itemId == 41277) {
				cookingId = COOKING_1_0_N;
			} else {
				cookingId = COOKING_1_0_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41278) || (itemId == 41286)) { // 
			if (itemId == 41278) {
				cookingId = COOKING_1_1_N;
			} else {
				cookingId = COOKING_1_1_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41279) || (itemId == 41287)) { // 餅
			if (itemId == 41279) {
				cookingId = COOKING_1_2_N;
			} else {
				cookingId = COOKING_1_2_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41280) || (itemId == 41288)) { // 蟻腳燒
			if (itemId == 41280) {
				cookingId = COOKING_1_3_N;
			} else {
				cookingId = COOKING_1_3_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41281) || (itemId == 41289)) { // 
			if (itemId == 41281) {
				cookingId = COOKING_1_4_N;
			} else {
				cookingId = COOKING_1_4_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41282) || (itemId == 41290)) { // 甘酢
			if (itemId == 41282) {
				cookingId = COOKING_1_5_N;
			} else {
				cookingId = COOKING_1_5_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41283) || (itemId == 41291)) { // 豬肉串燒
			if (itemId == 41283) {
				cookingId = COOKING_1_6_N;
			} else {
				cookingId = COOKING_1_6_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 41284) || (itemId == 41292)) { // 
			if (itemId == 41284) {
				cookingId = COOKING_1_7_N;
			} else {
				cookingId = COOKING_1_7_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49049) || (itemId == 49057)) { // 
			if (itemId == 49049) {
				cookingId = COOKING_2_0_N;
			} else {
				cookingId = COOKING_2_0_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49050) || (itemId == 49058)) { // 
			if (itemId == 49050) {
				cookingId = COOKING_2_1_N;
			} else {
				cookingId = COOKING_2_1_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49051) || (itemId == 49059)) { // 果子
			if (itemId == 49051) {
				cookingId = COOKING_2_2_N;
			} else {
				cookingId = COOKING_2_2_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49052) || (itemId == 49060)) { // 燒
			if (itemId == 49052) {
				cookingId = COOKING_2_3_N;
			} else {
				cookingId = COOKING_2_3_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49053) || (itemId == 49061)) { // 燒
			if (itemId == 49053) {
				cookingId = COOKING_2_4_N;
			} else {
				cookingId = COOKING_2_4_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49054) || (itemId == 49062)) { // 
			if (itemId == 49054) {
				cookingId = COOKING_2_5_N;
			} else {
				cookingId = COOKING_2_5_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49055) || (itemId == 49063)) { // 腳串燒
			if (itemId == 49055) {
				cookingId = COOKING_2_6_N;
			} else {
				cookingId = COOKING_2_6_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49056) || (itemId == 49064)) { // 
			if (itemId == 49056) {
				cookingId = COOKING_2_7_N;
			} else {
				cookingId = COOKING_2_7_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49244) || (itemId == 49252)) { // 燒
			if (itemId == 49244) {
				cookingId = COOKING_3_0_N;
			} else {
				cookingId = COOKING_3_0_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49245) || (itemId == 49253)) { // 燒
			if (itemId == 49245) {
				cookingId = COOKING_3_1_N;
			} else {
				cookingId = COOKING_3_1_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49246) || (itemId == 49254)) { // 
			if (itemId == 49246) {
				cookingId = COOKING_3_2_N;
			} else {
				cookingId = COOKING_3_2_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49247) || (itemId == 49255)) { // 燒
			if (itemId == 49247) {
				cookingId = COOKING_3_3_N;
			} else {
				cookingId = COOKING_3_3_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49248) || (itemId == 49256)) { // 手羽先
			if (itemId == 49248) {
				cookingId = COOKING_3_4_N;
			} else {
				cookingId = COOKING_3_4_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49249) || (itemId == 49257)) { // 燒
			if (itemId == 49249) {// 烤飛龍肉
				cookingId = COOKING_3_5_N;
			} else {// 特別的烤飛龍肉
				cookingId = COOKING_3_5_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49250) || (itemId == 49258)) { // 深海魚
			if (itemId == 49250) {
				cookingId = COOKING_3_6_N;
			} else {
				cookingId = COOKING_3_6_S;
			}
			eatCooking(pc, cookingId, time);
			
		} else if ((itemId == 49251) || (itemId == 49259)) { // 卵
			if (itemId == 49251) {
				cookingId = COOKING_3_7_N;
			} else {
				cookingId = COOKING_3_7_S;
			}
			eatCooking(pc, cookingId, time);
		}
		pc.sendPackets(new S_ServerMessage(76, item.getNumberedName(1, true))); // \f1%0食。
		pc.getInventory().removeItem(item, 1);
	}

	public static void eatCooking(final L1PcInstance pc, final int cookingId, final int time) {
		int cookingType = 0;
		switch (cookingId) {
		case COOKING_1_0_N:
		case COOKING_1_0_S:// 
			cookingType = 0;
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;

		case COOKING_1_1_N:
		case COOKING_1_1_S: // 
			cookingType = 1;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 中
				pc.getParty().updateMiniHP(pc);
			}
			break;

		case COOKING_1_2_N:
		case COOKING_1_2_S: // 餅
			cookingType = 2;
			break;

		case COOKING_1_3_N:
		case COOKING_1_3_S: // 蟻腳燒
			cookingType = 3;
			pc.addAc(-1);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;

		case COOKING_1_4_N:
		case COOKING_1_4_S: // 
			cookingType = 4;
			pc.addMaxMp(20);
			pc.sendPackets(new S_MPUpdate(pc));
			break;

		case COOKING_1_5_N:
		case COOKING_1_5_S: // 甘酢
			cookingType = 5;
			break;

		case COOKING_1_6_N:
		case COOKING_1_6_S: // 豬肉串燒
			cookingType = 6;
			pc.addMr(5);
			pc.sendPackets(new S_SPMR(pc));
			break;

		case COOKING_1_7_N:
		case COOKING_1_7_S: // 
			cookingType = 7;
			break;

		case COOKING_2_0_N:
		case COOKING_2_0_S: // 
			cookingType = 8;
			break;

		case COOKING_2_1_N:
		case COOKING_2_1_S: // 
			cookingType = 9;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(30);
			pc.sendPackets(new S_MPUpdate(pc));
			break;

		case COOKING_2_2_N:
		case COOKING_2_2_S: // 果子
			cookingType = 10;
			pc.addAc(-2);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;

		case COOKING_2_3_N:
		case COOKING_2_3_S: // 燒
			cookingType = 11;
			break;

		case COOKING_2_4_N:
		case COOKING_2_4_S: // 燒
			cookingType = 12;
			break;

		case COOKING_2_5_N:
		case COOKING_2_5_S: // 
			cookingType = 13;
			pc.addMr(10);
			pc.sendPackets(new S_SPMR(pc));
			break;

		case COOKING_2_6_N:
		case COOKING_2_6_S: // 腳串燒
			cookingType = 14;
			pc.addSp(1);
			pc.sendPackets(new S_SPMR(pc));
			break;

		case COOKING_2_7_N:
		case COOKING_2_7_S: // 
			cookingType = 15;
			break;

		case COOKING_3_0_N:
		case COOKING_3_0_S: // 燒
			cookingType = 16;
			break;

		case COOKING_3_1_N:
		case COOKING_3_1_S: // 燒
			cookingType = 17;
			pc.addMaxHp(50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(50);
			pc.sendPackets(new S_MPUpdate(pc));
			break;

		case COOKING_3_2_N:
		case COOKING_3_2_S: // 
			cookingType = 18;
			break;

		case COOKING_3_3_N:
		case COOKING_3_3_S: // 燒
			cookingType = 19;
			pc.addAc(-3);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;

		case COOKING_3_4_N:
		case COOKING_3_4_S: // 手羽先
			cookingType = 20;
			pc.addMr(15);
			pc.sendPackets(new S_SPMR(pc));
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;

		case COOKING_3_5_N:
		case COOKING_3_5_S: // 燒
			cookingType = 21;
			pc.addSp(2);
			pc.sendPackets(new S_SPMR(pc));
			break;

		case COOKING_3_6_N:
		case COOKING_3_6_S: // 深海魚
			cookingType = 22;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 中
				pc.getParty().updateMiniHP(pc);
			}
			break;

		case COOKING_3_7_N:
		case COOKING_3_7_S: // 卵
			cookingType = 23;
			break;
		}

		try {
			pc.sendPackets(new S_PacketBoxCooking(pc, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);

			if (((cookingId >= COOKING_1_0_N) && (cookingId <= COOKING_1_6_N))
					|| ((cookingId >= COOKING_1_0_S) && (cookingId <= COOKING_1_6_S))
					|| ((cookingId >= COOKING_2_0_N) && (cookingId <= COOKING_2_6_N))
					|| ((cookingId >= COOKING_2_0_S) && (cookingId <= COOKING_2_6_S))
					|| ((cookingId >= COOKING_3_0_N) && (cookingId <= COOKING_3_6_N))
					|| ((cookingId >= COOKING_3_0_S) && (cookingId <= COOKING_3_6_S))) {
				pc.setCookingId(cookingId);
				
			} else if ((cookingId == COOKING_1_7_N) || (cookingId == COOKING_1_7_S)
					|| (cookingId == COOKING_2_7_N) || (cookingId == COOKING_2_7_S)
					|| (cookingId == COOKING_3_7_N) || (cookingId == COOKING_3_7_S)) {
				pc.setDessertId(cookingId);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
