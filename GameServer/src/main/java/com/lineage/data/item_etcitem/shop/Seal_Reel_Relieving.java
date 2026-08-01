package com.lineage.data.item_etcitem.shop;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/***
 * 祝福的捲軸
 */
public class Seal_Reel_Relieving extends ItemExecutor {

    /**
     *
     */
    private Seal_Reel_Relieving() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Seal_Reel_Relieving();
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
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemobj = data[0];
        final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }        
        if (item1 != null &&item1.getItem().getType2() == 1|| item1.getItem().getType2() == 2){
         if(item1.getBless()==0||item1.getBless()==128){
         	pc.sendPackets(new S_ServerMessage("已經是祝福的物品"));
         	return;	
         }  
        if(item1.getBless()==2||item1.getBless()==130){
        	_xianzhi /=2;	
        }
        final Random _random = new Random();
        if(_random.nextInt(99) + 1<=_xianzhi){
                item1.setBless(0);                
                pc.getInventory().updateItem(item1, L1PcInventory.COL_ITEMID + L1PcInventory.COL_BLESS);
                pc.getInventory().saveItem(item1, L1PcInventory.COL_ITEMID + L1PcInventory.COL_BLESS);
                pc.sendPackets(new S_ServerMessage("祝福成功")); // 沒有任何事情發生。
                pc.getInventory().removeItem(item, 1); 
            } else {
                pc.sendPackets(new S_ServerMessage("祝福失敗")); // 沒有任何事情發生。
                pc.getInventory().removeItem(item, 1); 
            }
           }
        }
    private int _xianzhi = 0;   	
   	@Override
   	public void set_set(String[] set) {
   		try {
   			_xianzhi = Integer.parseInt(set[1]);   			
   			
   		} catch (Exception e) {
   		}
   	}
   	
   }

