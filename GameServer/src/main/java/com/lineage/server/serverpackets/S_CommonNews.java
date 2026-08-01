package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.list.Announcements;

/**
 * 公告視窗(帳號登入後)<br>
 * 類名稱：S_CommonNews<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午11:06:00<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CommonNews extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 公告視窗
	 */
	public S_CommonNews() {
		final ArrayList<String> info = Announcements.get().list();
		this.writeC(S_OPCODE_COMMONNEWS);
		final StringBuilder messagePack = new StringBuilder();
		for (final String message : info) {
			messagePack.append(message + "\n");
		}
		this.writeS(messagePack.toString());
	}

	/**
	 * 公告視窗(帳號登入後)
	 * @param s
	 */
	public S_CommonNews(final String s) {
		writeC(S_OPCODE_COMMONNEWS);
		writeS(s);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
