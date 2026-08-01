package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 移轉物品名單<br>
 * 類名稱：S_RetrieveShiftingList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午3:02:08<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_RetrieveShiftingList extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 移轉物品名單
	 * @param pc
	 * @param items 
	 */
	public S_RetrieveShiftingList(final L1PcInstance pc, List<L1ItemInstance> items) {
		this.writeC(S_OPCODE_SHOWRETRIEVELIST);
		this.writeD(pc.getId());
		this.writeH(items.size());
		this.writeC(0x02); // 移轉物品名單
		for (final L1ItemInstance item : items) {
			final int itemid = item.getId();
			this.writeD(itemid);
			//System.out.println("itemid:" + itemid);
			this.writeC(0x00);
			this.writeH(item.get_gfxid());
			this.writeC(item.getBless());
			this.writeD(1);
			this.writeC(item.isIdentified() ? 0x01 : 0x00);
			this.writeS(item.getViewName());
		}
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
