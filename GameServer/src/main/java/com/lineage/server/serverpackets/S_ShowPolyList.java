package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 變身清單(變形魔杖、變形術)<br>
 * 類名稱：S_ShowPolyList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午10:39:44<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ShowPolyList extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * NPC對話視窗(變身清單)
	 * @param objid
	 */
	public S_ShowPolyList(final int objid) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS("monlist");
	}
	
	/**
	 * NPC對話視窗(變身清單)
	 * @param target
	 */
	public S_ShowPolyList(final L1Character target) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(target.getId());
		this.writeS("monlist");
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
