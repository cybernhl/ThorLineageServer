package com.lineage.server.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1BowInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTime;
import com.lineage.server.model.gametime.L1GameTimeAdapter;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1SpawnTime;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.npc.NpcSpawnBossTimer;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

/**
 * 召喚控制項
 * 
 * @author daien
 * 
 */
public class L1Spawn extends L1GameTimeAdapter {

    private static final Log _log = LogFactory.getLog(L1Spawn.class);

    private final L1Npc _template;

    private int _id;
    private String _location;
    private int _maximumCount;
    private int _npcid;
    private int _groupId;
    private int _locx;
    private int _locy;
    private int _tmplocx;// 本次召喚X座標
    private int _tmplocy;// 本次召喚Y座標
    private short _tmpmapid;
    private int _randomx;
    private int _randomy;
    private int _locx1;
    private int _locy1;
    private int _locx2;
    private int _locy2;
    private int _heading;
    private int _minRespawnDelay;
    private int _maxRespawnDelay;
    private short _mapid;
    private boolean _respaenScreen;
    private int _movementDistance;
    private boolean _rest;
    private int _spawnType;
    private int _delayInterval;
    private L1SpawnTime _time;
    private Calendar _nextSpawnTime = null;
    private int next_spawn_time_random;
    private long _spawnInterval = 0;
    private int _existTime = 0;

    private Map<Integer, Point> _homePoint = null; // initspawn個

    private List<L1NpcInstance> _mobs = new ArrayList<L1NpcInstance>();

    private Random _random = new Random();

    private String _name;

    private class SpawnTask implements Runnable {

        private int _spawnNumber;

        private int _objectId;

        private long _delay;

        /**
         * 
         * @param spawnNumber
         *            召喚管理編號
         * @param objectId
         *            世界物件編號
         * @param delay
         *            延遲時間
         */
        private SpawnTask(final int spawnNumber, final int objectId, long delay) {
            this._spawnNumber = spawnNumber;
            this._objectId = objectId;
            this._delay = delay;
        }

        /**
         * 啟動線程
         */
        public void getStart() {
            GeneralThreadPool.get().schedule(this, this._delay);
        }

        @Override
        public void run() {
            L1Spawn.this.doSpawn(this._spawnNumber, this._objectId);
        }
    }

    public L1Spawn(final L1Npc mobTemplate) {
        this._template = mobTemplate;
    }

    public String getName() {
        return this._name;
    }

    public void setName(final String name) {
        this._name = name;
    }

    public short getMapId() {
        return this._mapid;
    }

    public void setMapId(final short _mapid) {
        this._mapid = _mapid;
    }

    public boolean isRespawnScreen() {
        return this._respaenScreen;
    }

    public void setRespawnScreen(final boolean flag) {
        this._respaenScreen = flag;
    }

    /**
     * 移動距離
     * 
     * @return
     */
    public int getMovementDistance() {
        return this._movementDistance;
    }

    /**
     * 移動距離
     * 
     * @param i
     */
    public void setMovementDistance(final int i) {
        this._movementDistance = i;
    }

    /**
     * 數量
     * 
     * @return
     */
    public int getAmount() {
        return this._maximumCount;
    }

    /**
     * 隊伍召喚編號
     * 
     * @return
     */
    public int getGroupId() {
        return this._groupId;
    }

    public int getId() {
        return this._id;
    }

    public String getLocation() {
        return this._location;
    }

    public int getLocX() {
        return this._locx;
    }

    public int getLocY() {
        return this._locy;
    }

    public int getNpcId() {
        return this._npcid;
    }

    public int getHeading() {
        return this._heading;
    }

    public int getRandomx() {
        return this._randomx;
    }

    public int getRandomy() {
        return this._randomy;
    }

    public int getLocX1() {
        return this._locx1;
    }

    public int getLocY1() {
        return this._locy1;
    }

    public int getLocX2() {
        return this._locx2;
    }

