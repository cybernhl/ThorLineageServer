package com.lineage.data.item_etcitem.event;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 喇叭<BR>
 * 
 * DELETE FROM `etcitem` WHERE `item_id`='49532'; INSERT INTO `etcitem` VALUES
 * (49532, '虔誠祝福', 'event.Item_Mazu', '虔誠祝福', 'other', 'normal', 'gemstone', 0,
 * 2563, 3963, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
 * 
 * @author loli
 * 
 */
public class laba extends ItemExecutor {

    private static final Log _log = LogFactory.getLog(laba.class);

    private laba() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new laba();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            // 例外狀況:物件為空
            if (item == null) {
                return;
            }
            // 例外狀況:人物為空
            if (pc == null) {
                return;
            }
			if (pc.isBigChat()) {
				pc.setBigChat(false);
				//item.setNowAuto(false);
				pc.sendPackets(new S_SystemMessage(item.getLogName() + "關閉。"));
			} else {
				pc.setBigChat(true);
			//	item.setNowAuto(true);
				pc.sendPackets(new S_SystemMessage(item.getLogName() + "開啟。"));
				pc.sendPackets(new S_SystemMessage("請輸入要廣播的內容"));
			}

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
