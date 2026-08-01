package com.lineage.server.serverpackets;

/**
 * 未知 B 人物列表之前
 * @author dexc
 *
 */
public class S_Unknown_B extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 未知 B 人物列表之前
	 * @param i
	 */
	public S_Unknown_B() {
		System.out.println("未知封包：" + this.getType());
		//this.writeC(S_OPCODE_CHARRESET);

		this.writeC(0x0a);
		this.writeC(0x02);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(0x2b);
		this.writeC(0x7f);
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
