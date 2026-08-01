package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.lineage.config.ConfigOther;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1CrownInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1DwarfInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1HousekeeperInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.Instance.L1TeleporterInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_Chat;
import com.lineage.server.serverpackets.S_Dexup;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_RangeSkill;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShowPolyList;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_PacketBoxIconAura;
import com.lineage.server.serverpackets.S_SkillIconShield;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.serverpackets.S_Strup;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldTrap;
import com.lineage.server.world.WorldWar;

/**
 * 技能施放判斷
 * @author dexc
 *
 */
public class L1SkillUse {

	private static final Log _log = LogFactory.getLog(L1SkillUse.class);

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOGIN = 1;
	public static final int TYPE_SPELLSC = 2;
	public static final int TYPE_NPCBUFF = 3;
	public static final int TYPE_GMBUFF = 4;

	private L1Skills _skill;
	private int _skillId;
	private int _getBuffDuration;// 技能時間
	private int _shockStunDuration;
	private int _getBuffIconDuration;// 技能圖示時間
	private int _targetID;
	private int _mpConsume = 0;
	private int _hpConsume = 0;
	private int _targetX = 0;
	private int _targetY = 0;
	private int _dmg = 0;// 傷害
	private int _skillTime = 0;
	private int _type = 0;
	private boolean _isPK = false;
	private int _bookmarkId = 0;
	private int _itemobjid = 0;
	private boolean _checkedUseSkill = false; // 事前濟
	private int _leverage = 10; // 1/10倍101倍
	private boolean _isFreeze = false;
	private boolean _isCounterMagic = true;
	private boolean _isGlanceCheckFail = false;

	/**執行者*/
	private L1Character _user = null;

	/**執行者為pc*/
	private L1PcInstance _player = null;
	
	/**執行者為npc*/
	private L1NpcInstance _npc = null;
	
	/**目標*/
	private L1Character _target = null;
	
	/**目標為NPC*/
	private L1NpcInstance _targetNpc = null;

	private int _calcType;
	private static final int PC_PC = 1;
	private static final int PC_NPC = 2;
	private static final int NPC_PC = 3;
	private static final int NPC_NPC = 4;

	private ArrayList<TargetStatus> _targetList;

