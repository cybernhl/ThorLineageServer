package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.lock.CharGemReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.templates.L1Event;

/**
 * 
 * 類名稱：GemItemSet<br>
 * 類描述：武器寶石鑲嵌系統<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:09:52<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class GemItemSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(GemItemSet.class);
	
	// 凹槽系統
	public static boolean START = false;

	/**強化成功機率(1/100)*/
	public static int HOLER = 0;

	/**最大加成數量*/
	public static int ARMORHOLE = 0;
	
	/**
	 *
	 */
	private GemItemSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new GemItemSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;
			
			final String[] set = event.get_eventother().split(",");
			
			HOLER = Integer.parseInt(set[0]);
			
			ARMORHOLE = Integer.parseInt(set[1]);
			
			//人物額外屬性資料
			CharGemReading.get().load();
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
