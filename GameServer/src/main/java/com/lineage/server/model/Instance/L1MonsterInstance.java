package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.*;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1UltimateBattle;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

/**
 * 對像:mob 控制項
 * @author daien
 *
 */
public class L1MonsterInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1MonsterInstance.class);

	private static final Random _random = new Random();
	
	// private static final boolean _tkpc = false; // 追殺邪惡玩家

	private boolean _storeDroped; // 背包是否禁止加入掉落物品
	private boolean finalDrop = false;
	public static final Lock lock = new ReentrantLock(false);

	// 使用處理
	@Override
	public void onItemUse() {
		if (!isActived() && (_target != null)) {
			useItem(USEITEM_HASTE, 40); // ４０％確率使用

			// 變形怪 變身數據處理
			if (getNpcTemplate().is_doppel() && (_target instanceof L1PcInstance)) {
				final L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900); // 正確值
				for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // ＨＰ４０％
			useItem(USEITEM_HEAL, 50); // ５０％確率回復使用
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
			if (0 < getCurrentHp()) {
				if ((getHiddenStatus() == HIDDEN_STATUS_SINK)
						|| (getHiddenStatus() == HIDDEN_STATUS_ICE)) {
					perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					
				} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
					perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
				}
				perceivedFrom.sendPackets(new S_NPCPack(this));
				onNpcAI(); // 啟動AI
				if (this.getBraveSpeed() == 1) {// 具有勇水狀態
					perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
				}
				
			} else {
				perceivedFrom.sendPackets(new S_NPCPack(this));
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void searchTarget() {
		// 攻擊目標搜尋
		L1PcInstance targetPlayer = searchTarget(this);
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
			
		} else {
			ISASCAPE = false;
		}
	}

	   private L1PcInstance searchTarget(L1MonsterInstance npc) {
	        // 攻擊目標搜尋
	        L1PcInstance targetPlayer = null;
	        for (final L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
	            try {
	                Thread.sleep(10);
	            } catch (InterruptedException e) {
	                _log.error(e.getLocalizedMessage(), e);
	            }
	            if (pc.getCurrentHp() <= 0) {
	                continue;
	            }
	            if (pc.isDead()) {
	                continue;
	            }
	            if (pc.isGhost()) {
	                continue;
	            }
	            if (pc.isPrivateShop()) {
	                continue;
	            }
	            if (pc.isGm()) {
	                continue;
	            }


	            if (npc.getMapId() == 410) {// 魔族神殿的MOB
	                // 忽略收到調職命令的小惡魔
	                if (pc.getTempCharGfx() == 4261) {
	                    continue;
	                }
	            }

	            if (npc.getNpcTemplate().get_family() == NpcTable.ORC) {
	                if (pc.getClan() != null) {
	                    if (pc.getClan().getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
	                        continue;
	                    }
	                }
	            }

	            final L1PcInstance tgpc1 = npc.attackPc1(pc);
	            if (tgpc1 != null) {
	                targetPlayer = tgpc1;
	                return targetPlayer;
	            }

	            final L1PcInstance tgpc2 = npc.attackPc2(pc);
	            if (tgpc2 != null) {
	                targetPlayer = tgpc2;
	                return targetPlayer;
	            }

	            // 條件滿場合、友好見先制攻擊。
	            // ?值（側）PC1以上（友好）
	            // ?值（側）PC-1以下（友好）
	            if (npc.getNpcTemplate().getKarma() < 0) {
	                if (pc.getKarmaLevel() >= 1) {
	                    continue;
	                }
	            }
	            if (npc.getNpcTemplate().getKarma() > 0) {
	                if (pc.getKarmaLevel() <= -1) {
	                    continue;
	                }
	            }

	            // 見棄者地 變身中、各陣營先制攻擊
	            if (pc.getTempCharGfx() == 6034) {
	                if (npc.getNpcTemplate().getKarma() < 0) {
	                    continue;
	                }
	            }
	            if (pc.getTempCharGfx() == 6035) {
	                if (npc.getNpcTemplate().getKarma() > 0) {
	                    continue;
	                }
	                if (npc.getNpcTemplate().get_npcId() == 46070) {// 被拋棄的魔族
	                    continue;
	                }
	                if (npc.getNpcTemplate().get_npcId() == 46072) {// 被拋棄的魔族
	                    continue;
	                }

	            }

	            // 邪惡玩家追殺
	            final L1PcInstance tgpc = npc.targetPlayer1000(pc);
	            if (tgpc != null) {
	                targetPlayer = tgpc;
	                return targetPlayer;
	            }

	        	if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
					if (pc.hasSkillEffect(67)) {
						if (getNpcTemplate().is_agrososc()) { // 傳回怪物是否看穿變身
							targetPlayer = pc;
							break;
						}
					} else if (getNpcTemplate().is_agro()) { // 傳回怪物是否主動攻擊
						targetPlayer = pc;
						break;
					}

	                // 特定外型搜尋
	                if (npc.getNpcTemplate().is_agrogfxid1() >= 0) {
	                    if (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid1()) {
	                        targetPlayer = pc;
	                        return targetPlayer;
	                    }
	                }
	                if (npc.getNpcTemplate().is_agrogfxid2() >= 0) {
	                    if (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid2()) {
	                        targetPlayer = pc;
	                        return targetPlayer;
	                    }
	                }
	            }
	        }
	        return targetPlayer;
	    }

	/**
	 * 攻擊虛擬玩家
	 */
	/*private void tkDe() {
		for (final L1Object tg : World.get().getVisibleObjects(this)) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			if (tg instanceof L1DeInstance) {
				L1DeInstance tgDe = (L1DeInstance) tg;
				if (tgDe.isDead()) {
					continue;
				}
				if (tgDe.getCurrentHp() <= 0) {
					continue;
				}
				if (_random.nextBoolean()) {
					this._hateList.add(tgDe, 0);
					this._target = tgDe;
				}
			}
		}
	}*/

	/**
	 * 克特
	 * @param pc
	 * @return
	 */
	private L1PcInstance attackPc2(final L1PcInstance pc) {
		if (this.getNpcId() == 45600) { // 克特
			if (pc.isCrown()) {// 王族
				if (pc.getTempCharGfx() == pc.getClassId()) {
					return pc;
				}
			}
			if (pc.isDarkelf()) {// 黑妖
				return pc;
			}
		}
		return null;
	}

	/**
	 * 競技場
	 * @param pc
	 * @return
	 */
	private L1PcInstance attackPc1(final L1PcInstance pc) {
		final int mapId = this.getMapId();
		boolean isCheck = false;
		if (mapId == 88) {
			isCheck = true;
		}
		if (mapId == 98) {
			isCheck = true;
		}
		if (mapId == 92) {
			isCheck = true;
		}
		if (mapId == 91) {
			isCheck = true;
		}
		if (mapId == 95) {
			isCheck = true;
		}
		if (isCheck) {
			if (!pc.isInvisble() || this.getNpcTemplate().is_agrocoi()) { // 
				return pc;
			}
		}
		return null;
	}

	/**
	 * 邪惡玩家追殺
	 * @param pc
	 * @return
	 */
	private L1PcInstance targetPlayer1000(final L1PcInstance pc) {
		if (ConfigOther.KILLRED) {
			if (!this.getNpcTemplate().is_agro() && !this.getNpcTemplate().is_agrososc()
					&& (this.getNpcTemplate().is_agrogfxid1() < 0)
					&& (this.getNpcTemplate().is_agrogfxid2() < 0)) { // 完全
				
				if (pc.getLawful() < -1000) { // 
					return pc;
				}
			}
		}
		return null;
	}

	/**
	 * 攻擊目標設置
	 */
	@Override
	public void setLink(final L1Character cha) {
		// 副本ID不相等
		if (this.get_showId() != cha.get_showId()) {
			return;
		}
		if ((cha != null) && this._hateList.isEmpty()) {
			this._hateList.add(cha, 0);
			this.checkTarget();
		}
	}

	public L1MonsterInstance(final L1Npc template) {
		super(template);
		this._storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (this.isAiRunning()) {
			return;
		}
		
		if (!this._storeDroped) {// 背包是否加入掉落物品
			final SetDropExecutor setdrop = new SetDrop();
			setdrop.setDrop(this, this.getInventory());

			this.getInventory().shuffle();
			this._storeDroped = true;
		}
		
		this.setActived(false);
		this.startAI();
	}

	/**
	 * 對話
	 */
	@Override
	public void onTalkAction(final L1PcInstance pc) {
		// 改變面向
		this.setHeading(this.targetDirection(pc.getX(), pc.getY()));
		this.broadcastPacketAll(new S_ChangeHeading(this));

		// 動作暫停
		set_stop_time(REST_MILLISEC);
		this.setRest(true);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		if (ATTACK != null) {
			ATTACK.attack(pc, this);
		}
		if ((this.getCurrentHp() > 0) && !this.isDead()) {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				//attack.addChaserAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	/**
	 * 受攻擊mp減少計算
	 */
	@Override
	public void ReceiveManaDamage(final L1Character attacker, final int mpDamage) {
		if ((mpDamage > 0) && !this.isDead()) {
			this.setHate(attacker, mpDamage);

			this.onNpcAI();

			// NPC互相幫助的判斷
			if (attacker instanceof L1PcInstance) {
				this.serchLink((L1PcInstance) attacker, 
						this.getNpcTemplate().get_family());
			}

			int newMp = this.getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			this.setCurrentMp(newMp);
		}
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
			damage /= 2.01;
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
		
		double coefficient = (1.0 - attrDeffence + 3.0 / 32.0);

		if (coefficient > 0) {
			damage *= coefficient;
		}
		this.receiveDamage(attacker, (int)damage);
	}

	/**
	 * 受攻擊hp減少計算
	 */
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		ISASCAPE = false;
		if ((this.getCurrentHp() > 0) && !this.isDead()) {
			if ((this.getHiddenStatus() == HIDDEN_STATUS_SINK)
					|| (this.getHiddenStatus() == HIDDEN_STATUS_FLY)) {
				return;
			}
			if (damage >= 0) {
				if (attacker instanceof L1EffectInstance) { // 效果不列入目標(設置主人為目標)
					final L1EffectInstance effect = (L1EffectInstance) attacker;
					attacker = effect.getMaster();
					if (attacker != null) {
						this.setHate(attacker, damage);
					}

				} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
					final L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
					attacker = ill.getMaster();
					if (attacker != null) {
						this.setHate(attacker, damage);
					}
				} else if (attacker instanceof L1MonsterInstance) {// 魔法師．哈汀(故事)
					switch (getNpcTemplate().get_npcId()) {
					case 91290: // 鐮刀死神的使者
					case 91294: // 巴風特
					case 91295: // 黑翼賽尼斯
					case 91296: // 賽尼斯
						this.setHate(attacker, damage);
						damage = 0;
						break;
					}
					
				} else {
					this.setHate(attacker, damage);
				}
			}
			
			if (damage > 0) {
				this.removeSkillEffect(FOG_OF_SLEEPING);
			}

			this.onNpcAI();

			L1PcInstance atkpc = null;
			// 攻擊者是PC
			if (attacker instanceof L1PcInstance) {
				atkpc = (L1PcInstance) attacker;
				if (damage > 0) {
					atkpc.setPetTarget(this);
					switch (getNpcTemplate().get_npcId()) {
					case 45681: // 林德拜爾
					case 45682: // 安塔瑞斯
					case 45683: // 法利昂
					case 45684: // 巴拉卡斯
						recall(atkpc);
						break;
					}
				}
				// NPC互相幫助的判斷
				this.serchLink(atkpc, this.getNpcTemplate().get_family());
			}

			final int newHp = this.getCurrentHp() - damage;
			if ((newHp <= 0) && !this.isDead()) {
				final int transformId = this.getNpcTemplate().getTransformId();
				// 變身
				if (transformId == -1) {
					this.setCurrentHpDirect(0);
					this.setDead(true);
					this.setStatus(ActionCodes.ACTION_Die);
					this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Die));
					openDoorWhenNpcDied(this);
					final Death death = new Death(attacker);
					GeneralThreadPool.get().execute(death);
					if (FinalKillDropFactory.getInstance().containsKey(this.getNpcId())) {
						lock.lock();
						try {
							if (!this.finalDrop && attacker instanceof L1PcInstance) {
								this.finalDrop = true;
								final int item_id = FinalKillDropFactory.getInstance().get(this.getNpcId());
								final L1Item item = ItemTable.get().getTemplate(item_id);
								((L1PcInstance) attacker).getInventory().storeItem(item_id, 1L);
								World.get().broadcastPacketToAll(new S_ServerMessage("\\fY【系統訊息】" + attacker.getName() + " 擊殺 獲得了『" + item.getName() + "』"));
							}
						} finally {
							lock.unlock();
						}
					}
					
				} else { // 變身
					// distributeExpDropKarma(attacker);
					this.transform(transformId);
				}
			}
			if (newHp > 0) {
				this.setCurrentHp(newHp);
				this.hide();
			}
			   // HP 顯示設置
            if (ConfigOther.HPBAR) {
				if ((attacker instanceof L1PcInstance))  {
			      L1PcInstance player = (L1PcInstance)attacker;
			       if (!player.isActived()) {
			      player.sendPackets(new S_HPMeter(this));
			       }
			    }
				// 讓寵物或召喚怪攻擊時也看得到怪物血條
				if (atkpc == null) {
					if (attacker instanceof L1PetInstance) {
						atkpc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();

					} else if (attacker instanceof L1SummonInstance) {
						atkpc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
					}

					// 存在PC主人
					if (atkpc != null) {
						broadcastPacketHP(atkpc);
					}

				} else {
					broadcastPacketHP(atkpc);
				}
			}
			
		} else if (!this.isDead()) { // 念
			this.setDead(true);
			this.setStatus(ActionCodes.ACTION_Die);
			final Death death = new Death(attacker);
			GeneralThreadPool.get().execute(death);
			// Death(attacker);
		}
	}

	/**
	 * NPC死亡開門的處理
	 * @param npc
	 */
	private static void openDoorWhenNpcDied(final L1NpcInstance npc) {
		final int[] npcId = { 46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152 };
		final int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010 };

		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
			}
		}
	}

	/**
	 * 開門的處理
	 * @param doorId
	 */
	private static void openDoorInCrystalCave(final int doorId) {
		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1DoorInstance) {
				final L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * 召回PC的處理(PC距離自身過遠)
	 *
	 * @param pc
	 */
	private void recall(final L1PcInstance pc) {
		if (this.getMapId() != pc.getMapId()) {
			return;
		}
		if (this.getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				final L1Location newLoc = this.getLocation().randomLocation(3, 4, false);
				if (this.glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(), this.getMapId(), 5, true);
					break;
				}
			}
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

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, this.getMaxMp());

		if (this.getCurrentMp() == currentMp) {
			return;
		}
		
		this.setCurrentMpDirect(currentMp);
	}

	/**
	 * 死亡判斷
	 * @author daien
	 *
	 */
	class Death implements Runnable {
		
		L1Character _lastAttacker;// 攻擊者

		/**
		 * 死亡判斷
		 * @param lastAttacker 攻擊者
		 */
		public Death(final L1Character lastAttacker) {
			this._lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			L1MonsterInstance mob = L1MonsterInstance.this;
			
			// 指定NPC死亡對話
			tark(mob);
			
			// 召喚幫手
			spawn(mob);
			
			mob.setDeathProcessing(true);
			mob.setCurrentHpDirect(0);
			mob.setDead(true);

			mob.setStatus(ActionCodes.ACTION_Die);

			mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Die));
			// 解除舊座標障礙宣告
			mob.getMap().setPassable(mob.getLocation(), true);
			
			mob.startChat(CHAT_TIMING_DEAD);

			mob.distributeExpDropKarma(this._lastAttacker);
			mob.giveUbSeal();

			mob.setDeathProcessing(false);

			mob.setExp(0);
			mob.setKarma(0);
			mob.allTargetClear();

			int deltime = 0;
			// 特定NPC死亡時間設置
			switch (mob.getNpcId()) {
			case 92000:// 傑弗雷庫(雌)
			case 92001:// 傑弗雷庫(雄)
				deltime = 60;
				break;

			default:
				deltime = ConfigAlt.NPC_DELETION_TIME;
				break;
			}
			mob.deleteMe();
