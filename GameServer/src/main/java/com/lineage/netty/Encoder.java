package com.lineage.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.lineage.echo.BasePacketPooling;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.sql.OpcodesTable;
import com.lineage.server.datatables.sql.ServerTable;
import com.lineage.server.serverpackets.OpcodesServer;
import com.lineage.server.serverpackets.ServerBasePacket;

public final class Encoder extends OneToOneEncoder {
	/**
	 * 每秒發送的數據包數量的記錄。登錄使用
	 */
	static public int send_length;

	static {
		send_length = 0;
	}

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {

		if (msg instanceof ServerBasePacket) {
			// 初始化
			ServerBasePacket sbp = (ServerBasePacket) msg;
			byte[] temp = sbp.getBytes();
			int length = temp.length + 2;
			BasePacketPooling.setPool(sbp);
			
			int opId = temp[0] & 0xff;

			if (OpcodesTable.get().getOpBroad(opId, "server") == 1) {
				System.out.println("S包：" + OpcodesTable.get().getOpClassName(opId, "server") + " : " + OpcodesTable.get().getOpName(opId, "server") + " : " + opId);
			}

			ClientExecutor c = (ClientExecutor) channel.getAttachment();
			if (c == null) {
				return msg;
			}

			byte[] data = new byte[length];
			data[0] = (byte) (length & 0xff);
			data[1] = (byte) (length >> 8 & 0xff); 

			if (test(temp[0], c.getPacketSendSize())) {
				byte[] test = new byte[8];
				test[0] = (byte) OpcodesServer.S_OPCODE_WEATHER;// 更新目前遊戲時間
				test[1] = (byte) (0);
				test[2] = (byte) (0);
				test[3] = (byte) (0);
				test[4] = (byte) (0);
				// 加密
				encrypt(c, test);
				c.setPakcetSendSize(c.getPacketSendSize() + test.length);
				encrypt(c, temp);
				c.setPakcetSendSize(c.getPacketSendSize() + temp.length);
				// 尺寸值等。
				data = new byte[length + test.length + 2];
				data[0] = (byte) (10 & 0xff);
				data[1] = (byte) (10 >> 8 & 0xff);
				data[10] = (byte) (length & 0xff);
				data[11] = (byte) (length >> 8 & 0xff);
				// 數據放
				System.arraycopy(test, 0, data, 2, test.length);
				System.arraycopy(temp, 0, data, 12, length - 2);
			} else {
				// 加密
				encrypt(c, temp);
				c.setPakcetSendSize(c.getPacketSendSize() + temp.length);
				// 數據放
				System.arraycopy(temp, 0, data, 2, temp.length);
			}
			// 整理。
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			buffer.writeBytes(data);
			msg = buffer;
			// 為日誌記錄的數據量更新。
			//send_length += length;
			//c.setRecvLength(c.getRecvLength() + length);
		}
		return msg;
	}

	private void encrypt(ClientExecutor c, byte[] data) {
		byte[] header = { (byte) (c.getPacketSendSize() & 0xff),
				(byte) (c.getPacketSendSize() >> 8 & 0xff),
				(byte) (c.getPacketSendSize() >> 16 & 0xff),
				(byte) (c.getPacketSendSize() >> 24 & 0xff) };
		byte[] temp = new byte[data.length];
		System.arraycopy(data, 0, temp, 0, data.length);
		int idx = header[0];
		for (int i = 0; i < data.length; ++i) {
			if (i > 0 && i % 8 == 0) {
				for (int j = 0; j < i; ++j)
					data[i] ^= temp[j];
				if (i % 16 == 0) {
					for (byte st : header)
						data[i] ^= st;
				}
				for (int j = 1; j < header.length; ++j)
					data[i] ^= header[j];
				try {
					// 第三編碼處理
					for (int j = 1; j < header.length; ++j)
						data[i + j] ^= header[j];
				} catch (Exception e) {
				}
			} else {
				data[i] ^= idx;
				if (i == 0) {
					try {
						for (int j = 1; j < header.length; ++j)
							data[i + j] ^= header[j];
					} catch (Exception e) {
					}
				}
			}
			idx = data[i];
		}
		temp = null;
		header = null;
	}

	private boolean test(byte op, long total_size) {
		int o = op & 0xff;
		if (o == OpcodesServer.S_OPCODE_POISON)// 魔法效果:中毒
			return total_size % 256 == 8;
		else if (o == OpcodesServer.S_OPCODE_SHOWHTML)// 產生 NPC 對話視窗
			return total_size % 256 == 16;
		else if (o == OpcodesServer.S_OPCODE_ATTRIBUTE)// 物件屬性 (門 開關)
			return total_size % 256 == 24;
		else if (o == OpcodesServer.S_OPCODE_ITEMAMOUNT// 物品狀態更新
				|| o == OpcodesServer.S_OPCODE_ITEMNAME)// 更新物品使用狀態-數量/狀態
			return total_size % 256 == 32;
		else if (o == OpcodesServer.S_OPCODE_CHANGEHEADING)// 物件面向
			return total_size % 256 == 38;
		else if (o == OpcodesServer.S_OPCODE_MOVEOBJECT)// 移動物件
			return total_size % 256 == 40;
		else if (o == OpcodesServer.S_OPCODE_CURSEBLIND)// 法術效果-暗盲咒術
			return total_size % 256 == 48;
		else if (o == OpcodesServer.S_OPCODE_TRUETARGET)// 魔法效果:精準目標
			return total_size % 256 == 64;
		else if (o == OpcodesServer.S_OPCODE_PINKNAME)// 角色名稱變紫色
			return total_size % 256 == 80;
		else if (o == OpcodesServer.S_OPCODE_SKILLBRAVE)// 魔法|物品效果圖示 {勇敢藥水類}
			return total_size % 256 == 88;
		else if (o == OpcodesServer.S_OPCODE_SOUND)// 撥放音效-寵物哨子
			return total_size % 256 == 112;
		return false;
	}

}
