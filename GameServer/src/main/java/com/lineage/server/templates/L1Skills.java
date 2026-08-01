package com.lineage.server.templates;

public class L1Skills {

	public static final int ATTR_NONE = 0;

	public static final int ATTR_EARTH = 1;

	public static final int ATTR_FIRE = 2;

	public static final int ATTR_WATER = 4;

	public static final int ATTR_WIND = 8;

	public static final int ATTR_RAY = 16;

	public static final int TYPE_PROBABILITY = 1;

	public static final int TYPE_CHANGE = 2;

	public static final int TYPE_CURSE = 4;

	public static final int TYPE_DEATH = 8;

	public static final int TYPE_HEAL = 16;

	public static final int TYPE_RESTORE = 32;

	public static final int TYPE_ATTACK = 64;

	public static final int TYPE_OTHER = 128;

	/**技能對自己*/
	public static final int TARGET_TO_ME = 0;

	/**技能對人物*/
	public static final int TARGET_TO_PC = 1;

	/**技能對NPC*/
	public static final int TARGET_TO_NPC = 2;

	/**技能對血盟*/
	public static final int TARGET_TO_CLAN = 4;

	/**技能對隊伍*/
	public static final int TARGET_TO_PARTY = 8;

	/**技能對寵物*/
	public static final int TARGET_TO_PET = 16;

	/**技能對地點*/
	public static final int TARGET_TO_PLACE = 32;

	private int _skillId;

	public int getSkillId() {
		return this._skillId;
	}

	public void setSkillId(final int i) {
		this._skillId = i;
	}

	private String _name;

	public String getName() {
		return this._name;
	}

	public void setName(final String s) {
		this._name = s;
	}

	private int _skillLevel;

	/**
	 * 技能分級
	 * @return
	 */
	public int getSkillLevel() {
		return this._skillLevel;
	}

	/**
	 * 技能分級
	 * @param i
	 */
	public void setSkillLevel(final int i) {
		this._skillLevel = i;
	}

	private int _skillNumber;

	public int getSkillNumber() {
		return this._skillNumber;
	}

	public void setSkillNumber(final int i) {
		this._skillNumber = i;
	}

	private int _mpConsume;

	public int getMpConsume() {
		return this._mpConsume;
	}

	public void setMpConsume(final int i) {
		this._mpConsume = i;
	}

	private int _hpConsume;

	public int getHpConsume() {
		return this._hpConsume;
	}

	public void setHpConsume(final int i) {
		this._hpConsume = i;
	}

	private int _itmeConsumeId;

	public int getItemConsumeId() {
		return this._itmeConsumeId;
	}

	public void setItemConsumeId(final int i) {
		this._itmeConsumeId = i;
	}

	private int _itmeConsumeCount;

	public int getItemConsumeCount() {
		return this._itmeConsumeCount;
	}

	public void setItemConsumeCount(final int i) {
		this._itmeConsumeCount = i;
	}

	private int _reuseDelay; // 單位：秒

	public int getReuseDelay() {
		return this._reuseDelay;
	}

	public void setReuseDelay(final int i) {
		this._reuseDelay = i;
	}

	private int _buffDuration; // 效果時間(單位:秒)

	/**
	 * 效果時間(單位:秒)
	 * @return
	 */
	public int getBuffDuration() {
		return this._buffDuration;
	}

	/**
	 * 效果時間(單位:秒)
	 * @param i
	 */
	public void setBuffDuration(final int i) {
		this._buffDuration = i;
	}

	private String _target;

	public String getTarget() {
		return this._target;
	}

	public void setTarget(final String s) {
		this._target = s;
	}

	private int _targetTo; // 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置

	public int getTargetTo() {
		return this._targetTo;
	}

	/**
	 * 施展對像
	 * @param i 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置
	 */
	public void setTargetTo(final int i) {
		this._targetTo = i;
	}

	private int _damageValue;

	/**
	 * 魔法基礎傷害
	 * @return
	 */
	public int getDamageValue() {
		return this._damageValue;
	}

	/**
	 * 魔法基礎傷害
	 * @param i
	 */
	public void setDamageValue(final int i) {
		this._damageValue = i;
	}

	private int _damageDice;

	/**
	 * 魔法基礎傷害隨機附加值
	 * @return
	 */
	public int getDamageDice() {
		return this._damageDice;
	}

