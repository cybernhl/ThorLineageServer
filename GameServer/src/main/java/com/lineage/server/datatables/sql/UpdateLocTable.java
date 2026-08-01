package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.lineage.DatabaseFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.storage.UpdateLocStorage;
import com.lineage.server.utils.SQLUtil;

public class UpdateLocTable implements UpdateLocStorage {

	private static final Log _log = LogFactory.getLog(UpdateLocTable.class);

	/**
	 * 解卡點
	 */
	@Override
	public void setPcLoc(final String accName) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE `characters` SET `LocX`=32781,`LocY`=32856,`MapID`=340 WHERE `account_name`=?");
			pstm.setString(1, accName);
			pstm.execute();
			pstm.close();
			con.close();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	/**
	 * 禁閉室
	 */
	// @Override
	public void setPcLoc(final int pcid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement("UPDATE `characters` SET `LocX`=33705,`LocY`=32504,`MapID`=4 WHERE `objid`=?");
			pstm.setInt(1, pcid);
			pstm.execute();
			pstm.close();
			con.close();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
