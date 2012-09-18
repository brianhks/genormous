/* 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
			
		public boolean isInfo()
			{
			return (m_logger.isInfoEnabled());
			}
			
		public void info(Object msg)
			{
			m_logger.info(msg);
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
		
	public boolean isInfo()
		{
		return (isInfoEnabled());
		}
	}
