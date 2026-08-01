package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
/*import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SummonPack;
import com.lineage.server.world.World;*/

/**
 * 要求離開遊戲
 *
 * @author daien
 *
 */
public class C_Disconnect extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Disconnect.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {

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
