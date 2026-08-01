package com.lineage.server.serverpackets;

import com.lineage.server.templates.L1EmblemIcon;

/**
 * 角色盟徽<br>
 * 類名稱：S_Emblem<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午10:54:31<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Emblem extends ServerBasePacket {

	private byte[] _byte = null;

	public S_Emblem(final L1EmblemIcon emblemIcon) {
		this.writeC(S_OPCODE_EMBLEM);
		this.writeD(emblemIcon.get_clanid());
		this.writeByte(emblemIcon.get_clanIcon());
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
