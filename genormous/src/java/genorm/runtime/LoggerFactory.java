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
