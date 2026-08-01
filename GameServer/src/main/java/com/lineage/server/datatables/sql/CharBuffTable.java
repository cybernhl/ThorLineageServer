package com.lineage.server.datatables.sql;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharBuffStorage;
import com.lineage.server.model.L1Cooking;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.templates.L1BuffTmp;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 保留技能紀錄
 * @author dexc
 *
 */
public class CharBuffTable implements CharBuffStorage {

	private static final Log _log = LogFactory.getLog(CharBuffTable.class);

	private static final Map<Integer, ArrayList<L1BuffTmp>> _buffMap =
		new HashMap<Integer, ArrayList<L1BuffTmp>>();
	
	// 執行保留的技能
	private static final int[] _buffSkill = {
		LIGHT, // 日光術
		STATUS_UNDERWATER_BREATH, // 伊娃的祝福
		
		SHAPE_CHANGE, // 變形術
		
		HASTE, // 加速術
		GREATER_HASTE, // 強力加速術
		
		SHIELD, // 保護罩
		SHADOW_ARMOR, // 影之防護
		EARTH_SKIN, // 大地防護
		EARTH_BLESS, // 大地的祝福
		IRON_SKIN, // 鋼鐵防護
		
		STATUS_BRAVE, // 勇敢藥水效果
		STATUS_HASTE, // 加速藥水效果
		STATUS_ELFBRAVE, // 精靈餅乾效果
		HOLY_WALK, // 神聖疾走
		MOVING_ACCELERATION, // 行走加速
		WIND_WALK, // 風之疾走
		
		PHYSICAL_ENCHANT_DEX, // 通暢氣脈術
		PHYSICAL_ENCHANT_STR, // 體魄強健術
		DRESS_MIGHTY, // 力量提升
		DRESS_DEXTERITY, // 敏捷提升
		
		GLOWING_AURA, // 激勵士氣
		SHINING_AURA, // 鋼鐵士氣
		BRAVE_AURA, // 衝擊士氣
		
		FIRE_WEAPON, // 火焰武器
		FIRE_BLESS, // 烈炎氣息
		BURNING_WEAPON, // 烈炎武器
		
		WIND_SHOT, // 風之神射
		STORM_EYE, // 暴風之眼
		STORM_SHOT, // 暴風神射
		
		STATUS_BLUE_POTION ,// 魔力回復藥水效果
		STATUS_CHAT_PROHIBITED, // 禁言效果
		
		COOKING_1_0_N,
		COOKING_1_0_S,
		COOKING_1_1_N,
		COOKING_1_1_S, // 料理(除)
		COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S,
		COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S,
		COOKING_1_6_N, COOKING_1_6_S, COOKING_2_0_N, COOKING_2_0_S,
		COOKING_2_1_N, COOKING_2_1_S, COOKING_2_2_N, COOKING_2_2_S,
		COOKING_2_3_N, COOKING_2_3_S, COOKING_2_4_N, COOKING_2_4_S,
		COOKING_2_5_N, COOKING_2_5_S, COOKING_2_6_N, COOKING_2_6_S,
		COOKING_3_0_N, COOKING_3_0_S, COOKING_3_1_N, COOKING_3_1_S,
		COOKING_3_2_N, COOKING_3_2_S, COOKING_3_3_N, COOKING_3_3_S,
		COOKING_3_4_N, COOKING_3_4_S, COOKING_3_5_N, COOKING_3_5_S,
		COOKING_3_6_N, COOKING_3_6_S,
		
		EXP11, EXP12, EXP13,EXP15,EXP17,EXP20, // 第一段經驗加倍

		SEXP13,SEXP15,SEXP17,SEXP20, // 第二段經驗加倍
		
		REEXP20, // 第三段經驗加倍
		
		STATUS_BRAVE3, // 巧克力蛋糕
	};

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_buff`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int char_obj_id = rs.getInt("char_obj_id");
				
				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(char_obj_id) != null) {
					final int skill_id = rs.getInt("skill_id");
					final int remaining_time = rs.getInt("remaining_time");
					final int poly_id = rs.getInt("poly_id");

					final L1BuffTmp buffTmp = new L1BuffTmp();
					buffTmp.set_char_obj_id(char_obj_id);
					buffTmp.set_skill_id(skill_id);
					buffTmp.set_remaining_time(remaining_time);
					buffTmp.set_poly_id(poly_id);

					addMap(char_obj_id, buffTmp);
					
				} else {
					delete(char_obj_id);
				}
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入保留技能紀錄資料數量: " + _buffMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除遺失資料
	 * @param objid
	 */
	private static void delete(final int objid) {
		final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
		if (list != null) {
			list.clear();// 移除此列表中的所有元素
		}
		_buffMap.remove(objid);
		
		// 清空資料庫紀錄
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement(
					"DELETE FROM `character_buff` WHERE `char_obj_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
	}

	/**
	 * 加入清單
	 * @param objId
	 * @param buffTmp
	 */
	private static void addMap(final int objId, final L1BuffTmp buffTmp) {
		final ArrayList<L1BuffTmp> list = _buffMap.get(objId);
		if (list == null) {
			final ArrayList<L1BuffTmp> newlist = new ArrayList<L1BuffTmp>();
			newlist.add(buffTmp);
			_buffMap.put(objId, newlist);

		} else {
			list.add(buffTmp);
		}
	}

	/**
	 * 增加保留技能紀錄
	 * @param pc
	 */
	@Override
	public void saveBuff(final L1PcInstance pc) {
		for (final int skillId : _buffSkill) {
			final int timeSec = pc.getSkillEffectTimeSec(skillId);
			if (0 < timeSec) {
				int polyId = -1;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getTempCharGfx();
				}
				this.storeBuff(pc.getId(), skillId, timeSec, polyId);
			}
		}
		// 刪除全部技能效果
		pc.clearSkillEffectTimer();
	}

	/**
	 * 寫入保留技能紀錄
	 * @param objId
	 * @param skillId
	 * @param time
	 * @param polyId
	 */
	private void storeBuff(final int objId, final int skillId, final int time, final int polyId) {
		final L1BuffTmp buffTmp = new L1BuffTmp();
		buffTmp.set_char_obj_id(objId);
		buffTmp.set_skill_id(skillId);
		buffTmp.set_remaining_time(time);
		buffTmp.set_poly_id(polyId);
		// 加入MAP
		addMap(objId, buffTmp);
		// 寫入資料庫
		storeBuffR(buffTmp);
	}

	/**
	 * 取回保留技能紀錄
	 * @param pc
	 */
	@Override
	public void buff(final L1PcInstance pc) {
		final int objid = pc.getId();
		final ArrayList<L1BuffTmp> list = _buffMap.get(objid);
		if (list != null) {
			for (final L1BuffTmp buffTmp : list) {
				// 取回資料
				final int skill_id = buffTmp.get_skill_id();
				final int remaining_time = buffTmp.get_remaining_time();// 秒
				final int poly_id = buffTmp.get_poly_id();

				if (remaining_time > 0) {
					if (poly_id != -1) {// 變身
						L1PolyMorph.doPoly(pc, poly_id, remaining_time, L1PolyMorph.MORPH_BY_LOGIN);

					} else {
						switch (skill_id) {
							case STATUS_UNDERWATER_BREATH:
								pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), remaining_time));
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));
								pc.setSkillEffect(STATUS_UNDERWATER_BREATH, remaining_time * 1000);
								break;
							case STATUS_BRAVE3: // 巧克力蛋糕
							pc.sendPackets(new S_PacketBoxThirdSpeed(remaining_time));
							pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case STATUS_BRAVE: // 勇敢藥水效果
							pc.sendPackets(new S_SkillBrave(pc.getId(), 1, remaining_time));
							pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
							pc.setBraveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_ELFBRAVE: // 精靈餅乾效果
							pc.sendPackets(new S_SkillBrave(pc.getId(), 3, remaining_time));
							pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 3, 0));
							pc.setBraveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_HASTE: // 加速藥水效果
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1, remaining_time));
							pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
							pc.setMoveSpeed(1);
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_BLUE_POTION: // 藍水
							pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION, remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case STATUS_CHAT_PROHIBITED: // 禁言
							pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case EXP13: // 第一段1.3倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.3倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP15: // 第一段1.5倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP17: // 第一段1.7倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段1.7倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;

						case EXP20: // 第一段雙倍經驗
							// 3021 目前正在享受 %0 倍經驗.【剩餘時間: %1 秒】
							pc.sendPackets(new S_ServerMessage("第一段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							// 狩獵的經驗職將會增加
							pc.sendPackets(new S_PacketBoxCooking(pc, 32, remaining_time));
							break;
							
						case SEXP13: // 第二段1.3倍經驗
							// 3083 第二段經驗1.3倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.3倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case SEXP15: // 第二段1.5倍經驗
							// 3084 第二段經驗1.5倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.5倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case SEXP17: // 第二段1.7倍經驗
							// 3085 第二段經驗1.7倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段1.7倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case SEXP20: // 第二段2.0倍經驗
							// 3082 第二段經驗2.0倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第二段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;
							
						case REEXP20: // 第三段雙倍經驗
							// 3086 特殊經驗雙倍效果時間尚有 %0 秒。
							pc.sendPackets(new S_ServerMessage("第三段2.0倍經驗 剩餘時間(秒):" + remaining_time));
							pc.setSkillEffect(skill_id, remaining_time * 1000);
							break;

						case COOKING_1_0_N:
						case COOKING_1_1_N:
						case COOKING_1_2_N:
						case COOKING_1_3_N:
						case COOKING_1_4_N:
						case COOKING_1_5_N:
						case COOKING_1_6_N:
						case COOKING_1_7_N:
						case COOKING_1_0_S:
						case COOKING_1_1_S:
						case COOKING_1_2_S:
						case COOKING_1_3_S:
						case COOKING_1_4_S:
						case COOKING_1_5_S:
						case COOKING_1_6_S:
						case COOKING_1_7_S:
						case COOKING_2_0_N:
						case COOKING_2_1_N:
						case COOKING_2_2_N:
						case COOKING_2_3_N:
						case COOKING_2_4_N:
						case COOKING_2_5_N:
						case COOKING_2_6_N:
						case COOKING_2_7_N:
						case COOKING_2_0_S:
						case COOKING_2_1_S:
						case COOKING_2_2_S:
						case COOKING_2_3_S:
						case COOKING_2_4_S:
						case COOKING_2_5_S:
						case COOKING_2_6_S:
						case COOKING_2_7_S:
						case COOKING_3_0_N:
						case COOKING_3_1_N:
						case COOKING_3_2_N:
						case COOKING_3_3_N:
						case COOKING_3_4_N:
						case COOKING_3_5_N:
						case COOKING_3_6_N:
						case COOKING_3_7_N:
						case COOKING_3_0_S:
						case COOKING_3_1_S:
						case COOKING_3_2_S:
						case COOKING_3_3_S:
						case COOKING_3_4_S:
						case COOKING_3_5_S:
						case COOKING_3_6_S:
						case COOKING_3_7_S:
							L1Cooking.eatCooking(pc, skill_id, remaining_time);
							break;

						default:
							// SKILL移轉
							final SkillMode mode = L1SkillMode.get().getSkill(skill_id);
							if (mode != null) {
								try {
									mode.start(pc, pc, null, remaining_time);
									
								} catch (Exception e) {
									_log.error(e.getLocalizedMessage(), e);
								}
								
							} else {
								final L1SkillUse l1skilluse = new L1SkillUse();
								l1skilluse.handleCommands(
										pc, skill_id, pc.getId(), pc.getX(), pc.getY(),
										remaining_time, L1SkillUse.TYPE_LOGIN);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 刪除全部保留技能紀錄
	 * @param pc
	 */
	@Override
	public void deleteBuff(final L1PcInstance pc) {
		delete(pc.getId());
	}

	/**
	 * 刪除全部保留技能紀錄
	 * @param objid
	 */
	@Override
	public void deleteBuff(final int objid) {
		delete(objid);
	}
	
	/**
	 * 寫入保留技能紀錄
	 * @param buffTmp
	 */
	private static void storeBuffR(L1BuffTmp buffTmp) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement(
					"INSERT INTO `character_buff` SET `char_obj_id`=?,`skill_id`=?,`remaining_time`=?,`poly_id`=?");
			ps.setInt(1, buffTmp.get_char_obj_id());
			ps.setInt(2, buffTmp.get_skill_id());
			ps.setInt(3, buffTmp.get_remaining_time());
			ps.setInt(4, buffTmp.get_poly_id());

			ps.execute();

		} catch (final SQLException e) {
			//_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
