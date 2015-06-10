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

import java.util.*;

public class ForeignKeySet
	{
	private String m_ftable;            //Foreign table name
	private HashSet<String> m_keySet;
	private ArrayList<Column> m_keys;  //Local columns that point to foreign key
	private Format m_formatter;
	private String m_onDelete;
	private String m_onUpdate;
	private String m_thisTable;
	
	private String getOnCommand(String value)
		{
		String ret = "";
		if (value.equals("cascade"))
			ret = "CASCADE";
		else if (value.equals("null"))
			ret = "SET NULL";
		else if (value.equals("default"))
			ret = "SET DEFAULT";
			
		return (ret);
		}
	
	public ForeignKeySet(String thisTable, String ftable, Format format)
		{
		m_thisTable = thisTable;
		m_formatter = format;
		m_ftable = ftable;
		m_keySet = new HashSet<String>();
		m_keys = new ArrayList<Column>();
		}
		
	public String getConstraintName()
		{
		StringBuilder name = new StringBuilder();
		name.append(m_thisTable);
		name.append("_").append(m_keys.get(0).getName());
			
		name.append("_fkey");
			
		return (name.toString());
		}
		
	public String getTableName() { return (m_ftable); }
	public Table getTable() { return (m_keys.get(0).getForeignTable()); }
	public ArrayList<Column> getKeys() { return (m_keys); }
	public String getMethodName() { return (m_formatter.formatForeignKeyMethod(this)); }
	
	public boolean getHasOnDelete() { return (m_onDelete != null); }
	public void setOnDelete(String onDelete) { m_onDelete = onDelete; }
	public String getOnDelete() { return (getOnCommand(m_onDelete)); }

	public boolean getHasOnUpdate() { return (m_onUpdate != null); }
	public void setOnUpdate(String onUpdate) { m_onUpdate = onUpdate; }
	public String getOnUpdate() { return (getOnCommand(m_onUpdate)); }
	
	
	public boolean addColumn(Column col) 
		{
		boolean ret = true;
		String key = col.getForeignTableColumnName();
		if (!m_keySet.contains(key))
			{
			m_keySet.add(key);
			m_keys.add(col);
			if (col.getOnUpdate() != null)
				m_onUpdate = col.getOnUpdate();
				
			if (col.getOnDelete() != null)
				m_onDelete = col.getOnDelete();
			}
		else
			ret = false;
			
		return (ret);
		}
	}
