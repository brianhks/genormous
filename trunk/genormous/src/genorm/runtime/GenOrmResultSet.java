package genorm.runtime;

import java.sql.*;
import java.util.*;

public interface GenOrmResultSet
	{
	public void close();
	public ArrayList<? extends GenOrmRecord> getArrayList(int maxRows);
	public java.sql.ResultSet getResultSet();
	public GenOrmRecord getRecord();
	public GenOrmRecord getOnlyRecord();
	public boolean hasNext();
	}
