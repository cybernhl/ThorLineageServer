package com.lineage.server.serverpackets;

/**
 * 物件動作種類(短時間)<br>
 * 類名稱：S_DoActionGFX<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:55:35<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_DoActionGFX extends ServerBasePacket {

	public static int ACTION_MAGIC = 0x16;

	private byte[] _byte = null;

	/**
	 * 物件動作種類(短時間)
	 * @param objectId
	 * @param actionId
	 */
	public S_DoActionGFX(final int objectId, final int actionId) {
		this.writeC(S_OPCODE_DOACTIONGFX);
		this.writeD(objectId);
		this.writeC(actionId);
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
