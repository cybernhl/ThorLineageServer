package com.lineage.server.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.QuestMobExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.QuesttSpawnTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1MobGroupSpawn;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.PerformanceTimer;

/**
 * 執行中副本組暫存
 * @author daien
 *
 */
public class L1QuestUser {

	private static final Log _log = LogFactory.getLog(L1QuestUser.class);
	
	private final int _id;// 副本唯一編號
	
	private final int _questid;// 副本任務編號
	
	private final short _mapid;// 副本執行地圖編號
	
	//private boolean _mobNull = true;// 怪物剩餘0特殊處理
	
	private QuestMobExecutor _mobNull = null;// 怪物剩餘0特殊處理
	
	private boolean _info = true;// 怪物剩餘訊息
	
	private boolean _outStop = false;// 該副本參加者其中之一離開 是否立即結束
	
	private int _time = -1;// 進入時間限制

	private final ArrayList<L1PcInstance> _userList;// 參加副本的PC
	
	private final ArrayList<L1NpcInstance> _npcList;// 副本中召喚的NPC
	
	/**
	 * 執行副本組暫存
	 * 
	 * @param id 副本唯一編號
	 * @param mapid 副本執行地圖編號
	 * @param questid 副本任務編號
	 */
	public L1QuestUser(final int id, final int mapid, final int questid) {
		_id = id;
		_mapid = (short) mapid;
		_questid = questid;
		_userList = new ArrayList<L1PcInstance>();
		_npcList = new ArrayList<L1NpcInstance>();
	}

	/**
	 * 副本唯一編號
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 副本任務編號
	 * @return
	 */
	public int get_questid() {
		return _questid;
	}

	/**
	 * 副本地圖編號
	 * @return
	 */
	public int get_mapid() {
		return _mapid;
	}
	
