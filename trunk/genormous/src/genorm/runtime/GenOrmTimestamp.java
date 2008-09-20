package genorm.runtime;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class GenOrmTimestamp extends GenOrmField
	{
	private Timestamp m_value;
	
	public GenOrmTimestamp(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public void setValue(Timestamp value)
		{
		m_value = value;
		}
		
	public Timestamp getValue()
		{
		return (m_value);
		}
	
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getTimestamp(pos);
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setTimestamp(pos, m_value);
		}
		
	public String getSQLValue()
		{
		StringBuilder sb = new StringBuilder();
		sb.append('\'');
		sb.append(m_value.toString());
		sb.append('\'');
		return (sb.toString());
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
		if (!(obj instanceof GenOrmTimestamp))
			return (false);
			
		GenOrmTimestamp other = (GenOrmTimestamp)obj;
		return (m_value.equals(other.m_value));
		}
		
	public String toString()
		{
		return (m_value.toString());
		}
	}
