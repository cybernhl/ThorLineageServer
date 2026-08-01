package com.lineage.server.templates;

public class L1MobSkill implements Cloneable {

	public static final int TYPE_NONE = 0;

	public static final int TYPE_PHYSICAL_ATTACK = 1;// 物理攻擊

	public static final int TYPE_MAGIC_ATTACK = 2;// 魔法攻擊

	public static final int TYPE_SUMMON = 3;// 召喚屬下

	public static final int TYPE_POLY = 4;// 強制變身

	public static final int AHTHARAS_1 = 5;// 群體沖暈

	public static final int AHTHARAS_2 = 6;// 群體相消

	public static final int AHTHARAS_3 = 7;// 群體壞物

	public static final int CHANGE_TARGET_NO = 0;

	public static final int CHANGE_TARGET_COMPANION = 1;

	public static final int CHANGE_TARGET_ME = 2;

	public static final int CHANGE_TARGET_RANDOM = 3;

	private final int skillSize;// 技能數量

	@Override
	public L1MobSkill clone() {
		try {
			return (L1MobSkill) (super.clone());
		} catch (final CloneNotSupportedException e) {
			throw (new InternalError(e.getMessage()));
		}
	}

	/**
	 * 技能數量
	 * @return
	 */
	public int getSkillSize() {
		return this.skillSize;
	}

	/**
	 * 技能數量
	 * @param sSize
	 */
	public L1MobSkill(final int sSize) {
		this.skillSize = sSize;

		this.type = new int[this.skillSize];
		this.triRnd = new int[this.skillSize];
		this.triHp = new int[this.skillSize];
		this.triCompanionHp = new int[this.skillSize];
		this.triRange = new int[this.skillSize];
		this.triCount = new int[this.skillSize];
		this.changeTarget = new int[this.skillSize];
		this.range = new int[this.skillSize];
		this.areaWidth = new int[this.skillSize];
		this.areaHeight = new int[this.skillSize];
		this.leverage = new int[this.skillSize];
		this.skillId = new int[this.skillSize];
		this.gfxid = new int[this.skillSize];
		this.actid = new int[this.skillSize];
		this.summon = new int[this.skillSize];
		this.summonMin = new int[this.skillSize];
		this.summonMax = new int[this.skillSize];
		this.polyId = new int[this.skillSize];
	}

	private int mobid;

	/**
	 * NPC編號
	 * @return
	 */
	public int get_mobid() {
		return this.mobid;
	}

	/**
	 * NPC編號
	 * @param i
	 */
	public void set_mobid(final int i) {
		this.mobid = i;
	}

	private String mobName;

	/**
	 * NPC名稱
	 * @return
	 */
	public String getMobName() {
		return this.mobName;
	}

	/**
	 * NPC名稱
	 * @param s
	 */
	public void setMobName(final String s) {
		this.mobName = s;
	}

	private int type[];// 技能類型

	/**
	 * 技能類型<BR>
	 * 1 物理攻擊<BR>
	 * 2 魔法攻擊<BR>
	 * 3 召喚屬下<BR>
	 * 4 強制變身<BR>
	 * 5 群體沖暈<BR>
	 * 6 群體相消<BR>
	 * 7 群體壞物<BR>
	 * @param idx
	 * @return
	 */
	public int getType(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.type[idx];
	}

	/**
	 * 技能類型<BR>
	 * 1 物理攻擊<BR>
	 * 2 魔法攻擊<BR>
	 * 3 召喚屬下<BR>
	 * 4 強制變身<BR>
	 * 5 群體沖暈<BR>
	 * 6 群體相消<BR>
	 * 7 群體壞物<BR>
	 * @param idx
	 * @param i
	 */
	public void setType(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.type[idx] = i;
	}

	private int triRnd[];

	/**
	 * 發動機率(%)
	 * @param idx
	 * @return
	 */
	public int getTriggerRandom(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.triRnd[idx];
	}

	/**
	 * 發動機率(%)
	 * @param idx
	 * @param i
	 */
	public void setTriggerRandom(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.triRnd[idx] = i;
	}

	int triHp[];

	/**
	 * HP條件發動(低於設定值)
	 * @param idx
	 * @return
	 */
	public int getTriggerHp(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.triHp[idx];
	}

	/**
	 * HP條件發動(HP低於設定值)
	 * @param idx
	 * @param i
	 */
	public void setTriggerHp(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.triHp[idx] = i;
	}

	int triCompanionHp[];

	/**
	 * 同族HP條件發動(同族HP低於設定值)
	 * @param idx
	 * @return
	 */
	public int getTriggerCompanionHp(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.triCompanionHp[idx];
	}

	/**
	 * 同族HP條件發動(同族HP低於設定值)
	 * @param idx
	 * @param i
	 */
	public void setTriggerCompanionHp(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.triCompanionHp[idx] = i;
	}

	int triRange[];

	/**
	 * 設定值小於0 則小於設定距離(轉正整數)發動技能<BR>
	 * 設定值大於0 則超出設定距離發動技能
	 * @param idx
	 * @return
	 */
	public int getTriggerRange(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		Math.abs(idx);
		return this.triRange[idx];
	}

	/**
	 * 設定值小於0 則小於設定距離(轉正整數)發動技能<BR>
	 * 設定值大於0 則超出設定距離發動技能
	 * @param idx
	 * @param i
	 */
	public void setTriggerRange(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.triRange[idx] = i;
	}

