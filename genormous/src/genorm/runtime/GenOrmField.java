package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

public abstract class GenOrmField
	{
	protected GenOrmFieldMeta m_fieldMeta;
	protected boolean m_isNull;
	
	public GenOrmField(GenOrmFieldMeta gofm)
		{
		m_fieldMeta = gofm;
		m_isNull = true;
		}
		
	public GenOrmFieldMeta getFieldMeta() { return (m_fieldMeta); }
	
	public void setNull()
		{
		m_isNull = true;
		}
		
	public boolean isNull() { return (m_isNull); }
	
	public abstract void setValue(ResultSet rs, int pos) throws java.sql.SQLException;
	public abstract void placeValue(PreparedStatement ps, int pos) throws java.sql.SQLException;
	//public abstract String getSQLValue();
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	public abstract String toString();
	}
