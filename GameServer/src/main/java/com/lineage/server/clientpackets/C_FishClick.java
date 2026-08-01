package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.timecontroller.pc.PcFishingTimer;

/**
 * 要求取消釣魚
 * @author daien
 *
 */
public class C_FishClick extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_FishClick.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			//this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			PcFishingTimer.finishFishing(pc, false);

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
