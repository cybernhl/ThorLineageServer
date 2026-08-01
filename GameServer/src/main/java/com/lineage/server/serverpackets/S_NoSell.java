package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;

/**
 * 賣中獎的彩票<br>
 * 類名稱：S_NoSell<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月6日 下午12:50:14<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_NoSell extends ServerBasePacket {
	private byte[] _byte = null;

	public S_NoSell(final L1NpcInstance npc) {
		this.buildPacket(npc);
	}

	private void buildPacket(final L1NpcInstance npc) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(npc.getId());
		this.writeS("nosell");
		this.writeC(0x01);
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
