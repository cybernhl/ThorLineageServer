package com.lineage.server.utils;

import com.lineage.server.IdFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.templates.L1Furniture;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldEffect;
import com.lineage.server.world.WorldQuest;

/**
 * 召喚控制項
 * @author dexc
 *
 */
public class L1SpawnUtil {

	private static final Log _log = LogFactory.getLog(L1SpawnUtil.class);

	// 正向
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 依PC位置召喚指定NPC
	 * 
	 * @param pc 召喚者
	 * @param npcId NPC編號
	 * @param r 召喚距離(不為0 NPC召喚與PC將有距離 否則重疊)
	 * @param t 存在時間(秒) 小於等於0不限制
	 */
	public static void spawn(final L1PcInstance pc, final int npcId, final int r, final int t) {
		L1SpawnUtil util = new L1SpawnUtil();
		util.spawnR(pc.getLocation(), npcId, pc.get_showId(), r, t);
	}
	
	/**
	 * 依LOC位置召喚指定NPC傳回NPC訊息)
	 * @param loc LOC
	 * @param npcId NPC編號
	 * @param showid 副本編號
	 * @param r 召喚距離(不為0 NPC召喚與PC將有距離 否則重疊)
	 * @param time 存在時間(秒) 小於等於0不限制
	 */
	public static L1NpcInstance spawnRx(final L1Location loc, final int npcId, final int showid, final int r, final int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}
			
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());
			
			if (r == 0) {
				npc.getLocation().set(loc);
				//npc.getLocation().forward(_pc.getHeading());
				
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(loc.getX() + (int) (Math.random() * r)
							- (int) (Math.random() * r));
					npc.setY(loc.getY() + (int) (Math.random() * r)
							- (int) (Math.random() * r));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation(), npc)) {
						break;
					}
					Thread.sleep(2);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(loc);
					//npc.getLocation().forward(_pc.getHeading());
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(5);

			npc.set_showId(showid);

			L1QuestUser q = WorldQuest.get().get(showid);
			if (q != null) {
				q.addNpc(npc);
			}
			
			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			
			// 設置NPC現身
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < time) {
				npc.set_spawnTime(time);
			}
			return npc;
			
		} catch (final Exception e) {
			_log.error("依LOC位置召喚指定NPC(傳回NPC訊息)發生異常: " + npcId, e);
		}
		return null;
	}
	
	/**
	 * 依LOC位置召喚指定NPC(未傳回NPC訊息)
	 * @param loc LOC
	 * @param npcId NPC編號
	 * @param showid 副本編號
	 * @param randomRange 範圍
	 */
	public static void spawnR(final L1Location loc, final int npcId, final int showid, final int randomRange) {
		L1SpawnUtil util = new L1SpawnUtil();
		util.spawnR(loc, npcId, showid, randomRange, 0);
	}
	
	private void spawnR(final L1Location loc, final int npcId, final int showid, final int randomRange, final int timeMillisToDelete) {
		SpawnR1 spawn = new SpawnR1(loc, npcId, showid, randomRange, timeMillisToDelete);
		GeneralThreadPool.get().schedule(spawn, 0);
	}
	
	private class SpawnR1 implements Runnable {
		//final L1PcInstance _pc;
		final L1Location _location;
		final int _npcId;
		final int _showid;
		final int _randomRange;
		final int _timeMillisToDelete;
		private SpawnR1(final L1Location location, final int npcId, final int showid, final int randomRange, 
				final int timeMillisToDelete) {
			_location = location;
			_npcId = npcId;
			_showid = showid;
			_randomRange = randomRange;
			_timeMillisToDelete = timeMillisToDelete;
		}

		@Override
		public void run() {
			try {
				final L1NpcInstance npc = NpcTable.get().newNpcInstance(_npcId);

				if (npc == null) {
					return;
				}
				
				npc.setId(IdFactoryNpc.get().nextId());
				npc.setMap(_location.getMap());
				
				if (_randomRange == 0) {
					npc.getLocation().set(_location);
					//npc.getLocation().forward(_pc.getHeading());
					
				} else {
					int tryCount = 0;
					do {
						tryCount++;
						npc.setX(_location.getX() + (int) (Math.random() * _randomRange)
								- (int) (Math.random() * _randomRange));
						npc.setY(_location.getY() + (int) (Math.random() * _randomRange)
								- (int) (Math.random() * _randomRange));
						if (npc.getMap().isInMap(npc.getLocation())
								&& npc.getMap().isPassable(npc.getLocation(), npc)) {
							break;
						}
						Thread.sleep(2);
					} while (tryCount < 50);

					if (tryCount >= 50) {
						npc.getLocation().set(_location);
						//npc.getLocation().forward(_pc.getHeading());
					}
				}

				npc.setHomeX(npc.getX());
				npc.setHomeY(npc.getY());
				npc.setHeading(5);

				// 設置副本編號 TODO
				npc.set_showId(_showid);

				L1QuestUser q = WorldQuest.get().get(_showid);
				if (q != null) {
					q.addNpc(npc);
				}
				
				World.get().storeObject(npc);
				World.get().addVisibleObject(npc);

				npc.turnOnOffLight();
				
				// 設置NPC現身
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				if (0 < _timeMillisToDelete) {
					// 存在時間(秒)
					npc.set_spawnTime(_timeMillisToDelete);
				}

			} catch (final Exception e) {
				_log.error("執行NPC召喚發生異常: " + _npcId, e);
			}
		}
	}

	/**
	 * 召喚已有傢俱資料
	 * @param temp
	 */
	public static void spawn(final L1Furniture temp) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(temp.get_npcid());

			if (npc == null) {
				return;
			}
			
			final L1FurnitureInstance furniture = (L1FurnitureInstance) npc;

			furniture.setId(IdFactoryNpc.get().nextId());
			furniture.setMap(temp.get_mapid());

			furniture.setX(temp.get_locx());
			furniture.setY(temp.get_locy());
			
			furniture.setHomeX(furniture.getX());
			furniture.setHomeY(furniture.getY());
			furniture.setHeading(0);
			furniture.setItemObjId(temp.get_item_obj_id());

			World.get().storeObject(furniture);
			World.get().addVisibleObject(furniture);

		} catch (final Exception e) {
			_log.error("執行傢俱召喚發生異常!", e);
		}
	}
	
	/**
	 * 執行傢俱召喚
	 * @param pc
	 * @return
	 */
	public static L1FurnitureInstance spawn(final L1PcInstance pc, final int npcid, final int itemObjectId) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			final L1FurnitureInstance furniture = (L1FurnitureInstance) npc;

			furniture.setId(IdFactoryNpc.get().nextId());
			furniture.setMap(pc.getMapId());
			
			if (pc.getHeading() == 0) {
				furniture.setX(pc.getX());
				furniture.setY(pc.getY() - 1);
				
			} else if (pc.getHeading() == 2) {
				furniture.setX(pc.getX() + 1);
				furniture.setY(pc.getY());
			}
			
			furniture.setHomeX(furniture.getX());
			furniture.setHomeY(furniture.getY());
			furniture.setHeading(0);
			furniture.setItemObjId(itemObjectId);

			World.get().storeObject(furniture);
			World.get().addVisibleObject(furniture);
			FurnitureSpawnReading.get().insertFurniture(furniture);
			return furniture;

		} catch (final Exception e) {
			_log.error("執行傢俱召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚指定編號NPC
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, 
			final int x, final int y, final short m, final int h) {
		return spawnT(npcid, x, y, m, h, 0);
	}

	/**
	 * 召喚指定編號NPC
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @param time 存在時間(秒)
	 * @return
	 */
	public static L1NpcInstance spawnT(final int npcid, 
			final int x, final int y, final short m, final int h, int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(m);

			npc.setX(x);
			npc.setY(y);

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(h);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			
			// 設置NPC現身
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < time) {
				// 存在時間(秒)
				npc.set_spawnTime(time);
			}
			return npc;

		} catch (final Exception e) {
			_log.error("執行NPC召喚發生異常: " + npcid, e);
		}
		return null;
	}

	/**
	 * 召喚分身
	 * @param pc
	 * @param loc
	 * @param h
	 * @return
	 */
	public static L1IllusoryInstance spawn(final L1PcInstance pc, final L1Location loc, final int h, final int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(81003);

			if (npc == null) {
				return null;
			}

			final L1IllusoryInstance ill = (L1IllusoryInstance) npc;
			ill.setId(IdFactoryNpc.get().nextId());
			ill.setMap(loc.getMap());

			ill.setX(loc.getX());
			ill.setY(loc.getY());

			ill.setHomeX(ill.getX());
			ill.setHomeY(ill.getY());
			ill.setHeading(h);
			
			ill.setNameId("分身");
			ill.setTitle(pc.getName() + " 的");// 567：的  
			ill.setMaster(pc);

			// 設置副本編號 TODO
			ill.set_showId(pc.get_showId());

			L1QuestUser q = WorldQuest.get().get(pc.get_showId());
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(ill);
			World.get().addVisibleObject(ill);

			if (time > 0) {
				// 存在時間(秒)
				ill.set_spawnTime(time);
			}
			
			if (pc.getWeapon() != null) {
				ill.setStatus(pc.getWeapon().getItem().getType1());
				if (pc.getWeapon().getItem().getRange() != -1) {
					ill.set_ranged(2);
					
				} else {
					ill.set_ranged(10);
					ill.setBowActId(66);
				}
			}
			ill.setLevel((int) (pc.getLevel() * 0.7));

			ill.setStr((int) (pc.getStr() * 0.7));
			ill.setCon((int) (pc.getCon() * 0.7));
			ill.setDex((int) (pc.getDex() * 0.7));
			ill.setInt((int) (pc.getInt() * 0.7));
			ill.setWis((int) (pc.getWis() * 0.7));
			
			ill.setMaxMp(10);
			ill.setCurrentMpDirect(10);
			
			ill.setTempCharGfx(pc.getTempCharGfx());
			ill.setGfxId(pc.getGfxId());

			final int attack = SprTable.get().getAttackSpeed(pc.getGfxId(), 1);
			final int move = SprTable.get().getMoveSpeed(pc.getGfxId(), ill.getStatus());

			ill.setPassispeed(move);
			ill.setAtkspeed(attack);
			
			ill.setBraveSpeed(pc.getBraveSpeed());
			ill.setMoveSpeed(pc.getMoveSpeed());
			
			return ill;

		} catch (final Exception e) {
			_log.error("執行分身召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚救援
	 * @param npcid
	 * @param loc
	 * @param show_id 副本編號
	 * @return
	 */
	public static L1MonsterInstance spawnX(final int npcid, final L1Location loc, int show_id) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			final L1MonsterInstance mob = (L1MonsterInstance) npc;
			mob.setId(IdFactoryNpc.get().nextId());
			mob.setMap(loc.getMap());

			mob.setX(loc.getX());
			mob.setY(loc.getY());

			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());

			// 設置副本編號 TODO
			mob.set_showId(show_id);

			L1QuestUser q = WorldQuest.get().get(show_id);
			if (q != null) {
				q.addNpc(npc);
			}
			
			mob.setMovementDistance(20);
			
			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);
			
			return mob;

		} catch (final Exception e) {
			_log.error("執行召喚救援發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚指定對員
	 * @param master
	 * @param npcid
	 * @param loc
	 * @return
	 */
	public static L1MonsterInstance spawnParty(final L1NpcInstance master, final int npcid, final L1Location loc) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			final L1MonsterInstance mob = (L1MonsterInstance) npc;
			mob.setId(IdFactoryNpc.get().nextId());
			mob.setMap(loc.getMap());

			mob.setX(loc.getX());
			mob.setY(loc.getY());

			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());
			
			mob.setHeading(master.getHeading());
			
			mob.setMaster(master);

			// 設置副本編號 TODO
			mob.set_showId(master.get_showId());

			L1QuestUser q = WorldQuest.get().get(master.get_showId());
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);
			
			return mob;

		} catch (final Exception e) {
			_log.error("執行召喚指定對員發生異常!", e);
		}
		return null;
	}

	/**
	 * 依召L1Location喚指定NPC
	 * 
	 * @param npcid
	 * @param loc
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final L1Location loc) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());

			npc.setX(loc.getX());
			npc.setY(loc.getY());

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);
			
			npc.turnOnOffLight();
			
			return npc;

		} catch (final Exception e) {
			_log.error("執行分身召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 依召L1Location喚指定NPC<BR>
	 * 附加 副本編號
	 * 
	 * @param npcid
	 * @param loc
	 * @param heading
	 * @param showId
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final L1Location loc, final int heading, final int showId) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());

			npc.setX(loc.getX());
			npc.setY(loc.getY());

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			
			npc.setHeading(heading);

			// 設置副本編號 TODO
			npc.set_showId(showId);

			L1QuestUser q = WorldQuest.get().get(showId);
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);
			
			npc.turnOnOffLight();
			
			return npc;

		} catch (final Exception e) {
			_log.error("執行副本NPC召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚賭場參賽者
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @param gfxid
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, 
			final int x, final int y, final short m, final int h, 
			final int gfxid) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}
			
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(m);

			npc.setX(x);
			npc.setY(y);

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(h);

			npc.setTempCharGfx(gfxid);
			npc.setGfxId(gfxid);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			return npc;

		} catch (final Exception e) {
			_log.error("執行NPC召喚發生異常: " + npcid, e);
		}
		return null;
	}

	/**
	 * 法師技能(火牢)
	 * 
	 * @param cha 主人
	 * @param targetX 目標X位置
	 * @param targetY 目標Y位置
	 */
	public static void doSpawnFireWall(final L1Character cha, final int targetX, final int targetY) {
		L1SpawnUtil util = new L1SpawnUtil();
		util.doSpawnFireWallR(cha, targetX, targetY);
	}
	
	public void doSpawnFireWallR(final L1Character cha, final int targetX, final int targetY) {
		SpawnR2 spawn = new SpawnR2(cha, targetX, targetY);
		GeneralThreadPool.get().schedule(spawn, 0);
	}

	private class SpawnR2 implements Runnable {
		private final L1Character _cha;
		private final int _targetX;
		private final int _targetY;
		private SpawnR2(final L1Character cha, final int targetX, final int targetY) {
			_cha = cha;
			_targetX = targetX;
			_targetY = targetY;
		}

		@Override
		public void run() {
			try {
				final int npcid = 81157;// 法師技能(火牢)
				final L1Npc firewall = NpcTable.get().getTemplate(npcid); // 法師技能(火牢)
				final int duration = SkillsTable.get().getTemplate(L1SkillId.FIRE_WALL).getBuffDuration();
				//System.out.println("法師技能(火牢)firewall:"+firewall);
				
				if (firewall == null) {
					return;
				}
				
				// 判斷位置用物件
				L1Character base = _cha;
				for (int i = 0; i < 8; i++) {
					Thread.sleep(2);
					//System.out.println("法師技能(火牢)i:"+i);
					int tmp = 0;

					for (final L1Object objects : World.get().getVisibleObjects(_cha)) {
						if (objects == null) {// 對像為空
							continue;
						}
						// 同地點相同主人 tmp + 1
						if (objects instanceof L1EffectInstance) {
							final L1EffectInstance effect = (L1EffectInstance) objects;
							if (_cha != null) {
								if (effect.getMaster().equals(_cha)) {
									tmp++;
								}
							}
						}
						//System.out.println("同地點相同物件 tmp + 1:"+tmp);
					}
					if (tmp >= 24) {// 畫面內 同使用者 最多召喚24個火牢物件
						return;
					}
					
					final int a = base.targetDirection(_targetX, _targetY);
					int x = base.getX();
					int y = base.getY();

					// XY位置增加
					x += HEADING_TABLE_X[a];
					y += HEADING_TABLE_Y[a];

					//System.out.println("XY位置增加:"+x+"/"+y);
					if (!base.isAttackPosition(x, y, 1)) {
						x = base.getX();
						y = base.getY();
					}
					
					// 判斷座標上 是否已有相同NPCID物件
					final L1Location loc = new L1Location(x, y, _cha.getMapId());
					if (WorldEffect.get().isEffect(loc, npcid)) {
						continue;
					}
					
					final L1Map map = L1WorldMap.get().getMap(_cha.getMapId());
					if (!map.isArrowPassable(x, y, _cha.getHeading())) {
						//System.out.println("指定座標遠程攻擊是否能通過。:"+map.isArrowPassable(x, y, cha.getHeading()));
						break;
					}

					// 施展者 是PC
					if (_cha instanceof L1PcInstance) {
						L1PcInstance user = (L1PcInstance) _cha;
						base = spawnEffect(npcid, duration, x, y, user.getMapId(), user, L1SkillId.FIRE_WALL);
						
					} else {
						base = spawnEffect(npcid, duration, x, y, _cha.getMapId(), null, L1SkillId.FIRE_WALL);
					}
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
			}
		}
	}

	/**
	 * 技能效果動畫召喚
	 *
	 * @param npcId 對應的NPC編號
	 * @param time 存在時間(秒)
	 * @param locX 設置的座標X
	 * @param locY 設置的座標Y
	 * @param mapId 設置的地圖編號
	 * @param user 施展的PC
	 * @param skiiId 技能編號
	 * @return
	 */
	public static L1EffectInstance spawnEffect(final int npcId, final int time, final int locX,
			final int locY, final short mapId, final L1Character user, final int skiiId) {
		L1EffectInstance effect = null;

		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}
			
			effect = (L1EffectInstance) npc;
			
			effect.setId(IdFactoryNpc.get().nextId());
			effect.setGfxId(effect.getGfxId());
			
			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);
			
			if (user != null) {
				effect.setMaster(user);// 施展者
				// 設置副本編號 TODO
				effect.set_showId(user.get_showId());

				L1QuestUser q = WorldQuest.get().get(user.get_showId());
				if (q != null) {
					q.addNpc(npc);
               }
                
                // 20171122 墓碑
                if (effect.getNpcId() == 86126 && effect.getMapId() != 5153) {
					effect.setNameId(user.getName());
					effect.setTempLawful(user.getLawful());
				}
            }
			
			effect.setSkillId(skiiId);// 引用技能編號
			
			World.get().storeObject(effect);
			World.get().addVisibleObject(effect);

			effect.broadcastPacketAll(new S_NPCPack(effect));
			
            // 20171122 墓碑
            if (effect.getGfxId() == 6654) {
				effect.broadcastPacketAll(new S_DoActionGFX(npc.getId(), 4));
			}
			
  
            
			// 畫面認識
			for (L1PcInstance pc : World.get().getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
			}

			if (time > 0) {
				// 存在時間(秒)
				effect.set_spawnTime(time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return effect;
	}

	/**
	 * 技能效果動畫召喚
	 *
	 * @param npcId 對應的NPC編號
	 * @param time 存在時間(秒)
	 * @param locX 設置的座標X
	 * @param locY 設置的座標Y
	 * @param mapId 設置的地圖編號
	 * @param user 施展的PC
	 * @param skiiId 技能編號
	 * @param gfxid 外型編號
	 * @return
	 */
	public static L1EffectInstance spawnEffect(final int npcId, final int time, final int locX,
			final int locY, final short mapId, final L1Character user, final int skiiId, final int gfxid) {
		L1EffectInstance effect = null;

		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}
			
			effect = (L1EffectInstance) npc;
			
			effect.setId(IdFactoryNpc.get().nextId());
			effect.setGfxId(gfxid);
			effect.setTempCharGfx(gfxid);
			
			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);
			
			if (user != null) {
				effect.setMaster(user);// 施展者
				// 設置副本編號 TODO
				effect.set_showId(user.get_showId());

				L1QuestUser q = WorldQuest.get().get(user.get_showId());
				if (q != null) {
					q.addNpc(npc);
				}
			}
			
			effect.setSkillId(skiiId);// 引用技能編號
			
			World.get().storeObject(effect);
			World.get().addVisibleObject(effect);

			effect.broadcastPacketAll(new S_NPCPack(effect));
			// 畫面認識
			for (L1PcInstance pc : World.get().getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
			}

			if (time > 0) {
				// 存在時間(秒)
				effect.set_spawnTime(time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return effect;
	}

	/**
	 * 召喚門(副本專用)<BR>
	 * 門編號 51~55 由賭場使用<BR>
	 * 門編號 10000~10003 由任務不死族的叛徒 (法師30級以上官方任務)使用<BR>
	 * 門編號 10004 由任務拯救被幽禁的吉姆 (騎士30級以上官方任務)使用<BR>
	 * 門編號 10005~10007 由安塔瑞斯棲息地使用<BR>
	 * 門編號 10008~10010 由法利昂棲息地使用<BR>
	 * 門編號 10011~10013 由法利昂棲息地使用<BR>
	 * 門編號 10014~10036 哈汀副本<BR>
	 * 
	 * @param quest 任務
	 * @param doorId 門編號
	 * @param gfxid 外型
	 * @param x X
	 * @param y Y
	 * @param mapid 地圖編號
	 * @param direction 
	 * @return 
	 */
	public static L1DoorInstance spawnDoor(L1QuestUser quest, int doorId, int gfxid, int x, int y, short mapid, int direction) {
		final L1Npc l1npc = NpcTable.get().getTemplate(81158);
		if (l1npc != null) {
			final L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(l1npc);

			int objid = IdFactoryNpc.get().nextId();
			door.setId(objid);

			door.setDoorId(doorId);
			door.setGfxId(gfxid);
			door.setX(x);
			door.setY(y);
			door.setMap(mapid);
			door.setHomeX(x);
			door.setHomeY(y);
			door.setDirection(direction);
			switch (gfxid) {
			case 89:// \
				door.setLeftEdgeLocation(y);
				door.setRightEdgeLocation(y);
				break;
				
			case 88:// /
				if (mapid == 9000) {
					door.setLeftEdgeLocation(x - 1);
					door.setRightEdgeLocation(x + 1);
				} else {
					door.setLeftEdgeLocation(x - 1);
					door.setRightEdgeLocation(x + 1);
				}
				break;
				
			case 90:// 地面/
				door.setLeftEdgeLocation(x);
				door.setRightEdgeLocation(x + 1);
				break;
				
			case 7556:// 安塔瑞斯洞穴 \
				door.setLeftEdgeLocation(y - 1);
				door.setRightEdgeLocation(y + 3);
				break;
				
			case 7858:// 法利昂洞穴 /
				door.setLeftEdgeLocation(x - 2);
				door.setRightEdgeLocation(x + 3);
				break;
				
			case 7859:// 法利昂洞穴 \
				door.setLeftEdgeLocation(y - 2);
				door.setRightEdgeLocation(y + 3);
				break;
				
			default:
				door.setLeftEdgeLocation(y);
				door.setRightEdgeLocation(y);
				break;
			}

			door.setMaxHp(0);
			door.setCurrentHp(0);
			door.setKeeperId(0);
			if (quest != null) {
				// 副本編號
				door.set_showId(quest.get_id());
				quest.addNpc(door);
			}
			door.close();
			
			World.get().storeObject(door);
			World.get().addVisibleObject(door);
			
			return door;
		}
		return null;
	}

	/**
	 * 召喚景觀(副本專用)
	 * @param showid 副本編號
	 * @param gfxid 圖型編號
	 * @param x X
	 * @param y Y
	 * @param map MAP
	 * @param timeMillisToDelete
	 * @return 
	 */
	public static L1FieldObjectInstance spawn(int showid, int gfxid, int x, int y, int map, int timeMillisToDelete) {
		try {
			final L1FieldObjectInstance field = 
					(L1FieldObjectInstance) NpcTable.get().newNpcInstance(71081);

			if (field != null) {
				field.setId(IdFactoryNpc.get().nextId());
				field.setGfxId(gfxid);
				field.setTempCharGfx(gfxid);
				field.setMap((short) map);
				field.setX(x);
				field.setY(y);
				field.setHomeX(x);
				field.setHomeY(y);
				field.setHeading(5);
				field.set_showId(showid);

				L1QuestUser q = WorldQuest.get().get(showid);
				if (q != null) {
					q.addNpc(field);
				}
				
				World.get().storeObject(field);
				World.get().addVisibleObject(field);
				
				if (0 < timeMillisToDelete) {
					// 存在時間(秒)
					field.set_spawnTime(timeMillisToDelete);
				}
				return field;
			}

		} catch (final Exception e) {
			_log.error("執行景觀(副本專用)召喚發生異常", e);
		}
		return null;
	}
	/**
	 * 外掛檢測-召喚假GM 執行虛擬GM召喚
	 *
	 * @param pc
	 * @return
	 */
	public static L1NpcInstance spawnGM(final L1PcInstance pc) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(70726);
			npc.setNameId("$973");
			npc.setGfxId(6137);
			npc.setTempCharGfx(6137);
			npc.setMoveSpeed(320);

			npc.setId(IdFactory.get().nextId());
			npc.setMap(pc.getMapId());

			int tryCount = 0;
			do {
				tryCount++;
				npc.setX(pc.getX() + 1);
				npc.setY(pc.getY() + 1);
				if (npc.getMap().isInMap(npc.getLocation())
						&& npc.getMap().isPassable(npc.getLocation(), npc)) {
					break;
				}
				Thread.sleep(1);
			} while (tryCount < 50);

			if (tryCount >= 50) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getHeading());
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(pc.getHeading());

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			return npc;

		} catch (final Exception e) {
			_log.error("執行虛擬GM召喚發生異常!", e);
		}
		return null;
	}
}
