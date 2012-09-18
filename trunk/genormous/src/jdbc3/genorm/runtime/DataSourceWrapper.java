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

import javax.sql.DataSource;
import java.sql.Connection;
import java.io.PrintWriter;

public class DataSourceWrapper implements DataSource
	{
	protected DataSource m_dataSource;
	
	public DataSourceWrapper(DataSource ds)
		{
		m_dataSource = ds;
		}
		
	//---------------------------------------------------------------------------
	public Connection getConnection()
			throws java.sql.SQLException
		{
		return (m_dataSource.getConnection());
		}
		
	//---------------------------------------------------------------------------
	public Connection getConnection(String user, String pass)
			throws java.sql.SQLException
		{
		return (m_dataSource.getConnection(user, pass));
		}
		
	//---------------------------------------------------------------------------
	public int getLoginTimeout()
			throws java.sql.SQLException
		{
		return (m_dataSource.getLoginTimeout());
		}
		
	//---------------------------------------------------------------------------
	public PrintWriter getLogWriter()
			throws java.sql.SQLException
		{
		return (m_dataSource.getLogWriter());
		}
		
	//---------------------------------------------------------------------------
	public void setLoginTimeout(int seconds)
			throws java.sql.SQLException
		{
		m_dataSource.setLoginTimeout(seconds);
		}
		
	//---------------------------------------------------------------------------
	public void setLogWriter(PrintWriter out)
			throws java.sql.SQLException
		{
		m_dataSource.setLogWriter(out);
		}
		
	}
