package genorm.runtime;


public class Log4jLogger extends org.apache.log4j.Logger
		implements Logger
	{
	private static class Factory
			implements org.apache.log4j.spi.LoggerFactory
		{
		public org.apache.log4j.Logger makeNewLoggerInstance(String name)
			{
			return (new Log4jLogger(name));
			}
		}
		
	private static class Wrapper
			implements Logger
		{
		private org.apache.log4j.Logger m_logger;
		
		public Wrapper(org.apache.log4j.Logger logger)
			{
			m_logger = logger;
			}
			
		public void debug(Object msg)
			{
			m_logger.debug(msg);
			}
			
		public void error(Object msg)
			{
			m_logger.error(msg);
			}
			
		public void error(Object msg, Throwable t)
			{
			m_logger.error(msg, t);
			}
			
		public boolean isDebug()
			{
			return (m_logger.isDebugEnabled());
			}
		}

	protected Log4jLogger(String name)
		{
		super(name);
		}

	public static Logger loadLogger(String name)
		{
		if (name == null)
			return (null);
			
		Factory factory = new Factory();
		if (factory == null)
			return (null);
			
		org.apache.log4j.Logger obj = org.apache.log4j.Logger.getLogger(name, factory);
		if (obj instanceof Log4jLogger)
			return ((Logger)obj);
		else
			return (new Wrapper(obj));
		}

	public boolean isDebug() 
		{ 
		return (isDebugEnabled()); 
		}
	}
