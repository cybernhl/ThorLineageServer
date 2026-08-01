package com.lineage.data.item_armor.set;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 套裝使用的判斷
 * @author daien
 *
 */
public class ArmorSetImpl extends ArmorSet {

	private static final Log _log = LogFactory.getLog(ArmorSetImpl.class);
	
	// 套裝編號
	private final int _id;
	
	// 套裝物品編號陣列
	private final int _ids[];
	
	// 套裝效果組合
	private final ArrayList<ArmorSetEffect> _effects;
	
	// 套裝效果動畫
	private final int _gfxids[];

	/**
	 * 套裝的判斷設置
	 * @param ids 套裝物品編號陣列
	 */
	protected ArmorSetImpl(final int id, final int ids[], final int gfxids[]) {
		this._id = id;
		this._ids = ids;
		this._gfxids = gfxids;
		this._effects = new ArrayList<ArmorSetEffect>();
	}

	/**
	 * 套裝編號
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 套裝物品編號陣列
	 * @return
	 */
	@Override
	public int[] get_ids() {
		return _ids;
	}
	
	/**
	 * 加入該套裝效果組合
	 * @param effect
	 */
	public void addEffect(final ArmorSetEffect effect) {
		this._effects.add(effect);
	}

	/**
	 * 移除該套裝效果組合
	 * @param effect
	 */
	public void removeEffect(final ArmorSetEffect effect) {
		this._effects.remove(effect);
	}
	
	/**
	 * 傳回該套裝附加的效果陣列
	 * @return
	 */
	@Override
	public int[] get_mode() {
		int[] mode = new int[21];
		for (final ArmorSetEffect set : _effects) {
			// 六屬性(力量,敏捷,體質,精神,魅力,智力)

			if (set instanceof EffectStat_Str) {
				// 套裝效果:力量增加
				mode[0] = set.get_mode();
			}

			if (set instanceof EffectStat_Dex) {
				// 套裝效果:敏捷增加
				mode[1] = set.get_mode();
			}

			if (set instanceof EffectStat_Con) {
				// 套裝效果:體質增加
				mode[2] = set.get_mode();
			}

			if (set instanceof EffectStat_Wis) {
				// 套裝效果:精神增加
				mode[3] = set.get_mode();
			}

			if (set instanceof EffectStat_Int) {
				// 套裝效果:智力增加
				mode[4] = set.get_mode();
			}

			if (set instanceof EffectStat_Cha) {
				// 套裝效果:魅力增加
				mode[5] = set.get_mode();
			}
			
			// HP相關
			if (set instanceof EffectHp) {
				// 套裝效果:HP增加
				mode[6] = set.get_mode();
			}
			
			// MP相關
			if (set instanceof EffectMp) {
				// 套裝效果:MP增加
				mode[7] = set.get_mode();
			}
			
			// SP(魔攻) XXX
			mode[8] = 0;
			
			// 加速效果 XXX
			mode[9] = 0;
			
			if (set instanceof EffectMr) {
				// 套裝效果:抗魔增加
				mode[10] = set.get_mode();
			}
			
			// 4屬性(水屬性,風屬性,火屬性,地屬性)

			if (set instanceof EffectDefenseFire) {
				// 套裝效果:火屬性增加
				mode[11] = set.get_mode();
			}

			if (set instanceof EffectDefenseWater) {
				// 套裝效果:水屬性增加
				mode[12] = set.get_mode();
			}

			if (set instanceof EffectDefenseWind) {
				// 套裝效果:風屬性增加
				mode[13] = set.get_mode();
			}

			if (set instanceof EffectDefenseEarth) {
				// 套裝效果:地屬性增加
				mode[14] = set.get_mode();
			}

			// 六耐性

			if (set instanceof EffectRegist_Freeze) {
				// 套裝效果:寒冰耐性增加
				mode[15] = set.get_mode();
			}

			if (set instanceof EffectRegist_Stone) {
				// 套裝效果:石化耐性增加
				mode[16] = set.get_mode();
			}

			if (set instanceof EffectRegist_Sleep) {
				// 套裝效果:睡眠耐性增加
				mode[17] = set.get_mode();
			}

			if (set instanceof EffectRegist_Blind) {
				// 套裝效果:暗闇耐性增加
				mode[18] = set.get_mode();
			}

			if (set instanceof EffectRegist_Stun) {
				// 套裝效果:暈眩耐性增加
				mode[19] = set.get_mode();
			}

			if (set instanceof EffectRegist_Sustain) {
				// 套裝效果:支撐耐性增加
				mode[20] = set.get_mode();
			}
		}
		return mode;
	}
	
	/**
	 * 套裝完成效果
	 * @param pc
	 */
	@Override
	public void giveEffect(final L1PcInstance pc) {
		try {
			for (final ArmorSetEffect effect : this._effects) {
				effect.giveEffect(pc);
			}
			// 套裝效果動畫
			if (_gfxids != null) {
				for (int gfx : _gfxids) {
					// 動畫效果
					pc.sendPacketsX8(
							new S_SkillSound(
									pc.getId(), gfx
									));
				}
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 套裝解除效果
	 * @param pc
	 */
	@Override
	public void cancelEffect(final L1PcInstance pc) {
		try {
			for (final ArmorSetEffect effect : this._effects) {
				effect.cancelEffect(pc);
			}
			
		} catch (final Exception ex) {
			_log.error(ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * 套裝完成
	 * @param pc
	 * @return
	 */
	@Override
	public final boolean isValid(final L1PcInstance pc) {
		return pc.getInventory().checkEquipped(this._ids);
	}

	/**
	 * 是否為套裝中組件
	 * @param id
	 * @return
	 */
	@Override
	public boolean isPartOfSet(final int id) {
		for (final int i : this._ids) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否裝備了相同界指2個
	 * @param pc
	 * @return
	 */
	@Override
	public boolean isEquippedRingOfArmorSet(final L1PcInstance pc) {
		final L1PcInventory pcInventory = pc.getInventory();
		L1ItemInstance armor = null;
		boolean isSetContainRing = false;

		// 尋找套裝物件是否為戒指
		for (final int id : this._ids) {
			armor = pcInventory.findItemId(id);
			if (armor.getItem().getUseType() == 23) {// 戒指
			//if ((armor.getItem().getType2() == 2) && (armor.getItem().getType() == 9)) { // ring
				isSetContainRing = true;
				break;
			}
		}

		// 是否裝備了相同界指2個
		if ((armor != null) && isSetContainRing) {
			final int itemId = armor.getItem().getItemId();
			// 已經帶了 2個戒指
			if (pcInventory.getTypeEquipped(2, 9) == 2) {
				L1ItemInstance ring[] = new L1ItemInstance[2];
				ring = pcInventory.getRingEquipped();// 裝備中界指陣列
				if ((ring[0].getItem().getItemId() == itemId)
						&& (ring[1].getItem().getItemId() == itemId)) {
					return true;
				}
			}
		}
		return false;
	}
}
