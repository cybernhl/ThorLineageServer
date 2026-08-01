package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件動作種類(長時間)<br>
 * 類名稱：S_CharVisualUpdate<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月31日 上午11:05:18<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_CharVisualUpdate extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物件動作種類(長時間)
	 * @param objid 物件OBJID
	 * @param weaponType 武器型態代號(TYPE)
	 */
	public S_CharVisualUpdate(final int objid, final int weaponType) {
		this.writeC(S_OPCODE_CHARVISUALUPDATE);
		this.writeD(objid);
		this.writeC(weaponType);
		this.writeC(0xff);
		this.writeC(0xff);
	}

	/**
	 * 物件動作種類(長時間)
	 * @param cha
	 */
	public S_CharVisualUpdate(final L1PcInstance cha) {
		this.writeC(S_OPCODE_CHARVISUALUPDATE);
		this.writeD(cha.getId());
		this.writeC(cha.getCurrentWeapon());
		this.writeC(0xff);
		this.writeC(0xff);
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}
}
