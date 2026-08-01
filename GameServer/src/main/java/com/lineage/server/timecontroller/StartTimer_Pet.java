package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.pet.*;

/**
 * PET專用時間軸 初始化啟動
 * @author dexc
 *
 */
public class StartTimer_Pet {

	public void start() throws InterruptedException {
		
		// Pet HP自然回復時間軸異
		final PetHprTimer petHprTimer = new PetHprTimer();
		petHprTimer.start();
		Thread.sleep(50);// 延遲
		
		// Pet MP自然回復時間軸
		final PetMprTimer petMprTimer = new PetMprTimer();
		petMprTimer.start();
		Thread.sleep(50);// 延遲

		// Summon HP自然回復時間軸
		final SummonHprTimer summonHprTimer = new SummonHprTimer();
		summonHprTimer.start();
		Thread.sleep(50);// 延遲
		
		// Summon MP自然回復時間軸
		final SummonMprTimer summonMprTimer = new SummonMprTimer();
		summonMprTimer.start();
		Thread.sleep(50);// 延遲
		
		// 召喚獸處理時間軸
		final SummonTimer summon_Timer = new SummonTimer();
		summon_Timer.start();
		Thread.sleep(50);// 延遲
		
		// 魔法娃娃處理時間軸
		final DollTimer dollTimer = new DollTimer();
		dollTimer.start();
		
		final DollHprTimer dollHpTimer = new DollHprTimer();
		dollHpTimer.start();
		
		final DollMprTimer dollMpTimer = new DollMprTimer();
		dollMpTimer.start();
		
		final DollGetTimer dollGetTimer = new DollGetTimer();
		dollGetTimer.start();
		
		final DollAidTimer dollAidTimer = new DollAidTimer();
		dollAidTimer.start();
	}
}
