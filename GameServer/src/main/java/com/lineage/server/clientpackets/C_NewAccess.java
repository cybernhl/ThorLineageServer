package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.serverpackets.S_LoginResult;
import com.lineage.server.templates.L1Account;

/**
 * 要求新增帳號
 *
 * @author daien
 *
 */
public class C_NewAccess extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_NewAccess.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			String loginName = this.readS();// USER NAME-1259300886982551650
			String password = this.readS();// PASS
			
			final StringBuilder ip = client.getIp();
			StringBuilder mac = client.getMac();

			if (mac == null) {
				mac = ip;
			}
			
			_log.info("E-MAIL: " + this.readS());
			String spwd = this.readS();// S.S.NO

			_log.info("TYPE: " + this.readC());
			_log.info("ADDRESS: " + this.readS());
			_log.info("PHONE: " + this.readS());
			_log.info("FAX: " + this.readS());

			L1Account account = AccountReading.get().getAccount(loginName);

			if (account == null) {
				account = AccountReading.get().create(
						loginName, 
						password, 
						ip.toString(), 
						mac.toString(), 
						spwd
						);
				client.toSender(new S_LoginResult(S_LoginResult.EVENT_RE_LOGIN));
			}
			client.toSender(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_ALREADY_EXISTS));
			
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
