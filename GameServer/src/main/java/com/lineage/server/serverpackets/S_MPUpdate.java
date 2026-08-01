package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.RangeInt;

/**
 * MP更新顯示<br>
 * 類名稱：S_MPUpdate<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月10日 下午11:51:12<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_MPUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	private static final RangeInt _mpRangeA = new RangeInt(0, 32767);

	private static final RangeInt _mpRangeX = new RangeInt(1, 32767);

	/**
	 * MP更新顯示
	 * @param currentmp
	 * @param maxmp
	 */
	public S_MPUpdate(final int currentmp, final int maxmp) {
		this.buildPacket(currentmp, maxmp);
	}

	/**
	 * MP更新顯示
	 * @param pc
	 */
	public S_MPUpdate(final L1PcInstance pc) {
		this.buildPacket(pc.getCurrentMp(), pc.getMaxMp());
	}
	
	/**
	 * MP更新顯示
	 * @param currentmp
	 * @param maxmp
	 * @return 
	 */
	private void buildPacket(final int currentmp, final int maxmp) {
		this.writeC(S_OPCODE_MPUPDATE);
		this.writeH(_mpRangeA.ensure(currentmp));
		this.writeH(_mpRangeX.ensure(maxmp));
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
