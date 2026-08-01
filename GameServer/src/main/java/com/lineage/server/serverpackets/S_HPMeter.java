package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件血條<br>
 * 類名稱：S_HPMeter<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午12:44:23<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_HPMeter extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件血條
	 * @param objId
	 * @param hpRatio
	 */
	public S_HPMeter(final int objId, final int hpRatio) {
		this.buildPacket(objId, hpRatio);
	}

	/**
	 * 物件血條
	 * @param cha
	 */
	public S_HPMeter(final L1Character cha) {
		final int objId = cha.getId();
		int hpRatio = 100;
		if (0 < cha.getMaxHp()) {
			hpRatio = 100 * cha.getCurrentHp() / cha.getMaxHp();
		}

		this.buildPacket(objId, hpRatio);
	}

	private void buildPacket(final int objId, final int hpRatio) {
		this.writeC(S_OPCODE_HPMETER);
		this.writeD(objId);
		this.writeC(hpRatio);
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
