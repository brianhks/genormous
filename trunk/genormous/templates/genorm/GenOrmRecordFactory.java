package $package$.genorm;

import java.util.Collection;
import java.sql.SQLException;

public interface GenOrmRecordFactory
	{
	public String getCreateStatement();
	public GenOrmRecord find(Object keys) throws SQLException;
	public GenOrmRecord create() throws SQLException;
	public GenOrmRecord createWithGeneratedKey() throws SQLException;
	public Collection<? extends GenOrmRecord> select(String where) throws SQLException;
	public Collection<? extends GenOrmRecord> select(String where, String orderBy) throws SQLException;
	}
	
