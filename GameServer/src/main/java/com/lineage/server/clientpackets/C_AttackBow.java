package com.lineage.server.clientpackets;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

/**
 * 要求角色攻擊(遠距離)
 *
 * @author dexc
 *
 */
public class C_AttackBow extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AttackBow.class);

	public C_AttackBow(final byte[] decrypt, final ClientExecutor client) {
		 try {
		// 資料載入
        this.read(decrypt);

        final L1PcInstance pc = client.getActiveChar();


        if (pc.isGhost()) { // 鬼魂模式
            return;
        }

        if (pc.isDead()) { // 死亡
            return;
        }

        if (pc.isTeleport()) { // 傳送中
            return;
        }

        if (pc.isPrivateShop()) { // 商店村模式
            return;
        }

        if (pc.getInventory().getWeight182() >= 24) { // 重量過重
            // 110 \f1當負重過重的時候，無法戰鬥。
            pc.sendPackets(new S_ServerMessage(110));
            return;
        }

        final int result = pc.speed_Attack().checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
        if (result == AcceleratorChecker.R_DISCONNECTED) {
            _log.error("要求角色攻擊:速度異常(" + pc.getName() + ")");
        } // */

        if (pc.isInvisble()) { // 隱身狀態
            return;
        }

        if (pc.isInvisDelay()) { // 隱身延遲
            return;
        }

        // 無法攻擊/使用道具/技能/回城的狀態
        if (pc.isParalyzedX()) {
            return;
        }

        int targetId = 0;
        int locx = 0;
        int locy = 0;
        int targetX = 0;
        int targetY = 0;

        try {
            // 攻擊點資訊
            targetId = this.readD();
            locx = this.readH();
            locy = this.readH();

        } catch (final Exception e) {
            return;
        }

        if (locx == 0) {
            return;
        }
        if (locy == 0) {
            return;
        }

        targetX = locx;
        targetY = locy;

        final L1Object target = World.get().findObject(targetId);

        if (target instanceof L1Character) {
            if (target.getMapId() != pc.getMapId()) { // 攻擊位置異常
                return;
            }
        }

        // 檢查地圖使用權
        CheckUtil.isUserMap(pc);

        if (target instanceof L1NpcInstance) {
            if (((L1NpcInstance) target).isDead()) {
                return;
            }
            final int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) { // 躲藏
                return;
            }
            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) { // 空中
                return;
            }
        }
        pc.setSkillEffect(L1SkillId.attack_no_spr, 500);
        // 絕對屏障解除
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            pc.startHpRegeneration();
            pc.startMpRegeneration();
        }

        pc.killSkillEffectTimer(MEDITATION);

        pc.delInvis(); // 透明狀態解除

        pc.setHPRegenState(REGENSTATE_ATTACK);
        pc.setMPRegenState(REGENSTATE_ATTACK);

        if ((target != null) && !((L1Character) target).isDead()) {
            if (target instanceof L1PcInstance) {
                L1PcInstance tg = (L1PcInstance) target;
                pc.setNowTarget(tg);
            }
            target.onAction(pc);

        } else { // 空攻擊
            // 設置面向
            pc.setHeading(pc.targetDirection(locx, locy));
            // 取回使用武器
            final L1ItemInstance weapon = pc.getWeapon();

            if (weapon != null) {
                // 武器類型
                final int weaponType = weapon.getItem().getType1();

                switch (weaponType) {
                case 20:// 弓
                    final L1ItemInstance arrow = pc.getInventory().getArrow();
                    if (arrow != null) { // 具有箭
                        this.arrowAction(pc, arrow, 66, targetX, targetY);

                    } else {
                        if (weapon.getName().equals("$1821")) {// 沙哈之弓
                            this.arrowAction(pc, null, 2349, targetX, targetY);

                        } else {
                            this.nullAction(pc);
                        }
                    }
                    break;
                case 62:// 鐵手甲
                    final L1ItemInstance sting = pc.getInventory().getSting();
                    if (sting != null) { // 具有飛刀
                        this.arrowAction(pc, sting, 2989, targetX, targetY);

                    } else {
                        this.nullAction(pc);
                    }
                    break;
                }
            }
        }

    } catch (final Exception e) {
        // _log.error(e.getLocalizedMessage(), e);

    } finally {
        this.over();
    }
}

/**
 * 空攻擊(具有消耗道具)
 * 
 * @param pc
 *            執行者
 * @param item
 *            消耗道具
 * @param gfx
 *            動畫
 * @param targetX
 *            目標X
 * @param targetY目標Y
 */
private void arrowAction(L1PcInstance pc, L1ItemInstance item, int gfx, int targetX, int targetY) {
    pc.sendPacketsAll(new S_UseArrowSkill(pc, gfx, targetX, targetY));

    if (item != null) {
        pc.getInventory().removeItem(item, 1);
    }
}

/**
 * 空攻擊(無消耗道具)
 * 
 * @param pc
 *            執行者
 */
private void nullAction(L1PcInstance pc) {
    int aid = 1;
    // 外型編號改變動作
    if (pc.getTempCharGfx() == 3860) {
        aid = 21;
    }

    pc.sendPacketsAll(new S_ChangeHeading(pc));
    // 送出封包(動作)
    pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), aid));
}

@Override
public String getType() {
    return this.getClass().getSimpleName();
}
}
