package com.lineage.mina;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.sql.OpcodesTable;
import com.lineage.server.serverpackets.OpcodesServer;
import com.lineage.server.serverpackets.ServerBasePacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class LineagePacketEncoder implements ProtocolEncoder {

	private static final Log _log = LogFactory
			.getLog(LineagePacketEncoder.class);

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		try {
			if (session.isClosing()) {
				System.out.println("客戶端連接已關閉停止數據發送。");
				return;
			}
			if (message != null) {
				ClientExecutor client = (ClientExecutor) session.getAttribute(ClientExecutor.CLIENT_KEY);
				if (client != null) {
					IoBuffer buffer = null;
					if (message instanceof ServerBasePacket) {
						ServerBasePacket bp = (ServerBasePacket) message;
						if (client.getActiveChar() != null && client.getActiveChar().getId() == 917282931) {
							WriteLogTxt.NormalLog(client.getActiveChar().getName() + "的封包監聽.txt", bp.getType());
						}
						byte[] d = bp.getBytes();
						int size = d.length;
						if (size > 0) {

							//_log.info("[server]" + Integer.valueOf(bp.getBytes()[0] & 0xFF) + " : "+ bp.getType());
							test(client, out, d, size);
							encrypt(d, size, client);
							buffer = buffer(d, size + 2);
							out.write(buffer);
						}
					} else {
						out.write(message);
					}
				} else {
					System.out.println("客戶端連接為空的數據連接。");
					out.write(message);
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void test(ClientExecutor client, ProtocolEncoderOutput out,
			byte[] d, int size) {
		if (size <= 0) {
			System.out.println("數據封包小於0終止發送");
			return;
		}

		switch (d[0] & 0xFF) {
		case OpcodesServer.S_OPCODE_POISON:// 魔法效果:毒素
			if (client.getPacketSendSize() % 256L == 8L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_SHOWHTML:// NPC對話視窗
			if (client.getPacketSendSize() % 256L == 16L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_ATTRIBUTE:// 物件屬性(門)
			if (client.getPacketSendSize() % 256L == 24L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_ITEMAMOUNT:// 更新物品可使用數量(背包)
		case 26:
			if (client.getPacketSendSize() % 256L == 32L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_CHANGEHEADING:// 更新物件面向
			if (client.getPacketSendSize() % 256L == 38L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_MOVEOBJECT:// 物件移動
			if (client.getPacketSendSize() % 256L == 40L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_CURSEBLIND:// 魔法效果:暗盲咒術
			if (client.getPacketSendSize() % 256L == 48L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_TRUETARGET:// 魔法效果:精準目標
			if (client.getPacketSendSize() % 256L == 64L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_PINKNAME: // 粉名(PK)
			if (client.getPacketSendSize() % 256L == 80L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_SKILLBRAVE:// 魔法效果:勇敢藥水纇
			if (client.getPacketSendSize() % 256L == 88L) {
				test(client, out);
			}
			break;
		case OpcodesServer.S_OPCODE_SOUND:// 撥放音效
			if (client.getPacketSendSize() % 256L == 112L) {
				test(client, out);
			}
			break;
		}
	}

	private void test(ClientExecutor client, ProtocolEncoderOutput out) {
		byte[] dd = new byte[8];
		dd[0] = 51;
		dd[1] = 0;
		dd[2] = 0;
		dd[3] = 0;
		dd[4] = 0;
		dd[5] = 0;
		dd[6] = 0;
		dd[7] = 0;
		encrypt(dd, 8, client);
		out.write(buffer(dd, 10));
	}
	private void encrypt(byte[] data, int size, ClientExecutor client) {
		byte[] size_temp = getByte(client.getPacketSendSize());
		byte[] temp = data.clone();
		int idx = size_temp[0];
		for (int i = 0; i < size; i++) {
			if (i > 0 && i % 8 == 0) {
				for (int j = 0; j < i; j++)
					data[i] ^= temp[j];

				if (i % 16 == 0) {
					byte[] abyte0;
					int l = (abyte0 = size_temp).length;
					for (int k = 0; k < l; k++) {
						byte st = abyte0[k];
						data[i] ^= st;
					}

				}
				for (int j = 1; j < 4; j++)
					data[i] ^= size_temp[j];

				for (int j = 1; j < 4; j++)
					if (i + j < size)
						data[i + j] ^= size_temp[j];

			} else {
				data[i] ^= idx;
				if (i == 0) {
					for (int j = 1; j < 4; j++)
						if (i + j < size)
							data[i + j] ^= size_temp[j];

				}
			}
			idx = data[i];
		}

		client.setPakcetSendSize(client.getPacketSendSize() + size);
	}

	private byte[] getByte(long s_size) {
		byte[] data = new byte[4];
		data[0] |= s_size & 255L;
		data[1] |= s_size >> 8 & 255L;
		data[2] |= s_size >> 16 & 255L;
		data[3] |= s_size >> 24 & 255L;
		return data;
	}

	public void dispose(IoSession client) throws Exception {

	}

	private IoBuffer buffer(byte[] data, int length) {
		byte[] size = new byte[2];
		size[0] |= length & 0xff;
		size[1] |= length >> 8 & 0xff;
		IoBuffer buffer = IoBuffer.allocate(length, false);
		buffer.put(size);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}