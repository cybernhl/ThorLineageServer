package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 武器技能
 * @author daien
 *
 */
public class L1WeaponSkill {
	
	private static final Log _log = LogFactory.getLog(L1WeaponSkill.class);

	private static Random _random = new Random();

	private int _weaponId;

	private int _probability;

	private int _fixDamage;

	private int _randomDamage;

	private int _area;

	private int _skillId;

	private int _skillTime;

	private int _effectId;

	private int _effectTarget; // 對像 0:相手 1:自分

	private boolean _isArrowType;

	private int _attr;

	public L1WeaponSkill(final int weaponId, final int probability, final int fixDamage,
			final int randomDamage, final int area, final int skillId, final int skillTime,
			final int effectId, final int effectTarget, final boolean isArrowType, final int attr) {
		this._weaponId = weaponId;
		this._probability = probability;
		this._fixDamage = fixDamage;
		this._randomDamage = randomDamage;
		this._area = area;
		this._skillId = skillId;
		this._skillTime = skillTime;
		this._effectId = effectId;
		this._effectTarget = effectTarget;
		this._isArrowType = isArrowType;
		this._attr = attr;
	}

	public int getWeaponId() {
		return this._weaponId;
	}

	public int getProbability() {
		return this._probability;
	}

	public int getFixDamage() {
		return this._fixDamage;
	}

	public int getRandomDamage() {
		return this._randomDamage;
	}

	public int getArea() {
		return this._area;
	}

	public int getSkillId() {
		return this._skillId;
	}

	public int getSkillTime() {
		return this._skillTime;
	}

	public int getEffectId() {
		return this._effectId;
	}

	public int getEffectTarget() {
		return this._effectTarget;
	}

	public boolean isArrowType() {
		return this._isArrowType;
	}

	public int getAttr() {
		return this._attr;
	}

	public static double getWeaponSkillDamage(final L1PcInstance pc, final L1Character cha,
			final int weaponId) {
		final L1WeaponSkill weaponSkill = WeaponSkillTable.get().getTemplate(
				weaponId);
		if ((pc == null) || (cha == null) || (weaponSkill == null)) {
			return 0;
		}

		final int chance = _random.nextInt(100) + 1;
		if (weaponSkill.getProbability() < chance) {
			return 0;
		}

		final int skillId = weaponSkill.getSkillId();
		if (skillId != 0) {
			final L1Skills skill = SkillsTable.get().getTemplate(skillId);
			if ((skill != null) && skill.getTarget().equals("buff")) {
				if (!isFreeze(cha)) { // 凍結狀態or中
					cha.setSkillEffect(skillId,
							weaponSkill.getSkillTime() * 1000);
				}
			}
		}

		final int effectId = weaponSkill.getEffectId();
		// 具有動畫
		if (effectId > 0) {
			int chaId = 0;
			if (weaponSkill.getEffectTarget() == 0) {
				chaId = cha.getId();
				
			} else {
				chaId = pc.getId();
			}
			final boolean isArrowType = weaponSkill.isArrowType();
			if (!isArrowType) {
				pc.sendPacketsX8(new S_SkillSound(chaId, effectId));
				
			} else {
				final S_UseAttackSkill packet = new S_UseAttackSkill(
						pc, 
						cha.getId(),
						effectId, 
						cha.getX(), 
						cha.getY(),
						ActionCodes.ACTION_Attack, 
						false
						);
				pc.sendPacketsX8(packet);
			}
		}
		double damage = 0;
		final int randomDamage = weaponSkill.getRandomDamage();
		if (randomDamage != 0) {
			damage = _random.nextInt(randomDamage);
		}
		damage += weaponSkill.getFixDamage();

		final int area = weaponSkill.getArea();
		if ((area > 0) || (area == -1)) { // 範圍技能
			for (final L1Object object : World.get().getVisibleObjects(cha, area)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()) {
					continue;
				}
				if (object.getId() == cha.getId()) { // 攻擊對像L1Attack處理除外
					continue;
				}

				// 攻擊對像MOB場合、範圍內MOB當
				// 攻擊對像PC,Summon,Pet場合、範圍內PC,Summon,Pet,MOB當
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if ((cha instanceof L1PcInstance)
						|| (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					if (!((object instanceof L1PcInstance)
							|| (object instanceof L1SummonInstance)
							|| (object instanceof L1PetInstance) 
							|| (object instanceof L1MonsterInstance))) {
						continue;
					}
				}

				damage = calcDamageReduction(pc, (L1Character) object, damage, weaponSkill.getAttr());
				if (damage <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) object;
					// 受傷動作
					targetPc.sendPacketsX8(
							new S_DoActionGFX(
									targetPc.getId(), 
									ActionCodes.ACTION_Damage
									));
					targetPc.receiveDamage(pc, (int) damage, false, false);
					
				} else if ((object instanceof L1SummonInstance)
						|| (object instanceof L1PetInstance)
						|| (object instanceof L1MonsterInstance)) {
					final L1NpcInstance targetNpc = (L1NpcInstance) object;
					// 受傷動作
					targetNpc.broadcastPacketX8(
							new S_DoActionGFX(
									targetNpc.getId(), 
									ActionCodes.ACTION_Damage
									));
					targetNpc.receiveDamage(pc, (int) damage);
				}
			}
		}

