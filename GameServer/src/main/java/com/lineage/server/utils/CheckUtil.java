package com.lineage.server.utils;

import static com.lineage.server.model.skill.L1SkillId.DECAY_POTION;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_PARALYZED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_PARALYZED;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.world.World;

/**
 * 對像檢查器
 * @author daien
 *
 */
public class CheckUtil {

	private static final Log _log = LogFactory.getLog(CheckUtil.class);

	private CheckUtil() {
	}

	/**
	 * 檢查攻擊致死成立的PC
	 * @param lastAttacker
	 * 
	 * @return 攻擊致死成立的PC
	 */
	public static L1PcInstance checkAtkPc(final L1Character lastAttacker) {
		try {
			// 判斷主要攻擊者
			L1PcInstance pc = null;
			
			if (lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
				pc = (L1PcInstance) lastAttacker;
				
			} else if (lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
				final L1PetInstance atk = (L1PetInstance) lastAttacker;
				if (atk.getMaster() != null) {
					if (atk.getMaster() instanceof L1PcInstance) {
						pc = (L1PcInstance) atk.getMaster();
					}
				}
				
			} else if (lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				final L1SummonInstance atk = (L1SummonInstance) lastAttacker;
				if (atk.getMaster() != null) {
					if (atk.getMaster() instanceof L1PcInstance) {
						pc = (L1PcInstance) atk.getMaster();
					}
				}
				
			} else if (lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				final L1IllusoryInstance atk = (L1IllusoryInstance) lastAttacker;
				if (atk.getMaster() != null) {
					if (atk.getMaster() instanceof L1PcInstance) {
						pc = (L1PcInstance) atk.getMaster();
					}
				}

			} else if (lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件
				final L1EffectInstance atk = (L1EffectInstance) lastAttacker;
				if (atk.getMaster() != null) {
					if (atk.getMaster() instanceof L1PcInstance) {
						pc = (L1PcInstance) atk.getMaster();
					}
				}
				
			} else {
				return null;
			}
			return pc;
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 檢查地圖使用權
	 * @param pc 檢查對像
	 */
	public static void isUserMap(final L1PcInstance pc) {
		try {
			if (!pc.isGm()) {
				// 檢查 時間/VIP 地圖使用權
				isTimeMap(pc);
				
				// 檢查團隊地圖使用權
				isPartyMap(pc);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 檢查團隊地圖使用權
	 * @param pc 檢查對像
	 */
	private static void isPartyMap(final L1PcInstance pc) {
		try {
			short mapid = pc.getMapId();
			Integer userCount = NpcTeleportTable.get().isPartyMap(mapid);
			
			if (userCount != null) {
				if (!pc.isInParty()) {
					// 425：您並沒有參加任何隊伍。  
					pc.sendPackets(new S_ServerMessage(425));
					L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
					return;
				}
				
				int partyCount = pc.getParty().partyUserInMap(mapid);
				if (partyCount < userCount.intValue()) {
					L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 檢查 時間/VIP 地圖使用權
	 * @param pc 檢查對像
	 */
	private static void isTimeMap(final L1PcInstance pc) {
		try {
			short mapid = pc.getMapId();
			// 時效地圖判斷
			if (NpcTeleportTable.get().isTimeMap(mapid)) {
				// 該人物 是否在使用者清單中
				Integer time = ServerUseMapTimer.MAP.get(pc);
				if (time == null) {
					L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 全物件無法使用
	 * @param pc 檢查對像
	 * @return
	 */
	public static boolean getUseItemAll(final L1PcInstance pc) {
		/*33 木乃伊的詛咒
		50 冰矛圍籬
		66 沉睡之霧
		71 藥水霜化術
		87 衝擊之暈
		157 大地屏障
		189 衝擊之膚
		192 奪命之雷*/
		if (pc.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
			return false;
		}

		if (pc.hasSkillEffect(STATUS_POISON_PARALYZED)) {
			return false;
		}

		if (pc.hasSkillEffect(FOG_OF_SLEEPING)) {
			return false;
		}

		if (pc.hasSkillEffect(SHOCK_STUN)) {
			return false;
		}

		return true;
	}

	/**
	 * 無法使用指定類型道具
	 * @param pc 檢查對像
	 * @return
	 */
	public static boolean getUseItem(final L1PcInstance pc) {
		/*33 木乃伊的詛咒
		50 冰矛圍籬
		66 沉睡之霧
		71 藥水霜化術
		87 衝擊之暈
		157 大地屏障
		189 衝擊之膚
		192 奪命之雷*/

		if (pc.hasSkillEffect(DECAY_POTION)) {
			pc.sendPackets(new S_ServerMessage(698)); // 喉嚨灼熱，無法喝東西。
			return false;
		}

		if (pc.hasSkillEffect(EARTH_BIND)) {
			return false;
		}

		if (pc.hasSkillEffect(ICE_LANCE)) {
			return false;
		}

		return true;
	}
	
	/**
	 * 該座標點上有物件(不可穿透目標)
	 * @param pc 檢查對像
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @return true有 false沒有
	 */
	public static boolean checkPassable(final L1PcInstance pc, final int locx, final int locy, final short mapid) {
		if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
			// 是副本專用地圖(不列入判斷的地形)
			return false;
		}
		// 1格範圍內物件
		final Collection<L1Object> allObj = World.get().getVisibleObjects(pc, 1);
		for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();) {
			final L1Object obj = iter.next();
			// 道具
			if (obj instanceof L1ItemInstance) {
				continue;
			}
			if (!(obj instanceof L1Character)) {
				continue;
			}
			final L1Character character = (L1Character) obj;
			// 忽略隱身
			if (character.isInvisble()) {
				continue;
			}
			if (character instanceof L1NpcInstance) {
				continue;
			}
			if (character instanceof L1PcInstance) {
				final L1PcInstance tgpc = (L1PcInstance) character;
				if (pc.isGmInvis()) {// 忽略GM隱身
					continue;
				}
				if (pc.isGhost()) {// 忽略鬼魂模式
					continue;
				}
				if ((tgpc.getX() == locx) && 
						(tgpc.getY() == locy) && 
						(tgpc.getMapId() == mapid)) {// 其他
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 檢查該物件是否允許攻擊(技能)
	 * @param cha
	 * @return
	 */
	public static boolean checkAttackSkill(final L1Character cha) {
		try {
			if (cha instanceof L1EffectInstance) { // 效果不列入目標
				return false;

			} else if (cha instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
				return false;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}
}
