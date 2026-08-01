package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件外型改變<br>
 * 類名稱：S_ChangeShape<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午1:05:08<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChangeShape extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件外型改變
	 * @param obj
	 * @param polyId
	 */
	public S_ChangeShape(final L1Character obj, final int polyId) {
		this.buildPacket(obj, polyId, false);
	}

	/**
	 * 物件外型改變
	 * @param obj
	 * @param polyId
	 * @param weaponTakeoff
	 */
	public S_ChangeShape(final L1Character obj, final int polyId, final boolean weaponTakeoff) {
		this.buildPacket(obj, polyId, weaponTakeoff);
	}

	private void buildPacket(final L1Character obj, final int polyId, final boolean weaponTakeoff) {
		this.writeC(S_OPCODE_POLY);
		this.writeD(obj.getId());
		this.writeH(polyId);
		// 何故29不明
		this.writeC(weaponTakeoff ? 0 : 29);
		this.writeC(0xFF);
		this.writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}
}
