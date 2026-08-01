package com.lineage.server.clientpackets;

import com.lineage.server.serverpackets.S_CreateDice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;

/**
 * 要求進入角色創建界面<br>
 * 類名稱：C_NewCharWin<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月30日 下午4:10:14<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class C_NewCharWin extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_NewCharWin.class);

	public C_NewCharWin(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			// 0:王族  1:騎士  2:妖精  3:法師
			int type = this.readC();
			client.toSender(new S_CreateDice(type));
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
