/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// 記錄文件檔
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
// 記錄文件檔 end

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;

/**
 * 防止物品作弊處理<br>
 * 類名稱：C_PickUpItem<br>
 * 創建人:xljnet<br>
 * 修改時間：2018年4月22日 下午1:54:54<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version Rev:3.2 Bin:81222<br>
 */
public class L1CheckPcItem {
	
	private int itemId;
	private boolean isStackable = false;

	public L1CheckPcItem() {
	}

	/**
	 * 檢查該道具記憶體數量是否與資料庫數量相符
	 * @param count	記憶體中的道具數量
	 * @param item	針對該道具交叉比對記憶體與資料庫
	 * @param pc	執行者
	 * @return 被檢查物品的資料庫數量 大於 記憶體數量 傳回 true 小於則傳回FALSE
	 */
	public synchronized boolean getPcDBItemCount(final long count, final L1ItemInstance item, final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int actualcount = 0;
		try {
		    con = DatabaseFactory.get().getConnection();
		    pstm = con.prepareStatement("SELECT * FROM character_items WHERE char_id =? AND item_id = ?");
		    pstm.setInt(1, pc.getId());
		    pstm.setInt(2, item.getItem().getItemId());
		    rs = pstm.executeQuery();
		    while (rs.next()) {
		        actualcount = rs.getInt("count");
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		
		if (actualcount >= count) {
			return true;
		} else {
			// 一但發現資料庫與記憶體數量不符合，則針對該角色做紀錄
			DeleteCharacter("玩家"
					+ ":【 " + pc.getName() + " 】 "
					+ "疑似有在洗 "
					+ item.getName()
					+ " 道具的可能，" 
					+ "時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
		}
		return false;	
	}
	
	public boolean checkPcItem(L1ItemInstance item, L1PcInstance pc) {
		itemId = item.getItem().getItemId();
		long itemCount = item.getCount();
		boolean isCheat = false;

		if ((findWeapon() || findArmor()) && itemCount != 1) {
			isCheat = true;
		} else if (findEtcitem()) {
			// 不可堆疊的道具同一格不等於1時設定為作弊
			if (!isStackable && itemCount != 1) {
				isCheat = true;
			// 金幣大於21億以及金幣負值設定為作弊
			} else if (itemId == 40308 && (itemCount > 2100000000 || itemCount < 0)) {
				isCheat = true;
			// 金元寶大於50萬以及金元寶負值設定為作弊
			} else if (itemId == 44070 && (itemCount > 500000 || itemCount < 0)) {
				isCheat = true;
			// 箭類大於21億以及負值設定為作弊
			} else if (itemId >= 40741 && itemId <= 40748 && (itemCount > 2100000000 || itemCount < 0)) {
				isCheat = true;
			// 可堆疊道具(金幣與金元寶除外)堆疊超過十萬個以及堆疊負值設定為作弊
			} else if (isStackable && (itemId != 40308 && itemId != 44070 && itemId != 40741
				&& itemId != 40742 && itemId != 40743 && itemId != 40744 && itemId != 40745
				&& itemId != 40746 && itemId != 40747 && itemId != 40748 && itemId != 40749)
				&& (itemCount > 100000 || itemCount < 0)) {
				isCheat = true;
			}
		}
		if (isCheat) {
			// 作弊成立則刪除道具
			//pc.getInventory().removeItem(item, itemCount);
			pc.sendPackets(new S_SystemMessage("除了金幣以外，任何道具只要超過10萬個，一律無法使用"));
			DeleteCharacter("玩家"
				+ ":【 " + pc.getName() + " 】 "
				+ "疑似有在洗 "
				+ item.getName()
				+ " 道具的可能，" 
				+ "時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
		}
		return isCheat;
	}

// 記錄文件檔
	public static void DeleteCharacter(String info) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("日誌/偵測異常類別各項紀錄/角色異常紀錄.txt", true));
			out.write(info + "\r\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
// 記錄文件檔 end

	private boolean findWeapon() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean inWeapon = false;

		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM weapon WHERE item_id = ?");
			pstm.setInt(1, itemId);
			rs = pstm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					inWeapon = true;
				}
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return inWeapon;
	}

	private boolean findArmor() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean inArmor = false;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM armor WHERE item_id = ?");
			pstm.setInt(1, itemId);
			rs = pstm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					inArmor = true;
				}
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return inArmor;
	}

	private boolean findEtcitem() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean inEtcitem = false;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM etcitem WHERE item_id = ?");
			pstm.setInt(1, itemId);
			rs = pstm.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					inEtcitem = true;
					isStackable = rs.getInt("stackable") == 1 ? true : false;
				}
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return inEtcitem;
	}
}
