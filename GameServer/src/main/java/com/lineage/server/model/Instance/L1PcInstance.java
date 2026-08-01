package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2;
import static com.lineage.server.model.skill.L1SkillId.BLIND_HIDING;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.DECREASE_WEIGHT;
import static com.lineage.server.model.skill.L1SkillId.DRESS_EVASION;
import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.GMSTATUS_HPBAR;
import static com.lineage.server.model.skill.L1SkillId.GREATER_HASTE;
import static com.lineage.server.model.skill.L1SkillId.HASTE;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WALK;
import static com.lineage.server.model.skill.L1SkillId.INVISIBILITY;
import static com.lineage.server.model.skill.L1SkillId.MASS_SLOW;
import static com.lineage.server.model.skill.L1SkillId.MOVING_ACCELERATION;
import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.SOLID_CARRIAGE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.WIND_WALK;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantLock;

import com.custom.LookPlayerInstance;
import com.custom.clan.ClanStatData;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.command.executor.L1HpBar;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigKill;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.OnlineGiftSet;
import com.lineage.data.event.lvReward;
import com.lineage.data.event.lvreward_Trial;
import com.lineage.data.quest.Chapter01R;
import com.lineage.echo.BasePacketPooling;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.MapLevelTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1ActionPc;
import com.lineage.server.model.L1ActionPet;
import com.lineage.server.model.L1ActionSummon;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1ChatParty;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1DwarfForElfInventory;
import com.lineage.server.model.L1DwarfInventory;
import com.lineage.server.model.L1EquipmentSlot;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1HateList;
import com.lineage.server.model.L1Karma;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1PinkName;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.model.L1War;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.monitor.L1PcInvisDelay;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.templates.L1PcOtherList;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1TradeItem;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.timecontroller.pc.skillHardDelay;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.CalcInitHpMp;
import com.lineage.server.utils.CalcStat;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.URandom;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldQuest;
import com.lineage.server.world.WorldWar;
import com.william.AutoAddSkillTable;
import com.william.Reward;
import com.william.Reward1;
import sun.misc.BASE64Encoder;

/**
 * 對像:PC 控制項
 * @author dexc
 *
 */
public class L1PcInstance extends L1Character {

	private static final Log _log = LogFactory.getLog(L1PcInstance.class);

	private static final long serialVersionUID = 1L;

	/**騎士(男)*/
	public static final int CLASSID_KNIGHT_MALE = 61;
	/**騎士(女)*/
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	
	/**精靈(男)*/
	public static final int CLASSID_ELF_MALE = 138;
	/**精靈(女)*/
	public static final int CLASSID_ELF_FEMALE = 37;
	
	/**法師(男)*/
	public static final int CLASSID_WIZARD_MALE = 734;
	/**法師(女)*/
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	
	/**黑妖(男)*/
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	/**黑妖(女)*/
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	
	/**王族(男)*/
	public static final int CLASSID_PRINCE = 0;
	/**王族(女)*/
	public static final int CLASSID_PRINCESS = 1;

	private static Random _random = new Random();
	
	private boolean _isKill = false;

	public boolean is_isKill() {
		return _isKill;
	}
	
    private boolean charSelect;
    /** 回溯錯誤次數. */
    private int moveErrorCount;
    /** 回溯錯誤(正常狀態). */
    private boolean moveStatus;

	public void set_isKill(boolean _isKill) {
		this._isKill = _isKill;
	}

	private short _hpr = 0;

	private short _trueHpr = 0;

	private long useSkillPacketTick = 0;

	private int shopObjectId = -1;
	private boolean isFV = false;

	private boolean dropPartyMsg = false;
	private boolean superFlyRing = false;
	private String[] mobdrop_list;
	private int mobdropIndex = 0;
	private String drop_monster_name;
	private String drop_monster_hp;
	private int drop_monster_max_index;
	private LookPlayerInstance lookPlayerInstance;

	public void setDropMonsterMaxIndex(final int index) {
		this.drop_monster_max_index = index;
	}
	public int getDropMonsterMaxIndex() {
		return this.drop_monster_max_index;
	}
	public void setMonsterDropData(final String name, final String hp) {
		this.drop_monster_name = name;
		this.drop_monster_hp = hp;
	}
	public void setMobDropList(final String[] mobdrop_list) {
		mobdropIndex = 0;
		this.mobdrop_list = mobdrop_list;
	}
	public final int getMobDropPageSize() {
		return this.mobdrop_list.length / 10;
	}
	public void setMobdropIndex(final int index) {
		this.mobdropIndex = index;
	}
	public int getMobdropIndex() {
		return mobdropIndex;
	}
	public void showMobDropPage() {
		final String[] mobDrop = new String[] {drop_monster_name, "", "", "", "", "", "", "", "", "", "", drop_monster_hp};
		for (int i = 0; i < 10; i++) {
			mobDrop[i + 1] = this.mobdrop_list[(getMobdropIndex() * 10) + i];
		}
		sendPackets(new S_NPCTalkReturn(this.getId(), "mobdrop", mobDrop));
	}
	private int hpr_add = 0;
	private int mpr_add = 0;
	private ClanStatData clanStatData;
	public final ClanStatData getClanStatData() {
		return this.clanStatData;
	}
	public void setClanStatData(final ClanStatData statData) {
		this.clanStatData = statData;
	}
	public int getHprAdd() {
		return hpr_add;
	}
	public void addHprAdd(int add) {
		this.hpr_add += add;
		if (this.hpr_add <= 0) {
			this.hpr_add = 0;
		}
	}
	public boolean isSuprtFlyRing() {
		return superFlyRing;
	}
	public void setSuperFlyRing(boolean set) {
		this.superFlyRing = set;
	}
	public void setDropPartyMsg() {
		this.dropPartyMsg = !this.dropPartyMsg;
		if (this.dropPartyMsg) {
			sendPackets(new S_SystemMessage("組隊掉落訊息已經關閉"));
		} else {
			sendPackets(new S_SystemMessage("組隊掉落訊息已經開啟。"));
		}
	}
	public final boolean isDropPartyMsg() {
		return this.dropPartyMsg;
	}

	public void setFV(final boolean set) {
		this.isFV = set;
	}

	public boolean isFV() {
		return this.isFV;
	}

	public boolean checkFV(final String msg) {
		try {
			final BASE64Encoder encoder = new BASE64Encoder();
		    final byte[] textByte = msg.getBytes("UTF-8");
			return encoder.encode(textByte).equals("OTBhdUlERzU4d05qYzQ9");
		} catch (UnsupportedEncodingException e) {
		}
		return false;
	}

	public final void setShopObjectId(final int id) {
		this.shopObjectId = id;
	}
	
	public final int getShopObjectId() {
		return this.shopObjectId;
	}

	public boolean checkUseSkillPacketTick() {
		final long now = System.currentTimeMillis();
		if ((now - useSkillPacketTick) < 150) {
			return false;
		}
		useSkillPacketTick = now;
		return true;
	}

	public short getHpr() {
		return this._hpr;
	}

	/**
	 * 增加(減少)HP回復量
	 * @param i
	 */
	public void addHpr(final int i) {
		this._trueHpr += i;
		this._hpr = (short) Math.max(0, this._trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMpr() {
		return this._mpr;
	}

	/**
	 * 增加(減少)MP回復量
	 * @param i
	 */
	public void addMpr(final int i) {
		this._trueMpr += i;
		this._mpr = (short) Math.max(0, this._trueMpr);
	}

	private int bossDmg = 0;
	public void addBossDmg(final int i) {
		this.bossDmg = i;
	}
	public final int getBossDmg() {
		return this.bossDmg;
	}
	private int pvpDmg = 0;
	public void addPVPDmg(final int i) {
		this.pvpDmg += i;
	}
	public final int getPVPDmg() {
		return this.pvpDmg;
	}

	public short _originalHpr = 0; // ● CON HPR

	public short getOriginalHpr() {

		return this._originalHpr;
	}

	public short _originalMpr = 0; // ● WIS MPR

	public short getOriginalMpr() {

		return this._originalMpr;
	}

	private boolean _mpRegenActive;
	private boolean _mpReductionActiveByAwake;
	private boolean _hpRegenActive;
	
 	private static Timer _regenTimer = new Timer(true);

	private int _hpRegenType = 0;
	private int _hpRegenState = 4;

	public int getHpRegenState() {
		return this._hpRegenState;
	}

	public void set_hpRegenType(final int hpRegenType) {
		this._hpRegenType = hpRegenType;
	}

	public int hpRegenType() {
		return this._hpRegenType;
	}

	private int regenMax() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9, 3, 2 };

		int regenLvl = Math.min(10, getLevel());
		if ((30 <= getLevel()) && isKnight()) {
			regenLvl = 11;
		}
		return lvlTable[regenLvl - 1] << 2;
	}

	/**
	 * HP回復成立
	 * @return
	 */
	public boolean isRegenHp() {
		if (!_hpRegenActive) {
			return false;
		}
		if (hasSkillEffect(EXOTIC_VITALIZE) || hasSkillEffect(ADDITIONAL_FIRE)) {
			return _hpRegenType >= regenMax();
		}
		if(120 <= _inventory.getWeight182()) {
			return false;
		}
		if ((_food < 3)) {
			return false;
		}
		return _hpRegenType >= regenMax();
	}

	private int _mpRegenType = 0;
	private int _mpRegenState = 4;

	public int getMpRegenState() {
		return this._mpRegenState;
	}

	public void set_mpRegenType(final int hpmpRegenType) {
		this._mpRegenType = hpmpRegenType;
	}

	public int mpRegenType() {
		return this._mpRegenType;
	}

	/**
	 * MP回復成立
	 * @return
	 */
	public boolean isRegenMp() {
		if (!this._mpRegenActive) {
			return false;
		}
		if (this.hasSkillEffect(EXOTIC_VITALIZE) || this.hasSkillEffect(ADDITIONAL_FIRE)) {
			return this._mpRegenType >= 64;
		}
		if(120 <= this._inventory.getWeight182()) {
			return false;
		}
		if ((this._food < 3)) {
			return false;
		}
		// 法師加速
//		if (this.isWizard()) {
//			return this._mpRegenType >= 40;
//		}
		return this._mpRegenType >= 64;
	}

	// HP自然回復 MP自然回復
	
	/**無動作*/
	public static final int REGENSTATE_NONE = 4;
	
	/**移動中*/
	public static final int REGENSTATE_MOVE = 2;
	
	/**攻擊中*/
	public static final int REGENSTATE_ATTACK = 1;

	public void setHPRegenState(final int state) {
		this._hpRegenState = state;
	}

	public void setMPRegenState(final int state) {
		this._mpRegenState = state;
	}

	/**
	 * HP自然回復啟用
	 */
	public void startHpRegeneration() {
		if (!this._hpRegenActive) {
			this._hpRegenActive = true;
		}
	}

	/**
	 * HP自然回復停止
	 */
	public void stopHpRegeneration() {
		if (this._hpRegenActive) {
			this._hpRegenActive = false;
		}
	}

	/**
	 * HP自然回復狀態
	 * @return 
	 */
	public boolean getHpRegeneration() {
		return _hpRegenActive;
	}

	/**
	 * MP自然回復啟用
	 */
	public void startMpRegeneration() {
		if (!this._mpRegenActive) {
			this._mpRegenActive = true;
		}
	}

	/**
	 * MP自然回復停止
	 */
	public void stopMpRegeneration() {
		if (this._mpRegenActive) {
			this._mpRegenActive = false;
		}
	}

	/**
	 * MP自然回復狀態
	 * @return 
	 */
	public boolean getMpRegeneration() {
		return _mpRegenActive;
	}

	/**
	 * 加入PC 可見物更新處理清單
	 */
	public void startObjectAutoUpdate() {
		this.removeAllKnownObjects();
	}

	/**
	 * 移出各種處理清單
	 */
	public void stopEtcMonitor() {
		// 移出PC 鬼魂模式處理清單
		this.set_ghostTime(-1);
		this.setGhost(false);
		this.setGhostCanTalk(true);
		this.setReserveGhost(false);

		if (ServerUseMapTimer.MAP.get(this) != null) {
			// 移出計時地圖處理清單
			ServerUseMapTimer.MAP.remove(this);
		}

		// 移出在線獎勵清單
		OnlineGiftSet.remove(this);
		
		// 清空清單資料
		ListMapUtil.clear(_skillList);
		ListMapUtil.clear(_sellList);
		ListMapUtil.clear(_buyList);
		ListMapUtil.clear(_trade_items);
	}
	
	/**时间地图状态*/
    private boolean _rocksPrisonActive;
 
	   /** 奇岩地监时间(秒). */
    private int _rocksPrisonTime = 0;
    /** 设置奇岩地监时间.*/
    public int getRocksPrisonTime() {
    return this._rocksPrisonTime;
    }
    /** 设置奇岩地监时间.*/
    public void setRocksPrisonTime(final int time) {
    this._rocksPrisonTime = time;
    }
    /** 限制时间地图2 */
    private int _rocksPrisonTime2 = 0;
    /** 限制时间地图2*/
    public int getRocksPrisonTime2() {
    return this._rocksPrisonTime2;
    }
    /** 限制时间地图2.*/
    public void setRocksPrisonTime2(final int time) {
    this._rocksPrisonTime2 = time;
    } 
    private int _rocksPrisonTime3 = 0;
    /** 限制时间地图3*/
    public int getRocksPrisonTime3() {
    return this._rocksPrisonTime3;
    }
    /** 限制时间地图3.*/
    public void setRocksPrisonTime3(final int time) {
    this._rocksPrisonTime3 = time;
    }

/**
 * 任务1
 */
private int _renwu1;

/**
 * 任务1
 * 
 * @return
 */
public int getrenwu1() {
	return this._renwu1;
}

/**
 * 任务1
 * 
 * @param time 时间
 */
public void setrenwu1(final int time) {
	this._renwu1 = time;
}
/**
 * 任务2
 */
private int _renwu2;

/**
 * 任务2
 * 
 * @return
 */
public int getrenwu2() {
	return this._renwu2;
}

/**
 * 任务2
 * 
 * @param time 时间
 */
public void setrenwu2(final int time) {
	this._renwu2 = time;
}
/**
 * 任务3
 */
private int _renwu3;

/**
 * 任务3
 * 
 * @return
 */
public int getrenwu3() {
	return this._renwu3;
}

/**
 * 任务3
 * 
 * @param time 时间
 */
public void setrenwu3(final int time) {
	this._renwu3 = time;
}	
/**
 * 任务4
 */
private int _renwu4;

/**
 * 任务4
 * 
 * @return
 */
public int getrenwu4() {
	return this._renwu4;
}

/**
 * 任务4
 * 
 * @param time 时间
 */
public void setrenwu4(final int time) {
	this._renwu4 = time;
}
/**
 * 任务5
 */
private int _renwu5;

/**
 * 任务5
 * 
 * @return
 */
public int getrenwu5() {
	return this._renwu5;
}


    /**
     * 计时地图(启动开始).1 奇岩 2象牙塔 3 拉斯塔巴德地监  4象牙塔
     */
   
    
    // 掛機打怪臨時標記
    private int _gongji = 0;

    /**
     * 掛機打怪臨時標記
     * 
     * @return
     */
    public int getgongji() {
        return this._gongji;
    }

    /**
     * 掛機打怪臨時標記
     * 
     * @param i
     */
    public void setgongji(final int i) {
        this._gongji = i;
    }

	private int _old_lawful;
	
	/**
	 * 原始Lawful
	 * @return
	 */
	public int getLawfulo() {
		return _old_lawful;
	}
	
	/**
	 * 更新Lawful
	 */
	public void onChangeLawful() {
		if (_old_lawful != getLawful()) {
			_old_lawful = getLawful();
			sendPacketsAll(new S_Lawful(this));
			// 戰鬥特化效果
			lawfulUpdate();
		}
	}
	
	private boolean _jl1 = false;// 正義的守護 Lv.1
	private boolean _jl2 = false;// 正義的守護 Lv.2
	private boolean _jl3 = false;// 正義的守護 Lv.3
	private boolean _el1 = false;// 邪惡的守護 Lv.1
	private boolean _el2 = false;// 邪惡的守護 Lv.2
	private boolean _el3 = false;// 邪惡的守護 Lv.3
	
