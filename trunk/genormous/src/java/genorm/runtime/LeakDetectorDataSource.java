package genorm.runtime;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.io.PrintWriter;

public class LeakDetectorDataSource extends DataSourceWrapper
	{
	private Set<Connection> m_connectionSet;
	private Map<Connection, String> m_connectionStack;
	private int m_stackStart;
	private int m_stackStop;
	
	public LeakDetectorDataSource(DataSource ds, int stackStart, int stackStop)
		{
		super(ds);
		m_connectionSet = new HashSet<Connection>();
		m_connectionStack = new HashMap<Connection, String>();
		m_stackStart = stackStart;
		m_stackStop = stackStop;
		}
		
	//---------------------------------------------------------------------------
	@Override
	public synchronized Connection getConnection()
			throws java.sql.SQLException
		{
		Connection con = m_dataSource.getConnection();
		if (!m_connectionSet.add(con))
			System.out.println("LEAK DETECTOR - Already handed out: "+con);
			
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (int I = m_stackStart; (I < stack.length && I < m_stackStop); I++)
			sb.append("      ").append(stack[I].toString()).append("\n");
			
		m_connectionStack.put(con, sb.toString());
		return (con);
		}
		
	//---------------------------------------------------------------------------
	public synchronized int listOpenConnections()
			throws java.sql.SQLException
		{
		int count = 0;
		for (Connection c : m_connectionSet)
			{
			if (!c.isClosed())
				{
				System.out.println("OPEN CONNECTION: "+m_connectionStack.get(c));
				count ++;
				}
			}
			
		return (count);
		}
		
	
	}