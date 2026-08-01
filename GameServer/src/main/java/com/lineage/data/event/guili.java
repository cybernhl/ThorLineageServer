package com.lineage.data.event;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.CardUseTalble;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.templates.L1Event;
import com.william.CardUse;

/**
 * 月卡系统<BR>
 * @author dexc
 *
 */
public class guili extends EventExecutor {

	private static final Log _log = LogFactory.getLog(guili.class);
	
	// 月卡系统
	public static boolean START = false;
	
	/**
	 *
	 */
	private guili() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new guili();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
   }

