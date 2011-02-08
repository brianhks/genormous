package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.Serializable;

public abstract class GenOrmField implements Serializable
	{
	protected GenOrmFieldMeta m_fieldMeta;
	protected boolean m_isNull;
	protected GenOrmRecordKey m_recordKey;  //If this field is a foreign key this is will point to the key.
	
	public GenOrmField(GenOrmFieldMeta gofm)
		{
		m_fieldMeta = gofm;
		m_isNull = true;
		m_recordKey = null;
		}
		
	public GenOrmFieldMeta getFieldMeta() { return (m_fieldMeta); }
	
	public boolean setNull()
		{
		if (!m_isNull)
			{
			m_isNull = true;
			return (true);
			}
		else
			return (false);
		}
		
	public boolean isNull() { return (m_isNull); }
	
	public abstract void setValue(ResultSet rs, int pos) throws java.sql.SQLException;
	public abstract void placeValue(PreparedStatement ps, int pos) throws java.sql.SQLException;
	//public abstract String getSQLValue();
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	public abstract String toString();
	
	public void setRecordKey(GenOrmRecordKey recordKey) { m_recordKey = recordKey; }
	public GenOrmRecordKey getRecordKey() { return (m_recordKey); }
	}
