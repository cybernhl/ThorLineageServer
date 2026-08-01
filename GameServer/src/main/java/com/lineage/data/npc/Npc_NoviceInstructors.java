// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Npc_NoviceInstructors.java

package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_NoviceInstructors extends NpcExecutor
{

	private Npc_NoviceInstructors()
	{
	}

	public static NpcExecutor get()
	{
		return new Npc_NoviceInstructors();
	}

	public int type()
	{
		return 2;
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount)
	{
		if (cmd.equalsIgnoreCase("E"))
			forNewPlayer(pc, npc);
	}

	private void forNewPlayer(L1PcInstance pc, L1NpcInstance npc)
	{
		int objid = npc.getId();
		String htmlid = null;
		if (pc.isCrown())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutorp1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutorp2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutorp3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutorp4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutorp5_bs";
		} else
		if (pc.isKnight())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutork1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutork2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutork3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutork4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutork5_bs";
		} else
		if (pc.isWizard())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutorm1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutorm2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutorm3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutorm4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutorm5_bs";
		} else
		if (pc.isElf())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutore1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutore2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutore3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutore4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutore5_bs";
		} else
		if (pc.isDarkelf())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutord1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutord2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutord3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutord4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutord5_bs";
		} else
		if (pc.isDarkelf())
		{
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutordk1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutordk2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutordk3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutordk4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutordk5_bs";
		} else
		if (pc.isDarkelf())
			if (pc.getLevel() >= 1 && pc.getLevel() <= 4)
				htmlid = "tutori1_bs";
			else
			if (pc.getLevel() >= 5 && pc.getLevel() <= 7)
				htmlid = "tutori2_bs";
			else
			if (pc.getLevel() >= 8 && pc.getLevel() <= 9)
				htmlid = "tutori3_bs";
			else
			if (pc.getLevel() >= 10 && pc.getLevel() <= 12)
				htmlid = "tutori4_bs";
			else
			if (pc.getLevel() >= 13)
				htmlid = "tutori5_bs";
		pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
	}
}
