package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;

/**
 * 物件移動<br>
 * 類名稱：S_MoveCharPacket<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月26日 下午12:57:32<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_MoveCharPacket extends ServerBasePacket {

	private byte[] _byte = null;

	// 反向
	private static final byte HEADING_TABLE_XR[] = { 0, -1, -1, -1, 0, 1, 1, 1 };
	private static final byte HEADING_TABLE_YR[] = { 1, 1, 0, -1, -1, -1, 0, 1 };
	
	/**
	 * 物件移動
	 * @param cha
	 */
	public S_MoveCharPacket(final L1Character cha) {
		int locx = cha.getX();
		int locy = cha.getY();
		final int heading = cha.getHeading();
		locx += HEADING_TABLE_XR[heading];
		locy += HEADING_TABLE_YR[heading];

		writeC(S_OPCODE_MOVEOBJECT);
		writeD(cha.getId());
		writeH(locx);
		writeH(locy);
		writeC(cha.getHeading());
	}
	
    /**
     * 物件移動 移動AI調用
     * @param cha
     * @param locx
     * @param locy
     */
    public S_MoveCharPacket(final L1Character cha, int locx, int locy) {
        final int heading = cha.getHeading();
        locx += HEADING_TABLE_XR[heading];
        locy += HEADING_TABLE_YR[heading];

        writeC(S_OPCODE_MOVEOBJECT);
        writeD(cha.getId());
        writeH(locx);
        writeH(locy);
        writeC(cha.getHeading());
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