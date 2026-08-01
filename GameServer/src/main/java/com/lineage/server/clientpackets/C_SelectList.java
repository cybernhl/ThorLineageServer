package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.world.World;

/**
 * 要求物品維修、領取寵物 
 *
 * @author daien
 *
 */
public class C_SelectList extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_SelectList.class);

	public C_SelectList(final byte[] decrypt, final ClientExecutor client) {
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

	            final int itemObjectId = this.readD();
	            final int npcObjectId = this.readD();

	            if (npcObjectId != 0) { // 武器修理
	                final L1Object obj = World.get().findObject(npcObjectId);
	                if (obj != null) {
	                    if (obj instanceof L1NpcInstance) {
	                        final L1NpcInstance npc = (L1NpcInstance) obj;
	                        final int difflocx = Math.abs(pc.getX() - npc.getX());
	                        final int difflocy = Math.abs(pc.getY() - npc.getY());
	                        // 3格以上距離無效
	                        if ((difflocx > 3) || (difflocy > 3)) {
	                            return;
	                        }
	                    }
	                }

	                final L1PcInventory pcInventory = pc.getInventory();
	                final L1ItemInstance item = pcInventory.getItem(itemObjectId);
	                final int cost = item.get_durability() * 200;// 每一點損壞度200元
	                if (!pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
	                    // 189：\f1金幣不足。 。
	                    pc.sendPackets(new S_ServerMessage(189));
	                    return;
	                }

	                item.set_durability(0);
	                // 464：%0 現在變成像個新的一樣。
	                pc.sendPackets(new S_ServerMessage(464, item.getLogName()));
	                pcInventory.updateItem(item, L1PcInventory.COL_DURABILITY);

	            } else { // 寵物清單
	                pc.petReceive(itemObjectId);
	            }

	        } catch (final Exception e) {
	            _log.error(e.getLocalizedMessage(), e);
	        } finally {
	            this.over();
	        }
	    }

	    @Override
	    public String getType() {
	        return this.getClass().getSimpleName();
	    }
	}
