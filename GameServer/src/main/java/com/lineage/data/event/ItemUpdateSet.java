package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.templates.L1Event;

/**
 * 升級裝備<BR>

# 新增升級裝備系統
DELETE FROM `server_event` WHERE `id`='37';
INSERT INTO `server_event` VALUES ('37', '升級裝備系統', 'ItemUpdateSet', '1', 'true', '說明:是否提供原始裝備附加屬性保留 true:是 false:否(不提供可堆疊物件交換)');

# 建立升級NPC
DELETE FROM `npc` WHERE `npcid`='91141';
DELETE FROM `npc` WHERE `npcid`='91142';
DELETE FROM `npc` WHERE `npcid`='91143';
INSERT INTO `npc` VALUES ('91141', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate', '', 'L1Merchant', '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '1', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0', '0', '0');
INSERT INTO `npc` VALUES ('91142', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate', '', 'L1Merchant', '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '1', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0', '0', '0');
INSERT INTO `npc` VALUES ('91143', '物品升級專員', '物品升級專員', 'shop.NPC_ItemUpdate', '', 'L1Merchant', '6757', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '1', '-1', '-1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0', '0', '-1', '0', '0', '0', '0', '0');

# 建立升級NPC召喚
DELETE FROM `server_event_spawn` WHERE `id`='40442';
INSERT INTO `server_event_spawn` VALUES ('40442', '37', '物品升級專員', '1', '91141', '0', '33422', '32809', '0', '0', '6', '0', '4', '0', '1');

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- 新增資料表 `server_item_update`
-- ----------------------------
DROP TABLE IF EXISTS `server_item_update`;
CREATE TABLE `server_item_update` (
  `itemid` int(10) NOT NULL COMMENT '要升級的物品ID',
  `toids` varchar(50) NOT NULL DEFAULT '0' COMMENT '可升級的物品ID組',
  `needids` varchar(50) NOT NULL DEFAULT '40308' COMMENT '升級需要消耗的物品',
  `needcounts` varchar(50) NOT NULL DEFAULT '1' COMMENT '升級需要消耗的物品數量',
  `other` varchar(255) DEFAULT '' COMMENT '說明',
  PRIMARY KEY (`itemid`),
  KEY `id` (`itemid`)
) ENGINE=MyISAM AUTO_INCREMENT=49524 DEFAULT CHARSET=utf8;

-- ----------------------------
-- 建立範例內容
-- ----------------------------
# 仔細看一下這一行範例 你應該就會知道怎設置
INSERT INTO `server_item_update` VALUES ('1', '2,3,4', '40308,44070', '100,10', '範例:歐西斯匕首->可交換為(骰子匕首,短劍的劍身,匕首) 需要金幣100元寶10');

 * @author daien
 *
 */
public class ItemUpdateSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(ItemUpdateSet.class);

	/**
	 * 是否提供原始裝備附加屬性保留<BR>
	 * true:是 false:否
	 */
	public static boolean MODE = false;// 是否提供原始裝備附加屬性保留
	
	/**
	 *
	 */
	private ItemUpdateSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new ItemUpdateSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");
			
			MODE = Boolean.parseBoolean(set[0]);
			
			ItemUpdateTable.get().load();
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
