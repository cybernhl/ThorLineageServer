package com.lineage.server.timecontroller.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.CastleWarGiftTable;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.L1WarSpawn;
import com.lineage.server.model.Instance.L1CrownInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.serverpackets.S_PacketBoxWar;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 城戰計時軸
 * @author dexc
 *
 */
public class ServerWarExecutor {

	private static final Log _log = LogFactory.getLog(ServerWarExecutor.class);

	private static ServerWarExecutor _instance;
	
	private L1Castle[] _l1castle = new L1Castle[8];
	
	/**
	 * 攻城開始時間
	 */
	private Calendar[] _war_start_time = new Calendar[8];
	
	/**
	 * 攻城結束時間
	 */
	private Calendar[] _war_end_time = new Calendar[8];
	
	/**
	 * 是否在攻城期間
	 */
	private boolean[] _is_now_war = new boolean[8];

	/**
	 * 城堡名稱
	 */
	private String[] _castleName = new String[]{
			"肯特","妖魔","風木","奇巖","海音","侏儒","亞丁","狄亞得要塞"
	};
	
	private ServerWarExecutor() {
		for (int i = 0; i < this._l1castle.length; i++) {
			this._l1castle[i] = CastleReading.get().getCastleTable(i + 1);
			this._war_start_time[i] = this._l1castle[i].getWarTime();
			this._war_end_time[i] = (Calendar) this._l1castle[i].getWarTime().clone();
			this._war_end_time[i].add(ConfigAlt.ALT_WAR_TIME_UNIT, ConfigAlt.ALT_WAR_TIME);
		}
	}

	public static ServerWarExecutor get() {
		if (_instance == null) {
			_instance = new ServerWarExecutor();
		}
		return _instance;
	}

	/**
	 * 目前時間
	 * @return
	 */
	public Calendar getRealTime() {
		final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	/**
	 * 是否為攻城期間
	 * @param castle_id
	 * @return
	 */
	public boolean isNowWar(final int castle_id) {
		return this._is_now_war[castle_id - 1];
	}

	/**
	 * 設置攻城時間(GM命令)
	 * @param castle_id
	 * @param calendar
	 */
	public void setWarTime(final int castle_id, final Calendar calendar) {
		_war_start_time[castle_id - 1] = (Calendar) calendar.clone();
	}

	/**
	 * 設置攻城結束時間(GM命令)
	 * @param castle_id
	 * @param calendar
	 */
	public void setEndWarTime(final int castle_id, final Calendar calendar) {
		_war_end_time[castle_id - 1] = (Calendar) calendar.clone();
		_war_end_time[castle_id - 1].add(ConfigAlt.ALT_WAR_TIME_UNIT, ConfigAlt.ALT_WAR_TIME);
	}

	/**
	 * 發佈訊息給予上線玩家
	 * @param player
	 */
	public void checkCastleWar(final L1PcInstance player) {
		for (int i = 0; i < 8; i++) {
			if (this._is_now_war[i]) {
				// (641) %s的攻城戰正在進行中。
				player.sendPackets(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_GOING, i + 1));
			}
		}
	}

	/**
	 * 戰爭中城堡數量
	 * @return
	 */
	public int checkCastleWar() {
		int x = 0;
		for (int i = 0; i < 8; i++) {
			if (this._is_now_war[i]) {
				x++;
			}
		}
		return x;
	}
	
	/**
	 * 攻城時間檢測
	 */
	protected void checkWarTime() {
		try {
			for (int i = 0; i < 8; i++) {// i + 1 = 城堡編號
				final Calendar now = getRealTime();
				
				if (this._war_start_time[i].before(now) // 戰爭開始
						&& this._war_end_time[i].after(now)) {
					if (this._is_now_war[i] == false) {
						this._is_now_war[i] = true;
						
						// 歸0殺人次數
						CharOtherReading.get().tam();
						
						// 召喚戰爭範圍旗幟
						final L1WarSpawn warspawn = new L1WarSpawn();
						
						warspawn.SpawnFlag(i + 1);
						// 城門修理閉
						for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
							if (L1CastleLocation.checkInWarArea(i + 1, door)) {
								door.repairGate();
							}
						}

						final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						final String time = sdf.format(this._war_start_time[i].getTime());
						_log.info(_castleName[i] + " 的攻城戰開始。時間: " + time);

						// (639) %s的攻城戰開始。
						World.get().broadcastPacketToAll(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_BEGIN, i + 1));
						int[] loc = new int[3];
						for (final L1PcInstance pc : World.get().getAllPlayers()) {
							final int castleId = i + 1;
							if (L1CastleLocation.checkInWarArea(castleId, pc) && !pc.isGm()) { // 戰爭範圍旗幟內
								final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
								if (clan != null) {
									if (clan.getCastleId() == castleId) { // 城盟成員
										continue;
									}
								}
								loc = L1CastleLocation.getGetBackLoc(castleId);
								L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
							}
						}
					}
					
				} else if (this._war_end_time[i].before(now)) { // 攻城戰結束
					if (this._is_now_war[i] == true) {
						this._is_now_war[i] = false;
						// (640) %s的攻城戰結束。
						World.get().broadcastPacketToAll(new S_PacketBoxWar(S_PacketBoxWar.MSG_WAR_END, i + 1));
						this._war_start_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL);
						this._war_end_time[i].add(ConfigAlt.ALT_WAR_INTERVAL_UNIT, ConfigAlt.ALT_WAR_INTERVAL);
						this._l1castle[i].setTaxRate(10); // 稅率10%
						this._l1castle[i].setPublicMoney(0); // 公金

						CastleReading.get().updateCastle(this._l1castle[i]);

						final int castle_id = i + 1;
						
						final List<L1War> list = WorldWar.get().getWarList();
						for (L1War war : list) {
							if (war.get_castleId() == castle_id) {
								war.ceaseCastleWar();// 城堡戰爭時間終止,防禦方獲勝
							}
						}
						
						for (final L1Object l1object : World.get().getObject()) {
							// 攻城戰旗幟消除
							if (l1object instanceof L1FieldObjectInstance) {
								final L1FieldObjectInstance flag = (L1FieldObjectInstance) l1object;
								if (L1CastleLocation.checkInWarArea(castle_id, flag)) {
									flag.deleteMe();
								}
							}
							// 地面王冠物件刪除
							if (l1object instanceof L1CrownInstance) {
								final L1CrownInstance crown = (L1CrownInstance) l1object;
								if (L1CastleLocation.checkInWarArea(castle_id, crown)) {
									crown.deleteMe();
								}
							}
							// 守護者之塔消除
							if (l1object instanceof L1TowerInstance) {
								final L1TowerInstance tower = (L1TowerInstance) l1object;
								if (L1CastleLocation.checkInWarArea(castle_id, tower)) {
									tower.deleteMe();
								}
							}
						}
						// 重新召換守護者之塔
						final L1WarSpawn warspawn = new L1WarSpawn();
						warspawn.spawnTower(castle_id);

						// 城門元戾
						for (final L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
							if (L1CastleLocation.checkInWarArea(castle_id, door)) {
								door.repairGate();
							}
						}
						
						// 戰爭結束訊息
						World.get().broadcastPacketToAll(new S_PacketBoxWar());

						final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						final String time = sdf.format(now.getTime());
						_log.info(_castleName[i] + " 的攻城戰結束。時間: " + time);
						
						// 攻城獎勵
						CastleWarGiftTable.get().get_gift(castle_id);
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
