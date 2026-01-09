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

package org.agileclick.genorm.plugins.memcached;

import org.agileclick.genorm.QueryPlugin;
import org.dom4j.Element;
import java.util.*;
import org.antlr.stringtemplate.*;
import org.agileclick.genorm.TemplateHelper;

public class MemCachePlugin extends TemplateHelper implements QueryPlugin
	{
	public static final String MEMCACHED_CLIENT_PROPERTY = "genorm.plugins.memcached.ClientProperty";
	
	StringTemplateGroup m_templateGroup;
	
	public MemCachePlugin()
			throws java.io.IOException
		{
		super();
		m_templateGroup = loadTemplateGroup("templates/MemCacheTemplate.java");
		}
		
	public void init(Properties config)
		{
		System.out.println("Initialized Plugin");
		}
		
	public Set<String> getQueryImports(Map<String, Object> attributes)
		{
		Set<String> imports = new HashSet<String>();
		imports.add("org.agileclick.ultramc.MemCachedClient");
		imports.add("java.io.Externalizable");
		imports.add("java.io.Serializable");
		imports.add("java.io.ObjectInput");
		imports.add("java.io.ObjectOutput");
		return (imports);
		}
		
	public Set<String> getQueryImplements(Map<String, Object> attributes)
		{
		Set<String> implementList = new HashSet<String>();
		implementList.add("Serializable");
		
		/* Query q = (Query)attributes.get("query");
		if (!q.isUpdate())
			{
			implementList.add("Externalizable");
			} */
		
		return (implementList);
		}
		
	public Set<String> getQueryRecordImplements(Map<String, Object> attributes)
		{
		Set<String> implementList = new HashSet<String>();
		//implementList.add("Externalizable");
		implementList.add("Serializable");
		return (implementList);
		}
		
	public String getQueryBody(Map<String, Object> attributes)
		{
		StringTemplate st = m_templateGroup.getInstanceOf("QueryBody");
		st.setAttributes(attributes);
		
		return (st.toString());
		}
		
	public String getQueryRecordBody(Map<String, Object> attributes)
		{
		return ("");
		}
		
	
	}
