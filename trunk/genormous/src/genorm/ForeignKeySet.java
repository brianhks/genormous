package genorm;

import java.util.*;

public class ForeignKeySet
	{
	private String m_table;
	private HashSet<String> m_keySet;
	private ArrayList<Column> m_keys;
	private Format m_formatter;
	
	public ForeignKeySet(String table, Format format)
		{
		m_formatter = format;
		m_table = table;
		m_keySet = new HashSet<String>();
		m_keys = new ArrayList<Column>();
		}
		
	public String getTableName() { return (m_table); }
	public Table getTable() { return (m_keys.get(0).getForeignTable()); }
	public ArrayList<Column> getKeys() { return (m_keys); }
	public String getMethodName() { return (m_formatter.formatForeignKeyMethod(this)); }
	
	
	public boolean addColumn(Column col) 
		{
		boolean ret = true;
		String key = col.getForeignTableColumnName();
		if (!m_keySet.contains(key))
			{
			m_keySet.add(key);
			m_keys.add(col);
			}
		else
			ret = false;
			
		return (ret);
		}
	}
