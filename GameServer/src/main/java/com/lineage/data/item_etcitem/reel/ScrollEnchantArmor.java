package com.lineage.data.item_etcitem.reel;

import java.util.Random;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.EnchantArmor;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 對盔甲施法的卷軸40074<br>
 * 對盔甲施法的卷軸140074<br>
 * 對盔甲施法的卷軸240074<br>
 */
public class ScrollEnchantArmor extends ItemExecutor {

	/**
	 *
	 */
	private ScrollEnchantArmor() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollEnchantArmor();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 對像OBJID
		final int targObjId = data[0];

		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}

		// 安定值
		final int safe_enchant = tgItem.getItem().get_safeenchant();

		boolean isErr = false;

		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 2:// 盔甲
		case 18:// T恤
		case 19:// 斗篷
		case 20:// 手套
		case 21:// 靴
		case 22:// 頭盔
		case 25:// 盾牌
			if (safe_enchant < 0) { // 物品不可強化
				isErr = true;
			}
			break;
			
		default:
			isErr = true;
			break;
		}
		
		if (tgItem.getBless() >= 128) {// 封印的裝備
			isErr = true;
		}

		if (isErr) {
			pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
			return;
		}

		// 物品已追加值
		final int enchant_level = tgItem.getEnchantLevel();
		if ((enchant_level - safe_enchant) >= ConfigOther.Armor_Lv) {
			pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
			return;
		}
		final EnchantExecutor enchantExecutor = new EnchantArmor();
		int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
		pc.getInventory().removeItem(item, 1);
		
		boolean isEnchant = true;
		if (enchant_level < -6) {// 盔甲將會消失,最大可追加到-7
			isEnchant = false;
			
		} else if (enchant_level < safe_enchant) {// 安定值內
			isEnchant = true;
			
		} else {// 超出安定值
			final int rnd = rand(0, 999999);
			int enchant_chance_armor;
			int enchant_level_tmp;

			if (safe_enchant == 0) { // 對防具安定直為0初始計算+2
				enchant_level_tmp = enchant_level + 2;
				
			} else {
				enchant_level_tmp = enchant_level;
			}

			int skip_safe_level = (enchant_level - safe_enchant);
			if (skip_safe_level == 0) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_1;
			} else if (skip_safe_level == 1) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_2;
			} else if (skip_safe_level == 2) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_3;
			} else if (skip_safe_level == 3) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_4;
			} else if (skip_safe_level == 4) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_5;
			} else if (skip_safe_level == 5) {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR_6;
			} else {
				enchant_chance_armor = ConfigRate.ENCHANT_CHANCE_ARMOR;
			}

			if (rnd < enchant_chance_armor) {
				isEnchant = true;

			} else {
				if ((enchant_level >= 9) && (rnd < (enchant_chance_armor * 2))) {
					randomELevel = 0;
					
				} else {
					isEnchant = false;
				}
			}
		}
		if ((randomELevel <= 0) && (enchant_level > -6)) {
			isEnchant = true;
		}

		boolean project = tgItem.getproctect();
		int protectRom = tgItem.getProctectRom();
		int protectType = tgItem.getProctectType();
		if (project) {
			tgItem.setproctect(false);
			tgItem.setProctectType(0);
			tgItem.setProctectRom(0);
			pc.sendPackets(new S_ItemStatus(tgItem));
			pc.getInventory().saveItem(tgItem, L1PcInventory.COL_PROTECT_INDEX);
		}
		
		if (isEnchant) {// 成功
			if (randomELevel > 0) {
				if ((enchant_level - safe_enchant) >= 0) {
					randomELevel = 1;
				}
			}
			enchantExecutor.successEnchant(pc, tgItem, randomELevel);
			
		} else {// 失敗
			if (project && (protectRom == 0 || protectRom == 100 || (rand(0, 999999) < protectRom * 10000))) {
				if (protectType == 1) {
					int set = tgItem.getEnchantLevel() - 1;
					if (set < 0) {
						set = 0;
					}
					tgItem.setEnchantLevel(set);
				} else if (protectType == 2) {
					tgItem.setEnchantLevel(0);
				}
				pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ENCHANTLVL);
				pc.sendPackets(new S_ItemStatus(tgItem));
				pc.sendPackets(new S_ServerMessage("由於受到了魔法力量的保護，雖然產生激烈的 藍色的 光芒，但是裝備沒有消失。"));
				return;
			}
			enchantExecutor.failureEnchant(pc, tgItem);
		}
	}
	public static final int rand(final int lbound, final int ubound) {
		final Random random = new Random();
		return (int) ((random.nextDouble() * (ubound - lbound + 1)) + lbound);
	}
}