package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 盟屋拍賣公告欄列表<br>
 * 類名稱：S_AuctionBoard<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午11:35:06<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_AuctionBoard extends ServerBasePacket {

	private byte[] _byte = null;

	public S_AuctionBoard(final L1NpcInstance board) {
		this.buildPacket(board);
	}

	private void buildPacket(final L1NpcInstance board) {
		final ArrayList<L1AuctionBoardTmp> houseListX = new ArrayList<L1AuctionBoardTmp>();

		final Collection<L1AuctionBoardTmp> boardList = AuctionBoardReading.get().getAuctionBoardTableList().values();
		for (final L1AuctionBoardTmp boardX : boardList) {
			final int houseId = boardX.getHouseId();
			if ((board.getX() == 33421) && (board.getY() == 32823)) { // 奇巖
				if ((houseId >= 262145) && (houseId <= 262189)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 33585) && (board.getY() == 33235)) { // 海音
				if ((houseId >= 327681) && (houseId <= 327691)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 33959) && (board.getY() == 33253)) { // 亞丁
				if ((houseId >= 458753) && (houseId <= 458819)) {
					houseListX.add(boardX);
				}
			} else if ((board.getX() == 32611) && (board.getY() == 32775)) { // 古魯丁
				if ((houseId >= 524289) && (houseId <= 524294)) {
					houseListX.add(boardX);
				}
			}
		}

		this.writeC(S_OPCODE_HOUSELIST);
		this.writeD(board.getId());
		this.writeH(houseListX.size()); // 數
		for (final L1AuctionBoardTmp boardX : houseListX) {
			this.writeD(boardX.getHouseId()); // 小屋編號
			this.writeS(boardX.getHouseName()); // 小屋名稱
			this.writeH(boardX.getHouseArea()); // 小屋大小
			final Calendar cal = boardX.getDeadline();
			this.writeC(cal.get(Calendar.MONTH) + 1); // 結束月份
			this.writeC(cal.get(Calendar.DATE)); // 結束日期
			this.writeD((int) boardX.getPrice()); // 傳回目前售價
		}
	}

	@Override
	public byte[] getContent() {
		if (this._byte == null) {
			this._byte = this.getBytes();
		}
		return this._byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
