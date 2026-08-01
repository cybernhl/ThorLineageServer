package com.lineage.server.serverpackets;

/**
 * 角色座標名單<br>
 * 類名稱：S_Bookmarks<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午4:42:39<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Bookmarks extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色座標名單
	 * @param name
	 * @param map
	 * @param id
	 */
	public S_Bookmarks(final String name, final int map, final int id) {
		this.buildPacket(name, map, id);
	}

	private void buildPacket(final String name, final int map, final int id) {
		this.writeC(S_OPCODE_BOOKMARKS);
		this.writeS(name);
		this.writeH(map);
		this.writeD(id);
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