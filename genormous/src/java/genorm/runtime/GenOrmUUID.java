package genorm.runtime;

import java.sql.ResultSet;
import java.util.UUID;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmUUID extends GenOrmFieldTemplate<UUID>
	{
	public GenOrmUUID(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = (UUID)rs.getObject(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.OTHER);
		else
			ps.setObject(pos, m_value, Types.OTHER);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.OTHER);
		else
			ps.setObject(pos, m_prevValue, Types.OTHER);
		}
		
	}
