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
import java.sql.Types;

public abstract class GenOrmFieldTemplate<T> extends GenOrmField
	{
	protected T m_value;
	protected T m_prevValue = null;
	
	public GenOrmFieldTemplate(GenOrmFieldMeta fieldMeta)
		{
		super(fieldMeta);
		m_value = null;
		}
		
	//---------------------------------------------------------------------------
	public boolean setValue(T value)
		{
		if (((m_value == null) && (value != null)) || 
				((m_value != null) && (!m_value.equals(value))))
			{
			m_value = value;
			if (m_value == null)
				setNull();
			else
				m_isNull = false;
			return (true);
			}
		else
			return (false);
		}
		
	//---------------------------------------------------------------------------
	public void setPrevValue(T value)
		{
		m_prevValue = value;
		}
		
	//---------------------------------------------------------------------------
	public T getValue()
		{
		return (m_value);
		}
		
	//---------------------------------------------------------------------------
	public T getPrevValue()
		{
		return (m_prevValue);
		}
		
	//---------------------------------------------------------------------------
	public String getSQLValue()
		{
		return (String.valueOf(m_value));
		}
		
	//---------------------------------------------------------------------------
	public int hashCode()
		{
		if (m_value == null)
			return (0);
		else
			return (m_value.hashCode());
		}
		
	//---------------------------------------------------------------------------
	public boolean equals(Object obj)
		{
		if (!(obj instanceof GenOrmFieldTemplate))
			return (false);
			
		GenOrmFieldTemplate other = (GenOrmFieldTemplate)obj;
		return (m_value.equals(other.m_value));
		}
		
	//---------------------------------------------------------------------------
	public String getPrevValueAsString()
		{
		return (m_prevValue == null ? "" : String.valueOf(m_prevValue));
		}
		
	//---------------------------------------------------------------------------
	public String toString()
		{
		return (m_value == null ? "" : String.valueOf(m_value));
		}
	}
