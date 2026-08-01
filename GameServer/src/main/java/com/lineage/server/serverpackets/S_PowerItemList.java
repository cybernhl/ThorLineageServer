package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物品清單<br>
 * 類名稱：S_PowerItemList<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月9日 下午3:00:42<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_PowerItemList extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物品清單
	 * @param pc
	 * @param items 
	 */
	public S_PowerItemList(final L1PcInstance pc, final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0a);
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			writeC(0x00);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(0x01);
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
		}
		items.clear();
	}
	
	public S_PowerItemList(final int objid, final List<L1ItemInstance> items) {
		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(objid);
		writeH(items.size());
		writeC(0x0c);
		for (final L1ItemInstance item : items) {
			final int itemobjid = item.getId();
			writeD(itemobjid);
			writeC(0x00);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(0x01);
			writeC(item.isIdentified() ? 0x01 : 0x00);
			writeS(item.getViewName());
		}
		items.clear();
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
