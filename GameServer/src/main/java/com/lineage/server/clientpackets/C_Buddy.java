package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Buddy;

/**
 * 要求查詢朋友名單
 *
 * @author daien
 *
 */
public class C_Buddy extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Buddy.class);

	public C_Buddy(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			//this.read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			pc.sendPackets(new S_Buddy(pc.getId()));
			
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
