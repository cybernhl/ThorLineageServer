package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 更新經驗值<br>
 * 類名稱：S_Exp<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:57:01<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Exp extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新經驗值
	 *
	 * @param pc
	 */
	public S_Exp(final L1PcInstance pc) {
		this.writeC(S_OPCODE_EXP);
		this.writeC(pc.getLevel());
		this.writeExp(pc.getExp());
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
