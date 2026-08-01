package com.lineage.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import com.lineage.config.ConfigRate;
import com.lineage.server.model.L1TaxCalculator;
import com.lineage.server.templates.L1ShopItem;

/**
 *
 * @author dexc
 *
 */
class L1ShopBuyOrder {

	private final L1ShopItem _item;

	private final int _count;

	public L1ShopBuyOrder(final L1ShopItem item, final int count) {
		this._item = item;
		this._count = Math.max(0, count);
	}

	public L1ShopItem getItem() {
		return this._item;
	}

	public int getCount() {
		return this._count;
	}
}

/**
 *
 * @author dexc
 *
 */
public class L1ShopBuyOrderList {

	private final L1Shop _shop;

	private final List<L1ShopBuyOrder> _list = new ArrayList<L1ShopBuyOrder>();

	private final L1TaxCalculator _taxCalc;

	private int _totalWeight = 0;

	private int _totalPrice = 0;

	private int _totalPriceTaxIncluded = 0;

	public L1ShopBuyOrderList(final L1Shop shop) {
		this._shop = shop;
		this._taxCalc = new L1TaxCalculator(shop.getNpcId());
	}

	/**
	 * 如果列表不包含元素，則返回 true。
	 * @return
	 */
	public boolean isEmpty() {
		return _list.isEmpty();
	}

	public void add(final int orderNumber, final int count) {
		if (this._shop.getSellingItems().size() < orderNumber) {
			return;
		}
		final int ch_count = Math.max(0, count);
		if (ch_count <= 0) {
			return;
		}
		
		final L1ShopItem shopItem = this._shop.getSellingItems().get(orderNumber);
		
		final int price = (int) (shopItem.getPrice() * ConfigRate.RATE_SHOP_SELLING_PRICE);// 物品單價
		// 檢查販賣物品總價
		for (int j = 0; j < ch_count; j++) {
			if (price * j < 0) {
				return;
			}
		}
		if (this._totalPrice < 0) {
			return;
		}
		this._totalPrice += price * ch_count;
		this._totalPriceTaxIncluded += /*this._taxCalc.layTax(price)*/price * ch_count;
		this._totalWeight += shopItem.getItem().getWeight() * ch_count * shopItem.getPackCount();

		if (shopItem.getItem().isStackable()) {
			this._list.add(new L1ShopBuyOrder(shopItem, ch_count * shopItem.getPackCount()));
			return;
		}

		for (int i = 0; i < (ch_count * shopItem.getPackCount()); i++) {
			this._list.add(new L1ShopBuyOrder(shopItem, 1));
		}
	}

	List<L1ShopBuyOrder> getList() {
		return this._list;
	}

	public int getTotalWeight() {
		return this._totalWeight;
	}

	public int getTotalPrice() {
		return this._totalPrice;
	}

	public int getTotalPriceTaxIncluded() {
		return this._totalPriceTaxIncluded;
	}

	L1TaxCalculator getTaxCalculator() {
		return this._taxCalc;
	}
}
