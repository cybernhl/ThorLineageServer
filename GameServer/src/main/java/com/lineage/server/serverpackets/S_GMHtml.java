package com.lineage.server.serverpackets;

/**
 * 顯示指定HTML<br>
 * 類名稱：S_GMHtml<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午3:25:29<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_GMHtml extends ServerBasePacket {
	
	/**
	 * 顯示指定HTML
	 * @param _objid
	 * @param html
	 */
	public S_GMHtml(final int _objid, final String html) {
		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(_objid);
		this.writeS(html);
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
	}
}
