package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件面向<br>
 * 類名稱：S_ChangeHeading<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:51:56<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChangeHeading extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件面向
	 * @param cha
	 */
	public S_ChangeHeading(final L1Character cha) {
		this.buildPacket(cha);
	}

	private void buildPacket(final L1Character cha) {
		this.writeC(S_OPCODE_CHANGEHEADING);
		this.writeD(cha.getId());
		this.writeC(cha.getHeading());
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
