package com.lineage.data.event;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.CardUseTalble;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.templates.L1Event;
import com.william.CardUse;

/**
 * 月卡系统<BR>
 * @author dexc
 *
 */
public class CardSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(CardSet.class);
	
	// 月卡系统
	public static boolean START = false;
	
	public static int USE_COUNT;
	
	/**
	 *
	 */
	private CardSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new CardSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
		final String[] set = event.get_eventother().split(",");

		try {
			USE_COUNT = Integer.parseInt(set[0]);
			
		} catch (Exception e) {
			USE_COUNT = 5;
			_log.error("未设定卡片使用张数(使用预设5张)");
		}
	}

	/**
	 * 人物登入
	 * @param pc
	 */
	public static void load_card_mode(final L1PcInstance pc) {
		try {
			for (final L1ItemInstance item : pc.getInventory().getItems()) {

				if (item.getItem().getType2() != 0) {
					continue;
				}
				if (item.get_card_use() != 1) {
					item.setEquipped(false);
					continue;
				}
				item.setEquipped(true);
				//Add
				for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
					if (liu.getItemId() == item.getItemId()){
						if (liu.getName() != null){
							pc.setOtherName(liu.getName());
						}						
						pc.setCardUseCount(pc.getCardUseCount()+1);
						pc.setExpNotLose(liu.getExpNotLose());
						pc.setItemNotLose(liu.getItemNotLose());
						pc.addMaxHp(liu.getAddHp());
						pc.addMaxMp(liu.getAddMp());
						pc.addHpr(liu.getAddHpr());
						pc.addMpr(liu.getAddHpr());
						pc.addStr(liu.getAddStr());
						pc.addDex(liu.getAddDex());
						pc.addInt(liu.getAddInt());
						pc.addCon(liu.getAddCon());
						pc.addWis(liu.getAddWis());
						pc.addCha(liu.getAddCha());
						pc.addEarth(liu.getAddEarth());
						pc.addWater(liu.getAddWater());
						pc.addFire(liu.getAddFire());
						pc.addWind(liu.getAddWind());
						pc.addRegistStun(liu.getAddStun());
						pc.addRegistStone(liu.getAddStone());
						pc.addRegistSleep(liu.getAddSleep());
						pc.add_regist_freeze(liu.getAddFreeze());
						pc.addRegistSustain(liu.getAddSustain());
						pc.addRegistBlind(liu.getAddBlind());
						pc.addMr(liu.getAddMr());
						pc.addSp(liu.getAddSp());
						pc.addHitModifierByArmor(liu.getAddHit());
						pc.addBowHitModifierByArmor(liu.getAddBowHit());
						pc.addDmgModifierByArmor(liu.getAddDmg());
						pc.addBowDmgModifierByArmor(liu.getAddBowDmg());
						pc.addDamageReductionByArmor(liu.getAddReductionDmg());;
						pc.add_magic_modifier_dmg(liu.getAddMagiDmg());
						pc.add_magic_reduction_dmg(liu.getAddReductionMagiDmg());
						pc.setOtherExpByItem(pc.getOtherExpByItem()+liu.getAddExpGet());
						pc.addAc(liu.getAddAc());
					}
				}
				// 更改人物状态
				pc.sendPackets(new S_OwnCharStatus(pc));
				// 更改人物魔法攻击与魔法防御
				pc.sendPackets(new S_SPMR(pc));
				
				pc.broadcastPacketAll(new S_OtherCharPacks(pc));
			}
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 物品使用
	 * @param pc
	 * @param item
	 */
	public static void set_card_mode(final L1PcInstance pc, final L1ItemInstance item) {
		if (!START) {
			return;
		}
		try {
			item.setEquipped(true);
			for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
				if (liu.getItemId() == item.getItemId()){
					if (liu.getName() != null){
						pc.setOtherName(liu.getName());
					}
					pc.setOtherName(liu.getName());					
					pc.setExpNotLose(liu.getExpNotLose());
					pc.setItemNotLose(liu.getItemNotLose());
					pc.addMaxHp(liu.getAddHp());
					pc.addMaxMp(liu.getAddMp());
					pc.addHpr(liu.getAddHpr());
					pc.addMpr(liu.getAddHpr());
					pc.addStr(liu.getAddStr());
					pc.addDex(liu.getAddDex());
					pc.addInt(liu.getAddInt());
					pc.addCon(liu.getAddCon());
					pc.addWis(liu.getAddWis());
					pc.addCha(liu.getAddCha());
					pc.addEarth(liu.getAddEarth());
					pc.addWater(liu.getAddWater());
					pc.addFire(liu.getAddFire());
					pc.addWind(liu.getAddWind());
					pc.addRegistStun(liu.getAddStun());
					pc.addRegistStone(liu.getAddStone());
					pc.addRegistSleep(liu.getAddSleep());
					pc.add_regist_freeze(liu.getAddFreeze());
					pc.addRegistSustain(liu.getAddSustain());
					pc.addRegistBlind(liu.getAddBlind());
					pc.addMr(liu.getAddMr());
					pc.addSp(liu.getAddSp());
					pc.addHitModifierByArmor(liu.getAddHit());
					pc.addBowHitModifierByArmor(liu.getAddBowHit());
					pc.addDmgModifierByArmor(liu.getAddDmg());
					pc.addBowDmgModifierByArmor(liu.getAddBowDmg());
					pc.addDamageReductionByArmor(liu.getAddReductionDmg());;
					pc.add_magic_modifier_dmg(liu.getAddMagiDmg());
					pc.add_magic_reduction_dmg(liu.getAddReductionMagiDmg());
					pc.setOtherExpByItem(pc.getOtherExpByItem()+liu.getAddExpGet());
					pc.addAc(liu.getAddAc());

					if (liu.getTime() > 0){
						final long time = System.currentTimeMillis();// 目前时间豪秒
						final long x1 = liu.getTime() * 60/* * 60*/;// 指定耗用时数
						final long x2 = x1 * 1000;// 转为豪秒
						final long upTime = x2 + time;// 目前时间 加上指定耗用时数

						// 时间数据
						final Timestamp ts = new Timestamp(upTime);
						item.set_time(ts);
						// 人物背包物品使用期限资料
						CharItemsTimeReading.get().addTime(item.getId(), ts);
					}
					pc.sendPackets(new S_ItemName(item));
				}
			}
			// 更改人物状态
			pc.sendPackets(new S_OwnCharStatus(pc));
			// 更改人物魔法攻击与魔法防御
			pc.sendPackets(new S_SPMR(pc));
			
			pc.broadcastPacketAll(new S_OtherCharPacks(pc));

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 物品使用
	 * @param pc
	 * @param item
	 */
	public static void set_card_mode2(final L1PcInstance pc, final L1ItemInstance item) {
		if (!START) {
			return;
		}
		try {
			item.setEquipped(true);
			for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
				if (liu.getItemId() == item.getItemId()){
					if (liu.getName() != null){
						pc.setOtherName(liu.getName());
					}
					pc.setOtherName(liu.getName());					
					pc.setExpNotLose(liu.getExpNotLose());
					pc.setItemNotLose(liu.getItemNotLose());
					pc.addMaxHp(liu.getAddHp());
					pc.addMaxMp(liu.getAddMp());
					pc.addHpr(liu.getAddHpr());
					pc.addMpr(liu.getAddHpr());
					pc.addStr(liu.getAddStr());
					pc.addDex(liu.getAddDex());
					pc.addInt(liu.getAddInt());
					pc.addCon(liu.getAddCon());
					pc.addWis(liu.getAddWis());
					pc.addCha(liu.getAddCha());
					pc.addEarth(liu.getAddEarth());
					pc.addWater(liu.getAddWater());
					pc.addFire(liu.getAddFire());
					pc.addWind(liu.getAddWind());
					pc.addRegistStun(liu.getAddStun());
					pc.addRegistStone(liu.getAddStone());
					pc.addRegistSleep(liu.getAddSleep());
					pc.add_regist_freeze(liu.getAddFreeze());
					pc.addRegistSustain(liu.getAddSustain());
					pc.addRegistBlind(liu.getAddBlind());
					pc.addMr(liu.getAddMr());
					pc.addSp(liu.getAddSp());
					pc.addHitModifierByArmor(liu.getAddHit());
					pc.addBowHitModifierByArmor(liu.getAddBowHit());
					pc.addDmgModifierByArmor(liu.getAddDmg());
					pc.addBowDmgModifierByArmor(liu.getAddBowDmg());
					pc.addDamageReductionByArmor(liu.getAddReductionDmg());;
					pc.add_magic_modifier_dmg(liu.getAddMagiDmg());
					pc.add_magic_reduction_dmg(liu.getAddReductionMagiDmg());
					pc.setOtherExpByItem(pc.getOtherExpByItem()+liu.getAddExpGet());
					pc.addAc(liu.getAddAc());
					pc.sendPackets(new S_ItemName(item));
				}
			}
			// 更改人物状态
			pc.sendPackets(new S_OwnCharStatus(pc));
			// 更改人物魔法攻击与魔法防御
			pc.sendPackets(new S_SPMR(pc));
			
			pc.broadcastPacketAll(new S_OtherCharPacks(pc));

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 解除使用
	 * @param pc
	 * @param item
	 */
	public static void remove_card_mode(final L1PcInstance pc, final L1ItemInstance item) {
		if (!START) {
			return;
		}
		try {
			item.setEquipped(false);
			for (CardUse liu : CardUseTalble.getInstance().getTemplate()) {
				if (liu.getItemId() == item.getItemId()){
					pc.setOtherName(null);					
					pc.setExpNotLose(0);
					pc.setItemNotLose(0);
					pc.addMaxHp(-liu.getAddHp());
					pc.addMaxMp(-liu.getAddMp());
					pc.addHpr(-liu.getAddHpr());
					pc.addMpr(-liu.getAddHpr());
					pc.addStr(-liu.getAddStr());
					pc.addDex(-liu.getAddDex());
					pc.addInt(-liu.getAddInt());
					pc.addCon(-liu.getAddCon());
					pc.addWis(-liu.getAddWis());
					pc.addCha(-liu.getAddCha());
					pc.addEarth(-liu.getAddEarth());
					pc.addWater(-liu.getAddWater());
					pc.addFire(-liu.getAddFire());
					pc.addWind(-liu.getAddWind());
					pc.addRegistStun(-liu.getAddStun());
					pc.addRegistStone(-liu.getAddStone());
					pc.addRegistSleep(-liu.getAddSleep());
					pc.add_regist_freeze(-liu.getAddFreeze());
					pc.addRegistSustain(-liu.getAddSustain());
					pc.addRegistBlind(-liu.getAddBlind());
					pc.addMr(-liu.getAddMr());
					pc.addSp(-liu.getAddSp());
					pc.addHitModifierByArmor(-liu.getAddHit());
					pc.addBowHitModifierByArmor(-liu.getAddBowHit());
					pc.addDmgModifierByArmor(-liu.getAddDmg());
					pc.addBowDmgModifierByArmor(-liu.getAddBowDmg());
					pc.addDamageReductionByArmor(-liu.getAddReductionDmg());;
					pc.add_magic_modifier_dmg(-liu.getAddMagiDmg());
					pc.add_magic_reduction_dmg(-liu.getAddReductionMagiDmg());	
					pc.setOtherExpByItem(pc.getOtherExpByItem() - liu.getAddExpGet());
					pc.addAc(-liu.getAddAc());
					pc.sendPackets(new S_ItemName(item));
				   }
			   }			
			// 更改人物状态
			pc.sendPackets(new S_OwnCharStatus(pc));
			// 更改人物魔法攻击与魔法防御
			pc.sendPackets(new S_SPMR(pc));
			
			pc.broadcastPacketAll(new S_OtherCharPacks(pc));
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}

