package com.add;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CustomBaccarat implements Runnable {
    public static final CustomBaccarat instance = new CustomBaccarat();

    public static CustomBaccarat getInstance() {
        return instance;
    }
    private final int coin_id = 140308; // TODO 籌碼編號
    private final List<PokerCard> cards = new ArrayList<>();
    private final Map<Integer, PlayerBetData> data = new HashMap<>();
    private L1NpcInstance npc = null;
    private final int trailSize = 174;
    private final List<TrailType> trails = new ArrayList<>(trailSize);
    private final String[] moneyImages = new String[]{"722", "723", "724", "725", "726", "727", "728", "729", "730", "731", "732"};
    private ReentrantLock lock = new ReentrantLock(false);
    private boolean open_bet = false;
    private boolean playerWinner = false, bankerWinner = false;

    public enum TrailType {
        莊贏("745", 2),
        莊和局("746", 2),
        莊和局莊閒對("747", 2),
        莊和局莊對("748", 2),
        莊和局閒對("749", 2),
        莊贏莊閒對("750", 2),
        莊贏莊對("751", 2),
        莊贏閒對("752", 2),
        閒贏("753", 1),
        閒和局("754", 1),
        閒和局莊閒對("755", 1),
        閒和局莊對("756", 1),
        閒和局閒對("757", 1),
        閒贏莊閒對("758", 1),
        閒贏莊對("759", 1),
        閒贏閒對("760", 1),
        和局("762", 3),
        和局莊對("763", 3),
        和局閒對("764", 3),
        和局莊閒對("765", 3),
        空("761", -1),
        ;
        private final String image;
        private final int type;

        TrailType(final String image, final int type) {
            this.image = image;
            this.type = type;
        }

        public final String getImage() {
            return this.image;
        }

        public final int getType() {
            return this.type;
        }
    }

    public enum Chips {
        籌碼100小("733"),
        籌碼100大("734"),
        籌碼500小("735"),
        籌碼500大("736"),
        籌碼1000小("737"),
        籌碼1000大("738"),
        籌碼10000小("739"),
        籌碼10000大("740"),
        ;
        private final String image;

        Chips(final String image) {
            this.image = image;
        }

        public final String getImage() {
            return this.image;
        }
    }

    public void load() {
        final Collection<L1Object> objects = World.get().getAllVisibleObjects().values();
        for (final L1Object obj : objects) {
            if (obj instanceof L1NpcInstance) {
                if (((L1NpcInstance) obj).getNpcId() == 5555554) {
                    npc = (L1NpcInstance) obj;
                    GeneralThreadPool.get().execute(this);
                    break;
                }
            }
        }
    }
    public void setPlayerWinner() {
        this.playerWinner = true;
    }
    public void setBankerWinner() {
        this.bankerWinner = true;
    }
    @Override
    public void run() {
        this.playerWinner = false;
        this.bankerWinner = false;
        if (this.npc == null) {
            return;
        }
        if (this.cards.size() < 6) {
            shuffle();
        }
        this.lock.lock();
        try {
            this.open_bet = true;
            this.npc.broadcastPacketX10(new S_ServerMessage("【百家樂】現在開放下注 30 秒後停止押分。"));
        } finally {
            this.lock.unlock();
        }
        try {
            for (int i = 0; i < 30; i++) {
                Thread.sleep(1000);
                if (i >= 25) {
                    this.npc.broadcastPacketX10(new S_ServerMessage("【百家樂】" + (30 - i) + " 秒後發牌。"));
                }
            }
        } catch (Exception e) {}
        this.lock.lock();
        try {
            this.open_bet = false;
        } finally {
            this.lock.unlock();
        }
        final PokerCard[] player_card = new PokerCard[3];
        final PokerCard[] banker_card = new PokerCard[3];
        player_card[0] = this.cards.remove(0);
        player_card[1] = this.cards.remove(0);
        banker_card[0] = this.cards.remove(0);
        banker_card[1] = this.cards.remove(0);

        int playerPoint = (player_card[0].getPoint() + player_card[1].getPoint()) % 10;
        int bankerPoint = (banker_card[0].getPoint() + banker_card[1].getPoint()) % 10;
        if (playerPoint < 6 && bankerPoint != 8 && bankerPoint != 9) {
            player_card[2] = this.cards.remove(0);
            playerPoint += player_card[2].getPoint();
            playerPoint %= 10;
            if (bankerPoint == 0 || bankerPoint == 1 || bankerPoint == 2) {
                banker_card[2] = this.cards.remove(0);
            } else if (bankerPoint == 3 && player_card[2].getPoint() != 8) {
                banker_card[2] = this.cards.remove(0);
            } else if (bankerPoint == 4 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                banker_card[2] = this.cards.remove(0);
            } else if (bankerPoint == 5 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 2 && player_card[2].getPoint() != 3 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                banker_card[2] = this.cards.remove(0);
            } else if (bankerPoint == 6 && (player_card[2].getPoint() == 6 || player_card[2].getPoint() == 7)) {
                banker_card[2] = this.cards.remove(0);
            }
        }
        if (player_card[2] == null && playerPoint != 8 && playerPoint != 9) {
            if (bankerPoint < 6) {
                banker_card[2] = this.cards.remove(0);
            }
        }

        playerPoint = calcTotalPoint(player_card);
        bankerPoint = calcTotalPoint(banker_card);
        if (this.playerWinner || this.bankerWinner) {
            while (this.playerWinner && playerPoint < bankerPoint) {
                if (this.cards.size() < 6) {
                    shuffle();
                }
                player_card[0] = null;
                player_card[1] = null;
                player_card[2] = null;
                banker_card[0] = null;
                banker_card[1] = null;
                banker_card[2] = null;
                player_card[0] = this.cards.remove(0);
                player_card[1] = this.cards.remove(0);
                banker_card[0] = this.cards.remove(0);
                banker_card[1] = this.cards.remove(0);

                playerPoint = (player_card[0].getPoint() + player_card[1].getPoint()) % 10;
                bankerPoint = (banker_card[0].getPoint() + banker_card[1].getPoint()) % 10;
                if (playerPoint < 6 && bankerPoint != 8 && bankerPoint != 9) {
                    player_card[2] = this.cards.remove(0);
                    playerPoint += player_card[2].getPoint();
                    playerPoint %= 10;
                    if (bankerPoint == 0 || bankerPoint == 1 || bankerPoint == 2) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 3 && player_card[2].getPoint() != 8) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 4 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 5 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 2 && player_card[2].getPoint() != 3 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 6 && (player_card[2].getPoint() == 6 || player_card[2].getPoint() == 7)) {
                        banker_card[2] = this.cards.remove(0);
                    }
                }
                if (player_card[2] == null && playerPoint != 8 && playerPoint != 9) {
                    if (bankerPoint < 6) {
                        banker_card[2] = this.cards.remove(0);
                    }
                }

                playerPoint = calcTotalPoint(player_card);
                bankerPoint = calcTotalPoint(banker_card);
            }
            while (this.bankerWinner && bankerPoint < playerPoint) {
                if (this.cards.size() < 6) {
                    shuffle();
                }
                player_card[0] = null;
                player_card[1] = null;
                player_card[2] = null;
                banker_card[0] = null;
                banker_card[1] = null;
                banker_card[2] = null;
                player_card[0] = this.cards.remove(0);
                player_card[1] = this.cards.remove(0);
                banker_card[0] = this.cards.remove(0);
                banker_card[1] = this.cards.remove(0);

                playerPoint = (player_card[0].getPoint() + player_card[1].getPoint()) % 10;
                bankerPoint = (banker_card[0].getPoint() + banker_card[1].getPoint()) % 10;
                if (playerPoint < 6 && bankerPoint != 8 && bankerPoint != 9) {
                    player_card[2] = this.cards.remove(0);
                    playerPoint += player_card[2].getPoint();
                    playerPoint %= 10;
                    if (bankerPoint == 0 || bankerPoint == 1 || bankerPoint == 2) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 3 && player_card[2].getPoint() != 8) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 4 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 5 && player_card[2].getPoint() != 10 && player_card[2].getPoint() != 1 && player_card[2].getPoint() != 2 && player_card[2].getPoint() != 3 && player_card[2].getPoint() != 8 && player_card[2].getPoint() != 9) {
                        banker_card[2] = this.cards.remove(0);
                    } else if (bankerPoint == 6 && (player_card[2].getPoint() == 6 || player_card[2].getPoint() == 7)) {
                        banker_card[2] = this.cards.remove(0);
                    }
                }
                if (player_card[2] == null && playerPoint != 8 && playerPoint != 9) {
                    if (bankerPoint < 6) {
                        banker_card[2] = this.cards.remove(0);
                    }
                }

                playerPoint = calcTotalPoint(player_card);
                bankerPoint = calcTotalPoint(banker_card);
            }
        }


        this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22480 + ((player_card[0].getRealPoint() - 1) * 6) + (player_card[0].getType() * 13 * 6)));
        this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22482 + ((banker_card[0].getRealPoint() - 1) * 6) + (banker_card[0].getType() * 13 * 6)));
        this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22481 + ((player_card[1].getRealPoint() - 1) * 6) + (player_card[1].getType() * 13 * 6)));
        this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22483 + ((banker_card[1].getRealPoint() - 1) * 6) + (banker_card[1].getType() * 13 * 6)));
        if (player_card[2] != null) {
            this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22484 + ((player_card[2].getRealPoint() - 1) * 6) + (player_card[2].getType() * 13 * 6)));
        }
        if (banker_card[2] != null) {
            this.npc.broadcastPacketX10(new S_SkillSound(this.npc.getId(), 22485 + ((banker_card[2].getRealPoint() - 1) * 6) + (banker_card[2].getType() * 13 * 6)));
        }
        try {
            Thread.sleep(17 * 1000);
        } catch (Exception e) {
        }
        this.npc.broadcastPacketX10(new S_NpcChatShouting(this.npc, "閒家牌 : " + player_card[0].getName() + "、" + player_card[1].getName() + (player_card[2] != null ? "、" + player_card[2].getName() : "") + (player_card[0].getRealPoint() == player_card[1].getRealPoint() ? " (對子)" : "")));
        this.npc.broadcastPacketX10(new S_NpcChatShouting(this.npc, "莊家牌 : " + banker_card[0].getName() + "、" + banker_card[1].getName() + (banker_card[2] != null ? "、" + banker_card[2].getName() : "") + (banker_card[0].getRealPoint() == banker_card[1].getRealPoint() ? " (對子)" : "")));
        final StringBuilder sb = new StringBuilder();
        sb.append("閒家 ").append(playerPoint).append(" 點 | 莊家 ").append(bankerPoint).append(" 點");
        if (playerPoint > bankerPoint) {
            sb.append(" 閒家贏");
        } else if (bankerPoint > playerPoint) {
            sb.append(" 莊家贏");
        } else {
            sb.append("雙方和局");
        }
        for (final Map.Entry<Integer, PlayerBetData> bet : this.data.entrySet()) {
            final L1PcInstance pc = World.get().getPlayer(bet.getKey());
            if (playerPoint > bankerPoint) {
                if (bet.getValue().getBetPlayer() > 0) {
                    int money = bet.getValue().getBetPlayer() * 2;
                    bet.getValue().gainBonus(pc, money);
                }
            } else if (bankerPoint > playerPoint) {
                if (bet.getValue().getBetBanker() > 0) {
                    int money = (int) (bet.getValue().getBetBanker() * 1.95);
                    bet.getValue().gainBonus(pc, money);
                }
            } else {
                if (bet.getValue().getBetTie() > 0) {
                    bet.getValue().gainBonus(pc, bet.getValue().getBetTie() + (bet.getValue().getBetTie() * 8));
                    World.get().broadcastPacketToAll(new S_ServerMessage("【百家樂】" + bet.getValue().getPlayerName() + " 押注和局獲得 8倍 獎金。"));
                }
                if (bet.getValue().getBetPlayer() > 0) {
                    bet.getValue().gainBonus(pc, bet.getValue().getBetPlayer());
                }
                if (bet.getValue().getBetBanker() > 0) {
                    bet.getValue().gainBonus(pc, bet.getValue().getBetBanker());
                }
            }
            if (player_card[0].getRealPoint() == player_card[1].getRealPoint() && bet.getValue().getBetPlayerPair() > 0) {
                bet.getValue().gainBonus(pc, bet.getValue().getBetPlayerPair() + (bet.getValue().getBetPlayerPair() * 11));
                World.get().broadcastPacketToAll(new S_ServerMessage("【百家樂】" + bet.getValue().getPlayerName() + " 押注閒對子獲得 11倍 獎金。"));
            }
            if (banker_card[0].getRealPoint() == banker_card[1].getRealPoint() && bet.getValue().getBetBankerPair() > 0) {
                bet.getValue().gainBonus(pc, bet.getValue().getBetBankerPair() + (bet.getValue().getBetBankerPair() * 11));
                World.get().broadcastPacketToAll(new S_ServerMessage("【百家樂】" + bet.getValue().getPlayerName() + " 押注莊對子獲得 11倍 獎金。"));
            }
            bet.getValue().clear();
        }
        addTrail(playerPoint, bankerPoint, player_card, banker_card);
        this.npc.broadcastPacketX10(new S_NpcChatShouting(this.npc, sb.toString()));
        this.npc.broadcastPacketX10(new S_ServerMessage("================================="));
        try {
            Thread.sleep(3 * 1000);
        } catch (Exception e) {
        }
        GeneralThreadPool.get().execute(this);
    }

    public void addTrail(final int playerPoint, final int bankerPoint, final PokerCard[] player_card, final PokerCard[] banker_card) {
        if (this.trails.size() >= trailSize) {
            this.trails.remove(0);
        }
        if (playerPoint > bankerPoint) {
            if (player_card[0].getRealPoint() == player_card[1].getRealPoint() && banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.閒贏莊閒對);
            } else if (player_card[0].getRealPoint() == player_card[1].getRealPoint()) {
                this.trails.add(TrailType.閒贏閒對);
            } else if (banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.閒贏莊對);
            } else {
                this.trails.add(TrailType.閒贏);
            }
        } else if (bankerPoint > playerPoint) {
            if (player_card[0].getRealPoint() == player_card[1].getRealPoint() && banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.莊贏莊閒對);
            } else if (player_card[0].getRealPoint() == player_card[1].getRealPoint()) {
                this.trails.add(TrailType.莊贏閒對);
            } else if (banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.莊贏莊對);
            } else {
                this.trails.add(TrailType.莊贏);
            }
        } else {
            if (player_card[0].getRealPoint() == player_card[1].getRealPoint() && banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.和局莊閒對);
            } else if (player_card[0].getRealPoint() == player_card[1].getRealPoint()) {
                this.trails.add(TrailType.和局閒對);
            } else if (banker_card[0].getRealPoint() == banker_card[1].getRealPoint()) {
                this.trails.add(TrailType.和局莊對);
            } else {
                this.trails.add(TrailType.和局);
            }
        }
        for (final L1PcInstance pc : World.get().getVisiblePlayer(this.npc)) {
            bigTrailTalk(pc);
        }
    }
    public int calcBetMoney(final L1PcInstance pc, final int chipsType, final int maxBet, final int nowBet) {
        int chips = chipsType == 0 ? 100 : chipsType == 1 ? 500 : chipsType == 2 ? 1000 : chipsType == 3 ? 10000 : 0;
        if (pc.getInventory().countItems(coin_id) < 100) {
            return 0;
        }
        if (chips > maxBet) {
            if (maxBet - nowBet > 0) {
                return maxBet - nowBet;
            }
        }
        final int selfChips = (int) pc.getInventory().countItems(coin_id);
        if (selfChips < chips && (nowBet + selfChips) <= maxBet) {
            return selfChips;
        }
        if ((nowBet + chips) <= maxBet) {
            return chips;
        }
        if (maxBet > nowBet && chips > (maxBet - nowBet)) {
            return (maxBet - nowBet);
        }
        return 0;
    }
    public void removeBet(final L1PcInstance pc, boolean store) {
        final PlayerBetData betData = getBetData(pc.getId(), pc.getName());
        betData.setChipsType(0);
        final int money = betData.getBetPlayer() + betData.getBetBanker() + betData.getBetTie() + betData.getBetPlayerPair() + betData.getBetBankerPair();
        betData.clear();
        if (store) {
            pc.getInventory().storeItem(coin_id, money);
        }
    }
    public void npcAction(final L1PcInstance pc, final String cmd) {
        if (cmd.equalsIgnoreCase("bet_exit")) {
            try {
                int _locX = 33442, _locY = 32801, _mapid = 4;
                final L1Map map = L1WorldMap.get().getMap((short) _mapid);
                int r = 10;
                int tryCount = 0;
                int newX = _locX;
                int newY = _locY;
                do {
                    tryCount++;
                    newX = _locX + (int) (Math.random() * r) - (int) (Math.random() * r);
                    newY = _locY + (int) (Math.random() * r) - (int) (Math.random() * r);
                    if (map.isPassable(newX, newY, pc)) {
                        break;
                    }
                    Thread.sleep(1);
                } while (tryCount < 5);

                if (tryCount >= 5) {
                    L1Teleport.teleport(pc, _locX, _locY, (short) _mapid, pc.getHeading(), true);

                } else {
                    L1Teleport.teleport(pc, newX, newY, (short) _mapid, pc.getHeading(), true);
                }

            } catch (InterruptedException e) {
            }
            return;
        }
        this.lock.lock();
        try {
            if (!this.open_bet) {
                return;
            }
            final PlayerBetData betData = getBetData(pc.getId(), pc.getName());
            if (cmd.equalsIgnoreCase("bet_player")) {
                final int money = calcBetMoney(pc, betData.getChipsType(), 50000, betData.getBetPlayer());
                if (money > 0) {
                    pc.getInventory().consumeItem(coin_id, money);
                    betData.addBetPlayer(money);
                    for (L1Object visible : World.get().getAllPlayers()) {
                        if ((visible instanceof L1PcInstance)) {
                            L1PcInstance GM = (L1PcInstance) visible;
                            if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                GM.sendPackets(new S_SystemMessage("百家樂：" + pc.getName() + " 押注『閒家』" + money + " 元 共計 " + betData.getBetPlayer() + " 元。"));
                            }
                        }
                    }
                }
            }
            if (cmd.equalsIgnoreCase("bet_player_pair")) {
                final int money = calcBetMoney(pc, betData.getChipsType(), 3000, betData.getBetPlayerPair());
                if (money > 0) {
                    pc.getInventory().consumeItem(coin_id, money);
                    betData.addBetPlayerPair(money);
                }
            }
            if (cmd.equalsIgnoreCase("bet_Tie")) {
                final int money = calcBetMoney(pc, betData.getChipsType(), 3000, betData.getBetTie());
                if (money > 0) {
                    pc.getInventory().consumeItem(coin_id, money);
                    betData.addBetTie(money);
                }
            }
            if (cmd.equalsIgnoreCase("bet_banker_pair")) {
                final int money = calcBetMoney(pc, betData.getChipsType(), 3000, betData.getBetBankerPair());
                if (money > 0) {
                    pc.getInventory().consumeItem(coin_id, money);
                    betData.addBetBankerPair(money);
                }
            }
            if (cmd.equalsIgnoreCase("bet_banker")) {
                final int money = calcBetMoney(pc, betData.getChipsType(), 50000, betData.getBetBanker());
                if (money > 0) {
                    pc.getInventory().consumeItem(coin_id, money);
                    betData.addBetBanker(money);
                    for (L1Object visible : World.get().getAllPlayers()) {
                        if ((visible instanceof L1PcInstance)) {
                            L1PcInstance GM = (L1PcInstance) visible;
                            if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                                GM.sendPackets(new S_SystemMessage("百家樂：" + pc.getName() + " 押注『莊家』" + money + " 元 共計 " + betData.getBetBanker() + " 元。"));
                            }
                        }
                    }
                }
            }
            if (cmd.equalsIgnoreCase("bet_100")) {
                betData.setChipsType(0);
            }
            if (cmd.equalsIgnoreCase("bet_500")) {
                betData.setChipsType(1);
            }
            if (cmd.equalsIgnoreCase("bet_1000")) {
                betData.setChipsType(2);
            }
            if (cmd.equalsIgnoreCase("bet_10000")) {
                betData.setChipsType(3);
            }
            if (cmd.equalsIgnoreCase("bet_remove")) {
                removeBet(pc, true);
                for (L1Object visible : World.get().getAllPlayers()) {
                    if ((visible instanceof L1PcInstance)) {
                        L1PcInstance GM = (L1PcInstance) visible;
                        if ((GM.isGm()) && (pc.getId() != GM.getId())) {
                            GM.sendPackets(new S_SystemMessage("百家樂：" + pc.getName() + " 退押全部。"));
                        }
                    }
                }
            }
        } finally {
            this.lock.unlock();
        }
        bigTrailTalk(pc);
    }
    public void bigTrailTalk(final L1PcInstance pc) {
        final String[] trail = new String[209];
        int index = 0;
        TrailType lastType = null;
        for (int i = 0; i < this.trails.size(); i++) {
            if (lastType == null || lastType.getType() == -1 || lastType.getType() == this.trails.get(i).getType()) {
                trail[index] = this.trails.get(i).getImage();
                lastType = this.trails.get(i);
            } else if (lastType.getType() != this.trails.get(i).getType() && this.trails.get(i).getType() == 3) {
                String image = "";
                if (this.trails.get(i) == TrailType.和局) {
                    trail[index] = lastType.getType() == 1 ? TrailType.閒和局.getImage() : TrailType.莊和局.getImage();
                } else if (this.trails.get(i) == TrailType.和局閒對) {
                    trail[index] = lastType.getType() == 1 ? TrailType.閒和局閒對.getImage() : TrailType.莊和局閒對.getImage();
                } else if (this.trails.get(i) == TrailType.和局莊對) {
                    trail[index] = lastType.getType() == 1 ? TrailType.閒和局莊對.getImage() : TrailType.莊和局莊對.getImage();
                } else {
                    trail[index] = lastType.getType() == 1 ? TrailType.閒和局莊閒對.getImage() : TrailType.莊和局莊閒對.getImage();
                }
                lastType = lastType;
            } else {
                for (int j = index; j % 6 != 0; j++) {
                    trail[j] = TrailType.空.getImage();
                    index++;
                }
                if (index > 173) {
                    final TrailType type = this.trails.get(i);
                    this.trails.clear();
                    this.trails.add(type);
                    break;
                }
                trail[index] = this.trails.get(i).getImage();
                lastType = this.trails.get(i);
            }
            index++;
        }
        for (int i = index; i < trail.length; i++) {
            trail[i] = TrailType.空.getImage();
            if (i >= 173) {
                break;
            }
        }
        final PlayerBetData betData = getBetData(pc.getId(), pc.getName());
        // 閒押注
        setMoneyImage(trail, 174, betData.getBetPlayer(), false);
        // 閒對子押注
        setMoneyImage(trail, 179, betData.getBetPlayerPair(), false);
        // 和局押注
        setMoneyImage(trail, 184, betData.getBetTie(), false);
        // 莊對子押注
        setMoneyImage(trail, 189, betData.getBetBankerPair(), false);
        // 莊押注
        setMoneyImage(trail, 194, betData.getBetBanker(), false);
        // 錢包餘額
        setMoneyImage(trail, 199, (int) pc.getInventory().countItems(coin_id), true);
        // 籌碼
        final int chipsType = betData.getChipsType();
        if (chipsType == 0) {
            trail[205] = Chips.籌碼100大.getImage();
            trail[206] = Chips.籌碼500小.getImage();
            trail[207] = Chips.籌碼1000小.getImage();
            trail[208] = Chips.籌碼10000小.getImage();
        } else if (chipsType == 1) {
            trail[205] = Chips.籌碼100小.getImage();
            trail[206] = Chips.籌碼500大.getImage();
            trail[207] = Chips.籌碼1000小.getImage();
            trail[208] = Chips.籌碼10000小.getImage();
        } else if (chipsType == 2) {
            trail[205] = Chips.籌碼100小.getImage();
            trail[206] = Chips.籌碼500小.getImage();
            trail[207] = Chips.籌碼1000大.getImage();
            trail[208] = Chips.籌碼10000小.getImage();
        } else if (chipsType == 3) {
            trail[205] = Chips.籌碼100小.getImage();
            trail[206] = Chips.籌碼500小.getImage();
            trail[207] = Chips.籌碼1000小.getImage();
            trail[208] = Chips.籌碼10000大.getImage();
        }
        pc.sendPackets(new S_NPCTalkReturn(this.npc.getId(), "bigTrail", trail));
    }
    public final PlayerBetData getBetData(final int player_id, final String player_name) {
        if (!this.data.containsKey(player_id)) {
            this.data.put(player_id, new PlayerBetData(player_id, player_name));
        }
        return this.data.get(player_id);
    }

    public void setMoneyImage(final String[] images, final int index, final int money, boolean wallet) {
        if (wallet) {
            if (money > 99999) {
                images[index] = this.moneyImages[(int) ((money / Math.pow(10, 5)) % 10)];
            } else {
                images[index] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 9999) {
                images[index + 1] = this.moneyImages[(int) ((money / Math.pow(10, 4)) % 10)];
            } else {
                images[index + 1] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 999) {
                images[index + 2] = this.moneyImages[(int) ((money / Math.pow(10, 3)) % 10)];
            } else {
                images[index + 2] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 99) {
                images[index + 3] = this.moneyImages[(int) ((money / Math.pow(10, 2)) % 10)];
            } else {
                images[index + 3] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 9) {
                images[index + 4] = this.moneyImages[(int) ((money / Math.pow(10, 1)) % 10)];
            } else {
                images[index + 4] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 0) {
                images[index + 5] = this.moneyImages[(int) ((money / Math.pow(10, 0)) % 10)];
            } else {
                images[index + 5] = this.moneyImages[this.moneyImages.length - 1];
            }
        } else {
            if (money > 9999) {
                images[index] = this.moneyImages[(int) ((money / Math.pow(10, 4)) % 10)];
            } else {
                images[index] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 999) {
                images[index + 1] = this.moneyImages[(int) ((money / Math.pow(10, 3)) % 10)];
            } else {
                images[index + 1] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 99) {
                images[index + 2] = this.moneyImages[(int) ((money / Math.pow(10, 2)) % 10)];
            } else {
                images[index + 2] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 9) {
                images[index + 3] = this.moneyImages[(int) ((money / Math.pow(10, 1)) % 10)];
            } else {
                images[index + 3] = this.moneyImages[this.moneyImages.length - 1];
            }
            if (money > 0) {
                images[index + 4] = this.moneyImages[(int) ((money / Math.pow(10, 0)) % 10)];
            } else {
                images[index + 4] = this.moneyImages[this.moneyImages.length - 1];
            }
        }
    }

    public final int calcTotalPoint(final PokerCard[] cards) {
        int point = cards[0].getPoint() + cards[1].getPoint();
        if (cards[2] != null) {
            point += cards[2].getPoint();
        }
        return point % 10;
    }

    public void shuffle() {
        this.cards.clear();
        for (int i = 0; i < 8; i++) {
            this.cards.add(new PokerCard("方塊A", 1, 0));
            this.cards.add(new PokerCard("方塊2", 2, 0));
            this.cards.add(new PokerCard("方塊3", 3, 0));
            this.cards.add(new PokerCard("方塊4", 4, 0));
            this.cards.add(new PokerCard("方塊5", 5, 0));
            this.cards.add(new PokerCard("方塊6", 6, 0));
            this.cards.add(new PokerCard("方塊7", 7, 0));
            this.cards.add(new PokerCard("方塊8", 8, 0));
            this.cards.add(new PokerCard("方塊9", 9, 0));
            this.cards.add(new PokerCard("方塊10", 10, 0));
            this.cards.add(new PokerCard("方塊J", 10, 11, 0));
            this.cards.add(new PokerCard("方塊Q", 10, 12, 0));
            this.cards.add(new PokerCard("方塊K", 10, 13, 0));

            this.cards.add(new PokerCard("梅花A", 1, 1));
            this.cards.add(new PokerCard("梅花2", 2, 1));
            this.cards.add(new PokerCard("梅花3", 3, 1));
            this.cards.add(new PokerCard("梅花4", 4, 1));
            this.cards.add(new PokerCard("梅花5", 5, 1));
            this.cards.add(new PokerCard("梅花6", 6, 1));
            this.cards.add(new PokerCard("梅花7", 7, 1));
            this.cards.add(new PokerCard("梅花8", 8, 1));
            this.cards.add(new PokerCard("梅花9", 9, 1));
            this.cards.add(new PokerCard("梅花10", 10, 1));
            this.cards.add(new PokerCard("梅花J", 10, 11, 1));
            this.cards.add(new PokerCard("梅花Q", 10, 12, 1));
            this.cards.add(new PokerCard("梅花K", 10, 13, 1));

            this.cards.add(new PokerCard("紅心A", 1, 2));
            this.cards.add(new PokerCard("紅心2", 2, 2));
            this.cards.add(new PokerCard("紅心3", 3, 2));
            this.cards.add(new PokerCard("紅心4", 4, 2));
            this.cards.add(new PokerCard("紅心5", 5, 2));
            this.cards.add(new PokerCard("紅心6", 6, 2));
            this.cards.add(new PokerCard("紅心7", 7, 2));
            this.cards.add(new PokerCard("紅心8", 8, 2));
            this.cards.add(new PokerCard("紅心9", 9, 2));
            this.cards.add(new PokerCard("紅心10", 10, 2));
            this.cards.add(new PokerCard("紅心J", 10, 11, 2));
            this.cards.add(new PokerCard("紅心Q", 10, 12, 2));
            this.cards.add(new PokerCard("紅心K", 10, 13, 2));

            this.cards.add(new PokerCard("黑桃A", 1, 3));
            this.cards.add(new PokerCard("黑桃2", 2, 3));
            this.cards.add(new PokerCard("黑桃3", 3, 3));
            this.cards.add(new PokerCard("黑桃4", 4, 3));
            this.cards.add(new PokerCard("黑桃5", 5, 3));
            this.cards.add(new PokerCard("黑桃6", 6, 3));
            this.cards.add(new PokerCard("黑桃7", 7, 3));
            this.cards.add(new PokerCard("黑桃8", 8, 3));
            this.cards.add(new PokerCard("黑桃9", 9, 3));
            this.cards.add(new PokerCard("黑桃10", 10, 3));
            this.cards.add(new PokerCard("黑桃J", 10, 11, 3));
            this.cards.add(new PokerCard("黑桃Q", 10, 12, 3));
            this.cards.add(new PokerCard("黑桃K", 10, 13, 3));
        }
        Collections.shuffle(this.cards);
    }

    public static class PokerCard {
        private final String name;
        private final int point, real_point;
        private final int type;

        public PokerCard(final String name, final int point, final int type) {
            this.name = name;
            this.point = point;
            this.real_point = point;
            this.type = type;
        }

        public PokerCard(final String name, final int point, final int real_point, final int type) {
            this.name = name;
            this.point = point;
            this.real_point = real_point;
            this.type = type;
        }

        public final String getName() {
            return this.name;
        }

        public final int getType() {
            return this.type;
        }

        public final int getPoint() {
            return this.point;
        }

        public final int getRealPoint() {
            return this.real_point;
        }
    }

    public static class PlayerBetData {
        private final int player_id;
        private final String player_name;
        private int chipsType = 0;
        private int bet_banker = 0, bet_player = 0, bet_tie = 0, bet_banker_pair = 0, bet_player_pair = 0;

        public PlayerBetData(final int player_id, final String player_name) {
            this.player_id = player_id;
            this.player_name = player_name;
        }

        public final int getPlayerId() {
            return this.player_id;
        }

        public final String getPlayerName() {
            return this.player_name;
        }

        public void addBetBanker(final int add) {
            this.bet_banker += add;
        }

        public void addBetPlayer(final int add) {
            this.bet_player += add;
        }

        public void addBetTie(final int add) {
            this.bet_tie += add;
        }

        public void addBetBankerPair(final int add) {
            this.bet_banker_pair += add;
        }

        public void addBetPlayerPair(final int add) {
            this.bet_player_pair += add;
        }

        public final int getBetBanker() {
            return this.bet_banker;
        }

        public final int getBetPlayer() {
            return this.bet_player;
        }

        public final int getBetTie() {
            return this.bet_tie;
        }

        public final int getBetBankerPair() {
            return this.bet_banker_pair;
        }

        public final int getBetPlayerPair() {
            return this.bet_player_pair;
        }
        public void clear() {
            this.chipsType = 0;
            this.bet_player = 0;
            this.bet_banker = 0;
            this.bet_player_pair = 0;
            this.bet_banker_pair = 0;
            this.bet_tie = 0;
        }
        public void setChipsType(final int type) {
            this.chipsType = type;
        }
        public final int getChipsType() {
            return this.chipsType;
        }
        public void gainBonus(final L1PcInstance pc, final int money) {
            if (pc == null) {
                try (Connection con = DatabaseFactory.get().getConnection()) {
                    try(PreparedStatement ps = con.prepareStatement("SELECT * FROM `custom_baccarat_datas` WHERE `player_id` = ?")) {
                        ps.setInt(1, this.player_id);
                        try(ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                try(PreparedStatement pse = con.prepareStatement("UPDATE `custom_baccarat_datas` SET `money` = `money` + ? WHERE `player_id` = ?")) {
                                    pse.setInt(1, money);
                                    pse.setInt(2, player_id);
                                    pse.execute();
                                }
                            } else {
                                try(PreparedStatement pse = con.prepareStatement("INSERT INTO `custom_baccarat_datas` (`player_id`, `player_name`, `money`) VALUES (?, ?, ?)")) {
                                    pse.setInt(1, player_id);
                                    pse.setString(2, player_name);
                                    pse.setInt(3, money);
                                    pse.execute();
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println(e);
                }
            } else {
                pc.getInventory().storeItem(CustomBaccarat.instance.coin_id, money);
            }
        }
    }
}
