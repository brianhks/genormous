package genorm.runtime;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

public class GenOrmTransactionConnection implements GenOrmConnection
	{
	private static final Logger s_logger = LoggerFactory.getLogger(GenOrmTransactionConnection.class.getName());
	
	private Connection m_connection;
	private boolean m_closeConnection;
	private ArrayList<GenOrmRecord> m_transactionList;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	private GenOrmDSEnvelope m_envelope;
	private Map<GenOrmRecordKey, GenOrmRecord> m_uniqueRecordMap;  //This map is used to ensure only one instance of a record exists in this trasaction
	private boolean m_committed;
	private boolean m_initializedConnection;
	private Map<String, Object> m_properties;
	private boolean m_transaction;
	
	public GenOrmTransactionConnection(GenOrmDSEnvelope dse)
		{
		this(dse, null);
		}
	
	public GenOrmTransactionConnection(GenOrmDSEnvelope dse, Connection con)
		{
		m_properties = new HashMap<String, Object>();
		m_connection = null;
		m_envelope = dse;
		m_transactionList = new ArrayList<GenOrmRecord>();
		m_uniqueRecordMap = new HashMap<GenOrmRecordKey, GenOrmRecord>();
		m_initializedConnection = false;
		m_transaction = true;
		m_closeConnection = false;
		}
		
	//---------------------------------------------------------------------------
	public GenOrmTransactionConnection setTransaction(boolean transaction)
		{
		m_transaction = transaction;
		return (this);
		}
		
	//---------------------------------------------------------------------------
	public void begin()
		{
		s_logger.debug("Begin called");
		try
			{
			//Check to see if the connection is already open
			
			//If we are doing this in a transaction then open and hold on to a connection
			if (m_transaction)
				{
				s_logger.debug("Beginning transaction");
				if (m_connection == null)
					{
					m_connection = m_envelope.getDataSource().getConnection();
					m_closeConnection = true;
					}
					
				m_connection.setAutoCommit(false);
				}
				
			m_committed = false;
			m_initializedConnection = true;
			}
		catch (SQLException sqle)
			{
			s_logger.error(sqle.getMessage(), sqle);
			throw new GenOrmException(sqle);
			}
		}
		
	//---------------------------------------------------------------------------	
	/**
		Sets a property to be associated with this connection
	*/
	public void setProperty(String name, Object value)
		{
		m_properties.put(name, value);
		}
		
	//---------------------------------------------------------------------------
	/**
		Gets a property set on this connection
	*/
	public Object getProperty(String name)
		{
		return (m_properties.get(name));
		}
		
	//---------------------------------------------------------------------------
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
		
	//---------------------------------------------------------------------------
	/**
		Returns the cached record if it exists, null otherwise
	*/
	public GenOrmRecord getCachedRecord(GenOrmRecordKey key)
		{
		return (m_uniqueRecordMap.get(key));
		}
		
	//---------------------------------------------------------------------------
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
		
		
	//---------------------------------------------------------------------------
	/**
		Flush all modified records that are part of the current transaction
	*/
	public void flush()
		{
		s_logger.debug("Flushing transaction");
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
		
	//---------------------------------------------------------------------------
	public void commit()
		{
		s_logger.debug("Committing");
		if (!m_initializedConnection)
			return;
		
		flush();
			
		try
			{
			if (m_closeConnection)
				{
				s_logger.debug("Calling connection commit");
				m_connection.commit();
				}
			
			m_uniqueRecordMap.clear();
			m_committed = true;
			}
		catch (SQLException sqle)
			{
			s_logger.error(sqle.getMessage(), sqle);
			throw new GenOrmException(sqle);
			}
		}
		
	//---------------------------------------------------------------------------
	public boolean isCommitted() { return (m_committed); }
	
	//---------------------------------------------------------------------------	
	public void close()
		{
		s_logger.debug("Closing");
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
			s_logger.error(sqle.getMessage(), sqle);
			throw new GenOrmException(sqle);
			}
		}
		
	//---------------------------------------------------------------------------
	public void rollback()
		{
		s_logger.debug("Rolling back");
		if (!m_initializedConnection)
			return;
			
		try
			{
			s_logger.debug("Rolling back connection");
			m_transactionList.clear();
			
			if (m_connection != null)
				m_connection.rollback();
			}
		catch (SQLException sqle)
			{
			s_logger.error(sqle.getMessage(), sqle);
			throw new GenOrmException(sqle);
			}
		}
		
	//---------------------------------------------------------------------------
	public void setConnection(Connection con)
		{
		if (m_initializedConnection)
			throw new GenOrmException("Cannot modify connection once a transaction has begun");
			
		m_connection = con;
		}
		
	//---------------------------------------------------------------------------
	public Connection getConnection()
		{
		return (m_connection);
		}
		
	//---------------------------------------------------------------------------
	public GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (m_envelope.getKeyGenerator(table));
		}
		
	//---------------------------------------------------------------------------
	public void addToTransaction(GenOrmRecord goi)
		{
		m_transactionList.add(goi);
		}
		
	//---------------------------------------------------------------------------
	/**
		The connection will be closed when the statement is closed
	*/
	public Statement createStatement()
			throws SQLException
		{
		if (m_connection != null)
			return (m_connection.createStatement());
		else
			{
			Connection con = m_envelope.getDataSource().getConnection();
			con.setAutoCommit(true);
			Statement stmt = con.createStatement();
			return (new GenOrmStatement(con, stmt));
			}
		}
			
	//---------------------------------------------------------------------------
	/**
		The connection will be closed when the statement is closed
	*/
	public PreparedStatement prepareStatement(String sql)
			throws SQLException
		{
		if (m_connection != null)
			return (m_connection.prepareStatement(sql));
		else
			{
			Connection con = m_envelope.getDataSource().getConnection();
			con.setAutoCommit(true);
			PreparedStatement stmt = con.prepareStatement(sql);
			return (new GenOrmPreparedStatement(con, stmt));
			}
		}
	}
