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

package org.agileclick.genorm.runtime;

import java.util.*;
import java.io.Serializable;

/**
	The Key is used to identify the uniqueness of this particular record.
	The Key is made up of the table name and a list of primary keys for the 
	record.
*/
public class GenOrmRecordKey implements Serializable
	{
	private String m_tableName;
	private Map<String, GenOrmField> m_keyFields;
	
	public GenOrmRecordKey(String tableName)
		{
		m_tableName = tableName;
		m_keyFields = new LinkedHashMap<String, GenOrmField>();
		}
		
	//------------------------------------------------------------------------
	public String getTableName() { return (m_tableName); }
	
	//------------------------------------------------------------------------
	/**
		For Generated code use only
	*/
	public void addKeyField(String name, GenOrmField field)
		{
		m_keyFields.put(name, field);
		field.setRecordKey(this);
		}
		
	//------------------------------------------------------------------------
	public Iterator<GenOrmField> getFieldIterator() { return (m_keyFields.values().iterator()); }
	
	//------------------------------------------------------------------------
	public GenOrmField getField(String name) { return (m_keyFields.get(name)); }
	
	//------------------------------------------------------------------------
	@Override
	public int hashCode()
		{
		Iterator<GenOrmField> it = m_keyFields.values().iterator();
		int hashCode = m_tableName.hashCode();
		while (it.hasNext())
			{
			GenOrmField gof = it.next();
			
			//Same hash code calculation as used in a java.util.List
			hashCode = 31 * hashCode + gof.hashCode();
			}
			
		return (hashCode);
		}
		
	//------------------------------------------------------------------------
	@Override
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmRecordKey))
			return (false);
			
		GenOrmRecordKey otherKey = (GenOrmRecordKey)obj;
		if (!otherKey.m_tableName.equals(m_tableName))
			return (false);
			
		//if m_keyFields is empty then there is no primary key and all records
		//do not equal each other.
		if (m_keyFields.size() == 0)
			return (false);
		
		return (otherKey.m_keyFields.equals(m_keyFields));
		}
		
	//---------------------------------------------------------------------------
	@Override
	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		sb.append(m_tableName);
		sb.append(" ");
		Iterator<GenOrmField> it = m_keyFields.values().iterator();
		while (it.hasNext())
			{
			GenOrmField field = it.next();
			sb.append(field.getFieldMeta().getFieldName());
			sb.append("=\"");
			sb.append(field);
			sb.append("\" ");
			}
			
		return (sb.toString());
		}
	}
