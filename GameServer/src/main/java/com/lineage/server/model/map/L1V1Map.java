package com.lineage.server.model.map;

import com.lineage.server.model.Instance.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;

import java.util.List;

public class L1V1Map extends L1Map {

	private static final Log _log = LogFactory.getLog(L1V1Map.class);
	
	private int _mapId;

	private int _worldTopLeftX;

	private int _worldTopLeftY;

	private int _worldBottomRightX;

	private int _worldBottomRightY;

	private byte _map[][];

	private boolean _isUnderwater;

	private boolean _isMarkable;

	private boolean _isTeleportable;

	private boolean _isEscapable;

	private boolean _isUseResurrection;

	private boolean _isUsePainwand;

	private boolean _isEnabledDeathPenalty;

	private boolean _isTakePets;

	private boolean _isRecallPets;

	private boolean _isUsableItem;

	private boolean _isUsableSkill;
	
    private boolean _isBot;
    
    private boolean _isBotItem;
    
    private boolean _isguaji;
    private boolean _isClan_Teleprot;

	/**
	 * Mob通行不可能上存在示
	 */
	private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128; // 1000 0000

	protected L1V1Map() {

	}

	public L1V1Map(final int mapId, final byte map[][], final int worldTopLeftX,
			final int worldTopLeftY, final boolean underwater, final boolean markable,
			final boolean teleportable, final boolean escapable, final boolean useResurrection,
			final boolean usePainwand, final boolean enabledDeathPenalty, final boolean takePets,
			final boolean recallPets, final boolean usableItem, final boolean usableSkill, 
			final boolean Bot, final boolean BotItem, final boolean isClan_Teleprot) {
		this._mapId = mapId;
		this._map = map;
		this._worldTopLeftX = worldTopLeftX;// 起點X
		this._worldTopLeftY = worldTopLeftY;// 起點Y

		this._worldBottomRightX = worldTopLeftX + map.length - 1;// 終點X
		this._worldBottomRightY = worldTopLeftY + map[0].length - 1;// 終點Y

		this._isUnderwater = underwater;
		this._isMarkable = markable;
		this._isTeleportable = teleportable;
		this._isEscapable = escapable;
		this._isUseResurrection = useResurrection;
		this._isUsePainwand = usePainwand;
		this._isEnabledDeathPenalty = enabledDeathPenalty;
		this._isTakePets = takePets;
		this._isRecallPets = recallPets;
		this._isUsableItem = usableItem;
		this._isUsableSkill = usableSkill;
        this._isBot = Bot;
        this._isBotItem = BotItem;
		this._isClan_Teleprot = isClan_Teleprot;
	}

	public L1V1Map(final L1V1Map map) {
		this._mapId = map._mapId;

		// _map
		this._map = new byte[map._map.length][];
		for (int i = 0; i < map._map.length; i++) {
			this._map[i] = map._map[i].clone();
		}

		this._worldTopLeftX = map._worldTopLeftX;
		this._worldTopLeftY = map._worldTopLeftY;
		this._worldBottomRightX = map._worldBottomRightX;
		this._worldBottomRightY = map._worldBottomRightY;

	}

	private int accessTile(final int x, final int y) {
		if (!this.isInMap(x, y)) { // 。良。
			return 0;
		}
		return this._map[x - this._worldTopLeftX][y - this._worldTopLeftY];
	}

	private int accessOriginalTile(final int x, final int y) {
		return this.accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
	}

	private void setTile(final int x, final int y, final int tile) {
		if (!this.isInMap(x, y)) { // 。良。
			return;
		}
		this._map[x - this._worldTopLeftX][y - this._worldTopLeftY] = (byte) tile;
	}

	/*
	 * 良氣
	 */
	public byte[][] getRawTiles() {
		return this._map;
	}

	@Override
	public int getId() {
		return this._mapId;
	}

	@Override
	public int getX() {
		return this._worldTopLeftX;
	}

	@Override
	public int getY() {
		return this._worldTopLeftY;
	}

	@Override
	public int getWidth() {
		return this._worldBottomRightX - this._worldTopLeftX + 1;
	}

	@Override
	public int getHeight() {
		return this._worldBottomRightY - this._worldTopLeftY + 1;
	}

	@Override
	public int getTile(final int x, final int y) {
		final short tile = this._map[x - this._worldTopLeftX][y - this._worldTopLeftY];
		if (0 != (tile & BITFLAG_IS_IMPASSABLE)) {
			return 300;
		}
		return this.accessOriginalTile(x, y);
	}

