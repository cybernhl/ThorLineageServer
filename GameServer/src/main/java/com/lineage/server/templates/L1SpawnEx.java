package com.lineage.server.templates;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Random;

/**
 * 召喚控制項
 * 製作中 daien 2012-05-11
 * @author daien
 *
 */
public abstract class L1SpawnEx implements Serializable {
	
	private static final long serialVersionUID = -755041639984207871L;

	protected static final Random _random = new Random();

	protected L1Npc _template;// NPC數據
	
	protected int _id;// 召喚編號
	
	protected int _tmplocx;// 本次召喚X座標
	
	protected int _tmplocy;// 本次召喚Y座標
	
	protected short _tmpmapid;// 本次召喚地圖編號
	
	protected int _maximumCount;// 數量
	
	protected int _npcid;// NPC編號
	
	protected int _groupId;// 隊伍編號
	
	protected int _locx1;// 座標X1
	
	protected int _locy1;// 座標Y1
	
	protected int _locx2;// 座標X2
	
	protected int _locy2;// 座標Y2
	
	protected int _heading;// 面向
	
	protected short _mapid;// 地圖編號
	
	protected Timestamp _nextSpawnTime = null;// 下次召喚時間
	
	protected int _spawnInterval = 0;// 下次召喚延遲(單位:分鐘)
	
	protected int _existTime = 0;// 存在時間(單位:分鐘)

	public abstract L1Npc get_template();

	public abstract int get_id();

	public abstract void set_id(int id);

	public abstract int get_maximumCount();

	public abstract void set_maximumCount(int maximumCount);

	public abstract int get_npcid();

	public abstract void set_npcid(int npcid);

	public abstract int get_groupId();

	public abstract void set_groupId(int groupId);

	public abstract int get_tmplocx();

	public abstract void set_tmplocx(int tmplocx);

	public abstract int get_tmplocy();

	public abstract void set_tmplocy(int tmplocy);

	public abstract short get_tmpmapid();

	public abstract void set_tmpmapid(short tmpmapid);

	public abstract int get_locx1();

	public abstract void set_locx1(int locx1);

	public abstract int get_locy1();

	public abstract void set_locy1(int locy1);

	public abstract int get_locx2();

	public abstract void set_locx2(int locx2);

	public abstract int get_locy2();

	public abstract void set_locy2(int locy2);

	public abstract int get_heading();

	public abstract void set_heading(int heading);

	public abstract short get_mapid();

	public abstract void set_mapid(short mapid);

	public abstract Timestamp get_nextSpawnTime();

	public abstract void set_nextSpawnTime(Timestamp nextSpawnTime);

	public abstract long get_spawnInterval();

	public abstract void set_spawnInterval(int spawnInterval);

	public abstract int get_existTime();

	public abstract void set_existTime(int existTime);

	/**
	 * 重新召喚
	 * @param objectId 世界物件編號
	 */
	public abstract void doSpawn(final int objectId);
	
}
