package com.lineage.server.serverpackets;

/**
 * 魔法效果:操作混亂
 * @author dexc
 *
 */
public class S_Liquor extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:操作混亂
	 * @param objecId
	 */
	public S_Liquor(final int objecId) {
		this.writeC(126);
		this.writeD(objecId);
		this.writeC(0x01);
	}

	/**
	 * 混亂武器(失心)
	 * @param objecId
	 * @param type 0:無 1:2:3:效果強度
	 */
	public S_Liquor(final int objecId, final int type) {
		this.writeC(126);
		this.writeC(type);
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
