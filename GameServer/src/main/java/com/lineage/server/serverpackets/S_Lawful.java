package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

/**
 * 更新正義值<br>
 * 類名稱：S_Lawful<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午10:31:36<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Lawful extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新正義值
	 * @param pc
	 */
	public S_Lawful(L1Character pc) {
		this.buildPacket(pc);
	}

	private void buildPacket(L1Character pc) {
		this.writeC(S_OPCODE_LAWFUL);
		this.writeD(pc.getId());
		this.writeD(pc.getLawful());
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