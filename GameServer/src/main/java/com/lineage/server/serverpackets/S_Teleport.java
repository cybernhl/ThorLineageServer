package com.lineage.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 傳送鎖定(瞬間移動)
 * 
 * @author dexc
 * 
 */
public class S_Teleport extends ServerBasePacket {

	private byte[] _byte = null;

	private static AtomicInteger _nextId = new AtomicInteger(100000);

	/**
	 * 傳送鎖定(瞬間移動)
	 * 
	 * @param pc
	 */
	public S_Teleport() {
		// 0000: 1a eb a7 44 02 d9 cd a7 ...D....
		this.writeC(S_OPCODE_TELEPORT);

		this.writeC(0xeb);
		this.writeC(0xa7);

		this.writeD(_nextId.incrementAndGet());
	}

	public S_Teleport(short nextMap, short currentMap) {
		writeC(S_OPCODE_POTAL);
		writeH(nextMap);
		switch (currentMap) {
		case 0:
			writeC(0xFC);
			writeC(0xFD);
			writeC(0x60);
			writeC(0x00);
			break;
		case 3:
			writeC(0x46);
			writeC(0xFF);
			writeC(0x2E);
			writeC(0x00);
			break;
		case 4:
			writeC(0xf4);
			writeC(0x09);
			writeC(0x9b);
			writeC(0xfd);
			break;
		case 63:
			writeC(0x4c);
			writeC(0x00);
			writeC(0x64);
			writeC(0x00);
			break;
		case 75:
			writeC(0xf6);
			writeC(0xff);
			writeC(0x86);
			writeC(0x00);
			break;
		default:
			writeC(0xf4);
			writeC(0x09);
			writeC(0x9b);
			writeC(0xfd);
			break;
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
