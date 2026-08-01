package com.lineage.server.serverpackets;

import java.util.Calendar;

import com.lineage.config.Config;

/**
 * 圍城時間設定
 * @author dexc
 *
 */
public class S_WarTime extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 圍城時間設定
	 * @param cal
	 */
	public S_WarTime(final Calendar cal) {
		// 1997/01/01 17:00基點
		final Calendar base_cal = Calendar.getInstance();
		base_cal.set(1997, 0, 1, 17, 0);
		final long base_millis = base_cal.getTimeInMillis();
		final long millis = cal.getTimeInMillis();
		long diff = millis - base_millis;
		diff -= 1200 * 60 * 1000; // 誤差修正
		diff = diff / 60000; // 分以下切捨
		// time1加算3:02（182分）進
		final int time = (int) (diff / 182);

		// writeD直前writeC時間調節
		// 0.7倍時間縮
		// 1調整次時間廣？
		this.writeC(S_OPCODE_WARTIME);
		this.writeH(0x0006); // 數（6以上無效）
		this.writeS(Config.TIME_ZONE); // 時間後（）內表示文字列
		this.writeC(0x00); // ?
		this.writeC(0x00); // ?
		this.writeC(0x00);
		this.writeD(time);
		this.writeC(0x00);
		this.writeD(time - 1);
		this.writeC(0x00);
		this.writeD(time - 2);
		this.writeC(0x00);
		this.writeD(time - 3);
		this.writeC(0x00);
		this.writeD(time - 4);
		this.writeC(0x00);
		this.writeD(time - 5);
		this.writeC(0x00);
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
