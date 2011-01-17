package genorm;

import org.antlr.stringtemplate.*;
import java.io.*;
import java.util.*;

public class TemplateHelper
	{
	private String m_destDir;
	protected int m_generatedFileCount;
	
	public TemplateHelper()
		{
		m_generatedFileCount = 0;
		}
	
	public String readResource(String fileName)
			throws IOException
		{
		InputStreamReader reader;
		StringBuffer sBuf = new StringBuffer(128);
		int inputChar;
		
		reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName));
		while((inputChar = reader.read()) != -1)
			{
			sBuf.append((char)inputChar);
			}
			
		reader.close();
			
		return (sBuf.toString());
		}
		
	//------------------------------------------------------------------------------
	public void setDestinationDir(String dir)
		{
		m_destDir = dir;
		}
		
	//------------------------------------------------------------------------------
	/**
		Writes the file if it does not already exist
	*/
	public void conditionalWriteTemplate(String templateName, Map<String, Object> attrs)
			throws IOException
		{
		conditionalWriteTemplate(m_destDir+"/"+templateName, "templates/"+templateName, attrs);
		}
		
	//------------------------------------------------------------------------------
	/**
		Writes the file if it does not already exist
	*/
	public void conditionalWriteTemplate(String fileName, String templateName, Map<String, Object> attrs)
			throws IOException
		{
		if (new File(fileName).exists())
			return;
			
		m_generatedFileCount ++;
		StringTemplate template = new StringTemplate("",
				org.antlr.stringtemplate.language.DefaultTemplateLexer.class);
				
		template.setName(fileName);
		template.setTemplate(readResource(templateName));
		
				
		template.setAttributes(attrs);
		
		File dir = new File(fileName).getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		
		FileWriter fw = new FileWriter(fileName);
		fw.write(template.toString());
		fw.close();
		}
	
	//------------------------------------------------------------------------------
	public void writeTemplate(String templateName, Map<String, Object> attrs)
			throws IOException
		{
		writeTemplate(m_destDir+"/"+templateName, "templates/"+templateName, attrs);
		}
		
	//------------------------------------------------------------------------------
	public void writeTemplate(String fileName, String templateName, Map<String, Object> attrs)
			throws IOException
		{
		m_generatedFileCount ++;
		StringTemplate template = new StringTemplate("",
				org.antlr.stringtemplate.language.DefaultTemplateLexer.class);
				
		template.setName(fileName);
		template.setTemplate(readResource(templateName));
		
				
		template.setAttributes(attrs);
		
		File dir = new File(fileName).getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		
		FileWriter fw = new FileWriter(fileName);
		fw.write(template.toString());
		fw.close();
		}
		
//------------------------------------------------------------------------------
	public StringTemplateGroup loadTemplateGroup(String file)
			throws IOException
		{
		StringTemplateGroup templateGroup = new StringTemplateGroup(
				new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)), 
				org.antlr.stringtemplate.language.DefaultTemplateLexer.class);
				
		return (templateGroup);
		}
	}
