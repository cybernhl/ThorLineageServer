package com.lineage.server.templates;

/**
 * 掉落物品資料
 * @author daien
 *
 */
public class L1Drop {

	int _mobId;
	int _itemId;
	int _min;
	int _max;
	int _chance;
	int bless;

	public L1Drop(final int mobId, final int itemId, final int min, final int max, final int chance, final int bless) {
		this._mobId = mobId;
		this._itemId = itemId;
		this._min = min;
		this._max = max;
		this._chance = chance;
		this.bless = bless;
	}

	/**
	 * 機率
	 * @return
	 */
	public int getChance() {
		return this._chance;
	}

	/**
	 * 物品編號
	 * @return
	 */
	public int getItemid() {
		return this._itemId;
	}

	/**
	 * 最大量
	 * @return
	 */
	public int getMax() {
		return this._max;
	}

	/**
	 * 最小量
	 * @return
	 */
	public int getMin() {
		return this._min;
	}

	/**
	 * NPC編號
	 * @return
	 */
	public int getMobid() {
		return this._mobId;
	}
	public int getBless() {
		return this.bless;
	}
}
