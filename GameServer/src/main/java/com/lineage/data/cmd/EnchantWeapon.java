package com.lineage.data.cmd;

import com.lineage.config.ConfigOther;
import com.lineage.server.WriteLogTxt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRecord;
import com.lineage.server.datatables.lock.LogEnchantReading;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.sql.Timestamp;

public class EnchantWeapon extends EnchantExecutor {

	private static final Log _log = LogFactory.getLog(EnchantWeapon.class);

	/**
	 * 強化紀錄(失敗)
	 * @param pc 執行者
	 * @param item 對像物件
	 */
	@Override
	public void failureEnchant(final L1PcInstance pc, final L1ItemInstance item) {
		WriteLogTxt.EvnetLog("武器強化失敗紀錄", "玩家:【 " + pc.getName() + " 】 衝爆武器[+" + item.getEnchantLevel() + " "
				+ item.getViewName() + "],時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
		final StringBuilder s = new StringBuilder();

		if (ConfigRecord.LOGGING_BAN_ENCHANT) {
			LogEnchantReading.get().failureEnchant(pc, item);
		}
		String pm = "";
		if (item.getEnchantLevel() > 0) {
			pm = "+";
		}
		s.append(pm + item.getEnchantLevel() + " " + item.getName());
//		// 未鑒定
//		if (!item.isIdentified()) {
//			s.append(item.getName());
//
//		} else {
//			String pm = "";
//			if (item.getEnchantLevel() > 0) {
//				pm = "+";
//			}
//			s.append(pm + item.getEnchantLevel() + " " + item.getName());
//		}
		// 164 \f1%0%s 產生激烈的 %1 光芒，一會兒後就消失了。
		if (item.getEnchantLevel() > item.getItem().get_safeenchant()) {
			World.get().broadcastPacketToAll(
					new S_HelpMessage(pc.getName(),s.toString() + " 產生激烈的 銀色的 光芒，一會兒後就消失了。"));
		}
		pc.sendPackets(new S_ServerMessage(164, s.toString(), "$252"));
		pc.getInventory().removeItem(item, item.getCount());
		_log.info("人物:" + pc.getName() + "點爆物品(武器)" + item.getItem().getName() + " 物品OBJID:" + item.getId());
	}

	/**
	 * 強化成功
	 * @param pc 執行者
	 * @param item 對像物件
	 * @param i 強化質
	 */
	@Override
	public void successEnchant(final L1PcInstance pc, final L1ItemInstance item, final int i) {
		final StringBuilder s = new StringBuilder();
		final StringBuilder sa = new StringBuilder();
		final StringBuilder sb = new StringBuilder();
//		 未鑒定
		if (!item.isIdentified()) {
			if (item.getEnchantLevel() > 0) {
				s.append("+").append(item.getEnchantLevel()).append(" ");
			}
			s.append(item.getName());

		} else {
			s.append(item.getLogName());
		}

		switch (i) {
		case 0:
			// \f1%0%s %2 產生激烈的 %1 光芒，但是沒有任何事情發生。
			pc.sendPackets(new S_ServerMessage(160, s.toString(), "$252", "$248"));
			return;
			
		case -1:
			sa.append("$246");// 黑色的
			sb.append("$247");// 一瞬間發出
			break;

		case 1: // '\001'
			sa.append("$245");// 藍色的
			sb.append("$247");// 一瞬間發出
			break;

		case 2: // '\002'
		case 3: // '\003'
			sa.append("$245");// 藍色的
			sb.append("$248");// 持續發出
			break;
		}

		// 161 \f1%0%s %2 %1 光芒。
		pc.sendPackets(new S_ServerMessage(161, s.toString(), sa.toString(), sb.toString()));

		final int oldEnchantLvl = item.getEnchantLevel();
		final int newEnchantLvl = oldEnchantLvl + i;
		if (oldEnchantLvl != newEnchantLvl) {
			if ((newEnchantLvl - item.getItem().get_safeenchant()) >= ConfigOther.weaponSuccessLvl) {// 強化值等於或超過9
				// 1,652：強化  
				// 產生訊息封包 (強化成功)
				World.get().broadcastPacketToAll(
						new S_HelpMessage(pc.getName(), 
								s.toString() + " " +sb.toString() + " " + sa.toString() + " $251"));
			}
			item.setEnchantLevel(newEnchantLvl);
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
		}
	}
}
