package com.lineage.server.timecontroller.server;

import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.MEFAntiBotExecute;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * //外掛檢測提問 by：阿樂
 *
 * @author L1jJP,Dexc,Tom
 *
 */
public class TTEDetectBot extends TimerTask {

    private static final Log _log = LogFactory.getLog(TTEDetectBot.class);

    @SuppressWarnings("unused")
    private ScheduledFuture<?> _timer;

    public void start() {
        final int timeMillis = ConfigOther.wgjc_sj * 1000; // 60秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
        System.out.println("啟動提問");
    }

    // @Override
    public void run() {
        try {
            final Collection<L1PcInstance> allpc = World.get().getAllPlayers();
            for (final L1PcInstance pc : allpc) {
                if (pc == null) { // 防止對像為空導致循環出錯
                    continue;
                }

                if (pc.isPrivateShop()) {
                    continue;
                }
                // 在以下地圖不進行提問。
                /*
                 * if (pc.getMapId() == 501 || pc.getMapId() == 350 ||
                 * pc.getMapId() == 725 || pc.getMapId() == 726 || pc.getMapId()
                 * == 508 || pc.getMapId() == 511) { continue; }
                 */
                // 在掛機地圖不提問
                /*
                 * if (DTMap.get().isAutoBot(pc.getMapId())) { continue; }
                 */

                // 在打BOSS時，不進行提問。
                L1Object target = null;
                target = pc.get_lastAttackObj();
                if (target != null) { // 如果攻擊目標不為空
                    if (target instanceof L1Character) {
                        if (!((L1Character) target).isDead()) {
                            if (target instanceof L1PcInstance) { // 在打架的，不提問，
                                continue;
                            }
                            /*
                             * if (target instanceof L1MonsterInstance) {
                             * //在打BOSS的，不提問， continue; } if (target instanceof
                             * L1NpcInstance) { //在打BOSS的，不提問， continue; }
                             */
                        }
                    }
                }
                if (pc.get_showGm() != null) {
                    continue;
                }
                if (pc.getMapId() == 15) { // 城堡內
                    continue;
                }
                // if (pc.isGm()) {
                // continue;
                // }
                /*
                 * if (pc.isPrivateShop()) {// 個人商店中人物 continue; }
                 */
                if (pc.isSafetyZone()) {
                    continue;
                }
                if (pc.castleWarResult()) {
                    continue;
                }
                if (pc.isGm()) {
                    continue;
                }

                /*
                 * if (pc.getHpRegeneration()){ continue; }
                 */

                /*
                 * if (!pc.hasSkillEffect(IDSkill.BOTQA)){ continue; }
                 */
                this.start(pc);
            }

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 防掛問答
     *
     * @param pc
     */
    private void start(final L1PcInstance pc) {
        final MEFAntiBotExecute mEFAntiBotExecute = new MEFAntiBotExecute(pc, 0, 1000);
        GeneralThreadPool.get().execute(mEFAntiBotExecute);
    }
}
