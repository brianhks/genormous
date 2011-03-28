
package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmBoolean extends GenOrmField
	{
	private boolean m_value;
	private Boolean m_prevValue = null;
	
	public GenOrmBoolean(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = false;
		}
		
	//---------------------------------------------------------------------------
	public boolean setValue(boolean value)
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
	public void setPrevValue(boolean value)
		{
		m_prevValue = value;
		}
		
	//---------------------------------------------------------------------------
	public boolean getValue()
		{
		return (m_value);
		}
		
	//---------------------------------------------------------------------------
	public Boolean getPrevValue()
		{
		return (m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getBoolean(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.BOOLEAN);
		else
			ps.setBoolean(pos, m_value);
		}
		
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.BOOLEAN);
		else
			ps.setBoolean(pos, m_prevValue);
		}
	
	//---------------------------------------------------------------------------	
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	//---------------------------------------------------------------------------
	public int hashCode()
		{
		return (new Boolean(m_value).hashCode());
		}
		
	//---------------------------------------------------------------------------
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmBoolean))
			return (false);
			
		GenOrmBoolean other = (GenOrmBoolean)obj;
		return (new Boolean(m_value).equals(other.m_value));
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
