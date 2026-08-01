package com.lineage.server.timecontroller.pet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldSummons;

/**
 * 召喚獸處理時間軸
 * @author dexc
 *
 */
public class SummonTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(SummonTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;// 60秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1SummonInstance> allPet = WorldSummons.get().all();
			// 不包含元素
			if (allPet.isEmpty()) {
				return;
			}
			
			for (final Iterator<L1SummonInstance> iter = allPet.iterator(); iter.hasNext();) {
				final L1SummonInstance summon = iter.next();
				final int time = summon.get_time() - 60;
				// time -= 60;
				if (time <= 0) {
					outSummon(summon);
					
				} else {
					summon.set_time(time);
				}
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.error("召喚獸處理時間軸異常重啟", e);
            _timer.cancel(false);
            // GeneralThreadPool.get().cancel(_timer, false);
			final SummonTimer summon_Timer = new SummonTimer();
			summon_Timer.start();
		}
	}

	/**
	 * 移除召喚獸
	 * @param tgpc
	 */
	private static void outSummon(final L1SummonInstance summon) {
		try {
			if (summon != null) {
				if (summon.destroyed()) {
					return;
				}
				// 怪物解散處理
				L1Inventory targetInventory = null;// 主人的背包
				if (summon.getMaster() != null) {
					if (summon.getMaster().getInventory() != null) {// 主人存在並且背包不為空
						targetInventory = summon.getMaster().getInventory();
					}
				}

				final List<L1ItemInstance> items = summon._inventory.getItems();
				for (final L1ItemInstance item : items) {
					if (targetInventory != null) {
						// 容量重量確認及送信
						if (summon.getMaster().getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
							summon._inventory.tradeItem(item, item.getCount(), targetInventory);
							// 143:\f1%0%s 給你 %1%o 。
							((L1PcInstance) summon.getMaster()).sendPackets(new S_ServerMessage(143, summon.getName(), item.getLogName()));

						} else { // 超過持有物件數量(掉落地面)
							item.set_showId(summon.get_showId());
							targetInventory =
									World.get().getInventory(summon.getX(), summon.getY(), summon.getMapId());
							summon._inventory.tradeItem(item, item.getCount(), targetInventory);
						}
					} else { // 主人遺失(掉落地面)
						item.set_showId(summon.get_showId());
						targetInventory =
								World.get().getInventory(summon.getX(), summon.getY(), summon.getMapId());
						summon._inventory.tradeItem(item, item.getCount(), targetInventory);
					}
				}
				summon.deleteMe();
//				if (summon.tamed()) {
//					// 召喚獸解放
//					summon.liberate();
//
//				} else {
//					// 解散
//					summon.Death(null);
//				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
