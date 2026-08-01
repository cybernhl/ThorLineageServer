package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.world.WorldClan;

/**
 * 物品名單(血盟倉庫)<br>
 * 類名稱：S_RetrievePledgeList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午3:01:32<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_RetrievePledgeList extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物品名單(血盟倉庫)
	 * @param objid
	 * @param pc
	 */
	public S_RetrievePledgeList(final int objid, final L1PcInstance pc) {
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}

		if ((clan.getWarehouseUsingChar() != 0)
				&& (clan.getWarehouseUsingChar() != pc.getId())) {// 目前無人使用 或是 使用者是自己
			pc.sendPackets(new S_ServerMessage(209)); // \f1血盟成員在使用倉庫。請稍後再使用。
			return;
		}

		if (pc.getInventory().getSize() < 180) {
			final int size = clan.getDwarfForClanInventory().getSize();
			if (size > 0) {
				clan.setWarehouseUsingChar(0); // 設置血盟倉庫目前使用者
//				clan.setWarehouseUsingChar(pc.getId()); // 設置血盟倉庫目前使用者
				this.writeC(S_OPCODE_SHOWRETRIEVELIST);
				this.writeD(objid);
				this.writeH(size);
				this.writeC(0x05); // 血盟倉庫
				for (final Object itemObject : clan.getDwarfForClanInventory().getItems()) {
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
