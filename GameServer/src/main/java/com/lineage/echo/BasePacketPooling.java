package com.lineage.echo;

import java.util.ArrayList;
import java.util.List;

import com.lineage.server.serverpackets.OpcodesServer;

public final class BasePacketPooling {

	// 動態生成的數據包處理
	static private List<OpcodesServer> pool;

	static public void init() {

		pool = new ArrayList<OpcodesServer>();

	}

	/**
	 * 列表中找到我們
	 * 
	 * @param c
	 * @return
	 */
	static public OpcodesServer getPool(Class<?> c) {
		synchronized (pool) {
			// System.out.printf("pool : %d\r\n", pool.size());

			OpcodesServer bp = find(c);
			// 對像列表中刪除
			if (bp != null)
				pool.remove(bp);

			// System.out.printf("pool : %d\r\n", pool.size());
			return bp;
		}
	}

	static public void setPool(OpcodesServer bp) {
	    try {
	        if (pool.contains(bp)) {
	            pool.add(bp);
	        }
	    } catch (final Exception e) {
	        // System.out.printf("pool : %d\r\n", pool.size());
	    }
	}

	/**
	 * 找到的對象返回
	 * 
	 * @param c
	 * @return
	 */
	static private OpcodesServer find(Class<?> c) {
		for (OpcodesServer b : pool) {
			if (b.getClass().equals(c))
				return b;
		}
		return null;
	}

	static public int getPoolSize() {
		return pool.size();
	}

}
