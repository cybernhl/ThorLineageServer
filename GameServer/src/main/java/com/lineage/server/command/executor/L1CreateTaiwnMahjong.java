package com.lineage.server.command.executor;

import com.add.CustomTaiwanMahjong;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1CreateTaiwnMahjong implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1CreateTaiwnMahjong.class);
    public L1CreateTaiwnMahjong() {

    }
    public static L1CommandExecutor getInstance() {
        return new L1CreateTaiwnMahjong();
    }
    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        for (int i = 0; i < 7; i++) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678890);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 4);
            npc.setY(pc.getY() - 2 + i);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        for (int i = 0; i < 8; i++) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678890);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() + 5);
            npc.setY(pc.getY() - 3 + i);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        for (int i = 0; i < 7; i++) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678890);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 3 + i);
            npc.setY(pc.getY() - 3);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        for (int i = 0; i < 8; i++) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678890);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 3 + i);
            npc.setY(pc.getY() + 5);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        if (true) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678888);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX());
            npc.setY(pc.getY() + 1);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
            CustomTaiwanMahjong.get().setNpc(npc);
        }
        if (true) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678892);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 3 + 7);
            npc.setY(pc.getY() - 3);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        if (true) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678893);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 4);
            npc.setY(pc.getY() - 3);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        if (true) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678894);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() + 4);
            npc.setY(pc.getY() + 4);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
        if (true) {
            final L1NpcInstance npc = NpcTable.get().newNpcInstance(30678895);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(pc.getMap());

            npc.setX(pc.getX() - 4);
            npc.setY(pc.getY() - 2 + 7);

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());

            npc.setHeading(pc.getHeading());

            // 設置副本編號 TODO
            npc.set_showId(pc.get_showId());

            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);

            npc.turnOnOffLight();

            NpcSpawnTable.get().storeSpawn2(npc, npc.getNpcTemplate());
        }
    }
}
