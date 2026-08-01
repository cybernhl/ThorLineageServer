package com.lineage.data.item_etcitem.teleport;

import java.sql.Timestamp;

import com.lineage.config.ConfigGuaji;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 掛機開關符 58030
 */
public class Hang_fu extends ItemExecutor {
	
    /**
	 *
	 */
    private Hang_fu() {
    	
    	// TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
    	
    	return new Hang_fu();
    }

    /**
     * 道具物件執行
     * 
     * @param data
     *            參數
     * @param pc
     *            執行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
		if (true) {
			pc.startAI();
			pc.setSkillEffect(9997, 120 * 60 * 1000);//強致掛機120分後回村休息 10分
			pc.sendPackets(new S_ServerMessage("自動狩獵已開始。"));
			pc.sendPackets(new S_ServerMessage("請勿在自動狩獵點擊物品.消失物品一概不負責。"));
			pc.sendPackets(new S_ServerMessage("掛機中:不顯示傷害 掉落物品。"));
			return;
		}
   	
    	  String type1 = "";
   	  	  String type2 = "0";
		  String type3 = "";  
		  String type4 = "";  
		  String type5 = "";  
		  String type6 = ""; 
		  String type7 = "0"; 
		  String type8 = "尚未設定"; 
		  String type9 = "0"; 
		  String type10 = "尚未設定"; 
		  String type11 = "0"; 
		  String type12 = "尚未設定"; 
		  String type13 = "0"; 
		  String type14 = ""; 
		
		
			/*  if(pc.get_fwgj()>0){
					type2 = ""+pc.get_fwgj();   	
       	}  
			*/
		
					  pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "AI", new String[]{type1,type2,type3,type4,type5,type6,type7,type8,type9,type10,type11,type12,type13}));
				  
		
    }
}
