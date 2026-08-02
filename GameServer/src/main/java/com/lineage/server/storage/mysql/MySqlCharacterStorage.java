package com.lineage.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.datatables.mappers.CharacterMapper;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.storage.CharacterStorage;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.utils.SQLUtil;

/**
 * PC資料
 * @author daien
 *
 */
public class MySqlCharacterStorage implements CharacterStorage {

	private static final Log _log = LogFactory.getLog(MySqlCharacterStorage.class);

	/**
	 * 載入PC資料
	 */
	@Override
	public L1PcInstance loadCharacter(final String charName, ClientExecutor _client) {
		L1PcInstance pc = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, charName);

			rs = pstm.executeQuery();
			if (!rs.next()) {
				/*
				 * SELECT結果返。
				 */
				return null;
			}
			pc = CharacterMapper.get().mapRow(rs, _client);

			// _log.finest("restored char data: ");

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
			return null;

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return pc;
	}

	@Override
	public void createCharacter(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO characters SET account_name=?,objid=?," +
					"char_name=?,level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?," +
					"MaxMp=?,CurMp=?,Ac=?,Str=?,Con=?,Dex=?,Cha=?,Intel=?," +
					"Wis=?,Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?," +
					"LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?," +
					"ClanRank=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?," +
					"PkCountForElf=?,ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?," +
					"HomeTownID=?,Contribution=?,Pay=?,HellTime=?,Banned=?,Karma=?," +
					"LastPk=?,LastPkForElf=?,CreateTime=?"
					);
			pstm.setString(++i, pc.getAccountName());
			pstm.setInt(++i, pc.getId());
			pstm.setString(++i, pc.getName());
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setLong(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAc());
			pstm.setInt(++i, pc.getBaseStr());
			pstm.setInt(++i, pc.getBaseCon());
			pstm.setInt(++i, pc.getBaseDex());
			pstm.setInt(++i, pc.getBaseCha());
			pstm.setInt(++i, pc.getBaseInt());
			pstm.setInt(++i, pc.getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getPkCountForElf());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			pstm.setShort(++i, pc.getAccessLevel());
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, 0);
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getLastPkForElf());

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			final String times = sdf.format(System.currentTimeMillis());
			int time = Integer.parseInt(times.replace("-", ""));
			pstm.setInt(++i, time);

			pstm.execute();

			// _log.finest("stored char data: " + pc.getName());
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void deleteCharacter(final String accountName, final String charName)
	throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"SELECT * FROM characters WHERE account_name=? AND char_name=?");
			pstm.setString(1, accountName);
			pstm.setString(2, charName);
			rs = pstm.executeQuery();
			
			if (!rs.next()) {
				throw new RuntimeException("could not delete character");
			}
			
			int objid = CharObjidTable.get().charObjid(charName);
			
			if (objid != 0) {
				// 刪除人物背包資料
				CharItemsReading.get().delUserItems(objid);
			}
			
			pstm = con.prepareStatement(
					"DELETE FROM character_buddys WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();
			
			pstm = con.prepareStatement(
					"DELETE FROM character_buff WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();
			
			pstm = con.prepareStatement(
					"DELETE FROM character_config WHERE object_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();

			pstm = con.prepareStatement(
					"DELETE FROM character_quests WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();
			
			pstm = con.prepareStatement(
					"DELETE FROM character_skills WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();
			
			pstm = con.prepareStatement(
					"DELETE FROM character_teleport WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
			pstm.setString(1, charName);
			pstm.execute();
			
			pstm = con.prepareStatement(
					"DELETE FROM characters WHERE char_name=?");
			pstm.setString(1, charName);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}
	
	@Override
	public void storeCharacter(final L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE characters SET level=?,HighLevel=?,Exp=?," +
					"MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?," +
					"Con=?,Dex=?,Cha=?,Intel=?,Wis=?,Status=?," +
					"Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?," +
					"MapID=?,Food=?,Lawful=?,Title=?,ClanID=?," +
					"Clanname=?,ClanRank=?,BonusStatus=?," +
					"ElixirStatus=?,ElfAttr=?,PKcount=?,PkCountForElf=?," +
					"ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?," +
					"HomeTownID=?,Contribution=?,HellTime=?,Banned=?," +
					"Karma=?,LastPk=?,LastPkForElf=? WHERE objid=?"
					);
			pstm.setInt(++i, pc.getLevel());
			pstm.setInt(++i, pc.getHighLevel());
			pstm.setLong(++i, pc.getExp());
			pstm.setInt(++i, pc.getBaseMaxHp());
			int hp = pc.getCurrentHp();
			if (hp < 1) {
				hp = 1;
			}
			pstm.setInt(++i, hp);
			pstm.setInt(++i, pc.getBaseMaxMp());
			pstm.setInt(++i, pc.getCurrentMp());
			pstm.setInt(++i, pc.getAc());
			pstm.setInt(++i, pc.getBaseStr());
			pstm.setInt(++i, pc.getBaseCon());
			pstm.setInt(++i, pc.getBaseDex());
			pstm.setInt(++i, pc.getBaseCha());
			pstm.setInt(++i, pc.getBaseInt());
			pstm.setInt(++i, pc.getBaseWis());
			pstm.setInt(++i, pc.getCurrentWeapon());
			pstm.setInt(++i, pc.getClassId());
			pstm.setInt(++i, pc.get_sex());
			pstm.setInt(++i, pc.getType());
			pstm.setInt(++i, pc.getHeading());
			pstm.setInt(++i, pc.getX());
			pstm.setInt(++i, pc.getY());
			pstm.setInt(++i, pc.getMapId());
			pstm.setInt(++i, pc.get_food());
			pstm.setInt(++i, pc.getLawful());
			pstm.setString(++i, pc.getTitle());
			pstm.setInt(++i, pc.getClanid());
			pstm.setString(++i, pc.getClanname());
			pstm.setInt(++i, pc.getClanRank());
			pstm.setInt(++i, pc.getBonusStats());
			pstm.setInt(++i, pc.getElixirStats());
			pstm.setInt(++i, pc.getElfAttr());
			pstm.setInt(++i, pc.get_PKcount());
			pstm.setInt(++i, pc.getPkCountForElf());
			pstm.setInt(++i, pc.getExpRes());
			pstm.setInt(++i, pc.getPartnerId());
			short leve = pc.getAccessLevel();
			if (leve >= 20000) {
				leve = 0;
			}
			pstm.setShort(++i, leve);
			pstm.setInt(++i, pc.getOnlineStatus());
			pstm.setInt(++i, pc.getHomeTownId());
			pstm.setInt(++i, pc.getContribution());
			pstm.setInt(++i, pc.getHellTime());
			pstm.setBoolean(++i, pc.isBanned());
			pstm.setInt(++i, pc.getKarma());
			pstm.setTimestamp(++i, pc.getLastPk());
			pstm.setTimestamp(++i, pc.getLastPkForElf());
			pstm.setInt(++i, pc.getId());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
