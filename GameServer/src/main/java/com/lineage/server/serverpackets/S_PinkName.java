package com.lineage.server.serverpackets;

/**
 * 角色名稱變紫色<br>
 * 類名稱：S_PinkName<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午12:45:30<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_PinkName extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色名稱變紫色
	 * @param objecId
	 * @param time
	 */
	public S_PinkName(final int objecId, final int time) {
		this.writeC(S_OPCODE_PINKNAME);
		this.writeD(objecId);
		this.writeH(time);
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