		return calcDamageReduction(pc, cha, damage, weaponSkill.getAttr());
	}

	public static double getBaphometStaffDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (14 >= chance) {
			final int locx = cha.getX();
			final int locy = cha.getY();
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = (intel + sp) * (1.8 + bsk) + _random.nextInt(intel + sp) * 1.8;
			pc.sendPacketsAll(new S_EffectLocation(locx, locy, 129));
		}
		return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_EARTH);
	}

	public static double getDiceDaggerDamage(final L1PcInstance pc,
			final L1PcInstance targetPc, final L1ItemInstance weapon) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (3 >= chance) {
			dmg = targetPc.getCurrentHp() * 2 / 3;
			if (targetPc.getCurrentHp() - dmg < 0) {
				dmg = 0;
			}
			final String msg = weapon.getLogName();
			pc.sendPackets(new S_ServerMessage(158, msg));
			// \f1%0蒸發。
			pc.getInventory().removeItem(weapon, 1);
		}
		return dmg;
	}

	/**
	 * 底比斯武器魔法的效果
	 * @param pc
	 * @param targetPc
	 * @param weapon
	 * @return
	 */
	public static double getChaserDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (8 >= chance) {
			dmg = 8.0;
			pc.sendPacketsAll(new S_EffectLocation(cha.getX(), cha.getY(), 7025));
		}
		return dmg;
	}

	/**
	 * 奇古獸傷害計算
	 * @param pc
	 * @param cha
	 * @return
	 */
	public static double getKiringkuDamage(final L1PcInstance pc, final L1Character cha) {
		int dmg = 0;
		final int dice = 5;
		final int diceCount = 2;
		final int value = 14;
		int kiringkuDamage = 0;
		int charaIntelligence = 0;
		//final int getTargetMr = 0;
		// XXX 值本來、每違不明為、判明DSK固定。

		for (int i = 0; i < diceCount; i++) {
			kiringkuDamage += (_random.nextInt(dice) + 1);
		}
		kiringkuDamage += value;

		final int spByItem = pc.getSp() - pc.getTrueSp();
		charaIntelligence = pc.getInt() + spByItem - 12;
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}
		final double kiringkuCoefficientA = (1.0 + charaIntelligence * 3.0 / 32.0);

		kiringkuDamage *= kiringkuCoefficientA;

		final double kiringkuFloor = Math.floor(kiringkuDamage);

		dmg += kiringkuFloor + pc.getWeapon().getEnchantLevel();
		
		switch (pc.getWeapon().getItem().getGfxId()) {
		case 3018:// 藍寶石奇古獸
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 6983));
			break;
			
		default:
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7049));
			break;
		}

		return calcDamageReduction(pc, cha, dmg, 0);
	}

	public static double getAreaSkillWeaponDamage(final L1PcInstance pc,
			final L1Character cha, final int weaponId) {
		double dmg = 0;
		int probability = 0;
		int attr = 0;
		final int chance = _random.nextInt(100) + 1;
		if (weaponId == 263) { // 
			probability = 5;
			attr = L1Skills.ATTR_WATER;
		} else if (weaponId == 260) { // 
			probability = 4;
			attr = L1Skills.ATTR_WIND;
		}
		if (probability >= chance) {
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			int area = 0;
			int effectTargetId = 0;
			int effectId = 0;
			L1Character areaBase = cha;
			double damageRate = 0;

			if (weaponId == 263) { // 
				area = 3;
				damageRate = 1.4D;
				effectTargetId = cha.getId();
				effectId = 1804;
				areaBase = cha;
				
			} else if (weaponId == 260) { // 
				area = 4;
				damageRate = 1.5D;
				effectTargetId = pc.getId();
				effectId = 758;
				areaBase = pc;
			}
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = (intel + sp) * (damageRate + bsk) + _random.nextInt(intel + sp) * damageRate;
			pc.sendPacketsX8(new S_SkillSound(effectTargetId, effectId));

			for (final L1Object object : World.get().getVisibleObjects(
					areaBase, area)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()) {
					continue;
				}
				if (object.getId() == cha.getId()) { // 攻擊對像除外
					continue;
				}

				// 攻擊對像MOB場合、範圍內MOB當
				// 攻擊對像PC,Summon,Pet場合、範圍內PC,Summon,Pet,MOB當
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if ((cha instanceof L1PcInstance)
						|| (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					if (!((object instanceof L1PcInstance)
							|| (object instanceof L1SummonInstance)
							|| (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
						continue;
					}
				}

				dmg = calcDamageReduction(pc, (L1Character) object, dmg, attr);
				if (dmg <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) object;
					// 受傷動作
					targetPc.sendPacketsX8(
							new S_DoActionGFX(
									targetPc.getId(), 
									ActionCodes.ACTION_Damage
									));
					
					targetPc.receiveDamage(pc, (int) dmg, false, false);
					
				} else if ((object instanceof L1SummonInstance)
						|| (object instanceof L1PetInstance)
						|| (object instanceof L1MonsterInstance)) {
					final L1NpcInstance targetNpc = (L1NpcInstance) object;
					// 受傷動作
					targetNpc.broadcastPacketX8(
							new S_DoActionGFX(
									targetNpc.getId(), 
									ActionCodes.ACTION_Damage
									));
					targetNpc.receiveDamage(pc, (int) dmg);
				}
			}
		}
		return calcDamageReduction(pc, cha, dmg, attr);
	}

	public static double getLightningEdgeDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (4 >= chance) {
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = (intel + sp) * (2 + bsk) + _random.nextInt(intel + sp) * 2;

			pc.sendPacketsX8(new S_SkillSound(cha.getId(), 10));
		}
		return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_WIND);
	}

	public static void giveArkMageDiseaseEffect(final L1PcInstance pc, final L1Character cha) {
		final int chance = _random.nextInt(1000) + 1;
		int probability = (5 - ((cha.getMr() / 10) * 5)) * 10;
		if (probability == 0) {
			probability = 10;
		}
		if (probability >= chance) {
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, L1SkillId.DISEASE, cha.getId(), cha.getX(), cha.getY(), 0, L1SkillUse.TYPE_GMBUFF);
		}
	}

	/**
	 * 深紅之弩凍結
	 * @param pc
	 * @param cha
	 */
	public static void giveFettersEffect(final L1PcInstance pc, final L1Character cha) {
		final int fettersTime = 8;
		if (isFreeze(cha)) { // 凍結狀態or中
			return;
		}
		if ((_random.nextInt(100) + 1) <= 2) {
			L1SpawnUtil.spawnEffect(81182, fettersTime, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
			if (cha instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) cha;
				targetPc.setSkillEffect(STATUS_FREEZE, fettersTime);
				targetPc.sendPacketsX8(new S_SkillSound(targetPc.getId(), 4184));
				
				targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
				
			} else if ((cha instanceof L1MonsterInstance)
					|| (cha instanceof L1SummonInstance)
					|| (cha instanceof L1PetInstance)) {
				final L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setSkillEffect(STATUS_FREEZE, fettersTime);
				npc.broadcastPacketX8(new S_SkillSound(npc.getId(), 4184));
				npc.setParalyzed(true);
			}
		}
	}

	public static double calcDamageReduction(final L1PcInstance pc, final L1Character cha,
			double dmg, final int attr) {
		// 凍結狀態or中
		if (isFreeze(cha)) {
			return 0;
		}

		// MR輕減
		final int mr = cha.getMr();
		double mrFloor = 0;
		if (mr <= 100) {
			mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2);
		} else if (mr >= 100) {
			mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10);
		}
		double mrCoefficient = 0;
		if (mr <= 100) {
			mrCoefficient = 1 - 0.01 * mrFloor;
		} else if (mr >= 100) {
			mrCoefficient = 0.6 - 0.01 * mrFloor;
		}
		dmg *= mrCoefficient;

		// 屬性輕減
		int resist = 0;
		if (attr == L1Skills.ATTR_EARTH) {
			resist = cha.getEarth();
		} else if (attr == L1Skills.ATTR_FIRE) {
			resist = cha.getFire();
		} else if (attr == L1Skills.ATTR_WATER) {
			resist = cha.getWater();
		} else if (attr == L1Skills.ATTR_WIND) {
			resist = cha.getWind();
		}
		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}
		final double attrDeffence = resistFloor / 32.0;
		dmg = (1.0 - attrDeffence) * dmg;

		return dmg;
	}

	/**
	 * 凍結中
	 * @param cha
	 * @return
	 */
	public static boolean isFreeze(final L1Character cha) {
		if (cha.hasSkillEffect(STATUS_FREEZE)) {
			return true;
		}
		if (cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
			return true;
		}
		if (cha.hasSkillEffect(ICE_LANCE)) {
			return true;
		}
		if (cha.hasSkillEffect(FREEZING_BLIZZARD)) {
			return true;
		}
		if (cha.hasSkillEffect(EARTH_BIND)) {
			return true;
		}

		// 判定
		if (cha.hasSkillEffect(COUNTER_MAGIC)) {
			cha.removeSkillEffect(COUNTER_MAGIC);
			final int castgfx = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx();
			cha.broadcastPacketX8(new S_SkillSound(cha.getId(), castgfx));
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 瘋狂流星雨
	 * @param _pc
	 * @param cha
	 * @param count
	 */
	public static void Crazymeteorshower(L1PcInstance _pc, L1Character cha, int count) {
		GeneralThreadPool.get().execute(new Crazymeteorshowertime(_pc, cha, count));
	}

	/**
	 * 繳械投降
	 * @param _pc 施展者
	 * @param _target 目標
	 */
	public static void Disarmsurrender(L1PcInstance _targetPc) {
		if (_targetPc.getWeapon() != null) {
			L1PcInventory pcInventory = (L1PcInventory) _targetPc.getInventory();
			pcInventory.setEquipped(_targetPc.getWeapon(), false, false, false);
			_targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 172));
			_targetPc.sendPackets(new S_SystemMessage("你已被繳械,武器被取下了."));
		}
	}
	
	/**
	 * 特殊魔法變形術
	 * @param _targetPc 目標
	 */
	public static void ShapeChange(L1PcInstance _targetPc) {
		int[] polyList = { 936, 3134, 1642, 931, 96, 4038, 938, 929, 1540,
				3783, 2145, 934, 3918, 3199, 3184, 3132, 3107, 3188, 3211,
				3143, 3182, 3156, 3154, 3178, 4133, 5089, 945, 4171, 2541,
				1649, 29 };
		int i = _random.nextInt(polyList.length);
		L1PolyMorph.doPoly(_targetPc, polyList[i], 3, L1PolyMorph.MORPH_BY_NPC);
	}
	
	/**
	 * 破甲攻擊
	 * @param _targetPc 目標
	 * @param EffectTime 持續時間
	 */
	public static void ArmorBreak(L1PcInstance _targetPc, int EffectTime) {
		if (!_targetPc.hasSkillEffect(L1SkillId.No_ReductionDmg)) {
			_targetPc.setSkillEffect(L1SkillId.No_ReductionDmg, EffectTime * 1000);
		}
	}
	
	/**
	 * 虛弱攻擊
	 * @param _targetPc 目標
	 * @param EffectTime 持續時間
	 */
	public static void FrailAttack(L1PcInstance _targetPc, int EffectTime) {
		if (!_targetPc.hasSkillEffect(L1SkillId.Lower_Dmg)) {
			_targetPc.setSkillEffect(L1SkillId.Lower_Dmg, EffectTime * 1000);
		}
	}

	/**
	 * 施毒術
	 * @param _target 目標
	 * @param EffectTime 持續時間
	 */
	public static void DrugApplication(L1Character _target, int EffectTime) {
		if (_target instanceof L1PcInstance) {// 目標只能是玩家
			final L1PcInstance tgpc = (L1PcInstance) _target;
			if (tgpc.getBloodletting() >= 5) {// 當DEBUFF達到5層時,停止疊加
				return;
			}
			tgpc.setSkillEffect(L1SkillId.Bloodletting, EffectTime * 1000);
			tgpc.sendPacketsAll(new S_Poison(tgpc.getId(), 1));
			if (tgpc.hasSkillEffect(L1SkillId.Bloodletting)) {
				int blood = tgpc.getBloodletting() + 1;
				if (blood >= 5) {
					blood = 5;
				}
				tgpc.setBloodletting(blood);
			}
		}
	}

	/**
	 * 魅惑
	 * @param _target 目標
	 * @param EffectTime 持續時間
	 */
	public static void Enchantment(L1Character _target, int EffectTime) {
		_target.setSkillEffect(L1SkillId.Enchantment, EffectTime * 1000);
	}
}
	/**
	 * 瘋狂流星雨
	 * 類名稱：Crazymeteorshowertime<br>
	 * 創建人:xljnet<br>
	 * 修改時間：2018年4月30日 下午1:08:53<br>
	 * 修改人:QQ:759347094<br>
	 * 修改備註:<br>
	 * @version Rev:3.2 Bin:81222<br>
	 */
