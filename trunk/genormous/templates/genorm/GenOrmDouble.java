package $package$.genorm;

import java.sql.ResultSet;

public class GenOrmDouble extends GenOrmField
	{
	private double m_value;
	
	public GenOrmDouble(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0.0;
		}
		
	public void setValue(double value)
		{
		m_value = value;
		}
		
	public double getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getDouble(pos);
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
