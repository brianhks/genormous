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


public class DefaultFormat
		implements Format
	{
	/*package*/ static String toCamelCase(String str, boolean upcaseFirstChar)
		{
		StringBuffer sb = new StringBuffer();
		char[] cArr = str.toLowerCase().toCharArray();
		
		if (upcaseFirstChar)
			{
			cArr[0] = Character.toUpperCase(cArr[0]);
			}
			
		boolean upcaseNext = false;
		for (int I = 0; I < cArr.length; I++)
			{
			if (cArr[I] == '_')
				{
				upcaseNext = true;
				}
			else
				{
				if (upcaseNext)
					{
					sb.append(Character.toUpperCase(cArr[I]));
					upcaseNext = false;
					}
				else
					sb.append(cArr[I]);
				}
			}
		
		return (sb.toString());
		}
		
	public String formatClassName(String tableName)
		{
		return (toCamelCase(tableName, true));
		}
		
	public String formatStaticName(String columnName)
		{
		if (Character.isDigit(columnName.toCharArray()[0]))
			return ("A_"+columnName.toUpperCase());
		else
			return (columnName.toUpperCase());
		}
		
	/* public String formatStaticNameRef(String columnName)
		{
		//This assumes that columns that referece end with _id
		//It will therefore remove them before generating the name
		if (columnName.endsWith("_id"))
			columnName = columnName.substring(0, (columnName.length()-3));
		else
			columnName += "_ref";
			
		return (formatStaticName(columnName));
		} */
		
	public String formatMethodName(String columnName)
		{
		return (toCamelCase(columnName, true));
		}
		
	public String formatForeignKeyMethod(ForeignKeySet fks)
		{
		//This assumes that columns that referece end with _id
		//It will therefore remove them before generating the name
		String name = "";
		if (fks.getKeys().size() > 1)
			name = fks.getTableName();
		else
			name = fks.getKeys().get(0).getName();
		
		if (name.endsWith("_id"))
			name = name.substring(0, (name.length()-3));
			
		name += "_ref";
			
		return (formatMethodName(name));
		}
		
	public String formatParameterName(String columnName)
		{
		return (toCamelCase(columnName, false));
		}
	}
