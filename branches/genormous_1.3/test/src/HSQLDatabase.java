import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;

import genorm.runtime.*;
import test.*;

import static org.junit.Assert.*;

public class HSQLDatabase implements Database
	{
	private DataSource m_dataSource;
	
	private String m_databaseDir;
	private String m_createSQL;
	
	public void setCreateSQL(String sqlFile) { m_createSQL = sqlFile; }
	public void setDatabaseDir(String dir) { m_databaseDir = dir; }
	
	public Connection getConnection() throws SQLException { return (m_dataSource.getConnection()); }
	
	public HSQLDatabase(RunContext context)
		{
		System.out.println("New Database");
		context.setParam("Database", this);
		}
	
	@Test
	public void createDataSource()
			throws Exception
		{
		jdbcDataSource ds = new jdbcDataSource();
		ds.setDatabase("jdbc:hsqldb:file:"+m_databaseDir+"/testdb");
		ds.setUser("sa");
		ds.setPassword("");
		
		//Test the connection
		Connection c = ds.getConnection();
		assertNotNull(c);
		c.close();
		
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
		
		StringBuilder sb = new StringBuilder();
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
			}
		
		c.commit();
		c.close();
		}
		
	@Test
	public void deleteDatabase()
			throws Exception
		{
		Connection c = m_dataSource.getConnection();
		c.setAutoCommit(false);
		
		c.createStatement().execute("SHUTDOWN");
		
		c.commit();
		c.close();
		
		File dbDir = new File(m_databaseDir);
		File[] dbFiles = dbDir.listFiles();
		for (File f : dbFiles)
			f.delete();
		}
	}
