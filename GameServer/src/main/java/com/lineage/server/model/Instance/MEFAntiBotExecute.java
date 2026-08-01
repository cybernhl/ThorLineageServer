package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.UpdateLocReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.URandom;
import com.lineage.server.world.World;

public class MEFAntiBotExecute implements Runnable {

    private static final Log _log = LogFactory.getLog(MEFAntiBotExecute.class);

    private static final int language = Config.CLIENT_LANGUAGE;

    private L1PcInstance _pc;

    private L1NpcInstance _npc;

    private boolean _isRun = true;

    private int _mode = 0;

    private String _modeTxt = "";

    private int _pcid = 0;

    private String _chat1 = " 您好!!";

    private String _chat2 = "請您限制時間(60s)之內說出以下文字: ";

    private String _chat3 = "正確回答可以獲得200萬金幣獎勵和增加攻擊的狀態30分鐘!";

    private String _chat4 = "謝謝!";

    private int _itemId;// 防掛每小時問答正確獎勵

    private int _itemCount;// 防掛每小時問答正確獎勵數量

    public MEFAntiBotExecute(final L1PcInstance pc, final int itemId,
                             final int itemCount) {
        this._pc = pc;
        this._itemId = itemId;
        this._itemCount = itemCount;
        this._pcid = pc.getId();
        pc.set_showGm(this);
        if (language == 5) {
            this._chat2 = "請在1分鐘內說出驗證碼→:";
            this._chat3 = "正確回答可以獲得200萬金幣獎勵和增加攻擊的狀態30分鐘!!";
            this._chat4 = "感謝您使用外掛檢測系統,系統獎勵您1000金幣作為獎勵!";
        }
    }

