package com.lineage.data.item_etcitem;

import com.lineage.data.event.CardSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

public class SpecialCard extends ItemExecutor
{

	public SpecialCard() 
	{
	}
	public static ItemExecutor get()
	{
		return new SpecialCard();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item)
	{
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		if (!CardSet.START) {
			pc.sendPackets(new S_ServerMessage("\\fR目前未開放此道具使用。"));
			return;
		}
		if (item.get_card_use() == 2) {// 到期
			pc.sendPackets(new S_ServerMessage("\\fT這個道具已經到期。"));
			return;
		}
		if (item.get_card_use() == 1) {// 使用中			
			item.set_card_use(0);
			pc.sendPackets(new S_ServerMessage("\\fT這個道具已經解除使用。"));
			pc.setCardUseCount(pc.getCardUseCount()-1);
			CardSet.remove_card_mode(pc, item);
			return;
		}
		if (pc.getCardUseCount() >= CardSet.USE_COUNT) {// 使用中
			pc.sendPackets(new S_ServerMessage("\\fT你使用的卡片數量已經達到最大上限。"));
			return;
		} 
		if (pc.getInventory().checkCardEquipped(item.getItemId())) {
			L1Item temp = ItemTable.get().getTemplate(item.getItemId());
			// 已經開啟使用(相同道具)
			pc.sendPackets(new S_ServerMessage("\\fR你已經啟動了一個" + temp.getName()));
			return;
		}
		if (pc.getInventory().checkCardEquipped1(item.getItem().getWeight())) {			
			// 已經開啟使用(相同道具)
			pc.sendPackets(new S_ServerMessage("同屬性的卡片只能佩戴一個"));
			return;
		}
		if (item.get_card_use() == 0){
			if (item.get_time() == null){
				item.set_card_use(1);
				pc.setCardUseCount(pc.getCardUseCount()+1);
				CardSet.set_card_mode(pc, item);
			}else{
				item.set_card_use(1);
				pc.setCardUseCount(pc.getCardUseCount()+1);
				CardSet.set_card_mode2(pc, item);
			}
			pc.sendPackets(new S_ServerMessage("\\fT這個道具已經開啟，正在使用中。"));
		}
	}
}
