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

import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求撿取物品
 *
 * @author daien
 *
 */
public class C_PickUpItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_PickUpItem.class);
	public static final Lock lock = new ReentrantLock(true);

	public C_PickUpItem(final byte[] decrypt, final ClientExecutor client) {
		lock.lock();
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isDead()) { // 死亡
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			if (pc.isInvisble()) { // 隱身狀態
				return;
			}

			if (pc.isInvisDelay()) { // 隱身延遲
				return;
			}

			final int x = this.readH();
			final int y = this.readH();
			final int objectId = this.readD();
			long pickupCount = this.readD();
			if (!pc.isFV()) {
				WriteLogTxt.NormalLog("檢取封包", "\r\n" + client.getActiveChar().getName() + " OBJID:" + objectId + " count:" + pickupCount + " x:" + x + " y:" + y);
			}
			if (pickupCount > Integer.MAX_VALUE) {
				pickupCount = Integer.MAX_VALUE;
			}
			pickupCount = Math.max(0, pickupCount);
			final L1Inventory groundInventory = 
				World.get().getInventory(x, y, pc.getMapId());
			
			final L1Object object = groundInventory.getItem(objectId);
			if ((object != null) && !pc.isDead()) {
				final L1ItemInstance item = (L1ItemInstance) object;
				if (item.getCount() <= 0) {
					return;
				}
				if ((item.getItemOwnerId() != 0) && (pc.getId() != item.getItemOwnerId())) {
					// 道具取得失敗。
					pc.sendPackets(new S_ServerMessage(623));
					return;
				}
				if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
					return;
				}
				item.set_showId(-1);
				// 容量重量確認
				if (pc.getInventory().checkAddItem(item, pickupCount) == L1Inventory.OK) {
					if ((item.getX() != 0) && (item.getY() != 0)) {
						groundInventory.tradeItem(item, pickupCount, pc.getInventory());
						// 改變亮度
						pc.turnOnOffLight();

						// 改變面向
						pc.setHeading(pc.targetDirection(item.getX(), item.getY()));
						
						// 因應改變面向 使用物件攻擊封包送出動作以及面向
						// 不需要對自己送
						//pc.sendPackets(new S_ChangeHeading(pc));
						// 送出封包(動作)
						//pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Pickup));
						//pc.sendPackets(new S_AttackPickUpItem(pc, objectId));
						if (!pc.isGmInvis()) {
							pc.broadcastPacketAll(new S_ChangeHeading(pc));
							// 送出封包(動作)
							pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Pickup));
						      pickupitem("IP" + "(" + pc.getNetConnection().getIp() + ")"
									    + "玩家" + ":【 " + pc.getName() + " 】 " + "把"
		    							+ "【 + " + item.getEnchantLevel() + " "
		    							+ item.getName() + "(" + pickupCount + ")" + " 】"
		    							+ " 從地上撿起來了," + "時間:" + "("
		    							+ new Timestamp(System.currentTimeMillis()) + ")。");
							//pc.broadcastPacketAll(new S_AttackPickUpItem(pc, objectId));
						}
					}
				}
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
			lock.unlock();
		}
	}
	// 記錄文件檔
	 	public static void pickupitem(String info) {
	 		try {
	 			BufferedWriter out = new BufferedWriter(new FileWriter(
	 					"日誌/地上物品撿取紀錄.txt", true));
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
