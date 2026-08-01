package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.Teleportation;

/**
 * 座標異常重整
 * @author dexc
 *
 */
public class S_Lock extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 座標異常重整
	 * @param type
	 * @param equipped
	 */
	public S_Lock(final L1PcInstance pc) {
		this.buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
        //pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        pc.setTeleportX(pc.getOleLocX());
        pc.setTeleportY(pc.getOleLocY());
        pc.setTeleportMapId(pc.getMapId());
        pc.setTeleportHeading(pc.getHeading());
        // 傳送回原始座標
        System.out.println("===傳送回原始座標sa===");
        Teleportation.teleportation(pc);
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
