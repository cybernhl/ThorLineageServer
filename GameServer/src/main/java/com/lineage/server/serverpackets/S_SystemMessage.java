package com.lineage.server.serverpackets;

/**
 * <br>
 * 類名稱：S_SystemMessage<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:34:39<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SystemMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 存在表示。
	 * nameid($xxx)含場合一方使用。
	 *
	 * @param msg
	 *            - 表示文字列
	 */
	public S_SystemMessage(final String msg) {
		this.writeC(S_OPCODE_GLOBALCHAT);
		this.writeC(0x09);
		this.writeS(msg);
	}

	/**
	 * 存在表示。
	 *
	 * @param msg
	 *            - 表示文字列
	 * @param nameid
	 *            - 文字列nameid($xxx)含場合true。
	 */
	public S_SystemMessage(final String msg, final boolean nameid) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(2);
		this.writeD(0);
		this.writeS(msg);
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
