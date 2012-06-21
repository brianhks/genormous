package genorm.runtime;

import javax.sql.DataSource;
import java.sql.Connection;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.sql.SQLFeatureNotSupportedException;

public class DataSourceWrapper implements DataSource
	{
	protected DataSource m_dataSource;
	
	public DataSourceWrapper(DataSource ds)
		{
		m_dataSource = ds;
		}
		
	//---------------------------------------------------------------------------
	public Connection getConnection()
			throws java.sql.SQLException
		{
		return (m_dataSource.getConnection());
		}
		
	//---------------------------------------------------------------------------
	public Connection getConnection(String user, String pass)
			throws java.sql.SQLException
		{
		return (m_dataSource.getConnection(user, pass));
		}
		
	//---------------------------------------------------------------------------
	public int getLoginTimeout()
			throws java.sql.SQLException
		{
		return (m_dataSource.getLoginTimeout());
		}
		
	//---------------------------------------------------------------------------
	public PrintWriter getLogWriter()
			throws java.sql.SQLException
		{
		return (m_dataSource.getLogWriter());
		}
		
	//---------------------------------------------------------------------------
	public void setLoginTimeout(int seconds)
			throws java.sql.SQLException
		{
		m_dataSource.setLoginTimeout(seconds);
		}
		
	//---------------------------------------------------------------------------
	public void setLogWriter(PrintWriter out)
			throws java.sql.SQLException
		{
		m_dataSource.setLogWriter(out);
		}
		
	//---------------------------------------------------------------------------
	public boolean isWrapperFor(Class<?> iface)
			throws java.sql.SQLException
		{
		return (m_dataSource.isWrapperFor(iface));
		}
		
	public <T> T unwrap(Class<T> iface)
			throws java.sql.SQLException
		{
		return (m_dataSource.unwrap(iface));
		}
		
	//---------------------------------------------------------------------------
	public Logger getParentLogger()
			throws  SQLFeatureNotSupportedException
		{
		return (m_dataSource.getParentLogger());
		}
	}
