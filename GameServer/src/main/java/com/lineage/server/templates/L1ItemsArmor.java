package com.lineage.server.templates;

public class L1ItemsArmor extends L1Item {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public L1ItemsArmor() {
	}

	private int _ac = 0; // ● ＡＣ

	@Override
	public int get_ac() {
		return this._ac;
	}

	public void set_ac(final int i) {
		this._ac = i;
	}

	private int _damageReduction = 0; // ● 輕減

	@Override
	public int getDamageReduction() {
		return this._damageReduction;
	}

	public void setDamageReduction(final int i) {
		this._damageReduction = i;
	}

	private int _weightReduction = 0; // ● 重量輕減

	@Override
	public int getWeightReduction() {
		return this._weightReduction;
	}

	public void setWeightReduction(final int i) {
		this._weightReduction = i;
	}

	private int _hitModifierByArmor = 0; // ● 命中率補正

	@Override
	public int getHitModifierByArmor() {
		return this._hitModifierByArmor;
	}

	public void setHitModifierByArmor(final int i) {
		this._hitModifierByArmor = i;
	}

	private int _dmgModifierByArmor = 0; // ● 補正

	@Override
	public int getDmgModifierByArmor() {
		return this._dmgModifierByArmor;
	}

	public void setDmgModifierByArmor(final int i) {
		this._dmgModifierByArmor = i;
	}

	private int _bowHitModifierByArmor = 0; // ● 弓命中率補正

	@Override
	public int getBowHitModifierByArmor() {
		return this._bowHitModifierByArmor;
	}

	public void setBowHitModifierByArmor(final int i) {
		this._bowHitModifierByArmor = i;
	}

	private int _bowDmgModifierByArmor = 0; // ● 弓補正

	@Override
	public int getBowDmgModifierByArmor() {
		return this._bowDmgModifierByArmor;
	}

	public void setBowDmgModifierByArmor(final int i) {
		this._bowDmgModifierByArmor = i;
	}

	private int _defense_water = 0; // 增加水屬性

	/**
	 * 增加水屬性
	 * @param i
	 */
	public void set_defense_water(final int i) {
		this._defense_water = i;
	}

	@Override
	public int get_defense_water() {
		return this._defense_water;
	}

	private int _defense_wind = 0; // 增加風屬性

	/**
	 * 增加風屬性
	 * @param i
	 */
	public void set_defense_wind(final int i) {
		this._defense_wind = i;
	}

	@Override
	public int get_defense_wind() {
		return this._defense_wind;
	}

	private int _defense_fire = 0; // 增加火屬性

	/**
	 * 增加火屬性
	 * @param i
	 */
	public void set_defense_fire(final int i) {
		this._defense_fire = i;
	}

	@Override
	public int get_defense_fire() {
		return this._defense_fire;
	}

	private int _defense_earth = 0; // 增加地屬性

	/**
	 * 增加地屬性
	 * @param i
	 */
	public void set_defense_earth(final int i) {
		this._defense_earth = i;
	}

	@Override
	public int get_defense_earth() {
		return this._defense_earth;
	}

	private int _regist_stun = 0; // 昏迷耐性

	/**
	 * 昏迷耐性
	 * @param i
	 */
	public void set_regist_stun(final int i) {
		this._regist_stun = i;
	}

	@Override
	public int get_regist_stun() {
		return this._regist_stun;
	}

	private int _regist_stone = 0; // 石化耐性

	/**
	 * 石化耐性
	 * @param i
	 */
	public void set_regist_stone(final int i) {
		this._regist_stone = i;
	}

	@Override
	public int get_regist_stone() {
		return this._regist_stone;
	}

	private int _regist_sleep = 0; // 睡眠耐性

	/**
	 * 睡眠耐性
	 * @param i
	 */
	public void set_regist_sleep(final int i) {
		this._regist_sleep = i;
	}

	@Override
	public int get_regist_sleep() {
		return this._regist_sleep;
	}

	private int _regist_freeze = 0; // 寒冰耐性

	/**
	 * 寒冰耐性
	 * @param i
	 */
	public void set_regist_freeze(final int i) {
		this._regist_freeze = i;
	}

	@Override
	public int get_regist_freeze() {
		return this._regist_freeze;
	}

	private int _regist_sustain = 0; // 支撐耐性

	/**
	 * 支撐耐性
	 * @param i
	 */
	public void set_regist_sustain(final int i) {
		this._regist_sustain = i;
	}

	@Override
	public int get_regist_sustain() {
		return this._regist_sustain;
	}

	private int _regist_blind = 0; // 暗黑耐性

	/**
	 * 暗黑耐性
	 * @param i
	 */
	public void set_regist_blind(final int i) {
		this._regist_blind = i;
	}

	@Override
	public int get_regist_blind() {
		return this._regist_blind;
	}

}