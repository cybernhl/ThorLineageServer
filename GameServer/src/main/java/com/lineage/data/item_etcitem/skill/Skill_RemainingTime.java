package com.lineage.data.item_etcitem.skill;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillTimer;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;

import java.util.HashMap;
import java.util.Map;

public class Skill_RemainingTime extends ItemExecutor {
    public static ItemExecutor get() {
        return new Skill_RemainingTime();
    }
    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
//        // 例外狀況:物件為空
//        if (item == null) {
//            return;
//        }
        // 例外狀況:人物為空
        if (pc == null) {
            return;
        }
        final HashMap<Integer, L1SkillTimer> effects = pc.getSkillEffects();
        if (effects.isEmpty()) {
            pc.sendPackets(new S_ServerMessage(166, "沒有狀態可以顯示"));
        }
        for (final Map.Entry<Integer, L1SkillTimer> effect : effects.entrySet()) {
            final L1Skills skill = SkillsTable.get().getTemplate(effect.getKey());
            if (skill == null || skill.getName().isEmpty()) {
                final String name = SkillsTable.get().getSkillItemName(effect.getKey());
                if (name.isEmpty()) {
                    continue;
                }
                if (effect.getValue() == null) {
                    pc.sendPackets(new S_ServerMessage(166, "狀態 『" + name + "』 剩餘秒數: 0 秒"));
                } else {
                    pc.sendPackets(new S_ServerMessage(166, "狀態 『" + name + "』 剩餘秒數: " + effect.getValue().getRemainingTime() + " 秒"));
                }
            }  else {
                pc.sendPackets(new S_ServerMessage(166, "技能 『" + skill.getName() + "』 剩餘秒數: " + effect.getValue().getRemainingTime() + " 秒"));
            }
        }
    }
}
