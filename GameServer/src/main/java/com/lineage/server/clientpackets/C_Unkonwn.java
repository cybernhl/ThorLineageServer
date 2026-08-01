package com.lineage.server.clientpackets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.echo.encryptions.PacketPrint;

/**
 * 未處理封包
 * @author dexc
 *
 */
public class C_Unkonwn extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Unkonwn.class);

	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			//this.read(decrypt);
			
			_log.info("未處理封包: " + (decrypt[0] & 0xff) + " (" + this.getNow_YMDHMS() + " 核心管理者紀錄用!)");
			_log.info(PacketPrint.get().printData(decrypt, decrypt.length));
			
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
	
	/**
	 * <font color=#00800>取得系統時間</font>
	 *
	 * @return 傳出標準時間格式 yyyy/MM/dd HH:mm:ss
	 */
	private final String getNow_YMDHMS() {
		final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		return nowDate;
	}
}
