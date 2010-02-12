package genorm.plugins.dbsupport;

import genorm.CreatePlugin;
import genorm.TemplateHelper;
import org.antlr.stringtemplate.*;
import java.io.*;
import org.dom4j.*;
import java.util.*;
import genorm.Table;
import genorm.Column;

public class Postgres implements CreatePlugin
	{
	private TemplateHelper m_helper;
	
	public void init(Element pluginElement, Properties config)
		{
		m_helper = new TemplateHelper();
		}
	
	public String getCreateSQL(Table table)
		{
		String sql = "";
		try
			{
			StringTemplateGroup tGroup = m_helper.loadTemplateGroup("templates/postgres_create.st");
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
	}