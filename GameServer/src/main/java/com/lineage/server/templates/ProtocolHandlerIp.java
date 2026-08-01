package com.lineage.server.templates;

/**
 * 連接IP暫存列陣<br>
 * 類名稱：ProtocolHandlerIp<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月25日 上午10:06:42<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public final class ProtocolHandlerIp {
	private String ip;
	private Integer count;
	private Long time;
	private Boolean block;

	/**
	 * 傳回當前登錄IP
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 設置當前登錄IP
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 傳回此IP連接次數
	 * @return
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * 設置此IP連接次數
	 * @param count
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * 傳回此IP登錄時間
	 * @return
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * 設置此IP登錄時間
	 * @param time
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * 傳回此IP是否忽略連接
	 * @return
	 */
	public Boolean getBlock() {
		return block;
	}

	/**
	 * 設置此IP是否忽略連接
	 * @param block
	 */
	public void setBlock(Boolean block) {
		this.block = block;
	}
}
