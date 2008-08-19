import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;

import genorm.runtime.*;
import test.*;

import static org.junit.Assert.*;

public interface Database
	{
	public Connection getConnection() throws SQLException;
	}
