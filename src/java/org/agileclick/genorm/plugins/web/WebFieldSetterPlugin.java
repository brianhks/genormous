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

import org.agileclick.genorm.ORMPlugin;
import org.agileclick.genorm.TemplateHelper;
import org.antlr.stringtemplate.*;
import java.io.*;
import java.util.*;
import org.dom4j.*;
import java.util.*;

public class WebFieldSetterPlugin implements ORMPlugin
	{
	private TemplateHelper m_helper;
	
	public void init(Element pluginElement, Properties config)
		{
		m_helper = new TemplateHelper();
		}
		
	public Set<String> getImplements(Map<String, Object> attributes)
		{
		return (new HashSet<String>());
		}
		
	public String getBody(Map<String, Object> attributes)
			throws java.io.IOException
		{
		StringTemplateGroup tGroup = m_helper.loadTemplateGroup("templates/WebFieldSetter.java");
		StringTemplate st = tGroup.getInstanceOf("class");
		st.setAttributes(attributes);
		
		return (st.toString());
		}
	
	}
