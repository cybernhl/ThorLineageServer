package com.lineage.server.serverpackets;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Party;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 隊伍UI
 * 
 * @author daien
 * 
 */
public class S_PacketBoxParty extends ServerBasePacket {

	private static final Log _log = LogFactory.getLog(S_PacketBoxParty.class);

	/**
	 * <font color=#00800>隊伍成員資料(隊員) </font><BR>
	 */
	public static final int MSG_PARTY1 = 0x68;

	/**
	 * <font color=#00800>隊伍成員資料(隊長) </font><BR>
	 */
	public static final int MSG_PARTY2 = 0x69;

	/**
	 * <font color=#00800>隊伍成員資料(更新) </font><BR>
	 */
	public static final int MSG_PARTY3 = 0x6e;

	/**
	 * <font color=#00800>1,698：%s成為了新的隊長。 </font>
	 */
	public static final int MSG_LEADER = 0x6a;

	private byte[] _byte = null;

	/**
	 * 隊伍成員資料 <BR>
	 * <font color=#00800> </font>
	 * 
	 * @param pc
	 *            資料接收者
	 * @param party
	 *            隊伍
	 * 
	 */
	public S_PacketBoxParty(final L1PcInstance pc, final L1Party party) {
		try {
			final HashMap<Integer, L1PcInstance> map = new HashMap<Integer, L1PcInstance>();
			map.putAll(party.partyUsers());

			writeC(S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY1);
			writeC(map.size() - 1);// 忽略自己資訊 因此-1
			for (Integer key : map.keySet()) {
				final L1PcInstance tgpc = map.get(key);
				if (tgpc == null) {
					continue;
				}
				if (pc.equals(tgpc)) {// 忽略自己
					continue;
				}
				writeD(tgpc.getId());
				writeS(tgpc.getName());

				final double nowhp = tgpc.getCurrentHp();
				final double maxhp = tgpc.getMaxHp();

				writeC((int) ((nowhp / maxhp) * 100D));
				writeD(tgpc.getMapId());
				writeH(tgpc.getX());
				writeH(tgpc.getY());
			}
			map.clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 更新隊伍 <BR>
	 * <font color=#00800> </font>
	 * 
	 * @param party
	 * @param pc
	 *            資料接收者
	 */
	public S_PacketBoxParty(final L1Party party, final L1PcInstance pc) {
		try {
			final HashMap<Integer, L1PcInstance> map = new HashMap<Integer, L1PcInstance>();
			map.putAll(party.partyUsers());

			writeC(S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY3);
			writeC(map.size() - 1);// 忽略自己資訊 因此-1
			for (Integer key : map.keySet()) {
				final L1PcInstance tgpc = map.get(key);
				if (tgpc == null) {
					continue;
				}
				if (pc.equals(tgpc)) {// 忽略自己
					continue;
				}
				writeD(tgpc.getId());
				writeD(tgpc.getMapId());
				writeH(tgpc.getX());
				writeH(tgpc.getY());
			}
			map.clear();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 隊伍成員資料(隊長)
	 * 
	 * @param pc
	 * @param map
	 */
	public S_PacketBoxParty(final L1PcInstance tgpc) {
		try {
			writeC(S_OPCODE_PACKETBOX);
			writeC(MSG_PARTY2);
			writeD(tgpc.getId());
			writeS(tgpc.getName());

			final double nowhp = tgpc.getCurrentHp();
			final double maxhp = tgpc.getMaxHp();

			writeC((int) ((nowhp / maxhp) * 100D));
			writeD(tgpc.getMapId());
			writeH(tgpc.getX());
			writeH(tgpc.getY());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 成為新的隊長<BR>
	 * <font color=#00800> %s成為了新的隊長 </font>
	 * 
	 * @param
	 */
	public S_PacketBoxParty(final int objid, final String name) {
		try {
			writeC(S_OPCODE_PACKETBOX);
			writeC(MSG_LEADER);
			writeD(objid);
			writeS(name);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}
}
