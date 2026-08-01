package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShowSummonList;
import com.lineage.server.templates.L1Npc;

import java.util.Random;

/**
 * 召喚術
 * 
 * @author dexc
 * 
 */
public class SUMMON_MONSTER extends SkillMode {

    public SUMMON_MONSTER() {
    }
    private static final Random _random = new Random();
    public final int rand(final int lbound, final int ubound) {
        return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;// magic.calcMagicDamage(L1SkillId.SUMMON_MONSTER);

        if (srcpc.isWarZone()) {
            srcpc.sendPackets(new S_ServerMessage("城戰區內無法召喚。"));
            return dmg;
        }

        final int level = srcpc.getLevel();
        int[] summons;
        if (!srcpc.getMap().isRecallPets()) {
            // 353：在這附近無法召喚怪物。
            srcpc.sendPackets(new S_ServerMessage(353));
            return dmg;
        }
        // 召喚控制戒指(20284)
        if (srcpc.getInventory().checkEquipped("$1937") || srcpc.getInventory().checkEquipped("受祝福的 召喚控制戒指")|| srcpc.getInventory().checkEquipped("召喚控制戒指(7日)")) {
            srcpc.sendPackets(new S_ShowSummonList(srcpc.getId()));
            if (!srcpc.isSummonMonster()) {
                srcpc.setShapeChange(false);
                srcpc.setSummonMonster(true);
            }

        } else {
            summons = new int[] { //
                    //
                    81083, // 歐熊
                    81084, // 蟑螂人
                    81085, // 巨大兵蟻
                    81086, // 食人妖精
                    81086, // 食人妖精王
                    81086, // 骷髏鬥士
                    81086 // 阿魯巴
                    //  81097, // 高侖熔岩怪
                    // 81100 // 魔熊
            };
//            summons = new int[] { //
//                    //
//                    81083, // 歐熊
//                    81084, // 蟑螂人
//                    81085, // 巨大兵蟻
//                    81086, // 食人妖精
//                    81087, // 食人妖精王
//                    81088, // 骷髏鬥士
//                    81089 // 阿魯巴
//                  //  81097, // 高侖熔岩怪
//                   // 81100 // 魔熊
//            };

            int summonid = 0;
            int summoncost = 3;
//            int summoncost = 6;
            int levelRange = 32;
            for (int i = 0; i < summons.length; i++) { // 目前等級
                if ((level < levelRange) || (i == summons.length - 1)) {
                    summonid = summons[i];
                    break;
                }
                levelRange += 4;
            }

            int petcost = 0;
            final Object[] petlist = srcpc.getPetList().values().toArray();
            for (final Object pet : petlist) {
                // 目前寵物數量
//                petcost += ((L1NpcInstance) pet).getPetcost();
                petcost += 3;
            }
            final int charisma = srcpc.getCha() - petcost;// + 6 - petcost;

//            if (summonid == 81097 || summonid == 81100) { // 高侖熔岩怪、魔熊
//                summoncost = 10;
//            }

            int summoncount = charisma / summoncost;
            if (srcpc.getLevel() >= 40 && summoncount > 0) {
                    int chance = rand(0, 999999);
                    if (chance < 200000) {
                        summoncount = 8;
                    } else if (chance < 400000) {
                        summoncount = 7;
                    } else if (chance < 600000) {
                        summoncount = 6;
                    } else if (chance < 800000) {
                        summoncount = 5;
                    } else {
                        summoncount = 4;
                    }
            }
            final L1Npc npcTemp = NpcTable.get().getTemplate(summonid);
            for (int i = 0; i < summoncount; i++) {
                final L1SummonInstance summon = new L1SummonInstance(npcTemp, srcpc, i);
                summon.setPetcost(summoncost);
                summon.setSkillSpawn(true);
            }
        }
        return dmg;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        final int dmg = 0;

        return dmg;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        final String s = (String) obj;
        String[] summonstr_list;
        int[] summonid_list;
        int[] summonlvl_list;
        int[] summoncha_list;
        int summonid = 0;
        int levelrange = 0;
        int summoncost = 0;

        // 傳回質
        summonstr_list = new String[] { //
                // 28級以上
                "7", // 歐熊
                "263", // 骷髏斧手
                // "519",

                // 32級以上
                "8", // 蟑螂人
                "264", // 萊肯
                // "520",

                // 36級以上
                "9", // 巨大兵蟻
                "265", // 艾爾摩士兵
                // "521",

                // 40級以上
                "10", // 食人妖精
                "266", // 巨斧牛人
                // "522",

                // 44級以上
                "11", // 食人妖精王
                "267", // 骷髏警衛
                // "523",

                // 48級以上
                "12", // 骷髏鬥士
                "268", // 翼魔
                // "524",

                // 52級以上
                "13", // 阿魯巴
                "269", // 魔狼
                // "525",

                // 56級以上
                "14", // 高侖熔岩怪
                "270", // 獨眼巨人
                "526",

                // 60級以上
                "15", // 魔熊
                "271", // 烈炎獸
                 "527",

                // 68級以上
                // "16",
                "17", // 變形怪首領

                // 72級以上
                "18", // 黑豹
                // "274"
        };

        // NPCID
        summonid_list = new int[] { //
                // 28級以上
                81083, 81090, // 歐熊、骷髏斧手
                // 32級以上
                81084, 81091, // 蟑螂人、萊肯
                // 36級以上
                81085, 81092, // 巨大兵蟻、艾爾摩士兵
                // 40級以上
                81086, 81093, // 食人妖精、巨斧牛人
                // 44級以上
                81087, 81094, // 食人妖精王、骷髏警衛
                // 48級以上
                81088, 81095, // 骷髏鬥士、翼魔
                // 52級以上
                81089, 81096, // 阿魯巴、魔狼
                // 56級以上
                81097, 81098,81099, // 高侖熔岩怪、獨眼巨人
                // 60級以上
                81100, 81101,81102, // 魔熊、烈炎獸
                // 68級以上
                81103, // 變形怪首領
                // 72級以上
                81104, // 黑豹
        };

        // 等級
        summonlvl_list = new int[] { 28, 28, 32, 32, 36, 36, 40, 40, 44, 44, 48, 48, 52, 52, 56, 56,56, 60,60, 60, 68, 72 };

        // 魅力
        summoncha_list = new int[] { 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 10, 10, 10,10, 12,12, 38, 44 };

        for (int loop = 0; loop < summonstr_list.length; loop++) {
            if (s.equalsIgnoreCase(summonstr_list[loop])) {
                summonid = summonid_list[loop];
                levelrange = summonlvl_list[loop];
                summoncost = summoncha_list[loop];
                break;
            }
        }

        // Lv不足
        if (srcpc.getLevel() < levelrange) {
            // 743 等級太低而無法召喚怪物
            srcpc.sendPackets(new S_ServerMessage(743));
            srcpc.sendPackets(new S_CloseList(srcpc.getId()));
            return;
        }

        int petcost = 0;
        final Object[] petlist = srcpc.getPetList().values().toArray();
        for (final Object pet : petlist) {
            // 目前耗用質
            petcost += ((L1NpcInstance) pet).getPetcost();
        }

        // 變形怪/黑豹/
        if (((summonid == 81238) || (summonid == 81240)) && (petcost != 0)) {
            srcpc.sendPackets(new S_CloseList(srcpc.getId()));
            return;
        }

        final int charisma = srcpc.getCha() + 6 - petcost;
        final int summoncount = charisma / summoncost;

        boolean isStop = false;
        if ((srcpc.getCha() + 6) < summoncost) {
            isStop = true;
        }
        if (summoncount <= 0) {
            isStop = true;
        }

        if (isStop) {
            // 319：\f1你不能擁有太多的怪物。
            srcpc.sendPackets(new S_ServerMessage(319));
            srcpc.sendPackets(new S_CloseList(srcpc.getId()));
            return;
        }

        final L1Npc npcTemp = NpcTable.get().getTemplate(summonid);

        for (int cnt = 0; cnt < summoncount; cnt++) {
            final L1SummonInstance summon = new L1SummonInstance(npcTemp, srcpc, cnt);
            int upPetcost = 0;
            /*if (summon.getNameId().equals("$1554")) {// 變形怪
                upPetcost = 7;
            }*/
            if (summon.getNameId().equals("$2106")) {// 巨大牛人
                upPetcost = 7;
            }
            if (summon.getNameId().equals("$2587")) {// 黑豹
                upPetcost = 7;
            }
            summon.setPetcost(summoncost + upPetcost);
        }
        srcpc.sendPackets(new S_CloseList(srcpc.getId()));
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        // TODO Auto-generated method stub
    }
}
