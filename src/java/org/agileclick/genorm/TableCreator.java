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

package org.agileclick.genorm;

import java.sql.*;
import java.io.*;
import java.util.*;

public class TableCreator
	{
	private class Table
		{
		private String m_name;
		private Map<String, Column> m_columns;
		
		public Table(String name)
			{
			m_name = name;
			m_columns = new TreeMap<String, Column>();
			}
			
		public Column getColumn(String name)
			{
			Column c = m_columns.get(name);
			if (c == null)
				c = new Column(name);
				
			return(c);
			}
			
		public void addColumn(String name, Column col)
			{
			m_columns.put(name, col);
			}
			
		public String getXML()
			{
			StringBuilder sb = new StringBuilder();
			
			sb.append("\t<table name=\""+m_name.toLowerCase()+"\">\n");
			sb.append("\t\t<comment></comment>\n");
			
			Iterator<Column> it = m_columns.values().iterator();
			while (it.hasNext())
				{
				sb.append(it.next().getXML());
				}
				
			sb.append("\t\t<!-- Table queries go here -->\n");
			sb.append("\t</table>\n\n");
			
			return (sb.toString());
			}
		}
		
	/**
		
	*/
	public static void main(String[] args)
			throws Exception
		{
		String driverClass = args[0];
		String connectString = args[1];
		String user = args[2];
		String password = args[3];
		String tableFile = args[4];
		
		Class.forName(driverClass);
		
		Connection con = DriverManager.getConnection(connectString,
				user, password);
				
		TableCreator tc = new TableCreator();
		tc.createTables(con, new File(tableFile));
		
		con.close();
		}
		
	private void printResults(ResultSet rs)
			throws SQLException
		{
		ResultSetMetaData meta = rs.getMetaData();
		int count = meta.getColumnCount();
		
		for (int I = 0; I < count; I++)
			{
			System.out.print(meta.getColumnName(I+1)+":"+rs.getString(I+1)+" ");
			}
			
		System.out.println();
		}
		
	public void createTables(Connection con, File output)
			throws SQLException, IOException
		{
		Map<String, Table> tableMap = new TreeMap<String, Table>();
		DatabaseMetaData meta = con.getMetaData();
		
		ResultSet tables = meta.getTables(null, null, null, new String[] {"TABLE"});
		while (tables.next())
			{
			String tableName = tables.getString("TABLE_NAME");
			System.out.println("Table "+tableName);
			
			Table table = new Table(tableName);
			tableMap.put(tableName, table);
			
			ResultSet columns = meta.getColumns(null, null, tableName, null);
			
			while (columns.next())
				{
				String columnName = columns.getString("COLUMN_NAME");
				String defValue = columns.getString("COLUMN_DEF");
				String typeName = columns.getString("TYPE_NAME");
				int type = columns.getInt("DATA_TYPE");
				if ((type == java.sql.Types.CHAR) || (type == java.sql.Types.VARCHAR))
					typeName += "("+columns.getString("CHAR_OCTET_LENGTH")+")";
					
				boolean allowNull = columns.getString("IS_NULLABLE").equals("YES");
				if (tableName.equals("documents_9"))
					printResults(columns);
				
				Column c = new Column(columnName);
				table.addColumn(columnName, c);
				if (defValue != null)
					c.setDefault(defValue);
					
				c.setAllowNull(allowNull);
				c.setCustomType(typeName);
				
				//System.out.println("  Column "+columnName+" "+typeName+" "+defValue+" "+allowNull);
				}
			}
			
		tables.beforeFirst();
		while (tables.next())
			{
			String tableName = tables.getString("TABLE_NAME");
			
			Table table = tableMap.get(tableName);
			
			ResultSet pkeys = meta.getPrimaryKeys(null, null, tableName);
			while (pkeys.next())
				{
				String key = pkeys.getString("COLUMN_NAME");
				//printResults(pkeys);
				
				Column c = table.getColumn(key);
				c.setPrimaryKey();
				}
				
			ResultSet fkeys = meta.getExportedKeys(null, null, tableName);
			while (fkeys.next())
				{
				Table t = tableMap.get(fkeys.getString("FKTABLE_NAME"));
				Column c = t.getColumn(fkeys.getString("FKCOLUMN_NAME"));
				
				c.setForeignKey();
				c.setForeignTableName(fkeys.getString("PKTABLE_NAME"));
				c.setForeignTableColumnName(fkeys.getString("PKCOLUMN_NAME"));
				
				//printResults(fkeys);
				}
			}
			
		StringBuilder sb = new StringBuilder();
		sb.append("<tables>\n");
		Iterator<Table> it = tableMap.values().iterator();
		while (it.hasNext())
			{
			sb.append(it.next().getXML());
			}
			
		sb.append("\t<queries>\n");
		sb.append("\t<!-- Cross table queries go here -->\n");
		sb.append("\t</queries>\n");
		sb.append("</tables>\n");
			
		FileWriter fw = new FileWriter(output);
		fw.write(sb.toString());
		fw.close();
		}
	}
