package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法效果:力量提升<br>
 * 類名稱：S_Strup<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:04:27<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Strup extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:力量提升
	 * @param pc 執行者
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_Strup(final L1PcInstance pc, final int type, final int time) {
		this.writeC(S_OPCODE_STRUP);
		this.writeH(time);
		this.writeC(pc.getStr());
		this.writeC(pc.getInventory().getWeight182());
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
