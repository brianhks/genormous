package genorm.runtime;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Types;

public class GenOrmDouble extends GenOrmField
	{
	private double m_value;
	private Double m_prevValue;
	
	public GenOrmDouble(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = 0.0;
		m_prevValue = null;
		}
		
	//---------------------------------------------------------------------------
	public boolean setValue(double value)
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
	public void setPrevValue(double value)
		{
		m_prevValue = value;
		}
		
	//---------------------------------------------------------------------------
	public double getValue()
		{
		return (m_value);
		}
		
	//---------------------------------------------------------------------------
	public Double getPrevValue()
		{
		return (m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public void setValue(ResultSet rs, int pos)
			throws java.sql.SQLException
		{
		m_value = rs.getDouble(pos);
		m_isNull = rs.wasNull();
		if (!m_isNull)
			m_prevValue = m_value;
		}
		
	//---------------------------------------------------------------------------
	public void placeValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_isNull)
			ps.setNull(pos, Types.DOUBLE);
		else
			ps.setDouble(pos, m_value);
		}
	
	//---------------------------------------------------------------------------
	public void placePrevValue(PreparedStatement ps, int pos) 
			throws java.sql.SQLException
		{
		if (m_prevValue == null)
			ps.setNull(pos, Types.DOUBLE);
		else
			ps.setDouble(pos, m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	//---------------------------------------------------------------------------
	public int hashCode()
		{
		return (new Double(m_value).hashCode());
		}
		
	//---------------------------------------------------------------------------
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmDouble))
			return (false);
			
		GenOrmDouble other = (GenOrmDouble)obj;
		return (new Double(m_value).equals(other.m_value));
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
