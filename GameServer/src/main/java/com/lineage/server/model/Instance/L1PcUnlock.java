package com.lineage.server.model.Instance;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_OwnCharPack;

public class L1PcUnlock {
	
	/**
	 * 原地更新畫面
	 * @param pc
	 */
	public static void Pc_Unlock(L1PcInstance pc) {
		
		if (pc == null) {
			return;
		}
		
        //更新Client端畫面以符合目前正確位置
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.removeAllKnownObjects();
        pc.updateObject();
        pc.sendVisualEffectAtTeleport();
        pc.sendPackets(new S_CharVisualUpdate(pc));
		//System.out.println("座標異常不執行移動送出回溯封包");
	}
	public static void Pc_Unlock_NPC(L1PcInstance pc) {
		
		if (pc == null) {
			return;
		}
		
        //更新Client端畫面以符合目前正確位置
      //  pc.sendPackets(new S_OwnCharPack(pc));
        pc.removeAllKnownObjects();
        pc.updateObject();
   //     pc.sendVisualEffectAtTeleport();
        pc.sendPackets(new S_CharVisualUpdate(pc));
		//System.out.println("座標異常不執行移動送出回溯封包");
	}


}
