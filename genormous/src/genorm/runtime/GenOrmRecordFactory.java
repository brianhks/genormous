package genorm.runtime;

import java.util.ArrayList;
import java.sql.SQLException;

public interface GenOrmRecordFactory
	{
	/**
		Returns the SQL create statement for this table
	*/
	public String getCreateStatement();
	public GenOrmRecord findRecord(Object keys);
	public GenOrmRecord createRecord();
	public GenOrmRecord createWithGeneratedKey();
	public GenOrmResultSet select(String where);
	public GenOrmResultSet select(String where, String orderBy);
	}
	
