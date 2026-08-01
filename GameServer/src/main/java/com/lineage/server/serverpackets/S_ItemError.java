package com.lineage.server.serverpackets;

/**
 * 學習魔法材料不足
 * @author dexc
 *
 */
public class S_ItemError extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 學習魔法材料不足
	 * @param skillid
	 */
	public S_ItemError(final int skillid) {
		this.buildPacket(skillid);
	}

	private void buildPacket(final int skillid) {
		System.out.println("未知封包：" + this.getType());
		//this.writeC(S_OPCODE_ITEMERROR);
		this.writeD(skillid);
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