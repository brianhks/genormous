package $package$.genorm;

import java.sql.ResultSet;

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
		
	public String getSQLValue()
		{
		StringBuilder sb = new StringBuilder();
		sb.append('\'');
		sb.append(m_value);
		sb.append('\'');
		return (sb.toString());
		}
		
	public String toString()
		{
		return (m_value);
		}
	}
