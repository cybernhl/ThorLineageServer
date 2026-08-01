package com.lineage.netty.login;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.netty.CodecFactory;
import com.lineage.server.serverpackets.S_ServerVersion;

public class LineageServer {

	private static final Log _log = LogFactory.getLog(LineageServer.class);
	
	// netty
	private static ServerBootstrap sb;
	/**
	 * 客戶端鏈接列表
	 */
	private static Map<Channel, ClientExecutor> Clientlist;
	
	public static void load(){
		Clientlist = new HashMap<Channel, ClientExecutor>();
		
		// Server服務啟動器
		// ServerBootstrap負責初始化netty服務器，並且開始監聽端口的socket請求
		// NioServerSocketChannelFactory 是ChannelFactory的實現接口之一，負責創建並管理服務端Channel
		sb = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),// boss線程池
				Executors.newCachedThreadPool()));// //worker線程池
		// 設置一個處理客戶端消息和各種消息事件的類
		sb.setPipelineFactory(new CodecFactory());

		sb.setOption("child.keepAlive", false);// 啟用/禁用Nagle算法
		sb.setOption("child.tcpNoDelay", true);// 啟用/禁用Nagle算法
		// 每秒接收數據包的最大數量
		sb.setOption("child.receiveBufferSize", Config.PACKET_RECV_MAX);
		sb.setOption("bufferFactory", HeapChannelBufferFactory.getInstance(ChannelBuffers.LITTLE_ENDIAN));
		// 連接超時設置，以毫秒為單位  避免DOSS攻擊
		sb.setOption("connectTimeoutMillis", 1000);
		
		final String[] portList = Config.GAME_SERVER_PORT.split("-");
		for (String ports : portList) {
			int key = Integer.parseInt(ports);
			// 服務器端口監聽
			try {
				sb.bind(new InetSocketAddress(key));//端口開始監聽
				
			} catch (final Exception e) {
				_log.fatal("IP位置加載錯誤 或 端口位置已被佔用:(" + key + ")", e);
				
			}
		}
	}
	
	/**
	 * 關閉服務器全部客戶端連接
	 * 
	 * @throws Exception
	 */
	static public void close() {
		try {
			for (ClientExecutor c : getList())
				c.close();
			Clientlist.clear();
			sb = null;
		} catch (Exception e) {
		}
	}
	
	/**
	 * 客戶端連接處理
	 * 
	 * @param c
	 */
	public static void connect(Channel socket) {
		ClientExecutor c = new ClientExecutor();
		socket.setAttachment(c);
		// 相關信息初始化
		c.update(socket);
		// 加入連接列表
		append(c);
		//
		c.toSender(new S_ServerVersion());
	}
	
	/**
	 * 客戶端終止處理
	 * 離開遊戲
	 * @param c
	 */
	public static void close(ClientExecutor c) {
		ClientExecutor temp = remove(c);
		if (temp == null) {
			return;
		}
		// 關閉連接
		c.close();
	}
	
	/**
	 * 加入客戶端連接列表
	 * @param c
	 */
	private static void append(ClientExecutor c) {
		synchronized (Clientlist) {
			Clientlist.put(c.getSocket(), c);
		}
	}

	/**
	 * 客戶端連接列表刪除
	 * @param c
	 * @return
	 */
	private static ClientExecutor remove(ClientExecutor c) {
		synchronized (Clientlist) {
			return Clientlist.remove(c.getSocket());
		}
	}

	private static List<ClientExecutor> getList() {
		synchronized (Clientlist) {
			return new ArrayList<ClientExecutor>(Clientlist.values());
		}
	}
	
	/**
	 * 客戶端連接列表數量
	 * @return
	 */
	static public int getClientSize() {
		return Clientlist.size();
	}
}