    public int getLocY2() {
        return this._locy2;
    }

    /**
     * 召喚延遲
     * 
     * @return 單位:秒
     */
    public int getMinRespawnDelay() {
        return this._minRespawnDelay;
    }

    /**
     * 召喚延遲
     * 
     * @return 單位:秒
     */
    public int getMaxRespawnDelay() {
        return this._maxRespawnDelay;
    }

    /**
     * 數量
     * 
     * @param amount
     */
    public void setAmount(final int amount) {
        this._maximumCount = amount;
    }

    public void setId(final int id) {
        this._id = id;
    }

    /**
     * 隊伍召喚編號
     * 
     * @param i
     */
    public void setGroupId(final int i) {
        this._groupId = i;
    }

    public void setLocation(final String location) {
        this._location = location;
    }

    public void setLocX(final int locx) {
        this._locx = locx;
    }

    public void setLocY(final int locy) {
        this._locy = locy;
    }

    public void setNpcid(final int npcid) {
        this._npcid = npcid;
    }

    public void setHeading(final int heading) {
        this._heading = heading;
    }

    /**
     * 召喚隨機範圍
     * 
     * @param randomx
     */
    public void setRandomx(final int randomx) {
        this._randomx = randomx;
    }

    /**
     * 召喚隨機範圍
     * 
     * @param randomy
     */
    public void setRandomy(final int randomy) {
        this._randomy = randomy;
    }

    public void setLocX1(final int locx1) {
        this._locx1 = locx1;
    }

    public void setLocY1(final int locy1) {
        this._locy1 = locy1;
    }

    public void setLocX2(final int locx2) {
        this._locx2 = locx2;
    }

    public void setLocY2(final int locy2) {
        this._locy2 = locy2;
    }

    /**
     * 召喚延遲
     * 
     * @param i
     *            單位:秒
     */
    public void setMinRespawnDelay(final int i) {
        this._minRespawnDelay = i;
    }

    /**
     * 召喚延遲
     * 
     * @param i
     *            單位:秒
     */
    public void setMaxRespawnDelay(final int i) {
        this._maxRespawnDelay = i;
    }

    public int getTmpLocX() {
        return this._tmplocx;
    }

    public int getTmpLocY() {
        return this._tmplocy;
    }

    public short getTmpMapid() {
        return this._tmpmapid;
    }

