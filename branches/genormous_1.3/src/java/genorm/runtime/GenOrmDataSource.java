package genorm.runtime;

import java.sql.*;
import javax.sql.DataSource;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

/**
	<p>This class is at the heart of handling database connections for all Genormous
	objects.  The static methods on this class manipulate connections that are 
	stored on the thread local data.
	</p>
	
	<p>The thread local storage is used so the developer does not have to pass the
	database connection around.  If your application has one database you can set
	set the default data source by calling {@link #setDataSource(GenOrmDataSource)}, 
	which sets the static member <code>s_dsEnvelope</code>.  Then throughout your
	application you can call <code>GenOrmDataSource.begin()</code> to begin a 
	transaction.  A connection will be created from the default data source and 
	the connection will be placed on the thread.</p>
	
	<p>You can also provide the connection or data source when you call begin by using
	either {@link #begin(GenOrmDSEnvelope)} or {@link #begin(Connection)}.</p>
	
	<p>Only one connection can be the current one at a time but, you can nest connections</p>
	<pre>
	//Open first db connection
	GenOrmDataSource.begin();
	...
	//Open second db connection to different db
	GenOrmDataSource.begin("key to other data source");
	//Make calls using second db connection
	...
	GenOrmDataSource.commit();
	GenOrmDataSource.close();
	
	//Now make calls to first db connection
	...
	GenOrmDataSource.commit();
	GenOrmDataSource.close();
	</pre>
	
	
*/
public class GenOrmDataSource
	{
	/**
		The default data source to use
	*/
	public static GenOrmDSEnvelope s_dsEnvelope;

	/**
		Map of keys to data sources.
	*/
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
		
	/**
		Sets the default data source used to create connections for each thread
		@param ds Envenlope containing the data source to use.
	*/
	public static void setDataSource(GenOrmDSEnvelope ds)
		{
		s_dsEnvelope = ds;
		}
		
	/**
		Associates a datasource with a key.  Later you can call 
		{@link #begin(String)} and pass the key associated with the datasource
		@param key Key to store the data source under
		@param ds Data source envelope
	*/
	public static void setDataSource(String key, GenOrmDSEnvelope ds)
		{
		s_dataSourceMap.put(key, ds);
		}
		
	/**
		Begin a transaction using a connection retrieved by first looking up the 
		data source with the <code>source</code> parameter.
		@param source Key used to lookup the data source to use to create the connection.
	*/
	public static void begin(String source)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dataSourceMap.get(source)));
		}
		
	/**
		Begin a transaction using the data source passed into the method
		@param source Data source used to create a connection.
	*/
	public static void begin(GenOrmDSEnvelope source)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(source));
		}
		
	/**
		Begin a transaction using the Connection passed in.
		@param con Connection to use
	*/
	public static void begin(Connection con)
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dsEnvelope, con));
		}

	/**
		Begin a transaction using the default data source that was set using
		{@link #setDataSource(GenOrmDataSource)}
	*/
	public static void begin()
		{
		s_tlConnectionList.get().addFirst(new GenOrmConnection(s_dsEnvelope));
		}

	/**
		Flush all modified records on the current connection
	*/
	public static void flush()
		{
		s_tlConnectionList.get().peek().flush();
		}
		
	/**
		Commit the transaciton on the current connection
	*/
	public static void commit()
		{
		s_tlConnectionList.get().peek().commit();
		}
		
	/**
		Close the current connection
	*/
	public static void close()
		{
		s_tlConnectionList.get().remove().close();
		}
		
	/**
		Roll back the current connection
	*/
	public static void rollback()
		{
		s_tlConnectionList.get().peek().rollback();
		}
		
	/**
		Return the {@link GenOrmConnection} from off the thread local data
		@return Returns the GenOrmConnection or null if one is not set
	*/
	public static GenOrmConnection getGenOrmConnection()
		{
		return (s_tlConnectionList.get().peek());
		}
		
	/**
		Return the java.sql.Connection object from off the thread local data
		@return Returns the java.sql.Connection or null if there is not a current
		connection set on the thread
	*/
	public static Connection getConnection()
		{
		GenOrmConnection genCon = getGenOrmConnection();
		if (genCon != null)
			return (genCon.getConnection());
		else
			return (null);
		}
		
	/**
		Returns the {@link GenOrmKeyGenerator} that is associated with the
		specified table
		@param table The SQL table to get the key generator for.
		@return Returns the GenOrmKeyGenerator or null if there is not a current
		connection set on the thread.
	*/
	public static GenOrmKeyGenerator getKeyGenerator(String table)
		{
		GenOrmConnection genCon = getGenOrmConnection();
		if (genCon != null)
			return (genCon.getKeyGenerator(table));
		else
			return (null);
		}
		
	/**
		Creates a <code>java.sql.Statement</code> using the current connection on 
		the thread
		@return Returns a Statement or null if there is not a current connection set
		on the thread
	*/
	public static Statement createStatement()
			throws SQLException
		{
		GenOrmConnection genCon = getGenOrmConnection();
		if (genCon != null)
			return (genCon.getConnection().createStatement());
		else
			throw new SQLException("Transaction has not been started");
		}
		
	/**
		Creates a <code>java.sql.PreparedStatement</code> using the current connection on 
		the thread
		@return Returns a PreparedStatement or null if there is not a current connection set
		on the thread
	*/
	public static PreparedStatement prepareStatement(String sql)
			throws SQLException
		{
		GenOrmConnection goc = s_tlConnectionList.get().peek();
		if (goc != null)
			return (goc.getConnection().prepareStatement(sql));
		else
			throw new SQLException("Transaction has not been started");
		}
	
	/**
		Shortcut to process SQL using the current connection.  This method is the same
		as calling 
		<code>Statement stmt = createStatement();
		stmt.executeUpdate(sql);
		stmt.close();</code>
		@param sql SQL update to process
	*/
	public static int rawUpdate(String sql)
			throws SQLException
		{
		Statement stmt = createStatement();
		int ret = stmt.executeUpdate(sql);
		stmt.close();
		return (ret);
		}
	}
