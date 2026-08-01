package com.lineage.data.item_etcitem.hole;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomHoleGemDataByArmor {
    public static final CustomHoleGemDataByArmor instance = new CustomHoleGemDataByArmor();
    public static CustomHoleGemDataByArmor getInstance() {
        return instance;
    }
    private final Map<Integer, List<GemData>> data = new HashMap<>();
    private final Map<Integer, GemData> index_data = new HashMap<>();
    public void loadAll() {
        try(Connection con = DatabaseFactory.get().getConnection()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `custom_gem_hole_armor_data`")) {
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
                    pc.addAc(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW防禦減少：" + gem.getValue()));
                } else {
                    pc.addAc(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW防禦增加：" + gem.getValue()));
                }
                break;
            case 1:
                if (remove) {
                    pc.addMpr(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW回魔減少：" + gem.getValue()));
                } else {
                    pc.addMpr(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW回魔增加：" + gem.getValue()));
                }
                break;
            case 2:
                if (remove) {
                    pc.addHpr(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW回血減少：" + gem.getValue()));
                } else {
                    pc.addHpr(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW回血增加：" + gem.getValue()));
                }
                break;
            case 3:
                if (remove) {
                    pc.addMr(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW抗魔減少：" + gem.getValue()));
                } else {
                    pc.addMr(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW抗魔增加：" + gem.getValue()));
                }
                pc.sendPackets(new S_SPMR(pc));
                break;
            case 4:
                if (remove) {
                    pc.addMaxMp(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW魔力減少：" + gem.getValue()));
                } else {
                    pc.addMaxMp(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW魔力增加：" + gem.getValue()));
                }
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                break;
            case 5:
                if (remove) {
                    pc.addMaxMp(-gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW體力減少：" + gem.getValue()));
                } else {
                    pc.addMaxMp(gem.getValue());
                    pc.sendPackets(new S_SystemMessage("\\fW體力增加：" + gem.getValue()));
                }
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                if (pc.isInParty()) { // 隊伍狀態
                    pc.getParty().updateMiniHP(pc);
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
