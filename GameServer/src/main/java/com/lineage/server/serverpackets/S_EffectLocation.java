package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Location;
import com.lineage.server.types.Point;

/**
 * 產生動畫(地點)<br>
 * 類名稱：S_EffectLocation<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午8:32:45<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class S_EffectLocation extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 產生動畫(地點)
	 *
	 * @param pt
	 *            - Point
	 * @param gfxId
	 *            - 動畫編號
	 */
	public S_EffectLocation(final Point pt, final int gfxId) {
		this(pt.getX(), pt.getY(), gfxId);
	}

	/**
	 * 產生動畫(地點)
	 *
	 * @param loc
	 *            - 座標資料
	 * @param gfxId
	 *            - 動畫編號
	 */
	public S_EffectLocation(final L1Location loc, final int gfxId) {
		this(loc.getX(), loc.getY(), gfxId);
	}
	
	/**
	 * 產生動畫(地點)
	 *
	 * @param x
	 *            - 座標資料X
	 * @param y
	 *            - 座標資料Y
	 * @param gfxId
	 *            - 動畫編號
	 */
	public S_EffectLocation(final int x, final int y, final int gfxId) {
		this.writeC(S_OPCODE_EFFECTLOCATION);
		this.writeH(x);
		this.writeH(y);
		this.writeH(gfxId);
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
