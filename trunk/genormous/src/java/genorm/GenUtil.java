package genorm;

import java.util.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import java.io.*;

public class GenUtil extends TemplateHelper
	{
	/**
		Genorm property to not inherit global column definitions.<br/>
		Value must be 'true' or 'false'
	*/
	public static final String PROP_INHERIT = "genorm.inherit";
	
	/**
		Property containing the package name for all generated ORM objects
	*/
	public static final String PROP_PACKAGE = "genorm.package";
	
	/**
	*/
	public static final String PROP_DESTINATION = "genorm.destination";
	
	/**
	*/
	public static final String PROP_GRAPHVIZ_FILE = "genorm.graphvizFile";
	
	/**
		Class name of a class that extends {@link Format}.  If not specified {@link DefaultFormat}
		is used.
	*/
	public static final String PROP_FORMATTER = "genorm.formatter";
	
	
	

	protected Map<String, String> m_javaTypeMap;
	protected Map<String, String> m_dbTypeMap;
	protected List<GenPlugin> m_pluginList;
	protected Properties m_config;
	protected Document m_source;
	private Format m_formatter;
	
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
	
	public GenUtil(String source)
			throws ConfigurationException
		{
		super();
		m_config = new Properties();
		
		m_javaTypeMap = new DefaultMap();
		m_dbTypeMap = new DefaultMap();
		m_pluginList = new ArrayList<GenPlugin>();
		
		try
			{
			SAXReader reader = new SAXReader();
			reader.setValidation(false);
			reader.setIncludeExternalDTDDeclarations(false);
			m_source = reader.read(new File(source));
		
			Element config = m_source.getRootElement().element("configuration");
			if (config != null)
				readConfiguration(m_config, config);
			}
		catch (DocumentException de)
			{
			throw new ConfigurationException(de);
			}
			
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
			return ((Format)loadClass(PROP_FORMATTER, m_config.getProperty(PROP_FORMATTER)));
			
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
	private Object loadClass(String propName, String className)
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
	protected void readConfiguration(Properties configProp, Element config)
			throws ConfigurationException
		{
		//Read in all options
		Iterator optIt = config.elementIterator("option");
		while (optIt.hasNext())
			{
			Element option = (Element)optIt.next();
			m_config.setProperty(option.attributeValue("name"), option.attributeValue("value"));
			}
			
		//Read in java types
		Element jtm = config.element("typeMap");
		if (jtm != null)
			{
			Iterator tIt = jtm.elementIterator("type");
			while (tIt.hasNext())
				{
				Element type = (Element)tIt.next();
				m_javaTypeMap.put(type.attributeValue("custom"), type.attributeValue("java"));
				}
			}
		
		//Read in db types
		Element dtm = config.element("typeMap");
		if (dtm != null)
			{
			Iterator tIt = dtm.elementIterator("type");
			while (tIt.hasNext())
				{
				Element type = (Element)tIt.next();
				m_dbTypeMap.put(type.attributeValue("custom"), type.attributeValue("db"));
				}
			}
		
			
		//Read in plugins
		Iterator plugins = config.selectNodes("plugin").iterator();
		while (plugins.hasNext())
			{
			Element plugin = (Element)plugins.next();
			GenPlugin gPlugin = (GenPlugin)loadClass("plugin", plugin.attributeValue("class"));
			
			gPlugin.init(plugin, configProp);
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
