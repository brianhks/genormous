import org.depunit.annotations.*;
import org.depunit.*;
import java.sql.*;
import java.util.*;
import test.*;

public class TranslactionlessTests
	{
	private String m_queryName;
	private String m_query;
	private List<Object> m_inputParams;
	
	private Database m_database;
	
	public TranslactionlessTests()
		{
		}
		
	//This gets set from the context
	public void setDatabase(HSQLDatabase db)
		{
		//System.out.println("Setting db in QueryTest");
		m_database = db;
		}
		
	
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "HSQLDatabase.createDatabase" } )
	public void testObject()
		{
		UniqueSentence us = UniqueSentence.factory.create(123);
		}
		
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "HSQLDatabase.createDatabase" } )
	public void testQuery()
		{
		LanguageListQuery query = new LanguageListQuery();
		
		LanguageListQuery.ResultSet rs = query.runQuery();
		
		rs.close();
		}
	}
