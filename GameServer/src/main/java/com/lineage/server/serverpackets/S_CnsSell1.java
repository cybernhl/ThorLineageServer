package com.lineage.server.serverpackets;

import com.lineage.data.event.ShopXSet;

/**
 * 選取物品數量(賣出價格定義)-->托售管理員專用<br>
 * 類名稱：S_CnsSell<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午10:48:19<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CnsSell1 extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選取物品數量
	 * (賣出價格定義)
	 * @param objectId NPC OBJID
	 * @param htmlid HTML名稱
	 * @param command 命令
	 */
	public S_CnsSell1(final int objectId, final String htmlid, final String command) {
		this.buildPacket(objectId, htmlid, command);
	}

	private void buildPacket(final int objectId, final String htmlid, final String command) {
		this.writeC(S_OPCODE_INPUTAMOUNT);
		this.writeD(objectId);
		this.writeD(0x00000000);// ?
		this.writeD(ShopXSet.MIN);// 數量初始質
		this.writeD(ShopXSet.MIN);// 最低可換數量
		this.writeD(ShopXSet.MAX);// 最高可換數量
		this.writeH(0x0000);// ?
		this.writeS(htmlid);// HTML
		this.writeS(command);// 命令
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
