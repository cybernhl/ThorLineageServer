package com.lineage.server.timecontroller.pc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 動作延遲使用
 * 
 * @author QQ:759347094
 * 
 */
public class skillHardDelay {

    private static final Log _log = LogFactory.getLog(skillHardDelay.class);
    final static int time = 400; //動作停滯400毫秒 

    /**
     * 動作延遲使用
     */
    private skillHardDelay() {
    }

    static class skillHardDelayTimer implements Runnable {

        private L1PcInstance _pc;

        public skillHardDelayTimer(final L1PcInstance pc) {
            _pc = pc;
        }

        @Override
        public void run() {
            stopDelayTimer();
        }

        public void stopDelayTimer() {
            _pc.setskillHardDelay(false);
//            System.out.println("skillHardDelay=" + _pc.isskillHardDelay());
        }
    }

    /**
     * 設置動作延遲使用
     * 
     * @param pc
     * @param time
     */
    public static void onHardUse(final L1PcInstance pc) {
        try {
//        	System.out.println("skillHardDelay=" + pc.isskillHardDelay());
            GeneralThreadPool.get().schedule(new skillHardDelayTimer(pc), time);

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
