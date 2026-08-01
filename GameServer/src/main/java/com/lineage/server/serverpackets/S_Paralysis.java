package com.lineage.server.serverpackets;

/**
 * 魔法效果:詛咒<br>
 * 類名稱：S_Paralysis<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月4日 下午12:52:40<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Paralysis extends ServerBasePacket {

	private byte[] _byte = null;

	/**你的身體完全麻痺了*/
	public static final int TYPE_PARALYSIS = 0x01;// 你的身體完全麻痺了

	/**你的身體完全麻痺了*/
	public static final int TYPE_PARALYSIS2 = 0x02;// 你的身體完全麻痺了

	/**睡眠狀態*/
	public static final int TYPE_SLEEP = 0x03;// 睡眠狀態

	/**凍結狀態*/
	public static final int TYPE_FREEZE = 0x04;// 凍結狀態

	/**衝擊之暈*/
	public static final int TYPE_STUN = 0x05;// 衝擊之暈

	/**雙腳被困*/
	public static final int TYPE_BIND = 0x06;// 雙腳被困

	/**解除傳送鎖定*/
	public static final int TYPE_TELEPORT_UNLOCK = 0x07;// 解除傳送鎖定

	/**
	 * 魔法效果:詛咒
	 * @param type
	 * @param flag
	 */
	public S_Paralysis(final int type, final boolean flag) {
		this.writeC(S_OPCODE_PARALYSIS);
		switch (type) {
		case TYPE_PARALYSIS: // 你的身體完全麻痺了
			if (flag == true) {
				this.writeC(0x02);
			} else {
				this.writeC(0x03);
			}
			break;

		case TYPE_PARALYSIS2: // 你的身體完全麻痺了
			if (flag == true) {
				this.writeC(0x04);
			} else {
				this.writeC(0x05);
			}
			break;

		case TYPE_TELEPORT_UNLOCK: // 傳送鎖定解除
			if (flag == true) {
				this.writeC(0x06);
			} else {
				this.writeC(0x07);
			}
			break;

		case TYPE_SLEEP: // 睡眠狀態
			if (flag == true) {
				this.writeC(0x0a);// this.writeC(10);
			} else {
				this.writeC(0x0b);// this.writeC(11);
			}
			break;

		case TYPE_FREEZE: // 凍結狀態
			if (flag == true) {
				this.writeC(0x0c);// this.writeC(12);
			} else {
				this.writeC(0x0d);// this.writeC(13);
			}
			break;

		case TYPE_STUN: // 衝擊之暈
			if (flag == true) {
				this.writeC(0x04);
			} else {
				this.writeC(0x05);
			}
			break;

		case TYPE_BIND: // 雙腳被困
			if (flag == true) {
				this.writeC(0x18);// this.writeC(24);
			} else {
				this.writeC(0x19);// this.writeC(25);
			}
			break;
		}
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
