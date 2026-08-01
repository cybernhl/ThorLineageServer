package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1FollowerInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.poison.L1Poison;
import com.lineage.server.model.skill.L1SkillTimer;
import com.lineage.server.model.skill.L1SkillTimerCreator;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;

/**
 * L1Character
 * @author daien
 *
 */
public class L1Character extends L1Object {

	private static final Log _log = LogFactory.getLog(L1Character.class);

	private static final long serialVersionUID = 1L;

	private L1Poison _poison = null;
	
	private boolean _sleeped;

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	
	private final HashMap<Integer, L1SkillTimer> _skillEffect = new HashMap<Integer, L1SkillTimer>();
	
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();
	
	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	public L1Character() {
		this._level = 1;
	}

	public final HashMap<Integer, L1SkillTimer> getSkillEffects() {
		return _skillEffect;
	}
	/**
	 * 物件復活
	 *
	 * @param hp
	 *            復活後的HP
	 */
	public void resurrect(int hp) {
		try {
			if (!isDead()) {
				return;
			}
			if (hp <= 0) {
				hp = 1;
			}
			// 設置為未死亡
			setDead(false);
			// 設置HP
			setCurrentHp(hp);
			// 設置狀態
			setStatus(0);
			// 解除變身
			L1PolyMorph.undoPoly(this);
			
			// 重新認識物件
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.sendPackets(new S_RemoveObject(this));
				pc.removeKnownObject(this);
				pc.updateObject();
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	
    private int _gongji=0;

    /**
     * 是否被攻擊
     * 
     * @return 是否被攻擊
     */
    public int getgongji() {
        return _gongji;
    }

    /**
     * 是否被攻擊
     * 
     * @param i
     */
    // 特殊處理場合（送信等）
    public void setgongji(final int i) {
        _gongji = i;
    }

	private int _secHp = -1;// 上次HP異動前HP

	/**
	 * 發送該物件可見HP
	 * @param pc
	 */
	public void broadcastPacketHP(L1PcInstance pc) {
		try {
			// 副本ID相等 必須在這方法之前先判斷
			// 記錄HP不相等於 目前HP
			if (_secHp != getCurrentHp()) {
				_secHp = getCurrentHp();
				pc.sendPackets(new S_HPMeter(this));
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	public void broadcastPacket(ServerBasePacket packet) {

		ArrayList<?> objs = World.get().getVisiblePlayer(this);
		  try {
		     L1PcInstance pc = null;
		     for (int i = 0; i < objs.size(); i++) {
		         pc = (L1PcInstance)objs.get(i);
		 
		     if ((pc.getMapId() < 16384) || (pc.getMapId() > 25088) || (pc.getInnKeyId() == getInnKeyId()))
		          pc.sendPackets(packet);
		     	}
		     } catch (Exception e) {
		       _log.error(e.getLocalizedMessage(), e);
		     } finally {
		       objs.clear();
		       objs = null;
		     }
		   }
	
	private int _currentHp;

	/**
	 * 現在的HP
	 *
	 * @return 現在HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * 設置新HP
	 *
	 * @param i
	 */
	// 特殊處理場合（送信等）
	public void setCurrentHp(final int i) {
		_currentHp = i;
		if (_currentHp >= getMaxHp()) {
			_currentHp = getMaxHp();
		}
	}

	/**
	 * 設置登場物件HP
	 *
	 * @param i
	 */
	public void setCurrentHpDirect(final int i) {
		_currentHp = i;
	}

	private int _currentMp;

	/**
	 * 現在的MP
	 *
	 * @return 現在MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * 設置新MP
	 *
	 * @param i
	 */
	public void setCurrentMp(final int i) {
		_currentMp = i;
		if (_currentMp >= getMaxMp()) {
			_currentMp = getMaxMp();
		}
	}

	/**
	 * 設置登場物件MP
	 *
	 * @param i
	 */
	public void setCurrentMpDirect(final int i) {
		_currentMp = i;
	}

	/**
	 * 是否為睡眠狀態
	 *
	 * @return true:麻痺狀態 false:無
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * 是否為睡眠狀態
	 *
	 * @param sleeped true:睡眠狀態 false:無
	 */
	public void setSleeped(final boolean sleeped) {
		this._sleeped = sleeped;
	}

	/**
	 * 無法攻擊/使用道具/技能/回城的狀態
	 *
	 * @return true:狀態中 false:無
	 */
	public boolean isParalyzedX() {
		// 冰矛圍籬
		if (hasSkillEffect(ICE_LANCE)) {
			return true;
		}
		// 冰雪颶風
		if (hasSkillEffect(FREEZING_BLIZZARD)) {
			return true;
		}
		// 大地屏障
		if (hasSkillEffect(EARTH_BIND)) {
			return true;
		}
		// 衝擊之暈
		if (hasSkillEffect(SHOCK_STUN)) {
			return true;
		}
		// 木乃伊的詛咒
		if (hasSkillEffect(CURSE_PARALYZE)) {
			return true;
		}
		return false;
	}
	
	private boolean _paralyzed;// 麻痺狀態
	private int _innKeyId;
	


	/**
	 * 是否為麻痺狀態
	 *
	 * @return true:麻痺狀態 false:無
	 */
	public boolean isParalyzed() {
		return this._paralyzed;
	}

	/**
	 * 設定麻痺狀態
	 *
	 * @param paralyzed true:麻痺狀態 false:無
	 */
	public void setParalyzed(final boolean paralyzed) {
		this._paralyzed = paralyzed;
	}

	L1Paralysis _paralysis;

	public L1Paralysis getParalysis() {
		return this._paralysis;
	}

	public void setParalaysis(final L1Paralysis p) {
		this._paralysis = p;
	}

	public void cureParalaysis() {
		if (this._paralysis != null) {
			this._paralysis.cure();
		}
	}

	/**
	 * 該物件全部可見範圍封包發送(不包含自己)
	 * @param packet 封包
	 */
	public void broadcastPacketAll(final OpcodesServer packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this)) {
				// 副本ID相等
				if (pc.get_showId() == this.get_showId()) {
					// 旅館內判斷
					if (pc.getMapId() < 16384 || pc.getMapId() > 25088 || pc.getInnKeyId() == this.getInnKeyId())
					pc.sendPackets(packet);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(範圍10)
	 * @param packet 封包
	 */
	public void broadcastPacketX10(final OpcodesServer packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 10)) {
				// 副本ID相等
				if (pc.get_showId() == this.get_showId()) {
					if (pc.getMapId() < 16384 || pc.getMapId() > 25088 || pc.getInnKeyId() == this.getInnKeyId())
					pc.sendPackets(packet);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(範圍8)
	 * @param packet 封包
	 */
	public void broadcastPacketX8(final OpcodesServer packet) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 8)) {
				// 副本ID相等
				if (pc.get_showId() == this.get_showId()) {
					if (pc.getMapId() < 16384 || pc.getMapId() > 25088 || pc.getInnKeyId() == this.getInnKeyId())
					pc.sendPackets(packet);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件指定範圍封包發送(指定範圍)
	 * @param packet 封包
	 * @param r 指定範圍
	 */
	public void broadcastPacketXR(final OpcodesServer packet, final int r) {
		try {
			for (final L1PcInstance pc : World.get().getVisiblePlayer(this, r)) {
				// 副本ID相等
				if (pc.get_showId() == this.get_showId()) {
					// 旅館內判斷
					if (pc.getMapId() < 16384 || pc.getMapId() > 25088 || pc.getInnKeyId() == this.getInnKeyId())
					pc.sendPackets(packet);
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該物件50格範圍封包發送
	 * @param packet 封包
	 */
	public void wideBroadcastPacket(final ServerBasePacket packet) {
		for (final L1PcInstance pc : World.get().getVisiblePlayer(this, 50)) {
			// 副本ID相等
			if (pc.get_showId() == this.get_showId()) {
				pc.sendPackets(packet);
			}
		}
	}

	/**
	 * 該物件可見範圍封包發送, (指定物件)
	 * @param packet 封包
	 * @param target 指定物件
	 */
	public void broadcastPacketExceptTargetSight(final ServerBasePacket packet, final L1Character target) {
		boolean isX8 = false;
		// 檢查城堡戰爭狀態
		if (ServerWarExecutor.get().checkCastleWar() > 0) {
			isX8 = true;;
		}
		if (isX8) {
			for (final L1PcInstance tgpc : World.get().getVisiblePlayerExceptTargetSight(this, target, 8)) {
				tgpc.sendPackets(packet);
			}
			
		} else {
			for (final L1PcInstance tgpc : World.get().getVisiblePlayerExceptTargetSight(this, target)) {
				tgpc.sendPackets(packet);
			}
		}
	}

	// 正向
	protected static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	protected static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 正面座標返。
	 *
	 * @return 正面座標
	 */
	public int[] getFrontLoc() {
		final int[] loc = new int[2];
		int x = this.getX();
		int y = this.getY();
		final int heading = this.getHeading();

		x += HEADING_TABLE_X[heading];
		y += HEADING_TABLE_Y[heading];

		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * 指定座標對硬的面向
	 *
	 * @param tx 座標 X值
	 * @param ty 座標 Y值
	 * @return 指定座標對硬的面向
	 */
	public int targetDirection(final int tx, final int ty) {

		final float dis_x = Math.abs(this.getX() - tx); // X點方向距離
		final float dis_y = Math.abs(this.getY() - ty); // Y點方向距離
		final float dis = Math.max(dis_x, dis_y); // 取回2者最大質
		if (dis == 0) {
			return this.getHeading(); // 距離為0表示不須改變面向
		}
		final int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右優先丸
		final int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右優先丸

		int dir_x = 0;
		int dir_y = 0;
		if (this.getX() < tx) {
			dir_x = 1;
		}
		if (this.getX() > tx) {
			dir_x = -1;
		}
		if (this.getY() < ty) {
			dir_y = 1;
		}
		if (this.getY() > ty) {
			dir_y = -1;
		}

		if (avg_x == 0) {
			dir_x = 0;
		}
		if (avg_y == 0) {
			dir_y = 0;
		}

		switch (dir_x) {
		case -1:
			switch (dir_y) {
			case -1:
				return 7; // 左
			case 0:
				return 6; // 左下
			case 1:
				return 5; // 下
			}
			break;
		case 0:
			switch (dir_y) {
			case -1:
				return 0; // 左上
			case 1:
				return 4; // 右下
			}
			break;
		case 1:
			switch (dir_y) {
			case -1:
				return 1; // 上
			case 0:
				return 2; // 右上
			case 1:
				return 3; // 右
			}
			break;
		}
		return this.getHeading(); // 。
	}

	/**
	 * 指定座標直線上、障害物存在**返。
	 *
	 * @param tx
	 *            座標X值
	 * @param ty
	 *            座標Y值
	 * @return 障害物無true、false返。
	 */
	public boolean glanceCheck(final int tx, final int ty) {
		final L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		//final int arw = 0;
		
		for (int i = 0; i < 30; i++) {
			if (((chx == tx) && (chy == ty)) || (((chx + 1) == tx) && ((chy - 1) == ty))
					|| (((chx + 1) == tx) && (chy == ty)) || (((chx + 1) == tx) && ((chy + 1) == ty))
					|| ((chx == tx) && ((chy + 1) == ty)) || (((chx - 1) == tx) && ((chy + 1) == ty))
					|| (((chx - 1) == tx) && (chy == ty)) || (((chx - 1) == tx) && ((chy - 1) == ty))
					|| ((chx == tx) && ((chy - 1) == ty))) {
				break;

			} else {
				int th = targetDirection(tx, ty);
				if (!map.isArrowPassable(chx, chy, th)) {
					return false;
				}
				if (chx < tx) {
					if (chy == ty) {
						chx++;
						
					} else if (chy > ty) {
						chx++;
						chy--;

					} else if (chy < ty) {
						chx++;
						chy++;

					}
					
				} else if (chx == tx) {
					if (chy < ty) {
						chy++;

					} else if (chy > ty) {
						chy--;
					}
					
				} else if (chx > tx) {
					if (chy == ty) {
						chx--;

					} else if (chy < ty) {
						chx--;
						chy++;

					} else if (chy > ty) {
						chx--;
						chy--;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 指定座標攻擊可能返。
	 *
	 * @param x
	 *            座標X值。
	 * @param y
	 *            座標Y值。
	 * @param range
	 *            攻擊可能範圍(數)
	 * @return 攻擊可能true,不可能false
	 */
	public boolean isAttackPosition(final int x, final int y, final int range) {
		if (range >= 7) {// 遠隔武器（７以上場合斜考慮畫面外出)
			if (getLocation().getTileDistance(new Point(x, y)) > range) {
				return false;
			}

		} else {// 近接武器
			if (getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		return this.glanceCheck(x, y);
	}

	/**
	 * 傳回背包資料
	 *
	 * @return L1Inventory
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * 、新效果追加。
	 *
	 * @param skillId
	 *            追加效果ID。
	 * @param timeMillis
	 *            追加效果持續時間。無限場合0。
	 */
	private void addSkillEffect(final int skillId, final int timeMillis) {
		L1SkillTimer timer = null;
		if (timeMillis > 0) {
			timer = L1SkillTimerCreator.create(this, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * 、效果設定。<br>
	 * 重複場合、新效果追加。<br>
	 * 重複場合、殘效果時間效果時間長方優先設定。
	 *
	 * @param skillId
	 *            設定效果ID。
	 * @param timeMillis 效果時間(單位:毫秒)
	 */
	public void setSkillEffect(final int skillId, final int timeMillis) {
		//System.out.println(skillId + "/" + timeMillis);
		if (hasSkillEffect(skillId)) {
			final int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			// 殘時間有限、效果時間方長無限場合上書。
			if ((remainingTimeMills >= 0)
					&& ((remainingTimeMills < timeMillis) || (timeMillis == 0))) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
			
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * 技能效果結束
	 *
	 * @param skillId 技能編號
	 */
	public void removeSkillEffect(final int skillId) {
		final L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}
	/**
	 * 刪除全部技能效果
	 */
	public void clearAllSkill() {
		for (final L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.end();
			}
		}
		_skillEffect.clear();
	}
	
	/**
	 * 指定技能效果消除
	 *
	 * @param skillId 技能編號
	 */
	public void killSkillEffectTimer(final int skillId) {
		final L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * 刪除全部技能效果
	 */
	public void clearSkillEffectTimer() {
		for (final L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.kill();
			}
		}
		_skillEffect.clear();
	}

	/**
	 * 是否有該技能效果
	 *
	 * @param skillId
	 * @return 有true 無false。
	 */
	public boolean hasSkillEffect(final int skillId) {
		return _skillEffect.containsKey(skillId);
	}
	
	/**
	 * 該物件目前具有的技能編號
	 * @return
	 */
	public Set<Integer> getSkillEffect() {
		return _skillEffect.keySet();
	}
	
	/**
	 * 該物件目前具有技能狀態
	 * @return true:沒有 false:有
	 */
	public boolean getSkillisEmpty() {
		return _skillEffect.isEmpty();
	}

	/**
	 * 技能效果剩餘時間
	 *
	 * @param skillId
	 * @return 剩餘秒數 無時間限制傳回-1
	 */
	public int getSkillEffectTimeSec(final int skillId) {
		final L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

	private boolean _isSkillDelay = false;

	/**
	 * 設定技能施放延遲中
	 * @param flag true:是 false:否
	 */
	public void setSkillDelay(final boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * 是否在技能施放延遲中
	 * @return true:是 false:否
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * 物件使用延遲編號設置
	 *
	 * @param delayId 延遲編號
	 * @param timer 時間(毫秒)
	 */
	public void addItemDelay(final int delayId, final L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * 物件使用延遲編號移除
	 *
	 * @param delayId 延遲編號
	 */
	public void removeItemDelay(final int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * 是否為延遲使用的物件
	 *
	 * @param delayId 延遲編號
	 * @return  true:是 false:否
	 */
	public boolean hasItemDelay(final int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * 是否為延遲使用的物件
	 *
	 * @param delayId 延遲編號
	 * @return 物件延遲設置
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(final int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * 加入寵物清單
	 *
	 * @param npc 
	 */
	public void addPet(final L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
		// 加入寵物控制介面
		sendPetCtrlMenu(npc, true);
	}

	/**
	 * 移除寵物清單
	 *
	 * @param npc 
	 */
	public void removePet(final L1NpcInstance npc) {
		_petlist.remove(npc.getId());
		// 移除寵物控制介面
		sendPetCtrlMenu(npc, false);
	}

	/**
	 * 傳回寵物控制清單
	 *
	 * @return 
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/**
	 * 寵物選單控制
	 * 
	 * @param npc
	 * @param type true:顯示 false:關閉
	 */
	private final void sendPetCtrlMenu(L1NpcInstance npc, boolean type) {
		if (npc instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) npc;
			L1Character cha = pet.getMaster();
			
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PetCtrlMenu(pc, pet, type));
				
				if (type) {
					pc.sendPackets(new S_HPMeter(pet));
				}
			}
			
		} else if (npc instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) npc;
			L1Character cha = summon.getMaster();
			
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PetCtrlMenu(pc, summon, type));
				
				if (type) {
					pc.sendPackets(new S_HPMeter(summon));
				}
			}
		}
	}

	private final Map<Integer, L1DollInstance> _dolls = new HashMap<Integer, L1DollInstance>();
	
	/**
	 * 設置娃娃
	 * @param doll
	 */
	public void addDoll(final L1DollInstance doll) {
		_dolls.put(doll.getItemObjId(), doll);
	}

	/**
	 * 移除娃娃
	 */
	public void removeDoll(final L1DollInstance doll) {
		_dolls.remove(doll.getItemObjId());
	}

	/**
	 * 移除娃娃
	 * @return 
	 */
	public L1DollInstance getDoll(final int key) {
		return _dolls.get(key);
	}

	/**
	 * 目前攜帶的娃娃
	 * @return 目前攜帶的娃娃
	 */
	public Map<Integer, L1DollInstance> getDolls() {
		return _dolls;
	}

	/**
	 * 加入跟隨者
	 * @param follower 跟隨者
	 */
	public void addFollower(final L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/**
	 * 移除跟隨者
	 * @param follower 跟隨者
	 */
	public void removeFollower(final L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/**
	 * 傳回跟隨者
	 * @return 跟隨者清單
	 */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}

	/**
	 * 、毒追加。
	 *
	 * @param poison
	 *            毒表、L1Poison。
	 */
	public void setPoison(final L1Poison poison) {
		this._poison = poison;
	}

	/**
	 * 毒治療。
	 */
	public void curePoison() {
		if (this._poison == null) {
			return;
		}
		this._poison.cure();
	}

	/**
	 * 毒狀態返。
	 *
	 * @return 毒表、L1Poison。
	 */
	public L1Poison getPoison() {
		return this._poison;
	}

	/**
	 * 毒付加
	 *
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(final int effectId) {
		this.broadcastPacketX8(new S_Poison(this.getId(), effectId));
	}

	/**
	 * 所在位置區域屬性返回
	 * @return 0一般區域 1安全區域 -1戰鬥區域
	 */
	public int getZoneType() {
		if (this.getMap().isSafetyZone(this.getLocation())) {
			return 1;
		} else if (this.getMap().isCombatZone(this.getLocation())) {
			return -1;
		} else { // 
			return 0;
		}
	}
	
	/**
	 * 位於安全區域中
	 * @return
	 */
	public boolean isSafetyZone() {
		if (this.getMap().isSafetyZone(this.getLocation())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 位於戰鬥區域中
	 * @return
	 */
	public boolean isCombatZone() {
		if (this.getMap().isCombatZone(this.getLocation())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 位於一般區域中
	 * @return
	 */
	public boolean isNoneZone() {
		return getZoneType() == 0;
	}

	private long _exp; // ● 經驗值

	/**
	 * 保持經驗值返。
	 *
	 * @return 經驗值。
	 */
	public long getExp() {
		return this._exp;
	}

	/**
	 * 保持經驗值設定。
	 *
	 * @param exp
	 *            經驗值。
	 */
	public void setExp(final long exp) {
		this._exp = exp;
	}

	// ■■■■■■■■■■ L1PcInstance移動 ■■■■■■■■■■
	private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
	private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	/**
	 * 是否為已認識物件
	 *
	 * @param obj 判斷對像
	 * @return true:是 false:不是
	 */
	public boolean knownsObject(final L1Object obj) {
		return this._knownObjects.contains(obj);
	}

	/**
	 * 全部認識物件(L1Object)清單
	 *
	 * @return 全部認識物件(L1Object)清單
	 */
	public List<L1Object> getKnownObjects() {
		return this._knownObjects;
	}

	/**
	 * 全部認識物件(PC)清單
	 *
	 * @return 全部認識物件(PC)清單
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return this._knownPlayer;
	}

	/**
	 * 加入已認識物件
	 *
	 * @param obj 加入對像
	 */
	public void addKnownObject(final L1Object obj) {
		if (!this._knownObjects.contains(obj)) {
			this._knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				this._knownPlayer.add((L1PcInstance) obj);
			}
		}
	}

	/**
	 * 移出已認識物件
	 *
	 * @param obj 移出對像
	 */
	public void removeKnownObject(final L1Object obj) {
		this._knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			this._knownPlayer.remove(obj);
		}
	}

	/**
	 * 全部認識對像移除
	 */
	public void removeAllKnownObjects() {
		this._knownObjects.clear();
		this._knownPlayer.clear();
	}

	// ■■■■■■■■■■  ■■■■■■■■■■

	private String _name; // ● 名前

	public String getName() {
		return this._name;
	}

	public void setName(final String s) {
		this._name = s;
	}

	private int _level; // ● 

	public int getLevel() {
		return this._level;
	}

	public void setLevel(final int level) {
		this._level = level;
	}

	private int _maxHp = 0; // 最大HP量(MOB 1~100000, 其他 1~32767)
	private int _trueMaxHp = 0; // ● 本當ＭＡＸＨＰ

	/**
	 * 最大HP量
	 * @return
	 */
	public int getMaxHp() {
		return (this._maxHp);
	}

	/**
	 * 最大HP
	 * @param hp
	 */
	public void setMaxHp(final int hp) {
		this._trueMaxHp = hp;
		this._maxHp = RangeInt.ensure(this._trueMaxHp, 1, 500000);
		this._currentHp = Math.min(this._currentHp, this._maxHp);
	}

	/**
	 * 增加(減少)HP上限
	 * @param i
	 */
	public void addMaxHp(final int i) {
		this.setMaxHp(this._trueMaxHp + i);
	}

	private short _maxMp = 0; // ● ＭＡＸＭＰ（0～32767）
	private int _trueMaxMp = 0; // ● 本當ＭＡＸＭＰ

	/**
	 * 最大MP量
	 * @return
	 */
	public short getMaxMp() {
		return (this._maxMp);
	}

	/**
	 * 最大MP
	 * @param mp
	 */
	public void setMaxMp(final int mp) {
		this._trueMaxMp = mp;
		this._maxMp = (short) RangeInt.ensure(this._trueMaxMp, 0, 32767);
		this._currentMp = Math.min(this._currentMp, this._maxMp);
	}

	/**
	 * 增加(減少)MP上限
	 * @param i
	 */
	public void addMaxMp(final int i) {
		this.setMaxMp(this._trueMaxMp + i);
	}

	private int _ac = 10; // ● ＡＣ（-211～44）
	private int _trueAc = 0; // ● 本當ＡＣ

	public int getAc() {
		int ac = _ac;
	
		return RangeInt.ensure(ac, -211, 44);
	}

	public void setAc(final int i) {
		this._trueAc = i;
		this._ac = RangeInt.ensure(i, -211, 44);
	}

	/**
	 * 增加(減少)防禦力
	 * @param i
	 */
	public void addAc(final int i) {
		this.setAc(this._trueAc + i);
	}

	private short _str = 0; // ● ＳＴＲ（1～127）
	private short _trueStr = 0; // ● 本當ＳＴＲ

	/**
	 * 力量
	 * @return
	 */
	public short getStr() {
		return (this._str);
	}

	public void setStr(final int i) {
		this._trueStr = (short) i;
		this._str = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)力量
	 * @param i
	 */
	public void addStr(final int i) {
		this.setStr(this._trueStr + i);
	}

	private short _con = 0; // ● ＣＯＮ（1～127）
	private short _trueCon = 0; // ● 本當ＣＯＮ

	/**
	 * 體質
	 * @return
	 */
	public short getCon() {
		return (this._con);
	}

	public void setCon(final int i) {
		this._trueCon = (short) i;
		this._con = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)體質
	 * @param i
	 */
	public void addCon(final int i) {
		this.setCon(this._trueCon + i);
	}

	private short _dex = 0; // ● ＤＥＸ（1～127）
	private short _trueDex = 0; // ● 本當ＤＥＸ

	/**
	 * 敏捷
	 * @return
	 */
	public short getDex() {
		return (this._dex);
	}

	public void setDex(final int i) {
		this._trueDex = (short) i;
		this._dex = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)敏捷
	 * @param i
	 */
	public void addDex(final int i) {
		this.setDex(this._trueDex + i);
	}

	private short _cha = 0; // ● ＣＨＡ（1～127）
	private short _trueCha = 0; // ● 本當ＣＨＡ

	/**
	 * 魅力
	 * @return
	 */
	public short getCha() {
		return (this._cha);
	}

	public void setCha(final int i) {
		this._trueCha = (short) i;
		this._cha = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)魅力
	 * @param i
	 */
	public void addCha(final int i) {
		this.setCha(this._trueCha + i);
	}

	private short _int = 0; // ● ＩＮＴ（1～127）
	private short _trueInt = 0; // ● 本當ＩＮＴ

	/**
	 * 智力
	 * @return
	 */
	public short getInt() {
		return (this._int);
	}

	public void setInt(final int i) {
		this._trueInt = (short) i;
		this._int = (short) RangeInt.ensure(i, 1, 254);
	}

	/**
	 * 增加(減少)智力
	 * @param i
	 */
	public void addInt(final int i) {
		this.setInt(this._trueInt + i);
	}

	private short _wis = 0; // ● ＷＩＳ（1～127）
	private short _trueWis = 0; // ● 本當ＷＩＳ

	/**
	 * 精神
	 * @return
	 */
	public short getWis() {
		return (this._wis);
	}

	public void setWis(final int i) {
		this._trueWis = (short) i;
		this._wis = (short) RangeInt.ensure(i, 1, 254);
	}
	
	/*      */   public int getInnKeyId() {
		/* 1740 */     return this._innKeyId;
		/*      */   }
		/*      */ 
		/*      */   public void setInnKeyId(int i) {
		/* 1744 */     this._innKeyId = i;
		/*      */   }

	/**
	 * 增加(減少)精神
	 * @param i
	 */
	public void addWis(final int i) {
		this.setWis(this._trueWis + i);
	}

	private int _wind = 0; // ● 風防禦（-128～127）
	private int _trueWind = 0; // ● 本當風防禦

	/**
	 * 風屬性
	 * @return
	 */
	public int getWind() {
		return this._wind;
	} // 使用

	/**
	 * 增加(減少)風屬性
	 * @param i
	 */
	public void addWind(final int i) {
		this._trueWind += i;
		if (this._trueWind >= 127) {
			this._wind = 127;
		} else if (this._trueWind <= -128) {
			this._wind = -128;
		} else {
			this._wind = this._trueWind;
		}
	}

	private int _water = 0; // ● 水防禦（-128～127）
	private int _trueWater = 0; // ● 本當水防禦

	/**
	 * 水屬性
	 * @return
	 */
	public int getWater() {
		return this._water;
	} // 使用

	/**
	 * 增加(減少)水屬性
	 * @param i
	 */
	public void addWater(final int i) {
		this._trueWater += i;
		if (this._trueWater >= 127) {
			this._water = 127;
		} else if (this._trueWater <= -128) {
			this._water = -128;
		} else {
			this._water = this._trueWater;
		}
	}

	private int _fire = 0; // ● 火防禦（-128～127）
	private int _trueFire = 0; // ● 本當火防禦

	/**
	 * 火屬性
	 * @return
	 */
	public int getFire() {
		return this._fire;
	} // 使用

	/**
	 * 增加(減少)火屬性
	 * @param i
	 */
	public void addFire(final int i) {
		this._trueFire += i;
		if (this._trueFire >= 127) {
			this._fire = 127;
		} else if (this._trueFire <= -128) {
			this._fire = -128;
		} else {
			this._fire = this._trueFire;
		}
	}

	private int _earth = 0; // ● 地防禦（-128～127）
	private int _trueEarth = 0; // ● 本當地防禦

	/**
	 * 地屬性
	 * @return
	 */
	public int getEarth() {
		return this._earth;
	} // 使用

	/**
	 * 增加(減少)地屬性
	 * @param i
	 */
	public void addEarth(final int i) {
		this._trueEarth += i;
		if (this._trueEarth >= 127) {
			this._earth = 127;
			
		} else if (this._trueEarth <= -128) {
			this._earth = -128;
			
		} else {
			this._earth = this._trueEarth;
		}
	}

	private int _addAttrKind; // 減少屬性種類

	public int getAddAttrKind() {
		return this._addAttrKind;
	}

	public void setAddAttrKind(final int i) {
		this._addAttrKind = i;
	}

	// 暈眩耐性
	private int _registStun = 0;
	private int _trueRegistStun = 0;

	/**
	 * 暈眩耐性
	 * @return
	 */
	public int getRegistStun() {
		return this._registStun;
	} // 使用

	/**
	 * 暈眩耐性
	 * @param i
	 */
	public void addRegistStun(final int i) {
		this._trueRegistStun += i;
		if (this._trueRegistStun > 127) {
			this._registStun = 127;
		} else if (this._trueRegistStun < -128) {
			this._registStun = -128;
		} else {
			this._registStun = this._trueRegistStun;
		}
	}

	// 石化耐性
	private int _registStone = 0;
	private int _trueRegistStone = 0;

	/**
	 * 石化耐性
	 * @return
	 */
	public int getRegistStone() {
		return this._registStone;
	}

	/**
	 * 石化耐性
	 * @param i
	 */
	public void addRegistStone(final int i) {
		this._trueRegistStone += i;
		if (this._trueRegistStone > 127) {
			this._registStone = 127;
		} else if (this._trueRegistStone < -128) {
			this._registStone = -128;
		} else {
			this._registStone = this._trueRegistStone;
		}
	}

	// 睡眠耐性
	private int _registSleep = 0;
	private int _trueRegistSleep = 0;

	/**
	 * 睡眠耐性
	 * @return
	 */
	public int getRegistSleep() {
		return this._registSleep;
	}

	/**
	 * 睡眠耐性
	 * @param i
	 */
	public void addRegistSleep(final int i) {
		this._trueRegistSleep += i;
		if (this._trueRegistSleep > 127) {
			this._registSleep = 127;
		} else if (this._trueRegistSleep < -128) {
			this._registSleep = -128;
		} else {
			this._registSleep = this._trueRegistSleep;
		}
	}

	// 凍結耐性
	private int _registFreeze = 0;
	private int _trueRegistFreeze = 0;

	/**
	 * 寒冰耐性
	 * @return
	 */
	public int getRegistFreeze() {
		return this._registFreeze;
	}

	/**
	 * 寒冰耐性
	 * @param i
	 */
	public void add_regist_freeze(final int i) {
		this._trueRegistFreeze += i;
		if (this._trueRegistFreeze > 127) {
			this._registFreeze = 127;
		} else if (this._trueRegistFreeze < -128) {
			this._registFreeze = -128;
		} else {
			this._registFreeze = this._trueRegistFreeze;
		}
	}

	// 支撐耐性
	private int _registSustain = 0;
	private int _trueRegistSustain = 0;

	/**
	 * 支撐耐性
	 * @return
	 */
	public int getRegistSustain() {
		return this._registSustain;
	}

	/**
	 * 支撐耐性
	 * @param i
	 */
	public void addRegistSustain(final int i) {
		this._trueRegistSustain += i;
		if (this._trueRegistSustain > 127) {
			this._registSustain = 127;
		} else if (this._trueRegistSustain < -128) {
			this._registSustain = -128;
		} else {
			this._registSustain = this._trueRegistSustain;
		}
	}

	// 暗黑耐性
	private int _registBlind = 0;
	private int _trueRegistBlind = 0;

	/**
	 * 暗黑耐性
	 * @return
	 */
	public int getRegistBlind() {
		return this._registBlind;
	}
	
	/**
	 * 暗黑耐性
	 * @param i
	 */
	public void addRegistBlind(final int i) {
		this._trueRegistBlind += i;
		if (this._trueRegistBlind > 127) {
			this._registBlind = 127;
		} else if (this._trueRegistBlind < -128) {
			this._registBlind = -128;
		} else {
			this._registBlind = this._trueRegistBlind;
		}
	}

	private double chit = 0.0;
	public final double getChit() {
		return this.chit;
	}
	public final void addChit(final double add) {
		this.chit += add;
	}

	private int _dmgup = 0; // ● 補正（-128～127）
	private int _trueDmgup = 0; // ● 本當補正

	/**
	 * 傷害增加
	 * @return
	 */
	public int getDmgup() {
		return this._dmgup;
	} // 使用

	/**
	 * 傷害增加
	 * @param i
	 */
	public void addDmgup(final int i) {
		this._trueDmgup += i;
		if (this._trueDmgup >= 127) {
			this._dmgup = 127;
		} else if (this._trueDmgup <= -128) {
			this._dmgup = -128;
		} else {
			this._dmgup = this._trueDmgup;
		}
	}

	private int _bowDmgup = 0; // ● 弓補正（-128～127）
	private int _trueBowDmgup = 0; // ● 本當弓補正

	/**
	 * 遠距離傷害增加
	 * @return
	 */
	public int getBowDmgup() {
		return this._bowDmgup;
	} // 使用

	/**
	 * 遠距離傷害增加
	 * @param i
	 */
	public void addBowDmgup(final int i) {
		this._trueBowDmgup += i;
		if (this._trueBowDmgup >= 127) {
			this._bowDmgup = 127;
		} else if (this._trueBowDmgup <= -128) {
			this._bowDmgup = -128;
		} else {
			this._bowDmgup = this._trueBowDmgup;
		}
	}

	private int _hitup = 0; // ● 命中補正（-128～127）
	private int _trueHitup = 0; // ● 本當命中補正

	/**
	 * 命中增加
	 * @return
	 */
	public int getHitup() {
		return this._hitup;
	} // 使用

	/**
	 * 命中增加
	 * @param i
	 */
	public void addHitup(final int i) {
		this._trueHitup += i;
		if (this._trueHitup >= 127) {
			this._hitup = 127;
		} else if (this._trueHitup <= -128) {
			this._hitup = -128;
		} else {
			this._hitup = this._trueHitup;
		}
	}

	private int _bowHitup = 0; // ● 弓命中補正（-128～127）
	private int _trueBowHitup = 0; // ● 本當弓命中補正

	/**
	 * 遠距離命中增加
	 * @return
	 */
	public int getBowHitup() {
		return this._bowHitup;
	} // 使用

	/**
	 * 遠距離命中增加
	 * @param i
	 */
	public void addBowHitup(final int i) {
		this._trueBowHitup += i;
		if (this._trueBowHitup >= 127) {
			this._bowHitup = 127;
		} else if (this._trueBowHitup <= -128) {
			this._bowHitup = -128;
		} else {
			this._bowHitup = this._trueBowHitup;
		}
	}

	private int _mr = 0; // ● 魔法防禦（0～）
	private int _trueMr = 0; // ● 本當魔法防禦

	/**
	 * 魔防
	 * @return
	 */
	public int getMr() {
		if (this.hasSkillEffect(ERASE_MAGIC) == true) {// 魔法消除
			return this._mr >> 2;// / 4;
		} else {
			return this._mr;
		}
	} // 使用

	public int getTrueMr() {
		return this._trueMr;
	} // 

	/**
	 * 魔防
	 * @param i
	 */
	public void addMr(final int i) {
		this._trueMr += i;
		if (this._trueMr <= 0) {
			this._mr = 0;
		} else {
			this._mr = this._trueMr;
		}
	}

	private int _sp = 0; // ● 增加ＳＰ

	/**
	 * 魔功
	 * @return
	 */
	public int getSp() {
		return this.getTrueSp() + this._sp;
	}

	/**
	 * 魔功
	 * @return
	 */
	public int getTrueSp() {
		return this.getMagicLevel() + this.getMagicBonus();
	}

	/**
	 * 增加魔功
	 * @param i
	 */
	public void addSp(final int i) {
		this._sp += i;
	}

	private boolean _isDead; // 死亡狀態

	/**
	 * 死亡狀態
	 * @return
	 */
	public boolean isDead() {
		return this._isDead;
	}

	/**
	 * 死亡狀態
	 * @param flag
	 */
	public void setDead(final boolean flag) {
		this._isDead = flag;
	}

	private int _status; // 初始化狀態

	/**
	 * 初始化狀態
	 * @return
	 */
	public int getStatus() {
		return this._status;
	}

	/**
	 * 初始化狀態
	 * @param i
	 */
	public void setStatus(final int i) {
		this._status = i;
	}

	private String _title; // 封號

	/**
	 * 封號
	 * @return
	 */
	public String getTitle() {
		return this._title;
	}

	/**
	 * 封號
	 * @param s
	 */
	public void setTitle(final String s) {
		this._title = s;
	}

	private int _lawful; // ● 

	/**
	 * 傳回正義質
	 * @return
	 */
	public int getLawful() {
		return this._lawful;
	}

	/**
	 * 設定正義質
	 * @param i
	 */
	public void setLawful(final int i) {
		this._lawful = i;
		if (this instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) this;
			if (pet.getMaster() != null) {
				final L1PcInstance pc = ((L1PcInstance) pet.getMaster());
				pc.sendPacketsAll(new S_Lawful(pet));
			}
		}
	}

	public synchronized void addLawful(final int i) {
		this._lawful += i;
		if (this._lawful > 32767) {
			this._lawful = 32767;
			
		} else if (this._lawful < -32768) {
			this._lawful = -32768;
		}
		if (this instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) this;
			if (pet.getMaster() != null) {
				final L1PcInstance pc = ((L1PcInstance) pet.getMaster());
				pc.sendPacketsAll(new S_Lawful(pet));
			}
		}
		if (this instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) this;
			pc.sendPacketsAll(new S_Lawful(pc));
		}
	}

	private int _heading; // 面向： 0.左上 1.上 2.右上 3.右 4.右下 5.下 6.左下 7.左

	/**
	 * 面向
	 * @return 0:左上 1:上 2:右上 3:右 4:右下 5:下 6:左下 7:左
	 */
	public int getHeading() {
		return this._heading;
	}

	/**
	 * 面向
	 * @param i 0:左上 1:上 2:右上 3:右 4:右下 5:下 6:左下 7:左
	 */
	public void setHeading(final int i) {
		this._heading = i;
	}

	private int _moveSpeed; // 移動加速狀態(綠水)

	/**
	 * 移動加速狀態(綠水)
	 * @return 0:無 1:加速 2:緩速
	 */
	public int getMoveSpeed() {
		return this._moveSpeed;
	}

	/**
	 * 移動加速狀態(綠水)
	 * @param i 0:無 1:加速 2:緩速
	 */
	public void setMoveSpeed(final int i) {
		this._moveSpeed = i;
	}

	private int _braveSpeed; // 攻擊加速狀態(勇水)

	/**
	 * 攻擊加速狀態(勇水)
	 * @return 0:無 1:勇水 5:強化勇水
	 */
	public int getBraveSpeed() {
		return this._braveSpeed;
	}

	/**
	 * 攻擊加速狀態(勇水)
	 * @param i 0:無 1:勇水 5:強化勇水
	 */
	public void setBraveSpeed(final int i) {
		this._braveSpeed = i;
	}

	private int _tempCharGfx; // 顯示外型編號

	/**
	 * 傳回顯示外型編號
	 * @return
	 */
	public int getTempCharGfx() {
		return this._tempCharGfx;
	}

	/**
	 * 設置顯示外型編號
	 * @param i
	 */
	public void setTempCharGfx(final int i) {
		this._tempCharGfx = i;
	}

	private int _gfxid; // 原始外型編號

	/**
	 * 傳回原始外型編號
	 * @return
	 */
	public int getGfxId() {
		return this._gfxid;
	}

	/**
	 * 設置原始外型編號
	 * @param i
	 */
	public void setGfxId(final int i) {
		this._gfxid = i;
	}

	/**
	 * 魔法等級
	 * @return
	 */
	public int getMagicLevel() {
		return this.getLevel() >> 2;// / 4;
	}

	/**
	 * 智力命中魔法追加
	 * @return
	 */
	public int getMagicBonus() {
		switch (this.getInt()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return -2;

		case 6:
		case 7:
		case 8:
			return -1;

		case 9:
		case 10:
		case 11:
			return 0;

		case 12:
		case 13:
		case 14:
			return 1;

		case 15:
		case 16:
		case 17:
			return 2;

		case 18:
			return 3;
		case 19:
			return 4;
		case 20:
			return 5;
		case 21:
			return 6;
		case 22:
			return 7;
		case 23:
			return 8;
		case 24:
			return 9;
		case 25:
		case 26:
		case 27:
			return 10;
		case 28:
		case 29:
		case 30:
			return 11;
		case 31:
		case 32:
		case 33:
			return 12;
		case 34:
		case 35:
		case 36:
			return 13;
		case 37:
		case 38:
		case 39:
			return 14;
		case 40:
		case 41:
		case 42:
		case 43:
			return 15;
		/*case 44:
			return 17;
		case 45:
			return 18;
		case 46:
			return 19;
		case 47:
			return 20;
		case 48:
			return 21;
		case 49:
			return 22;
		case 50:
			return 23;
		case 51:
			return 24;
		case 52:
			return 25;
		case 53:
			return 26;
		case 54:
			return 27;
		case 55:
			return 28;
		case 56:
			return 29;
		case 57:
			return 30;
		case 58:
			return 31;
		case 59:
			return 32;
		case 60:
			return 33;
		case 61:
			return 34;
		case 62:
			return 35;
		case 63:
			return 36;
		case 64:
			return 37;
		case 65:
			return 38;
		case 66:
			return 39;
		case 67:
			return 40;
		case 68:
			return 41;
		case 69:
			return 42;
		case 70:
			return 43;
		case 71:
			return 44;
		case 72:
			return 45;
		case 73:
			return 46;
		case 74:
			return 47;
		case 75:
			return 48;
		case 76:
			return 49;
		case 77:
			return 50;
		case 78:
			return 51;
		case 79:
			return 52;
		case 80:
			return 53;
		case 81:
			return 54;
		case 82:
			return 55;
		case 83:
			return 56;
		case 84:
			return 57;
		case 85:
			return 58;
		case 86:
			return 59;
		case 87:
			return 60;
		case 88:
			return 61;
		case 89:
			return 62;
		case 90:
			return 63;*/
		default:
			return this.getInt() - 28;
		}
	}

	/**
	 * 是否在隱身狀態
	 * @return
	 */
	public boolean isInvisble() {
		return (this.hasSkillEffect(INVISIBILITY) || this.hasSkillEffect(BLIND_HIDING));
	}

	/**
	 * 治療
	 * @param pt 治療質
	 */
	public void healHp(final int pt) {
		this.setCurrentHp(this.getCurrentHp() + pt);
	}

	private int _karma;

	/**
	 * 保持返。
	 *
	 * @return 。
	 */
	public int getKarma() {
		return this._karma;
	}

	/**
	 * 保持設定。
	 *
	 * @param karma
	 *            。
	 */
	public void setKarma(final int karma) {
		this._karma = karma;
	}

	public void setMr(final int i) {
		this._trueMr = i;
		if (this._trueMr <= 0) {
			this._mr = 0;
		} else {
			this._mr = this._trueMr;
		}
	}

	/**
	 * 光
	 */
	public void turnOnOffLight() {
		int lightSize = 0x00;
		if (this instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) this;
			lightSize = npc.getLightSize(); // npc.sql
		}

		for (final L1ItemInstance item : this.getInventory().getItems()) {
			if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // light系
				final int itemlightSize = item.getItem().getLightRange();
				if ((itemlightSize != 0) && item.isNowLighting()) {
					if (itemlightSize > lightSize) {
						lightSize = itemlightSize;
					}
				}
			}
		}
		// 照明法術
		if (this.hasSkillEffect(LIGHT)) {
			lightSize = 0x0e;
		}

		// 人物
		if (this instanceof L1PcInstance) {
			if (ConfigOther.LIGHT) {
				lightSize = 0x14;
			}
			final L1PcInstance pc = (L1PcInstance) this;
			pc.sendPackets(new S_Light(pc.getId(), lightSize));
		}
		
		if (!this.isInvisble()) {
			this.broadcastPacketAll(new S_Light(this.getId(), lightSize));
		}

		this.setOwnLightSize(lightSize); // S_OwnCharPack範圍
		this.setChaLightSize(lightSize); // S_OtherCharPack, S_NPCPack範圍
	}

	private int _chaLightSize; // ● 範圍

	/**
	 * 物件原始亮度
	 * @return
	 */
	public int getChaLightSize() {
		if (this.isInvisble()) {
			return 0;
		}
		if (ConfigOther.LIGHT) {
			return 14;
		}
		return this._chaLightSize;
	}

	/**
	 * 設置原始亮度
	 * @param i
	 */
	public void setChaLightSize(final int i) {
		this._chaLightSize = i;
	}

	private int _ownLightSize; // ● 範圍(S_OwnCharPack用)

	/**
	 * 傳回附加亮度
	 * @return
	 */
	public int getOwnLightSize() {
		if (this.isInvisble()) {
			return 0;
		}
		if (ConfigOther.LIGHT) {
			return 14;
		}
		return this._ownLightSize;
	}

	/**
	 * 設置附加亮度
	 * @param i
	 */
	public void setOwnLightSize(final int i) {
		this._ownLightSize = i;
	}

	private int _tmp; // 緩存數據

	/**
	 * 傳出 緩存數據
	 * @return the _tmp
	 */
	public int get_tmp() {
		return _tmp;
	}

	/**
	 * 設置 緩存數據
	 * @param tmp 對 _tmp 進行設置
	 */
	public void set_tmp(int tmp) {
		this._tmp = tmp;
	}

	private int _tmp_mr; // 暫存數據(MR)

	/**
	 * 傳出 暫存數據(MR)
	 * @return the _tmp_mr
	 */
	public int get_tmp_mr() {
		return _tmp_mr;
	}

	/**
	 * 設置 暫存數據(MR)
	 * @param tmp 對 _tmp_mr 進行設置
	 */
	public void set_tmp_mr(int tmp) {
		this._tmp_mr = tmp;
	}

	// 閃避率 +
	private int _dodge_up = 0;

	/**
	 * 閃避增加
	 * @return
	 */
	public int get_dodge() {
		return _dodge_up;
	}

	/**
	 * 閃避增加
	 * @param i
	 */
	public void add_dodge(int i) {
		_dodge_up += i;
		if (_dodge_up > 10) {
			_dodge_up = 10;
			
		} else if (_dodge_up < 0){
			_dodge_up = 0;
		}
	}

	// 閃避率 -
	private int _dodge_down = 0;

	/**
	 * 閃避減少
	 * @return
	 */
	public int get_dodge_down() {
		return _dodge_down;
	}

	/**
	 * 閃避減少
	 * @param i
	 */
	public void add_dodge_down(int i) {
		_dodge_down += i;
		if (_dodge_down > 10) {
			_dodge_down = 10;
			
		} else if (_dodge_down < 0){
			_dodge_down = 0;
		}
	}
	
	private int _Bloodletting;

	/**
	 * 傳回被施加施毒術次數
	 * @return
	 */
	public int getBloodletting() {
		return _Bloodletting;
	}

	/**
	 * 設置被施加施毒術次數
	 * @param i
	 */
	public void setBloodletting(final int i) {
		_Bloodletting = i;
	}
	   /**
     * 是否為一般npc.
     * 
     * @return 是則返回true
     */
    public boolean isGeneralNpc() {
        return false;
    }

    /**
     * 該坐標點上是否有物件(不可穿透目標)
     * 
     * @param locx
     *            - 前方坐標x
     * @param locy
     *            - 前方坐標y
     * @return true有 false沒有
     */
    public boolean checkPassable(final int locx, final int locy) {
        boolean isPc = false;
        L1PcInstance pc = null;
        if (this instanceof L1PcInstance) {
            pc = (L1PcInstance) this;
            isPc = true;
        }

        if (isPc) {
            // 是副本專用地圖(不列入判斷的地形)
            if (QuestMapTable.get().isQuestMap(getMapId())) {
                return false;
            }
        }

        // 1格範圍內物件
        final Collection<L1Object> allObj = World.get().getVisibleObjects(this, 1);
        if (allObj.isEmpty()) {
            return false;
        }
        for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();) {
            final L1Object obj = iter.next();
            // 道具
            if (obj instanceof L1ItemInstance) {
                continue;
            }
            if (!(obj instanceof L1Character)) {
                continue;
            }
            final L1Character character = (L1Character) obj;
            // 忽略隱身
            if (character.isInvisble()) {
                continue;
            }

            // 對方是怪物
            if (character instanceof L1MonsterInstance) {
                final L1MonsterInstance mon = (L1MonsterInstance) character;
                if (mon.isDead()) {
                    continue;
                }
                if (mon.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_FLY) {
                    continue; // 飛天
                }
                if (mon.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
                    continue; // 遁地
                }
                if (mon.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_ICE) {
                    continue; // 冰凍(水晶洞)
                }
                if (mon.isInvisble()) {
                    continue;
                }

                if (isPc) { // 己方是玩家、忽略1次以避免誤判頻繁
                    if (pc.isMoveStatus()) {
                        pc.setMoveErrorCount(0);
                    } else {
                        if (pc.getMoveErrorCount() >= 2) {
                            pc.setMoveErrorCount(pc.getMoveErrorCount() - 1);
                            return true;
                        }
                    }

                    if ((mon.getX() == locx) && (mon.getY() == locy) && (mon.getMapId() == getMapId())) {// 其他
                        pc.setMoveErrorCount(pc.getMoveErrorCount() + 1);
                        pc.setMoveStatus(false);
                    } else {
                        pc.setMoveStatus(true);
                    }
                } else { // 己方是非玩家
                    if ((mon.getX() == locx) && (mon.getY() == locy) && (mon.getMapId() == getMapId())) {// 其他
                        return true;
                    }
                }
            }

            // 對方是玩家
            if (character instanceof L1PcInstance) {
                final L1PcInstance tgpc = (L1PcInstance) character;
                if (tgpc.isDead()) { // 忽略死亡
                    continue;
                }
                if (tgpc.isInvisble()) { // 忽略隱身
                    continue;
                }
                if (tgpc.isGmInvis()) {// 忽略GM隱身
                    continue;
                }
                if (tgpc.isGhost()) {// 忽略鬼魂模式
                    continue;
                }
                if ((tgpc.getX() == locx) && (tgpc.getY() == locy) && (tgpc.getMapId() == getMapId())) {// 其他
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否在城戰區(戰爭時間內).
     */
    public boolean isWarZone() {
        boolean flag = false;
        for (int i = 1; i < 9; i++) {
            if (ServerWarExecutor.get().isNowWar(i)) {
                final int[] loc = L1CastleLocation.getWarArea(i);
                final int locx1 = loc[0];
                final int locx2 = loc[1];
                final int locy1 = loc[2];
                final int locy2 = loc[3];
                final short mapid = (short) loc[4];
                if (this.getMapId() == mapid) {
                    if (this.getX() >= locx1 && this.getX() <= locx2) {
                        if (this.getY() >= locy1 && this.getY() <= locy2) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }
	// 旅館
	private int _innRoomNumber;

	public int getInnRoomNumber() {
		return _innRoomNumber;
	}

	public void setInnRoomNumber(int i) {
		_innRoomNumber = i;
	}
	private boolean _isHall;

	public boolean checkRoomOrHall() {
		return _isHall;
	}

	public void setHall(boolean i) {
		_isHall = i;
	}
}

