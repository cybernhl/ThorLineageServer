package com.lineage.mina;

import com.lineage.DatabaseFactory;
import com.lineage.data.item_etcitem.hole.CustomHoleGemData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomSafeConnectIP {
    public static final CustomSafeConnectIP instance = new CustomSafeConnectIP();

    public static CustomSafeConnectIP getInstance() {
        return instance;
    }

    private final Set<String> ip_data = new HashSet<>();
    private final Lock lock = new ReentrantLock(false);

    public void loadAll(boolean reload) {
        this.lock.lock();
        try {
            if (reload) {
                this.ip_data.clear();
            }
            try(Connection con = DatabaseFactory.get().getConnection()) {
                try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `custom_safe_connect_ips`")) {
                    try(ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            this.ip_data.add(rs.getString("ip"));
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        } finally {
            this.lock.unlock();
        }
    }
    public final boolean containsKey(final String ip) {
        this.lock.lock();
        try {
            return this.ip_data.contains(ip);
        } finally {
            this.lock.unlock();
        }
    }
}
