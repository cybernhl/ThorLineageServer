package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Lock;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.Teleportation;

/**
 * 要求座標異常重整
 * @author dexc
 *
 */
public class C_UnLock extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_UnLock.class);

	public C_UnLock(byte[] data, ClientExecutor client) {
		try {
/*			// 資料載入
			read(decrypt);
			//int i = 0;
			int type = readC();*/
			//System.out.println("要求座標異常重整:"+type);
			final L1PcInstance pc = client.getActiveChar();
			Teleportation.teleportation(pc);
			
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
