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
	This behaves similar to GenOrmTransactionConnection except a connection is 
	not held open.  All records that have been modified are flushed using 
	separate connections when commit is called on this object.
*/
public class GenOrmGroupedConnection implements GenOrmConnection
	{
	private static final Logger s_logger = LoggerFactory.getLogger(GenOrmConnection.class.getName());
	
	private ArrayList<GenOrmRecord> m_transactionList;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	private GenOrmDSEnvelope m_envelope;
	private Map<GenOrmRecordKey, GenOrmRecord> m_uniqueRecordMap;  //This map is used to ensure only one instance of a record exists in this trasaction
	private boolean m_committed;
	private Map<String, Object> m_properties;
	
	public GenOrmGroupedConnection(GenOrmDSEnvelope dse)
		{
		m_properties = new HashMap<String, Object>();
		m_transactionList = new ArrayList<GenOrmRecord>();
		m_uniqueRecordMap = new HashMap<GenOrmRecordKey, GenOrmRecord>();
		
		m_envelope = dse;
		m_committed = false;
		}
		
	/**
		Sets a property to be associated with this connection
	*/
	public void setProperty(String name, Object value)
		{
		m_properties.put(name, value);
		}
		
	/**
		Gets a property set on this connection
	*/
	public Object getProperty(String name)
		{
		return (m_properties.get(name));
		}
		
	/**
		Returns a unique record instance for this transaction.
		If the record is in the cache it returns the cached record otherwise the 
		record is added to the cache and returned
	*/
	public GenOrmRecord getUniqueRecord(GenOrmRecord rec)
		{
		GenOrmRecord urec = m_uniqueRecordMap.get(rec.getRecordKey());
		if (urec == null)
			{
			urec = rec;
			m_uniqueRecordMap.put(rec.getRecordKey(), rec);
			}
		else
			{ //Need to ignore the new one
			rec.setIgnored(true);
			}
			
		return (urec);
		}
		
	/**
		Returns the cached record if it exists, null otherwise
	*/
	public GenOrmRecord getCachedRecord(GenOrmRecordKey key)
		{
		return (m_uniqueRecordMap.get(key));
		}
		
	/**
		Checks to see if a record key is part of this transaction.
		This is used by the GenOrmRecord to see if a foreign key is begin created
		at the same time.
		
		TODO: Sort out the records in order of depenendency and change this to 
		only return records that have not been commited.
	*/
	/*package*/ boolean isInTransaction(GenOrmRecordKey key)
		{
		return (m_uniqueRecordMap.containsKey(key));
		}
		
		
	/**
		Flush all modified records that are part of the current transaction
	*/
	public void flush()
		{
		GenOrmRecord currentRecord = null;
		//System.out.println("Flushing transaction list ("+m_transactionList.size()+")");
		try
			{
			Iterator<GenOrmRecord> it = m_transactionList.iterator();
			while (it.hasNext())
				{
				currentRecord = it.next();
				currentRecord.createIfNew(this);
				}
				
			it = m_transactionList.iterator();
			while (it.hasNext())
				{
				currentRecord = it.next();
				currentRecord.commitChanges();
				}
				
			m_transactionList.clear();
			}
		catch (SQLException sqle)
			{
			s_logger.error(sqle.getMessage(), sqle);
			if (currentRecord != null)
				throw new GenOrmException(currentRecord, sqle);
			else
				throw new GenOrmException(sqle);
			}
		}
		
	public void commit()
		{
		flush();
			
		m_uniqueRecordMap.clear();
		m_committed = true;
		}
		
	public boolean isCommitted() { return (m_committed); }
		
	public void close()
		{
		}
		
	public void rollback()
		{
		}
		
	public Connection getConnection()
		{
		return (null);
		}
		
	public GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (m_envelope.getKeyGenerator(table));
		}
		
	public boolean addToTransaction(GenOrmRecord goi)
		{
		m_transactionList.add(goi);
		return (true);
		}
		
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
