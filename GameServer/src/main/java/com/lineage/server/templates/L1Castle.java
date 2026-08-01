package com.lineage.server.templates;

import java.util.Calendar;

/**
 * 城堡數據暫存
 * @author DaiEn
 *
 */
public class L1Castle {
	
	/**
	 * 城堡數據暫存
	 * @param id 城堡編號
	 * @param name 城堡名稱
	 */
	public L1Castle(final int id, final String name) {
		this._id = id;
		this._name = name;
	}

	private int _id;

	public int getId() {
		return this._id;
	}

	private String _name;

	public String getName() {
		return this._name;
	}

	private Calendar _warTime;

	/**
	 * 傳回攻城時間
	 * @return
	 */
	public Calendar getWarTime() {
		return this._warTime;
	}

	/**
	 * 設置攻城時間
	 * @param i
	 */
	public void setWarTime(final Calendar i) {
		this._warTime = i;
	}

	private int _taxRate;

	public int getTaxRate() {
		return this._taxRate;
	}

	public void setTaxRate(final int i) {
		this._taxRate = i;
	}

	private long _publicMoney;

	public long getPublicMoney() {
		return this._publicMoney;
	}

	public void setPublicMoney(final long i) {
		this._publicMoney = i;
	}

}
