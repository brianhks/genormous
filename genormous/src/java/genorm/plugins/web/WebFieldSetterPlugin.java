package genorm.plugins.web;

import genorm.ORMPlugin;
import genorm.TemplateHelper;
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
