package $package$.genorm;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

public class GenOrmConnection
	{
	private Connection m_connection;
	private ArrayList<GenOrmRecord> m_transactionList;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	
	public GenOrmConnection()
		{
		m_connection = null;
		m_transactionList = new ArrayList<GenOrmRecord>();
		}
		
	public void begin(GenOrmDSEnvelope dse)
			throws SQLException
		{
		//Check to see if the connection is already open
		//Grab a stack trace here and store it.
		m_connection = dse.getDataSource().getConnection();
		m_keyGenMap = dse.getKeyGeneratorMap();
		m_connection.setAutoCommit(false);
		}
		
	public void commit()
			throws SQLException
		{
		Iterator<GenOrmRecord> it = m_transactionList.iterator();
		while (it.hasNext())
			it.next().commitChanges();
			
		m_connection.commit();
		m_transactionList.clear();
		}
		
	public void close()
			throws SQLException
		{
		m_connection.close();
		m_connection = null;
		}
		
	public void rollback()
			throws SQLException
		{
		m_transactionList.clear();
		m_connection.rollback();
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
