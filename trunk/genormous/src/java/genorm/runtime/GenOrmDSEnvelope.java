/* 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