	/**
	 * 魔法基礎傷害隨機附加值
	 * @param i
	 */
	public void setDamageDice(final int i) {
		this._damageDice = i;
	}

	private int _damageDiceCount;

	/**
	 * 魔法基礎傷害隨機附加值 附加次數
	 * @return
	 */
	public int getDamageDiceCount() {
		return this._damageDiceCount;
	}

	/**
	 * 魔法基礎傷害隨機附加值 附加次數
	 * @param i
	 */
	public void setDamageDiceCount(final int i) {
		this._damageDiceCount = i;
	}

	private int _probabilityValue;

	public int getProbabilityValue() {
		return this._probabilityValue;
	}

	public void setProbabilityValue(final int i) {
		this._probabilityValue = i;
	}

	private int _probabilityDice;

	/**
	 * 技能計算機率
	 * @return
	 */
	public int getProbabilityDice() {
		return this._probabilityDice;
	}

	/**
	 * 技能計算機率
	 * @param i
	 */
	public void setProbabilityDice(final int i) {
		this._probabilityDice = i;
	}

	private int _attr;// 魔法屬性

	/**
	 * 魔法屬性<br>
	 * 0.無屬性魔法,1.地屬性魔法,2.火屬性魔法,4.水屬性魔法,8.風屬性魔法,16.光屬性魔法
	 */
	public int getAttr() {
		return this._attr;
	}
	
    /**
     * 设置属性.
     * 
     * @param attr
     *            - 要设置的属性(1:地、2:火、4:水、8:风)
     */
	public void setAttr(final int i) {
		this._attr = i;
	}

	private int _type; // 魔法種類

	/**
	 * 魔法種類<br>
	 * 1:破壞 2:輔助 4:詛咒 8:死亡 16:治療 32:復活 64:攻擊 128:其他特殊
	 */
	public int getType() {
		return this._type;
	}

	public void setType(final int i) {
		this._type = i;
	}

	private int _lawful;

	public int getLawful() {
		return this._lawful;
	}

	public void setLawful(final int i) {
		this._lawful = i;
	}

	private int _ranged;

	/**
	 * 施放距離
	 * @return
	 */
	public int getRanged() {
		return this._ranged;
	}

	/**
	 * 施放距離
	 * @param i
	 */
	public void setRanged(final int i) {
		this._ranged = i;
	}

	private int _area;

	/***
	 * 技能範圍
	 * @return
	 */
	public int getArea() {
		return this._area;
	}

	/**
	 * 技能範圍
	 * @param i
	 */
	public void setArea(final int i) {
		this._area = i;
	}

	boolean _isThrough;

	public boolean isThrough() {
		return this._isThrough;
	}

	public void setThrough(final boolean flag) {
		this._isThrough = flag;
	}

	private int _id;

	public int getId() {
		return this._id;
	}

	public void setId(final int i) {
		this._id = i;
	}

	private String _nameId;

	public String getNameId() {
		return this._nameId;
	}

	public void setNameId(final String s) {
		this._nameId = s;
	}

	private int _actionId;

	/**
	 * 技能動作代號
	 * @return
	 */
	public int getActionId() {
		return this._actionId;
	}

	/**
	 * 技能動作代號
	 * @param i
	 */
	public void setActionId(final int i) {
		this._actionId = i;
	}

	private int _castGfx;

	public int getCastGfx() {
		return this._castGfx;
	}

	public void setCastGfx(final int i) {
		this._castGfx = i;
	}

	private int _castGfx2;

	public int getCastGfx2() {
		return this._castGfx2;
	}

	public void setCastGfx2(final int i) {
		this._castGfx2 = i;
	}

	private int _sysmsgIdHappen;

	public int getSysmsgIdHappen() {
		return this._sysmsgIdHappen;
	}

	public void setSysmsgIdHappen(final int i) {
		this._sysmsgIdHappen = i;
	}

	private int _sysmsgIdStop;

	public int getSysmsgIdStop() {
		return this._sysmsgIdStop;
	}

	public void setSysmsgIdStop(final int i) {
		this._sysmsgIdStop = i;
	}

	private int _sysmsgIdFail;

	public int getSysmsgIdFail() {
		return this._sysmsgIdFail;
	}

	public void setSysmsgIdFail(final int i) {
		this._sysmsgIdFail = i;
	}

}
