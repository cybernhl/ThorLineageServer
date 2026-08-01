package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

/**
 * 信件資料
 *
 * @author dexc
 *
 */
public class LetterTable {

	private static final Log _log = LogFactory.getLog(LetterTable.class);

	private static LetterTable _instance;

	public LetterTable() {
	}

	public static LetterTable getInstance() {
		if (_instance == null) {
			_instance = new LetterTable();
		}
		return _instance;
	}

	// ID一覽
	// 16:存在
	// 32:荷物多
	// 48:血盟存在
	// 64:※內容表示(白字)
	// 80:※內容表示(黑字)
	// 96:※內容表示(黑字)
	// 112:。%n參加競賣最終價格%0價格落札。
	// 128:提示金額高金額提示方現、殘念入札失敗。
	// 144:參加競賣成功、現在家所有狀態。
	// 160:所有家最終價格%1落札。
	// 176:申請競賣、競賣期間內提示金額以上支拂表明方現、結局取消。
	// 192:申請競賣、競賣期間內提示金額以上支拂表明方現、結局取消。
	// 208:血盟所有家、本領主領地歸屬、今後利用當方稅金收。
	// 224:、家課稅金%0納。
	// 240:、結局家課稅金%0納、警告家對所有權剝奪。

	public void writeLetter(final int itemObjectId, final int code, final String sender,
			final String receiver, final String date, final int templateId, final byte[] subject,
			final byte[] content) {

		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm1 = con.prepareStatement(
					"SELECT * FROM `character_letter` ORDER BY `item_object_id`");
			rs = pstm1.executeQuery();
			pstm2 = con.prepareStatement(
					"INSERT INTO `character_letter` SET `item_object_id`=?," +
					"`code`=?,`sender`=?,`receiver`=?,`date`=?,`template_id`=?," +
					"`subject`=?,`content`=?");
			pstm2.setInt(1, itemObjectId);
			pstm2.setInt(2, code);
			pstm2.setString(3, sender);
			pstm2.setString(4, receiver);
			pstm2.setString(5, date);
			pstm2.setInt(6, templateId);
			pstm2.setBytes(7, subject);
			pstm2.setBytes(8, content);
			pstm2.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public void deleteLetter(final int itemObjectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM `character_letter` WHERE `item_object_id`=?");
			pstm.setInt(1, itemObjectId);
			pstm.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

}
