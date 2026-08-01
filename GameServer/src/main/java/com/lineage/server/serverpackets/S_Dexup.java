package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法效果:敏捷提升<br>
 * 類名稱：S_Dexup<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:05:24<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Dexup extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:敏捷提升
	 * @param pc 原始值
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_Dexup(final L1PcInstance pc, final int type, final int time) {
		this.writeC(S_OPCODE_DEXUP);
		this.writeH(time);
		this.writeC(pc.getDex());
		this.writeC(type);
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
