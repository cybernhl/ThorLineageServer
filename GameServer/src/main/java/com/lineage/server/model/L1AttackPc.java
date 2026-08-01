package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.BURNING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_VENOM;
import static com.lineage.server.model.skill.L1SkillId.FIRE_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static com.lineage.server.model.skill.L1SkillId.TRUE_TARGET;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import com.lineage.config.Config;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.event.FeatureItemSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.william.DexDmg;
import com.william.StrDmg;

/**
 * 物理攻擊判斷項(PC)
 *
 * @author dexc
 */
public class L1AttackPc extends L1AttackMode {

    private static final Log _log = LogFactory.getLog(L1AttackPc.class);

    // private int _weaponDamage;// 武器大小傷

    private int weaponTotalDamage;//武器總傷害

    private int hit_rnd;// 攻擊命中機率

    // 攻擊模式 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
    private byte _attackType = 0x00;


    public L1AttackPc(final L1PcInstance attacker, final L1Character target) {
        if (target == null) {
            return;
        }

        if (target.isDead()) {
            return;
        }

        _pc = attacker;

        if (target instanceof L1PcInstance) {
            _targetPc = (L1PcInstance) target;
            _calcType = PC_PC;

        } else if (target instanceof L1NpcInstance) {
            _targetNpc = (L1NpcInstance) target;
            _calcType = PC_NPC;
        }

        // 武器情報取得
        _weapon = this._pc.getWeapon();
        if (_weapon != null) {
            _weaponId = _weapon.getItem().getItemId();
            _weaponType = _weapon.getItem().getType1();
            _weaponType2 = _weapon.getItem().getType();
            _weaponAddHit = _weapon.getItem().getHitModifier() + _weapon.getHitByMagic() + _pc.getEquipSlot().getAttachHit();
            _weaponAddDmg = _weapon.getItem().getDmgModifier() + _weapon.getDmgByMagic();
            if (!ConfigOther.特殊額外攻擊) {
                _weaponAddDmg += _pc.getEquipSlot().getAttachOtherDamage();
            }
            _weaponAddPVPDmg = _pc.getEquipSlot().getAttachPVPDamage();

            _weaponSmall = _weapon.getItem().getDmgSmall() + _pc.getEquipSlot().getAttachDmgSmall();
            _weaponLarge = _weapon.getItem().getDmgLarge() + _pc.getEquipSlot().getAttachDmgLarge();
            _weaponRange = _weapon.getItem().getRange();
            _weaponBless = _weapon.getItem().getBless();

            if ((_weaponType != 20) && (_weaponType != 62)) {
                _weaponEnchant = _weapon.getEnchantLevel() - _weapon.get_durability(); // 損傷分

            } else {
                _weaponEnchant = _weapon.getEnchantLevel();
            }

            _weaponMaterial = _weapon.getItem().getMaterial();
            if (_weaponType == 20) {// 弓 武器類型:箭取回
                _arrow = _pc.getInventory().getArrow();
                if (_arrow != null) {
                    _weaponBless = _arrow.getItem().getBless();
                    _weaponMaterial = _arrow.getItem().getMaterial();
                }
            }

            if (_weaponType == 62) {// 鐵手甲 武器類型:飛刀取回
                _sting = _pc.getInventory().getSting();
                if (_sting != null) {
                    _weaponBless = _sting.getItem().getBless();
                    _weaponMaterial = _sting.getItem().getMaterial();
                }
            }

            _weaponDoubleDmgChance = _weapon.getItem().getDoubleDmgChance();
            _weaponAttrEnchantKind = _weapon.getAttrEnchantKind();
            _weaponAttrEnchantLevel = _weapon.getAttrEnchantLevel();
        }

        // 追加補正
        if (_weaponType == 20) {// 弓 增加敏捷傷害
            //  Integer dmg = L1AttackList.DEXD.get((int) _pc.getDex());
            int dmg = DexDmg.getDexDmgSkill(_pc, _pc.getDex());
            if (dmg != 0) {
                _statusDamage = dmg;
                // _pc.sendPackets(new S_SystemMessage("kk(顯示用不增加)敏捷計算:" + _statusDamage));
            }

        } else { // 以外ＳＴＲ值參照abstract
            int dmg = StrDmg.getStrDmgSkill(_pc, _pc.getStr());
            if (dmg != 0) {
                _statusDamage = dmg;
                // _pc.sendPackets(new S_SystemMessage("力量計算:" + _statusDamage));
            }
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

        if (this._target == null) {// 物件遺失
            this._isHit = false;
            return this._isHit;
        }

        if (this._weaponRange != -1) {

            // 近距離武器攻擊距離判斷
            final int location = this._pc.getLocation().getTileLineDistance(this._target.getLocation());

            if (location > (this._weaponRange + 1)) {
                this._isHit = false; // 射程範圍外
                return this._isHit;
            }

        } else {
            // 遠距離武器攻擊距離判斷
            if (!this._pc.getLocation().isInScreen(this._target.getLocation())) {
                this._isHit = false; // 射程範圍外
                return this._isHit;
            }
        }
        //沙哈
        if ((this._weaponType == 20) && (this._arrow == null) && (this._weaponId != 190) && (this._weaponId != 50091) && (this._weaponId != 573349) && (this._weaponId != 573190)) {
            this._isHit = false; // 持弓 無箭
       /* } else if ((this._weaponType == 20) && (this._weaponId != 500190) && (this._arrow == null)) {
            this._isHit = false;*/ // 持弓 無箭
        } else if ((this._weaponType == 62) && (this._sting == null)) {
            this._isHit = false; // 持鐵手甲 無飛刀

        } else if (!this._pc.glanceCheck(this._targetX, this._targetY)) {
            this._isHit = false; // 攻擊方向中途具有障礙

        } else if ((this._weaponId == 247) || (this._weaponId == 248) || (this._weaponId == 249)) {
            this._isHit = false; // 試煉武器

        } else if (this._calcType == PC_PC) {
            this._isHit = this.calcPcHit();// PC TO PC

        } else if (this._calcType == PC_NPC) {
            this._isHit = this.calcNpcHit();// PC TO NPC
        }

        return this._isHit;
    }

    private int str_dex_Hit() {
        int hitRate = 0;
        final int hitDex = DexDmg.getDexHitSkill(_pc, _pc.getDex());
        if (hitDex != 0) {
            hitRate += hitDex;
        }

        final int hitStr =
                StrDmg.getStrHitSkill(_pc, _pc.getStr());
        if (hitStr != 0) {
            hitRate += hitStr;
        }
        if (_pc.isWizard()) {
            hitRate += 5;
        }

        return hitRate;
    }

    /**
     * PC對PC的命中
     *
     * @return
     */
    private boolean calcPcHit() {
        if (_targetPc == null) {
            return false;
        }

        // 傷害為0
        if (dmg0(_targetPc)) {
            return false;
        }

        // 迴避攻擊
        if (calcEvasion()) {
            return false;
        }
        if (_pc.hasSkillEffect(8957)) {
            return true;
        }

      /*  if (_weaponType2 == 17) {// 奇古獸
            return true;
        }*/

        _hitRate = _pc.getLevel();//等級命中
        //  _pc.sendPackets(new S_SystemMessage("等級追加命中:" + _hitRate));
        // 力量命中補正 / 敏捷命中補正
        _hitRate += str_dex_Hit();
        //_pc.sendPackets(new S_SystemMessage("能力補足命中:" + _hitRate));

        if ((_weaponType != 20) && (_weaponType != 62)) {
            _hitRate += _weaponAddHit + _pc.getHitup() + (_weaponEnchant * 0.6);
            //  _pc.sendPackets(new S_SystemMessage("近戰武器追加命中分類追加:" + _weaponAddHit));
            //  _pc.sendPackets(new S_SystemMessage("近戰武器追加命中分類追加:" + _pc.getHitup()));
            //  _pc.sendPackets(new S_SystemMessage("近戰武器追加命中分類追加:" + _weaponEnchant));
        } else {
            _hitRate += _weaponAddHit + _pc.getBowHitup() + (_weaponEnchant * 0.6);
            //  _pc.sendPackets(new S_SystemMessage("遠戰武器追加命中分類追加:" + _weaponAddHit));
            // _pc.sendPackets(new S_SystemMessage("遠戰武器追加命中分類追加:" + _pc.getBowHitup()));
            // _pc.sendPackets(new S_SystemMessage("遠戰武器追加命中分類追加:" + _weaponEnchant));
        }


        if ((_weaponType != 20) && (_weaponType != 62)) { // 防具追加命中
            _hitRate += _pc.getHitModifierByArmor();

        } else {
            _hitRate += _pc.getBowHitModifierByArmor();
        }

        final int weight182 = _pc.getInventory().getWeight182();
        if (weight182 > 80) { // 重量命中補正
            if ((80 < weight182) && (120 >= weight182)) {
                _hitRate -= 1;

            } else if ((121 <= weight182) && (160 >= weight182)) {
                _hitRate -= 3;

            } else if ((161 <= weight182) && (200 >= weight182)) {
                _hitRate -= 5;
            }
        }

        _hitRate += hitUp();//料理其他
        // _pc.sendPackets(new S_SystemMessage("_hitRate追加命中:" + _hitRate));

        int tgPcAc = this._targetPc.getAc();
        if (tgPcAc < 0) {
            tgPcAc *= -1;
            if (tgPcAc <= 10) {
                _hitRate -= 1;
            } else if (tgPcAc <= 20) {
                _hitRate -= 1;
            } else if (tgPcAc <= 30) {
                _hitRate -= 2;
            } else if (tgPcAc <= 40) {
                _hitRate -= 2;
            } else if (tgPcAc <= 50) {
                _hitRate -= 3;
            } else if (tgPcAc <= 60) {
                _hitRate -= 3;
            } else if (tgPcAc <= 70) {
                _hitRate -= 4;
            } else if (tgPcAc <= 80) {
                _hitRate -= 5;
            } else if (tgPcAc <= 90) {
                _hitRate -= 6;
            } else if (tgPcAc <= 100) {
                _hitRate -= 7;
            } else if (tgPcAc <= 110) {
                _hitRate -= 8;
            } else if (tgPcAc <= 120) {
                _hitRate -= 9;
            } else if (tgPcAc <= 130) {
                _hitRate -= 10;
            } else if (tgPcAc <= 300) {
                _hitRate -= 11;
            }
        }
        //   _pc.sendPackets(new S_SystemMessage("新增防禦機率:" + _hitRate));
      /*  int tgPcAc = this._targetPc.getAc();
        if (this._weaponType != 20 && this._weaponType != 62) {
            int attackerDice = this._targetPc.get_dodge() * 10;
            final int tgChaDodgeDown = this._targetPc.get_dodge_down() * 10;
            if (tgChaDodgeDown > 0) {
                attackerDice -= tgChaDodgeDown;
            }
            int tgPcAc = this._targetPc.getAc();
            if (tgPcAc < 0) {
                tgPcAc *= -1;
                if (tgPcAc <= 10) {
                    attackerDice += 1;
                }
                else if (tgPcAc <= 20) {
                    attackerDice += 2;
                }
                else if (tgPcAc <= 30) {
                    attackerDice += 3;
                }
                else if (tgPcAc <= 40) {
                    attackerDice += 4;
                }
                else if (tgPcAc <= 50) {
                    attackerDice += 5;
                }
                else if (tgPcAc <= 60) {
                    attackerDice += 6;
                }
                else if (tgPcAc <= 70) {
                    attackerDice += 7;
                }
                else if (tgPcAc <= 80) {
                    attackerDice += 10;
                }
                else if (tgPcAc <= 90) {
                    attackerDice += 23;
                }
                else if (tgPcAc <= 100) {
                    attackerDice += 25;
                }
                else if (tgPcAc <= 110) {
                    attackerDice += 32;
                }
                else if (tgPcAc <= 120) {
                    attackerDice += 38;
                }
                else if (tgPcAc <= 130) {
                    attackerDice += 40;
                }
                else if (tgPcAc <= 300) {
                    attackerDice += 50;
                }
            }
            _pc.sendPackets(new S_SystemMessage("命中" + attackerDice));
            if (attackerDice > 0) {
                if (attackerDice > 95) {
                    attackerDice = 95;
                }
                final int dg_rnd = L1AttackPc._random.nextInt(100) + 1;
                if (attackerDice >= dg_rnd) {
                    this._hitRate = 0;
                }
            }
        }*/
        // _pc.sendPackets(new S_SystemMessage("上方計算命中:" + _hitRate));
        int attackerDice = _random.nextInt(20) + 2 + _hitRate - 10;
        //  _pc.sendPackets(new S_SystemMessage("初始計算機率" + attackerDice));
        // 技能增加閃避
        attackerDice += attackerDice(_targetPc);
        //   _pc.sendPackets(new S_SystemMessage("技能增加迴避" + attackerDice));

        int defenderDice = 0;

        final int defenderValue = (int) (_targetPc.getAc() * 1.5) * -1;
        //  _pc.sendPackets(new S_SystemMessage("對方防禦" + defenderValue));
        if (_targetPc.getAc() >= 0) {
            defenderDice = 10 - _targetPc.getAc();
            //     _pc.sendPackets(new S_SystemMessage("對方防禦>0:" + attackerDice));
        } else if (_targetPc.getAc() < 0) {
            defenderDice = 10 + _random.nextInt(defenderValue) + 1;
            //     _pc.sendPackets(new S_SystemMessage("對方防禦<0:" + attackerDice));
        }

        final int fumble = _hitRate - 9;
        final int critical = _hitRate + 10;


        if (attackerDice <= fumble) {
            _hitRate = 15;

        } else if (attackerDice >= critical) {
            _hitRate = 95;

        } else {
            if (attackerDice > defenderDice) {
                _hitRate = 95;

            } else if (attackerDice <= defenderDice) {
                _hitRate = 15;
            }
        }

        final int rnd = _random.nextInt(100) + 1;
        if (_weaponType == 20) {// 弓 附加ER計算
            if (_hitRate > rnd) {
                return calcErEvasion();
            }
        }
        return _hitRate >= rnd;
    }

    /**
     * PC對NPC的命中
     *
     * @return
     */
    private boolean calcNpcHit() {
        // 對不可見的怪物額外判斷
        final int gfxid = this._targetNpc.getNpcTemplate().get_gfxid();
        switch (gfxid) {
            case 2412:// 南瓜的影子
                if (!_pc.getInventory().checkEquipped(20046)) {// 南瓜帽
                    return false;
                }
                break;
        }

        // 傷害為0
        if (dmg0(_targetNpc)) {
            return false;
        }

    /*    if (_weaponType2 == 17) {// 奇古獸 命中100%
            return true;
        }*/

        if (_npc instanceof L1PetInstance) {
            return false;
        }


        // ＮＰＣ命中率
        // ＝（PCLv＋補正＋STR補正＋DEX補正＋武器補正＋DAI枚數/2＋魔法補正）×5?{NPCAC×（-5）}
        _hitRate = _pc.getLevel();

        // 力量命中補正 / 敏捷命中補正
        _hitRate += str_dex_Hit();

        if ((_weaponType != 20) && (_weaponType != 62)) {
            _hitRate += _weaponAddHit + _pc.getHitup() + (_weaponEnchant * 0.6);

        } else {
            _hitRate += _weaponAddHit + _pc.getBowHitup() + (_weaponEnchant * 0.6);
        }
        // 防具追加命中
        if ((_weaponType != 20) && (_weaponType != 62)) { // 防具追加命中
            _hitRate += _pc.getHitModifierByArmor();
        } else {
            _hitRate += _pc.getBowHitModifierByArmor();
        }

        final int weight182 = _pc.getInventory().getWeight182();
        if (weight182 > 80) { // 重量命中補正
            if ((80 < weight182) && (120 >= weight182)) {
                _hitRate -= 1;

            } else if ((121 <= weight182) && (160 >= weight182)) {
                _hitRate -= 3;

            } else if ((161 <= weight182) && (200 >= weight182)) {
                _hitRate -= 5;
            }
        }
        _hitRate += hitUp();//// 料理追加命中 
        int tgNpcAc = this._targetNpc.getAc();
        if (tgNpcAc < 0) {
            tgNpcAc *= -1;
            if (tgNpcAc <= 10) {
                _hitRate -= 1;
            } else if (tgNpcAc <= 20) {
                _hitRate -= 1;
            }
            if (tgNpcAc <= 30) {
                _hitRate -= 3;
            } else if (tgNpcAc <= 40) {
                _hitRate -= 4;
            } else if (tgNpcAc <= 50) {
                _hitRate -= 5;
            } else if (tgNpcAc <= 60) {
                _hitRate -= 6;
            } else if (tgNpcAc <= 70) {
                _hitRate -= 7;
            } else if (tgNpcAc <= 80) {
                _hitRate -= 8;
            } else if (tgNpcAc <= 90) {
                _hitRate -= 10;
            }
        }
        // _pc.sendPackets(new S_SystemMessage("未計算命中" + _hitRate));

        int attackerDice = _random.nextInt(20) + 2 + _hitRate - 10;
        // _pc.sendPackets(new S_SystemMessage("亂數公式" + attackerDice));
        attackerDice += attackerDice(_targetNpc);
        // _pc.sendPackets(new S_SystemMessage("怪物技能閃避" + attackerDice));
        int defenderDice = 10 - _targetNpc.getAc();// 10- (-14) 24
        //  _pc.sendPackets(new S_SystemMessage("怪物-防禦" + defenderDice));
        int fumble = _hitRate - 9;
        //  _pc.sendPackets(new S_SystemMessage("命中-9=" + fumble));
        int critical = _hitRate + 10;
        // _pc.sendPackets(new S_SystemMessage("命中+10=" + critical));

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


        //	hit_rnd = _hitRate * 100 / defenderDice;// 攻擊命中機率

        if (hit_rnd >= 95) {
            hit_rnd = 95;

        } else if (hit_rnd <= 0) {
            hit_rnd = 0;
        }

        final int npcId = _targetNpc.getNpcTemplate().get_npcId();

        final Integer tgskill = L1AttackList.SKNPC.get(npcId);
        if (tgskill != null) {
            if (!_pc.hasSkillEffect(tgskill)) {
                _hitRate = 0;
            }
        }

        final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
        if (tgpoly != null) {
            if (tgpoly.equals(_pc.getTempCharGfx())) {
                _hitRate = 0;
            }
        }

        final int rnd = _random.nextInt(100) + 1;
        //  _pc.sendPackets(new S_SystemMessage("命中" + _hitRate));
        // _pc.sendPackets(new S_SystemMessage("隨機" + rnd));
        // _pc.sendPackets(new S_SystemMessage("--------------------------"));
        return _hitRate >= rnd;

    }

    /**
     * 追加命中
     *
     * @return
     */
    private int hitUp() {
        int hitUp = 0;
        if (_pc.getSkillEffect().size() <= 0) {
            return hitUp;
        }

        if (!_pc.getSkillisEmpty()) {
            try {
                // 追加命中(近距離武器)
                if ((_weaponType != 20) && (_weaponType != 62)) {
                    for (final Integer key : _pc.getSkillEffect()) {
                        final Integer integer = L1AttackList.SKU1.get(key);
                        if (integer != null) {
                            hitUp += integer;
                        }
                    }

                    // 追加命中(遠距離武器)
                } else {
                    for (final Integer key : _pc.getSkillEffect()) {
                        final Integer integer = L1AttackList.SKU2.get(key);
                        if (integer != null) {
                            hitUp += integer;
                        }
                    }
                }

            } catch (final ConcurrentModificationException e) {
                // 技能取回發生其他線程進行修改
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }

        return hitUp;
    }

    /**
     * 傷害計算
     */
    @Override
    public int calcDamage() {
        switch (_calcType) {
            case PC_PC:
                _damage = calcPcDamage();
                if (_pc.getMapId() == 96) {
                    _damage = 0;
                }

                break;

            case PC_NPC:
                _damage = calcNpcDamage();
                break;
        }
        if (_damage > 0) {
            // 特殊魔法武器
            if (_pc.getEquipSlot().getAttachDrainHp() > 0) { // 附魔吸血
                if (_random.nextInt(1000) <= 30) {
                    _pc.sendPackets(new S_ServerMessage("\\fW觸發吸血: +" + _pc.getEquipSlot().getAttachDrainHp()));
                    //_pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                    _pc.setCurrentHp((short) (_pc.getCurrentHp() + _pc.getEquipSlot().getAttachDrainHp()));
                }
            }
            if (_pc.getEquipSlot().getAttachDrainMp() > 0) { // 附魔吸魔
                if (_random.nextInt(1000) <= 70) {
//                    _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                    _pc.setCurrentMp((short) (_pc.getCurrentMp() + _pc.getEquipSlot().getAttachDrainMp()));
                    _pc.sendPackets(new S_ServerMessage("\\fY觸發吸魔: +" + _pc.getEquipSlot().getAttachDrainMp()));
                    if (_targetPc != null) {
                        _targetPc.setCurrentMp((short) (_targetPc.getCurrentMp() - _pc.getEquipSlot().getAttachDrainMp()));

                    } else if (_targetNpc != null) {
                        _targetNpc.setCurrentMp((short) (_targetNpc.getCurrentMp() - _pc.getEquipSlot().getAttachDrainMp()));
                    }
                }
            }
            if (_weapon != null) {
//                if (FeatureItemSet.POWER_START) {// 特殊屬性武器
                    final L1AttackPower attackPower = new L1AttackPower(_pc, _target, _weaponAttrEnchantKind, _weaponAttrEnchantLevel);
                    _damage = attackPower.set_item_power_custom(_damage);
//                    _damage = attackPower.set_item_power(_damage);
//                }
            }
        }
        if (_pc.getdmgbl() > 1) {
            _damage *= _pc.getdmgbl();
        }
        if (ConfigOther.特殊額外攻擊) {
            _damage += _pc.getEquipSlot().getAttachOtherDamage();
            if ((_weaponType != 20) && (_weaponType != 62)) { // 防具追加傷害
                _damage += _pc.getDmgModifierByArmor();
            } else {
                _damage += _pc.getBowDmgModifierByArmor();
            }
        }
        return _damage;
    }

    /**
     * 傷害質初始化
     *
     * @param weaponMaxDamage 可發出的最大攻擊質
     * @return
     */
    private int weaponDamage1(int weaponMaxDamage) {
        int weaponDamage = 0;
        // boolean soulFlame = false;// 技能(烈焰之魂)
        // 武器類型核心分類
        switch (_weaponType2) {
            case 0:// 空手
            case 4:// 弓
            case 10:// 鐵手甲
            case 13:// 弓(單手)
                weaponDamage = weaponMaxDamage;
                break;

            case 1:// 劍
            case 2:// 匕首
            case 3:// 雙手劍
            case 15:// 雙手斧
            case 5:// 矛(雙手)
            case 6:// 斧(單手)
            case 7:// 魔杖
            case 8:// 飛刀
            case 9:// 箭
            case 14:// 矛(單手)
            case 16:// 魔杖(雙手)
                if (_pc.hasSkillEffect(175)) {//烈焰之魂
                    weaponDamage = weaponMaxDamage;
                } else {
                    weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
                }
                //  _pc.sendPackets(new S_SystemMessage("打擊大小怪隨機獲得武器植:" + weaponDamage));
                // weaponDamage = weaponMaxDamage;

                break;
        }

       /* if (_pc.getClanid() != 0) {
            weaponDamage += getDamageUpByClan(_pc);// 血盟技能傷害提升
        }*/
        return weaponDamage;
    }

    /**
     * 傷害質最終計算
     *
     * @param weaponTotalDamage
     * @return
     */
    private double weaponDamage2(double weaponTotalDamage) {
        double dmg = 0.0;

        switch (_weaponType2) {
            case 1:// 劍
            case 2:// 匕首
            case 3:// 雙手劍
            case 5:// 矛(雙手)
            case 6:// 斧(單手)
            case 7:// 魔杖
            case 8:// 飛刀
            case 9:// 箭
            case 14:// 矛(單手)
            case 15:// 雙手斧
            case 16:// 魔杖(雙手)
                dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup()/*+ _pc.getOriginalDmgup()*/;//270移除 初始值額外增加判斷
                //    _pc.sendPackets(new S_SystemMessage("最終計算傷害分類武器1:" + weaponTotalDamage));
                //    _pc.sendPackets(new S_SystemMessage("最終計算傷害分類武器2:" + _statusDamage));
                //    _pc.sendPackets(new S_SystemMessage("最終計算傷害分類武器3:" + _pc.getDmgup()));
                //  _pc.sendPackets(new S_SystemMessage("kk傷害質最終計算" + dmg));
                break;


            case 0:// 空手
                dmg = (_random.nextInt(5) + 4) >> 2;// / 4;
                break;

            case 4:// 弓
            case 13:// 弓(單手)
                int add = _statusDamage;//錯誤 加上能力
                //  _pc.sendPackets(new S_SystemMessage("kk敏捷+++" + add));
         /*   switch (_calcType) {
         /*   case PC_PC:
                add *= 1.7D;
                break;

            case PC_NPC:
                add *= 1.3D;
                break;
            }*/
                dmg = weaponTotalDamage + add + _pc.getBowDmgup() /*+ _pc.getOriginalBowDmgup()*/;
                //  _pc.sendPackets(new S_SystemMessage("kk上方傷害計算" + weaponTotalDamage));
                //  _pc.sendPackets(new S_SystemMessage("kk敏捷(攻擊*1.2)" + add));
                //  _pc.sendPackets(new S_SystemMessage("kk計算結果弓" + dmg));
                if (_arrow != null) {//弓箭的隨機值
                    final int add_dmg = Math.max(_arrow.getItem().getDmgSmall(), 1);
                    dmg = dmg + _random.nextInt(add_dmg) + 1;
                    //    _pc.sendPackets(new S_SystemMessage("kk計算結果弓(隨機+最小)" + dmg));
                } else if (_weaponId == 190) { // 沙哈之弓
                    dmg = dmg + _random.nextInt(12) + 1;
                }

                break;


        }

        if (_weaponType2 != 0) {
            final int add_dmg = _weapon.getItem().get_add_dmg();////DB武器額外傷害設置 
            if (add_dmg != 0) {
                dmg += add_dmg;
            }
        }

        //     _pc.sendPackets(new S_SystemMessage("kk最終計算傷害:" + dmg));
        return dmg;
    }

    /**
     * PC基礎傷害提升計算
     *
     * @param dmg
     * @param weaponTotalDamage
     * @return
     */
    private double pcDmgMode(double dmg, double weaponTotalDamage) {

        dmg = calcBuffDamage(dmg);//基礎物理
        //  _pc.sendPackets(new S_SystemMessage("kk基礎1:" + dmg));
        //dmg += _pc.getDmgup();
        //_pc.sendPackets(new S_SystemMessage("kk基礎2:" + dmg));
        //	_pc.sendPackets(new S_SystemMessage("kk基礎2:" + _pc.getDmgup()));
    	/* if (_weaponType == 20 || _weaponType == 62) {
		dmg += _pc.getBowDmgup();
    	 }else{
    		 dmg += _pc.getDmgup();
    	 }*/
        //_pc.sendPackets(new S_SystemMessage("kk基礎3:" + dmg));
        //_pc.sendPackets(new S_SystemMessage("kk基礎3:" + _pc.getBowDmgup()));
        //娃娃機率 傷害 倒比
        dmg += _pc.dmgAdd();
        //_pc.sendPackets(new S_SystemMessage("kk基礎(敏捷額外攻擊)3:" + dmg));
        dmg += weaponSkill(_pc, _target, weaponTotalDamage);// 武器附加魔法

        addPcPoisonAttack(_target);

        if (!ConfigOther.特殊額外攻擊) {
            if ((_weaponType != 20) && (_weaponType != 62)) { // 防具追加傷害
                dmg += _pc.getDmgModifierByArmor();
            } else {
                dmg += _pc.getBowDmgModifierByArmor();
            }
        }

        dmg += dmgUp();


        return dmg;
    }

    /**
     * 武器強化魔法
     *
     * @param dmg
     * @return
     */
    private double calcBuffDamage(double dmg) {
        if (_weaponType == 20) {// 弓
            return dmg;
        }
        if (_weaponType == 62) {// 鐵手甲
            return dmg;
        }


        if (_pc.hasSkillEffect(FIRE_WEAPON)) {
            dmg += 4.0;
        }

        if (_pc.hasSkillEffect(BURNING_WEAPON)) {
            dmg += 6.0;
        }
        if (_pc.hasSkillEffect(BERSERKERS)) {
            dmg += 5.0;
        }
        //娃娃 額外使用道具增加 確定保底最低傷害增加
    	
    	
    	
    	/*if (_pc.hasSkillEffect(BLESS_WEAPON)) {
			dmg += 2.0;
		}*/

        //  _pc.sendPackets(new S_SystemMessage("kk武器強化魔法" + dmg));
        return dmg;
    }

    /**
     * PC對PC傷害計算
     */
    public int calcPcDamage() {
        //  final int random = _random.nextInt(100) + 1;
        if (_targetPc == null) {
            return 0;
        }
        //安全區 攻擊玩家傷害0 2019/02/17 terry770106 某些座標可以打 (盟屋)
        if (_targetPc.isSafetyZone()) {
            return 0;
        }
        // 傷害為0
        if (dmg0(_targetPc)) {
            _isHit = false;
            _drainHp = 0;
            return 0;
        }

        if (!_isHit) {
            return 0;
        }

        final int weaponMaxDamage = _weaponSmall;

        int weaponDamage = weaponDamage1(weaponMaxDamage);//// 武器亂數傷害計算 /列魂 / 鋼爪

        int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant + _weaponAddPVPDmg;
        //  double weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

        weaponTotalDamage += calcAttrEnchantDmg(); // 屬性強化

        // 傷害直最終計算
        double dmg = weaponDamage2(weaponTotalDamage);

        // PC基礎傷害提升計算
        dmg = pcDmgMode(dmg, weaponTotalDamage);
        if (_pc.getPVPDmg() > 0) {
            dmg += _pc.getPVPDmg();
        }

        dmg -= _targetPc.getDamageReductionByArmor() + _targetPc.get_reduction_dmg(); // 被攻擊者防具額外傷害減免

        dmg -= _targetPc.dmgDowe(); // 機率傷害減免 DOLL

        if (_targetPc.hasSkillEffect(TRUE_TARGET)) {// 精準目標增傷
            double attackerlv = _pc.getLevel();
            double adddmg = (attackerlv / 15) / 100 + 1.01D;
            dmg *= adddmg;
        }


        // 增幅防禦
        if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
            final int targetPcLvl = Math.max(_targetPc.getLevel(), 15);
            dmg -= (targetPcLvl - 50) / 5 + 5;
        }

        dmg = BuffDmgUp(dmg);// 屬火、燃鬥、增傷計算


        boolean dmgX2 = false;// 傷害除5
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

        // 魔法娃娃特殊技能
        if (!_pc.getDolls().isEmpty()) {
            for (final Iterator<L1DollInstance> iter = _pc.getDolls().values().iterator(); iter.hasNext(); ) {
                final L1DollInstance doll = iter.next();
                doll.startDollSkill(_targetPc, dmg);
            }
        }
        // 未命中傷害歸0
        if (!this._isHit) {
            dmg = 0.0;
        }
        if (_targetPc.isKnight()) {
            dmg -= 3;
        }

        if (dmg <= 0) {
            _isHit = false;
            _drainHp = 0;
        }
        if (_pc.isCrown() || _targetPc.isCrown()) {
            dmg *= 1.3D;
        }
        if (_pc.isElf()) {
            final int chance = _random.nextInt(100) + 1;
            if (chance < 20) {
                dmg *= 1.2D;
            }
        }
        if (_pc.getChit() > 0) {
            if (_random.nextInt(100) <= _pc.getChit()) {
                dmg *= 1.25D;// 2倍傷害
            }
        }
        dmg -= calcPcDefense();
        return (int) dmg;
    }
//TODO PC_NPC

    /**
     * PC對NPC傷害
     *
     * @return
     */
    private int calcNpcDamage() {
        // final int random = _random.nextInt(100) + 1;

        if (_targetNpc == null) {
            return 0;
        }

        // 傷害為0
        if (dmg0(_targetNpc)) {
            _isHit = false;
            _drainHp = 0;
            return 0;
        }

        if (!_isHit) {
            return 0;
        }


        int weaponMaxDamage = 0;
        //如果對方是小怪  && 目前武器最小傷害 大於0
        if (_targetNpc.getNpcTemplate().isSmall()) {
            if (_weaponSmall > 0) {
                weaponMaxDamage = _weaponSmall;
                //    _pc.sendPackets(new S_SystemMessage("kk打小怪傷害" + weaponMaxDamage));
            }
        } else if (_targetNpc.getNpcTemplate().isLarge()) {
            if (_weaponLarge > 0) {
                weaponMaxDamage = _weaponLarge;
                //     _pc.sendPackets(new S_SystemMessage("kk打大怪傷害" + weaponMaxDamage));
            }
        } else {
            if (_weaponSmall > 0) {
                weaponMaxDamage = _weaponSmall;
                //      _pc.sendPackets(new S_SystemMessage("kk最大傷害" + weaponMaxDamage));
            }
        }


        // 傷害直初始化 正常
        int weaponDamage = weaponDamage1(weaponMaxDamage);
        // _pc.sendPackets(new S_SystemMessage("傷害直初始化 正常" + weaponDamage));

        int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;
        //  _pc.sendPackets(new S_SystemMessage("計算" + weaponTotalDamage));
        //  _pc.sendPackets(new S_SystemMessage("kk上方傷害總加" + weaponDamage));
        //  _pc.sendPackets(new S_SystemMessage("kk武器額外傷害" + _weaponAddDmg));
        // _pc.sendPackets(new S_SystemMessage("kk武器安定值" + _weaponEnchant));

        weaponTotalDamage += calcMaterialBlessDmg(); // 祝福武器 銀/米索莉/奧裡哈魯根材質武器

        // _pc.sendPackets(new S_SystemMessage("怪物" + weaponTotalDamage));
        // _pc.sendPackets(new S_SystemMessage("(箭)" + weaponTotalDamage));
        weaponTotalDamage += calcAttrEnchantDmg(); // 屬性強化

        //  _pc.sendPackets(new S_SystemMessage("屬性" + weaponTotalDamage));
      /*  _pc.sendPackets(new S_SystemMessage("1:" + weaponDamage));
        _pc.sendPackets(new S_SystemMessage("2:" + _weaponAddDmg));
        _pc.sendPackets(new S_SystemMessage("3:" + _weaponEnchant));
        _pc.sendPackets(new S_SystemMessage("4:" + calcMaterialBlessDmg()));
        _pc.sendPackets(new S_SystemMessage("5:" + calcAttrEnchantDmg()));*/
        // 傷害直最終計算
        double dmg = weaponDamage2(weaponTotalDamage); //屬性加成、道具加成、力量加成

        // _pc.sendPackets(new S_SystemMessage("最終" + dmg));

        // PC基礎傷害提升計算
        dmg = pcDmgMode(dmg, weaponTotalDamage);

        //  _pc.sendPackets(new S_SystemMessage("在加上基礎" + dmg));

        //城區
        boolean isNowWar = false;
        int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
        if (castleId > 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        if (!isNowWar) {// 不是在攻城區內
            if ((_targetNpc instanceof L1PetInstance)) {// 寵物減免
                dmg /= 2.0D;
            }
            if ((_targetNpc instanceof L1SummonInstance)) {// 召喚獸減免
                L1SummonInstance summon = (L1SummonInstance) _targetNpc;
                if (summon.isExsistMaster()) {
                    dmg /= 2.0D;
                }
            }
        }

        if (_pc.hasSkillEffect(7951) && !_pc.isGm() && !_targetNpc.getNpcTemplate().is_boss() && !isInWarAreaAndWarTime(_pc)) { // 2AI
            _pc.killSkillEffectTimer(7951);
            _pc.setSkillEffect(7952, 2000);
        }
        //招換青蛙
      	 /*f (_pc.hasSkillEffect(7911)&& !_targetNpc.getNpcTemplate().is_boss() && !isInWarAreaAndWarTime(_pc)) { // 2AI
       		_pc.killSkillEffectTimer(7911);
       		L1SpawnUtil.spawn(_pc, ConfigAi.npcid, 0, ConfigAi.npctime);
      
       		_pc.setSkillEffect(7910, ConfigAi.lasttime * 1000);
       	}*/

        if (_targetNpc.getNpcTemplate().is_hard()) {//怪物是硬皮怪
            if ((_weaponType == 20) || (_weaponType == 62)) { // 弓 硬皮怪
                dmg /= 1.5;
            }
        }
        //炫色額外控制
   /*    int ran = _random.nextInt(100)+1;
       if (_pc.getInventory().checkdroptype(1) ) {
				if(ran <= 3) {
			dmg += dmg * 0.10D;
		}
		}*/


        dmg = BuffDmgUp(dmg);// 屬火、燃鬥、增傷計算

        //  _pc.sendPackets(new S_SystemMessage("其他" + dmg));
        // 魔法娃娃特殊技能
        if (!_pc.getDolls().isEmpty()) {
            for (final Iterator<L1DollInstance> iter = _pc.getDolls().values().iterator(); iter.hasNext(); ) {
                final L1DollInstance doll = iter.next();
                doll.startDollSkill(_targetNpc, dmg);
            }
        }

        // 未命中傷害歸0
        if (!_isHit) {
            dmg = 0D;
        }

        if (dmg <= 0D) {
            _isHit = false;
            _drainHp = 0; // 無場合吸收回復
        }
        if (_pc.getChit() > 0) {
            if (_random.nextInt(100) <= _pc.getChit()) {
                dmg *= 1.25D;// 2倍傷害
            }
        }
        if (_targetNpc.getMovementDistance() > 0) {
            if (_pc.getLocation().getLineDistance(_targetNpc.getHomeX(), _targetNpc.getHomeY()) > (_targetNpc.getMovementDistance() + 3)) {
                L1Teleport.teleport(_pc, _targetNpc.getX(), _targetNpc.getY(), _targetNpc.getMapId(), _targetNpc.getHeading(), false);
                dmg = 0;
            }
        }
        if (_targetNpc != null && _targetNpc.getNpcTemplate().is_boss()) {
            dmg += _pc.getBossDmg();
        }
        if (_pc.isCrown()) {
            dmg *= 1.3D;
        }
        if (_pc.isElf()) {
            final int chance = _random.nextInt(100) + 1;
            if (chance < 20) {
                dmg *= 1.2D;
            }
        }
        // System.out.println("PC對NPC傷害 武器系統:最後：" + dmg);

        //   _pc.sendPackets(new S_SystemMessage("ssssssssssss" + dmg));
        return (int) dmg;
    }


    /**
     * 技能對武器追加傷害
     *
     * @return
     */
    private double dmgUp() {
        double dmg = 0.0;

        if (_pc.getSkillEffect().size() <= 0) {
            return dmg;
        }

        if (!_pc.getSkillisEmpty()) {
            try {
                HashMap<Integer, Integer> skills = null;
                switch (_weaponType) {
                    case 20:// 弓
                        skills = L1AttackList.SKD2;
                        break;

                    //   case 24:// 鎖煉劍
                    default:
                        skills = L1AttackList.SKD1;
                        break;
                }

                if (skills != null) {
                    for (final Integer key : _pc.getSkillEffect()) {
                        final Integer integer = L1AttackList.SKD2.get(key);
                        if (integer != null) {
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

        return dmg;
    }

    /**
     * 武器附加魔法
     *
     * @param pcInstance
     * @param character
     * @param weaponTotalDamage
     * @return
     */
    private double weaponSkill(final L1PcInstance pcInstance, final L1Character character, double weaponTotalDamage) {
        double dmg = 0.0D;
        // dmg = WeaponSkillStart.start_weapon_skill(pcInstance, character, _weapon, weaponTotalDamage);
        if (dmg != 0.0D) {
            return dmg;
        }

        switch (_weaponId) {
      /*  case 265:// 底比斯歐西裡斯雙刀
        case 266:// 底比斯歐西裡斯雙手劍
        case 267:// 底比斯歐西裡斯弓
        case 268:*/// 底比斯歐西裡斯魔杖
            // dmg = L1WeaponSkill.getChaserDamage(_pc, _target);
            //  break;

            case 2:// 骰子匕首
            case 200002:// 骰子匕首
                if (this._targetPc != null) {
                    dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, _weapon);
                }
                break;

        /*case 124:// 巴風特魔杖
            dmg = L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
            break;*/

       /* case 204:// 深紅之弩
        case 100204:// 深紅之弩
            L1WeaponSkill.giveFettersEffect(_pc, _target);
            break;*/

       /* case 261:// 大法師魔仗
        case 500250:// 大法師魔仗
            L1WeaponSkill.giveArkMageDiseaseEffect(_pc, _target);
            break;*/

       /* case 260:// 狂風之斧
        case 500249:// 狂風之斧
        case 263:// 酷寒之矛
        case 500252:// 酷寒之矛
            dmg = L1WeaponSkill.getAreaSkillWeaponDamage(_pc, _target, _weaponId);
            break;*/

      /*  case 264:// 雷雨之劍
        case 500253:// 雷雨之劍
            dmg = L1WeaponSkill.getLightningEdgeDamage(_pc, _target);
            break;*/

     /*   case 262: // 毀滅巨劍HP奪取成功確率(暫定)75%
        case 500251: // 毀滅巨劍HP奪取成功確率(暫定)75%
        	 if (L1AttackPc._random.nextInt(110) <= 50) {
                 this._drainHp = Math.max((int)(weaponTotalDamage / 10.0), 1);
                 this._drainHp = Math.min(this._drainHp, 18);
                 break;
             }
            break;*/

            default:
                dmg = L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
                break;
        }

        return dmg;
    }

    /**
     * 祝福武器 銀/米索莉/奧裡哈魯根材質武器<BR>
     * 其他屬性定義
     *
     * @return
     */
    private int calcMaterialBlessDmg() {
        int damage = 0;
        if (_pc.getWeapon() != null) {
            final int undead = _targetNpc.getNpcTemplate().get_undead();
            switch (undead) {
                case 1:// 不死系
                    if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧裡哈魯根
                        damage += _random.nextInt(20) + 1;
                        // _pc.sendPackets(new S_SystemMessage("kk不死(箭)" + damage));
                    }
                    if (_weaponBless == 0) { // 祝福武器
                        damage += _random.nextInt(4) + 1;
                        // _pc.sendPackets(new S_SystemMessage("kk祝福武器" + damage));
                    }
                    switch (_weaponType) {
                        case 20:
                        case 62:
                            break;
                        default:
                            if (_weapon.getHolyDmgByMagic() != 0) {
                                damage += _weapon.getHolyDmgByMagic();// 武器強化魔法
                                //   _pc.sendPackets(new S_SystemMessage("kk武器強化魔法" + damage));
                            }
                            break;
                    }
                    break;
                case 2:// 惡魔系
                    if ((_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 米索莉/奧裡哈魯根
                        damage += _random.nextInt(3) + 1;
                        //  _pc.sendPackets(new S_SystemMessage("kk惡魔系" + damage));
                    }
                    if (_weaponBless == 0) { // 祝福武器
                        damage += _random.nextInt(4) + 1;
                    }
                    break;
                case 3:// 殭屍系
                    if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧裡哈魯根
                        damage += _random.nextInt(20) + 1;
                        // _pc.sendPackets(new S_SystemMessage("kk殭屍系" + damage));
                    }
                    if (_weaponBless == 0) { // 祝福武器
                        damage += _random.nextInt(4) + 1;
                        // _pc.sendPackets(new S_SystemMessage("kk祝福" + damage));
                    }
                    switch (_weaponType) {
                        case 20:
                        case 62:
                            break;
                        default:
                            if (_weapon.getHolyDmgByMagic() != 0) {
                                damage += _weapon.getHolyDmgByMagic();// 武器強化魔法
                                //   _pc.sendPackets(new S_SystemMessage("kk武器強化魔法" + damage));
                            }
                            break;
                    }
                    break;
                case 5:// 狼人系
                    if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧裡哈魯根
                        damage += _random.nextInt(20) + 1;
                        // _pc.sendPackets(new S_SystemMessage("kk狼人細" + damage));
                    }
                    break;
            }
        }
        return damage;
    }

    /**
     * 武器屬性卷軸
     * 武器屬性強化追加算出
     *
     * @return
     */
    private int calcAttrEnchantDmg() {
        int damage = 0;
        switch (this._weaponAttrEnchantLevel) {
            case 1:
                damage = 1;
                break;
            case 2:
                damage = 3;
                break;
            case 3:
                damage = 5;
                break;
        }
        //  _pc.sendPackets(new S_SystemMessage("kk屬性卷軸LV++:" + damage));
        // 對地火火風抗性的處理
        int resist = 0;
        switch (this._calcType) {
            case PC_PC:
                switch (this._weaponAttrEnchantKind) {
                    case 1: // 地
                        resist = this._targetPc.getEarth();
                        break;

                    case 2: // 火
                        resist = this._targetPc.getFire();
                        break;

                    case 4: // 水
                        resist = this._targetPc.getWater();
                        break;

                    case 8: // 風
                        resist = this._targetPc.getWind();
                        break;

                    case 16: // 光
                        resist = this._targetPc.getEarth();
                        break;

                    case 32: // 暗
                        resist = this._targetPc.getFire();
                        break;

                    case 64: // 聖
                        resist = this._targetPc.getWater();
                        break;

                    case 128: // 邪
                        resist = this._targetPc.getWind();
                        break;
                }
                break;

            case PC_NPC:
                switch (this._weaponAttrEnchantKind) {
                    case 1: // 地
                        resist = this._targetNpc.getEarth();
                        break;

                    case 2: // 火
                        resist = this._targetNpc.getFire();
                        break;

                    case 4: // 水
                        resist = this._targetNpc.getWater();
                        break;

                    case 8: // 風
                        resist = this._targetNpc.getWind();
                        break;

                    case 16: // 光
                        resist = this._targetNpc.getEarth();
                        break;

                    case 32: // 暗
                        resist = this._targetNpc.getFire();
                        break;

                    case 64: // 聖
                        resist = this._targetNpc.getWater();
                        break;

                    case 128: // 邪
                        resist = this._targetNpc.getWind();
                        break;
                }
                break;
        }

        int resistFloor = (int) (0.32 * Math.abs(resist));

        if (resist < 0) {
            resistFloor *= -1;
        }

        final double attrDeffence = resistFloor / 32.0;
        final double attrCoefficient = 1 - attrDeffence;

        damage *= attrCoefficient;

        return damage;
    }

    /**
     * 魔力奪取武器 MP奪取質計算
     */
    @Override
    public void calcStaffOfMana() {
        switch (this._weaponId) {
            case 126: // 瑪那魔杖
            case 127: // 鋼鐵瑪那魔杖
            case 301126: // 鋼鐵瑪那魔杖
            case 301127: // 鋼鐵瑪那魔杖
            case 600126:
            case 600127:
                int som_lvl = this._weaponEnchant + 3; // 最大MP吸收量設定
                if (som_lvl < 0) {
                    som_lvl = 0;
                }
                // MP修收量取得(最大吸收9)
                this._drainMana = Math.min(_random.nextInt(som_lvl) + 1, 9);
                break;

            case 259: // 魔力短劍
                switch (this._calcType) {
                    case PC_PC:
                        int mr = this._targetPc.getMr();
                        mr += this._targetPc.getEquipSlot().getAttachMr();
                        if (mr <= _random.nextInt(100) + 1) { // 確率MR依存
                            this._drainMana = 1; // 吸收量1固定
                        }
                        break;

                    case PC_NPC:
                        if (this._targetNpc.getMr() <= _random.nextInt(100) + 1) { // 確率MR依存
                            this._drainMana = 1; // 吸收量1固定
                        }
                        break;
                }
                break;
        }
    }


    /**
     * PC附加毒性攻擊
     *
     * @param attacker
     * @param target
     */
    private void addPcPoisonAttack(final L1Character target) {
        boolean isCheck = false;
        switch (_weaponId) {
            case 0:// 空手
                break;

            case 13:// 死亡之指
            case 14:// 混沌之刺
                isCheck = true;
                break;

            default:
                if (_pc.hasSkillEffect(ENCHANT_VENOM)) {
                    isCheck = true;
                }
                break;
        }

        if (isCheck) {
            final int chance = _random.nextInt(100) + 1;
            if (chance <= 10) {
                // 通常毒、3秒週期、HP-5
                L1DamagePoison.doInfection(_pc, target, 3000, 5);
            }
        }
    }


    /**
     * 屬火、燃鬥、增傷計算
     *
     * @param dmg
     */
    private double BuffDmgUp(double dmg) {
        int random = _random.nextInt(100) + 1;

        if ((_pc.hasSkillEffect(ELEMENTAL_FIRE) && (_weaponType != 20) && (_weaponType != 62)) && // 屬性之火
                (random <= 25)) {
            dmg *= 1.5D;
        }

        return dmg;
    }

    /**
     * 攻擊資訊送出
     */
    @Override
    public void action() {
        try {
            if (_pc == null) {
                return;
            }
            if (_target == null) {
                return;
            }
            // 改變面向
            _pc.setHeading(_pc.targetDirection(_targetX, _targetY));

            if (_weaponRange == -1) {// 遠距離武器
                actionX1();

            } else {// 近距離武器
                actionX2();
            }

            if (Config.DAMAGE_SKIN) {
                int i = (int) ((_damage / Math.pow(10, 0)) % 10) + 8001;// 個位
                int k = (int) ((_damage / Math.pow(10, 1)) % 10) + 8011;// 十位
                int h = (int) ((_damage / Math.pow(10, 2)) % 10) + 8021;// 百位
                int s = (int) ((_damage / Math.pow(10, 3)) % 10) + 8031;// 千位
                int m = (int) ((_damage / Math.pow(10, 4)) % 10) + 8041;// 萬位
                if (_damage <= 0) {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), 8051));// 官方版Miss
                } else if (_damage < 10) {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), i));// 個位數
                } else if (_damage < 100) {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), i));// 個位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), k));// 十位數
                } else if (_damage < 1000) {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), i));// 個位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), k));// 十位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), h));// 百位數
                } else if (_damage < 10000) {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), i));// 個位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), k));// 十位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), h));// 百位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), s));// 千位數
                } else {
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), i));// 個位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), k));// 十位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), h));// 百位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), s));// 千位數
                    _pc.sendPackets(new S_SkillSound(this._target.getId(), m));// 萬位數
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 近距離武器/空手
     */
    private void actionX2() {
        try {
            if (_target.isGeneralNpc()) { // 對話npc
                if (_targetId > 0) {
                    _pc.sendPacketsAll(new S_AttackPacketPc(_pc, _target));
                } else {
                    _pc.sendPacketsAll(new S_AttackPacketPc(_pc));
                }
            } else { // 其他對像
                if (_isHit) {// 命中
                    // System.out.println("命中");
                    if (_targetId > 0) {
                        _pc.sendPacketsX10(new S_AttackPacketPc(_pc, _target, _attackType, _damage));
                    }
                } else {// 未命中
                    if (_targetId > 0) {
                        _pc.sendPacketsX10(new S_AttackPacketPc(_pc, _target));
                    } else {
                        _pc.sendPacketsAll(new S_AttackPacketPc(_pc));
                    }
                }
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
            switch (_weaponType) {
                case 20:// 弓
                    switch (_weaponId) {
                        case 190: // 沙哈之弓 不具有箭
                            if (_arrow != null) { // 具有箭
                                _pc.getInventory().removeItem(_arrow, 1);
                            }
                            _pc.sendPacketsX10(
                                    new S_UseArrowSkill(
                                            _pc,
                                            _targetId,
                                            2349,
                                            _targetX,
                                            _targetY,
                                            _damage
                                    ));
                            break;

                        default:// 其他武器 沒有箭
                            if (_arrow != null) { // 具有箭
                                int arrowGfxid = 66;
                                switch (_pc.getTempCharGfx()) {
                                    case 8842:
                                    case 8900:// 海露拜
                                        arrowGfxid = 8904;// 黑
                                        break;

                                    case 8913:
                                    case 8845:// 朱裡安
                                        arrowGfxid = 8916;// 白
                                        break;

                                    case 7959:
                                    case 7967:
                                    case 7968:
                                    case 7969:
                                    case 7970:// 天上騎士
                                        arrowGfxid = 7972;// 火
                                        break;
                                }
                                _pc.sendPacketsX10(
                                        new S_UseArrowSkill(
                                                _pc,
                                                _targetId,
                                                arrowGfxid,
                                                _targetX,
                                                _targetY,
                                                _damage
                                        ));
                                _pc.getInventory().removeItem(_arrow, 1);

                            } else {
                                int aid = 1;
                                // 外型編號改變動作
                                if (_pc.getTempCharGfx() == 3860) {
                                    aid = 21;
                                }
                                _pc.sendPacketsAll(new S_ChangeHeading(_pc));
                                // 送出封包(動作)
                                _pc.sendPacketsAll(
                                        new S_DoActionGFX(
                                                _pc.getId(),
                                                aid
                                        ));
                            }
                    }
                    break;

                case 62: // 鐵手甲
                    if (_sting != null) {// 具有飛刀
                        int stingGfxid = 2989;
                        switch (_pc.getTempCharGfx()) {
                            case 8842:
                            case 8900:// 海露拜
                                stingGfxid = 8904;// 黑
                                break;

                            case 8913:
                            case 8845:// 朱裡安
                                stingGfxid = 8916;// 白
                                break;

                            case 7959:
                            case 7967:
                            case 7968:
                            case 7969:
                            case 7970:// 天上騎士
                                stingGfxid = 7972;// 火
                                break;
                        }
                        _pc.sendPacketsX10(
                                new S_UseArrowSkill(
                                        _pc,
                                        _targetId,
                                        stingGfxid,
                                        _targetX,
                                        _targetY,
                                        _damage
                                ));
                        _pc.getInventory().removeItem(_sting, 1);

                    } else {// 沒有飛刀
                        int aid = 1;
                        // 外型編號改變動作
                        if (_pc.getTempCharGfx() == 3860) {
                            aid = 21;
                        }

                        _pc.sendPacketsAll(new S_ChangeHeading(_pc));
                        // 送出封包(動作)
                        _pc.sendPacketsAll(
                                new S_DoActionGFX(
                                        _pc.getId(),
                                        aid
                                ));
                    }
                    break;
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 計算結果
     */
    @Override
    public void commit() {
        if (_isHit) {
            switch (_calcType) {
                case PC_PC:
                    commitPc();

                    break;

                case PC_NPC:
                    commitNpc();
                    //	_pc.sendPackets(new S_ServerMessage("傷害:"+_damage));
                    break;
            }
        }

        // gm攻擊資訊
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

        final String srcatk = _pc.getName();// 攻擊者
        String tgatk = "";// 被攻擊者
        String hitinfo = "";// 資訊
        String dmginfo = "";// 傷害
        String x = "";// 最終資訊

        switch (this._calcType) {
            case PC_PC:
                tgatk = _targetPc.getName();
                hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetPc.getCurrentHp();
                dmginfo = _isHit ? "傷害:" + _damage : "失敗";
                x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
                if (_pc.isGm()) {
                    // 166 \f1%0%s %4%1%3 %2。
                    _pc.sendPackets(new S_ServerMessage(166, "對PC送出攻擊: " + x));
                }

                if (_targetPc.isGm()) {
                    // 166 \f1%0%s %4%1%3 %2。
                    _targetPc.sendPackets(new S_ServerMessage(166, "受到PC攻擊: " + x));
                }
                break;

            case PC_NPC:
                tgatk = this._targetNpc.getName();
                hitinfo = " 命中:" + this._hitRate + "% 剩餘hp:" + this._targetNpc.getCurrentHp();
                dmginfo = this._isHit ? "傷害:" + this._damage : "失敗";
                x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
                if (this._pc.isGm()) {
                    // 166 \f1%0%s %4%1%3 %2。
                    this._pc.sendPackets(new S_ServerMessage(166, "對NPC送出攻擊: " + x));
                }
                break;
        }
    }


    /**
     * 對PC攻擊傷害結果
     */
    private void commitPc() {
        if ((_drainMana > 0) && (_targetPc.getCurrentMp() > 0)) {
            if (_drainMana > _targetPc.getCurrentMp()) {
                _drainMana = _targetPc.getCurrentMp();
            }
            short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
            _targetPc.setCurrentMp(newMp);
            newMp = (short) (this._pc.getCurrentMp() + _drainMana);
            _pc.setCurrentMp(newMp);
        }

        if (_drainHp > 0) { // HP吸收回復
            final short newHp = (short) (_pc.getCurrentHp() + _drainHp);
            _pc.setCurrentHp(newHp);
        }

        damagePcWeaponDurability(); // 武器受到傷害
        _targetPc.receiveDamage(_pc, _damage, false, false);
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
        if (_drainMana > 0) {
            final int drainValue = _targetNpc.drainMana(_drainMana);
            final int newMp = _pc.getCurrentMp() + drainValue;
            _pc.setCurrentMp(newMp);
            if (drainValue > 0) {
                final int newMp2 = _targetNpc.getCurrentMp() - drainValue;
                _targetNpc.setCurrentMpDirect(newMp2);
            }
        }

        if (this._drainHp > 0) { // HP吸收回復
            final short newHp = (short) (_pc.getCurrentHp() + _drainHp);
            _pc.setCurrentHp(newHp);
        }

        damageNpcWeaponDurability(); // 武器受到傷害
        _targetNpc.receiveDamage(_pc, _damage);
    }

    /**
     * 相手攻擊對有效判別
     */
    @Override
    public boolean isShortDistance() {
        boolean isShortDistance = true;
        if ((_weaponType == 20) || (_weaponType == 62)) { // 弓
            isShortDistance = false;
        }
        return isShortDistance;
    }

    /**
     * 反擊屏障的傷害反擊
     */
    @Override
    public void commitCounterBarrier() {
        final int damage = calcCounterBarrierDamage();
        if (damage == 0) {
            return;
        }
        // 受傷動作
        _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
        _pc.receiveDamage(_target, damage, false, true);
    }

    /**
     * 武器受到傷害 對NPC場合、損傷確率10%。祝福武器3%。
     */
    private void damageNpcWeaponDurability() {
        /*
         * 損傷NPC、素手、損傷武器使用、SOF中場合何。
         */
        if (this._calcType != PC_NPC) {
            return;
        }

        if (this._targetNpc.getNpcTemplate().is_hard() == false) {
            return;
        }

        if (this._weaponType == 0) {
            return;
        }

        if (this._weapon.getItem().get_canbedmg() == 0) {
            return;
        }

        if (this._pc.hasSkillEffect(SOUL_OF_FLAME)) {
            return;
        }

        final int random = _random.nextInt(100) + 1;
        switch (this._weaponBless) {
            case 0:// 祝福
                if (random < 3) {
                    // \f1你的%0%s壞了。
                    this._pc.sendPackets(new S_ServerMessage(268, this._weapon.getLogName()));
                    this._pc.getInventory().receiveDamage(this._weapon);
                }
                break;

            case 1:// 一般
            case 2:// 詛咒
                if (random < 10) {
                    // \f1你的%0%s壞了。
                    this._pc.sendPackets(new S_ServerMessage(268, this._weapon.getLogName()));
                    this._pc.getInventory().receiveDamage(this._weapon);
                }
                break;
        }
    }

    /**
     * 武器受到傷害 損傷確率10%
     */
    private void damagePcWeaponDurability() {
        if (this._calcType != PC_PC) {
            return;
        }

        if (this._weaponType == 0) {
            return;
        }

        if (this._weaponType == 20) {
            return;
        }

        if (this._weaponType == 62) {
            return;
        }

        if (this._targetPc.hasSkillEffect(BOUNCE_ATTACK) == false) {
            return;
        }

        if (this._pc.hasSkillEffect(SOUL_OF_FLAME)) {
            return;
        }

        if (_random.nextInt(100) + 1 <= 10) {
            // \f1你的%0%s壞了。
            this._pc.sendPackets(new S_ServerMessage(268, this._weapon.getLogName()));
            this._pc.getInventory().receiveDamage(this._weapon);
        }
    }


    private static boolean isInWarAreaAndWarTime(L1PcInstance pc) {
        // pcとtargetが戦争中に戦争エリアに居るか
        int castleId = L1CastleLocation.getCastleIdByArea(pc);
        if (castleId != 0) {
            if (ServerWarExecutor.get().isNowWar(castleId)) {
                return true;
            }
        }
        return false;
    }


}
