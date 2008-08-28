package genorm.runtime;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

public class GenOrmConnection
	{
	private Connection m_connection;
	private boolean m_closeConnection;
	private ArrayList<GenOrmRecord> m_transactionList;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	private Map<GenOrmRecord, GenOrmRecord> m_uniqueRecordMap;  //This map is used to ensure only one instance of a record exists in this trasaction
	private boolean m_committed;
	private boolean m_initializedConnection;
	
	public GenOrmConnection(GenOrmDSEnvelope dse)
		{
		this(dse, null);
		}
	
	public GenOrmConnection(GenOrmDSEnvelope dse, Connection con)
		{
		m_connection = null;
		m_transactionList = new ArrayList<GenOrmRecord>();
		m_uniqueRecordMap = new HashMap<GenOrmRecord, GenOrmRecord>();
		m_initializedConnection = false;
		
		try
			{
			//Check to see if the connection is already open
			//Grab a stack trace here and store it.
			if (con != null)
				{
				m_connection = con;
				m_closeConnection = false;
				}
			else
				{
				m_connection = dse.getDataSource().getConnection();
				m_closeConnection = true;
				}
			m_keyGenMap = dse.getKeyGeneratorMap();
			m_connection.setAutoCommit(false);
			m_committed = false;
			m_initializedConnection = true;
			}
		catch (SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	/**
		Returns a unique record instance for this transaction
	*/
	public GenOrmRecord getUniqueRecord(GenOrmRecord rec)
		{
		GenOrmRecord urec = m_uniqueRecordMap.get(rec);
		if (urec == null)
			{
			urec = rec;
			m_uniqueRecordMap.put(rec, rec);
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
	public GenOrmRecord getCachedRecord(GenOrmRecord rec)
		{
		return (m_uniqueRecordMap.get(rec));
		}
		
	public void commit()
		{
		if (!m_initializedConnection)
			return;
			
		try
			{
			Iterator<GenOrmRecord> it = m_transactionList.iterator();
			while (it.hasNext())
				it.next().createIfNew();
				
			it = m_transactionList.iterator();
			while (it.hasNext())
				it.next().commitChanges();
				
			if (m_closeConnection)
				m_connection.commit();
			m_transactionList.clear();
			m_uniqueRecordMap.clear();
			m_committed = true;
			}
		catch (SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	public void close()
		{
		if (!m_initializedConnection)
			return;
			
		try
			{
			if (!m_committed)
				rollback();
				
			if (m_closeConnection)
				m_connection.close();
			m_connection = null;
			}
		catch (SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	public void rollback()
		{
		if (!m_initializedConnection)
			return;
			
		try
			{
			m_transactionList.clear();
			m_connection.rollback();
			}
		catch (SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	public Connection getConnection()
		{
		return (m_connection);
		}
		
	public GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (m_keyGenMap.get(table));
		}
		
	public void addToTransaction(GenOrmRecord goi)
		{
		m_transactionList.add(goi);
		}
	}
