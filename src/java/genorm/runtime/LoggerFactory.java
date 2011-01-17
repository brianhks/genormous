package genorm.runtime;

import java.lang.reflect.Method;

public class LoggerFactory
	{
	private static Class getClass(String name)
		{
		Class ret = null;
		try
			{
			ret = Class.forName(name);
			}
		catch (Exception e)
			{
			//e.printStackTrace();
			}

		return (ret);
		}

	private static Logger loadLogger(Class c, String name)
		{
		Logger ret = null;
		try
			{
			Method m = c.getDeclaredMethod("loadLogger", String.class);
			ret = (Logger)m.invoke(null, name);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}

		return (ret);
		}

	public static Logger getLogger(String name)
		{
		Logger ret = null;
		if (getClass("org.apache.log4j.Logger") != null)
			{
			Class c = getClass("genorm.runtime.Log4jLogger");
			ret = loadLogger(c, name);
			}
		else
			{
			ret = new NullLogger();
			}
			
		return (ret);
		}
	}
