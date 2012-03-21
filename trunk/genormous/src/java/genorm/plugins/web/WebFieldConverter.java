package genorm.plugins.web;

import java.sql.Timestamp;

public interface WebFieldConverter
	{
	public int toInt(String field, String value);
	public String toString(String field, String value);
	public Timestamp toTimestamp(String field, String value);
	public boolean toBoolean(String field, String value);
	public double toDouble(String field, String value);
	public byte toByte(String field, String value);
	public long toLong(String field, String value);
	public byte[] toBytes(String field, String value);
	public java.math.BigDecimal toBigDecimal(String field, String value);
	public java.sql.Date toDate(String field, String value);
	public java.util.UUID toUUID(String field, String value);
	}
