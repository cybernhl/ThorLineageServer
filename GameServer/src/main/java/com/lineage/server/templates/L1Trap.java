package com.lineage.server.templates;

import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.poison.L1ParalysisPoison;
import com.lineage.server.model.poison.L1SilencePoison;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.utils.Dice;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 陷阱資料
 * @author dexc
 *
 */
public class L1Trap {

	private static final Log _log = LogFactory.getLog(L1Trap.class);

	private String _type;// 陷阱類型
	
	private int _trap;// 陷阱類型
	
	private int _id;// 陷阱編號

	private int _gfxId;// 效果圖像編號

	private boolean _isDetectionable;// 可以被探查

	private Dice _dice;// 基礎傷害質 / 基礎治療質

	private int _base;// 最小傷害質 / 最小治療質

	private int _diceCount;// 傷害次數 / 治療次數
	
	private int _npcId;// 召喚NPC編號

	private int _count;// 召喚數量
	
	private int _poisonType;// 詛咒類型

	private int _delay;// 詛咒延遲時間

	private int _time;// 詛咒時間

	private int _damage;// 詛咒傷害
	
	private int _skillId;// 技能編號

	private int _skillTimeSeconds;// 技能時間(秒)
	
	private L1Location _loc;// 傳送位置點資料

	/**
	 * 陷阱
	 * @param rs
	 */
	public L1Trap(final ResultSet rs) {
		try {
			this._id = rs.getInt("id");
			this._gfxId = rs.getInt("gfxId");
			this._isDetectionable = rs.getBoolean("isDetectionable");
			
			// 定義陷阱類型
			_type = rs.getString("type");
			if (_type.equalsIgnoreCase("L1DamageTrap")) {
				_trap = 1;// 陷阱-傷害 對接觸者
				this._dice = new Dice(rs.getInt("dice"));
				this._base = rs.getInt("base");
				this._diceCount = rs.getInt("diceCount");
				
			} else if (_type.equalsIgnoreCase("L1HealingTrap")) {
				_trap = 2;// 陷阱-治療 對接觸者
				this._dice = new Dice(rs.getInt("dice"));
				this._base = rs.getInt("base");
				this._diceCount = rs.getInt("diceCount");
				
			} else if (_type.equalsIgnoreCase("L1MonsterTrap")) {
				_trap = 3;// 陷阱-召喚怪物 對接觸者
				this._npcId = rs.getInt("monsterNpcId");
				this._count = rs.getInt("monsterCount");

			} else if (_type.equalsIgnoreCase("L1PoisonTrap")) {
				_trap = 4;// 陷阱-詛咒 對接觸者
				// 定義詛咒類型
				final String poisonType = rs.getString("poisonType");
				if (poisonType.equalsIgnoreCase("d")) {
					_poisonType = 1;// 一般型中毒
					
				} else if (poisonType.equalsIgnoreCase("s")) {
					_poisonType = 2;// 沈默型中毒
					
				} else if (poisonType.equalsIgnoreCase("p")) {
					_poisonType = 3;// 麻痺型中毒
				}
				
				this._delay = rs.getInt("poisonDelay");
				this._time = rs.getInt("poisonTime");
				this._damage = rs.getInt("poisonDamage");

			} else if (_type.equalsIgnoreCase("L1SkillTrap")) {
				_trap = 5;// 陷阱-施展指定技能 對接觸者
				this._skillId = rs.getInt("skillId");
				this._skillTimeSeconds = rs.getInt("skillTimeSeconds");

			} else if (_type.equalsIgnoreCase("L1TeleportTrap")) {
				_trap = 6;// 陷阱-傳送目標 對接觸者
				final int x = rs.getInt("teleportX");
				final int y = rs.getInt("teleportY");
				final int mapId = rs.getInt("teleportMapId");
				this._loc = new L1Location(x, y, mapId);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 類型
	 * @return
	 */
	public String getType() {
		return _type + "(" + this._trap + "-" + this._id + ")";
	}

	/**
	 * 陷阱編號
	 * @return
	 */
	public int getId() {
		return this._id;
	}

	/**
	 * 效果圖像編號
	 * @return
	 */
	public int getGfxId() {
		return this._gfxId;
	}

	/**
	 * 召喚圖像的處理
	 * @param trapObj
	 */
	private void sendEffect(final L1Object trapObj) {
		try {
			if (this.getGfxId() == 0) {
				return;
			}
			// 產生動畫
			final S_EffectLocation effect = 
				new S_EffectLocation(trapObj.getLocation(), this.getGfxId());

			for (final L1PcInstance pc : World.get().getRecognizePlayer(trapObj)) {
				pc.sendPackets(effect);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 踩到陷阱的處理
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		switch (this._trap) {
		case 0:// 未定義
			_log.error("陷阱的處理未定義: " + this._id);
			break;
			
		case 1:// 陷阱-傷害 對接觸者
			onType1(trodFrom, trapObj);
			break;
			
		case 2:// 陷阱-治療 對接觸者
			onType2(trodFrom, trapObj);
			break;
			
		case 3:// 陷阱-召喚怪物 對接觸者
			onType3(trodFrom, trapObj);
			break;
			
		case 4:// 陷阱-詛咒 對接觸者
			onType4(trodFrom, trapObj);
			break;
			
		case 5:// 陷阱-施展指定技能 對接觸者
			onType5(trodFrom, trapObj);
			break;
			
		case 6:// 陷阱-傳送目標 對接觸者
			onType6(trodFrom, trapObj);
			break;
		}
	}

	/**
	 * 被探查時狀況處理
	 * @param caster
	 * @param trapObj
	 */
	public void onDetection(final L1PcInstance caster, final L1Object trapObj) {
		if (this._isDetectionable) {
			this.sendEffect(trapObj);
		}
	}

	// TODO 陷阱類型對接觸物件的處理
	
	// 陷阱-傷害 對接觸者

	/**
	 * 陷阱-傷害 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType1(final L1PcInstance trodFrom, final L1Object trapObj) {
		//System.out.println("陷阱-傷害 對接觸者:" + _trap);
		try {
			if (_trap != 1) {
				return;
			}
			if (_base <= 0) {
				return;
			}
			this.sendEffect(trapObj);

			final int dmg = this._dice.roll(this._diceCount) + this._base;
			// 送出傷害
			trodFrom.receiveDamage(trodFrom, dmg, false, true);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	// 陷阱-治療 對接觸者
	
	/**
	 * 陷阱-治療 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType2(final L1PcInstance trodFrom, final L1Object trapObj) {
		//System.out.println("陷阱-治療 對接觸者:" + _trap);
		try {
			if (_trap != 2) {
				return;
			}
			if (_base <= 0) {
				return;
			}
			this.sendEffect(trapObj);

			final int pt = this._dice.roll(this._diceCount) + this._base;
			// 治療
			trodFrom.healHp(pt);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	// 陷阱-召喚怪物 對接觸者

	/**
	 * 陷阱-召喚怪物 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType3(final L1PcInstance trodFrom, final L1Object trapObj) {
		//System.out.println("陷阱-召喚怪物 對接觸者:" + _trap);
		try {
			if (_trap != 3) {
				return;
			}
			if (_npcId <= 0) {
				return;
			}
			this.sendEffect(trapObj);

			for (int i = 0 ; i < this._count ; i++) {
				final L1Location loc = trodFrom.getLocation().randomLocation(5, false);
				L1NpcInstance newNpc = L1SpawnUtil.spawn(this._npcId, loc);
				newNpc.setLink(trodFrom);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 陷阱-詛咒 對接觸者

	/**
	 * 陷阱-詛咒 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType4(final L1PcInstance trodFrom, final L1Object trapObj) {
		//System.out.println("陷阱-詛咒 對接觸者:" + _trap);
		try {
			if (_trap != 4) {
				return;
			}
			this.sendEffect(trapObj);

			// 判斷詛咒類型
			switch (this._poisonType) {
			case 1:// 一般型中毒
				//System.out.println("一般型中毒");
				L1DamagePoison.doInfection(trodFrom, trodFrom, this._time, this._damage);
				break;

			case 2:// 沈默型中毒
				//System.out.println("沈默型中毒");
				L1SilencePoison.doInfection(trodFrom);
				break;

			case 3:// 麻痺型中毒
				//System.out.println("麻痺型中毒");
				L1ParalysisPoison.doInfection(trodFrom, this._delay, this._time);
				break;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 陷阱-施展指定技能 對接觸者

	/**
	 * 陷阱-施展指定技能 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType5(final L1PcInstance trodFrom, final L1Object trapObj) {
		//System.out.println("陷阱-施展指定技能 對接觸者:" + _trap);
		try {
			if (_trap != 5) {
				return;
			}
			if (_skillId <= 0) {
				return;
			}
			this.sendEffect(trapObj);

			new L1SkillUse().handleCommands(trodFrom, _skillId, trodFrom.getId(),
					trodFrom.getX(), trodFrom.getY(), _skillTimeSeconds,
					L1SkillUse.TYPE_GMBUFF);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// 陷阱-傳送目標 對接觸者

	/**
	 * 陷阱-傳送目標 對接觸者
	 * @param trodFrom 接觸者
	 * @param trapObj 自己
	 */
	private void onType6(final L1PcInstance trodFrom, final L1Object trapObj) {
		try {
			if (_trap != 6) {
				return;
			}
			if (_loc == null) {
				return;
			}
			this.sendEffect(trapObj);

			L1Teleport.teleport(trodFrom, 
					this._loc.getX(), this._loc.getY(), 
					(short) this._loc.getMapId(), 5, true, L1Teleport.ADVANCED_MASS_TELEPORT);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}