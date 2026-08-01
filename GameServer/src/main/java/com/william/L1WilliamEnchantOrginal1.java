package com.william;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.BinaryOutputStream;

/**
 * 裝備強化能力系統
 */
public class L1WilliamEnchantOrginal1 {
	private final int _id;
	private final int _itemid;
	private final int _level;
	private final int _type;
	private int _addAc;
	private byte _addStr;
	private byte _addDex;
	private byte _addCon;
	private byte _addInt;
	private byte _addWis;
	private byte _addCha;
	private int _addMaxHp;
	private int _addMaxMp;
	private int _addHpr;
	private int _addMpr;
	private int _addDmg;
	private int _addBowDmg;
	private int _addHit;
	private int _addBowHit;
	private int _reduction_dmg;
	private int _addMr;
	private int _addSp;

	public L1WilliamEnchantOrginal1(final int id, final int itemid,
			final int level, final int type, final int addAc,
			final byte addStr, final byte addDex, final byte addCon,
			final byte addInt, final byte addWis, final byte addCha,
			final int addMaxHp, final int addMaxMp, final int addHpr,
			final int addMpr, final int addDmg, final int addBowDmg,
			final int addHit, final int addBowHit, final int reduction_dmg,
			final int addMr, final int addSp) {

		_id = id;
		_itemid = itemid;
		_level = level;
		_type = type;
		_addAc = addAc;
		_addStr = addStr;
		_addDex = addDex;
		_addCon = addCon;
		_addInt = addInt;
		_addWis = addWis;
		_addCha = addCha;
		_addMaxHp = addMaxHp;
		_addMaxMp = addMaxMp;
		_addHpr = addHpr;
		_addMpr = addMpr;
		_addDmg = addDmg;
		_addBowDmg = addBowDmg;
		_addHit = addHit;
		_addBowHit = addBowHit;
		_reduction_dmg = reduction_dmg;
		_addMr = addMr;
		_addSp = addSp;
	}

	public int getId() {
		return _id;
	}

	public int getItemId() {
		return _itemid;
	}

	public int getLevel() {
		return _level;
	}

	public int gettype() {
		return _type;
	}

	/** 額外防禦 */
	public int getAddAc() {
		return _addAc;
	}

	public byte getAddStr() {
		return _addStr;
	}

	public byte getAddDex() {
		return _addDex;
	}

	public byte getAddCon() {
		return _addCon;
	}

	public byte getAddInt() {
		return _addInt;
	}

	public byte getAddWis() {
		return _addWis;
	}

	public byte getAddCha() {
		return _addCha;
	}

	public int getAddMaxHp() {
		return _addMaxHp;
	}

	public int getAddMaxMp() {
		return _addMaxMp;
	}

	public int getAddHpr() {
		return _addHpr;
	}

	public int getAddMpr() {
		return _addMpr;
	}

	public int getAddDmg() {
		return _addDmg;
	}

	public int getAddBowDmg() {
		return _addBowDmg;
	}

	public int getAddHit() {
		return _addHit;
	}

	public int getAddBowHit() {
		return _addBowHit;
	}

	/** 所有傷害減免 */
	public int getReduction_dmg() {
		return _reduction_dmg;
	}

	public int getAddMr() {
		return _addMr;
	}

