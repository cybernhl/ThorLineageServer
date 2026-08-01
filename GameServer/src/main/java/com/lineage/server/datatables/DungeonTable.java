package com.lineage.server.datatables;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Inn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.Teleportation;

/**
 * 地圖切換點設置
 * 
 * @author dexc
 * 
 */
public class DungeonTable {

	private static final Log _log = LogFactory.getLog(DungeonTable.class);

	private static DungeonTable _instance = null;

	private static Map<String, NewDungeon> _dungeonMap = new HashMap<String, NewDungeon>();

	private enum DungeonType {
		NONE, SHIP_FOR_FI, SHIP_FOR_HEINE, SHIP_FOR_PI, SHIP_FOR_HIDDENDOCK, SHIP_FOR_GLUDIN, SHIP_FOR_TI, TALKING_ISLAND_HOTEL, GLUDIO_HOTEL, SILVER_KNIGHT_HOTEL, WINDAWOOD_HOTEL, HEINE_HOTEL, GIRAN_HOTEL, OREN_HOTEL, PIRATE_ISLAND
	};

	public static DungeonTable get() {
		if (_instance == null) {
			_instance = new DungeonTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			cn = DatabaseFactory.get().getConnection();

			ps = cn.prepareStatement("SELECT * FROM `dungeon`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int srcMapId = rs.getInt("src_mapid");
				final int srcX = rs.getInt("src_x");
				final int srcY = rs.getInt("src_y");
				final String key = new StringBuilder().append(srcMapId)
						.append(srcX).append(srcY).toString();
				final int newX = rs.getInt("new_x");
				final int newY = rs.getInt("new_y");
				final int newMapId = rs.getInt("new_mapid");
				final int heading = rs.getInt("new_heading");
				final String note = rs.getString("note");
				DungeonType dungeonType = DungeonType.NONE;
				if (srcX == 33423 && srcY == 33502 && srcMapId == 4 // 往遺忘之島的船
						|| srcX == 33424 && srcY == 33502 && srcMapId == 4
						|| srcX == 33425 && srcY == 33502 && srcMapId == 4
						|| srcX == 33426 && srcY == 33502 && srcMapId == 4
						|| srcX == 33427 && srcY == 33502 && srcMapId == 4
						|| srcX == 33428 && srcY == 33502 && srcMapId == 4
						|| srcX == 33429 && srcY == 33502 && srcMapId == 4
						|| srcX == 32735 && srcY == 32794 && srcMapId == 83) { // FI行きの船->ハイネ船着場
					dungeonType = DungeonType.SHIP_FOR_FI;
				} else if ((((srcX == 32935) || (srcX == 32936) || (srcX == 32937))
						&& (srcY == 33058) && (srcMapId == 70 // FI船着場->ハイネ行きの船
				))
						|| (((srcX == 32732) || (srcX == 32733)
								|| (srcX == 32734) || (srcX == 32735))
								&& (srcY == 32796) && (srcMapId == 84))) { // ハイネ行きの船->FI船着場
					dungeonType = DungeonType.SHIP_FOR_HEINE;
				} else if ((((srcX == 32750) || (srcX == 32751) || (srcX == 32752))
						&& (srcY == 32874) && (srcMapId == 445 // 隠された船着場->海賊島行きの船
				))
						|| (((srcX == 32731) || (srcX == 32732) || (srcX == 32733))
								&& (srcY == 32796) && (srcMapId == 447))) { // 海賊島行きの船->隠された船着場
					dungeonType = DungeonType.SHIP_FOR_PI;
				} else if ((((srcX == 32296) || (srcX == 32297) || (srcX == 32298))
						&& (srcY == 33087) && (srcMapId == 440 // 海賊島船着場->隠された船着場行きの船
				))
						|| (((srcX == 32735) || (srcX == 32736) || (srcX == 32737))
								&& (srcY == 32794) && (srcMapId == 446))) { // 隠された船着場行きの船->海賊島船着場
					dungeonType = DungeonType.SHIP_FOR_HIDDENDOCK;
				} else if (srcX == 32630 && srcY == 32983 && srcMapId == 0
						|| srcX == 32631 && srcY == 32983 && srcMapId == 0
						|| srcX == 32632 && srcY == 32983 && srcMapId == 0
						|| srcX == 32733 && srcY == 32796 && srcMapId == 5
						|| srcX == 32734 && srcY == 32796 && srcMapId == 5
						|| srcX == 32735 && srcY == 32796 && srcMapId == 5) {  // TalkingIslandShiptoAdenMainland->TalkingIsland
					dungeonType = DungeonType.SHIP_FOR_GLUDIN;
				} else  if (srcX == 32540 && srcY == 32728 && srcMapId == 4
						|| srcX == 32541 && srcY == 32728 && srcMapId == 4
						|| srcX == 32542 && srcY == 32728 && srcMapId == 4
						|| srcX == 32543 && srcY == 32728 && srcMapId == 4
						|| srcX == 32544 && srcY == 32728 && srcMapId == 4
						|| srcX == 32734 && srcY == 32794 && srcMapId == 6
						|| srcX == 32735 && srcY == 32794 && srcMapId == 6
						|| srcX == 32736 && srcY == 32794 && srcMapId == 6
						|| srcX == 32737 && srcY == 32794 && srcMapId == 6) { // AdenMainlandShiptoTalkingIsland->AdenMainland
					dungeonType = DungeonType.SHIP_FOR_TI;
				} else if ((srcX == 32602) && (srcY == 32930) && (srcMapId == 0)) { // 說話之島旅館
					dungeonType = DungeonType.TALKING_ISLAND_HOTEL;
				} else if ((srcX == 32631) && (srcY == 32750) && (srcMapId == 4)) { // 古魯丁旅館
					dungeonType = DungeonType.GLUDIO_HOTEL;
				} else if ((srcX == 33116) && (srcY == 33379) && (srcMapId == 4)) { // 銀騎士旅館
					dungeonType = DungeonType.SILVER_KNIGHT_HOTEL;
				} else if ((srcX == 32628) && (srcY == 33167) && (srcMapId == 4)) { // 風木旅館
					dungeonType = DungeonType.WINDAWOOD_HOTEL;
				} else if ((srcX == 33605) && (srcY == 33275) && (srcMapId == 4)) { // 海音旅館
					dungeonType = DungeonType.HEINE_HOTEL;
				} else if ((srcX == 33437) && (srcY == 32789) && (srcMapId == 4)) { // 奇岩旅館
					dungeonType = DungeonType.GIRAN_HOTEL;
				} else if ((srcX == 34068) && (srcY == 32254) && (srcMapId == 4)) { // 歐瑞旅館
					dungeonType = DungeonType.OREN_HOTEL;
				} else if ((srcX == 32450) && (srcY == 33047) && (srcMapId == 440)) { // 海賊島旅館
					dungeonType = DungeonType.PIRATE_ISLAND;
				}
				final NewDungeon newDungeon = new NewDungeon(newX, newY,
						(short) newMapId, heading, dungeonType, note.toLowerCase().contains("hotel"));
				if (_dungeonMap.containsKey(key)) {
					_log.error("相同SRC傳送座標(" + key + ")");
				}
				_dungeonMap.put(key, newDungeon);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入地圖切換點設置數量: " + _dungeonMap.size() + "(" + timer.get()
				+ "ms)");
	}

	private static AtomicInteger _nextId = new AtomicInteger(50000);

	private static class NewDungeon {
		int _id;
		int _newX;
		int _newY;
		short _newMapId;
		int _heading;
		DungeonType _dungeonType;
		boolean isHotel = false;

		private NewDungeon(final int newX, final int newY,
				final short newMapId, final int heading,
				final DungeonType dungeonType, boolean isHotel) {
			this._id = _nextId.incrementAndGet();
			this._newX = newX;
			this._newY = newY;
			this._newMapId = newMapId;
			this._heading = heading;
			this._dungeonType = dungeonType;
			this.isHotel = isHotel;
		}
	}

	/**
	 * 執行座標移動
	 * 
	 * @param locX
	 * @param locY
	 * @param mapId
	 * @param pc
	 * @return
	 */
	public boolean dg(final int locX, final int locY, final int mapId,
			final L1PcInstance pc) {
		final int servertime = L1GameTimeClock.getInstance().currentTime()
				.getSeconds();
		final int nowtime = servertime % 86400;
		final String key = new StringBuilder().append(mapId).append(locX)
				.append(locY).toString();

		if (_dungeonMap.containsKey(key)) {
			final NewDungeon newDungeon = _dungeonMap.get(key);
			final DungeonType dungeonType = newDungeon._dungeonType;
			boolean teleportable = false;
			int newX = newDungeon._newX;
			int newY = newDungeon._newY;
			short newMap = newDungeon._newMapId;
			int heading = newDungeon._heading;
			if (dungeonType == DungeonType.NONE) {
				teleportable = true;

			} else {
				if (dungeonType == DungeonType.TALKING_ISLAND_HOTEL || dungeonType == DungeonType.GLUDIO_HOTEL
						|| dungeonType == DungeonType.WINDAWOOD_HOTEL || dungeonType == DungeonType.SILVER_KNIGHT_HOTEL
						|| dungeonType == DungeonType.HEINE_HOTEL || dungeonType == DungeonType.GIRAN_HOTEL
						|| dungeonType == DungeonType.OREN_HOTEL || dungeonType == DungeonType.PIRATE_ISLAND) {
					int npcid = 0;
					int[] data = null;
					if (dungeonType == DungeonType.TALKING_ISLAND_HOTEL) {
						npcid = 70012; // 說話之島 - 瑟琳娜
						data = new int[] { 32745, 32803, 16384, 32743, 32808, 16896 };
					} else if (dungeonType == DungeonType.GLUDIO_HOTEL) {
						npcid = 70019; // 古魯丁 - 羅利雅
						data = new int[] { 32743, 32803, 17408, 32744, 32807, 17920 };
					} else if (dungeonType == DungeonType.GIRAN_HOTEL) {
						npcid = 70031; // 奇岩 - 瑪理
						data = new int[] { 32744, 32803, 18432, 32744, 32807, 18944 };
					} else if (dungeonType == DungeonType.OREN_HOTEL) {
						npcid = 70065; // 歐瑞 - 小安安
						data = new int[] { 32744, 32803, 19456, 32744, 32807, 19968 };
					} else if (dungeonType == DungeonType.WINDAWOOD_HOTEL) {
						npcid = 70070; // 風木 - 維萊莎
						data = new int[] { 32744, 32803, 20480, 32744, 32807, 20992 };
					} else if (dungeonType == DungeonType.SILVER_KNIGHT_HOTEL) {
						npcid = 70075; // 銀騎士 - 米蘭德
						data = new int[] { 32744, 32803, 21504, 32744, 32807, 22016 };
					} else if (dungeonType == DungeonType.HEINE_HOTEL) {
						npcid = 70084; // 海音 - 伊莉
						data = new int[] { 32744, 32803, 22528, 32744, 32807, 23040 };
					} else if (dungeonType == DungeonType.PIRATE_ISLAND) {
						npcid = 70096; // 海賊島 - 米列
						data = new int[] { 32744, 32803, 23552, 32744, 32807, 24064 };
					}
					// 確認房間鑰匙
					int type = checkInnKey(pc, npcid);
					if (type == 1) { // 房間
						newX = data[0];
						newY = data[1];
						newMap = (short) data[2];
						heading = 6;
						teleportable = true;
					} else if (type == 2) { // 會議室
						newX = data[3];
						newY = data[4];
						newMap = (short) data[5];
						heading = 6;
						teleportable = true;
					}
				} else if (nowtime >= 660 * 60 && nowtime < 720 * 60 // 11~12
						|| nowtime >= 900 * 60 && nowtime < 960 * 60 // 15~16
						|| nowtime >= 1140 * 60 && nowtime < 1200 * 60 // 19~20
						|| nowtime >= 1380 * 60 && nowtime < 0)// 23~00						
				{
					if ((pc.getInventory().checkItem(40299, 1) // 往古魯丁的船票
							&& dungeonType == DungeonType.SHIP_FOR_GLUDIN) // TalkingIslandShiptoAdenMainland
							|| (pc.getInventory().checkItem(40301, 1) // 海音港口船票
							&& dungeonType == DungeonType.SHIP_FOR_HEINE) // AdenMainlandShiptoForgottenIsland
							/*|| (pc.getInventory().checkItem(40302, 1) // 海賊島船票
							&& dungeonType == DungeonType.SHIP_FOR_PI)*/) { // ShipPirateislandtoHiddendock
						teleportable = true;
					}
				} else if (nowtime >= 540 * 60 && nowtime < 600 * 60 // 9~10
						|| nowtime >= 780 * 60 && nowtime < 840 * 60 // 13~14
						|| nowtime >= 1020 * 60 && nowtime < 1080 * 60 // 17~18
						|| nowtime >= 1260 * 60 && nowtime < 1320 * 60) // 21~22 
						{ // 21:00~22:00
					if ((pc.getInventory().checkItem(40298, 1) // 往說話之島的船票
							&& dungeonType == DungeonType.SHIP_FOR_TI) // AdenMainlandShiptoTalkingIsland
							|| (pc.getInventory().checkItem(40300, 1) // 遺忘之島船票
							&& dungeonType == DungeonType.SHIP_FOR_FI) // ForgottenIslandShiptoAdenMainland
							/*|| (pc.getInventory().checkItem(40303, 1) // 隱藏港口船票
							&& dungeonType == DungeonType.SHIP_FOR_HIDDENDOCK)*/) { // ShipHiddendocktoPirateisland
						teleportable = true;
					}
				}
			}
//			if (newDungeon.isHotel) {
//				if (!pc.getInventory().checkItem(40312, 1L)) {
//					teleportable = false;
//				} else {
//					pc.getInventory().removeItem(40312, 1L);
//				}
//			}
			if (teleportable) {
				final int id = newDungeon._id;
				// 2秒間は無敵（アブソルートバリア状態）にする。
				pc.setSkillEffect(ABSOLUTE_BARRIER, 2000);
				pc.stopHpRegeneration();
				pc.stopMpRegeneration();

				this.teleport(pc, id, newX, newY, newMap, heading, false);
				return true;
			}
		}
		return false;
	}
	// 檢查身上的鑰匙
	private int checkInnKey(L1PcInstance pc, int npcid) {
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (item.getInnNpcId() == npcid) { // 鑰匙與旅館NPC相符
				for (int i = 0; i < 16; i++) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcid, i);
					if (inn.getKeyId() == item.getKeyId()) {
						Timestamp dueTime = item.getDueTime();
						if (dueTime != null) { // 時間不為空值
							Calendar cal = Calendar.getInstance();
							if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // 租用時間未到
								pc.setInnKeyId(item.getKeyId()); // 登入此鑰匙
								// 1:房間 2:會議室
								return item.checkRoomOrHall() ? 2 : 1;
							}
						}
					}
				}
			}
		}
		return 0;
	}
	/**
	 * 執行傳送
	 * 
	 * @param pc
	 * @param newX
	 * @param newY
	 * @param newMap
	 * @param heading
	 * @param b
	 */
	private void teleport(final L1PcInstance pc, final int id, final int newX,
			final int newY, final short newMap, final int heading,
			final boolean b) {
		pc.setTeleportX(newX);
		pc.setTeleportY(newY);
		pc.setTeleportMapId(newMap);
		pc.setTeleportHeading(heading);
		pc.sendPackets(new S_Teleport(newMap, pc.getMapId()));
/*		Teleportation.teleportation(pc);*/
	}
}
