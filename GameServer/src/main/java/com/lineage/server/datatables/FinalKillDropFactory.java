package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FinalKillDropFactory {
    public static final FinalKillDropFactory instance = new FinalKillDropFactory();
    public static FinalKillDropFactory getInstance() {
        return instance;
    }
    private final Map<Integer, Integer> drop_data = new HashMap<>();
    public void load() {
        try(Connection con = DatabaseFactory.get().getConnection()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `系統_尾刀掉落系統`")) {
                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int monster_id = rs.getInt("monster_id");
                        final int item_id = rs.getInt("item_id");
                        this.drop_data.put(monster_id, item_id);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    public final boolean containsKey(final int monster_id) {
        return this.drop_data.containsKey(monster_id);
    }
    public final int get(final int monster_id) {
        return this.drop_data.get(monster_id);
    }
}
