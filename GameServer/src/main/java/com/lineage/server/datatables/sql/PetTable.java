package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.lineage.data.npc.shop.CustomPlayerShopByNpc;
import com.lineage.server.IdFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.mappers.PetMapper;
import com.lineage.server.datatables.storage.PetStorage;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 寵物資料表
 * @author dexc
 *
 */
public class PetTable implements PetStorage {

	private static final Log _log = LogFactory.getLog(PetTable.class);

	private static final Map<Integer, L1Pet> _pets = new HashMap<Integer, L1Pet>();

	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement(
					"SELECT * FROM `character_pets`");

			rs = ps.executeQuery();
			while (rs.next()) {
				final L1Pet pet = PetMapper.get().mapRow(rs);
				final int item_obj_id = pet.get_itemobjid();
				
				// 搜尋對應道具是否存在
				if (World.get().findObject(item_obj_id) != null) {
					_pets.put(new Integer(item_obj_id), pet);
					
				} else {
					if (!CustomPlayerShopByNpc.getInstance().getWorldItems().contains(item_obj_id)) {
						// 道具遺失 刪除相關資訊
						delete(item_obj_id);
					} else {
						_pets.put(new Integer(item_obj_id), pet);
					}
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
		_log.info("載入寵物資料數量: " + _pets.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 道具遺失 刪除相關資訊
	 * @param item_obj_id
	 */
	private static void delete(int item_obj_id) {
		Connection co = null;
		PreparedStatement pm = null;
		try {
			co = DatabaseFactoryLogin.get().getConnection();
			pm = co.prepareStatement(
					"DELETE FROM `character_pets` WHERE `item_obj_id`=?");
			pm.setInt(1, item_obj_id);
			
			pm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
	}

	@Override
	public void storeNewPet(final L1NpcInstance pet, final int objid, final int itemobjid) {
		// XXX 呼前處理重複
		final L1Pet l1pet = new L1Pet();
		l1pet.set_itemobjid(itemobjid);
		l1pet.set_objid(objid);
		l1pet.set_npcid(pet.getNpcTemplate().get_npcId());
		l1pet.set_name(pet.getNpcTemplate().get_name());
		//l1pet.set_level(pet.getNpcTemplate().get_level());
		l1pet.set_level(pet.getLevel());
		l1pet.set_hp(pet.getMaxHp());
		l1pet.set_mp(pet.getMaxMp());
		l1pet.set_exp((int) pet.getExp()); // Lv.5EXP
		l1pet.set_lawful(0);
		_pets.put(new Integer(itemobjid), l1pet);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_pets` SET `item_obj_id`=?," +
					"`objid`=?,`npcid`=?,`name`=?," +
					"`lvl`=?,`hp`=?,`mp`=?,`exp`=?,`lawful`=?");
			pstm.setInt(1, l1pet.get_itemobjid());
			pstm.setInt(2, l1pet.get_objid());
			pstm.setInt(3, l1pet.get_npcid());
			pstm.setString(4, l1pet.get_name());
			pstm.setInt(5, l1pet.get_level());
			pstm.setInt(6, l1pet.get_hp());
			pstm.setInt(7, l1pet.get_mp());
			pstm.setInt(8, l1pet.get_exp());
			pstm.setInt(9, l1pet.get_lawful());
			pstm.execute();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}


	public void storeCopyNewPet(final L1Pet copy, final int itemobjid) {
		// XXX 呼前處理重複
		final L1Pet l1pet = new L1Pet();
		l1pet.set_itemobjid(itemobjid);
		l1pet.set_objid(IdFactory.get().nextId());
		l1pet.set_npcid(copy.get_npcid());
		l1pet.set_name(copy.get_name());
		l1pet.set_level(copy.get_level());
		l1pet.set_hp(copy.get_hp());
		l1pet.set_mp(copy.get_mp());
		l1pet.set_exp(copy.get_exp());
		l1pet.set_lawful(copy.get_lawful());
		_pets.put(itemobjid, l1pet);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO `character_pets` SET `item_obj_id`=?," +
							"`objid`=?,`npcid`=?,`name`=?," +
							"`lvl`=?,`hp`=?,`mp`=?,`exp`=?,`lawful`=?");
			pstm.setInt(1, l1pet.get_itemobjid());
			pstm.setInt(2, l1pet.get_objid());
			pstm.setInt(3, l1pet.get_npcid());
			pstm.setString(4, l1pet.get_name());
			pstm.setInt(5, l1pet.get_level());
			pstm.setInt(6, l1pet.get_hp());
			pstm.setInt(7, l1pet.get_mp());
			pstm.setInt(8, l1pet.get_exp());
			pstm.setInt(9, l1pet.get_lawful());
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	@Override
	public void storePet(final L1Pet pet) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE `character_pets` SET `objid`=?," +
					"`npcid`=?,`name`=?,`lvl`=?,`hp`=?," +
					"`mp`=?,`exp`=?,`lawful`=? " +
					"WHERE `item_obj_id`=?");
			pstm.setInt(1, pet.get_objid());
			pstm.setInt(2, pet.get_npcid());
			pstm.setString(3, pet.get_name());
			pstm.setInt(4, pet.get_level());
			pstm.setInt(5, pet.get_hp());
			pstm.setInt(6, pet.get_mp());
			pstm.setInt(7, pet.get_exp());
			pstm.setInt(8, pet.get_lawful());
			pstm.setInt(9, pet.get_itemobjid());
			pstm.execute();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void deletePet(final int itemobjid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM `character_pets` WHERE `item_obj_id`=?");
			pstm.setInt(1, itemobjid);
			pstm.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_pets.remove(itemobjid);
	}

	/**
	 * Pets既名前存在返。
	 *
	 * @param nameCaseInsensitive
	 *            調名前。大文字小文字差異無視。
	 * @return 既名前存在true
	 */
	@Override
	public boolean isNameExists(final String nameCaseInsensitive) {
		final String nameLower = nameCaseInsensitive.toLowerCase();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			/*
			 * 同名前探。MySQLcase insensitive
			 * 本來LOWER必要、binary變更場合備。
			 */
			pstm = con.prepareStatement(
					"SELECT `item_obj_id` FROM `character_pets` WHERE LOWER(`name`)=?");
			pstm.setString(1, nameLower);
			rs = pstm.executeQuery();
			if (!rs.next()) { // 同名前無
				return false;
			}
			if (PetTypeTable.getInstance().isNameDefault(nameLower)) { // 名前重複
				return false;
			}
			
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return true;
	}

	@Override
	public L1Pet getTemplate(final int itemobjid) {
		return _pets.get(new Integer(itemobjid));
	}

	@Override
	public L1Pet getTemplateX(final int npcobjid) {
		for (L1Pet pet : _pets.values()) {
			if (pet.get_objid() == npcobjid) {
				return pet;
			}
		}
		return null;
	}

	@Override
	public L1Pet[] getPetTableList() {
		return _pets.values().toArray(new L1Pet[_pets.size()]);
	}
}
