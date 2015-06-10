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

import java.io.IOException;
import org.dom4j.Element;
import java.util.Map;

/**
	Defines the interface for plugins that are used to generate the create.sql file
*/
public interface CreatePlugin extends GenPlugin
	{
	public String getCreateSQL(Table table);
	public String getConstraintSQL(ForeignKeySet keySet);
	/**
		This it he characters to use when escaping field names in select statements
		Postgress uses double quote (") where mysql uses back tick `
		The code must returned the value as it can appear in a java code string
		so a double quote should be returned as "\"" 
	*/
	public String getFieldEscapeString();
	}
