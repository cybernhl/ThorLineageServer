package com.lineage.server.utils;

/**
 * 
 * 類名稱：URandom<br>
 * 類描述：新的隨機機制<br>
 * 創建人:warrior<br>
 * 修改時間：2016年4月18日 下午2:22:09<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public class URandom {
	
	private static final java.util.Random _random = new java.util.Random();
	
	private static final int _max = Short.MAX_VALUE;

	private static int _idx = 0;

	private static double[] _value = new double[_max + 1];

	static {
		for (_idx = 0; _idx < _max + 1; _idx ++) {
			_value[_idx] = (Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) % 1.0;
		}
	}
	
	/**
	 * 從指定數字範圍內取出隨機數<BR>
	 * @param n 10
	 * @return 0~9 隨機
	 * */
	public static int nextInt(int n) {
		_idx = _idx & _max;
		return (int) (_value[_idx ++] * n);
	}
	
	/**
	 * 從指定數字範圍內取出隨機數並偏移指定位數<BR>
	 * @param n 10
	 * @param offset 1
	 * @return 1~10 隨機
	 * */
	public static int nextInt(int n, int offset) {
		_idx = _idx & _max;
		return offset + (int) (_value[_idx ++] * n);
	}
	
	/**
	 * 取出隨機的布爾值
	 * @return
	 * */
	public static boolean nextBoolean() {
		return (nextInt(2) == 1);
	}
	
	/**
	 * 取出隨機的Byte值
	 * @return 0~255 隨機
	 * */
	public static byte nextByte() {
		return (byte) nextInt(256);
	}
	
	/**
	 * 取出隨機的Long值
	 * @return
	 * */
	public static long nextLong() {
		long value = nextInt(Integer.MAX_VALUE) << 32 + nextInt(Integer.MAX_VALUE);
		return value;
	}
	
	/**
	 * 取出隨機的Double值(java.util.Random)
	 * @return
	 * */
	public static double nextGaussian() {
		return _random.nextGaussian();
	}
}
