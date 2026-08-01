// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   L1QueryUtil.java

package com.lineage.server.utils;

import com.lineage.DatabaseFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.lineage.server.utils:
//			SQLUtil

public class L1QueryUtil
{
	public static interface EntityFactory
	{

		public abstract Object fromResultSet(ResultSet resultset)
			throws SQLException;
	}


	public L1QueryUtil()
	{
	}

	private static void setupPrepareStatement(PreparedStatement pstm, Object args[])
		throws SQLException
	{
		for (int i = 0; i < args.length; i++) {
			pstm.setObject(i + 1, args[i]);
		}

	}

	public static Object selectFirst(EntityFactory factory, String sql, Object args[])
	{
		List result = selectAll(factory, sql, args);
		return result.isEmpty() ? null : result.get(0);
	}

	public static List selectAll(EntityFactory factory, String sql, Object args[])
	{
		ArrayList result = new ArrayList();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(sql);
			setupPrepareStatement(pstm, args);
			Object entity;
			for (rs = pstm.executeQuery(); rs.next(); result.add(entity))
			{
				entity = factory.fromResultSet(rs);
				if (entity == null) {
					throw new NullPointerException((new StringBuilder(String.valueOf(factory.getClass().getSimpleName()))).append(" returned null.").toString());
				}
			}

		}
		catch (SQLException e)
		{
			throw new SecurityException(e);
		}
		SQLUtil.close(rs);
		SQLUtil.close(pstm);
		SQLUtil.close(con);
		return result;
	}

	public static boolean execute(Connection con, String sql, Object args[])
	{
		PreparedStatement pstm = null;
		boolean flag;
		try
		{
			pstm = con.prepareStatement(sql);
			setupPrepareStatement(pstm, args);
			flag = pstm.execute();
		}
		catch (SQLException e)
		{
			throw new SecurityException(e);
		}
		finally
		{
			SQLUtil.close(pstm);
		}
		SQLUtil.close(pstm);
		return flag;

	}

	public static boolean execute(String sql, Object args[])
	{
		Connection con = null;
		boolean flag;
		try
		{
			con = DatabaseFactory.get().getConnection();
			flag = execute(con, sql, args);
		}
		/*catch (SQLException e)
		{
			throw new SecurityException(e);
		}*/
		finally
		{
			SQLUtil.close(con);
		}
		
		SQLUtil.close(con);
		return flag;

	}
}
