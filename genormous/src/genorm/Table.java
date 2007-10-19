package genorm;

import java.util.*;

public class Table
	{
	private String m_tableName;
	private List<String> m_foreignTables;
	private ArrayList<Column> m_columns;
	private ArrayList<Column> m_primaryKeys;
	private Column m_primaryCol;
	private String m_comment;
	private int m_primaryKeyCount;
	private int m_foreignKeyCount;
	private Map<String, String> m_properties;
	private ArrayList<ForeignKeySet> m_foreignKeys;
	private Format m_formatter;
	private String m_createSQL;
	
	public Table(String tableName, Format format)
		{
		m_formatter = format;
		m_tableName = tableName;
		m_foreignTables = new LinkedList<String>();
		m_columns = new ArrayList<Column>();
		m_primaryKeys = new ArrayList<Column>();
		m_primaryCol = null;
		m_comment = "";
		m_primaryKeyCount = 0;
		m_foreignKeyCount = 0;
		m_properties = new HashMap<String, String>();
		m_foreignKeys = new ArrayList<ForeignKeySet>();
		m_createSQL = "";
		}
		
	public String getName()
		{
		return (m_tableName);
		}
		
	public void addProperty(String key, String value)
		{
		m_properties.put(key, value);
		}
		
	public Map<String, String> getProperties()
		{
		return (m_properties);
		}
		
	public String getClassName()
		{
		return (m_formatter.formatClassName(m_tableName));
		}
		
	public boolean isGeneratedKey()
		{
		return (m_primaryKeys.size() == 1);
		}
		
	private void addForeignTable(String table)
		{
		m_foreignTables.add(table);
		}
		
	public Iterator<String> getForeignIterator()
		{
		return (m_foreignTables.iterator());
		}
		
	public boolean getHasPrimaryKey()
		{
		return (m_primaryKeyCount != 0);
		}
		
	public boolean getHasForeignKey()
		{
		return (m_foreignKeyCount != 0);
		}
		
	public int getPrimaryKeyCount()
		{
		return (m_primaryKeyCount);
		}
		
	public void addColumn(Column col)
		{
		m_columns.add(col);
		if (col.isPrimaryKey())
			{
			m_primaryCol = col;
			m_primaryKeyCount ++;
			m_primaryKeys.add(col);
			}
			
		if (col.isForeignKey())
			{
			m_foreignKeyCount ++;
			String ftable = col.getForeignTableName();
			boolean added = false;
			Iterator<ForeignKeySet> it = m_foreignKeys.iterator();
			while (it.hasNext())
				{
				ForeignKeySet fks = it.next();
				if ((fks.getTableName().equals(ftable)) && (fks.addColumn(col)))
					{
					added = true;
					break;
					}
				}
				
			if (!added)
				{
				ForeignKeySet fks = new ForeignKeySet(ftable, m_formatter);
				fks.addColumn(col);
				m_foreignKeys.add(fks);
				}
				
			addForeignTable(col.getForeignTableName());
			}
		}
		
	public boolean getMultiplePrimaryKeys() { return (m_primaryKeys.size() > 1); }
	public ArrayList<Column> getColumns() { return (m_columns); }
	public ArrayList<Column> getPrimaryKeys() { return (m_primaryKeys); }
	public ArrayList<ForeignKeySet> getForeignKeys() { return (m_foreignKeys); }
	
	public Column getPrimaryKey()
		{
		if (m_primaryCol == null)
			{
			System.out.println(this.toString());
			throw new RuntimeException("No primary key set for table "+m_tableName);
			}
		return (m_primaryCol);
		}
		
	public String getComment() { return (m_comment); }
	public void setComment(String comment) { m_comment = comment; }
	
	public String toString()
		{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Table: "+m_tableName+"\n");
		Iterator<Column> it = m_columns.iterator();
		while (it.hasNext())
			{
			sb.append("   "+it.next().getName()+"\n");
			}
			
		return (sb.toString());
		}
		
	public void setCreateSQL(String sql) { m_createSQL = sql; }
	public String getCreateSQL() { return (m_createSQL); }
		
	}
