package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ShopXTable;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.ShopXTime;

/**
 * 托售管理員
 * 
 * @author dexc
 * 
 */
public class KXYS extends EventExecutor {

    private static final Log _log = LogFactory.getLog(KXYS.class);

    public static int tiem;// 手續費

    public static int KX1;// 寄售時間(天)

    public static int KX2;// 寄售時間(天)

    public static int KX3;// 寄售時間(天)
    
    public static int KX4;// 寄售時間(天)
    
    public static int KX5;// 寄售時間(天)
    
    public static int KX6;// 寄售時間(天)
    
    public static int KX7;// 寄售時間(天)
    
    public static int KX8;// 寄售時間(天)
    
    public static int KX9;// 寄售時間(天)

    /**
     *
     */
    private KXYS() {
        // TODO Auto-generated constructor stub
    }

    public static EventExecutor get() {
        return new KXYS();
    }

    @Override
    public void execute(final L1Event event) {
        try {
            final String[] set = event.get_eventother().split(",");

            tiem = Integer.parseInt(set[0]);

            KX1 = Integer.parseInt(set[1]);

            KX2 = Integer.parseInt(set[2]);

            KX3 = Integer.parseInt(set[3]);
            
            KX4 = Integer.parseInt(set[4]);
            
            KX5 = Integer.parseInt(set[5]);
            
            KX6 = Integer.parseInt(set[6]);
            
            KX7 = Integer.parseInt(set[7]);
            
            KX8 = Integer.parseInt(set[8]);
            
            KX9 = Integer.parseInt(set[9]);
           

        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
