package genorm.plugins.web;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;
import java.math.BigDecimal;

public class DefaultWebFieldConverter implements WebFieldConverter
	{
	public int toInt(String field, String value)
		{
		return (Integer.parseInt(value));
		}
		
	//---------------------------------------------------------------------------
	public String toString(String field, String value)
		{
		return (value);
		}
		
	//---------------------------------------------------------------------------
	public Timestamp toTimestamp(String field, String value)
		{
		return (null);
		}
		
	//---------------------------------------------------------------------------
	public boolean toBoolean(String field, String value)
		{
		return (Boolean.parseBoolean(value));
		}
		
	//---------------------------------------------------------------------------
	public double toDouble(String field, String value)
		{
		return (Double.parseDouble(value));
		}
	
	//---------------------------------------------------------------------------
	public byte toByte(String field, String value)
		{
		return (Byte.parseByte(value));
		}
	
	//---------------------------------------------------------------------------
	public long toLong(String field, String value)
		{
		return (Long.parseLong(value));
		}
	
	//---------------------------------------------------------------------------
	public byte[] toBytes(String field, String value)
		{
		return (null);
		}
	
	//---------------------------------------------------------------------------
	public BigDecimal toBigDecimal(String field, String value)
		{
		return (new BigDecimal(value));
		}
	
	//---------------------------------------------------------------------------
	public Date toDate(String field, String value)
		{
		return (null);
		}
	
	//---------------------------------------------------------------------------
	public UUID toUUID(String field, String value)
		{
		return (UUID.fromString(value));
		}
	}