	@Override
	public int getOriginalTile(final int x, final int y) {
		return this.accessOriginalTile(x, y);
	}

	@Override
	public boolean isInMap(final Point pt) {
		return this.isInMap(pt.getX(), pt.getY());
	}
	
	@Override
	public boolean isInMap(final int x, final int y) {
		// 茶色判定
		if ((this._mapId == 4) && ((x < 32520) || (y < 32070) || ((y < 32190) && (x < 33950)))) {
			return false;
		}
		return ((this._worldTopLeftX <= x) && (x <= this._worldBottomRightX)
				&& (this._worldTopLeftY <= y) && (y <= this._worldBottomRightY));
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final Point pt, final L1Character cha) {
		return this.isPassable(pt.getX(), pt.getY(), cha);
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final int x, final int y, final L1Character cha) {
		return this.isPassable(x, y - 1, 4, cha) || this.isPassable(x + 1, y, 6, cha)
		|| this.isPassable(x, y + 1, 0, cha) || this.isPassable(x - 1, y, 2, cha);
	}

	// 指定座標通行可能
	@Override
	public boolean isPassable(final Point pt, final int heading, final L1Character cha) {
		return this.isPassable(pt.getX(), pt.getY(), heading, cha);
	}
	
	// 正向
	private static final byte HEADING_TABLE_X[] = {  0,  1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1,  1,  0, -1 };

