package com.lineage.server.datatables;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.mappers.NpcMapper;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * NPC設置資料
 *
 * @author dexc
 *
 */
public class NpcTable {

	private static final Log _log = LogFactory.getLog(NpcTable.class);

	//private boolean _initialized;

	private static NpcTable _instance;

	public static int ORC = -1;

	private static final Map<Integer, L1Npc> _npcs = new HashMap<Integer, L1Npc>();

	private static final Map<String, Constructor<?>> _constructorCache = new HashMap<String, Constructor<?>>();

	private static final Map<String, Integer> _familyTypes = NpcTable.buildFamily();

	public static NpcTable get() {
		if (_instance == null) {
			_instance = new NpcTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		this.loadNpcList();
		this.loadNpcMonster();
		//this._initialized = true;
		_log.info("載入NPC設置資料數量: " + _npcs.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 取得執行類位置
	 * @param implName
	 * @return
	 */
	private Constructor<?> getConstructor(final String implName) {
		try {
			final String implFullName = "com.lineage.server.model.Instance."
				+ implName + "Instance";
			final Constructor<?> con = Class.forName(implFullName).getConstructors()[0];
			return con;
			
		} catch (final ClassNotFoundException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 加載NPC執行類位置
	 * @param implName
	 */
	private void registerConstructorCache(final String implName) {
		if (implName.isEmpty() || _constructorCache.containsKey(implName)) {
			return;
		}
		_constructorCache.put(implName, this.getConstructor(implName));
	}
	
	private void loadNpcList() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `npc_list`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final L1Npc npc = NpcMapper.get().mapRow(rs);
				final int npcId = npc.get_npcId();
				
				// 處理 Executor
				npc.setNpcExecutor(addClass(npcId, npc.get_classname()));
				
				// 處理 Family
				final Integer family = _familyTypes.get(rs.getString("family"));
				if (family == null) {
					npc.set_family(0);
				} else {
					npc.set_family(family.intValue());
				}
				
				final int agrofamily = rs.getInt("agrofamily");
				if ((npc.get_family() == 0) && (agrofamily == 1)) {
					npc.set_agrofamily(0);
				} else {
					npc.set_agrofamily(agrofamily);
				}

				this.registerConstructorCache(npc.getImpl());
				
				_npcs.put(npcId, npc);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private void loadNpcMonster() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `npc_monster`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final L1Npc npc = NpcMapper.get().mapRow(rs);
				final int npcId = npc.get_npcId();
				
				// 處理 Executor
				npc.setNpcExecutor(addClass(npcId, npc.get_classname()));
				
				// 處理 Family
				final Integer family = _familyTypes.get(rs.getString("family"));
				if (family == null) {
					npc.set_family(0);
				} else {
					npc.set_family(family.intValue());
				}
				
				final int agrofamily = rs.getInt("agrofamily");
				if ((npc.get_family() == 0) && (agrofamily == 1)) {
					npc.set_agrofamily(0);
				} else {
					npc.set_agrofamily(agrofamily);
				}

				this.registerConstructorCache(npc.getImpl());
				
				_npcs.put(npcId, npc);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 加入獨立執行CLASS位置
	 * @param npcid
	 * @param className
	 * @return 
	 */
	private NpcExecutor addClass(final int npcid, final String className) {
		try {
			if (!className.equals("0")) {
				String newclass = className;
				String[] set = null;
				if (className.indexOf(" ") != -1) {
					set = className.split(" ");
					try {
						newclass = set[0];
					} catch (final Exception e) {
					}
				}
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("com.lineage.data.npc.");
				stringBuilder.append(newclass);

				final Class<?> cls = Class.forName(stringBuilder.toString());
				final NpcExecutor exe = (NpcExecutor) cls.getMethod("get").invoke(null);
				if (set != null) {
					exe.set_set(set);
				}
				return exe;
			}

		} catch (final ClassNotFoundException e) {
			String error = "發生[NPC檔案]錯誤, 檢查檔案是否存在:" + className + " NpcId:" + npcid;
			_log.error(error);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 傳回該編號NPC資料
	 * @param id
	 * @return
	 */
	public L1Npc getTemplate(final int id) {
		return _npcs.get(id);
	}

	/**
	 * 取回NPC名稱
	 * @param id
	 * @return
	 */
	public String getNpcName(final int id) {
		final L1Npc npcTemp = this.getTemplate(id);
		if (npcTemp == null) {
			_log.error("取回NPC名稱錯誤 沒有這個編號的NPC: " + id);
			return null;
		}
		return npcTemp.get_nameid();
	}

	/**
	 * 依照NPCID取回新的L1NpcInstance資料
	 * @param id NPCID
	 * @return 
	 */
	public L1NpcInstance newNpcInstance(final int id) {
		try {
			final L1Npc npcTemp = this.getTemplate(id);
			if (npcTemp == null) {
				_log.error("依照NPCID取回新的L1NpcInstance資料發生異常(沒有這編號的NPC): " + id);
				return null;
			}
			return this.newNpcInstance(npcTemp);
			
		} catch (final Exception e) {
			_log.error("NPCID:" + id + "/" + e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 依照NPC資料 取回新的L1NpcInstance資料
	 * @param template NPC資料
	 * @return
	 */
	public L1NpcInstance newNpcInstance(final L1Npc template) {
		try {
			if (template == null) {
				_log.error("依照NPCID取回新的L1NpcInstance資料發生異常(NPC資料為空)");
				return null;
			}
			final Constructor<?> con = _constructorCache.get(template.getImpl());
			return (L1NpcInstance) con.newInstance(new Object[] { template });
			
		} catch (final Exception e) {
			_log.error("NPCID:" + template.get_npcId() + "/" + e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 建立NPC家族清單
	 * @return
	 */
	private static Map<String, Integer> buildFamily() {
		final Map<String, Integer> result = new HashMap<String, Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"select distinct(family) as family from npc_monster WHERE NOT trim(family) =''");
			rs = pstm.executeQuery();
			int id = 1;
			while (rs.next()) {
				final String family = rs.getString("family");
				int oid = id++;
				if (family.equalsIgnoreCase("orc")) {
					ORC = oid;
				}
				result.put(family, oid);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	/**
	 * 依照NPC名稱傳回NPCID
	 * @param name 依照NPC名稱
	 * @return
	 */
	public int findNpcIdByName(final String name) {
		for (final L1Npc npc : _npcs.values()) {
			if (npc.get_name().equals(name)) {
				return npc.get_npcId();
			}
		}
		return 0;
	}

	/**
	 * 依照NPC名稱傳回NPCID
	 * @param name 依照NPC名稱
	 * @return
	 */
	public int findNpcIdByNameWithoutSpace(final String name) {
		for (final L1Npc npc : _npcs.values()) {
			if (npc.get_name().replace(" ", "").equals(name)) {
				return npc.get_npcId();
			}
		}
		return 0;
	}
}
