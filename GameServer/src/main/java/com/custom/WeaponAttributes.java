package com.custom;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;

public class WeaponAttributes {
    public static void add(final L1PcInstance pc, final L1ItemInstance item) {
        if (item.getAttrEnchantLevel() <= 0) {
            return;
        }
//        pc.addDmgup(item.getAttrEnchantLevel()); // [每個屬性進階多+1~2傷害]
//        pc.addBowDmgup(item.getAttrEnchantLevel()); // [每個屬性進階多+1~2傷害]
//        pc.addSp(item.getAttrEnchantLevel()); // [每個屬性進階多+1~2傷害]
//        if (item.getAttrEnchantKind() == 4) { // 水
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addSp(1);
//                    break;
//                case 2:
//                    pc.addSp(3);
//                    break;
//                case 3:
//                    pc.addSp(5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 1) { // 地
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addAc(-1);
//                    break;
//                case 2:
//                    pc.addAc(-2);
//                    break;
//                case 3:
//                    pc.addAc(-5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 8) { // 風
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addBowDmgup(1);
//                    break;
//                case 2:
//                    pc.addBowDmgup(3);
//                    break;
//                case 3:
//                    pc.addBowDmgup(5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 2) { // 火
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addDmgup(1);
//                    break;
//                case 2:
//                    pc.addDmgup(3);
//                    break;
//                case 3:
//                    pc.addDmgup(5);
//                    break;
//            }
//        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
    public static void remove(final L1PcInstance pc, final L1ItemInstance item) {
        if (item.getAttrEnchantLevel() <= 0) {
            return;
        }
//        if (item.getAttrEnchantKind() == 4) { // 風
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addSp(-1);
//                    break;
//                case 2:
//                    pc.addSp(-3);
//                    break;
//                case 3:
//                    pc.addSp(-5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 1) { // 地
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addAc(1);
//                    break;
//                case 2:
//                    pc.addAc(3);
//                    break;
//                case 3:
//                    pc.addAc(5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 8) { // 水
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addBowDmgup(-1);
//                    break;
//                case 2:
//                    pc.addBowDmgup(-3);
//                    break;
//                case 3:
//                    pc.addBowDmgup(-5);
//                    break;
//            }
//        } else if (item.getAttrEnchantKind() == 2) { // 火
//            switch (item.getAttrEnchantLevel()) {
//                case 1:
//                    pc.addDmgup(-1);
//                    break;
//                case 2:
//                    pc.addDmgup(-3);
//                    break;
//                case 3:
//                    pc.addDmgup(-5);
//                    break;
//            }
//        }
//        switch (item.getAttrEnchantLevel()) {
//            case 1:
//                pc.addDmgup(-1);
//                pc.addBowDmgup(-1);
//                pc.addSp(-1);
//                break;
//            case 2:
//                pc.addDmgup(-3);
//                pc.addBowDmgup(-3);
//                pc.addSp(-3);
//                break;
//            case 3:
//                pc.addDmgup(-5);
//                pc.addBowDmgup(-5);
//                pc.addSp(-5);
//                break;
//        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
}
