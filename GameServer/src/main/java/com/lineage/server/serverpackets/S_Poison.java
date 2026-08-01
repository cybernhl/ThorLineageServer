package com.lineage.server.serverpackets;

/**
 * 魔法效果:毒素<br>
 * 類名稱：S_Poison<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午4:32:09<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Poison extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:毒素
	 *
	 * @param objId
	 *            外見變ID
	 * @param type
	 *            外見 0 = 通常色, 1 = 綠色, 2 = 灰色
	 */
	public S_Poison(final int objId, final int type) {
		this.writeC(S_OPCODE_POISON);
		this.writeD(objId);
		switch (type) {
		case 0: // 通常
			this.writeC(0x00);
			this.writeC(0x00);
			break;
			
		case 1: // 綠色
			this.writeC(0x01);
			this.writeC(0x00);
			break;
			
		case 2: // 灰色
			this.writeC(0x00);
			this.writeC(0x01);
			break;
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
