package com.lineage.server.templates;

/**
 * 傳送點資料
 * 
 * @author daien
 * 
 */
public class L1TeleportAction {

	//private int _npcid;

	//private String _cmd;

	private int _checkClass;

	private int _checkLawful;

	private int _min;

	private int _max;

	private int _startWeek;

	private int _startHour;

	private int _endHour;

	private int _time;

	private int _itemid;

	private int _count;

	private int _locx;

	private int _locy;

	private int _mapid;

	/**
	 * @return npc_id
	 */
//	public int get_NpcId() {
//		return _npcid;
//	}

	/**
	 * @param npc_id
	 */
//	public void set_NpcId(final int npc_id) {
//		_npcid = npc_id;
//	}

	/**
	 * @return 傳出 _orderid
	 */
//	public String getCmd() {
//		return _cmd;
//	}

	/**
	 * @param cmd
	 */
//	public void setCmd(final String cmd) {
//		_cmd = cmd;
//	}

	/**
	 * @return 傳出 _locx
	 */
	public int getLocx() {
		return _locx;
	}

	/**
	 * @param _locx
	 */
	public void setLocx(final int locx) {
		_locx = locx;
	}

	/**
	 * @return 傳出 _locy
	 */
	public int getLocy() {
		return _locy;
	}

	/**
	 * @param _locy
	 */
	public void setLocy(final int locy) {
		_locy = locy;
	}

	/**
	 * @return 傳出 _mapid
	 */
	public int getMapid() {
		return _mapid;
	}

	/**
	 * @param _mapid
	 */
	public void setMapid(final int mapid) {
		_mapid = mapid;
	}

	/**
	 * @return 傳出 _itemid
	 */
	public int getItemId() {
		return _itemid;
	}

	/**
	 * @param itemid
	 */
	public void setItemId(final int itemid) {
		_itemid = itemid;
	}

	/**
	 * @return 傳出 _count
	 */
	public int getCount() {
		return _count;
	}

	/**
	 * @param _count
	 */
	public void setCount(final int count) {
		_count = count;
	}

	/**
	 * @return 傳出 _time
	 */
	public int getTime() {
		return _time;
	}

	/**
	 * @param _time
	 */
	public void setTime(final int time) {
		_time = time;
	}

	public int getMin() {
		return _min;
	}

	public void setMin(int min) {
		_min = min;
	}

	public int getMax() {
		return _max;
	}

	public void setMax(int max) {
		_max = max;
	}

	/**
	 * 檢查職業 (0=不檢查 1=王 2=騎 4=妖 8=法 16=黑 32=龍 64=幻, 如需檢查2種以上請相加即可 例如 檢查王跟法
	 * 那就是1+8=輸入9)
	 * 
	 * @return
	 */
	public int getCheckClass() {
		return _checkClass;
	}

	public void setCheckClass(int i) {
		_checkClass = i;
	}

	/**
	 * 檢查正義屬性 0=不檢查 1=中立 2=正義 3=邪惡
	 * 
	 * @return
	 */
	public int getCheckLawful() {
		return _checkLawful;
	}

	public void setCheckLawful(int i) {
		_checkLawful = i;
	}

	/**
	 * 限制星期幾傳送 1~7
	 * 
	 * @return
	 */
	public int getStartWeek() {
		return _startWeek;
	}

	public void setStartWeek(int i) {
		_startWeek = i;
	}

	/**
	 * 限制幾點傳送 0~23
	 * 
	 * @return
	 */
	public int getStartHour() {
		return _startHour;
	}

	public void setStartHour(int i) {
		_startHour = i;
	}

	/**
	 * 限制幾點傳出 0~23
	 * 
	 * @return
	 */
	public int getEndHour() {
		return _endHour;
	}

	public void setEndHour(int i) {
		_endHour = i;
	}
}
