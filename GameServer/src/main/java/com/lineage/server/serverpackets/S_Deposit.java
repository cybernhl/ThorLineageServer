package com.lineage.server.serverpackets;

/**
 * 城堡寶庫(要求存入資金)
 * @author dexc
 *
 */
public class S_Deposit extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 城堡寶庫(要求存入資金)
	 * @param objecId
	 */
	public S_Deposit(final int objecId) {
		this.writeC(S_OPCODE_DEPOSIT);
		this.writeD(objecId);
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
