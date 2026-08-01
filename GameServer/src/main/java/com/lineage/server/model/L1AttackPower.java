package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.Random;

import com.lineage.server.model.Instance.L1DollInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;

/**
 * 屬性武器特殊攻擊
 * 0:無屬性 1:地 2:火 4:水 8:風 16:光 32:暗 64:聖 128:邪
 *
 * @author dexc
 */
public class L1AttackPower {

    private static final Log _log = LogFactory.getLog(L1AttackPower.class);

    private static final Random _random = new Random();

    // 執行PC
    private final L1PcInstance _pc;

    private final L1Character _target;

    // 目標PC
    private L1PcInstance _targetPc;

    // 目標NPC
    private L1NpcInstance _targetNpc;

    private int _weaponAttrEnchantKind = 0;

    private int _weaponAttrEnchantLevel = 0;

    public L1AttackPower(final L1PcInstance attacker, final L1Character target, int weaponAttrEnchantKind, int weaponAttrEnchantLevel) {
        _pc = attacker;
        _target = target;
        if (_target instanceof L1NpcInstance) {
            _targetNpc = (L1NpcInstance) _target;

        } else if (_target instanceof L1PcInstance) {
            _targetPc = (L1PcInstance) _target;
        }
        _weaponAttrEnchantKind = weaponAttrEnchantKind;
        _weaponAttrEnchantLevel = weaponAttrEnchantLevel;
    }

    public int set_item_power_custom(final int damage) {
        if (_weaponAttrEnchantKind <= 0) {
            return damage;
        }
        int reset_dmg = damage;
        for (int i = 0; i < _weaponAttrEnchantLevel; i++) {
            reset_dmg += _random.nextInt(2) + 1; // 每個屬性進階多+1~2傷害
        }
        int random = 0;
        int time = 0;
        int drainHp = 0;
        int drainMp = 0;
        double adddmg = 0.0D;
        switch (_weaponAttrEnchantLevel) {
            case 1:
                random = 10;
                time = 200; // 束縛敵人0.2秒
                adddmg = 1.05D;
                drainHp = _random.nextInt(3) + 1;
                drainMp = _random.nextInt(3) + 1;
                break;

            case 2:// 10%機率束縛敵人1.0秒
                random = 20;
                time = 400; // 束縛敵人0.4秒
                adddmg = 1.10D;
                drainHp = _random.nextInt(6) + 1;
                drainMp = _random.nextInt(6) + 1;
                break;

            case 3:// 20%機率束縛敵人1.5秒
                random = 30;
                time = 600; // 束縛敵人0.6秒
                adddmg = 1.15D;
                drainHp = _random.nextInt(9) + 1;
                drainMp = _random.nextInt(9) + 1;
                break;
            case 4:
                random = 40;
                time = 800; // 束縛敵人0.8秒
                adddmg = 1.20D;
                drainHp = _random.nextInt(12) + 1;
                drainMp = _random.nextInt(12) + 1;
                break;
            case 5:
                random = 50;
                time = 1000; // 束縛敵人1.0秒
                adddmg = 1.25D;
                drainHp = _random.nextInt(15) + 1;
                drainMp = _random.nextInt(15) + 1;
                break;
        }
        if (_random.nextInt(1000) <= random) {
            if (_weaponAttrEnchantKind == 256) { // 金
                if (_targetPc != null) {
                    if (_targetPc.getWeapon() != null) {
                        _targetPc.getInventory().setEquipped(_targetPc.getWeapon(), false);
                        // 娃娃刪除
                        if (!_targetPc.getDolls().isEmpty()) {
                            for (Object obj : _targetPc.getDolls().values().toArray()) {
                                final L1DollInstance doll = (L1DollInstance) obj;
                                doll.deleteDoll();
                            }
                        }
                    }
                }
            }
            if (_weaponAttrEnchantKind == 512) { // 木
                if (!L1WeaponSkill.isFreeze(_target)) { // 凍結狀態
                    _target.broadcastPacketX8(new S_SkillSound(_target.getId(), 4184));
                    if (_targetPc != null) {
                        _targetPc.setSkillEffect(STATUS_FREEZE, time);
                        _targetPc.sendPackets(new S_SkillSound(_target.getId(), 4184));
                        _targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

                    } else if (_targetNpc != null) {
                        _targetNpc.setSkillEffect(STATUS_FREEZE, time);
                        _targetNpc.setParalyzed(true);
                    }
                }
            }
            if (_weaponAttrEnchantKind == 4) { // 水
                _pc.setCurrentMp((short) (_pc.getCurrentMp() + drainMp));
                if (_targetPc != null) {
                    _targetPc.setCurrentMp((short) (_targetPc.getCurrentMp() - drainMp));

                } else if (_targetNpc != null) {
                    _targetNpc.setCurrentMp((short) (_targetNpc.getCurrentMp() - drainMp));
                }
            }
            if (_weaponAttrEnchantKind == 2) { // 火
                reset_dmg = (int) (reset_dmg * adddmg);
            }
            if (_weaponAttrEnchantKind == 1) { // 土
                _pc.setCurrentHp((short) (_pc.getCurrentHp() + drainHp));
            }
        }
        return reset_dmg;
    }

