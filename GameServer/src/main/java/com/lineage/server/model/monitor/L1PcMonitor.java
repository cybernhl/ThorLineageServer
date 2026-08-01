package com.lineage.server.model.monitor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.World;

/**
 * L1PcInstance定期處理、監視處理等行為共通的處理實裝抽像
 *
 * 各處理{@link #run()}{@link #execTask(L1PcInstance)}實裝。
 * PC上存在場合、run()即座。
 * 場合、定期實行、處理等停止必要。
 * 停止止、永遠定期實行。 定期實行單發場合制御不要。
 *
 * L1PcInstance參照直接持望。
 *
 * @author frefre
 *
 */
public abstract class L1PcMonitor implements Runnable {

	/** 對像L1PcInstanceID */
	protected int _id;

	/**
	 * 指定L1PcInstance對作成。
	 *
	 * @param oId {@link L1PcInstance#getId()}取得ID
	 */
	public L1PcMonitor(final int oId) {
		this._id = oId;
	}

	@Override
	public final void run() {
		final L1PcInstance pc = (L1PcInstance) World.get().findObject(this._id);
		if ((pc == null) || (pc.getNetConnection() == null)) {
			return;
		}
		this.execTask(pc);
	}

	/**
	 * 實行時處理
	 *
	 * @param pc
	 *            對像PC
	 */
	public abstract void execTask(L1PcInstance pc);
}
