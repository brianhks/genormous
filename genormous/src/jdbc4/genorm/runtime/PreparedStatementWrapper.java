/* 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package genorm.runtime;

import java.math.BigDecimal;
import java.util.Calendar;
import java.sql.Array;
import java.io.Reader;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Ref;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSetMetaData;
import java.sql.ParameterMetaData;
import java.sql.RowId;
import java.sql.NClob;
import java.sql.SQLXML;




public class PreparedStatementWrapper extends StatementWrapper
		implements PreparedStatement
	{
	protected PreparedStatement m_statement;
	
	public PreparedStatementWrapper(PreparedStatement statement)
		{
		super(statement);
		m_statement = statement;
		}
	
	public ResultSet executeQuery() throws SQLException
		{
		return (m_statement.executeQuery());
		}


	public int executeUpdate() throws SQLException
		{
		return (m_statement.executeUpdate());
		}


	public void setNull(int parameterIndex, int sqlType) throws SQLException
		{
		m_statement.setNull(parameterIndex, sqlType);
		}


	public void setBoolean(int parameterIndex, boolean x) throws SQLException
		{
		m_statement.setBoolean(parameterIndex, x);
		}


	public void setByte(int parameterIndex, byte x) throws SQLException
		{
		m_statement.setByte(parameterIndex, x);
		}


	public void setShort(int parameterIndex, short x) throws SQLException
		{
		m_statement.setShort(parameterIndex, x);
		}


	public void setInt(int parameterIndex, int x) throws SQLException
		{
		m_statement.setInt(parameterIndex, x);
		}


	public void setLong(int parameterIndex, long x) throws SQLException
		{
		m_statement.setLong(parameterIndex, x);
		}


	public void setFloat(int parameterIndex, float x) throws SQLException
		{
		m_statement.setFloat(parameterIndex, x);
		}


	public void setDouble(int parameterIndex, double x) throws SQLException
		{
		m_statement.setDouble(parameterIndex, x);
		}


	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
		{
		m_statement.setBigDecimal(parameterIndex, x);
		}


	public void setString(int parameterIndex, String x) throws SQLException
		{
		m_statement.setString(parameterIndex, x);
		}


	public void setBytes(int parameterIndex, byte x[]) throws SQLException
		{
		m_statement.setBytes(parameterIndex, x);
		}


	public void setDate(int parameterIndex, java.sql.Date x) throws SQLException
		{
		m_statement.setDate(parameterIndex, x);
		}


	public void setTime(int parameterIndex, java.sql.Time x) throws SQLException
		{
		m_statement.setTime(parameterIndex, x);
		}


	public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException
		{
		m_statement.setTimestamp(parameterIndex, x);
		}


	public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException
		{
		m_statement.setAsciiStream(parameterIndex, x, length);
		}


	public void setUnicodeStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException
		{
		m_statement.setUnicodeStream(parameterIndex, x, length);
		}


	public void setBinaryStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException
		{
		m_statement.setBinaryStream(parameterIndex, x, length);
		}


	public void clearParameters() throws SQLException
		{
		m_statement.clearParameters();
		}

	//----------------------------------------------------------------------
	// Advanced features:


	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException
		{
		m_statement.setObject(parameterIndex, x, targetSqlType);
		}


	public void setObject(int parameterIndex, Object x) throws SQLException
		{
		m_statement.setObject(parameterIndex, x);
		}


	public boolean execute() throws SQLException
		{
		return (m_statement.execute());
		}

	//--------------------------JDBC 2.0-----------------------------


	public void addBatch() throws SQLException
		{
		m_statement.addBatch();
		}


	public void setCharacterStream(int parameterIndex,
			java.io.Reader reader,
			int length) throws SQLException
		{
		m_statement.setCharacterStream(parameterIndex, reader, length);
		}


	public void setRef (int parameterIndex, Ref x) throws SQLException
		{
		m_statement.setRef(parameterIndex, x);
		}


	public void setBlob (int parameterIndex, Blob x) throws SQLException
		{
		m_statement.setBlob(parameterIndex, x);
		}


	public void setClob (int parameterIndex, Clob x) throws SQLException
		{
		m_statement.setClob(parameterIndex, x);
		}


	public void setArray (int parameterIndex, Array x) throws SQLException
		{
		m_statement.setArray(parameterIndex, x);
		}


	public ResultSetMetaData getMetaData() throws SQLException
		{
		return (m_statement.getMetaData());
		}


	public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
			throws SQLException
		{
		m_statement.setDate(parameterIndex, x, cal);
		}


	public void setTime(int parameterIndex, java.sql.Time x, Calendar cal)
			throws SQLException
		{
		m_statement.setTime(parameterIndex, x, cal);
		}


	public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
			throws SQLException
		{
		m_statement.setTimestamp(parameterIndex, x, cal);
		}


	public void setNull (int parameterIndex, int sqlType, String typeName)
			throws SQLException
		{
		m_statement.setNull(parameterIndex, sqlType, typeName);
		}

	//------------------------- JDBC 3.0 -----------------------------------


	public void setURL(int parameterIndex, java.net.URL x) throws SQLException
		{
		m_statement.setURL(parameterIndex, x);
		}


	public ParameterMetaData getParameterMetaData() throws SQLException
		{
		return (m_statement.getParameterMetaData());
		}

	//------------------------- JDBC 4.0 -----------------------------------

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
			throws SQLException
		{
		m_statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		}

 	public void setRowId(int parameterIndex, RowId x) throws SQLException
		{
		m_statement.setRowId(parameterIndex, x);
		}


	public void setNString(int parameterIndex, String value) throws SQLException
		{
		m_statement.setNString(parameterIndex, value);
		}


	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
		{
		m_statement.setNCharacterStream(parameterIndex, value, length);
		}


	public void setNClob(int parameterIndex, NClob value) throws SQLException
		{
		m_statement.setNClob(parameterIndex, value);
		}


	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException
		{
		m_statement.setClob(parameterIndex, reader, length);
		}


	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException
		{
		m_statement.setBlob(parameterIndex, inputStream, length);
		}


	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException
		{
		m_statement.setNClob(parameterIndex, reader, length);
		}


	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
		{
		m_statement.setSQLXML(parameterIndex, xmlObject);
		}



	public void setAsciiStream(int parameterIndex, java.io.InputStream x, long length)
			throws SQLException
		{
		m_statement.setAsciiStream(parameterIndex, x, length);
		}


	public void setBinaryStream(int parameterIndex, java.io.InputStream x,
			long length) throws SQLException
		{
		m_statement.setBinaryStream(parameterIndex, x, length);
		}


	public void setCharacterStream(int parameterIndex,
			java.io.Reader reader,
			long length) throws SQLException
		{
		m_statement.setCharacterStream(parameterIndex, reader, length);
		}


	public void setAsciiStream(int parameterIndex, java.io.InputStream x)
			throws SQLException
		{
		m_statement.setAsciiStream(parameterIndex, x);
		}


	public void setBinaryStream(int parameterIndex, java.io.InputStream x)
			throws SQLException
		{
		m_statement.setBinaryStream(parameterIndex, x);
		}


	public void setCharacterStream(int parameterIndex,
			java.io.Reader reader) throws SQLException
		{
		m_statement.setCharacterStream(parameterIndex, reader);
		}


	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
		{
		m_statement.setNCharacterStream(parameterIndex, value);
		}


	public void setClob(int parameterIndex, Reader reader)
			throws SQLException
		{
		m_statement.setClob(parameterIndex, reader);
		}


	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException
		{
		m_statement.setBlob(parameterIndex, inputStream);
		}


	public void setNClob(int parameterIndex, Reader reader)
			throws SQLException
		{
		m_statement.setNClob(parameterIndex, reader);
		}

	}
