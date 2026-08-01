package com.lineage.server.types;

/**
 * 座標左上點(left, top)、及右下點(right, bottom)圍座標領域指定。
 */
public class Rectangle {
	
	private int _left;
	private int _top;
	private int _right;
	private int _bottom;

	public Rectangle(final Rectangle rect) {
		this.set(rect);
	}

	public Rectangle(final int left, final int top, final int right, final int bottom) {
		this.set(left, top, right, bottom);
	}

	public Rectangle() {
		this(0, 0, 0, 0);
	}

	public void set(final Rectangle rect) {
		this.set(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
	}

	public void set(final int left, final int top, final int right, final int bottom) {
		this._left = left;
		this._top = top;
		this._right = right;
		this._bottom = bottom;
	}

	public int getLeft() {
		return this._left;
	}

	public int getTop() {
		return this._top;
	}

	public int getRight() {
		return this._right;
	}

	public int getBottom() {
		return this._bottom;
	}

	public int getWidth() {
		return this._right - this._left;
	}

	public int getHeight() {
		return this._bottom - this._top;
	}

	/**
	 * 指定點(x, y)、Rectangle範圍內判定。
	 *
	 * @param x
	 *            判定點X座標
	 * @param y
	 *            判定點Y座標
	 * @return 點(x, y)Rectangle範圍內場合、true。
	 */
	public boolean contains(final int x, final int y) {
		return ((this._left <= x) && (x <= this._right)) && ((this._top <= y) && (y <= this._bottom));
	}

	/**
	 * 指定Point、Rectangle範圍內判定。
	 *
	 * @param pt
	 *            判定Point
	 * @return ptRectangle範圍內場合、true。
	 */
	public boolean contains(final Point pt) {
		return this.contains(pt.getX(), pt.getY());
	}
}
