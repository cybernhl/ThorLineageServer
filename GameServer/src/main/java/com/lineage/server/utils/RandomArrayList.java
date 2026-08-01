/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.utils;

public class RandomArrayList {
	/** 泛用型随机矩阵，所使用的指标 */
	private static int listint = 0;

	/** 新型泛用型，适用Int的正数范围 */
	private static double[] ArrayDouble = new double[32767];

	public static void setArrayList() {
		for (listint = 0; listint < 32767; listint++) {
			ArrayDouble[listint] = Math.random();
		}
	}

	static {
		for (listint = 0; listint < 32767; listint++) {
			ArrayDouble[listint] = Math.random();
		}
	}

	private static int getlistint() {
		if (listint < 32766)
			return ++listint;
		else
			return listint = 0;
	}

	/**
	 * getByte(byte[] 容器) ：模仿Random.nextBytes(byte[]) 制作
	 */
	public static void getByte(byte[] arr) {
		for (int i = 0; i < arr.length; i++)
			arr[i] = (byte) getValue(128);
	}

	private static boolean haveNextGaussian = false;
	private static double nextGaussian;

	/**
	 * getGaussian() ：回传 高斯分配
	 */
	public static double getGaussian() {
		if (haveNextGaussian) {
			haveNextGaussian = false;
			return nextGaussian;
		} else {
			double v1, v2, s;
			do {
				v1 = 2 * ArrayDouble[getlistint()] - 1; // between -1.0 and 1.0
				v2 = 2 * ArrayDouble[getlistint()] - 1; // between -1.0 and 1.0
				s = v1 * v1 + v2 * v2;
			} while (s >= 1 || s == 0);
			double multiplier = Math.sqrt(-2 * Math.log(s) / s);
			nextGaussian = v2 * multiplier;
			haveNextGaussian = true;
			return v1 * multiplier;
		}
	}

	/**
	 * getValue() ：return between 0.0 and 1.0
	 */
	private static double getValue() {
		return ArrayDouble[getlistint()];
	}

	/**
	 *
	 */
	private static double getValue(int rang) {
		return getValue() * rang;
	}

	private static double getValue(double rang) {
		return getValue() * rang;
	}

	/**
	 * getInt(int 数值) 随机值的?静态，速度是nextInt(int 数值) 的数倍 根据呼叫的数值传回
	 * 静态表内加工后的数值,并采共同指标来决定传回的依据. EX:getInt(92988) => 0~92987
	 * 
	 * @param rang
	 *            - Int类型
	 * @return 0 ~ (数值-1)
	 */
	public static int getInt(int rang) {
		return (int) getValue(rang);
	}

	public static int getInt(double rang) {
		return (int) getValue(rang);
	}

	public static double getDouble() {
		return getValue();
	}

	public static double getDouble(double rang) {
		return getValue(rang);
	}

	/**
	 * getInc(int 数值, int 输出偏移值) 随机值的?静态，速度是nextInt(int 数值) 的数倍 根据呼叫的数值传回
	 * 静态表内加工后的数值,并采共同指标来决定传回的依据. EX:getInc(92988, 10) => (0~92987) + 10 =>
	 * 10~92997
	 * 
	 * @param rang
	 *            - Int类型
	 * @param increase
	 *            - 修正输出结果的范围
	 * @return 0 ~ (数值-1) + 输出偏移值
	 */
	public static int getInc(int rang, int increase) {
		return getInt(rang) + increase;
	}

	public static int getInc(double rang, int increase) {
		return getInt(rang) + increase;
	}

	public static double getDc(int rang, int increase) {
		return getValue(rang) + increase;
	}

	public static double getDc(double rang, int increase) {
		return getValue(rang) + increase;
	}
}
