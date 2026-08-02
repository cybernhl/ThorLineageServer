package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Quest;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Quest 模型轉換器
 */
public class QuestMapper implements RowMapper<L1Quest> {

    private static final QuestMapper _instance = new QuestMapper();

    public static QuestMapper get() {
        return _instance;
    }

    @Override
    public L1Quest mapRow(ResultSet rs) throws SQLException {
        final L1Quest quest = new L1Quest();
        
        quest.set_id(rs.getInt("id"));
        quest.set_questname(rs.getString("questname"));
        quest.set_questclass(rs.getString("questclass"));
        quest.set_queststart(rs.getInt("queststart") != 0);
        quest.set_del(rs.getInt("del") != 0);
        quest.set_questlevel(rs.getInt("questlevel"));
        quest.set_difficulty(rs.getInt("difficulty"));
        quest.set_note(rs.getString("note"));
        
        // 處理職業權限欄位 (根據 L1Quest.java 的 set_questuser 邏輯)
        try {
            quest.set_questuser(rs.getInt("questuser"));
        } catch (SQLException e) {
            // 忽略缺失欄位
        }

        return quest;
    }
}
