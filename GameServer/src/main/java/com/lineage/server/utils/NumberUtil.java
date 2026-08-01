package com.lineage.server.utils;

import java.util.Random;

public class NumberUtil {

	/**
	 * 少數小數點第二位確率上下丸整數返。 例1.330%確率切捨、70%確率切上。
	 *
	 * @param number
	 *            - 少數
	 * @return 丸整數
	 */
	public static int randomRound(final double number) {
		final double percentage = (number - Math.floor(number)) * 100;

		if (percentage == 0) {
			return ((int) number);
			
		} else {
			final int r = new Random().nextInt(100);
			if (r < percentage) {
				return ((int) number + 1);
			} else {
				return ((int) number);
			}
		}
	}
}
