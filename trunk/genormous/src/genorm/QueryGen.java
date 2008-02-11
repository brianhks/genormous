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
	private String m_source;
	private Format m_formatter;
	private String m_packageName;
	
	
	//===========================================================================
	private static class CommandLine
		{
		public String source;
		public String destination;
		public String targetPackage;
		
		public CommandLine()
			{
			source = null;
			destination = null;
			targetPackage = null;
			}
		}
		
	//===========================================================================
	private static final ParameterDef[] PARAMETERS = 
		{
		new StringDef('s', "source"),
		new StringDef('d', "destination"),
		new StringDef('p', "targetPackage"),
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
	public QueryGen(String source, String destDir, String packageName)
		{
		super(destDir);
		m_source = source;
		m_formatter = new DefaultFormat();
		m_packageName = packageName;
		}
		
//------------------------------------------------------------------------------
	public void setFormat(Format formatter)
		{
		m_formatter = formatter;
		}
		
//------------------------------------------------------------------------------
	public void generateClasses()
			throws Exception
		{
		FileWriter fw;
		int genfiles = 0;
		
		try
			{
			SAXReader reader = new SAXReader();
			Document xmldoc = reader.read(new File(m_source));
			
			StringTemplateGroup dataTypeMapGroup = loadTemplateGroup("templates/data_type_maps.st");
			
			StringTemplateGroup queryObjectTG = loadTemplateGroup("templates/ObjectQuery.java");
			queryObjectTG.setSuperGroup(dataTypeMapGroup);
			
			
			//NodeList nl = xmldoc.getElementsByTagName(QUERY);
			Iterator queryit = xmldoc.selectNodes("/queries/query").iterator();
			while (queryit.hasNext())
				{
				Element e = (Element)queryit.next();
				Query q = new Query(e, m_formatter);
				
				String fileName = q.getClassName() + "Query.java";
								
				genfiles++;
				Map<String, Object> attributes = new HashMap<String, Object>();
				//TextReplace sqlQuery = new TextReplace(this.getClass().getClassLoader().getResourceAsStream("ObjectQuery.tmpl"), "%");
				
				attributes.put("package", m_packageName);
				attributes.put("query", q);
				
				StringTemplate queryTemplate = queryObjectTG.getInstanceOf("objectQuery");
				queryTemplate.setAttributes(attributes);
				
				fw = new FileWriter(m_destDir+"/"+fileName);
				fw.write(queryTemplate.toString());
				fw.close();
				}
			
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("package", m_packageName);
			
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
