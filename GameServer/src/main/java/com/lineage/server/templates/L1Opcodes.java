package com.lineage.server.templates;

/**
 * 服務器封包存放暫存<br>
 * 類名稱：L1Opcodes<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月11日 下午12:12:06<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:更新182<br>
 * @version<br>
 */
public class L1Opcodes {
	
	private String op_type;
	
	/**
	 * 設置封包類型
	 * @param op_type
	 */
	public void set_Op_Type(final String op_type) {
		this.op_type = op_type;
	}
	
	/**
	 * 傳回封包類型
	 * @return
	 */
	public String get_Op_Type() {
		return this.op_type;
	}

	private String op_classnme;
	
	/**
	 * 設置封包名稱
	 * @param classname
	 */
	public void set_Op_Classname(final String op_classnme) {
		this.op_classnme = op_classnme;
	}
	
	/**
	 * 傳回封包名稱
	 * @return
	 */
	public String get_Op_Classname() {
		return this.op_classnme;
	}
	
	private int op_id;
	
	/**
	 * 設置封包ID
	 * @param op_id
	 */
	public void set_Op_Id(final int op_id) {
		this.op_id = op_id;
	}
	
	/**
	 * 傳回封包ID
	 * @return
	 */
	public int get_Op_Id() {
		return op_id;
	}
	
	private String op_name;

	/**
	 * 設置封包名稱
	 * @param op_name
	 */
	public void set_Op_Name(final String op_name) {
		this.op_name = op_name;
	}
	
	/**
	 * 傳回封包名稱
	 * @return
	 */
	public String get_Op_Name() {
		return this.op_name;
	}
	
	private int op_broad;
	
	/**
	 * 設置是否打印封包數據
	 * @param op_broad
	 *            0：不打印<br>
	 *            1：打印<br>
	 */
	public void set_Op_Broad(final int op_broad) {
		this.op_broad = op_broad;
	}
	
	/**
	 * 傳回是否打印數據
	 * @return
	 */
	public int get_Op_Broad() {
		return this.op_broad;
	}
}
