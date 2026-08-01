package com.lineage.server.templates;

import com.lineage.server.datatables.ItemTable;

/**
 * 貨幣商品
 * 
 * @author daien
 * 
 */
public class L1ShopItem {

	private final int _id;// id

	private final int _itemId;// item id

	private final L1Item _item;// item

	private final int _price;// 售價

	private final int _packCount;// 數量

	private final int _enchantLevel; // 強化等級 by 阿豪0330

	private int _purchasing_price;// NPC回收該道具所需商幣數量 by t阿豪0330

	public L1ShopItem(final int _id, final int itemId, final int price,
			final int packCount, final int enchantLevel,
			final int purchasing_price) {
		this._id = _id;
		this._itemId = itemId;
		this._item = ItemTable.get().getTemplate(itemId);
		this._price = price;
		this._packCount = packCount;
		this._enchantLevel = enchantLevel;
		this._purchasing_price = purchasing_price;
	}

	public L1ShopItem(final int itemId, final int price, final int packCount,
			final int enchantLevel) {
		this._id = -1;
		this._itemId = itemId;
		this._item = ItemTable.get().getTemplate(itemId);
		this._price = price;
		this._packCount = packCount;
		this._enchantLevel = enchantLevel;
	}

	public int getId() {
		return this._id;
	}

	public int getItemId() {
		return this._itemId;
	}

	public L1Item getItem() {
		return this._item;
	}

	public int getPrice() {
		return this._price;
	}

	public int getPackCount() {
		return this._packCount;
	}

	public int getEnchantLevel() { // 強化等級 by terry0412
		return this._enchantLevel;
	}

	public int getPurchasingPrice() {
		return this._purchasing_price;
	}
}
