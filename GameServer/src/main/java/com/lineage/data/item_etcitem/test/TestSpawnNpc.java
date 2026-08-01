package com.lineage.data.item_etcitem.test;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 重置Npc坐標信息spawnlist_npc<br>
 * 類名稱：TestSpawnNpc<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午5:03:53<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class TestSpawnNpc extends ItemExecutor {

	/**
	 *
	 */
	private TestSpawnNpc() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TestSpawnNpc();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 選取對像OBJID
		final int targObjId = data[0];
		// 選取對像X
		final int spellsc_x = data[1];
		// 選取對像Y
		final int spellsc_y = data[2];
		
		final L1Object target = World.get().findObject(targObjId);
		
		if (target != null) {
			L1NpcInstance npc = (L1NpcInstance) target;
			int id = npc.getNpcTemplate().getSpawnID();
			NpcSpawnTable.get().updataNpc(id, pc.getX(), pc.getY(), pc.getHeading(), pc.getMapId());
			pc.sendPackets(new S_SystemMessage(npc.getName() + " 重置完畢" + ":" + id));
		}

	}
}
