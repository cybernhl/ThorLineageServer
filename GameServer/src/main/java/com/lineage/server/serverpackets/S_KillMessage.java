package com.lineage.server.serverpackets;

import java.util.Random;

import com.lineage.config.ConfigKill;

/**
 * 殺人公告<br>
 * 類名稱：S_KillMessage<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午1:33:31<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_KillMessage extends ServerBasePacket {

	private byte[] _byte = null;

	private static final Random _random = new Random();

	/**
	 * 殺人公告
	 * @param winName
	 * @param deathName
	 */
	public S_KillMessage(final String winName, final String deathName) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(0x00);// 顏色
		this.writeD(0x00000000);
		String x1 = ConfigKill.KILL_TEXT_LIST.get(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1);
		this.writeS(String.format(x1, winName, deathName));
	}
	
	/**
	 * 賭場NPC對話
	 * @param winName
	 * @param deathName
	 */
	public S_KillMessage(final String name, final String msg, int i) {
		this.writeC(S_OPCODE_NPCSHOUT);
		this.writeC(0x00);// 顏色
		this.writeD(0x00000000);
		this.writeS(" \\fY[" + name + "] " + msg);
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
