package $package$.genorm;

import java.sql.ResultSet;

public class GenOrmInt extends GenOrmField
	{
	private int m_value;
	
	public GenOrmInt(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0;
		}
		
	public void setValue(int value)
		{
		m_value = value;
		}
		
	public int getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getInt(pos);
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
