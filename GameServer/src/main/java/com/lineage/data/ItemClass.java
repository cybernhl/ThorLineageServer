package com.lineage.data;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件模組相關
 *
 * @author dexc
 *
 */
public class ItemClass {

	private static final Log _log = LogFactory.getLog(ItemClass.class);
	
	// ITEMID, 執行類位置
	private static final Map<Integer, ItemExecutor> _classList = new HashMap<Integer, ItemExecutor>();

	private static ItemClass _instance;

	public static ItemClass get() {
		if (_instance == null) {
			_instance = new ItemClass();
		}
		return _instance;
	}

	/**
	 * 加入CLASS清單
	 * @param itemid 物件編號
	 * @param className 執行位置
	 * @param mode 0 if L1ItemsEtcItem, 1 if L1ItemsWeapon2 if L1ItemsArmor
	 */
	public void addList(final int itemid, final String className, final int mode) {
		if (className.equals("0") || className.equals("items")) {
			return;
		}
		try {
			String newclass = className;
			String[] set = null;
			if (className.indexOf(" ") != -1) {
				set = className.split(" ");
				newclass = set[0];
			}
			final StringBuilder stringBuilder = new StringBuilder();
			switch (mode) {
			case 0:// 道具
				stringBuilder.append("com.lineage.data.item_etcitem.");
				break;
				
			case 1:// 武器
				stringBuilder.append("com.lineage.data.item_weapon.");
				break;
				
			case 2:// 防具
				stringBuilder.append("com.lineage.data.item_armor.");
				break;
				
			case 3:// 新增物品表
				stringBuilder.append("com.lineage.data.item_etcitem.");
				break;
				
			case 4:// 新增防具
				stringBuilder.append("com.lineage.data.item_armor.");
				break;
				
			case 5:// 新增武器
				stringBuilder.append("com.lineage.data.item_weapon.");
				break;
			}
			stringBuilder.append(newclass);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final ItemExecutor exe = (ItemExecutor) cls.getMethod("get").invoke(null);
			if (set != null) {
				exe.set_set(set);
			}
			_classList.put(new Integer(itemid), exe);
			
		} catch (final ClassNotFoundException e) {
			String error = "發生[道具檔案]錯誤, 檢查檔案是否存在:" + className + " ItemId:" + itemid;
			_log.error(error);
			DataError.isError(_log, error, e);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalAccessException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final InvocationTargetException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final NoSuchMethodException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 道具的執行
	 * @param data
	 * @param pc
	 * @param item
	 */
	public void item(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if(pc == null) {
			return;
		}
		if(item == null) {
			return;
		}
		try {
			// CLASS執行位置取回
			final ItemExecutor exe = _classList.get(new Integer(item.getItemId()));
			if (exe != null) {
				exe.execute(data, pc, item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 武器的執行
	 * @param equipped
	 * @param pc
	 * @param item
	 */
	public void item_weapon(final boolean equipped, final L1PcInstance pc, final L1ItemInstance item) {
		if(pc == null) {
			return;
		}
		if(item == null) {
			return;
		}

		try {
			// CLASS執行位置取回
			final ItemExecutor exe = _classList.get(new Integer(item.getItemId()));
			if (exe != null) {
				int[] data = new int[1];
				data[0] = equipped ? 1 : 0;
				
				exe.execute(data, pc, item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 防具的執行
	 * @param equipped
	 * @param pc
	 * @param item
	 */
	public void item_armor(final boolean equipped, final L1PcInstance pc, final L1ItemInstance item) {
		if(pc == null) {
			return;
		}
		if(item == null) {
			return;
		}
		
		try {
			// CLASS執行位置取回
			final ItemExecutor exe = _classList.get(new Integer(item.getItemId()));
			if (exe != null) {
				int[] data = new int[1];
				data[0] = equipped ? 1 : 0;
				
				exe.execute(data, pc, item);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