	public int getAddSp() {
		return _addSp;
	}
	public static L1WilliamEnchantOrginal1 getL1WilliamEnchantOrginal(final L1ItemInstance item) {
		L1WilliamEnchantOrginal1 armorOrginal = null;
		L1WilliamEnchantOrginal1 armorOrginalOk = null;
		final L1WilliamEnchantOrginal1[] armorOrginalSize = EnchantOrginal1
				.getInstance().getArmorList();
		for (int i = 0; i < armorOrginalSize.length; i++) {
			int type = -1;
			if (item.getItem().getType2() == 2) {
				type = item.getItem().getUseType();
			}
			if (item.getItem().getType2() == 1) {
				type = item.getItem().getType1();
			}
			armorOrginalOk = item.getBless() == 1 ? EnchantOrginal1.getInstance().getTemplate(i) : EnchantOrginal1Bless.getInstance().getTemplate(i);
			if (item.getItem().get_safeenchant() >= 0) {
				if (armorOrginalOk.gettype() == type
						&& item.getEnchantLevel() == item.getItem()
						.get_safeenchant() + armorOrginalOk.getLevel()) { // 道具加成等級相同
					armorOrginal = armorOrginalOk;
					break;
				}
			}
		}
		if (armorOrginal == null) {
			return null;
		}
		return armorOrginal;
	}
	public static void itemDesc(final L1ItemInstance item, final BinaryOutputStream _os) {
		L1WilliamEnchantOrginal1 armorOrginal = getL1WilliamEnchantOrginal(item);
		if (armorOrginal == null) {
			return;
		}
		_os.writeC(0x27);
		_os.writeS("一過安定加成一");
		if (armorOrginal.getAddAc() != 0) { // 額外防禦
			_os.writeC(0x27);
			_os.writeS("防禦:" + armorOrginal.getAddAc() + ".");
		}
		if (armorOrginal.getAddStr() != 0) {
			_os.writeC(0x27);
			_os.writeS("力量+" + armorOrginal.getAddStr() + ".");
		}
		if (armorOrginal.getAddDex() != 0) {
			_os.writeC(0x27);
			_os.writeS("敏捷+" + armorOrginal.getAddDex() + ".");
		}
		if (armorOrginal.getAddCon() != 0) {
			_os.writeC(0x27);
			_os.writeS("體質+" + armorOrginal.getAddCon() + ".");
		}
		if (armorOrginal.getAddInt() != 0) {
			_os.writeC(0x27);
			_os.writeS("智力+" + armorOrginal.getAddInt() + ".");
		}
		if (armorOrginal.getAddWis() != 0) {
			_os.writeC(0x27);
			_os.writeS("精神+" + armorOrginal.getAddWis() + ".");
		}
		if (armorOrginal.getAddCha() != 0) {
			_os.writeC(0x27);
			_os.writeS("魅力+" + armorOrginal.getAddCha() + ".");
		}
		if (armorOrginal.getAddMaxHp() != 0) {
			_os.writeC(0x27);
			_os.writeS("血量+" + armorOrginal.getAddMaxHp() + ".");
		}
		if (armorOrginal.getAddMaxMp() != 0) {
			_os.writeC(0x27);
			_os.writeS("魔力+" + armorOrginal.getAddMaxMp() + ".");
		}
		if (armorOrginal.getAddHpr() != 0) {
			_os.writeC(0x27);
			_os.writeS("回血+" + armorOrginal.getAddHpr() + ".");
		}
		if (armorOrginal.getAddMpr() != 0) {
			_os.writeC(0x27);
			_os.writeS("回魔+" + armorOrginal.getAddMpr() + ".");
		}
		if (armorOrginal.getAddDmg() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				_os.writeC(0x27);
				_os.writeS("額外攻擊+" + armorOrginal.getAddDmg() + ".");
			}
		}
		if (armorOrginal.getAddHit() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				_os.writeC(0x27);
				_os.writeS("額外命中+" + armorOrginal.getAddHit() + ".");
			}
		}
		if (armorOrginal.getAddBowDmg() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				_os.writeC(0x27);
				_os.writeS("額外攻擊+" + armorOrginal.getAddBowDmg() + ".");
			}
		}
		if (armorOrginal.getAddBowHit() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				_os.writeC(0x27);
				_os.writeS("額外命中+" + armorOrginal.getAddBowHit() + ".");
			}
		}
		if (armorOrginal.getReduction_dmg() != 0) { // 所有傷害減免
			_os.writeC(0x27);
			_os.writeS("傷害減免+" + armorOrginal.getReduction_dmg() + ".");
		}
		if (armorOrginal.getAddMr() != 0) {
			_os.writeC(0x27);
			_os.writeS("抗魔+" + armorOrginal.getAddMr() + ".");
		}
		if (armorOrginal.getAddSp() != 0) {
			_os.writeC(0x27);
			_os.writeS("魔攻+" + armorOrginal.getAddSp() + ".");
		}
	}

	/** 增加效果 */
	public static void getAddArmorOrginal(final L1PcInstance pc,
			final L1ItemInstance item) {
		L1WilliamEnchantOrginal1 armorOrginal = getL1WilliamEnchantOrginal(item);
		if (armorOrginal == null) {
			return;
		}
		boolean spmr = false;
		if (armorOrginal.getAddAc() != 0) { // 額外防禦
			pc.addAc(-armorOrginal.getAddAc());
		}
		if (armorOrginal.getAddStr() != 0) {
			pc.addStr(armorOrginal.getAddStr());
		}
		if (armorOrginal.getAddDex() != 0) {
			pc.addDex(armorOrginal.getAddDex());
		}
		if (armorOrginal.getAddCon() != 0) {
			pc.addCon(armorOrginal.getAddCon());
		}
		if (armorOrginal.getAddInt() != 0) {
			pc.addInt(armorOrginal.getAddInt());
		}
		if (armorOrginal.getAddWis() != 0) {
			pc.addWis(armorOrginal.getAddWis());
		}
		if (armorOrginal.getAddCha() != 0) {
			pc.addCha(armorOrginal.getAddCha());
		}
		if (armorOrginal.getAddMaxHp() != 0) {
			pc.addMaxHp(armorOrginal.getAddMaxHp());
		}
		if (armorOrginal.getAddMaxMp() != 0) {
			pc.addMaxMp(armorOrginal.getAddMaxMp());
		}
		if (armorOrginal.getAddHpr() != 0) {
			pc.addHpr(armorOrginal.getAddHpr());
		}
		if (armorOrginal.getAddMpr() != 0) {
			pc.addMpr(armorOrginal.getAddMpr());
		}
		if (armorOrginal.getAddDmg() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				pc.addDmgup(armorOrginal.getAddDmg());
			}
		}
		if (armorOrginal.getAddHit() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				pc.addHitup(armorOrginal.getAddHit());
			}
		}
		if (armorOrginal.getAddBowDmg() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				pc.addBowDmgup(armorOrginal.getAddBowDmg());
			}
		}
		if (armorOrginal.getAddBowHit() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				pc.addBowHitup(armorOrginal.getAddBowHit());
			}
		}
		if (armorOrginal.getReduction_dmg() != 0) { // 所有傷害減免
			pc.addDamageReductionByArmor(armorOrginal.getReduction_dmg());
		}
		if (armorOrginal.getAddMr() != 0) {
			pc.addMr(armorOrginal.getAddMr());
			spmr = true;
		}
		if (armorOrginal.getAddSp() != 0) {			
				pc.addSp(armorOrginal.getAddSp());
				spmr = true;
			}		
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	/** 解除效果 */
	public static void getReductionArmorOrginal(final L1PcInstance pc,
			final L1ItemInstance item) {
		L1WilliamEnchantOrginal1 armorOrginal = getL1WilliamEnchantOrginal(item);
		if (armorOrginal == null) {
			return;
		}
		boolean spmr = false;
		if (armorOrginal.getAddAc() != 0) { // 額外防禦
			pc.addAc(armorOrginal.getAddAc());
		}
		if (armorOrginal.getAddStr() != 0) {
			pc.addStr(-armorOrginal.getAddStr());
		}
		if (armorOrginal.getAddDex() != 0) {
			pc.addDex(-armorOrginal.getAddDex());
		}
		if (armorOrginal.getAddCon() != 0) {
			pc.addCon(-armorOrginal.getAddCon());
		}
		if (armorOrginal.getAddInt() != 0) {
			pc.addInt(-armorOrginal.getAddInt());
		}
		if (armorOrginal.getAddWis() != 0) {
			pc.addWis(-armorOrginal.getAddWis());
		}
		if (armorOrginal.getAddCha() != 0) {
			pc.addCha(-armorOrginal.getAddCha());
		}
		if (armorOrginal.getAddMaxHp() != 0) {
			pc.addMaxHp(-armorOrginal.getAddMaxHp());
		}
		if (armorOrginal.getAddMaxMp() != 0) {
			pc.addMaxMp(-armorOrginal.getAddMaxMp());
		}
		if (armorOrginal.getAddHpr() != 0) {
			pc.addHpr(-armorOrginal.getAddHpr());
		}
		if (armorOrginal.getAddMpr() != 0) {
			pc.addMpr(-armorOrginal.getAddMpr());
		}
		if (armorOrginal.getAddDmg() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				pc.addDmgup(-armorOrginal.getAddDmg());
			}
		}
		if (armorOrginal.getAddHit() != 0) {
			if (item.getItem().getType1() != 20
					&& item.getItem().getType1() != 62) {
				pc.addHitup(-armorOrginal.getAddHit());
			}
		}
		if (armorOrginal.getAddBowDmg() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				pc.addBowDmgup(-armorOrginal.getAddBowDmg());
			}
		}
		if (armorOrginal.getAddBowHit() != 0) {
			if (item.getItem().getType1() == 20
					|| item.getItem().getType1() == 62) {
				pc.addBowHitup(-armorOrginal.getAddBowHit());
			}
		}
		if (armorOrginal.getReduction_dmg() != 0) { // 所有傷害減免
			pc.addDamageReductionByArmor(-armorOrginal.getReduction_dmg());
		}
		if (armorOrginal.getAddMr() != 0) {
			pc.addMr(-armorOrginal.getAddMr());
			spmr = true;
		}
		if (armorOrginal.getAddSp() != 0) {
				pc.addSp(-armorOrginal.getAddSp());
				spmr = true;
			}		
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}
	/** 增加效果 */
	/*
	 * public static void getAddArmorOrginal1(final L1PcInstance pc, final
	 * L1ItemInstance item) { L1WilliamEnchantOrginal armorOrginal1 = null;
	 * L1WilliamEnchantOrginal armorOrginalOk1 = null; final
	 * L1WilliamEnchantOrginal[] armorOrginalSize1 =
	 * EnchantOrginal.getInstance().getArmorList(); for (int i = 0; i <
	 * armorOrginalSize1.length; i++) { armorOrginalOk1 =
	 * EnchantOrginal.getInstance().getTemplate(i); if
	 * (item.getItem().get_safeenchant() > 0) { if (armorOrginalOk1.getItemId()
	 * == item.getItemId() && item.getEnchantLevel() == item.getItem()
	 * .get_safeenchant() + armorOrginalOk1.getLevel()) { // 道具加成等級相同
	 * armorOrginal1 = armorOrginalOk1; break; } } } if (armorOrginal1 == null)
	 * { return; } String add1 = ""; String add2 = ""; String add3 = ""; String
	 * add4 = ""; String add5 = ""; String add6 = ""; String add7 = ""; String
	 * add8 = ""; String add9 = ""; String add10 = ""; String add11 = ""; String
	 * add12 = ""; String add13 = ""; String add14 = ""; String add15 = "";
	 * String add16 = ""; String add17 = ""; String add18 = ""; boolean spmr =
	 * false; if (armorOrginalOk1.getAddAc() != 0) { // 額外防禦
	 * pc.addAc(-armorOrginalOk1.getAddAc()); add1 =
	 * "\\fW額外防禦增加："+armorOrginalOk1.getAddAc(); } if
	 * (armorOrginalOk1.getAddStr() != 0) {
	 * pc.addStr(armorOrginalOk1.getAddStr()); add2 =
	 * "\\fW額外力量增加："+armorOrginalOk1.getAddStr(); } if
	 * (armorOrginalOk1.getAddDex() != 0) {
	 * pc.addDex(armorOrginalOk1.getAddDex()); add3 =
	 * "\\fW額外敏捷增加："+armorOrginalOk1.getAddDex(); } if
	 * (armorOrginalOk1.getAddCon() != 0) {
	 * pc.addCon(armorOrginalOk1.getAddCon()); add4 =
	 * "\\fW額外體質增加："+armorOrginalOk1.getAddCon(); } if
	 * (armorOrginalOk1.getAddInt() != 0) {
	 * pc.addInt(armorOrginalOk1.getAddInt()); add5 =
	 * "\\fW額外智力增加："+armorOrginalOk1.getAddInt(); } if
	 * (armorOrginalOk1.getAddWis() != 0) {
	 * pc.addWis(armorOrginalOk1.getAddWis()); add6 =
	 * "\\fW額外精神增加："+armorOrginalOk1.getAddWis(); } if
	 * (armorOrginalOk1.getAddCha() != 0) {
	 * pc.addCha(armorOrginalOk1.getAddCha()); add7 = "\\fW額外魅力增加："+
	 * armorOrginalOk1.getAddCha(); } if (armorOrginalOk1.getAddMaxHp() != 0) {
	 * pc.addMaxHp(armorOrginalOk1.getAddMaxHp()); add8 = "\\fW額外血量增加："+
	 * armorOrginalOk1.getAddMaxHp(); } if (armorOrginalOk1.getAddMaxMp() != 0)
	 * { pc.addMaxMp(armorOrginalOk1.getAddMaxMp()); add9 = "\\fW額外魔力增加："+
	 * armorOrginalOk1.getAddMaxMp(); } if (armorOrginalOk1.getAddHpr() != 0) {
	 * pc.addHpr(armorOrginalOk1.getAddHpr()); add10 = "\\fW額外回血增加："+
	 * armorOrginalOk1.getAddHpr(); } if (armorOrginalOk1.getAddMpr() != 0) {
	 * pc.addMpr(armorOrginalOk1.getAddMpr()); add11 = "\\fW額外回魔增加："+
	 * armorOrginalOk1.getAddMpr(); } if (armorOrginalOk1.getAddDmg() != 0) {
	 * if(item.getItem().getType1()!=20&&item.getItem().getType1()!=62){
	 * pc.addDmgup(armorOrginalOk1.getAddDmg()); add12 = "\\fW額外攻擊增加"+
	 * armorOrginalOk1.getAddDmg(); } } if (armorOrginalOk1.getAddHit() != 0) {
	 * if(item.getItem().getType1()!=20&&item.getItem().getType1()!=62){
	 * pc.addHitup(armorOrginalOk1.getAddHit()); add13 = "\\fW額外命中增加"+
	 * armorOrginalOk1.getAddHit(); } } if (armorOrginalOk1.getAddBowDmg() != 0)
	 * { if(item.getItem().getType1()==20||item.getItem().getType1()==62){
	 * pc.addBowDmgup(armorOrginalOk1.getAddBowDmg()); add14 = "\\fW額外攻擊增加"+
	 * armorOrginalOk1.getAddBowDmg(); } } if (armorOrginalOk1.getAddBowHit() !=
	 * 0) { if(item.getItem().getType1()==20||item.getItem().getType1()==62){
	 * pc.addBowHitup(armorOrginalOk1.getAddBowHit()); add15 = "\\fW額外命中增加"+
	 * armorOrginalOk1.getAddBowHit(); } } if
	 * (armorOrginalOk1.getReduction_dmg() != 0) { // 所有傷害減免
	 * pc.addDamageReductionByArmor(armorOrginalOk1.getReduction_dmg()); add16 =
	 * "\\fW額外減免增加"+ armorOrginalOk1.getReduction_dmg(); } if
	 * (armorOrginalOk1.getAddMr() != 0) { pc.addMr(armorOrginalOk1.getAddMr());
	 * spmr = true; add17 = "\\fW額外魔防增加"+ armorOrginalOk1.getAddMr(); } if
	 * (armorOrginalOk1.getAddSp() != 0) {
	 * if(item.getItem().getType1()==40||item.getItem().getType1()==4){
	 * pc.addSp(armorOrginalOk1.getAddSp()); spmr = true; add18 = "\\fW額外魔攻增加"+
	 * armorOrginalOk1.getAddSp(); } } if (spmr) { pc.sendPackets(new
	 * S_SPMR(pc)); } pc.sendPackets(new S_OwnCharStatus(pc));
	 * pc.sendPackets(new S_SystemMessage(add1)); pc.sendPackets(new
	 * S_SystemMessage(add2)); pc.sendPackets(new S_SystemMessage(add3));
	 * pc.sendPackets(new S_SystemMessage(add4)); pc.sendPackets(new
	 * S_SystemMessage(add5)); pc.sendPackets(new S_SystemMessage(add6));
	 * pc.sendPackets(new S_SystemMessage(add7)); pc.sendPackets(new
	 * S_SystemMessage(add8)); pc.sendPackets(new S_SystemMessage(add9));
	 * pc.sendPackets(new S_SystemMessage(add10)); pc.sendPackets(new
	 * S_SystemMessage(add11)); pc.sendPackets(new S_SystemMessage(add12));
	 * pc.sendPackets(new S_SystemMessage(add13)); pc.sendPackets(new
	 * S_SystemMessage(add14)); pc.sendPackets(new S_SystemMessage(add15));
	 * pc.sendPackets(new S_SystemMessage(add16)); pc.sendPackets(new
	 * S_SystemMessage(add17)); pc.sendPackets(new S_SystemMessage(add18)); }
	 * 
	 * /** 解除效果
	 */
	/*
	 * public static void getReductionArmorOrginal1(final L1PcInstance pc, final
	 * L1ItemInstance item) { L1WilliamEnchantOrginal armorOrginal1 = null;
	 * L1WilliamEnchantOrginal armorOrginalOk1 = null; final
	 * L1WilliamEnchantOrginal[] armorOrginalSize1 = EnchantOrginal
	 * .getInstance().getArmorList(); for (int i = 0; i <
	 * armorOrginalSize1.length; i++) { armorOrginalOk1 =
	 * EnchantOrginal.getInstance().getTemplate(i); /* if
	 * (armorOrginalOk.getItemId() == item.getItemId() // 道具編號相同 &&
	 * item.getEnchantLevel() >= armorOrginalOk.getLevel()) { // 道具加成等級相同
	 * armorOrginal = armorOrginalOk; //break; }
	 */
	/*
	 * if (item.getItem().get_safeenchant() > 0) { if
	 * (armorOrginalOk1.getItemId() == item.getItemId() &&
	 * item.getEnchantLevel() == item.getItem() .get_safeenchant() +
	 * armorOrginalOk1.getLevel()) { // 道具加成等級相同 armorOrginal1 =
	 * armorOrginalOk1; break; } } } if (armorOrginal1 == null) { return; }
	 * boolean spmr = false; String add1 = ""; String add2 = ""; String add3 =
	 * ""; String add4 = ""; String add5 = ""; String add6 = ""; String add7 =
	 * ""; String add8 = ""; String add9 = ""; String add10 = ""; String add11 =
	 * ""; String add12 = ""; String add13 = ""; String add14 = ""; String add15
	 * = ""; String add16 = ""; String add17 = ""; String add18 = ""; if
	 * (armorOrginal1.getAddAc() != 0) { // 額外防禦
	 * pc.addAc(armorOrginal1.getAddAc()); add1 = "\\fU額外防禦減少：" +
	 * armorOrginal1.getAddAc(); } if (armorOrginal1.getAddStr() != 0) {
	 * pc.addStr(-armorOrginal1.getAddStr()); add2 = "\\fU額外力量減少：" +
	 * armorOrginal1.getAddStr(); } if (armorOrginal1.getAddDex() != 0) {
	 * pc.addDex(-armorOrginal1.getAddDex()); add3 = "\\fU額外敏捷減少：" +
	 * armorOrginal1.getAddDex(); } if (armorOrginal1.getAddCon() != 0) {
	 * pc.addCon(-armorOrginal1.getAddCon()); add4 = "\\fU額外體質減少：" +
	 * armorOrginal1.getAddCon(); } if (armorOrginal1.getAddInt() != 0) {
	 * pc.addInt(-armorOrginal1.getAddInt()); add5 = "\\fU額外智力減少：" +
	 * armorOrginal1.getAddInt(); } if (armorOrginal1.getAddWis() != 0) {
	 * pc.addWis(-armorOrginal1.getAddWis()); add6 = "\\fU額外精神減少：" +
	 * armorOrginal1.getAddWis(); } if (armorOrginal1.getAddCha() != 0) {
	 * pc.addCha(-armorOrginal1.getAddCha()); add7 = "\\fU額外魅力減少：" +
	 * armorOrginal1.getAddCha(); } if (armorOrginal1.getAddMaxHp() != 0) {
	 * pc.addMaxHp(-armorOrginal1.getAddMaxHp()); add8 = "\\fU額外血量減少：" +
	 * armorOrginal1.getAddMaxHp(); } if (armorOrginal1.getAddMaxMp() != 0) {
	 * pc.addMaxMp(-armorOrginal1.getAddMaxMp()); add9 = "\\fU額外魔力減少：" +
	 * armorOrginal1.getAddMaxMp(); } if (armorOrginal1.getAddHpr() != 0) {
	 * pc.addHpr(-armorOrginal1.getAddHpr()); add10 = "\\fU額外回血減少：" +
	 * armorOrginal1.getAddHpr(); } if (armorOrginal1.getAddMpr() != 0) {
	 * pc.addMpr(-armorOrginal1.getAddMpr()); add11 = "\\fU額外回魔減少：" +
	 * armorOrginal1.getAddMpr(); } if (armorOrginal1.getAddDmg() != 0) { if
	 * (item.getItem().getType1() != 20 && item.getItem().getType1() != 62) {
	 * pc.addDmgup(-armorOrginal1.getAddDmg()); add12 = "\\fU額外攻擊減少：" +
	 * armorOrginal1.getAddDmg(); } } if (armorOrginal1.getAddHit() != 0) { if
	 * (item.getItem().getType1() != 20 && item.getItem().getType1() != 62) {
	 * pc.addHitup(-armorOrginal1.getAddHit()); add13 = "\\fU額外命中減少：" +
	 * armorOrginal1.getAddHit(); } } if (armorOrginal1.getAddBowDmg() != 0) {
	 * if (item.getItem().getType1() == 20 || item.getItem().getType1() == 62) {
	 * pc.addBowDmgup(-armorOrginal1.getAddBowDmg()); add14 = "\\fU額外攻擊減少：" +
	 * armorOrginal1.getAddBowDmg(); } } if (armorOrginal1.getAddBowHit() != 0)
	 * { if (item.getItem().getType1() == 20 || item.getItem().getType1() == 62)
	 * { pc.addBowHitup(-armorOrginal1.getAddBowHit()); add15 = "\\fU額外命中減少：" +
	 * armorOrginal1.getAddBowHit(); } } if (armorOrginal1.getReduction_dmg() !=
	 * 0) { // 所有傷害減免
	 * pc.addDamageReductionByArmor(-armorOrginal1.getReduction_dmg()); add16 =
	 * "\\fU額外減免減少：" + armorOrginal1.getReduction_dmg(); } if
	 * (armorOrginal1.getAddMr() != 0) { pc.addMr(-armorOrginal1.getAddMr());
	 * add17 = "\\fU額外魔防減少：" + armorOrginal1.getAddMr(); spmr = true; } if
	 * (armorOrginal1.getAddSp() != 0) { if (item.getItem().getType1() == 40 ||
	 * item.getItem().getType1() == 4) { pc.addSp(-armorOrginal1.getAddSp());
	 * add18 = "\\fU額外魔攻減少：" + armorOrginal1.getAddSp(); spmr = true; } } if
	 * (spmr) { pc.sendPackets(new S_SPMR(pc)); } pc.sendPackets(new
	 * S_OwnCharStatus(pc)); pc.sendPackets(new S_SystemMessage(add1));
	 * pc.sendPackets(new S_SystemMessage(add2)); pc.sendPackets(new
	 * S_SystemMessage(add3)); pc.sendPackets(new S_SystemMessage(add4));
	 * pc.sendPackets(new S_SystemMessage(add5)); pc.sendPackets(new
	 * S_SystemMessage(add6)); pc.sendPackets(new S_SystemMessage(add7));
	 * pc.sendPackets(new S_SystemMessage(add8)); pc.sendPackets(new
	 * S_SystemMessage(add9)); pc.sendPackets(new S_SystemMessage(add10));
	 * pc.sendPackets(new S_SystemMessage(add11)); pc.sendPackets(new
	 * S_SystemMessage(add12)); pc.sendPackets(new S_SystemMessage(add13));
	 * pc.sendPackets(new S_SystemMessage(add14)); pc.sendPackets(new
	 * S_SystemMessage(add15)); pc.sendPackets(new S_SystemMessage(add16));
	 * pc.sendPackets(new S_SystemMessage(add17)); pc.sendPackets(new
	 * S_SystemMessage(add18));
	 */
	// }
}