	@Override
	public boolean isPassable(final int x, final int y, final int heading, final L1Character cha) {
		try {
			// 目前座標
			final int tile1 = this.accessTile(x, y);

			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			// 前往方向座標
			final int tile2 = this.accessTile(locx, locy);
			if (tile2 == 0x00) {
				return false;
			}
			if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
				if (cha instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) cha;
					final List<L1Object> objs = World.get().VisibleMob(locx, locy, mob);
					for (L1Object object : objs) {
						if (object != null) {
							objs.clear();
							return false;
						}
					}
					objs.clear();
				}
			}
			if (cha != null) {// NPC
				switch (_mapId) {
				case 0:// 說話之島
					return set_map(tile2, 0x01);
					
				case 4:// 大陸地區
				case 57:// 歌唱之島
				case 58:// 隱龍之地
				case 68:// 歌唱之島
				case 69:// 隱藏之谷
				case 70:// 遺忘之島
				case 303:// 夢幻之島
				case 430:// 精靈墓穴
				case 440:// 海賊島
				case 445:
				case 480:// 海賊島後半部
				case 613:// 奇怪之村落
				case 621:// 詭異村落
				case 630:// 天空之城
					switch (tile2) {
					case 28:
						return false;
					case 44:
						return false;
					case 21:
						return false;
					case 26:
						return false;
					case 29:
						return false;
					case 12:
						return false;
					}
					return set_map(tile2, 0x08);
					
				default:
					return set_map(tile2, 0x03);
				}
				
			} else {// PC
				int tile3;
				int tile4;
				switch (heading) {
				case 0:
					return (tile1 & 0x02) == 0x02;

				case 1:
					tile3 = this.accessTile(x, y - 1);
					tile4 = this.accessTile(x + 1, y);
					return ((tile3 & 0x01) == 0x01) || ((tile4 & 0x02) == 0x02);

				case 2:
					return (tile1 & 0x01) == 0x01;

				case 3:
					tile3 = this.accessTile(x, y + 1);
					return (tile3 & 0x01) == 0x01;

				case 4:
					return (tile2 & 0x02) == 0x02;

				case 5:
					return ((tile2 & 0x01) == 0x01) || ((tile2 & 0x02) == 0x02);

				case 6:
					return (tile2 & 0x01) == 0x01;

				case 7:
					tile3 = this.accessTile(x - 1, y);
					return (tile3 & 0x02) == 0x02;
				}
			}
			
		} catch (final Exception e) {
		}
		return false;
	}

	@Override
	public boolean isPassableDna(final int x, final int y, final int heading) {
		try {
			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			// 前往方向座標
			final int tile2 = this.accessTile(locx, locy);
			if (tile2 == 0x00) {
				return false;
			}
			switch (_mapId) {// XXX
			case 0:// 說話之島
				return set_map(tile2, 0x01);
				
			case 4:// 大陸地區
			case 57:// 歌唱之島
			case 58:// 隱龍之地
			case 68:// 歌唱之島
			case 69:// 隱藏之谷
			case 70:// 遺忘之島
			case 303:// 夢幻之島
			case 430:// 精靈墓穴
			case 440:// 海賊島
			case 445:
			case 480:// 海賊島後半部
			case 613:// 奇怪之村落
			case 621:// 詭異村落
			case 630:// 天空之城
				switch (tile2) {
				case 28:
					return false;
				case 44:
					return false;
				case 21:
					return false;
				case 26:
					return false;
				case 29:
					return false;
				case 12:
					return false;
				}
				return set_map(tile2, 0x08);
				
			default:
				return set_map(tile2, 0x03);
			}
			
		} catch (final Exception e) {
		}
		return false;
	}
	
	private boolean set_map(int tile2, int i) {
		return (tile2 & i) != 0x00;
	}

	@Override
	public boolean isDoorPassable(final int x, final int y, final int heading, final L1NpcInstance npc) {
		try {
			if (heading == -1) {
				return false;
			}
			final int locx = x + HEADING_TABLE_X[heading];
			final int locy = y + HEADING_TABLE_Y[heading];
			final int tile2 = this.accessTile(locx, locy);
			if (npc != null) {
				if (tile2 == 0x03) {// 關閉的門
					// 無首要目標
					if (npc.is_now_target() == null) {
						return false;
					}
					for (final L1Object object : World.get().getVisibleObjects(npc, 2)) {
						if (object instanceof L1DoorInstance) {// 障礙者是 門
							L1DoorInstance door = (L1DoorInstance) object;
							switch (door.getDoorId()) {
							case 6006:// 黃金鑰匙
							case 6007:// 銀鑰匙
							case 10000:// 不死族的鑰匙
							case 10001:// 殭屍鑰匙
							case 10002:// 骷髏鑰匙
							case 10003:// 機關門(說明:不死族的叛徒 (法師30級以上官方任務))
							case 10004:// 蛇女房間鑰匙
							case 10005:// 安塔瑞斯洞穴
							case 10006:// 安塔瑞斯洞穴
							case 10007:// 安塔瑞斯洞穴
							case 10008:// 法利昂洞穴
							case 10009:// 法利昂洞穴
							case 10010:// 法利昂洞穴
							case 10011:// 法利昂洞穴
							case 10012:// 法利昂洞穴
							case 10013:// 法利昂洞穴
							case 10019:// 魔法師．哈汀(故事) 禁開
							case 10036:// 魔法師．哈汀(故事) 禁開
							case 10015:// 魔法師．哈汀(故事)// NO 1
							case 10016:// 魔法師．哈汀(故事)// NO 2
							case 10017:// 魔法師．哈汀(故事)// NO 2
							case 10020:// 魔法師．哈汀(故事)// NO 4
								return false;
								
							default:
								if (door.getOpenStatus() == ActionCodes.ACTION_Close) {// 關閉的門
									if (npc instanceof L1GuardInstance) {// 警衛
										door.open();
										return true;
										
									} else {
										if (door.getKeeperId() == 0) {// 沒有管家的門
											door.open();
											return true;
										}
									}
									
								} else {// 開啟的門
									return true;
								}
							}
						}
					}
					return false;
					
				} else {// 開啟的門
					return true;
				}
			}
			
		} catch (final Exception e) {
		}
		return true;
	}
	
	

	// 設定座標障礙宣告
	@Override
	public void setPassable(final Point pt, final boolean isPassable) {
		this.setPassable(pt.getX(), pt.getY(), isPassable, 0x02);
	}
	 // 設定座標障礙宣告
 	@Override
 	public void setPassable(final int x, final int y, final boolean isPassable) {
 		if (isPassable) {
 			this.setTile(x, y, (short) (this.accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
 		} else {
 			this.setTile(x, y, (short) (this.accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
 		}
 	}
	@Override
	public void setPassable(final int x, final int y, final boolean isPassable, final int door) {
		switch (door) {
		case 0x00:// 0:門／
			set_door_0(x, y, isPassable);
			break;
			
		case 0x01:// 1:門＼
			set_door_1(x, y, isPassable);
			break;
			
		default:// 2+:一般
			if (isPassable) {
				this.setTile(x, y, (short) (this.accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
				
			} else {
				this.setTile(x, y, (short) (this.accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
			}
			break;
		}
	}

	/*
	 * 門／
	 */
	private void set_door_0(int x, int y, boolean isPassable) {
		try {
			if (isPassable) {// 可通過
				this._map[x - this._worldTopLeftX][y - this._worldTopLeftY] = 0x2f;
				
			} else {// 不可通過
				this._map[x - this._worldTopLeftX][y - this._worldTopLeftY] = 0x03;
				this._map[(x - 1) - this._worldTopLeftX][y - this._worldTopLeftY] = 0x03;
				this._map[(x + 1) - this._worldTopLeftX][y - this._worldTopLeftY] = 0x03;
			}
			
		} catch (final Exception e) {
			_log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
		}
	}

	/*
	 * 1:門
	 */
	private void set_door_1(int x, int y, boolean isPassable) {
		try {
			if (isPassable) {// 可通過
				this._map[x - this._worldTopLeftX][y - this._worldTopLeftY] = 0x2f;
				
			} else {// 不可通過
				this._map[x - this._worldTopLeftX][y - this._worldTopLeftY] = 0x03;
				this._map[x - this._worldTopLeftX][(y - 1) - this._worldTopLeftY] = 0x03;
				this._map[x - this._worldTopLeftX][(y + 1) - this._worldTopLeftY] = 0x03;
			}
			
		} catch (final Exception e) {
			_log.error("X:" + x + " Y:" + y + " MAP:" + _mapId, e);
		}
	}

	@Override
	public boolean isSafetyZone(final Point pt) {
		return this.isSafetyZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isSafetyZone(final int x, final int y) {
		final int tile = this.accessOriginalTile(x, y);
		return (tile & 0x30) == 0x10;
	}

	@Override
	public boolean isCombatZone(final Point pt) {
		return this.isCombatZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isCombatZone(final int x, final int y) {
		final int tile = this.accessOriginalTile(x, y);
		return (tile & 0x30) == 0x20;
	}

	@Override
	public boolean isNormalZone(final Point pt) {
		return this.isNormalZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isNormalZone(final int x, final int y) {
		final int tile = this.accessOriginalTile(x, y);
		return (tile & 0x30) == 0x00;
	}

	@Override
	public boolean isArrowPassable(final Point pt) {
		return this.isArrowPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isArrowPassable(final int x, final int y) {
		return (this.accessOriginalTile(x, y) & 0x0e) != 0;
	}

	@Override
	public boolean isArrowPassable(final Point pt, final int heading) {
		return this.isArrowPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isArrowPassable(final int x, final int y, final int heading) {
		try {
			final int newX = x + HEADING_TABLE_X[heading];
			final int newY = y + HEADING_TABLE_Y[heading];

			// 前往方向座標
			final int tile2 = this.accessTile(newX, newY);
			if (this._mapId == 4 && newX >= 33100 && newX <= 33115 && newY >= 32769 && newY <= 32773) {
				return true;
			}

			switch (tile2) {
			case 0x00:// 不可通行
			case 0x03:// 關閉的門
				return false;
				
			default:// 一般
				return (tile2 & 0x0c) != 0x00;
			}
			
		} catch (final Exception e) {
		}
		return false;
	}

	@Override
	public boolean isUnderwater() {
		return this._isUnderwater;
	}

	@Override
	public boolean isMarkable() {
		return this._isMarkable;
	}

	@Override
	public boolean isTeleportable() {
		return this._isTeleportable;
	}

	@Override
	public boolean isEscapable() {
		return this._isEscapable;
	}

	@Override
	public boolean isUseResurrection() {
		return this._isUseResurrection;
	}

	@Override
	public boolean isUsePainwand() {
		return this._isUsePainwand;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return this._isEnabledDeathPenalty;
	}

	@Override
	public boolean isTakePets() {
		return this._isTakePets;
	}

	@Override
	public boolean isRecallPets() {
		return this._isRecallPets;
	}

	@Override
	public boolean isUsableItem() {
		return this._isUsableItem;
	}

	@Override
	public boolean isUsableSkill() {
		return this._isUsableSkill;
	}
	
    @Override
    public boolean isBot() {
        return this._isBot;
    }
    
    @Override
    public boolean isBotItem() {
        return this._isBotItem;
    }

	@Override
	public boolean isFishingZone(final int x, final int y) {
		return this.accessOriginalTile(x, y) == 0x1c;
	}

	@Override
	public int isExistDoor(final int x, final int y) {
		try {
			return this._map[x - this._worldTopLeftX][y - this._worldTopLeftY];
		} catch (final Exception e) {
		}
		return 0x00;
	}

	@Override
	public String toString(final Point pt) {
		return "" + this.getOriginalTile(pt.getX(), pt.getY());
	}

	@Override
	public boolean isguaji() {
		return this._isguaji;
	}
	@Override
	public boolean isClan_Teleprot() {
		return _isClan_Teleprot;
	}
}
