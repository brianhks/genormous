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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenOrmPreparedStatement extends PreparedStatementWrapper
	{
	private Connection m_connection;
	
	public GenOrmPreparedStatement(Connection connection, PreparedStatement statement)
		{
		super(statement);
		m_connection = connection;
		}
		
		
	@Override
	public void close()
			throws SQLException
		{
		super.close();
		m_connection.close();
		}
	
	@Override
	public String toString()
		{
		return (m_statement.toString());
		}
	}
