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

import java.util.*;

import org.agileclick.genorm.parser.GenOrmParser;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class GenUtil extends TemplateHelper
	{
	/**
		Genorm property to not inherit global column definitions.
		Value must be 'true' or 'false'
	*/
	public static final String PROP_INHERIT = "genorm.inherit";
	
	/**
		Property containing the package name for all generated ORM objects
	*/
	public static final String PROP_PACKAGE = "genorm.package";
	
	/**
		Destination folder to write orm objects to
	*/
	public static final String PROP_DESTINATION = "genorm.destination";

	/**
		Destination to write the create.sql file to.  If not specified genorm.destination is used.
	*/
	public static final String PROP_SQL_DESTINATION = "genorm.sql.destination";
	
	/**
	*/
	public static final String PROP_GRAPHVIZ_FILE = "genorm.graphvizFile";
	
	/**
		Class name of a class that extends {@link Format}.  If not specified {@link DefaultFormat}
		is used.
	*/
	public static final String PROP_FORMATTER = "genorm.formatter";
	
	/**
		Sets the package for the GenOrmDataSource class.  This defaults to the 
		output package but if the queries are generated into a separate package
		then the orm objects this can be used to identify that other package.
	*/
	public static final String PROP_DATASOURCE_PACKAGE = "genorm.datasourcePackage";
	
	/**
	*/
	public static final String PROP_BASE_CLASS_PACKAGE = "";
	
	/**
	*/
	public static final String PROP_BASE_CLASS_DESTINATION = "";
	
	/**
	*/
	
	protected Map<String, String> m_javaTypeMap;
	protected Map<String, String> m_dbTypeMap;
	protected List<GenPlugin> m_pluginList;
	protected Properties m_config;
	protected Document m_source;
	private Format m_formatter;
	protected boolean m_verbose;


	protected GenOrmParser.Configuration m_configuration;
	protected GenOrmParser.Global m_globalColumns;
	protected List<GenOrmParser.Table> m_tables = new ArrayList<>();
	protected List<GenOrmParser.Query> m_queries = new ArrayList<>();
	
	/**
		This class is used for the java and db type maps.  If the value
		is not mapped then the key is returned from the get() call
	*/
	private class DefaultMap<K, V> extends HashMap<K, V>
		{
		public DefaultMap()
			{
			super();
			}
			
		@Override
		public V get(Object key)
			{
			V value = super.get(key);
			if (value == null)
				return ((V)key);
			else
				return (value);
			}
		}
	
	public GenUtil(String source, boolean verbose)
			throws ConfigurationException
		{
		super();
		m_verbose = verbose;
		m_config = new Properties();
		
		m_javaTypeMap = new DefaultMap<>();
		m_dbTypeMap = new DefaultMap<>();
		m_pluginList = new ArrayList<>();


		try
			{
			GenOrmParser genOrmParser = new GenOrmParser(new GenOrmParser.SlickHandler()
				{
				@Override
				public void parsedConfiguration(GenOrmParser.Configuration entry) throws Exception
					{
					m_configuration = entry;
					}

				@Override
				public void parsedGlobal(GenOrmParser.Global entry) throws Exception
					{
					m_globalColumns = entry;
					}

				@Override
				public void parsedTable(GenOrmParser.Table entry) throws Exception
					{
					m_tables.add(entry);
					}

				@Override
				public void parsedQuery(GenOrmParser.Query entry) throws Exception
					{
					m_queries.add(entry);
					}
				});

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(new File(source), genOrmParser);

			if (m_configuration != null)
				readConfiguration();
			}
		catch (ParserConfigurationException | SAXException | IOException e)
			{
			throw new ConfigurationException(e);
			}

		}
		
//------------------------------------------------------------------------------
	public String getJavaType(String custom)
		{
		String ret = m_javaTypeMap.get(custom);
		if (ret == null)
			ret = custom;
			
		return (ret);
		}
		
//------------------------------------------------------------------------------
	public void setFormat(Format formatter)
		{
		m_formatter = formatter;
		}
		
//------------------------------------------------------------------------------
	/**
		Gets the {@link Format} object specified in the {@link #PROP_FORMATTER}
		properties or an instance of {@link DefaultFormat}
	*/
	public Format getFormat()
			throws ConfigurationException
		{
		if (m_formatter != null)
			return (m_formatter);
			
		if (m_config.getProperty(PROP_FORMATTER) != null)
			return ((Format)loadClass(m_config.getProperty(PROP_FORMATTER)));
			
		return (new DefaultFormat());
		}
		
//------------------------------------------------------------------------------
	public void setTypesFile(String typeFile)
		{
		m_javaTypeMap = readPropertiesFile(new PropertiesFile(typeFile));
		}
		
		
//------------------------------------------------------------------------------
	public void setPackage(String packageName)
		{
		m_config.setProperty(PROP_PACKAGE, packageName);
		}
		
//------------------------------------------------------------------------------
	public void setDestination(String destination)
		{
		m_config.setProperty(PROP_DESTINATION, destination);
		}
	
//------------------------------------------------------------------------------
	private Object loadClass(String className)
			throws ConfigurationException
		{
		Object ret = null;
		
		try
			{
			if (className != null)
				ret = Class.forName(className).newInstance();
			}
		catch (Exception e)
			{
			throw new ConfigurationException("plugin", "Cannot load class "+className);
			}
			
		return (ret);
		}
		
//------------------------------------------------------------------------------
	protected void readConfiguration()
			throws ConfigurationException
		{
		//Read in all options
		for (GenOrmParser.Option option : m_configuration.getOptionList())
			{
			m_config.setProperty(option.getName(), option.getValue());
			}

		for (GenOrmParser.TypeMap typeMap : m_configuration.getTypeMapList())
			{
			m_javaTypeMap.put(typeMap.getCustom(), typeMap.getJavaType());
			m_dbTypeMap.put(typeMap.getCustom(), typeMap.getDBType());
			}


		for (GenOrmParser.Plugin plugin : m_configuration.getPluginList())
			{
			GenPlugin gPlugin = (GenPlugin)loadClass(plugin.getPluginClass());
			gPlugin.init(m_config);
			m_pluginList.add(gPlugin);
			}
		}
		
//------------------------------------------------------------------------------
	protected String getRequiredProperty(String prop)
			throws ConfigurationException
		{
		String value = m_config.getProperty(prop);
		
		if (value == null)
			throw new ConfigurationException(prop, "Option is required");
			
		return (value);
		}
	
//------------------------------------------------------------------------------
	protected Map<String, String> readPropertiesFile(PropertiesFile props)
		{
		if (!props.exists())
			return (null);
			
		Map<String, String> map = new HashMap<String, String>();
		
		Iterator<Object> keys = props.keySet().iterator();
		while (keys.hasNext())
			{
			String key = (String)keys.next();
			map.put(key, (String)props.get(key));
			}
			
		return (map);
		}
	}
