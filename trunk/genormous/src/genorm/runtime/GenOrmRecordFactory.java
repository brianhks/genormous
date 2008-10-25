package genorm.runtime;

import java.util.List;
import java.sql.SQLException;

public interface GenOrmRecordFactory
	{
	/**
		Returns the SQL create statement for this table
	*/
	public String getCreateStatement();
	public List<GenOrmFieldMeta> getFields();
	public GenOrmRecord findRecord(Object keys);
	public GenOrmRecord createRecord();
	public GenOrmRecord createWithGeneratedKey();
	public GenOrmResultSet select(String where);
	public GenOrmResultSet select(String where, String orderBy);
	}
	
