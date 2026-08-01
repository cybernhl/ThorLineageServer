package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 363 地图剩余使用时间
 * @author kzk
 *
 */
public class S_PacketBoxMapTimerOut extends ServerBasePacket {

	private byte[] _byte = null;
	
	public static final int DISPLAY_MAP_TIME = 159;
	
	public S_PacketBoxMapTimerOut(final L1PcInstance pc){
		writeC(S_OPCODE_PACKETBOX);//3.53这里需要改成9
		writeC((DISPLAY_MAP_TIME));
		writeD(4);
		writeD(1);
		writeS("$12125");
	//	writeD((ConfigOther.xsmaptime-pc.getRocksPrisonTime())/60);	
		writeS("$6081");
		//writeD((3600-pc.getRocksPrisonTime2())/60);
		writeD(3);
		writeS("$12126");
		//writeD((7200-pc.getRocksPrisonTime3())/60);
		writeD(6);
	}
	public static final int MAP_TIMER = 153;
	public S_PacketBoxMapTimerOut(final int value){
		  this.writeC(S_OPCODE_PACKETBOX);
		  this.writeC(MAP_TIMER);
		  this.writeH(value);
	}
	
	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}

	@Override
	public String getType()
	{
		return (new StringBuilder("[S] ")).append(getClass().getSimpleName()).append(" [S->C 發送封包盒子(離線切換觀看入場時間)]").toString();
	}
}
