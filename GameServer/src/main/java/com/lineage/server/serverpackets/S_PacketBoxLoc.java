package com.lineage.server.serverpackets;

/**
 * UI地圖座標傳送
 * @author kzk
 *
 */
public class S_PacketBoxLoc extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**UI地圖座標傳送*/
	public static final int SEND_LOC = 111;
	
	/**
	 * UI地圖座標傳送
	 * @param name
	 * @param map
	 * @param x
	 * @param y
	 * @param zone
	 */
	public S_PacketBoxLoc(String name,int map,int x,int y, int zone) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(SEND_LOC);
		writeS(name);
		writeH(map);
		writeH(x);
		writeH(y);
		writeD(zone);
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
