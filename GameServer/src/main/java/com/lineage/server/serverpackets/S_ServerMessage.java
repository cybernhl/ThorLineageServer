package com.lineage.server.serverpackets;

/**
 * 服務器訊息<br>
 * 類名稱：S_ServerMessage<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:34:23<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ServerMessage extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 服務器訊息(NPC對話)
	 * @param winName
	 * @param color<br>
	 * <font color="#bdaaa5">\\fR <b>顏色範例</b></font><br>
	 * <font color="#739e84">\\fS <b>顏色範例</b></font><br>
	 * <font color="#7b9e7b">\\fT <b>顏色範例</b></font><br>
	 * <font color="#7b9aad">\\fU <b>顏色範例</b></font><br>
	 * <font color="#a59ac6">\\fV <b>顏色範例</b></font><br>
	 * <font color="#ad92b5">\\fW <b>顏色範例</b></font><br>
	 * <font color="#b592ad">\\fX <b>顏色範例</b></font><br>
	 * <font color="#bd9a94">\\fY <b>顏色範例</b></font><br>
	 */
	public S_ServerMessage(final String name) {
		writeC(S_OPCODE_NPCSHOUT);
		writeC(0x00);// 顏色
		writeD(0x00000000);
		writeS(name);
	}

	/**
	 * 服務器訊息
	 * @param type
	 */
	public S_ServerMessage(final int type) {
		this.writeC(S_OPCODE_SERVERMSG);
		this.writeH(type);
		this.writeC(0x00);
	}

	/**
	 * 服務器訊息
	 * @param type
	 * @param msg1
	 */
	public S_ServerMessage(final int type, final String msg1) {
		this.buildPacket(type, new String[]{ msg1 });
	}

	/**
	 * 服務器訊息
	 * @param type
	 * @param msg1
	 * @param msg2
	 */
	public S_ServerMessage(final int type, final String msg1, final String msg2) {
		this.buildPacket(type, new String[]{ msg1, msg2 });
	}

	/**
	 * 服務器訊息
	 * @param type
	 * @param msg1
	 * @param msg2
	 * @param msg3
	 */
	public S_ServerMessage(final int type, final String msg1, final String msg2, final String msg3) {
		this.buildPacket(type, new String[]{ msg1, msg2, msg3 });
	}

	/**
	 * 服務器訊息
	 * @param type
	 * @param msg1
	 * @param msg2
	 * @param msg3
	 * @param msg4
	 */
	/*public S_ServerMessage(final int type, final String msg1, final String msg2, final String msg3, final String msg4) {
		this.buildPacket(type, new String[]{ msg1, msg2, msg3, msg4 });
	}*/

	/**
	 * 服務器訊息
	 * @param type
	 * @param msg1
	 * @param msg2
	 * @param msg3
	 * @param msg4
	 * @param msg5
	 */
	/*public S_ServerMessage(final int type, final String msg1, final String msg2, final String msg3, final String msg4, final String msg5) {
		this.buildPacket(type, new String[]{ msg1, msg2, msg3, msg4, msg5 });
	}*/

	/**
	 * 服務器訊息
	 * @param type
	 * @param info
	 */
	public S_ServerMessage(final int type, final String[] info) {
		this.buildPacket(type, info);
	}

	private void buildPacket(final int type, final String[] info) {
		this.writeC(S_OPCODE_SERVERMSG);
		this.writeH(type);
		
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
