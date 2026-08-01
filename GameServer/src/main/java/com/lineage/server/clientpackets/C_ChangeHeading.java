package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;

/**
 * 要求改變角色面向
 *
 * @author daien
 *
 */
public class C_ChangeHeading extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChangeHeading.class);

	public C_ChangeHeading(final byte[] decrypt, final ClientExecutor client) {
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

			final int heading = this.readC();

			pc.setHeading(heading);

			// _log.finest("Change Heading : " + pc.getHeading());

			if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
				pc.broadcastPacketAll(new S_ChangeHeading(pc));
			}
			
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
			
		} finally {
			this.over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}