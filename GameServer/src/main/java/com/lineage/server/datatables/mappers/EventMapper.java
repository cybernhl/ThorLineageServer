package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Event;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Event 模型轉換器
 */
public class EventMapper implements RowMapper<L1Event> {

    private static final EventMapper _instance = new EventMapper();

    public static EventMapper get() {
        return _instance;
    }

    @Override
    public L1Event mapRow(ResultSet rs) throws SQLException {
        final L1Event event = new L1Event();
        
        event.set_eventid(rs.getInt("eventid"));
        event.set_eventname(rs.getString("eventname"));
        event.set_eventclass(rs.getString("eventclass"));
        event.set_eventstart(rs.getInt("eventstart") != 0);
        event.set_eventother(rs.getString("eventother"));

        return event;
    }
}
