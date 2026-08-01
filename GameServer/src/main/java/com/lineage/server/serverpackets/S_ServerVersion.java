package com.lineage.server.serverpackets;

import com.lineage.config.Config;

/**
 * 服務器版本<br>
 * 類名稱：S_ServerVersion<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午2:03:35<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ServerVersion extends ServerBasePacket {

	private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

	private byte[] _byte = null;

	/**
	 * 伺服器版本
	 */
	public S_ServerVersion() {
		writeC(OpcodesServer.S_OPCODE_SERVERVERSION);
		writeC(0x00); // 允許登入
		// 是否允許PVP
		writeC(0x02); // low version
		writeD(Config.SVer); // serverver
		writeD(Config.CVer); // cache version
		writeD(Config.AVer); // auth ver
		writeD(Config.NVer); // npc ver
		writeD(Config.Time); // uptime
		writeC(0x00); // unk 1 [ 0x01:遊戲上，會員註冊進行。 ]
		writeC(0x00); // unk 2
		writeC(CLIENT_LANGUAGE); // language korean
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
