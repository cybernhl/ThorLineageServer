package com.william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PcStrDmg
{
	private static Logger _log = Logger.getLogger(PcStrDmg.class
			.getName());
	private static PcStrDmg _instance;
	private final HashMap<Integer, StrDmg> _itemIdIndex = new HashMap<Integer, StrDmg>();

	public static PcStrDmg getInstance() {
		if (_instance == null) {
			_instance = new PcStrDmg();
		}
		return _instance;
	}

	private PcStrDmg() {
		loadStrDmg();
	}

	private void loadStrDmg() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_Pc_str_dmg");
			rs = pstm.executeQuery();
			fillItemSummon(rs);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating william_Pc_str_dmg table", 
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillItemSummon(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int StrDmg = rs.getInt("Str");
			int AddDmg = rs.getInt("AddDmg");
			int AddHit = rs.getInt("AddHit");

			StrDmg StrDmgSkill = new StrDmg(StrDmg, AddDmg, AddHit);
			this._itemIdIndex.put(Integer.valueOf(StrDmg), StrDmgSkill);
		}
	}

	public StrDmg getTemplate(int StrDmg) {
		return (StrDmg)this._itemIdIndex.get(Integer.valueOf(StrDmg));
	}
}

