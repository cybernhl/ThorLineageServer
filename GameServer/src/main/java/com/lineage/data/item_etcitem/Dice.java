package com.lineage.data.item_etcitem;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 骰子1階40325<BR>
 * 骰子3階40326<BR>
 * 骰子4階40327<BR>
 * 骰子6階40328<BR>
 */
public class Dice extends ItemExecutor {

    /**
     *
     */
    private Dice() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Dice();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemId = item.getItemId();
        final Random _random = new Random();
        int gfxid = 0;
        switch (itemId) {
            case 40325: // 2種可能
				gfxid = 3237 + _random.nextInt(2);
                break;

            case 40326: // 3種可能
				gfxid = 3229 + _random.nextInt(3);
                break;

            case 40327: // 4種可能
				gfxid = 3241 + _random.nextInt(4);
                break;

            case 40328: // 6種可能
				gfxid = 3204 + _random.nextInt(6);
                break;
            case 404520:
				gfxid = 3204;
				break;
            case 404521:
				gfxid = 3205;
				break;
            case 404522:
				gfxid = 3206;
				break;
            case 404523:
				gfxid = 3207;
				break;
            case 404524:
				gfxid = 3208;
				break;
            case 404525:
				gfxid = 3209;
                break;
        }

        if (gfxid != 0) {
//            pc.getInventory().consumeItem(40318, 1);
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
        }
    }
}
