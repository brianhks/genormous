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

package genorm.servlet;

import genorm.runtime.GenOrmDSEnvelope;
import genorm.runtime.GenOrmServletDSEnvelope;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.lang.reflect.Constructor;


/**
	
*/
public class ContextListener implements ServletContextListener
	{
	/**
		JNDI Resource to use to get the database DataSource object
		<code>
&lt;context-param&gt;
	&lt;param-name&gt;genormJNDIDataSource&lt;/param-name&gt;
	&lt;param-value&gt;java:/comp/env/jdbc/postgres&lt;/param-value&gt;
&lt;/context-param&gt;
		</code>
	*/
	public static final String INIT_PARAM = "genormJNDIDataSource";
	public static final String ENVELOPE_CLASS_PARAM = "genormDSEnvelopeClass";
	
	public void contextInitialized(ServletContextEvent sce)
		{
		try
			{
			ServletContext context = sce.getServletContext();
			
			String resource = context.getInitParameter(INIT_PARAM);
			String envelope = context.getInitParameter(ENVELOPE_CLASS_PARAM);
			
			if ((resource != null) && (envelope != null))
				{
				InitialContext cxt = new InitialContext();
				
				DataSource ds = (DataSource) cxt.lookup(resource);
				
				Class envClass = getClass().getClassLoader().loadClass(envelope);
				Constructor constructor = envClass.getDeclaredConstructor(DataSource.class);
				
				GenOrmDSEnvelope envObj = (GenOrmDSEnvelope)constructor.newInstance(ds);
				if (envObj instanceof GenOrmServletDSEnvelope)
					{
					((GenOrmServletDSEnvelope)envObj).setServletContext(context);
					}
					
				envObj.initialize();
				}
			}
		catch (Exception e)
			{
			throw new RuntimeException(e);
			}
		}
		
		
	public void contextDestroyed(ServletContextEvent sce)
		{
		}
	}
