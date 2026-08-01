package com.lineage.commons.system;

import java.io.IOException;
import java.security.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigIpCheck;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 安全管理器
 * @author dexc
 */
public class LanSecurityManager extends SecurityManager {

	private static final Log _log = LogFactory.getLog(LanSecurityManager.class);

	/**IP封包驗證策略*/
	public static final Map<String, Integer> BANIPPACK = new ConcurrentHashMap<String, Integer>();

	/**禁止連線IP位置*/
	public static final Map<String, Integer> BANIPMAP = new HashMap<String, Integer>();

	/**禁止連線NAME位置*/
	public static final Map<String, Integer> BANNAMEMAP = new HashMap<String, Integer>();

	/**1個IP僅允許一個連線*/
	public static final Map<String, Integer> ONEIPMAP = new ConcurrentHashMap<String, Integer>();

	/**1個IP只定時間內僅允許連限一個(毫秒) 0:不啟用*/
	public static final Map<String, Long> ONETIMEMILLISMAP = new ConcurrentHashMap<String, Long>();

	/**
	 * 如果不允許調用線程從指定的主機和埠號接受套接字連接，則拋出 SecurityException。
	 */
	@Override
	public void checkAccept(final String host, final int port) {
		if (ConfigIpCheck.IPCHECKPACK) {
			// 禁止IP
			if (BANIPMAP.containsKey(host)) {
				throw new SecurityException();
			}
			// IP封包驗證策略
			if (BANIPPACK.containsKey(host)) {
				throw new SecurityException();
			}
			
		} else {
			// 禁止IP
			if (BANIPMAP.containsKey(host)) {
				throw new SecurityException();
			}
			// 1個IP僅允許一個連線
			if (ONEIPMAP.containsKey(host)) {
				throw new SecurityException();
			}
			// 1個IP只定時間內僅允許連限一個(毫秒) 0:不啟用
			if (ONETIMEMILLISMAP.containsKey(host)) {
				throw new SecurityException();
			}
			
			if (ConfigIpCheck.ONETIMEMILLIS != 0) {
				ONETIMEMILLISMAP.put(host, System.currentTimeMillis());
			}
			if (ConfigIpCheck.ISONEIP) {
				ONEIPMAP.put(host, port);
			}
			if (ConfigIpCheck.IPCHECK) {// DaiEn 2012-04-25
				if (!IpAttackCheck.get().check(host)) {
					throw new SecurityException();
				}
			}
		}
	}
	
	/**
	 * 如果不允許調用線程修改 thread 參數，則拋出 SecurityException 
	 */
	@Override
	public void checkAccess(final Thread t) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 如果基於當前有效的安全策略，不允許執行根據給定許可權所指定的請求訪問，則拋出 SecurityException。
	 */
	@Override
	public void checkPermission(final Permission perm) {
		// TODO Auto-generated constructor stub
	}
	
	public void stsrt_cmd() throws IOException {
		final RemoveIp removeIp = new RemoveIp(ConfigIpCheck.ONETIMEMILLIS);
		GeneralThreadPool.get().execute(removeIp);
	}
	
	public void stsrt_cmd_tmp() throws IOException {
		final RemoveTmpIp removeIp = new RemoveTmpIp();
		GeneralThreadPool.get().execute(removeIp);
	}
	
	private class RemoveTmpIp implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(1000);
					if (!BANIPPACK.isEmpty()) {
						for (String ip : BANIPPACK.keySet()) {
							final int time = BANIPPACK.get(ip) - 1;
							if (time <= 0) {
								BANIPPACK.remove(ip);
							} else {
								BANIPPACK.put(ip, time);
							}
						}
					}
				}
				
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private class RemoveIp implements Runnable {
		
		public int _time = 60000;

		public RemoveIp(int oNETIMEMILLIS) {
			_time = oNETIMEMILLIS;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(10000);
					if (!ONETIMEMILLISMAP.isEmpty()) {
						for (String ip : ONETIMEMILLISMAP.keySet()) {
							long time = ONETIMEMILLISMAP.get(ip);
							
							if (System.currentTimeMillis() - time >= _time) {
								ONETIMEMILLISMAP.remove(ip);
							}
						}
					}
				}
				
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
