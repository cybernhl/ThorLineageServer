package com.custom.plugin;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PcUnlock;
import com.lineage.server.model.L1Character;
import com.lineage.server.thread.NpcAiThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoPlayGame implements Runnable {
    public static final AutoPlayGame instance = new AutoPlayGame();
    public static AutoPlayGame get() {
        return instance;
    }
    public void start() {
        NpcAiThreadPool.get().execute(this);
    }
    private final Map<Integer, AutoPcData> data = new HashMap<>();
    private final Lock lock = new ReentrantLock(false);
    public void addPc(final L1PcInstance pc) {
        lock.lock();
        try {
            this.data.put(pc.getId(), new AutoPcData(pc));
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void run() {
        final List<Integer> delete = new ArrayList<>();
        while (true) {
            try {
                lock.lock();
                try {
                    for (final Map.Entry<Integer, AutoPcData> auto : this.data.entrySet()) {
                        final L1PcInstance pc = auto.getValue().getPlayer();
                        // 掛機停止判斷
                        if (pc == null || pc.getNetConnection() == null || pc.getNetConnection().getAccount() == null
                                || pc.getMaxHp() <= 0 || pc.isDead() || pc.getWeapon() == null || pc.getInventory().getWeight182() >= 197
                                || pc.getCurrentHp() <= 0
                        ) {
                            delete.add(auto.getKey());
                            if (pc != null) {
                                L1PcUnlock.Pc_Unlock(pc); // 畫面更新
                            }
                            continue;
                        }
                        // 是否可以進行動作 否則繞過等下一次
                        final long now_tick = System.currentTimeMillis();
                        if (now_tick < auto.getValue().getNextActionTick()) {
                            continue;
                        }
                        // 目標有效性檢查
                        auto.getValue().checkTarget();
                        // 檢測是否有目標
                        if (auto.getValue().getTarget() != null && !auto.getValue().getTarget().isDead()) {
                        } else {
                            // 尋找目標
                        }
                        // 更新下次動作時間
                        auto.getValue().updateNextActionTick(getSprActionDelay(pc, 0));
                    }
                    for (final int i : delete) {
                        this.data.remove(i);
                    }
                    delete.clear();
                } finally {
                    lock.unlock();
                }
                Thread.sleep(50); // 不讓線程佔用太多虛擬資源 順便讓動作不那麼像機器人
            } catch (Exception e) { }
        }
    }

    public final int getSprActionDelay(final L1PcInstance pc, final int action) {
        int ret = 0;
        if (action == 0) { // 攻擊動作
            ret = SprTable.get().getAttackSpeed(
                    pc.getTempCharGfx(),
                    pc.getCurrentWeapon() + 1);
        } else if (action == 1) { // 走路動作
            ret = SprTable.get().getMoveSpeed(
                    pc.getTempCharGfx(), pc.getCurrentWeapon());
        }

        if (pc.isHaste()) { // 自我加速藥水加成
            ret *= 0.755;
        }

        if (action == 1 && pc.isFastMovable()) { // 神聖疾走加成
            ret *= 0.755;
        }

        if (pc.isBrave()) { // 勇敢藥水加成
            ret *= 0.755;
        }

        if (pc.isElfBrave()) { // 精靈餅乾加成
            if (action == 0) {
                ret *= 0.9;
            } else {
                ret *= 0.855;
            }
        }
        return ret;
    }

    public static class AutoPcData {
        private long next_action_tick = 0;
        private final L1PcInstance pc;
        private L1Character target;
        public AutoPcData(final L1PcInstance pc) {
            this.pc = pc;
        }
        public final L1PcInstance getPlayer() {
            return this.pc;
        }
        public final long getNextActionTick() {
            return this.next_action_tick;
        }
        public void updateNextActionTick(final int delay) {
            this.next_action_tick = System.currentTimeMillis() + delay;
        }
        public void setTarget(final L1Character target) {
            this.target = target;
        }
        public final L1Character getTarget() {
            return this.target;
        }
        public void checkTarget() {
            try {
                if (target == null) {// 目標為空
                    return;
                }
                if (target.getMapId() != this.pc.getMapId()) {// 目標地圖不相等
                    target = null;
                    return;
                }
                if (target.getCurrentHp() <= 0) {// 目標HP小於等於0
                    target = null;
                    return;
                }

                if (target.isDead()) {// 目標死亡
                    target = null;
                    return;
                }
                if (this.pc.get_showId() != target.get_showId()) {// 副本ID不相等
                    target = null;
                    return;
                }
                if (this.pc.getLocation().getTileDistance(
                        target.getLocation()) > 15) {
                    target = null;
                    return;
                }

            } catch (final Exception e) {
                return;
            }
        }
    }
}