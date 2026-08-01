package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.templates.L1Opcodes;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 服務器封包存放<br>
 * 類名稱：OpcodesTable<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午12:10:43<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class OpcodesTable {

	private static final Log _log = LogFactory.getLog(OpcodesTable.class);
	
	private List<L1Opcodes> _Opcodeslist = new ArrayList<L1Opcodes>();

	private static OpcodesTable _instance;
	
	public static OpcodesTable get() {
		if (_instance == null) {
			_instance = new OpcodesTable();
		}
		return _instance;
	}
	/**
	 * 預先加載
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_opcodes`");
			rs = pstm.executeQuery();
			
			while (rs.next()) {
				final L1Opcodes op = new L1Opcodes();
				op.set_Op_Type(rs.getString("op_type").equalsIgnoreCase("client") ? "client" : "server");
				op.set_Op_Classname(rs.getString("op_classname"));
				op.set_Op_Id(rs.getInt("op_id"));
				op.set_Op_Name(rs.getString("op_name"));
				op.set_Op_Broad(rs.getInt("op_broad"));
				
				_Opcodeslist.add(op);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
		//_log.info("載入封包存放數量: " + _Opcodeslist.size() + "(" + timer.get() + "ms)");
	}
	
	/**
	 * 依照封包ID傳回對應名稱
	 * @param OpId
	 * @param type
	 * @return
	 */
	public String getOpName(int OpId, String type) {
		for (L1Opcodes op : _Opcodeslist) {
			if (op.get_Op_Id() == OpId && type == op.get_Op_Type()) {
				return op.get_Op_Name();
			}
		}
		return "未知的名稱";
	}
	
	/**
	 * 依照封包ID傳回對應ClassName
	 * @param OpId
	 * @param type
	 * @return
	 */
	public String getOpClassName(int OpId, String type) {
		for (L1Opcodes op : _Opcodeslist) {
			if (op.get_Op_Id() == OpId && type == op.get_Op_Type()) {
				return op.get_Op_Classname();
			}
		}
		return "未知的ClassName";
	}
	
	/**
	 * 是否打印此封包<br>
	 * 0:不打印<br>
	 * 1:打印<br>
	 * @param OpId
	 * @param type
	 * @return
	 */
	public int getOpBroad(int OpId, String type) {
		for (L1Opcodes op : _Opcodeslist) {
			if (op.get_Op_Id() == OpId && type == op.get_Op_Type()) {
				return op.get_Op_Broad();
			}
		}
		return 1;
	}
}
