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


public class GenOrmConstraint
	{
	private String m_foreignTable;
	private String m_constraintName;
	private String m_sql;
	
	public GenOrmConstraint(String foreignTable, String constraintName, String sql)
		{
		m_foreignTable = foreignTable;
		m_constraintName = constraintName;
		m_sql = sql;
		}
		
	public String getForeignTable()
		{
		return (m_foreignTable);
		}
		
	public String getConstraintName()
		{
		return (m_constraintName);
		}
		
	public String getSql()
		{
		return (m_sql);
		}
	}
