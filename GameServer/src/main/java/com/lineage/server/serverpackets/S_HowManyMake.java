package com.lineage.server.serverpackets;

/**
 * 選取物品數量(NPC道具交換數量)<br>
 * 類名稱：S_HowManyMake<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午11:53:05<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_HowManyMake extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量
	 * (NPC道具交換-附加HTML)
	 * @param objId
	 * @param max
	 * @param htmlId
	 */
	public S_HowManyMake(final int objId, final int max, final String htmlId) {
		this.writeC(S_OPCODE_INPUTAMOUNT);
		this.writeD(objId);
		this.writeD(0x00000000);// ?
		this.writeD(0x00000000);// 數量初始質
		this.writeD(0x00000000);// 最低可換數量
		this.writeD(max);// 最高可換數量
		this.writeH(0x0000);// ?
		this.writeS("request");// HTML
		this.writeS(htmlId);// 命令
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
