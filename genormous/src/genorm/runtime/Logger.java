package genorm.runtime;


public interface Logger
	{
	public void debug(Object msg);
	public void error(Object msg);
	public void error(Object msg, Throwable t);
	public boolean isDebug();
	}
