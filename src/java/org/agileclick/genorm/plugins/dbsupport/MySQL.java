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

package org.agileclick.genorm.plugins.dbsupport;

import org.agileclick.genorm.CreatePlugin;
import org.agileclick.genorm.TemplateHelper;
import org.antlr.stringtemplate.*;
import java.io.*;
import org.dom4j.*;
import java.util.*;
import org.agileclick.genorm.Table;
import org.agileclick.genorm.ForeignKeySet;

public class MySQL implements CreatePlugin
	{
	private TemplateHelper m_helper;
	
	public void init(Properties config)
		{
		m_helper = new TemplateHelper();
		}
		
	public String getFieldEscapeString() { return ("`"); }
	
	//---------------------------------------------------------------------------
	public String getCreateSQL(Table table)
		{
		String sql = "";
		try
			{
			StringTemplateGroup tGroup = m_helper.loadTemplateGroup("templates/mysql_create.st");
			StringTemplate createTemplate = tGroup.getInstanceOf("tableCreate");
			
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("table", table);
			attributes.putAll(table.getProperties());
			
			createTemplate.setAttributes(attributes);
			sql = createTemplate.toString().trim();
			}
		catch (IOException ioe)
			{
			ioe.printStackTrace();
			}
		
		return (sql);
		}
		
	//---------------------------------------------------------------------------
	public String getConstraintSQL(ForeignKeySet keySet)
		{
		String sql = "";
		try
			{
			StringTemplateGroup tGroup = m_helper.loadTemplateGroup("templates/mysql_create.st");
			StringTemplate createTemplate = tGroup.getInstanceOf("fkeyConstraint");
			
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("keyset", keySet);
			
			createTemplate.setAttributes(attributes);
			sql = createTemplate.toString().trim();
			}
		catch (IOException ioe)
			{
			ioe.printStackTrace();
			}
		
		return (sql);
		}
	}
