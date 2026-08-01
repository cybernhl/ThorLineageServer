package com.lineage.data.item_etcitem.teleport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTeleportTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;

/**
 * 自定義傳送符
 * 傳送點設置在etcitem_teleport內
 * 本物件使用後不會被刪除
 * classname: teleport.SOR_UserSet2
 * 設置範例:
 * teleport.SOR_UserSet2
 * @author dexc
 *
 */
public class SOR_UserSet4 extends ItemExecutor {

    private static final Log _log = LogFactory.getLog(SOR_UserSet2.class);

    /**
     *
     */
    private SOR_UserSet4() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new SOR_UserSet4();
    }

    /**
     * 道具物件執行
     * @param data 參數
     * @param pc 執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int[] loc = ItemTeleportTable.get().getLoc(item.getItemId());

        if (loc != null) {
            final int locX = loc[0];
            final int locY = loc[1];
            final short mapId = (short) loc[2];


            if (pc.getMapId() == 70) {
                // \f1您目前的條件不符合贖罪的資格，請再次確認您的PK次數、正義值及金幣是否符合。
                pc.sendPackets(new S_ServerMessage("\\f2此地區無法使用傳送符"));
                return;
            }

            if (pc.getMap().isEscapable()) {
                // 刪除道具
                //pc.getInventory().removeItem(item, 1);

                // 解除魔法技能絕對屏障
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                if (pc.isActived()) {
                    pc.setActived(false);
                    pc.sendPackets(new S_ServerMessage("掛機中請勿使用手動卷軸。"));
                    pc.sendPackets(new S_ServerMessage("自動狩獵已停止。"));
                    pc.killSkillEffectTimer(9997);
                    pc.killSkillEffectTimer(9996);
                    if( pc.get_fwgj()>0){
                        pc.setlslocx(0);
                        pc.setlslocy(0);
                        pc.set_fwgj(0);
                    }
            		/*  if (pc.getQuest().get_step(8780) == 1) {
             			 pc.killSkillEffectTimer(8132);
            			 pc.addWeightReduction(-ConfigGuaji.guajiWeight);
     	             	pc.sendPackets(new S_OwnCharStatus(pc));
             			}*/
                }

                final TeleportRunnable runnable = new TeleportRunnable(pc, locX, locY, mapId);
                GeneralThreadPool.get().schedule(runnable, 0);

                // 該地圖不允許使用回捲
            } else {
                // 276 \f1在此無法使用傳送。
                pc.sendPackets(new S_ServerMessage(276));
                // 解除傳送鎖定
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            }

            final int time = loc[3];
            if (time > 0) {
                pc.get_other().set_usemap(mapId);
                ServerUseMapTimer.put(pc, time);
                pc.sendPackets(new S_ServerMessage("使用時間限制:" + time + "秒"));
            }
        }
    }

    private class TeleportRunnable implements Runnable {

        private final L1PcInstance _pc;
        private int _locX = 0;
        private int _locY = 0;
        private int _mapid = 0;

        public TeleportRunnable(final L1PcInstance pc, final int x, final int y, final int mapid) {
            _pc = pc;
            _locX = x;
            _locY = y;
            _mapid = mapid;
        }

        @Override
        public void run() {
            final L1Map map = L1WorldMap.get().getMap((short) _mapid);
            L1Teleport.teleport(_pc, _locX, _locY, (short) _mapid, _pc.getHeading(), true);
        }
    }
}
