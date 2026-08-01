package com.lineage.server.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 任務紀錄
 * @author admin
 *
 */
public class L1PcQuest {

	private static final Log _log = LogFactory.getLog(L1PcQuest.class);

	public static final int QUEST_OILSKINMANT = 11;

	public static final int QUEST_DOROMOND = 20;
	public static final int QUEST_RUBA = 21;
	public static final int QUEST_AREX = 22;

	public static final int QUEST_LUKEIN1 = 23;
	public static final int QUEST_TBOX1 = 24;
	public static final int QUEST_TBOX2 = 25;
	public static final int QUEST_TBOX3 = 26;
	public static final int QUEST_SIMIZZ = 27;
	public static final int QUEST_DOIL = 28;
	public static final int QUEST_RUDIAN = 29;
	public static final int QUEST_RESTA = 30;
	public static final int QUEST_CADMUS = 31;
	public static final int QUEST_KAMYLA = 32;
	public static final int QUEST_CRYSTAL = 33;
	public static final int QUEST_LIZARD = 34;
	public static final int QUEST_KEPLISHA = 35;
	public static final int QUEST_DESIRE = 36;
	public static final int QUEST_SHADOWS = 37;
	public static final int QUEST_TOSCROLL = 39;
	public static final int QUEST_MOONOFLONGBOW = 40;
	public static final int QUEST_GENERALHAMELOFRESENTMENT = 41;

	public static final int QUEST_NOT = 0; // 任務尚未開始
	public static final int QUEST_END = 255; // 任務已經結束

	private L1PcInstance _owner = null;
	private Map<Integer, Integer> _quest = null;

	/**
	 * 任務紀錄模組
	 * @param owner
	 */
	public L1PcQuest(final L1PcInstance owner) {
		this._owner = owner;
	}

	/**
	 * 傳回執行任務者
	 * @return
	 */
	public L1PcInstance get_owner() {
		return this._owner;
	}

	/**
	 * 傳回任務進度
	 * @param quest_id 任務編號
	 * @return 進度
	 */
	public int get_step(final int quest_id) {
		try {
			final Integer step = this._quest.get(new Integer(quest_id));
			if (step == null) {
				return 0;
				
			} else {
				return step.intValue();
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 建立/更新 任務資料
	 * @param quest_id 任務編號
	 * @param step 進度
	 */
	public void set_step(final int quest_id, final int step) {
		try {
			final Integer key = this._quest.get(new Integer(quest_id));
			if (key == null) {
				if (step > 1) {
					_log.error("任務資訊建立過程異常 原因:起始設置任務進度不是1 (questid:" + quest_id + ")");
					return;
				}
				// 建立 任務資料
				CharacterQuestReading.get().storeQuest(this._owner.getId(), quest_id, step);
				
			} else {
				if (step > key.intValue() + 1) {
					_log.error("任務資訊建立過程異常 原因:設置任務進度超過原始進度! (questid:" + quest_id + ")");
					return;
				}
				// 更新 任務資料
				CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, step);
			}
			
			this._quest.put(new Integer(quest_id), new Integer(step));
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 結束任務
	 * @param quest_id 任務編號
	 */
	public void set_end(final int quest_id) {
		try {
			//this.set_step(quest_id, QUEST_END);
			// 更新 任務資料
			CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, QUEST_END);
			this._quest.put(new Integer(quest_id), new Integer(QUEST_END));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void removeQuest(final int quest_id) {
		try {
			//this.set_step(quest_id, QUEST_END);
			// 更新 任務資料
			CharacterQuestReading.get().delQuest(this._owner.getId(), quest_id);
			this._quest.remove(quest_id);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 該任務是否開始 (get_step 大於0 小於255 傳回任務已經開始)
	 * @param quest_id 任務編號
	 * @return true:已經開始 false:尚未開始
	 */
	public boolean isStart(final int quest_id) {
		try {
			final int step = this.get_step(quest_id);
			// 大於0 小於255
			if (step > QUEST_NOT && step < QUEST_END) {
				return true;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 該任務是否結束
	 * @param quest_id 任務編號
	 * @return true:已經結束 false:尚未結束
	 */
	public boolean isEnd(final int quest_id) {
		try {
			if (this.get_step(quest_id) == QUEST_END) {
				return true;
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 取回人物任務紀錄
	 */
	public void load() {
		try {
			// 取回人物任務紀錄
			_quest = CharacterQuestReading.get().get(this._owner.getId());
			if (this._quest == null) {
				_quest = new HashMap<Integer, Integer>(); 
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
