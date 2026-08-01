package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_UseMap;

/**
 * 各類小地圖<br>
 * 地圖:大陸全圖-16<br>
 * 地圖:說話之島-1<br>
 * 地圖:古魯丁-2<br>
 * 地圖:肯特城-3<br>
 * 地圖:妖魔城堡-4<br>
 * 地圖:妖精森林-5<br>
 * 地圖:風木之城-6<br>
 * 地圖:銀騎士村莊-7<br>
 * 地圖:龍之谷-8<br>
 * 地圖:奇巖-9<br>
 * 地圖:歌唱之島-10<br>
 * 地圖:隱藏之谷-11<br>
 * 地圖:海音-12<br>
 * 地圖:火龍窟-13<br>
 * 地圖-歐瑞-14<br>
 * 類名稱：MapR<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年9月13日 下午1:29:07<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class MapR extends ItemExecutor {

	/**
	 *
	 */
	private MapR() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MapR();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		pc.sendPackets(new S_UseMap(item.getId(), mapid));
	}
	
	private int mapid;
	
	public void set_set(String[] set) {
		try {
			mapid = Integer.parseInt(set[1]);
		} catch (Exception e) {
		}
	}
}