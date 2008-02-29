package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class GenOrmString extends GenOrmField
	{
	private String m_value;
	
	public GenOrmString(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public void setValue(String value)
		{
		m_value = value;
		}
		
	public String getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getString(pos);
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setString(pos, m_value);
		}
		
	public String getSQLValue()
		{
		StringBuilder sb = new StringBuilder();
		sb.append('\'');
		sb.append(m_value);
		sb.append('\'');
		return (sb.toString());
		}
		
	public int hashCode()
		{
		return (m_value.hashCode());
		}
		
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmString))
			return (false);
			
		GenOrmString other = (GenOrmString)obj;
		return (m_value.equals(other.m_value));
		}
		
	public String toString()
		{
		return (m_value);
		}
	}
