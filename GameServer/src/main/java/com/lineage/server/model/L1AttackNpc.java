package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;

import java.util.ConcurrentModificationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.poison.L1ParalysisPoison;
import com.lineage.server.model.poison.L1SilencePoison;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_AttackPacketNpc;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;

/**
 * 攻擊判定
 * 
 * @author dexc
 * 
 */
public class L1AttackNpc extends L1AttackMode {

    private static final Log _log = LogFactory.getLog(L1AttackNpc.class);

    public L1AttackNpc(final L1NpcInstance attacker, final L1Character target) {
        if (attacker == null) {
            return;
        }
        if (target == null) {
            return;
        }
        if (target.isDead()) {
            return;
        }
        if (target.getCurrentHp() <= 0) {
            return;
        }
        _npc = attacker;
        if (target instanceof L1PcInstance) {
            _targetPc = (L1PcInstance) target;
            _calcType = NPC_PC;

        } else if (target instanceof L1NpcInstance) {
            _targetNpc = (L1NpcInstance) target;
            _calcType = NPC_NPC;
        }
        _target = target;
        _targetId = target.getId();
        _targetX = target.getX();
        _targetY = target.getY();
    }

    /**
     * 命中判定
     */
    @Override
    public boolean calcHit() {
        if (_target == null) {// 物件遺失
            _isHit = false;
            return _isHit;
        }
        switch (_calcType) {
        case NPC_PC:
            _isHit = calcPcHit();
            break;

        case NPC_NPC:
            _isHit = calcNpcHit();
            break;
        }
        return _isHit;
    }

