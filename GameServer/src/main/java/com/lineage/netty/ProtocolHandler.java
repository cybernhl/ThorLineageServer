package com.lineage.netty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.netty.login.LineageServer;
import com.lineage.server.datatables.BadIpDatabase;
import com.lineage.server.templates.ProtocolHandlerIp;

/**
 * 協議處理器
 * 類名稱：ProtocolHandler<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年7月18日 下午10:14:34<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class ProtocolHandler extends SimpleChannelUpstreamHandler {

	/**
	 * 無限連接過濾列表(DOSS)
	 */
	static private Map<String, ProtocolHandlerIp> list_dos = new HashMap<String, ProtocolHandlerIp>();

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// 檢查連接方式，無效訪問請求強制切斷
		if (isBadClient(e.getChannel())) {
			e.getChannel().close();
			return;
		}
		// 客戶端最大連接數量檢查
		if (LineageServer.getClientSize() >= Config.MAX_ONLINE_USERS) {
			e.getChannel().close();
			return;
		}
	}

	/**
	 * 連接處理函數
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// 接口允許只處理.
		if (e.getChannel().isConnected()) {
			LineageServer.connect(e.getChannel());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {

		if (me.getMessage() == null)
			return;

		Object o = me.getChannel().getAttachment();

		if (o == null)
			return;

		if (me.getMessage() instanceof List) {
			ClientExecutor c = (ClientExecutor) o;
			List<byte[]> list = (List<byte[]>) me.getMessage();
			for (byte[] data : list)
				try {
					c.toPacket(data);
				} catch (Exception e) {
				}
			list.clear();
			list = null;
		}
	}

	/**
	 * 結束處理函數
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		Object o = e.getChannel().getAttachment();
		if (o == null) {
			return;
		}
		LineageServer.close((ClientExecutor) o);
		// System.out.println("channelClosed : "+e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {

		Object o = e.getChannel().getAttachment();
		if (o == null) {
			return;
		}

		LineageServer.close((ClientExecutor) o);
		// System.out.println("exceptionCaught : "+e);
	}

	/**
	 * 
	 * @param socket
	 * @return
	 */
	static protected Boolean isBadClient(Channel socket) {
		String[] address = socket.getRemoteAddress().toString().substring(1).split(":");
		String ip = address[0];
		Integer port = Integer.valueOf(address[1]);
		Long time = System.currentTimeMillis();

		// 過濾非法IP
		if (BadIpDatabase.find(ip) != null) {
			System.out.println("檢測到被禁止的IP連接：" + ip + "強制斷開連接.");
			return true;	
		}
		// ddos 攻擊防禦
/*		if (port <= 0) {
			System.out.println("檢查到攻擊IP：" + ip);
			return true;
		}
		// 無限連接過濾 (dos攻擊)
	ProtocolHandlerIp IP = null;
		synchronized (list_dos) {
			IP = list_dos.get(ip);
			if (IP == null) {
				IP = new ProtocolHandlerIp();
				IP.setBlock(false);
				IP.setIp(ip);
				IP.setTime(time);
				list_dos.put(IP.getIp(), IP);
				return false;
			}
		}
		// 指定IP忽略
		if (IP.getBlock())
			return true;
		// 一秒鐘內5次以上連接的人忽略
		if (time < IP.getTime() + 1000) {
			if (IP.getCount() > 5) {
				IP.setBlock(true);
				System.out.println("1秒內連接次數大於5忽略" + ip + "連接.");
				return true;
			} else {
				IP.setCount(IP.getCount() + 1);
			}
		} else {
			IP.setCount(0);
		}
		IP.setTime(time);*/
		return false; 
	}
	

}