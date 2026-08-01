package com.lineage.server.model;

import java.util.ConcurrentModificationException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 攻擊判定
 * @author dexc
 *
 */
public abstract class L1AttackMode {

	private static final Log _log = LogFactory.getLog(L1AttackMode.class);

	// 目標物件
	protected L1Character _target = null;

	// 執行PC
	protected L1PcInstance _pc = null;

	// 目標PC
	protected L1PcInstance _targetPc = null;

	// 執行NPC
	protected L1NpcInstance _npc = null;

	// 目標NPC
	protected L1NpcInstance _targetNpc = null;

	protected int _targetId;

	protected int _targetX;

	protected int _targetY;

	protected int _statusDamage = 0;

	protected int _hitRate = 0;

	protected int _calcType;

	protected static final int PC_PC = 1;

	protected static final int PC_NPC = 2;

	protected static final int NPC_PC = 3;

	protected static final int NPC_NPC = 4;

	protected boolean _isHit = false;

	protected int _damage = 0;

	protected int _drainMana = 0;

	protected int _drainHp = 0;

	protected int _attckGrfxId = 0;

	protected int _attckActId = 0;

	// 攻擊者場合武器情報
	protected L1ItemInstance _weapon = null;

	protected int _weaponId = 0;

	protected int _weaponType = 0;

	protected int _weaponType2 = 0;

	protected int _weaponAddHit = 0;// 命中追加

	protected int _weaponAddDmg = 0;// 傷害追加

	protected int _weaponAddPVPDmg = 0;// PVP傷害追加

	protected int _weaponSmall = 0;// 對小型

	protected int _weaponLarge = 0;// 對大型

	protected int _weaponRange = 1;// 武器攻擊距離

	protected int _weaponBless = 1;// 祝福類型

	protected int _weaponEnchant = 0;// 強化質

	protected int _weaponMaterial = 0;// 材質

	protected int _weaponDoubleDmgChance = 0;

	protected int _weaponAttrEnchantKind = 0;

	protected int _weaponAttrEnchantLevel = 0;

	protected L1ItemInstance _arrow = null;

	protected L1ItemInstance _sting = null;

	protected int _leverage = 10; // 攻擊倍率(1/10)

	protected static final Random _random = new Random();


	
	
