package com.lineage.server.datatables.storage;

import java.util.ArrayList;

import com.lineage.server.templates.L1BuddyTmp;

/**
 * 好友清單
 *
 * @author dexc
 *
 */
public interface BuddyStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 取回該人物好友列表
	 * @param pc
	 * @return
	 */
	public ArrayList<L1BuddyTmp> userBuddy(int playerobjid);

	/**
	 * 加入好友清單
	 * @param charId
	 * @param objId
	 * @param name
	 */
	public void addBuddy(int charId, int objId, String name);

	/**
	 * 移出好友清單
	 * @param charId
	 * @param buddyName
	 */
	public void removeBuddy(int charId, String buddyName);
}
