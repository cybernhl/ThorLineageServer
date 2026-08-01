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

public class PcDexDmg
{
	private static Logger _log = Logger.getLogger(PcDexDmg.class
			.getName());
	private static PcDexDmg _instance;
	private final HashMap<Integer, DexDmg> _itemIdIndex = new HashMap<Integer, DexDmg>();

	public static PcDexDmg getInstance() {
		if (_instance == null) {
			_instance = new PcDexDmg();
		}
		return _instance;
	}

	private PcDexDmg() {
		loadDexDmg();
	}

	private void loadDexDmg() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_Pc_dex_dmg");
			rs = pstm.executeQuery();
			fillItemSummon(rs);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating william_Pc_dex_dmg table", 
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillItemSummon(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int DexDmg = rs.getInt("Dex");
			int AddDmg = rs.getInt("AddDmg");
			int AddHit = rs.getInt("AddHit");

			DexDmg DexDmgSkill = new DexDmg(DexDmg, AddDmg, AddHit);
			this._itemIdIndex.put(Integer.valueOf(DexDmg), DexDmgSkill);
		}
	}

	public DexDmg getTemplate(int DexDmg) {
		return (DexDmg)this._itemIdIndex.get(Integer.valueOf(DexDmg));
	}
}

