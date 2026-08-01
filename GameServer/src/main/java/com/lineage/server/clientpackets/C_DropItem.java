package com.lineage.server.clientpackets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.WriteLogTxt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求丟棄物品
 *
 * @author daien
 *
 */
public class C_DropItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DropItem.class);
	public static final Lock lock  = new ReentrantLock(true);

	public C_DropItem(final byte[] decrypt, final ClientExecutor client) {
		lock.lock();
		try {
			// 資料載入
			this.read(decrypt);

			final int x = this.readH();
			final int y = this.readH();
			final int objectId = this.readD();
			int count = this.readD();
			if (!client.getActiveChar().isFV()) {
				WriteLogTxt.NormalLog("丟棄封包", "\r\n" + client.getActiveChar().getName() + " OBJID:" + objectId + " count:" + count + " x:" + x + " y:" + y);
			}
			if (count > Integer.MAX_VALUE) {
				count = Integer.MAX_VALUE;
			}
			count = Math.max(0, count);
			final L1PcInstance pc = client.getActiveChar();
			if (pc.isGhost()) {
				return;
			}

			// 執行人物不是GM
			if(!ConfigAlt.DORP_ITEM && !pc.isGm()) {
				// \f1你不能夠放棄此樣物品。
				pc.sendPackets(new S_ServerMessage(125));
				return;
			}

			final L1ItemInstance item = pc.getInventory().getItem(objectId);

			// 物品為空
			if (item == null) {
				return;
			}
			
			if (item.getCount() <= 0) {
				return;
			}
			
			// 執行人物不是GM
			if (!pc.isGm()) {
				if (!item.getItem().isTradable()) {
					// \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					return;
				}

				if (item.get_time() != null) {
					// \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					return;
				}
				if (ItemRestrictionsTable.RESTRICTIONS.contains(item.getItemId())) {
					// \f1%0%d是不可轉移的…
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
					return;
				}
			}

			// 寵物
			final Object[] petlist = pc.getPetList().values().toArray();
			for (final Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// \f1%0%d是不可轉移的…
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
						return;
					}
				}
			}

			// 取回娃娃
			if (pc.getDoll(item.getId()) != null) {
				// 1,181：這個魔法娃娃目前正在使用中。  
				pc.sendPackets(new S_ServerMessage(1181));
				return;
			}

			if (item.isEquipped()) {
				// \f1你不能夠放棄此樣物品。
				pc.sendPackets(new S_ServerMessage(125));
				return;
			}
			if (item.getBless() >= 128) { // 封印裝備
				// \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}
			
			
	        //_log.info("人物:" + pc.getName() + " 丟棄物品 " + item.getItem().getName() + " 物品OBJID:" + item.getId());
			pc.getInventory().tradeItem(item, count, pc.get_showId(),
					World.get().getInventory(x, y, pc.getMapId()));
			/*L1ItemInstance newItem = pc.getInventory().tradeItem(item, count, 
					World.get().getInventory(x, y, pc.getMapId()));
			newItem.set_showId(pc.get_showId());*/
			
			if (!pc.isFV()) {
				// 丟棄物品記錄
				dropitem("IP" + "(" + pc.getNetConnection().getIp() + ")"
						+ "玩家" + ":【 " + pc.getName() + " 】 " + "的"
						+ "【 + " + item.getEnchantLevel() + " "
						+ item.getName() + "(" + count + ")" + " 】"
						+ " 丟棄到地上," + "時間:" + "("
						+ new Timestamp(System.currentTimeMillis()) + ")。");
				if (item.getItemId() == 44070) {
					WriteLogTxt.YanBoLog("元寶丟棄紀錄",
							"玩家" + ":【 " + pc.getName() + " 】 " + "把" + "【 + " + item.getEnchantLevel() + " "
									+ item.getName() + "(" + item.getCount() + ")" + " 】" + " 給丟棄了。" + "(時間"
									+ new Timestamp(System.currentTimeMillis()) + ")。");
				} else {
					WriteLogTxt.NormalLog("丟棄一般物品紀錄",
							"玩家" + ":【 " + pc.getName() + " 】 " + "把" + "【 + " + item.getEnchantLevel() + " "
									+ item.getViewName() + "(" + item.getCount() + ")" + " 】" + " 給丟棄了。" + "(時間"
									+ new Timestamp(System.currentTimeMillis()) + ")。");
				}
			}
			pc.turnOnOffLight();
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
			lock.unlock();
		}
	}
	//TODO 記錄文件檔 by 阿傑 
			public static void dropitem(String info) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter("日誌/丟棄物品記錄.txt", true));
					out.write(info + "\r\n");
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
