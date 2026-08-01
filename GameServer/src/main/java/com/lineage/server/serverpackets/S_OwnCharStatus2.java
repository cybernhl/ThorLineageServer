package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 角色狀態<br>
 * 人物遊戲畫面人物資料視窗顯示用
 * @author dexc
 *
 */
public class S_OwnCharStatus2 extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色狀態
	 * @param pc
	 */
	public S_OwnCharStatus2(final L1PcInstance pc) {
		if (pc == null) {
			return;
		}
		this.buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		System.out.println("未知封包：" + this.getType());
		this.writeC(S_OPCODE_OWNCHARSTATUS2);
		this.writeC(pc.getStr());
		this.writeC(pc.getInt());
		this.writeC(pc.getWis());
		this.writeC(pc.getDex());
		this.writeC(pc.getCon());
		this.writeC(pc.getCha());
		this.writeC(pc.getInventory().getWeight182());
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
