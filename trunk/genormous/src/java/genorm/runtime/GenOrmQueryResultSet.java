package genorm.runtime;

import java.sql.*;
import java.util.*;

public interface GenOrmQueryResultSet
	{
	public void close();
	/**
		Returns the result set as an array and cloases the result set
		@param maxRows if the result set contains more than the max
			rows an exception is thrown.
	*/
	public List<? extends GenOrmQueryRecord> getArrayList(int maxRows);
	/**
		Returns the result set as an array and cloases the result set
	*/
	public List<? extends GenOrmQueryRecord> getArrayList();
	public java.sql.ResultSet getResultSet();
	public GenOrmQueryRecord getRecord();
	public GenOrmQueryRecord getOnlyRecord();
	public boolean next();
	}
