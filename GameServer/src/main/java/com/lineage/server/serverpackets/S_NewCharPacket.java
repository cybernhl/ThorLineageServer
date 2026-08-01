package com.lineage.server.serverpackets;

import java.text.SimpleDateFormat;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 *創造角色
 * @author dexc
 *
 */
public class S_NewCharPacket extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 創造角色
	 * @param pc
	 */
	public S_NewCharPacket(final L1PcInstance pc) {
		this.buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		this.writeC(S_OPCODE_NEWCHARPACK);
		this.writeS(pc.getName());
		this.writeS("");
		this.writeC(pc.getType());
		this.writeC(pc.get_sex());
		this.writeH(pc.getLawful());
		this.writeH(pc.getMaxHp());
		this.writeH(pc.getMaxMp());
		this.writeC(pc.getAc());
		this.writeC(pc.getLevel());
		this.writeC(pc.getStr());
		this.writeC(pc.getDex());
		this.writeC(pc.getCon());
		this.writeC(pc.getWis());
		this.writeC(pc.getCha());
		this.writeC(pc.getInt());
		
		// 大於0為GM權限
		this.writeC(0x00);
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final String times = sdf.format(System.currentTimeMillis());
		int time = Integer.parseInt(times.replace("-", ""));

		this.writeD(time);
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
