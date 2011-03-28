package genorm.runtime;

import java.sql.ResultSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmBigDecimal extends GenOrmFieldTemplate<BigDecimal>
	{
	public GenOrmBigDecimal(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBigDecimal(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.BIGINT);
		else
			ps.setBigDecimal(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.BIGINT);
		else
			ps.setBigDecimal(pos, m_prevValue);
		}
	}
