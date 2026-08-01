package com.lineage.data.npc.custom;

import com.custom.clan.ClanSpecialStatFactory;
import com.custom.clan.ClanStatData;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Clan_Level extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Clan_Level.class);
    public static NpcExecutor get() {
        return new Clan_Level();
    }
    @Override
    public int type() {
        return 3;
    }
    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        final ClanStatData stat = ClanSpecialStatFactory.getInstance().getStat(pc.getClan() == null ? 1 : pc.getClan().getLevel());
        final List<String> statNames = new ArrayList<>();
        if (stat.getDmgup() > 0) {
            statNames.add("近距離攻擊力+" + stat.getDmgup());
        }
        if (stat.getBowDmgup() > 0) {
            statNames.add("遠距離攻擊力+" + stat.getBowDmgup());
        }
        if (stat.getHit() > 0) {
            statNames.add("近距離命中率+" + stat.getHit());
        }
        if (stat.getBowHit() > 0) {
            statNames.add("遠距離命中率+" + stat.getBowHit());
        }
        if (stat.getAc() > 0) {
            statNames.add("防禦-" + stat.getAc());
        }
        if (stat.getMr() > 0) {
            statNames.add("抗魔+" + stat.getMr());
        }
        if (stat.getHp() > 0) {
            statNames.add("血量上限+" + stat.getHp());
        }
        if (stat.getMp() > 0) {
            statNames.add("魔力上限+" + stat.getMp());
        }
        if (stat.getSp() > 0) {
            statNames.add("魔攻+" + stat.getSp());
        }
        if (stat.getStr() > 0) {
            statNames.add("力量+" + stat.getStr());
        }
        if (stat.getDex() > 0) {
            statNames.add("敏捷+" + stat.getDex());
        }
        if (stat.getCon() > 0) {
            statNames.add("體力+" + stat.getCon());
        }
        if (stat.getWis() > 0) {
            statNames.add("精神+" + stat.getWis());
        }
        if (stat.getInt() > 0) {
            statNames.add("智力+" + stat.getInt());
        }
        if (stat.getCha() > 0) {
            statNames.add("魅力+" + stat.getCha());
        }
        statNames.add("打寶率+" + stat.getLevel() + "%");
        statNames.add("經驗率+" + stat.getAddExp() + "%");
        while (statNames.size() < 19) {
            statNames.add("");
        }
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "clan_spel_1",
                new String[]{
                        pc.getClan() == null ? "無" : pc.getClan().getClanName(),
                        pc.getClan() == null ? "1" : String.valueOf(pc.getClan().getLevel()),
                        statNames.get(0),
                        statNames.get(1),
                        statNames.get(2),
                        statNames.get(3),
                        statNames.get(4),
                        statNames.get(5),
                        statNames.get(6),
                        statNames.get(7),
                        statNames.get(8),
                        statNames.get(9),
                        statNames.get(10),
                        statNames.get(11),
                        statNames.get(12),
                        statNames.get(13),
                        statNames.get(14),
                        statNames.get(15),
                        statNames.get(16),
                        statNames.get(17),
                        statNames.get(18),
                        String.valueOf(stat.getNeedCount())
                }));
    }
    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        if (cmd.equalsIgnoreCase("clan_spel_2")) {
            final ClanStatData stat = ClanSpecialStatFactory.getInstance().getStat(pc.getClan() == null ? 1 : pc.getClan().getLevel());
            final List<String> neeed = new ArrayList<>();
            for (final Pair<Integer, Integer> need : stat.getNeedItems()) {
                final L1Item item = ItemTable.get().getTemplate(need.getKey());
                if (item == null) {
                    continue;
                }
                neeed.add(item.getName() + " x " + String.format("%,d", need.getValue()) + " 個");
            }
            while (neeed.size() < 10) {
                neeed.add("");
            }
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "clan_spel_2", neeed.toArray(new String[neeed.size()])));
        } else if (cmd.equalsIgnoreCase("updateclanlevel")) {
            pc.sendPackets(new S_CloseList(pc.getId()));
            if (pc.getClan() == null) {
                pc.sendPackets(new S_SystemMessage("您沒有血盟，無法升級"));
                return;
            }
            if (pc.getClan().getLeaderId() != pc.getId()) {
                pc.sendPackets(new S_SystemMessage("只有血盟盟主可以操作這個功能。"));
                return;
            }
            if (ClanSpecialStatFactory.getInstance().getStat(pc.getClan().getLevel() + 1) == null) {
                pc.sendPackets(new S_SystemMessage("您的血盟已經滿級！"));
                return;
            }
            final ClanStatData stat = ClanSpecialStatFactory.getInstance().getStat(pc.getClan().getLevel());
            for (final Pair<Integer, Integer> need : stat.getNeedItems()) {
                final L1Item item = ItemTable.get().getTemplate(need.getKey());
                if (item == null) {
                    pc.sendPackets(new S_SystemMessage("升級需求道具設定錯誤，請聯繫GM"));
                    return;
                }
                if (pc.getInventory().checkItemX(need.getKey(), need.getValue()) == null) {
                    pc.sendPackets(new S_SystemMessage(item.getName() + " x " + String.format("%,d", need.getValue()) + " 個不足"));
                    return;
                }
            }
            for (final Pair<Integer, Integer> need : stat.getNeedItems()) {
                pc.getInventory().consumeItem(need.getKey(), need.getValue());
            }
            pc.getClan().setLevel(pc.getClan().getLevel() + 1);
            ClanReading.get().updateClan(pc.getClan());
            ClanSpecialStatFactory.getInstance().clanStatSet(pc.getClan());
            World.get().broadcastPacketToAll(new S_ServerMessage("\\fU血盟【" + pc.getClanname() + "】成功將等級提升至 " + pc.getClan().getLevel() + " 級！！"));
        }
    }
}
