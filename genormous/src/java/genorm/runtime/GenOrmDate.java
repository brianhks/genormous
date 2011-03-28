package genorm.runtime;

import java.sql.ResultSet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmDate extends GenOrmFieldTemplate<Date>
	{
	public GenOrmDate(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getDate(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.DATE);
		else
			ps.setDate(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.DATE);
		else
			ps.setDate(pos, m_prevValue);
		}
		
	}
