package com.lineage.server.model.classes;

/**
 * 精靈
 * @author dexc
 *
 */
class L1ElfClassFeature extends L1ClassFeature {
	
	@Override
	public int getAcDefenseMax(final int ac) {
		return ac / 3;
	}

	@Override
	public int getMagicLevel(final int playerLevel) {
		return Math.min(6, playerLevel >> 3); // /8
	}

	@Override
	public int getAttackLevel(int playerLevel) {
		return playerLevel / 10;
	}

	@Override
	public int getHitLevel(int playerLevel) {
		return playerLevel / 30;
	}
}