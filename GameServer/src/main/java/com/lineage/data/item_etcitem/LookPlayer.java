package com.lineage.data.item_etcitem;

import com.custom.LookPlayerInstance;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1EquipmentSlot;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.world.World;

public class LookPlayer extends ItemExecutor {
    public static ItemExecutor get() {
        return new LookPlayer();
    }
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int spellsc_objid = data[0];
        final L1Object target = World.get().findObject(spellsc_objid);
        if (!(target instanceof L1PcInstance)) {
            return;
        }
        final L1PcInstance targetPc = (L1PcInstance) target;
        final L1ItemInstance weapon = targetPc.getWeapon();
        final LookPlayerInstance lookPlayerInstance = new LookPlayerInstance(pc, targetPc.getName(), targetPc.getEquipSlot().getArmors(), targetPc.getWeapon(),
                targetPc.getMaxHp(),targetPc.getMaxMp(),targetPc.getAc(),targetPc.getMr(),targetPc.getStr(),targetPc.getCon(),targetPc.getDex(),targetPc.getWis(),targetPc.getInt(),targetPc.getCha(),targetPc.getSp(), targetPc.getLevel());
        pc.setLookPlayerInstance(lookPlayerInstance);
        pc.getInventory().removeItem(item, 1);
        lookPlayerInstance.showMainPage();
    }
}
