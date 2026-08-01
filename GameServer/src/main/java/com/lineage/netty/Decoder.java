package com.lineage.netty;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import com.lineage.echo.ClientExecutor;

/**
 * 解碼<br>
 * 類名稱：Decoder<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月24日 下午5:36:48<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public final class Decoder extends OneToOneDecoder {

	// OneToOneDecoder 這是一個消息轉成另一個消息的解碼器，常常在FrameDecoder解碼之後再使用，因為經過FrameDecoder解碼後的一個個消息已經是完整獨立的消息對象，
	// 所以它不需要處理消息分隔的工作，它所承擔的職責也就輕很多，例如Base64編碼等工作。對使用者來說，只要繼承它並重寫decode方法。
	/**
	 * 每秒收到的數據包數量的記錄。登錄使用
	 */
	static public int recv_length;

	static {
		recv_length = 0;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		//
		ClientExecutor c = (ClientExecutor) channel.getAttachment();
		if (c == null)
			return msg;
		//
		synchronized (c.getBuffer()) {
			if (msg instanceof ChannelBuffer) {
				// ChannelBuffer是Netty中比較常用的一個類，其功能類似於字符數組，可以對其進行讀寫操作
				ChannelBuffer buffer = (ChannelBuffer) msg;

				int size = buffer.readableBytes();// 返回可讀區域的字節數
				byte[] data = new byte[size];
				buffer.readBytes(data);
				buffer = null;
				c.getBuffer().writeBytes(data);
				// 為日誌記錄的數據量更新
				recv_length += size;

				List<byte[]> list = new ArrayList<byte[]>();
				msg = list;
				// 處理.
				for (;;) {
					// head 尺寸提取
					int length = c.getBuffer().getByte(0) & 0xff;
					length |= c.getBuffer().getByte(1) << 8 & 0xff00;
					if (c.getBuffer().readableBytes() >= length) {// 可讀區域的字節數 大於等於長度
						c.getBuffer().readBytes(2);
						length -= 2;
						// 內存
						data = new byte[length];
						c.getBuffer().readBytes(data);
						c.getBuffer().discardReadBytes();//  扔掉已讀數據
						// 解密處理
						decrypt(c, data);
						// 設置傳輸數據
						c.setPacketSize(c.getPacketSize() + length);
						// 為日誌記錄更新包量.
						//c.setSendLength(c.getSendLength() + length + 2);
						list.add(data);
					} else {
						break;
					}
				}
			}
		}
		return msg;
	}

	/**
	 * 解密
	 * @param c
	 * @param data
	 */
	private void decrypt(ClientExecutor c, byte[] data) {
		byte[] header = { (byte) (c.getPacketSize() & 0xff),
				(byte) (c.getPacketSize() >> 8 & 0xff),
				(byte) (c.getPacketSize() >> 16 & 0xff),
				(byte) (c.getPacketSize() >> 24 & 0xff) };
		byte[] temp = new byte[data.length];
		System.arraycopy(data, 0, temp, 0, data.length);
		int idx = header[0];
		for (int i = 0; i < data.length; ++i) {
			if (i > 0 && i % 8 == 0) {
				for (int j = 0; j < i; ++j)
					data[i] ^= data[j];
				if (i % 16 == 0) {
					for (byte st : header)
						data[i] ^= st;
				}
				for (int j = 1; j < header.length; ++j)
					data[i] ^= header[j];
				try {
					for (int j = 1; j < header.length; ++j)
						data[i + j] ^= header[j];
				} catch (Exception e) {
				}
			} else {
				data[i] ^= idx;
				try {
					if (i == 0) {
						for (int j = 1; j < header.length; ++j) {
							data[i + j] ^= header[j];
						}
					}
				} catch (Exception e) {
				}
			}
			idx = temp[i];
		}
		temp = null;
		header = null;
	}

}
