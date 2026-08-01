package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品名單(個人倉庫)<br>
 * 類名稱：S_RetrieveList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午3:01:20<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_RetrieveList extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物品名單(個人倉庫)
	 * @param objid
	 * @param pc
	 */
	public S_RetrieveList(final int objid, final L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			final int size = pc.getDwarfInventory().getSize();
			if (size > 0) {
				this.writeC(S_OPCODE_SHOWRETRIEVELIST);
				this.writeD(objid);
				this.writeH(size);
				this.writeC(0x03); // 個人倉庫
				for (final Object itemObject : pc.getDwarfInventory().getItems()) {
					final L1ItemInstance item = (L1ItemInstance) itemObject;
					this.writeD(item.getId());
					int i = item.getItem().getUseType();
					if (i < 0) {
						i = 0;
					}
					this.writeC(i);// this.writeC(0x00);
					this.writeH(item.get_gfxid());
					this.writeC(item.getBless());
					this.writeD((int) Math.min(item.getCount(), 2000000000));
					this.writeC(item.isIdentified() ? 0x01 : 0x00);
					this.writeS(item.getViewName());
				}
			}

		} else {
			pc.sendPackets(new S_ServerMessage(263)); // 263 \f1一個角色最多可攜帶180個道具。
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
