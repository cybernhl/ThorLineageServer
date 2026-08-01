package com.lineage.server.serverpackets;

/**
 * 確認創建角色(一些判斷提示)<br>
 * 類名稱：S_CharCreateStatus<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午2:17:56<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CharCreateStatus extends ServerBasePacket {

	private byte[] _byte = null;

	/**創建角色成功*/
	public static final int REASON_OK = 0x02;

	/**已經有同樣的角色名稱。請重新輸入。*/
	public static final int REASON_ALREADY_EXSISTS = 0x06;

	/**無效的名字。若您擅自修改，將無法進行遊戲。*/
	public static final int REASON_INVALID_NAME = 0x09;

	/**超過初始可建立人物數量*/
	public static final int REASON_WRONG_AMOUNT = 0x15;

	/**
	 * 角色創造結果
	 * @param reason
	 */
	public S_CharCreateStatus(final int reason) {
		this.writeC(S_OPCODE_LOGINRESULT);
		this.writeC(reason);
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
