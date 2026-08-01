// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   RecordTable.java

package com.lineage.server.datatables;

import com.lineage.server.utils.L1QueryUtil;
import java.sql.Timestamp;

public class RecordTable
{

	private static RecordTable _instance;

	public RecordTable()
	{
	}

	public static RecordTable get()
	{
		if (_instance == null)
			_instance = new RecordTable();
		return _instance;
	}

	public void recordFailureArmor(String pcName, String itemName, String armorName, int armorObjid, String protection, String note, String ip)
	{
		String sql = "INSERT INTO 紀錄_角色_衝裝 (玩家,使用,裝備,編號,保護,說明,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, itemName, armorName, Integer.valueOf(armorObjid), protection, note, ip
		});
	}

	public void recordDeleItem(String pcName, String itemName, int itemCount, int itemObjid, String ip)
	{
		String sql = "INSERT INTO 紀錄_角色_刪物 (玩家,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, itemName, Integer.valueOf(itemCount), Integer.valueOf(itemObjid), ip
		});
	}
	public void recorddiu(String pcName, String itemName, int itemCount, int itemObjid, String ip)
	{
		String sql = "INSERT INTO 紀錄_角色_丟物 (玩家,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, itemName, Integer.valueOf(itemCount), Integer.valueOf(itemObjid), ip
		});
	}
	public void recordjian(String pcName, String itemName, int itemCount, int itemObjid, String ip)
	{
		String sql = "INSERT INTO 紀錄_角色_捡物 (玩家,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, itemName, Integer.valueOf(itemCount), Integer.valueOf(itemObjid), ip
		});
	}
	public void recordeWarehouse(String pcName, String action, String warehouse, String itemName, int itemCount, int itemObjid, String ip)
	{
		String sql = "INSERT INTO 紀錄_角色_倉庫 (玩家,執行,倉庫,道具,數量,編號,IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, action, warehouse, itemName, Integer.valueOf(itemCount), Integer.valueOf(itemObjid), ip
		});
	}
	
	public void recordeTrade(String pcName, String targeName, String itemName, int itemCount, int itemObjid, String ipA, String ipB)
	{
		String sql = "INSERT INTO 紀錄_角色_交易 (交易者,接受者,物品,數量,編號,交易者IP,接受者IP,時間) VALUE (?, ?, ?, ?, ?, ?, ?, SYSDATE())";
		L1QueryUtil.execute(sql, new Object[] {
			pcName, targeName, itemName, Integer.valueOf(itemCount), Integer.valueOf(itemObjid), ipA, ipB
		});
	}

}
