package com.lineage.server.serverpackets;

/**
 * 血盟小屋地圖(地點)<br>
 * 類名稱：S_HouseMap<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午11:39:42<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_HouseMap extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 血盟小屋地圖(地點)
	 * @param objectId
	 * @param house_number
	 */
	public S_HouseMap(final int objectId, final String house_number) {
		this.buildPacket(objectId, house_number);
	}

	private void buildPacket(final int objectId, final String house_number) {
		final int number = Integer.valueOf(house_number);

		this.writeC(S_OPCODE_HOUSEMAP);
		this.writeD(objectId);
		this.writeD(number);
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