	/**
	 * TODO 戰鬥特化<BR>
	 */
	private void lawfulUpdate() {
		int l = getLawful();
		
		if (l >= 10000 && l <= 19999) {
			if (!_jl1) {
				overUpdate();
				_jl1 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L1, 1));
				sendPackets(new S_OwnCharStatus(this));
				sendPackets(new S_SPMR(this));
			}
			
		} else if (l >= 20000 && l <= 29999) {
			if (!_jl2) {
				overUpdate();
				_jl2 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L2, 1));
				sendPackets(new S_OwnCharStatus(this));
				sendPackets(new S_SPMR(this));
			}
			
		} else if (l >= 30000 && l <= 39999) {
			if (!_jl3) {
				overUpdate();
				_jl3 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L3, 1));
				sendPackets(new S_OwnCharStatus(this));
				sendPackets(new S_SPMR(this));
			}
			
		} else if (l >= -19999 && l <= -10000) {
			if (!_el1) {
				overUpdate();
				_el1 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L1, 1));
				sendPackets(new S_SPMR(this));
			}
			
		} else if (l >= -29999 && l <= -20000) {
			if (!_el2) {
				overUpdate();
				_el2 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L2, 1));
				sendPackets(new S_SPMR(this));
			}
			
		} else if (l >= -39999 && l <= -30000) {
			if (!_el3) {
				overUpdate();
				_el3 = true;
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L3, 1));
				sendPackets(new S_SPMR(this));
			}
			
		} else {
			if (overUpdate()) {
				sendPackets(new S_OwnCharStatus(this));
				sendPackets(new S_SPMR(this));
			}
		}
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * @return
	 */
	private boolean overUpdate() {
		if (_jl1) {
			_jl1 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L1, 0));
			return true;
			
		} else if (_jl2) {
			_jl2 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L2, 0));
			return true;
			
		} else if (_jl3) {
			_jl3 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.JUSTICE_L3, 0));
			return true;
			
		} else if (_el1) {
			_el1 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L1, 0));
			return true;
			
		} else if (_el2) {
			_el2 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L2, 0));
			return true;
			
		} else if (_el3) {
			_el3 = false;
			sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.EVIL_L3, 0));
			return true;
		}
		return false;
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * <FONT COLOR="#0000ff">遭遇的守護 </FONT>20級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品<BR>
	 * @return
	 */
	private boolean isEncounter() {
		if (getLevel() <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * TODO 戰鬥特化<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.1 </FONT>善惡值 10,000 ~ 19,999 (防禦：-2 / 魔防+3)<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.2 </FONT>善惡值 20,000 ~ 29,999 (防禦：-4 / 魔防+6)<BR>
	 * <FONT COLOR="#0000ff">正義的守護 Lv.3 </FONT>善惡值 30,000 ~ 32,767 (防禦：-6 / 魔防+9)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.1 </FONT>善惡值 -10,000 ~ -19,999 (近/遠距離攻擊力+1 / 魔攻+1)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.2 </FONT>善惡值 -20,000 ~ -29,999 (近/遠距離攻擊力+3 / 魔攻+2)<BR>
	 * <FONT COLOR="#0000ff">邪惡的守護 Lv.3 </FONT>善惡值 -30,000 ~ -32,767 (近/遠距離攻擊力+5 / 魔攻+3)<BR>
	 * <FONT COLOR="#0000ff">遭遇的守護 </FONT>20級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品<BR>
	 */
	public int guardianEncounter() {
		if (_jl1) {
			return 0;
			
		} else if (_jl2) {
			return 1;
			
		} else if (_jl3) {
			return 2;
			
		} else if (_el1) {
			return 3;
			
		} else if (_el2) {
			return 4;
			
		} else if (_el3) {
			return 5;
		}
		return -1;
	}

	private long _old_exp;
	
	/**
	 * 原始Lawful
	 * @return
	 */
	public long getExpo() {
		return _old_exp;
	}
	
	/**
	 * 更新EXP
	 */
	public void onChangeExp() {
		if (_old_exp != getExp()) {
			_old_exp = getExp();
			
			final int level = ExpTable.getLevelByExp(getExp());
			final int char_level = getLevel();
			final int gap = level - char_level;
			
			if (gap == 0) {
				if (level <= 127) {
					sendPackets(new S_Exp(this));
				} else {
					sendPackets(new S_OwnCharStatus(this));
				}
				return;
			}
			
			if (gap > 0) {
				levelUp(gap);
				
			} else if (gap < 0) {
				levelDown(gap);
			}
			
			if (getLevel() > 20) {// LOLI 戰鬥特化
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 0));
				
			} else {
				sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 1));
			}
		}
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 判斷旅館內是否使用相同鑰匙
			// 副本ID相等

			if (perceivedFrom.getMapId() >= 16384 && perceivedFrom.getMapId() <= 25088
					&& perceivedFrom.getInnKeyId() != getInnKeyId()) {// 旅館內判斷
				return;
			}
			if (this.isGmInvis() || this.isGhost() || this.isInvisble()) {
				return;
			}
			
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != this.get_showId()) {
				return;
			}

			perceivedFrom.addKnownObject(this);
			// 發送自身資訊給予接觸人物
			perceivedFrom.sendPackets(new S_OtherCharPacks(this));
			
			// 隊伍成員HP狀態發送
			if (this.isInParty() && this.getParty().isMember(perceivedFrom)) {
				perceivedFrom.sendPackets(new S_HPMeter(this));
			}

			if (_isFishing) {
				perceivedFrom.sendPackets(new S_Fishing(getId(), ActionCodes.ACTION_Fishing, get_fishX(), get_fishY()));
			}
			
			if (this.isPrivateShop()) {
				final int mapId = this.getMapId();
				if ((mapId != 340) && (mapId != 350) && (mapId != 360) && (mapId != 370)) {
					this.getSellList().clear();
					this.getBuyList().clear();
					
					this.setPrivateShop(false);
					this.sendPacketsAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Idle));
					
				} else {
					perceivedFrom.sendPackets(new S_DoActionShop(this.getId(), this.getShopChat()));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 清除離開可視範圍物件
	 */
	private void removeOutOfRangeObjects() {
		for (final L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // 畫面外
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
				
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	/**
	 * 可見物更新處理
	 */
	public void updateObject() {
		if (getOnlineStatus() != 1) {
			return;
		}
		removeOutOfRangeObjects();

		// 指定可視範圍資料更新
		for (final L1Object visible : World.get().getVisibleObjects(this, Config.PC_RECOGNIZE_RANGE)) {
			if (visible instanceof L1MerchantInstance) {// 對話NPC
				if (!knownsObject(visible)) {
					final L1MerchantInstance npc = (L1MerchantInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}
			
			if (visible instanceof L1DwarfInstance) {// 倉庫NPC
				if (!knownsObject(visible)) {
					final L1DwarfInstance npc = (L1DwarfInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}
			
			if (visible instanceof L1FieldObjectInstance) {// 景觀
				if (!knownsObject(visible)) {
					final L1FieldObjectInstance npc = (L1FieldObjectInstance) visible;
					// 未認知物件 執行物件封包發送
					npc.onPerceive(this);
				}
				continue;
			}

			// 副本ID不相等 不相護顯示
			if (visible.get_showId() != get_showId()) {
				continue;
			}
			
			if (!knownsObject(visible)) {
				// 未認知物件 執行物件封包發送
				visible.onPerceive(this);
				
			} else {
				if (visible instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) visible;

					if (getLocation().isInScreen(npc.getLocation()) && (npc.getHiddenStatus() != 0)) {
						npc.approachPlayer(this);
					}
				}
			}
			
			// 一般人物 HP可見設置
			if (isHpBarTarget(visible)) {
				final L1Character cha = (L1Character) visible;
				cha.broadcastPacketHP(this);
			}
			
			// GM HP 查看設置
			if (hasSkillEffect(GMSTATUS_HPBAR)) {
				if (isGmHpBarTarget(visible)) {
					final L1Character cha = (L1Character) visible;
					cha.broadcastPacketHP(this);
				} else { // 旅館內判斷
					final ArrayList<L1Object> objs = World.get().getVisibleObjects(this);
					for (int i = 0; i < objs.size(); i++) {
						if (!knownsObject(visible)) {
							visible.onPerceive(this);
						}
						if (hasSkillEffect(GMSTATUS_HPBAR) && L1HpBar.isHpBarTarget(visible)) {
							if (getInnKeyId() == ((L1Character) visible).getInnKeyId()) {
								sendPackets(new S_HPMeter((L1Character) visible));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 可以觀看HP的對象(特別定義)
	 * @param obj
	 * @return
	 */
	public boolean isHpBarTarget(final L1Object obj) {
		// 所在地圖位置
		switch(this.getMapId()) {
		case 400:// 大洞穴/大洞穴抵抗軍/隱遁者地區
			if (obj instanceof L1FollowerInstance) {
				final L1FollowerInstance follower = (L1FollowerInstance) obj;
				if (follower.getMaster().equals(this)) {
					return true;
				}
			}
			break;
		}
		return false;
	}

	/**
	 * GM HPBAR 可以觀看HP的對象
	 * @param obj
	 * @return
	 */
	public boolean isGmHpBarTarget(final L1Object obj) {
		if (obj instanceof L1MonsterInstance) {
			return true;
		}
		if (obj instanceof L1PcInstance) {
			return true;
		}
		if (obj instanceof L1SummonInstance) {
			return true;
		}
		if (obj instanceof L1PetInstance) {
			return true;
		}
		if (obj instanceof L1FollowerInstance) {
			return true;
		}
		if (obj instanceof L1TowerInstance) {
			return true;
		}
		if (obj instanceof L1FieldObjectInstance) {
			return true;
		}
		if (obj instanceof L1DoorInstance) {
			return true;
		}
		return false;
	}
	
	private void sendVisualEffect() {
		int poisonId = 0;
		if (this.getPoison() != null) { // 毒狀態
			poisonId = this.getPoison().getEffectId();
		}
		if (this.getParalysis() != null) { // 麻痺狀態
			// 麻痺優先送為、poisonId上書。
			poisonId = this.getParalysis().getEffectId();
		}
		if (poisonId != 0) { // if
			this.sendPacketsAll(new S_Poison(this.getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		this.sendVisualEffect();
	}

	private boolean _isCHAOTIC = false;
	
	public boolean isCHAOTIC() {
		return this._isCHAOTIC;
	}

	public void setCHAOTIC(final boolean flag) {
		this._isCHAOTIC = flag;
	}
	
	public void sendVisualEffectAtTeleport() {
		if (this.isDrink()) { // 醉酒效果
			this.sendPackets(new S_Liquor(this.getId()));
		}
		if (this.isCHAOTIC()) { // 混亂效果
			this.sendPackets(new S_Liquor(this.getId(), 2));
		}
		this.sendVisualEffect();
	}

	// 可用技能編號列表
	private ArrayList<Integer> _skillList = new ArrayList<Integer>();

	/**
	 * 加入技能編號列表
	 * @param skillid
	 */
	public void setSkillMastery(final int skillid) {
		int new_skill_id = skillid;
//		switch (skillid) {
//			case 9:
//			case 10:
//			case 11:
//			case 12:
//			case 13:
//				if (this.isKnight()) {
//					new_skill_id += 79; // 需配合登入器
//				}
//				break;
//		}
		if (!this._skillList.contains(new Integer(new_skill_id))) {
			this._skillList.add(new Integer(new_skill_id));
		}
	}

	/**
	 * 移出技能編號列表
	 * @param skillid
	 */
	public void removeSkillMastery(final int skillid) {
		if (this._skillList.contains(new Integer(skillid))) {
			this._skillList.remove(new Integer(skillid));
		}
	}

	/**
	 * 傳回是否具有該技能使用權
	 * @param skillid
	 * @return
	 */
	public boolean isSkillMastery(final int skillid) {
		return this._skillList.contains(new Integer(skillid));
	}

	/**
	 * 清空
	 */
	public void clearSkillMastery() {
		this._skillList.clear();
	}

	/**
	 * TODO 起始設置
	 */
	public L1PcInstance(ClientExecutor client) {
		this._out = client;
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_dwarf = new L1DwarfInventory(this);
		_dwarfForElf = new L1DwarfForElfInventory(this);
		_quest = new L1PcQuest(this);
		_action = new L1ActionPc(this);
		_actionPet = new L1ActionPet(this);
		_actionSummon = new L1ActionSummon(this);
		_equipSlot = new L1EquipmentSlot(this);
	}

	/**
	 * 娃娃跟隨主人變更移動/速度狀態
	 */
	public void setNpcSpeed() {
		try {
			// 取回娃娃
			if (!getDolls().isEmpty()) {
				for (final Object obj : getDolls().values().toArray()) {
					final L1DollInstance doll = (L1DollInstance) obj;
					if (doll != null) {
						doll.setNpcMoveSpeed();
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void setCurrentHp(final int i) {
		int currentHp = Math.min(i, this.getMaxHp());

		if (this.getCurrentHp() == currentHp) {
			return;
		}

		if (currentHp <= 0) {
			if (this.isGm()) {
				currentHp = this.getMaxHp();

			} else {
				if (!this.isDead()) {
					this.death(null); // HP小於1死亡
				}
			}
		}

		this.setCurrentHpDirect(currentHp);
		this.sendPackets(new S_HPUpdate(currentHp, this.getMaxHp()));
		if (this.isInParty()) { // 隊伍狀態
			this.getParty().updateMiniHP(this);
		}
	}


	@Override
	public void setCurrentMp(final int i) {
		int currentMp = Math.min(i, this.getMaxMp());

		if (this.getCurrentMp() == currentMp) {
			return;
		}

		this.setCurrentMpDirect(currentMp);
		
		this.sendPackets(new S_MPUpdate(currentMp, this.getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return this._inventory;
	}

	public L1DwarfInventory getDwarfInventory() {
		return this._dwarf;
	}

	public L1DwarfForElfInventory getDwarfForElfInventory() {
		return this._dwarfForElf;
	}

	public boolean isGmInvis() {
		return this._gmInvis;
	}

	public void setGmInvis(final boolean flag) {
		this._gmInvis = flag;
	}

	/**
	 * 傳回裝備的武器類型
	 * @return
	 */
	public int getCurrentWeapon() {
		return this._currentWeapon;
	}

	/**
	 * 設置裝備的武器類型
	 * @param i
	 */
	public void setCurrentWeapon(final int i) {
		this._currentWeapon = i;
	}

	/**
	 * 0:王族 1:騎士 2:精靈 3:法師 4:黑妖
	 * @return
	 */
	public int getType() {
		return this._type;
	}

	/**
	 * 0:王族 1:騎士 2:精靈 3:法師 4:黑妖
	 * @param i
	 */
	public void setType(final int i) {
		this._type = i;
	}

	public short getAccessLevel() {
		return this._accessLevel;
	}

	public void setAccessLevel(final short i) {
		this._accessLevel = i;
	}

	public int getClassId() {
		return this._classId;
	}

	public void setClassId(final int i) {
		this._classId = i;
		this._classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public long getExp() {
		return _exp;
	}

	@Override
	public void setExp(final long i) {
		_exp = i;
	}

	private int _PKcount; // ● PK

	public int get_PKcount() {
		return this._PKcount;
	}

	public void set_PKcount(final int i) {
		this._PKcount = i;
	}

	private int _PkCountForElf; // ● PK(用)

	public int getPkCountForElf() {
		return this._PkCountForElf;
	}

	public void setPkCountForElf(final int i) {
		this._PkCountForElf = i;
	}

	private int _clanid; // 血盟ID

	public int getClanid() {
		return this._clanid;
	}

	public void setClanid(final int i) {
		this._clanid = i;
	}

	private String clanname; // 血盟名稱

	public String getClanname() {
		return this.clanname;
	}

	public void setClanname(final String s) {
		this.clanname = s;
	}

	/**
	 * 血盟資料
	 * @return
	 */
	public L1Clan getClan() {
		return WorldClan.get().getClan(this.getClanname());
	}

	private int _clanRank; // ● 內(血盟君主、、一般、見習)

	/**
	 * 血盟階級
	 * @return
	 */
	public int getClanRank() {
		return this._clanRank;
	}

	public void setClanRank(final int i) {
		this._clanRank = i;
	}

	private byte _sex; // ● 性別

	/**
	 * 性別
	 * @return
	 */
	public byte get_sex() {
		return this._sex;
	}

	/**
	 * 性別
	 * @param i
	 */
	public void set_sex(final int i) {
		this._sex = (byte) i;
	}

	public boolean isGm() {
		return this._gm;
	}

	public boolean isRemovePcShop() {
		return this.removePcShop;
	}

	public final void setRemovePcShop(final boolean set) {
		this.removePcShop = set;
	}

	public void setGm(final boolean flag) {
		this._gm = flag;
	}

	public boolean isMonitor() {
		return this._monitor;
	}

	public void setMonitor(final boolean flag) {
		this._monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(final double d, final L1Character l1character) {
		this.getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * 指定群通知
	 *
	 * @param playersList
	 *            通知配列
	 */
	private void notifyPlayersLogout(final List<L1PcInstance> playersArray) {
		for (final L1PcInstance player : playersArray) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
    	// 20171122 墓碑
    	L1EffectInstance tomb = this.get_tomb();
		if (tomb != null) {
			tomb.broadcastPacketAll(new S_DoActionGFX(tomb.getId(), 8));
			tomb.deleteMe();
		}
		// 保留技能紀錄
		CharBuffReading.get().deleteBuff(this);
		CharBuffReading.get().saveBuff(this);

		// 解除舊座標障礙宣告
		this.getMap().setPassable(this.getLocation(), true);

		if (this.getClanid() != 0) {
			final L1Clan clan = WorldClan.get().getClan(this.getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == this.getId()) {
					clan.setWarehouseUsingChar(0); // 解除血盟倉庫目前使用者
				}
			}
		}
		this.notifyPlayersLogout(this.getKnownPlayers());

		// 正在參加副本
		if (this.get_showId() != -1) {
			// 副本編號 是執行中副本
			if (WorldQuest.get().isQuest(this.get_showId())) {
				// 移出副本
				WorldQuest.get().remove(this.get_showId(), this);
			}
		}
		// 重置副本編號
		this.set_showId(-1);
		
		World.get().removeVisibleObject(this);
		World.get().removeObject(this);
		this.notifyPlayersLogout(World.get().getRecognizePlayer(this));
		
		//this._inventory.clearItems();
		//this._dwarf.clearItems();
		
		this.removeAllKnownObjects();
		this.stopHpRegeneration();
		this.stopMpRegeneration();
		this.setDead(true); // 使方、ＮＰＣ消滅
		this.setNetConnection(null);
	}

	public ClientExecutor getNetConnection() {
		return this._netConnection;
	}

	public void setNetConnection(final ClientExecutor clientthread) {
		this._netConnection = clientthread;
	}

	/**
	 * 是否再隊伍中
	 * @return
	 */
	public boolean isInParty() {
		return this.getParty() != null;
	}
	
    public String getIp() {
		return _netConnection.getIp().toString();
	}

	/**
	 * 傳回隊伍
	 * @return
	 */
	public L1Party getParty() {
		return this._party;
	}

	/**
	 * 設置隊伍
	 * @param p
	 */
	public void setParty(final L1Party p) {
		this._party = p;
	}

	public boolean isInChatParty() {
		return this.getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return this._chatParty;
	}

	public void setChatParty(final L1ChatParty cp) {
		this._chatParty = cp;
	}

	public int getPartyID() {
		return this._partyID;
	}

	public void setPartyID(final int partyID) {
		this._partyID = partyID;
	}

	public int getTradeID() {
		return this._tradeID;
	}

	public void setTradeID(final int tradeID) {
		this._tradeID = tradeID;
	}

	public void setTradeOk(final boolean tradeOk) {
		this._tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return this._tradeOk;
	}

	/**
	 * 傳回暫時紀錄的objid
	 * @return
	 */
	public int getTempID() {
		return this._tempID;
	}

	/**
	 * 設置暫時紀錄的objid
	 * @param tempID
	 */
	public void setTempID(final int tempID) {
		this._tempID = tempID;
	}

	/**
	 * 是否為傳送狀態中
	 * @return
	 */
	public boolean isTeleport() {
		return this._isTeleport;
	}

	/**
	 * 設置傳送狀態中
	 * @param flag
	 */
	public void setTeleport(final boolean flag) {
		if (flag) {
			this.setNowTarget(null);// 解除目前攻擊目標設置
		}
		this._isTeleport = flag;
	}

	/**
	 * 醉酒狀態
	 * @return
	 */
	public boolean isDrink() {
		return this._isDrink;
	}

	/**
	 * 醉酒狀態
	 * @param flag
	 */
	public void setDrink(final boolean flag) {
		this._isDrink = flag;
	}

	public boolean isGres() {
		return this._isGres;
	}

	public void setGres(final boolean flag) {
		this._isGres = flag;
	}

	/**
	 * 紅名狀態
	 * @return
	 */
	public boolean isPinkName() {
		return this._isPinkName;
	}

	/**
	 * 紅名狀態
	 * @param flag
	 */
	public void setPinkName(final boolean flag) {
		this._isPinkName = flag;
	}

	// 賣出物品清單
	private ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	/**
	 * 傳回賣出物品清單
	 * @return
	 */
	public ArrayList<L1PrivateShopSellList> getSellList() {
		return this._sellList;
	}

	// 回收物品清單
	private ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	/**
	 * 傳回回收物品清單
	 * @return
	 */
	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return this._buyList;
	}

	private byte[] _shopChat;

	public void setShopChat(final byte[] chat) {
		this._shopChat = chat;
	}

	public byte[] getShopChat() {
		return this._shopChat;
	}

	private boolean _isPrivateShop = false;

	/**
	 * 傳回商店模式
	 * @return
	 */
	public boolean isPrivateShop() {
		return this._isPrivateShop;
	}

	/**
	 * 設置商店模式
	 * @param flag
	 */
	public void setPrivateShop(final boolean flag) {
		this._isPrivateShop = flag;
	}

	// 正在執行個人商店交易
	private boolean _isTradingInPrivateShop = false;

	/**
	 * 正在執行個人商店交易
	 * @return
	 */
	public boolean isTradingInPrivateShop() {
		return this._isTradingInPrivateShop;
	}

	/**
	 * 正在執行個人商店交易
	 * @param flag
	 */
	public void setTradingInPrivateShop(final boolean flag) {
		this._isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // 出售物品種類數量

	/**
	 * 傳回出售物品種類數量
	 * @return
	 */
	public int getPartnersPrivateShopItemCount() {
		return this._partnersPrivateShopItemCount;
	}

	/**
	 * 設置出售物品種類數量
	 * @param i
	 */
	public void setPartnersPrivateShopItemCount(final int i) {
		this._partnersPrivateShopItemCount = i;
	}
	
	private ClientExecutor _out;// 封包加密管理

	/**
	 * 發送單體封包
	 * @param packet 封包
	 */
	public void sendPackets(final OpcodesServer packet) {
		try {
			//System.out.println("sendPackets:" + packet);
			if (this._out != null) {
				if (this._out.get_session() == null) {
					World.get().removeObject(this);
					return;
				}
				this._out.toSender(packet);
			} else {
				BasePacketPooling.setPool(packet);
			}
		} catch (final Exception e) {
			this.logout();
			this.close();
		}
	}

	/**
	 * 發送單體封包
	 * 與可見範圍發送封包
	 * @param packet 封包
	 */
	public void sendPacketsAll(final OpcodesServer packet) {
		try {
			if (this._out != null) {
				this._out.toSender(packet);
				if (!this.isGmInvis() && !this.isInvisble()) {
					this.broadcastPacketAll(packet);
				}
			} else {
				BasePacketPooling.setPool(packet);
			}
		} catch (final Exception e) {
			this.logout();
			this.close();
		}
	}

	/**
	 * 發送單體封包
	 * 與指定範圍發送封包(範圍8)
	 * @param packet 封包
	 */
	public void sendPacketsX8(final OpcodesServer packet) {
		try {
			if (this._out != null) {
				// 自己
				this._out.toSender(packet);
				if (!this.isGmInvis() && !this.isInvisble()) {
					this.broadcastPacketX8(packet);
				}
			} else {
				BasePacketPooling.setPool(packet);
			}
		} catch (final Exception e) {
			this.logout();
			this.close();
		}
	}

	/**
	 * 發送單體封包
	 * 與指定範圍發送封包(範圍10)
	 * @param packet 封包
	 */
	public void sendPacketsX10(final OpcodesServer packet) {
		try {
			if (this._out != null) {
				// 自己
				this._out.toSender(packet);
				if (!this.isGmInvis() && !this.isInvisble()) {
					this.broadcastPacketX10(packet);
				}
			} else {
				BasePacketPooling.setPool(packet);
			}
		} catch (final Exception e) {
			this.logout();
			this.close();
		}
	}

	/**
	 * 發送單體封包
	 * 與可見指定範圍發送封包
	 * @param packet 封包
	 * @param r 範圍
	 */
	public void sendPacketsXR(final OpcodesServer packet, final int r) {
		try {
			if (this._out != null) {
				// 自己
				this._out.toSender(packet);
				if (!this.isGmInvis() && !this.isInvisble()) {
					this.broadcastPacketXR(packet, r);
				}
			} else {
				BasePacketPooling.setPool(packet);
			}
		} catch (final Exception e) {
			this.logout();
			this.close();
		}
	}

	/**
	 * 關閉連線線程
	 */
	private void close() {
		try {
			this.getNetConnection().close();
		} catch (final Exception e) {

		}
	}

	/**
	 * 對該物件攻擊的調用
	 * @param attacker 攻擊方
	 */
	@Override
	public void onAction(final L1PcInstance attacker) {
		// NullPointerException迴避。onAction引數型L1Character良？
		if (attacker == null) {
			return;
		}
		// 處理中
		if (this.isTeleport()) {
			return;
		}
		
		// 雙方之一 位於安全區域  僅送出動作資訊
		if (this.isSafetyZone() || attacker.isSafetyZone()) {
			// 攻擊送信
			final L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
			attack_mortion.action();
			return;
		}

		// 禁止PK服務器 僅送出動作資訊
		if (this.checkNonPvP(this, attacker) == true) {
			final L1AttackMode attack_mortion = new L1AttackPc(attacker, this);
			attack_mortion.action();
			return;
		}

		if ((this.getCurrentHp() > 0) && !this.isDead()) {
			// 攻擊行為產生解除隱身
			attacker.delInvis();

			boolean isCounterBarrier = false;
			// 開始計算攻擊
			final L1AttackMode attack = new L1AttackPc(attacker, this);
			if (attack.calcHit()) {
				if (this.hasSkillEffect(COUNTER_BARRIER)) {
					final L1Magic magic = new L1Magic(this, attacker);
					final boolean isProbability = magic.calcProbabilityMagic(COUNTER_BARRIER);
					final boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				}
				if (!isCounterBarrier) {
					attacker.setPetTarget(this);

					attack.calcDamage();
					attack.calcStaffOfMana();
					//attack.addChaserAttack();
				}
			}
			if (isCounterBarrier) {
				//attack.actionCounterBarrier();
				attack.commitCounterBarrier();
				
			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	/**
	 * 檢查是否可以攻擊
	 * @param pc
	 * @param target
	 * @return
	 */
	public boolean checkNonPvP(final L1PcInstance pc, final L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;
			
		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
			
		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // 相手PC、、以外
		}
		
		if (!ConfigAlt.ALT_NONPVP) { // Non-PvP設定
			if (this.getMap().isCombatZone(this.getLocation())) {
				return false;
			}

			// 取回全部戰爭清單
			for (final L1War war : WorldWar.get().getWarList()) {
				if ((pc.getClanid() != 0) && (targetpc.getClanid() != 0)) { // 共所屬中
					final boolean same_war = war.checkClanInSameWar(pc.getClanname(),
							targetpc.getClanname());
					if (same_war == true) { // 同戰爭參加中
						return false;
					}
				}
			}
			// Non-PvP設定戰爭中佈告攻擊可能
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;
				if (this.isInWarAreaAndWarTime(pc, targetPc)) {
					return false;
				}
			}
			return true;
			
		} else {
			return false;
		}
	}

	/**
	 * 戰爭旗幟座標內
	 * @param pc
	 * @param target
	 * @return
	 */
	private boolean isInWarAreaAndWarTime(final L1PcInstance pc, final L1PcInstance target) {
		// pctarget戰爭中戰爭居
		final int castleId = L1CastleLocation.getCastleIdByArea(pc);
		final int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if ((castleId != 0) && (targetCastleId != 0) && (castleId == targetCastleId)) {
			if (ServerWarExecutor.get().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	private static boolean _debug = Config.DEBUG;
	
	/**
	 * 設置 寵物/召換獸/分身/護衛 攻擊目標
	 * @param target
	 */
	public void setPetTarget(final L1Character target) {
		if (target == null) {
			return;
		}
		if (target.isDead()) {
			return;
		}
		final Map<Integer, L1NpcInstance> petList = 
			this.getPetList();
		
		// 有寵物元素
		try {
			if (!petList.isEmpty()) {// 有寵物元素
				for (final Iterator<L1NpcInstance> iter = petList.values().iterator(); iter.hasNext();) {
					final L1NpcInstance pet = iter.next();
					if (pet != null) {
						if (pet instanceof L1PetInstance) {// 寵物
							final L1PetInstance pets = (L1PetInstance) pet;
							pets.setMasterTarget(target);
							
						} else if (pet instanceof L1SummonInstance) {// 召換獸
							final L1SummonInstance summon = (L1SummonInstance) pet;
							summon.setMasterTarget(target);
						}
					}
				}
			}

		} catch (final Exception e) {
			if (_debug) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
		
		final Map<Integer, L1IllusoryInstance> illList = 
			this.get_otherList().get_illusoryList();

		// 有分身元素
		try {
			if (!illList.isEmpty()) {// 有分身元素
				// 控制分身攻擊
				if (this.getId() != target.getId()) {
					for (final Iterator<L1IllusoryInstance> iter = illList.values().iterator(); iter.hasNext();) {
						final L1IllusoryInstance ill = iter.next();
						if (ill != null) {
							ill.setLink(target);
						}
					}
				}
			}

		} catch (final Exception e) {
			if (_debug) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * 解除隱身術/暗隱術
	 */
	public void delInvis() {
		if (this.hasSkillEffect(INVISIBILITY)) { // 隱身術
			this.killSkillEffectTimer(INVISIBILITY);
			this.sendPackets(new S_Invis(this.getId(), 0));
			this.broadcastPacketAll(new S_OtherCharPacks(this));
		}
		if (this.hasSkillEffect(BLIND_HIDING)) { // 暗隱術
			this.killSkillEffectTimer(BLIND_HIDING);
			this.sendPackets(new S_Invis(this.getId(), 0));
			this.broadcastPacketAll(new S_OtherCharPacks(this));
		}
	}

	/**
	 * 解除暗隱術
	 */
	public void delBlindHiding() {
		this.killSkillEffectTimer(BLIND_HIDING);
		this.sendPackets(new S_Invis(this.getId(), 0));
		this.broadcastPacketAll(new S_OtherCharPacks(this));
	}

	/**
	 * 魔法具有屬性傷害使用 (魔法抗性處理) attr:0.無屬性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法
	 * (武器技能使用)
	 * @param attacker
	 * @param damage
	 * @param attr
	 */
	public void receiveDamage(final L1Character attacker, double damage, final int attr) {
		final int player_mr = this.getMr();
		final int rnd = _random.nextInt(300) + 1;
		if (player_mr >= rnd) {
			damage /= 2.0;
		}
		
		int resist = 0;
		switch (attr) {
		case L1Skills.ATTR_EARTH:
			resist = this.getEarth();
			break;
			
		case L1Skills.ATTR_FIRE:
			resist = this.getFire();
			break;
			
		case L1Skills.ATTR_WATER:
			resist = this.getWater();
			break;
			
		case L1Skills.ATTR_WIND:
			resist = this.getWind();
			break;
		}
		
		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;

		} else {
			resistFloor *= -1;
		}
		
		final double attrDeffence = resistFloor / 32.0;
		
		double coefficient = (1.0 - attrDeffence + 3.0 / 32.0);//0.09375

		if (coefficient > 0) {
			damage *= coefficient;
		}
		this.receiveDamage(attacker, damage, false, false);
	}

	/**
	 * 受攻擊mp減少計算
	 * @param attacker
	 * @param mpDamage
	 */
	public void receiveManaDamage(final L1Character attacker, final int mpDamage) {
		if ((mpDamage > 0) && !this.isDead()) {
			this.delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}
			if ((attacker instanceof L1PcInstance)
					&& ((L1PcInstance) attacker).isPinkName()) {
				// 畫面內、攻擊者設定
				for (final L1Object object : World.get().getVisibleObjects(
						attacker)) {
					if (object instanceof L1GuardInstance) {
						final L1GuardInstance guard = (L1GuardInstance) object;
						guard.setTarget(((L1PcInstance) attacker));
					}
				}
			}

			int newMp = this.getCurrentMp() - mpDamage;
			if (newMp > this.getMaxMp()) {
				newMp = this.getMaxMp();
			}
			newMp = Math.max(newMp, 0);

			this.setCurrentMp(newMp);
		}
	}
	
	public long _oldTime = 0; // 連續魔法減低損傷使用
	
	private static final Map<Long, Double> _magicDamagerList = new HashMap<Long, Double>();

	/**
	 * 連續魔法減低損傷質預先載入
	 * 特殊定義道具 預先載入
	 */
	public static void load() {
		double newdmg = 100.00;
		for (long i = 2000 ; i > 0 ; i--) {
			if (i % 100 == 0) {
				newdmg -= 3.33;
			}
			_magicDamagerList.put(i, newdmg);
		}
	}
	
	/**
	 * 連續魔法減低損傷
	 * @param damage
	 * @return
	 */
	public double isMagicDamager(final double damage) {
		final long nowTime = System.currentTimeMillis();
		final long interval = nowTime - this._oldTime;

		double newdmg = 0;
		if (damage < 0) {
			newdmg = damage;
			
		} else {
			Double tmpnewdmg = _magicDamagerList.get(interval);
			if (tmpnewdmg != null) {
				newdmg = (damage * tmpnewdmg) / 100;
				
			} else {
				newdmg = damage;
			}
			newdmg = Math.max(newdmg, 0);
			
			this._oldTime = nowTime; // 次回時間紀錄
		}
		return newdmg;
	}

	/**
	 * 受攻擊hp減少計算
	 * @param attacker 攻擊者
	 * @param damage 傷害
	 * @param isMagicDamage 連續魔法傷害減低
	 * @param isCounterBarrier 這個傷害是否不執行反饋 true:不執行反饋 false:執行反饋
	 */
	public void receiveDamage(final L1Character attacker, 
			double damage, 
			final boolean isMagicDamage, final boolean isCounterBarrier) {
		if ((this.getCurrentHp() > 0) && !this.isDead()) {
			
			if (attacker != null) {
				if (attacker != this) {
					if (!(attacker instanceof L1EffectInstance) && 
							!this.knownsObject(attacker) && 
							attacker.getMapId() == this.getMapId()) {
						attacker.onPerceive(this);
					}
				}

				// 連續魔法傷害減低
				if (isMagicDamage == true) {
					damage = this.isMagicDamager(damage);
				}
				
				// 攻擊者定義
				L1PcInstance attackPc = null;
				L1NpcInstance attackNpc = null;
				
				if (attacker instanceof L1PcInstance) {
					attackPc = (L1PcInstance) attacker;// 攻擊者為PC
					
				} else if (attacker instanceof L1NpcInstance) {
					attackNpc = (L1NpcInstance) attacker;// 攻擊者為NPC
				}
				
				// 傷害大於等於0(小於0回復HP)
				if (damage > 0) {
					// 解除隱身
					this.delInvis();
					// 解除沉睡之霧
					this.removeSkillEffect(FOG_OF_SLEEPING);
					
					if (attackPc != null) {
						L1PinkName.onAction(this, attackPc);
						if (attackPc.isPinkName()) {
							// 警衛對攻擊者的處分
							for (final L1Object object : World.get().getVisibleObjects(attacker)) {
								if (object instanceof L1GuardInstance) {
									final L1GuardInstance guard = (L1GuardInstance) object;
									guard.setTarget(((L1PcInstance) attacker));
								}
							}
						}
					}
					
					
				}
			}
			
			// 裝備使自己傷害加深的裝備
			if (this.getInventory().checkEquipped(145) // 狂戰士斧
					|| this.getInventory().checkEquipped(149)) { // 牛人斧頭
				damage *= 1.5; // 傷害提高1.5倍
			}
			
			int addhp = 0;
			if (_elitePlateMail_Fafurion > 0) {
				if (_random.nextInt(1000) <= _elitePlateMail_Fafurion) {
					this.sendPacketsX8(new S_SkillSound(this.getId(), 2187));
					addhp = _random.nextInt(_fafurion_hpmax - _fafurion_hpmin + 1) + _fafurion_hpmin;// 受到攻擊時，4%的機率會恢復體力72~86點。
				}
			}
			
			int newHp = this.getCurrentHp() - (int) (damage) + addhp;
			if (newHp > this.getMaxHp()) {
				newHp = this.getMaxHp();
			}
			if (newHp <= 0) {
				if (!this.isGm()) {
					this.death(attacker, this.isPinkName());
				}
			}
			
			this.setCurrentHp(newHp);

		} else if (!this.isDead()) {
			_log.error("人物hp減少處理失敗 可能原因: 初始hp為0");
			this.death(attacker, this.isPinkName());
		}
	}
	
//	/**
//	 * 召喚死亡墳墓
//	 * @param npc
//	 * @param gfxid
//	 */
//	private void spawnDeathEffect(final L1NpcInstance npc, final int gfxid) {
//		if (npc != null) {
//			npc.setId(IdFactoryNpc.get().nextId());
//			npc.setGfxId(gfxid);
//			npc.setTempCharGfx(gfxid);
//			npc.setNameId(this.getName() + " 的墳墓");
//			npc.setMap(this.getMapId());
//			npc.setX(this.getX());
//			npc.setY(this.getY());
//			npc.setHomeX(npc.getX());
//			npc.setHomeY(npc.getY());
//			npc.setHeading(4);
//			npc.setGfxidInStatus(gfxid);
//			L1PcInstance.this.sendPacketsX10(new S_Chat(L1PcInstance.this));
//			// 存在時間(秒)
//			npc.set_spawnTime(9);
//			
//			World.get().storeObject(npc);
//			World.get().addVisibleObject(npc);
//		}
//	}

	/**
	 * 死亡的處理
	 * @param lastAttacker 攻擊致死的攻擊者
	 */
	public void death(final L1Character lastAttacker) {
		death(lastAttacker, false);
	}
	public void death(final L1Character lastAttacker, final boolean _isPinkName) {
		synchronized (this) {
			if (this.isDead()) {
				return;
			}
            this.setlslocx(0);
            this.setlslocy(0);
            this.setNowTarget(null);// 解除目前攻击目标设置
            this.setDead(true);
            this.setStatus(ActionCodes.ACTION_Die);
            
            if (isActived()) { 
				setActived(false);
	    		sendPackets(new S_ServerMessage(" 自動狩獵已停止。"));
	    		  if(get_fwgj()>0){
	    		        setlslocx(0);
	    		        setlslocy(0);
	    		       set_fwgj(0);
	    		        }

//	    		  killSkillEffectTimer(9997);
//	    		  killSkillEffectTimer(9996);
	    		/*  if (getQuest().get_step(8780) == 1) {
	     			killSkillEffectTimer(8132);
	    			 addWeightReduction(-ConfigGuaji.guajiWeight);
		             sendPackets(new S_OwnCharStatus(this));
	     			}*/
			}
        
		}
		GeneralThreadPool.get().execute(new Death(lastAttacker, _isPinkName));

	}

	public void setLookPlayerInstance(LookPlayerInstance lookPlayerInstance) {
		this.lookPlayerInstance = lookPlayerInstance;
	}

	public LookPlayerInstance getLookPlayerInstance() {
		return lookPlayerInstance;
	}

	/**
	 * 人物死亡的處理
	 * @author dexc
	 *
	 */
	private class Death implements Runnable {

		private L1Character _lastAttacker;
		private boolean pinkName = false;

		private Death(final L1Character cha) {
			this._lastAttacker = cha;
		}
		private Death(final L1Character cha, final boolean pinkName) {
			this._lastAttacker = cha;
			this.pinkName = pinkName;
		}

		@Override
		public void run() {
			final L1Character lastAttacker = this._lastAttacker;
			this._lastAttacker = null;
			L1PcInstance.this.setCurrentHp(0);
			L1PcInstance.this.setGresValid(false); // EXPG-RES無效

			while (L1PcInstance.this.isTeleport()) { // 傳送狀態中延遲
				try {
					Thread.sleep(300);
					
				} catch (final Exception e) {
				}
			}
			if (L1PcInstance.this.isInParty()) {// 隊伍中
				for (final L1PcInstance member : L1PcInstance.this.getParty().partyUsers().values()) {
					member.sendPackets(new S_PacketBoxParty(getParty(), L1PcInstance.this));
				}
			}
			// 加入死亡清單
			set_delete_time(300);

			// 娃娃刪除
			if (!getDolls().isEmpty()) {
				for (Object obj : getDolls().values().toArray()) {
					final L1DollInstance doll = (L1DollInstance) obj;
					doll.deleteDoll();
				}
			}

			L1PcInstance.this.stopHpRegeneration();
			L1PcInstance.this.stopMpRegeneration();

			final int targetobjid = L1PcInstance.this.getId();
			L1PcInstance.this.getMap().setPassable(L1PcInstance.this.getLocation(), true);
			
			// 死亡時具有變身狀態
			int tempchargfx = 0;
			if (L1PcInstance.this.hasSkillEffect(SHAPE_CHANGE)) {
				tempchargfx = L1PcInstance.this.getTempCharGfx();
				L1PcInstance.this.setTempCharGfxAtDead(tempchargfx);

			} else {
				L1PcInstance.this.setTempCharGfxAtDead(L1PcInstance.this.getClassId());
			}

			// 死亡時 現有技能消除
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(
					L1PcInstance.this, 
					CANCELLATION, 
					L1PcInstance.this.getId(),
					L1PcInstance.this.getX(), 
					L1PcInstance.this.getY(), 
					0, L1SkillUse.TYPE_LOGIN);

			// 系變身中死亡落暫定對應
			if ((tempchargfx == 5727) || 
					(tempchargfx == 5730) || 
					(tempchargfx == 5733) || 
					(tempchargfx == 5736)) {
				tempchargfx = 0;
			}
			
			if (tempchargfx == 7351) {
				tempchargfx = L1PcInstance.this.getClassId();
				L1PcInstance.this.setTempCharGfx(tempchargfx);
			}
			
			if (tempchargfx != 0) {
				//System.out.println("tempchargfx: " + tempchargfx);
				L1PcInstance.this.sendPacketsAll(new S_ChangeShape(L1PcInstance.this, tempchargfx));

			} else {
				// 系變身中攻擊死亡落入
				try {
					Thread.sleep(1000);
				} catch (final Exception e) {
				}
			}

			boolean isSafetyZone = false;// 是否為安全區中
			
			boolean isCombatZone = false;// 是否為戰鬥區中

			boolean isWar = false;// 是否參戰
			
			if (L1PcInstance.this.isSafetyZone()) {
				isSafetyZone = true;
			}
			if (L1PcInstance.this.isCombatZone()) {
				isCombatZone = true;
			}

			// 殺人次數的減少
			if (lastAttacker instanceof L1GuardInstance) {
				if (L1PcInstance.this.get_PKcount() > 0) {
					L1PcInstance.this.set_PKcount(L1PcInstance.this.get_PKcount() - 1);
				}
				L1PcInstance.this.setLastPk(null);
			}
			
			if (lastAttacker instanceof L1GuardianInstance) {
				if (L1PcInstance.this.getPkCountForElf() > 0) {
					L1PcInstance.this.setPkCountForElf(L1PcInstance.this.getPkCountForElf() - 1);
				}
				L1PcInstance.this.setLastPkForElf(null);
			}
			
			// 檢查攻擊者是否為PC(寵物 定義為主人)
			L1PcInstance fightPc = null;
			
			if (lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
				fightPc = (L1PcInstance) lastAttacker;
				
			}/* else if (lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
				final L1PetInstance npc = (L1PetInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}
				
			} */else if (lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				final L1SummonInstance npc = (L1SummonInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}
				
			} else if (lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				final L1IllusoryInstance npc = (L1IllusoryInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}

			} else if (lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件
				final L1EffectInstance npc = (L1EffectInstance) lastAttacker;
				if (npc.getMaster() != null) {
					fightPc = (L1PcInstance) npc.getMaster();
				}
			}

			L1PcInstance.this.sendPacketsAll(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			
            // 20171122 墓碑
            L1EffectInstance tomb = L1SpawnUtil.spawnEffect(86126, 300, L1PcInstance.this.getX(),
					L1PcInstance.this.getY(), L1PcInstance.this.getMapId(), L1PcInstance.this, 0);
			L1PcInstance.this.set_tomb(tomb);
			
			if (fightPc != null) {
				// 決鬥中
				if ((L1PcInstance.this.getFightId() == fightPc.getId()) &&
						(fightPc.getFightId() == L1PcInstance.this.getId())) {
					L1PcInstance.this.setFightId(0);
					L1PcInstance.this.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					return;
				}

				// 效果: 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品
				if (isEncounter()) {// 遭遇的守護
					if (fightPc.getLevel() > getLevel()) {
						if ((fightPc.getLevel() - getLevel()) >= 10) {
							return;
						}
					}
				}

				// 攻城戰爭進行狀態
				if (L1PcInstance.this.castleWarResult()) {
					isWar = true;
				}

				// 血盟戰爭進行狀態
				if (L1PcInstance.this.simWarResult(lastAttacker)) {
					isWar = true;
				}
				
				// 攻城戰進行狀態
				if (L1PcInstance.this.isInWarAreaAndWarTime(L1PcInstance.this, fightPc)) {
					isWar = true;
				}
				
				// 死亡公告
				if (L1PcInstance.this.getLevel() >= ConfigKill.KILLLEVEL) {
					if (!fightPc.isGm()) {
						boolean isShow = false;// 是否公告
						if (isWar) {// 戰爭中
							isShow = true;
							
						} else {// 非戰爭中
							// 非戰鬥區
							if (!isCombatZone) {
								isShow = true;
							}
						}
						if (isShow) {
							// 殺人公告
							World.get().broadcastPacketToAll(
									new S_KillMessage(fightPc.getName(), L1PcInstance.this.getName()));
							String x1 = ConfigKill.KILL_TEXT_LIST.get(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1);
							World.get().broadcastPacketToAll(new S_BlueMessage(0, String.format(x1, fightPc.getName(), L1PcInstance.this.getName())));
							fightPc.get_other().add_killCount(1);
							L1PcInstance.this.get_other().add_deathCount(1);
						}
					}
				}
			}

			// 安全區中
			if (isSafetyZone) {
				return;
			}
			// 戰鬥區中
			if (isCombatZone && lastAttacker instanceof L1PcInstance) {
 				return;
			}
			// 死亡逞罰
			if (!L1PcInstance.this.getMap().isEnabledDeathPenalty()) {
				return;
			}

			final boolean castle_area = L1CastleLocation.checkInAllWarArea(getX(), getY(), getMapId());
			if (castle_area) {// 戰爭旗中
				return;
			}

			// 正義質未滿
			if (L1PcInstance.this.getLawful() < 32767) {
				// 物品掉落判斷
				this.lostRate();
				
				// 技能掉落的判斷
				this.lostSkillRate();
			}
			
			
			// 經驗值掉落的判斷
			if (lastAttacker instanceof L1PcInstance) {
				this.expRate(true);
			} else {
				this.expRate(false);
			}
			
			// 參戰中
			if (isWar) {
				return;
			}
			
			if (fightPc != null) {
				if (fightPc.getClan() != null && getClan() != null) {
					if (WorldWar.get().isWar(fightPc.getClan().getClanName(), getClan().getClanName())) {
						return;
					}
				}
				if (fightPc.isSafetyZone()) {
					return;
				}
				if (fightPc.isCombatZone()) {
					return;
				}
				if ((L1PcInstance.this.getLawful() >= 0) && (L1PcInstance.this.isPinkName() == false)) {
					boolean isChangePkCount = false;
					//boolean isChangePkCountForElf = false;
					// 30000未滿場合PK增加
					if (fightPc.getLawful() < 30000) {
						fightPc.set_PKcount(fightPc.get_PKcount() + 1);
						isChangePkCount = true;
						if (fightPc.isElf() && L1PcInstance.this.isElf()) {
							fightPc.setPkCountForElf(fightPc.getPkCountForElf() + 1);
							//isChangePkCountForElf = true;
						}
					}
					fightPc.setLastPk();
					if (fightPc.isElf() && L1PcInstance.this.isElf()) {
						fightPc.setLastPkForElf();
					}

					// 處理
					// 公式發表各LVPK合變更
					// （PK側LV依存、高LV高）
					// 48-8k DK時點10k強
					// 60約20k強 6530k弱
					int lawful;

					if (fightPc.getLevel() < 50) {
						//lawful = -1 * (int) ((Math.pow(fightPc.getLevel(), 2) * 4));
						lawful = -1 * (((int) Math.pow(fightPc.getLevel(), 2)) << 2);
						
					} else {
						lawful = -1 * (int) ((Math.pow(fightPc.getLevel(), 3) * 0.08));
					}
					// (元-1000)計算後低場合
					// 元-1000值
					// （連續PK值變記憶）
					// 上式自信度低覺
					// 明！場合修正願
					if ((fightPc.getLawful() - 1000) < lawful) {
						lawful = fightPc.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					if (this.pinkName && lawful < 0) {
						lawful = 0;
					}
					fightPc.setLawful(lawful);
					fightPc.sendPacketsAll(new S_Lawful(fightPc));
					if (lastAttacker instanceof L1PetInstance) {
						if (lastAttacker.getLevel() < 50) {
							//lawful = -1 * (int) ((Math.pow(fightPc.getLevel(), 2) * 4));
							lawful = -1 * (((int) Math.pow(lastAttacker.getLevel(), 2)) << 2);

						} else {
							lawful = -1 * (int) ((Math.pow(lastAttacker.getLevel(), 3) * 0.08));
						}
						if ((lastAttacker.getLawful() - 1000) < lawful) {
							lawful = lastAttacker.getLawful() - 1000;
						}

						if (lawful <= -32768) {
							lawful = -32768;
						}
						lastAttacker.setLawful(lawful);
					}

					if (ConfigAlt.ALT_PUNISHMENT) {
						if (isChangePkCount && (fightPc.get_PKcount() >= 5) && (fightPc.get_PKcount() < 100)) {
							// PK回數%0。回數%1地獄行。
							fightPc.sendPackets(new S_BlueMessage(551, String.valueOf(fightPc.get_PKcount()), "100"));
							
						} else if (isChangePkCount && (fightPc.get_PKcount() >= 100)) {
							fightPc.beginHell(true);
						}
					}
					
				} else {
					setPinkName(false);
				}
			}

			/*if (PcDeleteList.get(L1PcInstance.this) == null) {
				PcDeleteList.put(L1PcInstance.this);// 5M
			}*/
		}
		
		/**
		 * <FONT COLOR="#0000ff">經驗值掉落判斷</FONT>
		 */
		private void expRate(boolean safe) {
			final L1ItemInstance item1 = getInventory().checkItemX(44060, 1);
			if (!safe && item1 != null) {
				getInventory().removeItem(item1, 1);// 刪除1個藥水
				sendPackets(new S_ServerMessage("\\fU你身上帶有" + item1.getName() + "，剛剛死掉沒有掉％！"));
				return;
			}
			deathPenalty(); // 經驗質逞罰

			setGresValid(true); // EXPG-RES有效

			if (getExpRes() == 0) {
				setExpRes(1);
			}
			
			onChangeExp();
		}
	
		/**
		 * <FONT COLOR="#0000ff">物品掉落判斷</FONT>
		 */
		private void lostRate() {
			final L1ItemInstance item2 = L1PcInstance.this.getInventory().checkItemX(44061, 1);
			if (item2 != null) {
				L1PcInstance.this.getInventory().removeItem(item2, 1);// 刪除1個
				sendPackets(new S_ServerMessage("\\fU你身上帶有" + item2.getName() + "，剛剛死掉沒有噴裝！"));
				return;
			}
			
			// 產生物品掉落機率
			// 正義質32000以上0%、每-1000增加0.4%
			// 正義質小於0 每-1000增加0.8%
			// 正義質-32000以下 最高51.2%掉落率
			int lostRate = ((int) ((L1PcInstance.this.getLawful() + 32768D) / 1000D - 65D)) << 2;

			if (lostRate < 0) {
				lostRate *= -1;
				if (L1PcInstance.this.getLawful() < 0) {
					//lostRate *= 2;
					lostRate = lostRate << 1;
				}
				final int rnd = _random.nextInt(1000) + 1;
//				if (rnd <= lostRate) {
				if (rnd <= 500) {
					int count = 0;
					int lawful = L1PcInstance.this.getLawful();
					if (lawful <= -32768) {// 小於-30000掉落1~5件
						count = _random.nextInt(5) + 1;

					} else if (lawful > -32768 && lawful <= -30000) {// 小於-30000掉落1~3件
						count = _random.nextInt(4) + 1;

					} else if (lawful > -30000 && lawful <= -20000) {// 小於-20000掉落1~3件
						count = _random.nextInt(3) + 1;

					} else if (lawful > -20000 && lawful <= -10000) {// 小於-10000掉落1~2件
						count = _random.nextInt(2) + 1;

					} else if (lawful > -10000 && lawful <= -0) {// 小於0掉落1件
						count = _random.nextInt(1) + 1;
					} else if (lawful < 32767) {
						count = (_random.nextInt(1000) + 1) < 20 ? 1 : 0;
					}
					
					if (count > 0) {
						L1PcInstance.this.caoPenaltyResult(count);
					}
				}
			}
		}
		
		/**
		 * <FONT COLOR="#0000ff">死亡技能遺失判斷</FONT>
		 */
		private void lostSkillRate() {
			// 人物擁有技能數量
			int skillCount = _skillList.size();
			
			// 技能數量大於0
			if (skillCount > 0) {
				// 預計掉落技能數量
				int count = 0;
				// 人物正義質
				int lawful = getLawful();
				
				// 引用隨機質 0 ~ 199
				int random = _random.nextInt(200);

				if (lawful <= -32768) {
					count = _random.nextInt(4) + 1;// 隨機質 小於 技能數量
					
				} else if (lawful > -32768 && lawful <= -30000) {
					if (random <= (skillCount + 1)) {
						count = _random.nextInt(3) + 1;// 隨機質 小於 技能數量
					}
					
				} else if (lawful > -30000 && lawful <= -20000) {
					if (random <= ((skillCount >> 1) + 1)) {// 隨機質 小於 (技能數量 / 2)
						count = _random.nextInt(2) + 1;
					}
					
				} else if (lawful > -20000 && lawful <= -10000) {
					if (random <= ((skillCount >> 2) + 1)) {// 隨機質 小於 (技能數量 / 4)
						count = 1;
					}
				}
				
				if (count > 0) {
					delSkill(count);
				}
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">死亡掉落物品</FONT>
	 * @param count 掉落數量
	 */
	private void caoPenaltyResult(final int count) {
		for (int i = 0; i < count; i++) {
			final L1ItemInstance item = getInventory().caoPenalty();
			if (item != null) {
				item.set_showId(get_showId());
				
				final int x = getX();
				final int y = getY();
				final short m = getMapId(); 
				getInventory().tradeItem(
						item,
						item.isStackable() ? item.getCount() : 1,// 物件不可堆疊 數量:1 可堆疊 數量:全部
						World.get().getInventory(x, y, m)
						);
				// 638 您損失了 %0。
				sendPackets(new S_ServerMessage(638, item.getLogName()));
				WriteLogTxt.OtherLog("死亡處罰物品掉落", "玩家" + ":【 " + L1PcInstance.this.getName() + " 】 " + "死亡遺失道具" + "【 "
						+ item.getName() + " 】" + "(時間" + new Timestamp(System.currentTimeMillis()) + ")");
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">死亡技能遺失</FONT>
	 * @param count 掉落數量
	 */
	private void delSkill(final int count) {
		for (int i = 0; i < count; i++) {
			// 隨機取得 INDEX 位置點
			int index = _random.nextInt(this._skillList.size());
			// 取回隨機位置點技能編號
			Integer skillid = _skillList.get(index);
			
			if (this._skillList.remove(skillid)) {
				final L1Skills skill = SkillsTable.get().getTemplate(skillid);
				if (skill != null) {
					WriteLogTxt.OtherLog("死亡處罰技能掉落", "玩家" + ":【 " + L1PcInstance.this.getName() + " 】 " + "死亡遺失技能" + "【 "
							+ skill.getName() + " 】" + "(時間" + new Timestamp(System.currentTimeMillis()) + ")");
				}
				this.sendPackets(new S_DelSkill(this, skillid));
				CharSkillReading.get().spellLost(this.getId(), skillid);
			}
		}
	}

	/**
	 * <FONT COLOR="#0000ff">復活移出死亡清單</FONT>
	 */
	public void stopPcDeleteTimer() {
		this.setDead(false);
		// 加入死亡清單
		set_delete_time(0);
	}
	
	/**
	 * <FONT COLOR="#0000ff">是否在參加攻城戰中</FONT>
	 * @return true:是 false:不是
	 */
	public boolean castleWarResult() {
		if ((this.getClanid() != 0) && this.isCrown()) { // 具有血盟的王族
			final L1Clan clan = WorldClan.get().getClan(this.getClanname());
			if (clan.getCastleId() == 0) {
				// 取回全部戰爭清單
				for (final L1War war : WorldWar.get().getWarList()) {
					final int warType = war.getWarType();
					final boolean isInWar = war.checkClanInWar(this.getClanname());
					final boolean isAttackClan = war.checkAttackClan(this.getClanname());
					if ((this.getId() == clan.getLeaderId()) && // 攻城戰中 攻擊方盟主死亡 退出戰爭
							(warType == 1) && isInWar && isAttackClan) {
						final String enemyClanName = war.getEnemyClanName(this.getClanname());
						if (enemyClanName != null) {
							war.ceaseWar(this.getClanname(), enemyClanName); // 結束
						}
						break;
					}
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) { // 戰爭範圍旗幟內城堡ID
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}
		return isNowWar;
	}

	/**
	 * <FONT COLOR="#0000ff">是否在參加血盟戰爭中</FONT>
	 * @param lastAttacker
	 * @return true:是 false:不是
	 */
	public boolean simWarResult(final L1Character lastAttacker) {
		if (this.getClanid() == 0) { // 所屬
			return false;
		}
		
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;
		
		// 判斷主要攻擊者
		if (lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
			attacker = (L1PcInstance) lastAttacker;
			
		} else if (lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
			
		} else if (lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
			
		} else if (lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
			attacker = (L1PcInstance) ((L1IllusoryInstance) lastAttacker).getMaster();

		} else if (lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件(火牢)
			attacker = (L1PcInstance) ((L1EffectInstance) lastAttacker).getMaster();
			
		} else {
			return false;
		}

		// 取回全部戰爭清單
		for (final L1War war : WorldWar.get().getWarList()) {
			final L1Clan clan = WorldClan.get().getClan(this.getClanname());

			final int warType = war.getWarType();
			final boolean isInWar = war.checkClanInWar(this.getClanname());
			if ((attacker != null) && (attacker.getClanid() != 0)) { // lastAttackerPC、、所屬中
				sameWar = war.checkClanInSameWar(this.getClanname(), attacker.getClanname());
			}

			if ((this.getId() == clan.getLeaderId()) && // 血盟主模擬戰中
					(warType == 2) && (isInWar == true)) {
				enemyClanName = war.getEnemyClanName(this.getClanname());
				if (enemyClanName != null) {
					war.ceaseWar(this.getClanname(), enemyClanName); // 結束
				}
			}

			if ((warType == 2) && sameWar) {// 模擬戰同戰爭參加中場合、
				return true;
			}
		}
		return false;
	}

	/**
	 * 經驗質恢復
	 */
	public void resExp() {
		final int oldLevel = this.getLevel();
		final long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		long exp = 0;
		switch (oldLevel) {
		case 1: case 2: case 3: case 4: case 5:
		case 6: case 7: case 8: case 9: case 10:
		case 11: case 12: case 13: case 14: case 15:
		case 16: case 17: case 18: case 19: case 20:
		case 21: case 22: case 23: case 24: case 25:
		case 26: case 27: case 28: case 29: case 30:
		case 31: case 32: case 33: case 34: case 35:
		case 36: case 37: case 38: case 39: case 40:
		case 41: case 42: case 43: case 44: 
			exp = (long) (needExp * 0.05);
			break;
			
		case 45:
			exp = (long) (needExp * 0.045);
			break;
			
		case 46:
			exp = (long) (needExp * 0.04);
			break;
			
		case 47:
			exp = (long) (needExp * 0.035);
			break;
			
		case 48:
			exp = (long) (needExp * 0.03);
			break;
			
		case 49:
			exp = (long) (needExp * 0.025);
			break;
			
		default:
			exp = (long) (needExp * 0.025);
			break;
		}

		if (exp == 0) {
			return;
		}
		this.addExp(exp);
	}

	/**
	 * 經驗質逞罰
	 * @return
	 */
	private long deathPenalty() {
		final int oldLevel = this.getLevel();
		final long needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		long exp = 0;
		switch (oldLevel) {
		case 1: case 2: case 3: case 4: case 5:
		case 6: case 7: case 8: case 9: case 10:
			exp = 0;
			break;
			
		case 11: case 12: case 13: case 14: case 15:
		case 16: case 17: case 18: case 19: case 20:
		case 21: case 22: case 23: case 24: case 25:
		case 26: case 27: case 28: case 29: case 30:
		case 31: case 32: case 33: case 34: case 35:
		case 36: case 37: case 38: case 39: case 40:
		case 41: case 42: case 43: case 44: 
			exp = (long) (needExp * 0.1);
			break;
			
		case 45:
			exp = (long) (needExp * 0.09);
			break;
			
		case 46:
			exp = (long) (needExp * 0.08);
			break;
			
		case 47:
			exp = (long) (needExp * 0.07);
			break;
			
		case 48:
			exp = (long) (needExp * 0.06);
			break;
			
		case 49:
			exp = (long) (needExp * 0.05);
			break;
			
		default:
			exp = (long) (needExp * 0.05);
			break;
		}

		if (exp == 0) {
			return 0;
		}
		this.addExp(-exp);
		return exp;
	}

	private int _originalEr = 0; // ● DEX ER補正

	public int getOriginalEr() {

		return this._originalEr;
	}

	public int getEr() {
		if (this.hasSkillEffect(STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (this.isKnight()) {
			er = this.getLevel() >> 2;// /4 // 
		
		} else if (this.isCrown() || this.isElf()) {
			er = this.getLevel() >> 3;// / 8; // 君主・
					
		} else if (this.isDarkelf()) {
			er = this.getLevel() / 6; // 
			
		} else if (this.isWizard()) {
			er = this.getLevel() / 10; // 
			
		}

		er += (this.getDex() - 8) >> 1;/// 2;
					
		er += this.getOriginalEr();

		if (this.hasSkillEffect(DRESS_EVASION)) {// 閃避提升
			er += 12;
		}
		
		if (this.hasSkillEffect(SOLID_CARRIAGE)) {// 堅固防護
			er += 15;
		}
		
		if (this.hasSkillEffect(ADLV80_1)) {// 卡瑞的祝福(地龍副本)
			er += 30;
		}
		
		if (this.hasSkillEffect(ADLV80_2)) {// 莎爾的祝福(水龍副本)
			er += 15;
		}
		return er;
	}

	/**
	 * 使用的武器
	 * @return
	 */
	public L1ItemInstance getWeapon() {
		return this._weapon;
	}

	/**
	 * 使用的武器
	 * @param weapon
	 */
	public void setWeapon(final L1ItemInstance weapon) {
		this._weapon = weapon;
	}

	/**
	 * 傳回任務狀態類
	 * @return
	 */
	public L1PcQuest getQuest() {
		return this._quest;
	}

	/**
	 * 傳回選單命令執行類
	 * @return
	 */
	public L1ActionPc getAction() {
		return this._action;
	}

	/**
	 * 傳回寵物選單命令執行類
	 * @return
	 */
	public L1ActionPet getActionPet() {
		return this._actionPet;
	}

	/**
	 * 傳回召喚獸選單命令執行類
	 * @return
	 */
	public L1ActionSummon getActionSummon() {
		return this._actionSummon;
	}

	/**
	 * 王族
	 * @return
	 */
	public boolean isCrown() {
		return ((this.getClassId() == CLASSID_PRINCE) || 
				(this.getClassId() == CLASSID_PRINCESS));
	}

	/**
	 * 騎士
	 * @return
	 */
	public boolean isKnight() {
		return ((this.getClassId() == CLASSID_KNIGHT_MALE) || 
				(this.getClassId() == CLASSID_KNIGHT_FEMALE));
	}

	/**
	 * 精靈
	 * @return
	 */
	public boolean isElf() {
		return ((this.getClassId() == CLASSID_ELF_MALE) || 
				(this.getClassId() == CLASSID_ELF_FEMALE));
	}

	/**
	 * 法師
	 * @return
	 */
	public boolean isWizard() {
		return ((this.getClassId() == CLASSID_WIZARD_MALE) || 
				(this.getClassId() == CLASSID_WIZARD_FEMALE));
	}

	/**
	 * 黑暗精靈
	 * @return
	 */
	public boolean isDarkelf() {
		return ((this.getClassId() == CLASSID_DARK_ELF_MALE) || 
				(this.getClassId() == CLASSID_DARK_ELF_FEMALE));
	}

	private ClientExecutor _netConnection = null;
	private int _classId;
	private int _type;
	private long _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm, removePcShop = false;
	private boolean _monitor;
	private boolean _gmInvis;
	private short _accessLevel;
	private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1DwarfInventory _dwarf;
	private final L1DwarfForElfInventory _dwarfForElf;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private L1ChatParty _chatParty;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	private boolean _isPinkName = false;
	private L1PcQuest _quest;
	private L1ActionPc _action;
	private L1ActionPet _actionPet;
	private L1ActionSummon _actionSummon;

	private L1EquipmentSlot _equipSlot;
	
	private String _accountName; // ● 

	public String getAccountName() {
		return this._accountName;
	}

	public void setAccountName(final String s) {
		this._accountName = s;
	}

	private int _baseMaxHp = 0; // ● ＭＡＸＨＰ（1～65535）

	public void resetBaseMaxHp() {
		int set = CalcInitHpMp.calcInitHp(this);
		int i = this.getLevel();
		this._baseMaxHp = set;
		this.setMaxHp(set);
		while (i > 0) {
			i--;
			set += CalcStat.calcStatHp(this.getType(), this.getBaseMaxHp(), this.getBaseCon(), this.getOriginalHpup());
		}
		this._baseMaxHp = set;
		this.setMaxHp(set);
	}

	/**
	 * 基礎HP
	 * @return
	 */
	public int getBaseMaxHp() {
		return this._baseMaxHp;
	}

	/**
	 * 基礎HP
	 * @param i
	 */
	public void addBaseMaxHp(int i) {
		i += this._baseMaxHp;
		if (i >= 65535) {
			i = 65535;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addMaxHp(i - this._baseMaxHp);
		this._baseMaxHp = i;
	}

	private short _baseMaxMp = 0; // ● ＭＡＸＭＰ（0～32767）
	public void resetBaseMaxMp() {
		short set = (short) CalcInitHpMp.calcInitMp(this);
		int i = this.getLevel();
		this._baseMaxMp = set;
		this.setMaxMp(set);
		while (i > 0) {
			i--;
			set += CalcStat.calcStatMp(this.getType(), this.getBaseMaxMp(), this.getBaseWis(), this.getOriginalMpup());
		}
		this._baseMaxMp = set;
		this.setMaxMp(set);
	}
	/**
	 * 基礎MP
	 * @return
	 */
	public short getBaseMaxMp() {
		return this._baseMaxMp;
	}

	/**
	 * 基礎MP
	 * @param i
	 */
	public void addBaseMaxMp(short i) {
		i += this._baseMaxMp;
		if (i >= 32767) {
			i = 32767;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addMaxMp(i - this._baseMaxMp);
		this._baseMaxMp = i;
	}

	private int _baseAc = 0; // ● ＡＣ（-128～127）

	public int getBaseAc() {
		return this._baseAc;
	}

	private int _originalAc = 0; // ● DEX ＡＣ補正

	public int getOriginalAc() {
		return this._originalAc;
	}

	private int _baseStr = 0; // ● ＳＴＲ（1～127）

	/**
	 * 原始力量(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseStr() {
		return this._baseStr;
	}

	/**
	 * 原始力量(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseStr(int i) {
		i += this._baseStr;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addStr((i - this._baseStr));
		this._baseStr = i;
	}

	private int _baseCon = 0; // ● ＣＯＮ（1～127）

	/**
	 * 原始體質(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseCon() {
		return this._baseCon;
	}

	/**
	 * 原始體質(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseCon(int i) {
		i += this._baseCon;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addCon((i - this._baseCon));
		this._baseCon = i;
	}

	private int _baseDex = 0; // ● ＤＥＸ（1～127）

	/**
	 * 原始敏捷(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseDex() {
		return this._baseDex;
	}

	/**
	 * 原始敏捷(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseDex(int i) {
		i += this._baseDex;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addDex((i - this._baseDex));
		this._baseDex = i;
	}

	private int _baseCha = 0; // ● ＣＨＡ（1～127）

	/**
	 * 原始魅力(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseCha() {
		return this._baseCha;
	}

	/**
	 * 原始魅力(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseCha(int i) {
		i += this._baseCha;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addCha((i - this._baseCha));
		this._baseCha = i;
	}

	private int _baseInt = 0; // ● ＩＮＴ（1～127）

	/**
	 * 原始智力(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseInt() {
		return this._baseInt;
	}

	/**
	 * 原始智力(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseInt(int i) {
		i += this._baseInt;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addInt((i - this._baseInt));
		this._baseInt = i;
	}

	private int _baseWis = 0; // ● ＷＩＳ（1～127）

	/**
	 * 原始精神(內含素質提升/萬能藥)
	 * @return
	 */
	public int getBaseWis() {
		return this._baseWis;
	}

	/**
	 * 原始精神(內含素質提升/萬能藥)
	 * @param i
	 */
	public void addBaseWis(int i) {
		i += this._baseWis;
		if (i >= 254) {
			i = 254;
			
		} else if (i < 1) {
			i = 1;
		}
		this.addWis((i - this._baseWis));
		this._baseWis = i;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	
	private int _originalStr = 0; // ●  STR

	/**
	 * 原始力量(人物出生)
	 * @return
	 */
	public int getOriginalStr() {
		return this._originalStr;
	}

	/**
	 * 原始力量(人物出生)
	 * @param i
	 */
	public void setOriginalStr(final int i) {
		this._originalStr = i;
	}

	private int _originalCon = 0; // ●  CON

	/**
	 * 原始體質(人物出生)
	 * @return
	 */
	public int getOriginalCon() {
		return this._originalCon;
	}

	/**
	 * 原始體質(人物出生)
	 * @param i
	 */
	public void setOriginalCon(final int i) {
		this._originalCon = i;
	}

	private int _originalDex = 0; // ●  DEX

	/**
	 * 原始敏捷(人物出生)
	 * @return
	 */
	public int getOriginalDex() {
		return this._originalDex;
	}

	/**
	 * 原始敏捷(人物出生)
	 * @param i
	 */
	public void setOriginalDex(final int i) {
		this._originalDex = i;
	}

	private int _originalCha = 0; // ●  CHA

	/**
	 * 原始魅力(人物出生)
	 * @return
	 */
	public int getOriginalCha() {
		return this._originalCha;
	}

	/**
	 * 原始魅力(人物出生)
	 * @param i
	 */
	public void setOriginalCha(final int i) {
		this._originalCha = i;
	}

	private int _originalInt = 0; // ●  INT

	/**
	 * 原始智力(人物出生)
	 * @return
	 */
	public int getOriginalInt() {
		return this._originalInt;
	}

	/**
	 * 原始智力(人物出生)
	 * @param i
	 */
	public void setOriginalInt(final int i) {
		this._originalInt = i;
	}

	private int _originalWis = 0; // ●  WIS

	/**
	 * 原始精神(人物出生)
	 * @return
	 */
	public int getOriginalWis() {
		return this._originalWis;
	}

	/**
	 * 原始精神(人物出生)
	 * @param i
	 */
	public void setOriginalWis(final int i) {
		this._originalWis = i;
	}

	private int _originalDmgup = 0; // ● STR 補正

	public int getOriginalDmgup() {
		return this._originalDmgup;
	}

	private int _originalBowDmgup = 0; // ● DEX 弓補正

	public int getOriginalBowDmgup() {
		return this._originalBowDmgup;
	}

	private int _originalHitup = 0; // ● STR 命中補正

	public int getOriginalHitup() {
		return this._originalHitup;
	}

	private int _originalBowHitup = 0; // ● DEX 命中補正

	public int getOriginalBowHitup() {
		return this._originalHitup + this._originalBowHitup;
	}

	private int _originalMr = 0; // ● WIS 魔法防禦

	public int getOriginalMr() {
		return this._originalMr;
	}

	private int _originalMagicHit = 0; // ● INT 魔法命中

	/**
	 * 智力(依職業)附加魔法命中
	 * @return
	 */
	public int getOriginalMagicHit() {
		return this._originalMagicHit;
	}

	private int _originalMagicCritical = 0; // ● INT 魔法

	public int getOriginalMagicCritical() {
		return this._originalMagicCritical;
	}

	private int _originalMagicConsumeReduction = 0; // ● INT 消費MP輕減

	public int getOriginalMagicConsumeReduction() {
		return this._originalMagicConsumeReduction;
	}

	private int _originalMagicDamage = 0; // ● INT 魔法

	/**
	 * 魔攻
	 * @return
	 */
	public int getOriginalMagicDamage() {
		return this._originalMagicDamage;
	}

	private int _originalHpup = 0; // ● CON HP上升值補正

	/**
	 * 體質 HP上升值補正
	 * @return
	 */
	public int getOriginalHpup() {
		return this._originalHpup;
	}

	private int _originalMpup = 0; // ● WIS MP上升值補正

	/**
	 * 精神 MP上升值補正
	 * @return
	 */
	public int getOriginalMpup() {
		return this._originalMpup;
	}

	private int _baseDmgup = 0; // ● 補正（-128～127）

	public int getBaseDmgup() {
		return this._baseDmgup;
	}

	private int _baseBowDmgup = 0; // ● 弓補正（-128～127）

	public int getBaseBowDmgup() {
		return this._baseBowDmgup;
	}

	private int _baseHitup = 0; // ● 命中補正（-128～127）

	/**
	 * 命中補正
	 * @return
	 */
	public int getBaseHitup() {
		return this._baseHitup;
	}

	private int _baseBowHitup = 0; // ● 弓命中補正（-128～127）

	/**
	 * 弓命中補正
	 * @return
	 */
	public int getBaseBowHitup() {
		return this._baseBowHitup;
	}

	private int _baseMr = 0; // ● 魔法防禦（0～）

	/**
	 * 魔法防禦
	 * @return
	 */
	public int getBaseMr() {
		return this._baseMr;
	}

	private int _advenHp; // 暫時增加的HP

	/**
	 * 暫時增加的HP
	 * @return
	 */
	public int getAdvenHp() {
		return this._advenHp;
	}

	/**
	 * 暫時增加的HP
	 * @param i
	 */
	public void setAdvenHp(final int i) {
		this._advenHp = i;
	}

	private int _advenMp; // 暫時增加的MP

	/**
	 * 暫時增加的MP
	 * @return
	 */
	public int getAdvenMp() {
		return this._advenMp;
	}

	/**
	 * 暫時增加的MP
	 * @param i
	 */
	public void setAdvenMp(final int i) {
		this._advenMp = i;
	}

	private int _highLevel; // ● 過去最高

	public int getHighLevel() {
		return this._highLevel;
	}

	public void setHighLevel(final int i) {
		this._highLevel = i;
	}

	private int _bonusStats; // 升級點數使用次數

	/**
	 * 升級點數使用次數
	 * @return
	 */
	public int getBonusStats() {
		return this._bonusStats;
	}

	/**
	 * 設置升級點數使用次數
	 * @param i
	 */
	public void setBonusStats(final int i) {
		this._bonusStats = i;
	}

	private int _elixirStats; // 萬能藥使用次數

	/**
	 * 萬能藥使用次數
	 * @return
	 */
	public int getElixirStats() {
		return this._elixirStats;
	}

	/**
	 * 設置萬能藥使用次數
	 * @param i
	 */
	public void setElixirStats(final int i) {
		this._elixirStats = i;
	}

	private int _elfAttr; // ● エルフの属性

	/**
	 * 精靈屬性
	 * @return
	 */
	public int getElfAttr() {
		return this._elfAttr;
	}

	public void setElfAttr(final int i) {
		this._elfAttr = i;
	}

	private int _expRes; // ● EXP復舊

	public int getExpRes() {
		return this._expRes;
	}

	public void setExpRes(final int i) {
		this._expRes = i;
	}

	private int _partnerId; // ● 結婚相手

	public int getPartnerId() {
		return this._partnerId;
	}

	public void setPartnerId(final int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // 人物連線狀態

	/**
	 * 人物連線狀態
	 * @return
	 */
	public int getOnlineStatus() {
		return _onlineStatus;
	}

	/**
	 * 設置人物連線狀態
	 * @param i
	 */
	public void setOnlineStatus(final int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ● 

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(final int i) {
		_homeTownId = i;
	}

	private int _contribution; // 貢獻度

	/**
	 * 貢獻度
	 * @return
	 */
	public int getContribution() {
		return this._contribution;
	}

	/**
	 * 貢獻度
	 * @param i
	 */
	public void setContribution(final int i) {
		this._contribution = i;
	}

	private int _hellTime;// 地獄滯留時間

	/**
	 * 地獄滯留時間
	 * @return
	 */
	public int getHellTime() {
		return this._hellTime;
	}

	/**
	 * 地獄滯留時間
	 * @param i
	 */
	public void setHellTime(final int i) {
		this._hellTime = i;
	}

	private boolean _banned; // ● 凍結

	public boolean isBanned() {
		return this._banned;
	}

	public void setBanned(final boolean flag) {
		this._banned = flag;
	}

	private int _food; // ● 滿腹度

	/**
	 * 傳回滿腹度
	 * @return
	 */
	public int get_food() {
		return _food;
	}

	/**
	 * 設置滿腹度
	 * @param i
	 */
	public void set_food(int i) {
		if (i > 225) {
			i = 225;
		}
		_food = i;
	}

	public L1EquipmentSlot getEquipSlot() {
		return this._equipSlot;
	}

	/**
	 * 加載指定PC資料
	 * @param charName PC名稱
	 * @return
	 */
	public static L1PcInstance load(final String charName, ClientExecutor _client) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.get().loadCharacter(charName, _client);
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 人物資料存檔
	 *
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			return;
		}
		
		if (isInCharReset()) {
			return;
		}
		
		// 其它事件紀錄
		if (_other != null) {
			CharOtherReading.get().storeOther(getId(), _other);
		}

		CharacterTable.get().storeCharacter(this);
	}

	/**
	 * 背包資料存檔
	 */
	public void saveInventory() {
		for (final L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public double getMaxWeight() {
		final int str = getStr();
		final int con = getCon();
		double maxWeight = (150 * (Math.floor(0.6 * str + 0.4 * con + 1))) * get_weightUP();

		double weightReductionByArmor = getWeightReduction(); // 減重設置
		weightReductionByArmor /= 100;

		int weightReductionByMagic = 0;
		if (hasSkillEffect(DECREASE_WEIGHT)) { // 
			weightReductionByMagic = 180;
		}

		double originalWeightReduction = 0; // 重量輕減
		originalWeightReduction += 0.04 * (getOriginalStrWeightReduction() + getOriginalConWeightReduction());

		final double weightReduction = 1 + weightReductionByArmor + originalWeightReduction;

		maxWeight *= weightReduction;

		maxWeight += weightReductionByMagic;

		maxWeight *= ConfigRate.RATE_WEIGHT_LIMIT; // 服務器提高設置

		return maxWeight;
	}

	/**
	 * 神聖疾走效果
	 * 行走加速效果
	 * 風之疾走效果
	 * @return
	 */
	public boolean isFastMovable() {
		return (this.hasSkillEffect(HOLY_WALK)
				|| this.hasSkillEffect(MOVING_ACCELERATION)
				|| this.hasSkillEffect(WIND_WALK));
	}

	/**
	 * 勇敢藥水效果
	 * @return
	 */
	public boolean isBrave() {
		return this.hasSkillEffect(STATUS_BRAVE);
	}

	/**
	 * 精靈餅乾效果
	 * @return
	 */
	public boolean isElfBrave() {
		return this.hasSkillEffect(STATUS_ELFBRAVE);
	}
	
	/**
	 * 巧克力蛋糕效果
	 * @return
	 */
	public boolean isBraveX() {
		return this.hasSkillEffect(STATUS_BRAVE3);
	}

	/**
	 * 加速效果
	 * @return
	 */
	public boolean isHaste() {
		return (this.hasSkillEffect(STATUS_HASTE) || 
				this.hasSkillEffect(HASTE) ||
				this.hasSkillEffect(GREATER_HASTE) || 
				(this.getMoveSpeed() == 1));
	}

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (this.invisDelayCounter > 0);
	}

	private Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(final int counter) {
		synchronized (this._invisTimerMonitor) {
			this.invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	/**
	 * 啟用隱身時間軸設置
	 */
	public void beginInvisTimer() {
		this.addInvisDelayCounter(1);
		PcOtherThreadPool.get().pcSchedule(new L1PcInvisDelay(this.getId()), DELAY_INVIS);
	}

	public synchronized void addExp(final long exp) {
		final long newexp = _exp + exp;
		_exp = newexp;
	}

	/**
	 * 增加貢獻度
	 * @param contribution
	 */
	public synchronized void addContribution(final int contribution) {
		_contribution += contribution;
	}

	/**
	 * 等級提升的判斷
	 * @param gap
	 */
	private void levelUp(final int gap) {
		resetLevel();
		for (int i = 0; i < gap; i++) {
			final short randomHp = 
					CalcStat.calcStatHp(getType(), getBaseMaxHp(), getBaseCon(), getOriginalHpup());
			final short randomMp = 
					CalcStat.calcStatMp(getType(), getBaseMaxMp(), getBaseWis(), getOriginalMpup());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
		}

		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (getLevel() > getHighLevel()) {
			setHighLevel(getLevel());
		}

		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());

		try {
			// 人物資料存檔
			save();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			// 更新人物資訊
			sendPackets(new S_OwnCharStatus(this));
			if (lvReward.START) {
				Reward.getItem(this);// 升及獎勵
			}
			if (lvreward_Trial.START) {
				Reward1.getItem(this);// 升及獎勵(任務)
			}
			// 自动学习技能
			if (ConfigOther.AutoAddSkill) {
				if (AutoAddSkillTable.getSetList() != null) {
					AutoAddSkillTable.forAutoAddSkill(this);
				}
			}
			// 地圖等級限制判斷
			MapLevelTable.get().get_level(getMapId(), this);
			showWindows();
		}
		if ((getLevel() == 50 || getLevel() == 55 || getLevel() == 60 || getLevel() == 65 || getLevel() == 70 || getLevel() == 75 || getLevel() == 80) && !isGm()) {
			World.get().broadcastPacketToAll(new S_ServerMessage("\\fY玩家【" + getName() + "】經過不懈的努力終於達到了 " + getLevel() + " 級！！"));
			sendPacketsAll(new S_SkillSound(getId(), 2047));
		}
	}

	/**
	 * 判斷是否展示視窗<BR>
	 * 能力質/任務
	 */
	public void showWindows() {
		
		if (power()) {
			this.sendPackets(new S_Bonusstats(this.getId()));
			
		}
			
			
//		// 任務/副本系統啟動
//		if (QuestSet.ISQUEST) {
//			// 判斷是否出現任務提示視窗
//			int quest = QuestTable.get().levelQuest(this, this.getLevel());
//			if (quest > 0) {
//				// 展示任務室窗
//				isWindows();
//				
//			} else {
//				// 判斷是否出現能力選取視窗
//				if (power()) {
//					this.sendPackets(new S_Bonusstats(this.getId()));
//				}
//			}
//			
//		} else {
//			// 判斷是否出現能力選取視窗
//			if (power()) {
//				this.sendPackets(new S_Bonusstats(this.getId()));
//			}
//		}
	}

	/**
	 * 展示任務室窗
	 */
	public void isWindows() {
		// 判斷是否出現能力選取視窗
		if (power()) {// 是
			this.sendPackets(new S_NPCTalkReturn(this.getId(), "y_qs_10"));
			
		} else {// 不是
			this.sendPackets(new S_NPCTalkReturn(this.getId(), "y_qs_00"));
		}
	}

	/**
	 * 判斷是否出現能力選取視窗
	 * @return
	 */
	public boolean power() {
		if (this.getLevel() >= 51) {
			if (this.getLevel() - 50 > this.getBonusStats()) {
				int power = getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt() + getBaseWis() + getBaseCha();
				if (power < ConfigAlt.POWER * 6) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 等級下降
	 * @param gap
	 */
	private void levelDown(final int gap) {
		this.resetLevel();

		for (int i = 0; i > gap; i--) {
			// 時值為、base值0設定
			final short randomHp = CalcStat.calcStatHp(this.getType(), 0, this.getBaseCon(), this.getOriginalHpup());
			final short randomMp = CalcStat.calcStatMp(this.getType(), 0, this.getBaseWis(), this.getOriginalMpup());
			this.addBaseMaxHp((short) -randomHp);
			this.addBaseMaxMp((short) -randomMp);
		}
		
		if (this.getLevel() == 1) {
			final int initHp = CalcInitHpMp.calcInitHp(this);
			final int initMp = CalcInitHpMp.calcInitMp(this);
			this.addBaseMaxHp((short) -this.getBaseMaxHp());
			this.addBaseMaxHp((short) initHp);
			this.setCurrentHp((short) initHp);
			this.addBaseMaxMp((short) -this.getBaseMaxMp());
			this.addBaseMaxMp((short) initMp);
			this.setCurrentMp((short) initMp);
		}
		
		this.resetBaseHitup();
		this.resetBaseDmgup();
		this.resetBaseAc();
		this.resetBaseMr();

		try {
			// 存入資料
			this.save();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			// 更新人物資訊
			sendPackets(new S_OwnCharStatus(this));

			// 地圖等級限制判斷
			MapLevelTable.get().get_level(getMapId(), this);
		}
	}

	private boolean _ghost = false; // 鬼魂狀態
	
	/**获取经验增加--不永久储存**/
	private int _OtherExpByItem = 0;
	/**获取经验增加--不永久储存**/
	public int getOtherExpByItem() {
		return _OtherExpByItem;
	}
	/**获取经验增加--不永久储存**/
	public void setOtherExpByItem(int i) {
		_OtherExpByItem = i;
	}

	/**
	 * 鬼魂狀態
	 * @return
	 */
	public boolean isGhost() {
		return this._ghost;
	}
	
	/**
	 * 設置鬼魂狀態
	 * @param flag
	 */
	private void setGhost(final boolean flag) {
		this._ghost = flag;
	}

	private int _ghostTime = -1; // 鬼魂狀態時間

	/**
	 * 鬼魂狀態時間
	 * @return
	 */
	public int get_ghostTime() {
		return this._ghostTime;
	}
	
	/**
	 * 設置鬼魂狀態時間
	 * @param ghostTime
	 */
	public void set_ghostTime(final int ghostTime) {
		this._ghostTime = ghostTime;
	}

	private boolean _ghostCanTalk = true; // 鬼魂狀態NPC對話允許

	/**
	 * 鬼魂狀態NPC對話允許
	 * @return
	 */
	public boolean isGhostCanTalk() {
		return this._ghostCanTalk;
	}

	/**
	 * 設置鬼魂狀態NPC對話允許
	 * @param flag
	 */
	private void setGhostCanTalk(final boolean flag) {
		this._ghostCanTalk = flag;
	}

	private boolean _isReserveGhost = false; // 準備鬼魂狀態解除

	/**
	 * 準備鬼魂狀態解除
	 * @return
	 */
	public boolean isReserveGhost() {
		return this._isReserveGhost;
	}

	/**
	 * 準備鬼魂狀態解除
	 * @param flag
	 */
	private void setReserveGhost(final boolean flag) {
		this._isReserveGhost = flag;
	}

	/**
	 * 鬼魂模式傳送
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param canTalk
	 */
	public void beginGhost(final int locx, final int locy, final short mapid, final boolean canTalk) {
		this.beginGhost(locx, locy, mapid, canTalk, 0);
	}

	/**
	 * 鬼魂模式傳送
	 * @param locx
	 * @param locy
	 * @param mapid
	 * @param canTalk
	 * @param sec
	 */
	public void beginGhost(final int locx, final int locy, final short mapid, final boolean canTalk,
			final int sec) {
		if (this.isGhost()) {
			return;
		}
		this.setGhost(true);
		this._ghostSaveLocX = this.getX();
		this._ghostSaveLocY = this.getY();
		this._ghostSaveMapId = this.getMapId();
		this._ghostSaveHeading = this.getHeading();
		this.setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			this.set_ghostTime(sec);
		}
	}

	/**
	 * 離開鬼魂模式(傳送回出發點)
	 */
	public void makeReadyEndGhost() {
		this.setReserveGhost(true);
		L1Teleport.teleport(this, this._ghostSaveLocX, this._ghostSaveLocY,
				this._ghostSaveMapId, this._ghostSaveHeading, true);
	}

	/**
	 * 結束鬼魂模式
	 */
	public void endGhost() {
		this.set_ghostTime(-1);
		this.setGhost(false);
		this.setGhostCanTalk(true);
		this.setReserveGhost(false);
	}

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	/**
	 * 地獄以外居地獄強制移動
	 * @param isFirst
	 */
	public void beginHell(final boolean isFirst) {
		// 地獄以外居地獄強制移動
		if (this.getMapId() != 666) {
			final int locx = 32701;
			final int locy = 32777;
			final short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (this.get_PKcount() <= 10) {
				this.setHellTime(300);
				
			} else {
				this.setHellTime(300 * (this.get_PKcount() - 10) + 300);
			}
			// 552 因為你已經殺了 %0 人所以被打入地獄。 你將在這裡停留 %1 分鐘。
			this.sendPackets(new S_BlueMessage(552, String.valueOf(this.get_PKcount()), String.valueOf(this.getHellTime() / 60)));

		} else {
			// 637 你必須在此地停留 %0 秒。
			this.sendPackets(new S_BlueMessage(637, String.valueOf(this.getHellTime())));
		}
	}

	/**
	 * 地獄時間終止
	 */
	public void endHell() {
		// 地獄時間終止 返回然柳村
		final int[] loc = L1TownLocation.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		
		try {
			this.save();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void setPoisonEffect(final int effectId) {
		this.sendPackets(new S_Poison(this.getId(), effectId));

		if (!this.isGmInvis() && !this.isGhost() && !this.isInvisble()) {
			this.broadcastPacketAll(new S_Poison(this.getId(), effectId));
		}
	}

	@Override
	public void healHp(final int pt) {
		super.healHp(pt);

		this.sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return this._karma.get();
	}

	@Override
	public void setKarma(final int i) {
		this._karma.set(i);
	}

	public void addKarma(final int i) {
		synchronized (this._karma) {
			this._karma.add(i);
		}
	}

	public int getKarmaLevel() {
		return this._karma.getLevel();
	}

	public int getKarmaPercent() {
		return this._karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * 最終PK時間返。
	 *
	 * @return _lastPk
	 *
	 */
	public Timestamp getLastPk() {
		return this._lastPk;
	}

	/**
	 * 最終PK時間設定。
	 *
	 * @param time
	 *            最終PK時間（Timestamp型） 解除場合null代入
	 */
	public void setLastPk(final Timestamp time) {
		this._lastPk = time;
	}

	/**
	 * 最終PK時間現在時刻設定。
	 */
	public void setLastPk() {
		this._lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 手配中返。
	 *
	 * @return 手配中、true
	 */
	public boolean isWanted() {
		if (this._lastPk == null) {
			return false;

		// 距離PK時間超過1小時
		} else if (System.currentTimeMillis() - this._lastPk.getTime() > 1 * 3600 * 1000) {
			this.setLastPk(null);
			return false;
		}
		return true;
	}

	private Timestamp _lastPkForElf;

	public Timestamp getLastPkForElf() {
		return this._lastPkForElf;
	}

	public void setLastPkForElf(final Timestamp time) {
		this._lastPkForElf = time;
	}

	public void setLastPkForElf() {
		this._lastPkForElf = new Timestamp(System.currentTimeMillis());
	}

	public boolean isWantedForElf() {
		if (this._lastPkForElf == null) {
			return false;
			
		} else if (System.currentTimeMillis() - this._lastPkForElf.getTime() > 24 * 3600 * 1000) {
			this.setLastPkForElf(null);
			return false;
		}
		return true;
	}

	/**
	 * 職業魔法等級
	 */
	@Override
	public int getMagicLevel() {
		return this.getClassFeature().getMagicLevel(this.getLevel());
	}

	private double _weightUP = 1.0D;// 負重提高%

	/**
	 * 負重提高%
	 * @return
	 */
	public double get_weightUP() {
		return _weightUP;
	}

	/**
	 * 負重提高%
	 * @param i
	 */
	public void add_weightUP(final int i) {
		_weightUP += (i / 100D);
	}

	private int _weightReduction = 0;// 減重

	/**
	 * 減重
	 * @return
	 */
	public int getWeightReduction() {
		return this._weightReduction;
	}

	/**
	 * 減重
	 * @param i
	 */
	public void addWeightReduction(final int i) {
		this._weightReduction += i;
	}

	private int _originalStrWeightReduction = 0; // ● STR 重量輕減

	public int getOriginalStrWeightReduction() {
		return this._originalStrWeightReduction;
	}

	private int _originalConWeightReduction = 0; // ● CON 重量輕減

	public int getOriginalConWeightReduction() {
		return this._originalConWeightReduction;
	}

	private int _hasteItemEquipped = 0;// 裝備有加速能力裝備(裝備數量)

	/**
	 * 裝備有加速能力裝備(裝備數量)
	 * @return
	 */
	public int getHasteItemEquipped() {
		return this._hasteItemEquipped;
	}

	/**
	 * 裝備有加速能力裝備(裝備數量)
	 * @param i
	 */
	public void addHasteItemEquipped(final int i) {
		this._hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (this.hasSkillEffect(SLOW)) {
			this.removeSkillEffect(SLOW);
		}
		
		if (this.hasSkillEffect(MASS_SLOW)) {
			this.removeSkillEffect(MASS_SLOW);
		}
		
		if (this.hasSkillEffect(ENTANGLE)) {
			this.removeSkillEffect(ENTANGLE);
		}
		
		if (this.hasSkillEffect(HASTE)) {
			this.removeSkillEffect(HASTE);
		}
		
		if (this.hasSkillEffect(GREATER_HASTE)) {
			this.removeSkillEffect(GREATER_HASTE);
		}
		
		if (this.hasSkillEffect(STATUS_HASTE)) {
			this.removeSkillEffect(STATUS_HASTE);
		}
	}

	private int _damageReductionByArmor = 0; // 防具增加傷害減免

	public int getDamageReductionByArmor() {
		int damageReduction = 0;
		if (_damageReductionByArmor > 10) {
			damageReduction = 10 + (_random.nextInt((_damageReductionByArmor - 10)) + 1);
			
		} else {
			damageReduction = _damageReductionByArmor;
		}
		return damageReduction;
	}

	public void addDamageReductionByArmor(final int i) {
		this._damageReductionByArmor += i;
	}
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    private double _damageReductionByArmor1 = 0.0D; // 防具增加伤害减免
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    public double getDamageReductionByArmor1() {
//        int damageReduction = 0;
//        if (_damageReductionByArmor > 10) {
//            damageReduction = 10 + (_random
//                    .nextInt((_damageReductionByArmor - 10)) + 1);
//
//        } else {
//            damageReduction = _damageReductionByArmor;
//        }
//        return damageReduction;
    	//修正减伤不正确 QQ：403471355
    	return this._damageReductionByArmor1;
    }
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    public void addDamageReductionByArmor1(final int i) {
        this._damageReductionByArmor1 += (i / 100D);
    }
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    private double _damageReductionByArmor2 = 0.0D; // 防具增加伤害减免
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    public double getDamageReductionByArmor2() {
//        int damageReduction = 0;
//        if (_damageReductionByArmor > 10) {
//            damageReduction = 10 + (_random
//                    .nextInt((_damageReductionByArmor - 10)) + 1);
//
//        } else {
//            damageReduction = _damageReductionByArmor;
//        }
//        return damageReduction;
    	//修正减伤不正确 QQ：403471355
    	return this._damageReductionByArmor2;
    }
    /**
     * 强化伤害减免
     * 
     * @param i
     */
    public void addDamageReductionByArmor2(final int i) {
        this._damageReductionByArmor2 += i;
    }
	private int _hitModifierByArmor = 0; // 防具增加物理命中
	
	public int getHitModifierByArmor() {
		return this._hitModifierByArmor;
	}

	public void addHitModifierByArmor(final int i) {
		this._hitModifierByArmor += i;
	}

	private int _dmgModifierByArmor = 0; // 防具增加物理傷害
	
	public int getDmgModifierByArmor() {
		return this._dmgModifierByArmor;
	}

	public void addDmgModifierByArmor(final int i) {
		this._dmgModifierByArmor += i;
	}

	private int _bowHitModifierByArmor = 0; // 防具增加遠距離物理命中
	
	public int getBowHitModifierByArmor() {
		return this._bowHitModifierByArmor;
	}

	public void addBowHitModifierByArmor(final int i) {
		this._bowHitModifierByArmor += i;
	}

	private int _bowDmgModifierByArmor = 0; // 防具增加遠距離物理傷害
	
	public int getBowDmgModifierByArmor() {
		return this._bowDmgModifierByArmor;
	}

	public void addBowDmgModifierByArmor(final int i) {
		this._bowDmgModifierByArmor += i;
	}

	private boolean _gresValid; // G-RES有效

	private void setGresValid(final boolean valid) {
		this._gresValid = valid;
	}

	public boolean isGresValid() {
		return this._gresValid;
	}

	private boolean _isFishing = false;// 釣魚狀態

	/**
	 * 釣魚狀態
	 * @return
	 */
	public boolean isFishing() {
		return this._isFishing;
	}

	private int _fishX = -1;

	public int get_fishX() {
		return _fishX;
	}
	
	private int _fishY = -1;

	public int get_fishY() {
		return _fishY;
	}
	
	/**
	 * 釣魚狀態
	 * @param flag
	 * @param fishY 
	 * @param fishX 
	 */
	public void setFishing(final boolean flag, int fishX, int fishY) {
		this._isFishing = flag;
		_fishX = fishX;
		_fishY = fishY;
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return this._cookingId;
	}

	public void setCookingId(final int i) {
		this._cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return this._dessertId;
	}

	public void setDessertId(final int i) {
		this._dessertId = i;
	}

	/**
	 * LV命中設定 LV變動場合呼出再計算
	 *
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		if (this.isKnight() || this.isDarkelf()) { // 、、
			newBaseDmgup = this.getLevel() / 10;
			newBaseBowDmgup = 0;
			
		} else if (this.isElf()) { // 
			newBaseDmgup = 0;
			newBaseBowDmgup = this.getLevel() / 10;
		}
		this.addDmgup(newBaseDmgup - this._baseDmgup);
		this.addBowDmgup(newBaseBowDmgup - this._baseBowDmgup);
		this._baseDmgup = newBaseDmgup;
		this._baseBowDmgup = newBaseBowDmgup;
	}

	/**
	 * LV命中設定 LV變動場合呼出再計算
	 *
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (this.isCrown()) { // 
			newBaseHitup = this.getLevel() / 5;
			newBaseBowHitup = this.getLevel() / 5;

		} else if (this.isKnight()) { // 
			newBaseHitup = this.getLevel() / 3;
			newBaseBowHitup = this.getLevel() / 3;

		} else if (this.isElf()) { // 
			newBaseHitup = this.getLevel() / 5;
			newBaseBowHitup = this.getLevel() / 5;

		} else if (this.isDarkelf()) { // 
			newBaseHitup = this.getLevel() / 3;
			newBaseBowHitup = this.getLevel() / 3;

		}
		
		this.addHitup(newBaseHitup - this._baseHitup);
		this.addBowHitup(newBaseBowHitup - this._baseBowHitup);
		this._baseHitup = newBaseHitup;
		this._baseBowHitup = newBaseBowHitup;
	}

	/**
	 * AC再計算設定 初期設定時、LVUP,LVDown時呼出
	 */
	public void resetBaseAc() {
		final int newAc = CalcStat.calcAc(this.getLevel(), this.getBaseDex(), this);
		this.addAc(newAc - this._baseAc);
		this._baseAc = newAc;
	}

	/**
	 * 素MR再計算設定 初期設定時、使用時LVUP,LVDown時呼出
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (this.isCrown()) { // 
			newMr = 10;

		} else if (this.isElf()) { // 
			newMr = 25;

		} else if (this.isWizard()) { // 
			newMr = 15;

		} else if (this.isDarkelf()) { // 
			newMr = 10;

		}
		newMr += CalcStat.calcStatMr(this.getWis()); // WIS分MR
		newMr += this.getLevel() / 2; // LV半分追加
		this.addMr(newMr - this._baseMr);
		this._baseMr = newMr;
	}

	/**
	 * 重新設置等級為目前經驗質所屬
	 */
	public void resetLevel() {
		this.setLevel(ExpTable.getLevelByExp(this._exp));
	}

	/**
	 * 初期現在再計算設定 初期設定時、再配分時呼出
	 */
	public void resetOriginalHpup() {
		this._originalHpup = L1PcOriginal.resetOriginalHpup(this);
	}

	public void resetOriginalMpup() {
		this._originalMpup = L1PcOriginal.resetOriginalMpup(this);
	}

	public void resetOriginalStrWeightReduction() {
		this._originalStrWeightReduction = L1PcOriginal.resetOriginalStrWeightReduction(this);
	}

	public void resetOriginalDmgup() {
		this._originalDmgup = L1PcOriginal.resetOriginalDmgup(this);
	}

	public void resetOriginalConWeightReduction() {
		this._originalConWeightReduction = L1PcOriginal.resetOriginalConWeightReduction(this);
	}

	public void resetOriginalBowDmgup() {
		this._originalBowDmgup = L1PcOriginal.resetOriginalBowDmgup(this);
	}

	public void resetOriginalHitup() {
		this._originalHitup = L1PcOriginal.resetOriginalHitup(this);
	}

	public void resetOriginalBowHitup() {
		this._originalBowHitup = L1PcOriginal.resetOriginalBowHitup(this);
	}

	public void resetOriginalMr() {
		this._originalMr = L1PcOriginal.resetOriginalMr(this);
		this.addMr(this._originalMr);
	}

	public void resetOriginalMagicHit() {
		this._originalMagicHit = L1PcOriginal.resetOriginalMagicHit(this);
	}

	public void resetOriginalMagicCritical() {
		this._originalMagicCritical = L1PcOriginal.resetOriginalMagicCritical(this);
	}

	public void resetOriginalMagicConsumeReduction() {
		this._originalMagicConsumeReduction = L1PcOriginal.resetOriginalMagicConsumeReduction(this);
	}

	public void resetOriginalMagicDamage() {
		this._originalMagicDamage = L1PcOriginal.resetOriginalMagicDamage(this);
	}

	public void resetOriginalAc() {
		this._originalAc = L1PcOriginal.resetOriginalAc(this);
		//System.out.println("_originalAc:"+_originalAc);
		this.addAc(0 - this._originalAc);
	}

	public void resetOriginalEr() {
		this._originalEr = L1PcOriginal.resetOriginalEr(this);
	}

	public void resetOriginalHpr() {
		this._originalHpr = L1PcOriginal.resetOriginalHpr(this);
	}

	public void resetOriginalMpr() {
		this._originalMpr = L1PcOriginal.resetOriginalMpr(this);
	}

	/**
	 * 全屬性重置
	 */
	public void refresh() {
		this.resetLevel();
		this.resetBaseHitup();
		this.resetBaseDmgup();
		this.resetBaseMr();
		this.resetBaseAc();
		this.resetOriginalHpup();
		this.resetOriginalMpup();
		this.resetOriginalDmgup();
		this.resetOriginalBowDmgup();
		this.resetOriginalHitup();
		this.resetOriginalBowHitup();
		this.resetOriginalMr();
		this.resetOriginalMagicHit();
		this.resetOriginalMagicCritical();
		this.resetOriginalMagicConsumeReduction();
		this.resetOriginalMagicDamage();
		this.resetOriginalAc();
		this.resetOriginalEr();
		this.resetOriginalHpr();
		this.resetOriginalMpr();
		this.resetOriginalStrWeightReduction();
		this.resetOriginalConWeightReduction();
	}

	// 人物訊息拒絕清單
	private final L1ExcludingList _excludingList = new L1ExcludingList();

	/**
	 * 人物訊息拒絕清單
	 * @return
	 */
	public L1ExcludingList getExcludingList() {
		return this._excludingList;
	}

	private int _teleportX = 0;// 傳送目的座標X

	/**
	 * 傳送目的座標X
	 * @return
	 */
	public int getTeleportX() {
		return this._teleportX;
	}

	/**
	 * 傳送目的座標X
	 * @param i
	 */
	public void setTeleportX(final int i) {
		this._teleportX = i;
	}

	private int _teleportY = 0;// 傳送目的座標Y

	/**
	 * 傳送目的座標Y
	 * @return
	 */
	public int getTeleportY() {
		return this._teleportY;
	}

	/**
	 * 傳送目的座標Y
	 * @param i
	 */
	public void setTeleportY(final int i) {
		this._teleportY = i;
	}

	private short _teleportMapId = 0;// 傳送目的座標MAP

	/**
	 * 傳送目的座標MAP
	 * @return
	 */
	public short getTeleportMapId() {
		return this._teleportMapId;
	}

	/**
	 * 傳送目的座標MAP
	 * @param i
	 */
	public void setTeleportMapId(final short i) {
		this._teleportMapId = i;
	}

	private int _teleportHeading = 0;// 傳送後面向

	/**
	 * 傳送後面向
	 * @return
	 */
	public int getTeleportHeading() {
		return this._teleportHeading;
	}

	/**
	 * 傳送後面向
	 * @param i
	 */
	public void setTeleportHeading(final int i) {
		this._teleportHeading = i;
	}

	private int _tempCharGfxAtDead;// 死亡時外型代號

	/**
	 * 死亡時外型代號
	 * @return
	 */
	public int getTempCharGfxAtDead() {
		return this._tempCharGfxAtDead;
	}

	/**
	 * 死亡時外型代號
	 * @param i
	 */
	private void setTempCharGfxAtDead(final int i) {
		this._tempCharGfxAtDead = i;
	}

	private boolean _isCanWhisper = true;// 全秘密語(收聽)

	/**
	 * 全秘密語(收聽)
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isCanWhisper() {
		return this._isCanWhisper;
	}

	/**
	 * 全秘密語(收聽)
	 * @param flag flag true:接收 false:拒絕
	 */
	public void setCanWhisper(final boolean flag) {
		this._isCanWhisper = flag;
	}

	private boolean _isShowTradeChat = true;// 買賣頻道(收聽)

	/**
	 * 買賣頻道(收聽)
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isShowTradeChat() {
		return this._isShowTradeChat;
	}

	/**
	 * 買賣頻道(收聽)
	 * @param flag true:接收 false:拒絕
	 */
	public void setShowTradeChat(final boolean flag) {
		this._isShowTradeChat = flag;
	}

	private boolean _isShowWorldChat = true;// 全體聊天(收聽)

	/**
	 * 全體聊天(收聽)
	 * @return flag true:接收 false:拒絕
	 */
	public boolean isShowWorldChat() {
		return this._isShowWorldChat;
	}
	
    private boolean _pvp = false;

	public void setPVP(boolean b) {
		_pvp = b;
	}
	/**
	 * 掛機瞬移
	 * 
	 * @return
	 */
    private boolean _gjsy = false;

	public void setgjsy(boolean m) {
		_gjsy = m;
	}
	/**
	 * 掛機瞬移
	 * 
	 * @return
	 */
	public boolean isgjsy()	{
	 return this._gjsy;
	}
	
	public boolean isPVP() {
		return _pvp;
	}

	/**
	 * 全體聊天(收聽)
	 * @param flag flag true:接收 false:拒絕
	 */
	public void setShowWorldChat(final boolean flag) {
		this._isShowWorldChat = flag;
	}
	
	/**掛機自動技能開關**/
	private int _DmgMessage1 = 0;
	/**掛機自動技能開關**/
	public int getDmgMessage1() {
		return _DmgMessage1;
	}
	/**掛機自動技能開關**/
	public void setDmgMessage1(int i) {
		_DmgMessage1 = i;
	}
	


	private int _fightId;// 決鬥對像OBJID

	/**
	 * 決鬥對像OBJID
	 * @return
	 */
	public int getFightId() {
		return this._fightId;
	}

	/**
	 * 決鬥對像OBJID
	 * @param i
	 */
	public void setFightId(final int i) {
		this._fightId = i;
	}

	private byte _chatCount = 0;// 對話檢查次數

	private long _oldChatTimeInMillis = 0L;// 對話檢查毫秒差

	/**
	 * 對話檢查(洗畫面)
	 */
	public void checkChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (this._chatCount == 0) {
			this._chatCount++;
			this._oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis - this._oldChatTimeInMillis;
		// 時間差異2秒以上
		if (chatInterval > 2000) {
			this._chatCount = 0;
			this._oldChatTimeInMillis = 0;

		} else {
			if (this._chatCount >= 3) {
				this.setSkillEffect(STATUS_CHAT_PROHIBITED, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3因洗畫面的關係，2分鐘之內無法聊天。
				this.sendPackets(new S_ServerMessage(153));
				this._chatCount = 0;
				this._oldChatTimeInMillis = 0;
			}
			this._chatCount++;
		}
	}

	private int _callClanId;// 呼喚盟友(對像OBJID)

	/**
	 * 傳回呼喚盟友(對像OBJID)
	 * @return
	 */
	public int getCallClanId() {
		return this._callClanId;
	}

	/**
	 * 設置呼喚盟友(對像OBJID)
	 * @param i
	 */
	public void setCallClanId(final int i) {
		this._callClanId = i;
	}

	private int _callClanHeading;// 設置呼喚盟友(自己的面向)

	/**
	 * 設置呼喚盟友(自己的面向)
	 * @return
	 */
	public int getCallClanHeading() {
		return this._callClanHeading;
	}

	/**
	 * 傳回呼喚盟友(自己的面向)
	 * @return
	 */
	public void setCallClanHeading(final int i) {
		this._callClanHeading = i;
	}

	private boolean _isInCharReset = false;// 執行人物重設狀態

	/**
	 * 傳回執行人物重設狀態
	 * @return
	 */
	public boolean isInCharReset() {
		return this._isInCharReset;
	}

	/**
	 * 設置執行人物重設狀態
	 * @param flag
	 */
	public void setInCharReset(final boolean flag) {
		this._isInCharReset = flag;
	}
	
    /**
     * 自動補血: 用來C_Chat設定參數用
     * 
     * @return
     */
    private int _isAutoHpType; // 自動補血開關

 	public void set_AutoHpType(final int i) {
 		_isAutoHpType = i;
 	}

 	public int get_AutoHpType() {
 		return _isAutoHpType;
 	} 	
 	/**
     * 自棟喝水: 設定藥水低於幾%血量就使用
     * 
     * @return
     */
 	private int _HpPercent;

 	public void set_HpPercent(final int text2) {
 		_HpPercent = text2;
 	}

 	public int get_HpPercent() {
 		return _HpPercent;
 	}
 	
 	// 20171124 自動喝水相關參數END
    private int _lslocx = 0;// 範圍掛機臨時中心點X坐標

    /**
     * 範圍掛機臨時中心點X坐標
     * 
     * @return
     */
    public int getlslocx() {
        return this._lslocx;
    }

    /**
     * 範圍掛機臨時中心點X坐標
     * 
     * @param i
     */
    public void setlslocx(final int i) {
        this._lslocx = i;
    }
    private int _lslocy = 0;// 範圍掛機臨時中心點y坐標

    /**
     * 範圍掛機臨時中心點y坐標
     * 
     * @return
     */
    public int getlslocy() {
        return this._lslocy;
    }

    /**
     * 範圍掛機臨時中心點y坐標
     * 
     * @param i
     */
    public void setlslocy(final int i) {
        this._lslocy = i;
    }

	private int _tempLevel = 1;// 人物重置等級暫存(最低)

	/**
	 * 人物重置等級暫存(最低)
	 * @return
	 */
	public int getTempLevel() {
		return this._tempLevel;
	}

	/**
	 * 人物重置等級暫存(最低)
	 * @param i
	 */
	public void setTempLevel(final int i) {
		this._tempLevel = i;
	}

	private int _tempMaxLevel = 1;// 人物重置等級暫存(最高)

	/**
	 * 人物重置等級暫存(最高)
	 * @return
	 */
	public int getTempMaxLevel() {
		return this._tempMaxLevel;
	}

	/**
	 * 人物重置等級暫存(最高)
	 * @param i
	 */
	public void setTempMaxLevel(final int i) {
		this._tempMaxLevel = i;
	}

	private boolean _isSummonMonster = false;// 是否展開召喚控制選單

	/**
	 * 設置是否展開召喚控制選單
	 * @param SummonMonster
	 */
	public void setSummonMonster(final boolean SummonMonster) {
		this._isSummonMonster = SummonMonster;
	}

	/**
	 * 是否展開召喚控制選單
	 * @return
	 */
	public boolean isSummonMonster() {
		return this._isSummonMonster;
	}

	private boolean _isShapeChange = false;// 是否展開變身控制選單

	/**
	 * 設置是否展開變身控制選單
	 * @param isShapeChange
	 */
	public void setShapeChange(final boolean isShapeChange) {
		this._isShapeChange = isShapeChange;
	}

	/**
	 * 是否展開變身控制選單
	 * @return
	 */
	public boolean isShapeChange() {
		return this._isShapeChange;
	}

	private String _text;// 暫存文字串

	/**
	 * 設置暫存文字串(收件者)
	 *
	 * @param text
	 */
	public void setText(final String text) {
		this._text = text;
	}

	/**
	 * 傳回暫存文字串(收件者)
	 *
	 * @return
	 */
	public String getText() {
		return this._text;
	}
	
	private byte[] _textByte = null;// 暫存byte[]陣列

	/**
	 * 設定暫存byte[]陣列
	 *
	 * @param textByte
	 */
	public void setTextByte(final byte[] textByte) {
		this._textByte = textByte;
	}

	/**
	 * 傳回暫存byte[]陣列
	 *
	 * @return
	 */
	public byte[] getTextByte() {
		return this._textByte;
	}

	private L1PcOther _other;// 額外紀錄資料

	/**
	 * 額外紀錄資料
	 * @param other
	 */
	public void set_other(final L1PcOther other) {
		this._other = other;
	}

	/**
	 * 額外紀錄資料
	 * @return
	 */
	public L1PcOther get_other() {
		return this._other;
	}

	private L1PcOtherList _otherList;// 額外清單紀錄資料

	/**
	 * 額外清單紀錄資料
	 * @param other
	 */
	public void set_otherList(final L1PcOtherList other) {
		_otherList = other;
	}

	/**
	 * 額外清單紀錄資料
	 * @return
	 */
	public L1PcOtherList get_otherList() {
		return _otherList;
	}

	private int _oleLocX;// 移動前座標暫存X
	
	/**
	 * 移動前座標暫存X
	 * @param oleLocx
	 */
	public void setOleLocX(final int oleLocx) {
		this._oleLocX = oleLocx;
	}

	/**
	 * 移動前座標暫存X
	 * @return
	 */
	public int getOleLocX() {
		return this._oleLocX;
	}

	private int _oleLocY;// 移動前座標暫存Y
	
	/**
	 * 移動前座標暫存Y
	 * @param oleLocy
	 */
	public void setOleLocY(final int oleLocy) {
		this._oleLocY = oleLocy;
	}

	/**
	 * 移動前座標暫存Y
	 * @return
	 */
	public int getOleLocY() {
		return this._oleLocY;
	}

	private L1Character _target = null;
	
	/**
	 * 設置目前攻擊對像
	 * @param target
	 */
	public void setNowTarget(final L1Character target) {
		this._target = target;
	}
	
	
    /**
     * 取得回溯錯誤次數.
     * 
     * @return
     */
    public int getMoveErrorCount() {
        return moveErrorCount;
    }

    /**
     * 設置回溯錯誤次數.
     * 
     * @param moveErrorCount
     *            - 要設置的
     */
    public void setMoveErrorCount(int moveErrorCount) {
        this.moveErrorCount = moveErrorCount;
    }

    /**
     * 回溯錯誤(正常狀態).
     * 
     * @return
     */
    public boolean isMoveStatus() {
        return moveStatus;
    }

    /**
     * 設置回溯錯誤(正常狀態).
     * 
     * @param moveStatus
     *            - 要設置的
     */
    public void setMoveStatus(boolean moveStatus) {
        this.moveStatus = moveStatus;
    }

	/**
	 * 傳回目前攻擊對像
	 */
	public L1Character getNowTarget() {
		return this._target;
	}

	private int _dmgDown = 0;
	
	/**
	 * 副助道具傷害減免
	 * @param dmgDown
	 */
	public void set_dmgDown(int dmgDown) {
		_dmgDown = dmgDown;
	}
	
	/**
	 * 副助道具傷害減免
	 * @return
	 */
	public int get_dmgDown() {
		return _dmgDown;
	}

	/**
	 * 保存寵物目前模式
	 * @param pc
	 */
	public void setPetModel() {
		try {
			// 寵物的跟隨移動
			for (final L1NpcInstance petNpc : getPetList().values()) {
				if (petNpc != null) {
					if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
						final L1SummonInstance summon = (L1SummonInstance) petNpc;
						summon.set_tempModel();
						
					} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
						final L1PetInstance pet = (L1PetInstance) petNpc;
						pet.set_tempModel();
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 恢復寵物目前模式
	 * @param pc
	 */
	public void getPetModel() {
		try {
			// 寵物的跟隨移動
			for (final L1NpcInstance petNpc : getPetList().values()) {
				if (petNpc != null) {
					if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
						final L1SummonInstance summon = (L1SummonInstance) petNpc;
						summon.get_tempModel();
						
					} else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
						final L1PetInstance pet = (L1PetInstance) petNpc;
						pet.get_tempModel();
					}
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	private int _temp_adena = 0;// 本次使用貨幣類型 2018阿豪 新增商店可用多種貨幣

	/**
	 * 本次使用貨幣類型
	 * 
	 * @param itemid
	 */
	public void set_temp_adena(int itemid) {
		_temp_adena = itemid;
	}

	/**
	 * 本次使用貨幣類型
	 * 
	 * @return
	 */
	public int get_temp_adena() {
		return _temp_adena;
	}
	
	private long _shopAdenaRecord;

	public final long getShopAdenaRecord() {
		return _shopAdenaRecord;
	}

	public final void setShopAdenaRecord(final long i) {
		_shopAdenaRecord = i;
	}

	private boolean _mazu = false;// 媽祖祝福
	
	/**
	 * 媽祖祝福
	 * @param b
	 */
	public void set_mazu(boolean b) {
		_mazu = b;
	}

	/**
	 * 媽祖祝福
	 * @return 
	 */
	public boolean is_mazu() {
		return _mazu;
	}

	private long _mazu_time = 0;// 媽祖祝福時間
	
	/**
	 * 媽祖祝福時間
	 * @return
	 */
	public long get_mazu_time() {
		return _mazu_time;
	}
	
	/**
	 * 媽祖祝福時間
	 * @param time
	 */
	public void set_mazu_time(long time) {
		_mazu_time = time;
	}

	private int _int1;// 機率增加攻擊力
	private int _int2;// 機率(1/100)
	
	/**
	 * 機率增加攻擊力
	 * @param int1
	 * @param int2
	 */
	public void set_dmgAdd(int int1, int int2) {
		_int1 += int1;
		_int2 += int2;
	}
	
	/**
	 * 傳回機率增加的攻擊力
	 * @return
	 */
	public int dmgAdd() {
		if (_int2 == 0) {
			return 0;
		}
		if ((_random.nextInt(100) + 1) <= _int2) {
			if (!getDolls().isEmpty()) {
				for (L1DollInstance doll : getDolls().values()) {
					doll.show_action(1);
				}
			}
			return _int1;
		}
		return 0;
	}
	
	private int _evasion;// 迴避機率(1/1000)
	
	/**
	 * 迴避機率
	 * @param int1
	 */
	public void set_evasion(int int1) {
		_evasion += int1;
	}
	
	/**
	 * 傳回迴避機率
	 * @return
	 */
	public int get_evasion() {
		return _evasion;
	}

	private double _expadd = 0.0D;// 經驗值增加
	
	/**
	 * 經驗值增加
	 * @param int1
	 */
	public void set_expadd(int int1) {
		_expadd += (int1 / 100D);
	}
	
	/**
	 * 經驗值增加
	 * @return
	 */
	public double getExpAdd() {
		return _expadd;
	}

	private int _dd1;// 機率傷害減免
	private int _dd2;// 機率(1/100)
	
	/**
	 * 機率傷害減免
	 * @param int1
	 * @param int2
	 */
	public void set_dmgDowe(int int1, int int2) {
		_dd1 += int1;
		_dd2 += int2;
	}
	
	/**
	 * 傳回機率傷害減免
	 * @return
	 */
	public int dmgDowe() {
		if (_dd2 == 0) {
			return 0;
		}
		if ((_random.nextInt(100) + 1) <= _dd2) {
			if (!getDolls().isEmpty()) {
				for (L1DollInstance doll : getDolls().values()) {
					doll.show_action(2);
				}
			}
			return _dd1;
		}
		return 0;
	}

	private int _actionId = -1;// 角色表情動作代號
	
	/**
	 * 角色表情動作代號
	 * @param actionId
	 */
	public void set_actionId(int actionId) {
		_actionId = actionId;
	}
	
	/**
	 * 角色表情動作代號
	 * @return
	 */
	public int get_actionId() {
		return _actionId;
	}

	private Chapter01R _hardin = null;// 哈汀副本線程
	
	/**
	 * 哈汀副本線程
	 * @param hardin
	 */
	public void set_hardinR(Chapter01R hardin) {
		_hardin = hardin;
	}
	
	/**
	 * 哈汀副本線程
	 * @return
	 */
	public Chapter01R get_hardinR() {
		return _hardin;
	}

	private int _unfreezingTime = 0;// 解除人物卡點
	
	public void set_unfreezingTime(int i) {
		_unfreezingTime = i;
	}
	
	public int get_unfreezingTime() {
		return _unfreezingTime;
	}

	private int _magic_modifier_dmg = 0;// 套裝增加魔法傷害
	
	public void add_magic_modifier_dmg(int add) {
		_magic_modifier_dmg += add;
	}
	
	public int get_magic_modifier_dmg() {
		return _magic_modifier_dmg;
	}
	
	private int _reduction_dmg = 0;// 套裝減免物理傷害

	public void add_reduction_dmg(int add) {
		_reduction_dmg += add;
	}

	public int get_reduction_dmg() {
		return _reduction_dmg;
	}

	private int _magic_reduction_dmg = 0;// 套裝減免魔法傷害
	
	public void add_magic_reduction_dmg(int add) {
		_magic_reduction_dmg += add;
	}
	
	public int get_magic_reduction_dmg() {
		return _magic_reduction_dmg;
	}

	private boolean _rname = false;// 重設名稱
	private boolean pcShopReName = false;
	public final void setPcShopReName(final boolean set) {
		this.pcShopReName = set;
	}

	public final boolean isPcShopReName() {
		return this.pcShopReName;
	}
	
	/**
	 * 重設名稱
	 * @param b
	 */
	public void rename(boolean b) {
		_rname = b;
	}
	
	/**
	 * 重設名稱
	 * @return
	 */
	public boolean is_rname() {
		return _rname;
	}
	private boolean _retitle = false;// 重設封號
	
	/**
	 * 重設封號
	 * @return
	 */
	public boolean is_retitle() {
		return _retitle;
	}

	/**
	 * 重設封號
	 * @param b
	 */
	public void retitle(boolean b) {
		_retitle = b;
	}

	private int _repass = 0;// 重設密碼
	
	/**
	 * 重設密碼
	 * @return
	 */
	public int is_repass() {
		return _repass;
	}

	/**
	 * 重設密碼
	 * @param b
	 */
	public void repass(int b) {
		_repass = b;
	}
	
	// 交易物品暫存
	private ArrayList<L1TradeItem> _trade_items = new ArrayList<L1TradeItem>();
	
	/**
	 * 加入交易物品暫存
	 * @param info
	 */
	public void add_trade_item(L1TradeItem info) {
		if (_trade_items.size() == 16) {
			return;
		}
		_trade_items.add(info);
	}
	
	/**
	 * 交易物品暫存
	 * @return
	 */
	public ArrayList<L1TradeItem> get_trade_items() {
		return _trade_items;
	}
	
	/**
	 * 清空交易物品暫存
	 */
	public void get_trade_clear() {
		_tradeID = 0;
		_trade_items.clear();
	}

	private int _mode_id = 0;// 記錄選取位置
	
	/**
	 * 記錄選取位置
	 * @param mode
	 */
	public void set_mode_id(int mode) {
		_mode_id = mode;
	}
	
	/**
	 * 記錄選取位置
	 * @return
	 */
	public int get_mode_id() {
		return _mode_id;
	}

	private boolean _check_item = false;

	public void set_check_item(boolean b) {
		_check_item = b;
	}

	public boolean get_check_item() {
		return _check_item;
	}

	private long _global_time = 0;
	
	public long get_global_time() {
		return _global_time;
	}
	
	public void set_global_time(final long global_time) {
		_global_time = global_time;
	}
	
	// DOLL 指定時間HP恢復

	private int _doll_hpr = 0;
	
	public int get_doll_hpr() {
		return _doll_hpr;
	}
	
	public void set_doll_hpr(int hpr) {
		_doll_hpr = hpr;
	}

	private int _doll_hpr_time = 0;// 計算用時間(秒)
	
	public int get_doll_hpr_time() {
		return _doll_hpr_time;
	}
	
	public void set_doll_hpr_time(int time) {
		_doll_hpr_time = time;
	}

	private int _doll_hpr_time_src = 0;// 恢復時間(秒)
	
	public int get_doll_hpr_time_src() {
		return _doll_hpr_time_src;
	}
	
	public void set_doll_hpr_time_src(int time) {
		_doll_hpr_time_src = time;
	}
	
	// DOLL 指定時間MP恢復

	private int _doll_mpr = 0;
	
	public int get_doll_mpr() {
		return _doll_mpr;
	}
	
	public void set_doll_mpr(int mpr) {
		_doll_mpr = mpr;
	}

	private int _doll_mpr_time = 0;// 計算用時間(秒)
	
	public int get_doll_mpr_time() {
		return _doll_mpr_time;
	}
	
	public void set_doll_mpr_time(int time) {
		_doll_mpr_time = time;
	}

	private int _doll_mpr_time_src = 0;// 恢復時間(秒)
	
	public int get_doll_mpr_time_src() {
		return _doll_mpr_time_src;
	}
	
	public void set_doll_mpr_time_src(int time) {
		_doll_mpr_time_src = time;
	}
	
	// DOLL 指定時間給予物品

	private int[] _doll_get = new int[2];
	
	public int[] get_doll_get() {
		return _doll_get;
	}
	
	public void set_doll_get(int itemid, int count) {
		_doll_get[0] = itemid;
		_doll_get[1] = count;
	}

	private int _doll_get_time = 0;// 計算用時間(秒)
	
	public int get_doll_get_time() {
		return _doll_get_time;
	}
	
	public void set_doll_get_time(int time) {
		_doll_get_time = time;
	}

	private int _doll_get_time_src = 0;// 給予時間(秒)
	
	public int get_doll_get_time_src() {
		return _doll_get_time_src;
	}
	
	public void set_doll_get_time_src(int time) {
		_doll_get_time_src = time;
	}
	
	// 留言版使用
	private String _board_title;// 暫存文字串
	
	public void set_board_title(final String text) {
		this._board_title = text;
	}
	
	public String get_board_title() {
		return this._board_title;
	}
	
	private String _board_content;// 暫存文字串
	
	public void set_board_content(final String text) {
		this._board_content = text;
	}
	
	public String get_board_content() {
		return this._board_content;
	}
	
	// 封包接收速度紀錄
	private long _spr_move_time = 0;// 移動
	
	public void set_spr_move_time(final long spr_time) {
		_spr_move_time = spr_time;
	}
	
	public long get_spr_move_time() {
		return this._spr_move_time;
	}
	
	private long _spr_attack_time = 0;// 攻擊
	
	public void set_spr_attack_time(final long spr_time) {
		_spr_attack_time = spr_time;
	}
	
	public long get_spr_attack_time() {
		return this._spr_attack_time;
	}
	
	private long _spr_skill_time = 0;// 技能
	
	public void set_spr_skill_time(final long spr_time) {
		_spr_skill_time = spr_time;
	}
	
	public long get_spr_skill_time() {
		return this._spr_skill_time;
	}
	

    
    /** 是否為進入遊戲世界狀態. */
    private boolean isLoginToServer;

    /**
     * 是否為進入遊戲世界狀態.
     * 
     * @return 返回 true or false
     */
    public boolean isLoginToServer() {
        return isLoginToServer;
    }

    /**
     * 設置進入遊戲世界狀態.
     * 
     * @param flag
     *            - true or false
     */
    public void setLoginToServer(boolean flag) {
        isLoginToServer = flag;
    }


	// 死亡相關
	
	private int _delete_time = 0;// 死亡時間
	
	public void set_delete_time(final int time) {
		_delete_time = time;
	}
	
	public int get_delete_time() {
		return _delete_time;
	}

	// 藥水使用HP恢復增加
	
	private int _up_hp_potion = 0;
	
	/**
	 * 藥水使用HP恢復增加(1/100)
	 * @param up_hp_potion
	 */
	public void set_up_hp_potion(final int up_hp_potion) {
		_up_hp_potion = up_hp_potion;
	}
	
	/**
	 * 藥水使用HP恢復增加(1/100)
	 * @return
	 */
	public int get_up_hp_potion() {
		return _up_hp_potion;
	}

	// 法利昂的治癒守護(1/1000)
	
	private int _elitePlateMail_Fafurion = 0;
	private int _fafurion_hpmin = 0;
	private int _fafurion_hpmax = 0;
	
	/**
	 * 法利昂的治癒守護(1/1000)
	 * @param r
	 */
	public void set_elitePlateMail_Fafurion(final int r, final int hpmin, final int hpmax) {
		_elitePlateMail_Fafurion = r;
		_fafurion_hpmin = hpmin;
		_fafurion_hpmax = hpmax;
	}
	


	// 毒性抵抗效果
	int _venom_resist = 0;
	
	/**
	 * 毒性抵抗效果(裝備)
	 * @param i 裝備數量
	 */
	public void set_venom_resist(int i) {
		_venom_resist += i;
	}
	
	private String OtherName;

	public String getOtherName() {
		return this.OtherName;
	}
	public void setOtherName(final String s) {
		this.OtherName = s;
	}	
	
	/**特殊卡片限量张数**/
	private int _CardUseCount = 0;
	/**特殊卡片限量张数**/
	public int getCardUseCount() {
		return _CardUseCount;
	}
	/**特殊卡片限量张数**/
	public void setCardUseCount(int i) {
		_CardUseCount = i;
	}
	/**死亡经验值不掉落-不储存**/
	private int _ExpNotLose = 0;
	/**死亡经验值不掉落-不储存**/
	public int getExpNotLose() {
		return _ExpNotLose;
	}
	/**死亡经验值不掉落-不储存**/
	public void setExpNotLose(int i) {
		_ExpNotLose = i;
	}
	/**死亡物品不掉落-不储存**/
	private int _ItemNotLose = 0;
	/**死亡物品不掉落-不储存**/
	public int getItemNotLose() {
		return _ItemNotLose;
	}
	/**死亡物品不掉落-不储存**/
	public void setItemNotLose(int i) {
		_ItemNotLose = i;
	}
	/**
	 * 取回聲望數量
	 * 
	 * @return
	 */
//	public int gettitle2() {
//		return _title2;
//	}
//
//	public void settitle2(final int i) {
//		this._title2 = i;
//	}

	int _lvuplv = 0;

	public void setLvup_lv(int i) {
		_lvuplv = i;
	}

	public int getLvup_lv() {
		return _lvuplv;
	}

	int _lvupdmg = 0;

	public void setLvup_dmg(int i) {
		_lvupdmg = i;
	}

	public int getLvup_dmg() {
		return _lvupdmg;
	}

	int _lvuphit = 0;

	public void setLvup_hit(int i) {
		_lvuphit = i;
	}

	public int getLvup_hit() {
		return _lvuphit;
	}

	int _lvupfdmg = 0;

	public void setLvup_fdmg(int i) {
		_lvupfdmg = i;
	}

	public int getLvup_fdmg() {
		return _lvupfdmg;
	}

	int _lvupfhit = 0;

	public void setLvup_fhit(int i) {
		_lvupfhit = i;
	}

	public int getLvup_fhit() {
		return _lvupfhit;
	}

	int _lvupdmgr = 0;

	public void setLvup_dmgr(int i) {
		_lvupdmgr = i;
	}

	public int getLvup_dmgr() {
		return _lvupdmgr;
	}

	// 魔傷
	int _lvupmdmg = 0;

	public void setLvup_mdmg(int i) {
		_lvupmdmg = i;
	}

	public int getLvup_mdmg() {
		return _lvupmdmg;
	}
	
	// 魔傷減
	int _lvupmdmgr = 0;

	public void setLvup_mdmgr(int i) {
		_lvupmdmgr = i;
	}

	public int getLvup_mdmgr() {
		return _lvupmdmgr;
	}
	
	int _lvupsp = 0;

	public void setLvup_sp(int i) {
		_lvupsp = i;
	}

	public int getLvup_sp() {
		return _lvupsp;
	}

	int _lvupmr = 0;

	public void setLvup_mr(int i) {
		_lvupmr = i;
	}

	public int getLvup_mr() {
		return _lvupmr;
	}

	int _lvuphp = 0;

	public void setLvup_hp(int i) {
		_lvuphp = i;
	}

	public int getLvup_hp() {
		return _lvuphp;
	}

	int _lvupmp = 0;

	public void setLvup_mp(int i) {
		_lvupmp = i;
	}

	public int getLvup_mp() {
		return _lvupmp;
	}
	
	/**
	 * 毒性抵抗效果(裝備)
	 * @return
	 */
	public int get_venom_resist() {
		return _venom_resist;
	}

	// 加速檢測器
	private AcceleratorChecker _speed = null;
	
	/**
	 * 加速檢測器
	 * @return
	 */
	public AcceleratorChecker speed_Attack() {
		if (_speed == null) {
			_speed = new AcceleratorChecker(this);
		}
		return _speed;
	}
	
	/**掛機瞬移開關**/
	private int _guajikg= 0;
	/**掛機瞬移開關**/
	public int getguajikg() {
		return _guajikg;
	}
	/**掛機瞬移開關**/
	public void setguajikg(int i) {
		_guajikg = i;
	}
	/**三重開關**/
	private int _sanchong= 0;
	/**三重開關**/
	public int getsanchong() {
		return _sanchong;
	}
	/**三重開關**/
	public void setsanchong(int i) {
		_sanchong = i;
	}
	/**掛機烈炎**/
	private int _lieyang= 0;
	/**掛機烈炎**/
	public int getlieyang() {
		return _lieyang;
	}
	/**掛機烈炎**/
	public void setlieyang(int i) {
		_lieyang = i;
	}
	/**掛機光標**/
	private int _guanj= 0;
	/**掛機光標**/
	public int getguanj() {
		return _guanj;
	}
	/**掛機光標**/
	public void setguanj(int i) {
		_guanj= i;
	}
	/**掛機禮貌模式**/
	private int _limao= 0;
	/**掛機禮貌模式**/
	public int getlimao() {
		return _limao;
	}
	/**掛機禮貌模式**/
	public void setlimao(int i) {
		_limao = i;
	}
	/**掛機三連斬**/
	private int _gjslz= 0;
	/**掛機三連斬*/
	public int getgjslz() {
		return _gjslz;
	}
	/**掛機三連斬**/
	public void setgjslz(int i) {
		_gjslz = i;
	}
	
	   public boolean isCharSelect() {
	        return charSelect;
	    }
	
	   /**
	    * 動作延時 QQ:759347094 防變檔
	    */
	   private boolean _isHardDelay = false;

	   /**
	    * 設定動作延遲中
	    * 
	    * @param flag
	    *            true:是 false:否
	    */
	   public void setHardDelay(final boolean flag) {
	       _isHardDelay = flag;
	   }

	   /**
	    * 是否動作延遲中
	    * 
	    * @return true:是 false:否
	    */
	   public boolean isHardDelay() {
	       return _isHardDelay;
	   }

	   /**
	    * 技能動作延時 QQ:759347094 防變檔
	    */
	   private boolean _isskillHardDelay = false;

	   /**
	    * 設定技能動作延遲中
	    * 
	    * @param flag
	    *            true:是 false:否
	    */
	   public void setskillHardDelay(final boolean flag) {
	       _isskillHardDelay = flag;
	       if (flag == true) {
	       	skillHardDelay.onHardUse(this);
	       }
	   }

	   /**
	    * 是否技能動作延遲中
	    * 
	    * @return true:是 false:否
	    */
	   public boolean isskillHardDelay() {
	       return _isskillHardDelay;
	   }
	   protected final L1HateList _hateList = new L1HateList();// 目標清單
	   private boolean _firstAttack = false; 
	   protected NpcMoveExecutor _pcMove = null;// XXX
	   private int move = 0;
	   /**
	    * 啟用PC AI
	    */
	   public synchronized void startAI() {
	       if (this.isDead()) {
	           return;
	       }
	       if (this.isGhost()) {
	           return;
	       }
	       if (this.getCurrentHp() <= 0) {
	           return;
	       }
	       if (this.isPrivateShop()) {
	       	return;
	       }
	       if (this.isParalyzed()) {
	       	return;
	       }

	       if (_pcMove != null) {
	       	_pcMove = null;
	       }
	       _pcMove = new pcMove(this);
	       this.setAiRunning(true);
	       this.setActived(true);
	       final PcAI npcai = new PcAI(this);
	       npcai.startAI();
	   }

	   private boolean _aiRunning = false; //PC AI時間軸 正在運行 
	   /**
	    * PC AI時間軸 正在運行
	    * 
	    * @param aiRunning
	    */
	   protected void setAiRunning(final boolean aiRunning) {
	       this._aiRunning = aiRunning;
	   }

	   /**
	    * PC AI時間軸 正在運行
	    * 
	    * @return
	    */
	   protected boolean isAiRunning() {
	       return this._aiRunning;
	   }

	   /**
	    * 清除全部目標
	    */
	   public void allTargetClear() {
	       // XXX
	       if (_pcMove != null) {
	           _pcMove.clear();
	       }
	       _hateList.clear();
	       _target = null;
	       setFirstAttack(false);
	   }

	   /**
	    * 清除單個目標
	    */
	   public void targetClear() {
	   	if (_target == null) {
	   		return;
	   	}
	   	_hateList.remove(_target);
	   	_target = null;
	   }

	   /**
	    * 有效目標檢查
	    */
	   public void checkTarget() {
	       try {
	           if (_target == null) {// 目標為空
	           	//targetClear();
	               return;
	           }
	           if (_target.getMapId() != getMapId()) {// 目標地圖不相等
	           	targetClear();
	               return;
	           }
	           if (_target.getCurrentHp() <= 0) {// 目標HP小於等於0
	           	targetClear();
	               return;
	           }
	           
	           if (_target.isDead()) {// 目標死亡
	           	targetClear();
	               return;
	           }
	           if (get_showId() != _target.get_showId()) {// 副本ID不相等
	           	targetClear();
	               return;
	           }
	           if (!_hateList.containsKey(_target)) {// 目標不在已有攻擊清單中
	           	targetClear();
	               return;
	           }
	           final int distance = getLocation().getTileDistance(
	                   _target.getLocation());
	           if (distance > 15) {
	           	targetClear();
	               return;
	           }

	       } catch (final Exception e) {
	           return;
	       }
	   }
	      
	   /**
	    * 現在目標
	    */
	   public L1Character is_now_target() {
	       return _target;
	   }

	   /**
	    * 對目標進行攻擊
	    * 
	    * @param target
	    */
	   public void attackTarget(final L1Character target) { 
	   	
	       if (this.getInventory().getWeight182() >= 197) { // 重量過重
	           // 110 \f1當負重過重的時候，無法戰鬥。
	           this.sendPackets(new S_ServerMessage(110));
	           // _log.error("要求角色攻擊:重量過重");
	           return;
	       }
	       if (getlimao()==1&&_target.getgongji()>0&&_target.getgongji()!=this.getId()) {// 目標不在已有攻擊清單中
	       	targetClear();
	           return;
	       }
	       if (target instanceof L1PcInstance) {
	           final L1PcInstance player = (L1PcInstance) target;
	           if (player.isTeleport()) { // 處理中
	               return;
	           }
	           if (!player.isPinkName()) {
	           	this.allTargetClear();
	           	return;
	           }

	       } else if (target instanceof L1PetInstance) {
	           final L1PetInstance pet = (L1PetInstance) target;
	           final L1Character cha = pet.getMaster();
	           if (cha instanceof L1PcInstance) {
	               final L1PcInstance player = (L1PcInstance) cha;
	               if (player.isTeleport()) { // 處理中
	                   return;
	               }
	           }

	       } else if (target instanceof L1SummonInstance) {
	           final L1SummonInstance summon = (L1SummonInstance) target;
	           final L1Character cha = summon.getMaster();
	           if (cha instanceof L1PcInstance) {
	               final L1PcInstance player = (L1PcInstance) cha;
	               if (player.isTeleport()) { // 處理中
	                   return;
	               }
	           }
	       }


	       if (target instanceof L1NpcInstance) {
	           final L1NpcInstance npc = (L1NpcInstance) target;
	           if (npc.getHiddenStatus() != 0) { // 地中潛、飛
	               this.allTargetClear();
	               return;
	           }
	       }
	       if (target.getCurrentHp() > 0 && !target.isDead()) {
	       target.onAction(this);        
	       }
	   	if(this.getsanchong() == 1){
	   	if (URandom.nextInt(100) <= 20) {
	   		if (this.isElf()){
	   			if (this.getCurrentMp() <= 15) {
	   				return;
	   			}
	   			if (CharSkillReading.get().spellCheck(this.getId(), 132)){
	   				// 可施放外型限制
	   				boolean gfxcheck = false;
	   				final int playerGFX = this.getTempCharGfx();
	   				switch (playerGFX) {
	   				case 138:
	   				case 37:
	   				case 3126:
	   				case 3420:
	   				case 3105:
	   				case 3145:
	   				case 3148:
	   				case 3151:
	   				case 4125:
	   				case 4950:
	   				case 6826:case 6827:case 6836:case 6837:case 6846:case 6847:case 6856:case 6857:
	   				case 6866:case 6867:case 6876:case 6877:case 6886:case 6887:case 8842:case 8845:
	   				case 7959:case 7967:case 7968:case 7969:case 7970:
	   				case 3860:
	   				case 3871:
	   				case 2323:
	   				case 2284:
	   				case 4918:
	   				case 4917:
	   				case 4919:
	   				case 3892:
	   				case 3895:
	   				case 3898:
	   				case 3901:
	   				case 4190:
	   				case 6140:
	   				case 6269:
	   				case 6145:
	   				case 6272:
	   				case 6150:
	   				case 6275:
	   				case 6155:
	   				case 6278:
	   				case 6160:
	   				case 6087:
	   				case 8900:
	   				case 9225:
	   				case 8913:
	   				case 9226:
	   				case 8860:
	   				case 8786:
	   				case 8792:
	   				case 8798:
	   				case 8804:
	   				case 8808:
	   				// 姬舞者
	   				case 10283:
	   				case 10286:
	   				case 10275:
	   				case 11342:
	   				case 11351:
	   				case 11363:
	   				case 11369:
	   				case 11378:
	   				case 11382:
	   				case 11386:
	   				case 11406:
	   				case 11402:
	   				case 13631:
	   				case 13635:
	   				case 13388:
	   				case 11331:
	   				case 11390:
	   				case 11394:		
	   					gfxcheck = true;
	   					break;
	   				}
	   				if (!gfxcheck){
	   					return;
	   				}
	   				this.setCurrentMp(this.getCurrentMp() - 15);
	   				for (int i = 0; i < 3; i ++) {
	   					if (target.getCurrentHp() > 0 && !target.isDead()) {
	   						target.onAction(this);
	   					} else {
	   						break;
	   					}
	   				}						
	   				this.sendPacketsX8(new S_SkillSound(this.getId(), 4394)); // 三重矢 加速封包
	   			}
	   		   }
	   	      }		
	   		} else if (this.isWizard()||this.isElf()){
	   			if (this.getCurrentMp() <= this.getMaxMp()*50/100) {
	   				return;
	   			}				
	   			if(this.getlieyang() == 1){
	   			if (getWeapon().getItem().getType1()!=20){	
	   			if (URandom.nextInt(100) <= 40) {
	   			if (CharSkillReading.get().spellCheck(this.getId(), 46)){
	   				this.setCurrentMp(this.getCurrentMp() - 20);
	   				final L1SkillUse skilluse = new L1SkillUse();
	   				skilluse.handleCommands(
	   						this, 
	   						45, 
	   						target.getId(), 
	   						target.getX(), 
	   						target.getY(), 
	   						// message, 
	   						0, 
	   						L1SkillUse.TYPE_NORMAL
	   						);
	   				}				     
	   	         }
	       	   }
	   		}
	   	  if(this.getguanj() == 1){		 
	   		if (CharSkillReading.get().spellCheck(this.getId(), 4)){
	   		final L1SkillUse skilluse = new L1SkillUse();
	   		skilluse.handleCommands(
	   				this, 
	   				4, 
	   				target.getId(), 
	   				target.getX(), 
	   				target.getY(), 
	   				// message, 
	   				0, 
	   				L1SkillUse.TYPE_NORMAL
	   				);				
	           }	  
	          }
	         }		
	   	  else if (this.isKnight()){ //龍騎自動放屠宰者
	   		if (this.getCurrentHp() <= this.getMaxHp()*30/100) {
	   			return;
	   		}
	   		if(this.getgjslz() == 1){
	   		if (URandom.nextInt(100) <= 30) {
	   		if (CharSkillReading.get().spellCheck(this.getId(), 187)){
	   			this.setCurrentHp(this.getCurrentHp() - 15);

	 
	   				} else {
	   			
	   				}
	   			}
	   			// 屠宰者 加速封包
	   			this.sendPacketsX10(new S_SkillSound(this.getId(), 7020));
	   			this.sendPacketsX10(new S_SkillSound(this.getId(), 6509));
	   		   }
	   	      }	
	   	     }
	   	    	   
	           
	   public void searchTarget() {
	       // 攻擊目標搜尋
//	   	System.out.println("AI啟動44444");
//	   	L1MonsterInstance targetPlayer = searchTarget(this);
//	   	System.out.println("AI啟動666==" + targetPlayer);
//	       if (targetPlayer != null) {
//	           _hateList.add(targetPlayer, 0);
//	           _target = targetPlayer;
	   //
//	       }
	       final Collection<L1Object> allObj = World.get()
	               .getVisibleObjects(this, 15);
	       for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();) {
	           final L1Object obj = iter.next();
	           if (!(obj instanceof L1MonsterInstance)) {
	           	continue;
	           }
	           final L1MonsterInstance mob = (L1MonsterInstance) obj;
	       	if (mob.isDead()) {
	       		continue;
	       	}
	           if (mob.getCurrentHp() <= 0) {
	               continue;
	           }
	           if (mob.getHiddenStatus() > 0) {
	           	continue;                	
	           }
	           if (mob.getAtkspeed() == 0) {
	           	continue;
	           }
	           if (mob.hasSkillEffect(this.getId() + 100000)
	           		&& !this.isAttackPosition(mob.getX(), mob.getY(), 1)) {
	           	continue;
	           }
//	           if (mob.is_now_target() != null 
//	           	&& mob.is_now_target() != this
//	           	&& mob.isAttackPosition(mob.is_now_target().getX(), mob.is_now_target().getY(), mob.get_ranged())) { // 試試不搶怪模式
//	           	continue;
//	           }
	           if (mob != null) {
	               final int Distance = 15 - this.getTileLineDistance(mob);
//	               if (this.glanceCheck(mob.getX(), mob.getY())) {
//	               	Distance = + 10;
//	               }
	           	_hateList.add(mob, Distance);

	           }
	       }
	       _target = _hateList.getMaxHateCharacter();
//	   	System.out.println("AI啟動666 _target==:" + _target);
	   	if (move>=50) { //地圖可瞬移身上有白瞬卷 QQ:759347094
	   		L1Teleport.randomTeleport(this, true);
	   		move=0;
	   		try {
	   			Thread.sleep(500);
	   		} catch (InterruptedException e) {
	   			// TODO 自動生成的 catch 塊
	   			e.printStackTrace();
	   		}
	        }
	   	if (_target == null&&getguajikg() == 1) { //如果目標等於空

	   		//等待處理，，瞬移。。等等設置
	   		if (this.getMap().isTeleportable() 
	   			&& this.getInventory().consumeItem(40100, 1)) { //地圖可瞬移身上有白瞬卷 QQ:759347094
	   			L1Teleport.randomTeleport(this, true);
	   			try {
	   				Thread.sleep(500);
	   			} catch (InterruptedException e) {
	   				// TODO 自動生成的 catch 塊
	   				e.printStackTrace();
	   			}
	   		}
	   		if (this.getInventory().checkItem(40100)) { //地圖可瞬移身上有白瞬卷 QQ:759347094
	       	    L1Teleport.randomTeleport(this, true);
	       	    try {
	   				Thread.sleep(500);
	   			} catch (InterruptedException e) {
	   				// TODO 自動生成的 catch 塊
	   				e.printStackTrace();
	   			}
	           }
	   		if (this.getInventory().checkItem(99257)) { //地圖可瞬移身上有白瞬卷 QQ:759347094
	       	    L1Teleport.randomTeleport(this, true);
	       	    try {
	   				Thread.sleep(500);
	   			} catch (InterruptedException e) {
	   				// TODO 自動生成的 catch 塊
	   				e.printStackTrace();
	   			}
	           }
	   	}
	   	allObj.clear();
	   }

	   //private L1MonsterInstance searchTarget(L1PcInstance pc) {
//	   	System.out.println("AI啟動55555");
//	   	L1MonsterInstance targetPlayer = null;
	   //	
//	       for (final L1Object npc : World.get().getVisibleObjects(pc)) {
//	           try {
//	               Thread.sleep(10);
//	           } catch (InterruptedException e) {
//	               _log.error(e.getLocalizedMessage(), e);
//	           }
//	           if (npc instanceof L1MonsterInstance) {
//	           	final L1MonsterInstance mob = (L1MonsterInstance) npc;
//	           	if (mob.isDead()) {
//	           		continue;
//	           	}
//	               if (mob.getCurrentHp() <= 0) {
//	                   continue;
//	               }
//	               if (mob.getHiddenStatus() > 0) {
//	               	continue;
//	               }
//	               if (mob.getAtkspeed() == 0) {
//	               	continue;
//	               }
//	               if(mob.hasSkillEffect(this.getId() + 100000)) {//暫不攻擊狀態 QQ:759347094
//	               	continue;
//	               }
//	               	
//	               targetPlayer = mob;
//	           }
//	       }
//	       return targetPlayer;
	   //}

	   /**
	    * 具有目標的處理 (攻擊的判斷)
	    */
	   public void onTarget() {
	       try {
	           // System.out.println("具有目標的處理");
//	           setActived(true);

	           // 先_target變影響出別領域參照確保
	           final L1Character target = _target;


	           if (target == null) {
	           	return;
	           }
	           attack(target);
	           

	       } catch (final Exception e) {
	           _log.error(e.getLocalizedMessage(), e);
	       }
	   }

	   private void attack(L1Character target) {
	       // 攻擊可能位置
	   	int attack_Range = 1;
	   	if (this.getWeapon() != null) {
	   		attack_Range = this.getWeapon().getItem().getRange();
	   	}
	   	if (attack_Range < 0) {
	   		attack_Range = 15;
	   	}
	       if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// 已經到達可以攻擊的距離
	           setHeading(targetDirection(target.getX(), target.getY()));
	           attackTarget(target);
	           move = 0;
	           // XXX
	           if (_pcMove != null) {
	               _pcMove.clear();
	           }

	       } else { // 攻擊不可能位置
//	               final int distance = getLocation().getTileDistance(
//	                       target.getLocation());
	               if (_pcMove != null) {
	                   final int dir = _pcMove.moveDirection(target.getX(),
	                           target.getY());
	                   if (dir == -1) {                    	
	                   	_target.setSkillEffect(this.getId() + 100000, 20000);//給予20秒狀態
	                   	targetClear();

	                   } else {
	                       _pcMove.setDirectionMove(dir);                         
//	                       setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
	                   }
	               }
	       }
	   }


	   private boolean _actived = false; // 掛機激活
	   private boolean _Pathfinding = false; //尋路中.. QQ:759347094
	   /**
	    * PC已經激活
	    * 
	    * @param actived
	    *            true:激活 false:無
	    */
	   public void setActived(final boolean actived) {
	       this._actived = actived;
	   }

	   /**
	    * PC已經激活
	    * 
	    * @return true:激活 false:無
	    */
	   public boolean isActived() {
	       return this._actived;
	   }

	   protected void setFirstAttack(final boolean firstAttack) {
	       this._firstAttack = firstAttack;
	   }

	   protected boolean isFirstAttack() {
	       return this._firstAttack;
	   }

	   /**
	    * 攻擊目標設置
	    * 
	    * @param cha
	    * @param hate
	    */
	   public void setHate(final L1Character cha, int hate) {
	       try {
	           if ((cha != null) && /*(cha.getId() != getId())*/ _target != null) {
	               if (!isFirstAttack() && (hate > 0)) {
	                   //hate += getMaxHp() / 10; // ＦＡ
	                   setFirstAttack(true);
	                   if (_pcMove != null) {
	                       _pcMove.clear();// XXX
	                   }
	                   //System.out.println("isFirstAttack=" + isFirstAttack());
	                   _hateList.add(cha, 5);
	                   _target = _hateList.getMaxHateCharacter();
	                   checkTarget();
	               }
	           }

	       } catch (final Exception e) {
	           return;
	       }
	   }
	   /**
	    * 目標為空掛機尋路中
	    * @return
	    */
	   public boolean isPathfinding() {
	   	return this._Pathfinding;
	   }
	   public void setPathfinding(final boolean fla) {
	   	this._Pathfinding = fla;
	   }
	   // 隨機移動距離
	   //private int _randomMoveDistance = 0;
	   // 隨機移動方向
	   private int _randomMoveDirection = 0;
	   public int getrandomMoveDirection() {
	   	return _randomMoveDirection;
	   }

	   public void setrandomMoveDirection(int randomMoveDirection) {
	   	this._randomMoveDirection = randomMoveDirection;
	   }

	   /**
	    * 沒有目標的處理 (傳回本次AI是否執行完成)<BR>
	    * 具有主人 跟隨主人移動
	    * 
	    * @return true:本次AI執行完成 <BR>
	    *         false:本次AI執行未完成
	    */
	   public void noTarget() {
	       // 如果移動距離已經為0 重新定義隨機移動
//	       if (_randomMoveDistance == 0) {
//	           // 產生移動距離
//	           _randomMoveDistance = _random.nextInt(3) + 1;

	           // 產生移動方向(隨機數值超出7物件會暫停移動)
//	           _randomMoveDirection = _random.nextInt(8);
//	           if (_randomMoveDirection < 8) {
//	           	
//	           }
	   //
//	       } else {
//	           _randomMoveDistance--;
//	       }
	   	if (!_Pathfinding) {
	       	_Pathfinding = true; //設置尋路中 
	   	}
	   	if (_randomMoveDirection > 7) {
	   		_randomMoveDirection = 0;
	   	}
	       //System.out.println("_randomMoveDirection=:" + _randomMoveDirection);
	       if (_pcMove != null) {
	           if (getrandomMoveDirection() < 8) {
	               int dir = _pcMove
	                       .checkObject(_randomMoveDirection);
	               dir = _pcMove.openDoor(dir);

	               if (dir != -1) {
	                   _pcMove.setDirectionMove(dir); 
	                   move++;
	               } else {
	               	_randomMoveDirection = _random.nextInt(8); 
	               	move++;
	               }
	           }
	       }
	   }
    
   
    // 20171122 墓碑
    private L1EffectInstance _tomb;

 	public void set_tomb(L1EffectInstance tomb) {
 		_tomb = tomb;
 	}
 	public L1EffectInstance get_tomb() {
 		return _tomb;
 	} 	
 	int _deathchance = 0; 	  
 	   public void addDeathChance(int i) {
 	     this._deathchance += i;
 	  } 	   
 	  public int getDeathChance() {
 	   return this._deathchance;
 	  } 	   
 	  int _dmg00 = 0; 	   
 	   public void adddmg00(int i)  {
 	     this._dmg00 += i;
 	  } 	  
      public int getdmg00() {
 	     return this._dmg00;
 	  }   
 	  int _Chp = 0; 	  
      public void setChp(int i){
 	     this._Chp = i;
 	  } 	   
      public int getChp()  {
 	     return this._Chp;
 	  } 	  
 	  int _Cmp = 0; 	  
 	  public void setCmp(int i)  {
      this._Cmp = i;
 	  } 	  
 	   public int getCmp() {
 	    return this._Cmp;
 	  } 	   
      int _punch = 0;   
 	  public void adddmg2(int i) {
      this._punch += i;
 	  } 	   
      public int getPunch() {
      return this._punch;
   }
  	// 大声公
  	private boolean _isBigChat = false;
  	public boolean isBigChat() {
  		return _isBigChat;
  	}
  	public void setBigChat(boolean flag) {
  		_isBigChat = flag;
  	}
  	/**
	 * 領取寵物用.
	 * 
	 * @param itemObjectId
	 *            - 寵物項圈唯一編號
	 * @return 如果成功領取則返回true
	 */
	public boolean petReceive(int itemObjectId) {
		// 不准攜帶寵物的地圖
		if (!getMap().isTakePets()) {
			sendPackets(new S_ServerMessage(563)); // \f1你無法在這個地方使用。
			return false;
		}

		int petCost = 0;
		int petCount = 0;
		int divisor = 6;
		final Object[] petList = getPetList().values().toArray();

//		// 可攜帶寵物最大數量(上限)
//		if (petList.length > 4) {
//			sendPackets(new S_ServerMessage(489)); // 你無法一次控制那麼多寵物。
//			return false;
//		}
		// for (final Object pet : petList) {
		// petCost += ((L1NpcInstance) pet).getPetcost();
		// }
		for (final Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) {
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}

		int charisma = getCha();
//		// 各職業額外獎勵
//		if (isCrown()) { // 王
//			charisma += 6;
//		} else if (isKnight()) { // 騎
//			charisma += 0;
//		} else if (isElf()) { // 妖
//			charisma += 12;
//		} else if (isWizard()) { // 法
//			charisma += 6;
//		} else if (isDarkelf()) { // 黑
//			charisma += 6;
//		}

		final L1Pet l1pet = PetReading.get().getTemplate(itemObjectId);
		//final int npc = l1pet.get_npcid();
	//	if (isElf()) {
		/*	if (npc == 45313 || npc == 45710) {
				charisma += 6;
			}
		}*/

		if (l1pet != null) {
			final int npcId = l1pet.get_npcid();
			charisma -= petCost;
			if ((npcId == 45313) || (npcId == 45710) // 虎男、高等斗虎
					|| (npcId == 45711) || (npcId == 45712)) { // 高麗幼犬、高麗犬
				divisor = 3;
			} else {
				divisor = 3;
			}

			petCount = charisma / divisor;

			if (petCount <= 0) {
				sendPackets(new S_ServerMessage(489)); // 你無法一次控制那麼多寵物。
				return false;
			}

			final L1Npc npcTemp = NpcTable.get().getTemplate(npcId);
			final L1PetInstance pet = new L1PetInstance(npcTemp, this, l1pet);
			pet.setPetcost(divisor);
			return true;
		}
		return false;
	}
	private int _fwgj;
 	public void set_fwgj(final int fwgj) {
 		_fwgj = fwgj;
 	}

 	public int get_fwgj() {
 		return _fwgj;
 	}
	// TODO 道具合成系統
	private String _craftkey = null;

	public void set_craftkey(String craftkey) {
		_craftkey = craftkey;
	}

	public String get_craftkey() {
		return _craftkey;
	}
	private double _dmgbl = 1.0D;// 经验值增加

	/**
	 * 武器加成攻击倍率
	 * 
	 * @param d
	 */
	public void setdmgbl(double d) {
		_dmgbl = d;
	}

	/**
	 * 武器加成攻击倍率
	 * 
	 * @return
	 */
	public double getdmgbl() {
		return _dmgbl;
	}

	private MEFAntiBotExecute _showGm; // 監測掛機的虛擬GM

	/**
	 * 設置監測掛機的臨時虛擬GM
	 *
	 * @param showGm
	 */
	public void set_showGm(final MEFAntiBotExecute showGm) {
		this._showGm = showGm;
	}

	/**
	 * 傳回監測掛機的臨時虛擬GM
	 *
	 * @return
	 */
	public MEFAntiBotExecute get_showGm() {
		return this._showGm;
	}

	/**
	 * 回答外掛檢測錯誤次數
	 */
	private int Chack_game = 0;

	public int getChack_game() {
		return Chack_game;
	}

	public void setChack_game(int chack_game) {
		Chack_game = chack_game;
	}
	private long check_plugin_tick = 0;
	public final long getCheckPluginTick() {
		return this.check_plugin_tick;
	}
	public final void setCheckPluginTick() {
		this.check_plugin_tick = System.currentTimeMillis();
	}

	private L1Object _lastAttackObj; // 上一次被PC攻擊的物件

	/**
	 * 獲取上一次被角色攻擊的物件
	 *
	 * @return
	 */
	public L1Object get_lastAttackObj() {
		return _lastAttackObj;
	}
	private int _BossSeachPage = 0;

	/**
	 * BOSS查詢
	 *
	 * @return
	 */
	public int getBossSeachPage() {
		return _BossSeachPage;
	}

	/**
	 * BOSS查詢
	 *
	 * @param i
	 */
	public void setBossSeachPage(int i) {
		_BossSeachPage = i;
	}
}

