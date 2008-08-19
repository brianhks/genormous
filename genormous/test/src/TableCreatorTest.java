import org.depunit.annotations.*;
import org.depunit.*;
import java.sql.*;
import java.io.File;
import java.util.*;
import genorm.TableCreator;

public class TableCreatorTest
	{
	private String m_queryName;
	private String m_query;
	private List<Object> m_inputParams;
	
	private Database m_database;
	
	//This gets set from the context
	public void setDatabase(MySQLDatabase db)
		{
		m_database = db;
		}
		
	
	@Test(
		hardDependencyOn = { "MySQLDatabase.createDataSource" } )
	public void testTableCreator()
			throws Exception
		{
		Connection c = m_database.getConnection();
		
		TableCreator tc = new TableCreator();
		
		tc.createTables(c, new File("test_tables.xml"));
		}
	}