	/**
	 * 傷害為0
	 * @param pc
	 * @return true 傷害為0
	 */
	protected static boolean dmg0(final L1Character character) {
		try {
			if (character == null) {
				return false;
			}
			
			if (character.getSkillisEmpty()) {
				return false;
			}
			
			if (character.getSkillEffect().size() <= 0) {
				return false;
			}

			for (final Integer key : character.getSkillEffect()) {
				final Integer integer = L1AttackList.SKM0.get(key);
				if (integer != null) {
					return true;
				}
			}
			
		} catch (final ConcurrentModificationException e) {
			// 技能取回發生其他線程進行修改
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 技能增加閃避
	 * @param character
	 * @return
	 */
	protected static int attackerDice(final L1Character character) {
		/*int attackerDice = 0;
		try {
			if (character == null) {
				return 0;
			}
			
			if (character.isDead()) {// 死亡
				return 0;
			}

			if (character.getSkillisEmpty()) {// 無技能狀態
				return 0;
			}

			if (character.getSkillEffect().size() <= 0) {// 無技能狀態
				return 0;
			}

			for (final Integer key : character.getSkillEffect()) {
				final Integer integer = L1AttackList.SKU3.get(key);
				if (integer != null) {
					attackerDice += integer;
				}
			}
			
		} catch (final ConcurrentModificationException e) {
			// 技能取回發生其他線程進行修改
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}*/
		int attackerDice = 0;
		if (character.get_dodge() > 0) {
			attackerDice -= character.get_dodge();
		}
		if (character.get_dodge_down() > 0) {
			attackerDice += character.get_dodge_down();
		}
		return attackerDice;
	}
	
	/**
	 * 攻擊倍率(1/10)
	 * @param i
	 */
	public void setLeverage(final int i) {
		_leverage = i;
	}

	/**
	 * 攻擊倍率(1/10)
	 * @return
	 */
	protected int getLeverage() {
		return _leverage;
	}

	public void setActId(final int actId) {
		_attckActId = actId;
	}

	public void setGfxId(final int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}
	
	/**
	 * ER迴避率
	 * @return true:命中 false:未命中
	 */
	protected boolean calcErEvasion() {
		final int er = _targetPc.getEr();
		final int rnd = _random.nextInt(100) + 1;
		return er < rnd;
	}
	
	/**
	 * 迴避
	 * @return true:迴避成功 false:迴避未成功
	 */
	protected boolean calcEvasion() {
		if (_targetPc == null) {
			return false;
		}
		final int ev = _targetPc.get_evasion();
		if (ev == 0) {
			return false;
		}
		final int rnd = _random.nextInt(1000) + 1;
		return ev >= rnd;
	}

	/**
	 * PC防禦力傷害直減低
	 * @return
	 */
	   protected int calcPcDefense() {
	        try {
	            if (_targetPc != null) {
	                final int ac = Math.max(0, 10 - _targetPc.getAc());

	                final int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
	              /*  if (acDefMax != 0) {
	                    final int srcacd = Math.max(1, (acDefMax >> 3));
	                    final int acdown = _random.nextInt(acDefMax) + srcacd;*/
	                return _random.nextInt(acDefMax + 1);
	                
	            }

	        } catch (final Exception e) {
	            _log.error(e.getLocalizedMessage(), e);
	        }
	        return 0;
	    }

	/**
	 * (NPC防禦力 + 額外傷害減低) 傷害減低
	 * @return
	 */
	protected int calcNpcDamageReduction() {
		// TEST
		int damagereduction = _targetNpc.getNpcTemplate().get_damagereduction();// 額外傷害減低
		try {
			final int srcac = _targetNpc.getAc();
			final int ac = Math.max(0, 10 - srcac);

			final int acDefMax = ac / 8;// 防禦力傷害減免降低1/7 XXX
			if (acDefMax != 0) {
				final int srcacd = Math.max(1, acDefMax);// XXX
				return _random.nextInt(acDefMax) + srcacd + damagereduction;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		return damagereduction;
	}

	/**
	 * 反擊屏障的傷害反擊計算
	 * @return
	 */
	protected int calcCounterBarrierDamage() {
		int damage = 0;
		try {
			// 反擊對象是PC
			if (_targetPc != null) {
				final L1ItemInstance weapon = _targetPc.getWeapon();
				if (weapon != null) {
					if (weapon.getItem().getType() == 3) { // 雙手劍
						int attr_DmgModifier = 0;
						int attr_DmgLarge = 0;
					//	L1ItemSpecialAttributeChar attr_char = _weapon.get_ItemAttrName();

						// (BIG最大+強化數+追加)*2
						// (>> 1: 除)  (<< 1: 乘)
						damage = (weapon.getItem().getDmgLarge() + attr_DmgLarge +
								weapon.getEnchantLevel() + 
								weapon.getItem().getDmgModifier() + attr_DmgModifier) << 1;// * 2;
					}
				}
				
			// 反擊對象是NPC
			} else if (_targetNpc != null) {
				// (>> 1: 除)  (<< 1: 乘)
				damage = (_targetNpc.getStr() + _targetNpc.getLevel()) << 1;// * 2;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return damage;
	}


	
	public abstract boolean calcHit();

	/**
	 * 攻擊資訊送出
	 */
	public abstract void action();

	/**
	 * 傷害計算
	 * @return
	 */
	public abstract int calcDamage();

	/**
	 * 底比斯武器魔法的效果
	 */
	//public abstract void addChaserAttack();

	/**
	 * 武器MP吸收量計算
	 */
	public abstract void calcStaffOfMana();

	/**
	 * 計算結果反映
	 */
	public abstract void commit();

	/**
	 * 受到反擊屏障傷害表示
	 */
	//public abstract void actionCounterBarrier();

	/**
	 * 攻擊使用武器是否為近距離武器判斷
	 * @return
	 */
	public abstract boolean isShortDistance();

	/**
	 * 反擊屏障的傷害反擊
	 */
	public abstract void commitCounterBarrier();

	/**
	 * 疼痛的歡愉的傷害反擊
	 */
	//public abstract void commitJoyOfPain();

	/**
	 * 致命身軀的傷害反擊
	 */
	//public abstract void commitMortalBody();

}
