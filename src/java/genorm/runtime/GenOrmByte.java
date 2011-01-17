package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmByte extends GenOrmField
	{
	private byte m_value;
	
	public GenOrmByte(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0;
		}
		
	public boolean setValue(byte value)
		{
		if (m_isNull || (m_value != value))
			{
			m_value = value;
			m_isNull = false;
			return (true);
			}
		else
			return (false);
		}
		
	public byte getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getByte(pos);
		m_isNull = rs.wasNull();
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.TINYINT);
		else
			ps.setByte(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	public int hashCode()
		{
		return (new Byte(m_value).hashCode());
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmByte))
			return (false);
			
		GenOrmByte other = (GenOrmByte)obj;
		return (new Byte(m_value).equals(other.m_value));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
