package com.lineage.server.timecontroller;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.server.timecontroller.pc.*;

/**
 * PC專用時間軸 初始化啟動
 * @author dexc
 *
 */
public class StartTimer_Pc {

	public void start() throws InterruptedException {
		// 人物資料自動保存計時器
		if (Config.AUTOSAVE_INTERVAL > 0) {
			final PcAutoSaveTimer save = new PcAutoSaveTimer();
			save.start();
		}
		// 背包物品自動保存計時器
		if (Config.AUTOSAVE_INTERVAL_INVENTORY > 0) {
			final PcAutoSaveInventoryTimer save = new PcAutoSaveInventoryTimer();
			save.start();
		}
		// 人物釣魚計時器
		final PcFishingTimer fishingTimeController = new PcFishingTimer();
		fishingTimeController.start();
		Thread.sleep(50);// 延遲
		
		// PC 可見物更新處理 時間軸 XXX
		final UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
		objectCTimer.start();
	
		final UpdateObjectETimer objectETimer = new UpdateObjectETimer();
		objectETimer.start();
		final UpdateObjectKTimer objectKTimer = new UpdateObjectKTimer();
		objectKTimer.start();
		final UpdateObjectWTimer objectWTimer = new UpdateObjectWTimer();
		objectWTimer.start();
		Thread.sleep(50);// 延遲
		
		final HprTimerCrown hprCrown = new HprTimerCrown();
		hprCrown.start();
	
		final HprTimerElf hprElf = new HprTimerElf();
		hprElf.start();
		final HprTimerKnight hprKnight = new HprTimerKnight();
		hprKnight.start();
		final HprTimerWizard hprWizard = new HprTimerWizard();
		hprWizard.start();
		Thread.sleep(50);// 延遲

		final MprTimerCrown mprCrown = new MprTimerCrown();
		mprCrown.start();
		
		final MprTimerElf mprElf = new MprTimerElf();
		mprElf.start();
		final MprTimerKnight mprKnight = new MprTimerKnight();
		mprKnight.start();
		final MprTimerWizard mprWizard = new MprTimerWizard();
		mprWizard.start();
		Thread.sleep(50);// 延遲
		
		// 施毒術PC扣血計算時間軸
		final HprTimerBloodlettingPc BloodlettingPc = new HprTimerBloodlettingPc();
		BloodlettingPc.start();
		// PC EXP更新處理 時間軸
		final ExpTimer expTimer = new ExpTimer();
		expTimer.start();
		// PC Lawful更新處理 時間軸
		final LawfulTimer lawfulTimer = new LawfulTimer();
		lawfulTimer.start();
		// PC 死亡刪除處理 時間軸
		final PcDeleteTimer deleteTimer = new PcDeleteTimer();
		deleteTimer.start();
		// PC 鬼魂模式處理 時間軸
		final PcGhostTimer ghostTimer = new PcGhostTimer();
		ghostTimer.start();
		// PC 解除人物卡點計時時間軸
		final UnfreezingTimer unfreezingTimer = new UnfreezingTimer();
		unfreezingTimer.start();
		// 隊伍更新時間軸
		final PartyTimer partyTimer = new PartyTimer();
		partyTimer.start();
		Thread.sleep(50);// 延遲
		
		if (ConfigAlt.ALT_PUNISHMENT) {
			final PcHellTimer hellTimer = new PcHellTimer();
			hellTimer.start();
		}
	}
}
