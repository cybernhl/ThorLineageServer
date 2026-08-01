package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.FaceToFace;

/**
 * 要求邀請加入隊伍(要求創立隊伍)
 *
 * @author daien
 *
 */
public class C_CreateParty extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_CreateParty.class);

	public C_CreateParty(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}
			
			if (pc.isDead()) { // 死亡
				return;
			}
			
			if (pc.isTeleport()) { // 傳送中
				return;
			}
			
			final L1PcInstance targetPc = FaceToFace.faceToFace(pc);
			
			if (targetPc != null) {
				this.CreateParty(pc, targetPc);
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}
	
	public void CreateParty(final L1PcInstance pc, final L1PcInstance targetPc){
		if (targetPc.isInParty()) {
			// 您無法邀請已經參加其他隊伍的人。
			pc.sendPackets(new S_ServerMessage(415));
			return;
		}

		if (pc.isInParty()) {
			if (pc.getParty().isLeader(pc)) {
				targetPc.setPartyID(pc.getId());
				// 玩家 %0%s 邀請您加入隊伍？(Y/N)
				targetPc.sendPackets(new S_Message_YN(422, pc.getName()));

			} else {
				// 只有領導者才能邀請其他的成員。
				pc.sendPackets(new S_ServerMessage(416));
			}

		} else {
			targetPc.setPartyID(pc.getId());
			// 玩家 %0%s 邀請您加入隊伍？(Y/N)
			targetPc.sendPackets(new S_Message_YN(422, pc.getName()));
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
