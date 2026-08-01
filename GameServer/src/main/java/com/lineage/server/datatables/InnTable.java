/**
 * License THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). THE WORK IS PROTECTED
 * BY COPYRIGHT AND/OR OTHER APPLICABLE LAW. ANY USE OF THE WORK OTHER THAN AS
 * AUTHORIZED UNDER THIS LICENSE OR COPYRIGHT LAW IS PROHIBITED. BY EXERCISING
 * ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND AGREE TO BE BOUND BY THE
 * TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE MAY BE CONSIDERED TO BE A
 * CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED HERE IN CONSIDERATION
 * OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InnTable {

	private static Logger _log = Logger.getLogger(InnTable.class.getName());

	private static final Log _logx = LogFactory.getLog(InnTable.class);

	private static class Inn {
		private final Map<Integer, L1Inn> _inn = new HashMap<Integer, L1Inn>();
	}

	private static final Map<Integer, Inn> _dataMap = new HashMap<Integer, Inn>();

	private static InnTable _instance;

	public static InnTable getInstance() {
		if (_instance == null) {
			_instance = new InnTable();
		}
		return _instance;
	}

	private InnTable() {
		load();
	}

	private void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Inn inn = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM inn");

			rs = pstm.executeQuery();
			L1Inn l1inn;
			int roomNumber;
			while (rs.next()) {
				int key = rs.getInt("npcid");
				if (!_dataMap.containsKey(key)) {
					inn = new Inn();
					_dataMap.put(key, inn);
				} else {
					inn = _dataMap.get(key);
				}

				l1inn = new L1Inn();
				l1inn.setInnNpcId(rs.getInt("npcid"));
				roomNumber = rs.getInt("room_number");
				l1inn.setRoomNumber(roomNumber);
				l1inn.setKeyId(rs.getInt("key_id"));
				l1inn.setLodgerId(rs.getInt("lodger_id"));
				l1inn.setHall(rs.getBoolean("hall"));
				l1inn.setDueTime(rs.getTimestamp("due_time"));

				inn._inn.put(Integer.valueOf(roomNumber), l1inn);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		_logx.info("載入旅館鑰匙資料數量: " + inn._inn.size() + "(" + timer.get() + "ms)");
	}

	public void updateInn(L1Inn inn) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con
					.prepareStatement(
							"UPDATE inn SET key_id=?,lodger_id=?,hall=?,due_time=? WHERE npcid=? and room_number=?");

			pstm.setInt(1, inn.getKeyId());
			pstm.setInt(2, inn.getLodgerId());
			pstm.setBoolean(3, inn.isHall());
			pstm.setTimestamp(4, inn.getDueTime());
			pstm.setInt(5, inn.getInnNpcId());
			pstm.setInt(6, inn.getRoomNumber());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1Inn getTemplate(int npcid, int roomNumber) {
		if (_dataMap.containsKey(npcid)) {
			return _dataMap.get(npcid)._inn.get(roomNumber);
		}
		return null;
	}
}