class Crazymeteorshowertime implements Runnable {
	private L1PcInstance _pc;
	private L1Character _cha;
	private int _count;

	Crazymeteorshowertime(L1PcInstance pc, L1Character cha, int count) {
		_pc = pc;
		_cha = cha;
		_count = count;
	}

	@Override
	public void run() {
		try {
			Random _random = new Random();
			double damage = 0;

			for (L1Object object : World.get().getVisibleObjects(_pc, 10)) {// 10格內
				damage = _pc.getStr() + _pc.getInt() + _pc.getDex() + _pc.getLevel();
				damage = Math.min(
						_random.nextInt((int) damage) + _pc.getLevel(), (damage * 2));
				damage = L1WeaponSkill.calcDamageReduction(_pc, (L1Character) _cha, damage, L1Skills.ATTR_FIRE);

				if (damage <= 0) {
					continue;
				}

				if (object instanceof L1MonsterInstance) {
					((L1NpcInstance) object)
							.broadcastPacketX10(new S_DoActionGFX(object.getId(), ActionCodes.ACTION_Damage));
					((L1NpcInstance) object).receiveDamage(_pc, (int) damage);
				}
			}

			if (_cha instanceof L1MonsterInstance) {
				int x;
				int y;
				for (int i = _count; i > 0; i--) {
					if (_random.nextInt(11) > 5) {
						x = _cha.getX() + _random.nextInt(8) + 1;
					} else {
						x = _cha.getX() - _random.nextInt(8) + 1;
					}
					if (_random.nextInt(11) > 5) {
						y = _cha.getY() + _random.nextInt(8) + 1;
					} else {
						y = _cha.getY() - _random.nextInt(8) + 1;
					}
					int[] data = { ActionCodes.ACTION_SkillAttack, 0, 762, 8 };
					_pc.sendPacketsX10(new S_UseAttackSkill(_cha, 0, x, y, data, false));
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {

		}
	}
}