    /**
     * NPC對PC命中
     * 
     * @return
     */
    private boolean calcPcHit() {
        // 傷害為0
        if (dmg0(_targetPc)) {
            return false;
        }

        // 召喚怪 打玩家 安全區
        if (_npc instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) _npc;
            //if (summon.isExsistMaster()) {
                if (summon.isSafetyZone() || _targetPc.isSafetyZone()) {
                    return false;
                }
        }
        // 寵物怪 打玩家 安全區
        if (_npc instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) _npc;
            //if (pet.getMaster() != null) {
                if (pet.isSafetyZone() || _targetPc.isSafetyZone()) {
                    return false;
                }
            //}
        }

        // 迴避攻擊
        if (calcEvasion()) {
            return false;
        }

        // this._hitRate += this._npc.getLevel();// XXX
        _hitRate += _npc.getLevel() + 5;

        if (_npc instanceof L1PetInstance) { // 寵物武器命中追加
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        _hitRate += _npc.getHitup();

        // int attackerDice = _random.nextInt(20) + 1 + this._hitRate - 1;// XXX
        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 3;

        // 技能增加閃避
        attackerDice += attackerDice(_targetPc);

        // 防禦力抵銷
        int defenderDice = 0;

        final int defenderValue = (_targetPc.getAc()) * -1;

        if (_targetPc.getAc() >= 0) {
            defenderDice = 10 - _targetPc.getAc();

        } else if (_targetPc.getAc() < 0) {
            defenderDice = 10 + _random.nextInt(defenderValue) + 1;
        }

        // 基礎命中
        final int fumble = _hitRate;
        // 基礎命中 + 19
        final int critical = _hitRate + 19;

        if (attackerDice <= fumble) {
            _hitRate = 15;

        } else if (attackerDice >= critical) {
            _hitRate = 100;

        } else {
            // 防禦力抵銷
            if (attackerDice > defenderDice) {
                _hitRate = 100;

            } else if (attackerDice <= defenderDice) {
                _hitRate = 15;
            }
        }

        // BOSS XXX
        if (_npc.getNpcTemplate().is_boss()) {
            attackerDice += 30;// 2011-12-10(15)
        }
    	// 中魅惑DEBUFF降低50%命中
		if (_targetPc.hasSkillEffect(L1SkillId.Enchantment)) {
			_hitRate -= (_hitRate * 0.5);
		}
        // 比較用機率
        final int rnd = _random.nextInt(100) + 1;

        // NPC攻擊距離2格以上附加ER計算
        if ((_npc.get_ranged() >= 10) && (_hitRate > rnd) && (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) >= 2)) {
            return calcErEvasion();
        }

     //   _targetPc.sendPacketsAll(new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
        return _hitRate >= rnd;
    }

    /**
     * NPC對NPC命中
     * 
     * @return
     */
    private boolean calcNpcHit() {
        // 傷害為0
        if (dmg0(_targetNpc)) {
            return false;
        }

        // this._hitRate += this._npc.getLevel();// XXX
        _hitRate += _npc.getLevel() + 3;

        if (_npc instanceof L1PetInstance) { // 寵物武器命中追加
            _hitRate += ((L1PetInstance) _npc).getHitByWeapon();
        }

        _hitRate += _npc.getHitup();

        // int attackerDice = _random.nextInt(20) + 1 + this._hitRate - 1;// XXX
        int attackerDice = _random.nextInt(20) + 1 + _hitRate - 3;

        // 技能增加閃避
        attackerDice += attackerDice(_targetNpc);

        // BOSS XXX
        if (_npc.getNpcTemplate().is_boss()) {
            attackerDice += 30;// 2011-12-10(10)
        }

        int defenderDice = 0;

        final int defenderValue = (_targetNpc.getAc()) * -1;

        if (_targetNpc.getAc() >= 0) {
            defenderDice = 10 - _targetNpc.getAc();

        } else if (_targetNpc.getAc() < 0) {
            defenderDice = 10 + _random.nextInt(defenderValue) + 1;
        }

        final int fumble = _hitRate;
        final int critical = _hitRate + 19;

        if (attackerDice <= fumble) {
            _hitRate = 15;

        } else if (attackerDice >= critical) {
            _hitRate = 100;

        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 100;

            } else if (attackerDice <= defenderDice) {
                _hitRate = 15;
            }
        }

        final int rnd = _random.nextInt(100) + 1;
        return _hitRate >= rnd;
    }

    /**
     * 傷害計算
     */
    @Override
    public int calcDamage() {
        switch (_calcType) {
        case NPC_PC:
            _damage = calcPcDamage();
            break;

        case NPC_NPC:
            _damage = calcNpcDamage();
            break;
        }
        return _damage;
    }

    /**
     * NPC基礎傷害提升計算
     * 
     * @return
     */
    private double npcDmgMode(double dmg) {
        // 暴擊
       /* if (_random.nextInt(100) < 15) {
            dmg *= 1.70;
        }*/

        dmg += _npc.getDmgup();

        if (isUndeadDamage()) {
            dmg *= 1.20;
        }

        dmg = (int) (dmg * (getLeverage() / 10D));

        // BOSS XXX
        if (_npc.getNpcTemplate().is_boss()) {
            dmg *= 1.70;// 2011-12-10(1.45)
        }

        if (_npc.isWeaponBreaked()) { // ＮＰＣ中。
            dmg /= 2;
        }

        return dmg;
    }

    /**
     * NPC對PC傷害
     * 
     * @return
     */
	private int calcPcDamage() {
		if (_targetPc == null) {
			return 0;
		}
		// 傷害為0
		if (dmg0(_targetPc)) {
			_isHit = false;
			return 0;
		}
		
		final int lvl = _npc.getLevel();
		double dmg = 0D;

		final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
		dmg = _random.nextInt(lvl) + (_npc.getStr() * 0.5) + dmgStr;

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 7); // 每7級追加1點攻擊力 XXX
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

    
        // NPC基礎傷害提升計算
        dmg = npcDmgMode(dmg);

        dmg -= calcPcDefense();// 防禦減傷
        dmg -= _targetPc.getEquipSlot().getAttachReductionDamage();
        dmg -= _targetPc.getEquipSlot().getAttachAc();
    /*	final int ac = Math.max(0, 10 - _targetPc.getAc());
		final int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
		dmg -= _random.nextInt(acDefMax + 1);      */
			//	 _targetPc.sendPackets(new S_System*/Message("減少傷害1:" + acDefMax));
              //    _targetPc.sendPackets(new S_SystemMessage("減少傷害2:" + dmg));
       // dmg -= calcPcDefense();// 被攻擊者防禦力傷害直減低
    
		// _targetPc.sendPackets(new S_SystemMessage("減少傷害1:" + acDefMax));
       //  _targetPc.sendPackets(new S_SystemMessage("減少傷害2:" + dmg));
		
		// 防具傷害減免
        dmg -= _targetPc.getDamageReductionByArmor()  +  _targetPc.get_reduction_dmg(); // 被攻擊者防具額外傷害減免

        dmg -= _targetPc.dmgDowe(); // 機率傷害減免

       /* if (_targetPc.getClanid() != 0) {
            dmg -= getDamageReductionByClan(_targetPc);// 被攻擊者血盟技能傷害減免
        }*/

        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            final int targetPcLvl = Math.max(_targetPc.getLevel(), 50);
            dmg -= (targetPcLvl - 50) / 5 + 1;
        }
    
        boolean dmgX2 = false;// 傷害除2
        // 取回技能
        if (!_targetPc.getSkillisEmpty() && _targetPc.getSkillEffect().size() > 0) {
            try {
                for (final Integer key : _targetPc.getSkillEffect()) {
                    final Integer integer = L1AttackList.SKD3.get(key);
                    if (integer != null) {
                        if (integer.equals(key)) {
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
            dmg /= 2;
        }
     /*   if(_targetPc.getLocation().getTileDistance(
        		_npc.getLocation())<=3 &&_targetPc.isActived() && !_targetPc.hasSkillEffect(100611)){
        	_targetPc.targetClear();
        	_targetPc._hateList.add(_npc, 0);
        	_targetPc.setNowTarget(_npc);        	     		
        }*/
        // 、攻擊
        boolean isNowWar = false;
        final int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
        if (castleId > 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        if (!isNowWar) {
            if (_npc instanceof L1PetInstance) {
                dmg = 1;
            }
            if (_npc instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) _npc;
                if (summon.isExsistMaster()) {
                    if (!summon.isSkillSpawn()) {
                        dmg = 1;
                    }
                }
            }
        }

        if (dmg <= 0) {
            _isHit = false;
        }

        addNpcPoisonAttack(_targetPc);

        // 未命中 傷害歸0
        if (!_isHit) {
            dmg = 0.0;
        }
        if (_targetPc.isKnight()) {
            if (_npc.getNpcTemplate().is_boss()) {
                dmg -= 3;
            } else {
                dmg -= 3;
            }
            if (dmg <= 0) {
                dmg = 0;
            }
        }
        dmg -= dmg * ConfigOther.ATTACK_DEF;
        if (dmg <= 0) {
            dmg = 0;
        }

        // double tempDmg = dmg;
       /* L1MobDmg mobDmg = MobDmgTable.get().getAll(_npc.getNpcId());
        if (mobDmg != null) {
            dmg = dmg / mobDmg.get_dmg();
        }*/
        // hSystem.out.println("原始傷害：" + tempDmg + " 修正傷害：" + dmg);
        //System.out.println("降低前DMG:" + dmg);
      //  dmg = dmg * ConfigOther.DMGRE;//
        //System.out.println("降低後DMG:" + dmg);
        if (_targetPc.isCrown()) {
            dmg *= 1.3D;
        }
        return (int) dmg;
    }

    /**
     * NPC對NPC傷害
     * 
     * @return
     */
    private int calcNpcDamage() {
        if (_targetNpc == null) {
            return 0;
        }

        // 傷害為0
        if (dmg0(_targetNpc)) {
            _isHit = false;
            return 0;
        }

        final int lvl = _npc.getLevel();
        double dmg = 0;

        if (_npc instanceof L1PetInstance) {
            dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + (_npc.getStr() / 2) + 1;
            // dmg += (lvl / 16); // 每16級追加1點攻擊力// TEST
            dmg += (lvl / 14); // 每14級追加1點攻擊力 XXX
            dmg += ((L1PetInstance) _npc).getDamageByWeapon();

        } else {
            final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
            dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + dmgStr;
        }

        // NPC基礎傷害提升計算
        dmg = npcDmgMode(dmg);

        dmg -= calcNpcDamageReduction();// NPC防禦力傷害直減低

        addNpcPoisonAttack(_targetNpc);

        if (dmg <= 0) {
            _isHit = false;
        }

        // 未命中 傷害歸0
        if (!_isHit) {
            dmg = 0.0;
        }

        return (int) dmg;
    }

    /**
     * ＮＰＣ夜間攻擊力變化
     * 
     * @return
     */
    private boolean isUndeadDamage() {
        boolean flag = false;
        final int undead = this._npc.getNpcTemplate().get_undead();
        final boolean isNight = L1GameTimeClock.getInstance().currentTime().isNight();
        if (isNight) {
            switch (undead) {
            case 1:
            case 3:
            case 4:
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * ＮＰＣ毒攻擊付加
     * 
     * @param attacker
     * @param target
     */
    private void addNpcPoisonAttack(final L1Character target) {
        switch (_npc.getNpcTemplate().get_poisonatk()) {
        case 1:// 通常毒
            if (15 >= _random.nextInt(100) + 1) {
                // 3秒週期5
                L1DamagePoison.doInfection(_npc, target, 3000, 5);
            }
            break;

        case 2:// 沈默毒
            if (15 >= _random.nextInt(100) + 1) {
                L1SilencePoison.doInfection(target);
            }
            break;

        case 4:// 麻痺毒
            if (15 >= _random.nextInt(100) + 1) {
                // 20秒後45秒間麻痺
                L1ParalysisPoison.doInfection(target, 20000, 45000);
            }
            break;
        }
        if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // 麻痺攻擊
        }
    }

    /**
     * 攻擊資訊送出
     */
    @Override
    public void action() {
        try {
            if (_npc == null) {
                return;
            }
            if (_npc.isDead()) {
                return;
            }

            _npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 設置新面向

            // 距離2格以上攻擊
            final boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
            int bowActId = _npc.getBowActId();

            // 遠距離武器
            if (isLongRange && (bowActId > 0)) {
                actionX1();

                // 近距離武器
            } else {
                actionX2();
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 遠距離武器
     */
	private void actionX1() {
		try {
			int bowActId = _npc.getBowActId();
			// 攻擊資訊封包
			_npc.broadcastPacketX10(
					new S_UseArrowSkill(
							_npc, 
							_targetId, 
							bowActId,
							_targetX, 
							_targetY, 
							_damage
							));
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

    /**
     * 近距離武器
     */
    private void actionX2()
    {
      try
      {
        int actId = 1;//預設攻擊動作
        if (getActId() > 1) {//有指定攻擊動作編號
          actId = getActId();
        }      
        
        	//特定外型改變攻擊動作
  	    switch (_npc.getTempCharGfx()) {	    
  	    case 1780://烈炎獸
  	    case 7430:
  	    case 13076:
  	    	actId = 30;
  	    	break;
  	    case 2757://吸血鬼
  	    case 4104:
  	    case 13096:
  	    	actId = 18;
  	    	break;
  	    }

        if (_isHit) {//命中   	  
          if (getGfxId() > 0) {//有動畫編號        
            _npc.broadcastPacketAll(
              new S_UseAttackSkill(
              _npc, 
              _target.getId(), 
              getGfxId(), 
              _targetX, 
              _targetY, 
              actId, 
              _damage));
          }
          else {//沒有動畫編號
            _npc.broadcastPacketAll(
              new S_AttackPacketNpc(
              _npc, 
              _target, 
              actId, 
              _damage));
          }
        }
        else if (getGfxId() > 0) {//未命中 有動畫編號     
          _npc.broadcastPacketAll(
            new S_UseAttackSkill(
            _target, 
            _npc.getId(), 
            getGfxId(), 
            _targetX, 
            _targetY, 
            actId, 
            0));
        }
        else {//未命中 沒動畫編號
      	  _npc.broadcastPacketAll(new S_AttackPacketNpc(_npc, _target, actId));
        }

      }
      catch (Exception e)
      {
        _log.error(e.getLocalizedMessage(), e);
      }
    }



    /**
     * 計算結果反映
     */
    @Override
    public void commit() {
        if (_isHit) {
            switch (_calcType) {
            case NPC_PC:
                commitPc();
                break;

            case NPC_NPC:
                commitNpc();
                break;
            }
        }

        // gm攻擊資訊
        if (!ConfigAlt.ALT_ATKMSG) {
            return;

        } else {
            if (_calcType == NPC_NPC) {
                return;
            }
            if (!_targetPc.isGm()) {
                return;
            }
        }

        final String srcatk = _npc.getName();
        ;// 攻擊者
        final String tgatk = _targetPc.getName();
        ;// 被攻擊者
        final String dmginfo = _isHit ? "傷害:" + _damage : "失敗";// 傷害
        final String hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetPc.getCurrentHp();// 資訊
        final String x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;

        _targetPc.sendPackets(new S_ServerMessage(166, "受到NPC攻擊: " + x));

    }

    /**
     * 對PC攻擊傷害結果
     */
    private void commitPc() {
        _targetPc.receiveDamage(_npc, _damage, false, false);
		if (_damage > 0 && ConfigOther.poly_Mlist.contains(_targetPc.getTempCharGfx())
				&& !_targetPc.hasSkillEffect(L1SkillId.bddzpoly)) {
			_targetPc.sendPackets(new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
			_targetPc.broadcastPacketAll(new S_DoActionGFX(_targetPc.getId(), ActionCodes.ACTION_Damage));
			_targetPc.setSkillEffect(L1SkillId.bddzpoly, 100);
		}
    }

    /**
     * 對NPC攻擊傷害結果
     */
    private void commitNpc() {
        _targetNpc.receiveDamage(_npc, _damage);
    }

    /**
     * 受到反擊屏障傷害表示
     */
    /*
     * @Override public void actionCounterBarrier() { // 受傷動作
     * _npc.broadcastPacketAll( new S_DoActionGFX(_npc.getId(),
     * ActionCodes.ACTION_Damage )); }
     */

    /**
     * 相手攻擊對有效判別
     */
    @Override
    public boolean isShortDistance() {
        boolean isShortDistance = true;
        final boolean isLongRange = (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) > 1);
        final int bowActId = _npc.getBowActId();
        // 距離2以上、攻擊者弓ID場合遠攻擊
        if (isLongRange && (bowActId > 0)) {
            isShortDistance = false;
        }
        return isShortDistance;
    }

    /**
     * 反擊屏障的傷害反擊
     */
    @Override
    public void commitCounterBarrier() {
        final int damage = this.calcCounterBarrierDamage();
        if (damage == 0) {
            return;
        }
        _npc.receiveDamage(_target, damage);
        // 受傷動作
        _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
        /*
         * if (_targetPc != null) { _npc.receiveDamage(_targetPc, damage); //
         * 受傷動作 _targetPc.sendPacketsAll( new S_DoActionGFX( _targetPc.getId(),
         * ActionCodes.ACTION_Damage ));
         * 
         * } else if (_targetNpc != null) { _npc.receiveDamage(_targetNpc,
         * damage); // 受傷動作 _targetNpc.broadcastPacketAll( new
         * S_DoActionGFX(_targetNpc.getId(), ActionCodes.ACTION_Damage ));
         * 
         * }
         */
    }

    /**
     * 疼痛的歡愉的傷害反擊
     */
    /*
     * @Override public void commitJoyOfPain() {
     * 
     * }
     */

    /**
     * 致命身軀的傷害反擊
     */
    /*
     * @Override public void commitMortalBody() {
     * 
     * }
     */

    @Override
    public void calcStaffOfMana() {
        // Auto-generated method stub
    }
}
