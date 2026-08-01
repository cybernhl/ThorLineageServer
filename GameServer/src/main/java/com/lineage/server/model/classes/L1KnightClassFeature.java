package com.lineage.server.model.classes;

/**
 * 騎士
 * @author dexc
 *
 */
class L1KnightClassFeature extends L1ClassFeature {
	
	@Override
	public int getAcDefenseMax(final int ac) {
		return (int) (ac * 0.4);
	}

	@Override
	public int getMagicLevel(final int playerLevel) {
		return playerLevel / 50;
	}

	@Override
	public int getAttackLevel(int playerLevel) {
		return playerLevel / 10;
	}

	@Override
	public int getHitLevel(int playerLevel) {
		return playerLevel / 10;
	}
}