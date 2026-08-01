package com.lineage.server.serverpackets;

/**
 * 血盟成員清單<br>
 * 類名稱：S_Pledge<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午10:33:33<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_Pledge extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 血盟成員清單(盟主查詢)
	 * @param string
	 * @param objid
	 * @param clanName 血盟的名稱
	 * @param olmembers 在線成員名稱
	 * @param allmembers 全部成員名稱
	 */
	public S_Pledge(final int objid, final String clanName,
			final StringBuilder olmembers,
			final StringBuilder allmembers) {
		this.buildPacket(objid, clanName, olmembers, allmembers);
	}

	private void buildPacket(final int objid,
			final String clanname, 
			final StringBuilder olmembers, 
			final StringBuilder allmembers) {

		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS("pledgeM");
		this.writeC(0);
		this.writeH(0x03);
		this.writeS(clanname);// 血盟的名稱
		this.writeS(olmembers.toString());// 在線成員名稱
		this.writeS(allmembers.toString());// 全部成員名稱
	}

	/**
	 * 血盟成員清單(成員查詢)
	 * @param string
	 * @param objid
	 * @param clanName 血盟的名稱
	 * @param olmembers 在線成員名稱
	 */
	public S_Pledge(final int objid, final String clanName,
			final StringBuilder olmembers) {
		this.buildPacket(objid, clanName, olmembers);
	}

	private void buildPacket(final int objid,
			final String clanname, 
			final StringBuilder olmembers) {

		this.writeC(S_OPCODE_SHOWHTML);
		this.writeD(objid);
		this.writeS("pledge");
		this.writeC(0);
		this.writeH(0x02);
		this.writeS(clanname);// 血盟的名稱
		this.writeS(olmembers.toString());// 在線成員名稱
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