    /**
     * 屬性武器特殊攻擊
     * 0:無屬性 1:地 2:火 4:水 8:風 16:光 32:暗 64:聖 128:邪
     *
     * @param damage
     * @return
     */
    public int set_item_power(final int damage) {
        int reset_dmg = damage;
        try {
            if (_weaponAttrEnchantKind > 0) {
                int random = 0;
                // 魔法特效
                switch (_weaponAttrEnchantKind) {
                    case 1: // 地
                        int time = 0;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 5%機率束縛敵人0.8秒
                                random = 10;
                                time = 800;
                                break;

                            case 2:// 10%機率束縛敵人1.0秒
                                random = 20;
                                time = 1000;
                                break;

                            case 3:// 20%機率束縛敵人1.5秒
                                random = 30;
                                time = 1500;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            if (!L1WeaponSkill.isFreeze(_target)) { // 凍結狀態
                                _target.broadcastPacketX8(new S_SkillSound(_target.getId(), 4184));
                                if (_targetPc != null) {
                                    _targetPc.setSkillEffect(STATUS_FREEZE, time);
                                    _targetPc.sendPackets(new S_SkillSound(_target.getId(), 4184));
                                    _targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

                                } else if (_targetNpc != null) {
                                    _targetNpc.setSkillEffect(STATUS_FREEZE, time);
                                    _targetNpc.setParalyzed(true);
                                }
                            }
                        }
                        break;

                    case 2: // 火
                        double adddmg = 0.0D;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 5%發動機率 造成1.2倍傷害
                                random = 10;
                                adddmg = 1.2D;
                                break;

                            case 2:// 10%發動機率 造成1.4倍傷害
                                random = 20;
                                adddmg = 1.4D;
                                break;

                            case 3:// 20%發動機率 造成1.6倍傷害
                                random = 30;
                                adddmg = 1.6D;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7756));
                            reset_dmg = (int) (reset_dmg * adddmg);
                        }
                        break;

                    case 4: // 水
                        int drainHp = 0;
                        int drainMp = 0;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 5%發動機率 吸血(傷害的0.2倍,例如打100滴剛發動就是吸了20滴HP) 吸魔隨機吸1~5
                                random = 10;
                                drainHp = (int) (reset_dmg * 0.2D);
                                drainMp = 1 + _random.nextInt(5);
                                break;

                            case 2:// 10%發動機率 吸血(傷害的0.4倍,例如打100滴剛發動就是吸了40滴HP) 吸魔隨機吸2~10
                                random = 20;
                                drainHp = (int) (reset_dmg * 0.4D);
                                drainMp = 2 + _random.nextInt(10);
                                break;

