package com.lineage.server.model;

public class L1TaxCalculator {

	/**
	 * 戰爭稅15%固定
	 */
	private static final int WAR_TAX_RATES = 15;

	/**
	 * 國稅10%固定（地域稅對割合）
	 */
	private static final int NATIONAL_TAX_RATES = 10;

	/**
	 * 稅10%固定（戰爭稅對割合）
	 */
	private static final int DIAD_TAX_RATES = 10;

	private final int _taxRatesCastle;
	private final int _taxRatesTown;
	private final int _taxRatesWar = WAR_TAX_RATES;

	/**
	 * @param merchantNpcId
	 *            計算對像商店NPCID
	 */
	public L1TaxCalculator(final int merchantNpcId) {
		this._taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(merchantNpcId);
		this._taxRatesTown = L1TownLocation.getTownTaxRateByNpcid(merchantNpcId);
	}

	public int calcTotalTaxPrice(final int price) {
		final int taxCastle = price * this._taxRatesCastle;
		final int taxTown = price * this._taxRatesTown;
		final int taxWar = price * WAR_TAX_RATES;
		return (taxCastle + taxTown + taxWar) / 100;
	}

	// XXX 個別計算為、丸誤差出。
	public int calcCastleTaxPrice(final int price) {
		return (price * this._taxRatesCastle) / 100 - this.calcNationalTaxPrice(price);
	}

	public int calcNationalTaxPrice(final int price) {
		return (price * this._taxRatesCastle) / 100 / (100 / NATIONAL_TAX_RATES);
	}

	public int calcTownTaxPrice(final int price) {
		return (price * this._taxRatesTown) / 100;
	}

	public int calcWarTaxPrice(final int price) {
		return (price * this._taxRatesWar) / 100;
	}

	public int calcDiadTaxPrice(final int price) {
		return (price * this._taxRatesWar) / 100 / (100 / DIAD_TAX_RATES);
	}

	/**
	 * 課稅後價格求。
	 *
	 * @param price
	 *            課稅前價格
	 * @return 課稅後價格
	 */
	public int layTax(final int price) {
		return price + this.calcTotalTaxPrice(price);
	}
}
