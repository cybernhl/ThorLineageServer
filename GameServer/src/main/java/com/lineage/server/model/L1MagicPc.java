package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DARK_BLIND;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.FINAL_BURN;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.Lower_Dmg;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.TAMING_MONSTER;
import static com.lineage.server.model.skill.L1SkillId.WEAPON_BREAK;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.ConcurrentModificationException;

import com.lineage.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;

/**
 * 魔法攻擊判定(PC)
 * 
 * @author daien
 *
 */
public class L1MagicPc extends L1MagicMode {

	private static final Log _log = LogFactory.getLog(L1MagicPc.class);

	/**
	 * 魔法攻擊判定(PC)
	 * 
	 * @param attacker
	 * @param target
	 */
	public L1MagicPc(final L1PcInstance attacker, final L1Character target) {
		if (attacker == null) {
			return;
		}

		_pc = attacker;

		if (target instanceof L1PcInstance) {
			_calcType = PC_PC;
			_targetPc = (L1PcInstance) target;

		} else {
			_calcType = PC_NPC;
			_targetNpc = (L1NpcInstance) target;
		}

	}

	/**
	 * 職業魔法等級
	 * 
	 * @return
	 */
	private int getMagicLevel() {
		return _pc.getMagicLevel();
	}

	/**
	 * 智力命中魔法追加
	 * 
	 * @return
	 */
	private int getMagicBonus() {
		return _pc.getMagicBonus();
	}

	/**
	 * 傳回正義質
	 * 
	 * @return
	 */
	private int getLawful() {
		return _pc.getLawful();
	}

	/**
	 * 攻擊成功的判斷 ●●●● 確率系魔法成功判定 ●●●● 計算方法 攻擊側：LV + ((MagicBonus * 3) *
	 * 魔法固有係數) 防禦側：((LV / 2) + (MR * 3)) / 2 攻擊成功率：攻擊側 - 防禦側
	 */
	@Override
	public boolean calcProbabilityMagic(final int skillId) {
		int probability = 0;// 魔法成功機率
		boolean isSuccess = false;

		switch (_calcType) {
		case PC_PC:
			// 魔法相消術
			if (skillId == CANCELLATION) {
				if (_pc != null && _targetPc != null) {
					// 對像為自己100%成功
					if (_pc.getId() == _targetPc.getId()) {
						return true;
					}

					// 相同血盟100%成功
					if (_pc.getClanid() > 0) {
						if (_pc.getClanid() == _targetPc.getClanid()) {
							return true;
						}
					}

					// 相同隊伍100%成功
					if (_pc.isInParty()) {
						if (_pc.getParty().isMember(_targetPc)) {
							return true;
						}
					}
				}
			}

			// 攻擊者 或是 被攻擊者 在安全區內
			if (!checkZone(skillId)) {
				return false;
			}

			// 被攻擊者受到大地屏障
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				// 施展法術不是壞物術或魔法相消術
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}

			// 迴避
			if (calcEvasion()) {
				return false;
			}
			break;

		case PC_NPC:
			if (_targetNpc != null) {
				// 對不可見的怪物額外判斷
				final int gfxid = this._targetNpc.getNpcTemplate().get_gfxid();
				switch (gfxid) {
				case 2412:// 南瓜的影子
					if (!_pc.getInventory().checkEquipped(20046)) {// 南瓜帽
						return false;
					}
					break;
				}
				// NPC需附加技能可攻擊
				final int npcId = _targetNpc.getNpcTemplate().get_npcId();
				final Integer tgskill = L1AttackList.SKNPC.get(npcId);
				if (tgskill != null) {
					if (!_pc.hasSkillEffect(tgskill)) {
						return false;
					}
				}

				// NPC指定外型不可攻擊
				final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
				if (tgpoly != null) {
					if (tgpoly.equals(_pc.getTempCharGfx())) {
						return false;
					}
				}

				// NPC抵抗技能(NPCID / 技能編號) 列表中該技能對該NPC施展失敗
				final boolean dgskill = L1AttackList.DNNPC.containsKey(npcId);
				if (dgskill) {
					Integer[] dgskillids = L1AttackList.DNNPC.get(npcId);
					for (Integer dgskillid : dgskillids) {
						if (dgskillid.equals(skillId)) {
							return false;
						}
					}
				}
			}

			// 魔法相消術
			if (skillId == CANCELLATION) {
				return true;
			}

			// 被攻擊者受到大地屏障
			if (this._targetNpc.hasSkillEffect(EARTH_BIND)) {
				// 施展法術不是壞物術或魔法相消術
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}
			break;
		}

