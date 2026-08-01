package com.lineage.server.clientpackets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求刪除物品
 *
 * @author daien
 *
 */
public class C_DeleteInventoryItem extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_DeleteInventoryItem.class);

	public C_DeleteInventoryItem(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			final L1PcInstance pc = client.getActiveChar();

			final int itemObjectId = this.readD();
			
			final L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

			// 物品為空
			if (item == null) {
				return;
			}
			if (item.getCount() <= 0) {
				return;
			}

			// 執行人物不是GM
			if (!pc.isGm()) {
				if (item.getItem().isCantDelete()) {
					// 125 \f1你不能夠放棄此樣物品。
					pc.sendPackets(new S_ServerMessage(125));
					return;
				}
			}

			// 寵物
			final Object[] petlist = pc.getPetList().values().toArray();
			for (final Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// 125 \f1你不能夠放棄此樣物品。
						pc.sendPackets(new S_ServerMessage(125));
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
				// 125 \f1你不能夠放棄此樣物品。
				pc.sendPackets(new S_ServerMessage(125));
				return;
			}
			if (item.getBless() >= 128) { // 封印的裝備
				// 210 \f1%0%d是不可轉移的…
				pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
				return;
			}
			_log.info("人物:" + pc.getName() + "刪除物品" + item.getItem().getName() + " 物品OBJID:" + item.getId());
			
		    // 玩家類別各項紀錄:刪除道具物品記錄
 			deleteitem("玩家" + ":【 " + pc.getName() + " 】 " + "把" + "【 + "
 					+ item.getEnchantLevel() + " " + item.getName() + "("
 					+ item.getCount() + ")" + " 】" + " 給刪除了," + "時間:" + "("
 					+ new Timestamp(System.currentTimeMillis()) + ")。");
            
			pc.getInventory().removeItem(item, item.getCount());
			pc.turnOnOffLight();
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}
 	// 記錄文件檔
 	public static void deleteitem(String info) {
 		try {
 			BufferedWriter out = new BufferedWriter(new FileWriter(
 					"日誌/刪除物品記錄.txt", true));
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
