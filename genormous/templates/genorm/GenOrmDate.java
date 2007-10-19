package $package$.genorm;

import java.sql.ResultSet;
import java.sql.Date;

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
		
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