		// 計算魔法成功機率
		probability = calcProbability(skillId);

		// 法師提高機率 +智力/4 (>> 1: 除) (<< 1: 乘)
		/*
		 * if (_pc.isWizard()) { probability += _pc.getInt() / 4;//
		 * 2012-05-12(9) }
		 */

		final int rnd = _random.nextInt(100) + 1;

		// 最大成功率90%
		probability = Math.min(probability, 90);
		// 最小成功率1%
		probability = Math.max(probability, 1);

		if (probability >= rnd) {
			isSuccess = true;

		} else {
			isSuccess = false;
		}

		// gm攻擊資訊
		if (!ConfigAlt.ALT_ATKMSG) {
			return isSuccess;

		} else {
			switch (_calcType) {
			case PC_PC:
				if (!_pc.isGm()) {
					if (!_targetPc.isGm()) {
						return isSuccess;
					}
				}
				break;

			case PC_NPC:
				if (!_pc.isGm()) {
					return isSuccess;
				}
				break;
			}
		}

		switch (_calcType) {
		case PC_PC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對PC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			if (this._targetPc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("受到PC技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_targetPc.sendPackets(new S_ServerMessage(166, atkMsg
						.toString()));
			}
			break;

		case PC_NPC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對NPC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetNpc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			break;
		}
		return isSuccess;
	}

	/**
	 * 攻擊者 或是 被攻擊者 在安全區內
	 * 
	 * @param skillId
	 * @return
	 */
	private boolean checkZone(final int skillId) {
		if ((_pc != null) && (_targetPc != null)) {
			// 攻擊者 或是 被攻擊者 在安全區內
			if (_pc.isSafetyZone() || _targetPc.isSafetyZone()) {
				// 施展技能限制安全區域無法使用
				final Boolean isBoolean = L1AttackList.NZONE.get(skillId);
				if (isBoolean != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 魔法命中的判斷
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcProbability(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int attackLevel = _pc.getLevel();// 攻擊者等級
		int defenseLevel = 0;// 對手等級
		int probability = 0;// 輸出機率

		switch (this._calcType) {
		case PC_PC:
			if (_targetPc.isGm()) {
				return -1;
			}
			defenseLevel = _targetPc.getLevel();
			break;

		case PC_NPC:
			if (_targetNpc != null && _targetNpc.getMovementDistance() > 0) {
				if (_pc.getLocation().getLineDistance(_targetNpc.getHomeX(), _targetNpc.getHomeY()) > (_targetNpc.getMovementDistance() + 3)) {
					L1Teleport.teleport(_pc, _targetNpc.getX(), _targetNpc.getY(), _targetNpc.getMapId(), _targetNpc.getHeading(), false);
					return -1;
				}
			}
			defenseLevel = _targetNpc.getLevel();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					final L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
			break;
		}

		switch (skillId) {
		case ENTANGLE:// 地面障礙
		case WIND_SHACKLE:// 風之枷鎖
		case ELEMENTAL_FALL_DOWN:// 弱化屬性
		case RETURN_TO_NATURE:// 釋放元素
		case POLLUTE_WATER:// 污濁之水
			// 成功確率 魔法固有係數 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			probability -= getTargetMr() / 80;
			break;

		case AREA_OF_SILENCE:// 封印禁地
		case STRIKER_GALE:// 精準射擊
			// 成功確率 魔法固有係數 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			break;

		case ERASE_MAGIC:// 魔法消除
			// 成功確率 魔法固有係數 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			probability -= getTargetMr() / 80;
			break;

		case EARTH_BIND:// 大地屏障
			// 追加智力影響(>> 1: 除) (<< 1: 乘)
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 3 + (_pc.getInt() >> 3);// /8

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 4 + (_pc.getInt() >> 2);// /4

			} else {// 攻擊者等級大於被攻擊者
				probability = 5 + (_pc.getInt() >> 1);// /2
			}
			// probability -= getTargetMr() / 40;
			break;

		case SHOCK_STUN:// 衝擊之暈
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 22;// SRC 20

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 40;// SRC NO

			} else {// 攻擊者等級大於等於被攻擊者
				probability = 60;// SRC 80
			}
			break;

		case COUNTER_BARRIER:// 反擊屏障
			// 成功機率 基本確率 + LV差1每 +-1%
			probability = l1skills.getProbabilityValue() + attackLevel
					- defenseLevel;
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			break;

		// case RESIST_FEAR:// 恐懼無助*/
		case SILENCE:// 魔法封印
		case WEAPON_BREAK:// 壞物術
		case SLOW:// 緩速術
			final int dice3 = l1skills.getProbabilityDice();
			int diceCount3 = 0;

			if (_pc.isWizard()) {
				diceCount3 = getMagicBonus() + getMagicLevel() + 1;

			} else {
				diceCount3 = getMagicBonus() + getMagicLevel() - 1;
			}

			diceCount3 = Math.max(diceCount3, 1);

			for (int i = 0; i < diceCount3; i++) {
				probability += (_random.nextInt(dice3) + 1);
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// 智力(依職業)附加魔法命中
			probability += 2 * _pc.getOriginalMagicHit();

			// 扣除抗魔減免
			probability -= getTargetMr();

			// 等級差(被攻擊者 - 攻擊者) / 24
			int levelR = Math.max(defenseLevel / 24, 1);

			probability /= levelR;
			break;

		case ICE_LANCE:// 冰矛圍籬 - 寒冰耐性
			// 取回技能計算機率
			final int diceICE = l1skills.getProbabilityDice();
			int diceCountICE = Math.max(getMagicBonus() + getMagicLevel() + 1,
					1);

			for (int i = 0; i < diceCountICE; i++) {
				if (diceICE > 0) {
					probability += (_random.nextInt(diceICE) + 1);
				}
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);

			probability -= getTargetMr();
			break;

		default:
			// 取回技能計算機率
			final int dice2 = l1skills.getProbabilityDice();
			int diceCount2 = 0;

			if (_pc.isWizard()) {
				diceCount2 = getMagicBonus() + getMagicLevel() + 1;

			} else if (_pc.isElf()) {
				diceCount2 = getMagicBonus() + getMagicLevel() - 1;

			} else {
				diceCount2 = getMagicBonus() + getMagicLevel() - 1;
			}

			diceCount2 = Math.max(diceCount2, 1);

			for (int i = 0; i < diceCount2; i++) {
				if (dice2 > 0) {
					probability += (_random.nextInt(dice2) + 1);
				}
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// 智力(依職業)附加魔法命中 *2 (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);

			probability -= getTargetMr();

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if (((_targetNpc.getMaxHp()) >> 2) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;

				} else if (((_targetNpc.getMaxHp() << 2) >> 2) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.2;

				} else if (((_targetNpc.getMaxHp() * 3) >> 2) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
			break;
		}

		// 耐性 (>> 1: 除) (<< 1: 乘)
		switch (_calcType) {
		case PC_PC:
			switch (skillId) {
			case EARTH_BIND:// 大地屏障 - 支撐耐性
				probability -= (_targetPc.getRegistSustain() >> 1);
				break;

			case SHOCK_STUN:// 衝擊之暈 - 昏迷耐性
				probability -= (_targetPc.getRegistStun() >> 1);
				break;

			case CURSE_PARALYZE:// 木乃伊的詛咒 - 石化耐性
				probability -= (_targetPc.getRegistStone() >> 1);
				break;

			case FOG_OF_SLEEPING:// 沉睡之霧 - 睡眠耐性
				probability -= (_targetPc.getRegistSleep() >> 1);
				break;

			case ICE_LANCE:// 冰矛圍籬 - 寒冰耐性
			case FREEZING_BLIZZARD:// 冰雪颶風
				probability -= (_targetPc.getRegistFreeze() >> 1);
				break;

			case CURSE_BLIND:// 闇盲咒術 - 暗黑耐性
			case DARKNESS:// 黑闇之影
			case DARK_BLIND:// 暗黑盲咒
				probability -= (_targetPc.getRegistBlind() >> 1);
				break;
			}
			break;
		}
		return probability;
	}

	/**
	 * 魔法傷害值計算
	 * 
	 * @param skillId
	 * @return
	 */
	@Override
	public int calcMagicDamage(final int skillId) {
		int damage = 0;
		switch (_calcType) {
		case PC_PC:
			damage = calcPcMagicDamage(skillId);
			break;

		case PC_NPC:
			damage = calcNpcMagicDamage(skillId);
			break;
		}

		damage = calcMrDefense(damage);
		return damage;
	}

	/**
	 * PC對PC魔法傷害計算
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcPcMagicDamage(final int skillId) {
		if (_targetPc == null) {
			return 0;
		}
		// 傷害為0
		if (dmg0(_targetPc)) {
			return 0;
		}

		int dmg = 0;
		if (skillId == FINAL_BURN) {
			dmg = _pc.getCurrentMp();

		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (int) (dmg * (getLeverage() / 10D));
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具傷害減免

		dmg -= dmg * _targetPc.getDamageReductionByArmor2(); // 防具减伤

		dmg -= _targetPc.dmgDowe(); // 機率傷害減免

		if (_targetPc.getClanid() != 0) {
			dmg -= getDamageReductionByClan(_targetPc);// 血盟技能魔法傷害減免
		}

		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = Math.max(_targetPc.getLevel(), 50);
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}

		boolean dmgX2 = false;// 傷害除2
		// 取回技能
		if (!_targetPc.getSkillisEmpty()
				&& _targetPc.getSkillEffect().size() > 0) {
			try {
				for (final Integer key : _targetPc.getSkillEffect()) {
					final Integer integer = L1AttackList.SKD3.get(key);
					// 傷害減免
					if (integer != null) {
						if (integer.equals(key)) {
							// 技能編號與返回值相等
							dmgX2 = true;

						} else {
							dmg += integer;
						}
					}
				}

			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		if (dmgX2) {
			dmg = (dmg >> 1);// dmg /= 2;
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ8)) {
			if (_random.nextInt(100) <= 8) {
				dmg *= 2.0D;// 2倍傷害
			}
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ10)) {
			if (_random.nextInt(100) <= 10) {
				dmg *= 2.0D;// 2倍傷害
			}
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ12)) {
			if (_random.nextInt(100) <= 12) {
				dmg *= 2.0D;// 2倍傷害
			}
		}

		if (_pc.getChit() > 0) {
			if (_random.nextInt(100) <= _pc.getChit()) {
				dmg *= 1.25D;// 2倍傷害
			}
		}

		// 虛弱攻擊造成的傷害降低50%
		if (_pc.hasSkillEffect(Lower_Dmg)) {
			dmg *= 0.5;
		}
		if (_pc.getdmgbl() > 1) {
			dmg *= _pc.getdmgbl();
		}
		// 技能鏡反射
		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getWis() >= _random.nextInt(100)) {
					_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_targetPc.sendPacketsX8(new S_SkillSound(_targetPc.getId(),
							4395));
					_pc.receiveDamage(_targetPc, dmg, false, false);
					dmg = 0;
					_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
				}
			}
		}
		return Math.max(dmg, 0);
	}

	/**
	 * PC對NPC魔法傷害計算
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcNpcMagicDamage(final int skillId) {
		if (_targetNpc == null) {
			return 0;
		}
		// 傷害為0
		if (dmg0(_targetNpc)) {
			return 0;
		}

		final int npcId = _targetNpc.getNpcTemplate().get_npcId();
		final Integer tgskill = L1AttackList.SKNPC.get(npcId);
		if (tgskill != null) {
			if (!_pc.hasSkillEffect(tgskill)) {
				return 0;
			}
		}

		final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
		if (tgpoly != null) {
			if (tgpoly.equals(_pc.getTempCharGfx())) {
				return 0;
			}
		}

		int dmg = 0;
		if (skillId == FINAL_BURN) {
			dmg = _pc.getCurrentMp();

		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (int) (dmg * (getLeverage() / 10D));
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ8)) {
			if (_random.nextInt(100) <= 8) {
				dmg *= 2.0D;// 2倍傷害
			}
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ10)) {
			if (_random.nextInt(100) <= 10) {
				dmg *= 2.0D;// 2倍傷害
			}
		}
		if (_pc.hasSkillEffect(L1SkillId.EFFECT_STRENGTHENING_MBJ12)) {
			if (_random.nextInt(100) <= 12) {
				dmg *= 2.0D;// 2倍傷害
			}
		}

		if (_pc.getChit() > 0) {
			if (_random.nextInt(100) <= _pc.getChit()) {
				dmg *= 1.25D;// 2倍傷害
			}
		}

		boolean isNowWar = false;// 戰爭中
		final int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}

		boolean isPet = false;// 是寵物
//		if (_targetNpc instanceof L1PetInstance) {
//			isPet = true;
//			if (_targetNpc.getMaster().equals(_pc)) {
//				dmg = 0;
//			}
//		}
//		if (_targetNpc instanceof L1SummonInstance) {
//			final L1SummonInstance summon = (L1SummonInstance) _targetNpc;
//			if (summon.isExsistMaster()) {
//				isPet = true;
//			}
//			if (_targetNpc.getMaster().equals(_pc)) {
//				dmg = 0;
//			}
//		}

		if (!isNowWar && isPet) {// 非戰爭中 對象是寵物
			if (dmg != 0) {
				dmg = (dmg >> 3);// dmg /= 8;
			}
		}

		// 虛弱攻擊造成的傷害降低50%
		if (_pc.hasSkillEffect(Lower_Dmg)) {
			dmg *= 0.5;
		}
		if (_pc.getdmgbl() > 1) {
			dmg *= _pc.getdmgbl();
		}
		dmg += _pc.getEquipSlot().getAttachMagicDamage();
		if (dmg > 0 && _targetNpc != null && _targetNpc.getMaster() != null && _pc != null) {
			if (_targetNpc.getMaster().equals(_pc)) {
				return 0;
			}
		}
		return dmg;
	}

	/**
	 * damage_dice、damage_dice_count、damage_value、SP魔法算出
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcMagicDiceDamage(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int dice = l1skills.getDamageDice();
		final int diceCount = l1skills.getDamageDiceCount();
		final int value = l1skills.getDamageValue();
		int magicDamage = 0;
		int charaIntelligence = 0;
		if (dice > 0) {
			for (int i = 0; i < diceCount; i++) {
				magicDamage += (_random.nextInt(dice) + 1);
			}
		}

		magicDamage += value;

		if (_pc.getClanid() != 0) {
			// 血盟技能魔法傷害增加
			magicDamage += getDamageUpByClan(_pc);
		}

		final int spByItem = getTargetSp();// this._pc.getSp() -
											// this._pc.getTrueSp(); //
											// SP變動
		charaIntelligence = Math.max(_pc.getInt() + spByItem - 12, 1);

		/*
		 * if (charaIntelligence < 1) { charaIntelligence = 1; }
		 */

		final double attrDeffence = calcAttrResistance(l1skills.getAttr());

		double coefficient = Math.max(
				(1.0 - attrDeffence + charaIntelligence * 3.0 / 32.0), 0.0);
		/*
		 * if (coefficient < 0) { coefficient = 0; }
		 */

		magicDamage *= coefficient;

		final int rnd = _random.nextInt(100) + 1;
		if (l1skills.getSkillLevel() <= 6) {
			if (rnd <= (10 + _pc.getOriginalMagicCritical())) {
				final double criticalCoefficient = 1.5; // 魔法
				magicDamage *= criticalCoefficient;
			}
		}

		magicDamage += _pc.getOriginalMagicDamage();
		if (_pc.isWizard()) {
			magicDamage *= 1.1D;
		}

		return magicDamage;
	}

	/**
	 * 回復量（對）算出
	 * 
	 * @param skillId
	 * @return
	 */
	@Override
	public int calcHealing(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int dice = l1skills.getDamageDice();
		final int value = l1skills.getDamageValue();
		int magicDamage = 0;

		int magicBonus = Math.min(getMagicBonus(), 10);

		/*
		 * int magicBonus = this.getMagicBonus(); if (magicBonus > 10) {
		 * magicBonus = 10; }
		 */

		final int diceCount = value + magicBonus;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (int) (magicDamage * (getLeverage() / 10D));

		return magicDamage;
	}

	/**
	 * ＭＲ魔法傷害減輕
	 * 
	 * @param dmg
	 * @return
	 */
	private int calcMrDefense(int dmg) {
		// 取回目標抗魔
		final int mr = getTargetMr();

		double mrFloor = 0;
		double mrCoefficient = 0;
		Double[] mrF = L1AttackList.MRDMG.get(new Integer(mr));
		if (mrF != null) {
			mrFloor = mrF[0].doubleValue();
			mrCoefficient = mrF[1].doubleValue();

		} else {
			mrFloor = 11;
			mrCoefficient = 0.5;
		}

		// 計算減低的傷害
		dmg *= (mrCoefficient - (0.01 * Math.floor((mr - _pc
				.getOriginalMagicHit()) / mrFloor)));
		if (ConfigOther.MR_DMG_HALVED_ENABLE) {
			if (mr >= ConfigOther.MR_DMG_HALVED_VALUE) {
				dmg /= 1.7;
				dmg += (_pc.getSp() / 2);
			}
		}
		return dmg;
	}

	/**
	 * 計算結果反映
	 * 
	 * @param damage
	 * @param drainMana
	 */
	@Override
	public void commit(final int damage, final int drainMana) {
		L1Character _target = null;// 傷害數字顯示
		switch (_calcType) {
		case PC_PC:
			_target = _targetPc;// 傷害數字顯示
			commitPc(damage, drainMana);
			break;

		case PC_NPC:
			_target = _targetNpc;// 傷害數字顯示
			commitNpc(damage, drainMana);
			break;
		}

		/*
		 * // 加入魔法傷害數字顯示優化寫法 int i = -1; int k = -1; int h = -1; int s = -1; int
		 * m = -1;
		 * 
		 * if (damage > 0) { i = (int) ((damage / Math.pow(10, 0)) % 10) +
		 * 8001;// 個位 if (damage >= 10) { k = (int) ((damage / Math.pow(10, 1))
		 * % 10) + 8011;// 十位 if (damage >= 100) { h = (int) ((damage /
		 * Math.pow(10, 2)) % 10) + 8021;// 百位 if (damage >= 1000) { s = (int)
		 * ((damage / Math.pow(10, 3)) % 10) + 8031;// 千位 if (damage >= 10000) {
		 * m = (int) ((damage / Math.pow(10, 4)) % 10) + 8041;// 萬位 } } } } }
		 * 
		 * if (i != -1) { this._pc.sendPacketsAll(new
		 * S_SkillSound(_target.getId(), i));// 個位數 if (k != -1) {
		 * this._pc.sendPacketsAll(new S_SkillSound(_target.getId(), k));// 十位數
		 * if (h != -1) { this._pc.sendPacketsAll(new
		 * S_SkillSound(_target.getId(), h));// 百位數 if (s != -1) {
		 * this._pc.sendPacketsAll(new S_SkillSound(_target.getId(), s));// 千位數
		 * if (m != -1) { this._pc.sendPacketsAll(new
		 * S_SkillSound(_target.getId(), m));// 萬位數 } } } } } else {
		 * this._pc.sendPacketsAll(new S_SkillSound(_target.getId(), 8051));//
		 * 官方版Miss }
		 */

		if (Config.DAMAGE_SKIN) {
			int i = (int) ((damage / Math.pow(10, 0)) % 10) + 8001;// 個位
			int k = (int) ((damage / Math.pow(10, 1)) % 10) + 8011;// 十位
			int h = (int) ((damage / Math.pow(10, 2)) % 10) + 8021;// 百位
			int s = (int) ((damage / Math.pow(10, 3)) % 10) + 8031;// 千位
			int m = (int) ((damage / Math.pow(10, 4)) % 10) + 8041;// 萬位
			if (damage <= 0) {
				_pc.sendPackets(new S_SkillSound(_target.getId(), 8051));// 官方版Miss
			} else if (damage < 10) {
				_pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
			} else if (damage < 100) {
				_pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), k));// 十位數
			} else if (damage < 1000) {
				_pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), k));// 十位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), h));// 百位數
			} else if (damage < 10000) {
				_pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), k));// 十位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), h));// 百位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), s));// 千位數
			} else {
				_pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), k));// 十位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), h));// 百位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), s));// 千位數
				_pc.sendPackets(new S_SkillSound(_target.getId(), m));// 萬位數
			}
		}

		// 值及命中率確認用
		if (!ConfigAlt.ALT_ATKMSG) {
			return;
		} else {
			switch (_calcType) {
			case PC_PC:
				if (!_pc.isGm()) {
					if (!_targetPc.isGm()) {
						return;
					}
				}
				break;

			case PC_NPC:
				if (!_pc.isGm()) {
					return;
				}
				break;
			}
		}

		switch (_calcType) {
		case PC_PC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對PC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetPc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			if (this._targetPc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("受到PC技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetPc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_targetPc.sendPackets(new S_ServerMessage(166, atkMsg
						.toString()));
			}
			break;

		case PC_NPC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對NPC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetNpc.getNameId() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetNpc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			break;
		}
	}

	/**
	 * 對pc傷害的輸出
	 * 
	 * @param damage
	 * @param drainMana
	 */
	private void commitPc(final int damage, int drainMana) {
		try {
			if (drainMana > 0) {
				if (_targetPc.getCurrentMp() > 0) {
					drainMana = Math.min(drainMana, _targetPc.getCurrentMp());
					final int newMp = _pc.getCurrentMp() + drainMana;
					_pc.setCurrentMp(newMp);

				} else {
					drainMana = 0;
				}
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage, true, false);
			if (damage > 0
					&& ConfigOther.poly_Mlist.contains(_targetPc
							.getTempCharGfx())
					&& !_targetPc.hasSkillEffect(L1SkillId.bddzpoly)) {
				_targetPc.sendPackets(new S_DoActionGFX(_targetPc.getId(),
						ActionCodes.ACTION_Damage));
				_targetPc.broadcastPacketAll(new S_DoActionGFX(_targetPc
						.getId(), ActionCodes.ACTION_Damage));
				_targetPc.setSkillEffect(L1SkillId.bddzpoly, 100);
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 對npc傷害的輸出
	 * 
	 * @param damage
	 * @param drainMana
	 */
	private void commitNpc(final int damage, int drainMana) {
		try {
			if (drainMana > 0) {
				if (_targetNpc.getCurrentMp() > 0) {
					final int drainValue = _targetNpc.drainMana(drainMana);
					final int newMp = _pc.getCurrentMp() + drainValue;
					_pc.setCurrentMp(newMp);

				} else {
					drainMana = 0;
				}
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
