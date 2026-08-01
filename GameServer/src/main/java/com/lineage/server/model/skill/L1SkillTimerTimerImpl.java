package com.lineage.server.model.skill;

import java.util.concurrent.ScheduledFuture;

import com.lineage.server.model.Instance.L1PetInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.thread.GeneralThreadPool;

import static com.lineage.server.model.item.L1ItemId.*;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_HASTE_SELF;

/**
 * 技能效果時間軸
 * @author daien
 *
 */
public class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {

	private static final Log _log = LogFactory.getLog(L1SkillTimerTimerImpl.class);

	private ScheduledFuture<?> _future = null;

	private final L1Character _cha;
	
	private final int _timeMillis;
	
	private final int _skillId;
	
	private int _remainingTime;

	/**
	 * 技能效果時間軸
	 * @param cha 執行者
	 * @param skillId 技能編號
	 * @param timeMillis 技能時間(毫秒)
	 */
	public L1SkillTimerTimerImpl(final L1Character cha, final int skillId, final int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		/*if (_skillId == 68||_skillId == 89) {
			System.out.println("_remainingTime:"+_remainingTime);
		}*/
		if (_remainingTime <= 0) {
			/*if (_skillId == 68||_skillId == 89) {
				System.out.println("_remainingTime:"+0);
			}*/
			_cha.removeSkillEffect(_skillId);
			if (_cha instanceof L1PetInstance) {
				if (_skillId == L1SkillId.STATUS_HASTE) {
					// 效果長順
					if (_cha.getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)
							|| _cha.getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF, 1)
							|| _cha.getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)
							|| _cha.getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
						((L1PetInstance) _cha).useItem(1, 100);
					}
				}
			}
		}
	}

	@Override
	public void begin() {
		//System.out.println("skillId:"+_skillId + " " + _remainingTime + "/" + _timeMillis);
		_future = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);

		} catch (final Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void kill() {
		try {
			if (_future != null) {
				// 試圖取消對此任務的執行。
				//如果任務已完成、或已取消，或者由於某些其他原因而無法取消，則此嘗試將失敗。
				//當調用 cancel 時，如果調用成功，而此任務尚未啟動，則此任務將永不運行。
				//如果任務已經啟動，則 mayInterruptIfRunning 參數確定是否應該以試圖停止任務的方式來中斷執行此任務的線程。 
				
				//此方法返回後，對 isDone() 的後續調用將始終返回 true。
				//如果此方法返回 true，則對 isCancelled() 的後續調用將始終返回 true。 
				//參數：
				//mayInterruptIfRunning - 如果應該中斷執行此任務的線程，則為 true；否則允許正在運行的任務運行完成 
				//返回：
				//如果無法取消任務，則返回 false，這通常是由於它已經正常完成；否則返回 true
				_future.cancel(false);
			}

		} catch (final Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}
}
