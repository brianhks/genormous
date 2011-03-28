import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;
import java.util.ArrayList;

import genorm.runtime.*;
import test.*;

import static org.junit.Assert.*;

public class ErrorTests
	{
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "PostgresDatabase.createDatabase" })
	public void closedConnection()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		try
			{
			GenOrmDataSource.createStatement().execute("select * from does_not_exist");
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
			
		Connection c = GenOrmDataSource.getConnection();
		System.out.println("isclosed "+c.isClosed());
		//System.out.println("isvalid "+c.isValid(0));
		
		GenOrmDataSource.createStatement().execute("select * from test");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		

		
	//---------------------------------------------------------------------------
	}
