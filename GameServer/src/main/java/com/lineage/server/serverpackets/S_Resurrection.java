package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件復活<br>
 * 類名稱：S_Resurrection<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月2日 下午5:44:59<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Resurrection extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件復活
	 * @param target 被復活的人物
	 * @param use 使用復活的人物
	 * @param type
	 */
	public S_Resurrection(final L1PcInstance target, final L1Character use, final int type) {
		this.writeC(S_OPCODE_RESURRECTION);
		this.writeD(target.getId());// 被復活的對象
		this.writeC(type);
		this.writeD(use.getId());// 使用復活的人物
		this.writeD(target.getClassId());
	}

	/**
	 * 物件復活
	 * @param target 被復活的對象
	 * @param use 使用復活的對象
	 * @param type
	 */
	public S_Resurrection(final L1Character target, final L1Character use, final int type) {
		this.writeC(S_OPCODE_RESURRECTION);
		this.writeD(target.getId());// 被復活的對象
		this.writeC(type);
		this.writeD(use.getId());// 使用復活的人物
		this.writeH(target.getGfxId());
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
