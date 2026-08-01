package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 攻城獎勵設置
 * 
 * @author dexc
 * 
 */
public class CastleWarGiftTable {

	private static final Log _log = LogFactory.getLog(CastleWarGiftTable.class);

	private static final Map<Integer, ArrayList<Gift>> _gifts = new HashMap<Integer, ArrayList<Gift>>();

	private static CastleWarGiftTable _instance;

	public static CastleWarGiftTable get() {
		if (_instance == null) {
			_instance = new CastleWarGiftTable();
		}
		return _instance;
	}
	
	private class Gift {
		private int _itemid;
		private int _count;
		private boolean _recover = false;
	}
	
	/**
	 * 初始化載入
	 */
	public void load() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"SELECT * FROM `server_castle_war_gift`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int key = rs.getInt("castle_id");
				final int itemid = rs.getInt("itemid");
				final int count = rs.getInt("count");
				final boolean recover = rs.getBoolean("recover");
				
				final Gift e = new Gift();
				e._itemid = itemid;
				e._count = count;
				e._recover = recover;
				
				ArrayList<Gift> list = _gifts.get(key);
				if (list == null) {
					list = new ArrayList<Gift>();
				}
				list.add(e);
				
				_gifts.put(key, list);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 是否具有攻城獎勵設置
	 * @param objid
	 * @return
	 */
	public boolean isGift(final int key) {
		final ArrayList<Gift> list = _gifts.get(key);
		if (list == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 給予攻城獎勵
	 * @param key
	 */
	public void get_gift(final int key) {
		L1Clan castle_clan = null;
		final ArrayList<Gift> list = _gifts.get(key);
		if (list == null) {
			return;
		}
		
		try {
			castle_clan = L1CastleLocation.castleClan(key);
			/*final Collection<L1Clan> allClan = WorldClan.get().getAllClans();
			// 不包含元素
			if (allClan.isEmpty()) {
				return;
			}
			
			for (final Iterator<L1Clan> iter = allClan.iterator(); iter.hasNext();) {
				final L1Clan clan = iter.next();
				if (clan.getCastleId() == key) {
					castle_clan = clan; // 設置血盟
				}
			}*/
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
//			if (castle_clan != null) {
//				for (final Iterator<Gift> iter = list.iterator(); iter.hasNext();) {
//					final Gift gift = iter.next();
//					if (gift._recover) {// 該物品設置回收
//						recover_item(gift._itemid);
//					}
//					get_gift(castle_clan, gift._itemid, gift._count);
//				}
//			}
		}
	}

	/**
	 * 給予物品
	 * @param castle_clan
	 * @param itemid
	 * @param count
	 */
	private void get_gift(final L1Clan castle_clan, final int itemid, final int count) {
		try {
			if (castle_clan.getOnlineClanMemberSize() > 0) {
				// 取回線上成員
				for (L1PcInstance tgpc : castle_clan.getOnlineClanMember()) {
					final L1ItemInstance item = ItemTable.get().createItem(itemid);
					if (item != null) {
						item.setCount(count);
						// 加入背包
						tgpc.getInventory().storeItem(item);
						// 送出訊息
						tgpc.sendPackets(new S_ServerMessage("獲得攻城獎勵: " + item.getLogName()));
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 回收其他人物該物品
	 * @param itemid
	 */
	private void recover_item(final int itemid) {
		try {
			// 全部線上人物
			final Collection<L1PcInstance> allpc = World.get().getAllPlayers();
			for (final Iterator<L1PcInstance> iter = allpc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				final L1ItemInstance t1 = tgpc.getInventory().findItemId(itemid);
				if (t1 != null) {
					if (t1.isEquipped()) {
						tgpc.getInventory().setEquipped(t1, false, false, false);
					}
					tgpc.getInventory().removeItem(t1);
					// 158：\f1%0%s 消失。  
					tgpc.sendPackets(new S_ServerMessage(158, t1.getLogName()));
				}
			}
			
			// 刪除該物品全部數據
			CharItemsReading.get().del_item(itemid);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
