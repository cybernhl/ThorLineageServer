package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;


import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1NpcTalkData;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;

/**
 * 對像:精靈守護神 控制項
 * @author daien
 *
 */
public class L1GuardianInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1GuardianInstance.class);

	private Random _random = new Random();
	private L1GuardianInstance _npc = this;

	// 妖森NPC道具重置时间
	private int _configtime = Config.DFDropItemTime;

	/**
	 * @param template
	 */
	public L1GuardianInstance(L1Npc template) {
		super(template);
		if (Config.DFDropItemTime > 0) {
			// 妖森NPC道具重置时间
			if (!isDropitems()) {
				doGDropItem(0);
			}
		}
	}
	

	/**
	 * 目標搜尋
	 */
	@Override
	public void searchTarget() {
		// 目標搜尋
		L1PcInstance targetPlayer = searchTarget(this);

		if (targetPlayer != null) {
			this._hateList.add(targetPlayer, 0);
			this._target = targetPlayer;
		}
	}

	private static L1PcInstance searchTarget(L1GuardianInstance npc) {
		L1PcInstance targetPlayer = null;

		for (final L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			if ((pc.getCurrentHp() <= 0) || pc.isDead() || pc.isGm()
					|| pc.isGhost()) {
				continue;
			}

			// 副本ID不相等
			if (npc.get_showId() != pc.get_showId()) {
				continue;
			}
			if (!pc.isInvisble() || npc.getNpcTemplate().is_agrocoi()) { // 
				if (!pc.isElf()) { // 以外
					targetPlayer = pc;
					// $804 人類，如果你重視你的生命現在就快離開這神聖的地方。
					npc.wideBroadcastPacket(new S_NpcChatShouting(npc, "$804"));
					break;

				} else if (pc.isElf() && pc.isWantedForElf()) {
					targetPlayer = pc;
					// $815 若殺害同族，必須以自己的生命贖罪。
					npc.wideBroadcastPacket(new S_NpcChat(npc, "$815"));
					break;
				}
			}
		}
		return targetPlayer;
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

	@Override
	public void onNpcAI() {
		if (this.isAiRunning()) {
			return;
		}
		this.setActived(false);
		this.startAI();
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (player.getType() == 2 && player.getCurrentWeapon() == 0 && player.isElf()) {
			final L1AttackPc attack = new L1AttackPc(player, this);
			if (attack.calcHit()) {
				// 妖森任务道具重置
				if (Config.DFDropItemTime > 0) {
					try {  
						final String npcName = getNpcTemplate().get_name();
						String itemName = "";

						final int npcId = getNpcTemplate().get_npcId();
						int itemCount = 0;
						final int chance = RandomArrayList.getInc(100, 1);
						// 蘑菇汁
						L1Item item40499 = ItemTable.get().getTemplate(40499);
						// 芮克妮的网
						L1Item item40503 = ItemTable.get().getTemplate(40503);
						// 安特之树皮
						L1Item item40505 = ItemTable.get().getTemplate(40505);
						// 安特的水果
						L1Item item40506 = ItemTable.get().getTemplate(40506);
						// 安特之树枝
						L1Item item40507 = ItemTable.get().getTemplate(40507);
						// 潘的鬃毛
						L1Item item40519 = ItemTable.get().getTemplate(40519);
						// 安特
						if (npcId == 70848) {
							// 蘑菇汁 换 树皮
							if (_inventory.checkItem(40499)) { 									
								itemName = item40505.getName();
								itemCount = (int) _inventory.countItems(40499);
								if (itemCount >= 1) {
									itemName += " (" + itemCount + ")";
								}
								_inventory.consumeItem(40499, itemCount);
								player.getInventory().storeItem(40505, itemCount);
								player.sendPackets(new S_ServerMessage(143, npcName, itemName));
								if (!isDropitems()) {
									doGDropItem(3);
								}
							// 树枝
							} else if (_inventory.checkItem(40507)) {
								if (chance <= 60 && chance > 10) {
									itemName = item40507.getName();
									itemName += " (6)";
								_inventory.consumeItem(40507, 6);
									player.getInventory().storeItem(40507, 6);
									player.sendPackets(new S_ServerMessage(143,
											npcName, itemName));
								} else {
										itemName = item40499.getName();
									player.sendPackets(new S_ServerMessage(337,
											itemName));
								}
							// 水果
							} else if (_inventory.checkItem(40506) && !_inventory.checkItem(40507)) {
								if (chance >= 10) {
									itemName = item40506.getName();
									_inventory.consumeItem(40506, 1);
									player.getInventory().storeItem(40506, 1);
									player.sendPackets(new S_ServerMessage(143, npcName, itemName));
								} else {
									itemName = item40499.getName();
									player.sendPackets(new S_ServerMessage(337, itemName));
								}
							} else {
								if (!forDropitems()) {
									setDropItems(false);
									doGDropItem(_configtime);
								}
								if (chance > 60) {
									broadcastPacketAll(new S_NpcChatPacket(_npc, "$822", 0));
								} else {
									itemName = item40499.getName();
									//\f1%0不足%s。
									player.sendPackets(new S_ServerMessage(337, itemName));
								}
							}
						}
						// 潘
						if (npcId == 70850) {
							// 潘的鬃毛
							if (_inventory.checkItem(40519)) {
								if (chance <= 30) {
									itemName = item40519.getName();
									itemName += " (5)";
									_inventory.consumeItem(40519, 5);
									player.getInventory().storeItem(40519, 5);
									player.sendPackets(new S_ServerMessage(143, npcName, itemName));
								}
							} else {
								if (!forDropitems()) {
									setDropItems(false);
									doGDropItem(_configtime);
								}
								if (chance > 30) {
									broadcastPacketAll(new S_NpcChatPacket(_npc, "$824", 0));
								}
							}
						}
						// 芮克妮
						if (npcId == 70846) {
							// 安特之树枝 换 芮克妮的网
							if (_inventory.checkItem(40507)) {
								itemName = item40503.getName();
								itemCount = (int) _inventory.countItems(40507);
								if (itemCount >= 1) {
									itemName += " (" + itemCount + ")";
								}
								_inventory.consumeItem(40507, itemCount);
								player.getInventory().storeItem(40503, itemCount);
								player.sendPackets(new S_ServerMessage(143, npcName, itemName));
							} else {
								itemName = item40507.getName();
								player.sendPackets(new S_ServerMessage(337, itemName));
							}
						}
					} catch (final Exception e) {
						_log.error("发生错误", e);
					}					
				} else {
					if (getNpcTemplate().get_npcId() == 70848) { // 安特
						int chance = RandomArrayList.getInc(100, 1);
						if (chance <= 10) {
							player.getInventory().storeItem(40506, 1);
							player.sendPackets(new S_ServerMessage(143, "$755", "$794")); 
						} else if (chance <= 60 && chance > 10) {
							player.getInventory().storeItem(40507, 6);
							player.sendPackets(new S_ServerMessage(143, "$755", "$763")); 
						} else if (chance <= 70 && chance > 60) {
							player.getInventory().storeItem(40505, 1);
							player.sendPackets(new S_ServerMessage(143, "$755", "$770")); 
						}
					}
					if (getNpcTemplate().get_npcId() == 70850) { // 潘
						int chance = RandomArrayList.getInc(100, 1);
						if (chance <= 30) {
							player.getInventory().storeItem(40519, 5);
							player.sendPackets(new S_ServerMessage(143, "$753", "$760" + " (" + 5 + ")")); 
						}
					}
					if (getNpcTemplate().get_npcId() == 70846) { // 芮克妮
						int chance = RandomArrayList.getInc(100, 1);
						if (chance <= 30) {
							player.getInventory().storeItem(40503, 1);
							player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
						}
					}
				}
				attack.calcDamage();
				attack.calcStaffOfMana();
				//attack.addPcPoisonAttack(player, this);
				//attack.addChaserAttack();
			}
			attack.action();
			attack.commit();
		} else if (getCurrentHp() > 0 && !isDead()) {
			L1AttackPc attack = new L1AttackPc(player, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				//attack.addPcPoisonAttack(player, this);
				//attack.addChaserAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		final int objid = this.getId();
		final L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(this.getNpcTemplate().get_npcId());
		final L1Object object = World.get().findObject(this.getId());
		final L1NpcInstance target = (L1NpcInstance) object;
		//final String htmlid = null;
		//final String[] htmldata = null;

		if (talking != null) {
			final int pcx = player.getX(); // PCX座標
			final int pcy = player.getY(); // PCY座標
			final int npcx = target.getX(); // NPCX座標
			final int npcy = target.getY(); // NPCY座標

			if ((pcx == npcx) && (pcy < npcy)) {
				this.setHeading(0);
				
			} else if ((pcx > npcx) && (pcy < npcy)) {
				this.setHeading(1);
				
			} else if ((pcx > npcx) && (pcy == npcy)) {
				this.setHeading(2);
				
			} else if ((pcx > npcx) && (pcy > npcy)) {
				this.setHeading(3);
				
			} else if ((pcx == npcx) && (pcy > npcy)) {
				this.setHeading(4);
				
			} else if ((pcx < npcx) && (pcy > npcy)) {
				this.setHeading(5);
				
			} else if ((pcx < npcx) && (pcy == npcy)) {
				this.setHeading(6);
				
			} else if ((pcx < npcx) && (pcy < npcy)) {
				this.setHeading(7);
			}
			this.broadcastPacketAll(new S_ChangeHeading(this));

			if (player.getLawful() < -1000) { // 
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
			} else {
				player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
			}
			// html表示送信
			/*if (htmlid != null) { // htmlid指定場合
				if (htmldata != null) { // html指定場合表示
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
				
			} else {
				if (player.getLawful() < -1000) { // 
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}*/

			// 動作暫停
			set_stop_time(REST_MILLISEC);
			this.setRest(true);
		}
	}

	/**
	 * 受攻擊hp減少計算
	 */
	@Override
	public void receiveDamage(L1Character attacker, final int damage) { // 攻擊ＨＰ減使用
		ISASCAPE = false;
		if ((attacker instanceof L1PcInstance) && (damage > 0)) {
			final L1PcInstance pc = (L1PcInstance) attacker;
			if ((pc.getType() == 2) && // 素手
					(pc.getCurrentWeapon() == 0)) {
			} else {
				if ((this.getCurrentHp() > 0) && !this.isDead()) {
					if (damage >= 0) {
						if (attacker instanceof L1EffectInstance) { // 效果不列入目標
							//this.setHate(attacker, damage);

						} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
							L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
							attacker = ill.getMaster();
							this.setHate(attacker, damage);
							
						} else {
							this.setHate(attacker, damage);
						}
						//this.setHate(attacker, damage);
					}
					if (damage > 0) {
						this.removeSkillEffect(FOG_OF_SLEEPING);
					}
					this.onNpcAI();
					// 互相幫助的判斷
					this.serchLink(pc, this.getNpcTemplate().get_family());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					final int newHp = this.getCurrentHp() - damage;
					if ((newHp <= 0) && !this.isDead()) {
						this.setCurrentHpDirect(0);
						this.setDead(true);
						this.setStatus(ActionCodes.ACTION_Die);
						
						final Death death = new Death(attacker);
						GeneralThreadPool.get().execute(death);
					}
					if (newHp > 0) {
						this.setCurrentHp(newHp);
					}
				} else if (!this.isDead()) { // 念
					this.setDead(true);
					this.setStatus(ActionCodes.ACTION_Die);
					
					final Death death = new Death(attacker);
					GeneralThreadPool.get().execute(death);
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

		/*if (this.getMaxHp() > this.getCurrentHp()) {
			this.startHpRegeneration();
		}*/
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, this.getMaxMp());

		if (this.getCurrentMp() == currentMp) {
			return;
		}

		this.setCurrentMpDirect(currentMp);

		/*if (this.getMaxMp() > this.getCurrentMp()) {
			this.startMpRegeneration();
		}*/
	}

	/**
	 * 死亡判斷
	 * @author daien
	 *
	 */
	class Death implements Runnable {
		
		L1Character _lastAttacker;
		
		/**
		 * 死亡判斷
		 * @param lastAttacker 攻擊者
		 */
		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}
		
		@Override
		public void run() {
			L1GuardianInstance.this.setDeathProcessing(true);
			L1GuardianInstance.this.setCurrentHpDirect(0);
			L1GuardianInstance.this.setDead(true);
			L1GuardianInstance.this.setStatus(ActionCodes.ACTION_Die);
			final int targetobjid = L1GuardianInstance.this.getId();
			L1GuardianInstance.this.getMap().setPassable(L1GuardianInstance.this.getLocation(), true);
			L1GuardianInstance.this.broadcastPacketAll(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));

			L1PcInstance player = null;

			// 判斷主要攻擊者
			if (_lastAttacker instanceof L1PcInstance) {// 攻擊者是玩家
				player = (L1PcInstance) _lastAttacker;
				
			} else if (_lastAttacker instanceof L1PetInstance) {// 攻擊者是寵物
				player = (L1PcInstance) ((L1PetInstance) _lastAttacker).getMaster();
				
			} else if (_lastAttacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				player = (L1PcInstance) ((L1SummonInstance) _lastAttacker).getMaster();
				
			} else if (_lastAttacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				player = (L1PcInstance) ((L1IllusoryInstance) _lastAttacker).getMaster();

			} else if (_lastAttacker instanceof L1EffectInstance) {// 攻擊者是 技能物件
				player = (L1PcInstance) ((L1EffectInstance) _lastAttacker).getMaster();
			}
			
			if (player != null) {
				final ArrayList<L1Character> targetList = L1GuardianInstance.this._hateList.toTargetArrayList();
				final ArrayList<Integer> hateList = L1GuardianInstance.this._hateList.toHateArrayList();
				final long exp = L1GuardianInstance.this.getExp();
				CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);

				final ArrayList<L1Character> dropTargetList = L1GuardianInstance.this._dropHateList.toTargetArrayList();
				if (_npc.getNpcId() != 70846 &&
				_npc.getNpcId() != 70848 &&
				_npc.getNpcId() != 70850) {
					final ArrayList<Integer> dropHateList = L1GuardianInstance.this._dropHateList.toHateArrayList();
					try {
						// XXX
						final DropShareExecutor dropShareExecutor = new DropShare();
						dropShareExecutor.dropShare(L1GuardianInstance.this._npc, dropTargetList, dropHateList);

					} catch (final Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}

				// 止刺設定。or倒場合入。
				player.addKarma((int) (L1GuardianInstance.this.getKarma() * ConfigRate.RATE_KARMA));
			}
			L1GuardianInstance.this.setDeathProcessing(false);

			L1GuardianInstance.this.setKarma(0);
			L1GuardianInstance.this.setExp(0);
			L1GuardianInstance.this.allTargetClear();

			L1GuardianInstance.this.startDeleteTimer(ConfigAlt.NPC_DELETION_TIME);
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
	}

	public void doFinalAction(final L1PcInstance player) {
	}
	/**
	 * 妖森NPC道具重置时间启动
	 * @param timer
	 */
	private void doGDropItem(final int timer) {
		GDropItemTask task = new GDropItemTask();
		GeneralThreadPool.get().schedule(task, timer);
	}
	/**
	 * 妖森NPC道具重置时间轴
	 * @author admin
	 *
	 */
	private class GDropItemTask implements Runnable {
		final int npcId = getNpcTemplate().get_npcId();

		private GDropItemTask() {
		}

		@Override
		public void run() {
			try {
				if (_configtime > 0 && !isDropitems()) {
					// 安特
					if (npcId == 70848) { 
						if (!_inventory.checkItem(40506)
								&& !_inventory.checkItem(40507)) {
							_inventory.storeItem(40506, 1);
							_inventory.storeItem(40507, 66);
							_inventory.storeItem(40505, 8);
						}
					}
					// 潘
					if (npcId == 70850) { 
						if (!_inventory.checkItem(40519)) {
							_inventory.storeItem(40519, 30);
						}
					}
					setDropItems(true);
					giveDropItems(true);
					doGDropItem(_configtime);
				} else {
					giveDropItems(false);
				}
			} catch (Exception e) {
				_log.error("资料载入错误", e);
			}
		}
	}

}
