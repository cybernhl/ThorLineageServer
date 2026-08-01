package com.lineage.server.model.gametime;

/**
 * <p>
 * 時間變化受取。
 * </p>
 * <p>
 * 時間變化監視、含定義實裝、
 * 關連abstractL1GameTimeAdapter擴張。
 * </p>
 * <p>
 * 作成、
 * L1GameTimeClockaddListener使用L1GameTimeClock登錄。
 * 時間變化通知、月日時分變行。
 * </p>
 * <p>
 * 、L1GameTimeClock上動作。
 * 處理時間場合、他通知遲可能性。
 * 完了時間要處理、呼出含處理行場合、內部新作成處理行。
 * </p>
 *
 */
public interface L1GameTimeListener {
	
	/**
	 * 時間月變呼出。
	 *
	 * @param time
	 *            最新時間
	 */
	public void onMonthChanged(L1GameTime time);

	/**
	 * 時間日變呼出。
	 *
	 * @param time
	 *            最新時間
	 */
	public void onDayChanged(L1GameTime time);

	/**
	 * 時間時間變呼出。
	 *
	 * @param time
	 *            最新時間
	 */
	public void onHourChanged(L1GameTime time);

	/**
	 * 時間分變呼出。
	 *
	 * @param time
	 *            最新時間
	 */
	public void onMinuteChanged(L1GameTime time);
}
