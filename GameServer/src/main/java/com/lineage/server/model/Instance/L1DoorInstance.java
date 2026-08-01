package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Door;
import com.lineage.server.serverpackets.S_DoorPack;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 對像:門 控制項
 * @author dexc
 *
 */
public class L1DoorInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	public static final int PASS = 0x00;// 可通行
	public static final int NOT_PASS = 0x41;// 不可通行

	private static final Log _log = LogFactory.getLog(L1DoorInstance.class);

	public L1DoorInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			if ((this.getMaxHp() == 0) || (this.getMaxHp() == 1)) { // 破壞不可能對像外
				return;
			}

			if ((this.getCurrentHp() > 0) && !this.isDead()) {
				final L1AttackMode attack = new L1AttackPc(pc, this);
				if (attack.calcHit()) {
					attack.calcDamage();
					//attack.addChaserAttack();
				}
				attack.action();
				attack.commit();
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != this.get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);
			
			if (this.getOpenStatus() == ActionCodes.ACTION_Open) {
				this.setOpenStatus(ActionCodes.ACTION_Open);
				this.setPassable(L1DoorInstance.PASS);
				
			} else {
				this.setOpenStatus(ActionCodes.ACTION_Close);
				this.setPassable(L1DoorInstance.NOT_PASS);
			}
		
			perceivedFrom.sendPackets(new S_DoorPack(this));
			this.sendDoorPacket();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void deleteMe() {
		try {
			this.setPassable(PASS);
			this.sendDoorPacket();

			this._destroyed = true;
			if (this.getInventory() != null) {
				this.getInventory().clearItems();
			}
			this.allTargetClear();
			this._master = null;
			World.get().removeVisibleObject(this);
			World.get().removeObject(this);
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.removeKnownObject(this);
				pc.sendPackets(new S_RemoveObject(this));
			}
			this.removeAllKnownObjects();
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		if ((this.getMaxHp() == 0) || (this.getMaxHp() == 1)) { // 破壞不可能對像外
			return;
		}

		if ((this.getCurrentHp() > 0) && !this.isDead()) {
			final int newHp = this.getCurrentHp() - damage;
			if ((newHp <= 0) && !this.isDead()) {
				this.setStatus(ActionCodes.ACTION_DoorDie);
				final Death death = new Death(attacker);
				GeneralThreadPool.get().execute(death);
			}
			if (newHp > 0) {
				this.setCurrentHp(newHp);
				if ((this.getMaxHp() * 1 / 6) > this.getCurrentHp()) {
					if (this._crackStatus != 5) {
						this.broadcastPacketAll(new S_DoActionGFX(this.getId(),
								ActionCodes.ACTION_DoorAction5));
						this.setStatus(ActionCodes.ACTION_DoorAction5);
						this._crackStatus = 5;
					}
				} else if ((this.getMaxHp() * 2 / 6) > this.getCurrentHp()) {
					if (this._crackStatus != 4) {
						this.broadcastPacketAll(new S_DoActionGFX(this.getId(),
								ActionCodes.ACTION_DoorAction4));
						this.setStatus(ActionCodes.ACTION_DoorAction4);
						this._crackStatus = 4;
					}
				} else if ((this.getMaxHp() * 3 / 6) > this.getCurrentHp()) {
					if (this._crackStatus != 3) {
						this.broadcastPacketAll(new S_DoActionGFX(this.getId(),
								ActionCodes.ACTION_DoorAction3));
						this.setStatus(ActionCodes.ACTION_DoorAction3);
						this._crackStatus = 3;
					}
				} else if ((this.getMaxHp() * 4 / 6) > this.getCurrentHp()) {
					if (this._crackStatus != 2) {
						this.broadcastPacketAll(new S_DoActionGFX(this.getId(),
								ActionCodes.ACTION_DoorAction2));
						this.setStatus(ActionCodes.ACTION_DoorAction2);
						this._crackStatus = 2;
					}
				} else if ((this.getMaxHp() * 5 / 6) > this.getCurrentHp()) {
					if (this._crackStatus != 1) {
						this.broadcastPacketAll(new S_DoActionGFX(this.getId(),
								ActionCodes.ACTION_DoorAction1));
						this.setStatus(ActionCodes.ACTION_DoorAction1);
						this._crackStatus = 1;
					}
				}
			}
		} else if (!this.isDead()) { // 念
			this.setStatus(ActionCodes.ACTION_DoorDie);
			final Death death = new Death(attacker);
			GeneralThreadPool.get().execute(death);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, this.getMaxHp());

		if (this.getCurrentHp() == currentHp) {
			return;
		}

		this.setCurrentHpDirect(currentHp);
	}

	/**
	 * 門死亡過程
	 * @author daien
	 *
	 */
	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(final L1Character lastAttacker) {
			this._lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setCurrentHpDirect(0);
			setDead(true);
			getMap().setPassable(getLocation(), true);

			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorDie));
			setPassable(PASS);
			setOpenStatus(ActionCodes.ACTION_Open);
			setPassable(PASS);
			sendDoorPacket();
		}
	}

	private void sendDoorPacket() {
		final int entranceX = this.getEntranceX();
		final int entranceY = this.getEntranceY();
		final int leftEdgeLocation = this.getLeftEdgeLocation();
		final int rightEdgeLocation = this.getRightEdgeLocation();

		final int size = rightEdgeLocation - leftEdgeLocation;
		if (size == 0) { // 1分幅
			this.sendPacket(entranceX, entranceY);
			
		} else { // 2分以上幅
			if (this.getDirection() == 0) { // ／向
				for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
					this.sendPacket(x, entranceY);
				}
				
			} else { // ＼向
				for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
					this.sendPacket(entranceX, y);
				}
			}
		}
	}

	private void sendPacket(final int x, final int y) {
		final S_Door packet = new S_Door(x, y, this.getDirection(), this.getPassable());
		this.broadcastPacketAll(packet);
	}

	private void set_open() {
		final int entranceX = this.getEntranceX();
		final int entranceY = this.getEntranceY();
		final int leftEdgeLocation = this.getLeftEdgeLocation();
		final int rightEdgeLocation = this.getRightEdgeLocation();
		if (this.getDirection() == 0) { // ／方向
			this.getMap().setPassable(entranceX, entranceY, true, 0);
			for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
				this.getMap().setPassable(x, entranceY, true, 0);
			}
			
		} else { // ＼方向
			this.getMap().setPassable(entranceX, entranceY, true, 1);
			for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
				this.getMap().setPassable(entranceX, y, true, 1);
			}
		}
	}
	
	/**
	 * 開門
	 */
	public void open() {
		if (this.isDead()) {
			return;
		}
		if (this.getOpenStatus() == ActionCodes.ACTION_Close) {
			this.setOpenStatus(ActionCodes.ACTION_Open);
			this.setPassable(L1DoorInstance.PASS);
			this.broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Open));
			this.sendDoorPacket();
			set_open();
		}
	}

	private void set_close() {
		final int entranceX = this.getEntranceX();
		final int entranceY = this.getEntranceY();
		final int leftEdgeLocation = this.getLeftEdgeLocation();
		final int rightEdgeLocation = this.getRightEdgeLocation();
		if (this.getDirection() == 0) { // ／方向
			this.getMap().setPassable(entranceX, entranceY, false, 0);
			for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
				this.getMap().setPassable(x, entranceY, false, 0);
			}
			
		} else { // ＼方向
			this.getMap().setPassable(entranceX, entranceY, false, 1);
			for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
				this.getMap().setPassable(entranceX, y, false, 1);
			}
		}
	}

	/**
	 * 關門
	 */
	public void close() {
		if (this.isDead()) {
			return;
		}
		if (this.getOpenStatus() == ActionCodes.ACTION_Open) {
			this.setOpenStatus(ActionCodes.ACTION_Close);
			this.setPassable(L1DoorInstance.NOT_PASS);
			this.broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Close));
			this.sendDoorPacket();
			set_close();
		}
	}

	public void repairGate() {
		if (this.getMaxHp() > 1) {
			this.setDead(false);
			this.setCurrentHp(this.getMaxHp());
			this.setStatus(0);
			this.setCrackStatus(0);
			this.setOpenStatus(ActionCodes.ACTION_Open);
			this.close();
		}
	}

	private int _doorId = 0;// 門的編號

	/**
	 * 門的編號<BR>
	 * 門編號 51~55 由賭場使用<BR>
	 * 門編號 10000~10003 由任務不死族的叛徒 (法師30級以上官方任務)使用<BR>
	 * 10004 蛇女房間<BR>
	 * 10005~10007 安塔瑞斯洞穴<BR>
	 * 10008~10013 法利昂洞穴<BR>
	 * 10014~10035 哈汀副本<BR>
	 * @return
	 */
	public int getDoorId() {
		return this._doorId;
	}

	/**
	 * 門的編號
	 * @param i
	 */
	public void setDoorId(final int i) {
		this._doorId = i;
	}

	private int _direction = 0; // 門的定位

	/**
	 * 門的定位
	 * @return
	 */
	public int getDirection() {
		return this._direction;
	}

	/**
	 * 門的定位
	 * @param i 0:／ 1:＼
	 */
	public void setDirection(final int i) {
		if ((i == 0) || (i == 1)) {
			this._direction = i;
		}
	}

	public int getEntranceX() {
		int entranceX = 0;
		if (this.getDirection() == 0) { // ／向
			entranceX = this.getX();
		} else { // ＼向
			entranceX = this.getX() - 1;
		}
		return entranceX;
	}

	public int getEntranceY() {
		int entranceY = 0;
		if (this.getDirection() == 0) { // ／向
			entranceY = this.getY() + 1;
		} else { // ＼向
			entranceY = this.getY();
		}
		return entranceY;
	}

	private int _leftEdgeLocation = 0; // 左端座標(向X軸orY軸決定)

	/**
	 * 門的左端
	 * @return
	 */
	public int getLeftEdgeLocation() {
		return this._leftEdgeLocation;
	}

	/**
	 * 門的左端
	 * @param i
	 */
	public void setLeftEdgeLocation(final int i) {
		this._leftEdgeLocation = i;
	}

	private int _rightEdgeLocation = 0; // 右端座標(向X軸orY軸決定)

	/**
	 * 門的右端
	 * @return
	 */
	public int getRightEdgeLocation() {
		return this._rightEdgeLocation;
	}

	/**
	 * 門的右端
	 * @param i
	 */
	public void setRightEdgeLocation(final int i) {
		this._rightEdgeLocation = i;
	}

	private int _openStatus = ActionCodes.ACTION_Close;

	public int getOpenStatus() {
		return this._openStatus;
	}

	private void setOpenStatus(final int i) {
		if ((i == ActionCodes.ACTION_Open) || (i == ActionCodes.ACTION_Close)) {
			this._openStatus = i;
		}
	}

	private int _passable = NOT_PASS;// 是否可通行

	/**
	 * 是否可通行
	 * @return 0:可通行(PASS) 1:不可通行(NOT_PASS)
	 */
	public int getPassable() {
		return this._passable;
	}

	/**
	 * 是否可通行
	 * @param i 0:可通行(PASS) 1:不可通行(NOT_PASS)
	 */
	private void setPassable(final int i) {
		if ((i == PASS) || (i == NOT_PASS)) {
			this._passable = i;
		}
	}

	private int _keeperId = 0;// 管理員編號

	/**
	 * 管理員編號
	 * @return
	 */
	public int getKeeperId() {
		return this._keeperId;
	}

	/**
	 * 管理員編號
	 * @param i
	 */
	public void setKeeperId(final int i) {
		this._keeperId = i;
	}

	private int _crackStatus;

	private void setCrackStatus(final int i) {
		this._crackStatus = i;
	}

	/**
	 * 打開全部關閉的門
	 * 核心啟動時調用一次
	 */
	public static void openDoor() {
		final L1DoorInstance[] allDoor = DoorSpawnTable.get().getDoorList();
		// 不包含元素
		if (allDoor.length <= 0) {
			return;
		}
		
		for (final L1DoorInstance door : allDoor) {
			switch (door.getDoorId()) {
			case 5001:// 水晶洞穴 1樓
			case 5002:// 水晶洞穴 2樓
			case 5003:// 水晶洞穴 2樓
			case 5004:// 水晶洞穴 2樓
			case 5005:// 水晶洞穴 2樓
			case 5006:// 水晶洞穴 2樓
			case 5007:// 水晶洞穴 3樓
			case 5008:// 水晶洞穴 3樓
			case 5009:// 水晶洞穴 3樓
			case 5010:// 水晶洞穴 3樓
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
				door.set_close();
				break;
				
			default:
				// HP大於1
				if (door.getMaxHp() > 1) {
					door.set_close();
					continue;
				}
				// 具有守門員
				if (door.getKeeperId() != 0) {
					door.set_close();
					continue;
				}
				// 開門
				door.open();
				break;
			}
		}
	}

	/**
	 * 控制賭場的門
	 * @param isOpen true開 false關
	 */
	public static void openGam(final boolean isOpen) {
		final L1DoorInstance[] allDoor = DoorSpawnTable.get().getDoorList();
		// 不包含元素
		if (allDoor.length <= 0) {
			return;
		}
		
		for (final L1DoorInstance door : allDoor) {
			switch (door.getDoorId()) {
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
				if (isOpen) {
					door.open();
					
				} else {
					door.close();
				}
				break;
			}
		}
	}
}
