package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;

/**
 * 施毒術PC扣血計算時間軸<br>
 * 類名稱：HprTimerBloodlettingPc<br>
 * 創建人:xljnet<br>
 * 修改時間：2018年4月30日 下午9:26:10<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version Rev:3.2 Bin:81222<br>
 */
public class HprTimerBloodlettingPc extends TimerTask {

	private static final Log _log = LogFactory.getLog(HprTimerBloodlettingPc.class);
	
	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000;// 1秒一次
		_timer = PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
	
	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}
			
			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (check(tgpc)) {
					//tgpc.setCurrentHp(tgpc.getCurrentHp() - (50 * tgpc.getBloodletting()));
					tgpc.receiveDamage(tgpc, (50 * tgpc.getBloodletting()), false, false);
					Thread.sleep(1);
				}
			}
		} catch (final Exception e) {
			_log.error("施毒術PC扣血計算時間軸異常重啟", e);
			PcOtherThreadPool.get().cancel(_timer, false);
			final HprTimerBloodlettingPc Bloodlettingpc = new HprTimerBloodlettingPc();
			Bloodlettingpc.start();
		}
	}
	
	/**
	 * 判斷是否符合扣血條件
	 * @param tgpc 
	 * @return true:執行 false:不執行
	 */
	private static boolean check(L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}
			
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			
			// 死亡
			if (tgpc.isDead()) {
				return false;
			}
			
			if (tgpc.isSafetyZone()) {
				return false;
			}
			
			if (tgpc.isTradingInPrivateShop()) {
				return false;
			}
			
			if (tgpc.isGhost()) {
				return false;
			}
			
			if (tgpc.isPrivateShop()) {
				return false;
			}
			
			if (!tgpc.hasSkillEffect(L1SkillId.Bloodletting)) {
				return false;
			}
			
		} catch (final Exception e) {
			return false;
		}
		return true;
	}
}
