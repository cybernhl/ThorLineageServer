package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;

/**
 * 自動登入
 * @author dexc未啟用
 *
 */
public class C_AutoLogin extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AutoLogin.class);

	public C_AutoLogin(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			this.read(decrypt);
			
			int un = this.readC();
			_log.info("自動登入系統定位:" + un);

			String loginName = this.readS().toLowerCase();
			String srcpassword = this.readS();

			////C_AuthLogin authLogin = new C_AuthLogin();
			//authLogin.checkLogin(client, loginName, srcpassword, true);
			
			// 測試用
			/*for (int i = 0 ; i < 50 ; i++) {
				final ClientExecutor clientx = new ClientExecutor(client.get_socket());
				C_AuthLogin authLoginx = new C_AuthLogin();
				authLoginx.checkLogin(clientx, "aaaa"+i, "aaaa"+i, true);
			}*/
			
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
