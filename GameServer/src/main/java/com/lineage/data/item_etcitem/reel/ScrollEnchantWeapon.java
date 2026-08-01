package com.lineage.data.item_etcitem.reel;

import java.util.Random;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1ItemUpdata;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>對武器施法的卷軸</font><BR>
 *
 */
public class ScrollEnchantWeapon extends ItemExecutor {

	/**
	 *
	 */
	private ScrollEnchantWeapon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollEnchantWeapon();
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
		
		final int safe_enchant = tgItem.getItem().get_safeenchant();
		boolean isErr = false;
		
		// 取得物件觸發事件
		final int use_type = tgItem.getItem().getUseType();
		switch (use_type) {
		case 1:// 武器
			if (safe_enchant < 0) { // 物品不可強化
				isErr = true;
			}
			break;
			
		default:
			isErr = true;
			break;
		}

		final int weaponId = tgItem.getItem().getItemId();
		if ((weaponId >= 246) && (weaponId <= 255)) { // 物品不可強化
			isErr = true;
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
		if ((enchant_level - safe_enchant) >= ConfigOther.Weapon_Lv) {
			pc.sendPackets(new S_ServerMessage(79));// 沒有任何事發生
			return;
		}
		final EnchantExecutor enchantExecutor = new EnchantWeapon();
		int randomELevel = enchantExecutor.randomELevel(tgItem, item.getBless());
		pc.getInventory().removeItem(item, 1);
		
		boolean isEnchant = true;
		if (enchant_level < -6) {// 武器將會消失,最大可追加到-7
			isEnchant = false;
			
		} else if (enchant_level < safe_enchant) {// 安定值內
			isEnchant = true;
			
		} else {
			final int rnd2 = rand(0, 999999);
			int enchant_chance_wepon;
			int skip_safe_level = (enchant_level - safe_enchant);
			if (skip_safe_level == 0) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_1;
			} else if (skip_safe_level == 1) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_2;
			} else if (skip_safe_level == 2) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_3;
			} else if (skip_safe_level == 3) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_4;
			} else if (skip_safe_level == 4) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_5;
			} else if (skip_safe_level == 5) {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON_6;
			} else {
				enchant_chance_wepon = ConfigRate.ENCHANT_CHANCE_WEAPON;
			}

			if (rnd2 < enchant_chance_wepon) {
				isEnchant = true;

			} else {
				if ((enchant_level >= 9) && (rnd2 < (enchant_chance_wepon * 2))) {
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