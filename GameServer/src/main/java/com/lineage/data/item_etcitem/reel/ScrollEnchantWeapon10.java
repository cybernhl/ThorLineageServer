package com.lineage.data.item_etcitem.reel;

import java.util.Random;

import com.lineage.data.cmd.EnchantExecutor;
import com.lineage.data.cmd.EnchantWeapon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 
 * 类名称：ScrollEnchantWeapon10<br>
 * 类描述：对武器施法的防爆卷轴<br>
 * 创建人:四海<br>
 * 修改时间：2018年8月31日 下午10:42:03<br>
 * 修改人:QQ:40347155<br>
 * 修改备注:<br>
 * @version<br>
 */
public class ScrollEnchantWeapon10 extends ItemExecutor {

	private ScrollEnchantWeapon10() {}

	public static ItemExecutor get() {
		return new ScrollEnchantWeapon10();
	}

	/**
	 * 道具物件执行
	 * @param data 参数
	 * @param pc 执行者
	 * @param tgItem 物件
	 */
	@Override
	public void execute(final int[] readD, final L1PcInstance pc, final L1ItemInstance item) {
		final int l = readD[0];
		L1ItemInstance tgItem = pc.getInventory().getItem(l);
		
		if (tgItem == null) {
			return;
		}
		
		final int safe_enchant = tgItem.getItem().get_safeenchant();
		boolean isErr = false;
		
		// 取得物件触发事件
		final int use_type = tgItem.getItem().getType2();
		switch (use_type) {
		case 1:// 武器
			if (safe_enchant < 0) { // 物品不可强化
				isErr = true;
			}
			break;
			
		default:
			isErr = true;
			break;
		}
		final int weaponId = tgItem.getItem().getItemId();
		if ((weaponId >= 246) && (weaponId <= 255)) { // 物品不可强化
			isErr = true;
		}
		
		if (tgItem.getBless() >= 128) {// 封印的装备
			isErr = true;
		}

		if (isErr) {
			pc.sendPackets(new S_ServerMessage(79));// 没有任何事发生
			return;
		}
		// 物品已追加值
		final int enchant_level = tgItem.getEnchantLevel();
		
		// 限定 最高加到多少
		if (enchant_level != event) {
			pc.sendPackets(new S_ServerMessage("此卷只能對+"+event+"武器使用"));// 没有任何事发生
			return;
		}
		
		final EnchantExecutor enchantExecutor = new EnchantWeapon();
		pc.getInventory().removeItem(item, 1);

		boolean isEnchant = false;
		final Random random = new Random();		
		int r = random.nextInt(100) + 1;	
		if (r <= rand){
			isEnchant = true;
		}
		
		if (isEnchant) {// 成功
			enchantExecutor.successEnchant(pc, tgItem, 1);
			
		} else {// 失败
			enchantExecutor.successEnchant(pc, tgItem, 0);
		}
	}
    private int event = 1; // 時間
    private int rand = 1; // 時間
   @Override
   public void set_set(String[] set) {
	   try {	
		event= Integer.parseInt(set[1]);	
		rand = Integer.parseInt(set[2]);
	  } catch (Exception e) {
	  }
     }
}