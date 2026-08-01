package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 衝突技能抵銷
 * @author dexc
 *
 */
public class L1BuffUtil {

	private static final Log _log = LogFactory.getLog(L1BuffUtil.class);

	/**
	 * 無法使用藥水
	 * @param pc
	 * @return true:可以使用 false:無法使用
	 */
	public static boolean stopPotion(final L1PcInstance pc) {
		if (pc.hasSkillEffect(L1SkillId.DECAY_POTION) == true) { // 藥水霜化術
			// 698 喉嚨灼熱，無法喝東西。
			pc.sendPackets(new S_ServerMessage(698)); 
			return false;
		}
		return true;
	}
	
	/**
	 * 解除魔法技能絕對屏障
	 * @param pc
	 */
	public static void cancelAbsoluteBarrier(final L1PcInstance pc) { // 解除魔法技能絕對屏障
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
	}

	/**
	 * 加速效果 抵銷對應技能
	 * @param pc
	 */
	public static void hasteStart(final L1PcInstance pc) {
		try {
			// 解除加速術
			if (pc.hasSkillEffect(HASTE)) {
				pc.killSkillEffectTimer(HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);
				
			}
			
			// 解除強力加速術
			if (pc.hasSkillEffect(GREATER_HASTE)) {
				pc.killSkillEffectTimer(GREATER_HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);
				
			}
			
			// 解除加速藥水
			if (pc.hasSkillEffect(STATUS_HASTE)) {
				pc.killSkillEffectTimer(STATUS_HASTE);
				pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
				pc.setMoveSpeed(0);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 加速藥水效果
	 * @param pc
	 * @param timeMillis
	 */
	public static void haste(final L1PcInstance pc, final int timeMillis) {
		try {
			hasteStart(pc);
			
			// 加速藥水效果
			pc.setSkillEffect(STATUS_HASTE, timeMillis);

			final int objId = pc.getId();
			pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
			pc.broadcastPacketAll(new S_SkillHaste(objId, 1, 0));
			
			pc.sendPacketsX8(new S_SkillSound(objId, 191));
			pc.setMoveSpeed(1);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 勇敢效果 抵銷對應技能
	 * @param pc
	 */
	public static void braveStart(final L1PcInstance pc) {
		try {
			/*{ HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, 
				STATUS_BRAVE2, STATUS_ELFBRAVE, STATUS_RIBRAVE, BLOODLUST },*/
			
			// 解除神聖疾走
			if (pc.hasSkillEffect(HOLY_WALK)) {
				pc.killSkillEffectTimer(HOLY_WALK);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			
			// 解除行走加速
			if (pc.hasSkillEffect(MOVING_ACCELERATION)) {
				pc.killSkillEffectTimer(MOVING_ACCELERATION);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			
			// 解除風之疾走
			if (pc.hasSkillEffect(WIND_WALK)) {
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			
			// 解除勇敢藥水效果
			if (pc.hasSkillEffect(STATUS_BRAVE)) {
				pc.killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}

			// 解除精靈餅乾效果
			if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
				pc.killSkillEffectTimer(STATUS_ELFBRAVE);
				pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 勇敢藥水效果
	 * @param pc 對像
	 * @param timeMillis TIME
	 */
	public static void brave(final L1PcInstance pc, final int timeMillis) {
		try {
			braveStart(pc);
			
			// 勇敢藥水效果
			pc.setSkillEffect(STATUS_BRAVE, timeMillis);

			final int objId = pc.getId();
			pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
			pc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));
			
			pc.sendPacketsX8(new S_SkillSound(objId, 751));
			pc.setBraveSpeed(1);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 經驗加倍技能判斷(第一段)
	 * @param pc
	 * @return
	 */
	public static boolean cancelExpSkill(final L1PcInstance pc) {
		// 停止初段技能
		if (pc.hasSkillEffect(COOKING_1_7_N)) {
			pc.removeSkillEffect(COOKING_1_7_N);
		}
		if (pc.hasSkillEffect(COOKING_1_7_S)) {
			pc.removeSkillEffect(COOKING_1_7_S);
		}
		if (pc.hasSkillEffect(COOKING_2_7_N)) {
			pc.removeSkillEffect(COOKING_2_7_N);
		}
		if (pc.hasSkillEffect(COOKING_2_7_S)) {
			pc.removeSkillEffect(COOKING_2_7_S);
		}
		if (pc.hasSkillEffect(COOKING_3_7_N)) {
			pc.removeSkillEffect(COOKING_3_7_N);
		}
		if (pc.hasSkillEffect(COOKING_3_7_S)) {
			pc.removeSkillEffect(COOKING_3_7_S);
		}

		// 返回已有技能
		if (pc.hasSkillEffect(EXP11)) {
			final int time = pc.getSkillEffectTimeSec(EXP11);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段110%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP12)) {
			final int time = pc.getSkillEffectTimeSec(EXP12);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段120%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP13)) {
			final int time = pc.getSkillEffectTimeSec(EXP13);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段130%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP15)) {
			final int time = pc.getSkillEffectTimeSec(EXP15);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段150%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP17)) {
			final int time = pc.getSkillEffectTimeSec(EXP17);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段170%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(EXP20)) {
			final int time = pc.getSkillEffectTimeSec(EXP20);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("\\fX第一段200%經驗 剩餘時間(秒):" + time));
			return false;
		}
		return true;
	}

	/**
	 * 經驗加倍技能判斷(第二段)
	 * @param pc
	 * @return
	 */
	public static boolean cancelExpSkill_2(final L1PcInstance pc) {
		// 返回已有技能
		if (pc.hasSkillEffect(SEXP11)) {
			final int time = pc.getSkillEffectTimeSec(SEXP11);
			// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
			pc.sendPackets(new S_ServerMessage("第二段110%經驗 剩餘時間(秒):" + time));
			return false;
		}
		// 返回已有技能
		if (pc.hasSkillEffect(SEXP13)) {
			final int time = pc.getSkillEffectTimeSec(SEXP13);
			// 3083 第二段經驗1.3倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段130%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP15)) {
			final int time = pc.getSkillEffectTimeSec(SEXP15);
			// 3084 第二段經驗1.5倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段150%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP17)) {
			final int time = pc.getSkillEffectTimeSec(SEXP17);
			// 3085 第二段經驗1.7倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段170%經驗 剩餘時間(秒):" + time));
			return false;
		}
		if (pc.hasSkillEffect(SEXP20)) {
			final int time = pc.getSkillEffectTimeSec(SEXP20);
			// 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
			pc.sendPackets(new S_ServerMessage("第二段200%經驗 剩餘時間(秒):" + time));
			return false;
		}
		return true;
	}

	/**
	 * 四大龍物品
	 * @param pc
	 * @return
	 */
	public static int cancelDragon(final L1PcInstance pc) {
		if (pc.hasSkillEffect(DRAGON1)) {
			return pc.getSkillEffectTimeSec(DRAGON1);
		}
		if (pc.hasSkillEffect(DRAGON2)) {
			return pc.getSkillEffectTimeSec(DRAGON2);
		}
		if (pc.hasSkillEffect(DRAGON3)) {
			return pc.getSkillEffectTimeSec(DRAGON3);
		}
		if (pc.hasSkillEffect(DRAGON4)) {
			return pc.getSkillEffectTimeSec(DRAGON4);
		}
		if (pc.hasSkillEffect(DRAGON5)) {
			return pc.getSkillEffectTimeSec(DRAGON5);
		}
		if (pc.hasSkillEffect(DRAGON6)) {
			return pc.getSkillEffectTimeSec(DRAGON6);
		}
		if (pc.hasSkillEffect(DRAGON7)) {
			return pc.getSkillEffectTimeSec(DRAGON7);
		}
		return -1;
	}
}
