package $package$.genorm;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;

public class GenOrmBinary extends GenOrmField
	{
	private byte[] m_value;
	
	public GenOrmBinary(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	public void setValue(byte[] value)
		{
		m_value = value;
		}
		
	public byte[] getValue()
		{
		return (m_value);
		}
		
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBytes(pos);
		}
		
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		ps.setBytes(pos, m_value);
		}
		
	public String getSQLValue()
		{
		return ("");
		}
		
	public String toString()
		{
		return ("");
		}
	}
