package com.lineage.server.templates;

/**
 * 
 * 類名稱：L1ItemGem<br>
 * 類描述：武器寶石鑲嵌系統<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:21:40<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class L1ItemGem {

	private int _item_obj_id;

	private int _dmg;
	
	private int _dmgew;
	
	private int _hit;
	
	private int _punchcount;
	
	private String _itemname;
	
	private String _pcname;

	public int get_item_obj_id() {
		return _item_obj_id;
	}

	public void set_item_obj_id(int item_obj_id) {
		this._item_obj_id = item_obj_id;
	}

	/**
	 * 攻擊力
	 * */
	public int get_dmg() {
		return _dmg;
	}

	/**
	 * 攻擊力
	 * */
	public void set_dmg(int _dmg) {
		this._dmg = _dmg;
	}

	/**
	 * 額外攻擊力
	 * @return
	 */
	public int get_dmgew() {
		return _dmgew;
	}

	/**額外攻擊*/
	public void set_dmgew(int _dmgew) {
		this._dmgew = _dmgew;
	}

	/**
	 * 命中
	 * @return
	 */
	public int get_hit() {
		return _hit;
	}

	/**命中*/
	public void set_hit(int _hit) {
		this._hit = _hit;
	}

	/**
	 * 鑲嵌寶石數量
	 * @return
	 */
	public int get_punchcount() {
		return _punchcount;
	}

	/**鑲嵌寶石數量*/
	public void set_punchcount(int _punchcount) {
		this._punchcount = _punchcount;
	}
	
	/**
	 * 鑲嵌寶石武器的名稱
	 * @return
	 */
	public String get_itemname(){
		return _itemname;
	}
	
	/**
	 * 設定鑲嵌寶石的武器名稱
	 * @param _itemname
	 */
	public void set_itemname(String _itemname){
		this._itemname = _itemname;
	}
	
	/**
	 * 鑲嵌寶石武器的玩家名字
	 * @return
	 */
	public String get_pcname(){
		return _pcname;
	}
	
	public void set_pcname(String _pcname){
		this._pcname = _pcname;
	}
}