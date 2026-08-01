package com.william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import com.lineage.server.templates.L1PcOther;

import java.sql.Connection;

import java.sql.ResultSet;

import java.sql.Statement;

import java.util.ArrayList;


public class PayBonus
{
	public static void getItem(L1PcInstance pc, long count)
	{
	Connection conn = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat
					.executeQuery("SELECT * FROM william_PayBonus");
			ArrayList arraylist = null;
			
			int nowb = pc.get_other().get_getbonus();
			if (rs != null) {
				while (rs.next()) {
					int money = rs.getInt("money");
					int itemid = rs.getInt("give_item");
				int itemcount = rs.getInt("give_count");
					if (((int) count + nowb >= money) &&
					(nowb < money)) {
						L1ItemInstance items = pc.getInventory()
								.storeItem(itemid, itemcount);
						pc.sendPackets(new S_ServerMessage("\\fW獲得贊助滿"
								+ money + "好禮:" + items.getName() + "("
								+ itemcount + ")"));
						}
					}
				}
			
			pc.get_other().set_getbonus(
					pc.get_other().get_getbonus() + (int) count);
			if ((conn != null) && (!conn.isClosed())) {
				conn.close();
				}
			}
		catch (Exception ex) {
		}
	}
	
}

