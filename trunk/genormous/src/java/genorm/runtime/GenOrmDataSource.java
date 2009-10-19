package genorm.runtime;

import java.sql.*;
import javax.sql.DataSource;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class GenOrmDataSource
	{
	public static GenOrmDSEnvelope s_dsEnvelope;

	public static Map<String, GenOrmDSEnvelope> s_dataSourceMap = new HashMap<String, GenOrmDSEnvelope>();
	
	/**
		The linked list acts as a stack for multiple connections on the same thread.
		Only the top connection is used at a time.
	*/
	private static ThreadLocal<LinkedList<GenOrmConnection>> s_tlConnectionList = new ThreadLocal<LinkedList<GenOrmConnection>>()
			{
			@Override
			protected synchronized LinkedList<GenOrmConnection> initialValue()
				{
				return (new LinkedList<GenOrmConnection>());
				}
			};
			
	public static void setDataSource(GenOrmDSEnvelope ds)
		{
		s_dsEnvelope = ds;
		}
		
	public static void setDataSource(String key, GenOrmDSEnvelope ds)
		{
		s_dataSourceMap.put(key, ds);
		}
		
	public static void begin(String source)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dataSourceMap.get(source)));
		}
		
	public static void begin(GenOrmDSEnvelope source)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(source));
		}
		
	public static void begin(Connection con)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dsEnvelope, con));
		}

	public static void begin()
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dsEnvelope));
		}

	public static void flush()
		{
		s_tlConnectionList.get().peek().flush();
		}
		
	public static void commit()
		{
		s_tlConnectionList.get().peek().commit();
		}
		
	public static void close()
		{
		s_tlConnectionList.get().remove().close();
		}
		
	public static void rollback()
		{
		s_tlConnectionList.get().peek().rollback();
		}
		
	public static GenOrmConnection getGenOrmConnection()
		{
		return (s_tlConnectionList.get().peek());
		}
		
	public static Connection getConnection()
		{
		return (s_tlConnectionList.get().peek().getConnection());
		}
		
	public static GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (s_tlConnectionList.get().peek().getKeyGenerator(table));
		}
		
	public static Statement createStatement()
			throws SQLException
		{
		return (s_tlConnectionList.get().peek().getConnection().createStatement());
		}
		
	public static PreparedStatement prepareStatement(String sql)
			throws SQLException
		{
		GenOrmConnection goc = s_tlConnectionList.get().peek();
		return (goc.getConnection().prepareStatement(sql));
		}
	
	public static int rawUpdate(String sql)
			throws SQLException
		{
		Statement stmt = createStatement();
		int ret = stmt.executeUpdate(sql);
		stmt.close();
		return (ret);
		}
	}