	private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12,
		13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44/*, 46*/ ,48, 49, 52, 54, 55,
		57, 60, 61, 63,65, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN,
		REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
		97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
		114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146,
		147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164, 165,
		166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE,
		10026, 10027, 10028, 10029 };

	/**
	 * 攻擊倍率(1/10)
	 */
	public void setLeverage(final int i) {
		this._leverage = i;
	}

	/**
	 * 攻擊倍率(1/10)
	 * @return
	 */
	public int getLeverage() {
		return this._leverage;
	}

	private boolean isCheckedUseSkill() {
		return this._checkedUseSkill;
	}

	private void setCheckedUseSkill(final boolean flg) {
		this._checkedUseSkill = flg;
	}

	/**
	 * 
	 * @param player 攻擊者為PC
	 * @param skillid 技能編號
	 * @param target_id 目標OBJID
	 * @param x X座標
	 * @param y Y座標
	 * @param time 時間
	 * @param type 類型
	 * @param attacker 攻擊者為NPC
	 * @return
	 */
	public boolean checkUseSkill(final L1PcInstance player, final int skillid,
			final int target_id, final int x, final int y, final int time, final int type,
			final L1Character attacker) {
		// 初期設定
		setCheckedUseSkill(true);
		_targetList = new ArrayList<TargetStatus>(); // 初期化

		_skill = SkillsTable.get().getTemplate(skillid);
		if (_skill == null) {
			return false;
		}
		_skillId = skillid;
		_targetX = x;
		_targetY = y;
		_skillTime = time;
		_type = type;
		boolean checkedResult = true;

		if (attacker == null) {
			// pc
			_player = player;
			_user = _player;
			
		} else {
			// npc
			_npc = (L1NpcInstance) attacker;
			_user = _npc;
		}

		if (_skill.getTarget().equals("none")) {
			_targetID = _user.getId();
			_targetX = _user.getX();
			_targetY = _user.getY();
			
		} else {
			_targetID = target_id;
		}

		switch (type) {
		case TYPE_NORMAL: // 通常魔法使用時
			checkedResult = this.isNormalSkillUsable();
			break;
			
		case TYPE_SPELLSC: // 使用時
			/*checkedResult = this.isSpellScrollUsable();
			break;*/
			
		case TYPE_NPCBUFF:
			checkedResult = true;
			break;
		}
		
		if (!checkedResult) {
			return false;
		}

		// 、詠唱對像座標
		// 詠唱者座標配置例外
		// id58火牢 id63治癒能量風暴
		if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM)) {
			return true;
		}

		final L1Object object = World.get().findObject(_targetID);
		if (object instanceof L1ItemInstance) {
			return false;
		}
		if (_user instanceof L1PcInstance) {
			if (object instanceof L1PcInstance) {
				_calcType = PC_PC;
				
			} else {
				_calcType = PC_NPC;
				_targetNpc = (L1NpcInstance) object;
			}
			
		} else if (_user instanceof L1NpcInstance) {
			if (object instanceof L1PcInstance) {
				_calcType = NPC_PC;
				
			} else if (_skill.getTarget().equals("none")) {
				_calcType = NPC_PC;
				
			} else {
				_calcType = NPC_NPC;
				_targetNpc = (L1NpcInstance) object;
			}
		}

		switch (_skillId) {
		// 可使用傳送戒指技能
		case TELEPORT:
		case MASS_TELEPORT:
			_bookmarkId = target_id;
			break;
		
		// 技能對像為道具
		case CREATE_MAGICAL_WEAPON:
		case BRING_STONE:
		case BLESSED_ARMOR:
		case ENCHANT_WEAPON:
		case SHADOW_FANG:
			_itemobjid = target_id;
			break;
		}
		
		_target = (L1Character) object;

		if (!(_target instanceof L1MonsterInstance)
				&& _skill.getTarget().equals("attack")
				&& (_user.getId() != target_id)) {
			_isPK = true; // 以外攻擊系、自分以外場合PK。
		}

		// 初期設定

		// 事前
		if (!(object instanceof L1Character)) { // 以外場合何。
			checkedResult = false;
		}
		
		// 技能發動 目標清單判定
		makeTargetList();
		
		if ((_targetList.size() == 0) 
				&& (_user instanceof L1NpcInstance)) {
			checkedResult = false;
		}
		// 事前
		return checkedResult;
	}

	/**
	 * 通常使用時使用者狀態使用可能判斷
	 *
	 * @return false 使用不可能狀態場合 在以下情況下不可使用技能
	 */
	private boolean isNormalSkillUsable() {
		// 使用者PC場合
		if (this._user instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) this._user;

			if (!this.isAttrAgrees()) { // 精靈魔法、屬性一致何。
				return false;
			}

			if ((this._skillId == ELEMENTAL_PROTECTION) && (pc.getElfAttr() == 0)) {
				pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失敗。
				return false;
			}

			// DIG使用可
			if ((this._skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
				// 未確認
				pc.sendPackets(new S_ServerMessage(352, "$967")); // 魔法利用性向值%0。
				return false;
			}

			if ((this.isItemConsume() == false) && !this._player.isGm()) { // 消費
				this._player.sendPackets(new S_ServerMessage(299)); // \f1施放魔法所需材料不足。
				return false;
			}
		}
		// 使用者NPC場合
		else if (this._user instanceof L1NpcInstance) {

			// 狀態使用不可
			if (this._user.hasSkillEffect(SILENCE)) {
				// NPC掛場合1回使用效果。
				this._user.removeSkillEffect(SILENCE);
				return false;
			}
		}

		// PC、NPC共通
		if (!this.isHPMPConsume()) { // 消費HP、MP
			return false;
		}
		return true;
	}

	/**
	 * pc 用技能施放判斷
	 * @param player
	 * @param skillId
	 * @param targetId
	 * @param x
	 * @param y
	 * @param timeSecs 秒
	 * @param type
	 */
	public void handleCommands(final L1PcInstance player, final int skillId, final int targetId,
			final int x, final int y, final int timeSecs, final int type) {
		this.handleCommands(player, skillId, targetId, x, y, timeSecs, type, null);
	}

	/**
	 * 通用技能施放判斷
	 * @param player
	 * @param skillId
	 * @param targetId
	 * @param x
	 * @param y
	 * @param timeSecs
	 * @param type
	 * @param attacker
	 */
	public void handleCommands(final L1PcInstance player, final int skillId, final int targetId,
			final int x, final int y, final int timeSecs, final int type,
			final L1Character attacker) {
		try {
			// 事前？
			if (!isCheckedUseSkill()) {
				final boolean isUseSkill = 
					checkUseSkill(
							player, 
							skillId, 
							targetId, 
							x, 
							y, 
							timeSecs, 
							type, 
							attacker
							);
				
				if (!isUseSkill) {
					failSkill();
					return;
				}
			}
			switch (type) {
			case TYPE_NORMAL: // 魔法詠唱時
				if (!_isGlanceCheckFail || 
						(_skill.getArea() > 0) || 
						_skill.getTarget().equals("none")) {
					if ((_skill.getType() == L1Skills.TYPE_ATTACK) && _targetList.isEmpty() && skillId != 70 && skillId != 59 && skillId != 53 && skillId != 62) {
							final L1PcInstance pc = (L1PcInstance) _user;
							pc.sendPackets(new S_ServerMessage("距離太遠了"));
					} else {
						if (_skillId == SHOCK_STUN && _targetList.isEmpty()) {
							break;
						}
						runSkill();
						useConsume();
						if (player != null && player.hasSkillEffect(attack_no_spr)) {
							sendGrfx(false);
						} else {
							sendGrfx(true);
						}
						sendFailMessageHandle();
						setDelay();
					}
				}
				break;
				
			case TYPE_LOGIN: // 時（HPMP材料消費、）
				runSkill();
				break;

			case TYPE_SPELLSC: // 使用時（HPMP材料消費）
				runSkill();
				if (player != null && player.hasSkillEffect(attack_no_spr)) {
					sendGrfx(false);
				} else {
					sendGrfx(true);
				}
				break;

			case TYPE_GMBUFF: // GMBUFF使用時（HPMP材料消費、魔法）
				runSkill();
				sendGrfx(false);
				break;

			case TYPE_NPCBUFF: // NPCBUFF使用時（HPMP材料消費）
				runSkill();
				sendGrfx(true);
				break;
			}
			setCheckedUseSkill(false);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 法術施展失敗的處理
	 */
	private void failSkill() {
		setCheckedUseSkill(false);
		switch (_skillId) {
		case TELEPORT:
		case MASS_TELEPORT:
		case TELEPORT_TO_MATHER:
			// 解除傳送鎖定
			_player.sendPackets(
					new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			break;
		}
	}

	/**
	 * 可加入設置目標的判斷
	 * @param cha 加入判斷的目標物件
	 * @return true:可加入目標 false:不可加入目標
	 * @throws Exception
	 */
	private boolean isTarget(final L1Character cha) throws Exception {
		if (cha == null) {
			return false;
		}
		
		// 副本ID不相等
		if (_user.get_showId() != cha.get_showId()) {
			return false;
		}
		
		if (_npc != null) {
			// 在目標清單中
			if (_npc.isHate(cha)) {
				return true;
			}
			// 施展者是寵物 XXX
			if (_npc instanceof L1PetInstance) {
				if (cha instanceof L1MonsterInstance) {
					return true;
				}
			}
			// 施展者是召喚獸
			if (_npc instanceof L1SummonInstance) {
				if (cha instanceof L1MonsterInstance) {
					return true;
				}
			}
		}
		
		// 該物件是否允許攻擊
		if (!CheckUtil.checkAttackSkill(cha)) {
			return false;
		}

		boolean flg = false;

		// 目標是門
		if (cha instanceof L1DoorInstance) {
			// 目標不可破壞設置
			if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
				return false;
			}
		}

		// 目標是魔法娃娃 拒絕所有技能
		if (cha instanceof L1DollInstance) {
			return false;
		}
		
		// 目標是人物
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			// 鬼魂模式 以及 GM隱身
			if (pc.isGhost() || pc.isGmInvis()) {
				return false;
			}
		}
		
		// NPC 對 PC
		if (_calcType == NPC_PC) {
			if ((cha instanceof L1PcInstance) || 
					(cha instanceof L1PetInstance) || 
					(cha instanceof L1SummonInstance)) {
				flg = true;
			}
		}

		// PC 對 NPC
		if (_calcType == PC_NPC) {
			// 判斷目標為人物
			if (cha instanceof L1PcInstance) {
				// 位於安全區域中
				if (cha.isSafetyZone()) {
					return false;
				}
			}
		}
		
		// 元Pet、Summon以外NPC場合、PC、Pet、Summon對像外
		if ((_calcType == PC_NPC)
				// 目標是NPC
				&& (_target instanceof L1NpcInstance)
				// 不能是寵物
				&& !(_target instanceof L1PetInstance)
				// 不能是召喚獸
				&& !(_target instanceof L1SummonInstance)
				&& ((cha instanceof L1PetInstance)
						|| (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PcInstance))) {
			return false;
		}

		// 元以外NPC場合、對像外
		if ((_calcType == PC_NPC) && 
				(_target instanceof L1NpcInstance) && 
				!(_target instanceof L1GuardInstance) && 
				(cha instanceof L1GuardInstance)) {
			return false;
		}

		// NPC對PC場合。
		if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))
				&& (_calcType == NPC_PC)
				&& !(cha instanceof L1PetInstance)
				&& !(cha instanceof L1SummonInstance)
				&& !(cha instanceof L1PcInstance)) {
			return false;
		}

		// NPC對NPC使用者MOB、MOB場合。
		if ((_skill.getTarget().equals("attack") || (_skill.getType() == L1Skills.TYPE_ATTACK))
				&& (_calcType == NPC_NPC)
				&& (_user instanceof L1MonsterInstance)
				&& (cha instanceof L1MonsterInstance)) {
			return false;
		}

		// 無方向範圍攻擊魔法攻擊NPC對像外
		if (_skill.getTarget().equals("none")
				&& (_skill.getType() == L1Skills.TYPE_ATTACK)
				&& ((cha instanceof L1CrownInstance)
						|| (cha instanceof L1DwarfInstance)
						|| (cha instanceof L1EffectInstance)
						|| (cha instanceof L1FieldObjectInstance)
						|| (cha instanceof L1FurnitureInstance)
						|| (cha instanceof L1HousekeeperInstance)
						|| (cha instanceof L1MerchantInstance) 
						|| (cha instanceof L1TeleporterInstance))) {
			return false;
		}

		// 攻擊系對像自分對像外
		if ((_skill.getType() == L1Skills.TYPE_ATTACK)
				&& (cha.getId() == _user.getId())) {
			return false;
		}

		// 自分H-A場合效果無
		if ((cha.getId() == _user.getId()) 
				&& (_skillId == HEAL_ALL)) {
			return false;
		}

		if ((((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
				|| ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) 
				|| ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
				&& (cha.getId() == _user.getId()) && (_skillId != HEAL_ALL)) {
			return true; // 員自分效果。（、除外）
		}

		// 攻擊者是PC
		if ((_user instanceof L1PcInstance) && (_skill.getTarget().equals("attack") 
						|| (_skill.getType() == L1Skills.TYPE_ATTACK)) && (_isPK == false)) {
			
			// 目標是寵物
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				// 自己的寵物
				if (_player.getId() == summon.getMaster().getId()) {
					return false;
				}
				
			} else if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				// 自己的寵物
				if (_player.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		if ((_skill.getTarget().equals("attack") 
				|| (_skill.getType() == L1Skills.TYPE_ATTACK))
				// 目標不是怪物
				&& !(cha instanceof L1MonsterInstance)
				// 不是PK狀態
				&& (_isPK == false)
				// 目標是人物
				&& (_target instanceof L1PcInstance)) {

			L1PcInstance enemy = null;

			try {
				enemy = (L1PcInstance) cha;
				
			} catch (final Exception e) {
				return false;
			}

			// 無所遁形術
			if ((_skillId == COUNTER_DETECTION)
					&& (enemy.getZoneType() != 1)
					&& (cha.hasSkillEffect(INVISIBILITY) 
							|| cha.hasSkillEffect(BLIND_HIDING))) {
				return true; // 中
			}
			if ((_player.getClanid() != 0) && (enemy.getClanid() != 0) && _player.getClanid() != enemy.getClanid()) { // 所屬中
				// 取回全部戰爭清單
				for (final L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(_player.getClanname())) { // 自戰爭參加中
						if (war.checkClanInSameWar( // 同戰爭參加中
								_player.getClanname(), enemy.getClanname())) {
							if (L1CastleLocation.checkInAllWarArea(
									enemy.getX(), 
									enemy.getY(), 
									enemy.getMapId())
									) {
								return true;
							}
						}
					}
				}
			}
			return false; // 攻擊PK場合
		}

		if ((_user.glanceCheck(cha.getX(), cha.getY()) == false)
				&& (_skill.isThrough() == false)) {
			// 、復活障害物判定
			if (!((_skill.getType() == L1Skills.TYPE_CHANGE) || (_skill.getType() == L1Skills.TYPE_RESTORE))) {
				_isGlanceCheckFail = true;
				return false; // 直線上障害物
			}
		}

		if (cha.hasSkillEffect(ICE_LANCE)
				&& ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD))) {
			return false; // 中、、
		}

		if (cha.hasSkillEffect(FREEZING_BLIZZARD)
				&& ((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD))) {
			return false; // 中、、
		}

		if (cha.hasSkillEffect(EARTH_BIND) && (_skillId == EARTH_BIND)) {
			return false; //  中 
		}

		if (!(cha instanceof L1MonsterInstance)
				&& ((_skillId == TAMING_MONSTER) || (_skillId == CREATE_ZOMBIE))) {
			return false; // （）
		}
		if (cha.isDead()
				&& ((_skillId != CREATE_ZOMBIE) && (_skillId != RESURRECTION)
						&& (_skillId != GREATER_RESURRECTION) && (_skillId != CALL_OF_NATURE))) {
			return false; // 死亡
		}

		if ((cha.isDead() == false)
				&& ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION)
						|| (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
			return false; // 死亡
		}

		if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
				&& ((_skillId == CREATE_ZOMBIE) || (_skillId == RESURRECTION)
						|| (_skillId == GREATER_RESURRECTION) || (_skillId == CALL_OF_NATURE))) {
			return false; // 、
		}

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			// 
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				switch (_skillId) {
				case CURSE_BLIND:
				case WEAPON_BREAK:
				case DARKNESS:
				case WEAKNESS:
				case DISEASE:
				case FOG_OF_SLEEPING:
				case MASS_SLOW: 
				case SLOW:
				case CANCELLATION: 
				case SILENCE:
				case DECAY_POTION:
				case MASS_TELEPORT: 
				case DETECTION:
				case COUNTER_DETECTION:
				case ERASE_MAGIC: 
				case ENTANGLE:
				case PHYSICAL_ENCHANT_DEX:
				case PHYSICAL_ENCHANT_STR:
				case BLESS_WEAPON: 
				case EARTH_SKIN:
				case IMMUNE_TO_HARM:
				case REMOVE_CURSE:
					return true;
					
				default:
					return false;
				}
			}
		}

		// 目標在隱身狀態(地下)
		if (cha instanceof L1NpcInstance) {
			final int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
			switch (hiddenStatus) {
			case L1NpcInstance.HIDDEN_STATUS_SINK:
				switch (_skillId) {
				case DETECTION:
				case COUNTER_DETECTION:
					return true;
				}
				return false;
				
			case L1NpcInstance.HIDDEN_STATUS_FLY:
				return false;
			}
		}

		if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // PC
		)
		&& (cha instanceof L1PcInstance)) {
			flg = true;
			
		} else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // NPC
		)
		
		&& ((cha instanceof L1MonsterInstance)
				|| (cha instanceof L1NpcInstance)
				|| (cha instanceof L1SummonInstance) 
				|| (cha instanceof L1PetInstance))) {
			flg = true;
			
		} else if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET)
				&& (_user instanceof L1PcInstance)) { // Summon,Pet
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				if (summon.getMaster() != null) {
					if (_player.getId() == summon.getMaster().getId()) {
						flg = true;
					}
				}
			}
			
			if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				if (pet.getMaster() != null) {
					if (_player.getId() == pet.getMaster().getId()) {
						flg = true;
					}
				}
			}
		}
		
		if ((_calcType == PC_PC) && (cha instanceof L1PcInstance)) {
			
			final L1PcInstance xpc = (L1PcInstance) cha;
			if (((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN)
					&& (((_player.getClanid() != 0 // 員
					)
					&& (_player.getClanid() == xpc.getClanid())) || _player.isGm())) {
				return true;
			}
			
			if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
					&& (_player.getParty().isMember(xpc) || _player.isGm())) {
				return true;
			}
		}

		return flg;
	}

	/**
	 * 是否為同組
	 * @param npc 
	 * @param cha 
	 * @return
	 */
	private boolean isParty(final L1NpcInstance npc, final L1Character cha) {
		if (npc.getMaster() == null) {
			return false;
		}
		// 在目標清單中
		if (npc.isHate(cha)) {
			return false;
		}
		
		final int masterId = npc.getMaster().getId();
		
		// 目標是人物
		if (cha instanceof L1PcInstance) {
			if (cha.getId() == masterId) {
				return true;
			}
			return false;
		}
		
		// 目標是寵物
		if (cha instanceof L1PetInstance) {
			final L1PetInstance tgPet = (L1PetInstance) cha;
			if (tgPet.getMaster() != null && 
					tgPet.getMaster().getId() == masterId) {
				return true;
			}
			return false;
		}
		
		// 目標是召喚獸
		if (cha instanceof L1SummonInstance) {
			final L1SummonInstance tgSu = (L1SummonInstance) cha;
			if (tgSu.getMaster() != null && 
					tgSu.getMaster().getId() == masterId) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 技能發動 目標清單判定
	 */
	private void makeTargetList() {
		try {
			if (this._type == TYPE_LOGIN) { // 時(死亡時、化屋敷含)使用者
				this._targetList.add(new TargetStatus(this._user));
				return;
			}
			
			if ((this._skill.getTargetTo() == L1Skills.TARGET_TO_ME) && 
					((this._skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
				this._targetList.add(new TargetStatus(this._user)); // 使用者
				return;
			}

			// 具有攻擊範圍設置
			if (this._skill.getRanged() != -1) {
				if (this._user.getLocation().getTileLineDistance(
						this._target.getLocation()) > this._skill.getRanged()) {
					return; // 射程範圍外
				}
				
			} else {
				// 距離不可見
				if (!this._user.getLocation().isInScreen(this._target.getLocation())) {
					return; // 射程範圍外
				}
			}

			if ((this.isTarget(this._target) == false)
					&& !(this._skill.getTarget().equals("none"))) {
				// 對像違發動。
				return;
			}

			// 直線上目標列舉
			switch (this._calcType) {
			case LIGHTNING:
				final ArrayList<L1Object> al1object = 
					World.get().getVisibleLineObjects(this._user, this._target);
				for (final L1Object tgobj : al1object) {
					if (tgobj == null) {
						continue;
					}
					
					if (!(tgobj instanceof L1Character)) { // 以外場合何。
						continue;
					}
					
					final L1Character cha = (L1Character) tgobj;
					if (this.isTarget(cha) == false) {
						continue;
					}
					// 技能發動 目標清單判定:直線上目標列舉
					this._targetList.add(new TargetStatus(cha));
				}
				al1object.clear();
				return;
			}

			// 單一目標攻擊
			if (this._skill.getArea() == 0) {
				if (!this._user.glanceCheck(this._target.getX(), this._target.getY())) { // 直線上障害物
					if (((this._skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK)
							&& (this._skillId != 10026)
							&& (this._skillId != 10027)
							&& (this._skillId != 10028) 
							&& (this._skillId != 10029)) { // 安息攻擊以外攻擊
						// 發生、發生、發動
						this._targetList.add(new TargetStatus(this._target, false));
						return;
					}
				}
				this._targetList.add(new TargetStatus(this._target));
				
			// 範圍攻擊
			} else {
				if (!this._skill.getTarget().equals("none")) {
					this._targetList.add(new TargetStatus(this._target));
				}

				if ((this._skillId != HEAL_ALL) && 
						!(this._skill.getTarget().equals("attack") ||
								(this._skill.getType() == L1Skills.TYPE_ATTACK))) {
					// 攻擊系以外H-A以外自身含
					this._targetList.add(new TargetStatus(this._user));
				}

				List<L1Object> objects;
				// 全畫面物件
				if (this._skill.getArea() == -1) {
					objects = World.get().getVisibleObjects(this._user);
					
				// 指定範圍物件
				} else {
					objects = World.get().getVisibleObjects(this._target, this._skill.getArea());
				}
				//System.out.println("攻擊範圍物件數量:"+objects.size());
				for (final L1Object tgobj : objects) {
					if (tgobj == null) {
						continue;
					}
					
					if (!(tgobj instanceof L1Character)) {
						continue;
					}
					
					if (tgobj instanceof L1MonsterInstance) {
						L1MonsterInstance mob = (L1MonsterInstance) tgobj;
						if (mob.getNpcId() == 45166) {// 膽小的南瓜怪
							continue;
						}
						if (mob.getNpcId() == 45167) {// 殘暴的南瓜怪
							continue;
						}
					}
					
					final L1Character cha = (L1Character) tgobj;

					if (!this.isTarget(cha)) {
						continue;
					}

					// 技能發動 目標清單判定:加入目標清單 - 迴圈
					this._targetList.add(new TargetStatus(cha));
				}
				return;
			}

		} catch (final Exception e) {
			//_log.error("SkillId:" + this._skillId + " UserName:" + this._player.getName());
		}
	}

	/**
	 * 訊息發送
	 * @param pc
	 */
	private void sendHappenMessage(final L1PcInstance pc) {
		final int msgID = this._skill.getSysmsgIdHappen();
		if (msgID > 0) {
			pc.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 失敗表示
	private void sendFailMessageHandle() {
		// 攻擊以外對像指定失敗場合失敗送信
		// ※攻擊障害物成功時同。
		if ((_skill.getType() != L1Skills.TYPE_ATTACK)
				&& !_skill.getTarget().equals("none")
				&& (_targetList.size() == 0)) {
			sendFailMessage();
		}
	}

	// 表示（失敗）
	private void sendFailMessage() {
		final int msgID = _skill.getSysmsgIdFail();
		if ((msgID > 0) && (_user instanceof L1PcInstance)) {
			_player.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 精靈魔法屬性使用者屬性一致？（對處、對應消去下)
	private boolean isAttrAgrees() {
		final int magicattr = _skill.getAttr();
		if (_user instanceof L1NpcInstance) { // NPC使場合OK
			return true;
		}

		if ((_skill.getSkillLevel() >= 17) && (_skill.getSkillLevel() <= 22)
				&& (magicattr != 0 // 精靈魔法、無屬性魔法、
				)
				&& (magicattr != _player.getElfAttr() // 使用者魔法屬性一致。
				)
				&& !_player.isGm()) { // GM例外
			return false;
		}
		return true;
	}

	/**
	 * 判斷技能的使用是否需要消耗HP/MP
	 * @return
	 */
	private boolean isHPMPConsume() {
		this._mpConsume = _skill.getMpConsume();
		this._hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		if (this._user instanceof L1NpcInstance) {
			currentMp = _npc.getCurrentMp();
			currentHp = _npc.getCurrentHp();
			
		} else {
			currentMp = _player.getCurrentMp();
			currentHp = _player.getCurrentHp();

			// MPINT輕減
			if ((this._player.getInt() > 12) && (this._skillId > HOLY_WEAPON)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV2以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 13) && (this._skillId > STALAC)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV3以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 14) && (this._skillId > WEAK_ELEMENTAL)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV4以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 15) && (this._skillId > MEDITATION)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV5以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 16) && (this._skillId > DARKNESS)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV6以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 17) && (this._skillId > BLESS_WEAPON)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV7以上
				this._mpConsume--;
			}
			if ((this._player.getInt() > 18) && (this._skillId > DISEASE)
					&& (this._skillId <= FREEZING_BLIZZARD)) { // LV8以上
				this._mpConsume--;
			}

			if ((this._player.getInt() > 12) && (this._skillId >= SHOCK_STUN)
					&& (this._skillId <= COUNTER_BARRIER)) {
				this._mpConsume -= (this._player.getInt() - 12);
			}

			// MP裝備輕減
			if ((this._skillId == PHYSICAL_ENCHANT_DEX)
					&& this._player.getInventory().checkEquipped(20013)) { // 迅速裝備中PE:DEX
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == HASTE)
					&& this._player.getInventory().checkEquipped(20013)) { // 迅速裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			// MP裝備輕減
			if ((this._skillId == PHYSICAL_ENCHANT_DEX)
					&& this._player.getInventory().checkEquipped(320013)) { // 迅速裝備中PE:DEX
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == HASTE)
					&& this._player.getInventory().checkEquipped(320013)) { // 迅速裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
//			if (!_player.isCrown()) {
				if ((this._skillId == HEAL) && this._player.getInventory().checkEquipped(20014)) { // 治癒裝備中
					//this._mpConsume /= 2;
					this._mpConsume = this._mpConsume >> 1;
				}
				if ((this._skillId == EXTRA_HEAL)
						&& this._player.getInventory().checkEquipped(20014)) { // 治癒裝備中
					//this._mpConsume /= 2;
					this._mpConsume = this._mpConsume >> 1;
				}
//			}
//			if (!_player.isCrown()) {
				if ((this._skillId == HEAL) && this._player.getInventory().checkEquipped(320014)) { // 治癒裝備中
					//this._mpConsume /= 2;
					this._mpConsume = this._mpConsume >> 1;
				}
				if ((this._skillId == EXTRA_HEAL)
						&& this._player.getInventory().checkEquipped(320014)) { // 治癒裝備中
					//this._mpConsume /= 2;
					this._mpConsume = this._mpConsume >> 1;
				}
//			}
			if ((this._skillId == ENCHANT_WEAPON)
					&& this._player.getInventory().checkEquipped(20015)) { // 力裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == ENCHANT_WEAPON)
					&& this._player.getInventory().checkEquipped(320015)) { // 力裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == DETECTION)
					&& this._player.getInventory().checkEquipped(20015)) { // 力裝備中無所遁形術
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == DETECTION)
					&& this._player.getInventory().checkEquipped(320015)) { // 力裝備中無所遁形術
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == PHYSICAL_ENCHANT_STR)
					&& this._player.getInventory().checkEquipped(20015)) { // 力裝備中PE:STR
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == PHYSICAL_ENCHANT_STR)
					&& this._player.getInventory().checkEquipped(320015)) { // 力裝備中PE:STR
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == HASTE)
					&& this._player.getInventory().checkEquipped(20008)) { // 裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}
			if ((this._skillId == GREATER_HASTE)
					&& this._player.getInventory().checkEquipped(20023)) { // 裝備中
				//this._mpConsume /= 2;
				this._mpConsume = this._mpConsume >> 1;
			}

			if (0 < this._skill.getMpConsume()) { // MP消費
				this._mpConsume = Math.max(this._mpConsume, 1); // 最低1消費。
			}

			// MPINT輕減
			if (this._player.getOriginalMagicConsumeReduction() > 0) {
				this._mpConsume -= this._player.getOriginalMagicConsumeReduction();
			}
		}

		if (currentHp < this._hpConsume + 1) {
			if (this._user instanceof L1PcInstance) {
				// 279 \f1因體力不足而無法使用魔法。
				this._player.sendPackets(new S_ServerMessage(279));
			}
			return false;
			
		} else if (currentMp < this._mpConsume) {
			if (this._user instanceof L1PcInstance) {
				// 278 \f1因魔力不足而無法使用魔法。
				this._player.sendPackets(new S_ServerMessage(278));
				if (this._player.isGm()) {
					this._player.setCurrentMp(this._player.getMaxMp());
				}
			}
			return false;
		}

		return true;
	}

	// 必要材料？
	// 判斷技能的使用是否需要其他物品的輔助
	private boolean isItemConsume() {

		final int itemConsume = this._skill.getItemConsumeId();
		final int itemConsumeCount = this._skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return true; // 材料必要魔法
		}

		if (!this._player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false; // 必要材料足。
		}

		return true;
	}

	/**
	 * 使用技能後，相應的HP和MP、Lawful、材料的減少
	 */
	private void useConsume() {
		if (this._user instanceof L1NpcInstance) {
			// NPC場合、HP、MP
			if (!(this._npc instanceof L1PetInstance)) {
				final int current_hp = this._npc.getCurrentHp() - this._hpConsume;
				this._npc.setCurrentHp(current_hp);
			}

			final int current_mp = this._npc.getCurrentMp() - this._mpConsume;
			this._npc.setCurrentMp(current_mp);
			return;
		}

		// HP・MP
		if (this.isHPMPConsume()) {
			if (this._skillId == FINAL_BURN) { //  
				this._player.setCurrentHp(1);
				this._player.setCurrentMp(0);
				
			} else if (this._skillId == NATURES_TOUCH) {
				this._player.setCurrentHp(this._player.getMaxHp());
				final int current_mp = this._player.getCurrentMp() - this._mpConsume;
				this._player.setCurrentMp(current_mp);
			} else {
				final int current_hp = this._player.getCurrentHp() - this._hpConsume;
				this._player.setCurrentHp(current_hp);

				final int current_mp = this._player.getCurrentMp() - (this._mpConsume == 0 && _skill.getMpConsume() > 0 ? 1 : this._mpConsume);
				this._player.setCurrentMp(current_mp);
			}
		}

		// Lawful
		int lawful = this._player.getLawful() + this._skill.getLawful();
		if (lawful > 32767) {
			lawful = 32767;
		}
		if (lawful < -32767) {
			lawful = -32767;
		}
		this._player.setLawful(lawful);

		final int itemConsume = this._skill.getItemConsumeId();
		final int itemConsumeCount = this._skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return; // 材料必要魔法
		}

		// 使用材料
		this._player.getInventory().consumeItem(itemConsume, itemConsumeCount);
	}

	// 追加。
	// 使用相應技能要對玩家人物或者使用的相應道具追加必要動作
	private void addMagicList(final L1Character cha, final boolean repetition) {
		///System.out.println("111111111111");
		if (_skillTime == 0) {
			_getBuffDuration = _skill.getBuffDuration() * 1000; // 效果時間
			if (_skill.getBuffDuration() == 0) {
				if (_skillId == INVISIBILITY) { // 
					cha.setSkillEffect(INVISIBILITY, 0);
				}
				return;
			}
		} else {
			_getBuffDuration = _skillTime * 1000; // time0以外、效果時間設定
		}

		if (_skillId == SHOCK_STUN) {
			_getBuffDuration = _shockStunDuration;
		}

		if (_skillId == CURSE_POISON) { // 效果處理L1Poison移讓。
			return;
		}
		if ((_skillId == CURSE_PARALYZE) || (_skillId == CURSE_PARALYZE2)) { // 效果處理L1CurseParalysis移讓。
			return;
		}
		if (_skillId == SHAPE_CHANGE) { // 效果處理L1PolyMorph移讓。
			return;
		}
		if ((_skillId == BLESSED_ARMOR)
				|| (_skillId == HOLY_WEAPON) // 武器・防具效果處理L1ItemInstance移讓。
				|| (_skillId == ENCHANT_WEAPON) 
				|| (_skillId == BLESS_WEAPON)
				|| (_skillId == SHADOW_FANG)) {
			return;
		}
		if (((_skillId == ICE_LANCE) || (_skillId == FREEZING_BLIZZARD))
				&& !_isFreeze) { // 凍結失敗
			return;
		}
		/*if ((this._skillId == AWAKEN_ANTHARAS) 
				|| (this._skillId == AWAKEN_FAFURION)
				|| (this._skillId == AWAKEN_VALAKAS)) { // 覺醒效果處理L1Awake移讓。
			return;
		}*/
		final SkillMode mode = L1SkillMode.get().getSkill(this._skillId);
		if (mode == null) {
			cha.setSkillEffect(_skillId, _getBuffDuration);
		}
		// XXX
		if ((cha instanceof L1PcInstance) && repetition) { // 對像PC既重複場合
			final L1PcInstance pc = (L1PcInstance) cha;
			sendIcon(pc);
		}
	}

	/**
	 * 發送技能圖示
	 * @param pc
	 */
	private void sendIcon(final L1PcInstance pc) {
		if (this._skillTime == 0) {
			this._getBuffIconDuration = this._skill.getBuffDuration(); // 效果時間
			
		} else {
			this._getBuffIconDuration = this._skillTime; // time0以外、效果時間設定
		}

		//System.out.println("發送技能圖示");
		switch (this._skillId) {
		case SHIELD: // 
			pc.sendPackets(new S_SkillIconShield(5, this._getBuffIconDuration));
			break;
			
		case SHADOW_ARMOR: //  
			pc.sendPackets(new S_SkillIconShield(3, this._getBuffIconDuration));
			break;
			
		case DRESS_DEXTERITY: //  
			pc.sendPackets(new S_Dexup(pc, 2, this._getBuffIconDuration));
			break;
			
		case DRESS_MIGHTY: //  
			pc.sendPackets(new S_Strup(pc, 2, this._getBuffIconDuration));
			break;
			
		case GLOWING_AURA: //  
			pc.sendPackets(new S_PacketBoxIconAura(113, this._getBuffIconDuration));
			break;
			
		case SHINING_AURA: //  
			pc.sendPackets(new S_PacketBoxIconAura(114, this._getBuffIconDuration));
			break;
			
		case BRAVE_AURA: //  
			pc.sendPackets(new S_PacketBoxIconAura(116, this._getBuffIconDuration));
			break;
			
		case FIRE_WEAPON: //  
			pc.sendPackets(new S_PacketBoxIconAura(147, this._getBuffIconDuration));
			break;
			
		case WIND_SHOT: //  
			pc.sendPackets(new S_PacketBoxIconAura(148, this._getBuffIconDuration));
			break;
			
		case FIRE_BLESS: //  
			pc.sendPackets(new S_PacketBoxIconAura(154, this._getBuffIconDuration));
			break;
			
		case STORM_EYE: //  
			pc.sendPackets(new S_PacketBoxIconAura(155, this._getBuffIconDuration));
			break;
			
		case EARTH_BLESS: //  
			pc.sendPackets(new S_SkillIconShield(7, this._getBuffIconDuration));
			break;
			
		case BURNING_WEAPON: //  
			pc.sendPackets(new S_PacketBoxIconAura(162, this._getBuffIconDuration));
			break;
			
		case STORM_SHOT: //  
			pc.sendPackets(new S_PacketBoxIconAura(165, this._getBuffIconDuration));
			break;
			
		case IRON_SKIN: //  
			pc.sendPackets(new S_SkillIconShield(10, this._getBuffIconDuration));
			break;
			
		case EARTH_SKIN: //  
			pc.sendPackets(new S_SkillIconShield(6, this._getBuffIconDuration));
			break;
			
		case PHYSICAL_ENCHANT_STR: //  ：STR
			pc.sendPackets(new S_Strup(pc, 5, this._getBuffIconDuration));
			break;
			
		case PHYSICAL_ENCHANT_DEX: //  ：DEX
			pc.sendPackets(new S_Dexup(pc, 5, this._getBuffIconDuration));
			break;
			
		case HASTE: 
		case GREATER_HASTE: // 
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, this._getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
			break;
			
		case HOLY_WALK: 
		case MOVING_ACCELERATION:
		case WIND_WALK: // 、、
			pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
//			pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
			break;
			
		case SLOW: 
		case MASS_SLOW:
		case ENTANGLE: // 、、
			pc.sendPackets(new S_SkillHaste(pc.getId(), 2, this._getBuffIconDuration));
			pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 2, 0));
			break;
			
		case IMMUNE_TO_HARM:
			pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, this._getBuffIconDuration));
			break;
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	// 送信
	// 圖解發送，技能使用完畢後發送結束提示圖標
	private void sendGrfx(final boolean isSkillAction) {
		int actionId = _skill.getActionId();
		final int castgfx = _skill.getCastGfx();
		if (castgfx == 0) {
			return; // 表示無
		}

		// TODO 施展者為PC
		if (_user instanceof L1PcInstance) {
			if ((_skillId == FIRE_WALL) || (_skillId == LIFE_STREAM)) {
				final L1PcInstance pc = (L1PcInstance) _user;
				if (_skillId == FIRE_WALL) {
					pc.setHeading(pc.targetDirection(_targetX, _targetY));
					pc.sendPacketsAll(new S_ChangeHeading(pc));
				}
				pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), actionId));
				return;
			}

			final int targetid = this._target.getId();

			if (_skillId == SHOCK_STUN) {
				if (_targetList.size() == 0) { // 失敗
					return;
					
				} else {
					if (_target instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) _target;
						pc.sendPacketsAll(new S_SkillSound(pc.getId(), 4434));
						
					} else if (_target instanceof L1NpcInstance) {
						_target.broadcastPacketX10(new S_SkillSound(_target.getId(), 4434));
					}
					return;
				}
			}

			if (_skillId == LIGHT) {
				final L1PcInstance pc = (L1PcInstance) _target;
				pc.sendPackets(new S_Sound(145));
			}

			if ((_targetList.size() == 0) && !(_skill.getTarget().equals("none"))) {
				// 數０對像指定場合、魔法使用表示終了
				final int tempchargfx = _player.getTempCharGfx();
				switch (tempchargfx) {
				case 5727:
				case 5730: // 系變身對應
					actionId = ActionCodes.ACTION_SkillBuff;
					break;
					
				case 5733:
				case 5736:
					actionId = ActionCodes.ACTION_Attack;
					break;
				}
				if (isSkillAction) {
					_player.sendPacketsX10(
							new S_DoActionGFX(_player.getId(), actionId));
				}
				return;
			}

			if (_skill.getTarget().equals("attack") && (_skillId != 18)) {
				// 目標對像 是否為寵物 召喚獸 虛擬人物
				if (isPcSummonPet(_target)) {
					if (_player.isSafetyZone() || // 自己位於安全區
							_target.isSafetyZone() || // 目標位於安全區
							_player.checkNonPvP(_player, _target) // 檢查是否可以攻擊
							) {
						// 封包:物件攻擊(NPC / PC 技能使用)
						_player.sendPacketsX10(
								new S_UseAttackSkill(
										_player, 
										0, 
										castgfx, 
										_targetX, 
										_targetY, 
										actionId,
										_dmg
										));
						return;
					}
				}

				// 單體攻擊魔法
				if (_skill.getArea() == 0) {
					// 封包:物件攻擊(NPC / PC 技能使用)
					if (_player.hasSkillEffect(attack_no_spr)) {
						_player.sendPacketsX10(
								new S_UseAttackSkill(
										_player,
										targetid,
										castgfx,
										_targetX,
										_targetY,
										actionId,
										_dmg, false
								));
					} else {
						_player.sendPacketsX10(
								new S_UseAttackSkill(
										_player,
										targetid,
										castgfx,
										_targetX,
										_targetY,
										actionId,
										_dmg, true
								));
					}
				// 有方向範圍魔法
				} else {
					// 封包:範圍魔法
					if (_player.hasSkillEffect(attack_no_spr)) {
						_player.sendPacketsX10(
								new S_RangeSkill(
										_player,
										_targetList,
										castgfx,
										actionId,
										S_RangeSkill.TYPE_DIR, false
								));
					} else {
						_player.sendPacketsX10(
								new S_RangeSkill(
										_player,
										_targetList,
										castgfx,
										actionId,
										S_RangeSkill.TYPE_DIR, true
								));
					}
				}
				
			} else if (_skill.getTarget().equals("none")
					&& (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍攻擊魔法
				//System.out.println("無方向範圍攻擊魔法 目標物件數量:" + _targetList.size());
				_player.sendPacketsX10(
						new S_RangeSkill(
								_player, 
								_targetList, 
								castgfx, 
								actionId, 
								S_RangeSkill.TYPE_NODIR
								));
				
			} else { // 補助魔法
				// 、、以外
				if ((_skillId != 5) && (_skillId != 69) && (_skillId != 131)) {
					// 魔法使動作使用者
					if (isSkillAction) {
						_player.sendPacketsX10(
								new S_DoActionGFX(_player.getId(), _skill.getActionId()));
					}

					if ((_skillId == COUNTER_MAGIC) ||// 魔法屏障
							(_skillId == COUNTER_BARRIER) ||// 反擊屏障
							(_skillId == COUNTER_MIRROR)) {// 鏡反射
						_player.sendPackets(new S_SkillSound(targetid, castgfx));
						
					} else if (_skillId == TRUE_TARGET) { // 精準目標
						return;

					} else {
						_player.sendPacketsX10(new S_SkillSound(targetid, castgfx));
					}
				}

				// 表示全員、必要性、送信
				for (final TargetStatus ts : _targetList) {
					final L1Character cha = ts.getTarget();
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				}
			}
			
		// TODO 施展者是NPC
		} else if (this._user instanceof L1NpcInstance) { // NPC使場合
			final int targetid = this._target.getId();

			if (this._user instanceof L1MerchantInstance) {
				this._user.broadcastPacketX10(new S_SkillSound(targetid, castgfx));
				return;
			}

			if ((this._targetList.size() == 0) && !(this._skill.getTarget().equals("none"))) {
				// 數０對像指定場合、魔法使用表示終了
				this._user.broadcastPacketX10(new S_DoActionGFX(this._user.getId(), this._skill.getActionId()));
				return;
			}

			if (this._skill.getTarget().equals("attack") && (this._skillId != 18)) {
				if (this._skill.getArea() == 0) { // 單體攻擊魔法
					this._user.broadcastPacketX10(
							new S_UseAttackSkill(
									this._user, 
									targetid, 
									castgfx, 
									this._targetX, 
									this._targetY, 
									actionId,
									this._dmg
									));

				} else { // 有方向範圍攻擊魔法
					this._user.broadcastPacketX10(
							new S_RangeSkill(
									this._user, 
									this._targetList, 
									castgfx, 
									actionId, 
									S_RangeSkill.TYPE_DIR
									));
				}
				
			} else if (this._skill.getTarget().equals("none")
					&& (this._skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範圍魔法
				//System.out.println("無方向範圍魔法");
				this._user.broadcastPacketX10(
						new S_RangeSkill(
								this._user, 
								this._targetList, 
								castgfx, 
								actionId, 
								S_RangeSkill.TYPE_NODIR
								));
				
			} else { // 補助魔法
				// 、、以外
				if ((this._skillId != 5) && (this._skillId != 69) && (this._skillId != 131)) {
					// 魔法使動作使用者
					this._user.broadcastPacketX10(new S_DoActionGFX(this._user.getId(), this._skill.getActionId()));
					this._user.broadcastPacketX10(new S_SkillSound(targetid, castgfx));
				}
			}
		}
	}
	
	// 不允許重複的技能組
	private static final int[][] REPEATEDSKILLS = {
			{ FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT },
			
			{ SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
			
			{ HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, 
				STATUS_ELFBRAVE },
				
			{ HASTE, GREATER_HASTE, STATUS_HASTE },
			
			{ PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
			
			{ PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
			
			{ GLOWING_AURA, SHINING_AURA }
			};
	
	/**
	 * 刪除不能重複/同時使用的技能，圖標更改為剛使用時的圖標
	 * @param cha
	 */
	private void deleteRepeatedSkills(final L1Character cha) {
		for (final int[] skills : REPEATEDSKILLS) {
			for (final int id : skills) {
				if (id == _skillId) {
					stopSkillList(cha, skills);
				}
			}
		}
	}

	/**
	 * 刪除全部重複的正在使用的技能
	 * @param cha
	 * @param repeat_skill
	 */
	private void stopSkillList(final L1Character cha, final int[] repeat_skill) {
		for (final int skillId : repeat_skill) {
			if (skillId != _skillId) {
				cha.removeSkillEffect(skillId);
			}
		}
	}

	// 技能使用延遲的設定
	private void setDelay() {
		if (this._skill.getReuseDelay() > 0) {
			L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay(), _skill.getSkillId());
		}
	}

	/**
	 * 發動技能效果
	 */
	private void runSkill() {
		switch (_skillId) {
		case LIFE_STREAM:// 法師技能(治癒能量風暴)
			L1SpawnUtil.spawnEffect(81169,
					_skill.getBuffDuration(), _targetX, _targetY,
					_user.getMapId(), _user, 0);
			return;
			
		case FIRE_WALL:// 法師技能(火牢)
			//System.out.println("法師技能(火牢):"+_targetX+"/"+_targetY);
			L1SpawnUtil.doSpawnFireWall(_user, _targetX, _targetY);
			return;
		}

		// 有使用次數限制的技能使用成功與否的判斷
		for (final int skillId : EXCEPT_COUNTER_MAGIC) {
			if (_skillId == skillId) {
				_isCounterMagic = false; // 無效
				break;
			}
		}

		// NPC使用onActionNullPointerException發生
		// PC使用時
		if ((_skillId == SHOCK_STUN) && (_user instanceof L1PcInstance)) {
			_target.onAction(_player);
		}
		
		if (!this.isTargetCalc(_target)) {
			return;
		}

		try {
			TargetStatus ts = null;
			L1Character cha = null;
			//int dmg = 0;
			int drainMana = 0;
			int heal = 0;
			boolean isSuccess = false;
			int undeadType = 0;

			for (final Iterator<TargetStatus> iter = _targetList.iterator(); iter.hasNext();) {
				ts = null;
				cha = null;
				//dmg = 0;
				heal = 0;
				isSuccess = false;
				undeadType = 0;

				ts = iter.next();
				cha = ts.getTarget();

				//System.out.println("發動技能效果");
				if (_npc != null) {
					// 施展者是寵物 XXX
					if (_npc instanceof L1PetInstance) {
						if (isParty(_npc, cha)) {
							ts.isCalc(false);
							_dmg = 0;
							continue;
						}
					}
					// 施展者是召喚獸
					if (_npc instanceof L1SummonInstance) {
						if (isParty(_npc, cha)) {
							ts.isCalc(false);
							_dmg = 0;
							continue;
						}
					}
				}
				
				if (!ts.isCalc() || !this.isTargetCalc(cha)) {
					ts.isCalc(false);
					continue; // 計算必要。不需要計算
				}

				final L1Magic magic = new L1Magic(_user, cha);
				magic.setLeverage(getLeverage());
				
				if (cha instanceof L1MonsterInstance) { // 判定
					undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
				}

				// 確率系失敗確定場合
				// 概率系技能失敗的確定
				if (((_skill.getType() == L1Skills.TYPE_CURSE) || (
						_skill.getType() == L1Skills.TYPE_PROBABILITY)) 
						&& isTargetFailure(cha)) {
					iter.remove();
					continue;
				}

				if (cha instanceof L1PcInstance) { // PC場合送信。
					// 只有在目標為pc的情況下發送圖標，代表使用成功
					if (_skillTime == 0) {
						_getBuffIconDuration = _skill.getBuffDuration(); // 效果時間
						
					} else {
						_getBuffIconDuration = _skillTime; // time0以外、效果時間設定
					}
				}
				
				deleteRepeatedSkills(cha); // 刪除重複的技能

				//System.out.println("NPC對PC傷害計算 XXX:"+this._skill.getType());
				switch (_skill.getType()) {
				case L1Skills.TYPE_ATTACK:// 攻擊系＆使用者以外。
					if (_user.getId() != cha.getId()) {
						// 攻擊系技能和使用者除外
						if (isUseCounterMagic(cha)) { // 發動場合、削除
							iter.remove();
							continue;
						}
						_dmg = magic.calcMagicDamage(_skillId);
						cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
					}
					break;
					
				case L1Skills.TYPE_CURSE:
				case L1Skills.TYPE_PROBABILITY: // 確率系
					isSuccess = magic.calcProbabilityMagic(this._skillId);
					if (_type == TYPE_GMBUFF) {
						isSuccess = true;
					}
					if (this._skillId != ERASE_MAGIC) {
						cha.removeSkillEffect(ERASE_MAGIC); // 魔法消除
					}
					
					if (this._skillId != FOG_OF_SLEEPING) {
						cha.removeSkillEffect(FOG_OF_SLEEPING); // 沉睡之霧
					}
					
					if (isSuccess) { // 成功發動場合、削除
						if (this.isUseCounterMagic(cha)) { // 發動
							iter.remove();
							continue;
						}
						
					} else { // 失敗場合、削除
						if ((this._skillId == FOG_OF_SLEEPING) && (cha instanceof L1PcInstance)) {
							final L1PcInstance pc = (L1PcInstance) cha;
							// 297 你感覺些微地暈眩。
							pc.sendPackets(new S_ServerMessage(297));
						}
						iter.remove();
						continue;
					}
					break;
					
				case L1Skills.TYPE_HEAL: // 回復系
					// 回復量表現
					this._dmg = -1 * magic.calcHealing(this._skillId);
					if (cha.hasSkillEffect(WATER_LIFE)) { // 水之元氣(回復量2倍)
						//this._dmg *= 2;
						// (>> 1: 除)  (<< 1: 乘)
						this._dmg = (this._dmg << 1);
					}
					
					if (cha.hasSkillEffect(POLLUTE_WATER)) { // 污濁之水(回復量1/2倍)
						//this._dmg /= 2;
						// (>> 1: 除)  (<< 1: 乘)
						this._dmg = (this._dmg >> 1);
					}
					
					if (cha.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
						this._dmg = (this._dmg >> 1);
					}
					
					if (cha.hasSkillEffect(ADLV80_2_3)) {
						this._dmg *= -1;
					}
					break;
				}

				// TODO SKILL移轉
				final SkillMode mode = L1SkillMode.get().getSkill(this._skillId);
				if (mode != null) {
					// 施展者是PC
					if (this._user instanceof L1PcInstance) {
						switch (this._skillId) {
						case TELEPORT:// 指定傳送5
						case MASS_TELEPORT:// 集體傳送術69
							this._dmg = mode.start(this._player, cha, magic, this._bookmarkId);
							break;
							
						case CALL_CLAN:// 呼喚盟友
						case RUN_CLAN:// 援護盟友118
							this._dmg = mode.start(this._player, cha, magic, this._targetID);
							break;
							
						default:
							this._dmg = mode.start(this._player, cha, magic, _skill.getBuffDuration());
							break;
						}
					}
					// 施展者是NPC
					if (this._user instanceof L1NpcInstance) {
						this._dmg = mode.start(this._npc, cha, magic, this._getBuffIconDuration);
					}
					
				} else {
					// ■■■■ 個別處理書。 ■■■■
					// 需要個別處理的技能（無法簡單以技能的屬系做判斷）
					// 使用濟場合 重複使用無效的技能
					// 重出來例外 衝擊之暈例外
					if (cha.hasSkillEffect(this._skillId)) {
						this.addMagicList(cha, true); // 魔法效果時間上書
						if (this._skillId != SHAPE_CHANGE) { // 變形術
							continue;
						}
					}
				}

				if (this._skillId == DETECTION) { // 無所遁形術
					if (cha instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						final int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(this._player);
						}
					}
					
				} else if (this._skillId == COUNTER_DETECTION) { // 無所遁形術
					if (cha instanceof L1PcInstance) {
						this._dmg = magic.calcMagicDamage(this._skillId);
						
					} else if (cha instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						final int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(this._player);
						} else {
							this._dmg = 0;
						}
						
					} else {
						this._dmg = 0;
					}

				// ★★★ 回復系 ★★★ 恢復系技能
				} else if (
						(this._skillId == HEAL) || 
						(this._skillId == EXTRA_HEAL) || 
						(this._skillId == GREATER_HEAL) || 
						(this._skillId == FULL_HEAL) || 
						(this._skillId == HEAL_ALL) || 
						(this._skillId == NATURES_TOUCH) || 
						(this._skillId == NATURES_BLESSING)
						) {
					if (this._user instanceof L1PcInstance) {
						cha.removeSkillEffect(WATER_LIFE);
					}
					
				// ★★★ 攻擊系 ★★★ 攻擊系技能
				// 、
				} else if ((this._skillId == CHILL_TOUCH) || (this._skillId == VAMPIRIC_TOUCH)) {
					heal = this._dmg;

				} else if ((this._skillId == 10026) || (this._skillId == 10027)
						|| (this._skillId == 10028) || (this._skillId == 10029)) { // 安息攻擊
					if (this._user instanceof L1NpcInstance) {
						this._user.broadcastPacketX8(new S_NpcChat(this._npc, "$3717")); // 、安息與。

					} else {
						this._player.broadcastPacketX8(new S_Chat(this._player, "$3717")); // 、安息與。
					}
					
				} else if (this._skillId == 10057) { // 引寄
					L1Teleport.teleportToTargetFront(cha, this._user, 1);
					
				// ★★★ 確率系 ★★★ 確率系技能
				} else if ((this._skillId == SLOW) || (this._skillId == MASS_SLOW)
						|| (this._skillId == ENTANGLE)) { // 、
					// 、
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) { 
							continue;
						}
					}
					if (cha.getBraveSpeed() == 5) {// 具有強化勇水狀態
						continue;
					}
					switch (cha.getMoveSpeed()) {
					case 0:
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_SkillHaste(pc.getId(), 2, this._getBuffIconDuration));
						}
						cha.broadcastPacketAll(new S_SkillHaste(cha.getId(), 2, this._getBuffIconDuration));
						cha.setMoveSpeed(2);
						break;
						
					case 1:
						int skillNum = 0;
						if (cha.hasSkillEffect(HASTE)) {
							skillNum = HASTE;
							
						} else if (cha.hasSkillEffect(GREATER_HASTE)) {
							skillNum = GREATER_HASTE;
							
						} else if (cha.hasSkillEffect(STATUS_HASTE)) {
							skillNum = STATUS_HASTE;
						}
						
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(this._skillId);
							cha.setMoveSpeed(0);
							continue;
						}
						break;
					}
					
				/*} else if ((this._skillId == CURSE_BLIND) || (this._skillId == DARKNESS)) {
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
							pc.sendPackets(new S_CurseBlind(2));
							
						} else {
							pc.sendPackets(new S_CurseBlind(1));
						}
					}*/
					
				} else if (this._skillId == CURSE_POISON) {
					L1DamagePoison.doInfection(this._user, cha, 3000, 5);

				/*} else if ((this._skillId == CURSE_PARALYZE)
						|| (this._skillId == CURSE_PARALYZE2)) {
					if (!cha.hasSkillEffect(EARTH_BIND)
							&& !cha.hasSkillEffect(ICE_LANCE)
							&& !cha.hasSkillEffect(FREEZING_BLIZZARD)
							&& !cha.hasSkillEffect(FREEZING_BREATH)) {
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 8000, 16000);
							
						} else if (cha instanceof L1MonsterInstance) {
							L1CurseParalysis.curse(cha, 0, 16000);
						}
					}*/
					
				} else if (this._skillId == WEAKNESS) { // 
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-5);
						pc.addHitup(-1);
					}
					
				} else if (this._skillId == DISEASE) { // 
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-6);
						pc.addAc(12);
					}
					
				} else if ((this._skillId == ICE_LANCE) // 
				|| (this._skillId == FREEZING_BLIZZARD)) { // 
					// 計算攻擊是否成功
					this._isFreeze = magic.calcProbabilityMagic(this._skillId);
					if (this._isFreeze) {
						//final int time = this._skill.getBuffDuration() * 1000;
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							// 法師技能(冰矛圍籬)
							L1SpawnUtil.spawnEffect(
									81168, this._skill.getBuffDuration(), 
									cha.getX(), cha.getY(), 
									cha.getMapId(), this._user, 0);
							
							pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
							
						} else if ((cha instanceof L1MonsterInstance)
								|| (cha instanceof L1SummonInstance)
								|| (cha instanceof L1PetInstance)) {
							final L1NpcInstance npc = (L1NpcInstance) cha;
							// 法師技能(冰矛圍籬)
							L1SpawnUtil.spawnEffect(81168, this._skill.getBuffDuration(), 
									cha.getX(), cha.getY(), 
									cha.getMapId(), this._user, 0);
							
							npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
							npc.setParalyzed(true);
						}
					}
					
				} else if (this._skillId == EARTH_BIND) { // 
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_Poison(pc.getId(), 2));
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
						
					} else if ((cha instanceof L1MonsterInstance)
							|| (cha instanceof L1SummonInstance)
							|| (cha instanceof L1PetInstance)) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						
						npc.broadcastPacketAll(new S_Poison(npc.getId(), 2));
						npc.setParalyzed(true);
					}
					
				/*} else if (this._skillId == WIND_SHACKLE) { //  
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(),
								this._getBuffIconDuration));
					}*/
					
				/*} else if (this._skillId == CANCELLATION) {
					// 對象是NPC
					if (cha instanceof L1NpcInstance) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						// 取回NPCID
						final int npcId = npc.getNpcTemplate().get_npcId();
						switch (npcId) {
						case KnightLv45_1._searcherid:// 調查員
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								npc.setTempCharGfx(1314);
								npc.broadcastPacketAll(new S_ChangeShape(npc, 1314));
								return;
								
							} else {
								return;
							}
							
						case ElfLv45_2._npcId:// 獨角獸
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								final int x = npc.getX();
								final int y = npc.getY();
								final short m = npc.getMapId();
								final int h = npc.getHeading();
								npc.deleteMe();
								L1SpawnUtil.spawnT(45641, x, y, m, h, 300);
								return;
								
							} else {
								return;
							}
							
						case EWLv40_1._roiid:// 羅伊
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								npc.setTempCharGfx(4310);
								npc.broadcastPacketAll(new S_ChangeShape(npc, 4310));
								return;
								
							} else {
								return;
							}
						}
					}
					if ((this._player != null) && this._player.isInvisble()) {
						this._player.delInvis();
					}
					if (!(cha instanceof L1PcInstance)) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setMoveSpeed(0);
						npc.setBraveSpeed(0);
						npc.broadcastPacketAll(new S_SkillHaste(cha.getId(), 0, 0));
						npc.broadcastPacketAll(new S_SkillBrave(cha.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}
					// 解除 技能解除
					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (this.isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 強化、異常解除
					cha.curePoison();
					cha.cureParalaysis();
					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_END; skillNum++) {
						if ((skillNum == STATUS_CHAT_PROHIBITED // 禁止解除
						)
						|| (skillNum == STATUS_CURSE_BARLOG // 咒解除
						)
						|| (skillNum == STATUS_CURSE_YAHEE)) { // 咒解除
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 料理解除
					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (this.isNotCancelable(skillNum)) {
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 裝備時關連何掛解除
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							pc.setMoveSpeed(0);
							pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
						}
					}
					cha.removeSkillEffect(STATUS_FREEZE); // Freeze解除
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
						if (pc.isPrivateShop()) {
							pc.sendPacketsAll(new S_DoActionShop(pc.getId(), pc.getShopChat()));
						}
						if (this._user instanceof L1PcInstance) {
							L1PinkName.onAction(pc, this._user);
						}
					}*/
					
				} else if (this._skillId == TURN_UNDEAD) {
					if ((undeadType == 1) || (undeadType == 3)) {
						// 對像HP。
						this._dmg = cha.getCurrentHp();
					}
					
				} else if (this._skillId == MANA_DRAIN) { //  
					final Random random = new Random();
					final int chance = random.nextInt(10) + 5;
					drainMana = chance + (this._user.getInt() / 2);
					if (cha.getCurrentMp() < drainMana) {
						drainMana = cha.getCurrentMp();
					}
					
				} else if (this._skillId == WEAPON_BREAK) { //  
					/*
					 * 對NPC場合、L1Magic算出1/2
					 * 、對PC場合記入。 損傷量1~(int/3)
					 */
					if ((this._calcType == PC_PC) || (this._calcType == NPC_PC)) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							final L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								final Random random = new Random();
								final int weaponDamage = random.nextInt(this._user.getInt() / 3) + 1;
								// \f1%0損傷。
								pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
								pc.getInventory().receiveDamage(weapon, weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}
					
				} else if (this._skillId == FOG_OF_SLEEPING) {
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
					}
					cha.setSleeped(true);

				}

				// ●●●● PC效果 ●●●● 只允許pc使用的技能
				if ((this._calcType == PC_PC) || (this._calcType == NPC_PC)) {
					// ★★★ 特殊系★★★ 特殊技能
					if (this._skillId == CREATE_MAGICAL_WEAPON) { // 
						//  
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(
								this._itemobjid);
						if ((item != null) && (item.getItem().getType2() == 1)) {
							final int item_type = item.getItem().getType2();
							final int safe_enchant = item.getItem().get_safeenchant();
							final int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
							if (safe_enchant < 0) { // 強化不可
								pc.sendPackets( // \f1何起。
										new S_ServerMessage(79));
							} else if (safe_enchant == 0) { // 安全圈+0
								pc.sendPackets( // \f1何起。
										new S_ServerMessage(79));
							} else if ((item_type == 1) && (enchant_level == 0)) {
								if (!item.isIdentified()) {// 未鑒定
									pc.sendPackets( // \f1%0%2%1光。
											new S_ServerMessage(161, item_name,
													"$245", "$247"));
								} else {
									item_name = "+0 " + item_name;
									pc.sendPackets( // \f1%0%2%1光。
											new S_ServerMessage(161, "+0 "
													+ item_name, "$245", "$247"));
								}
								item.setEnchantLevel(1);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_ENCHANTLVL);
							} else {
								pc.sendPackets( // \f1何起。
										new S_ServerMessage(79));
							}
						} else {
							pc.sendPackets( // \f1何起。
									new S_ServerMessage(79));
						}
						
					} else if (this._skillId == BRING_STONE) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						final Random random = new Random();
						final L1ItemInstance item = pc.getInventory().getItem(
								this._itemobjid);
						if (item != null) {
							final int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc
									.getWis() - 6) * 1.2);
							final int brave = (int) (dark / 2.1);
							final int wise = (int) (brave / 2.0);
							final int kayser = (int) (wise / 1.9);
							final int chance = random.nextInt(100) + 1;
							if (item.getItem().getItemId() == 40320) {
								pc.getInventory().removeItem(item, 1);
								if (dark >= chance) {
									pc.getInventory().storeItem(40321, 1);
									pc.sendPackets(new S_ServerMessage(403,
									"$2475")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失敗。
								}
							} else if (item.getItem().getItemId() == 40321) {
								pc.getInventory().removeItem(item, 1);
								if (brave >= chance) {
									pc.getInventory().storeItem(40322, 1);
									pc.sendPackets(new S_ServerMessage(403,
									"$2476")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失敗。
								}
							} else if (item.getItem().getItemId() == 40322) {
								pc.getInventory().removeItem(item, 1);
								if (wise >= chance) {
									pc.getInventory().storeItem(40323, 1);
									pc.sendPackets(new S_ServerMessage(403,
									"$2477")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失敗。
								}
							} else if (item.getItem().getItemId() == 40323) {
								pc.getInventory().removeItem(item, 1);
								if (kayser >= chance) {
									pc.getInventory().storeItem(40324, 1);
									pc.sendPackets(new S_ServerMessage(403,
									"$2478")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失敗。
								}
							}
						}

					/*} else if ((this._skillId == LESSER_ELEMENTAL)
							|| (this._skillId == GREATER_ELEMENTAL)) { // 、
						final L1PcInstance pc = (L1PcInstance) cha;
						final int attr = pc.getElfAttr();
						if (attr != 0) { // 無屬性實行
							if (!pc.getMap().isRecallPets()) {
								// 353：在這附近無法召喚怪物。  
								pc.sendPackets(new S_ServerMessage(353));
								return;
							}

							int petcost = 0;
							final Object[] petlist = pc.getPetList().values()
							.toArray();
							for (final Object pet : petlist) {
								// 現在
								petcost += ((L1NpcInstance) pet)
								.getPetcost();
							}

							if (petcost == 0) { // 1匹所屬NPC實行
								int summonid = 0;
								int summons[];
								if (this._skillId == LESSER_ELEMENTAL) { // [地,火,水,風]
									summons = new int[] { 45306, 45303,
											45304, 45305 };
								} else {
									// [地,火,水,風]
									summons = new int[] { 81053, 81050,
											81051, 81052 };
								}
								int npcattr = 1;
								for (int i = 0; i < summons.length; i++) {
									if (npcattr == attr) {
										summonid = summons[i];
										i = summons.length;
									}
									npcattr *= 2;
								}
								// 特殊設定場合出現
								if (summonid == 0) {
									final Random random = new Random();
									final int k3 = random.nextInt(4);
									summonid = summons[k3];
								}

								final L1Npc npcTemp = NpcTable.get()
								.getTemplate(summonid);
								final L1SummonInstance summon = new L1SummonInstance(
										npcTemp, pc);
								summon.setPetcost(pc.getCha() + 7); // 精靈他NPC所屬
							}
						}*/
						
					} else if (this._skillId == ABSOLUTE_BARRIER) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.stopHpRegeneration();
						pc.stopMpRegeneration();
					}

					// ★★★ 變化系（） ★★★ 變化系技能
					if (this._skillId == LIGHT) { // 
						// addMagicList()後、turnOnOffLight()送信
						
					} else if (this._skillId == GLOWING_AURA) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addBowHitup(5);
						pc.addMr(20);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_PacketBoxIconAura(113, this._getBuffIconDuration));
						
					} else if (this._skillId == SHINING_AURA) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-8);
						pc.sendPackets(new S_PacketBoxIconAura(114,
								this._getBuffIconDuration));
						
					} else if (this._skillId == BRAVE_AURA) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.addBowDmgup(5);
						pc.sendPackets(new S_PacketBoxIconAura(116,
								this._getBuffIconDuration));
						
					} else if (this._skillId == SHIELD) { // 
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-2);
						pc.sendPackets(new S_SkillIconShield(5,
								this._getBuffIconDuration));
						
					} else if (this._skillId == SHADOW_ARMOR) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-3);
						pc.sendPackets(new S_SkillIconShield(3, this._getBuffIconDuration));

					} else if (this._skillId == DRESS_DEXTERITY) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 2);
						pc.sendPackets(new S_Dexup(pc, 2, this._getBuffIconDuration));

					} else if (this._skillId == DRESS_MIGHTY) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 2);
						pc.sendPackets(new S_Strup(pc, 2, this._getBuffIconDuration));

					} else if (this._skillId == SHADOW_FANG) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(
								this._itemobjid);
						if ((item != null) && (item.getItem().getType2() == 1)) {
							item.setSkillWeaponEnchant(pc, this._skillId, this._skill
									.getBuffDuration() * 1000);
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
						
					} else if (this._skillId == ENCHANT_WEAPON) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(
								this._itemobjid);
						if ((item != null) && (item.getItem().getType2() == 1)) {
							pc.sendPackets(new S_ServerMessage(161, item
									.getLogName(), "$245", "$247"));
							item.setSkillWeaponEnchant(pc, this._skillId, this._skill
									.getBuffDuration() * 1000);
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
						
					} else if ((this._skillId == HOLY_WEAPON //  
					)
					|| (this._skillId == BLESS_WEAPON)) { //  
						if (!(cha instanceof L1PcInstance)) {
							return;
						}
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getWeapon() == null) {
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						for (final L1ItemInstance item : pc.getInventory().getItems()) {
							if (pc.getWeapon().equals(item)) {
								pc.sendPackets(new S_ServerMessage(161, item
										.getLogName(), "$245", "$247"));
								item.setSkillWeaponEnchant(pc, this._skillId, this._skill
										.getBuffDuration() * 1000);
								return;
							}
						}
						
					} else if (this._skillId == BLESSED_ARMOR) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						final L1ItemInstance item = pc.getInventory().getItem(
								this._itemobjid);
						if ((item != null) && (item.getItem().getType2() == 2)
								&& (item.getItem().getType() == 2)) {
							pc.sendPackets(new S_ServerMessage(161, item
									.getLogName(), "$245", "$247"));
							item.setSkillArmorEnchant(pc, this._skillId, this._skill
									.getBuffDuration() * 1000);
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
						
					} else if (this._skillId == EARTH_BLESS) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-7);
						pc.sendPackets(new S_SkillIconShield(7,
								this._getBuffIconDuration));
						
					} else if (this._skillId == RESIST_MAGIC) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(10);
						pc.sendPackets(new S_SPMR(pc));
						
					} else if (this._skillId == CLEAR_MIND) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addWis((byte) 3);
						pc.resetBaseMr();
						
					} else if (this._skillId == RESIST_ELEMENTAL) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addWind(10);
						pc.addWater(10);
						pc.addFire(10);
						pc.addEarth(10);
						pc.sendPackets(new S_OwnCharStatus(pc));

					} else if (this._skillId == ELEMENTAL_PROTECTION) { // 
						final L1PcInstance pc = (L1PcInstance) cha;
						final int attr = pc.getElfAttr();
						if (attr == 1) {
							pc.addEarth(50);
						} else if (attr == 2) {
							pc.addFire(50);
						} else if (attr == 4) {
							pc.addWater(50);
						} else if (attr == 8) {
							pc.addWind(50);
						}
					} else if ((this._skillId == INVISIBILITY)
							|| (this._skillId == BLIND_HIDING)) { // 、
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Invis(pc.getId(), 1));
						pc.broadcastPacketAll(new S_RemoveObject(pc));
						
					} else if (this._skillId == IRON_SKIN) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-10);
						pc.sendPackets(new S_SkillIconShield(10,
								this._getBuffIconDuration));
						
					} else if (this._skillId == EARTH_SKIN) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-6);
						pc.sendPackets(new S_SkillIconShield(6,
								this._getBuffIconDuration));
						
					} else if (this._skillId == PHYSICAL_ENCHANT_STR) { // ：STR
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 5);
						pc
						.sendPackets(new S_Strup(pc, 5,
								this._getBuffIconDuration));
						
					} else if (this._skillId == PHYSICAL_ENCHANT_DEX) { // ：DEX
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 5);
						pc
						.sendPackets(new S_Dexup(pc, 5,
								this._getBuffIconDuration));
						
					} else if (this._skillId == FIRE_WEAPON) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_PacketBoxIconAura(147,
								this._getBuffIconDuration));
						
					} else if (this._skillId == FIRE_BLESS) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(7);
						pc.sendPackets(new S_PacketBoxIconAura(154,
								this._getBuffIconDuration));

					} else if (this._skillId == BURNING_WEAPON) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addHitup(3);
						pc.sendPackets(new S_PacketBoxIconAura(162,
								this._getBuffIconDuration));
						
					} else if (this._skillId == WIND_SHOT) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(ConfigOther.WIND_SHOT_BOW_HIT_VALUE);
						pc.addBowDmgup(ConfigOther.WIND_SHOT_BOW_DMG_VALUE);
						pc.sendPackets(new S_PacketBoxIconAura(148,
								this._getBuffIconDuration));
						
					} else if (this._skillId == STORM_EYE) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(ConfigOther.STORM_EYE_BOW_HIT_VALUE);
						pc.addBowDmgup(ConfigOther.STORM_EYE_BOW_DMG_VALUE);
						pc.sendPackets(new S_PacketBoxIconAura(155,
								this._getBuffIconDuration));
						
					} else if (this._skillId == STORM_SHOT) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowDmgup(ConfigOther.STORM_SHOT_BOW_DMG_VALUE);
						pc.addBowHitup(ConfigOther.STORM_SHOT_BOW_HIT_VALUE);
						pc.sendPackets(new S_PacketBoxIconAura(165,
								this._getBuffIconDuration));
						
					} else if (this._skillId == BERSERKERS) { // 狂暴術
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(10);
						pc.addDmgup(5);
						pc.addHitup(2);
						
					} else if (this._skillId == SHAPE_CHANGE) { // 變形術67
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_ShowPolyList(pc.getId()));
						if (!pc.isShapeChange()) {
							pc.setSummonMonster(false);
							pc.setShapeChange(true);
						}

					} else if (this._skillId == GREATER_HASTE) { //  
						final L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
						if (pc.getMoveSpeed() != 2) { // 中以外
							pc.setDrink(false);
							pc.setMoveSpeed(1);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1, this._getBuffIconDuration));
							pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
							
						} else { // 中
							int skillNum = 0;
							if (pc.hasSkillEffect(SLOW)) {
								skillNum = SLOW;
							} else if (pc.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							} else if (pc.hasSkillEffect(ENTANGLE)) {
								skillNum = ENTANGLE;
							}
							if (skillNum != 0) {
								pc.removeSkillEffect(skillNum);
								pc.removeSkillEffect(GREATER_HASTE);
								pc.setMoveSpeed(0);
								continue;
							}
						}
						
					} else if ((this._skillId == HOLY_WALK)
							|| (this._skillId == MOVING_ACCELERATION)
							|| (this._skillId == WIND_WALK)) { // 、、
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.setBraveSpeed(1); // 神聖疾走改勇敢藥水效果 原本是4
						pc.sendPackets(new S_SkillBrave(pc.getId(), 4, this._getBuffIconDuration));
//						pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 4, 0));
						
					/*} else if (this._skillId == BLOODLUST) { // 
						final L1PcInstance pc = (L1PcInstance) cha;
						
						//
						if (pc.hasSkillEffect(STATUS_BRAVE2)) {
							// 1,413：目前情況是無法使用。  
							pc.sendPackets(new S_ServerMessage(1413)); 
							return;
						}
						
						// 勇敢效果 抵銷對應技能
						L1BuffUtil.braveStart(pc);
						
						pc.setBraveSpeed(6);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 6, this._getBuffIconDuration));
						pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 6, 0));*/

					}
				}

				// ●●●● NPC效果 ●●●● npc使用的技能
				if ((_calcType == PC_NPC) || (_calcType == NPC_NPC)) {
					// ★★★ 系 ★★★ 寵物使用的技能
					if ((_skillId == TAMING_MONSTER)
							&& ((L1MonsterInstance) cha).getNpcTemplate().isTamable()) { // 
						int petcost = 0;
						final Object[] petlist = _user.getPetList().values().toArray();
						for (final Object pet : petlist) {
							// 現在
							petcost += 6;//((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
//						if (_player.isElf()) { // 
//							charisma += 12;
//
//						} else if (_player.isWizard()) { // 
//							charisma += 6;
//						}
						charisma -= petcost;
						if (charisma >= 6) { // 確認
							final L1SummonInstance summon = new L1SummonInstance(
									this._targetNpc, this._user, false);
							this._target = summon; // 入替
							
						} else {
							this._player.sendPackets(new S_ServerMessage(319)); // \f1以上操。
						}
						
					} else if (this._skillId == CREATE_ZOMBIE) { // 
						int petcost = 0;
						final Object[] petlist = this._user.getPetList().values()
						.toArray();
						for (final Object pet : petlist) {
							// 現在
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = this._user.getCha();
						if (this._player.isElf()) { // 
							charisma += 12;
						} else if (this._player.isWizard()) { // 
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // 確認
							final L1SummonInstance summon = new L1SummonInstance(
									this._targetNpc, this._user, true);
							this._target = summon; // 入替
						} else {
							this._player.sendPackets(new S_ServerMessage(319)); // \f1以上操。
						}
						
					} else if (this._skillId == WEAK_ELEMENTAL) { //  
						if (cha instanceof L1MonsterInstance) {
							final L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
							final int weakAttr = npcTemp.get_weakAttr();
							if ((weakAttr & 1) == 1) { // 地
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2169));
							}
							if ((weakAttr & 2) == 2) { // 火
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2167));
							}
							if ((weakAttr & 4) == 4) { // 水
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2166));
							}
							if ((weakAttr & 8) == 8) { // 風
								cha.broadcastPacketX8(new S_SkillSound(cha.getId(), 2168));
							}
						}
						
					} else if (this._skillId == RETURN_TO_NATURE) { // 
						if (cha instanceof L1SummonInstance) {
							final L1SummonInstance summon = (L1SummonInstance) cha;
							summon.broadcastPacketX10(new S_SkillSound(summon.getId(), 2245));
							summon.returnToNature();
							
						} else {
							if (this._user instanceof L1PcInstance) {
								this._player.sendPackets(new S_ServerMessage(79));
							}
						}
					}
				}

				// ■■■■ 個別處理 ■■■■

				if ((this._skill.getType() == L1Skills.TYPE_HEAL)
						&& (this._calcType == PC_NPC) && (undeadType == 1)) {
					this._dmg *= -1; // 、回復系。
				}

				if ((this._skill.getType() == L1Skills.TYPE_HEAL)
						&& (this._calcType == PC_NPC) && (undeadType == 3)) {
					this._dmg = 0; // 、系回復系無效
				}

				if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) && (this._dmg < 0)) { // 、使用
					this._dmg = 0;
				}

				//System.out.println("dmg1:"+dmg); XXX
				if ((this._dmg != 0) || (drainMana != 0)) {
					//System.out.println("結果質2:(HP) " + this._dmg);
					magic.commit(this._dmg, drainMana); // 系、回復系值。
				}

				//System.out.println("dmg2:"+dmg);
				// 系他、別途回復場合（V-T）
				if (heal > 0) {
					//System.out.println("dmg3:"+dmg);
					if ((heal + this._user.getCurrentHp()) > this._user.getMaxHp()) {
						this._user.setCurrentHp(this._user.getMaxHp());
						
					} else {
						this._user.setCurrentHp(heal + this._user.getCurrentHp());
					}
				}

				if (cha instanceof L1PcInstance) { // PC、AC送信
					final L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
					pc.sendPackets(new S_OwnCharStatus(pc));
					sendHappenMessage(pc); // 送信
				}

				addMagicList(cha, false); // 魔法效果時間設定

				if (cha instanceof L1PcInstance) { // PC、狀態更新
					final L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				}
			}

			if ((_skillId == DETECTION) || (_skillId == COUNTER_DETECTION)) { // 無所遁形術、無所遁形術
				detection(_player);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 解除返。 被技能攻擊而暫時不能解除
	 */
	/*private boolean isNotCancelable(final int skillNum) {
		return (skillNum == ENCHANT_WEAPON) ||
		(skillNum == BLESSED_ARMOR) ||
		(skillNum == ABSOLUTE_BARRIER) ||
		(skillNum == ADVANCE_SPIRIT) ||
		(skillNum == SHOCK_STUN) ||
		(skillNum == SHADOW_FANG) ||
		(skillNum == REDUCTION_ARMOR) ||
		(skillNum == SOLID_CARRIAGE) ||
		(skillNum == COUNTER_BARRIER) ||
		(skillNum == AWAKEN_ANTHARAS) ||
		(skillNum == AWAKEN_FAFURION) ||
		(skillNum == AWAKEN_VALAKAS);
	}*/

	private void detection(final L1PcInstance pc) {
		if (!pc.isGmInvis() && pc.isInvisble()) { // 自分
			pc.delInvis();
			pc.beginInvisTimer();
		}

		for (final L1PcInstance tgt : World.get().getVisiblePlayer(pc)) {
			if (!tgt.isGmInvis() && tgt.isInvisble()) {
				tgt.delInvis();
			}
		}
		
		// 偵測陷阱的處理
		WorldTrap.get().onDetection(pc);
	}

	/**
	 * 目標判定
	 * @param cha
	 * @param cha 
	 * @return
	 */
	private boolean isTargetCalc(final L1Character cha) {
		// 攻擊魔法Non−PvP判定
		if (this._skill.getTarget().equals("attack") && (this._skillId != TURN_UNDEAD)) { // 攻擊魔法
			if (this.isPcSummonPet(cha)) { // 對像PC、、
				if (this._player.isSafetyZone() ||
						cha.isSafetyZone() ||// 攻擊側攻擊側
						this._player.checkNonPvP(this._player, cha)) 
				{ // Non-PvP設定
					return false;
				}
			}
		}
		switch (this._skillId) {
		// 沉睡之霧
		case FOG_OF_SLEEPING:
			if (this._user.getId() == cha.getId()) {
				return false;
			}
			break;
			
		// 集體緩速術
		case MASS_SLOW:
			if (this._user.getId() == cha.getId()) {
				return false;
			}
			
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				if (this._user.getId() == summon.getMaster().getId()) {
					return false;
				}
				
			} else if (cha instanceof L1PetInstance) {
				final L1PetInstance pet = (L1PetInstance) cha;
				if (this._user.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
			break;

		// 集體傳送術
		case MASS_TELEPORT:
			if (this._user.getId() != cha.getId()) {
				return false;
			}
			break;
		}
		return true;
	}

	/**
	 * 目標對像 是否為寵物 召喚獸 虛擬人物
	 * @param cha
	 * @return
	 */
	private boolean isPcSummonPet(final L1Character cha) {
		// PC 對 PC
		switch (this._calcType) {
		case PC_PC:
			return true;

		// PC 對 NPC
		case PC_NPC:
			// 目標對像為召喚獸
			if (cha instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) cha;
				// 目標對像具有主人
				if (summon.isExsistMaster()) {
					return true;
				}
			}
			// 目標對像為寵物
			if (cha instanceof L1PetInstance) {
				return true;
			}
			return false;
			
		default:
			return false;
		}
	}

	// 對必失敗返
	private boolean isTargetFailure(final L1Character cha) {
		boolean isTU = false;
		boolean isErase = false;
		boolean isManaDrain = false;
		int undeadType = 0;
		if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // 、確率系無效
			return true;
		}

		if (cha instanceof L1PcInstance) { // 對PC場合
			if ((this._calcType == PC_PC) && this._player.checkNonPvP(this._player, cha)) { // Non-PvP設定
				final L1PcInstance pc = (L1PcInstance) cha;
				if ((this._player.getId() == pc.getId())
						|| ((pc.getClanid() != 0) && (this._player.getClanid() == pc
								.getClanid()))) {
					return false;
				}
				return true;
			}
			return false;
		}

		if (cha instanceof L1MonsterInstance) { // 可能判定
			isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
		}

		if (cha instanceof L1MonsterInstance) { // 可能判定
			isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
		}

		if (cha instanceof L1MonsterInstance) { // 判定
			undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
		}

		// 可能？
		if (cha instanceof L1MonsterInstance) {
			isManaDrain = true;
		}
		/*
		 * 成功除外條件１：T-U成功、對像。 成功除外條件２：T-U成功、對像無效。
		 * 成功除外條件３：、、、、、無效
		 * 成功除外條件４：成功、以外場合
		 */
		if (((this._skillId == TURN_UNDEAD) && 
				((undeadType == 0) || (undeadType == 2))) || ((this._skillId == TURN_UNDEAD) && 
						(isTU == false)) || (((this._skillId == ERASE_MAGIC) 
						|| (this._skillId == SLOW)
						|| (this._skillId == MANA_DRAIN) 
						|| (this._skillId == MASS_SLOW)
						|| (this._skillId == ENTANGLE) 
						|| (this._skillId == WIND_SHACKLE)) && (isErase == false))
						|| ((this._skillId == MANA_DRAIN) && (isManaDrain == false))) {
			return true;
		}
		return false;
	}

	// 發動返
	private boolean isUseCounterMagic(final L1Character cha) {
		// 有效中
		if (this._isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
			cha.removeSkillEffect(COUNTER_MAGIC);
			final int castgfx = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx();
			cha.broadcastPacketX10(new S_SkillSound(cha.getId(), castgfx));
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}

}
