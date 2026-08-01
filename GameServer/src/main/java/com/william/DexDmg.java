package com.william;

import com.lineage.server.model.Instance.L1PcInstance;


public class DexDmg
{
	private int _DexDmg;
	private int _AddDmg;
	private int _AddHit;

	public DexDmg(int DexDmg,int AddDmg,int AddHit)
	{
		this._DexDmg = DexDmg;
		this._AddDmg = AddDmg;
		this._AddHit = AddHit;


	}

	public int getDexDmg() {
		return this._DexDmg;
	}
	public int getAddDmg() {
		return this._AddDmg;
	}
	public int getAddHit() {
		return this._AddHit;
	}
	//敏捷影響攻擊力
	public static int getDexDmgSkill(L1PcInstance pc, int dex) {
		
		int dmg = 0;
		
		DexDmg DexDmgSkill = PcDexDmg.getInstance().getTemplate(dex);


		if (dex >= DexDmgSkill.getDexDmg()){
			dmg = DexDmgSkill.getAddDmg();
		}
		return dmg;
	}
	//敏捷影響命中
	public static int getDexHitSkill(L1PcInstance pc, int dex) {
		
		int hit = 0;
		
		DexDmg DexDmgSkill = PcDexDmg.getInstance().getTemplate(dex);


		if (dex >= DexDmgSkill.getDexDmg()){
			hit = DexDmgSkill.getAddHit();
		}
		return hit;
	}
}