                            case 3:// 20%發動機率 吸血(傷害的0.6倍,例如打100滴剛發動就是吸了60滴HP) 吸魔隨機吸3~15
                                random = 30;
                                drainHp = (int) (reset_dmg * 0.6D);
                                drainMp = 3 + _random.nextInt(15);
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7749));
                            _pc.setCurrentHp((short) (_pc.getCurrentHp() + drainHp));
                            _pc.setCurrentMp((short) (_pc.getCurrentMp() + drainMp));
                            if (_targetPc != null) {
                                _targetPc.setCurrentMp((short) (_targetPc.getCurrentMp() - drainMp));

                            } else if (_targetNpc != null) {
                                _targetNpc.setCurrentMp((short) (_targetNpc.getCurrentMp() - drainMp));
                            }
                        }
                        break;

                    case 8: // 風
                        int r = 1;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 5%機率造成一格範圍傷害 (原始傷害固定50+浮動100)
                                random = 10;
                                r = 1;
                                break;

                            case 2:// 10%機率造成二格範圍傷害 (魔法傷害固定50+浮動100)
                                random = 20;
                                r = 2;
                                break;

                            case 3:// 20%機率造成三格範圍傷害 (魔法傷害固定50+浮動100)
                                random = 30;
                                r = 3;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7752));
                            final int dmg = 50 + _random.nextInt(100);
                            if (_targetPc != null) {
                                for (L1Object tgobj : World.get().getVisibleObjects(_pc, r)) {
                                    if (tgobj instanceof L1PcInstance) {
                                        final L1PcInstance tgpc = (L1PcInstance) tgobj;
                                        if (tgpc.isDead()) {
                                            continue;
                                        }
                                        // 排除同盟
                                        if (tgpc.getClanid() == _pc.getClanid()) {
                                            if (tgpc.getClanid() != 0) {
                                                continue;
                                            }
                                        }
                                        // 排除安全區
                                        if (tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
                                            continue;
                                        }
                                        tgpc.receiveDamage(_pc, dmg, false, false);// 物理傷害
                                    }
                                }

                            } else if (_targetNpc != null) {
                                for (L1Object tgobj : World.get().getVisibleObjects(_pc, r)) {
                                    if (tgobj instanceof L1MonsterInstance) {
                                        final L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
                                        if (tgmob.isDead()) {
                                            continue;
                                        }
                                        tgmob.receiveDamage(_pc, dmg);// 物理傷害
                                    }
                                }
                            }
                        }
                        break;

                    case 16: // 光
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 光之:1%召喚光裂(依人物魔功智力產生傷害)
                                random = 10;
                                break;

                            case 2:// 閃耀:2%召喚光裂(依人物魔功智力產生傷害)
                                random = 20;
                                break;

                            case 3:// 光靈:3%召喚光裂(依人物魔功智力產生傷害)
                                random = 30;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            final L1Magic magic = new L1Magic(_pc, _target);
                            final int magic_dmg = magic.calcMagicDamage(DISINTEGRATE);
                            magic.commit(magic_dmg, 0);
                            final L1Skills skill = SkillsTable.get().getTemplate(DISINTEGRATE);
                            final int castgfx = skill.getCastGfx();

                            _target.broadcastPacketX8(new S_SkillSound(_target.getId(), castgfx));
                            if (_targetPc != null) {
                                _targetPc.sendPackets(new S_SkillSound(_target.getId(), castgfx));
                            }
                        }
                        break;

                    case 32: // 暗
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 暗之:1%施展闇盲
                                random = 10;
                                break;

                            case 2:// 陰影:2%施展闇盲
                                random = 20;
                                break;

                            case 3:// 暗靈:3%施展闇盲
                                random = 30;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            // SKILL移轉
                            final SkillMode mode = L1SkillMode.get().getSkill(CURSE_BLIND);
                            if (mode != null) {
                                mode.start(_pc, _target, null, 10);
                            }
                            _target.broadcastPacketX8(new S_SkillSound(_target.getId(), 746));
                            if (_targetPc != null) {
                                _targetPc.sendPackets(new S_SkillSound(_target.getId(), 746));
                            }
                        }
                        break;

                    case 64: // 聖
                        int integer = 0;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 聖之:1%施展魔法封印(封印時間:5秒)
                                random = 10;
                                integer = 5;
                                break;

                            case 2:// 神聖:2%施展魔法封印(封印時間:8秒)
                                random = 20;
                                integer = 8;
                                break;

                            case 3:// 聖靈:3%施展魔法封印(封印時間:10秒)
                                random = 30;
                                integer = 10;
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            if (!_target.hasSkillEffect(SILENCE)) {
                                _target.setSkillEffect(SILENCE, integer * 1000);
                                _target.broadcastPacketX8(new S_SkillSound(_target.getId(), 2177));
                                if (_targetPc != null) {
                                    _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 2177, integer));
                                }
                            }
                        }
                        break;

                    case 128: // 邪
                        int[] gfxs = null;
                        switch (_weaponAttrEnchantLevel) {
                            case 1:// 邪之:1%施展變形術(目標變形:狼人,妖魔鬥士)
                                random = 10;
                                gfxs = new int[]{3865, 3864};
                                break;

                            case 2:// 邪惡:2%施展變形術(目標變形:狼人,妖魔鬥士,人形殭屍)
                                random = 20;
                                gfxs = new int[]{3865, 3864, 3872};
                                break;

                            case 3:// 邪靈:3%施展變形術(目標變形:狼人,妖魔鬥士,人形殭屍,紙人)
                                random = 30;
                                gfxs = new int[]{3865, 3864, 3872, 1538};
                                break;
                        }
                        if (_random.nextInt(1000) <= random) {
                            if (_targetPc != null) {
                                _targetPc.sendPacketsX8(new S_SkillSound(_target.getId(), 230));
                                final int polyId = gfxs[_random.nextInt(gfxs.length)];
                                L1PolyMorph.doPoly(_targetPc, polyId, 60, L1PolyMorph.MORPH_BY_ITEMMAGIC);

                            } else if (_targetNpc != null) {
                                // 不是BOSS召喚表物件
                                if (!_targetNpc.getNpcTemplate().is_boss()) {
                                    _target.broadcastPacketX8(new S_SkillSound(_target.getId(), 230));
                                    final int polyId = gfxs[_random.nextInt(gfxs.length)];
                                    L1PolyMorph.doPoly(_target, polyId, 60, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                }
                            }
                        }
                        break;
                }
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return reset_dmg;
    }

}
