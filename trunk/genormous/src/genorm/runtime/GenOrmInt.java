package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmInt extends GenOrmField
	{
	private int m_value;
	
	public GenOrmInt(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0;
		}
		
	public boolean setValue(int value)
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
		
	public int getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getInt(pos);
		m_isNull = rs.wasNull();
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.INTEGER);
		else
			ps.setInt(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	public int hashCode()
		{
		return (new Integer(m_value).hashCode());
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmInt))
			return (false);
			
		GenOrmInt other = (GenOrmInt)obj;
		return (new Integer(m_value).equals(other.m_value));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
