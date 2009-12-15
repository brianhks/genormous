package genorm.runtime;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.StringTokenizer;

public abstract class SQLQuery
	{
	protected Locale m_locale;
	protected Formatter m_formatter;
	
	//---------------------------------------------------------------------------
	public SQLQuery()
		{
		m_formatter = new DefaultFormatter();
		}
		
	//---------------------------------------------------------------------------
	public void setFormatter(Formatter formatter)
		{
		m_formatter = formatter;
		}
		
	//---------------------------------------------------------------------------
	public void setLocale(Locale locale)
		{
		m_formatter.setLocale(locale);
		}
		
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	public abstract String getQueryName();
	public abstract String getQuery();
	
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	}
