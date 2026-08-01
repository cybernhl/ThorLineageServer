package com.lineage.server.timecontroller.pc;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_NONE;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_S;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BLUE_POTION;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import com.lineage.server.model.skill.L1SkillId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.types.Point;

/**
 * PC MP回復執行
 * @author dexc
 *
 */
public class MprExecutor {

	private static final Log _log = LogFactory.getLog(MprExecutor.class);

	// 精神 回復MP增加/減少 <精神量, 影響質>
	private static final Map<Integer, Integer> _wis = new HashMap<Integer, Integer>();

	// 技能 回復MP增加/減少 <技能編號, 影響質>
	private static final Map<Integer, Integer> _skill = new HashMap<Integer, Integer>();

	// 地圖 回復MP增加/減少 <地圖編號, 影響質>
	private static final Map<Integer, Integer> _mapId = new HashMap<Integer, Integer>();

	private static MprExecutor _instance;
	
	protected static MprExecutor get() {
		if (_instance == null) {
			_instance = new MprExecutor();
		}
		return _instance;
	}

	private MprExecutor() {
		_skill.put(MEDITATION, 5);
		_skill.put(COOKING_1_2_N, 3);
		_skill.put(COOKING_1_2_S, 3);
		_skill.put(COOKING_2_4_N, 2);
		_skill.put(COOKING_2_4_S, 2);
		_skill.put(COOKING_3_5_N, 2);
		_skill.put(COOKING_3_5_S, 2);
		_skill.put(ADLV80_1, 3);
		_skill.put(ADLV80_2, 3);
		_skill.put(L1SkillId.STATUS_WISDOM_POTION, 2);
		//_skill.put(STATUS_BLUE_POTION, 8);

		// 旅館
		_mapId.put(16384, 3);
		_mapId.put(16384, 3);
		_mapId.put(16896, 3);
		_mapId.put(17408, 3);
		_mapId.put(17920, 3);
		_mapId.put(18432, 3);
		_mapId.put(18944, 3);
		_mapId.put(19968, 3);
		_mapId.put(19456, 3);
		_mapId.put(20480, 3);
		_mapId.put(20992, 3);
		_mapId.put(21504, 3);
		_mapId.put(22016, 3);
		_mapId.put(22528, 3);
		_mapId.put(23040, 3);
		_mapId.put(23552, 3);
		_mapId.put(24064, 3);
		_mapId.put(24576, 3);
		_mapId.put(25088, 3);
		// 城堡
		_mapId.put(15, 10);// 肯特內城
		_mapId.put(29, 10);// 風木城內城
		_mapId.put(52, 10);// 奇巖內城
		_mapId.put(64, 10);// 海音城堡
		//this._mapId.put(66, 10);// 侏儒洞穴
		_mapId.put(300, 10);// 亞丁內城

		_wis.put(0, 1);
		_wis.put(1, 1);
		_wis.put(2, 1);
		_wis.put(3, 1);
		_wis.put(4, 1);
		_wis.put(5, 1);
		_wis.put(6, 1);
		_wis.put(7, 1);
		_wis.put(8, 1);
		_wis.put(9, 1);
		_wis.put(10, 1);
		_wis.put(11, 1);
		_wis.put(12, 1);
		_wis.put(13, 1);
		_wis.put(14, 1);
		_wis.put(15, 2);
		_wis.put(16, 2);
	}

	/**
	 * PC MP回復執行 判斷
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	protected boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			
			// 死亡
			if (tgpc.isDead()) {
				return false;
			}
			
			// 傳送狀態
			if (tgpc.isTeleport()) {
				return false;
			}
			
			// MP已滿
			if (tgpc.getCurrentMp() >= tgpc.getMaxMp()) {
				return false;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
	
	protected void checkRegenMp(final L1PcInstance tgpc) {
		try {
			tgpc.set_mpRegenType(tgpc.mpRegenType() + tgpc.getMpRegenState());
			tgpc.setMPRegenState(REGENSTATE_NONE);

			if (tgpc.isRegenMp()) {
				regenMp(tgpc);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private static void regenMp(final L1PcInstance tgpc) {
		if (tgpc.getInventory().getWeight182() >= 15) {
			return;
		}
		if (tgpc.get_food() <= 43) {
			return;
		}
		tgpc.set_mpRegenType(0);

		if (tgpc.getMapId() == 201) {// 法師試煉地監
			// 魔力不會自動回復
			return;
		}
		int baseMpr = 1;
		// 精神補正
		Integer wis = _wis.get(tgpc.getWis());
		if (wis != null) {
			baseMpr = wis;
			
		} else {
			wis = (int) tgpc.getWis();
			baseMpr = 3;
		}

		// 技能補正
		if (!tgpc.getSkillisEmpty() && tgpc.getSkillEffect().size() > 0) {
			try {
				for (final Object key : tgpc.getSkillEffect().toArray()) {
					if (((Integer)key).equals(STATUS_BLUE_POTION)) {// 藍水
						if (wis < 11) { // 精神未滿11
							baseMpr += 1;

						} else {
							baseMpr += tgpc.getWis() - 10;
						}
						
					} else {
						final Integer integer = _skill.get(key);
						if (integer != null) {
							baseMpr += integer;
						}
					}
				}
				
			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		// 血盟小屋
		if (L1HouseLocation.isInHouse(tgpc.getX(), tgpc.getY(), tgpc.getMapId())) {
			baseMpr += ConfigOther.HOMEMPR;
		}

		// 地下盟屋
		if (L1HouseLocation.isInHouse(tgpc.getMapId())) {
			baseMpr += ConfigOther.HOMEMPR;
		}
		final Integer rmp = _mapId.get((int) tgpc.getMapId());
		if (rmp != null) {
			baseMpr += rmp;
		}

		// 世界樹
		if (tgpc.isElf()) {
			if (tgpc.getMapId() == 4) {
				if (tgpc.getLocation().isInScreen(new Point(33055, 32336))) {
					baseMpr += 2;
				}
			}
		}

		if (tgpc.getOriginalMpr() > 0) { // WIS MPR補正
			baseMpr += tgpc.getOriginalMpr();
		}

		int itemMpr = tgpc.getInventory().mpRegenPerTick();
		itemMpr += tgpc.getMpr();

		itemMpr += tgpc.getEquipSlot().getAttachMpR();

		final int mpr = baseMpr + itemMpr;
		int newMp = tgpc.getCurrentMp() + mpr;

		newMp = Math.max(newMp, 0);

		tgpc.setCurrentMp(newMp);
	}
}
