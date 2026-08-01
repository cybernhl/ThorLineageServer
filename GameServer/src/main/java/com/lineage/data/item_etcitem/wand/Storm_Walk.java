package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_OwnCharPack;

/**
 * 暴風疾走42501
 */
public class Storm_Walk extends ItemExecutor {

	/**
	 *
	 */
	private Storm_Walk() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Storm_Walk();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		final int spellsc_x = data[1];// data[1]=readH()
		final int spellsc_y = data[2];// data[2]=readH()
		// 更新角色所在的地圖
		pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));
		pc.sendPackets(new S_OwnCharPack(pc));
		pc.sendPackets(new S_CharVisualUpdate(pc));
		L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(), pc.getHeading(), true, L1Teleport.CHANGE_POSITION);
	}
}
