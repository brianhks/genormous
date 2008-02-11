package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

public abstract class GenOrmField
	{
	protected GenOrmFieldMeta m_fieldMeta;
	
	public GenOrmField(GenOrmFieldMeta gofm)
		{
		m_fieldMeta = gofm;
		}
		
	public GenOrmFieldMeta getFieldMeta() { return (m_fieldMeta); }
	
	public abstract void setValue(ResultSet rs, int pos) throws java.sql.SQLException;
	public abstract void placeValue(PreparedStatement ps, int pos) throws java.sql.SQLException;
	//public abstract String getSQLValue();
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	public abstract String toString();
	}
