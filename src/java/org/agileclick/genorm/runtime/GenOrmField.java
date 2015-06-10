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

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.Serializable;

public abstract class GenOrmField implements Serializable
	{
	protected GenOrmFieldMeta m_fieldMeta;
	protected boolean m_isNull;
	protected GenOrmRecordKey m_recordKey;  //If this field is a foreign key this is will point to the key.
	
	public GenOrmField(GenOrmFieldMeta gofm)
		{
		m_fieldMeta = gofm;
		m_isNull = true;
		m_recordKey = null;
		}
		
	public GenOrmFieldMeta getFieldMeta() { return (m_fieldMeta); }
	
	public boolean setNull()
		{
		if (!m_isNull)
			{
			m_isNull = true;
			return (true);
			}
		else
			return (false);
		}
		
	public boolean isNull() { return (m_isNull); }
	
	public abstract void setValue(ResultSet rs, int pos) throws java.sql.SQLException;
	public abstract void placeValue(PreparedStatement ps, int pos) throws java.sql.SQLException;
	public abstract void placePrevValue(PreparedStatement ps, int pos) throws java.sql.SQLException;
	//public abstract String getSQLValue();
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	
	public abstract Object getPrevValue();
	
	/**
		Returns the value of the field as a string
	*/
	public abstract String toString();
	
	public abstract String getPrevValueAsString();
	
	public void setRecordKey(GenOrmRecordKey recordKey) { m_recordKey = recordKey; }
	public GenOrmRecordKey getRecordKey() { return (m_recordKey); }
	}
