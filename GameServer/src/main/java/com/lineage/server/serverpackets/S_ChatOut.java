package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 強制登出人物(死亡後重新開始 - 未知)
 * @author dexc
 *
 */
public class S_ChatOut extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 強制登出人物(死亡後重新開始)
	 * @param objid
	 */
	public S_ChatOut(final int objid) {
		this.buildPacket(objid);
	}

	/**
	 * 強制登出人物(死亡後重新開始)
	 * @param pc
	 */
	public S_ChatOut(L1PcInstance pc) {
		this.buildPacket(pc.getId());
	}

    private void buildPacket(final int objid) {
        System.out.println("強制登出人物(死亡後重新開始) " + this.getClass().getName());
        // this.writeC(S_OPCODE_CHAROUT);
        this.writeD(objid);
        this.writeD(0x00000000);
        this.writeD(0x00000000);
        this.writeD(0x00000000);
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