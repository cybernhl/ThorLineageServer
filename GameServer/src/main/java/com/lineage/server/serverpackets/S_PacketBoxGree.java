package com.lineage.server.serverpackets;

/**
 * 畫面中央顏色對話訊息
 * @author dexc
 *
 */
public class S_PacketBoxGree extends ServerBasePacket {

	private byte[] _byte = null;

	/**畫面中央顏色對話訊息*/
	private static final int GREEN_MESSAGE = 0x54;

	/**畫面特效*/
	private static final int SECRETSTORY_GFX = 0x53;

	/**
	 * \\fW=莓紅紫
	 * \\fT=橄欖綠
	 * \\fR=大便色
	 * \\fU=土耳其藍
	 * \\fY=暗粉紅 
	 * \\fI=鵝黃色 
	 * \\fS=綠藍色
	 * \\fX=紫紅色 
	 * \\fV=紫色
	 * @param time
	 */
	public S_PacketBoxGree(final String msg) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(GREEN_MESSAGE);// 57
		writeC(0x02);// 44
		writeS(msg);
	}
	
	/**
	 * 秘壇副本特殊特效(哈汀-歐林)
	 * @param type 01 畫面粉紅特效
	 * @param type 02 畫面震動
	 * @param type 03 煙火特效 
	 */
	public S_PacketBoxGree(int type) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(SECRETSTORY_GFX);//0x53
		writeD(type);//01畫面震動 02 畫面粉紅特效 03 煙火特效
		writeC(0x00);
		writeC(0x00);
		writeC(0x00); 
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
