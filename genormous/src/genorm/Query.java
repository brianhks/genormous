package genorm;

import org.dom4j.*;
import java.util.*;
import java.util.regex.*;

public class Query
	{
	//XML elements and attribute names
	public static final String NAME = "name";
	public static final String QUERY = "query";
	public static final String TYPE = "type";
	public static final String TAG = "tag";
	public static final String INPUT = "input";
	public static final String REPLACE = "replace";
	public static final String RETURN = "return";
	public static final String PARAM = "param";
	
	
	private Format m_formatter;
	private String m_queryName;
	private ArrayList<Parameter> m_inputs;
	private ArrayList<Parameter> m_replacements;
	private ArrayList<Parameter> m_outputs;
	private String m_sqlQuery;
	
	public Query(Element queryRoot, Format formatter)
		{
		m_formatter = formatter;
		m_queryName = queryRoot.attributeValue(NAME);
		
		m_inputs = getParameters(queryRoot.element(INPUT));
		m_replacements = getParameters(queryRoot.element(REPLACE));
		m_outputs = getParameters(queryRoot.element(RETURN));
		
		//Validate the outputs with what is in the select
		Pattern selectPattern = Pattern.compile("select(.+?)from.*", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Pattern paramPattern = Pattern.compile(".+as\\s(.+)|.+\\.(.+)|(.+)", Pattern.CASE_INSENSITIVE);
		
		m_sqlQuery = queryRoot.elementTextTrim("sql");
		Matcher m = selectPattern.matcher(m_sqlQuery);
		if (m.matches())
			{
			String select = m.group(1).trim();
			String split[] = select.split(",");
			
			// Check split length with m_outputs length to make sure they match
			if (split.length != m_outputs.size())
				{
				System.out.println("Warning, output parameter count does not match the select statement");
				
				System.out.println("Select parameters:");
				for (int I = 0; I < split.length; I++)
					System.out.println("  "+split[I].trim());
					
				System.out.println("Declared parameters:");
				for (Parameter par : m_outputs)
					System.out.println("  "+par.getName());
				}
			else
				{
				for (int I = 0; I < split.length; I++)
					{
					String param = split[I];
					m = paramPattern.matcher(param.trim());
					if (m.matches())
						{
						int group;
						for (group = 1; group <= 3; group++)
							if (m.group(group) != null)
								break;
						
						String paramName = m_outputs.get(I).getName();
						if (!paramName.equals(m.group(group)))
							{
							System.out.println("Param "+paramName+" does not match select statement "+m.group(group));
							}
						}
					else
						System.out.println("Select param \""+param+"\" does not match regular expression");
					}
				}
			}
		}
		
	private ArrayList<Parameter> getParameters(Element e)
		{
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		if (e != null)
			{
			Iterator it = e.elementIterator(PARAM);
			while (it.hasNext())
				{
				Element p = (Element)it.next();
				if (p.attribute(TAG) != null)
					params.add(new Parameter(p.attributeValue(NAME), p.attributeValue(TYPE), p.attributeValue(TAG), m_formatter));
				else
					params.add(new Parameter(p.attributeValue(NAME), p.attributeValue(TYPE), m_formatter));
				}
			}
			
		return (params);
		}
		
	public String getQueryName() { return (m_queryName); }
	public ArrayList<Parameter> getInputs() { return (m_inputs); }
	public ArrayList<Parameter> getReplacements() { return (m_replacements); }
	public ArrayList<Parameter> getOutputs() { return (m_outputs); }
	public int getOutputsCount() { return (m_outputs.size()); }
	public boolean isReplaceQuery() { return (m_replacements.size() > 0); }
	public String getSqlQuery() { return (m_sqlQuery.replaceAll("\\n+", "\\\\n")); }
	
	public boolean isParamQuery()
		{
		return (m_replacements.size() > 0 || m_inputs.size() > 0);
		}
	
	
	public String getClassName() { return (m_formatter.formatClassName(m_queryName)); }
	}
