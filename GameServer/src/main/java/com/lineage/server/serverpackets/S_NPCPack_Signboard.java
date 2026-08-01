package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1SignboardInstance;

/**
 * 物件封包 - 告示
 * @author dexc
 *
 */
public class S_NPCPack_Signboard extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件封包 - 告示
	 * @param signboard
	 */
	public S_NPCPack_Signboard(final L1SignboardInstance signboard) {
		this.writeC(S_OPCODE_CHARPACK);
		this.writeH(signboard.getX()); // X
		this.writeH(signboard.getY()); // Y
		this.writeD(signboard.getId()); // OBJID
		this.writeH(signboard.getGfxId()); // GFXID
		this.writeC(0x00); // 物件外觀屬性
		this.writeC(this.getDirection(signboard.getHeading())); // 方向
		this.writeC(0x00); // 亮度 0:normal, 1:fast, 2:slow
		this.writeC(0x00); // 速度
		this.writeD(0x00000000); // 數量, 經驗值
		this.writeH(0x0000); // 正義質
		//this.writeS(null); // 名稱
		this.writeS(signboard.getNameId() + ":" + signboard.getNpcId() + " GFX:" + signboard.getGfxId());
		this.writeS(signboard.getName()); // 封號
		this.writeC(0x00); // 狀態
		this.writeD(0x00000000); // 血盟OBJID
		this.writeS(null); // 血盟名稱
		this.writeS(null); // 主人名稱
		
		// 0:NPC,道具 
		// 1:中毒 ,
		// 2:隱身 
		// 4:人物
		// 8:詛咒 
		// 16:勇水
		// 32:??
		// 64:??(??)
		// 128:invisible but name
		this.writeC(0x00); // 物件分類
		
		this.writeC(0xFF); // HP顯示
		this.writeC(0x00); // 距離(通)
		this.writeC(0x00); // LV
		this.writeC(0x00);
		this.writeC(0xFF);
		this.writeC(0xFF);
	}

	private int getDirection(final int heading) {
		int dir = 0;
		switch (heading) {
		case 2:
			dir = 1;
			break;
		case 3:
			dir = 2;
			break;
		case 4:
			dir = 3;
			break;
		case 6:
			dir = 4;
			break;
		case 7:
			dir = 5;
			break;
		}
		return dir;
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
