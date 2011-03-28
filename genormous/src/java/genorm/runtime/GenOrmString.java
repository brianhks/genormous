package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmString extends GenOrmFieldTemplate<String>
	{
	public GenOrmString(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getString(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
	
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.VARCHAR);
		else
			ps.setString(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.VARCHAR);
		else
			ps.setString(pos, m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	@Override
	public String getSQLValue()
		{
		StringBuilder sb = new StringBuilder();
		sb.append('\'');
		sb.append(m_value);
		sb.append('\'');
		return (sb.toString());
		}
	
	}
