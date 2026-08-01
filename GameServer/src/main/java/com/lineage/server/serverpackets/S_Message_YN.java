package com.lineage.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 選項(Yes/No)<br>
 * 類名稱：S_Message_YN<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午12:38:56<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Message_YN extends ServerBasePacket {

	private byte[] _byte = null;

	// 交易編序
	private static AtomicInteger _sequentialNumber = new AtomicInteger(1); 

	/**
	 * 選項(Yes/No)
	 * @param type
	 */
	public S_Message_YN(final int type) {
		writeC(S_OPCODE_YES_NO);
		writeH(type);
	}
	
	/**
	 * 選項(Yes/No)<BR>
	 * 交易
	 * @param name
	 */
	public S_Message_YN(final String name) {
		writeC(S_OPCODE_YES_NO);
		//writeH(0x0000);
		//writeD(_sequentialNumber.incrementAndGet());
		writeH(0x00fc);
		writeS(name);
	}

	/**
	 * 選項(Yes/No)
	 * @param type
	 * @param msg
	 */
	public S_Message_YN(final int type, final String msg) {
		writeC(S_OPCODE_YES_NO);
		writeH(type);
		writeS(msg);
	}

	/**
	 * 選項(Yes/No)
	 * @param type
	 * @param msg1
	 * @param msg2
	 */
	public S_Message_YN(final int type, final String msg1, final String msg2) {
		writeC(S_OPCODE_YES_NO);
		writeH(type);
		writeS(msg1);
		writeS(msg2);
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
