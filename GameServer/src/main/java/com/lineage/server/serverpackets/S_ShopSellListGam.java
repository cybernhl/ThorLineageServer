package com.lineage.server.serverpackets;

import java.util.Map;

import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.gambling.Gambling;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.timecontroller.event.GamblingTime;

/**
 * 買食人妖精競賽票<br>
 * 類名稱：S_ShopSellListGam<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午2:30:55<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ShopSellListGam extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 買食人妖精競賽票
	 * @param npc
	 */
	public S_ShopSellListGam(final L1PcInstance pc, final L1NpcInstance npc) {
		this.writeC(S_OPCODE_SHOWSHOPBUYLIST);
		this.writeD(npc.getId());

		final Gambling gambling = GamblingTime.get_gambling();
		final Map<Integer, GamblingNpc> list = gambling.get_allNpc();

		if (list.size() <= 0) {
			this.writeH(0x0000);
			return;
		}

		this.writeH(list.size());

		// 食人妖精競賽票
		final L1Item item = ItemTable.get().getTemplate(40309);

		int i = 0;
		for (final GamblingNpc gamblingNpc : list.values()) {
			i++;
			pc.get_otherList().add_gamList(gamblingNpc, i);
			
			this.writeD(i);
			this.writeH(item.getGfxId());
			this.writeD(GamblingSet.GAMADENA);

			final int no = GamblingTime.get_gamblingNo();
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(gamblingNpc.get_npc().getNameId());
			stringBuilder.append(" [" + no + "-" + gamblingNpc.get_npc().getNpcId() + "]");

			this.writeS(stringBuilder.toString());
			this.writeC(0x00);
		}

		this.writeH(0x0007); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