	/**
	 * 物件距離是否達成施展技能距離
	 * @param idx
	 * @param distance
	 * @return
	 */
	public boolean isTriggerDistance(final int idx, final int distance) {
		final int triggerRange = this.getTriggerRange(idx);

		if (((triggerRange < 0) && (distance <= Math.abs(triggerRange)))
				|| ((triggerRange > 0) && (distance >= triggerRange))) {
			return true;
		}
		return false;
	}

	int triCount[];

	/**
	 * 技能發動次數
	 * @param idx
	 * @return
	 */
	public int getTriggerCount(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.triCount[idx];
	}

	/**
	 * 技能發動次數
	 * @param idx
	 * @param i
	 */
	public void setTriggerCount(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.triCount[idx] = i;
	}

	int changeTarget[];

	/**
	 * 技能發動時目標判定<BR>
	 * 1:目前攻擊者<BR>
	 * 2:目前攻擊自己的對象<BR>
	 * 3:範圍目標<BR>
	 * @param idx
	 * @return
	 */
	public int getChangeTarget(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.changeTarget[idx];
	}

	/**
	 * 技能發動時目標判定<BR>
	 * 1:目前攻擊者<BR>
	 * 2:目前攻擊自己的對象<BR>
	 * 3:範圍目標<BR>
	 * @param idx
	 * @param i
	 */
	public void setChangeTarget(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.changeTarget[idx] = i;
	}

	int range[];

	/**
	 * 攻擊距離(物理攻擊設置)<BR>
	 * 物理攻擊必須設定1以上
	 * @param idx
	 * @return
	 */
	public int getRange(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.range[idx];
	}

	/**
	 * 攻擊距離(物理攻擊設置)<BR>
	 * 物理攻擊必須設定1以上
	 * @param idx
	 * @param i
	 */
	public void setRange(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.range[idx] = i;
	}

	/*
	 * 範圍攻擊橫幅、單體攻擊0設定、範圍攻擊0以上設定
	 * WidthHeight設定攻擊者橫幅Width、奧行Height。
	 * Width+-、1指定、中心左右1對像。
	 */
	int areaWidth[];

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * @param idx
	 * @return
	 */
	public int getAreaWidth(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.areaWidth[idx];
	}

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * @param idx
	 * @param i
	 */
	public void setAreaWidth(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.areaWidth[idx] = i;
	}

	/*
	 * 範圍攻擊高、單體攻擊0設定、範圍攻擊1以上設定
	 */
	int areaHeight[];

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * @param idx
	 * @return
	 */
	public int getAreaHeight(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.areaHeight[idx];
	}

	/**
	 * 攻擊範圍(物理攻擊設置)<BR>
	 * 單體攻擊設置0<BR>
	 * 範圍攻擊必須設定1以上
	 * @param idx
	 * @param i
	 */
	public void setAreaHeight(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.areaHeight[idx] = i;
	}

	int leverage[];

	/**
	 * 攻擊倍率(1/10)
	 * @param idx
	 * @return
	 */
	public int getLeverage(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.leverage[idx];
	}

	/**
	 * 攻擊倍率(1/10)
	 * @param idx
	 * @param i
	 */
	public void setLeverage(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.leverage[idx] = i;
	}

	int skillId[];

	/**
	 * 對應魔法技能編號
	 * @param idx
	 * @return
	 */
	public int getSkillId(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.skillId[idx];
	}

	/**
	 * 對應魔法技能編號
	 * @param idx
	 * @param i
	 */
	public void setSkillId(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.skillId[idx] = i;
	}

	int gfxid[];

	/**
	 * 物理攻擊使用的技能動畫
	 * @param idx
	 * @return
	 */
	public int getGfxid(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.gfxid[idx];
	}

	/**
	 * 物理攻擊使用的技能動畫
	 * @param idx
	 * @param i
	 */
	public void setGfxid(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.gfxid[idx] = i;
	}

	int actid[];

	/**
	 * 物理攻擊使用的動作編號
	 * @param idx
	 * @return
	 */
	public int getActid(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.actid[idx];
	}

	/**
	 * 物理攻擊使用的動作編號
	 * @param idx
	 * @param i
	 */
	public void setActid(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.actid[idx] = i;
	}

	int summon[];

	/**
	 * 召喚技能使用屬下編號
	 * @param idx
	 * @return
	 */
	public int getSummon(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.summon[idx];
	}

	/**
	 * 召喚技能使用屬下編號
	 * @param idx
	 * @param i
	 */
	public void setSummon(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.summon[idx] = i;
	}

	int summonMin[];

	/**
	 * 召喚最小數量
	 * @param idx
	 * @return
	 */
	public int getSummonMin(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.summonMin[idx];
	}

	/**
	 * 召喚最小數量
	 * @param idx
	 * @param i
	 */
	public void setSummonMin(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.summonMin[idx] = i;
	}

	int summonMax[];

	/**
	 * 召喚最大數量
	 * @param idx
	 * @return
	 */
	public int getSummonMax(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.summonMax[idx];
	}

	/**
	 * 召喚最大數量
	 * @param idx
	 * @param i
	 */
	public void setSummonMax(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.summonMax[idx] = i;
	}

	int polyId[];

	/**
	 * 強制變身代號
	 * @param idx
	 * @return
	 */
	public int getPolyId(final int idx) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return 0;
		}
		return this.polyId[idx];
	}

	/**
	 * 強制變身代號
	 * @param idx
	 * @param i
	 */
	public void setPolyId(final int idx, final int i) {
		if ((idx < 0) || (idx >= this.getSkillSize())) {
			return;
		}
		this.polyId[idx] = i;
	}
}
