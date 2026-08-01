package com.add;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigOther;
import com.lineage.server.Shutdown;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CustomTaiwanMahjong implements Runnable {
    private static final Log _log = LogFactory.getLog(CustomTaiwanMahjong.class);
    private static CustomTaiwanMahjong _instance;
    private final Map<Integer, MahjongData> data = new HashMap<>();
    private final Map<Integer, List<BetData>> bet_data = new HashMap<>();
    private final Map<Integer, BetMoney> bet_money_data = new HashMap<>();
    private final int coin_id = ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_ID;
    private int banker_id = -1;
    private String banker_name = "";
    private int banker_money = 0;
    private int pool_money = 0;
    private boolean openBet = false;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final ReentrantLock banker_lock = new ReentrantLock(true);
    private final ReentrantLock bet_lock = new ReentrantLock(true);
    private L1NpcInstance npc;
    private boolean bet_banker = true;
    private boolean bet_banker_rob = false;
    private final List<MahjongCard> cards = new ArrayList<>();
    private int first_money = 0, mid_money = 0, end_money = 0;

    private boolean killWinSkill = false;
    private boolean first_win = false, first_lose = false;
    private boolean mid_win = false, mid_lose = false;
    private boolean end_win = false, end_lose = false;

    public final boolean isKillWinSkill() {
        return this.killWinSkill;
    }
    public final void setKillWinSkill() {
        this.killWinSkill = true;
    }

    private boolean killLoseSkill = false;

    public final boolean isKillLoseSkill() {
        return this.killLoseSkill;
    }
    public final void setKillLoseSkill() {
        this.killLoseSkill = true;
    }

    public static CustomTaiwanMahjong get() {
        if (_instance == null) {
            _instance = new CustomTaiwanMahjong();
        }
        return _instance;
    }
    public final L1NpcInstance getNpc() {
        return this.npc;
    }
    public void setNpc(final L1NpcInstance npc) {
        this.npc = npc;
    }
    public final void cheatTalk(final L1PcInstance pc) {
        final String firstMoney = "初位: " + String.format("%,d", first_money) + " 元";
        final String midMoney = "川位: " + String.format("%,d", mid_money) + " 元";
        final String endMoney = "尾位: " + String.format("%,d", end_money) + " 元";
        pc.sendPackets(new S_NPCTalkReturn(this.npc.getId(), "betcheat", new String[] {firstMoney, midMoney, endMoney, "【咬死他】", "【賠給他】", "【本局通殺】", "【本局通賠】", "【取消所有操作】"}));
    }
    public final void cheatAction(final L1PcInstance pc, final String cmd) {
        if (cmd.equalsIgnoreCase("cheat_6")) {
            killLoseSkill = false;
            setKillWinSkill();
            pc.sendPackets(new S_HelpMessage("此局通殺"));
        } else if (cmd.equalsIgnoreCase("cheat_7")) {
            killWinSkill = false;
            setKillLoseSkill();
            pc.sendPackets(new S_HelpMessage("此局通賠"));
        } else {
            killWinSkill = false;
            killLoseSkill = false;
            if (cmd.equalsIgnoreCase("cheat_8")) {
                first_win = false;
                first_lose = false;
                mid_win = false;
                mid_lose = false;
                end_win = false;
                end_lose = false;
            } else if (cmd.equalsIgnoreCase("cheat_0")) {
                first_lose = false;
                first_win = true;
                pc.sendPackets(new S_HelpMessage("此局咬初"));
            } else if (cmd.equalsIgnoreCase("cheat_1")) {
                first_win = false;
                first_lose = true;
                pc.sendPackets(new S_HelpMessage("此局賠初"));
            } else if (cmd.equalsIgnoreCase("cheat_2")) {
                mid_lose = false;
                mid_win = true;
                pc.sendPackets(new S_HelpMessage("此局咬川"));
            } else if (cmd.equalsIgnoreCase("cheat_3")) {
                mid_lose = true;
                mid_win = false;
                pc.sendPackets(new S_HelpMessage("此局賠川"));
            } else if (cmd.equalsIgnoreCase("cheat_4")) {
                end_win = true;
                end_lose = false;
                pc.sendPackets(new S_HelpMessage("此局咬尾"));
            } else if (cmd.equalsIgnoreCase("cheat_5")) {
                end_win = false;
                end_lose = true;
                pc.sendPackets(new S_HelpMessage("此局賠尾"));
            }
        }
        cheatTalk(pc);
    }
    public void npcAction(final L1PcInstance pc, final int objid, final String cmd) {
        if (Shutdown.SHUTDOWN) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (cmd.equalsIgnoreCase("bet_type_0")) {
            banker_lock.lock();
            try {
                if (!bet_banker) {
                    pc.sendPackets(new S_SystemMessage("搶莊失敗，目前的莊家是: " + banker_name));
                    return;
                }
                pc.sendPackets(new S_ItemCount(objid, ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT, 2000000000, "bet_number", "bet_type_0"));
            } finally {
                banker_lock.unlock();
            }
        } else if (cmd.equalsIgnoreCase("bet_type_4")) {
            lock.lock();
            try {
                if (!this.data.containsKey(pc.getId())) {
                    return;
                }
                final MahjongData mahjongData = this.data.get(pc.getId());
                final int money = mahjongData.getMoney();
                mahjongData.addMoney(-money);
                if (money != mahjongData.getMoney()) {
                    pc.getInventory().storeItem(coin_id, money);
                    pc.sendPackets(new S_ServerMessage("獲得 " + String.format("%,d", money) + " " + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME));
                }
                WriteLogTxt.NormalLog("推筒子紀錄", pc.getName() + " 領回了 " + money + " 元");
            } finally {
                lock.unlock();
            }
        } else {
            bet_lock.lock();
            try {
                if (!openBet) {
                    pc.sendPackets(new S_SystemMessage("目前還不能下注"));
                    return;
                }
                if (cmd.equalsIgnoreCase("bet_type_1")) {
                    pc.sendPackets(new S_ItemCount(objid, 10, 2000000000, "bet_number", "bet_type_1"));
                } else if (cmd.equalsIgnoreCase("bet_type_2")) {
                    pc.sendPackets(new S_ItemCount(objid, 10, 2000000000, "bet_number", "bet_type_2"));
                } else if (cmd.equalsIgnoreCase("bet_type_3")) {
                    pc.sendPackets(new S_ItemCount(objid, 10, 2000000000, "bet_number", "bet_type_3"));
                }
            } finally {
                bet_lock.unlock();
            }
        }
    }
    public void sendNpcTalk(final L1PcInstance pc, final int objid) {
        final int hour = LocalDateTime.now().getHour();
//        if (hour != 20 && hour != 22) {
//            pc.sendPackets(new S_SystemMessage("推筒子目前僅開放 20:00~21:00 跟 22:00~23:00。"));
//            return;
//        }
        if (Shutdown.SHUTDOWN) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (pc == null) {
            return;
        }
        if (objid <= 0) {
            return;
        }
        banker_lock.lock();
        try {
            if (bet_banker) {
                pc.sendPackets(new S_NPCTalkReturn(objid, "bet_banker", new String[] {ConfigOther.CUSTOM_TAIWAN_MAHJONG_TEXT, String.format("%,d", banker_money), String.format("%,d", pool_money), String.format("%,d", this.data.containsKey(pc.getId()) ? this.data.get(pc.getId()).getMoney() : 0)}));
                return;
            }
        } finally {
            banker_lock.unlock();
        }
        bet_lock.lock();
        try {
            if (openBet) {
                if (banker_id != pc.getId()) {
                    pc.sendPackets(new S_NPCTalkReturn(objid, "bet_player", new String[] {banker_name, ConfigOther.CUSTOM_TAIWAN_MAHJONG_TEXT, String.format("%,d", banker_money), String.format("%,d", pool_money), String.format("%,d", this.data.containsKey(pc.getId()) ? this.data.get(pc.getId()).getMoney() : 0)}));
                    return;
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(objid, "nobet_banker", new String[] {ConfigOther.CUSTOM_TAIWAN_MAHJONG_TEXT, String.format("%,d", banker_money), String.format("%,d", pool_money), String.format("%,d", this.data.containsKey(pc.getId()) ? this.data.get(pc.getId()).getMoney() : 0)}));
                    return;
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(objid, "nobet_player", new String[] {banker_name, ConfigOther.CUSTOM_TAIWAN_MAHJONG_TEXT, String.format("%,d", banker_money), String.format("%,d", pool_money), String.format("%,d", this.data.containsKey(pc.getId()) ? this.data.get(pc.getId()).getMoney() : 0)}));
                return;
            }
        } finally {
            bet_lock.unlock();
        }
    }
    public void setBet(final L1PcInstance pc, final int objid, final int amount, final String cmd) {
        if (Shutdown.SHUTDOWN) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (cmd.equalsIgnoreCase("bet_type_0")) {
            robBanker(pc, amount);
        } else {
            if (cmd.equalsIgnoreCase("bet_type_1") || cmd.equalsIgnoreCase("bet_type_2") || cmd.equalsIgnoreCase("bet_type_3")) {
                playerBet(pc, amount, cmd);
            }
        }
    }
    public void playerBet(final L1PcInstance pc, final int money, final String cmd) {
        bet_lock.lock();
        try {
            if (!openBet) {
                pc.sendPackets(new S_SystemMessage("目前未開放下注"));
                return;
            }
            if (money > pool_money) {
                pc.sendPackets(new S_SystemMessage("目前最大下注金額: " + String.format("%,d", pool_money)));
                return;
            }
            if (money < 10) {
                pc.sendPackets(new S_SystemMessage("最低下注金額是: " + String.format("%,d", 10)));
                return;
            }
            if (!pc.getInventory().checkItem(coin_id, money)) {
                pc.sendPackets(new S_SystemMessage("您的" + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME + "不足 " + String.format("%,d", money) + " 元。"));
                return;
            }
            int type = 0;
            String name;
            switch (cmd) {
                case "bet_type_1":
                    type = 1;
                    name = "初";
                    first_money += money;
                    break;
                case "bet_type_2":
                    type = 2;
                    name = "川";
                    mid_money += money;
                    break;
                case "bet_type_3":
                    type = 3;
                    name = "尾";
                    end_money += money;
                    break;
                default:
                    return;
            }
            pc.getInventory().consumeItem(coin_id, money);
            pool_money -= money;
            if (!this.bet_data.containsKey(type)) {
                this.bet_data.put(type, new ArrayList<>());
            }
            this.bet_data.get(type).add(new BetData(pc.getId(), type, money));

            getBetMoneyInstance(pc.getId()).addMoney(money);
            getBetMoneyInstance(pc.getId()).setName(pc.getName());
            pc.sendPacketsX10(new S_Chat(pc, "押 " + String.format("%,d", money) + " " + name));
            WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + pc.getId() + " 下注 " + money + " 元");
        } finally {
            bet_lock.unlock();
        }
    }
    public final BetMoney getBetMoneyInstance(final int id) {
        if (!this.bet_money_data.containsKey(id)) {
            this.bet_money_data.put(id, new BetMoney());
        }
        return this.bet_money_data.get(id);
    }
    @Override
    public void run() {
        bet_lock.lock();
        try {
            this.openBet = true;
        } finally {
            bet_lock.unlock();
        }
        sendMessage(npc, "【推筒子】現在開放下注，60秒後開牌。");
        if (cards.size() < 8) {
            for (int i = 0; i < 4; i++) {
                cards.add(new MahjongCard("白板", 0));
                cards.add(new MahjongCard("一筒", 1));
                cards.add(new MahjongCard("二筒", 2));
                cards.add(new MahjongCard("三筒", 3));
                cards.add(new MahjongCard("四筒", 4));
                cards.add(new MahjongCard("五筒", 5));
                cards.add(new MahjongCard("六筒", 6));
                cards.add(new MahjongCard("七筒", 7));
                cards.add(new MahjongCard("八筒", 8));
                cards.add(new MahjongCard("九筒", 9));
            }
            Collections.shuffle(cards);
        }
        try {
            Thread.sleep(50 * 1000);
        } catch (Exception e) {}
        int sleep = 10;
        while (sleep > 0) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            } finally {
                sleep--;
                sendMessage(npc, "【推筒子】剩餘下注時間【" + sleep + " 秒】。");
            }
        }
        bet_lock.lock();
        try {
            this.openBet = false;
        } finally {
            bet_lock.unlock();
        }
        sendMessage(npc, "【推筒子】關閉下注，現在開始搖骰。");
        int dice = rand(0, 5);
        int dice1 = rand(0, 5);
        int dice2 = rand(0, 5);
        sendPackets(npc, new S_SkillSound(npc.getId(), dice + 22460));
        sendPackets(npc, new S_SkillSound(npc.getId(), dice1 + 22466));
        sendPackets(npc, new S_SkillSound(npc.getId(), dice2 + 22472));
        dice++;
        dice1++;
        dice2++;
        int total = dice + dice1 + dice2;
        int calc = total;
        calc %= 4;
        calc--;
        if (calc < 0) {
            calc = 3;
        }
        try {
            Thread.sleep(5250);
        } catch (Exception e) {}
        sendMessage(npc, "【推筒子】" + String.format("[%d、%d、%d] %d 點 【%s】", dice, dice1, dice2, total, (calc == 0 ? "莊" : calc == 1 ? "初" : calc == 2 ? "川" : "尾")));
        final MahjongCard[] banker_card = new MahjongCard[2];
        final MahjongCard[] first_card = new MahjongCard[2];
        final MahjongCard[] mid_card = new MahjongCard[2];
        final MahjongCard[] end_card = new MahjongCard[2];
        for (int i = 0; i < 2; i++) {
            switch (calc) {
                case 0:
                    banker_card[i] = cards.remove(0);
                    first_card[i] = cards.remove(0);
                    mid_card[i] = cards.remove(0);
                    end_card[i] = cards.remove(0);
                    break;
                case 1:
                    first_card[i] = cards.remove(0);
                    mid_card[i] = cards.remove(0);
                    end_card[i] = cards.remove(0);
                    banker_card[i] = cards.remove(0);
                    break;
                case 2:
                    mid_card[i] = cards.remove(0);
                    end_card[i] = cards.remove(0);
                    banker_card[i] = cards.remove(0);
                    first_card[i] = cards.remove(0);
                    break;
                default:
                    end_card[i] = cards.remove(0);
                    banker_card[i] = cards.remove(0);
                    first_card[i] = cards.remove(0);
                    mid_card[i] = cards.remove(0);
                    break;
            }
        }
        if (this.killWinSkill) {
            exchangeWin(banker_card, first_card);
            exchangeWin(banker_card, mid_card);
            exchangeWin(banker_card, end_card);
        } else if (this.killLoseSkill) {
            exchangeLose(banker_card, first_card);
            exchangeLose(banker_card, mid_card);
            exchangeLose(banker_card, end_card);
        }
        if (this.first_win) {
            exchangeWin(banker_card, first_card);
        }
        if (this.first_lose) {
            exchangeLose(banker_card, first_card);
        }
        if (this.mid_win) {
            exchangeWin(banker_card, mid_card);
        }
        if (this.mid_lose) {
            exchangeLose(banker_card, mid_card);
        }
        if (this.end_win) {
            exchangeWin(banker_card, end_card);
        }
        if (this.end_lose) {
            exchangeLose(banker_card, end_card);
        }
        this.killWinSkill = false;
        this.killLoseSkill = false;
        this.first_win = false;
        this.first_lose = false;
        this.mid_win = false;
        this.mid_lose = false;
        this.end_win = false;
        this.end_lose = false;
        calc *= 80;
        sendPackets(npc, new S_SkillSound(npc.getId(), 22140 + calc + banker_card[0].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22150 + calc + banker_card[1].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22160 + calc + mid_card[0].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22170 + calc + mid_card[1].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22180 + calc + first_card[0].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22190 + calc + first_card[1].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22200 + calc + end_card[0].getPoint()));
        sendPackets(npc, new S_SkillSound(npc.getId(), 22210 + calc + end_card[1].getPoint()));
        try {
            Thread.sleep(7125);
        } catch (Exception e) {}
        sendMessage(npc, "【推筒子】莊家 : " + banker_card[0].getName() + "、" + banker_card[1].getName() + " " + getCardName(banker_card));
        int first_win = win(banker_card, first_card);
        sendMessage(npc, "【推筒子】初 : " + first_card[0].getName() + "、" + first_card[1].getName() + " " + getCardName(first_card) + " " + (first_win == 1 ? "贏" : first_win == 2 ? "和" : "輸"));
        int mid_win = win(banker_card, mid_card);
        sendMessage(npc, "【推筒子】川 : " + mid_card[0].getName() + "、" + mid_card[1].getName() + " " + getCardName(mid_card) + " " + (mid_win == 1 ? "贏" : mid_win == 2 ? "和" : "輸"));
        int end_win = win(banker_card, end_card);
        sendMessage(npc, "【推筒子】尾 : " + end_card[0].getName() + "、" + end_card[1].getName() + " " + getCardName(end_card) + " " + (end_win == 1 ? "贏" : end_win == 2 ? "和" : "輸"));
        long gainBankerMoney = banker_money;
        for (final Map.Entry<Integer, List<BetData>> bet : this.bet_data.entrySet()) {
            for (final BetData betData : bet.getValue()) {
                final L1PcInstance pc = World.get().getPlayer(betData.getPlayerId());
                int winner = betData.getType() == 1 ? first_win : betData.getType() == 2 ? mid_win : end_win;
                int gainPlayerMoney = betData.getMoney() + (int) ((double) betData.getMoney() * 0.94);
                if (winner == 1) {
                    if (pc == null) {
                        addMahjongData(betData.getPlayerId(), gainPlayerMoney);
                        this.getBetMoneyInstance(betData.getPlayerId()).addEndMoney(gainPlayerMoney);
                        WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + betData.getPlayerId() + " 取得了 " + gainPlayerMoney + " 元");
                    } else {
                        pc.getInventory().storeItem(coin_id, gainPlayerMoney);
                        this.getBetMoneyInstance(pc.getId()).addEndMoney(gainPlayerMoney);
                        pc.sendPackets(new S_ServerMessage("獲得 " + String.format("%,d", gainPlayerMoney) + " " + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME));
                    }
                    gainBankerMoney -= betData.getMoney();
                } else if (winner == 2) {
                    if (pc == null) {
                        addMahjongData(betData.getPlayerId(), betData.getMoney());
                        this.getBetMoneyInstance(betData.getPlayerId()).addEndMoney(gainPlayerMoney);
                        WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + betData.getPlayerId() + " 取得了 " + gainPlayerMoney + " 元");
                    } else {
                        pc.getInventory().storeItem(coin_id, betData.getMoney());
                        this.getBetMoneyInstance(pc.getId()).addEndMoney(gainPlayerMoney);
                        pc.sendPackets(new S_ServerMessage("獲得 " + String.format("%,d", betData.getMoney()) + " " + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME));
                    }
                } else {
                    gainBankerMoney += (int) ((double) betData.getMoney() * 0.94);
                }
            }
        }
        if (gainBankerMoney > 0) {
            if (gainBankerMoney > Integer.MAX_VALUE) {
                gainBankerMoney = Integer.MAX_VALUE;
            }
            final L1PcInstance banker = World.get().getPlayer(banker_id);
            if (banker == null) {
                addMahjongData(banker_id, (int) gainBankerMoney);
                WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + banker_id + " 取得了 " + gainBankerMoney + " 元");
                this.getBetMoneyInstance(banker_id).addEndMoney(gainBankerMoney);
            } else {
                banker.getInventory().storeItem(coin_id, gainBankerMoney);
                this.getBetMoneyInstance(banker.getId()).addEndMoney(gainBankerMoney);
                banker.sendPackets(new S_ServerMessage("獲得 " + String.format("%,d", gainBankerMoney) + " " + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME));
            }
        }
        try (Connection con = DatabaseFactory.get().getConnection()) {
            for (final Map.Entry<Integer, BetMoney> betMoneyEntry : this.bet_money_data.entrySet()) {
                boolean check = false;
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `日誌_推筒子輸贏` WHERE `player_id` = ?")) {
                    ps.setInt(1, betMoneyEntry.getKey());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            check = true;
                        }
                    }
                }
                if (check) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE `日誌_推筒子輸贏` SET `money` = `money` + ? WHERE `player_id` = ?")) {
                        ps.setLong(1, betMoneyEntry.getValue().calcMoney());
                        ps.setInt(2, betMoneyEntry.getKey());
                        ps.execute();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO `日誌_推筒子輸贏` (`player_id`, `money`, `name`) VALUES (?, ?, ?)")) {
                        ps.setInt(1, betMoneyEntry.getKey());
                        ps.setLong(2, betMoneyEntry.getValue().calcMoney());
                        ps.setString(3, betMoneyEntry.getValue().getName());

                        ps.execute();
                    }
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        banker_id = -1;
        banker_name = "";
        banker_money = 0;
        pool_money = 0;
        bet_lock.lock();
        try {
            for (final Map.Entry<Integer, List<BetData>> bet : this.bet_data.entrySet()) {
                bet.getValue().clear();
            }
            this.bet_money_data.clear();
            this.bet_data.clear();
            first_money = 0;
            mid_money = 0;
            end_money = 0;
        } finally {
            bet_lock.unlock();
        }
        banker_lock.lock();
        try {
            bet_banker = true;
        } finally {
            banker_lock.unlock();
        }
        sendMessage(npc, "【推筒子】牌局結束，請搶莊");
    }

    public void exchangeWin(final MahjongCard[] banker, final MahjongCard[] player) {
        if (win(banker, player) == 1) {
            final MahjongCard[] tmpCard = new MahjongCard[2];
            tmpCard[0] = banker[0];
            tmpCard[1] = banker[1];
            banker[0] = player[0];
            banker[1] = player[1];
            player[0] = tmpCard[0];
            player[1] = tmpCard[1];
        }
    }

    public void exchangeLose(final MahjongCard[] banker, final MahjongCard[] player) {
        if (win(banker, player) == 0) {
            final MahjongCard[] tmpCard = new MahjongCard[2];
            tmpCard[0] = banker[0];
            tmpCard[1] = banker[1];
            banker[0] = player[0];
            banker[1] = player[1];
            player[0] = tmpCard[0];
            player[1] = tmpCard[1];
        }
    }
    public final double getFinalPoint(final MahjongCard[] point) {
        if (point[0].getPoint() == point[1].getPoint()) {
            return point[0].getPoint() == 0 ? 100 : point[0].getPoint() * 10;
        } else {
            double ret = 0;
            ret = point[0].getPoint() + point[1].getPoint();
            if (point[0].getPoint() == 0 || point[1].getPoint() == 0) {
                ret += 0.5;
            } else {
                ret %= 10;
            }
            return ret;
        }
    }
    public final int win(final MahjongCard[] banker, final MahjongCard[] player) {
        double banker_point = getFinalPoint(banker);
        double player_point = getFinalPoint(player);

        if (player_point > banker_point) {
            return 1;
        }

        if (banker_point > player_point) {
            return 0;
        }
        final int player_big = Math.max(player[0].getPoint(), player[1].getPoint());
        final int banker_big = Math.max(banker[0].getPoint(), banker[1].getPoint());
        if (player_big > banker_big) {
            return 1;
        }
        if (banker_big > player_big) {
            return 0;
        }
        return 2;
    }
    public final String getCardName(final MahjongCard[] card) {
        if (card[0].getPoint() == card[1].getPoint()) {
            return "【" + card[0].getName() + "對子】";
        }
        String ret = "【" + ((card[0].getPoint() + card[1].getPoint()) % 10) + "點";
        if (card[0].getPoint() == 0 || card[1].getPoint() == 0) {
            ret += "半";
        }
        ret += "】";
        return ret;
    }
    public final void sendPackets(final L1NpcInstance npc, final ServerBasePacket packet) {
        final List<L1PcInstance> players = World.get().getVisiblePlayer(npc, 99);
        for (final L1PcInstance pc : players) {
            pc.sendPackets(packet);
        }
        players.clear();
    }
    public final void sendMessage(final L1NpcInstance npc, final String msg) {
        final List<L1PcInstance> players = World.get().getVisiblePlayer(npc, 99);
        for (final L1PcInstance pc : players) {
            pc.sendPackets(new S_ServerMessage(msg));
        }
        players.clear();
    }
    private final Random _random = new Random();
    public int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
    public void robBanker(final L1PcInstance pc, final int money) {
        banker_lock.lock();
        try {
            if (!bet_banker) {
                pc.sendPackets(new S_SystemMessage("目前無法搶莊"));
                return;
            }
            if (money < ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT) {
                pc.sendPackets(new S_SystemMessage("最低下注金額是: " + String.format("%,d", ConfigOther.CUSTOM_TAIWAN_MAHJONG_MIN_AMOUNT)));
                return;
            }
            if (money <= banker_money) {
                pc.sendPackets(new S_SystemMessage("您的下注金額必須比 " + banker_name + " 多 (" + String.format("%,d", banker_money) + ")"));
                return;
            }
            if (pc == null) {
                return;
            }
            if (money <= 0) {
                pc.sendPackets(new S_SystemMessage("發生錯誤，請聯繫管理員 error code : 1。"));
                return;
            }
            if (!pc.getInventory().checkItem(coin_id, money)) {
                pc.sendPackets(new S_SystemMessage("您的" + ConfigOther.CUSTOM_TAIWAN_MAHJONG_ITEM_NAME + "不足 " + String.format("%,d", money) + " 元。"));
                return;
            }
            try {
                if (banker_id != -1) {
                    final L1PcInstance target = World.get().getPlayer(banker_id);
                    if (target == null) {
                        addMahjongData(banker_id, banker_money);
                        WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + banker_id + " 取得了 " + banker_money + " 元");
                    } else {
                        target.getInventory().storeItem(coin_id, banker_money);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            pc.getInventory().consumeItem(coin_id, money);
            banker_id = pc.getId();
            banker_name = pc.getName();
            banker_money = money;
            pool_money = money;
            sendMessage(npc, "【推筒子】" + banker_name + " 消耗 " + String.format("%,d", banker_money) + " 搶莊成功");
            WriteLogTxt.NormalLog("推筒子紀錄", "【玩家編號】" + pc.getId() + " 搶莊 " + money + " 元");
            if (!bet_banker_rob) {
                bet_banker_rob = true;
                GeneralThreadPool.get().execute(new RobBankerRunnable());
            }
        } finally {
            banker_lock.unlock();
        }
    }

    public static class RobBankerRunnable implements Runnable {
        @Override
        public void run() {
            int sleep = 15;
            while (sleep > 0) {
                try {
                    Thread.sleep(1000);
                    sleep--;
                    CustomTaiwanMahjong.get().sendMessage(CustomTaiwanMahjong.get().npc, "【推筒子】剩餘搶莊時間【" + sleep + " 秒】。");
                } catch (Exception e) {}
            }
            CustomTaiwanMahjong.get().banker_lock.lock();
            try {
                CustomTaiwanMahjong.get().bet_banker_rob = false;
                CustomTaiwanMahjong.get().bet_banker = false;
                CustomTaiwanMahjong.get().getBetMoneyInstance(CustomTaiwanMahjong.get().banker_id).addMoney(CustomTaiwanMahjong.get().banker_money);
                CustomTaiwanMahjong.get().getBetMoneyInstance(CustomTaiwanMahjong.get().banker_id).setName(CustomTaiwanMahjong.get().banker_name);
                GeneralThreadPool.get().execute(CustomTaiwanMahjong.get());
            } finally {
                CustomTaiwanMahjong.get().banker_lock.unlock();
            }
        }
    }

    public void load() {
        if (this.npc == null) {
            for (final L1Object object : World.get().getObject()) {
                if (object instanceof L1NpcInstance) {
                    if (((L1NpcInstance) object).getNpcId() == 30678888) {
                        this.npc = (L1NpcInstance) object;
                        break;
                    }
                }
            }
        }
        final PerformanceTimer timer = new PerformanceTimer();
        try (Connection con = DatabaseFactory.get().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM `custom_taiwan_mahjong_data`")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int id = rs.getInt("id");
                        final int player_id = rs.getInt("player_id");
                        final int money = rs.getInt("money");
                        this.data.put(player_id, new MahjongData(id, player_id, money));
                    }
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        _log.info("讀取->推筒子遊戲 : " +  data.size() + "(" + timer.get() + "ms)");
    }
    public final boolean addMahjongData(final int pc_id, final int money) {
        this.lock.lock();
        try {
            if (!this.data.containsKey(pc_id)) {
                try (Connection con = DatabaseFactory.get().getConnection()) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO `custom_taiwan_mahjong_data` (`player_id`, `money`) VALUES (?, ?)", 1)) {
                        ps.setInt(1, pc_id);
                        ps.setInt(2, money);
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                this.data.put(pc_id, new MahjongData(rs.getInt(1), pc_id, money));
                                return true;
                            } else {
                                _log.info("錯誤->推筒子遊戲添加數據出錯 玩家編號:" + pc_id + " 金額:" + money);
                            }
                        }
                    }
                } catch (SQLException e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            } else {
                this.data.get(pc_id).addMoney(money);
                return true;
            }
        } finally {
            this.lock.unlock();
        }
        return false;
    }
    public static class MahjongData {
        private final int id, player_id;
        private int money = 0;

        private final ReentrantLock lock = new ReentrantLock(true);
        public MahjongData(final int id, final int player_id, final int money) {
            this.id = id;
            this.player_id = player_id;
            this.money = money;
        }

        public final int getId() {
            return this.id;
        }

        public final int geTPlayerId() {
            return this.player_id;
        }

        public final int getMoney() {
            return this.money;
        }

        public final void addMoney(final int money) {
            long total = (long) this.money + (long) money;
            if (total <= 0) {
                total = 0;
            }
            total = Math.min(total, 2147483647L);
            try (Connection con = DatabaseFactory.get().getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("UPDATE `custom_taiwan_mahjong_data` SET `money` = ? WHERE `id` = ?")) {
                    ps.setInt(1, (int) total);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    this.money = (int) total;
                }
            } catch (SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
    public static class MahjongCard {
        private final String name;
        private final int point;

        public MahjongCard(final String name, final int point) {
            this.name = name;
            this.point = point;
        }

        public final String getName() {
            return this.name;
        }

        public final int getPoint() {
            return this.point;
        }
    }
    public static class BetData {
        private final int player_id;
        private final int type;
        private final int money;

        public BetData(final int player_id, final int type, final int money) {
            this.player_id = player_id;
            this.type = type;
            this.money = money;
        }

        public final int getPlayerId() {
            return this.player_id;
        }

        public final int getType() {
            return this.type;
        }

        public final int getMoney() {
            return this.money;
        }
    }
    public static class BetMoney {
        private long money = 0;
        private long endMoney = 0;
        private String name;

        public void setName(final String name) {
            this.name = name;
        }
        public final String getName() {
            return this.name;
        }
        public void addMoney(final long add) {
            this.money += add;
        }
        public void addEndMoney(final long add) {
            this.endMoney += add;
        }
        public final long getMoney() {
            return this.money;
        }
        public long getEndMoney() {
            return this.endMoney;
        }
        public long calcMoney() {
            return this.endMoney - this.money;
        }
    }
}
