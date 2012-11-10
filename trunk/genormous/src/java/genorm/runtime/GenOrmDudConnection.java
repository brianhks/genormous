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
import javax.sql.DataSource;
import java.sql.*;

/**
	This class is for transactionless connections to the db
	All connections are associated with the statements and closed
	when the statement is closed
*/
public class GenOrmDudConnection implements GenOrmConnection
	{
	private GenOrmDSEnvelope m_envelope;
	
	
	public GenOrmDudConnection(GenOrmDSEnvelope dse)
		{
		m_envelope = dse;
		}
		
	/**
		Sets a property to be associated with this connection
	*/
	public void setProperty(String name, Object value) {}
		
	/**
		Gets a property set on this connection
	*/
	public Object getProperty(String name) { return (null); }
		
	/**
		Returns a unique record instance for this transaction.
		If the record is in the cache it returns the cached record otherwise the 
		record is added to the cache and returned
	*/
	public GenOrmRecord getUniqueRecord(GenOrmRecord rec) { return (rec); }
		
	/**
		Returns the cached record if it exists, null otherwise
	*/
	public GenOrmRecord getCachedRecord(GenOrmRecordKey key) { return (null); }
		
	/**
		Checks to see if a record key is part of this transaction.
		This is used by the GenOrmRecord to see if a foreign key is begin created
		at the same time.
		
		TODO: Sort out the records in order of depenendency and change this to 
		only return records that have not been commited.
	*/
	/*package*/ boolean isInTransaction(GenOrmRecordKey key) { return (false); }
		
		
	/**
		Flush all modified records that are part of the current transaction
	*/
	public void flush() {}
		
	public void commit() {}
		
	public boolean isCommitted() { return (true); } 
		
	public void close() {}
		
	public void rollback() {}
		
	public Connection getConnection() { return (null); }
		
	public GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (m_envelope.getKeyGenerator(table));
		}
		
	public boolean addToTransaction(GenOrmRecord goi) { return (false); }
	
	/**
		The connection will be closed when the statement is closed
	*/
	public Statement createStatement()
			throws SQLException
		{
		Connection con = m_envelope.getDataSource().getConnection();
		con.setAutoCommit(true);
		Statement stmt = con.createStatement();
		return (new GenOrmStatement(con, stmt));
		}
			
	/**
		The connection will be closed when the statement is closed
	*/
	public PreparedStatement prepareStatement(String sql)
			throws SQLException
		{
		Connection con = m_envelope.getDataSource().getConnection();
		con.setAutoCommit(true);
		PreparedStatement stmt = con.prepareStatement(sql);
		return (new GenOrmPreparedStatement(con, stmt));
		}
	}
