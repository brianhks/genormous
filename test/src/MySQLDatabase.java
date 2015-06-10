import java.sql.*;
import javax.sql.*;
import com.mysql.jdbc.jdbc2.optional.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;

import test.*;

import static org.junit.Assert.*;

public class MySQLDatabase implements Database
	{
	private DataSource m_dataSource;
	
	private String m_databaseDir;
	private String m_createSQL;
	
	public void setCreateSQL(String sqlFile) { m_createSQL = sqlFile; }
	public void setDatabaseDir(String dir) { m_databaseDir = dir; }
	
	public Connection getConnection() throws SQLException { return (m_dataSource.getConnection()); }
	
	public MySQLDatabase(RunContext context)
		{
		System.out.println("New Database");
		context.setParam("Database", this);
		}
	
	@Test
	public void createDataSource()
			throws Exception
		{
		MysqlDataSource ds = new MysqlDataSource();
		ds.setDatabaseName("kyle2");
		ds.setServerName("192.168.1.101");
		ds.setPort(3306);
		ds.setUser("scope");
		ds.setPassword("doxtek");
		
		//Test the connection
		Connection c = ds.getConnection();
		assertNotNull(c);
		c.close();
		
		m_dataSource = ds;
		GenOrmDataSource.setDataSource(new DSEnvelope(m_dataSource));
		}
	
	}
