package com.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 裝備強化能力系統
 */
public class EnchantOrginal {

	private static Logger _log = Logger.getLogger(EnchantOrginal.class
			.getName());

	private static final Log _logx = LogFactory.getLog(EnchantOrginal.class
			.getName());

	private static EnchantOrginal _instance;

	private final HashMap<Integer, L1WilliamEnchantOrginal> _ArmorIndex = new HashMap<Integer, L1WilliamEnchantOrginal>();

	public static EnchantOrginal getInstance() {
		if (_instance == null) {
			_instance = new EnchantOrginal();
		}
		return _instance;
	}

	private EnchantOrginal() {
		loadArmorOrginal();
	}

	public void loadArmorOrginal() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			// pstm =
			// con.prepareStatement("SELECT * FROM william_lv_armor_weapon");
			// pstm =
			// con.prepareStatement("select * from william_lv_weapon order by itemid,enchantlevel asc");
			pstm = con
					.prepareStatement("select * from 系統_武器強化加成 order by 強化值,武器類型 asc");
			rs = pstm.executeQuery();
			fillWeaponSkill(rs);

		} catch (final SQLException e) {
			_log.log(Level.SEVERE, "error while creating 系統_武器強化加成  table", e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillWeaponSkill(final ResultSet rs) throws SQLException {
		final PerformanceTimer timer = new PerformanceTimer();
		int id = 0;
		while (rs.next()) {
			final int itemid = rs.getInt("itemid");
			final int level = rs.getInt("強化值");
			final int type = rs.getInt("武器類型");
			final double dmgbl = rs.getDouble("攻擊倍率");
			final int addAc = rs.getInt("addAc"); // 額外防禦
			final byte addStr = rs.getByte("addStr");
			final byte addDex = rs.getByte("addDex");
			final byte addCon = rs.getByte("addCon");
			final byte addInt = rs.getByte("addInt");
			final byte addWis = rs.getByte("addWis");
			final byte addCha = rs.getByte("addCha");
			final int addMaxHp = rs.getInt("addMaxHp");
			final int addMaxMp = rs.getInt("addMaxMp");
			final int addHpr = rs.getInt("addHpr");
			final int addMpr = rs.getInt("addMpr");
			final int addDmg = rs.getInt("addDmg");
			final int addBowDmg = rs.getInt("addBowDmg");
			final int addHit = rs.getInt("addHit");
			final int addBowHit = rs.getInt("addBowHit");
			final int reduction_dmg = rs.getInt("addDmgReduction"); // 所有傷害減免
			final int addMr = rs.getInt("addMr");
			final int addSp = rs.getInt("addSp");
			final int addPVPDmg = rs.getInt("addPVPDmg");

			final L1WilliamEnchantOrginal ArmorOrginal = new L1WilliamEnchantOrginal(
					id, itemid, level, type, dmgbl, addAc, addStr, addDex,
					addCon, addInt, addWis, addCha, addMaxHp, addMaxMp, addHpr,
					addMpr, addDmg, addBowDmg, addHit, addBowHit,
					reduction_dmg, addMr, addSp, addPVPDmg);
			_ArmorIndex.put(id, ArmorOrginal);
			id++;
		}
		_logx.info("載入武器加成能力值數據: " + _ArmorIndex.size() + "(" + timer.get()
				+ "ms)");
	}

	public L1WilliamEnchantOrginal getTemplate(final int Armor) {
		return _ArmorIndex.get(Armor);
	}

	public L1WilliamEnchantOrginal[] getArmorList() {
		return _ArmorIndex.values().toArray(
				new L1WilliamEnchantOrginal[_ArmorIndex.size()]);
	}
}
