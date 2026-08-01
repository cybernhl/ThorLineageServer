package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1ItemGem;

/**
 * 
 * 類名稱：CharGemStorage<br>
 * 類描述：武器寶石鑲嵌系統<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:13:47<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public interface CharGemStorage {
	
	/**
	 * 資料預先載入
	 */
	public void load();

	/**
	 * 增加物品凹槽資料
	 * @param objid
	 * @param attr
	 * @return
	 */
	public void storeItem(final int objId, final L1ItemGem attr) throws Exception;
	
	/**
	 * 更新凹槽資料
	 * @param item_obj_id
	 * @param attr
	 */
	public void updateItem(final int item_obj_id, final L1ItemGem attr);
}
