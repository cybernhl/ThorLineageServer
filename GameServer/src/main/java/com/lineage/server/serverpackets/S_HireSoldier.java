package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_HireSoldier extends ServerBasePacket {

	private byte[] _byte = null;

	// HTML開送npcdeloy-j.html表示
	// OK押C_127飛
	public S_HireSoldier(final L1PcInstance pc) {
		System.out.println("未知封包：" + this.getType());
		//this.writeC(S_OPCODE_HIRESOLDIER);
		this.writeH(0x0000); // ? 返含
		this.writeH(0x0000); // ? 返含
		this.writeH(0x0000); // 僱用傭兵總數
		this.writeS(pc.getName());
		this.writeD(0x00000000); // ? 返含
		this.writeH(0x0000); // 配置可能傭兵數
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
