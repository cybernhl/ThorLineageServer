package com.lineage.server.serverpackets;

/**
 * 選取物品數量(賣出小屋)<br>
 * 類名稱：S_SellHouse<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午11:58:18<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_SellHouse extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量
	 * (賣出小屋)
	 * @param objectId
	 * @param houseNumber
	 */
	public S_SellHouse(final int objectId, final String houseNumber) {
		this.buildPacket(objectId, houseNumber);
	}

	private void buildPacket(final int objectId, final String houseNumber) {
		this.writeC(S_OPCODE_INPUTAMOUNT);
		this.writeD(objectId);
		this.writeD(0); // ?
		this.writeD(100000);// 數量初始質
		this.writeD(100000);// 最低可換數量
		this.writeD(2000000000);// 最高可換數量
		this.writeH(0); // ?
		this.writeS("agsell");// HTML
		this.writeS("agsell " + houseNumber);// 命令
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
