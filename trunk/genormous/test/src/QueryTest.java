import org.depunit.annotations.*;
import org.depunit.*;
import java.sql.*;
import java.util.*;

public class QueryTest
	{
	private String m_queryName;
	private String m_query;
	private List<Object> m_inputParams;
	
	private Database m_database;
	
	public QueryTest()
		{
		}
		
	//This gets set from the context
	public void setDatabase(Database db)
		{
		m_database = db;
		}
		
	//Set methods for each query
	public void setQueryName(String name) { m_queryName = name; }
	public void setQuery(String query) { m_query = query; }
	public void setInputParams(List<Object> params) { m_inputParams = params; }
	
	@Test(
		hardDependencyOn = { "Database.createDatabase" } )
	public void testQuery()
			throws SQLException
		{
		System.out.println("    "+m_queryName);
		Connection c = m_database.getConnection();
		
		PreparedStatement ps = c.prepareStatement(m_query);
		
		Iterator<Object> it = m_inputParams.iterator();
		int pindex = 1;
		while (it.hasNext())
			{
			Object param = it.next();
			if (param instanceof Integer)
				ps.setInt(pindex, (Integer)param);
			else if (param instanceof String)
				ps.setString(pindex, (String)param);
				
			pindex ++;
			}
			
		ps.execute();
		
		ps.close();
		}
	}
