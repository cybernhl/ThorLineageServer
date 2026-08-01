package com.custom.ability;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.utils.BinaryOutputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomWeaponAbility {
    public static final CustomWeaponAbility instance = new CustomWeaponAbility();
    public static CustomWeaponAbility getInstance() {
        return instance;
    }
    private final Map<Integer, AbilityData> data = new HashMap<>();
    public void load() {
        try(Connection con = DatabaseFactory.get().getConnection()) {
            try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `特殊_武器潛能設定`")) {
                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getInt("開關") > 0) {
                            this.data.put(rs.getInt("潛能編號"), new AbilityData(rs.getInt("潛能編號"), rs.getString("潛能名稱"), rs.getInt("潛能類型"), rs.getInt("潛能數值"), rs.getInt("潛能機率")));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    public final Map<Integer, AbilityData> getData() {
        return this.data;
    }
    public boolean canUseType(int useType) {
        switch (useType) {
            case 1: // 武器
                return true;
        }
        return false;
    }
}
