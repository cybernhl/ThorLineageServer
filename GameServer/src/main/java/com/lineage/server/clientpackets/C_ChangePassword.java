package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.serverpackets.S_CommonNews;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_LoginResult;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 修改密碼(登錄界面)<br>
 * 類名稱：C_ChangePassword<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年10月3日 下午6:13:43<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class C_ChangePassword extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_ChangePassword.class);
	
	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
	
    public C_ChangePassword(byte[] decrypt, ClientExecutor client) throws Exception {
    	try {
            // 資料載入
            this.read(decrypt);
            String accountName = readS().toLowerCase();
            String password = readS().toLowerCase();
            String newPassword = readS().toLowerCase();
            
            if (!ConfigAlt.CHANGPASSWORD) {
            	client.toSender(new S_CommonNews("修改密碼服務未開放."));
            	close(client);
            	return;
            }
            final L1Account account = AccountReading.get().getAccount(accountName);
            
            // 沒有該帳號
            if (account == null) {
                client.set_error(client.get_error() + 1);
                client.toSender(new S_LoginResult(S_LoginResult.EVENT_ERROR_USER));
                return;
            } else {
                // 帳號禁止登入
                if (LanSecurityManager.BANNAMEMAP.containsKey(accountName)) {
                    client.toSender(new S_LoginResult(S_LoginResult.EVENT_CANT_USE));
                    close(client);
                    return;
                }
                if (!password.equalsIgnoreCase(account.get_password())) {
                    client.set_error(client.get_error() + 1);
                    // 錯誤3次踢掉
                    if (client.get_error() < 2) {
                        client.toSender(new S_LoginResult(S_LoginResult.EVENT_PASS_CHECK));
                    }
                    return;
                }

                boolean iserror = false;
                if (newPassword.length() < 8) {
                    iserror = true;
                } else {
                    for (int i = 0; i < newPassword.length(); i++) {
                        final String ch = newPassword.substring(i, i + 1);
                        if (!_check_pwd.contains(ch.toLowerCase())) {
                            iserror = true;
                            break;
                        }
                    }
                }
                if (iserror) {
                    client.set_error(client.get_error() + 1);
                    client.toSender(new S_LoginResult(S_LoginResult.EVENT_NAME_ERROR));
                    return;
                }

                // 更新密碼
                AccountReading.get().updatePwd(account.get_login(), newPassword);
                // 返回登錄界面
                client.toSender(new S_LoginResult(S_LoginResult.EVENT_RE_LOGIN));
            }
            
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }
    
    private void close(ClientExecutor client) {
        final KickTimeController kickTime = new KickTimeController(client, null);
        kickTime.schedule();
    }
    
    /**
     * 斷線延遲軸
     * 
     * @author dexc
     * 
     */
    private class KickTimeController implements Runnable {

        private ClientExecutor _kick1 = null;

        private ClientExecutor _kick2 = null;

        private KickTimeController(final ClientExecutor kick1, final ClientExecutor kick2) {
            this._kick1 = kick1;
            this._kick2 = kick2;
        }

        private void schedule() {
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                this._kick1.toSender(new S_Disconnect());
                Thread.sleep(1000);
                if (_kick1 != null) {
                    this._kick1.set_error(10);
                }

                if (this._kick2 != null) {
                    this._kick2.set_error(10);
                }

            } catch (final InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
    
    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

}
