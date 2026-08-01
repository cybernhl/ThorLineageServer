package com.lineage.data.npc.game;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
public class CustomDots {
    public static final CustomDots instance = new CustomDots();

    public static CustomDots getInstance() {
        return instance;
    }

    private final Map<String, BetType> players = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    private final int costId = 40308;
    private int bankerOID = -1;
    private boolean opened = false, opened2 = false;
    private int timer = 10 * 1000;
    private int moneyPool = 0;

    private int dice1Point = 0;
    private int dice2Point = 0;
    private L1NpcInstance masterNpc = null;

    private final List<Card> cards = new ArrayList<>();
    public final String startBet(final L1PcInstance pc, final int price, final int type) {
        if (this.opened || this.opened2) {
            return "請等待本局結束在進行下注";
        }
        if (pc == null) {
            return "發生未知錯誤";
        }
        if (players.containsKey(pc.getName())) {
            return "您已經下注";
        }
        if (!pc.getInventory().checkItem(costId, price)) {
            return "您身上的金幣不足 " + String.format("%,d", price) + " 元";
        }
        pc.getInventory().consumeItem(costId, price);
        this.lock.lock();
        try {
            this.players.put(pc.getName(), new BetType(price, type));
        } finally {
            this.lock.unlock();
        }
        final L1NpcInstance npc = getMasterNpc();
        if (npc != null) {
            npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】" + pc.getName() + " 下注 【" + getTypeByName(type) + "】金額: " + String.format("%,d 元", price)));
        }
        return "下注成功，請等待開牌";
    }

    public final boolean setBankerOID(final L1PcInstance pc) {
        if (this.opened || this.opened2) {
            return false;
        }
        if (pc == null) {
            return false;
        }
        this.lock.lock();
        try {
            if (this.bankerOID != -1) {
                return false;
            }
            this.bankerOID = pc.getId();
            this.opened = true;
            this.opened2 = true;
            GeneralThreadPool.get().schedule(new OpenTimer(), timer);
            final L1NpcInstance npc = getMasterNpc();
            if (npc != null) {
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】" + pc.getName() + " 搶莊成功，請賭客開始下注，30秒後停止下注"));
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】" + "底池最大金額: " + String.format("%,d", moneyPool)));
            }
        } finally {
            this.lock.unlock();
        }
        return true;
    }

    public final String getTypeByName(final int type) {
        switch (type) {
            case 1:
                return "初";
            case 2:
                return "川";
            case 3:
                return "尾";
        }
        return "";
    }

    public final L1NpcInstance getMasterNpc() {
        if (masterNpc != null) {
            return masterNpc;
        }
        for (final L1Object object : World.get().getObject()) {
            if (!(object instanceof L1NpcInstance)) {
                continue;
            }
            final L1NpcInstance npc = (L1NpcInstance) object;
            if (npc.getX() == 33482 && npc.getY() == 32802 && npc.getMapId() == 4) {
                masterNpc = npc;
                return npc;
            }
        }
        return null;
    }

    public static class BetType {
        private final int price;
        private final int type;
        public BetType(final int price, final int type) {
            this.price = price;
            this.type = type;
        }

        public final int getPrice() {
            return this.price;
        }

        public final int getType() {
            return this.type;
        }
    }

    public static class Card {
        private final int card1;
        private final int card2;
        private static final String[] names = {"白板", "一筒", "二筒", "三筒", "四筒", "五筒", "六筒", "七筒", "八筒", "九筒"};
        private static final String[] names2 = {"沒點", "一點", "兩點", "三點", "四點", "五點", "六點", "七點", "八點", "九點"};
        public Card(final int card1, final int card2) {
            this.card1 = card1;
            this.card2 = card2;
        }

        public final int getCard1() {
            return this.card1;
        }

        public final int getCard2() {
            return this.card2;
        }

