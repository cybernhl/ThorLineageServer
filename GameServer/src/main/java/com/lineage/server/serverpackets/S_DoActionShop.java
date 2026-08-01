package com.lineage.server.serverpackets;

import com.lineage.server.ActionCodes;

/**
 * 物件動作種類(短時間)-個人商店
 * @author dexc
 *
 */
public class S_DoActionShop extends ServerBasePacket {

	/**
	 * PC使用
	 * @param object
	 * @param message
	 */
	public S_DoActionShop(final int object, final byte[] message) {
		this.writeC(S_OPCODE_DOACTIONGFX);
		this.writeD(object);
		this.writeC(ActionCodes.ACTION_Shop);// 動作編號
		this.writeByte(message);// 文字內容
	}

	/**
	 * 虛擬人物使用
	 * @param object
	 * @param message
	 */
	public S_DoActionShop(final int object, final String message) {
		this.writeC(S_OPCODE_DOACTIONGFX);
		this.writeD(object);
		this.writeC(ActionCodes.ACTION_Shop);// 動作編號
		this.writeS(message);// 文字內容
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
	}
}
