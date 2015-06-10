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

import org.dom4j.*;
import java.util.*;

public class Parameter
	{
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String TAG = "tag";
	public static final String TEST = "test";
	public static final String REF = "ref";
	
	private String m_name;
	private String m_type;
	private String m_tag;
	private Format m_formatter;
	private String m_testParam;
	private boolean m_reference = false;
	
	public Parameter(Element p, Format formatter, Map<String, String> typeMap)
		{
		m_formatter = formatter;
		m_name = p.attributeValue(NAME);
		if (typeMap != null)
			m_type = (String)typeMap.get(p.attributeValue(TYPE));
		else
			m_type = p.attributeValue(TYPE);
			
		m_tag = p.attributeValue(TAG);
		m_testParam = p.attributeValue(TEST);
		}
		
	//---------------------------------------------------------------------------
	public Parameter(String name, String type, Format formatter)
		{
		this(name, type, null, formatter);
		}
		
	//---------------------------------------------------------------------------
	public Parameter(String name, String type, String tag, Format formatter)
		{
		m_formatter = formatter;
		m_name = name;
		m_type = type;
		m_tag = tag;
		}
		
	//---------------------------------------------------------------------------
	/*
		Creates a reference parameter
	*/
	public Parameter(Parameter other)
		{
		m_formatter = other.m_formatter;
		m_name = other.m_name;
		m_type = other.m_type;
		m_tag = other.m_tag;
		m_reference = true;
		}
	
	//---------------------------------------------------------------------------
	public String getName() { return (m_name); }
	public String getXmlName() { return (m_formatter.formatParameterName(m_name)); }
	public String getParameterName() { return (m_formatter.formatParameterName(m_name)); }
	public String getMethodName() { return (m_formatter.formatMethodName(m_name)); }
	public String getType() { return (m_type); }
	public String getTag() { return (m_tag); }
	public boolean isBooleanType() { return (m_type.equals("boolean")); }
	public boolean isReference() { return (m_reference); }
	public String getTestParam() 
		{
		if (m_type.equals("String"))
			return ("\""+m_testParam+"\"");
		else if (m_type.equals("java.util.UUID"))
			{
			try
				{
				UUID.fromString(m_testParam);
				return ("UUID.fromString(\""+m_testParam+"\")");
				}
			catch (IllegalArgumentException iae) {	}
			return (m_testParam);
			}
		else
			return (m_testParam);
		}
		
	@Override
	public boolean equals(Object obj)
		{
		Parameter other = (Parameter)obj;
			
		return (other.m_type.equals(m_type));
		}
	}