//			mob.startDeleteTimer(deltime);
		}

		/**
		 * 死亡呼救
		 * @param mob
		 */
		private void spawn(L1MonsterInstance mob) {
			// 以NPCID定義
			switch (mob.getNpcId()) {

			}
			// 以地圖編號定義
			switch (mob.getMapId()) {

			}
		}

		/**
		 * NPC 死亡 新手教學/特定死亡說話
		 * @param mob
		 */
		private void tark(final L1MonsterInstance mob) {
			// 取回NPC所在地圖編號
			/*short mapid = mob.getMapId();
			switch (mapid) {
			case 68:// 歌唱之島
			case 69:// 隱藏之谷
			//case 630:// 英雄領地
				final int rnd1 = 8000 + _random.nextInt(12);
				mob.broadcastPacketX8(new S_NpcChat(mob, "$" + rnd1));
				break;
			}*/
			
			// 取回NPC編號
			int npcid = mob.getNpcId();
			switch (npcid) {

			}
		}
	}

	/**
	 * 判斷主要攻擊者(最後殺死NPC的人)
	 * @param lastAttacker
	 */
	private void distributeExpDropKarma(final L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		
		// 判斷主要攻擊者
		L1PcInstance pc = null;

		// NPC具有死亡判斷設置
		if (DEATH != null) {
			pc = DEATH.death(lastAttacker, this);
			
		} else {
			// 判斷主要攻擊者
			pc = CheckUtil.checkAtkPc(lastAttacker);
		}

		if (pc != null) {
			final ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			final ArrayList<Integer> hateList = _hateList.toHateArrayList();
			// 取回經驗值
			final long exp = getExp();
			
			// 加入經驗值與積分
			CalcExp.calcExp(pc, this.getId(), targetList, hateList, exp);
			final int score = NpcScoreTable.get().get_score(getNpcId());
			if (score > 0 && !isResurrect()) {
				// 3032 你得到了 %0 積分。
				pc.sendPackets(new S_ServerMessage("\\fX你得到了" + score + "積分"));
				pc.get_other().add_score(score);
			}

			// 死亡後續處理
			if (isDead()) {
				// 掉落物品分配
				distributeDrop();
				// 陣營
				giveKarma(pc);
				   if (pc.isActived()) { //啟動掛機中
	        	    	setSkillEffect(8853, 120 * 1000);
	        	    	//pc.sendPackets(new S_SystemMessage("怪物死亡重新計算120秒,"));
	        	    	//pc.sendPackets(new S_SystemMessage("----------------"));
	        	        }
			}
		}
	}

	/**
	 * 掉落物品分配
	 */
	private void distributeDrop() {
		final ArrayList<L1Character> dropTargetList = this._dropHateList.toTargetArrayList();
		final ArrayList<Integer> dropHateList = this._dropHateList.toHateArrayList();
		try {
			// 設置掉落物品
			final DropShareExecutor dropShareExecutor = new DropShare();
			dropShareExecutor.dropShare(L1MonsterInstance.this, dropTargetList, dropHateList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 陣營
	 * @param pc
	 */
	private void giveKarma(final L1PcInstance pc) {
		int karma = this.getKarma();
		if (karma != 0) {
			final int karmaSign = Integer.signum(karma);
			final int pcKarmaLevel = pc.getKarmaLevel();
			final int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			// 背信行為5倍
			if ((pcKarmaLevelSign != 0) && (karmaSign != pcKarmaLevelSign)) {
				karma *= 5;
			}
			// 止刺設定。or倒場合入。
			pc.addKarma((int) (karma * ConfigRate.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (this.getUbSealCount() != 0) { // UB勇者證
			final L1UltimateBattle ub = UBTable.getInstance().getUb(this.getUbId());
			if (ub != null) {
				for (final L1PcInstance pc : ub.getMembersArray()) {
					if ((pc != null) && !pc.isDead() && !pc.isGhost()) {
						final L1ItemInstance item = 
							// 勇者的勳章(41402)
							pc.getInventory().storeItem(41402, this.getUbSealCount());
						// 403 獲得%0%o 。
						pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
					}
				}
			}
		}
	}

	/**
	 * 背包是否禁止加入掉落物品
	 * @return true:不加入 false:加入
	 */
	public boolean is_storeDroped() {
		return this._storeDroped;
	}

	/**
	 * 設置背包是否禁止加入掉落物品
	 * @param flag true:不加入 false:加入
	 */
	public void set_storeDroped(final boolean flag) {
		this._storeDroped = flag;
	}

	private int _ubSealCount = 0; // 無限大賽可獲得的勇氣之證數量

	/**
	 * 給予勇氣之證數量
	 * @return
	 */
	public int getUbSealCount() {
		return this._ubSealCount;
	}

	/**
	 * 設置給予勇氣之證數量
	 * @param i
	 */
	public void setUbSealCount(final int i) {
		this._ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	/**
	 * UBID
	 * @return
	 */
	public int getUbId() {
		return this._ubId;
	}

	/**
	 * UBID
	 * @param i
	 */
	public void setUbId(final int i) {
		this._ubId = i;
	}

	/**
	 * 一定機率躲藏
	 */
	private void hide() {
		final int npcid = this.getNpcTemplate().get_npcId();
		switch (npcid) {
		case 45061: // 弱化史巴托
		case 45161: // 史巴托
		case 45181: // 史巴托
		case 45455: // 殘暴的史巴托
			if (this.getMaxHp() / 3 > this.getCurrentHp()) {
				final int rnd = _random.nextInt(10);
				if (1 > rnd) {
					this.allTargetClear();
					this.setHiddenStatus(HIDDEN_STATUS_SINK);
					this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Hide));
					this.setStatus(13);
					this.broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;
			
		case 45682: // 安塔瑞斯
			if (this.getMaxHp() / 3 > this.getCurrentHp()) {
				final int rnd = _random.nextInt(50);
				if (1 > rnd) {
					this.allTargetClear();
					this.setHiddenStatus(HIDDEN_STATUS_SINK);
					this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_AntharasHide));
					this.setStatus(20);
					this.broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;
			
		case 45067: // 弱化哈維
		case 45264: // 哈維
		case 45452: // 哈維
		case 45090: // 弱化格利芬
		case 45321: // 格利芬
		case 45445: // 格利芬
//			if (this.getMaxHp() / 3 > this.getCurrentHp()) {
//				final int rnd = _random.nextInt(10);
//				if (1 > rnd) {
//					this.allTargetClear();
//					this.setHiddenStatus(HIDDEN_STATUS_FLY);
//					this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Moveup));
//					this.setStatus(4);
//					this.broadcastPacketAll(new S_NPCPack(this));
//				}
//			}
			break;
		/*case 45681: // 林德拜爾 XXX 暫時移除躲藏
			if (this.getMaxHp() / 3 > this.getCurrentHp()) {
				final int rnd = _random.nextInt(50);
				if (1 > rnd) {
					this.allTargetClear();
					this.setHiddenStatus(HIDDEN_STATUS_FLY);
					this.broadcastPacket(new S_DoActionGFX(this.getId(),
							ActionCodes.ACTION_Moveup));
					this.setStatus(11);
					this.broadcastPacket(new S_NPCPack(this));
				}
			}*/
			
		case 46107: // 底比斯 曼陀羅草(白)
		case 46108: // 底比斯 曼陀羅草(黑)
			if (this.getMaxHp() / 4 > this.getCurrentHp()) {
				final int rnd = _random.nextInt(10);
				if (1 > rnd) {
					this.allTargetClear();
					this.setHiddenStatus(HIDDEN_STATUS_SINK);
					this.broadcastPacketAll(new S_DoActionGFX(this.getId(), ActionCodes.ACTION_Hide));
					this.setStatus(13);
					this.broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;
		}
	}

	/**
	 * 召喚後隱藏
	 */
	public void initHide() {
		// 出現直後隱動作
		// 潛MOB一定確率地中潛狀態、
		// 飛MOB飛狀態
		final int npcid = this.getNpcTemplate().get_npcId();
		final int rnd = _random.nextInt(3);
		switch (npcid) {
		case 45061: // 弱化史巴托
		case 45161: // 史巴托
		case 45181: // 史巴托
		case 45455: // 殘暴的史巴托
			if (1 > rnd) {
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(13);
			}
			break;
			
		case 45045: // 弱化高侖石頭怪
		case 45126: // 高侖石頭怪
		case 45134: // 高侖石頭怪
		case 45281: // 奇巖 高侖石頭怪
			if (1 > rnd) {
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(4);
			}
			break;
			
		case 45067: // 弱化哈維
		case 45264: // 哈維
		case 45452: // 哈維
		case 45090: // 弱化格利芬
		case 45321: // 格利芬
		case 45445: // 格利芬
//			this.setHiddenStatus(HIDDEN_STATUS_FLY);
//			this.setStatus(4);
			break;
			
		case 45681: // 林德拜爾
//			this.setHiddenStatus(HIDDEN_STATUS_FLY);
//			this.setStatus(11);
			break;
			
		case 46107: // 底比斯 曼陀羅草(白)
		case 46108: // 底比斯 曼陀羅草(黑)
			if (1 > rnd) {
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(13);
			}
			break;
			
		case 46125:// 高侖鋼鐵怪
		case 46126:// 萊肯
		case 46127:// 歐熊
		case 46128:// 冰原老虎
			this.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			this.setStatus(4);
			break;
		}
	}

	public void initHideForMinion(final L1NpcInstance leader) {
		// 屬出現直後隱動作（同動作）
		final int npcid = this.getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			switch (npcid) {
			case 45061: // 
			case 45161: // 
			case 45181: // 
			case 45455: // 
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(13);
				break;
			case 45045: // 
			case 45126: // 
			case 45134: // 
			case 45281: // 
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(4);
				break;
			case 46107: //  (白)
			case 46108: //  (黑)
				this.setHiddenStatus(HIDDEN_STATUS_SINK);
				this.setStatus(13);
				break;
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			switch (npcid) {
			case 45067: // 
			case 45264: // 
			case 45452: // 
			case 45090: // 
			case 45321: // 
			case 45445: // 
//				this.setHiddenStatus(HIDDEN_STATUS_FLY);
//				this.setStatus(4);
				break;
			case 45681:  // 林德拜爾
//				this.setHiddenStatus(HIDDEN_STATUS_FLY);
//				this.setStatus(11);
				break;
			case 46125:
			case 46126:
			case 46127:
			case 46128:
				this.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
				this.setStatus(4);
				break;
			}
		}
	}

	@Override
	protected void transform(final int transformId) {
		super.transform(transformId);
		// DROP再設定
		this.getInventory().clearItems();
		// XXX
		final SetDropExecutor setDropExecutor = new SetDrop();
		setDropExecutor.setDrop(this, this.getInventory());
		//DropTable.getInstance().setDrop(this, getInventory());
		this.getInventory().shuffle();
	}
	// 怪物上次回血時間
	private long _lasthprtime = 0;

	/**
	 * 怪物上次回血時間
	 *
	 * @return
	 */
	public long getLastHprTime() {
		if (_lasthprtime == 0) {// 上次回血時間為0
			return _lasthprtime = (System.currentTimeMillis() / 1000) - 5;// 前五秒的時間
		}
		return _lasthprtime;
	}

	/**
	 * 怪物上次回血時間
	 *
	 * @param time
	 */
	public void setLastHprTime(long time) {
		_lasthprtime = time;
	}

}
