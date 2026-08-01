package com.lineage.server.serverpackets;

/**
 * 更新目前遊戲時間<br>
 * 類名稱：S_GameTime<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:59:55<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_GameTime extends ServerBasePacket {

	public S_GameTime(final int time) {
		this.writeC(S_OPCODE_GAMETIME);
		this.writeD(time);
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
	}
}