    /**
     * 抵達召喚時間
     * 
     * @param npcTemp
     * @param next_spawn_time
     * @return
     */
    private boolean isSpawnTime(L1NpcInstance npcTemp) {
        if (_nextSpawnTime != null) {
            // 取得目前時間
            final Calendar cals = Calendar.getInstance();
            long nowTime = System.currentTimeMillis();
            cals.setTimeInMillis(nowTime);

            if (cals.after(_nextSpawnTime)) {
                // System.out.println("抵達召喚時間");
                return true;

            } else {
                if (NpcSpawnBossTimer.MAP.get(npcTemp) == null) {
                    long spawnTime = _nextSpawnTime.getTimeInMillis();
                    // 加入等候清單(5秒誤差補正)
                    long spa = ((spawnTime - nowTime) / 1000) + 5;
                    // 加入等候清單(5秒誤差補正)
                    NpcSpawnBossTimer.MAP.put(npcTemp, spa);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 下次召喚時間
     * 
     * @return
     */
    public Calendar get_nextSpawnTime() {
        return _nextSpawnTime;
    }

    /**
     * 下次召喚時間
     * 
     * @param next_spawn_time
     */
    public void set_nextSpawnTime(Calendar next_spawn_time) {
        _nextSpawnTime = next_spawn_time;
    }

    /**
     * 差異時間(單位:分鐘)
     * 
     * @param spawn_interval
     */
    public void set_spawnInterval(long spawn_interval) {
        _spawnInterval = spawn_interval;
    }

    /**
     * 差異時間(單位:分鐘)
     * 
     * @param spawn_interval
     * @return
     */
    public long get_spawnInterval() {
        return _spawnInterval;
    }

    /**
     * 存在時間(單位:分鐘)
     * 
     * @param exist_time
     */
    public void set_existTime(int exist_time) {
        _existTime = exist_time;
    }
    public int get_existTime() {
        return _existTime;
    }

    private int calcRespawnDelay() {
        int respawnDelay = this._minRespawnDelay * 1000;
        if (this._delayInterval > 0) {
            respawnDelay += _random.nextInt(this._delayInterval) * 1000;
        }

        final L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();
        if ((this._time != null) && !this._time.getTimePeriod().includes(currentTime)) { // 指定時間外指定時間時間足
            long diff = (this._time.getTimeStart().getTime() - currentTime.toTime().getTime());
            if (diff < 0) {
                diff += 24 * 1000L * 3600L;
            }
            diff /= 6; // real time to game time
            respawnDelay = (int) diff;
        }
        return respawnDelay;
    }

    /**
     * SpawnTask的啟動
     * 
     * @param spawnNumber
     *            管理編號
     * @param objectId
     *            世界物件編號
     */
    public void executeSpawnTask(final int spawnNumber, final int objectId) {
        if (_nextSpawnTime != null) {
            this.doSpawn(spawnNumber, objectId);

        } else {
            final SpawnTask task = new SpawnTask(spawnNumber, objectId, this.calcRespawnDelay());
            task.getStart();
        }
    }

    private boolean _initSpawn = false;

    private boolean _spawnHomePoint;

    public void init() {
        if ((this._time != null) && this._time.isDeleteAtEndTime()) {
            // 時間外削除指定、時間經過通知受。
            L1GameTimeClock.getInstance().addListener(this);
        }
        this._delayInterval = this._maxRespawnDelay - this._minRespawnDelay;
        this._initSpawn = true;
        // 持
        if (ConfigAlt.SPAWN_HOME_POINT && (ConfigAlt.SPAWN_HOME_POINT_COUNT <= this.getAmount()) && (ConfigAlt.SPAWN_HOME_POINT_DELAY >= this.getMinRespawnDelay())
                && this.isAreaSpawn()) {
            this._spawnHomePoint = true;
            this._homePoint = new HashMap<Integer, Point>();
        }

        int spawnNum = 0;
        while (spawnNum < this._maximumCount) {
            // spawnNum1～maxmumCount
            this.doSpawn(++spawnNum);
        }
        this._initSpawn = false;
    }

    /**
     * 場合、spawnNumber基spawn。 以外場合、spawnNumber未使用。
     */
    protected void doSpawn(final int spawnNumber) { // 初期配置
        // 指定時間外、次spawn予約終。
        if ((this._time != null) && !this._time.getTimePeriod().includes(L1GameTimeClock.getInstance().currentTime())) {
            this.executeSpawnTask(spawnNumber, 0);
            return;
        }
        this.doSpawn(spawnNumber, 0);
    }

    /**
     * 重新召喚
     * 
     * @param spawnNumber
     *            召喚管理編號
     * @param objectId
     *            世界物件編號
     */
    protected void doSpawn(final int spawnNumber, final int objectId) {
        _tmplocx = 0;
        _tmplocy = 0;
        _tmpmapid = 0;

        L1NpcInstance npcTemp = null;
        try {
            int newlocx = this.getLocX();
            int newlocy = this.getLocY();
            int tryCount = 0;

            npcTemp = NpcTable.get().newNpcInstance(this._template);
            synchronized (this._mobs) {
                this._mobs.add(npcTemp);
            }

            if (objectId == 0) {
                npcTemp.setId(IdFactoryNpc.get().nextId());

            } else {
                npcTemp.setId(objectId); // 世界物件編號再利用
            }

            if ((0 <= this.getHeading()) && (this.getHeading() <= 7)) {
                npcTemp.setHeading(this.getHeading());
            } else {
                // heading值正
                npcTemp.setHeading(5);
            }

            // npc召喚地圖換位
            final int npcId = npcTemp.getNpcTemplate().get_npcId();
            if ((npcId == 45488) && (this.getMapId() == 9)) { // 卡士伯
                npcTemp.setMap((short) (this.getMapId() + _random.nextInt(2)));

            } else if ((npcId == 45601) && (this.getMapId() == 11)) { // 死亡騎士
                npcTemp.setMap((short) (this.getMapId() + _random.nextInt(3)));

            } else {
                npcTemp.setMap(this.getMapId());
            }

            npcTemp.setMovementDistance(this.getMovementDistance());
            npcTemp.setRest(this.isRest());

            // 設置召喚的XY座標位置
            while (tryCount <= 50) {

                if (this.isAreaSpawn()) { // 區域召喚
                    Point pt = null;
                    if (this._spawnHomePoint && (null != (pt = this._homePoint.get(spawnNumber)))) { // 元再出現場合
                        final L1Location loc = new L1Location(pt, this.getMapId()).randomLocation(ConfigAlt.SPAWN_HOME_POINT_RANGE, false);
                        newlocx = loc.getX();
                        newlocy = loc.getY();

                    } else {
                        final int rangeX = this.getLocX2() - this.getLocX1();
                        final int rangeY = this.getLocY2() - this.getLocY1();
                        newlocx = _random.nextInt(rangeX) + this.getLocX1();
                        newlocy = _random.nextInt(rangeY) + this.getLocY1();
                    }

                    if (tryCount > 49) { // 已經召喚失敗次數
                        if (_nextSpawnTime == null) {
                            newlocx = this.getLocX();
                            newlocy = this.getLocY();

                        } else {
                            // 延後5秒
                            final SpawnTask task = new SpawnTask(spawnNumber, npcTemp.getId(), 5000L);
                            task.getStart();
                            return;
                        }
                    }

                } else if (this.isRandomSpawn()) { // 範圍召喚
                    newlocx = (this.getLocX() + ((int) (Math.random() * this.getRandomx()) - (int) (Math.random() * this.getRandomx())));
                    newlocy = (this.getLocY() + ((int) (Math.random() * this.getRandomy()) - (int) (Math.random() * this.getRandomy())));

                } else { // 定點召喚
                    newlocx = this.getLocX();
                    newlocy = this.getLocY();
                }

                if (this.getSpawnType() == SPAWN_TYPE_PC_AROUND) {// 周邊PC躲避
                    final L1Location loc = new L1Location(newlocx, newlocy, this.getMapId());
                    // 13格內PC物件
                    final ArrayList<L1PcInstance> pcs = World.get().getVisiblePc(loc);
                    if (pcs.size() > 0) {
                        final L1Location newloc = loc.randomLocation(20, false);
                        newlocx = newloc.getX();
                        newlocy = newloc.getY();
                    }
                }

                npcTemp.setX(newlocx);
                npcTemp.setHomeX(newlocx);
                npcTemp.setY(newlocy);
                npcTemp.setHomeY(newlocy);

                if (_nextSpawnTime == null) {
                    if (npcTemp.getMap().isInMap(npcTemp.getLocation()) && npcTemp.getMap().isPassable(npcTemp.getLocation(), npcTemp)) {
                        if (npcTemp instanceof L1MonsterInstance) {
                            if (this.isRespawnScreen()) {
                                break;
                            }

                            // 13格內PC物件
                            final ArrayList<L1PcInstance> pcs = World.get().getVisiblePc(npcTemp.getLocation());
                            if (pcs.size() == 0) {
                                break;
                            }
                            /*
                             * final L1MonsterInstance mobtemp =
                             * (L1MonsterInstance) npcTemp; if
                             * (World.get().getVisiblePlayer(mobtemp).size() ==
                             * 0) { break; }
                             */
                            // 畫面內具有PC物件 延後5秒
                            final SpawnTask task = new SpawnTask(spawnNumber, npcTemp.getId(), 5000L);
                            task.getStart();
                            return;
                        }
                    }

                } else {
                    // 座標可通行決定召喚位置
                    if (npcTemp.getMap().isPassable(npcTemp.getLocation(), npcTemp)) {
                        break;
                    }
                }
                tryCount++;
            }

            if (npcTemp instanceof L1MonsterInstance) {
                ((L1MonsterInstance) npcTemp).initHide();
            }

            npcTemp.setSpawn(this);
            npcTemp.setreSpawn(true);
            npcTemp.setSpawnNumber(spawnNumber); // L1Spawn管理番號(使用)
            if (this._initSpawn && this._spawnHomePoint) { // 初期配置設定
                final Point pt = new Point(npcTemp.getX(), npcTemp.getY());
                this._homePoint.put(spawnNumber, pt); // 保存point再出現時使
            }

            // 具備時間函數以時間為準
            if (_nextSpawnTime != null) {
                if (!isSpawnTime(npcTemp)) {
                    return;
                }
            }

            // 地獄不掉落物品
            if (npcTemp instanceof L1MonsterInstance) {
                final L1MonsterInstance mob = (L1MonsterInstance) npcTemp;
                if (mob.getMapId() == 666) {
                    mob.set_storeDroped(true);
                }
            }

            // 招換巴風特傳出玩家到定點
            if ((npcId == 45573) && (npcTemp.getMapId() == 2)) { // 巴風特
                for (final L1PcInstance pc : World.get().getAllPlayers()) {
                    if (pc.getMapId() == 2) {
                        L1Teleport.teleport(pc, 32664, 32797, (short) 2, 0, true);
                    }
                }
            }

            // 招換冰人惡魔傳出玩家到定點
            if (((npcId == 46142) && (npcTemp.getMapId() == 73)) || ((npcId == 46141)// 冰人惡魔
                    && (npcTemp.getMapId() == 74))) {
                for (final L1PcInstance pc : World.get().getAllPlayers()) {
                    if ((pc.getMapId() >= 72) && (pc.getMapId() <= 74)) {
                        L1Teleport.teleport(pc, 32840, 32833, (short) 72, pc.getHeading(), true);
                    }
                }
            }

            doCrystalCave(npcId);

            World.get().storeObject(npcTemp);
            World.get().addVisibleObject(npcTemp);

            if (npcTemp instanceof L1MonsterInstance) {
                final L1MonsterInstance mobtemp = (L1MonsterInstance) npcTemp;
                if (!this._initSpawn && (mobtemp.getHiddenStatus() == 0)) {
                    mobtemp.onNpcAI(); // AI啟用
                }

                if (_existTime > 0) {
                    // 存在時間(秒)
                    mobtemp.set_spawnTime(_existTime * 60);
                }
            }

            // 具備NPC隊伍
            if (this.getGroupId() != 0) {
                // 召喚隊伍成員
                L1MobGroupSpawn.getInstance().doSpawn(npcTemp, this.getGroupId(), this.isRespawnScreen(), this._initSpawn);
            }

            npcTemp.turnOnOffLight();
            npcTemp.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 開始
            
			// added by terry0412
			if (isBroadcast() && getBroadcastInfo() != null
					&& !getBroadcastInfo().isEmpty()) {
				World.get().broadcastPacketToAll(
						new S_SystemMessage(String.format(getBroadcastInfo(),
								npcTemp.getName())));
			}

            _tmplocx = newlocx;
            _tmplocy = newlocy;
            _tmpmapid = npcTemp.getMapId();

            boolean setPassable = true;// 是否設置障礙
            if (npcTemp instanceof L1DollInstance) {// 魔法娃娃
                setPassable = false;
            }
            if (npcTemp instanceof L1EffectInstance) {// 效果
                setPassable = false;
            }
            if (npcTemp instanceof L1FieldObjectInstance) {// 景觀
                setPassable = false;
            }
            if (npcTemp instanceof L1FurnitureInstance) {// 傢俱
                setPassable = false;
            }
            if (npcTemp instanceof L1DoorInstance) {// 門
                setPassable = false;
            }
            if (npcTemp instanceof L1BowInstance) {// 固定攻擊器
                setPassable = false;
            }
            if (setPassable) {
                L1WorldMap.get().getMap(npcTemp.getMapId()).setPassable(npcTemp.getX(), npcTemp.getY(), false, 2);
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setRest(final boolean flag) {
        this._rest = flag;
    }

    public boolean isRest() {
        return this._rest;
    }

    // private static final int SPAWN_TYPE_NORMAL = 0;

    private static final int SPAWN_TYPE_PC_AROUND = 1;

    // private static final int PC_AROUND_DISTANCE = 30;

    private int getSpawnType() {
        return this._spawnType;
    }

    /**
     * 召喚模式 0:無 1:閃避PC
     * 
     * @param type
     */
    public void setSpawnType(final int type) {
        this._spawnType = type;
    }

    /**
     * 區域召喚
     * 
     * @return
     */
    private boolean isAreaSpawn() {
        return (this.getLocX1() != 0) && (this.getLocY1() != 0) && (this.getLocX2() != 0) && (this.getLocY2() != 0);
    }

    /**
     * 範圍召喚
     * 
     * @return
     */
    private boolean isRandomSpawn() {
        return (this.getRandomx() != 0) || (this.getRandomy() != 0);
    }

    public L1SpawnTime getTime() {
        return this._time;
    }

    public void setTime(final L1SpawnTime time) {
        this._time = time;
    }

    @Override
    public void onMinuteChanged(final L1GameTime time) {
        if (this._time.getTimePeriod().includes(time)) {
            return;
        }
        synchronized (this._mobs) {
            if (this._mobs.isEmpty()) {
                return;
            }
            // 指定時間外削除
            for (final L1NpcInstance mob : this._mobs) {
                mob.setCurrentHpDirect(0);
                mob.setDead(true);
                mob.setStatus(ActionCodes.ACTION_Die);
                mob.deleteMe();
            }
            this._mobs.clear();
        }
    }

    public static void doCrystalCave(final int npcId) {
        final int[] npcId2 = { 46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152 };
        final int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010 };

        for (int i = 0; i < npcId2.length; i++) {
            if (npcId == npcId2[i]) {
                closeDoorInCrystalCave(doorId[i]);
            }
        }
    }

    private static void closeDoorInCrystalCave(final int doorId) {
        for (final L1Object object : World.get().getObject()) {
            if (object instanceof L1DoorInstance) {
                final L1DoorInstance door = (L1DoorInstance) object;
                if (door.getDoorId() == doorId) {
                    door.close();
                }
            }
        }
    }
	private boolean _isBroadcast;

	public final boolean isBroadcast() {
		return this._isBroadcast;
	}

	public final void setBroadcast(final boolean isBroadcast) {
		this._isBroadcast = isBroadcast;
	}

	private String _broadcastInfo;

	public final String getBroadcastInfo() {
		return this._broadcastInfo;
	}

	public final void setBroadcastInfo(final String broadcastInfo) {
		this._broadcastInfo = broadcastInfo;
	}


    /**
     * 取得重生時間隨機百分比.
     * 
     * @return 隨機百分比
     */
    public int getNext_spawn_time_random() {
        return next_spawn_time_random;
    }

    /**
     * 設置重生時間隨機百分比.
     * 
     * @param next_spawn_time_random
     *            - 要設置的隨機百分比
     */
    public void setNext_spawn_time_random(int next_spawn_time_random) {
        this.next_spawn_time_random = next_spawn_time_random;
    }
}
