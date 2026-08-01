package com.lineage.server.serverpackets;

/**
 * 遊戲天氣<br>
 * 1~3雪<br>
 * 17~19雨<br>
 * 類名稱：S_Weather<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午2:04:48<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Weather extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 遊戲天氣
	 * @param weather
	 */
	public S_Weather(final int weather) {
		this.buildPacket(weather);
	}

	private void buildPacket(final int weather) {
		this.writeC(S_OPCODE_WEATHER);
		this.writeC(weather);
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
