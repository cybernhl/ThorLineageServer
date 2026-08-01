package com.lineage.server.templates;

import java.util.Random;

public class L1ItemsWeapon extends L1Item {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public L1ItemsWeapon() {
	}

	private static Random _random = new Random();

	private int _add_dmg_min = 0;
	
	private int _add_dmg_max = 0;
	
	public void set_add_dmg(final int add_dmg_min, final int add_dmg_max) {
		this._add_dmg_min = add_dmg_min;
		this._add_dmg_max = add_dmg_max;
	}
	
	@Override
	public int get_add_dmg() {
		int add_dmg = 0;
		if (_add_dmg_min != 0 && _add_dmg_max != 0) {
			add_dmg = _add_dmg_min + _random.nextInt(_add_dmg_max - _add_dmg_min);
		}
		return add_dmg;
	}
	
	private int _range = 0; // ● 射程範圍

	@Override
	public int getRange() {
		return this._range;
	}

	public void setRange(final int i) {
		this._range = i;
	}

	private int _hitModifier = 0; // ● 命中率補正

	@Override
	public int getHitModifier() {
		return this._hitModifier;
	}

	public void setHitModifier(final int i) {
		this._hitModifier = i;
	}

	private int _dmgModifier = 0; // ● 補正

	@Override
	public int getDmgModifier() {
		return this._dmgModifier;
	}

	public void setDmgModifier(final int i) {
		this._dmgModifier = i;
	}

	private int _doubleDmgChance; // ● DB、發動確率

	@Override
	public int getDoubleDmgChance() {
		return this._doubleDmgChance;
	}

	public void setDoubleDmgChance(final int i) {
		this._doubleDmgChance = i;
	}

	private int _magicDmgModifier = 0; // ● 攻擊魔法補正

	@Override
	public int getMagicDmgModifier() {
		return this._magicDmgModifier;
	}

	public void setMagicDmgModifier(final int i) {
		this._magicDmgModifier = i;
	}

	private int _canbedmg = 0; // ● 損傷有無

	@Override
	public int get_canbedmg() {
		return this._canbedmg;
	}

	public void set_canbedmg(final int i) {
		this._canbedmg = i;
	}

	@Override
	public boolean isTwohandedWeapon() {
		final int weapon_type = this.getType();

		final boolean bool = ((weapon_type == 3) || (weapon_type == 4)
				|| (weapon_type == 5) || (weapon_type == 11) || (weapon_type == 12)
				|| (weapon_type == 15) || (weapon_type == 16) || (weapon_type == 18));

		return bool;
	}
}
