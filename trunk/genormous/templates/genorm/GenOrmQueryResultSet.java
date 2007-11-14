package $package$.genorm;

import java.sql.*;
import java.util.*;

public interface GenOrmQueryResultSet
	{
	public void close();
	public ArrayList<? extends GenOrmQueryRecord> getArrayList(int maxRows);
	public java.sql.ResultSet getResultSet();
	public GenOrmQueryRecord getRecord();
	public GenOrmQueryRecord getOnlyRecord();
	public boolean hasNext();
	}
