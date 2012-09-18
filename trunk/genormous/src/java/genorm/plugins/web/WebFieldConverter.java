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

package genorm.plugins.web;

import java.sql.Timestamp;

public interface WebFieldConverter
	{
	public int toInt(String field, String value);
	public String toString(String field, String value);
	public Timestamp toTimestamp(String field, String value);
	public boolean toBoolean(String field, String value);
	public double toDouble(String field, String value);
	public byte toByte(String field, String value);
	public long toLong(String field, String value);
	public byte[] toBytes(String field, String value);
	public java.math.BigDecimal toBigDecimal(String field, String value);
	public java.sql.Date toDate(String field, String value);
	public java.util.UUID toUUID(String field, String value);
	}
