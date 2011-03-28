package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.Arrays;
import java.sql.Types;

public class GenOrmBinary extends GenOrmFieldTemplate<byte[]>
	{
	public GenOrmBinary(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		}
		
	//---------------------------------------------------------------------------	
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBytes(pos);
		m_isNull = rs.wasNull();
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_value == null)
			ps.setNull(pos, Types.BINARY);
		else
			ps.setBytes(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.BINARY);
		else
			ps.setBytes(pos, m_prevValue);
		}
	}
