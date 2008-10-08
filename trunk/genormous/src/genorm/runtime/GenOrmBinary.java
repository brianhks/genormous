package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.Arrays;

public class GenOrmBinary extends GenOrmField
	{
	private byte[] m_value;
	
	public GenOrmBinary(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public boolean setValue(byte[] value)
		{
		if (((m_value == null) && (value != null)) || 
				((m_value != null) && (!m_value.equals(value))))
			{
			m_value = value;
			if (m_value == null)
				setNull();
			else
				m_isNull = false;
			return (true);
			}
		else
			return (false);
		}
		
	public byte[] getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBytes(pos);
		m_isNull = rs.wasNull();
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setBytes(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return ("");
		}
		
	public int hashCode()
		{
		if (m_value == null)
			return (0);
		else
			return (Arrays.hashCode(m_value));
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmBinary))
			return (false);
			
		GenOrmBinary other = (GenOrmBinary)obj;
		return (Arrays.equals(m_value, other.m_value));
		}
		
	public String toString()
		{
		return ("");
		}
	}
