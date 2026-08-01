package com.lineage.echo;


import com.lineage.server.WriteLogTxt;
import com.lineage.server.clientpackets.C_NPCAction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class PacketCheck {
    private final ReentrantLock lock = new ReentrantLock(false);
    private final Map<Integer, PacketCheckData> packet_check = new HashMap<>();
    private final ClientExecutor client;
    public PacketCheck(final ClientExecutor client) {
        this.client = client;
    }

    public void addCheck(final int opcode) {
        if (
                opcode == OpcodesClient.C_OPCODE_USESKILL ||
        opcode == OpcodesClient.C_OPCODE_MOVECHAR ||
        opcode == OpcodesClient.C_OPCODE_TRADEADDITEM ||
        opcode == OpcodesClient.C_OPCODE_ARROWATTACK ||
        opcode == OpcodesClient.C_OPCODE_USEITEM ||
        opcode == OpcodesClient.C_OPCODE_CHAT ||
        opcode == OpcodesClient.C_OPCODE_CHATGLOBAL ||
        opcode == OpcodesClient.C_OPCODE_DELETEINVENTORYITEM ||
        opcode == OpcodesClient.C_OPCODE_ATTACK ||
        opcode == OpcodesClient.C_OPCODE_CHARRESET ||
        opcode == OpcodesClient.C_OPCODE_CHANGEHEADING) {
            return;
        }
        if (!packet_check.containsKey(opcode)) {
            lock.lock();
            try {
                packet_check.put(opcode, new PacketCheckData());
            } finally {
                lock.unlock();
            }
        }
        final PacketCheckData data = this.packet_check.get(opcode);
        final long now = System.currentTimeMillis();
        if (data.isTimeOk(now, opcode)) {
            data.clearCount();
        } else {
            data.addCount();
            if (data.getCount() >= 15) {
                if (opcode == OpcodesClient.C_OPCODE_NPCACTION && this.client.getActiveChar() != null && this.client.getActiveChar().getMapId() == 96) {
                    if (data.getCount() >= 50) {
                        WriteLogTxt.NormalLog("CC攻擊", (client != null ? (client.getActiveChar() != null ? client.getActiveChar().getName() : client.getAccountName()) : "未知連線") + " 觸發斷線 opcode:" + opcode);
                        if (this.client != null) {
                            this.client.kick();
                        }
                    }
                } else {
                    WriteLogTxt.NormalLog("CC攻擊", (client != null ? (client.getActiveChar() != null ? client.getActiveChar().getName() : client.getAccountName()) : "未知連線") + " 觸發斷線 opcode:" + opcode);
                    if (this.client != null) {
                        this.client.kick();
                    }
                }
//                if (opcode == OpcodesClient.C_OPCODE_USEITEM) {
//                        System.out.println((this.client.getActiveChar() != null ? this.client.getActiveChar().getName() : "未知玩家") + " 瘋狂使用道具");
//                        data.clearCount();
//                } else if (opcode == OpcodesClient.C_OPCODE_USESKILL) {
//                        System.out.println((this.client.getActiveChar() != null ? this.client.getActiveChar().getName() : "未知玩家") + " 瘋狂使用技能");
//                        data.clearCount();
//                }

            }
        }
        data.setTime(now);
    }
    public static class PacketCheckData {
        private long time = System.currentTimeMillis();
        private int count = 0;
        public final void clearCount() {
            this.count = 0;
        }
        public final void addCount() {
            this.count++;
        }
        public final int getCount() {
            return this.count;
        }
        public final long getTime() {
            return this.time;
        }
        public final void setTime(final long time) {
            this.time = time;
        }

        public final boolean isTimeOk(final long now, final int opcode) {
            return (now - this.time) >= (opcode == OpcodesClient.C_OPCODE_USEITEM || opcode == OpcodesClient.C_OPCODE_USESKILL ? 30 : 2000);
        }
    }
}
