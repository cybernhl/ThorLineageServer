package com.lineage.server.serverpackets;

/**
 * 交易狀態(完成、取消)<br>
 * 類名稱：S_TradeStatus<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 上午11:13:44<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class S_TradeStatus extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 交易狀態
	 * @param type 0:交易完成 1:交易取消
	 */
	public S_TradeStatus(final int type) {
		this.writeC(S_OPCODE_TRADESTATUS);
		this.writeC(type); // 0:交易完成 1:交易取消
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
