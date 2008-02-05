package $package$.genorm;

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
		
	public void setValue(Date value)
		{
		m_value = value;
		}
		
	public Date getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getDate(pos);
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
		return (m_value.hashCode());
		}
		
	public boolean equals(Object obj)
		{
		return (m_value.equals(obj));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
