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

import java.io.Serializable;

public class GenOrmFieldMeta implements Serializable
	{
	private String m_fieldName;
	private String m_fieldType;
	private int m_dirtyFlag;
	private boolean m_primaryKey;
	private boolean m_foreignKey;
	
	public GenOrmFieldMeta(String fieldName, String fieldType, int dirtyFlag, boolean primaryKey, boolean foreignKey)
		{
		m_fieldName = fieldName;
		m_fieldType = fieldType;
		m_dirtyFlag = dirtyFlag;
		m_primaryKey = primaryKey;
		m_foreignKey = foreignKey;
		}
		
	public String getFieldName() { return (m_fieldName); }
	public String getFieldType() { return (m_fieldType); }
	public int getDirtyFlag() { return (m_dirtyFlag); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	public boolean isForeignKey() { return (m_foreignKey); }
	}
