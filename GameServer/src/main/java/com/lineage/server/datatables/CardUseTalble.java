/**
 * 
 */
package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;
import com.william.CardUse;

/**
 * @author long
 *
 */
public class CardUseTalble
{
	private static CardUseTalble _instance;

	private final ArrayList<CardUse> data = new ArrayList<CardUse>();

	public static CardUseTalble getInstance() {
		if (_instance == null) {
			_instance = new CardUseTalble();
		}
		return _instance;
	}

	private CardUseTalble() {
		loadItemSummon();
	}

	private void loadItemSummon() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_card_use");
			rs = pstm.executeQuery();
			fillItemSummon(rs);
		} catch (SQLException e) 
		{
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillItemSummon(ResultSet rs) throws SQLException
	{
		while (rs.next())
		{
			int ItemId = rs.getInt("ItemId");//卷轴编号
			String Name = rs.getString("Name");
			int Time = rs.getInt("Time");
			int ExpNotLose = rs.getInt("ExpNotLose");
			int CampExpNotLose = rs.getInt("CampExpNotLose");
			int ItemNotLose = rs.getInt("ItemNotLose");
			int AddHp = rs.getInt("AddHp");
			int AddMp = rs.getInt("AddMp");
			int AddStr = rs.getInt("AddStr");
			int AddDex = rs.getInt("AddDex");
			int AddInt = rs.getInt("AddInt");
			int AddCon = rs.getInt("AddCon");
			int AddWis = rs.getInt("AddWis");
			int AddCha = rs.getInt("AddCha");
			int AddHpr = rs.getInt("AddHpr");
			int AddMpr = rs.getInt("AddMpr");
			int AddEarth = rs.getInt("AddEarth");
			int AddWater = rs.getInt("AddWater");
			int AddFire = rs.getInt("AddFire");
			int AddWind = rs.getInt("AddWind");
			int AddStun = rs.getInt("AddStun");
			int AddStone = rs.getInt("AddStone");
			int AddSleep = rs.getInt("AddSleep");
			int AddFreeze = rs.getInt("AddFreeze");
			int AddSustain = rs.getInt("AddSustain");
			int AddBlind = rs.getInt("AddBlind");
			int AddMr = rs.getInt("AddMr");
			int AddSp = rs.getInt("AddSp");
			int AddHit = rs.getInt("AddHit");
			int AddBowHit = rs.getInt("AddBowHit");
			int AddDmg = rs.getInt("AddDmg");
			int AddBowDmg = rs.getInt("AddBowDmg");
			int AddReductionDmg = rs.getInt("AddReductionDmg");
			int AddMagiDmg = rs.getInt("AddMagiDmg");
			int AddReductionMagiDmg = rs.getInt("AddReductionMagiDmg");
			int AddExpGet = rs.getInt("AddExpGet");
			int AddAc = rs.getInt("AddAc");

			data.add(new CardUse(ItemId,Name, Time,ExpNotLose,CampExpNotLose,ItemNotLose,AddHp, AddMp,
					AddStr,
					AddDex,
					AddInt,
					AddCon,
					AddWis,
					AddCha,
					AddHpr,
					AddMpr,
					AddEarth,
					AddWater,
					AddFire,
					AddWind,
					AddStun,
					AddStone,
					AddSleep,
					AddFreeze,
					AddSustain,
					AddBlind,
					AddMr,
					AddSp,
					AddHit,
					AddBowHit,
					AddDmg,
					AddBowDmg,
					AddReductionDmg,
					AddMagiDmg,
					AddReductionMagiDmg,
					AddExpGet,
					AddAc));
		}

		//System.out.println("[初始化] 物品升级清单已载入: " + data.size());
	}

	public final ArrayList<CardUse> getTemplate(){
		return data;
	}
}
