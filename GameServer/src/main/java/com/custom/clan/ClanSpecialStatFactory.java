package com.custom.clan;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigOther;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ClanSpecialStatFactory {
    public static final ClanSpecialStatFactory instnace = new ClanSpecialStatFactory();
    public static ClanSpecialStatFactory getInstance() {
        return instnace;
    }
    private final Map<Integer, ClanStatData> data = new HashMap<>();
    public final ClanStatData getStat(final int level) {
        return this.data.get(level);
    }
    public void load() {
        try(Connection con = DatabaseFactory.get().getConnection()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `特殊_血盟等級能力`")) {
                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int level = rs.getInt("血盟等級");
                        final int need_count = rs.getInt("人數限制");
                        final int dmgup = rs.getInt("近距離攻擊力");
                        final int bow_dmgup = rs.getInt("遠距離攻擊力");
                        final int hit = rs.getInt("近距離命中率");
                        final int bow_hit = rs.getInt("遠距離命中率");
                        final int ac = rs.getInt("防禦");
                        final int mr = rs.getInt("抗魔");
                        final int hp = rs.getInt("血量");
                        final int mp = rs.getInt("魔力");
                        final int sp = rs.getInt("魔法攻擊");
                        final int str = rs.getInt("力量");
                        final int dex = rs.getInt("敏捷");
                        final int _con = rs.getInt("體力");
                        final int wis = rs.getInt("精神");
                        final int _int = rs.getInt("智力");
                        final int cha = rs.getInt("魅力");
                        final int add_exp = rs.getInt("經驗");
                        final String[] need_item_id = (rs.getString("升級消耗道具,隔開") == null || rs.getString("升級消耗道具,隔開").isEmpty()) ? new String[] {} : rs.getString("升級消耗道具,隔開").split(",");
                        final String[] need_item_count = (rs.getString("升級消耗道具數量,隔開") == null || rs.getString("升級消耗道具數量,隔開").isEmpty()) ? new String[] {} : rs.getString("升級消耗道具數量,隔開").split(",");
                        this.data.put(level, new ClanStatData(level, need_count, dmgup, bow_dmgup, hit, bow_hit, ac, mr, hp, mp, sp, str, dex, _con, wis, _int, cha, need_item_id, need_item_count, add_exp));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    public void clanStatSet(final L1Clan clan) {
        if (clan != null) {
            int clanOnlineCount = clan.getAllMembersSize();
            final L1PcInstance[] clanPlayers = clan.getOnlineClanMember();
//            for (final L1PcInstance clan_pc : clanPlayers) {
//                if (clan_pc.getLevel() >= ConfigOther.CLAN_STAT_LEVEL) {
//                    clanOnlineCount++;
//                }
//            }
            if (clanOnlineCount >= ConfigOther.CLAN_STAT_LEVEL_COUNT) {
                for (final L1PcInstance clan_pc : clanPlayers) {
                    ClanSpecialStatFactory.getInstance().set(clan_pc);
                }
            } else {
                for (final L1PcInstance clan_pc : clanPlayers) {
                    ClanSpecialStatFactory.getInstance().remove(clan_pc);
                }
            }
        }
    }
    public void remove(final L1PcInstance pc) {
        if (pc == null) {
            return;
        }
        if (pc.getClanStatData() == null) {
            return;
        }
        final ClanStatData stat = pc.getClanStatData();
        pc.setClanStatData(null);
        if (stat.getDmgup() > 0) {
            pc.addDmgup(-stat.getDmgup());
        }
        if (stat.getBowDmgup() > 0) {
            pc.addBowDmgup(-stat.getBowDmgup());
        }
        if (stat.getHit() > 0) {
            pc.addHitup(-stat.getHit());
        }
        if (stat.getBowHit() > 0) {
            pc.addBowHitup(-stat.getBowHit());
        }
        if (stat.getAc() < 0) {
            pc.addAc(-stat.getAc());
        }
        if (stat.getMr() > 0) {
            pc.addMr(-stat.getMr());
        }
        if (stat.getHp() > 0) {
            pc.addMaxHp(-stat.getHp());
        }
        if (stat.getMp() > 0) {
            pc.addMaxMp(-stat.getMp());
        }
        if (stat.getSp() > 0) {
            pc.addSp(-stat.getSp());
        }
        if (stat.getStr() > 0) {
            pc.addStr(-stat.getStr());
        }
        if (stat.getDex() > 0) {
            pc.addDex(-stat.getDex());
        }
        if (stat.getCon() > 0) {
            pc.addCon(-stat.getCon());
        }
        if (stat.getWis() > 0) {
            pc.addWis(-stat.getWis());
        }
        if (stat.getInt() > 0) {
            pc.addInt(-stat.getInt());
        }
        if (stat.getCha() > 0) {
            pc.addCha(-stat.getCha());
        }
        if (stat.getAddExp() > 0) {
            pc.set_expadd(-stat.getAddExp());
        }
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        if (pc.isInParty()) { // 隊伍狀態
            pc.getParty().updateMiniHP(pc);
        }
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    }
    public void set(final L1PcInstance pc) {
        if (pc == null) {
            return;
        }
        remove(pc);
        if (pc.getClan() == null) {
            return;
        }
        final ClanStatData stat = this.data.get(pc.getClan().getLevel());
        if (stat == null) {
            return;
        }
        if (pc.getClan().getAllMembers().length < stat.getNeedCount()) {
            return;
        }
        pc.setClanStatData(stat);
        if (stat.getDmgup() > 0) {
            pc.addDmgup(stat.getDmgup());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-近距離攻擊力+" + stat.getDmgup()));
        }
        if (stat.getBowDmgup() > 0) {
            pc.addBowDmgup(stat.getBowDmgup());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-遠距離攻擊力+" + stat.getBowDmgup()));
        }
        if (stat.getHit() > 0) {
            pc.addHitup(stat.getHit());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-近距離命中率+" + stat.getHit()));
        }
        if (stat.getBowHit() > 0) {
            pc.addBowHitup(stat.getBowHit());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-遠距離命中率+" + stat.getBowHit()));
        }
        if (stat.getAc() < 0) {
            pc.addAc(stat.getAc());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-防禦-" + stat.getAc()));
        }
        if (stat.getMr() > 0) {
            pc.addMr(stat.getMr());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-抗魔+" + stat.getMr()));
        }
        if (stat.getHp() > 0) {
            pc.addMaxHp(stat.getHp());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-血量+" + stat.getHp()));
        }
        if (stat.getMp() > 0) {
            pc.addMaxMp(stat.getMp());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-魔力+" + stat.getMp()));
        }
        if (stat.getSp() > 0) {
            pc.addSp(stat.getSp());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-魔攻+" + stat.getSp()));
        }
        if (stat.getStr() > 0) {
            pc.addStr(stat.getStr());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-力量+" + stat.getStr()));
        }
        if (stat.getDex() > 0) {
            pc.addDex(stat.getDex());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-敏捷+" + stat.getDex()));
        }
        if (stat.getCon() > 0) {
            pc.addCon(stat.getCon());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-體力+" + stat.getCon()));
        }
        if (stat.getWis() > 0) {
            pc.addWis(stat.getWis());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-精神+" + stat.getWis()));
        }
        if (stat.getInt() > 0) {
            pc.addInt(stat.getInt());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-智力+" + stat.getInt()));
        }
        if (stat.getCha() > 0) {
            pc.addCha(stat.getCha());
//            pc.sendPackets(new S_ServerMessage("\\fX血盟等級-魅力+" + stat.getCha()));
        }
        if (stat.getAddExp() > 0) {
            pc.set_expadd(stat.getAddExp());
        }
//        pc.sendPackets(new S_ServerMessage("\\fX血盟等級-打寶率+" + stat.getLevel() + "%"));
//        pc.sendPackets(new S_ServerMessage("\\fX血盟等級-經驗率+" + (stat.getLevel() * 5) + "%"));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        if (pc.isInParty()) { // 隊伍狀態
            pc.getParty().updateMiniHP(pc);
        }
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
    }
}
