package genorm.runtime;

import java.sql.*;
import java.util.*;

public interface GenOrmResultSet
	{
	public void close();
	/**
		Returns the result set as an array and cloases the result set
		@param maxRows if the result set contains more than the max
			rows an exception is thrown.
	*/
	public ArrayList<? extends GenOrmRecord> getArrayList(int maxRows);
	/**
		Returns the result set as an array and cloases the result set
	*/
	public ArrayList<? extends GenOrmRecord> getArrayList();
	public java.sql.ResultSet getResultSet();
	public GenOrmRecord getRecord();
	public GenOrmRecord getOnlyRecord();
	public boolean next();
	}
