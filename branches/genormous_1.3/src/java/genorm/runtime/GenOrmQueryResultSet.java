package genorm.runtime;

import java.sql.*;
import java.util.*;

public interface GenOrmQueryResultSet
	{
	public void close();
	public List<? extends GenOrmQueryRecord> getArrayList(int maxRows);
	public java.sql.ResultSet getResultSet();
	public GenOrmQueryRecord getRecord();
	public GenOrmQueryRecord getOnlyRecord();
	public boolean next();
	}
