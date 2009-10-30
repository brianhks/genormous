package genorm;

import java.io.*;
import org.dom4j.io.SAXReader;
import org.dom4j.*;
import java.util.*;
import java.util.regex.*;
import org.jargp.*;
import org.antlr.stringtemplate.*;

import static java.lang.System.out;

public class QueryGen extends TemplateHelper
	{
	public static final String SOURCE = "genorm.querygen.source";
	public static final String DESTINATION = "genorm.querygen.destination";
	public static final String PACKAGE = "genorm.querygen.package";
	public static final String FORMATTER = "genomr.querygen.formatter";
	public static final String TYPE_MAP = "genorm.querygen.typeMap";
	
	private Format m_formatter;
	private String m_packageName;
	private Map<String, String> m_typeMap;
	private Properties m_config;
	private ArrayList<QueryPlugin> m_pluginList;
	
	//===========================================================================
	private static class CommandLine
		{
		public String source;
		public String destination;
		public String targetPackage;
		public String configurationFile;
		
		public CommandLine()
			{
			source = null;
			destination = null;
			targetPackage = null;
			configurationFile = null;
			}
		}
		
	//===========================================================================
	private static final ParameterDef[] PARAMETERS = 
		{
		new StringDef('s', "source"),
		new StringDef('d', "destination"),
		new StringDef('p', "targetPackage"),
		new StringDef('c', "configurationFile"),
		};
		
	
	
//==============================================================================
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
	private static String readFile(String fileName)
			throws IOException
		{
		FileReader fReader;
		StringBuilder sBuf = new StringBuilder(128);
		int inputChar;
		
		fReader = new FileReader(fileName);
		while((inputChar = fReader.read()) != -1)
			{
			sBuf.append((char)inputChar);
			}
			
		fReader.close();
			
		return (sBuf.toString());
		}
		
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
	public static void main(String[] args)
		{
		CommandLine cl = new CommandLine();
		ArgumentProcessor proc = new ArgumentProcessor(PARAMETERS);
		
		proc.processArgs(args, cl);
		
		QueryGen og = new QueryGen(cl.source, cl.destination, cl.targetPackage);
		
		try
			{
			og.generateClasses();
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}
		
//------------------------------------------------------------------------------
	public QueryGen()
		{
		m_config = new Properties();
		m_pluginList = new ArrayList<QueryPlugin>();
		}
		
//------------------------------------------------------------------------------
	public QueryGen(Properties configuration)
		{
		m_config = configuration;
		m_pluginList = new ArrayList<QueryPlugin>();
		}
		
//------------------------------------------------------------------------------
	public QueryGen(String source, String destDir, String packageName)
		{
		m_config = new Properties();
		m_config.setProperty(SOURCE, source);
		if (destDir != null)
			m_config.setProperty(DESTINATION, destDir);
		if (packageName != null)
			m_config.setProperty(PACKAGE, packageName);
		m_pluginList = new ArrayList<QueryPlugin>();
		}

//------------------------------------------------------------------------------
	private String getRequiredProperty(String prop)
			throws ConfigurationException
		{
		String value = m_config.getProperty(prop);
		
		if (value == null)
			throw new ConfigurationException(prop, "Option is required");
			
		return (value);
		}

//------------------------------------------------------------------------------
	private Object loadClass(String prop, String defaultClass)
			throws ConfigurationException
		{
		Object ret = null;
		
		String className = m_config.getProperty(prop, defaultClass);
		
		try
			{
			if (className != null)
				ret = Class.forName(className).newInstance();
			}
		catch (ClassNotFoundException cnfe)
			{
			throw new ConfigurationException(prop, "Cannot load class "+className);
			}
		catch (InstantiationException ie)
			{
			throw new ConfigurationException(prop, "Cannot load class "+className);
			}
		catch (IllegalAccessException iae)
			{
			throw new ConfigurationException(prop, "Cannot load class "+className);
			}
			
		return (ret);
		}
		
//------------------------------------------------------------------------------
	public void setConfiguration(String configurationFile)
		{
		setConfiguration(new PropertiesFile(configurationFile));
		}
		
//------------------------------------------------------------------------------
	public void setConfiguration(Properties configuration)
		{
		m_config = configuration;
		}
		
//------------------------------------------------------------------------------
	public void setConfigOption(String option, String value)
		{
		m_config.setProperty(option, value);
		}

//------------------------------------------------------------------------------
	/* package */ void setTypeMap(Map<String, String> typeMap)
		{
		m_typeMap = typeMap;
		}
		
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
	private void loadConfiguration(Element e)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException
		{
		if (e.attributeValue("package") != null)
			m_config.setProperty(PACKAGE, e.attributeValue("package"));
			
		if (e.attributeValue("destination") != null)
			m_config.setProperty(DESTINATION, e.attributeValue("destination"));
			
		Iterator types = e.selectNodes("type").iterator();
		if (types.hasNext())
			m_typeMap = new HashMap<String, String>();
			
		while (types.hasNext())
			{
			Element type = (Element)types.next();
			m_typeMap.put(type.attributeValue("custom"), type.attributeValue("java"));
			}
			
		Iterator plugins = e.selectNodes("plugin").iterator();
		while (plugins.hasNext())
			{
			Element plugin = (Element)plugins.next();
			QueryPlugin qPlugin = (QueryPlugin)Class.forName(plugin.attributeValue("class")).newInstance();
			
			qPlugin.init(plugin);
			m_pluginList.add(qPlugin);
			}
		}
		
//------------------------------------------------------------------------------
	public void generateClasses()
			throws Exception
		{
		FileWriter fw;
		int genfiles = 0;
		
		String source = getRequiredProperty(SOURCE);
		
		Format formatter = (Format)loadClass(FORMATTER, "genorm.DefaultFormat");
		if (m_typeMap == null)
			m_typeMap = Genormous.readPropertiesFile(new PropertiesFile(m_config.getProperty(TYPE_MAP)));
			
		try
			{
			SAXReader reader = new SAXReader();
			Document xmldoc = reader.read(new File(source));
			
			Iterator config = xmldoc.selectNodes("/queries/configuration/querygen").iterator();
			if (config.hasNext())
				loadConfiguration((Element)config.next());
				
			String destDir = getRequiredProperty(DESTINATION);
			super.setDestinationDir(destDir);
			String packageName = getRequiredProperty(PACKAGE);
			
			StringTemplateGroup dataTypeMapGroup = loadTemplateGroup("templates/data_type_maps.st");
			
			StringTemplateGroup queryObjectTG = loadTemplateGroup("templates/ObjectQuery.java");
			queryObjectTG.setSuperGroup(dataTypeMapGroup);
			
			
			//NodeList nl = xmldoc.getElementsByTagName(QUERY);
			Iterator queryit = xmldoc.selectNodes("/*/query").iterator();
			while (queryit.hasNext())
				{
				Element e = (Element)queryit.next();
				Query q = new Query(e, formatter, m_typeMap);
				
				String fileName = q.getClassName() + "Query.java";
				String dataFileName = q.getClassName() + "Data.java";
								
				genfiles++;
				Map<String, Object> attributes = new HashMap<String, Object>();
				//TextReplace sqlQuery = new TextReplace(this.getClass().getClassLoader().getResourceAsStream("ObjectQuery.tmpl"), "%");
				
				attributes.put("package", packageName);
				attributes.put("query", q);
				
				//StringBuilder pluginIncludes = new StringBuilder();
				StringBuilder pluginQueryBodies = new StringBuilder();
				
				Set<String> importSet = new TreeSet<String>();
				Set<String> queryImplementSet = new TreeSet<String>();
				Set<String> recordImplementSet = new TreeSet<String>();
				for (QueryPlugin qp : m_pluginList)
					{
					importSet.addAll(qp.getQueryImports(attributes));
					queryImplementSet.addAll(qp.getQueryImplements(attributes));
					
					pluginQueryBodies.append(qp.getQueryBody(attributes));
					pluginQueryBodies.append("\n");
					
					recordImplementSet.addAll(qp.getQueryRecordImplements(attributes));
					//pluginIncludes.append(qp.getIncludes(attributes));
					//pluginMethods.append(qp.getMethods(attributes));
					
					//pluginIncludes.append("\n");
					//pluginMethods.append("\n");
					}
					
				attributes.put("importList", importSet);
				attributes.put("queryImplementSet", queryImplementSet);
				attributes.put("queryImplementSetNotEmpty", !queryImplementSet.isEmpty());
				attributes.put("recordImplementSet", recordImplementSet);
				
				attributes.put("pluginQueryBody", pluginQueryBodies.toString());
				
				StringTemplate queryTemplate = queryObjectTG.getInstanceOf("objectQuery");
				queryTemplate.setAttributes(attributes);
				
				fw = new FileWriter(m_destDir+"/"+fileName);
				fw.write(queryTemplate.toString());
				fw.close();
				
				File dataFile = new File(m_destDir+"/"+dataFileName);
				if (!q.isUpdate() && !dataFile.exists())
					{
					genfiles++;
					queryTemplate = queryObjectTG.getInstanceOf("objectQueryData");
					queryTemplate.setAttributes(attributes);
					
					fw = new FileWriter(m_destDir+"/"+dataFileName);
					fw.write(queryTemplate.toString());
					fw.close();
					}
				}
			
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("package", packageName);
			
			
			
			/* genfiles++;
			writeTemplate("SQLQuery.java", attributes);
			
			genfiles++;
			writeTemplate("Formatter.java", attributes);
			
			genfiles++;
			writeTemplate("DefaultFormatter.java", attributes); */
			
				
			System.out.println("Generated "+genfiles+" class files");
			}
		catch (Exception e)
			{
			System.out.println("Dude an exception");
			e.printStackTrace();
			return;
			}
		}
	}
