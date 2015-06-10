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

package org.agileclick.genorm.plugins.web;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.UUID;
import java.math.BigDecimal;

public class DefaultWebFieldConverter implements WebFieldConverter
	{
	public int toInt(String field, String value)
		{
		return (Integer.parseInt(value));
		}
		
	//---------------------------------------------------------------------------
	public String toString(String field, String value)
		{
		return (value);
		}
		
	//---------------------------------------------------------------------------
	public Timestamp toTimestamp(String field, String value)
		{
		return (null);
		}
		
	//---------------------------------------------------------------------------
	public boolean toBoolean(String field, String value)
		{
		if (value == null)
			return false;

		return (Boolean.parseBoolean(value));
		}
		
	//---------------------------------------------------------------------------
	public double toDouble(String field, String value)
		{
		if (value == null)
			return (0.0);

		return (Double.parseDouble(value));
		}
	
	//---------------------------------------------------------------------------
	public byte toByte(String field, String value)
		{
		if (value == null)
			return (0);

		return (Byte.parseByte(value));
		}
	
	//---------------------------------------------------------------------------
	public long toLong(String field, String value)
		{
		if (value == null)
			return (0L);

		return (Long.parseLong(value));
		}
	
	//---------------------------------------------------------------------------
	public byte[] toBytes(String field, String value)
		{
		return (null);
		}
	
	//---------------------------------------------------------------------------
	public BigDecimal toBigDecimal(String field, String value)
		{
		return (new BigDecimal(value));
		}
	
	//---------------------------------------------------------------------------
	public Date toDate(String field, String value)
		{
		return (null);
		}
	
	//---------------------------------------------------------------------------
	public UUID toUUID(String field, String value)
		{
		return (UUID.fromString(value));
		}
	}
