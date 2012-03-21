package genorm.runtime;

import java.util.*;
import java.sql.Timestamp;
import java.math.BigDecimal;

public interface Formatter
	{
	public String toString(String name, String str);
	public String toString(String name, int val);
	public String toString(String name, java.util.Date date);
	public String toString(String name, boolean bool);
	public String toString(String name, double val);
	public String toString(String name, BigDecimal bd);
	public String toString(String name, Timestamp ts);
	public String toString(String name, byte[] bytes);
	public String toString(String name, java.util.UUID val);
	public void setLocale(Locale locale);
	}
