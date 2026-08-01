package com.lineage.data.item_etcitem;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_NPCPack_Summon;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.utils.Teleportation;
import com.lineage.server.world.World;

/**
 * 哨子40315
 */
public class New_Whistle extends ItemExecutor {

	private static Random _random = new Random();
    /**
     *
     */
    private New_Whistle() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new New_Whistle();
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
    	
    	short mapId = pc.getTeleportMapId();
		final int head = pc.getTeleportHeading();
		
        pc.sendPacketsX8(new S_Sound(437));

             //   pet.call_New();
            //    pc.setTeleportX(pc.getX());
        	//	pc.setTeleportY(pc.getY());
        	//	pc.setTeleportMapId((pc.getMapId()));
        	//	pc.setTeleportHeading(pc.getHeading());
        		
        		//Teleportation.teleportation(pc);
    			if (!pc.isGhost()) {
    				// 可以攜帶寵物
    				if (pc.getMap().isTakePets()) {

    			        final Object[] petList = pc.getPetList().values().toArray();
    			        for (final Object petObject : petList) {
    			            if (petObject instanceof L1PetInstance) {
    			                final L1PetInstance pet = (L1PetInstance) petObject;
    					// 寵物的跟隨移動
    					for (final L1NpcInstance petNpc : pc.getPetList().values()) {
    						// 主人身邊隨機座標取回
    						final L1Location loc = pc.getLocation().randomLocation(3, false);
    						int nx = loc.getX();
    						int ny = loc.getY();
    						if ((pc.getMapId() == 5125) || (pc.getMapId() == 5131) || (pc.getMapId() == 5132) || (pc.getMapId() == 5133) || (pc.getMapId() == 5134)) { // 寵物戰戰場
    							nx = 32799 + _random.nextInt(5) - 3;
    							ny = 32864 + _random.nextInt(5) - 3;
    						}

    						teleport(petNpc, nx, ny, mapId, head);
    						 pet.call_New();
    						if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
                                final L1SummonInstance summon = (L1SummonInstance) petNpc;
                                pc.sendPackets(new S_NPCPack_Summon(summon, pc));

                            } else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
                                final L1PetInstance pet1 = (L1PetInstance) petNpc;
                                pc.sendPackets(new S_NPCPack_Pet(pet1, pc));
                            }
    					
    						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
    							// 畫面內可見人物 認識更新
    							visiblePc.removeKnownObject(petNpc);

    						}
    					}

       			   	 pet.call_New();
    				}
    				
    			}
    			        
            }
        }
    }
    /**
	 * 寵物的傳送
	 * 
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param head
	 */
	private static void teleport(final L1NpcInstance npc, final int x, final int y, final short map, final int head) {
		try {
			World.get().moveVisibleObject(npc, map);

			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true);
			npc.setX(x);
			npc.setY(y);
			npc.setMap(map);
			npc.setHeading(head);
			L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false);

		} catch (final Exception e) {
		}
	}

}