        public final String getName() {
            if (this.card1 == this.card2) {
                return names[this.card1] + "對子";
            }
            if (this.card1 == 0 || this.card2 == 0) {
                return names2[Math.max(this.card1, this.card2)] + "半";
            }
            return names2[(this.card1 + this.card2) % 10];
        }
    }
    private class OpenTimer extends TimerTask {
        @Override
        public void run() {
            lock.lock();
            try {
                opened = false;
            } finally {
                lock.unlock();
            }
            final L1NpcInstance npc = getMasterNpc();
            if (npc != null) {
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】停止下注，5 秒後開局"));
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "-"));
            }
            GeneralThreadPool.get().schedule(new Open2Timer(), 5 * 1000);
        }
    }

    public static final int rand(final int lbound, final int ubound) {
        return (int) ((new Random().nextDouble() * (ubound - lbound + 1)) + lbound);
    }

    private class Open2Timer extends TimerTask {
        @Override
        public void run() {
            final int dice1 = rand(1, 6);
            final int dice2 = rand(1, 6);
            dice1Point = dice1;
            dice2Point = dice2;
            final L1NpcInstance npc = getMasterNpc();
            if (npc != null) {
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10093 + (dice1 - 1)));
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10099 + (dice2 - 1)));
            }
            GeneralThreadPool.get().schedule(new Open3Timer(), 4 * 1000);
        }
    }
    private class Open3Timer extends TimerTask {
        @Override
        public void run() {
            final L1NpcInstance npc = getMasterNpc();
            if (npc != null) {
                final int number = (dice1Point + dice2Point) % 4;
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】骰出 [" + dice1Point + "、" + dice2Point + "] 共 " + (dice1Point + dice2Point) + " 點 【" + (number == 0 ? "尾" : number == 3 ? "川" : number == 2 ? "初" : "莊") + "】馬上開牌"));
                npc.broadcastPacketX10(new S_NpcChatShouting(npc, "-"));
            }
            final List<Integer> cardList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < 10; i++) {
                    cardList.add(i);
                }
            }
            Collections.shuffle(cardList); // 洗牌
            cards.add(new Card(cardList.remove(0), cardList.remove(0))); // 莊
            cards.add(new Card(cardList.remove(0), cardList.remove(0))); // 初
            cards.add(new Card(cardList.remove(0), cardList.remove(0))); // 川
            cards.add(new Card(cardList.remove(0), cardList.remove(0))); // 尾
            try {
                Thread.sleep(2000);
            } catch (Exception e) {}
            cardList.clear();
            if (npc != null) {
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10007 + cards.get(0).getCard1()));
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10017 + cards.get(0).getCard2()));

                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10047 + cards.get(1).getCard1()));
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10057 + cards.get(1).getCard2()));

                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10027 + cards.get(2).getCard1()));
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10037 + cards.get(2).getCard2()));


                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10067 + cards.get(3).getCard1()));
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 10077 + cards.get(3).getCard2()));
            }
            GeneralThreadPool.get().schedule(new Open4Timer(), 5 * 1000);
        }
    }
    private class Open4Timer extends TimerTask {
        @Override
        public void run() {
            lock.lock();
            try {
                final L1NpcInstance npc = getMasterNpc();
                if (npc != null) {
                    npc.broadcastPacketX10(new S_NpcChatShouting(npc, "【推筒子】莊【" + cards.get(0).getName() + "】初【" + cards.get(1).getName() + "】川【" + cards.get(2).getName() + "】尾【" + cards.get(3).getName() + "】"));
                    npc.broadcastPacketX10(new S_NpcChatShouting(npc, "-"));
                }
                for (final Map.Entry<String, BetType> bet : players.entrySet()) {
                    final L1PcInstance pc = World.get().getPlayer(bet.getKey());
                    if (pc != null) {

                    }
                }
                opened2 = false;
                players.clear();
                cards.clear();
                bankerOID = -1;
            } finally {
                lock.unlock();
            }
        }
    }
}
