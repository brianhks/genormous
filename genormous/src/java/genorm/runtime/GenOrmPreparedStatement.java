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
	
	}
