package com.lineage.server.serverpackets;

/**
 * 畫面中藍色訊息
 * @author dexc
 *
 */
public class S_BlueMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 畫面中藍色訊息
	 * @param type
	 */
	public S_BlueMessage(final int type) {
		this.buildPacket(type, null);
	}

	/**
	 * 畫面中藍色訊息
	 * @param type
	 * @param msg1
	 */
	public S_BlueMessage(final int type, final String msg1) {
		this.buildPacket(type, new String[]{ msg1 });
	}

	/**
	 * 畫面中藍色訊息
	 * @param type
	 * @param msg1
	 * @param msg2
	 */
	public S_BlueMessage(final int type, final String msg1, final String msg2) {
		this.buildPacket(type, new String[]{ msg1, msg2 });
	}

	/**
	 * 畫面中藍色訊息
	 * @param type
	 * @param info
	 */
	public S_BlueMessage(final int type, final String[] info) {
		this.buildPacket(type, info);
	}

	private void buildPacket(final int type, final String[] info) {
		System.out.println("未知封包：" + this.getType());
		this.writeC(125);
//		this.writeH(type);
		if (info == null) {
			this.writeC(0x00);

		} else {
			this.writeC(info.length);
			for (int i = 0 ; i < info.length ; i++) {
				this.writeS(info[i]);
			}
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
