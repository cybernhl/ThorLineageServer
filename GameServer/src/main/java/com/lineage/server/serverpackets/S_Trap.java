package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1TrapInstance;

/**
 * 物件封包 - 陷阱(GM探查用)
 * @author DaiEn
 *
 */
public class S_Trap extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物件封包 - 陷阱(GM探查用)
	 * @param trap
	 * @param name
	 */
	public S_Trap(final L1TrapInstance trap, final String name) {
		this.writeC(S_OPCODE_CHARPACK);
		this.writeH(trap.getX()); // X
		this.writeH(trap.getY()); // Y
		this.writeD(trap.getId()); // OBJID
		this.writeH(0x0007); // GFXID
		this.writeC(0x00); // 物件外觀屬性
		this.writeC(0x00); // 方向
		this.writeC(0x00); // 亮度 0:normal, 1:fast, 2:slow
		this.writeC(0x00); // 速度
		this.writeD(0x00000000); // 數量, 經驗值
		this.writeH(0x0000); // 正義質
		this.writeS(name); // 名稱
		this.writeS(null); // 封號
		this.writeC(0x00); // 狀態
		this.writeD(0x00000000); // 血盟OBJID
		this.writeS(null); // 血盟名稱
		this.writeS(null); // 主人名稱
		this.writeC(0x00); // 物件分類
		this.writeC(0xFF); // HP顯示
		this.writeC(0x00); // 距離(通)
		this.writeC(0x00); // LV
		this.writeC(0x00);
		this.writeC(0xFF);
		this.writeC(0xFF);
		
		/*this.writeC(S_OPCODE_CHARPACK);
		this.writeH(trap.getX());
		this.writeH(trap.getY());
		this.writeD(trap.getId());
		this.writeH(0x07); // adena
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeD(0x00);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeS(name);
		this.writeC(0x00);
		this.writeD(0x00);
		this.writeD(0x00);
		this.writeC(255);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeC(0x00);
		this.writeH(65535);
		// writeD(0x401799a);
		this.writeD(0);
		this.writeC(8);
		this.writeC(0);*/
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
