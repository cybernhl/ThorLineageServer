package com.lineage.data.item_etcitem.hole;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class CustomHoleGemData {
    public static final CustomHoleGemData instance = new CustomHoleGemData();
    public static CustomHoleGemData getInstance() {
        return instance;
    }
    private final Map<Integer, List<GemData>> data = new HashMap<>();
    private final Map<Integer, GemData> index_data = new HashMap<>();
    public void loadAll() {
        try(Connection con = DatabaseFactory.get().getConnection()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `custom_gem_hole_data`")) {
                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int id = rs.getInt("id");
                        final int item_id = rs.getInt("item_id");
                        final String name = rs.getString("name");
                        final int chance = rs.getInt("chance");
                        final int type = rs.getInt("type");
                        final int value = rs.getInt("value");
                        final int out = rs.getInt("out");
                        if (!this.data.containsKey(item_id)) {
                            this.data.put(item_id, new ArrayList<>());
                        }
                        final GemData gem = new GemData(name, chance, type, value, id, out);
                        this.data.get(item_id).add(gem);
                        this.index_data.put(id, gem);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    public final boolean containsKey(final int itemId) {
        return this.data.containsKey(itemId);
    }
    public final boolean containsKeyByIndex(final int index) {
        return this.index_data.containsKey(index);
    }
    public final List<GemData> getGemData(final int itemId) {
        return this.data.get(itemId);
    }

    public final GemData getGemDataByIndex(final int index) {
        return this.index_data.get(index);
    }
    public final void resetPcStat(final L1PcInstance pc, final int index, boolean remove) {
        if (!this.index_data.containsKey(index)) {
            return;
        }
        final GemData gem = this.index_data.get(index);
        switch (gem.getType()) {
            case 0:
                if (remove) {
                    pc.addDmgup(-gem.getValue());
                    pc.addBowDmgup(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW額外傷害減少：" + gem.getValue()));
                } else {
                    pc.addDmgup(gem.getValue());
                    pc.addBowDmgup(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW額外傷害增加：" + gem.getValue()));
                }
                break;
            case 1:
                if (remove) {
                    pc.addChit(-((double) (gem.getValue()) / 100.0));
                    pc.sendPackets(new S_SystemMessage("\\fW暴擊率減少：" + ((double) (gem.getValue()) / 100.0) + "%"));
                } else {
                    pc.addChit((double) (gem.getValue()) / 100.0);
                    pc.sendPackets(new S_SystemMessage("\\fW暴擊率增加：" + ((double) (gem.getValue()) / 100.0) + "%"));
                }
                break;
            case 2:
                if (remove) {
                    pc.addSp(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW魔法攻擊減少：" + gem.getValue()));
                } else {
                    pc.addSp(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW魔法攻擊增加：" + gem.getValue()));
                }
                break;
            case 3:
                if (remove) {
                    pc.addPVPDmg(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fWPVP傷害減少：" + gem.getValue()));
                } else {
                    pc.addPVPDmg(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fWPVP傷害增加：" + gem.getValue()));
                }
                break;
            case 4:
                if (remove) {
                    pc.addBossDmg(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fWBOSS傷害減少：" + gem.getValue()));
                } else {
                    pc.addBossDmg(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fWBOSS傷害增加：" + gem.getValue()));
                }
                break;
        }
    }
    public static class GemData {
        private final String name;
        private final int chance, type, value, index, out;
        public GemData(final String name, final int chance, final int type, final int value, final int index, final int out) {
            this.name = name;
            this.chance = chance;
            this.type = type;
            this.value = value;
            this.index = index;
            this.out = out;
        }
        public final String getName() {
            return this.name;
        }
        public final int getChance() {
            return this.chance;
        }
        public final int getType() {
            return this.type;
        }
        public final int getValue() {
            return this.value;
        }
        public final int getIndex() {
            return this.index;
        }
        public final boolean isOut() {
            return this.out > 0;
        }
    }
}
