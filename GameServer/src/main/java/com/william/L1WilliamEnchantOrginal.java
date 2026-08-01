package com.william;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.BinaryOutputStream;

/**
 * 武器強化能力系統
 */
public class L1WilliamEnchantOrginal {
	private final int _id;
	private final int _itemid;
	private final int _level;
	private final int _type;
	private final double _dmgbl;
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
	private int _addPVPDmg;

	public L1WilliamEnchantOrginal(final int id, final int itemid,
			final int level, final int type, final double dmgbl,
			final int addAc, final byte addStr, final byte addDex,
			final byte addCon, final byte addInt, final byte addWis,
			final byte addCha, final int addMaxHp, final int addMaxMp,
			final int addHpr, final int addMpr, final int addDmg,
			final int addBowDmg, final int addHit, final int addBowHit,
			final int reduction_dmg, final int addMr, final int addSp, final int addPVPDmg) {

		_id = id;
		_itemid = itemid;
		_level = level;
		_type = type;
		_dmgbl = dmgbl;
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
		_addPVPDmg = addPVPDmg;
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

	public double getdmgbl() {
		return _dmgbl;
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
	public int getAddPVPDmg() {
		return _addPVPDmg;
	}

	public static L1WilliamEnchantOrginal getL1WilliamEnchantOrginal(final L1ItemInstance item) {
		L1WilliamEnchantOrginal armorOrginal = null;
		L1WilliamEnchantOrginal armorOrginalOk = null;
		final L1WilliamEnchantOrginal[] armorOrginalSize = EnchantOrginal
				.getInstance().getArmorList();
		for (int i = 0; i < armorOrginalSize.length; i++) {
			int type = -1;
			if (item.getItem().getType2() == 2) {
				type = item.getItem().getUseType();
			}
			if (item.getItem().getType2() == 1) {
				type = item.getItem().getType1();
			}
			armorOrginalOk = item.getBless() == 1 ? EnchantOrginal.getInstance().getTemplate(i) : EnchantOrginalBless.getInstance().getTemplate(i);
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
		L1WilliamEnchantOrginal armorOrginal = getL1WilliamEnchantOrginal(item);
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
		if (armorOrginal.getdmgbl() > 1) {
			_os.writeC(0x27);
			_os.writeS("武器攻擊倍率+" + armorOrginal.getdmgbl() + ".");
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
			if (item.getItem().getType1() == 40
					|| item.getItem().getType1() == 4) {
				_os.writeC(0x27);
				_os.writeS("魔攻+" + armorOrginal.getAddSp() + ".");
			}
		}
		if (armorOrginal.getAddPVPDmg() != 0) {
			_os.writeC(0x27);
			_os.writeS("PVP傷害+" + armorOrginal.getAddPVPDmg() + ".");
		}
	}
	/** 增加效果 */
	public static void getAddArmorOrginal(final L1PcInstance pc,
			final L1ItemInstance item) {
		L1WilliamEnchantOrginal armorOrginal = getL1WilliamEnchantOrginal(item);
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
		if (armorOrginal.getdmgbl() > 1) {
			pc.setdmgbl(armorOrginal.getdmgbl());
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
			if (item.getItem().getType1() == 40
					|| item.getItem().getType1() == 4) {
				pc.addSp(armorOrginal.getAddSp());
				spmr = true;
			}
		}
		if (armorOrginal.getAddPVPDmg() != 0) {
			pc.addPVPDmg(armorOrginal.getAddPVPDmg());
		}
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	/** 解除效果 */
	public static void getReductionArmorOrginal(final L1PcInstance pc,
			final L1ItemInstance item) {
		L1WilliamEnchantOrginal armorOrginal = getL1WilliamEnchantOrginal(item);
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
		if (armorOrginal.getdmgbl() > 1) {
			pc.setdmgbl(1.0);
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
			if (item.getItem().getType1() == 40
					|| item.getItem().getType1() == 4) {
				pc.addSp(-armorOrginal.getAddSp());
				spmr = true;
			}
		}
		if (armorOrginal.getAddPVPDmg() != 0) {
			pc.addPVPDmg(-armorOrginal.getAddPVPDmg());
		}
		if (spmr) {
			pc.sendPackets(new S_SPMR(pc));
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}
	/** 增加效果 */
}
