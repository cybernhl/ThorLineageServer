package com.lineage.data.item_etcitem;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.CardUseTalble;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_IdentifyDesc;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import com.william.CardUse;

import java.util.Random;

/**
 * 鑒定卷軸40126 象牙塔鑒定卷軸40098
 */
public class Appraisal_Reel extends ItemExecutor {

	/**
	 *
	 */
	private Appraisal_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Appraisal_Reel();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	private static final Random _random = new Random();
	public final int rand(final int lbound, final int ubound) {
		return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
	}
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		final int itemobj = data[0];
		final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
		if (item1 == null) {
			return;
		}
		boolean ability = false;
		if (item1.getCanAbilityType() == 1) {
			ability = true;
			int chance = rand(0, 999999);
			if (chance < ConfigOther.ABILITY_3_CHANCE) {
				item1.setCanAbilityType(4);
			} else if (chance < ConfigOther.ABILITY_2_CHANCE) {
				item1.setCanAbilityType(3);
			} else {
				item1.setCanAbilityType(2);
			}
		}
		if (!item1.isIdentified()) {
			item1.setIdentified(true);
			pc.getInventory().updateItem(item1, L1PcInventory.COL_IS_ID);
		}
		if (item1.getItem().getQuality1() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality1()));
		}
		if (item1.getItem().getQuality2() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality2()));
		}

		if (item1.getItem().getQuality3() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality3()));
		}
		if (item1.getItem().getQuality4() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality4()));
		}
		if (item1.getItem().getQuality5() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality5()));
		}
		if (item1.getItem().getQuality6() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality6()));
		}
		if (item1.getItem().getQuality7() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality7()));
		}

		if (item1.getItem().getQuality8() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality8()));
		}
		if (item1.getItem().getQuality9() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality9()));
		}
		if (item1.getItem().getQuality10() != null) {
			pc.sendPackets(new S_SystemMessage("\\fW"
					+ item1.getItem().getQuality10()));
		}
		for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
			if (liu.getItemId() == item1.getItemId()) {
				if (liu.getAddHp() > 0) {
					pc.sendPackets(new S_SystemMessage("\\fW最大HP +"
							+ liu.getAddHp()));
				}
				if (liu.getAddMp() > 0) {
					pc.sendPackets(new S_SystemMessage("\\fW最大MP +"
							+ liu.getAddMp()));
				}
				if (liu.getAddHpr() > 0) {
					pc.sendPackets(new S_SystemMessage("\\fW體力回復 +"
							+ liu.getAddHpr()));
				}
				if (liu.getAddMpr() > 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魔力回復 +"
							+ liu.getAddMpr()));
				}
				if (liu.getAddStr()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW力量 +"
							+ liu.getAddStr()));
				}
				if (liu.getAddDex()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW敏捷 +"
							+ liu.getAddDex()));
				}
				if (liu.getAddInt()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW智力 +"
							+ liu.getAddInt()));
				}
				if (liu.getAddWis()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW精神 +"
							+ liu.getAddWis()));
				}
				if (liu.getAddCon()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW體質 +"
							+ liu.getAddCon()));
				}
				if (liu.getAddCha()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魅力 +"
							+ liu.getAddCha()));
				}
				if (liu.getAddEarth()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW地屬性 +"
							+ liu.getAddEarth()));
				}
				if (liu.getAddWater()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW水屬性 +"
							+ liu.getAddWater()));
				}
				if (liu.getAddFire()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW火屬性 +"
							+ liu.getAddFire()));
				}
				if (liu.getAddWind()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW風屬性 +"
							+ liu.getAddWind()));
				}
				if (liu.getAddStun()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW暈眩耐性 +"
							+ liu.getAddStun()));
				}
				if (liu.getAddStone()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW石化耐性 +"
							+ liu.getAddStone()));
				}
				if (liu.getAddSleep()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW睡眠耐性 +"
							+ liu.getAddSleep()));
				}
				if (liu.getAddFreeze()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW寒冰耐性 +"
							+ liu.getAddFreeze()));
				}
				if (liu.getAddSustain()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW支撐耐性 +"
							+ liu.getAddSustain()));
				}
				if (liu.getAddBlind()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW暗黑耐性 +"
							+ liu.getAddBlind()));
				}
				if (liu.getAddMr()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魔法防禦 +"
							+ liu.getAddMr()));
				}
				if (liu.getAddSp()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魔攻 +"
							+ liu.getAddSp()));
				}
				if (liu.getAddHit()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW近距離命中 +"
							+ liu.getAddHit()));
				}
				if (liu.getAddBowHit()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW遠距離命中 +"
							+ liu.getAddBowHit()));
				}
				if (liu.getAddDmg()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW近距離攻擊 +"
							+ liu.getAddDmg()));
				}
				if (liu.getAddBowDmg()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW遠距離攻擊 +"
							+ liu.getAddBowDmg()));
				}
				if (liu.getAddMagiDmg()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魔法攻擊 +"
							+ liu.getAddMagiDmg()));
				}
				if (liu.getAddReductionDmg()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW物理傷害減免 +"
							+ liu.getAddReductionDmg()));
				}
				if (liu.getAddReductionMagiDmg()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW魔法傷害減免 +"
							+ liu.getAddReductionMagiDmg()));
				}
				if (liu.getAddExpGet()> 0) {
					pc.sendPackets(new S_SystemMessage("\\fW經驗 +"
							+ liu.getAddExpGet()+"%"));
				}
				if (liu.getAddAc() != 0) {
					pc.sendPackets(new S_SystemMessage("\\fW防禦 +"
							+ (liu.getAddAc() < 0 ? Math.abs(liu.getAddAc()) : liu.getAddAc()) + "%"));
				}
			}
		}
		pc.sendPackets(new S_IdentifyDesc(item1));
		pc.getInventory().removeItem(item, 1);
		if (ability) {
			pc.getInventory().updateItem(item1, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item1, L1PcInventory.COL_ABILITY_POS_1_ID + L1PcInventory.COL_ABILITY_POS_2_ID + L1PcInventory.COL_ABILITY_POS_3_ID + L1PcInventory.COL_CAN_ABILITY_TYPE);
			if (item1.getCanAbilityType() >= 3) {
				World.get().broadcastPacketToAll(new S_BlueMessage(0, "\\f=" + pc.getName() + " 的 " + item1.getViewName() + " 鑑定出了！"));
				World.get().broadcastPacketToAll(new S_ServerMessage("\\fT" + pc.getName() + " 的 " + item1.getViewName() + " 鑑定出了！"));
			}
		}
	}
}
