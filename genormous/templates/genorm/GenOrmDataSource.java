package $package$.genorm;

import java.sql.*;
import javax.sql.DataSource;
import java.util.Map;
import java.util.HashMap;

public class GenOrmDataSource
	{
	public static GenOrmDSEnvelope s_dsEnvelope;

	public static Map<String, GenOrmDSEnvelope> s_dataSourceMap = new HashMap<String, GenOrmDSEnvelope>();
	
	private static ThreadLocal<GenOrmConnection> s_tlConnection = new ThreadLocal<GenOrmConnection>()
			{
			@Override
			protected synchronized GenOrmConnection initialValue()
				{
				return (new GenOrmConnection());
				}
			};
			
	public static void setDataSource(DataSource ds)
		{
		s_dsEnvelope = new GenOrmDSEnvelope(ds);
		}
		
	public static void setDataSource(String key, DataSource ds)
		{
		s_dataSourceMap.put(key, new GenOrmDSEnvelope(ds));
		}
		
	public static void begin(String source)
		{
		s_tlConnection.get().begin(s_dataSourceMap.get(source));
		}
		
	public static void begin(GenOrmDSEnvelope source)
		{
		s_tlConnection.get().begin(source);
		}

	public static void begin()
		{
		s_tlConnection.get().begin(s_dsEnvelope);
		}
		
	public static void commit()
		{
		s_tlConnection.get().commit();
		}
		
	public static void close()
		{
		s_tlConnection.get().close();
		}
		
	public static void rollback()
		{
		s_tlConnection.get().rollback();
		}
		
	public static GenOrmConnection getGenOrmConnection()
		{
		return (s_tlConnection.get());
		}
		
	public static Connection getConnection()
		{
		return (s_tlConnection.get().getConnection());
		}
		
	public static GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (s_tlConnection.get().getKeyGenerator(table));
		}
		
	public static Statement createStatement()
			throws SQLException
		{
		return (s_tlConnection.get().getConnection().createStatement());
		}
		
	public static PreparedStatement prepareStatement(String sql)
			throws SQLException
		{
		return (s_tlConnection.get().getConnection().prepareStatement(sql));
		}
	}
