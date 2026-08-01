package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_PetMenuPacket;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_NPCPack_Summon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;

/**
 * 召喚獸控制項
 * @author daien
 *
 */
public class L1SummonInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1SummonInstance.class);

	private static final int _summonTime = 3600;
	
	private int _currentPetStatus;
	
	private int _checkMove= 0;
	
	private boolean _tamed;
	
	private boolean _isReturnToNature = false;
	private boolean isSkillSpawn = false;
	private int index = 0;
	
	private static Random _random = new Random();

	public boolean tamed() {
		return this._tamed;
	}
	public boolean isSkillSpawn() {
		return this.isSkillSpawn;
	}

	public void setSkillSpawn(final boolean set) {
		this.isSkillSpawn = set;
	}
	
	// 場合處理
	@Override
	public boolean noTarget() {
		switch (this._currentPetStatus) {
		case 3:// 休息
			return true;
			
		case 4:// 散開
			if ((_master != null)
					&& (_master.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(_master.getLocation()) < 5)) {
				if (_npcMove != null) {
					int dir = _npcMove.targetReverseDirection(_master.getX(), _master.getY());
					dir = _npcMove.checkObject(dir);
					
					_npcMove.setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
				
			} else {
				_currentPetStatus = 3;
				return true;
			}
			break;
			
		case 5:// 警戒
			if ((Math.abs(getHomeX() - getX()) > 1)
					|| (Math.abs(getHomeY() - getY()) > 1)) {
				if (_npcMove != null) {
					final int dir = _npcMove.moveDirection(getHomeX() + this.index, getHomeY());
					if (dir == -1) {
						// 離現在地
						setHomeX(getX());
						setHomeY(getY());
						
					} else {
						_npcMove.setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
					}
				}
			}
			break;
			
		default:
			if ((_master != null) && (_master.getMapId() == getMapId())) {
				// 主人跟隨
				final int location = getLocation().getTileLineDistance(_master.getLocation());
				//System.out.println("主人跟隨 距離: "+location);
				if (location > 2) {
					if (_npcMove != null) {
						final int dir = _npcMove.moveDirection(_master.getX() + this.index, _master.getY());
						//System.out.println("控制者遺失次數: "+_checkMove);
						if (dir == -1) {
							_checkMove++;
							if (_checkMove >= 10) {
								// 控制者遺失
								//System.out.println("控制者遺失次數: "+_checkMove);
								_checkMove = 0;
								_currentPetStatus = 3;
								return true;
							}
							
						} else {
							_checkMove = 0;
							_npcMove.setDirectionMove(dir);
							setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
						}
					}
				}

			} else {
				// 控制者遺失
				//System.out.println("控制者遺失else");
				_currentPetStatus = 3;
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * 召喚獸
	 * @param template
	 * @param master
	 */
	public L1SummonInstance(final L1Npc template, final L1Character master, final int index) {
		super(template);
		this.index = index;
		this.setId(IdFactoryNpc.get().nextId());
		// 副本編號
		this.set_showId(master.get_showId());

		this.set_time(_summonTime);
		/*if (SummonTimer.MAP.get(this) == null) {
			SummonTimer.MAP.put(this, _summonTime);
		}*/

		this.setMaster(master);
		this.setX(master.getX() + _random.nextInt(5) - 2);
		this.setY(master.getY() + _random.nextInt(5) - 2);
		this.setMap(master.getMapId());
		this.setHeading(5);
		this.setLightSize(template.getLightSize());

		_currentPetStatus = 3;
		_tamed = false;

		World.get().storeObject(this);
		World.get().addVisibleObject(this);
		
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			this.onPerceive(pc);
		}
		master.addPet(this);
		if (master instanceof L1PcInstance) {
			// 增加物件組人
			addMaster((L1PcInstance) master);
			
		} else if (master instanceof L1NpcInstance) {
			//L1NpcInstance npc = (L1NpcInstance) master;
			// 增加物件組人
			//npc.broadcastPacketX10(new S_NewMaster(npc.getNameId(), this));
			_currentPetStatus = 1;
			//System.out.println("增加物件組人:"+npc.getNameId());
		}
	}

	/**
	 * 造屍術
	 * @param target
	 * @param master
	 * @param isCreateZombie
	 */
	public L1SummonInstance(final L1NpcInstance target, final L1Character master, final boolean isCreateZombie) {
		super(null);
		this.setId(IdFactoryNpc.get().nextId());
		// 副本編號
		this.set_showId(master.get_showId());

		if (isCreateZombie) { // 
			int npcId = 45065;
			final L1PcInstance pc = (L1PcInstance) master;
			final int level = pc.getLevel();
			if (pc.isWizard()) {
				if ((level >= 24) && (level <= 31)) {
					npcId = 81183;
				} else if ((level >= 32) && (level <= 39)) {
					npcId = 81184;
				} else if ((level >= 40) && (level <= 43)) {
					npcId = 81185;
				} else if ((level >= 44) && (level <= 47)) {
					npcId = 81186;
				} else if ((level >= 48) && (level <= 51)) {
					npcId = 81187;
				} else if (level >= 52) {
					npcId = 81188;
				}
			} else if (pc.isElf()) {
				if (level >= 48) {
					npcId = 81183;
				}
			}
			final L1Npc template = NpcTable.get().getTemplate(npcId).clone();
			this.setting_template(template);

		} else { // 
			this.setting_template(target.getNpcTemplate());
			this.setCurrentHpDirect(target.getCurrentHp());
			this.setCurrentMpDirect(target.getCurrentMp());
		}
		
		this.set_time(_summonTime);
		/*if (SummonTimer.MAP.get(this) == null) {
			SummonTimer.MAP.put(this, _summonTime);
		}*/

		this.setMaster(master);
		this.setX(target.getX());
		this.setY(target.getY());
		this.setMap(target.getMapId());
		this.setHeading(target.getHeading());
		this.setLightSize(target.getLightSize());
		this.setPetcost(6);

		if ((target instanceof L1MonsterInstance) && !((L1MonsterInstance) target).is_storeDroped()) {
			// XXX
			final SetDropExecutor setDropExecutor = new SetDrop();
			setDropExecutor.setDrop(target, target.getInventory());
		}
		
		this.setInventory(target.getInventory());
		target.setInventory(null);

		this._currentPetStatus = 3;
		this._tamed = true;

		// 攻擊中場合止
		for (final L1NpcInstance each : master.getPetList().values()) {
			each.targetRemove(target);
		}

		target.deleteMe();
		World.get().storeObject(this);
		World.get().addVisibleObject(this);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			this.onPerceive(pc);
		}
		master.addPet(this);
		if (master instanceof L1PcInstance) {
			// 增加物件組人
			addMaster((L1PcInstance) master);
		}
	}

	@Override
	public void receiveDamage(final L1Character attacker, final int damage) { // 攻擊ＨＰ減使用
		ISASCAPE = false;
		if (this.getCurrentHp() > 0) {
			if (damage > 0) {
				//this.setHate(attacker, 0); // 無 //TODO 召喚吸寶
				this.removeSkillEffect(FOG_OF_SLEEPING);
				if (!this.isExsistMaster()) {
					this._currentPetStatus = 1;
					this.setTarget(attacker);
				}
			}

			if ((attacker instanceof L1PcInstance) && (damage > 0)) {
				final L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
			}

			final int newHp = this.getCurrentHp() - damage;
			if (newHp <= 0) {
				this.Death(attacker);
			} else {
				this.setCurrentHp(newHp);
			}
		} else if (!this.isDead()) {// 念
			_log.error("NPC hp減少處理失敗 可能原因: 初始hp為0(" + this.getNpcId() + ")");
			this.Death(attacker);
		}
	}

	/**
	 * 死亡
	 * @param lastAttacker
	 */
	public synchronized void Death(final L1Character lastAttacker) {
		if (!this.isDead()) {
			this.setDead(true);
			this.setCurrentHp(0);
			this.setStatus(ActionCodes.ACTION_Die);

			this.getMap().setPassable(this.getLocation(), true);

			// 怪物解散處理
			L1Inventory targetInventory = null;// 主人的背包
			if (this._master != null) {
				if (this._master.getInventory() != null) {// 主人存在並且背包不為空
					targetInventory = this._master.getInventory();
				}
			}
			
			final List<L1ItemInstance> items = this._inventory.getItems();
			for (final L1ItemInstance item : items) {
				if (targetInventory != null) {
					// 容量重量確認及送信
					if (this._master.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
						this._inventory.tradeItem(item, item.getCount(), targetInventory);
						// 143:\f1%0%s 給你 %1%o 。
						((L1PcInstance) this._master).sendPackets(new S_ServerMessage(143, this.getName(), item.getLogName()));
					
					} else { // 超過持有物件數量(掉落地面)
						item.set_showId(this.get_showId());
						targetInventory = 
							World.get().getInventory(this.getX(), this.getY(), this.getMapId());
						this._inventory.tradeItem(item, item.getCount(), targetInventory);
					}
				} else { // 主人遺失(掉落地面)
					item.set_showId(this.get_showId());
					targetInventory = 
						World.get().getInventory(this.getX(), this.getY(), this.getMapId());
					this._inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}

			if (this._tamed && false) {
				this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Die));
				this.startDeleteTimer(ConfigAlt.NPC_DELETION_TIME * 2);
				
			} else {
				this.deleteMe();
			}
		}
	}

	public synchronized void returnToNature() {
		this._isReturnToNature = true;
		if (!this._tamed) {
			this.getMap().setPassable(this.getLocation(), true);
			// 怪物解散處理
			L1Inventory targetInventory = null;// 主人的背包
			if (this._master != null) {
				if (this._master.getInventory() != null) {// 主人存在並且背包不為空
					targetInventory = this._master.getInventory();
				}
			}
			final List<L1ItemInstance> items = this._inventory.getItems();
			for (final L1ItemInstance item : items) {
				if (targetInventory != null) {
					// 容量重量確認及送信
					if (this._master.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
						this._inventory.tradeItem(item, item.getCount(), targetInventory);
						// 143:\f1%0%s 給你 %1%o 。
						((L1PcInstance) this._master).sendPackets(new S_ServerMessage(143, this.getName(), item.getLogName()));
						
					} else { // 超過持有物件數量(掉落地面)
						item.set_showId(this.get_showId());
						targetInventory = 
							World.get().getInventory(this.getX(), this.getY(), this.getMapId());
						this._inventory.tradeItem(item, item.getCount(), targetInventory);
					}
					
				} else { // 主人遺失(掉落地面)
					item.set_showId(this.get_showId());
					targetInventory = 
						World.get().getInventory(this.getX(), this.getY(), this.getMapId());
					this._inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}
			this.deleteMe();
			
		} else {
			this.liberate();
		}
	}

	// 消去處理
	@Override
	public void deleteMe() {
		if (_master != null) {
			_master.removePet(this);
		}
		if (_destroyed) {
			return;
		}
		if (!_tamed && !_isReturnToNature) {
			broadcastPacketX8(new S_SkillSound(getId(), 169));
		}
		//_master.getPetList().remove(getId());
		super.deleteMe();
	}

	// 、時解放處理
	public void liberate() {
		final L1MonsterInstance monster = new L1MonsterInstance(this.getNpcTemplate());
		monster.setId(IdFactoryNpc.get().nextId());

		monster.setX(this.getX());
		monster.setY(this.getY());
		monster.setMap(this.getMapId());
		monster.setHeading(this.getHeading());
		monster.set_storeDroped(true);
		monster.setInventory(this.getInventory());
		this.setInventory(null);
		monster.setCurrentHpDirect(this.getCurrentHp());
		monster.setCurrentMpDirect(this.getCurrentMp());
		monster.setExp(0);

		this.deleteMe();
		World.get().storeObject(monster);
		World.get().addVisibleObject(monster);
	}

	public void setTarget(final L1Character target) {
		if ((target != null) && (
				(this._currentPetStatus == 1) || 
				(this._currentPetStatus == 2) || 
				(this._currentPetStatus == 5)
				)) {
			this.setHate(target, 0);
			if (!this.isAiRunning()) {
				this.startAI();
			}
		}
	}

	/**
	 * 設置主人目標
	 * @param target
	 */
	public void setMasterTarget(final L1Character target) {
		//System.out.println("設置主人目標");
		if ((target != null) && (
				(_currentPetStatus == 1) || // 攻擊
				(_currentPetStatus == 5)// 警戒
				)) {
			setHate(target, 10);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	/**
	 * 對該物件攻擊的調用
	 */
	@Override
	public void onAction(final L1PcInstance player) {
		if (player == null) {
			return;
		}
		final L1Character cha = this.getMaster();
		if (cha == null) {
			return;
		}
		final L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) { // 傳送處理中
			return;
		}
//		if (master.equals(player)) {// 攻擊者是主人
//			final L1AttackMode attack_mortion = new L1AttackPc(player, this); // 攻擊判斷
//			attack_mortion.action();
//			return;
//		}
		if ((this.isSafetyZone() || player.isSafetyZone()) && this.isExsistMaster()) {// 安全區域中
			final L1AttackMode attack_mortion = new L1AttackPc(player, this); // 攻擊判斷
			attack_mortion.action();
			return;
		}

		if (player.checkNonPvP(player, this)) {
			return;
		}
		
		final L1AttackMode attack = new L1AttackPc(player, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		if (this.isDead()) {
			return;
		}
		if (this._master.equals(player)) {
			player.sendPackets(new S_PetMenuPacket(this, 0));
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
		final int status = Integer.parseInt(action);
		//int status = ActionType(action);
		switch (status) {
		case 0:
			return;

		case 6:// 解散
			if (this._tamed) {
				// 、解放
//				this.liberate();
				this.Death(null);
			} else {
				// 解散
				this.Death(null);
			}
			break;

		default:
			// 同主人狀態更新
			final Object[] petList = this._master.getPetList().values().toArray();
			for (final Object petObject : petList) {
				if (petObject instanceof L1SummonInstance) {
					// 
					final L1SummonInstance summon = (L1SummonInstance) petObject;
					summon.set_currentPetStatus(status);
					
				} else {
					// 
				}
			}
			break;
		}
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
		
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack_Summon(this, perceivedFrom));
			if (getMaster() != null) {
				if (perceivedFrom.getId() == getMaster().getId()) {
					perceivedFrom.sendPackets(new S_HPMeter(getId(), 100 * getCurrentHp() / getMaxHp()));
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onItemUse() {
		if (!this.isActived()) {
			// １００％確率使用
			this.useItem(USEITEM_HASTE, 100);
		}
		if (this.getCurrentHp() * 100 / this.getMaxHp() < 40) {
			// ＨＰ４０％
			// １００％確率回復使用
			this.useItem(USEITEM_HEAL, 100);
		}
	}

	@Override
	public void onGetItem(final L1ItemInstance item, boolean isGM) {
		if (this.getNpcTemplate().get_digestitem() > 0) {
			this.setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (this.getCurrentHp() != this.getMaxHp()) {
				this.useItem(USEITEM_HEAL, 100);
			}
		} else if (Arrays
				.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			this.useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, this.getMaxHp());

		if (this.getCurrentHp() == currentHp) {
			return;
		}
		
		this.setCurrentHpDirect(currentHp);

		// 寵物血條更新
		if (this._master instanceof L1PcInstance) {
			final int hpRatio = 100 * currentHp / this.getMaxHp();
			final L1PcInstance master = (L1PcInstance) this._master;
			master.sendPackets(new S_HPMeter(this.getId(), hpRatio));
		}
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, this.getMaxMp());

		if (this.getCurrentMp() == currentMp) {
			return;
		}
		
		this.setCurrentMpDirect(currentMp);
	}

	public void set_currentPetStatus(final int i) {
		this._currentPetStatus = i;
		set_tempModel();
		switch (this._currentPetStatus) {
		case 5:
			this.setHomeX(this.getX());
			this.setHomeY(this.getY());
			break;

		case 3:
			this.allTargetClear();
			break;

		default:
			if (!this.isAiRunning()) {
				this.startAI();
			}
			break;
		}
	}

	public int get_currentPetStatus() {
		return this._currentPetStatus;
	}

	/**
	 * 是否具有主人
	 * @return
	 */
	public boolean isExsistMaster() {
		boolean isExsistMaster = true;
		if (this.getMaster() != null) {
			final String masterName = this.getMaster().getName();
			if (World.get().getPlayer(masterName) == null) {
				isExsistMaster = false;
			}
		}
		return isExsistMaster;
	}
	
	private int _time = 0;

	/**
	 * 設置剩餘使用時間
	 * @return
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * 剩餘使用時間
	 * @param time
	 */
	public void set_time(final int time) {
		this._time = time;
	}

	private int _tempModel = 3;
	
	public void set_tempModel() {
		_tempModel = _currentPetStatus;
	}
	
	public void get_tempModel() {
		_currentPetStatus = _tempModel;
	}
}
