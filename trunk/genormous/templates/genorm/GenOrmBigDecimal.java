package $package$.genorm;

import java.sql.ResultSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;

public class GenOrmBigDecimal extends GenOrmField
	{
	private BigDecimal m_value;
	
	public GenOrmBigDecimal(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public void setValue(BigDecimal value)
		{
		m_value = value;
		}
		
	public BigDecimal getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBigDecimal(pos);
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setBigDecimal(pos, m_value);
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
