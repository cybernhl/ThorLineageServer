package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件名稱改變<br>
 * 類名稱：S_ChangeName<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:52:36<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_ChangeName extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件名稱改變
	 * @param objectId
	 * @param name
	 */
	public S_ChangeName(final int objectId, final String name) {
		this.writeC(S_OPCODE_CHANGENAME);
		this.writeD(objectId);
		this.writeS(name);
	}

	/**
	 * 物件名稱改變(GM)
	 * @param objectId
	 * @param name
	 * @param mode
	 */
	public S_ChangeName(final int objectId, final String name, final int mode) {
		String color = "";
		switch (mode) {
		case 0:
			color = "\\f=\\f1";
			break;
		case 1:
			color = "\\f=\\f2";
			break;
		case 2:
			color = "\\f=\\f3";
			break;
		case 3:
			color = "\\f=\\f4";
			break;
		case 4:
			color = "\\f=\\f5";
			break;
		case 5:
			color = "\\f=\\f6";
			break;
		case 6:
			color = "\\f=\\f7";
			break;
		case 7:
			color = "\\f=\\f8";
			break;
		case 8:
			color = "\\f=\\f9";
			break;
		case 9:
			color = "\\f=\\f=";
			break;
		case 10:
			color = "\\f=\\f<";
			break;
		}
		this.writeC(S_OPCODE_CHANGENAME);
		this.writeD(objectId);
		this.writeS(color + "GM \\f=" + name);
	}

	/**
	 * 物件名稱改變(顏色)
	 * @param pc 執行人物
	 * @param isName 執行爵位顏色改變 true:執行 false:不執行
	 */
	public S_ChangeName(final L1PcInstance pc, final boolean isName) {
		this.writeC(S_OPCODE_CHANGENAME);
		this.writeD(pc.getId());

		final StringBuilder stringBuilder = new StringBuilder();
		if (isName) {
			if (pc.get_other().get_color() != 0) {
				stringBuilder.append(pc.get_other().color());
			}
		}
		stringBuilder.append(pc.getName());
		this.writeS(stringBuilder.toString());
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
