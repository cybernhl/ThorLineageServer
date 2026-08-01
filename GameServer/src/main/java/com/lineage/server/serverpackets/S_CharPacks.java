package com.lineage.server.serverpackets;

/**
 * 角色資訊<br>
 * 類名稱：S_CharPacks<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 下午1:53:58<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CharPacks extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色資訊
	 * @param name
	 * @param clanName
	 * @param type
	 * @param sex
	 * @param lawful
	 * @param hp
	 * @param mp
	 * @param ac
	 * @param lv
	 * @param str
	 * @param dex
	 * @param con
	 * @param wis
	 * @param cha
	 * @param intel
	 */
	public S_CharPacks(final String name, final String clanName, final int type, final int sex,
			final int lawful, final int hp, final int mp, final int ac, final int lv, final int str, final int dex,
			final int con, final int wis, final int cha, final int intel, final int time) {
		writeC(S_OPCODE_CHARLIST);
		writeS(name);
		writeS(clanName);
		writeC(type);
		writeC(sex);
		writeH(lawful);
		writeH(hp);
		writeH(mp);
		
		if (ac > 0x0a) {// 10
			writeC(0x0a);// 10
			
		} else {
			writeC(ac);
		}
		
		if (lv > 0x7f) {// 127
			writeC(0x7f);// 127
			
		} else {
			writeC(lv);
		}
		
		if (str > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(str);
		}
		if (dex > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(dex);
		}
		if (con > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(con);
		}
		if (wis > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(wis);
		}
		if (cha > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(cha);
		}
		if (intel > 0x7f) {// 127
			writeC(0x7f);// 127
		} else {
			writeC(intel);
		}
		
		// 大於0為GM權限
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
