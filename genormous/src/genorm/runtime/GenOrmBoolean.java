package genorm.runtime;

import java.sql.ResultSet;
import java.sql.Date;
import java.sql.PreparedStatement;

public class GenOrmBoolean extends GenOrmField
	{
	private boolean m_value;
	
	public GenOrmBoolean(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = false;
		}
		
	public boolean setValue(boolean value)
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
		
	public boolean getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBoolean(pos);
		m_isNull = rs.wasNull();
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setBoolean(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	public int hashCode()
		{
		return (new Boolean(m_value).hashCode());
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmBoolean))
			return (false);
			
		GenOrmBoolean other = (GenOrmBoolean)obj;
		return (new Boolean(m_value).equals(other.m_value));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
