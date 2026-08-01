package com.lineage.server.clientpackets;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;

import com.lineage.server.ActionCodes;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.world.World;

/**
 * 要求角色攻擊
 *
 * @author dexc
 *
 */
public class C_Attack extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Attack.class);

	public C_Attack(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// 資料載入
			this.read(decrypt);
			int targetId = readD();
			int x = readH();
			int y = readH();
			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost() || pc.isDead() || pc.isTeleport()) {
				return;
			}		
			if (pc.isPrivateShop()) {
				return;
			}
			L1Object target = World.get().findObject(targetId);
			if (pc.getInventory().getWeight182() >= 24) { // 重量过重
				// 110 \f1当负重过重的时候，无法战斗。
				pc.sendPackets(new S_ServerMessage(110));
				return;
			}

			if (pc.isInvisble()) {
				return;
			}

			if (pc.isInvisDelay()) {
				return;
			}

			if (target instanceof L1Character) {
				if (target.getMapId() != pc.getMapId()
						|| pc.getLocation().getLineDistance(
								target.getLocation()) > 20D) { // 攻击距离确认
					return;
				}
			}

			if (target instanceof L1NpcInstance) {
				if (((L1NpcInstance) target).isDead()) {
					return;
				}
				if (((L1NpcInstance) target).getHiddenStatus() != 0) { // 空中、钻地
					return;
				}
			}
			pc.setSkillEffect(L1SkillId.attack_no_spr, 500);
			// 攻要求间隔をチェックする
			final int result = pc.speed_Attack().checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
			if (result == AcceleratorChecker.R_DISCONNECTED) {
				_log.error("要求角色攻擊:速度異常(" + pc.getName() + ")");
			}//*/

			// 绝对屏障解除
			if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
				pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
				// pc.startMpRegenerationByDoll();
			}
			pc.killSkillEffectTimer(L1SkillId.MEDITATION);

			pc.delInvis(); // 透明态の解除

			pc.setHPRegenState(REGENSTATE_ATTACK);
			pc.setMPRegenState(REGENSTATE_ATTACK);

			if ((target instanceof L1Character) && !((L1Character) target).isDead()) {
				if (target instanceof L1PcInstance) {
					L1PcInstance tg = (L1PcInstance) target;
					pc.setNowTarget(tg);
				}
				target.onAction(pc);
			} else { // 空攻
				int OutGfxId = -1;
				if (pc.getWeapon() != null) {
					L1ItemInstance arrow = pc.getInventory().getArrow();
					L1ItemInstance sting = pc.getInventory().getSting();
					L1ItemInstance weapon = pc.getWeapon();
					int weaponType = weapon.getItem().getType1();
					if (weaponType == 20) {
						if (arrow != null) { // 矢がある场合
							OutGfxId = 66;
							pc.getInventory().removeItem(arrow, 1);
						} else if (weapon.getItemId() == 190||weapon.getItemId() == 100190// &&weapon.getItemId()
															// ==
															// 100190//9.3活动新增
						) { // 矢が无くてサイハの场合
							OutGfxId = 2349;
						}
						pc.sendPacketsAll(new S_UseArrowSkill(pc, OutGfxId, x, y)); // 发送封包
					} else if (weaponType == 62 && sting != null) { // ガントレット
						OutGfxId = 2989;
						pc.getInventory().removeItem(sting, 1);
						pc.sendPacketsAll(new S_UseArrowSkill(pc, OutGfxId, x, y)); // 发送封包
					}
				}
				pc.setHeading(pc.targetDirection(x, y)); // 向きのセット
				pc.sendPackets(new S_ChangeHeading(pc));
				pc.sendPacketsAll(new S_AttackPacketPc(pc));
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			this.over();
		}

	}
}
