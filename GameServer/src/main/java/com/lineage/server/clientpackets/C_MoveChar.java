package com.lineage.server.clientpackets;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.datatables.DungeonRTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_Lock;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldTrap;

/**
 * 要求角色移動
 * 基本封包長度:
 *
 * @author daien
 *
 */
public class C_MoveChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_MoveChar.class);

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public C_MoveChar(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			final L1PcInstance pc = client.getActiveChar();

			/*final long noetime = System.currentTimeMillis();
			if (noetime - pc.get_spr_move_time() <= SprTable.get().spr_move_speed(pc.getTempCharGfx())) {
				if (!pc.isGm()) {
					pc.getNetConnection().kick();
					return;
				}
			}
			pc.set_spr_move_time(noetime);*/

			if (pc.isDead()) {// 死亡
				return;
			}

			if (pc.isTeleport()) { // 順移處理作業
				return;
			}
			
          /*  if (pc.isBotActived()) { // 掛機狀態無法移動
            	return;
            }*/
			
			int locx = 0;// 目前位置
			int locy = 0;
			int heading = 0;

			try {
				locx = this.readH();
				locy = this.readH();
				heading = this.readC();

				heading = Math.min(heading, 7);

			} catch (final Exception e) {
				// 座標取回失敗
				return;
			}

			pc.killSkillEffectTimer(MEDITATION);// 解除冥想術
			pc.setCallClanId(0); // 人物移動呼喚盟友無效

			if (!pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 絕對屏障狀態
				pc.setHPRegenState(REGENSTATE_MOVE);
				pc.setMPRegenState(REGENSTATE_MOVE);
			}
			
			// 解除舊座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), true);
			
			// 移動前位置
			final int oleLocx = pc.getX();
			final int oleLocy = pc.getY();

			// 移動後位置
			final int newlocx = locx + HEADING_TABLE_X[heading];
			final int newlocy = locy + HEADING_TABLE_Y[heading];
			
			try {
				// 不允許穿過該點
				boolean isError = false;
				
				// 異位判斷(封包數據 與 核心數據 不吻合)
				if ((locx != oleLocx) && (locy != oleLocy)) {
					isError = true;
				}
				
				// 商店村模式
				if (pc.isPrivateShop()) {
					isError = true;
				}
				
				// 無法攻擊/使用道具/技能/回城的狀態
				if (pc.isParalyzedX()) {
					isError = true;
				}
				
				// 位置具有障礙
				if (!isError) {
					// 穿透判斷
					//boolean isPassable = pc.getMap().isPassable(newlocx, newlocy);
					boolean isPassable = pc.getMap().isPassable(oleLocx, oleLocy, heading, null);
					//System.out.println("穿透判斷: " + isPassable);
					// 該點不可通行
					if (!isPassable) {
						//System.out.println("該點不可通行");
						// 該座標點上具有物件
						if (CheckUtil.checkPassable(pc, newlocx, newlocy, pc.getMapId())) {
							//System.out.println("該座標點上具有物件");
							isError = true;
						}
					}
				}

				if (isError) {
					pc.sendPackets(new S_OwnCharPack(pc));
			        pc.removeAllKnownObjects();
			        pc.updateObject();
			        pc.sendVisualEffectAtTeleport();
			        pc.sendPackets(new S_CharVisualUpdate(pc));
					return;
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			
			final int result = pc.speed_Attack().checkInterval(AcceleratorChecker.ACT_TYPE.MOVE);
			if (result == AcceleratorChecker.R_DISCONNECTED) {
				_log.error("要求角色移動:速度異常(" + pc.getName() + ")");
			}//*/

			// 檢查地圖使用權
			CheckUtil.isUserMap(pc);

			// 地圖切換
			if (DungeonTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
				return;
			}
			
			// 地圖切換(多點)
			if (DungeonRTable.get().dg(newlocx, newlocy, pc.getMap().getId(), pc)) {
				return;
			}
			
			// 記錄移動前座標
			pc.setOleLocX(oleLocx);
			pc.setOleLocY(oleLocy);
			
			// 設置新作標點
			pc.getLocation().set(newlocx, newlocy);
			
			// 設置新面向
			pc.setHeading(heading);
			
			if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
				// 送出移動封包
				pc.broadcastPacketAll(new S_MoveCharPacket(pc));
			}
			
			// 設置娃娃移動
			pc.setNpcSpeed();

			// 新增座標障礙宣告
			pc.getMap().setPassable(pc.getLocation(), false);

			// 踩到陷阱的處理
			WorldTrap.get().onPlayerMoved(pc);
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}