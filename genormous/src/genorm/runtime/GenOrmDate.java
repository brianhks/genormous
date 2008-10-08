package genorm.runtime;

import java.sql.ResultSet;
import java.sql.Date;
import java.sql.PreparedStatement;

public class GenOrmDate extends GenOrmField
	{
	private Date m_value;
	
	public GenOrmDate(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public boolean setValue(Date value)
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
		
	public Date getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getDate(pos);
		m_isNull = rs.wasNull();
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setDate(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	public int hashCode()
		{
		if (m_value == null)
			return (0);
		else
			return (m_value.hashCode());
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmDate))
			return (false);
			
		GenOrmDate other = (GenOrmDate)obj;
		return (m_value.equals(other.m_value));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
