package com.lineage.server.serverpackets;

/**
 * 角色皇冠<br>
 * 類名稱：S_CastleMaster<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:51:22<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CastleMaster extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色皇冠
	 * @param type 城堡編號
	 * @param objecId 人物OBJID
	 */
	public S_CastleMaster(final int type, final int objecId) {
		this.buildPacket(type, objecId);
	}

	private void buildPacket(final int type, final int objecId) {
		this.writeC(S_OPCODE_CASTLEMASTER);
		this.writeC(type);
		this.writeD(objecId);
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
