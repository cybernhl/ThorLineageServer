package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.storage.EzpayStorage3;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 網站購物資料
 * 
 * @author dexc
 */
public class EzpayTable3 implements EzpayStorage3 {
	private static final Log _log = LogFactory.getLog(EzpayTable.class);

	/**
	 * 傳回指定帳戶購物資料
	 * 
	 * @param loginName 帳號名稱
	 * @return
	 */
	public Map<Integer, int[]> ezpayInfo(String loginName) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<Integer, int[]> list = new HashMap<Integer, int[]>();
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			String sqlstr = "SELECT * FROM `系統_即時發放獎勵` WHERE `指定發送玩家帳號`=? ORDER BY `流水號`";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			rs = ps.executeQuery();

			while (rs.next()) {
				int[] value = new int[3];
				int out = rs.getInt("是否已送出");
				int ready = rs.getInt("是否給予開放");
				if ((out == 0) && (ready == 1)) {
					int key = rs.getInt("流水號");
					int p_id = rs.getInt("商品道具編號");// ITEMID
					int count = rs.getInt("商品數量");// 數量
					value[0] = key;
					value[1] = p_id;
					value[2] = count;

					list.put(Integer.valueOf(key), value);
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return list;
	}

	/**
	 * 傳回指定帳戶購物資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id        流水號
	 * @return
	 */
	private boolean is_holding(String loginName, int id) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			String sqlstr = "SELECT * FROM `系統_即時發放獎勵` WHERE `指定發送玩家帳號`=? AND `流水號`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			ps.setInt(2, id);
			rs = ps.executeQuery();

			while (rs.next()) {
				int out = rs.getInt("是否已送出");
				if (out != 0) {
					return false;
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return true;
	}

	/**
	 * 更新資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id        ID
	 * @param pcname    領取人物
	 * @param ip        IP
	 * @return
	 */
	public boolean update(String loginName, int id, String pcname, String ip) {
		if (!is_holding(loginName, id)) {// 已經領回
			return false;
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			Timestamp lastactive = new Timestamp(System.currentTimeMillis());

			con = DatabaseFactoryLogin.get().getConnection();
			String sqlstr = "UPDATE `系統_即時發放獎勵` SET `是否已送出`=1,`領取人名稱`=?,`領取時間`=?,`領取人ip`=? WHERE `流水號`=? AND `指定發送玩家帳號`=?";
			pstm = con.prepareStatement(sqlstr);

			pstm.setString(1, pcname);// 領取人物
			pstm.setTimestamp(2, lastactive);// 時間
			pstm.setString(3, ip);// IP位置

			pstm.setInt(4, id);
			pstm.setString(5, loginName);// 帳號名稱

			pstm.execute();
			return true;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}
}
