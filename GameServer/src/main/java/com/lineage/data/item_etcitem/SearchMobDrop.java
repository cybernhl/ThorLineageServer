package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.item_etcitem.teleport.Move_Reel2;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class SearchMobDrop extends ItemExecutor {
    private SearchMobDrop() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new SearchMobDrop();
    }
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int spellsc_objid = data[0];
        final L1Object target = World.get().findObject(spellsc_objid);
        if (!(target instanceof L1MonsterInstance)) {
            return;
        }
        final L1MonsterInstance monster = (L1MonsterInstance) target;
        pc.setMonsterDropData(monster.getName(), String.valueOf(monster.getCurrentHp()));
        final String[] mobdrop_list = new String[100];
        final ArrayList<L1Drop> dropList = SetDrop.get_droplist().get(monster.getNpcId());
        if (dropList == null) {
            pc.setDropMonsterMaxIndex(0);
            pc.setMobdropIndex(0);
            pc.setMobDropList(mobdrop_list);
            return;
        }
        int index = 0;
        final ArrayList<L1DropMap> dropListMap =
                SetDrop.get_droplist_map().containsKey((int) pc.getMapId()) ?
                SetDrop.get_droplist_map().get((int) pc.getMapId()).get(0)
                : null;
        for (final L1Drop drop : dropList) {
            final L1Item tmp = ItemTable.get().getTemplate(drop.getItemid());
            if (tmp == null) {
                continue;
            }
            if (drop.getBless() > 0) {
                mobdrop_list[index] = tmp.getName() + "[祝福]";
            } else {
                mobdrop_list[index] = tmp.getName();
            }
            index++;
        }
        if (dropListMap != null) {
            for (final L1DropMap drop : dropListMap) {
                final L1Item tmp = ItemTable.get().getTemplate(drop.getItemid());
                if (tmp == null) {
                    continue;
                }
                mobdrop_list[index] = tmp.getName();
                index++;
            }
        }
        pc.setDropMonsterMaxIndex(index);
        pc.setMobdropIndex(0);
        pc.setMobDropList(mobdrop_list);
        pc.showMobDropPage();
    }
}
