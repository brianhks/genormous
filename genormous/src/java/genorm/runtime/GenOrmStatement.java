package genorm.runtime;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class GenOrmStatement extends StatementWrapper
	{
	private Connection m_connection;
	
	public GenOrmStatement(Connection connection, Statement statement)
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
	
	}
