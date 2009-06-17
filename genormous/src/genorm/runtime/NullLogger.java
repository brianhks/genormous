package genorm.runtime;

public class NullLogger
		implements Logger
	{
	public void debug(Object msg) {}
	public void error(Object msg) {}
	public void error(Object msg, Throwable t) {}
	public boolean isDebug() { return (false); }
	public boolean isInfo() { return (false); }
	public void info(Object msg) {}
	}
