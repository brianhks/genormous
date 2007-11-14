package $package$.genorm;

import java.util.ArrayList;
import java.sql.SQLException;

public interface GenOrmRecordFactory
	{
	public String getCreateStatement();
	public GenOrmRecord find(Object keys) throws SQLException;
	public GenOrmRecord create() throws SQLException;
	public GenOrmRecord createWithGeneratedKey() throws SQLException;
	public GenOrmResultSet select(String where) throws SQLException;
	public GenOrmResultSet select(String where, String orderBy) throws SQLException;
	}
	