	/**
	 * 加入副本執行成員
	 * @param pc
	 */
	public void add(final L1PcInstance pc) {
		try {
			// 列表中不包含該項元素
			if (!_userList.contains(pc)) {
				_userList.add(pc);
				pc.set_showId(_id);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			_log.info("加入副本執行成員(" + _questid + "-" + _id + "):" + pc.getName());
		}
	}
	
	/**
	 * 移出副本執行成員
	 * @param pc
	 */
	public void remove(final L1PcInstance pc) {
		try {
			// 列表中包含該項元素
			if (_userList.remove(pc)) {
				//_userList.remove(pc);
				pc.set_showId(-1);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			_log.info("移出副本執行成員(" + _questid + "-" + _id + "):" + pc.getName());
		}
	}

	/**
	 * 進入時間限制(單位:秒)<BR>
	 * -1 不限制
	 * @param time
	 */
	public void set_time(final int time) {
		this._time = time;
	}

	/**
	 * 進入時間限制(單位:秒)<BR>
	 * -1 不限制
	 * @return
	 */
	public int get_time() {
		return _time;
	}
	
	/**
	 * 具有時間限制
	 * @return true:有 false:沒有
	 */
	public boolean is_time() {
		return _time != -1;
	}

	/**
	 * 該執行中副本剩餘PC
	 * @return
	 */
	public ArrayList<L1PcInstance> pcList() {
		return _userList;
	}

	/**
	 * 該執行中副本剩餘人數
	 * @return
	 */
	public int size() {
		return _userList.size();
	}

	/**
	 * 該執行中副本剩餘NPC
	 * @return
	 */
	public List<L1NpcInstance> npcList() {
		return _npcList;
	}

	/**
	 * 增加副本中NPC
	 * @param door
	 */
	public void addNpc(L1NpcInstance npc) {
		_npcList.add(npc);
	}

	/**
	 * 該執行中副本中指定NPCID的NPC
	 * @return
	 */
	public ArrayList<L1NpcInstance> npcList(int npcid) {
		final ArrayList<L1NpcInstance> npcList = new ArrayList<L1NpcInstance>();
		for (L1NpcInstance npc : _npcList) {
			// ID相等 並且未死亡
			if (npc.getNpcId() == npcid && !npc.isDead()) {
				npcList.add(npc);
			}
		}
		if (npcList.size() <= 0) {
			return null;
		}
		return npcList;
	}

	/**
	 * 該執行中副本剩餘NPC(全部)數量
	 * @return
	 */
	public int npcSize() {
		return _npcList.size();
	}
	
	/**
	 * 該執行中副本剩餘NPC(怪物)數量
	 * @return
	 */
	public int mobSize() {
		int i = 0;
		for (L1NpcInstance npc : _npcList) {
			// 是怪物
			if (npc instanceof L1MonsterInstance) {
				i += 1;
			}
		}
		return i;
	}
	
	/**
	 * 召喚副本怪物
	 * @param get_id 任務編號
	 */
	public void spawnQuestMob() {
		final PerformanceTimer timer = new PerformanceTimer();
		try {
			// 取回召喚列表
			final ArrayList<L1QuestMobSpawn> spawnList = 
				QuesttSpawnTable.get().getMobSpawn(_questid);
			
			if (spawnList.size() > 0) {// 列表中具有物件
				for (L1QuestMobSpawn mobSpawn : spawnList) {
					// 指定地圖編號
					if (mobSpawn.get_mapid() == _mapid) {
						// 召喚數量
						final int count = mobSpawn.get_count();
						if (count > 0) {
							// 迴圈召喚數量
							for (int i = 0 ; i < count ; i++) {
								spawn(mobSpawn);
							}
						}
					}
				}
			}
			spawnList.clear();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			_log.info("副本任務啟動(" + _questid + "-" + _id + ") NPC完成召喚 數量:" + 
					_npcList.size() + "(" + timer.get() + "ms)");
		}
	}

	private Random _random = new Random();
	
	/**
	 * 召喚NPC
	 * @param mobSpawn
	 */
	private void spawn(final L1QuestMobSpawn mobSpawn) {
		try {
			final int npcid = mobSpawn.get_npc_templateid();
			final int group_id = mobSpawn.get_group_id();
			final int locx1 = mobSpawn.get_locx1();
			final int locy1 = mobSpawn.get_locy1();
			final int locx2 = mobSpawn.get_locx2();
			final int locy2 = mobSpawn.get_locy2();
			final int heading = mobSpawn.get_heading();
			final int mapid = mobSpawn.get_mapid();
			final L1Npc template = NpcTable.get().getTemplate(npcid);
			if (template == null) {
				_log.error("召喚NPC編號: " + npcid + " 不存在資料庫中!");
				
			} else {
				// 區域召喚
				if (locx1 != 0 && locy1 != 0 && locx2 != 0 && locy2 != 0) {
					int x = 0;
					int y = 0;
					
					final L1Map map = L1WorldMap.get().getMap((short) mapid);
					int tryCount = 0;
					// 設置召喚的XY座標位置(50次 定位循環)
					while (tryCount <= 50) {
						x = _random.nextInt((locx2 - locx1)) + locx1;
						y = _random.nextInt((locy2 - locy1)) + locy1;

						// 座標可通行決定召喚位置
						if (map.isInMap(x, y) && map.isPassable(x, y, null)) {
							final L1Location loc = new L1Location(x, y, mapid);
							final L1NpcInstance mob = L1SpawnUtil.spawn(npcid, loc, heading, _id);
							/*if (mob instanceof L1MonsterInstance) {
								((L1MonsterInstance) mob).set_storeDroped(false);
							}*/
							//System.out.println(mob.get_showId() + " 任務地圖內物件:"+mob.getNpcTemplate().get_name());
							_npcList.add(mob);// 加入列表
							groupSpawn(group_id, mob);// 召喚隊伍成員
							break;
						}
						tryCount++;
					}
					
				} else {
					final L1Location loc = new L1Location(locx1, locy1, mapid);
					final L1NpcInstance mob = L1SpawnUtil.spawn(npcid, loc, heading, _id);
					//System.out.println(mob.get_showId() + " 任務地圖內物件:"+mob.getNpcTemplate().get_name());
					_npcList.add(mob);// 加入列表
					groupSpawn(group_id, mob);// 召喚隊伍成員
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 召喚隊伍成員
	 * @param group_id 
	 * @param mob
	 */
	private void groupSpawn(final int group_id, final L1NpcInstance mob) {
		if (group_id != 0) {
			// 召喚隊伍成員
			L1MobGroupSpawn.getInstance().doSpawn(mob, group_id, true, true);
		}
		// NPC具有隊伍狀態
		if (mob.getMobGroupInfo() != null) {
			for (L1NpcInstance mobx : mob.getMobGroupInfo().getList()) {
				if (!mobx.equals(mob)) {// 不是隊長
					_npcList.add(mobx);// 隊員加入列表
				}
			}
		}
	}

	/**
	 * 移除副本怪物
	 * @param mob
	 */
	public void removeMob(final L1NpcInstance mob) {
		try {
			// 移除NPC
			if (_npcList.remove(mob)) {
				if (is_info()) {
					// 13:剩餘怪物：
					sendPackets(new S_HelpMessage("\\fY剩餘怪物：" + mobSize()));
				}
			}
			if (mobSize() <= 0) {
				if (this._mobNull != null) {
					this._mobNull.stopQuest(this);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * 移除副本怪物
	 */
	public void removeMob() {
		try {
			ArrayList<L1NpcInstance> allList = new ArrayList<L1NpcInstance>();
			allList.addAll(_npcList);
			// 移除NPC
			for (L1NpcInstance npc : allList) {
				npc.deleteMe();
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			// 清空副本NPC清單
			ListMapUtil.clear(_npcList);
			
			_log.info("副本任務結束(" + _questid + "-" + _id + ")");
		}
	}

	/**
	 * 完成任務結束副本
	 */
	public void endQuest() {
		try {
			// 移除玩家
			for (L1PcInstance pc : _userList) {
				if (pc.getMapId() == _mapid) {
					// 傳送成員離開(奇巖 十字架下)
					L1Teleport.teleport(pc, 33430, 32814, (short) 4, 4, true);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			ListMapUtil.clear(_userList);
		}
	}

	/**
	 * 發送封包資料給予任務中執行玩家
	 * @param s_HelpMessage
	 */
	public void sendPackets(final ServerBasePacket basePacket) {
		try {
			for (L1PcInstance pc : _userList) {
				pc.sendPackets(basePacket);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		}
	}

	/**
	 * 怪物剩餘訊息
	 * @param _info
	 */
	public void set_info(boolean _info) {
		this._info = _info;
	}

	/**
	 * 怪物剩餘訊息
	 * @return
	 */
	public boolean is_info() {
		return _info;
	}

	/**
	 * 該副本參加者其中之一離開 是否立即結束
	 * @param _outStop
	 */
	public void set_outStop(boolean _outStop) {
		this._outStop = _outStop;
	}

	/**
	 * 該副本參加者其中之一離開 是否立即結束
	 * @return
	 */
	public boolean is_outStop() {
		return _outStop;
	}

	/**
	 * 怪物剩餘0特殊處理
	 * @param _outStop
	 */
	public void set_object(QuestMobExecutor mobNull) {
		this._mobNull = mobNull;
	}

	/**
	 * 怪物剩餘0特殊處理
	 * @return
	 */
	public QuestMobExecutor get_object() {
		return _mobNull;
	}
}
