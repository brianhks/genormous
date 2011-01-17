package genorm.runtime;

import java.util.*;
import javax.sql.*;

/**
	<p>One of the classes generated is a class called <code>DSEnvelope</code>.
	<code>DSEnvelope</code> implements this interface and can be used to pass the 
	data source to the {@link GenOrmDataSource} methods.</p>
	
	<p>Here is some code from one of the unit tests that sets up the data source</p>
	<pre>
	jdbcDataSource ds = new jdbcDataSource();
	ds.setDatabase("jdbc:hsqldb:file:"+m_databaseDir+"/testdb");
	ds.setUser("sa");
	ds.setPassword("");
	
	//Test the connection
	Connection c = ds.getConnection();
	assertNotNull(c);
	c.close();
	
	m_dataSource = ds;
	GenOrmDataSource.setDataSource(new DSEnvelope(m_dataSource));
	</pre>
	
	<p>You can override what key generator is returned by subclassing DSEnvelope
	and passing your subclass to {@link GenOrmDataSource}</p>
*/
public interface GenOrmDSEnvelope
	{
	/**
		Returns the <code>java.sql.DataSource</code> that is used to create connections
		from.
	*/
	public DataSource getDataSource();
	
	
	public void initialize();
		
	/**
		Returns the key generator for the specified table
	*/
	public GenOrmKeyGenerator getKeyGenerator(String table);
	}