    // @Override
    public void run() {
        this._npc = L1SpawnUtil.spawnGM(this._pc);
        this._mode = URandom.nextInt(900) + 100;

        String modeTxt = String.valueOf(this._mode);
        for (int i = 0; i < modeTxt.length(); i++) {
            char aa = modeTxt.charAt(i); // 取字符串下標索引是i的數 i循環的次數根據字符串的長度.
            if (aa == '1')
                this._modeTxt += "1";
            if (aa == '2')
                this._modeTxt += "2";
            if (aa == '3')
                this._modeTxt += "3";
            if (aa == '4')
                this._modeTxt += "4";
            if (aa == '5')
                this._modeTxt += "5";
            if (aa == '6')
                this._modeTxt += "6";
            if (aa == '7')
                this._modeTxt += "7";
            if (aa == '8')
                this._modeTxt += "8";
            if (aa == '9')
                this._modeTxt += "9";
            if (aa == '0')
                this._modeTxt += "0";
        }

        try {
            final ShowGMChat showGMChat = new ShowGMChat();
            GeneralThreadPool.get().execute(showGMChat);

            int runcount = 0;
            while (this._isRun) {
                runcount++;
                Thread.sleep(1000);
                this.chat(runcount);
            }
            this._pc.set_showGm(null);
            this._npc.broadcastPacketX8(new S_NpcChat(this._npc, this._chat4));
            Thread.sleep(1000);
            this.delme();

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private class ShowGMChat implements Runnable {

        // @Override
        public void run() {
            try {
                while (MEFAntiBotExecute.this._isRun) {
                    if (MEFAntiBotExecute.this._pc == null
                            || MEFAntiBotExecute.this._pc.getNetConnection() == null) {
                        MEFAntiBotExecute.this.setPcLoc();
                        MEFAntiBotExecute.this._isRun = false;
                    }
                    /*
                     * if (MEFAntiBotExecute.this._pc.getMapId() == 501 ||
                     * MEFAntiBotExecute.this._pc.getMapId() == 350) {
                     * MEFAntiBotExecute.this._isRun = false; } if
                     * (MEFAntiBotExecute.this._pc.isGm()) {
                     * MEFAntiBotExecute.this._isRun = false; }
                     *
                     * if (MEFAntiBotExecute.this._pc.isPrivateShop()) {
                     * MEFAntiBotExecute.this._isRun = false; } if
                     * (MEFAntiBotExecute.this._pc.isSafetyZone()) {
                     * MEFAntiBotExecute.this._isRun = false; } if
                     * (MEFAntiBotExecute.this._pc.castleWarResult()) {
                     * MEFAntiBotExecute.this._isRun = false; }
                     */

                    /*
                     * if (MEFAntiBotExecute.this._pc.getHpRegenState() != 1 &&
                     * !MEFAntiBotExecute.this._pc.isSkill() ){
                     * MEFAntiBotExecute.this._isRun = false; }
                     */

                    if (MEFAntiBotExecute.this._isRun) {
                        if (MEFAntiBotExecute.this._npc.getMapId() != MEFAntiBotExecute.this._pc
                                .getMapId()) {
                            MEFAntiBotExecute.this.teleport();

                        } else {
                            final int location = MEFAntiBotExecute.this._npc
                                    .getLocation().getTileLineDistance(
                                            MEFAntiBotExecute.this._pc
                                                    .getLocation());
                            if (location >= 8) {
                                MEFAntiBotExecute.this.teleport();

                            } else if ((location < 8) && (location > 2)) {
                                final int dir = MEFAntiBotExecute.this._npc
                                        .getMove()
                                        .moveDirection(
                                                MEFAntiBotExecute.this._pc
                                                        .getX(),
                                                MEFAntiBotExecute.this._pc
                                                        .getY());
                                if (dir != -1) {
                                    MEFAntiBotExecute.this._npc.getMove()
                                            .setDirectionMove(dir);
                                }
                            }
                        }
                    }
                    final int moveSpeed = MEFAntiBotExecute.this._npc
                            .getMoveSpeed();
                    if (moveSpeed <= 0) {
                        MEFAntiBotExecute.this._isRun = false;
                    }
                    Thread.sleep(MEFAntiBotExecute.this._npc.getMoveSpeed());
                }

            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }

    }

    private void setPcLoc() {
        UpdateLocReading.get().setPcLoc(this._pcid);
    }

    /**
     * 刪除NPC
     */
    private void delme() {
        for (final L1PcInstance pc : World.get().getVisiblePlayer(this._npc)) {
            pc.sendPackets(new S_SkillSound(this._npc.getId(), 169));
        }
        this._npc.deleteMe();
    }

    /**
     * 傳回數字答案
     *
     * @return
     */
    public int getInt() {
        return this._mode;
    }

    private static final int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX,
            PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
            // BERSERKERS,
            // IMMUNE_TO_HARM, //
            // ADVANCE_SPIRIT, //

            // REDUCTION_ARMOR,
            // BOUNCE_ATTACK,//
            // SOLID_CARRIAGE,
            // BURNING_SPIRIT,

            // VENOM_RESIST,
            // DOUBLE_BRAKE,
            // UNCANNY_DODGE,
            // DRESS_EVASION, //
            // GLOWING_AURA,
            // BRAVE_AURA,

            // RESIST_MAGIC,
            // CLEAR_MIND,
            // ELEMENTAL_PROTECTION,
            // AQUA_PROTECTER,
            // BURNING_WEAPON,
            // IRON_SKIN,
            // EXOTIC_VITALIZE,
            // WATER_LIFE,
            // ELEMENTAL_FIRE,
            // SOUL_OF_FLAME,
            // ADDITIONAL_FIRE
    };

    /**
     * 停止
     */
    public void stopGm() {
        if (this._itemId != 0) {
            CreateNewItem
                    .createNewItem(this._pc, this._itemId, this._itemCount);
            this._pc.sendPackets(new S_ServerMessage("回答正確"));

            for (int i = 0; i < allBuffSkill.length; i++) {
                final L1Skills skill = SkillsTable.get().getTemplate(
                        allBuffSkill[i]);
                new L1SkillUse().handleCommands(_pc, allBuffSkill[i],
                        _pc.getId(), _pc.getX(), _pc.getY(),
                        skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
            }
            _pc.setCurrentHp(_pc.getMaxHp());
            // pc.setCurrentMp(pc.getMaxMp());

            // WriteLogTxt.Recording("AI_測試通過紀錄", "帳號：〈" + _pc.getAccountName()
            // + "〉IP：〈" +
            // _pc.getNetConnection().getIp() + "〉玩家：〈" + _pc.getName() +
            // "〉AI測試通過。");
        }
        this._isRun = false;
    }

    @SuppressWarnings("unused")
    private void randomTeleport(final L1PcInstance pc) {
        if (pc.getMapId() != 99) {
            pc.getMap().isTeleportable();
            pc.getNetConnection().kick();// 中斷
            // WriteLogTxt.Recording("AI_外掛斷線紀錄", "帳號：〈" + pc.getAccountName() +
            // "〉IP：〈" +
            // pc.getNetConnection().getIp() + "〉玩家：〈" + pc.getName() +
            // "〉AI測試斷線。");

        }
    }

    /**
     * 傳送
     */
    private void teleport() {
        World.get().moveVisibleObject(this._npc, this._pc.getMapId());
        this._npc.setX(this._pc.getX());
        this._npc.setY(this._pc.getY());
        this._npc.setMap(this._pc.getMapId());
        this._npc.setHeading(5);
    }

    /**
     * 對話
     *
     * @param runcount
     */
    private void chat(final int runcount) {
        if (runcount == 2) {
            this._npc.broadcastPacketX8(new S_NpcChat(this._npc, this._pc
                    .getName() + this._chat1));
            return;
        }

        if (runcount >= 60) {
            // 樂在其中將本功能更改用於進行在線問答，成功答題後獎勵，沒有成功答題無處罰。
            // this.randomTeleport(this._pc);
            _pc.getNetConnection().kick();// 中斷
            _pc.setChack_game(0);
            this._isRun = false;
            // 傳送
            return;
        }

        if (runcount % 5 == 0) {
            this._npc.broadcastPacketX8(new S_NpcChat(this._npc, "玩家["
                    + this._pc.getName() + "]" + this._chat2 + this._modeTxt));
            this._pc.setCheckPluginTick();
            return;
        }

//        if (runcount % 30 == 0) {
//            this._npc.broadcastPacketX8(new S_NpcChat(this._npc, this._chat3));
//            return;
//        }
    }

    // 記錄在文檔
    // private static void Bot(String info) {
    // try {
    // BufferedWriter out = new BufferedWriter(new FileWriter("AllLog/bot.log",
    // true));
    // out.write(info + "\r\n");
    // out.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

}
