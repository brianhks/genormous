package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmLong extends GenOrmField
	{
	private long m_value;
	private Long m_prevValue = null;
	
	public GenOrmLong(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0L;
		}
		
	//---------------------------------------------------------------------------
	public boolean setValue(long value)
		{
		if (m_isNull || (m_value != value))
			{
			m_value = value;
			m_isNull = false;
			return (true);
			}
		else
			return (false);
		}

	//---------------------------------------------------------------------------
	public void setPrevValue(long value)
		{
		m_prevValue = value;
		}
		
	//---------------------------------------------------------------------------
	public long getValue()
		{
		return (m_value);
		}
		
	//---------------------------------------------------------------------------
	public Long getPrevValue()
		{
		return (m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getLong(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.INTEGER);
		else
			ps.setLong(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.INTEGER);
		else
			ps.setLong(pos, m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	//---------------------------------------------------------------------------
	public int hashCode()
		{
		return (new Long(m_value).hashCode());
		}
		
	//---------------------------------------------------------------------------
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmLong))
			return (false);
			
		GenOrmLong other = (GenOrmLong)obj;
		return (new Long(m_value).equals(other.m_value));
		}
	
	//---------------------------------------------------------------------------
	public String getPrevValueAsString()
		{
		return (String.valueOf(m_prevValue));
		}
		
	//---------------------------------------------------------------------------
	public String toString()
		{
		return (String.valueOf(m_value));
		}
	}
