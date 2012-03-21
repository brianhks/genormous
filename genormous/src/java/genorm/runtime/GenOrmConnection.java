package genorm.runtime;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

public interface GenOrmConnection
	{
	/**
		Sets a property to be associated with this connection
	*/
	public void setProperty(String name, Object value);
		
	/**
		Gets a property set on this connection
	*/
	public Object getProperty(String name);
		
	/**
		Returns a unique record instance for this transaction.
		If the record is in the cache it returns the cached record otherwise the 
		record is added to the cache and returned
	*/
	public GenOrmRecord getUniqueRecord(GenOrmRecord rec);
		
	/**
		Returns the cached record if it exists, null otherwise
	*/
	public GenOrmRecord getCachedRecord(GenOrmRecordKey key);
		
	/**
		Checks to see if a record key is part of this transaction.
		This is used by the GenOrmRecord to see if a foreign key is begin created
		at the same time.
		
		TODO: Sort out the records in order of depenendency and change this to 
		only return records that have not been commited.
	*/
	///*package*/ boolean isInTransaction(GenOrmRecordKey key);
		
		
	/**
		Flush all modified records that are part of the current transaction
	*/
	public void flush();
		
	public void commit();
		
	public boolean isCommitted();
		
	public void close();
		
	public void rollback();
		
	public Connection getConnection();
		
	public GenOrmKeyGenerator getKeyGenerator(String table);
		
	/**
		Returns true if the record was added to a transaction
		returns false if there is no transaction
	*/
	public boolean addToTransaction(GenOrmRecord goi);
	
	public Statement createStatement()
			throws SQLException;
			
	public PreparedStatement prepareStatement(String sql)
			throws SQLException;
	}
