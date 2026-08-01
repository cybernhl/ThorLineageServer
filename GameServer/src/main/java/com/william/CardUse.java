package com.william;

public class CardUse
{
	private final int _ItemId; // 升级的材料
	private String _Name;
	private int _Time;
	private int _ExpNotLose;
	private int _CampExpNotLose;
	private int _ItemNotLose;
	private int _AddHp;
	private int _AddMp;
	private int _AddStr;
	private int _AddDex;
	private int _AddInt;
	private int _AddCon;
	private int _AddWis;
	private int _AddCha;
	private int _AddHpr;
	private int _AddMpr;
	private int _AddEarth;
	private int _AddWater;
	private int _AddFire;
	private int _AddWind;
	private int _AddStun;
	private int _AddStone;
	private int _AddSleep;
	private int _AddFreeze;
	private int _AddSustain;
	private int _AddBlind;
	private int _AddMr;
	private int _AddSp;
	private int _AddHit;
	private int _AddBowHit;
	private int _AddDmg;
	private int _AddBowDmg;
	private int _AddReductionDmg;
	private int _AddMagiDmg;
	private int _AddReductionMagiDmg;
	private int _AddExpGet;
	private int _AddAc;

	
	public CardUse(int ItemId,String Name, int Time , int ExpNotLose, int CampExpNotLose,int ItemNotLose,
			int AddHp, 
			int AddMp,
			int AddStr,
			int AddDex,
			int AddInt,
			int AddCon,
			int AddWis,
			int AddCha,
			int AddHpr,
			int AddMpr,
			int AddEarth,
			int AddWater,
			int AddFire,
			int AddWind,
			int AddStun,
			int AddStone,
			int AddSleep,
			int AddFreeze,
			int AddSustain,
			int AddBlind,
			int AddMr,
			int AddSp,
			int AddHit,
			int AddBowHit,
			int AddDmg,
			int AddBowDmg,
			int AddReductionDmg,
			int AddMagiDmg,
			int AddReductionMagiDmg,
			int AddExpGet,
			int AddAc)
	{
		this._ItemId = ItemId;
		this._Name = Name;
		this._Time = Time;
		this._ExpNotLose = ExpNotLose;
		this._CampExpNotLose = CampExpNotLose;
		this._ItemNotLose = ItemNotLose;
		this._AddHp = AddHp;
		this._AddMp = AddMp;
		this._AddStr = AddStr;
		this._AddDex = AddDex;
		this._AddInt = AddInt;
		this._AddCon = AddCon;
		this._AddWis = AddWis;
		this._AddCha = AddCha;
		this._AddHpr = AddHpr;
		this._AddMpr = AddMpr;
		this._AddEarth = AddEarth;
		this._AddWater = AddWater;
		this._AddFire = AddFire;
		this._AddWind = AddWind;
		this._AddStun = AddStun;
		this._AddStone = AddStone;
		this._AddSleep = AddSleep;
		this._AddFreeze = AddFreeze;
		this._AddSustain = AddSustain;
		this._AddBlind = AddBlind;
		this._AddMr = AddMr;
		this._AddSp = AddSp;
		this._AddHit = AddHit;
		this._AddBowHit = AddBowHit;
		this._AddDmg = AddDmg;
		this._AddBowDmg = AddBowDmg;
		this._AddReductionDmg = AddReductionDmg;
		this._AddMagiDmg = AddMagiDmg;
		this._AddReductionMagiDmg = AddReductionMagiDmg;
		this._AddExpGet = AddExpGet;
		this._AddAc = AddAc;
	}
	
	/**
	 * @return item
	 */
	public final int getItemId()
	{
		return this._ItemId;
	}
	public String getName() {
		return this._Name;
	}
	public int getTime() {
		return this._Time;
	}
	
	public int getExpNotLose() {
		return this._ExpNotLose;
	}
	public int getCampExpNotLose() {
		return this._CampExpNotLose;
	}
	public int getItemNotLose() {
		return this._ItemNotLose;
	}
	public int getAddHp() {
		return this._AddHp;
	}
	public int getAddMp() {
		return this._AddMp;
	}
	public int getAddStr() {
		return this._AddStr;
	}
	public int getAddDex() {
		return this._AddDex;
	}
	public int getAddInt() {
		return this._AddInt;
	}
	public int getAddCon() {
		return this._AddCon;
	}
	public int getAddWis() {
		return this._AddWis;
	}
	public int getAddCha() {
		return this._AddCha;
	}
	public int getAddHpr() {
		return this._AddHpr;
	}
	public int getAddMpr() {
		return this._AddMpr;
	}
	public int getAddEarth() {
		return this._AddEarth;
	}

	public int getAddWater() {
		return this._AddWater;
	}

	public int getAddFire() {
		return this._AddFire;
	}

	public int getAddWind() {
		return this._AddWind;
	}
	public int getAddStun() {
		return this._AddStun;
	}
	public int getAddStone() {
		return this._AddStone;
	}
	public int getAddSleep() {
		return this._AddSleep;
	}
	public int getAddFreeze() {
		return this._AddFreeze;
	}
	public int getAddSustain() {
		return this._AddSustain;
	}
	public int getAddBlind() {
		return this._AddBlind;
	}

	public int getAddMr() {
		return this._AddMr;
	}
	public int getAddSp() {
		return this._AddSp;
	}
	public int getAddHit() {
		return this._AddHit;
	}
	public int getAddBowHit() {
		return this._AddBowHit;
	}

	public int getAddDmg() {
		return this._AddDmg;
	}
	public int getAddBowDmg() {
		return this._AddBowDmg;
	}
	public int getAddReductionDmg() {
		return this._AddReductionDmg;
	}

	public int getAddMagiDmg() {
		return this._AddMagiDmg;
	}
	public int getAddReductionMagiDmg() {
		return this._AddReductionMagiDmg;
	}
	public int getAddExpGet() {
		return this._AddExpGet;
	}
	public int getAddAc() { return this._AddAc; }

}
