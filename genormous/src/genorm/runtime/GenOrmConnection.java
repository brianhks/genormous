package genorm.runtime;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

public class GenOrmConnection
	{
	private Connection m_connection;
	private ArrayList<GenOrmRecord> m_transactionList;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	private Map<GenOrmRecord, GenOrmRecord> m_uniqueRecordMap;  //This map is used to ensure only one instance of a record exists in this trasaction
	private boolean m_committed;
	
	public GenOrmConnection()
		{
		m_connection = null;
		m_transactionList = new ArrayList<GenOrmRecord>();
		m_uniqueRecordMap = new HashMap<GenOrmRecord, GenOrmRecord>();
		}
		
	public void begin(GenOrmDSEnvelope dse)
		{
		try
			{
			//Check to see if the connection is already open
			//Grab a stack trace here and store it.
			m_connection = dse.getDataSource().getConnection();
			m_keyGenMap = dse.getKeyGeneratorMap();
			m_connection.setAutoCommit(false);
			m_committed = false;
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
			
		return (urec);
		}
		
	public void commit()
		{
		try
			{
			Iterator<GenOrmRecord> it = m_transactionList.iterator();
			while (it.hasNext())
				it.next().createIfNew();
				
			it = m_transactionList.iterator();
			while (it.hasNext())
				it.next().commitChanges();
				
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
		try
			{
			if (!m_committed)
				rollback();
				
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
