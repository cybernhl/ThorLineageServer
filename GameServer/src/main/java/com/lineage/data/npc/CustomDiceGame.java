package com.lineage.data.npc;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomDiceGame extends NpcExecutor {
    private static final CustomDiceGame instance = new CustomDiceGame();
    private static  final Random _random = new Random();
    private int banker_id = -1;
    private String banker_name = "無";
    private long banker_money = 0;
    private long banker_big_money = 0;
    private long banker_small_money = 0;
    private final int coin_id = 40308;
    private final Map<Integer, PlayerDiceBetData> data = new HashMap<>();
    private final Map<Integer, Long> dc_data = new HashMap<>();
    private final Lock lock = new ReentrantLock(false);
    private final Lock dc_lock = new ReentrantLock(false);
    private boolean canRobBanker = true;
    private boolean canBet = false;
    private boolean firstRob = true;
    private boolean biged = false;
    private boolean smalled = false;
    private boolean killed = false;
    public static NpcExecutor get() {
        return instance;
    }
    public static CustomDiceGame getInstance() {
        return instance;
    }
    public void setBig() {
        this.biged = true;
        this.smalled = false;
        this.killed = false;
    }
    public void setSmall() {
        this.biged = false;
        this.smalled = true;
        this.killed = false;
    }
    public void setKill() {
        this.biged = false;
        this.smalled = false;
        this.killed = true;
    }
    @Override
    public int type() {
        return 3;
    }
    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        if (Shutdown.SHUTDOWN) {
            pc.sendPackets(new S_ServerMessage("伺服器準備關閉，請重啟後再試"));
            return;
        }
        backDCData(pc);
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bet_dice", new String[] {
                banker_name,
                String.format("%,d", banker_money),
                canBet ? "開放下注" : canRobBanker ? "開放搶莊" : "等待開獎中",
                String.format("%,d", calcBigRemaBetCount()),
                String.format("%,d", calcSmallRemaBetCount()),
                String.format("%,d", this.data.containsKey(pc.getId()) ? this.data.get(pc.getId()).money : 0),
        }));
    }
    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc,
                       final String cmd, final long amount) {
        if (Shutdown.SHUTDOWN) {
            pc.sendPackets(new S_ServerMessage("伺服器準備關閉，請重啟後再試"));
            return;
        }
        if (cmd.equals("dice_type_0")) {
            pc.sendPackets(new S_ItemCount(npc.getId(), ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT, 2000000000, "bet_number", "dice_type_4"));
            return;
        }
        if (cmd.equals("dice_type_1")) {
            pc.sendPackets(new S_ItemCount(npc.getId(), ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT / 2, 2000000000, "bet_number", "dice_type_5"));
            return;
        }
        if (cmd.equals("dice_type_2")) {
            pc.sendPackets(new S_ItemCount(npc.getId(), ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT / 2, 2000000000, "bet_number", "dice_type_6"));
            return;
        }
        if (cmd.equals("dice_type_4")) {
            robBanker(pc, amount, npc);
            return;
        }
        if (cmd.equals("dice_type_5")) {
            bet(pc, 0, amount, npc);
            return;
        }
        if (cmd.equals("dice_type_6")) {
            bet(pc, 1, amount, npc);
            return;
        }
    }
    @Override
    public void set_set(String[] set) {
        System.out.println("set_set");
    }
    public void addDCData(final int pcId, long money) {
        dc_lock.lock();
        try {
            long add = 0;
            if (this.dc_data.containsKey(pcId)) {
                add = this.dc_data.get(pcId);
            }
            add += money;
            this.dc_data.put(pcId, money);
        } finally {
            dc_lock.unlock();
        }
    }
    public void backDCData(final L1PcInstance pc) {
        dc_lock.lock();
        try {
            if (this.dc_data.containsKey(pc.getId())) {
                final long money = this.dc_data.get(pc.getId());
                if (money > 0) {
                    pc.getInventory().storeItem(coin_id, money);
                    this.dc_data.put(pc.getId(), 0L);
                    pc.sendPackets(new S_ServerMessage("返還斷線資金 " + String.format("%,d", money) + " 元"));
                }
            }
        } finally {
            dc_lock.unlock();
        }
    }
    public void robBanker(final L1PcInstance pc, final long money, final L1NpcInstance npc) {
        this.lock.lock();
        try {
            if (pc == null) {
                return;
            }
            if (money <= 0) {
                return;
            }
            if (!canRobBanker) {
                return;
            }
            final long coin_count = pc.getInventory().countItems(coin_id);
            if (coin_count < money) {
                pc.sendPackets(new S_ServerMessage("您身上的金幣不足 " + String.format("%,d", money) + " 元"));
                return;
            }
            if (banker_id == pc.getId()) {
                pc.sendPackets(new S_ServerMessage("您已經是莊家了!"));
                return;
            }
            if (banker_id != -1) {
                if (money <= banker_money) {
                    pc.sendPackets(new S_ServerMessage("搶莊失敗，目前的莊家是【" + banker_name + "】必須大於 " + String.format("%,d", banker_money) + " 元"));
                    return;
                }
                final L1PcInstance banker = World.get().getPlayer(banker_id);
                if (banker == null) {
                    addDCData(banker_id, banker_money);
                } else {
                    banker.getInventory().storeItem(coin_id, banker_money);
                    banker.sendPackets(new S_ServerMessage("獲得金幣「" + String.format("%,d", banker_money) + " 元」"));
                }
            }
            pc.getInventory().consumeItem(coin_id, money);
            this.banker_id = pc.getId();
            this.banker_name = pc.getName();
            this.banker_money = money;
            sendMessage(npc, "【骰寶】玩家「" + pc.getName() + "」消耗 " + String.format("%,d", money) + " 元 搶莊成功");
            if (firstRob) {
                firstRob = false;
                sendMessage(npc, "【骰寶】30秒後停止搶莊");
                GeneralThreadPool.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        int rob_delay = 30;
                        while (rob_delay > 0) {
                            rob_delay--;
                            if (rob_delay <= 10) {
                                sendMessage(npc, "【骰寶】剩餘搶莊時間 " + rob_delay + " 秒。");
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {}
                        }
                        GeneralThreadPool.get().execute(new Runnable() {
                            @Override
                            public void run() {
                                int delay = 45;
                                lock.lock();
                                try {
                                    firstRob = true;
                                    canRobBanker = false;
                                    canBet = true;
                                    sendMessage(npc, "【骰寶】現在開放下注，" + delay + " 秒後關閉下注。");
                                } finally {
                                    lock.unlock();
                                }
                                while (delay > 0) {
                                    delay--;
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception e) {}
                                    if (delay <= 5) {
                                        sendMessage(npc, "【骰寶】" + delay + " 秒後關閉下注");
                                    }
                                }
                                lock.lock();
                                try {
                                    canBet = false;
                                } finally {
                                    lock.unlock();
                                }
                                delay = 5;
                                while (delay > 0) {
                                    delay--;
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception e) {}
                                    sendMessage(npc, "【骰寶】" + delay + " 秒後開骰");
                                }
                                int dice = rand(1, 6);
                                int dice2 = rand(1, 6);
                                int dice3 = rand(1, 6);
                                int total = dice + dice2 + dice3;
                                if (biged) {
                                    while (isSmall(total)) {
                                        dice = rand(1, 6);
                                        dice2 = rand(1, 6);
                                        dice3 = rand(1, 6);
                                        total = dice + dice2 + dice3;
                                    }
                                }
                                if (smalled) {
                                    while (!isSmall(total)) {
                                        dice = rand(1, 6);
                                        dice2 = rand(1, 6);
                                        dice3 = rand(1, 6);
                                        total = dice + dice2 + dice3;
                                    }
                                }
                                if (killed) {
                                    int rand = rand(1, 6);
                                    dice = rand;
                                    dice2 = rand;
                                    dice3 = rand;
                                    total = dice + dice2 + dice3;
                                }
                                sendPackets(npc, new S_SkillSound(npc.getId(), 3203 + dice));
                                try {Thread.sleep(4400);} catch (Exception e) {}
                                sendPackets(npc, new S_SkillSound(npc.getId(), 3203 + dice2));
                                try {Thread.sleep(4400);} catch (Exception e) {}
                                sendPackets(npc, new S_SkillSound(npc.getId(), 3203 + dice3));
                                try {Thread.sleep(4400);} catch (Exception e) {}
                                sendMessage(npc, "【骰寶】" + dice + "、" + dice2 + "、" + dice3 + " [ " + (total) + " 點 " + (isSmall(total) ? "小" : "大") + " ] " + (isTotalKilled(dice, dice2, dice3) ? "豹子通殺" : ""));
                                WinOrLose(dice, dice2, dice3);
                                data.clear();
                                banker_id = -1;
                                banker_name = "無";
                                banker_money = 0;
                                banker_big_money = 0;
                                banker_small_money = 0;
                                biged = false;
                                smalled = false;
                                killed = false;
                                canRobBanker = true;
                            }
                        });
                    }
                });
            }
        } finally {
            this.lock.unlock();
        }
    }
    public void WinOrLose(int dice, int dice2, int dice3) {
        boolean isSmall = isSmall(dice + dice2 + dice3);
        boolean isKilled = isTotalKilled(dice, dice2, dice3);
        long gainBanker = banker_money;
        for (final Map.Entry<Integer, PlayerDiceBetData> player : this.data.entrySet()) {
            if (isKilled) {

            } else {
                if (isSmall) {
                    if (player.getValue().type == 1) {
                        final L1PcInstance pc = World.get().getPlayer(player.getKey());
                        long money = player.getValue().money * 2;
                        if (pc == null) {
                            addDCData(player.getKey(), money);
                        } else {
                            pc.getInventory().storeItem(coin_id, money);
                            pc.sendPackets(new S_ServerMessage("獲得金幣「" + String.format("%,d", money) + " 元」"));
                            gainBanker -= player.getValue().money;
                        }
                    } else {
                        gainBanker += player.getValue().money;
                    }
                } else {
                    if (player.getValue().type == 0) {
                        final L1PcInstance pc = World.get().getPlayer(player.getKey());
                        long money = player.getValue().money * 2;
                        if (pc == null) {
                            addDCData(player.getKey(), money);
                        } else {
                            pc.getInventory().storeItem(coin_id, money);
                            pc.sendPackets(new S_ServerMessage("獲得金幣「" + String.format("%,d", money) + " 元」"));
                            gainBanker -= player.getValue().money;
                        }
                    } else {
                        gainBanker += player.getValue().money;
                    }
                }
            }
        }
        final L1PcInstance banker = World.get().getPlayer(banker_id);
        if (banker == null) {
            addDCData(banker_id, gainBanker);
        } else {
            banker.getInventory().storeItem(coin_id, gainBanker);
            banker.sendPackets(new S_ServerMessage("獲得金幣「" + String.format("%,d", gainBanker) + " 元」"));
        }
    }
    public final boolean isSmall(final int total) {
        return total <= 10;
    }
    public final boolean isTotalKilled(final int dice, final int dice2, final int dice3) {
        return dice == dice2 && dice == dice3;
    }
    public final void sendMessage(final L1NpcInstance npc, final String msg) {
        final List<L1PcInstance> players = World.get().getVisiblePlayer(npc, 10);
        for (final L1PcInstance pc : players) {
            pc.sendPackets(new S_ServerMessage(msg));
        }
        players.clear();
    }
    public final void sendPackets(final L1NpcInstance npc, final ServerBasePacket packet) {
        final List<L1PcInstance> players = World.get().getVisiblePlayer(npc, 10);
        for (final L1PcInstance pc : players) {
            pc.sendPackets(packet);
        }
        players.clear();
    }
    public void bet(final L1PcInstance pc, final int type, final long money, final L1NpcInstance npc) {
        this.lock.lock();
        try {
            if (pc == null) {
                return;
            }
            if (pc.getId() == banker_id) {
                pc.sendPackets(new S_ServerMessage("您目前是莊家，無法下注。"));
                return;
            }
            if (money <= 0) {
                return;
            }
            if (!canBet) {
                return;
            }
            if (this.data.containsKey(pc.getId()) && this.data.get(pc.getId()).type != type) {
                pc.sendPackets(new S_ServerMessage("您無法下注雙邊，請等待開骰"));
                return;
            }
            final long coin_count = pc.getInventory().countItems(coin_id);
            if (coin_count < money) {
                pc.sendPackets(new S_ServerMessage("下注失敗，您的金幣不足 " + String.format("%,d", money) + " 元。"));
                return;
            }
            final long calc = type == 0 ? calcBigRemaBetCount() : calcSmallRemaBetCount();
            if (money > calc) {
                pc.sendPackets(new S_ServerMessage("下注失敗，" + (type == 0 ? "大" : "小") + " 剩餘可押「" + calc + "」元。"));
                return;
            }
            if (type == 0) {
                this.banker_big_money += money;
            } else {
                this.banker_small_money += money;
            }
            pc.getInventory().consumeItem(coin_id, money);
            if (this.data.containsKey(pc.getId())) {
                this.data.get(pc.getId()).money += money;
            } else {
                this.data.put(pc.getId(), new PlayerDiceBetData(pc.getId(), type, money));
            }
            sendMessage(npc, "【骰寶】" + pc.getName() + " 下注骰寶 " + (type == 0 ? "大" : "小") + " " + String.format("%,d", money) + " 元。");
        } finally {
            this.lock.unlock();
        }
    }
    public final long calcBigRemaBetCount() {
        return (this.banker_money / 2) - this.banker_big_money;
    }
    public final long calcSmallRemaBetCount() {
        return (this.banker_money / 2) - this.banker_small_money;
    }
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
    public static class PlayerDiceBetData {
        private final int player_id;
        private int type;
        private long money;
        public PlayerDiceBetData(final int player_id, final int type, final long money) {
            this.player_id = player_id;
            this.type = type;
            this.money = money;
        }
    }
}