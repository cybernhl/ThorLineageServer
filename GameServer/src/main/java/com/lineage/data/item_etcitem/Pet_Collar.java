package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import java.util.Collection;
import java.util.Map;

public class Pet_Collar extends ItemExecutor
{
  public static ItemExecutor get()
  {
    return new Pet_Collar();
  }

  public void execute(int[] data, L1PcInstance pc, L1ItemInstance item)
  {
    if (pc.getInventory().checkItem(41160)) {
      if (withdrawPet(pc, item.getId())) {
        pc.getInventory().consumeItem(41160, 1L);
      }
    }
    else
      pc.sendPackets(new S_ServerMessage(79));
  }

  private boolean withdrawPet(L1PcInstance pc, int itemObjectId)
  {
    if (!pc.getMap().isTakePets()) {
      pc.sendPackets(new S_ServerMessage(563));
      return false;
    }

    int petCost = 0;
	int petCount = 0;
    int divisor = 6;
    Object[] petList = pc.getPetList().values().toArray();
    if (petList.length > 4)
    {
      pc.sendPackets(new S_ServerMessage(489));
      return false;
    }
    for (Object pet : petList) {
      if (((pet instanceof L1PetInstance)) && 
        (((L1PetInstance)pet).getItemObjId() == itemObjectId)) {
        return false;
      }

      petCost += ((L1NpcInstance)pet).getPetcost();
    }
    int charisma = pc.getCha();
//    if (pc.isCrown()) { // 王
//		charisma += 6;
//	} else if (pc.isKnight()) { // 騎
//		charisma += 0;
//	} else if (pc.isElf()) { // 妖
//		charisma += 12;
//	} else if (pc.isWizard()) { // 法
//		charisma += 6;
//	}
  /*  charisma -= petCost;
    int petCount = charisma / 6;
    if (petCount <= 0)  {
      pc.sendPackets(new S_ServerMessage(489));
      return false;
    }*/

    L1Pet l1pet = PetReading.get().getTemplate(itemObjectId);

    if (l1pet != null) {
		final int npcId = l1pet.get_npcid();
		charisma -= petCost;
		if ((npcId == 45313) || (npcId == 45710) // 虎男、高等斗虎
				|| (npcId == 45711) || (npcId == 45712)) { // 高麗幼犬、高麗犬
			divisor = 3;
		} else {
			divisor = 3;
		}

		petCount = charisma / divisor;

		if (petCount <= 0) {
			pc.sendPackets(new S_ServerMessage("你無法一次控制那麼多寵物。")); // 你無法一次控制那麼多寵物。
			return false;
		}

		final L1Npc npcTemp = NpcTable.get().getTemplate(npcId);
	    final L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
		pet.setPetcost(divisor);
		return true;
	}
	return false;
}
}