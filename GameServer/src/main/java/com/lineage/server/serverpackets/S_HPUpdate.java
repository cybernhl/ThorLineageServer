package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.RangeInt;

/**
 * HP更新顯示<br>
 * 類名稱：S_HPUpdate<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午2:00:21<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_HPUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	private static final RangeInt _hpRange = new RangeInt(1, 65535);

	/**
	 * HP更新顯示
	 * @param currentHp
	 * @param maxHp
	 */
	public S_HPUpdate(final int currentHp, final int maxHp) {
		this.buildPacket(currentHp, maxHp);
	}

	/**
	 * HP更新顯示
	 * @param pc
	 */
	public S_HPUpdate(final L1PcInstance pc) {
		this.buildPacket(pc.getCurrentHp(), pc.getMaxHp());
	}

	public void buildPacket(final int currentHp, final int maxHp) {
		this.writeC(S_OPCODE_HPUPDATE);
		this.writeH(_hpRange.ensure(currentHp));
		this.writeH(_hpRange.ensure(maxHp));
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
