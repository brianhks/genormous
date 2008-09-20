package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

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
		if (m_value == null)
			setNull();
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
		if (m_isNull)
			ps.setNull(pos, Types.VARCHAR);
		else
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
		if (m_value == null)
			return (0);
		else
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
