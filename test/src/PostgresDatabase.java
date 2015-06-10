import java.sql.*;
import javax.sql.*;
import org.postgresql.jdbc3.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;

import test.*;

import static org.junit.Assert.*;

public class PostgresDatabase implements Database
	{
	private Jdbc3PoolingDataSource m_dataSource;
	
	private String m_databaseDir;
	private String m_createSQL;
	
	public void setCreateSQL(String sqlFile) { m_createSQL = sqlFile; }
	public void setDatabaseDir(String dir) { m_databaseDir = dir; }
	
	public Connection getConnection() throws SQLException { return (m_dataSource.getConnection()); }
	
	public PostgresDatabase(RunContext context)
		{
		System.out.println("New Database");
		context.setParam("Database", this);
		}
	
	//---------------------------------------------------------------------------
	@Test
	public void createDataSource()
			throws Exception
		{
		Jdbc3PoolingDataSource ds = new Jdbc3PoolingDataSource();
		
		ds.setDataSourceName("Postgres Test DataSource");
		ds.setServerName("127.0.0.1");
		ds.setPortNumber(5432);
		ds.setDatabaseName("postgres");
		ds.setUser("postgres");
		ds.setPassword("postgres");
		
		//Test the connection
		Connection c = ds.getConnection();
		assertNotNull(c);
		c.createStatement().execute("CREATE DATABASE genormous");
		c.commit();
		c.close();
		
		ds.close();
		
		ds = new Jdbc3PoolingDataSource();
		ds.setDataSourceName("Postgres Test DataSource");
		ds.setServerName("127.0.0.1");
		ds.setPortNumber(5432);
		ds.setDatabaseName("genormous");
		ds.setUser("postgres");
		ds.setPassword("postgres");
		
		m_dataSource = ds;
		GenOrmDataSource.setDataSource(new DSEnvelope(m_dataSource));
		}
		
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "createDataSource" },
		cleanupMethod = "deleteDatabase" )
	public void createDatabase()
			throws Exception
		{
		Connection c = m_dataSource.getConnection();
		c.setAutoCommit(false);
		
		Statement s = c.createStatement();
		s.execute("create table test ( \"key\" varchar null, \"value\" varchar null, primary key (\"key\"))");
		
		/* StringBuilder sb = new StringBuilder();
		FileReader fr = new FileReader(m_createSQL);
		int ch;
		while ((ch = fr.read()) != -1)
			{
			sb.append((char)ch);
			}
			
		String[] tableCommands = sb.toString().split(";");
		
		Statement s = c.createStatement();
		for (String command : tableCommands)
			{
			s.execute(command);
			} */
		
		c.commit();
		c.close();
		}
		
	//---------------------------------------------------------------------------
	@Test
	public void deleteDatabase()
			throws Exception
		{
		m_dataSource.close();
		
		Jdbc3PoolingDataSource ds = new Jdbc3PoolingDataSource();
		
		ds.setDataSourceName("Postgres Test DataSource");
		ds.setServerName("127.0.0.1");
		ds.setPortNumber(5432);
		ds.setDatabaseName("postgres");
		ds.setUser("postgres");
		ds.setPassword("postgres");
		
		//Test the connection
		Connection c = ds.getConnection();
		assertNotNull(c);
		c.createStatement().execute("DROP DATABASE genormous");
		c.commit();
		c.close();
		
		ds.close();
		}
	}
