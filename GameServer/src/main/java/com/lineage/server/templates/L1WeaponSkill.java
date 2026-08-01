package com.lineage.server.templates;

import java.util.ArrayList;
import java.util.Random;

import com.lineage.server.model.weaponskill.L1WSkillExecutor;
import com.lineage.server.model.weaponskill.L1WSprExecutor;

/**
 * 武器技能數據暫存
 * 製作中 daien 2012-05-12
 * @author daien
 *
 */
public class L1WeaponSkill {
	
	private final static Random _random = new Random();

	private int _weapon_id;// 編號
	
	private int _random_skill;// 發動機率(1/1000)
	
	private int _dmg_count;// 傷害次數(基礎設置1 大於1將會傷害多次(設定次數))
	
	private boolean _dmg_mr = false;// 傷害是否受到抗魔影響減低
	
	private boolean _dmg_ac = false;// 傷害是否受到防禦影響減低
	
	private boolean _dmg_int = false;// 傷害是否受使用者智力影響提高
	
	private boolean _dmg_str = false;// 傷害是否受使用者力量影響提高
	
	private boolean _dmg_dex = false;// 傷害是否受使用者敏捷影響提高

	private ArrayList<L1WSkillExecutor> _powerList = null;

	private ArrayList<L1WSprExecutor> _sprList = null;

	public ArrayList<L1WSkillExecutor> get_powerList() {
		return _powerList;
	}

	public void set_powerList(ArrayList<L1WSkillExecutor> powerList) {
		this._powerList = powerList;
	}

	public ArrayList<L1WSprExecutor> get_sprList() {
		return _sprList;
	}

	public void set_sprList(ArrayList<L1WSprExecutor> sprList) {
		this._sprList = sprList;
	}
	
}
