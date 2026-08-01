package com.lineage.data.item_etcitem.doll;

import java.util.Iterator;
import java.util.List;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.WorldWar;

/**
 * 魔法娃娃
 */
public class Magic_Doll extends ItemExecutor {

    /**
     *
     */
    private Magic_Doll() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Magic_Doll();
    }

    /**
     * 道具物件執行
     * 
     * @param data
     *            參數
     * @param pc
     *            執行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemId = item.getItemId();
        final int itemobj = item.getId();
        this.useMagicDoll(pc, itemId, itemobj);
    }

    private void useMagicDoll(final L1PcInstance pc, final int itemId, final int itemObjectId) {
        if (pc.getDoll(itemObjectId) != null) {
            // 娃娃收回
            pc.getDoll(itemObjectId).deleteDoll();
            return;
        }

        if (pc.getDolls().size() >= ConfigAlt.MAX_DOLL_COUNT) {
            pc.sendPackets(new S_ServerMessage(319));
            return;
        }

       /* if (!pc.getDolls().isEmpty()) {
            for (final Iterator<L1DollInstance> iter = pc.getDolls().values().iterator(); iter.hasNext();) {
                final L1DollInstance doll = iter.next();
                // for (L1DollInstance doll : pc.getDolls().values()) {
                final int itemid2 =	pc.getInventory().getItem(doll.getItemObjId()).getItemId();
                if (itemid2 == itemId) {
                    pc.sendPackets(new S_ServerMessage("\\fY不能攜帶相同的娃娃"));
                    return;
                }
            }
        }*/
      //新加入
      		if (!pc.getDolls().isEmpty()) {
      			for (final Iterator<L1DollInstance> iter = pc.getDolls().values().iterator(); iter.hasNext();) {
      				final L1DollInstance doll = iter.next();
      			final int itemid2 =	pc.getInventory().getItem(doll.getItemObjId()).getItemId();
      			//for (L1DollInstance doll : pc.getDolls().values()) {
      				if (itemid2 == itemId) {
      					pc.sendPackets(new S_ServerMessage("\\fY不能攜帶相同的娃娃"));
      					return;
      				}
      			    L1Doll Dolltype = DollPowerTable.get().get_type(itemId);
      			    L1Doll Dolltype2 = DollPowerTable.get().get_type(itemid2);
      			    if((Dolltype.get_type() == Dolltype2.get_type())&& (Dolltype.get_type()>0)&&(Dolltype2.get_type()>0) )	{
      				pc.sendPackets(new S_ServerMessage("\\fY不能攜帶相同類型的娃娃"));
      					return;

      			   }
      			}
      		}
        if (!ConfigOther.WAR_DOLL) {
            // 戰爭
            if (pc.getClan() != null) {
                boolean inWar = false;
                if (pc.getClan().getCastleId() != 0) {
                    if (ServerWarExecutor.get().isNowWar(pc.getClan().getCastleId())) { // 戰爭時間內
                        inWar = true;
                    }

                } else {
                    final List<L1War> warList = WorldWar.get().getWarList(); // 全部戰爭清單
                    for (final Iterator<L1War> iter = warList.iterator(); iter.hasNext();) {
                        final L1War war = iter.next();
                        if (war.checkClanInWar(pc.getClan().getClanName())) { // 戰爭中
                            inWar = true;
                            break;
                        }
                    }
                }

                if (inWar) {
                    // 1531：加入血盟中或戰鬥中時，無法進行召喚魔法娃娃。
                    pc.sendPackets(new S_ServerMessage(1531));
                    return;
                }
            }
        }

        boolean iserror = false;
        final L1Doll type = DollPowerTable.get().get_type(itemId);
        if (type != null) {
            if (type.get_need() != null) {
                final int[] itemids = type.get_need();
                final int[] counts = type.get_counts();

                for (int i = 0; i < itemids.length; i++) {
                    if (!pc.getInventory().checkItem(itemids[i], counts[i])) {
                        final L1Item temp = ItemTable.get().getTemplate(itemids[i]);
                        pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
                        iserror = true;
                    }
                }

                if (!iserror) {
                    for (int i = 0; i < itemids.length; i++) {
                        pc.getInventory().consumeItem(itemids[i], counts[i]);
                    }
                }
            }
            final L1Npc template = NpcTable.get().getTemplate(71082);
            if (!pc.getDolls().isEmpty()) {
            	for (L1DollInstance doll:pc.getDolls().values())
            	{
            		if(doll.getType().equals(type))
            		{
            			pc.sendPackets(new S_ServerMessage("\\fY不能攜帶相同的娃娃"));
            			iserror = true;
            		}
            	}
            }
            if (!iserror) {
                
                /* L1DollInstance doll = */new L1DollInstance(template, pc, itemObjectId, type);
            }
        }
    }
}
