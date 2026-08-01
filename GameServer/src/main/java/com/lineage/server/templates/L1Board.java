package com.lineage.server.templates;

/**
 * 佈告欄資料
 * @author dexc
 *
 */
public class L1Board {

	private int _id;

	private String _name;

	private String _date;

	private String _title;

	private String _content;

	/**
	 * 傳回佈告欄ID
	 * @return
	 */
	public int get_id() {
		return this._id;
	}

	/**
	 * 設置佈告欄ID
	 * @param id
	 */
	public void set_id(final int id) {
		this._id = id;
	}

	/**
	 * 傳回佈告欄公佈者
	 * @return
	 */
	public String get_name() {
		return this._name;
	}

	/**
	 * 設置佈告欄公佈者
	 * @param name
	 */
	public void set_name(final String name) {
		this._name = name;
	}

	/**
	 * 傳回佈告欄公佈日期
	 * @return
	 */
	public String get_date() {
		return this._date;
	}

	/**
	 * 設置佈告欄公佈日期
	 * @param date
	 */
	public void set_date(final String date) {
		this._date = date;
	}

	/**
	 * 傳回佈告欄標題
	 * @return
	 */
	public String get_title() {
		return this._title;
	}

	/**
	 * 設置佈告欄標題
	 * @param title
	 */
	public void set_title(final String title) {
		this._title = title;
	}

	/**
	 * 傳回佈告欄內容
	 * @return
	 */
	public String get_content() {
		return this._content;
	}

	/**
	 * 設置佈告欄內容
	 * @param content
	 */
	public void set_content(final String content) {
		this._content = content;
	}
}