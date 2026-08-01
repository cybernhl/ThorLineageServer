package com.lineage.server.command.executor;

import com.lineage.server.datatables.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class L1Reload implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Reload.class);

    public static L1CommandExecutor getInstance() {
        return new L1Reload();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        if (arg.equalsIgnoreCase("掉寶")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入掉寶資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入掉寶資料數量。"));
            }
            DropTable.get().load();
            DropMapTable.get().load();

        } else if (arg.equalsIgnoreCase("shop")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入商店販賣資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入商店販賣資料數量。"));
            }
            ShopTable.get().load();
            ShopCnTable.get().restshopCn();

        } else if (arg.equalsIgnoreCase("armor")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入防具資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入防具資料。"));
            }

            ItemTable.get().load();
            ArmorSetTable.get().load();

        } else if (arg.equalsIgnoreCase("etcitem")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入道具資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入道具資料。"));
            }

            ItemTable.get().load();

        } else if (arg.equalsIgnoreCase("npcchat")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入npcchat資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入npcchat資料。"));
            }

            NPCTalkDataTable.get().load();

        } else if (arg.equalsIgnoreCase("server_shopx")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入禁止拍賣物品資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入禁止拍賣物品資料數量。"));
            }

            ShopXTable.get().load();

        } else if (arg.equalsIgnoreCase("skills")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入技能設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入技能設置資料數量。"));
            }

            SkillsTable.get().load();

        } else if (arg.equalsIgnoreCase("droplist_map")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入掉落物品資料數量(指定地圖)。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入掉落物品資料數量(指定地圖)。"));
            }

            DropMapTable.get().load();

        } else if (arg.equalsIgnoreCase("server_event")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入活動設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入活動設置資料數量。"));
            }

            EventTable.get().load();

        }else if (arg.equalsIgnoreCase("dungeon")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入地圖切換點設置數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入地圖切換點設置數量。"));
            }

            DungeonTable.get().load();

        } else if (arg.equalsIgnoreCase("npc")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入NPC設置資料數量。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入NPC設置資料數量。"));
            }

            NpcTable.get().load();

        } else if (arg.equalsIgnoreCase("spawnlist")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入放怪資料。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入放怪資料。"));
            }
            SpawnTable.get().load();
        } else if (arg.equalsIgnoreCase("npcaction")) {
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重新載入npcaction。");
            } else {
                pc.sendPackets(new S_SystemMessage("重新載入npcaction。"));
            }
            NpcActionTable.load();
        }
    }
}