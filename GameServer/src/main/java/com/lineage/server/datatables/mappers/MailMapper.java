package com.lineage.server.datatables.mappers;

import com.lineage.server.templates.L1Mail;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * L1Mail 模型轉換器
 */
public class MailMapper implements RowMapper<L1Mail> {

    private static final MailMapper _instance = new MailMapper();

    public static MailMapper get() {
        return _instance;
    }

    @Override
    public L1Mail mapRow(ResultSet rs) throws SQLException {
        final L1Mail mail = new L1Mail();
        
        mail.setId(rs.getInt("id"));
        mail.setType(rs.getInt("type"));
        mail.setSenderName(rs.getString("sender"));
        mail.setReceiverName(rs.getString("receiver"));
        mail.setDate(rs.getString("date"));
        mail.setReadStatus(rs.getInt("read_status"));
        
        final byte[] subject = rs.getBytes("subject");
        if (subject != null) {
            mail.setSubject(subject);
        }
        
        final byte[] content = rs.getBytes("content");
        if (content != null) {
            mail.setContent(content);
        }

        return mail;
    }
}
