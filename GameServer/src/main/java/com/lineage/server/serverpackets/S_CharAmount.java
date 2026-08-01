package com.lineage.server.serverpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;

/**
 * 角色列表<br>
 * 類名稱：S_CharAmount<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:53:16<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CharAmount extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色列表
	 * @param value 已創人物數量
	 * @param client
	 */
	public S_CharAmount(final int value, final ClientExecutor client) {
		this.buildPacket(value, client);
	}

	private void buildPacket(final int value, final ClientExecutor client) {
		final int characterSlot = client.getAccount().get_character_slot();
		
		final int maxAmount = ConfigAlt.DEFAULT_CHARACTER_SLOT + characterSlot;

		this.writeC(S_OPCODE_CHARAMOUNT);
		this.writeC(value);// 已創人物數量
		this.writeC(maxAmount); // max amount
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
