package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Board;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Board 模型轉換器
 */
public class BoardMapper implements RowMapper<L1Board> {

    private static final BoardMapper _instance = new BoardMapper();

    public static BoardMapper get() {
        return _instance;
    }

    @Override
    public L1Board mapRow(ResultSet rs) throws SQLException {
        final L1Board board = new L1Board();
        
        board.set_id(rs.getInt("id"));
        board.set_name(rs.getString("name"));
        board.set_date(rs.getString("date"));
        board.set_title(rs.getString("title"));
        board.set_content(rs.getString("content"));

        return board;
    }
}
