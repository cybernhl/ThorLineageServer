package com.lineage.data.item_armor.set;

import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 套裝效果:裝備者變形
 * @author daien
 *
 */
public class EffectPolymorph implements ArmorSetEffect {
	
	private int _gfxId; // 變形外觀編號

	/**
	 * 套裝效果:裝備者變形
	 * @param gfxId 變形外觀編號
	 */
	public EffectPolymorph(final int gfxId) {
		this._gfxId = gfxId;
	}

	@Override
	public void giveEffect(final L1PcInstance pc) {
		// 6080:騎馬的公主 6094:騎馬的王子
		if ((this._gfxId == 6080) || (this._gfxId == 6094)) {
			if (pc.get_sex() == 0) {// 男性
				this._gfxId = 6094;
				
			} else {
				this._gfxId = 6080;
			}
			// 檢查軍馬頭盔是否具有可用次數
			if (!this.isRemainderOfCharge(pc)) {
				return;
			}
		}
		// 執行變身
		L1PolyMorph.doPoly(pc, this._gfxId, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	}

	@Override
	public void cancelEffect(final L1PcInstance pc) {
		// 6080:騎馬的公主 6094:騎馬的王子
		if ((this._gfxId == 6080) || (this._gfxId == 6094)) {
			if (pc.get_sex() == 0) {// 男性
				this._gfxId = 6094;
				
			} else {
				this._gfxId = 6080;
			}
		}
		if (pc.getTempCharGfx() != this._gfxId) {
			return;
		}
		// 解除變身
		L1PolyMorph.undoPoly(pc);
	}

	/**
	 * 檢查軍馬頭盔是否具有可用次數
	 * @param pc
	 * @return true:有 false:沒有
	 */
	private boolean isRemainderOfCharge(final L1PcInstance pc) {
		// 身上攜帶軍馬頭盔
		if (pc.getInventory().checkItem(20383, 1)) {// 軍馬頭盔
			final L1ItemInstance item = pc.getInventory().findItemId(20383);
			if (item != null) {
				if (item.getChargeCount() != 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int get_mode() {
		return this._gfxId;
	}
}
