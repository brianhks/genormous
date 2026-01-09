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

import java.io.*;

import org.agileclick.genorm.parser.GenOrmParser;
import org.dom4j.io.SAXReader;
import org.dom4j.*;
import java.util.*;
import java.util.regex.*;
import org.jargp.*;
import org.antlr.stringtemplate.*;

import static java.lang.System.out;

public class QueryGen extends GenUtil
	{

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
		
		
		try
			{
			QueryGen og = new QueryGen(cl.source); //, cl.destination, cl.targetPackage);
			
			if (cl.destination != null)
				og.setDestination(cl.destination);
				
			if (cl.targetPackage != null)
				og.setPackage(cl.targetPackage);
			
			og.generateClasses();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
		
//------------------------------------------------------------------------------
	public QueryGen(String source)
			throws ConfigurationException
		{
		super(source, false);
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
	public void generateClasses()
			throws Exception
		{
		FileWriter fw;
		int genfiles = 0;
		
		String destDir = getRequiredProperty(PROP_DESTINATION);
		
		super.setDestinationDir(destDir);
		new File(destDir).mkdirs();
		
		Format formatter = super.getFormat();
			
		try
			{
			String packageName = getRequiredProperty(PROP_PACKAGE);
			
			StringTemplateGroup dataTypeMapGroup = loadTemplateGroup("templates/data_type_maps.st");
			
			StringTemplateGroup queryObjectTG = loadTemplateGroup("templates/ObjectQuery.java");
			queryObjectTG.setSuperGroup(dataTypeMapGroup);

			for (GenOrmParser.Query parserQuery : m_queries)
				{
				Query q = new Query(parserQuery, formatter, m_javaTypeMap);

				String fileName = q.getClassName() + "Query.java";
				String dataFileName = q.getClassName() + "Data.java";

				genfiles++;
				Map<String, Object> attributes = new HashMap<String, Object>();

				attributes.put("package", packageName);
				attributes.put("query", q);
				attributes.put("dsPackage", m_config.getProperty(PROP_DATASOURCE_PACKAGE, packageName)+".");

				StringBuilder pluginQueryBodies = new StringBuilder();

				Set<String> importSet = new TreeSet<String>();
				Set<String> queryImplementSet = new TreeSet<String>();
				Set<String> recordImplementSet = new TreeSet<String>();
				for (GenPlugin gp : m_pluginList)
					{
					QueryPlugin qp;
					if (gp instanceof QueryPlugin)
						qp = (QueryPlugin)gp;
					else
						continue;

					importSet.addAll(qp.getQueryImports(attributes));
					queryImplementSet.addAll(qp.getQueryImplements(attributes));

					pluginQueryBodies.append(qp.getQueryBody(attributes));
					pluginQueryBodies.append("\n");

					recordImplementSet.addAll(qp.getQueryRecordImplements(attributes));
					}

				attributes.put("importList", importSet);
				attributes.put("queryImplementSet", queryImplementSet);
				attributes.put("queryImplementSetNotEmpty", !queryImplementSet.isEmpty());
				attributes.put("recordImplementSet", recordImplementSet);

				attributes.put("pluginQueryBody", pluginQueryBodies.toString());

				StringTemplate queryTemplate = queryObjectTG.getInstanceOf("objectQuery");
				queryTemplate.setAttributes(attributes);

				fw = new FileWriter(destDir+"/"+fileName);
				fw.write(queryTemplate.toString());
				fw.close();

				File dataFile = new File(destDir+"/"+dataFileName);
				if (!q.isUpdate() && !dataFile.exists())
					{
					genfiles++;
					queryTemplate = queryObjectTG.getInstanceOf("objectQueryData");
					queryTemplate.setAttributes(attributes);

					fw = new FileWriter(destDir+"/"+dataFileName);
					fw.write(queryTemplate.toString());
					fw.close();
					}
				}

			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("package", packageName);
			
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
