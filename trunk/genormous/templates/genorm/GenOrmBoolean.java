package $package$.genorm;

import java.sql.ResultSet;
import java.sql.Date;

public class GenOrmBoolean extends GenOrmField
	{
	private boolean m_value;
	
	public GenOrmBoolean(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = false;
		}
		
	public void setValue(boolean value)
		{
		m_value = value;
		}
		
	public boolean getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBoolean(pos);
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
