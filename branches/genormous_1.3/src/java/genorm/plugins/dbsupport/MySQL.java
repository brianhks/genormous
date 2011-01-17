package genorm.plugins.dbsupport;

import genorm.CreatePlugin;
import genorm.TemplateHelper;
import org.antlr.stringtemplate.*;
import java.io.*;
import org.dom4j.*;
import java.util.*;
import genorm.Table;
import genorm.Column;
import genorm.ForeignKeySet;

public class MySQL implements CreatePlugin
	{
	private TemplateHelper m_helper;
	
	public void init(Element pluginElement, Properties config)
		{
		m_helper = new TemplateHelper();
		}
		
	public String getFieldEscapeString() { return ("`"); }
	
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
