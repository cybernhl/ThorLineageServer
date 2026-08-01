package com.lineage.server.serverpackets;

import java.util.Random;

/**
 * 戒指(傳戒、夜視頭、召戒)<br>
 * 類名稱：S_Ability<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:49:30<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CreateDice extends ServerBasePacket {
	private byte[] _byte = null;
	private int type;
	public static final Random _random = new Random();
	public static int rand(final int lbound, final int ubound) {
		return (int) ((_random.nextDouble() * (ubound - lbound + 1)) + lbound);
	}
	public S_CreateDice(final int type) {
		this.type = type;
		this.buildPacket();
	}

	private void buildPacket() {
		this.writeC(S_OPCODE_OWNCHARSTATUS2);
		int randomCount = 0;
		final int[] states = new int[6];
		switch (type) {
			case 0: // 王族
				randomCount = 8;
				states[0] = 13;
				states[1] = 10;
				states[2] = 10;
				states[3] = 11;
				states[4] = 13;
				states[5] = 10;
				break;
			case 1: // 騎士
				randomCount = 4;
				states[0] = 16;
				states[1] = 12;
				states[2] = 14;
				states[3] = 9;
				states[4] = 12;
				states[5] = 8;
				break;
			case 2: // 妖精
				randomCount = 7;
				states[0] = 11;
				states[1] = 12;
				states[2] = 12;
				states[3] = 12;
				states[4] = 9;
				states[5] = 12;
				break;
			case 3: // 法師
				randomCount = 16;
				states[0] = 8;
				states[1] = 7;
				states[2] = 12;
				states[3] = 12;
				states[4] = 8;
				states[5] = 12;
				break;
			default:
				return;
		}
		while (randomCount > 0) {
			int i = rand(0, states.length - 1);
			int value = rand(0, randomCount);
			if (i == 0 && (states[i] + value) > 20) {
				continue;
			}
			if (i > 0 && (states[i] + value) > 18) {
				continue;
			}
			states[i] += value;
			randomCount -= value;
		}
		this.writeC(states[0]);
		this.writeC(states[1]);
		this.writeC(states[2]);
		this.writeC(states[3]);
		this.writeC(states[4]);
		this.writeC(states[5]);
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
