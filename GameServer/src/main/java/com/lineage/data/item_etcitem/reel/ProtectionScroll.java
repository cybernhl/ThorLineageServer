package com.lineage.data.item_etcitem.reel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.util.Random;

public class ProtectionScroll extends ItemExecutor {
    private int _type;

    private int _type2;

    private int _rom;

    private int _enchant;

    private int enchantRom = 0;

    public static ItemExecutor get() {
        return new ProtectionScroll();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {

        final int targObjId = data[0];

        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

        if (tgItem != null) {
            if (tgItem.getItem().get_safeenchant() <= -1) {// 無法使用防武卷強化的道具
                pc.sendPackets(new S_ServerMessage("此裝備無法強化"));
                return;
            }

            if (tgItem.getproctect()) {// 正在保護中
                pc.sendPackets(new S_ServerMessage("此裝備已經有強化保護狀態"));
                return;
            }

            final int use_type = tgItem.getItem().getUseType();
            boolean ok = false;
            switch (use_type) {
                case 1:
                    if (_type2 == 1 || _type2 == 2) {
                        ok = true;
                    }
                    break;
                case 2:// 盔甲
                case 18:// T恤
                case 19:// 斗篷
                case 20:// 手套
                case 21:// 靴
                case 22:// 頭盔
                case 25:// 盾牌
                case 47:// 脛甲
                case 23:// 戒指
                case 24:// 項鍊
                case 37:// 腰帶
                case 40:// 耳環
                    if (_type2 == 1 || _type2 == 3) {
                        ok = true;
                    }
                    break;
            }
            if (!ok) {
                pc.sendPackets(new S_ServerMessage("無法使用在你選擇的裝備"));
                return;
            }

            if (tgItem.getEnchantLevel() >= _enchant) {
                pc.sendPackets(new S_ServerMessage("裝備強化值已達到保護卷所能保護的最大值"));
                return;
            }

            if ((rand(0, 999999) < (this.enchantRom * 10000)) || this.enchantRom == 0) {
                tgItem.setproctect(true);
                tgItem.setProctectRom(_rom);
                tgItem.setProctectType(_type);
                pc.sendPackets(new S_ItemStatus(tgItem));
                pc.sendPackets(new S_ServerMessage("你的裝備已經獲得保護卷軸保護，請立即使用強化卷強化"));
                pc.getInventory().saveItem(tgItem, L1PcInventory.COL_PROTECT_INDEX);
            } else {
                pc.sendPackets(new S_ServerMessage("保護失敗."));
            }
            pc.getInventory().removeItem(item, 1);

        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _type = Integer.parseInt(set[1]);
        } catch (final Exception localException) {

        }

        try {
            _rom = Integer.parseInt(set[2]);
        } catch (final Exception localException) {

        }

        try {
            _enchant = Integer.parseInt(set[3]);
        } catch (final Exception localException) {

        }

        try {
            _type2 = Integer.parseInt(set[4]);
        } catch (final Exception localException) {

        }

        try {
            enchantRom = Integer.parseInt(set[5]);
        } catch (final Exception e) {

        }
    }
    public static final int rand(final int lbound, final int ubound) {
        final Random random = new Random();
        return (int) ((random.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}
