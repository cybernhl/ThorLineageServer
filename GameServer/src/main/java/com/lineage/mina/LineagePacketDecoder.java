package com.lineage.mina;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class LineagePacketDecoder extends CumulativeProtocolDecoder {
	private static final byte[] xorValue = new byte[] {(byte) 0x12, (byte) 0x8B, (byte) 0x67, (byte) 0x29, (byte) 0x50, (byte) 0x46, (byte) 0x17, (byte) 0xB9, (byte) 0x09, (byte) 0x24, (byte) 0x9E, (byte) 0xFC, (byte) 0xF0, (byte) 0x9E, (byte) 0xE4, (byte) 0x52};
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		ClientExecutor client = (ClientExecutor) session.getAttribute(ClientExecutor.CLIENT_KEY);
		if (!session.isClosing() && client!=null) {
			in.mark();
			byte hiByte = !ConfigOther.packet_crypt ? in.get() : (byte)((int) in.get() ^ xorValue[0]);
			byte loByte = !ConfigOther.packet_crypt ? in.get() : (byte)((int) in.get() ^ xorValue[1]);
			
			int dataLength = hiByte & 0xff;
			dataLength |= loByte << 8 & 0xff00;
			
			dataLength -= 2;
			if (dataLength > 1440) {
				System.out.println("長度大於1440");
				client.kick();
				return true;
			}
			
			if (dataLength <= 0) {
				System.out.println("長度為負數或者0");
				client.kick();
				return true;
			}
			
			byte[] data = new byte[dataLength];
			if (in.remaining() < dataLength) {
				in.reset();
				System.out.println("in.remaining() < size");
				return false;
			}
			
			in.get(data, 0, dataLength);
			if (ConfigOther.packet_crypt) {
				for (int i = 0; i < dataLength; i ++) {
					data[i] ^= xorValue[(i + 2) % 10];
				}
			}
			decrypt(data, dataLength, client);
			out.write(data);
			return true;
		}
		return false;
	}
	
	private void decrypt(byte[] data, int size, ClientExecutor client) {
		byte[] size_temp = getByte(client.getPacketSize());
		byte[] temp = data.clone();
		int idx = size_temp[0];
		for (int i = 0; i < size; i++) {
			if (i > 0 && i % 8 == 0) {
				for (int j = 0; j < i; j++)
					data[i] ^= data[j];

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
			idx = temp[i];
		}
		client.setPacketSize(client.getPacketSize() + size);
	}

	private byte[] getByte(long c_size) {
		byte[] data = new byte[4];
		data[0] |= c_size & 255L;
		data[1] |= c_size >> 8 & 255L;
		data[2] |= c_size >> 16 & 255L;
		data[3] |= c_size >> 24 & 255L;
		return data;
	}

	public void dispose(IoSession client) throws Exception {

	}

	public void finishDecode(IoSession client, ProtocolDecoderOutput output)
			throws Exception {

	}
}