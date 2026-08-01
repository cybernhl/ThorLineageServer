package com.william;

import com.lineage.server.model.Instance.L1PcInstance;


public class StrDmg
{
	private int _StrDmg;
	private int _AddDmg;
	private int _AddHit;

	public StrDmg(int StrDmg,int AddDmg,int AddHit)
	{
		this._StrDmg = StrDmg;
		this._AddDmg = AddDmg;
		this._AddHit = AddHit;


	}

	public int getStrDmg() {
		return this._StrDmg;
	}
	public int getAddDmg() {
		return this._AddDmg;
	}
	public int getAddHit() {
		return this._AddHit;
	}
	//力量影響攻擊力
	public static int getStrDmgSkill(L1PcInstance pc, int str) {
		
		int dmg = 0;
		
		StrDmg StrDmgSkill = PcStrDmg.getInstance().getTemplate(str);


		if (str >= StrDmgSkill.getStrDmg()){
			dmg = StrDmgSkill.getAddDmg();
		}
		return dmg;
	}
	//力量影響命中
	public static int getStrHitSkill(L1PcInstance pc, int str) {
		
		int hit = 0;
		
		StrDmg StrDmgSkill = PcStrDmg.getInstance().getTemplate(str);


		if (str >= StrDmgSkill.getStrDmg()){
			hit = StrDmgSkill.getAddHit();
		}
		return hit;
	}
}

