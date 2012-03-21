package genorm.runtime;

import java.util.*;
import java.text.DateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;

public class DefaultFormatter implements Formatter
	{
	private Locale m_locale;
	
	public DefaultFormatter()
		{
		m_locale = Locale.US;
		}
	
	public void setLocale(Locale locale)
		{
		m_locale = locale;
		}
		
	public String toString(String name, String str)
		{
		if (str == null)
			return ("");
		else
			return (str);
		}
		
	public String toString(String name, int val)
		{
		return (Integer.toString(val));
		}
		
	public String toString(String name, java.util.Date date)
		{
		if (date == null)
			return ("");
		else
			{
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, m_locale);
			return (df.format(date));
			}
		}
		
	public String toString(String name, boolean bool)
		{
		return (Boolean.toString(bool));
		}
		
	public String toString(String name, double val)
		{
		return (Double.toString(val));
		}
		
	public String toString(String name, BigDecimal bd)
		{
		if (bd == null)
			return ("");
		else
			{
			NumberFormat nf = NumberFormat.getCurrencyInstance(m_locale);
			return (nf.format(bd.doubleValue()));
			}
		}
		
	public String toString(String name, Timestamp ts)
		{
		if (ts == null)
			return ("");
		else
			{
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, m_locale);
			return (df.format(ts));
			}
		}
		
	public String toString(String name, byte[] bytes)
		{
		return ("");
		}
		
	public String toString(String name, java.util.UUID val)
		{
		if (val == null)
			return ("");
		else
			return (val.toString());
		}
	
	}
