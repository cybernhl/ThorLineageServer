package com.lineage.server.templates;

/**
 * Event(活動)設置 暫存
 * @author daien
 *
 */
public class L1Event {

	private int _eventid;// 活動編號

	private String _eventname;// 活動名稱

	private String _eventclass;// 活動CLASS

	private boolean _eventstart;// 活動是否啟用

	private String _eventother;// 活動其他設定

	/**
	 * 活動編號
	 * @return the _eventid
	 */
	public int get_eventid() {
		return this._eventid;
	}

	/**
	 * 活動編號
	 * @param eventid the _eventid to set
	 */
	public void set_eventid(final int eventid) {
		this._eventid = eventid;
	}

	/**
	 * 活動名稱
	 * @return the _eventname
	 */
	public String get_eventname() {
		return this._eventname;
	}

	/**
	 * 活動名稱
	 * @param eventname the _eventname to set
	 */
	public void set_eventname(final String eventname) {
		this._eventname = eventname;
	}

	/**
	 * 活動CLASS
	 * @return the _eventclass
	 */
	public String get_eventclass() {
		return this._eventclass;
	}

	/**
	 * 活動CLASS
	 * @param eventclass the _eventclass to set
	 */
	public void set_eventclass(final String eventclass) {
		this._eventclass = eventclass;
	}

	/**
	 * 活動是否啟用
	 * @return the _eventstart
	 */
	public boolean is_eventstart() {
		return this._eventstart;
	}

	/**
	 * 活動是否啟用
	 * @param eventstart the _eventstart to set
	 */
	public void set_eventstart(final boolean eventstart) {
		this._eventstart = eventstart;
	}

	/**
	 * 活動其他設定
	 * @return the _eventother
	 */
	public String get_eventother() {
		return this._eventother;
	}

	/**
	 * 活動其他設定
	 * @param eventother the _eventother to set
	 */
	public void set_eventother(final String eventother) {
		this._eventother = eventother;
	}
}
