package com.lineage.server.utils;

import java.util.Random;

/**
 * <p>
 * 最低值low最大值high圍、數值範圍指定。
 * </p>
 * <p>
 * <b>同期化。</b> 複數同時、
 * 1以上範圍變更場合、外部的同期化必要。
 * </p>
 */
public class RangeInt {
	
	private static final Random _rnd = new Random();
	
	private int _low;
	
	private int _high;

	public RangeInt(final int low, final int high) {
		this._low = low;
		this._high = high;
	}

	public RangeInt(final RangeInt range) {
		this(range._low, range._high);
	}

	/**
	 * 數值i、範圍內返。
	 *
	 * @param i
	 *            數值
	 * @return 範圍內true
	 */
	public boolean includes(final int i) {
		return (this._low <= i) && (i <= this._high);
	}

	public static boolean includes(final int i, final int low, final int high) {
		return (low <= i) && (i <= high);
	}

	/**
	 * 數值i、範圍內丸。
	 *
	 * @param i
	 *            數值
	 * @return 丸值
	 */
	public int ensure(final int i) {
		int r = i;
		r = (this._low <= r) ? r : this._low;
		r = (r <= this._high) ? r : this._high;
		return r;
	}

	/**
	 * 
	 * @param n
	 * @param low
	 * @param high
	 * @return
	 */
	public static int ensure(final int n, final int low, final int high) {
		int r = n;
		r = (low <= r) ? r : low;
		r = (r <= high) ? r : high;
		return r;
	}

	/**
	 * 範圍內值生成。
	 *
	 * @return 範圍內值
	 */
	public int randomValue() {
		return _rnd.nextInt(this.getWidth() + 1) + this._low;
	}

	public int getLow() {
		return this._low;
	}

	public int getHigh() {
		return this._high;
	}

	public int getWidth() {
		return this._high - this._low;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RangeInt)) {
			return false;
		}
		final RangeInt range = (RangeInt) obj;
		return (this._low == range._low) && (this._high == range._high);
	}

	@Override
	public String toString() {
		return "low=" + this._low + ", high=" + this._high;
	}
}
